package cn.org.merlin.api;

import cn.org.merlin.convert.adapt.FieldType;

public interface TypeAdaptor<SOURCE, TARGET> extends Converter<SOURCE, TARGET> {

  FieldType getType();

  @Override
  default Class<SOURCE> getSourceType() {
    return (Class<SOURCE>) getType().getSource();
  }
}
