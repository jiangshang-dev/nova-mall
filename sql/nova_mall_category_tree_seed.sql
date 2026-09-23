-- 商品多级分类示例数据（已有库可执行）
USE `nova_mall`;

-- 数码电器下级
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 11,1,'手机通讯',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=11);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 12,1,'电脑办公',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=12);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 13,1,'智能穿戴',3,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=13);

INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 111,11,'手机',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=111);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 112,11,'耳机音响',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=112);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 121,12,'笔记本',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=121);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 131,13,'智能手表',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=131);

-- 服装鞋帽下级
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 21,2,'男装',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=21);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 22,2,'女装',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=22);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 211,21,'T恤',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=211);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 212,21,'外套',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=212);

-- 食品饮料下级
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 31,3,'休闲零食',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=31);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 32,3,'饮料冲调',2,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=32);
INSERT INTO `mall_goods_category` (`id`,`parent_id`,`name`,`sort`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 311,31,'坚果炒货',1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM mall_goods_category WHERE id=311);

-- 样例商品挂到更细分类
UPDATE `mall_goods` SET `category_id`=112 WHERE `id`=1 AND EXISTS (SELECT 1 FROM mall_goods_category WHERE id=112);
UPDATE `mall_goods` SET `category_id`=131 WHERE `id`=2 AND EXISTS (SELECT 1 FROM mall_goods_category WHERE id=131);
UPDATE `mall_goods` SET `category_id`=211 WHERE `id`=3 AND EXISTS (SELECT 1 FROM mall_goods_category WHERE id=211);
UPDATE `mall_goods` SET `category_id`=311 WHERE `id`=4 AND EXISTS (SELECT 1 FROM mall_goods_category WHERE id=311);
