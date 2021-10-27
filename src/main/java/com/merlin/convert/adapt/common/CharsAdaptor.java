package com.merlin.convert.adapt.common;

import com.merlin.convert.adapt.FieldType;
import com.merlin.api.TypeAdaptor;

public class CharsAdaptor implements TypeAdaptor<String, char[]> {

  @Override
  public FieldType getType() {
    return FieldType.STRING;
  }

  @Override
  public char[] sourceToTarget(String s) {
    return null == s ? null : s.toCharArray();
  }

  @Override
  public String targetToSource(char[] chars) {
    return null == chars ? null : new String(chars);
  }

  @Override
  public Class<char[]> getTargetType() {
    return char[].class;
  }
}
