package cn.org.merlin;

import cn.org.merlin.api.AliasController;
import cn.org.merlin.api.MessageConverter;
import cn.org.merlin.convert.MessageConverterImpl;
import cn.org.merlin.convert.OriginConverter;
import cn.org.merlin.convert.adapt.FieldType;
import cn.org.merlin.convert.adapt.common.BytesAdaptor;
import cn.org.merlin.convert.adapt.common.CharsAdaptor;
import cn.org.merlin.convert.adapt.common.DateAdaptor;
import cn.org.merlin.convert.adapt.common.SqlDateAdaptor;
import cn.org.merlin.convert.adapt.common.TimestampAdaptor;
import cn.org.merlin.convert.mapped.MappedTypeConverter;
import cn.org.merlin.convert.multi.MultiTypeConverter;
import cn.org.merlin.convert.multi.MultiTypeWrapper;
import cn.org.merlin.convert.tool.ClassUtils;
import cn.org.merlin.convert.tool.ProtoHelper;
import cn.org.merlin.convert.tool.SPILoader;
import cn.org.merlin.convert.wrap.OneofWrappedTypesConverter;
import cn.org.merlin.convert.wrap.WrappedTypesConverter;
import cn.org.merlin.api.TypeAdaptor;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Merlin {

  private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final Map<TypeHolder, MessageConverter> messageConverters = new ConcurrentHashMap<>();
  private final Map<TypeHolder, TypeAdaptor> adaptors = new ConcurrentHashMap<>();
  private final Set<Class> wrappedTypes = new HashSet<>();

  private AliasController aliasController;

  private Merlin(Builder builder) {

    registerWellKnownTypes();

    if (!builder.ignoreSpi) {
      List<TypeAdaptor> adaptors = SPILoader.load(TypeAdaptor.class);
      if (null != adaptors && !adaptors.isEmpty()) {
        adaptors.forEach(this::registerAdaptor);
      }

      List<MessageConverter> messageConverters = SPILoader.load(MessageConverter.class);
      if (null != messageConverters && !messageConverters.isEmpty()) {
        messageConverters.forEach(this::registerConverter);
      }

      // maybe we can support multi alias manager
      List<AliasController> aliases = SPILoader.load(AliasController.class);
      if (null != aliases && aliases.size() == 1) {
        aliasController = aliases.get(0);
      }
    }

    if (null != builder.adaptors) {
      builder.adaptors.forEach(this::registerAdaptor);
    }

    if (null != builder.converters) {
      builder.converters.forEach(this::registerConverter);
    }

    if (null != builder.aliasController) {
      this.aliasController = builder.aliasController;
    }
  }

  public MessageConverter getOrCreate(Message protoType, Type type) {
    if (null == protoType || null == type) {
      return null;
    }

    Class clz = ClassUtils.resolveType(type);

    TypeHolder key = new TypeHolder(protoType.getClass(), clz);
    MessageConverter converter = messageConverters.get(key);
    if (null != converter) {
      return converter;
    }

    if (isMessage(clz)) {
      converter = new OriginConverter(protoType, ProtoHelper.getDefaultInstance(clz));
    } else if (isMappedType(type, protoType)) {
      return new MappedTypeConverter(protoType, type, this);
    } else if (isWrappedType(clz, protoType)) {
      converter = new WrappedTypesConverter(protoType, type, this);
    } else if (isOneofWrappedType(clz, protoType)) {
      converter = new OneofWrappedTypesConverter(protoType, type, this);
    } else {
      converter = new MessageConverterImpl(protoType, clz, this);
    }

    MessageConverter old = messageConverters.putIfAbsent(key, converter);
    return null == old ? converter : old;
  }

  private boolean isMessage(Class clz) {
    return Message.class.isAssignableFrom(clz);
  }

  private boolean isMappedType(Type type, Message protoType) {
    Class clz = ClassUtils.resolveType(type);
    if (Map.class.isAssignableFrom(clz)) {
      Type[] parameterTypes = ClassUtils.getParameterTypes(type);
      if (null != parameterTypes && parameterTypes.length == 2) {
        Class k = ClassUtils.resolveType(parameterTypes[0]);
        if (k == Integer.class || k == Long.class || k == String.class) {
          List<FieldDescriptor> fields = protoType.getDescriptorForType().getFields();
          if (fields.size() == 1) {
            FieldDescriptor f = fields.get(0);
            return !f.isMapField();
          } else {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean isWrappedType(Class<?> clz, Message protoType) {
    Objects.requireNonNull(clz);

    if (protoType.getDescriptorForType().getFields().size() != 1) {
      return false;
    }

    if (clz.isEnum()) {
      return true;
    }

    if (wrappedTypes.contains(clz)) {
      return true;
    }

    if (Map.class.isAssignableFrom(clz)
        || Collection.class.isAssignableFrom(clz)
        || clz.isArray()) {
      return true;
    }

    return false;
  }

  private boolean isOneofWrappedType(Class<?> clz, Message protoType) {
    Descriptor descriptor = protoType.getDescriptorForType();
    List<OneofDescriptor> oneofs = descriptor.getOneofs();
    if (oneofs.size() == 1) {
      OneofDescriptor oneofDescriptor = oneofs.get(0);
      List<FieldDescriptor> fields = oneofDescriptor.getFields();
      if (fields.size() == descriptor.getFields().size()
          && !ClassUtils.containsSerializableField(clz, oneofDescriptor.getName())) {
        return true;
      }
    }
    return false;
  }

  public <T extends Message> T objectToMessage(Object source, T protoType) {
    return objectToMessage(source, null, protoType);
  }

  public <T extends Message> T objectToMessage(Object source, Type sourceType, T protoType) {
    if (null == source || null == protoType) {
      return null;
    }

    MessageConverter converter =
        getOrCreate(protoType, null != sourceType ? sourceType : source.getClass());
    Message message = converter.targetToSource(source);
    logger.debug("convert object:{} to message:{}", source, message);
    return (T) message;
  }

  public <T> T messageToObject(Message source, Class<T> target) {
    return (T) messageToObject(source, (Type) target);
  }

  public Object messageToObject(Message source, Type target) {
    Class clz = ClassUtils.resolveType(target);
    if (clz == void.class) {
      return null;
    }

    Object result;
    if (null == source || null == target) {
      result = null;
    } else {
      MessageConverter converter = getOrCreate(source, target);
      result = converter.sourceToTarget(source);
      logger.debug("convert message:{} to object:{}", source, result);
    }

    if (null == result && clz.isPrimitive()) {
      return Array.get(Array.newInstance(clz, 1), 0);
    }

    return result;
  }

  public Message multiTypeToMessage(Type[] types, Object[] values, Message protoType) {
    MultiTypeWrapper wrapper = MultiTypeWrapper.fromTypesAndValues(types, values);
    Message message = new MultiTypeConverter<>(protoType, wrapper, this).targetToSource(wrapper);
    logger.debug("convert multiType{} to message:{} ", wrapper, message);
    return message;
  }

  public Object[] messageToMultiType(Message source, Type[] types) {
    MultiTypeWrapper wrapper = MultiTypeWrapper.fromTypes(types);
    Object target = new MultiTypeConverter<>(source, wrapper, this).sourceToTarget(source);
    logger.debug("convert message{} to multiType:{} ", source, target);
    return ((MultiTypeWrapper) target).getValues();
  }

  private void registerWellKnownTypes() {
    wrappedTypes.add(String.class);
    wrappedTypes.add(int.class);
    wrappedTypes.add(Integer.class);
    wrappedTypes.add(long.class);
    wrappedTypes.add(Long.class);
    wrappedTypes.add(char.class);
    wrappedTypes.add(Character.class);
    wrappedTypes.add(byte.class);
    wrappedTypes.add(Byte.class);
    wrappedTypes.add(short.class);
    wrappedTypes.add(Short.class);
    wrappedTypes.add(float.class);
    wrappedTypes.add(Float.class);
    wrappedTypes.add(double.class);
    wrappedTypes.add(Double.class);
    wrappedTypes.add(boolean.class);
    wrappedTypes.add(Boolean.class);
    registerAdaptor(new BytesAdaptor());
    registerAdaptor(new CharsAdaptor());
    registerAdaptor(new DateAdaptor());
    registerAdaptor(new SqlDateAdaptor());
    registerAdaptor(new TimestampAdaptor());
  }

  public void registerConverter(MessageConverter converter) {
    Objects.requireNonNull(converter);
    Message protoType = converter.getProtoType();
    Class<?> objectType = converter.getTargetType();
    Objects.requireNonNull(protoType);
    Objects.requireNonNull(objectType);
    messageConverters.put(new TypeHolder(protoType.getClass(), objectType), converter);
  }

  public void unregisterConverter(Message protoType, Class<?> clz) {
    Objects.requireNonNull(protoType);
    Objects.requireNonNull(clz);
    messageConverters.remove(new TypeHolder(protoType.getClass(), clz));
  }

  public void registerAdaptor(TypeAdaptor adaptor) {
    Objects.requireNonNull(adaptor);
    FieldType fieldType = adaptor.getType();
    Class<?> objectType = adaptor.getTargetType();
    Objects.requireNonNull(fieldType);
    Objects.requireNonNull(objectType);
    wrappedTypes.add(objectType);
    adaptors.put(new TypeHolder(fieldType.getSource(), objectType), adaptor);
  }

  public TypeAdaptor getAdaptor(FieldType type, Class<?> target) {
    return adaptors.get(new TypeHolder(type.getSource(), target));
  }

  public TypeAdaptor getAdaptor(FieldDescriptor.Type type, Class<?> target) {
    if (null == type || null == target) {
      return null;
    }

    switch (type) {
      case INT32:
      case SINT32:
      case SFIXED32:
        return getAdaptor(FieldType.INT32, target);

      case INT64:
      case SINT64:
      case SFIXED64:
        return getAdaptor(FieldType.INT64, target);

      case FLOAT:
        return getAdaptor(FieldType.FLOAT, target);

      case DOUBLE:
        return getAdaptor(FieldType.DOUBLE, target);

      case BOOL:
        return getAdaptor(FieldType.BOOLEAN, target);

      case UINT32:
      case FIXED32:
        return getAdaptor(FieldType.UINT32, target);

      case UINT64:
      case FIXED64:
        return getAdaptor(FieldType.UINT64, target);

      case STRING:
        return getAdaptor(FieldType.STRING, target);

      case BYTES:
        return getAdaptor(FieldType.BYTE_STRING, target);

      case ENUM:
      case MESSAGE:
      case GROUP:
      default:
        return null;
    }
  }

  public AliasController getAliasController() {
    return aliasController;
  }

  public static class Builder {
    boolean ignoreSpi;

    List<MessageConverter> converters;
    List<TypeAdaptor> adaptors;
    AliasController aliasController;

    public Builder ignoreSpi() {
      this.ignoreSpi = true;
      return this;
    }

    public Builder withConverters(List<MessageConverter> converters) {
      this.converters = converters;
      return this;
    }

    public Builder withAdaptors(List<TypeAdaptor> adaptors) {
      this.adaptors = adaptors;
      return this;
    }

    public Builder withAliasController(AliasController aliasController) {
      this.aliasController = aliasController;
      return this;
    }

    public Merlin build() {
      return new Merlin(this);
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  static class TypeHolder {

    Class<?> source;
    Class<?> target;

    public TypeHolder(Class<?> source, Class<?> target) {
      this.source = source;
      this.target = target;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TypeHolder that = (TypeHolder) o;
      return com.google.common.base.Objects.equal(source, that.source)
          && com.google.common.base.Objects.equal(target, that.target);
    }

    @Override
    public int hashCode() {
      return com.google.common.base.Objects.hashCode(source, target);
    }
  }
}
