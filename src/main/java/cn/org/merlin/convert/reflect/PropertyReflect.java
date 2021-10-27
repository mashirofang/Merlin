package cn.org.merlin.convert.reflect;

import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import cn.org.merlin.convert.tool.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReflect implements Reflectable {

  private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private PropertyDescriptor property;
  private Method readMethod;
  private Method writeMethod;
  private Type[] parameterTypes;
  private boolean isArray;
  private Type type;

  public PropertyReflect(PropertyDescriptor property) {
    this.property = property;
    this.readMethod = property.getReadMethod();
    this.writeMethod = property.getWriteMethod();
    this.parameterTypes = ClassUtils.getParameterTypes(readMethod.getGenericReturnType());
    this.isArray = readMethod.getReturnType().isArray();
    this.type = readMethod.getGenericReturnType();
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
      value = readMethod.invoke(target, null);
    } catch (Exception e) {
      // nop
      logger.error("get field:{} from target fail! target:{},e:{}", property.getName(), target, e);
    }
    return value;
  }

  @Override
  public void set(Object target, Object value) {

    try {
      writeMethod.invoke(target, value);
    } catch (Exception e) {
      // nop
      logger.error("set field:{} to target fail! target:{},e", property.getName(), target, e);
    }
  }
}
