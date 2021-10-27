package com.merlin.convert.field;

import com.merlin.api.Converter;
import com.merlin.api.MessageConverter;
import com.merlin.Merlin;
import com.merlin.convert.entry.EntryConverter;
import com.merlin.convert.reflect.Reflectable;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;

import java.lang.reflect.Type;

public class NestedMessageConverter extends SimpleFieldConverter {

  private Converter converter;

  protected NestedMessageConverter(
      Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  private MessageConverter getConverter() {

    Message fieldType = protoType.newBuilderForType().newBuilderForField(field).build();
    if (field.isMapField()) {
      Type[] types = reflectable.getParameterTypes();
      if (null != types && types.length == 2) {
        return new EntryConverter((MapEntry) fieldType, types[0], types[1], merlin);
      }
    } else {
      Type target = getSingleTargetType();
      if (null != target) {
        return merlin.getOrCreate(fieldType, target);
      }
    }
    return null;
  }

  @Override
  public Object convertPbToObject(Object pbValue) {
    if (null != converter || null != (converter = getConverter())) {
      return converter.sourceToTarget(pbValue);
    }
    return null;
  }

  @Override
  public Object convertObjectToPb(Object value) {
    if (null == value) {
      return null;
    }

    if (null != converter || null != (converter = getConverter())) {
      return converter.targetToSource(value);
    }
    return null;
  }
}
