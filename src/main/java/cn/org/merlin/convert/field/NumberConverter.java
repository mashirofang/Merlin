package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public abstract class NumberConverter extends SimpleFieldConverter {

  protected NumberConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertPbToObject(Object pbValue) {
    if (null == target) {
      return null;
    }

    return convertNumber(target, (Number) pbValue);
  }

  protected Object convertNumber(Class<?> target, Number value) {
    if (target == int.class || target == Integer.class) {
      return value.intValue();
    }

    if (target == long.class || target == Long.class) {
      return value.longValue();
    }

    if (target == float.class || target == Float.class) {
      return value.floatValue();
    }

    if (target == double.class || target == Double.class) {
      return value.doubleValue();
    }

    if (target == byte.class || target == Byte.class) {
      return value.byteValue();
    }

    if (target == short.class || target == Short.class) {
      return value.shortValue();
    }

    if (target == char.class || target == Character.class) {
      return (char) value.intValue();
    }

    if (target == boolean.class || target == Boolean.class) {
      return value.intValue() != 0;
    }

    if (target == String.class) {
      return String.valueOf(value);
    }

    return null;
  }
}
