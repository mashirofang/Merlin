package com.merlin.convert.reflect;

import com.merlin.convert.tool.ClassUtils;
import com.merlin.convert.wrap.ObjectWrapper;

import java.lang.reflect.Type;

public class WrappedTypeReflect implements Reflectable {

  private Class<?> clz;
  private Type[] parameterTypes;
  private boolean isArray;
  private Type type;

  public WrappedTypeReflect(Type type) {
    this.type = type;
    this.clz = ClassUtils.resolveType(type);
    this.parameterTypes = ClassUtils.getParameterTypes(type);
    this.isArray = clz.isArray();
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public Type[] getParameterTypes() {
    return parameterTypes;
  }

  @Override
  public boolean isArray() {
    return isArray;
  }

  @Override
  public Object get(Object target) {
    return ((ObjectWrapper) target).getValue();
  }

  @Override
  public void set(Object target, Object value) {
    ((ObjectWrapper) target).setValue(value);
  }
}
