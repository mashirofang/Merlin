package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class BooleanConverter extends SimpleFieldConverter {

  protected BooleanConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertPbToObject(Object pbValue) {

    if (null == target) {
      return null;
    }

    Boolean value = (Boolean) pbValue;

    if (target == boolean.class || target == Boolean.class) {
      return value;
    }

    if (target == int.class || target == Integer.class) {
      return value ? 1 : 0;
    }

    if (target == long.class || target == Long.class) {
      return value ? 1L : 0L;
    }

    if (target == float.class || target == Float.class) {
      return value ? 1F : 0F;
    }

    if (target == double.class || target == Double.class) {
      return value ? 1D : 0D;
    }

    if (target == byte.class || target == Byte.class) {
      return value ? (byte) 1 : (byte) 0;
    }

    if (target == short.class || target == Short.class) {
      return value ? (short) 1 : (short) 0;
    }

    if (target == char.class || target == Character.class) {
      return value ? (char) 1 : (char) 0;
    }

    if (target == String.class) {
      return String.valueOf(value);
    }

    return null;
  }

  @Override
  public Object convertObjectToPb(Object value) {

    if (null == value) {
      return null;
    }

    if (value instanceof Boolean) {
      return value;
    }

    if (value instanceof Integer) {
      return ((Number) value).intValue() > 0;
    }

    if (value instanceof Double) {
      return ((Number) value).doubleValue() > 0;
    }

    if (value instanceof Float) {
      return ((Number) value).floatValue() > 0;
    }

    if (value instanceof Byte) {
      return ((Number) value).byteValue() > 0;
    }

    if (value instanceof Long) {
      return ((Number) value).longValue() > 0;
    }

    if (value instanceof Short) {
      return ((Number) value).shortValue() > 0;
    }

    if (value instanceof Character) {
      return ((Character) value).charValue() > 0;
    }

    if (value instanceof String) {
      String text = (String) value;
      return (text.equalsIgnoreCase("true") || text.equalsIgnoreCase("t") || text.equals("1"));
    }

    return null;
  }
}
