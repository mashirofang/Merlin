package cn.org.merlin;

import cn.org.merlin.api.TypeAdaptor;
import cn.org.merlin.userdefine.YearAdaptor;
import com.google.common.collect.Lists;
import cn.org.merlin.beans.ConvertUser2;
import org.junit.Test;

import java.util.ArrayList;

public class UserDefineConverterTest {

  TypeAdaptor adaptor = new YearAdaptor();
  ArrayList<TypeAdaptor> converters = Lists.newArrayList(adaptor);
  Merlin merlin = Merlin.newBuilder().ignoreSpi().withAdaptors(converters).build();

  @Test
  public void beanToMessage() {

    ConvertUser2 bean = new ConvertUser2();
    bean.setUserName("name1");
    bean.setUserID("id1");

    ConvertUser result = merlin.objectToMessage(bean, ConvertUser.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void messageToBean() {
    ConvertUser message = ConvertUser.newBuilder().setId("id1").setName("name1").build();
    ConvertUser2 result = merlin.messageToObject(message, ConvertUser2.class);
    System.out.print(result);
  }
}
