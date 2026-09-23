-- 运费/配送、订单扩展、店铺配置（已有库执行）
-- 请使用: mysql ... --default-character-set=utf8mb4 < this_file.sql
SET NAMES utf8mb4;
USE `nova_mall`;

-- 若列已存在会报错，可忽略
ALTER TABLE `mall_order` ADD COLUMN `delivery_type` VARCHAR(32) DEFAULT NULL COMMENT '配送方式：hour/next_day/three_day' AFTER `pay_type`;
ALTER TABLE `mall_order` ADD COLUMN `delivery_name` VARCHAR(64) DEFAULT NULL COMMENT '配送方式名称快照' AFTER `delivery_type`;

CREATE TABLE IF NOT EXISTS `mall_freight_setting` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `delivery_type` VARCHAR(32) NOT NULL COMMENT '配送编码：hour/next_day/three_day',
  `delivery_name` VARCHAR(64) NOT NULL COMMENT '配送名称',
  `freight` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费（元）',
  `free_threshold` DECIMAL(10,2) DEFAULT NULL COMMENT '满额免运费阈值，空=不包邮',
  `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：1是 0否',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
  `create_user` INT DEFAULT NULL COMMENT '创建人',
  `update_user` INT DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_freight_type` (`delivery_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送运费设置表';

CREATE TABLE IF NOT EXISTS `mall_shop_config` (
  `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(512) DEFAULT NULL COMMENT '配置值',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺运营配置';

INSERT INTO `mall_shop_config` (`config_key`,`config_value`,`remark`,`update_time`)
SELECT 'service_city','北京市','仅配送该城市（不送外地）',UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_shop_config WHERE config_key='service_city');

INSERT INTO `mall_freight_setting`
(`delivery_type`,`delivery_name`,`freight`,`free_threshold`,`enabled`,`sort`,`deleted`,`create_time`,`update_time`)
SELECT 'hour','同城一小时达',12.00,99.00,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_freight_setting WHERE delivery_type='hour');

INSERT INTO `mall_freight_setting`
(`delivery_type`,`delivery_name`,`freight`,`free_threshold`,`enabled`,`sort`,`deleted`,`create_time`,`update_time`)
SELECT 'next_day','隔天达',8.00,79.00,1,2,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_freight_setting WHERE delivery_type='next_day');

INSERT INTO `mall_freight_setting`
(`delivery_type`,`delivery_name`,`freight`,`free_threshold`,`enabled`,`sort`,`deleted`,`create_time`,`update_time`)
SELECT 'three_day','三天内送达',0.00,NULL,1,3,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_freight_setting WHERE delivery_type='three_day');
