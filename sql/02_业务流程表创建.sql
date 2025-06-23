-- jshERP智能生产管理模块 - 业务流程表创建脚本
-- 版本: v1.0
-- 创建日期: 2025-06-22
-- 说明: 创建生产管理系统的业务流程相关表

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 掐丝点蓝制作记录表
-- =====================================================
DROP TABLE IF EXISTS `jsh_cloisonne_production`;
CREATE TABLE `jsh_cloisonne_production` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `production_number` varchar(50) NOT NULL COMMENT '制作编号',
  `work_order_id` bigint(20) COMMENT '关联工单ID',
  `work_order_number` varchar(50) COMMENT '关联工单号',
  `material_id` bigint(20) NOT NULL COMMENT '原材料ID',
  `material_name` varchar(100) NOT NULL COMMENT '原材料名称',
  `material_model` varchar(50) COMMENT '原材料型号',
  `production_mode` varchar(20) NOT NULL COMMENT '制作模式：ORDER_DRIVEN-订单驱动,STOCK_DRIVEN-库存驱动,PLAN_DRIVEN-计划驱动,URGENT-紧急制作',
  `production_type` varchar(20) NOT NULL COMMENT '制作类型：HANDMADE-手工制作,SEMI_AUTO-半自动,CUSTOM-定制',
  `quantity` decimal(24,6) NOT NULL COMMENT '制作数量',
  `completed_quantity` decimal(24,6) DEFAULT 0 COMMENT '完成数量',
  `unit_name` varchar(10) NOT NULL COMMENT '单位',
  `unit_cost` decimal(24,6) COMMENT '单位成本',
  `labor_cost` decimal(24,6) COMMENT '人工成本',
  `material_cost` decimal(24,6) COMMENT '材料成本',
  `total_cost` decimal(24,6) COMMENT '总成本',
  `priority` varchar(10) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：URGENT-紧急,HIGH-高,NORMAL-普通,LOW-低',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待开始,IN_PROGRESS-进行中,PAUSED-暂停,COMPLETED-已完成,CANCELLED-已取消',
  `craft_type` varchar(50) COMMENT '工艺类型',
  `color_scheme` varchar(50) COMMENT '颜色方案',
  `pattern_design` varchar(100) COMMENT '图案设计',
  `size_specification` varchar(50) COMMENT '尺寸规格',
  `quality_grade` varchar(10) COMMENT '质量等级：A-优秀,B-良好,C-合格,D-不合格',
  `difficulty_level` varchar(10) COMMENT '难度等级：EASY-简单,MEDIUM-中等,HARD-困难,EXPERT-专家级',
  `estimated_hours` decimal(10,2) COMMENT '预计工时',
  `actual_hours` decimal(10,2) COMMENT '实际工时',
  `plan_start_time` datetime COMMENT '计划开始时间',
  `plan_end_time` datetime COMMENT '计划完成时间',
  `actual_start_time` datetime COMMENT '实际开始时间',
  `actual_end_time` datetime COMMENT '实际完成时间',
  `assigned_worker_id` bigint(20) COMMENT '分配工人ID',
  `assigned_worker_name` varchar(50) COMMENT '分配工人姓名',
  `supplier_id` bigint(20) COMMENT '供应商ID',
  `supplier_name` varchar(100) COMMENT '供应商名称',
  `customer_id` bigint(20) COMMENT '客户ID（订单驱动时）',
  `customer_name` varchar(100) COMMENT '客户名称',
  `production_reason` varchar(200) COMMENT '制作原因',
  `quality_requirements` text COMMENT '质量要求',
  `special_instructions` text COMMENT '特殊说明',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_production_number` (`production_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_worker_id` (`assigned_worker_id`),
  KEY `idx_production_mode` (`production_mode`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝点蓝制作记录表';

-- =====================================================
-- 2. 配饰制作记录表
-- =====================================================
DROP TABLE IF EXISTS `jsh_accessory_production`;
CREATE TABLE `jsh_accessory_production` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `production_number` varchar(50) NOT NULL COMMENT '制作编号',
  `work_order_id` bigint(20) COMMENT '关联工单ID',
  `work_order_number` varchar(50) COMMENT '关联工单号',
  `semi_product_id` bigint(20) NOT NULL COMMENT '半成品ID',
  `semi_product_name` varchar(100) NOT NULL COMMENT '半成品名称',
  `accessory_type` varchar(50) NOT NULL COMMENT '配饰类型',
  `accessory_style` varchar(50) COMMENT '配饰风格',
  `quantity` decimal(24,6) NOT NULL COMMENT '制作数量',
  `completed_quantity` decimal(24,6) DEFAULT 0 COMMENT '完成数量',
  `unit_name` varchar(10) NOT NULL COMMENT '单位',
  `unit_cost` decimal(24,6) COMMENT '单位成本',
  `labor_cost` decimal(24,6) COMMENT '人工成本',
  `material_cost` decimal(24,6) COMMENT '材料成本',
  `accessory_cost` decimal(24,6) COMMENT '配件成本',
  `total_cost` decimal(24,6) COMMENT '总成本',
  `priority` varchar(10) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：URGENT-紧急,HIGH-高,NORMAL-普通,LOW-低',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待开始,IN_PROGRESS-进行中,PAUSED-暂停,COMPLETED-已完成,CANCELLED-已取消',
  `assembly_method` varchar(50) COMMENT '组装方式',
  `finishing_process` varchar(50) COMMENT '后处理工艺',
  `quality_grade` varchar(10) COMMENT '质量等级：A-优秀,B-良好,C-合格,D-不合格',
  `difficulty_level` varchar(10) COMMENT '难度等级：EASY-简单,MEDIUM-中等,HARD-困难,EXPERT-专家级',
  `estimated_hours` decimal(10,2) COMMENT '预计工时',
  `actual_hours` decimal(10,2) COMMENT '实际工时',
  `plan_start_time` datetime COMMENT '计划开始时间',
  `plan_end_time` datetime COMMENT '计划完成时间',
  `actual_start_time` datetime COMMENT '实际开始时间',
  `actual_end_time` datetime COMMENT '实际完成时间',
  `assigned_worker_id` bigint(20) COMMENT '分配工人ID',
  `assigned_worker_name` varchar(50) COMMENT '分配工人姓名',
  `customer_id` bigint(20) COMMENT '客户ID',
  `customer_name` varchar(100) COMMENT '客户名称',
  `design_requirements` text COMMENT '设计要求',
  `quality_requirements` text COMMENT '质量要求',
  `special_instructions` text COMMENT '特殊说明',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_production_number` (`production_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_semi_product_id` (`semi_product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_worker_id` (`assigned_worker_id`),
  KEY `idx_accessory_type` (`accessory_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配饰制作记录表';

-- =====================================================
-- 3. 后工任务记录表
-- =====================================================
DROP TABLE IF EXISTS `jsh_post_processing_task`;
CREATE TABLE `jsh_post_processing_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `work_order_id` bigint(20) COMMENT '关联工单ID',
  `work_order_number` varchar(50) COMMENT '关联工单号',
  `production_id` bigint(20) COMMENT '关联生产记录ID',
  `production_number` varchar(50) COMMENT '关联生产编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_model` varchar(50) COMMENT '产品型号',
  `task_type` varchar(20) NOT NULL COMMENT '任务类型：POLISHING-抛光打磨,QUALITY_CHECK-质量检验,PACKAGING-包装处理,LABELING-标签贴附,FINAL_CHECK-最终检查',
  `task_category` varchar(20) COMMENT '任务分类：MANUAL-手工,MACHINE-机器,MIXED-混合',
  `quantity` decimal(24,6) NOT NULL COMMENT '任务数量',
  `completed_quantity` decimal(24,6) DEFAULT 0 COMMENT '完成数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `unit_name` varchar(10) NOT NULL COMMENT '单位',
  `unit_fee` decimal(24,6) COMMENT '单位费用',
  `total_fee` decimal(24,6) COMMENT '总费用',
  `priority` varchar(10) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：URGENT-紧急,HIGH-高,NORMAL-普通,LOW-低',
  `status` varchar(20) NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可领取,CLAIMED-已领取,IN_PROGRESS-进行中,COMPLETED-已完成,QUALITY_CHECKED-已质检',
  `difficulty_level` varchar(10) COMMENT '难度等级：EASY-简单,MEDIUM-中等,HARD-困难,EXPERT-专家级',
  `estimated_hours` decimal(10,2) COMMENT '预计工时',
  `actual_hours` decimal(10,2) COMMENT '实际工时',
  `plan_start_time` datetime COMMENT '计划开始时间',
  `plan_end_time` datetime COMMENT '计划完成时间',
  `actual_start_time` datetime COMMENT '实际开始时间',
  `actual_end_time` datetime COMMENT '实际完成时间',
  `deadline` datetime COMMENT '截止时间',
  `assigned_worker_id` bigint(20) COMMENT '分配工人ID',
  `assigned_worker_name` varchar(50) COMMENT '分配工人姓名',
  `claim_time` datetime COMMENT '领取时间',
  `complete_time` datetime COMMENT '完成时间',
  `quality_check_time` datetime COMMENT '质检时间',
  `quality_checker_id` bigint(20) COMMENT '质检员ID',
  `quality_checker_name` varchar(50) COMMENT '质检员姓名',
  `quality_result` varchar(10) COMMENT '质检结果：PASS-合格,FAIL-不合格,REWORK-返工',
  `quality_score` decimal(5,2) COMMENT '质量评分',
  `task_requirements` text COMMENT '任务要求',
  `quality_standards` text COMMENT '质量标准',
  `completion_notes` text COMMENT '完成说明',
  `quality_notes` text COMMENT '质检说明',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_number` (`task_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_production_id` (`production_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_worker_id` (`assigned_worker_id`),
  KEY `idx_deadline` (`deadline`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后工任务记录表';

-- =====================================================
-- 4. 任务状态变更记录表
-- =====================================================
DROP TABLE IF EXISTS `jsh_task_status_log`;
CREATE TABLE `jsh_task_status_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `task_type` varchar(20) NOT NULL COMMENT '任务类型：WORK_ORDER-工单,PRODUCTION_TASK-生产任务,POST_PROCESSING-后工任务',
  `old_status` varchar(20) COMMENT '原状态',
  `new_status` varchar(20) NOT NULL COMMENT '新状态',
  `status_change_reason` varchar(200) COMMENT '状态变更原因',
  `operator_id` bigint(20) COMMENT '操作人ID',
  `operator_name` varchar(50) COMMENT '操作人姓名',
  `change_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '变更时间',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_change_time` (`change_time`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务状态变更记录表';

-- =====================================================
-- 5. 工艺流程模板表
-- =====================================================
DROP TABLE IF EXISTS `jsh_process_template`;
CREATE TABLE `jsh_process_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_type` varchar(20) NOT NULL COMMENT '模板类型：CLOISONNE-掐丝点蓝,ACCESSORY-配饰制作,POST_PROCESS-后工处理',
  `product_category` varchar(50) COMMENT '适用产品类别',
  `process_description` text COMMENT '工艺流程描述',
  `step_count` int(11) NOT NULL DEFAULT 0 COMMENT '工艺步骤数',
  `estimated_total_hours` decimal(10,2) COMMENT '预计总工时',
  `difficulty_level` varchar(10) COMMENT '难度等级：EASY-简单,MEDIUM-中等,HARD-困难,EXPERT-专家级',
  `required_skills` varchar(200) COMMENT '所需技能',
  `quality_standards` text COMMENT '质量标准',
  `safety_requirements` text COMMENT '安全要求',
  `tools_equipment` text COMMENT '所需工具设备',
  `materials_list` text COMMENT '材料清单',
  `status` varchar(10) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用,INACTIVE-停用,DRAFT-草稿',
  `version` varchar(10) NOT NULL DEFAULT '1.0' COMMENT '版本号',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_template_type` (`template_type`),
  KEY `idx_status` (`status`),
  KEY `idx_product_category` (`product_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺流程模板表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入说明
SELECT '业务流程表创建完成！' as message;
SELECT '包含表：掐丝点蓝制作记录表、配饰制作记录表、后工任务记录表、任务状态变更记录表、工艺流程模板表' as tables;
