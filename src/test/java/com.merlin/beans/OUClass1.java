package com.merlin.beans;

import com.google.common.base.MoreObjects;

public class OUClass1 extends OUBase {

  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("name", name).add("id", id).toString();
  }
}
