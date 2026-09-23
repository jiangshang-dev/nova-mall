-- 客服会话增加会员 IP
USE `nova_mall`;
SET NAMES utf8mb4;
ALTER TABLE `mall_cs_session`
  ADD COLUMN `client_ip` VARCHAR(64) DEFAULT NULL COMMENT '会员客户端IP' AFTER `agent_name`;
