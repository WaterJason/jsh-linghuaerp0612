-- jshERP智能生产管理模块 - 核心业务表创建脚本
-- 版本: v1.0
-- 创建日期: 2025-06-22
-- 说明: 创建生产管理系统的核心业务表

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 生产工单表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_work_order`;
CREATE TABLE `jsh_production_work_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_number` varchar(50) NOT NULL COMMENT '工单号',
  `order_type` varchar(20) NOT NULL COMMENT '工单类型：CLOISONNE-掐丝点蓝,ACCESSORY-配饰制作',
  `order_source` varchar(20) NOT NULL COMMENT '工单来源：SALES-销售订单,STOCK-库存补充,PLAN-计划生产,URGENT-紧急制作',
  `material_id` bigint(20) NOT NULL COMMENT '商品ID',
  `material_name` varchar(100) NOT NULL COMMENT '商品名称',
  `material_model` varchar(50) COMMENT '商品型号',
  `material_unit` varchar(10) NOT NULL COMMENT '单位',
  `plan_quantity` decimal(24,6) NOT NULL COMMENT '计划数量',
  `completed_quantity` decimal(24,6) DEFAULT 0 COMMENT '完成数量',
  `unit_price` decimal(24,6) COMMENT '单价',
  `total_amount` decimal(24,6) COMMENT '总金额',
  `priority` varchar(10) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：URGENT-紧急,HIGH-高,NORMAL-普通,LOW-低',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待派单,ASSIGNED-已派单,IN_PROGRESS-进行中,COMPLETED-已完成,CANCELLED-已取消',
  `plan_start_time` datetime COMMENT '计划开始时间',
  `plan_end_time` datetime COMMENT '计划完成时间',
  `actual_start_time` datetime COMMENT '实际开始时间',
  `actual_end_time` datetime COMMENT '实际完成时间',
  `assigned_worker_id` bigint(20) COMMENT '分配工人ID',
  `assigned_worker_name` varchar(50) COMMENT '分配工人姓名',
  `customer_id` bigint(20) COMMENT '客户ID（销售订单来源）',
  `customer_name` varchar(100) COMMENT '客户名称',
  `sales_order_id` bigint(20) COMMENT '销售订单ID',
  `sales_order_number` varchar(50) COMMENT '销售订单号',
  `craft_requirements` text COMMENT '工艺要求',
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
  UNIQUE KEY `uk_order_number` (`order_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_worker_id` (`assigned_worker_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工单表';

-- =====================================================
-- 2. 工人信息表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_worker`;
CREATE TABLE `jsh_production_worker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `worker_number` varchar(50) NOT NULL COMMENT '工人编号',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `worker_type` varchar(20) NOT NULL COMMENT '工人类型：FULL_TIME-全职,PART_TIME-兼职,TEMPORARY-临时',
  `gender` varchar(1) COMMENT '性别：M-男,F-女',
  `phone` varchar(20) COMMENT '联系电话',
  `email` varchar(100) COMMENT '邮箱',
  `id_card` varchar(20) COMMENT '身份证号',
  `address` varchar(200) COMMENT '地址',
  `specialty` varchar(100) NOT NULL COMMENT '专业技能',
  `skill_level` varchar(10) NOT NULL COMMENT '技能等级：JUNIOR-初级,INTERMEDIATE-中级,SENIOR-高级,EXPERT-专家',
  `experience_years` int(11) COMMENT '工作经验年数',
  `hourly_rate` decimal(10,2) COMMENT '小时工资',
  `piece_rate` decimal(10,2) COMMENT '计件工资',
  `status` varchar(10) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-在职,INACTIVE-离职,SUSPENDED-停职',
  `availability_status` varchar(10) NOT NULL DEFAULT 'AVAILABLE' COMMENT '可用状态：AVAILABLE-可用,BUSY-忙碌,OFFLINE-离线',
  `current_workload` int(11) DEFAULT 0 COMMENT '当前工作负荷',
  `max_workload` int(11) DEFAULT 10 COMMENT '最大工作负荷',
  `efficiency_rating` decimal(5,2) DEFAULT 100 COMMENT '效率评级',
  `quality_rating` decimal(5,2) DEFAULT 100 COMMENT '质量评级',
  `hire_date` date COMMENT '入职日期',
  `department` varchar(50) COMMENT '部门',
  `supervisor_id` bigint(20) COMMENT '主管ID',
  `supervisor_name` varchar(50) COMMENT '主管姓名',
  `emergency_contact` varchar(50) COMMENT '紧急联系人',
  `emergency_phone` varchar(20) COMMENT '紧急联系电话',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_worker_number` (`worker_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_worker_name` (`worker_name`),
  KEY `idx_specialty` (`specialty`),
  KEY `idx_status` (`status`),
  KEY `idx_availability` (`availability_status`),
  KEY `idx_supervisor_id` (`supervisor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工人信息表';

-- =====================================================
-- 3. 生产任务表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_task`;
CREATE TABLE `jsh_production_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单号',
  `task_type` varchar(20) NOT NULL COMMENT '任务类型：CLOISONNE-掐丝点蓝制作,ACCESSORY-配饰制作,POST_PROCESS-后工处理',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `task_description` text COMMENT '任务描述',
  `material_id` bigint(20) NOT NULL COMMENT '商品ID',
  `material_name` varchar(100) NOT NULL COMMENT '商品名称',
  `plan_quantity` decimal(24,6) NOT NULL COMMENT '计划数量',
  `completed_quantity` decimal(24,6) DEFAULT 0 COMMENT '完成数量',
  `unit_name` varchar(10) NOT NULL COMMENT '单位',
  `priority` varchar(10) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：URGENT-紧急,HIGH-高,NORMAL-普通,LOW-低',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待派单,ASSIGNED-已派单,IN_PROGRESS-进行中,PAUSED-暂停,COMPLETED-已完成,CANCELLED-已取消',
  `plan_start_time` datetime COMMENT '计划开始时间',
  `plan_end_time` datetime COMMENT '计划完成时间',
  `actual_start_time` datetime COMMENT '实际开始时间',
  `actual_end_time` datetime COMMENT '实际完成时间',
  `estimated_hours` decimal(10,2) COMMENT '预计工时',
  `actual_hours` decimal(10,2) COMMENT '实际工时',
  `assigned_worker_id` bigint(20) COMMENT '分配工人ID',
  `assigned_worker_name` varchar(50) COMMENT '分配工人姓名',
  `worker_specialty` varchar(50) COMMENT '工人专业技能',
  `difficulty_level` varchar(10) COMMENT '难度等级：EASY-简单,MEDIUM-中等,HARD-困难,EXPERT-专家级',
  `craft_type` varchar(50) COMMENT '工艺类型',
  `color_scheme` varchar(50) COMMENT '颜色方案',
  `quality_requirements` text COMMENT '质量要求',
  `special_instructions` text COMMENT '特殊说明',
  `progress_percentage` decimal(5,2) DEFAULT 0 COMMENT '完成进度百分比',
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
  KEY `idx_material_id` (`material_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_worker_id` (`assigned_worker_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产任务表';

-- =====================================================
-- 4. 生产报工表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_report`;
CREATE TABLE `jsh_production_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_number` varchar(50) NOT NULL COMMENT '报工单号',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单号',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `report_type` varchar(20) NOT NULL COMMENT '报工类型：START-开始,PROGRESS-进度,COMPLETE-完成,PAUSE-暂停,RESUME-恢复',
  `report_date` date NOT NULL COMMENT '报工日期',
  `report_time` datetime NOT NULL COMMENT '报工时间',
  `work_hours` decimal(10,2) COMMENT '工作时间',
  `completed_quantity` decimal(24,6) COMMENT '完成数量',
  `qualified_quantity` decimal(24,6) COMMENT '合格数量',
  `defective_quantity` decimal(24,6) COMMENT '不合格数量',
  `progress_percentage` decimal(5,2) COMMENT '完成进度百分比',
  `quality_score` decimal(5,2) COMMENT '质量评分',
  `efficiency_score` decimal(5,2) COMMENT '效率评分',
  `work_description` text COMMENT '工作描述',
  `quality_note` text COMMENT '质量说明',
  `issue_description` text COMMENT '问题描述',
  `solution_description` text COMMENT '解决方案',
  `photo_urls` text COMMENT '照片URL列表（JSON格式）',
  `attachment_urls` text COMMENT '附件URL列表（JSON格式）',
  `next_step_plan` text COMMENT '下一步计划',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_number` (`report_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_report_date` (`report_date`),
  KEY `idx_report_type` (`report_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产报工表';

-- =====================================================
-- 5. 工单材料清单表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_work_order_material`;
CREATE TABLE `jsh_production_work_order_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单号',
  `material_id` bigint(20) NOT NULL COMMENT '材料ID',
  `material_name` varchar(100) NOT NULL COMMENT '材料名称',
  `material_model` varchar(50) COMMENT '材料型号',
  `material_unit` varchar(10) NOT NULL COMMENT '材料单位',
  `material_type` varchar(20) NOT NULL COMMENT '材料类型：RAW-原材料,SEMI-半成品,ACCESSORY-配件',
  `required_quantity` decimal(24,6) NOT NULL COMMENT '需求数量',
  `allocated_quantity` decimal(24,6) DEFAULT 0 COMMENT '已分配数量',
  `consumed_quantity` decimal(24,6) DEFAULT 0 COMMENT '已消耗数量',
  `unit_cost` decimal(24,6) COMMENT '单位成本',
  `total_cost` decimal(24,6) COMMENT '总成本',
  `supplier_id` bigint(20) COMMENT '供应商ID',
  `supplier_name` varchar(100) COMMENT '供应商名称',
  `warehouse_id` bigint(20) COMMENT '仓库ID',
  `warehouse_name` varchar(100) COMMENT '仓库名称',
  `allocation_status` varchar(20) DEFAULT 'PENDING' COMMENT '分配状态：PENDING-待分配,ALLOCATED-已分配,CONSUMED-已消耗',
  `allocation_time` datetime COMMENT '分配时间',
  `consumption_time` datetime COMMENT '消耗时间',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_allocation_status` (`allocation_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单材料清单表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入说明
SELECT '核心业务表创建完成！' as message;
SELECT '包含表：生产工单表、工人信息表、生产任务表、生产报工表、工单材料清单表' as tables;
