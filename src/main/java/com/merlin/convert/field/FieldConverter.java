package com.merlin.convert.field;

import com.google.protobuf.Message;

public interface FieldConverter {

  void buildObjectField(Object target, Message source);

  void buildPbField(Message.Builder builder, Object source);
}
