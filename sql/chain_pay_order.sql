-- 链上支付业务订单（IM）
CREATE TABLE IF NOT EXISTS `t_chain_pay_order` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `order_id` varchar(64) NOT NULL COMMENT '链上 orderId，与合约 pay 参数一致',
  `user_id` varchar(64) NOT NULL COMMENT 'IM 用户 ID',
  `scene` varchar(32) NOT NULL COMMENT 'credit_activate / meeting_create / meeting_extend / vip_open',
  `chain_id` int NOT NULL DEFAULT 56 COMMENT '链 ID',
  `amount` decimal(18,2) NOT NULL COMMENT '人类可读支付金额(USDT)',
  `odic_amount` decimal(18,2) DEFAULT NULL COMMENT 'USDT amount 按汇率换算后的 ODIC 金额',
  `raw_amount` varchar(80) DEFAULT NULL COMMENT '18 位小数 rawAmount',
  `token_symbol` varchar(16) DEFAULT 'ODIC' COMMENT '展示代币符号',
  `token_address` varchar(128) DEFAULT NULL COMMENT '代币合约地址',
  `payment_type` int NOT NULL COMMENT '合约 paymentType',
  `biz_payload` text COMMENT '业务参数 JSON',
  `tx_hash` varchar(128) DEFAULT NULL COMMENT '链上交易 hash',
  `user_address` varchar(128) DEFAULT NULL COMMENT '付款钱包地址',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2业务完成 3失败 4过期',
  `reconcile_status` tinyint NOT NULL DEFAULT 0 COMMENT '0未对账 1已对账',
  `expire_time` datetime DEFAULT NULL COMMENT '订单过期时间',
  `pay_time` datetime DEFAULT NULL COMMENT '链上支付确认时间',
  `complete_time` datetime DEFAULT NULL COMMENT '业务完成时间',
  `remark` varchar(512) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_id` (`order_id`),
  KEY `idx_user_scene_status` (`user_id`,`scene`,`status`),
  KEY `idx_tx_hash` (`tx_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='链上支付订单';

-- 菜单（财务与资产中心 -> 资金管理）；已存在 menu_id 2256/2257 时请勿重复执行
INSERT INTO `sys_menu_ry` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `url`, `target`, `menu_type`, `visible`, `is_refresh`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(2256, '链上支付订单', 2029, 75, '/chainPayOrder', '', 'C', '0', '1', 'asset:fund:chain-pay:view', '#', 'admin', NOW(), '', NULL, ''),
(2257, '查看', 2256, 10, '#', '', 'F', '0', '1', 'asset:fund:chain-pay:view', '#', 'admin', NOW(), '', NULL, '')
ON DUPLICATE KEY UPDATE
    `menu_name` = VALUES(`menu_name`),
    `parent_id` = VALUES(`parent_id`),
    `order_num` = VALUES(`order_num`),
    `url` = VALUES(`url`),
    `perms` = VALUES(`perms`);
