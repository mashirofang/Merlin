package cn.org.merlin.beans;

import com.google.common.base.MoreObjects;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConvertTestBean {

  private int f1;
  private char f2;
  private byte f3;
  private short f4;

  private long f5;
  private float f6;
  private double f7;
  private boolean f8;
  private String f9;

  private byte[] f10;
  private Type f11;
  private Date f12;

  private List<String> f13;

  private Map<String, Integer> f14;
  private Map<String, ConvertUser> f15;
  private ConvertUser f16;

  private String[] f17;

  public int getF1() {
    return f1;
  }

  public void setF1(int f1) {
    this.f1 = f1;
  }

  public char getF2() {
    return f2;
  }

  public void setF2(char f2) {
    this.f2 = f2;
  }

  public byte getF3() {
    return f3;
  }

  public void setF3(byte f3) {
    this.f3 = f3;
  }

  public short getF4() {
    return f4;
  }

  public void setF4(short f4) {
    this.f4 = f4;
  }

  public long getF5() {
    return f5;
  }

  public void setF5(long f5) {
    this.f5 = f5;
  }

  public float getF6() {
    return f6;
  }

  public void setF6(float f6) {
    this.f6 = f6;
  }

  public double getF7() {
    return f7;
  }

  public void setF7(double f7) {
    this.f7 = f7;
  }

  public boolean isF8() {
    return f8;
  }

  public void setF8(boolean f8) {
    this.f8 = f8;
  }

  public String getF9() {
    return f9;
  }

  public void setF9(String f9) {
    this.f9 = f9;
  }

  public byte[] getF10() {
    return f10;
  }

  public void setF10(byte[] f10) {
    this.f10 = f10;
  }

  public Type getF11() {
    return f11;
  }

  public void setF11(Type f11) {
    this.f11 = f11;
  }

  public Date getF12() {
    return f12;
  }

  public void setF12(Date f12) {
    this.f12 = f12;
  }

  public List<String> getF13() {
    return f13;
  }

  public void setF13(List<String> f13) {
    this.f13 = f13;
  }

  public Map<String, Integer> getF14() {
    return f14;
  }

  public void setF14(Map<String, Integer> f14) {
    this.f14 = f14;
  }

  public Map<String, ConvertUser> getF15() {
    return f15;
  }

  public void setF15(Map<String, ConvertUser> f15) {
    this.f15 = f15;
  }

  public ConvertUser getF16() {
    return f16;
  }

  public void setF16(ConvertUser f16) {
    this.f16 = f16;
  }

  public String[] getF17() {
    return f17;
  }

  public void setF17(String[] f17) {
    this.f17 = f17;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("f1", f1)
        .add("f2", f2)
        .add("f3", f3)
        .add("f4", f4)
        .add("f5", f5)
        .add("f6", f6)
        .add("f7", f7)
        .add("f8", f8)
        .add("f9", f9)
        .add("f10", f10)
        .add("f11", f11)
        .add("f12", f12)
        .add("f13", f13)
        .add("f14", f14)
        .add("f15", f15)
        .add("f16", f16)
        .add("f17", f17)
        .toString();
  }
}
