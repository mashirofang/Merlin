package com.merlin;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.merlin.beans.ConvertTestBean;
import com.merlin.beans.ConvertUser;
import com.merlin.beans.Type;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConverterTest {

  static ConvertTestBean bean;
  static ConvertTestMessage message;

  static {
    buildBean();
    buildMessage();
  }

  static void buildMessage() {
    message =
        ConvertTestMessage.newBuilder()
            .setF1(1)
            .setF2(2)
            .setF3(3)
            .setF4(4)
            .setF5(5)
            .setF6(6)
            .setF7(7)
            .setF8(true)
            .setF9("f9")
            .setF10(ByteString.copyFrom("f10".getBytes()))
            .setF11(com.merlin.Type.svip)
            .setF12(new Date(1000).getTime())
            .addAllF13(Lists.newArrayList("f13_1", "f13_2"))
            .putF14("k1", 1000)
            .putF14("k2", 2000)
            .putF15("q1", com.merlin.ConvertUser.newBuilder().setId("id1").setName("name1").build())
            .putF15("q2", com.merlin.ConvertUser.newBuilder().setId("id2").setName("name2").build())
            .setF16(com.merlin.ConvertUser.newBuilder().setId("id3").setName("name3").build())
            .addAllF17(Lists.newArrayList("a1", "a2", "a3"))
            .build();
  }

  static void buildBean() {

    bean = new ConvertTestBean();
    bean.setF1(1);
    bean.setF2((char) 2);
    bean.setF3((byte) 3);
    bean.setF4((short) 4);
    bean.setF5(5l);
    bean.setF6(6f);
    bean.setF7(7d);
    bean.setF8(true);
    bean.setF9("f9");
    bean.setF10("f10".getBytes());
    bean.setF11(Type.svip);
    bean.setF12(new Date(1000));
    bean.setF13(Lists.newArrayList("f13_1", "f13_2"));

    Map<String, Integer> map = new HashMap<>();
    map.put("k1", 1000);
    map.put("k2", 2000);
    bean.setF14(map);

    Map<String, ConvertUser> map2 = new HashMap<>();

    ConvertUser user = new ConvertUser();
    user.setId("id1");
    user.setName("name1");
    map2.put("q1", user);

    ConvertUser user2 = new ConvertUser();
    user2.setId("id2");
    user2.setName("name2");
    map2.put("q2", user2);

    bean.setF15(map2);
    bean.setF16(new ConvertUser("id3", "name3"));

    String[] array = {"a1", "a2", "a3"};
    bean.setF17(array);
  }

  @Test
  public void beanToMessage() {
    Merlin merlin = Merlin.newBuilder().build();
    ConvertTestMessage result =
        merlin.objectToMessage(bean, ConvertTestMessage.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void messageToBean() {
    Merlin merlin = Merlin.newBuilder().build();
    ConvertTestBean result = merlin.messageToObject(message, ConvertTestBean.class);
    System.out.print(result);
  }
}
