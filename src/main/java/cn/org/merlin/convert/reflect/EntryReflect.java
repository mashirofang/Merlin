package cn.org.merlin.convert.reflect;

import cn.org.merlin.convert.entry.SimpleEntry;
import cn.org.merlin.convert.tool.ClassUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class EntryReflect implements Reflectable {

  private boolean isKey;
  private Type type;
  private Type[] parameterTypes;

  public EntryReflect(SimpleEntry simpleEntry, boolean isKey) {
    this.isKey = isKey;
    this.type = isKey ? simpleEntry.getKeyType() : simpleEntry.getValueType();
    this.parameterTypes = ClassUtils.getParameterTypes(type);
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
    return false;
  }

  @Override
  public Object get(Object target) {
    return isKey ? ((Map.Entry) target).getKey() : ((Map.Entry) target).getValue();
  }

  @Override
  public void set(Object target, Object value) {
    if (isKey) {
      ((SimpleEntry) target).setKey(value);
    } else {
      ((SimpleEntry) target).setValue(value);
    }
  }
}
