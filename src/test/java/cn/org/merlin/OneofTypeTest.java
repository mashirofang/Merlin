package cn.org.merlin;

import cn.org.merlin.beans.OUClass1;
import cn.org.merlin.beans.OUClass2;
import cn.org.merlin.beans.OUWrapper;
import org.junit.Test;

public class OneofTypeTest {

  Merlin merlin = Merlin.newBuilder().ignoreSpi().build();

  @Test
  public void beanToMessage() {

    OUWrapper bean = new OUWrapper();
    OUClass1 value = new OUClass1();
    value.setId(111);
    value.setName("name1");

    bean.setValue(value);

    Tof result = merlin.objectToMessage(bean, Tof.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void beanToMessage2() {

    OUWrapper bean = new OUWrapper();
    OUClass2 value = new OUClass2();
    value.setId(111);
    value.setType("ty1");

    bean.setValue(value);

    Tof result = merlin.objectToMessage(bean, Tof.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void messageToBean() {

    Tof message =
        Tof.newBuilder().setV1(TofInfo1.newBuilder().setId(123).setName("name").build()).build();

    OUWrapper result = merlin.messageToObject(message, OUWrapper.class);
    System.out.print(result);
  }

  @Test
  public void messageToBean2() {

    Tof message =
        Tof.newBuilder().setV2(TofInfo2.newBuilder().setId(123).setType("type").build()).build();

    OUWrapper result = merlin.messageToObject(message, OUWrapper.class);
    System.out.print(result);
  }
}
