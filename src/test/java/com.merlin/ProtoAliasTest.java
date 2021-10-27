package com.merlin;

import com.merlin.beans.ConvertUser2;
import com.merlin.beans.ConvertUser3;
import org.junit.Test;

public class ProtoAliasTest {

  Merlin merlin = Merlin.newBuilder().ignoreSpi().build();

  @Test
  public void beanToMessage() {

    ConvertUser3 bean = new ConvertUser3();
    bean.setUserName("name1");
    bean.setUserID("id1");

    AliasTestMessage1 result = merlin.objectToMessage(bean, AliasTestMessage1.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void messageToBean() {
    AliasTestMessage1 message =
        AliasTestMessage1.newBuilder().setId("id1").setName("name1").build();
    ConvertUser3 result = merlin.messageToObject(message, ConvertUser3.class);
    System.out.print(result);
  }
}
