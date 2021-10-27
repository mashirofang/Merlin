package com.merlin.convert.field;

import com.merlin.convert.reflect.Reflectable;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.OneofDescriptor;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OneofConverter implements FieldConverter {

  private Reflectable reflectable;
  private OneofDescriptor oneofDescriptor;
  private Map<Class, SimpleFieldConverter> convertersByClass;
  private Map<String, SimpleFieldConverter> convertersByField;

  public OneofConverter(Reflectable reflectable, OneofDescriptor oneofDescriptor) {
    this.reflectable = reflectable;
    this.oneofDescriptor = oneofDescriptor;
    this.convertersByClass = new HashMap<>();
    this.convertersByField = new HashMap<>();
  }

  public void add(String field, Class clz, SimpleFieldConverter fieldConverter) {
    convertersByClass.put(clz, fieldConverter);
    convertersByField.put(field, fieldConverter);
  }

  @Override
  public void buildPbField(Message.Builder builder, Object source) {
    Object value = reflectable.get(source);
    SimpleFieldConverter fieldConverter;
    if (null != value) {
      Class<?> target = value.getClass();
      if (Map.class.isAssignableFrom(target)) {
        fieldConverter = convertersByClass.get(Map.class);
      } else if (List.class.isAssignableFrom(target)) {
        fieldConverter = convertersByClass.get(List.class);
      } else {
        fieldConverter = convertersByClass.get(value.getClass());
      }
      if (null != fieldConverter) {
        fieldConverter.buildPbField(builder, source);
      }
    }
  }

  @Override
  public void buildObjectField(Object target, Message source) {
    if (source.hasOneof(oneofDescriptor)) {
      FieldDescriptor f = source.getOneofFieldDescriptor(oneofDescriptor);
      convertersByField.get(f.getName()).buildObjectField(target, source);
    }
  }
}
