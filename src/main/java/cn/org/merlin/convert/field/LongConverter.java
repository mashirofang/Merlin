package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;

public class LongConverter extends NumberConverter {

  private boolean unsigned;

  protected LongConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
    unsigned = field.getType() == Type.UINT64 || field.getType() == Type.FIXED64;
  }

  @Override
  public Object convertPbToObject(Object pbValue) {

    if (null == target) {
      return null;
    }

    Long value = (Long) pbValue;

    if (target == String.class && unsigned) {
      return Long.toUnsignedString(value);
    } else {
      return super.convertNumber(target, value);
    }
  }

  @Override
  public Object convertObjectToPb(Object value) {
    if (null == value) {
      return null;
    }

    if (value instanceof Long) {
      return value;
    }

    if (value instanceof Number) {
      return ((Number) value).longValue();
    }

    if (value instanceof Boolean) {
      return value.equals(Boolean.TRUE) ? 1L : 0L;
    }

    if (value instanceof Character) {
      return (long) ((Character) value).charValue();
    }

    if (value instanceof String) {
      try {
        if (unsigned) {
          return Long.parseUnsignedLong((String) value);
        } else {
          return Long.parseLong((String) value);
        }
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }
}
