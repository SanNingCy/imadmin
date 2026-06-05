# -*- coding: utf-8 -*-
"""从 ruoyi.sql 生成 ruoyi-compat-chat_platform.sql（无 DROP、冲突表改名）。"""
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent
ruoyi = (ROOT / "ruoyi.sql").read_text(encoding="utf-8")

RENAME = {
    "sys_config": "sys_param_config",
    "sys_user": "sys_user_ry",
    "sys_role": "sys_role_ry",
    "sys_menu": "sys_menu_ry",
    "sys_post": "sys_post_ry",
    "sys_notice_read": "sys_notice_read_ry",
    "sys_dict_type": "sys_dict_type_ry",
    "sys_role_menu": "sys_role_menu_ry",
    "sys_user_role": "sys_user_role_ry",
}
SKIP_PREFIXES = ("QRTZ_", "qrtz_")

HEADER = """-- =============================================================================
-- 若依 + IM 同库安装（chat_platform_db）— 可直接执行
-- 保证：无 DROP TABLE；不修改/删除 IM 已有表与数据
-- IM 仍用：sys_config, sys_user, sys_menu, sys_role 等原表
-- 若依用：sys_param_config, sys_user_ry, sys_menu_ry 等（见 RUOYI-COMPAT.md）
-- =============================================================================
USE chat_platform_db;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

"""

out = [HEADER]
lines = ruoyi.splitlines()
i = 0
while i < len(lines):
    line = lines[i]
    m_drop = re.match(r"\s*DROP TABLE IF EXISTS `([^`]+)`", line)
    if m_drop:
        t = m_drop.group(1)
        if any(t.startswith(p) for p in SKIP_PREFIXES):
            out.append(f"-- [跳过] IM 已有 Quartz 表 `{t}`")
        elif t in RENAME:
            out.append(f"-- [跳过 DROP] 保护 IM 表 `{t}`，若依使用 `{RENAME[t]}`")
        else:
            out.append(f"-- [跳过 DROP] 保护已有表 `{t}`，下方 CREATE IF NOT EXISTS")
        i += 1
        continue

    m_create = re.match(r"\s*CREATE TABLE `([^`]+)`", line)
    if m_create:
        t = m_create.group(1)
        if any(t.startswith(p) for p in SKIP_PREFIXES):
            out.append(f"-- [跳过] `{t}` 已存在")
            i += 1
            while i < len(lines) and not lines[i].strip().endswith(";"):
                i += 1
            if i < len(lines):
                i += 1
            continue
        nt = RENAME.get(t, t)
        out.append(line.replace(f"CREATE TABLE `{t}`", f"CREATE TABLE IF NOT EXISTS `{nt}`"))
        i += 1
        while i < len(lines):
            if lines[i].strip() == ";":
                out.append(";")
                i += 1
                break
            if re.match(r"\s*DROP TABLE", lines[i]) or re.match(r"\s*CREATE TABLE", lines[i]):
                break
            out.append(lines[i])
            i += 1
        out.append("")
        continue

    m_ins = re.match(r"\s*INSERT INTO `([^`]+)`", line)
    if m_ins:
        t = m_ins.group(1)
        if any(t.startswith(p) for p in SKIP_PREFIXES):
            i += 1
            continue
        nt = RENAME.get(t, t)
        out.append(line.replace(f"INSERT INTO `{t}`", f"INSERT IGNORE INTO `{nt}`"))
        i += 1
        continue

    if re.match(r"\s*(BEGIN|COMMIT)\s*;?\s*$", line.strip()):
        out.append(line)
    i += 1

out.append("SET FOREIGN_KEY_CHECKS = 1;")
out.append("")
out.append("-- 安装完成。若依后台账号见 sys_user_ry（默认 admin / admin123）")

dest = ROOT / "ruoyi-compat-chat_platform.sql"
dest.write_text("\n".join(out), encoding="utf-8")
print(f"Wrote {dest} ({dest.stat().st_size} bytes, {len(out)} lines)")
