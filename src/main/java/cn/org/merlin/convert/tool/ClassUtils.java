package cn.org.merlin.convert.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Closeable;
import java.io.Externalizable;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ClassUtils {

  private static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  /** Suffix for array class names: {@code "[]"}. */
  public static final String ARRAY_SUFFIX = "[]";

  /** Prefix for internal array class names: {@code "["}. */
  private static final String INTERNAL_ARRAY_PREFIX = "[";

  /** Prefix for internal non-primitive array class names: {@code "[L"}. */
  private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";

  /** The package separator character: {@code '.'}. */
  private static final char PACKAGE_SEPARATOR = '.';

  /** The path separator character: {@code '/'}. */
  private static final char PATH_SEPARATOR = '/';

  /** The inner class separator character: {@code '$'}. */
  private static final char INNER_CLASS_SEPARATOR = '$';

  /**
   * Map with primitive wrapper type as key and corresponding primitive type as value, for example:
   * Integer.class -> int.class.
   */
  private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new IdentityHashMap<>(8);

  /**
   * Map with primitive type as key and corresponding wrapper type as value, for example: int.class
   * -> Integer.class.
   */
  private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new IdentityHashMap<>(8);

  /**
   * Map with primitive type name as key and corresponding primitive type as value, for example:
   * "int" -> "int.class".
   */
  private static final Map<String, Class<?>> primitiveTypeNameMap = new HashMap<>(32);

  /**
   * Map with common Java language class name as key and corresponding Class as value. Primarily for
   * efficient deserialization of remote invocations.
   */
  private static final Map<String, Class<?>> commonClassCache = new HashMap<>(64);

  /**
   * Common Java language interfaces which are supposed to be ignored when searching for 'primary'
   * user-level interfaces.
   */
  private static final Set<Class<?>> javaLanguageInterfaces;

  static {
    primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
    primitiveWrapperTypeMap.put(Byte.class, byte.class);
    primitiveWrapperTypeMap.put(Character.class, char.class);
    primitiveWrapperTypeMap.put(Double.class, double.class);
    primitiveWrapperTypeMap.put(Float.class, float.class);
    primitiveWrapperTypeMap.put(Integer.class, int.class);
    primitiveWrapperTypeMap.put(Long.class, long.class);
    primitiveWrapperTypeMap.put(Short.class, short.class);

    // Map entry iteration is less expensive to initialize than forEach with lambdas
    for (Map.Entry<Class<?>, Class<?>> entry : primitiveWrapperTypeMap.entrySet()) {
      primitiveTypeToWrapperMap.put(entry.getValue(), entry.getKey());
      registerCommonClasses(entry.getKey());
    }

    Set<Class<?>> primitiveTypes = new HashSet<>(32);
    primitiveTypes.addAll(primitiveWrapperTypeMap.values());
    Collections.addAll(
        primitiveTypes,
        boolean[].class,
        byte[].class,
        char[].class,
        double[].class,
        float[].class,
        int[].class,
        long[].class,
        short[].class);
    primitiveTypes.add(void.class);
    for (Class<?> primitiveType : primitiveTypes) {
      primitiveTypeNameMap.put(primitiveType.getName(), primitiveType);
    }

    registerCommonClasses(
        Boolean[].class,
        Byte[].class,
        Character[].class,
        Double[].class,
        Float[].class,
        Integer[].class,
        Long[].class,
        Short[].class);
    registerCommonClasses(
        Number.class,
        Number[].class,
        String.class,
        String[].class,
        Class.class,
        Class[].class,
        Object.class,
        Object[].class);
    registerCommonClasses(
        Throwable.class,
        Exception.class,
        RuntimeException.class,
        Error.class,
        StackTraceElement.class,
        StackTraceElement[].class);
    registerCommonClasses(
        Enum.class,
        Iterable.class,
        Iterator.class,
        Enumeration.class,
        Collection.class,
        List.class,
        Set.class,
        Map.class,
        Map.Entry.class,
        Optional.class);

    Class<?>[] javaLanguageInterfaceArray = {
      Serializable.class,
      Externalizable.class,
      Closeable.class,
      AutoCloseable.class,
      Cloneable.class,
      Comparable.class
    };
    registerCommonClasses(javaLanguageInterfaceArray);
    javaLanguageInterfaces = new HashSet<>(Arrays.asList(javaLanguageInterfaceArray));
  }

  /** Register the given common classes with the ClassUtils cache. */
  private static void registerCommonClasses(Class<?>... commonClasses) {
    for (Class<?> clazz : commonClasses) {
      commonClassCache.put(clazz.getName(), clazz);
    }
  }

  public static Class<?> resolvePrimitiveClassName(@Nullable String name) {
    Class<?> result = null;
    // Most class names will be quite long, considering that they
    // SHOULD sit in a package, so a length check is worthwhile.
    if (name != null && name.length() <= 8) {
      // Could be a primitive - likely.
      result = primitiveTypeNameMap.get(name);
    }
    return result;
  }

  public static Class<?> forName(String name) throws ClassNotFoundException {

    Class<?> clazz = resolvePrimitiveClassName(name);
    if (clazz == null) {
      clazz = commonClassCache.get(name);
    }
    if (clazz != null) {
      return clazz;
    }

    // "java.lang.String[]" style arrays
    if (name.endsWith(ARRAY_SUFFIX)) {
      String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
      Class<?> elementClass = forName(elementClassName);
      return Array.newInstance(elementClass, 0).getClass();
    }

    // "[Ljava.lang.String;" style arrays
    if (name.startsWith(NON_PRIMITIVE_ARRAY_PREFIX) && name.endsWith(";")) {
      String elementName = name.substring(NON_PRIMITIVE_ARRAY_PREFIX.length(), name.length() - 1);
      Class<?> elementClass = forName(elementName);
      return Array.newInstance(elementClass, 0).getClass();
    }

    // "[[I" or "[[Ljava.lang.String;" style arrays
    if (name.startsWith(INTERNAL_ARRAY_PREFIX)) {
      String elementName = name.substring(INTERNAL_ARRAY_PREFIX.length());
      Class<?> elementClass = forName(elementName);
      return Array.newInstance(elementClass, 0).getClass();
    }

    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try {
      return Class.forName(name, false, classLoader);
    } catch (ClassNotFoundException ex) {
      int lastDot = name.lastIndexOf(PACKAGE_SEPARATOR);
      if (lastDot != -1) {
        String inner =
            name.substring(0, lastDot) + INNER_CLASS_SEPARATOR + name.substring(lastDot + 1);
        try {
          return Class.forName(inner, false, classLoader);
        } catch (ClassNotFoundException ex2) {
          // Swallow - let original exception get through
        }
      }
      throw ex;
    }
  }

  public static Type[] getParameterTypes(Type type) {
    if (type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments();
    } else if (type instanceof Class) {
      Class clz = (Class) type;
      if (clz.isArray()) {
        return new Class[] {clz.getComponentType()};
      }
    }
    return null;
  }

  public static Class resolveType(Type type) {
    if (type instanceof ParameterizedType) {
      return (Class) ((ParameterizedType) type).getRawType();
    } else if (type instanceof Class) {
      return (Class) type;
    } else if (type instanceof TypeVariable) {
      return Object.class;
    }
    return Object.class;
  }

  public static Collection<Object> createCollection(Class type) {
    if (null == type) {
      return null;
    }

    Collection<Object> result = null;
    if (!Modifier.isAbstract(type.getModifiers())) {
      try {
        result = (Collection<Object>) type.getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        logger.error("collection type don't contain non-param construct! type:{}", type.getName());
      }
    } else if (type.isAssignableFrom(ArrayList.class)) {
      result = new ArrayList<>();
    } else if (type.isAssignableFrom(HashSet.class)) {
      result = new HashSet<>();
    } else {
      logger.error("create collection instance fail! type:{}", type.getName());
    }

    return result;
  }

  public static Map<Object, Object> createMap(Class type) {
    if (null == type) {
      return null;
    }

    Map<Object, Object> result = null;
    if (!Modifier.isAbstract(type.getModifiers())) {
      try {
        result = (Map) type.newInstance();
      } catch (Exception e) {
        logger.error("map type don't contain non-param construct! type:{}", type.getName());
      }
    } else if (type.isAssignableFrom(HashMap.class)) {
      result = new HashMap<>();
    } else {
      logger.error("create map instance fail! type:{}", type.getName());
    }
    return result;
  }

  public static Object createInstance(Class clz) {
    if (null == clz) {
      logger.warn("can't create instance from null!");
      return null;
    }

    if (clz.isPrimitive()) {
      if (void.class == clz) {
        return null;
      } else {
        return Array.get(Array.newInstance(clz, 1), 0);
      }
    }

    try {
      return clz.newInstance();
    } catch (Throwable error) {
      logger.debug("try to create instance by unsafe, clz:{}", clz.getName(), error);
      Object clzInstance = UnsafeUtils.allocateInstance(clz);
      if (clzInstance != null) {
        logger.debug("The {} instance has been created by unsafe.", clz.getName());
      }
      return clzInstance;
    }
  }

  public static Field getSerializableFieldFromClass(Class<?> clz, String name) {

    if (null == clz || clz == Object.class || null == name) {
      return null;
    }

    try {
      Field field = clz.getDeclaredField(name);
      if (!Modifier.isStatic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
        return field;
      }
    } catch (NoSuchFieldException e) {
      // nop
    }

    // from super
    return getSerializableFieldFromClass(clz.getSuperclass(), name);
  }

  public static boolean containsSerializableField(Class<?> clz, String name) {
    if (clz == null || name == null) {
      return false;
    }

    try {
      return null != new PropertyDescriptor(name, clz);
    } catch (IntrospectionException e) {
      return null != ClassUtils.getSerializableFieldFromClass(clz, name);
    }
  }
}
