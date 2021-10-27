package cn.org.merlin;

import cn.org.merlin.beans.ConvertUser3;
import cn.org.merlin.userdefine.AliasManger;
import org.junit.Test;

public class ControllerAliasTest {

  Merlin merlin = Merlin.newBuilder().ignoreSpi().withAliasController(new AliasManger()).build();

  @Test
  public void beanToMessage() {

    ConvertUser3 bean = new ConvertUser3();
    bean.setUserName("name1");
    bean.setUserID("id1");

    AliasTestMessage2 result = merlin.objectToMessage(bean, AliasTestMessage2.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void messageToBean() {
    AliasTestMessage2 message =
        AliasTestMessage2.newBuilder().setId("id1").setName("name1").build();
    ConvertUser3 result = merlin.messageToObject(message, ConvertUser3.class);
    System.out.print(result);
  }
}
