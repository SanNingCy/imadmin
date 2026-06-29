-- 信用分双价格：price=ODIC（旧版展示/余额扣款），price_usdt=USDT（链上支付/新版展示）
-- 已存在 price_usdt 列时请勿重复执行
ALTER TABLE `t_credit_score_config`
    ADD COLUMN `price_usdt` decimal(18, 2) NULL COMMENT '链上开通信用分USDT价' AFTER `price`;

-- 示例（按实际调整）：price 改回 ODIC，price_usdt 填链上 USDT 价
-- UPDATE t_credit_score_config SET price = 1000000.00, price_usdt = 5.00 WHERE id = 1;
