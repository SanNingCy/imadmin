# 若依 SQL 与 IM 库（chat_platform_db）兼容说明

## 结论（先看）

| 做法 | 是否破坏 IM | 若依后台完整度 |
|------|-------------|----------------|
| **直接执行 `ruoyi.sql` 到 chat_platform_db** | **会**（DROP 同名表） | 看似完整，实际毁掉 IM |
| **新建库 `web4x_ry`，只在那里执行 `ruoyi.sql`** | **不会** | 若依 100%（推荐） |
| **同库 + `ruoyi-compat-chat_platform.sql` + 分表** | **不会** | 需逐步改 Mapper，长期工程量大 |

---

## 为什么不能直接跑 ruoyi.sql？

`ruoyi.sql` 里大量 `DROP TABLE IF EXISTS` + `CREATE TABLE`。  
与 IM 库 **同名但结构不同** 的表包括（不完全列表）：

| 表名 | IM（chat-ops）用途 | 若依用途 |
|------|-------------------|----------|
| `sys_config` | IM 系统配置（smtp、logo…） | 若依键值参数 config_id… |
| `sys_user` | IM 后台用户（id、login_name…） | 若依用户（user_id、dept_id…） |
| `sys_role` / `sys_menu` / `sys_post` | IM 权限体系 | 若依 RBAC（字段不同） |
| `sys_notice_read` | IM 公告已读 | 若依公告已读（结构不同） |
| `sys_dict_type` | IM 字典 | 若依字典 |
| `QRTZ_*` | 定时任务 | 定时任务（可能可共用） |

执行 `ruoyi.sql` 会先 **删掉** IM 表再建若依表 → **IM 业务数据与接口全部报废**。

---

## 推荐方案：双库（最省事、最安全）

```text
chat_platform_db   ← 现有 IM 库，不动、不执行 ruoyi.sql
web4x_ry           ← 新建空库，完整执行 ruoyi.sql
```

1. MySQL 建库：`CREATE DATABASE web4x_ry DEFAULT CHARSET utf8mb4;`
2. 只对 `web4x_ry` 执行：`sql/ruoyi.sql`
3. 应用配置（后续可在 `application.yml` 增加若依从库数据源；当前 IM 仍走 `chat_platform_db`）

**优点**：零表名冲突，若依、IM 各用各的 SQL，互不影响。  
**缺点**：需要配置双数据源后，若依 Thymeleaf 后台才连 `web4x_ry`（需二次开发）。

---

## 同库兼容方案 B（chat_platform_db，推荐你当前做法）

目标：**不 DROP、不改 IM 已有表**，若依用 **`*_ry` / `sys_param_config`** 新表。

### 已落地（可直接执行）

| 项 | 说明 |
|----|------|
| **`sql/ruoyi-compat-chat_platform.sql`** | 由 `build_ruoyi_compat.ps1` 从 `ruoyi.sql` 生成：**无 DROP**、冲突表改名、`INSERT IGNORE`、跳过 `QRTZ_*` |
| **若依 Mapper** | `web4x-system/.../mapper/system/*.xml` 已指向 `sys_*_ry`（`SysConfigMapper` → `sys_param_config`） |
| **IM 不动** | `sys_config`、`sys_user`、`sys_menu`、`sys_role` 等 **不会被脚本删除或覆盖** |

### 表名对照（若依侧）

| ruoyi.sql 原表 | 同库若依实际表 |
|----------------|----------------|
| `sys_config` | `sys_param_config` |
| `sys_user` | `sys_user_ry` |
| `sys_role` | `sys_role_ry` |
| `sys_menu` | `sys_menu_ry` |
| `sys_post` | `sys_post_ry` |
| `sys_dict_type` | `sys_dict_type_ry` |
| `sys_role_menu` | `sys_role_menu_ry` |
| `sys_user_role` | `sys_user_role_ry` |
| `sys_notice_read` | `sys_notice_read_ry` |
| `gen_*`、`sys_dept`、`sys_job`… | **原名**，`CREATE TABLE IF NOT EXISTS` |

### 菜单说明

- **IM 管理端**：继续用 **`sys_menu_two` / `sys_role_menu_two`**（IM 代码，不变）
- **若依 Thymeleaf 后台**：用 **`sys_menu_ry`**（本脚本初始化数据，与 IM `sys_menu` 无关）

### 对 IM 业务的影响（执行前必读）

- **不会**：删表、改 IM 表结构、清空 IM 的 `sys_user` / `sys_config` 等数据
- **会**：在库中 **新增** 若依专用表；若某表 **已存在且结构相同**（如 `sys_dept`），`CREATE IF NOT EXISTS` 跳过，**已有数据保留**
- **可能**：若依默认数据通过 `INSERT IGNORE` 写入 `*_ry` 表，**不覆盖**已有主键行
- **Quartz**：不建 `QRTZ_*`（假定 IM 已有）

---

## 配置文件与 chat-ops 的关系

- **IM 配置源**：`chat-ops/src/main/resources/application*.yml`（只改 chat-ops，不要在 web4x-admin 里抄一份）
- **构建**：`web4x-im` 打包时把上述文件放进 `classpath:chat-ops/`
- **web4x-admin**：`application.yml` / `application-dev.yml` 等通过 `spring.config.import` 引用；**仅 `application-dev.yml` 保留若依 Druid 覆盖段**

目录要求：`D:/project/chat-ops` 与 `D:/project/web4x` 同级。

---

## 操作步骤（同库、保 IM）

1. **备份** `chat_platform_db`（建议）
2. **不要** 执行 `ruoyi.sql`
3. 在 `chat_platform_db` 执行：**`sql/ruoyi-compat-chat_platform.sql`**
4. 重启应用；若依登录走 `/login`，账号在 **`sys_user_ry`**（默认 `admin`）
5. 若 SQL 有变，可重新运行：`powershell -File sql/build_ruoyi_compat.ps1`

---

## 登录说明（IM + 若依）

- **IM API**：`/sys/login`（JWT），用户表 = IM `sys_user`
- **若依页面**：`/login`，用户表 = 若依 `sys_user`（双库时在 `web4x_ry`）
- 当前 `im.shiro.enabled=true` 时以 IM 安全栈为主；若依完整登录需双库或 `sys_user_ry` 分表后再对接
