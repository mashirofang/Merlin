package cn.org.merlin.convert.field;

import cn.org.merlin.convert.reflect.Reflectable;
import cn.org.merlin.Merlin;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class FieldConverterFactory {

  public static SimpleFieldConverter create(
          Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    if (protoType != null && field != null && reflectable != null) {
      switch (field.getJavaType()) {
        case INT:
          return new IntConverter(protoType, field, reflectable, merlin);
        case FLOAT:
          return new FloatConverter(protoType, field, reflectable, merlin);
        case DOUBLE:
          return new DoubleConverter(protoType, field, reflectable, merlin);
        case LONG:
          return new LongConverter(protoType, field, reflectable, merlin);
        case BOOLEAN:
          return new BooleanConverter(protoType, field, reflectable, merlin);
        case STRING:
          return new StringConverter(protoType, field, reflectable, merlin);
        case BYTE_STRING:
          return new ByteStringConverter(protoType, field, reflectable, merlin);
        case ENUM:
          return new EnumConverter(protoType, field, reflectable, merlin);
        case MESSAGE:
          return new NestedMessageConverter(protoType, field, reflectable, merlin);
        default:
          return null;
      }
    }
    return null;
  }
}
