package com.merlin.convert.field;

import com.merlin.Merlin;
import com.merlin.convert.reflect.Reflectable;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class FloatConverter extends NumberConverter {

  protected FloatConverter(
      Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertObjectToPb(Object value) {
    if (null == value) {
      return null;
    }

    if (value instanceof Float) {
      return value;
    }

    if (value instanceof Number) {
      return ((Number) value).floatValue();
    }

    if (value instanceof Boolean) {
      return value.equals(Boolean.TRUE) ? 1F : 0F;
    }

    if (value instanceof Character) {
      return (float) ((Character) value).charValue();
    }

    if (value instanceof String) {
      try {
        return Float.parseFloat((String) value);
      } catch (NumberFormatException e) {
        return null;
      }
    }

    return null;
  }
}
