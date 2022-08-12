# Protobuf Manager

Google protobuf 文件统一管理平台, 旨在解决 proto 文件统一管理，查询，编辑权限统一，文件快速分享，PB文件相互依赖问题

😁更多功能还在完善中...

![首页-1](img/%E9%A6%96%E9%A1%B5-1.png)

> 技术栈 - 后端 vert.x, 前端 mui

## 启动

1. 启动后端 - `mvn exec:java -f .\protobuf-manager\pom.xml`
2. 启动前端 - `cd frontend; npm start`

## 功能表单

- 服务列表
    - 按 服务名、作者搜索
    - 罗列服务列表

- 创建应用

- Protobuf
    - 创建PB
    - 编辑PB

- 用户信息

## API

See [test-api.rest](test-api.rest)

## 后续规划

- 完善功能
- 增加 protobuf 代码生成、下载