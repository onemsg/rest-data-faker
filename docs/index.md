# 快速开始

Rest Data Faker 可以帮助您快速构建灵活的 Fake Data RESTful API，基于 JDK17、[DataFaker](https://www.datafaker.net/)、[Vert.X](https://vertx.io/) 和 [MUI](https://mui.com/) 构建。

![演示-前端](img/演示-2.gif){ align=left }

## 特性

- 支持创建 JSON Object 类型和 Array 类型 的 Restful API
- 数组数据接口支持 `limit` 参数
- 支持丰富的数据类型（详细可参见 [DataFaker 的 Providers](https://www.datafaker.net/documentation/providers/)）
- 支持多语言，比如英语en、中文zh_CN、韩文ko、日文ja
- 支持接口延时配置
- 支持前端控制台管理

## 启动

1. 克隆项目到本地
2. 执行 `mvn exec:java -f .\data-faker\pom.xml` 启动服务
3. 浏览器打开 `http://127.0.0.1:9000` 使用前端控制台
4. 前端部分请看 [frontend/readme](./frontend/readme.md)

> 后端默认静态路径使用 `frontend/build`

## API 使用

- `expression`: `#{xxx}` 语法参考 [DataFaker | Documentation | Expressions](https://www.datafaker.net/documentation/expressions/)
- `locale`: 参考 Java `Locale::new` 支持的参数格式

### 创建 `JsonObject` 格式数据

```
POST http://localhost:9000/api/datafaker/create-object  
Content-Type: application/json

{
    "path": "/api/people",  // 请保证路径 /api 开头
    "name": "个人信息",
    "intro": "个人信息接口",
    "expression": {
        "fullname": "#{Name.full_name}", 
        "age": "#{number.number_between '15','50'}",
        "gender": "#{Gender.binaryTypes}",
        "address": {
            "city": "#{Address.city}",
            "street": "#{Address.streetAddress}",
            "zipCode": "#{Address.zipCode}"
        }
    },
    "locale": "zh_CN"    // 英语en、中文zh_CN、韩文ko、日文ja; 缺省为 中文zh_CN
}
```
返回 `201`

当请求 `GET http://localhost:9000/api/people` 时会返回

```json
{
  "fullname": "韩昊强",
  "age": 47,
  "gender": "Male",
  "address": {
    "city": "蓬莱",
    "street": "严街065号",
    "zipCode": 904910
  }
}
```

### 创建 `JsonArray` 格式数据

```
POST http://localhost:9000/api/datafaker/create-array
Content-Type: application/json

{
    "path": "/api/people/list", // 请保证路径 /api 开头
    "name": "所有个人信息",
    "intro": "所有个人信息接口",
    "expression": {
        "fullname": "#{Name.full_name}", 
        "age": "#{number.number_between '15','50'}",
        "gender": "#{Gender.binaryTypes}",
        "address": {
            "city": "#{Address.city}",
            "street": "#{Address.streetAddress}",
            "zipCode": "#{Address.zipCode}"
        }
    },
    "locale": "zh_CN"    // 英语en、中文zh_CN、韩文ko、日文ja; 缺省为 中文zh_CN
}
```
返回 `201`

当请求 `GET http://localhost:9000/api/people/list?limit=1` 时会返回

```json
[
  {
    "fullname": "夏文轩",
    "age": 17,
    "gender": "Female",
    "address": {
      "city": "沈阳",
      "street": "蔡侬39991号",
      "zipCode": 476397
    }
  }
]
```

### 查看已创建接口

```
GET http://localhost:9000/api/datafaker/list
```

返回 `200`

```json
[
  {
    "id": 6,
    "path": "/api/people/list",
    "name": "所有个人信息",
    "intro": "所有个人信息接口",
    "expression": {
      "fullname": "#{Name.full_name}",
      "age": "#{number.number_between '15','50'}",
      "gender": "#{Gender.binaryTypes}",
      "address": {
        "city": "#{Address.city}",
        "street": "#{Address.streetAddress}",
        "zipCode": "#{Address.zipCode}"
      }
    },
    "type": "ARRAY",
    "createdTime": "2022-08-11T22:16:41.6263039"
  },
  {
    "id": 5,
    "path": "/api/people",
    "name": "个人信息",
    "intro": "个人信息接口",
    "expression": {
      "fullname": "#{Name.full_name}",
      "age": "#{number.number_between '15','50'}",
      "gender": "#{Gender.binaryTypes}",
      "address": {
        "city": "#{Address.city}",
        "street": "#{Address.streetAddress}",
        "zipCode": "#{Address.zipCode}"
      }
    },
    "type": "OBJECT",
    "createdTime": "2022-08-11T22:13:17.4111089"
  }
]
```

### 删除已创建的接口
```
DELETE http://localhost:9000/api/datafaker/remove?id={id}
```

返回 `200`

### 请求 Fake Data 接口

```
GET http://localhost:9000/{自定义数据路径}
```
返回 `expression` 定义的数据

> 请求参数 `limit` 仅对 `JsonArray` 有效

### 测试 datafaker 表达式 

```
http://localhost:9000/api/datafaker/test-expression?text=%23{Name.full_name}
```

> `#` 符号请用 `%23` 代替

