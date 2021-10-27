package cn.org.merlin.convert.wrap;

import cn.org.merlin.convert.field.FieldConverterFactory;
import cn.org.merlin.convert.field.OneofConverter;
import cn.org.merlin.convert.field.SimpleFieldConverter;
import cn.org.merlin.convert.reflect.ReflectWrapper;
import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.convert.reflect.WrappedTypeReflect;
import cn.org.merlin.Merlin;
import cn.org.merlin.api.MessageConverter;
import cn.org.merlin.convert.tool.ClassUtils;
import cn.org.merlin.convert.tool.ProtoHelper;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.Message;

import java.lang.reflect.Type;

public class OneofWrappedTypesConverter<M extends Message, T> implements MessageConverter<M, T> {

  private M protoType;
  private Type type;
  private Class<T> clz;
  private OneofDescriptor oneofDescriptor;
  private OneofConverter converter;
  private Merlin merlin;

  public OneofWrappedTypesConverter(M protoType, Type type, Merlin merlin) {
    this.protoType = protoType;
    this.type = type;
    this.clz = ClassUtils.resolveType(type);
    this.merlin = merlin;
    this.init();
  }

  private void init() {
    Descriptor descriptor = protoType.getDescriptorForType();
    this.oneofDescriptor = descriptor.getOneofs().get(0);
    Reflectable reflectable = new WrappedTypeReflect(type);
    this.converter = new OneofConverter(reflectable, oneofDescriptor);
    for (Descriptors.FieldDescriptor oneofFiled : oneofDescriptor.getFields()) {
      Class targetClz = ProtoHelper.getOneofType(oneofFiled, protoType);
      ReflectWrapper rw = new ReflectWrapper(targetClz, reflectable);
      SimpleFieldConverter fc = FieldConverterFactory.create(protoType, oneofFiled, rw, merlin);
      converter.add(oneofFiled.getName(), targetClz, fc);
    }
  }

  @Override
  public Object sourceToTarget(Message source) {
    ObjectWrapper wapper = new ObjectWrapper();
    converter.buildObjectField(wapper, source);
    return wapper.getValue();
  }

  @Override
  public Message targetToSource(Object source) {
    ObjectWrapper wapper = new ObjectWrapper(source);
    Message.Builder builder = protoType.newBuilderForType();
    converter.buildPbField(builder, wapper);
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
