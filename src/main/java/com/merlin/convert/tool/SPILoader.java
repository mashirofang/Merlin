package com.merlin.convert.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

public final class SPILoader {


  public static <T> List<T> load(Class<T> clz) {
    Objects.requireNonNull(clz);

    ServiceLoader<T> services = ServiceLoader.load(clz);
    List<T> result = new ArrayList<>();
    if (null != services) {
      for (T converter : services) {
        result.add(converter);
      }
    }
    return result;
  }

}
