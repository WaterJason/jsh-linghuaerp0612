-- =====================================================
-- jshERP薪酬管理模块数据表创建脚本
-- 创建时间: 2025-06-22
-- 描述: 薪酬管理模块的6张核心数据表
-- =====================================================

-- 1. 员工薪酬档案表
DROP TABLE IF EXISTS `jsh_salary_profile`;
CREATE TABLE `jsh_salary_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `employee_name` varchar(100) NOT NULL COMMENT '员工姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `position` varchar(100) DEFAULT NULL COMMENT '职位',
  `entry_date` date DEFAULT NULL COMMENT '入职时间',
  `daily_wage` decimal(10,2) DEFAULT 0.00 COMMENT '日薪标准',
  `salary_status` varchar(20) DEFAULT 'ACTIVE' COMMENT '薪酬状态：ACTIVE-生效，INACTIVE-失效',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 多租户字段（必需）
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段（必需）
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_tenant` (`employee_id`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_status` (`tenant_id`, `salary_status`, `delete_flag`),
  KEY `idx_department` (`department`, `tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪酬档案表';

-- 2. 薪酬项目配置表
DROP TABLE IF EXISTS `jsh_salary_item`;
CREATE TABLE `jsh_salary_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_code` varchar(50) NOT NULL COMMENT '薪酬项目编码',
  `item_name` varchar(100) NOT NULL COMMENT '薪酬项目名称',
  `item_type` varchar(20) NOT NULL COMMENT '项目类型：FIXED-固定薪酬，COMMISSION-提成，ALLOWANCE-津贴',
  `calculation_formula` varchar(500) DEFAULT NULL COMMENT '计算公式',
  `default_rate` decimal(10,4) DEFAULT 0.0000 COMMENT '默认比例',
  `base_amount` decimal(10,2) DEFAULT 0.00 COMMENT '基础金额',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-禁用',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  
  -- 多租户字段（必需）
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段（必需）
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code_tenant` (`item_code`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_type` (`tenant_id`, `item_type`, `delete_flag`),
  KEY `idx_status_sort` (`status`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬项目配置表';

-- 3. 员工薪酬项目关联表
DROP TABLE IF EXISTS `jsh_employee_salary_item`;
CREATE TABLE `jsh_employee_salary_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `salary_item_id` bigint(20) NOT NULL COMMENT '薪酬项目ID，关联jsh_salary_item表',
  `custom_rate` decimal(10,4) DEFAULT NULL COMMENT '个人定制比例',
  `custom_amount` decimal(10,2) DEFAULT NULL COMMENT '个人定制金额',
  `effective_date` date NOT NULL COMMENT '生效日期',
  `expiry_date` date DEFAULT NULL COMMENT '失效日期',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-生效，INACTIVE-失效',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 多租户字段（必需）
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段（必需）
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_item_tenant` (`employee_id`, `salary_item_id`, `tenant_id`, `delete_flag`),
  KEY `idx_employee_tenant` (`employee_id`, `tenant_id`, `delete_flag`),
  KEY `idx_item_tenant` (`salary_item_id`, `tenant_id`, `delete_flag`),
  KEY `idx_effective_date` (`effective_date`, `expiry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪酬项目关联表';

-- 4. 薪酬计算记录表
DROP TABLE IF EXISTS `jsh_salary_calculation`;
CREATE TABLE `jsh_salary_calculation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `calculation_number` varchar(50) NOT NULL COMMENT '计算单号',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `employee_name` varchar(100) NOT NULL COMMENT '员工姓名',
  `calculation_month` varchar(7) NOT NULL COMMENT '计算月份，格式：YYYY-MM',
  `total_amount` decimal(12,2) DEFAULT 0.00 COMMENT '薪酬总额',
  `fixed_amount` decimal(12,2) DEFAULT 0.00 COMMENT '固定薪酬金额',
  `commission_amount` decimal(12,2) DEFAULT 0.00 COMMENT '提成金额',
  `allowance_amount` decimal(12,2) DEFAULT 0.00 COMMENT '津贴金额',
  `deduction_amount` decimal(12,2) DEFAULT 0.00 COMMENT '扣除金额',
  `actual_amount` decimal(12,2) DEFAULT 0.00 COMMENT '实发金额',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，CALCULATING-计算中，PENDING_APPROVAL-待审批，APPROVED-已审批，REJECTED-已拒绝',
  `calculation_date` datetime DEFAULT NULL COMMENT '计算时间',
  `approver_id` bigint(20) DEFAULT NULL COMMENT '审批人ID',
  `approve_date` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批备注',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 多租户字段（必需）
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段（必需）
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_number_tenant` (`calculation_number`, `tenant_id`, `delete_flag`),
  UNIQUE KEY `uk_employee_month_tenant` (`employee_id`, `calculation_month`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_month` (`tenant_id`, `calculation_month`, `delete_flag`),
  KEY `idx_status_tenant` (`status`, `tenant_id`, `delete_flag`),
  KEY `idx_calculation_date` (`calculation_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬计算记录表';

-- 5. 薪酬发放记录表
DROP TABLE IF EXISTS `jsh_salary_payment`;
CREATE TABLE `jsh_salary_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `payment_number` varchar(50) NOT NULL COMMENT '发放单号',
  `calculation_id` bigint(20) NOT NULL COMMENT '薪酬计算记录ID，关联jsh_salary_calculation表',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `employee_name` varchar(100) NOT NULL COMMENT '员工姓名',
  `calculation_month` varchar(7) NOT NULL COMMENT '薪酬月份，格式：YYYY-MM',
  `payment_amount` decimal(12,2) NOT NULL COMMENT '发放金额',
  `payment_date` date DEFAULT NULL COMMENT '发放日期',
  `payment_method` varchar(20) DEFAULT 'BANK_TRANSFER' COMMENT '发放方式：BANK_TRANSFER-银行转账，CASH-现金，OTHER-其他',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户银行',
  `payment_status` varchar(20) DEFAULT 'PENDING' COMMENT '发放状态：PENDING-待发放，PROCESSING-发放中，PAID-已发放，FAILED-发放失败',
  `financial_voucher_id` bigint(20) DEFAULT NULL COMMENT '财务凭证ID',
  `failure_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 多租户字段（必需）
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段（必需）
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_number_tenant` (`payment_number`, `tenant_id`, `delete_flag`),
  UNIQUE KEY `uk_calculation_tenant` (`calculation_id`, `tenant_id`, `delete_flag`),
  KEY `idx_employee_month` (`employee_id`, `calculation_month`, `tenant_id`, `delete_flag`),
  KEY `idx_payment_status` (`payment_status`, `tenant_id`, `delete_flag`),
  KEY `idx_payment_date` (`payment_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬发放记录表';

-- 6. 薪酬明细表
DROP TABLE IF EXISTS `jsh_salary_detail`;
CREATE TABLE `jsh_salary_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `calculation_id` bigint(20) NOT NULL COMMENT '薪酬计算记录ID，关联jsh_salary_calculation表',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `salary_item_id` bigint(20) NOT NULL COMMENT '薪酬项目ID，关联jsh_salary_item表',
  `item_code` varchar(50) NOT NULL COMMENT '薪酬项目编码',
  `item_name` varchar(100) NOT NULL COMMENT '薪酬项目名称',
  `item_type` varchar(20) NOT NULL COMMENT '项目类型：FIXED-固定薪酬，COMMISSION-提成，ALLOWANCE-津贴',
  `base_data` varchar(500) DEFAULT NULL COMMENT '基础数据（如销售额、工作天数等）',
  `calculation_formula` varchar(500) DEFAULT NULL COMMENT '计算公式',
  `rate_used` decimal(10,4) DEFAULT NULL COMMENT '使用的比例',
  `amount_used` decimal(10,2) DEFAULT NULL COMMENT '使用的金额',
  `calculation_amount` decimal(12,2) NOT NULL COMMENT '计算金额',
  `final_amount` decimal(12,2) NOT NULL COMMENT '最终金额',
  `data_source` varchar(100) DEFAULT NULL COMMENT '数据来源（如销售模块、生产模块等）',
  `calculation_detail` text DEFAULT NULL COMMENT '计算详情（JSON格式）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 多租户字段（必需）
  `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段（必需）
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  KEY `idx_calculation_tenant` (`calculation_id`, `tenant_id`, `delete_flag`),
  KEY `idx_employee_tenant` (`employee_id`, `tenant_id`, `delete_flag`),
  KEY `idx_item_tenant` (`salary_item_id`, `tenant_id`, `delete_flag`),
  KEY `idx_item_type` (`item_type`, `tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬明细表';
