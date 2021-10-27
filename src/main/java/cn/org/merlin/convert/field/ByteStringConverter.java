package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class ByteStringConverter extends SimpleFieldConverter {

  protected ByteStringConverter(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertPbToObject(Object pbValue) {

    if (null == target) {
      return null;
    }

    ByteString value = (ByteString) pbValue;

    if (target == String.class) {
      return value.toStringUtf8();
    }

    if (target == byte[].class) {
      return value.toByteArray();
    }

    return null;
  }

  @Override
  public Object convertObjectToPb(Object value) {
    if (null == value) {
      return null;
    }

    if (value instanceof String) {
      return ByteString.copyFromUtf8((String) value);
    } else if (value instanceof byte[]) {
      return ByteString.copyFrom((byte[]) value);
    } else {
      return null;
    }
  }
}
