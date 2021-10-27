package cn.org.merlin.beans;

import com.google.common.base.MoreObjects;

import java.time.Year;

public class YearTestBean {

  Year year;

  public Year getYear() {
    return year;
  }

  public void setYear(Year year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("year", year).toString();
  }
}
