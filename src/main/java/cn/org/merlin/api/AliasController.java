package cn.org.merlin.api;

import com.google.protobuf.Message;

public interface AliasController {

  /**
   * 用于别名管理，protobuf 中的 field 与 javabean field 的映射 ，默认两者相同
   *
   * @param prototype
   * @param clazz
   * @param protoFieldName
   * @return alias of clazz name
   */
  String alias(Message prototype, Class clazz, String protoFieldName);
}
