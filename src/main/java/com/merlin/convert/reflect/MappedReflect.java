package com.merlin.convert.reflect;

import com.merlin.convert.tool.ClassUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class MappedReflect implements Reflectable {

  private Object k;
  private Type v;
  private Type[] types;

  public MappedReflect(Object k, Type v) {
    this.k = k;
    this.v = v;
    this.types = ClassUtils.getParameterTypes(v);
  }

  @Override
  public Type getType() {
    return v;
  }

  @Override
  public Type[] getParameterTypes() {
    return types;
  }

  @Override
  public boolean isArray() {
    return false;
  }

  @Override
  public Object get(Object target) {
    return ((Map) target).get(k);
  }

  @Override
  public void set(Object target, Object value) {
    ((Map) target).put(k, value);
  }
}
