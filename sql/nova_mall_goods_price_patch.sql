-- 商品增加原价字段（已有库执行）
USE `nova_mall`;

ALTER TABLE `mall_goods`
  ADD COLUMN `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价（划线价）' AFTER `price`;

UPDATE `mall_goods` SET `original_price` = ROUND(`price` * 1.3, 2) WHERE `original_price` IS NULL;
