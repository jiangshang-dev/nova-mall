-- 收货地址 / 订单 / 订单项（已有库执行）
USE `nova_mall`;

CREATE TABLE IF NOT EXISTS `mall_address` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `receiver_name` VARCHAR(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人手机号',
  `province` VARCHAR(64) DEFAULT NULL COMMENT '省',
  `city` VARCHAR(64) DEFAULT NULL COMMENT '市',
  `district` VARCHAR(64) DEFAULT NULL COMMENT '区/县',
  `detail` VARCHAR(255) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认：1是 0否',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_address_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

CREATE TABLE IF NOT EXISTS `mall_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `user_id` INT NOT NULL COMMENT '下单用户ID',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待支付 1已支付 2已取消 3已发货 4已完成',
  `pay_type` VARCHAR(16) DEFAULT NULL COMMENT '支付方式：alipay/wxpay',
  `pay_time` BIGINT DEFAULT NULL COMMENT '支付时间（毫秒时间戳）',
  `pay_trade_no` VARCHAR(64) DEFAULT NULL COMMENT '第三方支付流水号',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '应付金额',
  `goods_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
  `freight_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '运费',
  `receiver_name` VARCHAR(64) NOT NULL COMMENT '收货人姓名（快照）',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人手机（快照）',
  `receiver_address` VARCHAR(512) NOT NULL COMMENT '收货地址全文（快照）',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '买家备注',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_order_user` (`user_id`),
  KEY `idx_order_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单表';

CREATE TABLE IF NOT EXISTS `mall_order_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
  `goods_id` INT NOT NULL COMMENT '商品ID',
  `goods_name` VARCHAR(128) NOT NULL COMMENT '商品名称（快照）',
  `goods_cover` VARCHAR(512) DEFAULT NULL COMMENT '商品封面（快照）',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '成交单价',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_order_item_order` (`order_id`),
  KEY `idx_order_item_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城订单明细表';

-- 演示地址（演示用户 user id=2）
INSERT INTO `mall_address`
(`user_id`,`receiver_name`,`receiver_phone`,`province`,`city`,`district`,`detail`,`is_default`,`deleted`,`create_time`,`update_time`)
SELECT 2,'演示用户','13800000000','北京市','北京市','朝阳区','望京街道诺瓦大厦 18 层',1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `mall_address` WHERE user_id=2 LIMIT 1);
