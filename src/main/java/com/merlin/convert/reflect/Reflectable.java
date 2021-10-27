package com.merlin.convert.reflect;

import java.lang.reflect.Type;

public interface Reflectable {

  Type getType();

  Type[] getParameterTypes();

  boolean isArray();

  Object get(Object target);

  void set(Object target, Object value);
}
