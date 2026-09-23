-- 商品详情 / 评论 / 购物车（已有库执行）
USE `nova_mall`;

-- 若列已存在会报错，可忽略
ALTER TABLE `mall_goods` ADD COLUMN `detail` MEDIUMTEXT NULL COMMENT '商品详情（HTML/富文本）' AFTER `sub_title`;

CREATE TABLE IF NOT EXISTS `mall_goods_comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `goods_id` INT NOT NULL COMMENT '商品ID',
  `user_id` INT NOT NULL COMMENT '评价用户ID',
  `user_name` VARCHAR(64) DEFAULT NULL COMMENT '评价用户昵称（冗余）',
  `star` TINYINT NOT NULL DEFAULT 5 COMMENT '星级：1-5',
  `content` VARCHAR(1000) NOT NULL COMMENT '评价内容',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1显示 0隐藏',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_goods_comment_goods` (`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

CREATE TABLE IF NOT EXISTS `mall_cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `goods_id` INT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  `checked` TINYINT NOT NULL DEFAULT 1 COMMENT '是否勾选结算：1是 0否',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  KEY `idx_cart_user` (`user_id`),
  KEY `idx_cart_user_goods` (`user_id`,`goods_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

UPDATE `mall_goods` SET `detail` = CONCAT(
  '<h3>商品介绍</h3><p>', IFNULL(`sub_title`, `name`), '</p>',
  '<p>正品保障 · 售后无忧 · 自营发货</p><p>支持七天无理由退换（部分品类除外）。</p>'
) WHERE `detail` IS NULL OR `detail` = '';

INSERT INTO `mall_goods_comment`
(`goods_id`,`user_id`,`user_name`,`star`,`content`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 1,2,'演示用户',5,'音质不错，续航也能打，自营很快。',1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `mall_goods_comment` LIMIT 1);

INSERT INTO `mall_goods_comment`
(`goods_id`,`user_id`,`user_name`,`star`,`content`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 4,2,'演示用户',4,'包装精美，坚果很新鲜，会回购。',1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE (SELECT COUNT(1) FROM `mall_goods_comment` WHERE goods_id=4)=0;

-- 后台：商品评价菜单
INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 26,20,'商品评价',2,'/goods/comment','goods/comment/index','goods:comment:list','StarOutlined',3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=26);

INSERT INTO `sys_role_permission` (`role_id`,`permission_id`)
SELECT 1, 26 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `sys_role_permission` WHERE role_id=1 AND permission_id=26);
