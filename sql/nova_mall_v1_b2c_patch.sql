-- 单商家 B2C 后台菜单增量（已有库可执行本脚本）
USE `nova_mall`;

-- 演示用户补充邮箱/手机，便于前台多账号登录
UPDATE `sys_user` SET `email`='user@nova.com', `phone`='13800000000' WHERE `id`=2;
UPDATE `sys_user` SET `email`='admin@nova.com' WHERE `id`=1;

-- 订单管理
INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 30,0,'订单管理',1,'/order','Layout',NULL,'ShoppingCartOutlined',3,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=30);

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 31,30,'订单列表',2,'/order/list','order/list/index','order:list','AppstoreOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=31);

-- 会员管理
INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 40,0,'会员管理',1,'/member','Layout',NULL,'TeamOutlined',4,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=40);

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 41,40,'会员列表',2,'/member/list','member/list/index','member:list','UserOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=41);

-- 营销中心
INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 50,0,'营销中心',1,'/marketing','Layout',NULL,'GiftOutlined',5,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=50);

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 51,50,'优惠券',2,'/marketing/coupon','marketing/coupon/index','marketing:coupon','GiftOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=51);

-- 店铺设置
INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 60,0,'店铺设置',1,'/shop','Layout',NULL,'ShopOutlined',6,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=60);

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 61,60,'基本信息',2,'/shop/setting','shop/setting/index','shop:setting','SettingOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM `sys_permission` WHERE id=61);

-- 授权给超级管理员
INSERT INTO `sys_role_permission` (`role_id`,`permission_id`)
SELECT 1, p.id FROM `sys_permission` p
WHERE p.id IN (30,31,40,41,50,51,60,61)
  AND NOT EXISTS (
    SELECT 1 FROM `sys_role_permission` rp WHERE rp.role_id=1 AND rp.permission_id=p.id
  );
