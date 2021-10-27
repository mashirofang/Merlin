package com.merlin.convert.adapt.common;

import com.merlin.api.TypeAdaptor;
import com.merlin.convert.adapt.FieldType;

import java.sql.Date;

public class SqlDateAdaptor implements TypeAdaptor<Long, Date> {

  @Override
  public Long targetToSource(Date date) {
    return null == date ? null : date.getTime();
  }

  @Override
  public Date sourceToTarget(Long source) {
    return null == source ? null : new Date(source);
  }

  @Override
  public Class<Date> getTargetType() {
    return Date.class;
  }

  @Override
  public FieldType getType() {
    return FieldType.INT64;
  }
}
