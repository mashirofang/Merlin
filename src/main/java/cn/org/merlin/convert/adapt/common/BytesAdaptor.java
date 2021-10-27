package cn.org.merlin.convert.adapt.common;

import cn.org.merlin.convert.adapt.FieldType;
import cn.org.merlin.api.TypeAdaptor;

import java.nio.charset.StandardCharsets;

public class BytesAdaptor implements TypeAdaptor<String, byte[]> {

  @Override
  public FieldType getType() {
    return FieldType.STRING;
  }

  @Override
  public byte[] sourceToTarget(String s) {
    return null == s ? null : s.getBytes();
  }

  @Override
  public String targetToSource(byte[] bytes) {
    return null == bytes ? null : new String(bytes, StandardCharsets.UTF_8);
  }

  @Override
  public Class<byte[]> getTargetType() {
    return byte[].class;
  }
}
