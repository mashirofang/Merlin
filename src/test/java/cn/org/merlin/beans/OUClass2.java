package cn.org.merlin.beans;

import com.google.common.base.MoreObjects;

public class OUClass2 extends OUBase {

  private String type;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("type", type).add("id", id).toString();
  }
}
