# Welcome to Protobuf Manager

Google protobuf 文件统一管理平台, 旨在解决 proto 文件统一管理，查询，编辑权限统一，文件快速分享，PB文件相互依赖问题

- 支持 proto 按 Group、Application 分组创建、管理
- 关键字搜索 proto
- 支持前端管理平台
- 支持用户验证、鉴权 [💡未来规划]
- 完善前端控制台 [💡未来规划]
- 支持 protobuf 校验 [💡未来规划]
- 支持 protobuf 代码生成 [💡未来规划]

![首页-1](img/%E9%A6%96%E9%A1%B5-1.png)

## 工程介绍

### 项目目录

- `db` - SQL 文件
- `frontend` - 前端模块，基于 React + [MUI](https://mui.com/)
- `protobuf-manager` - 后端模块 vert.x 实现
- `protobuf-manager-spring` - 后端模块 spring 实现

### 启动

1. 启动 vert.x 后端 - `mvn exec:java -f .\protobuf-manager\pom.xml`
2. 启动 spring 后端 - `mvn spring-boot:run -f .\protobuf-manager-spring\pom.xml`
3. 启动前端 - `cd frontend; npm start`

> vert.x 和 spring 旨在使用不同的框架实现同样的功能

## API & Test

### For `protobuf-manager`

```http
@devhost=http://localhost:8888

# About Application

###
GET {{devhost}}/api/application
SERVICE-AUTH-USERNAME: onemsg

###
GET {{devhost}}/api/application/123456
SERVICE-AUTH-USERNAME: onemsg

###
GET {{devhost}}/api/application/1
SERVICE-AUTH-USERNAME: onemsg


# About Protobuf

###
GET {{devhost}}/api/protobuf?search=A&pageIndex=1&pageSize=10
SERVICE-AUTH-USERNAME: onemsg

### 
GET {{devhost}}/api/protobuf/1
SERVICE-AUTH-USERNAME: onemsg

### 
GET {{devhost}}/api/protobuf/7
SERVICE-AUTH-USERNAME: onemsg

###
GET {{devhost}}/api/protobuf?search=&pageIndex=1&pageSize=5
SERVICE-AUTH-USERNAME: onemsg

###
GET {{devhost}}/api/protobuf?search=cms_oneconfig_server&pageIndex=1&pageSize=5
SERVICE-AUTH-USERNAME: onemsg

###
POST {{devhost}}/api/protobuf
SERVICE-AUTH-USERNAME: onemsg
Content-Type: application/json

{
    "applicationId": 1,
    "name": "book_server",
    "intro": "book_server pb",
    "owner": "mashugaung",
    "lastModifier": "mashugaung"
}

###
DELETE {{devhost}}/api/application/2
SERVICE-AUTH-USERNAME: onemsg


# Others

###
GET {{devhost}}/api/400
```

### For `protobuf-manager-spring`

```http
@devhost=http://localhost:5000

# About Application

###
GET {{devhost}}/api/application/name?groupId=1

###
GET {{devhost}}/api/application/groups

###
POST {{devhost}}/api/application
Content-Type: application/json

{
    "name": "order-server",
    "intro": "订单服务, 提供订单相关接口",
    "groupId": 1

}

###
DELETE {{devhost}}/api/application/8

### About protobuf
GET {{devhost}}/api/protobuf?search=order-server

###
GET {{devhost}}/api/protobuf?search=

###
GET {{devhost}}/api/protobuf/3

###
POST {{devhost}}/api/protobuf
Content-Type: application/json

{
    "applicationId": 10,
    "name": "order_service",
    "intro": "订单服务rpc接口",
    "protocol": "grpc",
    "creator": "onemsg"
}

###
PUT {{devhost}}/api/protobuf/1/intro
Content-Type: application/json

{
    "intro": "订单服务rpc接口的描述"
}

###
PUT {{devhost}}/api/protobuf/3/current-version
Content-Type: application/json

{
    "version": 102
}

###
DELETE {{devhost}}/api/protobuf/3

###
POST {{devhost}}/api/protobuf/code
Content-Type: application/json

{
    "protoId": 4,
    "code": "code...",
    "creator": "onemsg"
}

###
GET {{devhost}}/api/protobuf/code/18

###
GET {{devhost}}/api/protobuf/4/code
```