package com.merlin.userdefine;

import com.merlin.ConvertUser;
import com.merlin.ConvertUser.Builder;
import com.merlin.api.MessageConverter;
import com.merlin.beans.ConvertUser2;

public class UserConverter implements MessageConverter<ConvertUser, ConvertUser2> {

  @Override
  public ConvertUser2 sourceToTarget(ConvertUser source) {
    ConvertUser2 target = new ConvertUser2();
    target.setUserID(source.getId());
    target.setUserName(source.getName());
    return target;
  }

  @Override
  public ConvertUser targetToSource(ConvertUser2 source) {

    Builder builder = ConvertUser.newBuilder();
    if (null != source.getUserID()) {
      builder.setId(source.getUserID());
    }

    if (null != source.getUserName()) {
      builder.setName(source.getUserName());
    }

    return builder.build();
  }

  @Override
  public Class getSourceType() {
    return ConvertUser.class;
  }

  @Override
  public Class getTargetType() {
    return ConvertUser2.class;
  }

  @Override
  public ConvertUser getProtoType() {
    return ConvertUser.getDefaultInstance();
  }
}
