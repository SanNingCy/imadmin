USE chat_platform_db;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- skip DROP `gen_table`
CREATE TABLE IF NOT EXISTS `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) COLLATE utf8mb4_bin DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) COLLATE utf8mb4_bin DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作 sub主子表操作）',
  `package_name` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '生成功能作者',
  `form_col_num` int DEFAULT '1' COMMENT '表单布局（单列 双列 三列）',
  `gen_type` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) COLLATE utf8mb4_bin DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='代码生成业务表';

BEGIN;
COMMIT;
-- skip DROP `gen_table_column`
CREATE TABLE IF NOT EXISTS `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) COLLATE utf8mb4_bin DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='代码生成业务表字段';

BEGIN;
COMMIT;
-- skip quartz `QRTZ_BLOB_TRIGGERS`
-- skip create `QRTZ_BLOB_TRIGGERS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_CALENDARS`
-- skip create `QRTZ_CALENDARS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_CRON_TRIGGERS`
-- skip create `QRTZ_CRON_TRIGGERS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_FIRED_TRIGGERS`
-- skip create `QRTZ_FIRED_TRIGGERS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_JOB_DETAILS`
-- skip create `QRTZ_JOB_DETAILS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_LOCKS`
-- skip create `QRTZ_LOCKS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_PAUSED_TRIGGER_GRPS`
-- skip create `QRTZ_PAUSED_TRIGGER_GRPS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_SCHEDULER_STATE`
-- skip create `QRTZ_SCHEDULER_STATE`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_SIMPLE_TRIGGERS`
-- skip create `QRTZ_SIMPLE_TRIGGERS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_SIMPROP_TRIGGERS`
-- skip create `QRTZ_SIMPROP_TRIGGERS`
BEGIN;
COMMIT;
-- skip quartz `QRTZ_TRIGGERS`
-- skip create `QRTZ_TRIGGERS`
BEGIN;
COMMIT;
-- skip DROP protect IM `sys_config` ruoyi=`sys_param_config`
CREATE TABLE IF NOT EXISTS `sys_param_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) COLLATE utf8mb4_bin DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) COLLATE utf8mb4_bin DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='参数配置表';

BEGIN;
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '初始化密码 123456');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '深黑主题theme-dark，浅色主题theme-light，深蓝主题theme-blue');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '用户管理-密码字符范围', 'sys.account.chrtype', '0', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '主框架页-菜单导航显示风格', 'sys.index.menuStyle', 'default', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '菜单导航显示风格（default为左侧导航菜单，topnav为顶部导航菜单）');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, '主框架页-是否开启页脚', 'sys.index.footer', 'true', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '是否开启底部页脚显示（true显示，false隐藏）');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, '主框架页-是否开启页签', 'sys.index.tagsView', 'true', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '是否开启菜单多页签显示（true显示，false隐藏）');
INSERT IGNORE INTO `sys_param_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2026-06-03 07:59:20', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
COMMIT;
-- skip DROP `sys_dept`
CREATE TABLE IF NOT EXISTS `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) COLLATE utf8mb4_bin DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '邮箱',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='部门表';

BEGIN;
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (100, 0, '0', 'WEB4X科技', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (101, 100, '0,100', '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (105, 101, '0,100,101', '测试部门', 3, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (108, 102, '0,100,102', '市场部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
INSERT IGNORE INTO `sys_dept` (`dept_id`, `parent_id`, `ancestors`, `dept_name`, `order_num`, `leader`, `phone`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (109, 102, '0,100,102', '财务部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL);
COMMIT;
-- skip DROP `sys_dict_data`
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) COLLATE utf8mb4_bin DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典数据表';

BEGIN;
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '性别男');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '性别女');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '性别未知');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '显示菜单');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '隐藏菜单');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '正常状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '停用状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '正常状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '停用状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '默认分组');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '系统分组');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '系统默认是');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '系统默认否');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '通知');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '公告');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '正常状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '关闭状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '其他操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '新增操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '修改操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '删除操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '授权操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '导出操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '导入操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '强退操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '生成操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '清空操作');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '正常状态');
INSERT IGNORE INTO `sys_dict_data` (`dict_code`, `dict_sort`, `dict_label`, `dict_value`, `dict_type`, `css_class`, `list_class`, `is_default`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '停用状态');
COMMIT;
-- skip DROP protect IM `sys_dict_type` ruoyi=`sys_dict_type_ry`
CREATE TABLE IF NOT EXISTS `sys_dict_type_ry` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '字典类型',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='字典类型表';

BEGIN;
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '用户性别列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '菜单状态列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '系统开关列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '任务状态列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '任务分组列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '系统是否列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '通知类型列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '通知状态列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '操作类型列表');
INSERT IGNORE INTO `sys_dict_type_ry` (`dict_id`, `dict_name`, `dict_type`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '登录状态列表');
COMMIT;
-- skip DROP `sys_job`
CREATE TABLE IF NOT EXISTS `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) COLLATE utf8mb4_bin DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) COLLATE utf8mb4_bin DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务调度表';

BEGIN;
INSERT IGNORE INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT IGNORE INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT IGNORE INTO `sys_job` (`job_id`, `job_name`, `job_group`, `invoke_target`, `cron_expression`, `misfire_policy`, `concurrent`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2026-06-03 07:59:20', '', NULL, '');
COMMIT;
-- skip DROP `sys_job_log`
CREATE TABLE IF NOT EXISTS `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '日志信息',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) COLLATE utf8mb4_bin DEFAULT '' COMMENT '异常信息',
  `start_time` datetime DEFAULT NULL COMMENT '执行开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '执行结束时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务调度日志表';

BEGIN;
COMMIT;
-- skip DROP `sys_logininfor`
CREATE TABLE IF NOT EXISTS `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `login_name` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '登录账号',
  `ipaddr` varchar(128) COLLATE utf8mb4_bin DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '操作系统',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='系统访问记录';

BEGIN;



SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_menu_ry
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu_ry`;
CREATE TABLE `sys_menu_ry`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '#' COMMENT '请求地址',
  `target` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '打开方式（menuItem页签 menuBlank新窗口）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `is_refresh` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '是否刷新（0刷新 1不刷新）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2256 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu_ry
-- ----------------------------
INSERT INTO `sys_menu_ry` VALUES (1, '系统管理', 0, 1, '#', '', 'M', '0', '1', '', 'fa fa-gear', 'admin', '2026-06-03 07:59:20', '', NULL, '系统管理目录');
INSERT INTO `sys_menu_ry` VALUES (2, '系统监控', 0, 11, '#', 'menuItem', 'M', '0', '1', '', 'fa fa-video-camera', 'admin', '2026-06-03 07:59:20', 'admin', '2026-06-06 11:57:07', '系统监控目录');
INSERT INTO `sys_menu_ry` VALUES (3, '系统工具', 0, 3, '#', 'menuItem', 'M', '1', '1', '', 'fa fa-bars', 'admin', '2026-06-03 07:59:20', 'admin', '2026-06-06 11:56:17', '系统工具目录');
INSERT INTO `sys_menu_ry` VALUES (100, '用户管理', 1, 1, '/system/user', '', 'C', '0', '1', 'system:user:view', 'fa fa-user-o', 'admin', '2026-06-03 07:59:20', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu_ry` VALUES (101, '角色管理', 1, 2, '/system/role', '', 'C', '0', '1', 'system:role:view', 'fa fa-user-secret', 'admin', '2026-06-03 07:59:20', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu_ry` VALUES (102, '菜单管理', 1, 3, '/system/menu', '', 'C', '0', '1', 'system:menu:view', 'fa fa-th-list', 'admin', '2026-06-03 07:59:20', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu_ry` VALUES (103, '部门管理', 1, 4, '/system/dept', '', 'C', '0', '1', 'system:dept:view', 'fa fa-outdent', 'admin', '2026-06-03 07:59:20', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu_ry` VALUES (104, '岗位管理', 1, 5, '/system/post', '', 'C', '0', '1', 'system:post:view', 'fa fa-address-card-o', 'admin', '2026-06-03 07:59:20', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu_ry` VALUES (105, '字典管理', 1, 6, '/system/dict', '', 'C', '0', '1', 'system:dict:view', 'fa fa-bookmark-o', 'admin', '2026-06-03 07:59:20', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu_ry` VALUES (106, '参数设置', 1, 7, '/system/config', '', 'C', '0', '1', 'system:config:view', 'fa fa-sun-o', 'admin', '2026-06-03 07:59:20', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu_ry` VALUES (107, '通知公告', 1, 8, '/system/notice', '', 'C', '0', '1', 'system:notice:view', 'fa fa-bullhorn', 'admin', '2026-06-03 07:59:20', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu_ry` VALUES (108, '日志管理', 1, 9, '#', '', 'M', '0', '1', '', 'fa fa-pencil-square-o', 'admin', '2026-06-03 07:59:20', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu_ry` VALUES (109, '在线用户', 2, 1, '/monitor/online', '', 'C', '0', '1', 'monitor:online:view', 'fa fa-user-circle', 'admin', '2026-06-03 07:59:20', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu_ry` VALUES (110, '定时任务', 2, 2, '/monitor/job', '', 'C', '0', '1', 'monitor:job:view', 'fa fa-tasks', 'admin', '2026-06-03 07:59:20', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu_ry` VALUES (111, '数据监控', 2, 3, '/monitor/data', '', 'C', '0', '1', 'monitor:data:view', 'fa fa-bug', 'admin', '2026-06-03 07:59:20', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu_ry` VALUES (112, '服务监控', 2, 4, '/monitor/server', '', 'C', '0', '1', 'monitor:server:view', 'fa fa-server', 'admin', '2026-06-03 07:59:20', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu_ry` VALUES (113, '缓存监控', 2, 5, '/monitor/cache', '', 'C', '0', '1', 'monitor:cache:view', 'fa fa-cube', 'admin', '2026-06-03 07:59:20', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu_ry` VALUES (114, '表单构建', 3, 1, '/tool/build', '', 'C', '0', '1', 'tool:build:view', 'fa fa-wpforms', 'admin', '2026-06-03 07:59:20', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu_ry` VALUES (115, '代码生成', 3, 2, '/tool/gen', '', 'C', '0', '1', 'tool:gen:view', 'fa fa-code', 'admin', '2026-06-03 07:59:20', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu_ry` VALUES (116, '系统接口', 3, 3, '/tool/swagger', '', 'C', '0', '1', 'tool:swagger:view', 'fa fa-gg', 'admin', '2026-06-03 07:59:20', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu_ry` VALUES (500, '操作日志', 108, 1, '/monitor/operlog', '', 'C', '0', '1', 'monitor:operlog:view', 'fa fa-address-book', 'admin', '2026-06-03 07:59:20', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu_ry` VALUES (501, '登录日志', 108, 2, '/monitor/logininfor', '', 'C', '0', '1', 'monitor:logininfor:view', 'fa fa-file-image-o', 'admin', '2026-06-03 07:59:20', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu_ry` VALUES (1000, '用户查询', 100, 1, '#', '', 'F', '0', '1', 'system:user:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1001, '用户新增', 100, 2, '#', '', 'F', '0', '1', 'system:user:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1002, '用户修改', 100, 3, '#', '', 'F', '0', '1', 'system:user:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1003, '用户删除', 100, 4, '#', '', 'F', '0', '1', 'system:user:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1004, '用户导出', 100, 5, '#', '', 'F', '0', '1', 'system:user:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1005, '用户导入', 100, 6, '#', '', 'F', '0', '1', 'system:user:import', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1006, '重置密码', 100, 7, '#', '', 'F', '0', '1', 'system:user:resetPwd', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1007, '角色查询', 101, 1, '#', '', 'F', '0', '1', 'system:role:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1008, '角色新增', 101, 2, '#', '', 'F', '0', '1', 'system:role:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1009, '角色修改', 101, 3, '#', '', 'F', '0', '1', 'system:role:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1010, '角色删除', 101, 4, '#', '', 'F', '0', '1', 'system:role:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1011, '角色导出', 101, 5, '#', '', 'F', '0', '1', 'system:role:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1012, '菜单查询', 102, 1, '#', '', 'F', '0', '1', 'system:menu:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1013, '菜单新增', 102, 2, '#', '', 'F', '0', '1', 'system:menu:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1014, '菜单修改', 102, 3, '#', '', 'F', '0', '1', 'system:menu:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1015, '菜单删除', 102, 4, '#', '', 'F', '0', '1', 'system:menu:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1016, '部门查询', 103, 1, '#', '', 'F', '0', '1', 'system:dept:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1017, '部门新增', 103, 2, '#', '', 'F', '0', '1', 'system:dept:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1018, '部门修改', 103, 3, '#', '', 'F', '0', '1', 'system:dept:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1019, '部门删除', 103, 4, '#', '', 'F', '0', '1', 'system:dept:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1020, '岗位查询', 104, 1, '#', '', 'F', '0', '1', 'system:post:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1021, '岗位新增', 104, 2, '#', '', 'F', '0', '1', 'system:post:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1022, '岗位修改', 104, 3, '#', '', 'F', '0', '1', 'system:post:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1023, '岗位删除', 104, 4, '#', '', 'F', '0', '1', 'system:post:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1024, '岗位导出', 104, 5, '#', '', 'F', '0', '1', 'system:post:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1025, '字典查询', 105, 1, '#', '', 'F', '0', '1', 'system:dict:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1026, '字典新增', 105, 2, '#', '', 'F', '0', '1', 'system:dict:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1027, '字典修改', 105, 3, '#', '', 'F', '0', '1', 'system:dict:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1028, '字典删除', 105, 4, '#', '', 'F', '0', '1', 'system:dict:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1029, '字典导出', 105, 5, '#', '', 'F', '0', '1', 'system:dict:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1030, '参数查询', 106, 1, '#', '', 'F', '0', '1', 'system:config:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1031, '参数新增', 106, 2, '#', '', 'F', '0', '1', 'system:config:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1032, '参数修改', 106, 3, '#', '', 'F', '0', '1', 'system:config:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1033, '参数删除', 106, 4, '#', '', 'F', '0', '1', 'system:config:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1034, '参数导出', 106, 5, '#', '', 'F', '0', '1', 'system:config:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1035, '公告查询', 107, 1, '#', '', 'F', '0', '1', 'system:notice:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1036, '公告新增', 107, 2, '#', '', 'F', '0', '1', 'system:notice:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1037, '公告修改', 107, 3, '#', '', 'F', '0', '1', 'system:notice:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1038, '公告删除', 107, 4, '#', '', 'F', '0', '1', 'system:notice:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1039, '操作查询', 500, 1, '#', '', 'F', '0', '1', 'monitor:operlog:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1040, '操作删除', 500, 2, '#', '', 'F', '0', '1', 'monitor:operlog:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1041, '详细信息', 500, 3, '#', '', 'F', '0', '1', 'monitor:operlog:detail', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1042, '日志导出', 500, 4, '#', '', 'F', '0', '1', 'monitor:operlog:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1043, '登录查询', 501, 1, '#', '', 'F', '0', '1', 'monitor:logininfor:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1044, '登录删除', 501, 2, '#', '', 'F', '0', '1', 'monitor:logininfor:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1045, '日志导出', 501, 3, '#', '', 'F', '0', '1', 'monitor:logininfor:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1046, '账户解锁', 501, 4, '#', '', 'F', '0', '1', 'monitor:logininfor:unlock', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1047, '在线查询', 109, 1, '#', '', 'F', '0', '1', 'monitor:online:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1048, '批量强退', 109, 2, '#', '', 'F', '0', '1', 'monitor:online:batchForceLogout', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1049, '单条强退', 109, 3, '#', '', 'F', '0', '1', 'monitor:online:forceLogout', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1050, '任务查询', 110, 1, '#', '', 'F', '0', '1', 'monitor:job:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1051, '任务新增', 110, 2, '#', '', 'F', '0', '1', 'monitor:job:add', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1052, '任务修改', 110, 3, '#', '', 'F', '0', '1', 'monitor:job:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1053, '任务删除', 110, 4, '#', '', 'F', '0', '1', 'monitor:job:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1054, '状态修改', 110, 5, '#', '', 'F', '0', '1', 'monitor:job:changeStatus', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1055, '任务详细', 110, 6, '#', '', 'F', '0', '1', 'monitor:job:detail', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1056, '任务导出', 110, 7, '#', '', 'F', '0', '1', 'monitor:job:export', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1057, '生成查询', 115, 1, '#', '', 'F', '0', '1', 'tool:gen:list', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1058, '生成修改', 115, 2, '#', '', 'F', '0', '1', 'tool:gen:edit', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1059, '生成删除', 115, 3, '#', '', 'F', '0', '1', 'tool:gen:remove', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1060, '预览代码', 115, 4, '#', '', 'F', '0', '1', 'tool:gen:preview', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (1061, '生成代码', 115, 5, '#', '', 'F', '0', '1', 'tool:gen:code', '#', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2000, '用户与社交管理', 0, 6, '/social', 'menuItem', 'M', '0', '1', '', 'AppstoreAddOutlined', 'admin', '2026-06-06 10:53:01', 'admin', '2026-06-06 11:01:26', '');
INSERT INTO `sys_menu_ry` VALUES (2001, '群组与会议管理', 0, 7, '/group', 'menuItem', 'M', '0', '1', '', 'ApartmentOutlined', 'admin', '2026-06-06 10:53:01', 'admin', '2026-06-06 11:01:30', '');
INSERT INTO `sys_menu_ry` VALUES (2002, '用户管理', 2000, 30, '/user', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2004, '用户封禁管理', 2002, 60, '/ban', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2005, '用户注销申请', 2002, 90, '/cancel-apply', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2006, '好友关系管理', 2002, 120, '/friends', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2007, '默认好友配置', 2002, 30, '/default-friends', '', 'C', '1', '1', 'view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2008, '用户安全与行为', 2000, 60, '/security', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2009, '登录记录审计', 2008, 30, '/login-audit', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2010, '账户变更日志', 2008, 60, '/account-change-logs', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2011, '密保问题管理', 2008, 90, '/qa', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2012, '会员码管理', 2008, 120, '/vip-code', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2013, '内容与互动', 2000, 90, '/content', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2014, '表情包管理', 2013, 30, '/emojis', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2015, '朋友圈动态管理', 2013, 60, '/moments', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2016, '动态评论管理', 2013, 90, '/moment-comments', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2017, '群组管理', 2001, 30, '/manage', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2018, '群组信息列表', 2017, 30, '/info', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2019, '群成员管理', 2017, 60, '/member', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2020, '群投诉记录', 2017, 90, '/complaints', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2021, '群升级记录', 2017, 120, '/upgrade-logs', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2022, '群运营工具', 2017, 30, '/tools', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2023, '群欢迎语配置', 2017, 180, '/welcome', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2024, '群定时消息管理', 2017, 210, '/scheduled-message', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2025, '会议管理', 2001, 60, '/meeting', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2026, '会议列表查看', 2025, 30, '/list', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2027, '会议配置管理', 2025, 30, '/config', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2028, '财务与资产中心', 0, 5, '/asset', 'menuItem', 'M', '0', '1', '', 'DollarCircleOutlined', 'admin', '2026-06-06 10:53:01', 'admin', '2026-06-06 11:01:21', '');
INSERT INTO `sys_menu_ry` VALUES (2029, '资金管理', 2028, 30, '/fund', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2030, '用户余额明细', 2029, 30, '/balance', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2031, '充值记录查询', 2029, 60, '/deposit', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2032, '提现申请审批', 2029, 90, '/withdrawApply', '', 'C', '0', '1', 'asset:fund:withdraw:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2033, '链桥入金IM记录对账', 2029, 30, '/payment', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2034, '交易记录', 2028, 60, '/trade', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2035, '红包交易记录', 2034, 30, '/packet', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2036, '群红包领取明细', 2034, 60, '/claim-logs', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2037, '转账记录查询', 2034, 30, '/transfer', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2038, 'IM出金交易记录', 2034, 30, '/streams', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2039, '费率配置', 2028, 90, '/rate', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2040, '平台费率设置', 2039, 30, '/platform', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2041, '签到奖励配置', 2039, 90, '/signin-reward', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2042, '平台运营与配置', 0, 4, '/ops', 'menuItem', 'M', '0', '1', '', 'DollarCircleOutlined', 'admin', '2026-06-06 10:53:01', 'admin', '2026-06-06 11:00:57', '');
INSERT INTO `sys_menu_ry` VALUES (2043, '内容运营', 2042, 30, '/content', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2044, '公告管理配置', 2043, 30, '/announcement', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2045, '系统通知推送', 2043, 30, '/notify', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2046, '版本更新管理', 2043, 30, '/version', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2047, '提现页标题设置', 2043, 30, '/withdraw-title', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2048, '帮助与支持', 2042, 60, '/support', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2049, '平台协议管理', 2048, 30, '/agreement', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2050, '常见问题FAQ', 2048, 60, '/faq', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2051, '意见反馈处理', 2048, 90, '/feedback', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2052, '投诉原因配置', 2048, 120, '/complaint-reasons', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2053, '系统配置', 2042, 90, '/system', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2054, '基础平台配置', 2053, 30, '/base', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2055, '功能开关管理', 2053, 60, '/features', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2056, '安全与风控', 0, 8, '/risk', 'menuItem', 'M', '0', '1', '', 'SaveOutlined', 'admin', '2026-06-06 10:53:01', 'admin', '2026-06-06 11:01:35', '');
INSERT INTO `sys_menu_ry` VALUES (2057, '安全管理', 2056, 30, '/security', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2058, '封禁记录查询', 2057, 30, '/ban-logs', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2059, '异常行为监控', 2057, 30, '/abnormal-monitor', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2060, '敏感操作审计', 2057, 30, '/sensitive-audit', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2061, '风控管理', 2056, 30, '/control', '', 'C', '1', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2062, '交易风控设置', 2061, 30, '/trade', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2063, '内容审核规则', 2061, 60, '/content', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2064, '防欺诈配置', 2061, 90, '/anti-fraud', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2065, '用户列表', 2002, 150, '/list', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2066, '签到记录', 2008, 150, '/sign-logs', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2067, '新增', 2044, 30, '#', '', 'F', '0', '1', 'ops:content:announcement:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2068, '查看', 2044, 30, '#', '', 'F', '0', '1', 'ops:content:announcement:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2069, '删除', 2044, 30, '#', '', 'F', '0', '1', 'ops:content:announcement:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2070, '修改', 2044, 60, '#', '', 'F', '0', '1', 'ops:content:announcement:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2071, '新增', 2045, 30, '#', '', 'F', '0', '1', 'ops:content:notify:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2072, '查看', 2045, 60, '#', '', 'F', '0', '1', 'ops:content:notify:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2073, '修改', 2045, 90, '#', '', 'F', '0', '1', 'ops:content:notify:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2074, '删除', 2045, 120, '#', '', 'F', '0', '1', 'ops:content:notify:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2075, '新增', 2046, 30, '#', '', 'F', '0', '1', 'ops:content:version:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2076, '查看', 2046, 60, '#', '', 'F', '0', '1', 'ops:content:version:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2077, '修改', 2046, 90, '#', '', 'F', '0', '1', 'ops:content:version:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2078, '删除', 2046, 120, '#', '', 'F', '0', '1', 'ops:content:version:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2079, '新增', 2047, 30, '#', '', 'F', '0', '1', 'ops:content:withdraw-title:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2080, '查看', 2047, 60, '#', '', 'F', '0', '1', 'ops:content:withdraw-title:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2081, '修改', 2047, 90, '#', '', 'F', '0', '1', 'ops:content:withdraw-title:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2082, '删除', 2047, 120, '#', '', 'F', '0', '1', 'ops:content:withdraw-title:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2083, '修改', 2049, 30, '#', '', 'F', '0', '1', 'ops:support:agreement:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2084, '查看', 2049, 30, '#', '', 'F', '0', '1', 'ops:support:agreement:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2085, '新增', 2050, 30, '#', '', 'F', '0', '1', 'ops:support:faq:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2086, '查看', 2050, 60, '#', '', 'F', '0', '1', 'ops:support:faq:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2087, '修改', 2050, 90, '#', '', 'F', '0', '1', 'ops:support:faq:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2088, '删除', 2050, 120, '#', '', 'F', '0', '1', 'ops:support:faq:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2089, '查看', 2051, 30, '#', '', 'F', '0', '1', 'ops:support:feedback:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2090, '删除', 2051, 60, '#', '', 'F', '0', '1', 'ops:support:feedback:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2091, '新增', 2052, 30, '#', '', 'F', '0', '1', 'ops:support:complaint-reasons:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2092, '查看', 2052, 60, '#', '', 'F', '0', '1', 'ops:support:complaint-reasons:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2093, '修改', 2052, 90, '#', '', 'F', '0', '1', 'ops:support:complaint-reasons:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2094, '删除', 2052, 120, '#', '', 'F', '0', '1', 'ops:support:complaint-reasons:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2095, '查看', 2054, 30, '#', '', 'F', '0', '1', 'ops:system:base:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2096, '修改', 2054, 30, '#', '', 'F', '0', '1', 'ops:system:base:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2097, '新增', 2055, 30, '#', '', 'F', '0', '1', 'ops:system:features:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2098, '查看', 2055, 30, '#', '', 'F', '0', '1', 'ops:system:features:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2099, '修改', 2055, 30, '#', '', 'F', '0', '1', 'ops:system:features:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2100, '删除', 2055, 30, '#', '', 'F', '0', '1', 'ops:system:features:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2101, '审核通过', 2031, 30, '#', '', 'F', '0', '1', 'asset:fund:deposit:approved', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2102, '驳回', 2031, 60, '#', '', 'F', '0', '1', 'asset:fund:deposit:reject', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2103, '锁定', 2031, 90, '#', '', 'F', '0', '1', 'asset:fund:deposit:lock', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2104, '查看', 2032, 30, '#', '', 'F', '0', '1', 'asset:fund:withdraw:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2105, '审核', 2032, 30, '#', '', 'F', '0', '1', 'asset:fund:withdraw:review', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2106, '封禁', 2004, 30, '#', '', 'F', '0', '1', 'social:user:ban:ban', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2107, '解禁', 2004, 60, '#', '', 'F', '0', '1', 'social:user:ban:unban', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2108, '审核通过', 2005, 30, '#', '', 'F', '0', '1', 'social:user:cancel-apply:approved', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2109, '驳回', 2005, 60, '#', '', 'F', '0', '1', 'social:user:cancel-apply:reject', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2110, '新增好友', 2006, 30, '#', '', 'F', '0', '1', 'social:user:friends:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2111, '删除好友', 2006, 60, '#', '', 'F', '0', '1', 'social:user:friends:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2112, '查看', 2065, 30, '#', '', 'F', '0', '1', 'social:user:list:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2113, '修改', 2065, 60, '#', '', 'F', '0', '1', 'social:user:list:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2114, '变更余额', 2065, 90, '#', '', 'F', '0', '1', 'social:user:list:ChangeBalance', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2115, '交易明细', 2065, 120, '#', '', 'F', '0', '1', 'social:user:list:transaction', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2116, '清除支付密码', 2065, 150, '#', '', 'F', '0', '1', 'social:user:list:clear', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2117, '删除', 2065, 180, '#', '', 'F', '0', '1', 'social:user:list:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2118, '查看', 2009, 30, '#', '', 'F', '0', '1', 'social:security:login-audit:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2119, '新增', 2011, 30, '#', '', 'F', '0', '1', 'social:security:qa:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2120, '查看', 2011, 60, '#', '', 'F', '0', '1', 'social:security:qa:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2121, '修改', 2011, 90, '#', '', 'F', '0', '1', 'social:security:qa:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2122, '删除', 2011, 120, '#', '', 'F', '0', '1', 'social:security:qa:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2123, '新增', 2012, 30, '#', '', 'F', '0', '1', 'social:security:vip-code:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2124, '查看', 2012, 60, '#', '', 'F', '0', '1', 'social:security:vip-code:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2125, '删除', 2012, 90, '#', '', 'F', '0', '1', 'social:security:vip-code:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2126, '批量生成', 2012, 120, '#', '', 'F', '0', '1', 'social:security:vip-code:batch', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2127, '编辑', 2012, 150, '#', '', 'F', '0', '1', 'social:security:vip-code:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2128, '会员码同步', 2012, 180, '#', '', 'F', '0', '1', 'social:security:vip-code:syncStatus', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2129, '评论列表', 2015, 30, '#', '', 'F', '0', '1', 'social:content:moments:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2130, '删除', 2015, 60, '#', '', 'F', '0', '1', 'social:content:moments:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2131, '封禁', 2058, 30, '#', '', 'F', '0', '1', 'risk:security:ban-logs:ban', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2132, '解禁', 2058, 60, '#', '', 'F', '0', '1', 'risk:security:ban-logs:unban', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2134, '用户谷歌安全验证', 2057, 60, '/google-verify', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2135, '用户密保问题', 2057, 30, '/security-question', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2136, '查看', 2033, 30, '#', '', 'F', '0', '1', 'asset:fund:payment:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2137, '查看', 2038, 30, '#', '', 'F', '0', '1', 'asset:trade:streams:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2138, '查看', 2040, 30, '#', '', 'F', '0', '1', 'asset:rate:platform:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2139, '编辑', 2040, 60, '#', '', 'F', '0', '1', 'asset:rate:platform:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2140, '新建', 2040, 90, '#', '', 'F', '0', '1', 'asset:rate:platform:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2141, '查看', 2041, 30, '#', '', 'F', '0', '1', 'asset:rate:signin-reward:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2142, '编辑', 2041, 60, '#', '', 'F', '0', '1', 'asset:rate:signin-reward:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2143, '评论列表', 2016, 30, '#', '', 'F', '0', '1', 'social:content:moment-comments:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2144, '删除', 2016, 60, '#', '', 'F', '0', '1', 'social:content:moment-comments:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2145, '查看', 2018, 30, '#', '', 'F', '0', '1', 'group:manage:info:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2146, '编辑', 2018, 60, '#', '', 'F', '0', '1', 'group:manage:info:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2147, '删除', 2018, 90, '#', '', 'F', '0', '1', 'group:manage:info:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2148, '群成员', 2018, 120, '#', '', 'F', '0', '1', 'group:manage:info:member', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2149, '查看', 2020, 30, '#', '', 'F', '0', '1', 'group:manage:complaints:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2150, '查看', 2135, 30, '#', '', 'F', '0', '1', 'risk:security:security-question:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2151, '编辑', 2135, 60, '#', '', 'F', '0', '1', 'risk:security:security-question:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2152, '重置密保', 2135, 90, '#', '', 'F', '0', '1', 'risk:security:security-question:reset', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2153, '重置谷歌验证码', 2134, 30, '#', '', 'F', '0', '1', 'risk:security:google-verify:reset', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2154, '解除禁言', 2019, 30, '#', '', 'F', '0', '1', 'group:manage:member:reset', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2160, 'IM内部转账交易记录', 2034, 30, '/im-streams', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2161, '查看', 2160, 30, '#', '', 'F', '0', '1', 'asset:trade:im-streams:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2162, '提现配置', 2053, 30, '/withdrawConfig', '', 'C', '0', '1', 'ops:system:withdraw:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2163, '查看', 2162, 30, '#', '', 'F', '0', '1', 'ops:system:withdraw:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2164, '编辑', 2162, 90, '#', '', 'F', '0', '1', 'ops:system:withdraw:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2165, '会议室时长配置', 2025, 90, '/duration', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2166, '会议室人员配置', 2025, 120, '/personnel', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2167, '会议室订单', 2025, 150, '/order', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2168, '会议代币系数配置', 2025, 180, '/token-config', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2169, '会议室价格配置', 2025, 210, '/price', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2170, '解散会议室', 2026, 30, '#', '', 'F', '0', '1', 'group:meeting:list:dismiss', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2171, '新增', 2165, 30, '#', '', 'F', '0', '1', 'group:meeting:duration:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2172, '删除', 2165, 60, '#', '', 'F', '0', '1', 'group:meeting:duration:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2173, '编辑', 2165, 90, '#', '', 'F', '0', '1', 'group:meeting:duration:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2174, '查看', 2165, 120, '#', '', 'F', '0', '1', 'group:meeting:duration:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2175, '新增', 2166, 30, '#', '', 'F', '0', '1', 'group:meeting:personnel:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2176, '编辑', 2166, 60, '#', '', 'F', '0', '1', 'group:meeting:personnel:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2177, '删除', 2166, 90, '#', '', 'F', '0', '1', 'group:meeting:personnel:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2178, '查看', 2166, 120, '#', '', 'F', '0', '1', 'group:meeting:personnel:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2179, '新增', 2169, 30, '#', '', 'F', '0', '1', 'group:meeting:price:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2180, '编辑', 2169, 60, '#', '', 'F', '0', '1', 'group:meeting:price:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2181, '删除', 2169, 90, '#', '', 'F', '0', '1', 'group:meeting:price:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2182, '查看', 2169, 120, '#', '', 'F', '0', '1', 'group:meeting:price:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2183, '用户信用分', 2000, 30, '/credit', '', 'M', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2184, '信用分基础配置', 2183, 30, '/configuration', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2185, '信用分类型配置', 2183, 60, '/type', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2186, '信用分明细记录', 2183, 90, '/detailed-records', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2187, '用户信用分管理', 2183, 120, '/management', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2188, '公共配置', 2183, 30, '/common-configuration', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2189, '新增', 2184, 30, '#', '', 'F', '0', '1', 'social:credit:configuration:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2190, '删除', 2184, 60, '#', '', 'F', '0', '1', 'social:credit:configuration:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2191, '编辑', 2184, 90, '#', '', 'F', '0', '1', 'social:credit:configuration:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2192, '查看', 2184, 120, '#', '', 'F', '0', '1', 'social:credit:configuration:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2193, '查看', 2185, 30, '#', '', 'F', '0', '1', 'social:credit:type:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2194, '删除', 2185, 60, '#', '', 'F', '0', '1', 'social:credit:type:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2195, '编辑', 2185, 90, '#', '', 'F', '0', '1', 'social:credit:type:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2196, '新增', 2185, 120, '#', '', 'F', '0', '1', 'social:credit:type:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2197, '查看', 2186, 30, '#', '', 'F', '0', '1', 'social:credit:detailed-records:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2198, '新增', 2187, 30, '#', '', 'F', '0', '1', 'social:credit:management:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2199, '查看', 2187, 60, '#', '', 'F', '0', '1', 'social:credit:management:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2200, '修改', 2187, 90, '#', '', 'F', '0', '1', 'social:credit:management:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2201, '删除', 2187, 120, '#', '', 'F', '0', '1', 'social:credit:management:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2202, '查看', 2188, 30, '#', '', 'F', '0', '1', 'social:credit:common-configuration:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2203, '修改', 2188, 60, '#', '', 'F', '0', '1', 'social:credit:common-configuration:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2204, '全局敏感词', 2057, 90, '/sensitive-word', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2205, '修改', 2204, 30, '#', '', 'F', '0', '1', 'risk:security:sensitive-word:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2206, '删除', 2204, 60, '#', '', 'F', '0', '1', 'risk:security:sensitive-word:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2207, '添加', 2204, 90, '#', '', 'F', '0', '1', 'risk:security:sensitive-word:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2208, '广场朋友圈管理', 0, 9, '/piamom', 'menuItem', 'M', '0', '1', '', 'AppstoreOutlined', 'admin', '2026-06-06 10:53:01', 'admin', '2026-06-06 11:01:39', '');
INSERT INTO `sys_menu_ry` VALUES (2209, '朋友圈管理', 2208, 10, '/piamom/moment', '', 'M', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2210, '朋友圈列表', 2209, 10, '/piamom/moment/list', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2211, '查看', 2210, 10, '#', '', 'F', '0', '1', 'piamom:moment:list:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2212, '编辑', 2210, 20, '#', '', 'F', '0', '1', 'piamom:moment:list:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2213, '删除', 2210, 30, '#', '', 'F', '0', '1', 'piamom:moment:list:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2214, '朋友圈评论列表', 2209, 20, '/piamom/moment/comment', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2215, '查看', 2214, 10, '#', '', 'F', '0', '1', 'piamom:moment:comment:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2216, '删除', 2214, 20, '#', '', 'F', '0', '1', 'piamom:moment:comment:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2217, '朋友圈点赞列表', 2209, 30, '/piamom/moment/like', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2218, '查看', 2217, 10, '#', '', 'F', '0', '1', 'piamom:moment:like:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2219, '删除', 2217, 20, '#', '', 'F', '0', '1', 'piamom:moment:like:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2220, '广场管理', 2208, 20, '/piamom/square', '', 'M', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2221, '广场列表', 2220, 10, '/piamom/square/list', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2222, '查看', 2221, 10, '#', '', 'F', '0', '1', 'piamom:square:list:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2223, '编辑', 2221, 20, '#', '', 'F', '0', '1', 'piamom:square:list:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2224, '删除', 2221, 30, '#', '', 'F', '0', '1', 'piamom:square:list:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2225, '广场评论列表', 2220, 20, '/piamom/square/comment', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2226, '查看', 2225, 10, '#', '', 'F', '0', '1', 'piamom:square:comment:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2227, '删除', 2225, 20, '#', '', 'F', '0', '1', 'piamom:square:comment:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2228, '广场点赞列表', 2220, 30, '/piamom/square/like', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2229, '查看', 2228, 10, '#', '', 'F', '0', '1', 'piamom:square:like:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2230, '删除', 2228, 20, '#', '', 'F', '0', '1', 'piamom:square:like:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2231, '广场热门配置', 2220, 40, '/piamom/square/hot-config', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2232, '查看', 2231, 10, '#', '', 'F', '0', '1', 'piamom:square:hot-config:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2233, '新增', 2231, 20, '#', '', 'F', '0', '1', 'piamom:square:hot-config:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2234, '编辑', 2231, 30, '#', '', 'F', '0', '1', 'piamom:square:hot-config:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2235, '删除', 2231, 40, '#', '', 'F', '0', '1', 'piamom:square:hot-config:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2236, '广场发帖信用分额度', 2220, 50, '/piamom/square/publish-quota', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2237, '查看', 2236, 10, '#', '', 'F', '0', '1', 'piamom:square:publish-quota:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2238, '新增', 2236, 20, '#', '', 'F', '0', '1', 'piamom:square:publish-quota:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2239, '编辑', 2236, 30, '#', '', 'F', '0', '1', 'piamom:square:publish-quota:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2240, '删除', 2236, 40, '#', '', 'F', '0', '1', 'piamom:square:publish-quota:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2241, '举报管理', 2208, 30, '/piamom/report', '', 'M', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2242, '举报类型配置', 2241, 10, '/piamom/report/config', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2243, '查看', 2242, 10, '#', '', 'F', '0', '1', 'piamom:report:config:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2244, '新增', 2242, 20, '#', '', 'F', '0', '1', 'piamom:report:config:add', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2245, '编辑', 2242, 30, '#', '', 'F', '0', '1', 'piamom:report:config:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2246, '删除', 2242, 40, '#', '', 'F', '0', '1', 'piamom:report:config:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2247, '举报记录审核', 2241, 20, '/piamom/report/record', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2248, '查看', 2247, 10, '#', '', 'F', '0', '1', 'piamom:report:record:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2249, '审核', 2247, 20, '#', '', 'F', '0', '1', 'piamom:report:record:edit', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2250, '互动消息', 2208, 40, '/piamom/interact', '', 'M', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2251, '互动消息', 2250, 10, '/piamom/notify/list', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2252, '查看', 2251, 10, '#', '', 'F', '0', '1', 'piamom:notify:list:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2253, '用户主页统计', 2250, 20, '/piamom/profile/stat', '', 'C', '0', '1', NULL, '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2254, '查看', 2253, 10, '#', '', 'F', '0', '1', 'piamom:profile:stat:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2255, '删除', 2253, 20, '#', '', 'F', '0', '1', 'piamom:profile:stat:delete', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2256, '链上支付订单', 2029, 75, '/chainPayOrder', '', 'C', '0', '1', 'asset:fund:chain-pay:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');
INSERT INTO `sys_menu_ry` VALUES (2257, '查看', 2256, 10, '#', '', 'F', '0', '1', 'asset:fund:chain-pay:view', '#', 'admin', '2026-06-06 10:53:01', '', NULL, '');

SET FOREIGN_KEY_CHECKS = 1;

INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (100, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '登录成功', '2026-06-03 08:34:15');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (101, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '退出成功', '2026-06-03 08:37:17');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '1', '验证码错误', '2026-06-03 08:39:30');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '登录成功', '2026-06-03 08:39:32');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (104, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '退出成功', '2026-06-03 08:44:24');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (105, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '登录成功', '2026-06-03 08:44:31');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (106, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '退出成功', '2026-06-03 08:48:12');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (107, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '登录成功', '2026-06-03 08:48:16');
INSERT IGNORE INTO `sys_logininfor` (`info_id`, `login_name`, `ipaddr`, `login_location`, `browser`, `os`, `status`, `msg`, `login_time`) VALUES (108, 'admin', '127.0.0.1', '内网IP', 'Chrome 148', 'Mac OS >=10.15.7', '0', '退出成功', '2026-06-03 08:48:43');
COMMIT;

-- skip DROP `sys_notice`
CREATE TABLE IF NOT EXISTS `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '公告标题',
  `notice_type` char(1) COLLATE utf8mb4_bin NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='通知公告表';

BEGIN;
COMMIT;
-- skip DROP protect IM `sys_notice_read` ruoyi=`sys_notice_read_ry`
CREATE TABLE IF NOT EXISTS `sys_notice_read_ry` (
  `read_id` bigint NOT NULL AUTO_INCREMENT COMMENT '已读主键',
  `notice_id` int NOT NULL COMMENT '公告id',
  `user_id` bigint NOT NULL COMMENT '用户id',
  `read_time` datetime NOT NULL COMMENT '阅读时间',
  PRIMARY KEY (`read_id`),
  UNIQUE KEY `uk_user_notice` (`user_id`,`notice_id`) COMMENT '同一用户同一公告只记录一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='公告已读记录表';

BEGIN;
COMMIT;
-- skip DROP `sys_oper_log`
CREATE TABLE IF NOT EXISTS `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) COLLATE utf8mb4_bin DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) COLLATE utf8mb4_bin DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) COLLATE utf8mb4_bin DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) COLLATE utf8mb4_bin DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) COLLATE utf8mb4_bin DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='操作日志记录';

BEGIN;
INSERT IGNORE INTO `sys_oper_log` (`oper_id`, `title`, `business_type`, `method`, `request_method`, `operator_type`, `oper_name`, `dept_name`, `oper_url`, `oper_ip`, `oper_location`, `oper_param`, `json_result`, `status`, `error_msg`, `oper_time`, `cost_time`) VALUES (100, '通知公告', 3, 'com.web4x.web.controller.system.SysNoticeController.remove()', 'POST', 1, 'admin', '研发部门', '/system/notice/remove', '127.0.0.1', '内网IP', '{\"ids\":[\"3,2,1\"]}', '{\"msg\":\"操作成功\",\"code\":0}', 0, NULL, '2026-06-03 08:35:35', 32);
COMMIT;
-- skip DROP protect IM `sys_post` ruoyi=`sys_post_ry`
CREATE TABLE IF NOT EXISTS `sys_post_ry` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) COLLATE utf8mb4_bin NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='岗位信息表';

BEGIN;
INSERT IGNORE INTO `sys_post_ry` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT IGNORE INTO `sys_post_ry` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT IGNORE INTO `sys_post_ry` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2026-06-03 07:59:20', '', NULL, '');
INSERT IGNORE INTO `sys_post_ry` (`post_id`, `post_code`, `post_name`, `post_sort`, `status`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2026-06-03 07:59:20', '', NULL, '');
COMMIT;
-- skip DROP protect IM `sys_role` ruoyi=`sys_role_ry`
CREATE TABLE IF NOT EXISTS `sys_role_ry` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) COLLATE utf8mb4_bin DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `status` char(1) COLLATE utf8mb4_bin NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色信息表';

BEGIN;
INSERT INTO `sys_role_ry` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, '超级管理员', 'admin', 1, '1', '0', '0', 'admin', '2026-06-03 07:59:20', '', NULL, '超级管理员');
INSERT INTO `sys_role_ry` (`role_id`, `role_name`, `role_key`, `role_sort`, `data_scope`, `status`, `del_flag`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, '客服', 'common', 2, '2', '0', '0', 'admin', '2026-06-03 07:59:20', 'admin', '2026-06-10 11:01:17', '客服');

COMMIT;
-- skip DROP `sys_role_dept`
CREATE TABLE IF NOT EXISTS `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色和部门关联表';

BEGIN;
INSERT IGNORE INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (2, 100);
INSERT IGNORE INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (2, 101);
INSERT IGNORE INTO `sys_role_dept` (`role_id`, `dept_id`) VALUES (2, 105);
COMMIT;
-- skip DROP protect IM `sys_role_menu` ruoyi=`sys_role_menu_ry`
CREATE TABLE IF NOT EXISTS `sys_role_menu_ry` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='角色和菜单关联表';

BEGIN;
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 2);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 3);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 4);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 100);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 101);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 102);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 103);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 104);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 105);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 106);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 107);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 108);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 109);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 110);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 111);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 112);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 113);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 114);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 115);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 116);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 500);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 501);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1000);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1001);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1002);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1003);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1004);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1005);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1006);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1007);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1008);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1009);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1010);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1011);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1012);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1013);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1014);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1015);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1016);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1017);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1018);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1019);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1020);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1021);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1022);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1023);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1024);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1025);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1026);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1027);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1028);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1029);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1030);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1031);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1032);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1033);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1034);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1035);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1036);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1037);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1038);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1039);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1040);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1041);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1042);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1043);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1044);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1045);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1046);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1047);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1048);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1049);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1050);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1051);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1052);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1053);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1054);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1055);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1056);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1057);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1058);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1059);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1060);
INSERT IGNORE INTO `sys_role_menu_ry` (`role_id`, `menu_id`) VALUES (2, 1061);
COMMIT;
-- skip DROP protect IM `sys_user` ruoyi=`sys_user_ry`
CREATE TABLE IF NOT EXISTS `sys_user_ry` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `login_name` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT '登录账号',
  `user_name` varchar(30) COLLATE utf8mb4_bin DEFAULT '' COMMENT '用户昵称',
  `user_type` varchar(2) COLLATE utf8mb4_bin DEFAULT '00' COMMENT '用户类型（00系统用户 01注册用户）',
  `email` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) COLLATE utf8mb4_bin DEFAULT '' COMMENT '手机号码',
  `sex` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '头像路径',
  `password` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '密码',
  `salt` varchar(20) COLLATE utf8mb4_bin DEFAULT '' COMMENT '盐加密',
  `status` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_bin DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) COLLATE utf8mb4_bin DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_bin DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户信息表';

BEGIN;
INSERT INTO `sys_user_ry` (`user_id`, `dept_id`, `login_name`, `user_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `salt`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (1, 103, 'admin', '超级管理员', '00', 'ry@163.com', '15888888888', '0', '/profile/avatar/2026/06/09/c285b4ad769f41e58e40f1bdfbfc43c6.png', '56f1dc69414e231e3fc11eb8ce0925e5', 'b72763', '0', '0', '127.0.0.1', '2026-06-10 11:07:30', '2026-06-10 10:56:12', 'admin', '2026-06-03 07:59:20', '', '2026-06-10 10:56:12', '管理员');
INSERT INTO `sys_user_ry` (`user_id`, `dept_id`, `login_name`, `user_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `salt`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (2, 105, 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '8e6d98b90472783cc73c17047ddccf36', '222222', '0', '0', '127.0.0.1', NULL, NULL, 'admin', '2026-06-03 07:59:20', '', NULL, '测试员');
INSERT INTO `sys_user_ry` (`user_id`, `dept_id`, `login_name`, `user_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `salt`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (100, 101, 'android', 'android', '00', 'liangboai2@outlook.com', '15888854561', '0', '/profile/avatar/2026/06/10/ccf81d733b704841b4e062b7b3069d71.png', 'c28d8f0bca4dcfdb20e0b99a80fc2be2', 'a97ff8', '0', '0', '127.0.0.1', '2026-06-10 11:00:08', '2026-06-10 10:59:02', 'admin', '2026-06-10 10:59:02', '', '2026-06-10 11:02:32', NULL);
INSERT INTO `sys_user_ry` (`user_id`, `dept_id`, `login_name`, `user_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `salt`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (101, 101, 'iosadmin', 'iosadmin', '00', 'liangboai@outlook.com', '15345621258', '0', '', 'a45543af6adeccb7429a57dfc1549454', '9dbf58', '0', '0', '', NULL, '2026-06-10 11:04:21', 'admin', '2026-06-10 11:04:21', '', NULL, NULL);
INSERT INTO `sys_user_ry` (`user_id`, `dept_id`, `login_name`, `user_name`, `user_type`, `email`, `phonenumber`, `sex`, `avatar`, `password`, `salt`, `status`, `del_flag`, `login_ip`, `login_date`, `pwd_update_date`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES (102, 107, 'web4x', 'web4x', '00', 'longtaiyou@seekweb4.com', '15789545454', '0', '/profile/avatar/2026/06/10/a033db6964e24501be67530833be03b5.png', 'ff1fcff477e6f1fafbeeff7509ad7f07', '07202c', '0', '0', '127.0.0.1', '2026-06-10 11:06:13', '2026-06-10 11:05:49', 'admin', '2026-06-10 11:05:49', '', NULL, NULL);

COMMIT;
-- skip DROP `sys_user_online`
CREATE TABLE IF NOT EXISTS `sys_user_online` (
  `sessionId` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户会话id',
  `login_name` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '登录账号',
  `dept_name` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '部门名称',
  `ipaddr` varchar(128) COLLATE utf8mb4_bin DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '操作系统',
  `status` varchar(10) COLLATE utf8mb4_bin DEFAULT '' COMMENT '在线状态on_line在线off_line离线',
  `start_timestamp` datetime DEFAULT NULL COMMENT 'session创建时间',
  `last_access_time` datetime DEFAULT NULL COMMENT 'session最后访问时间',
  `expire_time` int DEFAULT '0' COMMENT '超时时间，单位为分钟',
  `session_data` blob COMMENT '序列化的Session数据，用于服务重启后恢复会话',
  PRIMARY KEY (`sessionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='在线用户记录';

BEGIN;
COMMIT;
-- skip DROP `sys_user_post`
CREATE TABLE IF NOT EXISTS `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户与岗位关联表';

BEGIN;
INSERT IGNORE INTO `sys_user_post` (`user_id`, `post_id`) VALUES (1, 1);
INSERT IGNORE INTO `sys_user_post` (`user_id`, `post_id`) VALUES (2, 2);
COMMIT;
-- skip DROP protect IM `sys_user_role` ruoyi=`sys_user_role_ry`
CREATE TABLE IF NOT EXISTS `sys_user_role_ry` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='用户和角色关联表';

BEGIN;
INSERT INTO `sys_user_role_ry` (`user_id`, `role_id`) VALUES (1, 1);
INSERT INTO `sys_user_role_ry` (`user_id`, `role_id`) VALUES (2, 2);
INSERT INTO `sys_user_role_ry` (`user_id`, `role_id`) VALUES (100, 2);
INSERT INTO `sys_user_role_ry` (`user_id`, `role_id`) VALUES (101, 2);
INSERT INTO `sys_user_role_ry` (`user_id`, `role_id`) VALUES (102, 2);

COMMIT;
SET FOREIGN_KEY_CHECKS = 1;

