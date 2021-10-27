# Merlin
 
    Merlin （梅林） 是一个用于 protobuf message 和 java bean 转换的工具       
    
    
## Get Started    

## MAVEN

```
<dependency>
    <groupId>com.merlin</groupId>
    <artifactId>message-converter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## EXAMPLE

```

    // create instance
    Merlin merlin = Merlin.newBuilder().build();
    
    
    // object to message
    ConvertTestMessage result =
        merlin.objectToMessage(bean, ConvertTestMessage.getDefaultInstance());

    // message to object
    ConvertTestBean result = merlin.messageToObject(message, ConvertTestBean.class);

```

[example](./src/test/java/com.merlin/ConverterTest.java)

## FIELD 对应关系
    
| #    |   protoType  | javaType | remark    |
| ---: |--------------| -------- |-----------|
| 1    |  int32       |   int    | basic     | 
| 2    |  int32       |   char   | basic     | 
| 3    |  int32       |   byte   | basic     | 
| 4    |  int32       |   short  | basic     |  
| 5    |  int64       |   long   | basic     | 
| 6    |  float       |   float  | basic     | 
| 7    |  double      |   double | basic     | 
| 8    |  boolean     |   boolean| basic     | 
| 9    |  string      |   string | basic     | 
| 10   |  bytes       |   byte[] | basic     |
| 11   |  enum        |   enum   | enum      |  
| 12   |  message     |   bean   | bean      |
| 13   |  int64       |   Date   | special   |  
| 14   |  map         |   map    | container |  
| 15   |  repeated    |   array  | container |  
| 16   |  repeated    |   list   | container |  
| 16   |  repeated    |   set    | container |  

## WRAP 能力

对于所有非 message 的类型, 支持在 proto 中使用 message 进行一层包装 , 包装后的结构可以正常转换

例如: String 和 Message 转换

```
message StringWrap{
  string value = 1;
}
```
   
### null 值的问题

Integer , Long 等包装类, 需要对 null 进行区分时, 可以使用 wrap 能力包装 (pb 的基本类型没有 null)
google 官方也直接提供了对应包装型 message (com.google.Int32Value , com.google.Int64Value 等) , 可以直接使用    



```
class Bean{
  Integer id;
}
```

```
syntax = "proto3";

option java_package = "com.merlin";
import "google/protobuf/wrappers.proto";

message BeanMessage {
   com.google.Int32Value id = 1;
}

```

不需要对 null 进行区分时, 则直接使用基本类型 (int32 ,int64 等) 

## 多维数组或多重容器

可以使用 wrap 能力对每层的结构进行包装,  例如 Map<String, List<String> >  对应的 message :

```
message StringMultimap {
    map<string, StringList> values = 1;
}

message StringList {
    repeated string values = 1;
}
``` 

## 自定义

### 注册 message 转换规则

实现 MessageConverter 接口, 自定义 message -> bean 的转换规则, 注册后适用于整个 merlin 实例 , 支持直接调用方法注册和 SPI 注册

[示例](./src/test/java/com.merlin/userdefine/UserConverter.java)

```
  UserConverter userConverter = new UserConverter();
  ArrayList<MessageConverter> converters = Lists.newArrayList(userConverter);
  Merlin merlin = Merlin.newBuilder().withConverters(converters).build();
```

[SPI示例](./src/test/resources/META-INF/services/com.merlin.api.MessageConverter) 
在 resources 下创建 META-INF/services/MessageConverter 文件, 文件内一行一个填入实现的转换器

### 注册 field 转换规则

实现 TypeAdaptor 接口, 自定义 field -> bean 的转换规则, 注册后适用于整个 merlin 实例 , 支持直接调用方法注册和 SPI 注册, 示例:

###  支持的类型

| #    |   protoType  | fieldType |
| ---: |--------------| -------- |
| 1    |  int32,sint32,sfixed32 |   INT32    | 
| 2    |  uint32,fixed32|   UINT32   | 
| 3    |  int64,sint64,sfixed64     |   INT64   | 
| 4    |  uint64,fixed64       |   UINT64  | 
| 5    |  float       |   FLOAT   | 
| 6    |  double       |   DOUBLE  | 
| 7    |  boolean      |   BOOLEAN | 
| 8    |  string       |   STRING  | 
| 9    |  bytes      |   BYTE_STRING | 


[示例](./src/test/java/com.merlin/UserDefineAdaptorTest.java)

```
  UserConverter userConverter = new UserConverter();
  ArrayList<MessageConverter> converters = Lists.newArrayList(userConverter);
  Merlin merlin = Merlin.newBuilder().withConverters(converters).build();
```

[SPI示例](./src/test/resources/META-INF/services/com.merlin.api.TypeAdaptor) 
在 resources 下创建 META-INF/services/MessageConverter 文件, 文件内一行一个填入实现的属性转换


### 注册别名识别



[示例](./src/test/java/com.merlin/ProtoAliasTest.java):


merlin 提供了全局别名控制器和 protobuf_field_option 别名能力
protobuf(注意要 import merlin 的 descriptor):
```
syntax = "proto3";

option java_package = "com.merlin";
option java_multiple_files = true;
import "merlin/Descriptor.proto";

message AliasTestMessage1 {
    string id = 1 [(field_alias) = "userID"];
    string name = 2 [(field_alias) = "userName"];
}
```

全局控制:

```
Merlin merlin = Merlin.newBuilder().withAliasController(new AliasManger()).build();
```

[SPI示例](./src/test/resources/META-INF/services/com.merlin.api.AliasController) 
在 resources 下创建 META-INF/services/AliasController 文件, 只支持一个


## 继承/多态 -> oneof

如果 javabean 中用了父类或者抽象类 , message 中需要定义对应的 oneof 字段


[proto_example](./prototest/test/OneofTest.proto)

[convert_example](./src/test/java/com.merlin/OneofTypeTest.java)


















