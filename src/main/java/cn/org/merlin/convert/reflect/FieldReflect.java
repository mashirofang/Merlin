package cn.org.merlin.convert.reflect;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import cn.org.merlin.convert.tool.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FieldReflect implements Reflectable {

  private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  protected Field field;
  private Type[] parameterTypes;
  private boolean isArray;
  private Type type;

  public FieldReflect(Field field) {
    this.field = field;
    this.field.setAccessible(true);
    this.parameterTypes = ClassUtils.getParameterTypes(field.getGenericType());
    this.isArray = field.getType().isArray();
    this.type = field.getGenericType();
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
    Object value = null;

    try {
      value = this.field.get(target);
    } catch (IllegalAccessException e) {
      // nop
      logger.error("get field:{} from target fail! target:{},e:{}", field.getName(), target, e);
    }
    return value;
  }

  @Override
  public void set(Object target, Object value) {
    try {
      this.field.set(target, value);
    } catch (IllegalAccessException e) {
      // nop
      logger.error("set field:{} to target fail! target:{},e", field.getName(), target, e);
    }
  }
}
