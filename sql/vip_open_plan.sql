-- 会员链上开通套餐 + 公共配置开关字段
-- 已执行过请勿重复执行 ALTER 部分（列已存在会报错）

CREATE TABLE IF NOT EXISTS `t_vip_open_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `plan_name` varchar(64) NOT NULL COMMENT '套餐名称，如月卡/年卡',
  `duration_days` int NOT NULL COMMENT '开通/续费会员天数',
  `price` decimal(18, 2) NOT NULL COMMENT 'USDT 价格（链上实扣）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用 1启用',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` tinyint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员链上开通套餐';

-- 会员兑换 / 链上购买开关（后台 t_credit_score_avatar_display_config 维护，1=开启 0=关闭；NULL 视为开启，兼容旧数据）
ALTER TABLE `t_credit_score_avatar_display_config`
    ADD COLUMN `vip_redeem_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '会员兑换码开关：1开启 0关闭' AFTER `scan_login_check_im_code`,
    ADD COLUMN `vip_purchase_enabled` tinyint NOT NULL DEFAULT 1 COMMENT '会员链上购买开关：1开启 0关闭' AFTER `vip_redeem_enabled`;

-- 菜单：平台运营与配置 -> 系统配置 -> 会员开通套餐；已存在 menu_id 2259-2269 时请勿重复执行
INSERT INTO `sys_menu_ry` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `url`, `target`, `menu_type`, `visible`, `is_refresh`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(2259, '会员开通套餐', 2053, 60, '/vipOpenPlan', '', 'C', '0', '1', 'ops:system:vip-open-plan:view', '#', 'admin', NOW(), '', NULL, ''),
(2260, '查看', 2259, 30, '#', '', 'F', '0', '1', 'ops:system:vip-open-plan:view', '#', 'admin', NOW(), '', NULL, ''),
(2261, '新增', 2259, 60, '#', '', 'F', '0', '1', 'ops:system:vip-open-plan:add', '#', 'admin', NOW(), '', NULL, ''),
(2262, '编辑', 2259, 90, '#', '', 'F', '0', '1', 'ops:system:vip-open-plan:edit', '#', 'admin', NOW(), '', NULL, ''),
(2263, '删除', 2259, 120, '#', '', 'F', '0', '1', 'ops:system:vip-open-plan:delete', '#', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
    `menu_name` = VALUES(`menu_name`),
    `parent_id` = VALUES(`parent_id`),
    `order_num` = VALUES(`order_num`),
    `url` = VALUES(`url`),
    `perms` = VALUES(`perms`);
