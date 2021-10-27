package cn.org.merlin.convert.adapt.common;

import cn.org.merlin.api.TypeAdaptor;
import cn.org.merlin.convert.adapt.FieldType;

import java.util.Date;

public class DateAdaptor implements TypeAdaptor<Long, Date> {

  @Override
  public Long targetToSource(Date target) {
    return null == target ? null : target.getTime();
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
