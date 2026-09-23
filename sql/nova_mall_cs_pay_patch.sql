-- 人工客服会话与消息
USE `nova_mall`;
SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS `mall_cs_session` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_no` VARCHAR(64) NOT NULL COMMENT '会话编号',
  `member_id` INT NOT NULL COMMENT '会员用户ID',
  `member_name` VARCHAR(64) DEFAULT NULL COMMENT '会员昵称快照',
  `agent_id` INT DEFAULT NULL COMMENT '客服用户ID',
  `agent_name` VARCHAR(64) DEFAULT NULL COMMENT '客服昵称快照',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '0排队 1会话中 2已结束',
  `last_msg` VARCHAR(255) DEFAULT NULL COMMENT '最后一条消息摘要',
  `last_time` BIGINT DEFAULT NULL COMMENT '最后消息时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
  `create_user` INT DEFAULT NULL COMMENT '创建人',
  `update_user` INT DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cs_session_no` (`session_no`),
  KEY `idx_cs_member` (`member_id`),
  KEY `idx_cs_agent_status` (`agent_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工客服会话';

CREATE TABLE IF NOT EXISTS `mall_cs_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `session_id` BIGINT NOT NULL COMMENT '会话ID',
  `session_no` VARCHAR(64) NOT NULL COMMENT '会话编号',
  `from_user_id` INT NOT NULL COMMENT '发送人',
  `from_user_name` VARCHAR(64) DEFAULT NULL COMMENT '发送人昵称',
  `from_role` VARCHAR(16) NOT NULL COMMENT 'member/agent/system',
  `content` VARCHAR(1000) NOT NULL COMMENT '消息内容',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
  `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
  `create_user` INT DEFAULT NULL COMMENT '创建人',
  `update_user` INT DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_cs_msg_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人工客服消息';

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 70,0,'客服中心',1,'/cs','Layout',NULL,'CustomerServiceOutlined',7,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id=70);

INSERT INTO `sys_permission` (`id`,`parent_id`,`name`,`type`,`path`,`component`,`perms`,`icon`,`sort`,`visible`,`status`,`deleted`,`create_time`,`update_time`)
SELECT 71,70,'客服工作台',2,'/cs/desk','cs/desk/index','cs:desk','MessageOutlined',1,1,1,0,UNIX_TIMESTAMP()*1000,UNIX_TIMESTAMP()*1000
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_permission WHERE id=71);

INSERT INTO `sys_role_permission` (`role_id`,`permission_id`)
SELECT 1,70 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id=1 AND permission_id=70);
INSERT INTO `sys_role_permission` (`role_id`,`permission_id`)
SELECT 1,71 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_role_permission WHERE role_id=1 AND permission_id=71);
