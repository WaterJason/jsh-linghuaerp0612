-- jshERP 初始数据脚本
-- 插入默认租户、管理员用户等基础数据

USE `jsh_erp`;

-- 插入默认租户
INSERT IGNORE INTO `jsh_tenant` (`id`, `tenant_name`, `login_name`, `user_num_limit`, `expire_time`, `enabled`, `remark`, `create_time`, `update_time`, `delete_flag`) VALUES
(63, 'waterxi', 'waterxi', 1000000, '2099-12-31 23:59:59', 1, '默认租户', NOW(), NOW(), '0');

-- 插入默认管理员用户 (密码: 123456)
INSERT IGNORE INTO `jsh_user` (`id`, `username`, `login_name`, `password`, `position`, `department`, `email`, `phone_number`, `is_manager`, `is_system`, `status`, `description`, `remark`, `tenant_id`, `create_time`, `update_time`, `delete_flag`) VALUES
(63, 'admin', 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', '信息部', 'admin@jsherp.com', '13800138000', 1, 1, 0, '系统管理员', '默认管理员账户', 63, NOW(), NOW(), '0');

-- 插入默认普通用户 (密码: 123456)
INSERT IGNORE INTO `jsh_user` (`id`, `username`, `login_name`, `password`, `position`, `department`, `email`, `phone_number`, `is_manager`, `is_system`, `status`, `description`, `remark`, `tenant_id`, `create_time`, `update_time`, `delete_flag`) VALUES
(64, 'jsh', 'jsh', 'e10adc3949ba59abbe56e057f20f883e', '业务员', '销售部', 'jsh@jsherp.com', '13800138001', 0, 0, 0, '业务员', '默认业务员账户', 63, NOW(), NOW(), '0');

-- 提交事务
COMMIT;