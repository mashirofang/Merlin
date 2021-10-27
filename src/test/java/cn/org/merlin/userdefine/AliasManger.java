package cn.org.merlin.userdefine;

import cn.org.merlin.api.AliasController;
import com.google.protobuf.Message;

import java.util.HashMap;
import java.util.Map;

public class AliasManger implements AliasController {

  static Map<String, String> map = new HashMap<>();

  static {
    map.put("id", "userID");
    map.put("name", "userName");
  }

  @Override
  public String alias(Message prototype, Class clazz, String protoFieldName) {
    return map.get(protoFieldName);
  }
}
