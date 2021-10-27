package cn.org.merlin.beans;

import com.google.common.base.MoreObjects;

public class OUWrapper {

  private OUBase value;

  public OUBase getValue() {
    return value;
  }

  public void setValue(OUBase value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("value", value).toString();
  }
}
