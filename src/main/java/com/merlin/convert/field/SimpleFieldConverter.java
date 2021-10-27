package com.merlin.convert.field;

import com.merlin.Merlin;
import com.merlin.api.TypeAdaptor;
import com.merlin.convert.reflect.Reflectable;
import com.merlin.convert.tool.ClassUtils;
import com.merlin.convert.tool.ProtoHelper;
import com.merlin.convert.wrap.ObjectWrapper;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class SimpleFieldConverter implements FieldConverter {

  private static final Logger logger = LoggerFactory.getLogger(SimpleFieldConverter.class);

  protected Message protoType;
  protected FieldDescriptor field;
  protected Reflectable reflectable;
  protected Type type;
  protected Class target;
  protected TypeAdaptor adaptor;
  protected Merlin merlin;

  protected SimpleFieldConverter(
      Message protoType, FieldDescriptor field, Reflectable reflectable, Merlin merlin) {
    this.protoType = protoType;
    this.field = field;
    this.reflectable = reflectable;
    this.type = getSingleTargetType();
    this.target = ClassUtils.resolveType(type);
    this.merlin = merlin;
    this.adaptor = merlin.getAdaptor(field.getType(), target);
  }

  public abstract Object convertPbToObject(Object pbValue);

  public abstract Object convertObjectToPb(Object value);

  protected Object adaptOrConvertPbToObject(Object pbValue) {
    if (null != adaptor) {
      return adaptor.sourceToTarget(pbValue);
    } else {
      return convertPbToObject(pbValue);
    }
  }

  protected Object adaptOrConvertObjectToPb(Object value) {
    if (null != adaptor) {
      return adaptor.targetToSource(value);
    } else {
      return convertObjectToPb(value);
    }
  }

  protected Type getSingleTargetType() {
    Type target;
    if (field.isRepeated()) {
      Type[] parameterTypes = reflectable.getParameterTypes();
      if (null != parameterTypes && parameterTypes.length == 1) {
        target = reflectable.getParameterTypes()[0];
      } else {
        return Object.class;
      }
    } else {
      target = reflectable.getType();
    }
    return target;
  }

  @Override
  public void buildPbField(Message.Builder builder, Object source) {

    Object value = reflectable.get(source);

    if (null == value) {
      return;
    }

    // single
    if (!field.isRepeated()) {
      Object pbValue = adaptOrConvertObjectToPb(value);
      if (null != pbValue) {
        builder.setField(field, pbValue);
      }
    } else if (field.isMapField()) {
      // map
      if (value instanceof Map) {
        for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
          addRepeatedField(builder, entry);
        }
      }
      // 暂时不支持 multi map
    } else if (reflectable.isArray()) {
      // array
      int length = Array.getLength(value);
      for (int i = 0; i < length; i++) {
        addRepeatedField(builder, Array.get(value, i));
      }
    } else {
      // collection
      if (value instanceof Collection) {
        for (Object obj : (Collection<?>) value) {
          addRepeatedField(builder, obj);
        }
      }
    }
  }

  @Override
  public void buildObjectField(Object object, Message source) {
    if (!(object instanceof ObjectWrapper) && !ProtoHelper.hasValue(source, field)) {
      return;
    }

    Object pbValue = source.getField(field);

    if (!this.field.isRepeated()) {
      Object value = adaptOrConvertPbToObject(pbValue);
      if (null != value) {
        reflectable.set(object, value);
      }
    } else if (this.field.isMapField()) {
      Map<Object, Object> map = ClassUtils.createMap(ClassUtils.resolveType(reflectable.getType()));
      if (null == map) {
        logger.error("create map fail! field:{}", this.field.getName());
        return;
      }
      for (MapEntry<Object, Object> entry : (List<MapEntry<Object, Object>>) pbValue) {
        Map.Entry<?, ?> value = (Map.Entry<?, ?>) adaptOrConvertPbToObject(entry);
        if (null != value && null != value.getKey() && null != value.getValue()) {
          map.put(value.getKey(), value.getValue());
        }
      }
      reflectable.set(object, map);
    } else {
      List<?> list = (List<?>) pbValue;
      if (reflectable.isArray()) {
        Object array = Array.newInstance(this.target, list.size());
        if (null == array) {
          logger.error("create array fail! field:{}", this.field.getName());
          return;
        }
        int i = 0;
        for (Object p : list) {
          Array.set(array, i, adaptOrConvertPbToObject(p));
          i++;
        }
        reflectable.set(object, array);
      } else {
        Collection<Object> collection =
            ClassUtils.createCollection(ClassUtils.resolveType(reflectable.getType()));
        if (null == collection) {
          logger.error("create collection fail! field:{}", this.field.getName());
          return;
        }
        for (Object p : list) {
          Object value = adaptOrConvertPbToObject(p);
          if (null != value) {
            collection.add(value);
          }
        }
        reflectable.set(object, collection);
      }
    }
  }

  private void addRepeatedField(Message.Builder builder, Object obj) {
    Object pbValue = adaptOrConvertObjectToPb(obj);
    if (null != pbValue) {
      builder.addRepeatedField(field, pbValue);
    }
  }
}
