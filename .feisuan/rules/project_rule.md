
# ai-springboot 项目开发规范指南

为保证项目代码质量、可维护性、安全性与可扩展性，所有开发工作应遵循以下规范。

## 一、项目基本信息

| 项目 | 配置 |
|---|---|
| **项目名称** | `ai-springboot` |
| **项目作者** | `luocao` |
| **操作系统** | Windows 11 |
| **工作目录** | `D:\workspace\java\mavenhome\springbootprogram\ai-springboot` |
| **构建工具** | Maven |
| **JDK 版本** | JDK 17.0.16 |
| **Java 语言版本** | Java 17 |
| **Spring Boot 版本** | Spring Boot 4.1.0 |
| **项目版本** | `0.0.1-SNAPSHOT` |
| **GroupId** | `com.example` |
| **ArtifactId** | `ai-springboot` |
| **服务端口** | `8080` |
| **数据库** | MySQL |
| **数据库名称** | `mental_health_assistant` |

> 项目实际使用的是 Spring Boot 4.1.0，后续开发规范以项目 `pom.xml` 中的实际版本为准，不使用基础模板中的 Spring Boot 3.x 版本描述。

## 二、项目目录结构

```text
ai-springboot
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── aispringboot
    │   │               ├── common       # 通用组件、常量、统一响应、异常处理等
    │   │               ├── controller   # Web 接口层
    │   │               ├── dto
    │   │               │   ├── command   # 请求参数及写操作对象
    │   │               │   └── response  # 返回结果对象
    │   │               ├── entity        # 数据库实体对象
    │   │               ├── mapper        # MyBatis-Plus 数据访问接口
    │   │               └── service       # 业务逻辑层
    │   └── resources
    │       ├── static                    # 静态资源
    │       ├── templates                  # 模板文件
    │       └── application.yml            # 应用配置文件
    └── test
        └── java
            └── com
                └── example
                    └── aispringboot      # 测试代码，包结构应与主代码保持一致
```

### 目录职责

- `common`
  - 存放统一返回结果、全局异常处理、业务异常、公共常量、工具类等。
  - 通用代码应保持稳定，禁止存放具体业务逻辑。
- `controller`
  - 负责接收 HTTP 请求、参数校验及返回 HTTP 响应。
  - 不得直接操作数据库或编写复杂业务逻辑。
- `dto.command`
  - 存放新增、修改、查询等请求参数对象。
  - 用于接收客户端输入，避免直接使用 Entity 接收外部请求。
- `dto.response`
  - 存放返回给客户端的响应对象。
  - 禁止直接将 Entity 返回给前端。
- `entity`
  - 存放数据库表对应的实体类。
  - 只负责数据映射，不应承载复杂业务逻辑。
- `mapper`
  - 存放 MyBatis-Plus Mapper 接口。
  - 只负责数据库访问，不应编写业务流程。
- `service`
  - 存放业务接口及其实现类。
  - 业务实现类应放入 `service.impl` 子包中。
- `resources`
  - `application.yml` 存放环境配置。
  - 静态资源放入 `static`，模板文件放入 `templates`。

## 三、技术栈与依赖规范

### 核心技术栈

- Spring Boot `4.1.0`
- Java `17`
- Maven
- Spring MVC
- Spring JDBC
- MyBatis-Plus `3.5.7`
- MySQL
- Lombok
- Jakarta Bean Validation

### 主要依赖

| 依赖 | 用途 |
|---|---|
| `spring-boot-starter-data-jdbc` | Spring JDBC 数据访问支持 |
| `spring-boot-starter-webmvc` | Web MVC、REST 接口开发 |
| `mysql-connector-j` | MySQL 数据库驱动，仅运行时使用 |
| `lombok` | 自动生成 getter、setter、构造方法及日志字段 |
| `spring-boot-starter-validation` | 参数校验 |
| `mybatis-plus-spring-boot3-starter:3.5.7` | MyBatis-Plus 数据访问及增强功能 |
| `spring-boot-starter-data-jdbc-test` | JDBC 测试支持 |
| `spring-boot-starter-webmvc-test` | Web MVC 测试支持 |

### 依赖使用要求

1. 数据访问层统一优先使用 MyBatis-Plus。
2. 不得在同一业务模块中无约束地混用 JDBC、MyBatis-Plus 或其他持久化方案。
3. 新增依赖前必须确认：
   - 是否已有现有依赖可以满足需求；
   - 是否兼容 Spring Boot `4.1.0`；
   - 是否兼容 Java 17；
   - 是否存在安全漏洞或维护风险。
4. `mybatis-plus-spring-boot3-starter` 命名表明其面向 Spring Boot 3 生态，升级或调整 Spring Boot 版本时必须重点验证兼容性。
5. 生产环境禁止引入不必要的测试依赖和开发依赖。
6. Maven 依赖版本优先由 Spring Boot Parent 统一管理；明确指定版本时，应在代码评审中说明原因。

## 四、构建与运行规范

### Maven 构建

常用命令如下：

```bash
# 清理并编译项目
mvn clean compile

# 执行测试
mvn test

# 打包项目
mvn clean package

# 跳过测试打包，仅允许在明确场景下使用
mvn clean package -DskipTests
```

### 启动规范

- 本地开发应使用 Maven 或 IDE 启动 Spring Boot 应用。
- 应用默认端口为 `8080`。
- 启动前应确认 MySQL 服务已启动，并且数据库 `mental_health_assistant` 已创建。
- 修改端口或数据库连接后，应同步更新相关运行文档。

## 五、配置文件与敏感信息规范

当前配置位于：

```text
src/main/resources/application.yml
```

当前数据库配置包括：

- 数据库地址：`localhost:3306`
- 数据库名称：`mental_health_assistant`
- 用户名：`root`
- 服务端口：`8080`

### 配置要求

1. 数据库密码、访问令牌、密钥等敏感信息不得硬编码到源码或提交到公共代码仓库。
2. 开发、测试、生产环境应使用不同配置，推荐使用：
   - `application-dev.yml`
   - `application-test.yml`
   - `application-prod.yml`
3. 敏感配置应通过环境变量、启动参数或安全配置中心注入。
4. 生产环境禁止使用 `root` 数据库账号，应使用权限最小化的专用账号。
5. 数据库连接、日志级别及服务端口等配置应支持按环境覆盖。
6. `useSSL=false` 仅适用于明确的本地开发场景，生产环境应根据数据库部署情况配置安全连接。

## 六、分层架构规范

### Controller 层

- 只负责：
  - 接收 HTTP 请求；
  - 调用参数校验；
  - 调用 Service；
  - 封装并返回响应。
- 禁止直接调用 Mapper。
- 禁止在 Controller 中编写复杂业务逻辑。
- 使用 DTO 接收请求参数，使用 Response DTO 返回结果。
- 接口路径、请求方式及响应结构应保持统一。

### Service 层

- 负责业务逻辑、业务校验和事务边界管理。
- 业务逻辑必须通过 Service 接口暴露。
- Service 实现类放在 `service.impl` 包中。
- 不应将数据库实体直接返回给 Controller。
- 对跨多个数据库操作的业务，应合理使用事务。
- 事务注解原则上放在 Service 层。

示例结构：

```text
service
├── UserService.java
└── impl
    └── UserServiceImpl.java
```

### Mapper 层

- Mapper 接口统一放在 `mapper` 包中。
- 优先继承 MyBatis-Plus 提供的通用 Mapper，例如：

```java
public interface UserMapper extends BaseMapper<UserEntity> {
}
```

- Mapper 只负责数据访问，不负责业务判断。
- 禁止手动拼接外部输入形成 SQL。
- 复杂查询应使用参数绑定、条件构造器或经过评审的 XML/注解 SQL。
- 注意避免 N+1 查询及无条件全表查询。

### Entity 层

- Entity 用于数据库表映射，不作为接口入参或出参。
- Entity 类应放在 `entity` 包中。
- 字段命名应与数据库字段映射规则保持一致。
- 数据库表、字段及索引的变更应经过评审。
- 不应在 Entity 中堆积与持久化无关的业务逻辑。

## 七、接口、DTO 与对象命名规范

### 命名规则

| 类型 | 命名规范 | 示例 |
|---|---|---|
| 类名 | `UpperCamelCase` | `UserController` |
| 方法及变量 | `lowerCamelCase` | `saveUser()` |
| 常量 | `UPPER_SNAKE_CASE` | `MAX_LOGIN_ATTEMPTS` |
| Service 接口 | 以 `Service` 结尾 | `UserService` |
| Service 实现类 | 以 `ServiceImpl` 结尾 | `UserServiceImpl` |
| Mapper 接口 | 以 `Mapper` 结尾 | `UserMapper` |
| Controller 类 | 以 `Controller` 结尾 | `UserController` |
| Entity 类 | 以 `Entity` 结尾 | `UserEntity` |
| Command 对象 | 以 `Command` 结尾 | `UserCreateCommand` |
| Response 对象 | 以 `Response` 或 `VO` 结尾 | `UserResponse` |

### 对象使用原则

- 请求对象使用 `dto.command` 包中的类型。
- 返回对象使用 `dto.response` 包中的类型。
- 数据库对象使用 `entity` 包中的类型。
- 禁止为了省事在 Controller 中直接复用 Entity。
- 对象转换应集中处理，避免在多个 Controller 中重复编写转换逻辑。

## 八、参数校验规范

- 使用 `spring-boot-starter-validation` 提供的 Jakarta Validation。
- Spring Boot 4.x 中校验注解使用：

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
```

- Controller 接收请求对象时使用 `@Valid` 或 `@Validated`。
- 常用校验注解包括：
  - `@NotBlank`
  - `@NotNull`
  - `@NotEmpty`
  - `@Size`
  - `@Min`
  - `@Max`
  - `@Pattern`
- 不得仅依赖前端校验，服务端必须进行完整校验。
- 错误信息应清晰、统一，不得暴露堆栈信息、SQL 语句或敏感配置。

## 九、事务与数据库规范

- `@Transactional` 原则上仅用于 Service 层。
- 事务方法应保持职责单一，避免将远程调用、耗时操作放入长事务。
- 避免在循环中频繁提交事务。
- 批量操作应优先使用批量接口，合理控制批次大小。
- 查询必须明确字段、条件和分页策略，禁止无条件查询大表。
- 用户输入不得直接拼接 SQL。
- 数据库异常应转换为统一业务异常或系统异常。
- 重要数据表应根据查询场景建立合理索引。

## 十、Lombok 与代码生成规范

项目已配置 Lombok 注解处理器，可使用以下注解：

- `@Getter`
- `@Setter`
- `@Data`
- `@NoArgsConstructor`
- `@AllArgsConstructor`
- `@RequiredArgsConstructor`
- `@Builder`
- `@Slf4j`

使用要求：

1. DTO 可根据需要使用 `@Data`、`@Builder` 等注解。
2. Entity 应谨慎使用 `@Data`，避免 `equals`、`hashCode` 与数据库关联关系产生副作用。
3. Service 依赖注入优先使用构造器注入，可配合 `@RequiredArgsConstructor`。
4. 日志统一使用 `@Slf4j`，禁止使用 `System.out.println`。
5. 不应为了减少代码量而滥用 Lombok；公共核心对象应保证代码行为清晰可读。

## 十一、注释与文档规范

- 项目代码作者为 **luocao**。
- 所有新增的公共类、接口、方法及复杂字段应添加 Javadoc。
- 注释必须使用用户的第一语言：**中文**。
- 注释应说明：
  - 类或方法的用途；
  - 参数含义；
  - 返回值含义；
  - 异常条件；
  - 复杂业务规则或特殊处理原因。
- 禁止编写与代码无关、无法维护或明显过时的注释。
- 修改代码逻辑时，应同步更新相关注释。
- 对外 API 应补充接口说明、参数说明和返回结果说明。

示例：

```java
/**
 * 根据用户编号查询用户信息。
 *
 * @param userId 用户编号
 * @return 用户响应信息
 */
UserResponse getUserById(Long userId);
```

## 十二、日志与异常处理规范

### 日志

- 使用 SLF4J，推荐通过 Lombok 的 `@Slf4j` 注入日志对象。
- 禁止使用 `System.out.println` 和 `System.err.println`。
- 日志级别应合理使用：
  - `DEBUG`：调试信息；
  - `INFO`：关键业务流程；
  - `WARN`：可恢复异常或风险提示；
  - `ERROR`：系统错误或不可恢复异常。
- 禁止打印密码、Token、身份证号等敏感信息。
- 生产环境避免打印完整请求体和数据库敏感信息。

### 异常

- 使用统一异常处理机制，例如 `@RestControllerAdvice`。
- 业务异常与系统异常应区分处理。
- 不得直接向前端返回异常堆栈。
- 不得使用空 `catch` 屏蔽异常。
- 异常信息应便于定位问题，但不能泄露系统内部实现细节。

## 十三、测试规范

- 测试代码应放在：

```text
src/test/java/com/example/aispringboot
```

- 测试包结构应与主代码包结构保持一致。
- 新增 Service、Controller 或复杂 Mapper 功能时，应补充对应测试。
- 测试应覆盖：
  - 正常业务流程；
  - 参数校验失败；
  - 业务异常；
  - 数据不存在；
  - 边界条件；
  - 数据库操作异常。
- 测试数据不得依赖个人本地环境中的固定数据。
- 测试不得连接生产数据库。
- 提交代码前应至少执行：

```bash
mvn clean test
```

## 十四、安全规范

- 防止 SQL 注入，所有 SQL 参数必须绑定。
- 防止 XSS，对用户输入进行必要的校验和输出处理。
- 防止越权访问，在 Service 层校验用户数据访问权限。
- 不信任任何客户端传入的身份、权限及业务状态字段。
- 敏感接口应根据实际需求增加认证、授权、限流和审计。
- 密码不得明文存储或明文打印。
- 敏感配置不得提交到 Git。
- 对文件上传、外部 URL、反序列化等功能进行严格校验。

## 十五、代码质量原则

开发过程中遵循以下原则：

| 原则 | 要求 |
|---|---|
| **SOLID** | 保持高内聚、低耦合，便于维护和扩展 |
| **DRY** | 避免重复代码，提取合理的公共方法或组件 |
| **KISS** | 保持实现简单、清晰、易理解 |
| **YAGNI** | 只实现当前明确需要的功能 |
| **Fail Fast** | 尽早发现并报告非法参数和异常状态 |
| **OWASP** | 遵循常见 Web 安全防护要求 |

## 十六、提交前检查清单

提交代码前应确认：

- [ ] 使用 Java 17 语法和项目兼容的 Spring Boot API。
- [ ] 使用 Maven 完成编译和测试。
- [ ] Controller 未直接调用 Mapper。
- [ ] 业务逻辑位于 Service 层。
- [ ] Service 实现类位于 `service.impl` 包中。
- [ ] 请求和响应未直接暴露 Entity。
- [ ] 参数使用 Jakarta Validation 校验。
- [ ] 数据库操作未拼接外部输入。
- [ ] 未提交密码、Token 或其他敏感信息。
- [ ] 使用 `@Slf4j`，未使用 `System.out.println`。
- [ ] 新增公共代码已添加中文 Javadoc。
- [ ] 测试代码位于正确的测试目录。
- [ ] 已执行 `mvn clean test` 并确认通过。
