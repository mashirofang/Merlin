package cn.org.merlin.convert;

import cn.org.merlin.convert.field.FieldConverter;
import cn.org.merlin.convert.field.FieldConverterFactory;
import cn.org.merlin.convert.field.OneofConverter;
import cn.org.merlin.convert.field.SimpleFieldConverter;
import cn.org.merlin.convert.reflect.FieldReflect;
import cn.org.merlin.convert.reflect.PropertyReflect;
import cn.org.merlin.convert.reflect.ReflectWrapper;
import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.convert.tool.ClassUtils;
import cn.org.merlin.convert.tool.ProtoHelper;
import cn.org.merlin.Merlin;
import cn.org.merlin.api.AliasController;
import cn.org.merlin.api.MessageConverter;
import com.google.common.collect.Sets;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MessageConverterImpl<M extends Message, T> implements MessageConverter<M, T> {

  private static final Logger logger = LoggerFactory.getLogger(MessageConverterImpl.class);

  private final M protoType;
  private final Class<T> clz;
  private List<FieldConverter> converters;
  private Merlin merlin;

  public MessageConverterImpl(M protoType, Class<T> clz, Merlin merlin) {

    this.protoType = Objects.requireNonNull(protoType);
    this.merlin = merlin;

    if (clz == Object.class) {
      Class oc = ProtoHelper.resolveFromOption(protoType.getDescriptorForType().getOptions());
      this.clz = oc == null ? clz : oc;
    } else {
      this.clz = clz;
    }
    this.converters = new ArrayList<>();
    init();
  }

  private void init() {
    Descriptor descriptor = protoType.getDescriptorForType();

    Set<FieldDescriptor> fields = Sets.newHashSet(descriptor.getFields());
    List<OneofDescriptor> oneofs = descriptor.getOneofs();

    for (Descriptors.OneofDescriptor oneof : oneofs) {
      String fieldName = oneof.getName();
      String alias =
          (String)
              oneof.getOptions().getField(cn.org.merlin.Descriptor.oneofFieldAlias.getDescriptor());

      Reflectable reflectable = this.createReflectable(fieldName, alias);
      OneofConverter converter = new OneofConverter(reflectable, oneof);
      for (Descriptors.FieldDescriptor oneofFiled : oneof.getFields()) {
        Class targetClz = ProtoHelper.getOneofType(oneofFiled, protoType);
        ReflectWrapper rw = new ReflectWrapper(targetClz, reflectable);
        SimpleFieldConverter fieldNode =
            FieldConverterFactory.create(protoType, oneofFiled, rw, merlin);
        fields.remove(oneofFiled);
        if (null != fieldNode) {
          converter.add(oneofFiled.getName(), targetClz, fieldNode);
        }
      }
      converters.add(converter);
    }

    for (Descriptors.FieldDescriptor field : fields) {

      String alias =
          (String) field.getOptions().getField(cn.org.merlin.Descriptor.fieldAlias.getDescriptor());

      String fieldName = field.getName();
      Reflectable reflectable = this.createReflectable(fieldName, alias);
      SimpleFieldConverter fieldNode =
          FieldConverterFactory.create(protoType, field, reflectable, merlin);
      if (null != fieldNode) {
        converters.add(fieldNode);
      }
    }
  }

  /**
   * 从bean生成 message
   *
   * @param source javabean
   * @return message
   */
  @Override
  public Message targetToSource(Object source) {
    Message.Builder builder = protoType.newBuilderForType();
    converters.forEach(node -> node.buildPbField(builder, source));
    return builder.build();
  }

  /**
   * 从message生成bean
   *
   * @param source from type
   */
  @Override
  public Object sourceToTarget(Message source) {
    Object target = ClassUtils.createInstance(clz);
    if (null != target) {
      converters.forEach(node -> node.buildObjectField(target, source));
    }
    return target;
  }

  @Override
  public Class getTargetType() {
    return clz;
  }

  @Override
  public Class getSourceType() {
    return protoType.getClass();
  }

  @Override
  public M getProtoType() {
    return protoType;
  }

  protected Reflectable createReflectable(String name, String aliasName) {

    AliasController fieldAlias = merlin.getAliasController();
    String objectFieldName = null;

    if (null != fieldAlias) {
      objectFieldName = fieldAlias.alias(protoType, clz, name);
    }

    if (null == objectFieldName || objectFieldName.isEmpty()) {
      if (null == aliasName || aliasName.isEmpty()) {
        objectFieldName = name;
      } else {
        objectFieldName = aliasName;
      }
    }

    try {
      // get/set
      return new PropertyReflect(new PropertyDescriptor(objectFieldName, clz));
    } catch (IntrospectionException e) {

      // field
      Field field = ClassUtils.getSerializableFieldFromClass(clz, objectFieldName);
      if (null != field) {
        return new FieldReflect(field);
      } else {
        String format = "message:{}; field:{} not found in class:{}";
        logger.warn(format, protoType.getClass().getName(), objectFieldName, clz.getName());
      }
    }
    return null;
  }
}
