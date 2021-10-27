package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class StringConverter extends SimpleFieldConverter {

  protected StringConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertPbToObject(Object pbValue) {

    if (null == target) {
      return null;
    }

    String value = (String) pbValue;

    if (target == String.class) {
      return value;
    }

    if (target == boolean.class || target == Boolean.class) {
      return Boolean.valueOf(value);
    }

    if (target == int.class || target == Integer.class) {
      try {
        return Integer.parseInt(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (target == long.class || target == Long.class) {
      try {
        return Long.parseLong(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (target == float.class || target == Float.class) {
      try {
        return Float.parseFloat(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (target == double.class || target == Double.class) {
      try {
        return Double.parseDouble(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (target == byte.class || target == Byte.class) {
      try {
        return Byte.parseByte(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (target == short.class || target == Short.class) {

      try {
        return Short.parseShort(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    if (target == char.class || target == Character.class) {
      try {
        return (char) Integer.parseInt(value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }

  @Override
  public Object convertObjectToPb(Object value) {

    if (null == value) {
      return null;
    }

    if (value instanceof String) {
      return value;
    } else {
      return String.valueOf(value);
    }
  }
}
