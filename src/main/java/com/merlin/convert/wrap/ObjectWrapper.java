package com.merlin.convert.wrap;

public class ObjectWrapper {

  private Object value;

  public ObjectWrapper() {}

  public ObjectWrapper(Object value) {
    this.value = value;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object object) {
    this.value = object;
  }
}
