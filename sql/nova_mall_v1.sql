-- Nova Mall 一期建库脚本
CREATE DATABASE IF NOT EXISTS `nova_mall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `nova_mall`;

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_name` VARCHAR(64) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `user_real_name` VARCHAR(64) DEFAULT NULL,
  `phone` VARCHAR(20) DEFAULT NULL,
  `email` VARCHAR(128) DEFAULT NULL,
  `avatar` VARCHAR(512) DEFAULT NULL,
  `user_flag` TINYINT NOT NULL DEFAULT 2 COMMENT '1内部 2管理员 3普通用户',
  `status` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  `remark` VARCHAR(255) DEFAULT NULL,
  `create_time` BIGINT DEFAULT NULL,
  `update_time` BIGINT DEFAULT NULL,
  `create_user` INT DEFAULT NULL,
  `update_user` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_name` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_code` VARCHAR(64) NOT NULL,
  `role_name` VARCHAR(64) NOT NULL,
  `sort` INT DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  `remark` VARCHAR(255) DEFAULT NULL,
  `create_time` BIGINT DEFAULT NULL,
  `update_time` BIGINT DEFAULT NULL,
  `create_user` INT DEFAULT NULL,
  `update_user` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `parent_id` INT NOT NULL DEFAULT 0,
  `name` VARCHAR(64) NOT NULL,
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '1目录 2菜单 3按钮',
  `path` VARCHAR(128) DEFAULT NULL,
  `component` VARCHAR(128) DEFAULT NULL,
  `perms` VARCHAR(128) DEFAULT NULL,
  `icon` VARCHAR(64) DEFAULT NULL,
  `sort` INT DEFAULT 0,
  `visible` TINYINT NOT NULL DEFAULT 1,
  `status` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  `create_time` BIGINT DEFAULT NULL,
  `update_time` BIGINT DEFAULT NULL,
  `create_user` INT DEFAULT NULL,
  `update_user` INT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `role_id` INT NOT NULL,
  `permission_id` INT NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(64) DEFAULT NULL,
  `business_type` TINYINT DEFAULT 0,
  `method` VARCHAR(256) DEFAULT NULL,
  `request_method` VARCHAR(16) DEFAULT NULL,
  `oper_name` VARCHAR(64) DEFAULT NULL,
  `oper_url` VARCHAR(512) DEFAULT NULL,
  `oper_ip` VARCHAR(64) DEFAULT NULL,
  `oper_param` TEXT,
  `json_result` TEXT,
  `status` TINYINT DEFAULT 1,
  `error_msg` VARCHAR(2000) DEFAULT NULL,
  `cost_time` BIGINT DEFAULT 0,
  `oper_time` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `config_key` VARCHAR(128) NOT NULL,
  `config_value` VARCHAR(512) DEFAULT NULL,
  `remark` VARCHAR(255) DEFAULT NULL,
  `create_time` BIGINT DEFAULT NULL,
  `update_time` BIGINT DEFAULT NULL,
  `create_user` INT DEFAULT NULL,
  `update_user` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `mall_goods_category`;
CREATE TABLE `mall_goods_category` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `parent_id` INT NOT NULL DEFAULT 0,
  `name` VARCHAR(64) NOT NULL,
  `sort` INT DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  `create_time` BIGINT DEFAULT NULL,
  `update_time` BIGINT DEFAULT NULL,
  `create_user` INT DEFAULT NULL,
  `update_user` INT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `mall_goods`;
CREATE TABLE `mall_goods` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `category_id` INT DEFAULT NULL,
  `name` VARCHAR(128) NOT NULL,
  `sub_title` VARCHAR(255) DEFAULT NULL,
  `cover` VARCHAR(512) DEFAULT NULL,
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  `stock` INT NOT NULL DEFAULT 0,
  `sales` INT NOT NULL DEFAULT 0,
  `status` TINYINT NOT NULL DEFAULT 1,
  `deleted` TINYINT NOT NULL DEFAULT 0,
  `create_time` BIGINT DEFAULT NULL,
  `update_time` BIGINT DEFAULT NULL,
  `create_user` INT DEFAULT NULL,
  `update_user` INT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `sys_user` (`id`,`user_name`,`password`,`user_real_name`,`user_flag`,`status`,`deleted`,`create_time`,`update_time`,`create_user`,`update_user`)
VALUES (1,'admin','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','超级管理员',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000,1,1),
(2,'user','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi','演示用户',3,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000,1,1);

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
(25,20,'商品分类',2,'/goods/category','goods/category/index','goods:category:list','ClusterOutlined',2,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `sys_role_permission` (`role_id`,`permission_id`) SELECT 1, id FROM `sys_permission`;

INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`) VALUES
(1,0,'数码电器',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(2,0,'服装鞋帽',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(3,0,'食品饮料',3,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `mall_goods` (`id`,`category_id`,`name`,`sub_title`,`cover`,`price`,`stock`,`sales`,`status`,`deleted`,`create_time`,`update_time`) VALUES
(1,1,'Nova 无线耳机','降噪长续航','https://via.placeholder.com/300',299.00,100,12,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(2,1,'Nova 智能手表','健康监测','https://via.placeholder.com/300',899.00,50,8,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(3,2,'纯棉T恤','舒适透气','https://via.placeholder.com/300',99.00,200,30,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000),
(4,3,'有机坚果礼盒','每日坚果','https://via.placeholder.com/300',128.00,80,15,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);

INSERT INTO `sys_config` (`config_key`,`config_value`,`remark`,`create_time`,`update_time`)
VALUES ('site.name','Nova商城','站点名称',UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000);
