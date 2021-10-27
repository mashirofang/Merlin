package com.merlin.convert.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils {

  private static final Logger logger = LoggerFactory.getLogger(UnsafeUtils.class);
  private static Unsafe unsafe = getUnsafe();

  private UnsafeUtils() {}

  private static synchronized Unsafe getUnsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      unsafe = (Unsafe) field.get(null);
    } catch (Throwable e) {
      // noop
    }
    return unsafe;
  }

  public static Object allocateInstance(Class<?> cls) {
    try {
      return unsafe.allocateInstance(cls);
    } catch (Throwable e) {
      logger.debug("can't create instance for class:{}, e{}", cls, e);
      return null;
    }
  }
}
