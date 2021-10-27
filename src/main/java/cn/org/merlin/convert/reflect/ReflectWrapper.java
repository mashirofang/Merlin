package cn.org.merlin.convert.reflect;

import java.lang.reflect.Type;

public class ReflectWrapper implements Reflectable {

  private Class<?> target;
  private Reflectable reflectable;

  public ReflectWrapper(Class<?> target, Reflectable reflectable) {
    this.target = target;
    this.reflectable = reflectable;
  }

  @Override
  public Type getType() {
    return target;
  }

  @Override
  public Type[] getParameterTypes() {
    return reflectable.getParameterTypes();
  }

  @Override
  public boolean isArray() {
    return reflectable.isArray();
  }

  @Override
  public Object get(Object target) {
    return reflectable.get(target);
  }

  @Override
  public void set(Object target, Object value) {
    reflectable.set(target, value);
  }
}
