package com.merlin.convert.wrap;

import com.merlin.Merlin;
import com.merlin.api.MessageConverter;
import com.merlin.convert.field.SimpleFieldConverter;
import com.merlin.convert.field.FieldConverterFactory;
import com.merlin.convert.reflect.Reflectable;
import com.merlin.convert.reflect.WrappedTypeReflect;
import com.merlin.convert.tool.ClassUtils;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import java.lang.reflect.Type;
import java.util.List;

public class WrappedTypesConverter<M extends Message, T> implements MessageConverter<M, T> {

  private M protoType;
  private Type type;
  private Class<T> clz;
  private SimpleFieldConverter converter;
  private Merlin merlin;

  public WrappedTypesConverter(M protoType, Type type, Merlin merlin) {
    this.protoType = protoType;
    this.type = type;
    this.clz = ClassUtils.resolveType(type);
    this.merlin = merlin;
    this.init();
  }

  private void init() {
    List<FieldDescriptor> fields = protoType.getDescriptorForType().getFields();
    FieldDescriptor field = fields.get(0);
    Reflectable reflectable = new WrappedTypeReflect(type);
    this.converter = FieldConverterFactory.create(protoType, field, reflectable, merlin);
  }

  @Override
  public Object sourceToTarget(Message source) {
    ObjectWrapper wapper = new ObjectWrapper();
    converter.buildObjectField(wapper, source);
    return wapper.getValue();
  }

  @Override
  public Message targetToSource(Object source) {
    Message.Builder builder = protoType.newBuilderForType();
    converter.buildPbField(builder, new ObjectWrapper(source));
    return builder.build();
  }

  @Override
  public Class getSourceType() {
    return protoType.getClass();
  }

  @Override
  public Class getTargetType() {
    return clz;
  }

  @Override
  public M getProtoType() {
    return protoType;
  }
}
