-- jshERP 数据库初始化脚本
-- 创建数据库和用户，设置权限

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `jsh_erp` 
  DEFAULT CHARACTER SET utf8mb4 
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `jsh_erp`;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `jsh_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `login_name` varchar(255) DEFAULT NULL COMMENT '登录用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `position` varchar(200) DEFAULT NULL COMMENT '职位',
  `department` varchar(255) DEFAULT NULL COMMENT '所属部门',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(100) DEFAULT NULL COMMENT '手机号码',
  `is_manager` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否为管理者 0-员工 1-管理者',
  `is_system` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否系统自带数据',
  `status` tinyint(4) DEFAULT '0' COMMENT '状态，0:正常，1:删除',
  `description` varchar(500) DEFAULT NULL COMMENT '用户描述',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '修改人',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`,`delete_flag`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';