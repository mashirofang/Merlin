package com.merlin.convert.entry;

import java.lang.reflect.Type;
import java.util.Map;

public class SimpleEntry<K,V> implements Map.Entry<K,V> {
  private K key;
  private V value;
  private Type keyType;
  private Type valueType;

  public SimpleEntry(Type keyType, Type valueType) {
    this.keyType = keyType;
    this.valueType = valueType;
  }

  @Override
  public K getKey() {
    return key;
  }

  @Override
  public V getValue() {
    return value;
  }

  public void setKey(K key) {
    this.key = key;
  }

  @Override
  public V setValue(V value) {
    this.value = value;
    return value;
  }

  public Type getKeyType() {
    return keyType;
  }

  public Type getValueType() {
    return valueType;
  }
}
