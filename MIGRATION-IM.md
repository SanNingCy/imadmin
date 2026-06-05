# web4x + chat-ops（IM）集成说明

## 已完成

1. **新增模块 `web4x-im`**：从 `chat-ops` **复制**全部 Java/资源（**未修改** `D:\project\chat-ops` 仓库）。
2. **保留若依**：`web4x-common`、`web4x-framework`、`web4x-system`、`web4x-quartz`、`web4x-generator`、`web4x-admin` 及 Thymeleaf 前端。
3. **配置**：环境 profile 文件（`application-dev.yml` 等）由你自行维护；**IM/若依合并项写在 `application.yml`**，尽量不改动各 profile 文件。
4. **MyBatis-Plus**：已引入 `mybatis-plus-spring-boot4-starter`，与原有 MyBatis XML 共存。
5. **安全**：默认 `im.shiro.enabled=true` 使用 IM（chat-ops）JWT Shiro；设为 `false` 可回退若依 Shiro。
   - **若依页面登录**（`POST /login`）：走 IM `sys_user` 校验，会话 principal 为 JWT，侧栏菜单来自 `sys_menu_two`（`ImMenuTreeBridge`）。
   - **IM API 登录**（`POST /sys/login`）：行为与 chat-ops 一致，不受影响。
   - 若依「系统管理」CRUD 仍读写 `*_ry` 分表；账号/菜单权限以 IM 库为准时请走 IM 后台接口或后续再统一控制器。
6. **若依 IM 页面**：`/im/member`、`/im/group`、`/im/friend`（调用原 `/member/member/list` 等接口）。
7. **菜单 SQL**：`sql/im-menu-init.sql`

## 启动前准备

- JDK **21**（若依 4.8.3 / Spring Boot 4 要求；本机 Maven 若仍指向 JDK 8，请设置 `JAVA_HOME`）
- MySQL 库 `chat_platform_db`（与各 profile 数据源配置一致），需包含 IM 业务表
- **禁止** 在 IM 库直接执行 `sql/ruoyi.sql`（会 DROP 覆盖 IM 表）；兼容说明见 **`sql/RUOYI-COMPAT.md`**，同库先执行 **`sql/ruoyi-compat-chat_platform.sql`**
- Redis、MongoDB（与各 profile 中 spring 配置一致）

## 启动

```bash
cd web4x
mvn -pl web4x-admin -am package -DskipTests
java -jar web4x-admin/target/web4x-admin.jar
```

- 管理登录（IM API）：`POST /sys/login`（JWT，cookie/header 带 `token`）
- 若依 Thymeleaf 登录（`im.shiro.enabled=true` 默认）：`GET/POST /login`，账号为 IM `sys_user`
- 纯若依 Shiro（`im.shiro.enabled=false`）：`/login` 使用 `sys_user_ry`
- 端口：开发环境以激活的 profile 为准（如 `application-dev.yml` 中的 `server.port`）

## 后续可按模块补充 Thymeleaf

参考 `templates/im/member/member.html`，为 `chatlog`、`piamom`、`notice` 等模块增加 `/im/xxx` 页面，并执行菜单 SQL。

## chat-ops 独立部署

`chat-ops` 仓库**未做任何改动**，可继续单独部署，互不影响。

## 为何依赖报错很多？

`web4x-im` 是从 **chat-ops（Spring Boot 2.3）** 复制到 **web4x（Spring Boot 4）** 的整包业务代码，不是手写新模块：

1. **原 POM 里的依赖不会跟着 Java 文件一起复制**，需在 `web4x-im/pom.xml` 里按 chat-ops 逐项补齐（Swagger、json-lib、AspectJ、protobuf 等）。
2. **javax → jakarta**：邮件、事务、Servlet 等包名在 Boot 3/4 已换，需改 import 或加 Jakarta 依赖。
3. **Spring 6/ Boot 4 API 变化**：如 `NestedIOException` 已移除，需改为 `IOException` 等。
4. **业务逻辑未删**：资产/链桥调用等代码仍在（与 chat-ops 一致），仅编译期补依赖，**不影响运行时 IM 接口行为**。

依赖以 `web4x-im/pom.xml` 为准，改完后请 **Maven Reload**。
