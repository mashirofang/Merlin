package cn.org.merlin.userdefine;

import cn.org.merlin.api.TypeAdaptor;
import cn.org.merlin.convert.adapt.FieldType;

import java.time.Year;


public class YearAdaptor implements TypeAdaptor<Integer, Year> {

  @Override
  public FieldType getType() {
    return FieldType.INT32;
  }

  @Override
  public Year sourceToTarget(Integer s) {
    return null == s ? null : Year.of(s);
  }

  @Override
  public Integer targetToSource(Year year) {
    return null == year ? null : year.getValue();
  }

  @Override
  public Class getTargetType() {
    return Year.class;
  }
}
