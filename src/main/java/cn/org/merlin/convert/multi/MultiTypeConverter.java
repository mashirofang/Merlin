package cn.org.merlin.convert.multi;

import cn.org.merlin.Merlin;
import cn.org.merlin.api.MessageConverter;
import cn.org.merlin.convert.field.FieldConverter;
import cn.org.merlin.convert.field.FieldConverterFactory;
import cn.org.merlin.convert.reflect.SortedReflect;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MultiTypeConverter<M extends Message, T> implements MessageConverter<M, T> {

  // message 的原型
  private final M protoType;
  private List<FieldConverter> converters;
  private MultiTypeWrapper wrapper;
  private Merlin merlin;

  public MultiTypeConverter(M protoType, MultiTypeWrapper wrapper, Merlin merlin) {
    this.protoType = Objects.requireNonNull(protoType);
    this.wrapper = wrapper;
    this.converters = new ArrayList<>();
    this.merlin = merlin;

    Descriptor descriptor = protoType.getDescriptorForType();
    List<FieldDescriptor> fields = descriptor.getFields();
    for (int i = 0; i < fields.size(); i++) {
      FieldDescriptor field = fields.get(i);
      SortedReflect reflectable = new SortedReflect(wrapper, i);
      converters.add(FieldConverterFactory.create(protoType, field, reflectable, merlin));
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
    Object target = MultiTypeWrapper.fromTypes(wrapper.getTypes());
    converters.forEach(node -> node.buildObjectField(target, source));
    return target;
  }

  @Override
  public Class getTargetType() {
    return MultiTypeWrapper.class;
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
