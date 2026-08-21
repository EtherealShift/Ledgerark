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
| `ledgerark-system` | 业务模块：用户/角色相关的 Service、Mapper、DTO、VO，以及业务实体（entity.sys）、枚举（enums）、常量（constant）、异常（exception） |
| `ledgerark-common` | 公共模块（纯公共，不依赖业务）：统一响应 `Result`、`ResultCode`、`BaseEntity`、`PageQuery`、`LoginUser`、`BaseException` |

## 功能

- 用户注册：用户名/邮箱唯一性校验（存在即拒绝），BCrypt 密码加密存储，注册默认 `status=0` / `user_type=2`（工号留空由管理员分配），事务保护
- 用户登录：登录前参数校验、BCrypt 密码比对，登录成功**返回 token + 用户信息**（`SysUserLoginResponseVO`），用户信息（`LoginUser`）写入 Sa-Token Session
- 登录鉴权：Sa-Token 拦截器统一校验登录状态，登录/注册接口白名单放行
- 逻辑删除：需要软删的表（`sys_user`、`sys_role`）使用 `del_flag` + `@TableLogic`（方案 B，全局 `logic-delete-field: delFlag`）
- 用户体系：`sys_user`（用户表，`user_type` 区分超级管理员/普通用户）、`sys_role`（部门角色表）、`sys_user_role`（用户-角色关联表，支持一个用户多角色）
- 全局异常处理：参数校验、业务异常，以及 Sa-Token 未登录/无权限/无角色异常，统一转换为 `Result` 响应

## 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+

## 快速开始

### 1. 初始化数据库

创建数据库 `ledgerark`（utf8mb4）后，按顺序执行建表脚本：

```bash
mysql -u root -p ledgerark < sql/sys_user.sql        # 用户表
mysql -u root -p ledgerark < sql/sys_role.sql        # 角色表
mysql -u root -p ledgerark < sql/sys_user_role.sql   # 用户-角色关联表
mysql -u root -p ledgerark < sql/sys_manage.sql      # 系统扩展表（部门/菜单/字典/操作日志）
mysql -u root -p ledgerark < sql/biz_archive.sql     # 档案业务表（分类/档案/凭证/发票/借阅/销毁等）
mysql -u root -p ledgerark < sql/init_data.sql       # 可选：预置角色与数据字典（幂等）
mysql -u root -p ledgerark < sql/reset_sys_core.sql  # 可选：重置核心三表并预置超级管理员 admin（密码 admin123，首次登录后请修改）
```

> 各表字段说明、v1→v2 对齐约定与执行顺序详见 `docs/档案管理系统设计文档.md` §5.5。
> 注意：`reset_sys_core.sql` 会清空并重建 `sys_role` / `sys_user` / `sys_user_role`，执行前请备份数据；若已建 `sys_role_menu` 会被一并删除。

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
├── ledgerark-system     # 业务模块（Service、Mapper、DTO/VO、业务实体/枚举/常量/异常）
├── ledgerark-common     # 公共模块（统一响应、基类、公共枚举/异常）
├── docs                # 设计文档（档案管理系统设计文档）
├── sql                  # 数据库脚本
└── pom.xml              # 父 POM（依赖版本统一管理）
```