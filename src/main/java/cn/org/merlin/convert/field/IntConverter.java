package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.Type;
import com.google.protobuf.Message;

public class IntConverter extends NumberConverter {

  private boolean unsigned;

  protected IntConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
    unsigned = field.getType() == Type.UINT32 || field.getType() == Type.FIXED32;
  }

  @Override
  public Object convertPbToObject(Object pbValue) {

    if (null == target) {
      return null;
    }

    if (target == String.class && unsigned) {
      return Integer.toUnsignedString((Integer) pbValue);
    }

    return convertNumber(target, (Integer) pbValue);
  }

  @Override
  public Object convertObjectToPb(Object value) {
    if (null == value) {
      return null;
    }

    if (value instanceof Integer) {
      return value;
    }

    if (value instanceof Number) {
      return ((Number) value).intValue();
    }

    if (value instanceof Boolean) {
      return value.equals(Boolean.TRUE) ? 1 : 0;
    }

    if (value instanceof Character) {
      return (int) ((Character) value).charValue();
    }

    if (value instanceof String) {

      try {
        if (unsigned) {
          return Integer.parseUnsignedInt((String) value);
        } else {
          return Integer.parseInt((String) value);
        }
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }
}
