package com.merlin.convert.adapt.common;

import com.merlin.api.TypeAdaptor;
import com.merlin.convert.adapt.FieldType;

import java.sql.Timestamp;

public class TimestampAdaptor implements TypeAdaptor<Long, Timestamp> {

  @Override
  public Long targetToSource(Timestamp timestamp) {
    return null == timestamp ? null : timestamp.getTime();
  }

  @Override
  public Timestamp sourceToTarget(Long source) {
    return null == source ? null : new Timestamp(source);
  }

  @Override
  public Class<Timestamp> getTargetType() {
    return Timestamp.class;
  }

  @Override
  public FieldType getType() {
    return FieldType.INT64;
  }
}
