package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class DoubleConverter extends NumberConverter {

  protected DoubleConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertObjectToPb(Object value) {

    if (null == value) {
      return null;
    }

    if (value instanceof Double) {
      return value;
    }

    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }

    if (value instanceof Boolean) {
      return value.equals(Boolean.TRUE) ? 1D : 0D;
    }

    if (value instanceof Character) {
      return (double) ((Character) value).charValue();
    }

    if (value instanceof String) {
      try {
        return Double.parseDouble((String) value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }
}
