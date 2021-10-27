package com.merlin.convert.reflect;

import com.merlin.convert.multi.MultiTypeWrapper;
import com.merlin.convert.tool.ClassUtils;

import java.lang.reflect.Type;

public class SortedReflect implements Reflectable {

  private int index;
  private Type[] parameterTypes;
  private Type type;
  private boolean isArray;

  public SortedReflect(MultiTypeWrapper wrapper, int index) {
    this.index = index;
    this.type = wrapper.getTypes()[index];
    this.parameterTypes = ClassUtils.getParameterTypes(type);
    this.isArray = ClassUtils.resolveType(type).isArray();
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
    return ((MultiTypeWrapper) target).getValues()[index];
  }

  @Override
  public void set(Object target, Object value) {
    ((MultiTypeWrapper) target).getValues()[index] = value;
  }
}
