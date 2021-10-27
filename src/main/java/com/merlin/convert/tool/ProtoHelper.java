package com.merlin.convert.tool;

import com.merlin.Descriptor;
import com.merlin.exception.ConvertException;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import com.google.protobuf.DescriptorProtos.MessageOptions;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.ListValue;
import com.google.protobuf.Message;
import com.google.protobuf.NullValue;
import com.google.protobuf.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class ProtoHelper {

  private ProtoHelper() {
    throw new ConvertException("unsupported");
  }

  private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public static Class getOneofType(Descriptors.FieldDescriptor field, Message protoType) {
    FieldOptions options = field.getOptions();
    String target = (String) options.getField(Descriptor.targetClazz.getDescriptor());
    if (null != target && !target.isEmpty()) {
      try {
        return ClassUtils.forName(target);
      } catch (ClassNotFoundException e) {
        logger.error("class:{} defined in oneof field :{} not found ", target, field.getName());
        throw new ConvertException("Convert oneof field fail ! class not found!", e);
      }
    }

    switch (field.getJavaType()) {
      case INT:
        return Integer.class;
      case LONG:
        return Long.class;
      case FLOAT:
        return Float.class;
      case DOUBLE:
        return Double.class;
      case BOOLEAN:
        return Boolean.class;
      case STRING:
        return String.class;
      case BYTE_STRING:
        return byte[].class;
      case MESSAGE:
        Message child = protoType.newBuilderForType().newBuilderForField(field).build();
        if (child.getClass() == Struct.class) {
          return Map.class;
        } else if (child.getClass() == ListValue.class) {
          return List.class;
        }

        DescriptorProtos.MessageOptions messageOptions = child.getDescriptorForType().getOptions();
        String clz = (String) messageOptions.getField(Descriptor.clazz.getDescriptor());
        if (null != clz && !clz.isEmpty()) {
          try {
            return ClassUtils.forName(clz);
          } catch (ClassNotFoundException e) {
            logger.error("class:{} defined in message:{} not found ", clz, child.getClass());
            return Object.class;
          }
        } else {
          throw new ConvertException("Convert oneof field fail ! target class not found!");
        }
      case ENUM:
        if (field.getEnumType().getClass() == NullValue.getDescriptor().getClass()) {
          return Void.class;
        }
      default:
    }
    throw new ConvertException(
        "field :" + field.getName() + ", oneof type must be defined in option");
  }

  public static boolean hasValue(Message message, FieldDescriptor desc) {
    if (desc.isRepeated()) {
      return true;
    } else if (message.hasField(desc)) {
      return true;
    } else {
      if (desc.getJavaType() != JavaType.MESSAGE) {
        return true;
      }
    }
    return false;
  }

  public static Class resolveFromOption(MessageOptions options) {
    String clz = (String) options.getField(Descriptor.clazz.getDescriptor());
    if (null != clz && !clz.isEmpty()) {
      try {
        return ClassUtils.forName(clz);
      } catch (ClassNotFoundException e) {
        logger.error("class:{} defined in message:{} not found ", clz);
      }
    }
    return null;
  }

  public static <T extends Message> T getDefaultInstance(Class<T> clazz) {
    try {
      Method method = clazz.getMethod("getDefaultInstance");
      return (T) method.invoke(method);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get default instance for " + clazz, e);
    }
  }
}
