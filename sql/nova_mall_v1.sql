-- Nova Mall 一期建库脚本（表与字段均含中文注释）
CREATE DATABASE IF NOT EXISTS `nova_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `nova_mall`;

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `user_name` VARCHAR(64) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(128) NOT NULL COMMENT '密码（BCrypt）',
  `user_real_name` VARCHAR(64) DEFAULT NULL COMMENT '真实姓名/昵称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
  `user_flag` TINYINT NOT NULL DEFAULT 2 COMMENT '用户标识：1内部 2管理员 3普通用户',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表（后台管理员与C端会员共用）';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` VARCHAR(64) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(64) NOT NULL COMMENT '角色名称',
  `sort` INT DEFAULT 0 COMMENT '排序（越小越靠前）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '权限/菜单ID',
  `parent_id` INT NOT NULL DEFAULT 0 COMMENT '父级ID，0为顶级',
  `name` VARCHAR(64) NOT NULL COMMENT '菜单/权限名称',
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1目录 2菜单 3按钮',
  `path` VARCHAR(128) DEFAULT NULL COMMENT '路由路径',
  `component` VARCHAR(128) DEFAULT NULL COMMENT '前端组件路径',
  `perms` VARCHAR(128) DEFAULT NULL COMMENT '权限标识，如 system:user:list',
  `icon` VARCHAR(64) DEFAULT NULL COMMENT '菜单图标',
  `sort` INT DEFAULT 0 COMMENT '排序（越小越靠前）',
  `visible` TINYINT NOT NULL DEFAULT 1 COMMENT '是否可见：1是 0否',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统权限/菜单表';

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `role_id` INT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` INT NOT NULL COMMENT '角色ID',
  `permission_id` INT NOT NULL COMMENT '权限/菜单ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `title` VARCHAR(64) DEFAULT NULL COMMENT '模块标题',
  `business_type` TINYINT DEFAULT 0 COMMENT '业务类型：0其它 1新增 2修改 3删除等',
  `method` VARCHAR(256) DEFAULT NULL COMMENT '方法名称',
  `request_method` VARCHAR(16) DEFAULT NULL COMMENT '请求方式（GET/POST等）',
  `oper_name` VARCHAR(64) DEFAULT NULL COMMENT '操作人员',
  `oper_url` VARCHAR(512) DEFAULT NULL COMMENT '请求URL',
  `oper_ip` VARCHAR(64) DEFAULT NULL COMMENT '操作IP',
  `oper_param` TEXT COMMENT '请求参数',
  `json_result` TEXT COMMENT '返回结果JSON',
  `status` TINYINT DEFAULT 1 COMMENT '操作状态：1成功 0失败',
  `error_msg` VARCHAR(2000) DEFAULT NULL COMMENT '错误消息',
  `cost_time` BIGINT DEFAULT 0 COMMENT '耗时（毫秒）',
  `oper_time` BIGINT DEFAULT NULL COMMENT '操作时间（毫秒时间戳）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统操作日志表';

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` VARCHAR(128) NOT NULL COMMENT '配置键',
  `config_value` VARCHAR(512) DEFAULT NULL COMMENT '配置值',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注说明',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数配置表';

DROP TABLE IF EXISTS `mall_goods_category`;
CREATE TABLE `mall_goods_category` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` INT NOT NULL DEFAULT 0 COMMENT '父分类ID，0为顶级',
  `name` VARCHAR(64) NOT NULL COMMENT '分类名称',
  `sort` INT DEFAULT 0 COMMENT '排序（越小越靠前）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

DROP TABLE IF EXISTS `mall_goods`;
CREATE TABLE `mall_goods` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `category_id` INT DEFAULT NULL COMMENT '所属分类ID',
  `name` VARCHAR(128) NOT NULL COMMENT '商品名称',
  `sub_title` VARCHAR(255) DEFAULT NULL COMMENT '副标题/卖点',
  `detail` MEDIUMTEXT NULL COMMENT '商品详情（HTML/富文本）',
  `cover` VARCHAR(512) DEFAULT NULL COMMENT '封面图URL',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '优惠价/售价',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价（划线价）',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '上下架：1上架 0下架',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0否 1是',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间（毫秒时间戳）',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间（毫秒时间戳）',
  `create_user` INT DEFAULT NULL COMMENT '创建人用户ID',
  `update_user` INT DEFAULT NULL COMMENT '更新人用户ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

DROP TABLE IF EXISTS `mall_goods_comment`;
CREATE TABLE `mall_goods_comment` (
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

DROP TABLE IF EXISTS `mall_cart`;
CREATE TABLE `mall_cart` (
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

INSERT INTO `sys_user` (`id`,`user_name`,`password`,`user_real_name`,`phone`,`email`,`user_flag`,`status`,`deleted`,`create_time`,`update_time`,`create_user`,`update_user`)
VALUES (1,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','超级管理员',NULL,'admin@nova.com',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000,1,1),
(2,'user','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','演示用户','13800000000','user@nova.com',3,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000,1,1);

INSERT INTO `sys_role` (`id`,`role_code`,`role_name`,`sort`,`status`,`deleted`,`create_time`,`update_time`,`create_user`,`update_user`)
VALUES (1,'SUPER_ADMIN','超级管理员',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000,1,1),
(2,'MEMBER','商城会员',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000,1,1);

INSERT INTO `sys_user_role` (`user_id`,`role_id`) VALUES (1,1),(2,2);

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`) VALUES
(1,0,'系统管理',1,'/system','Layout',NULL,'SettingOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(2,1,'用户管理',2,'/system/user','system/user/index','system:user:list','UserOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(3,2,'用户新增',3,NULL,NULL,'system:user:add',NULL,1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(4,2,'用户修改',3,NULL,NULL,'system:user:edit',NULL,2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(5,2,'用户删除',3,NULL,NULL,'system:user:remove',NULL,3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(6,1,'角色管理',2,'/system/role','system/role/index','system:role:list','TeamOutlined',2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(7,6,'角色新增',3,NULL,NULL,'system:role:add',NULL,1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(8,6,'角色修改',3,NULL,NULL,'system:role:edit',NULL,2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(9,6,'角色删除',3,NULL,NULL,'system:role:remove',NULL,3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(10,1,'菜单管理',2,'/system/menu','system/menu/index','system:menu:list','MenuOutlined',3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(11,10,'菜单新增',3,NULL,NULL,'system:menu:add',NULL,1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(12,10,'菜单修改',3,NULL,NULL,'system:menu:edit',NULL,2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(13,10,'菜单删除',3,NULL,NULL,'system:menu:remove',NULL,3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(14,1,'操作日志',2,'/system/operlog','system/operlog/index','system:operlog:list','FileSearchOutlined',4,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(20,0,'商品管理',1,'/goods','Layout',NULL,'ShoppingOutlined',2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(21,20,'商品列表',2,'/goods/list','goods/list/index','goods:list','AppstoreOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(22,21,'商品新增',3,NULL,NULL,'goods:add',NULL,1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(23,21,'商品修改',3,NULL,NULL,'goods:edit',NULL,2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(24,21,'商品删除',3,NULL,NULL,'goods:remove',NULL,3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(25,20,'商品分类',2,'/goods/category','goods/category/index','goods:category:list','ClusterOutlined',2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(26,20,'商品评价',2,'/goods/comment','goods/comment/index','goods:comment:list','StarOutlined',3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(30,0,'订单管理',1,'/order','Layout',NULL,'ShoppingCartOutlined',3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(31,30,'订单列表',2,'/order/list','order/list/index','order:list','AppstoreOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(40,0,'会员管理',1,'/member','Layout',NULL,'TeamOutlined',4,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(41,40,'会员列表',2,'/member/list','member/list/index','member:list','UserOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(50,0,'营销中心',1,'/marketing','Layout',NULL,'GiftOutlined',5,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(51,50,'优惠券',2,'/marketing/coupon','marketing/coupon/index','marketing:coupon','GiftOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(60,0,'店铺设置',1,'/shop','Layout',NULL,'ShopOutlined',6,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(61,60,'基本信息',2,'/shop/setting','shop/setting/index','shop:setting','SettingOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `sys_role_permission` (`role_id`,`permission_id`) SELECT 1, id FROM `sys_permission`;

INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`) VALUES
(1,0,'数码电器',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(2,0,'服装鞋帽',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(3,0,'食品饮料',3,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `mall_goods` (`id`,`category_id`,`name`,`sub_title`,`detail`,`cover`,`price`,`original_price`,`stock`,`sales`,`status`,`deleted`,`create_time`,`update_time`) VALUES
(1,1,'Nova 无线耳机','降噪长续航','<h3>商品介绍</h3><p>降噪长续航</p><p>正品保障 · 售后无忧 · 自营发货</p>','https://via.placeholder.com/300',299.00,399.00,100,12,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(2,1,'Nova 智能手表','健康监测','<h3>商品介绍</h3><p>健康监测</p><p>正品保障 · 售后无忧 · 自营发货</p>','https://via.placeholder.com/300',899.00,1099.00,50,8,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(3,2,'纯棉T恤','舒适透气','<h3>商品介绍</h3><p>舒适透气</p><p>正品保障 · 售后无忧 · 自营发货</p>','https://via.placeholder.com/300',99.00,159.00,200,30,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(4,3,'有机坚果礼盒','每日坚果','<h3>商品介绍</h3><p>每日坚果</p><p>正品保障 · 售后无忧 · 自营发货</p>','https://via.placeholder.com/300',128.00,168.00,80,15,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `mall_goods_comment` (`goods_id`,`user_id`,`user_name`,`star`,`content`,`status`,`deleted`,`create_time`,`update_time`) VALUES
(1,2,'演示用户',5,'音质不错，续航也能打，自营很快。',1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(4,2,'演示用户',4,'包装精美，坚果很新鲜，会回购。',1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `sys_config` (`config_key`,`config_value`,`remark`,`create_time`,`update_time`)
VALUES ('site.name','Nova商城','站点名称',UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
