package com.merlin;

import com.google.common.collect.Lists;
import com.merlin.api.MessageConverter;
import com.merlin.beans.ConvertUser2;
import com.merlin.beans.YearTestBean;
import com.merlin.userdefine.UserConverter;
import org.junit.Test;

import java.time.Year;
import java.util.ArrayList;

public class UserDefineAdaptorTest {

  UserConverter userConverter = new UserConverter();
  ArrayList<MessageConverter> converters = Lists.newArrayList(userConverter);
  Merlin merlin = Merlin.newBuilder().ignoreSpi().withConverters(converters).build();

  @Test
  public void beanToMessage() {

    YearTestBean bean = new YearTestBean();
    bean.setYear(Year.now());

    YearTestMessage result = merlin.objectToMessage(bean, YearTestMessage.getDefaultInstance());
    System.out.print(result);
  }

  @Test
  public void messageToBean() {
    YearTestMessage message = YearTestMessage.newBuilder().setYear(2021).build();
    YearTestBean result = merlin.messageToObject(message, YearTestBean.class);
    System.out.print(result);
  }
}
