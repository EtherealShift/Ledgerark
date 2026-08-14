# ledgerark

> 基于 Spring Boot 的多模块后端项目。

## 技术栈

| 技术 | 版本/说明 |
|------|-----------|
| Java | 21 |
| Spring Boot | 4.1.0 |
| 构建 | Maven 多模块 |
| Lombok | 1.18.46 |
| 工具库 | Hutool 5.8.47 |
| 数据库 | MySQL（Connector/J 预留） |

## 模块结构

| 模块 | 说明 |
|------|------|
| `ledgerark-admin` | 启动模块，Web 入口（`@SpringBootApplication`） |
| `ledgerark-system` | 业务模块 |
| `ledgerark-common` | 公共模块：统一响应 `Result`、状态码 `ResultCode` |

## 环境要求

- JDK 21+
- Maven 3.9+

## 快速开始

### 构建

```bash
mvn clean package
```

### 运行

```bash
mvn spring-boot:run -pl ledgerark-admin
```

启动后访问 http://localhost:8080

## 目录结构

```
ledgerark
├── ledgerark-admin      # 启动模块
├── ledgerark-system     # 业务模块
├── ledgerark-common     # 公共模块
└── pom.xml              # 父 POM
```