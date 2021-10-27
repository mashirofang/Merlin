package cn.org.merlin.convert.entry;

import cn.org.merlin.convert.field.FieldConverterFactory;
import cn.org.merlin.convert.field.SimpleFieldConverter;
import cn.org.merlin.convert.reflect.EntryReflect;
import cn.org.merlin.Merlin;
import cn.org.merlin.api.MessageConverter;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapEntry.Builder;

import java.lang.reflect.Type;
import java.util.Map;

public class EntryConverter implements MessageConverter<MapEntry, Map.Entry> {

  private static final String KEY = "key";
  private static final String VALUE = "value";

  private MapEntry protoType;
  private SimpleEntry entryType;

  private SimpleFieldConverter keyConverter;
  private SimpleFieldConverter valueConverter;

  private Merlin merlin;

  public EntryConverter(MapEntry protoType, Type keyType, Type valueType, Merlin merlin) {
    this.protoType = protoType;
    this.entryType = new SimpleEntry(keyType, valueType);

    for (FieldDescriptor field : protoType.getDescriptorForType().getFields()) {
      String fieldName = field.getName();
      if (KEY.equals(fieldName)) {
        EntryReflect reflectable = new EntryReflect(entryType, true);
        keyConverter = FieldConverterFactory.create(protoType, field, reflectable, merlin);
      } else if (VALUE.equals(fieldName)) {
        EntryReflect reflectable = new EntryReflect(entryType, false);
        valueConverter = FieldConverterFactory.create(protoType, field, reflectable, merlin);
      }
    }
  }

  @Override
  public Map.Entry sourceToTarget(MapEntry source) {
    SimpleEntry target = new SimpleEntry<>(entryType.getKeyType(), entryType.getValueType());
    keyConverter.buildObjectField(target, source);
    valueConverter.buildObjectField(target, source);
    return target;
  }

  @Override
  public MapEntry targetToSource(Map.Entry source) {
    Builder builder = protoType.newBuilderForType();
    keyConverter.buildPbField(builder, source);
    valueConverter.buildPbField(builder, source);
    return builder.build();
  }

  @Override
  public Class getSourceType() {
    return protoType.getClass();
  }

  @Override
  public Class getTargetType() {
    return SimpleEntry.class;
  }

  @Override
  public MapEntry getProtoType() {
    return protoType;
  }
}
