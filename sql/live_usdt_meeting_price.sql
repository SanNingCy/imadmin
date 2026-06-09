-- USDT 定价复用固定价格表 t_live_fixed_price_config（时长/人数仍用原配置表）
-- 通过 pricing_mode 与 ODIC「会议室价格配置」隔离，不影响既有数据与接口

-- 已存在 pricing_mode 列时请勿重复执行本句
ALTER TABLE `t_live_fixed_price_config`
    ADD COLUMN `pricing_mode` varchar(16) NOT NULL DEFAULT 'ODIC'
        COMMENT '定价模式：ODIC=代币固定价 USDT=USDT统一定价' AFTER `fixed_price`;

UPDATE `t_live_fixed_price_config` SET `pricing_mode` = 'ODIC' WHERE `pricing_mode` IS NULL OR `pricing_mode` = '';

-- 可选索引（按需执行）
-- CREATE INDEX `idx_live_fixed_price_mode` ON `t_live_fixed_price_config` (`pricing_mode`);

-- 旧独立表已废弃（USDT 定价改复用 t_live_fixed_price_config），若曾创建可手动删除：
-- DROP TABLE IF EXISTS `t_live_usdt_meeting_price_config`;
