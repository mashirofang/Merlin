package com.merlin.convert.field;

import com.merlin.Merlin;
import com.merlin.convert.reflect.Reflectable;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;

public class EnumConverter extends SimpleFieldConverter {

  protected EnumConverter(
      Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    super(protoType, field, reflectable, merlin);
  }

  @Override
  public Object convertPbToObject(Object pbValue) {

    if (null == target) {
      return null;
    }

    Descriptors.EnumValueDescriptor value = (Descriptors.EnumValueDescriptor) pbValue;

    String name = value.getName();

    Enum[] enums = (Enum[]) target.getEnumConstants();
    for (int i = 0; i < enums.length; i++) {
      Enum en = enums[i];
      if (en.name().equals(name)) {
        return en;
      }
    }
    return null;
  }

  @Override
  public Object convertObjectToPb(Object value) {

    if (null == value) {
      return null;
    }

    return field.getEnumType().findValueByName(((Enum<?>) value).name());
  }
}
