package cn.org.merlin.convert.adapt;

import com.google.protobuf.ByteString;

public enum FieldType {
  INT32(Integer.class),
  UINT32(Integer.class),
  INT64(Long.class),
  UINT64(Long.class),
  FLOAT(Float.class),
  DOUBLE(Double.class),
  BOOLEAN(Boolean.class),
  STRING(String.class),
  BYTE_STRING(ByteString.class);

  private Class<?> source;

  FieldType(Class<?> source) {
    this.source = source;
  }

  public Class<?> getSource() {
    return source;
  }
}
