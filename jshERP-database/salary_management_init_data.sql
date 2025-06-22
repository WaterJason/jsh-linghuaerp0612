-- =====================================================
-- jshERP薪酬管理模块基础数据初始化脚本
-- 创建时间: 2025-06-22
-- 描述: 初始化薪酬项目配置等基础数据
-- =====================================================

-- 清空现有数据（仅用于开发环境）
-- DELETE FROM `jsh_salary_item` WHERE `tenant_id` = 0;

-- 插入薪酬项目配置基础数据
INSERT INTO `jsh_salary_item` (
  `item_code`, 
  `item_name`, 
  `item_type`, 
  `calculation_formula`, 
  `default_rate`, 
  `base_amount`, 
  `description`, 
  `status`, 
  `sort_order`, 
  `tenant_id`, 
  `delete_flag`, 
  `creator`
) VALUES 
-- 固定薪酬类
('DAILY_WAGE', '日薪标准', 'FIXED', '日薪 × 工作天数', 1.0000, 0.00, '员工的固定日薪收入，按实际工作天数计算', 'ACTIVE', 1, 0, '0', 1),

-- 销售提成类
('SALES_COMMISSION', '销售提成', 'COMMISSION', '销售额 × 提成比例', 0.0500, 0.00, '珐琅制品销售业绩提成，默认5%', 'ACTIVE', 2, 0, '0', 1),
('COFFEE_COMMISSION', '咖啡店提成', 'COMMISSION', '咖啡店销售额 × 提成比例', 0.0300, 0.00, '咖啡店销售业绩提成，默认3%', 'ACTIVE', 3, 0, '0', 1),

-- 生产提成类
('CLOISONNE_FEE', '掐丝点蓝制作费', 'COMMISSION', '完成件数 × 单件制作费', 0.0000, 50.00, '掐丝点蓝制作提成，按件计费，默认50元/件', 'ACTIVE', 4, 0, '0', 1),
('ACCESSORY_FEE', '配饰制作费', 'COMMISSION', '完成件数 × 单件制作费', 0.0000, 30.00, '配饰制作提成，按件计费，默认30元/件', 'ACTIVE', 5, 0, '0', 1),

-- 培训提成类
('TRAINING_COMMISSION', '团建项目提成', 'COMMISSION', '培训收入 × 提成比例', 0.1000, 0.00, '团建项目培训提成，默认10%', 'ACTIVE', 6, 0, '0', 1),
('HANDCRAFT_COMMISSION', '散客手作提成', 'COMMISSION', '手作收入 × 提成比例', 0.1500, 0.00, '散客手作指导提成，默认15%', 'ACTIVE', 7, 0, '0', 1),

-- 业务拓展类
('CHANNEL_DEVELOPMENT', '渠道开发提成', 'COMMISSION', '渠道收入 × 提成比例', 0.0800, 0.00, '渠道开发业务提成，默认8%', 'ACTIVE', 8, 0, '0', 1),
('ARTWORK_SALES', '作品销售费', 'COMMISSION', '作品销售额 × 提成比例', 0.0600, 0.00, '个人作品销售提成，默认6%', 'ACTIVE', 9, 0, '0', 1),

-- 外派津贴类
('OUTSIDE_INSTRUCTOR', '讲师费（外出）', 'ALLOWANCE', '外出天数 × 日津贴标准', 0.0000, 200.00, '外出讲师津贴，默认200元/天', 'ACTIVE', 10, 0, '0', 1),
('OUTSIDE_ASSISTANT', '助理费（外出）', 'ALLOWANCE', '外出天数 × 日津贴标准', 0.0000, 150.00, '外出助理津贴，默认150元/天', 'ACTIVE', 11, 0, '0', 1),

-- 内部津贴类
('INSIDE_INSTRUCTOR', '讲师费（在馆）', 'ALLOWANCE', '授课小时数 × 小时津贴标准', 0.0000, 80.00, '在馆讲师津贴，默认80元/小时', 'ACTIVE', 12, 0, '0', 1),
('INSIDE_ASSISTANT', '助理费（在馆）', 'ALLOWANCE', '协助小时数 × 小时津贴标准', 0.0000, 50.00, '在馆助理津贴，默认50元/小时', 'ACTIVE', 13, 0, '0', 1);

-- 插入薪酬计算序列号配置（用于生成单号）
-- 注意：这里假设jshERP有序列号管理表，如果没有则需要创建或使用其他方式
-- INSERT INTO `jsh_sequence` (`seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`) VALUES 
-- ('salary_calculation', 1, 999999999, 1, 1),
-- ('salary_payment', 1, 999999999, 1, 1);

-- 创建薪酬管理相关的功能菜单（需要根据jshERP的菜单管理方式调整）
-- 注意：这里的菜单ID需要根据实际的jsh_function表结构调整
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

-- 创建薪酬管理角色权限配置示例
-- 注意：这里需要根据实际的租户和角色情况调整
-- INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`) VALUES 
-- ('UserRole', 用户ID, '薪酬管理员角色ID', '1,2,3,4,7,8,9', 租户ID),
-- ('UserRole', 用户ID, '薪酬查询员角色ID', '4,7', 租户ID);

-- 创建薪酬项目与部门的默认关联配置
-- 这里可以根据不同部门配置不同的薪酬项目组合
-- 例如：珐琅制作部门默认包含日薪、销售提成、掐丝点蓝制作费等

-- 验证数据插入结果
SELECT 
  item_code,
  item_name,
  item_type,
  default_rate,
  base_amount,
  description,
  status
FROM `jsh_salary_item` 
WHERE `tenant_id` = 0 AND `delete_flag` = '0'
ORDER BY `sort_order`;

-- 显示薪酬项目统计信息
SELECT 
  item_type,
  COUNT(*) as item_count,
  GROUP_CONCAT(item_name ORDER BY sort_order) as items
FROM `jsh_salary_item` 
WHERE `tenant_id` = 0 AND `delete_flag` = '0' AND `status` = 'ACTIVE'
GROUP BY item_type
ORDER BY 
  CASE item_type 
    WHEN 'FIXED' THEN 1 
    WHEN 'COMMISSION' THEN 2 
    WHEN 'ALLOWANCE' THEN 3 
    ELSE 4 
  END;
