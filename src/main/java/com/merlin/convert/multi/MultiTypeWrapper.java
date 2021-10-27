package com.merlin.convert.multi;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

public class MultiTypeWrapper {

  private MultiTypeWrapper() {}

  private Type[] types;
  private Object[] values;

  public Type[] getTypes() {
    return types;
  }

  public Object[] getValues() {
    return values;
  }

  public static MultiTypeWrapper fromTypes(Type[] types) {
    Objects.requireNonNull(types);
    MultiTypeWrapper wrapper = new MultiTypeWrapper();
    wrapper.types = Arrays.copyOf(types, types.length);
    wrapper.values = new Object[types.length];
    return wrapper;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("MultiTypeWrapper{");
    sb.append("types=").append(types == null ? "null" : Arrays.asList(types).toString());
    sb.append(", values=").append(values == null ? "null" : Arrays.asList(values).toString());
    sb.append('}');
    return sb.toString();
  }

  public static MultiTypeWrapper fromTypesAndValues(Type[] types, Object[] values) {
    Objects.requireNonNull(types);
    Objects.requireNonNull(values);
    if (types.length != values.length) {
      throw new IllegalArgumentException("count of types must equal count of values");
    }
    MultiTypeWrapper wrapper = new MultiTypeWrapper();
    wrapper.types = Arrays.copyOf(types, types.length);
    wrapper.values = Arrays.copyOf(values, values.length);
    return wrapper;
  }
}
