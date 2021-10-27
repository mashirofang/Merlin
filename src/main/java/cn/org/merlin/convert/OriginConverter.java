package cn.org.merlin.convert;

import cn.org.merlin.api.MessageConverter;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Message;
import com.google.protobuf.Message.Builder;

import java.util.List;

public class OriginConverter<S extends Message, T extends Message>
    implements MessageConverter<S, T> {

  private S sourceType;
  private T targetType;

  public OriginConverter(S sourceType, T targetType) {
    this.sourceType = sourceType;
    this.targetType = targetType;
  }

  private <TARGET extends Message> TARGET convert(Message source, TARGET targetType) {
    Builder builder = targetType.newBuilderForType();
    Descriptor sourceDescriptor = source.getDescriptorForType();
    Descriptor targetDescriptor = targetType.getDescriptorForType();
    for (FieldDescriptor sf : sourceDescriptor.getFields()) {
      Object value = source.getField(sf);
      FieldDescriptor tf = targetDescriptor.findFieldByName(sf.getName());
      if (null != tf && sf.getType() == tf.getType() && (hasValue(source, sf))) {
        if (sf.isRepeated()) {
          for (Object v : (List<Object>) value) {
            Object targetValue = convert(sf, tf, v, builder);
            if (null != targetValue) builder.addRepeatedField(tf, targetValue);
          }
        } else {
          Object targetValue = convert(sf, tf, value, builder);
          if (null != targetValue) builder.setField(tf, targetValue);
        }
      }
    }
    return (TARGET) builder.build();
  }

  private boolean hasValue(Message source, FieldDescriptor field) {
    if (field.isRepeated()) {
      return source.getRepeatedFieldCount(field) > 0;
    } else if (field.getJavaType() == JavaType.ENUM) {
      return true;
    } else {
      return source.hasField(field);
    }
  }

  private Object convert(FieldDescriptor sf, FieldDescriptor tf, Object value, Builder builder) {
    switch (sf.getJavaType()) {
      case INT:
      case LONG:
      case DOUBLE:
      case FLOAT:
      case STRING:
      case BOOLEAN:
      case BYTE_STRING:
        return value;
      case ENUM:
        return tf.getEnumType().findValueByName(((EnumValueDescriptor) value).getName());
      case MESSAGE:
        return convert((Message) value, builder.newBuilderForField(tf).build());
    }
    return null;
  }

  @Override
  public T sourceToTarget(S source) {
    return convert(source, targetType);
  }

  @Override
  public S targetToSource(T target) {
    return convert(target, sourceType);
  }

  @Override
  public Class<S> getSourceType() {
    return (Class<S>) sourceType.getClass();
  }

  @Override
  public Class<T> getTargetType() {
    return (Class<T>) targetType.getClass();
  }
}
