# Ledgerark

> 基于 Spring Boot 的多模块后端项目，提供用户认证与角色权限管理。

## 技术栈

| 技术 | 版本/说明 |
|------|-----------|
| Java | 21 |
| Spring Boot | 4.1.0 |
| 构建 | Maven 多模块 |
| MyBatis-Plus | 3.5.17（ORM） |
| Sa-Token | 1.45.0（认证鉴权，登录校验 + Session 管理） |
| Spring Security Crypto | BCrypt 密码加密（仅用加密模块，未引入完整 Security） |
| MySQL | Connector/J 26.7.0 |
| Lombok | 1.18.46 |
| 工具库 | Hutool 5.8.47 |

## 模块结构

| 模块 | 说明 |
|------|------|
| `ledgerark-admin` | 启动模块，Web 入口（`@SpringBootApplication`、Controller） |
| `ledgerark-framework` | 框架模块：Sa-Token 拦截器配置、密码编码器配置、全局异常处理器、登录服务 |
| `ledgerark-system` | 业务模块：用户/角色相关的 Service、Mapper、DTO、VO |
| `ledgerark-common` | 公共模块：统一响应 `Result`、状态码、枚举、实体类、常量、异常 |

## 功能

- 用户注册：用户名/邮箱唯一性校验，BCrypt 密码加密存储
- 用户登录：登录前参数校验、密码比对，登录后用户信息（`LoginUser`）写入 Sa-Token Session
- 登录鉴权：Sa-Token 拦截器统一校验登录状态，登录/注册接口白名单放行
- 用户体系：`sys_user`（用户表，`user_type` 区分超级管理员/普通用户）、`sys_role`（部门角色表）、`sys_user_role`（用户-角色关联表，支持一个用户多角色）
- 全局异常处理：参数校验异常、业务异常统一转换为 `Result` 响应

## 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+

## 快速开始

### 1. 初始化数据库

创建数据库 `ledgerark` 并执行建表脚本：

```bash
mysql -u root -p ledgerark < sql/sys_role.sql
```

数据库连接配置见 `ledgerark-admin/src/main/resources/application.yaml`。

### 2. 构建

```bash
mvn clean package
```

### 3. 运行

```bash
mvn spring-boot:run -pl ledgerark-admin
```

启动后访问 http://localhost:8080

## 目录结构

```
ledgerark
├── ledgerark-admin      # 启动模块（Web 入口、配置文件）
├── ledgerark-framework  # 框架模块（认证配置、全局异常、登录服务）
├── ledgerark-system     # 业务模块（Service、Mapper、DTO/VO）
├── ledgerark-common     # 公共模块（实体、枚举、常量、异常、统一响应）
├── sql                  # 数据库脚本
└── pom.xml              # 父 POM（依赖版本统一管理）
```