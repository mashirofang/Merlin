package com.merlin.convert.mapped;

import com.merlin.Merlin;
import com.merlin.api.MessageConverter;
import com.merlin.exception.ConvertException;
import com.merlin.convert.field.FieldConverter;
import com.merlin.convert.field.FieldConverterFactory;
import com.merlin.convert.field.SimpleFieldConverter;
import com.merlin.convert.reflect.MappedReflect;
import com.merlin.convert.reflect.Reflectable;
import com.merlin.convert.tool.ClassUtils;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MappedTypeConverter<M extends Message, T> implements MessageConverter<M, T> {

  private final M protoType;
  private final Class<T> clz;
  private Type type;
  private List<FieldConverter> converters;
  private Class k;
  private Type v;

  private Merlin merlin;

  public MappedTypeConverter(M protoType, Type type, Merlin merlin) {
    this.protoType = Objects.requireNonNull(protoType);
    this.type = Objects.requireNonNull(type);
    this.clz = ClassUtils.resolveType(type);
    this.converters = new ArrayList<>();
    this.merlin = merlin;
    init();
  }

  private void init() {

    Type[] parameterTypes = ClassUtils.getParameterTypes(type);
    if (null != parameterTypes && parameterTypes.length == 2) {
      k = ClassUtils.resolveType(parameterTypes[0]);
      v = parameterTypes[1];
    }

    List<FieldDescriptor> fields = protoType.getDescriptorForType().getFields();
    for (Descriptors.FieldDescriptor field : fields) {
      Object kf = resolveField(field);
      Reflectable reflectable = new MappedReflect(kf, v);
      SimpleFieldConverter fieldNode =
          FieldConverterFactory.create(protoType, field, reflectable, merlin);
      if (null != fieldNode) {
        this.converters.add(fieldNode);
      }
    }
  }

  private Object resolveField(FieldDescriptor field) {
    if (k == String.class) {
      return field.getName();
    } else if (k == Integer.class) {
      return field.getNumber();
    } else if (k == Long.class) {
      return Integer.valueOf(field.getNumber()).longValue();
    } else {
      throw new ConvertException("unsupported!");
    }
  }

  @Override
  public Object sourceToTarget(Message source) {
    Map target = ClassUtils.createMap(clz);
    if (null != target) {
      converters.forEach(node -> node.buildObjectField(target, source));
    }
    return target;
  }

  @Override
  public Message targetToSource(Object source) {
    Message.Builder builder = protoType.newBuilderForType();
    converters.forEach(node -> node.buildPbField(builder, source));
    return builder.build();
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
}
