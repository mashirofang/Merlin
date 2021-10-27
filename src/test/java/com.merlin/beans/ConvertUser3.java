package com.merlin.beans;

import com.google.common.base.MoreObjects;

public class ConvertUser3 {

  private String userID;
  private String userName;

  public String getUserID() {
    return userID;
  }

  public void setUserID(String userID) {
    this.userID = userID;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("userID", userID)
        .add("userName", userName)
        .toString();
  }
}
