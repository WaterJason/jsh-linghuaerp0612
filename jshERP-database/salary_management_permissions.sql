-- =====================================================
-- jshERP薪酬管理模块权限配置脚本
-- 创建时间: 2025-06-22
-- 描述: 配置薪酬管理模块的菜单权限和按钮权限
-- =====================================================

-- 检查是否已存在薪酬管理菜单，如果存在则先删除
DELETE FROM `jsh_function` WHERE `number` LIKE '11%' AND `name` LIKE '%薪酬%';

-- 插入薪酬管理功能菜单
INSERT INTO `jsh_function` (
  `id`,
  `number`, 
  `name`, 
  `p_number`, 
  `url`, 
  `component`, 
  `state`, 
  `sort`, 
  `enabled`, 
  `type`, 
  `push_btn`, 
  `op_btn_str`, 
  `icon`
) VALUES 
-- 一级菜单：薪酬管理
(1100, '11', '薪酬管理', '0', '/salary', 'RouteView', 0, 11, 1, 0, '', '', 'money-collect'),

-- 二级菜单
(1101, '1101', '薪酬档案', '11', '/salary/profile', 'salary/SalaryProfileList', 0, 1, 1, 1, '1,2,3,4,7', '新增,修改,删除,查看,导出', ''),
(1102, '1102', '薪酬计算', '11', '/salary/calculation', 'salary/SalaryCalculation', 0, 2, 1, 1, '1,2,3,4,7,8', '新增,修改,删除,查看,导出,计算', ''),
(1103, '1103', '薪酬发放', '11', '/salary/payment', 'salary/SalaryPayment', 0, 3, 1, 1, '1,2,3,4,7,9', '新增,修改,删除,查看,导出,发放', ''),
(1104, '1104', '薪资查询', '11', '/salary/inquiry', 'salary/SalaryInquiry', 0, 4, 1, 1, '4,7', '查看,导出', ''),
(1105, '1105', '薪酬配置', '11', '/salary/config', 'salary/SalaryConfig', 0, 5, 1, 1, '1,2,3,4', '新增,修改,删除,查看', '');

-- 验证菜单插入结果
SELECT 
  id,
  number,
  name,
  p_number,
  url,
  enabled,
  type,
  push_btn,
  op_btn_str
FROM `jsh_function` 
WHERE `number` LIKE '11%'
ORDER BY `number`;

-- 创建薪酬管理角色权限配置示例
-- 注意：以下配置需要根据实际的租户ID和用户ID进行调整

-- 薪酬管理员角色：拥有所有薪酬管理权限
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('UserRole', 用户ID, '薪酬管理员角色ID', '1,2,3,4,7,8,9', 租户ID);

-- 薪酬专员角色：拥有薪酬档案和计算权限，无发放权限
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('UserRole', 用户ID, '薪酬专员角色ID', '1,2,3,4,7,8', 租户ID);

-- 薪酬查询员角色：只有查看和导出权限
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('UserRole', 用户ID, '薪酬查询员角色ID', '4,7', 租户ID);

-- 财务人员角色：拥有薪酬发放权限
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('UserRole', 用户ID, '财务人员角色ID', '4,7,9', 租户ID);

-- 为admin用户分配薪酬管理权限（示例）
-- 注意：需要根据实际的用户ID和租户ID进行调整
INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
('UserRole', 1, '1101', '1,2,3,4,7', 0),
('UserRole', 1, '1102', '1,2,3,4,7,8', 0),
('UserRole', 1, '1103', '1,2,3,4,7,9', 0),
('UserRole', 1, '1104', '4,7', 0),
('UserRole', 1, '1105', '1,2,3,4', 0);

-- 创建薪酬管理相关的数据权限配置
-- 数据权限类型说明：
-- 1. 个人薪资权限：员工只能查看自己的薪资信息
-- 2. 部门薪资权限：部门经理可以查看本部门员工薪资
-- 3. 全部薪资权限：HR和财务可以查看所有员工薪资

-- 个人薪资权限（员工只能查看自己的薪资）
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('SalaryDataPermission', 员工用户ID, 'PERSONAL', '', 租户ID);

-- 部门薪资权限（部门经理可以查看本部门薪资）
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('SalaryDataPermission', 部门经理用户ID, 'DEPARTMENT', '部门名称', 租户ID);

-- 全部薪资权限（HR和财务可以查看所有薪资）
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('SalaryDataPermission', HR用户ID, 'ALL', '', 租户ID);

-- 创建薪酬审批权限配置
-- 薪酬计算审批权限
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('SalaryApprovalPermission', HR主管用户ID, 'CALCULATION_APPROVAL', '', 租户ID);

-- 薪酬发放审批权限
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('SalaryApprovalPermission', 财务主管用户ID, 'PAYMENT_APPROVAL', '', 租户ID);

-- 验证权限配置结果
SELECT 
  ub.type,
  ub.key_id,
  u.username,
  ub.value,
  ub.btn_str,
  ub.tenant_id
FROM `jsh_user_business` ub
LEFT JOIN `jsh_user` u ON ub.key_id = u.id
WHERE ub.value LIKE '11%' OR ub.type LIKE '%Salary%'
ORDER BY ub.type, ub.key_id;

-- 显示薪酬管理菜单权限统计
SELECT 
  f.number,
  f.name,
  f.push_btn,
  f.op_btn_str,
  COUNT(ub.id) as assigned_users
FROM `jsh_function` f
LEFT JOIN `jsh_user_business` ub ON f.number = ub.value
WHERE f.number LIKE '11%'
GROUP BY f.id, f.number, f.name, f.push_btn, f.op_btn_str
ORDER BY f.number;
