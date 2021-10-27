package cn.org.merlin.api;

import cn.org.merlin.convert.tool.ProtoHelper;
import com.google.protobuf.Message;

public interface MessageConverter<M extends Message, T> extends Converter<M, T> {

  /**
   * 将目标对象转回一个 message
   *
   * @param target
   * @return
   */
  @Override
  M targetToSource(T target);

  /**
   * 获取 proto 的原型, 提供一个基于反射的默认实现
   *
   * @return
   */
  default M getProtoType() {
    return ProtoHelper.getDefaultInstance(getSourceType());
  }
}
