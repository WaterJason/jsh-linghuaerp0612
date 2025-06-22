-- =====================================================
-- jshERP 智能生产管理模块数据库表结构
-- 创建时间: 2025-06-22
-- 版本: 1.0
-- 说明: 基于jshERP架构规范设计的生产管理核心表
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 生产工单表 (jsh_production_work_order)
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_work_order`;
CREATE TABLE `jsh_production_work_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `work_order_name` varchar(100) NOT NULL COMMENT '工单名称',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID，关联jsh_material表',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_spec` varchar(200) DEFAULT NULL COMMENT '产品规格',
  `production_mode` varchar(20) NOT NULL DEFAULT 'STOCK_DRIVEN' COMMENT '制作模式：ORDER_DRIVEN(订单驱动)、STOCK_DRIVEN(库存驱动)、SELF_PLANNED(自主计划)、EMERGENCY(紧急制作)',
  `production_type` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '制作类型：NORMAL(正常制作)、REWORK(返工)、SAMPLE(样品)、EMERGENCY(紧急)',
  `priority` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：LOW(低)、NORMAL(普通)、HIGH(高)、URGENT(紧急)',
  `production_reason` varchar(200) DEFAULT NULL COMMENT '制作原因',
  `planned_quantity` decimal(24,6) NOT NULL COMMENT '计划数量',
  `actual_quantity` decimal(24,6) DEFAULT 0 COMMENT '实际完成数量',
  `unit_id` bigint(20) DEFAULT NULL COMMENT '单位ID，关联jsh_unit表',
  `unit_name` varchar(20) DEFAULT NULL COMMENT '单位名称',
  `plan_start_time` datetime DEFAULT NULL COMMENT '计划开始时间',
  `plan_end_time` datetime DEFAULT NULL COMMENT '计划完成时间',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际完成时间',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '工单状态：PENDING(待开始)、IN_PROGRESS(进行中)、COMPLETED(已完成)、CANCELLED(已取消)、PAUSED(已暂停)',
  `workshop_id` bigint(20) DEFAULT NULL COMMENT '车间ID',
  `workshop_name` varchar(50) DEFAULT NULL COMMENT '车间名称',
  `responsible_person` varchar(50) DEFAULT NULL COMMENT '负责人',
  `estimated_cost` decimal(24,6) DEFAULT 0 COMMENT '预估成本',
  `actual_cost` decimal(24,6) DEFAULT 0 COMMENT '实际成本',
  `estimated_hours` decimal(24,6) DEFAULT 0 COMMENT '预估工时',
  `actual_hours` decimal(24,6) DEFAULT 0 COMMENT '实际工时',
  `quality_standard` varchar(200) DEFAULT NULL COMMENT '质量标准',
  `remark` text DEFAULT NULL COMMENT '备注',
  `source_type` varchar(20) DEFAULT NULL COMMENT '来源类型：SALES_ORDER(销售订单)、STOCK_PLAN(库存计划)、MANUAL(手动创建)',
  `source_id` bigint(20) DEFAULT NULL COMMENT '来源ID',
  `source_number` varchar(50) DEFAULT NULL COMMENT '来源单号',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_order_number_tenant` (`work_order_number`, `tenant_id`, `delete_flag`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status_tenant` (`status`, `tenant_id`),
  KEY `idx_plan_time` (`plan_start_time`, `plan_end_time`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工单表';

-- =====================================================
-- 2. 生产任务表 (jsh_production_task)
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_task`;
CREATE TABLE `jsh_production_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID，关联jsh_production_work_order表',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `task_type` varchar(20) NOT NULL COMMENT '任务类型：QISI_DIANLIAN(掐丝点蓝)、PEISHI_ZHIZUO(配饰制作)、HOUGONG_CHULI(后工处理)、ZHILIANG_JIANYAN(质量检验)',
  `process_step` varchar(50) DEFAULT NULL COMMENT '工序步骤',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID，关联jsh_material表',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `task_quantity` decimal(24,6) NOT NULL COMMENT '任务数量',
  `completed_quantity` decimal(24,6) DEFAULT 0 COMMENT '已完成数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `unit_id` bigint(20) DEFAULT NULL COMMENT '单位ID',
  `unit_name` varchar(20) DEFAULT NULL COMMENT '单位名称',
  `priority` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '优先级：LOW(低)、NORMAL(普通)、HIGH(高)、URGENT(紧急)',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '任务状态：PENDING(待派单)、ASSIGNED(已派单)、IN_PROGRESS(进行中)、COMPLETED(已完成)、QUALITY_CHECKED(已质检)、CANCELLED(已取消)',
  `worker_id` bigint(20) DEFAULT NULL COMMENT '工人ID，关联jsh_user表',
  `worker_name` varchar(50) DEFAULT NULL COMMENT '工人姓名',
  `worker_specialty` varchar(50) DEFAULT NULL COMMENT '工人专长',
  `assign_time` datetime DEFAULT NULL COMMENT '派单时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `plan_start_time` datetime DEFAULT NULL COMMENT '计划开始时间',
  `plan_end_time` datetime DEFAULT NULL COMMENT '计划完成时间',
  `estimated_hours` decimal(24,6) DEFAULT 0 COMMENT '预估工时',
  `actual_hours` decimal(24,6) DEFAULT 0 COMMENT '实际工时',
  `unit_fee` decimal(24,6) DEFAULT 0 COMMENT '单价工费',
  `total_fee` decimal(24,6) DEFAULT 0 COMMENT '总工费',
  `quality_level` varchar(20) DEFAULT NULL COMMENT '质量等级：A+、A、B、C、D',
  `quality_score` decimal(5,2) DEFAULT NULL COMMENT '质量评分(1-5分)',
  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_number_tenant` (`task_number`, `tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_status_tenant` (`status`, `tenant_id`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_plan_time` (`plan_start_time`, `plan_end_time`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产任务表';

-- =====================================================
-- 3. 生产报工表 (jsh_production_report)
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_report`;
CREATE TABLE `jsh_production_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `report_number` varchar(50) NOT NULL COMMENT '报工编号',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID，关联jsh_production_task表',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID，关联jsh_user表',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `report_type` varchar(20) NOT NULL DEFAULT 'PROGRESS' COMMENT '报工类型：PROGRESS(进度报工)、COMPLETE(完工报工)、PAUSE(暂停报工)、RESUME(恢复报工)',
  `report_time` datetime NOT NULL COMMENT '报工时间',
  `work_date` date NOT NULL COMMENT '工作日期',
  `completed_quantity` decimal(24,6) NOT NULL COMMENT '完成数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `work_hours` decimal(24,6) NOT NULL COMMENT '工作时长(小时)',
  `unit_fee` decimal(24,6) DEFAULT 0 COMMENT '单价工费',
  `total_fee` decimal(24,6) DEFAULT 0 COMMENT '总工费',
  `quality_level` varchar(20) DEFAULT NULL COMMENT '质量等级：A+、A、B、C、D',
  `work_content` text DEFAULT NULL COMMENT '工作内容描述',
  `work_photos` text DEFAULT NULL COMMENT '工作照片(JSON格式存储)',
  `problem_description` text DEFAULT NULL COMMENT '问题描述',
  `is_completed` tinyint(1) DEFAULT 0 COMMENT '是否完工：0否 1是',
  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_number_tenant` (`report_number`, `tenant_id`, `delete_flag`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_work_date` (`work_date`),
  KEY `idx_report_type` (`report_type`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产报工表';

-- =====================================================
-- 4. 质量检验表 (jsh_quality_inspection)
-- =====================================================
DROP TABLE IF EXISTS `jsh_quality_inspection`;
CREATE TABLE `jsh_quality_inspection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `inspection_number` varchar(50) NOT NULL COMMENT '质检编号',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID，关联jsh_production_task表',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID，关联jsh_material表',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `inspector_id` bigint(20) NOT NULL COMMENT '质检员ID，关联jsh_user表',
  `inspector_name` varchar(50) NOT NULL COMMENT '质检员姓名',
  `inspection_time` datetime NOT NULL COMMENT '质检时间',
  `inspection_type` varchar(20) NOT NULL DEFAULT 'FINAL' COMMENT '质检类型：INCOMING(来料检验)、PROCESS(过程检验)、FINAL(最终检验)、REWORK(返工检验)',
  `inspection_quantity` decimal(24,6) NOT NULL COMMENT '检验数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `qualification_rate` decimal(5,2) DEFAULT 0 COMMENT '合格率(%)',
  `appearance_score` decimal(3,1) DEFAULT NULL COMMENT '外观评分(1-5分)',
  `size_score` decimal(3,1) DEFAULT NULL COMMENT '尺寸评分(1-5分)',
  `color_score` decimal(3,1) DEFAULT NULL COMMENT '颜色评分(1-5分)',
  `texture_score` decimal(3,1) DEFAULT NULL COMMENT '质感评分(1-5分)',
  `detail_score` decimal(3,1) DEFAULT NULL COMMENT '细节评分(1-5分)',
  `overall_effect_score` decimal(3,1) DEFAULT NULL COMMENT '整体效果评分(1-5分)',
  `overall_score` decimal(3,1) DEFAULT NULL COMMENT '综合评分(1-5分)',
  `quality_grade` varchar(20) DEFAULT NULL COMMENT '质量等级：A+、A、B、C、D',
  `overall_result` varchar(20) NOT NULL COMMENT '总体结果：PASS(合格)、FAIL(不合格)、REWORK(需返工)',
  `problem_description` text DEFAULT NULL COMMENT '问题描述',
  `improvement_suggestion` text DEFAULT NULL COMMENT '改进建议',
  `quality_photos` text DEFAULT NULL COMMENT '质检照片(JSON格式存储)',
  `inspection_standard` text DEFAULT NULL COMMENT '检验标准',
  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inspection_number_tenant` (`inspection_number`, `tenant_id`, `delete_flag`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_inspector_id` (`inspector_id`),
  KEY `idx_inspection_time` (`inspection_time`),
  KEY `idx_overall_result` (`overall_result`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检验表';

-- =====================================================
-- 5. 物流追踪表 (jsh_logistics_tracking)
-- =====================================================
DROP TABLE IF EXISTS `jsh_logistics_tracking`;
CREATE TABLE `jsh_logistics_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tracking_number` varchar(50) NOT NULL COMMENT '追踪编号',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID，关联jsh_production_work_order表',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `logistics_type` varchar(20) NOT NULL COMMENT '物流类型：MATERIAL_IN(原料入库)、SEMI_PRODUCT_OUT(半成品出库)、PRODUCT_IN(成品入库)、PRODUCT_OUT(成品出库)',
  `material_id` bigint(20) DEFAULT NULL COMMENT '物料ID，关联jsh_material表',
  `material_name` varchar(100) DEFAULT NULL COMMENT '物料名称',
  `quantity` decimal(24,6) NOT NULL COMMENT '数量',
  `unit_name` varchar(20) DEFAULT NULL COMMENT '单位名称',
  `from_location` varchar(100) DEFAULT NULL COMMENT '发货地点',
  `to_location` varchar(100) DEFAULT NULL COMMENT '收货地点',
  `carrier` varchar(50) DEFAULT NULL COMMENT '承运人/物流公司',
  `carrier_contact` varchar(50) DEFAULT NULL COMMENT '承运人联系方式',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `estimated_arrival_time` datetime DEFAULT NULL COMMENT '预计到达时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING(待发货)、SHIPPED(已发货)、IN_TRANSIT(运输中)、ARRIVED(已到达)、RECEIVED(已收货)、EXCEPTION(异常)',
  `tracking_info` text DEFAULT NULL COMMENT '追踪信息(JSON格式存储)',
  `exception_reason` varchar(200) DEFAULT NULL COMMENT '异常原因',
  `handler` varchar(50) DEFAULT NULL COMMENT '处理人',
  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tracking_number_tenant` (`tracking_number`, `tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_logistics_type` (`logistics_type`),
  KEY `idx_status` (`status`),
  KEY `idx_ship_time` (`ship_time`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流追踪表';

-- =====================================================
-- 6. 工人技能表 (jsh_worker_skill)
-- =====================================================
DROP TABLE IF EXISTS `jsh_worker_skill`;
CREATE TABLE `jsh_worker_skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID，关联jsh_user表',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `skill_type` varchar(50) NOT NULL COMMENT '技能类型：QISI_DIANLIAN(掐丝点蓝)、PEISHI_ZHIZUO(配饰制作)、HOUGONG_CHULI(后工处理)、ZHILIANG_JIANYAN(质量检验)',
  `skill_level` varchar(20) NOT NULL DEFAULT 'BEGINNER' COMMENT '技能等级：BEGINNER(新手)、INTERMEDIATE(中级)、ADVANCED(高级)、EXPERT(专家)、MASTER(大师)',
  `skill_score` decimal(3,1) DEFAULT 3.0 COMMENT '技能评分(1-5分)',
  `experience_years` decimal(5,2) DEFAULT 0 COMMENT '经验年限',
  `certification_level` varchar(50) DEFAULT NULL COMMENT '认证等级',
  `certification_date` date DEFAULT NULL COMMENT '认证日期',
  `hourly_rate` decimal(10,2) DEFAULT 0 COMMENT '小时工资',
  `piece_rate` decimal(10,2) DEFAULT 0 COMMENT '计件工资',
  `efficiency_rate` decimal(5,2) DEFAULT 100 COMMENT '效率系数(%)',
  `quality_rate` decimal(5,2) DEFAULT 100 COMMENT '质量系数(%)',
  `is_active` tinyint(1) DEFAULT 1 COMMENT '是否启用：0否 1是',
  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_worker_skill_tenant` (`worker_id`, `skill_type`, `tenant_id`, `delete_flag`),
  KEY `idx_skill_type` (`skill_type`),
  KEY `idx_skill_level` (`skill_level`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工人技能表';

-- =====================================================
-- 7. 生产统计表 (jsh_production_statistics)
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_statistics`;
CREATE TABLE `jsh_production_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` varchar(20) NOT NULL COMMENT '统计类型：DAILY(日统计)、WEEKLY(周统计)、MONTHLY(月统计)、YEARLY(年统计)',
  `work_order_count` int(11) DEFAULT 0 COMMENT '工单数量',
  `task_count` int(11) DEFAULT 0 COMMENT '任务数量',
  `completed_task_count` int(11) DEFAULT 0 COMMENT '完成任务数量',
  `total_quantity` decimal(24,6) DEFAULT 0 COMMENT '总生产数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `qualification_rate` decimal(5,2) DEFAULT 0 COMMENT '合格率(%)',
  `total_hours` decimal(24,6) DEFAULT 0 COMMENT '总工时',
  `total_cost` decimal(24,6) DEFAULT 0 COMMENT '总成本',
  `total_fee` decimal(24,6) DEFAULT 0 COMMENT '总工费',
  `efficiency_rate` decimal(5,2) DEFAULT 0 COMMENT '效率(%)',
  `worker_count` int(11) DEFAULT 0 COMMENT '参与工人数',
  `avg_task_hours` decimal(10,2) DEFAULT 0 COMMENT '平均任务工时',
  `avg_quality_score` decimal(3,1) DEFAULT 0 COMMENT '平均质量评分',
  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date_type_tenant` (`stat_date`, `stat_type`, `tenant_id`, `delete_flag`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产统计表';

-- =====================================================
-- 初始化序列号配置
-- =====================================================
INSERT INTO `jsh_sequence` (`seq_name`, `min_value`, `max_value`, `current_val`, `increment_val`, `remark`) VALUES
('production_work_order_seq', 1, 999999999999999999, 1, 1, '生产工单编号序列'),
('production_task_seq', 1, 999999999999999999, 1, 1, '生产任务编号序列'),
('production_report_seq', 1, 999999999999999999, 1, 1, '生产报工编号序列'),
('quality_inspection_seq', 1, 999999999999999999, 1, 1, '质量检验编号序列'),
('logistics_tracking_seq', 1, 999999999999999999, 1, 1, '物流追踪编号序列')
ON DUPLICATE KEY UPDATE `remark` = VALUES(`remark`);

-- =====================================================
-- 复合索引优化（可选，根据查询需求添加）
-- =====================================================

-- 生产工单表复合索引
-- ALTER TABLE `jsh_production_work_order` ADD INDEX `idx_product_status_tenant` (`product_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_production_work_order` ADD INDEX `idx_priority_time_tenant` (`priority`, `plan_start_time`, `tenant_id`);

-- 生产任务表复合索引
-- ALTER TABLE `jsh_production_task` ADD INDEX `idx_worker_status_tenant` (`worker_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_production_task` ADD INDEX `idx_type_priority_tenant` (`task_type`, `priority`, `tenant_id`);

-- 生产报工表复合索引
-- ALTER TABLE `jsh_production_report` ADD INDEX `idx_worker_date_tenant` (`worker_id`, `work_date`, `tenant_id`);
-- ALTER TABLE `jsh_production_report` ADD INDEX `idx_task_type_tenant` (`task_id`, `report_type`, `tenant_id`);

-- 质量检验表复合索引
-- ALTER TABLE `jsh_quality_inspection` ADD INDEX `idx_product_result_tenant` (`product_id`, `overall_result`, `tenant_id`);
-- ALTER TABLE `jsh_quality_inspection` ADD INDEX `idx_inspector_time_tenant` (`inspector_id`, `inspection_time`, `tenant_id`);

-- 物流追踪表复合索引
-- ALTER TABLE `jsh_logistics_tracking` ADD INDEX `idx_workorder_status_tenant` (`work_order_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_logistics_tracking` ADD INDEX `idx_type_status_time` (`logistics_type`, `status`, `ship_time`);

-- 工人技能表复合索引
-- ALTER TABLE `jsh_worker_skill` ADD INDEX `idx_skill_level_active` (`skill_type`, `skill_level`, `is_active`);
-- ALTER TABLE `jsh_worker_skill` ADD INDEX `idx_worker_active_tenant` (`worker_id`, `is_active`, `tenant_id`);

-- 生产统计表复合索引
-- ALTER TABLE `jsh_production_statistics` ADD INDEX `idx_type_date_tenant` (`stat_type`, `stat_date`, `tenant_id`);

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 数据库表结构说明
-- =====================================================

/*
表结构设计说明：

1. 生产工单表 (jsh_production_work_order)
   - 主要用于管理生产工单，支持多种制作模式
   - 包含完整的时间管理、成本管理、状态管理
   - 支持与销售订单、库存计划的关联
   - 状态流转：PENDING → IN_PROGRESS → COMPLETED/CANCELLED/PAUSED

2. 生产任务表 (jsh_production_task)
   - 工单的具体执行单元，支持任务分解
   - 包含工人分配、进度跟踪、质量管理
   - 支持多种任务类型和工序步骤
   - 状态流转：PENDING → ASSIGNED → IN_PROGRESS → COMPLETED → QUALITY_CHECKED

3. 生产报工表 (jsh_production_report)
   - 记录详细的生产过程信息
   - 支持进度报工、完工报工等多种类型
   - 包含工时统计、质量记录、费用计算
   - 支持照片上传和问题描述

4. 质量检验表 (jsh_quality_inspection)
   - 完整的质量管理体系
   - 支持多维度质量评分（外观、尺寸、颜色、质感、细节、整体效果）
   - 包含问题跟踪和改进建议
   - 自动计算合格率和综合评分

5. 物流追踪表 (jsh_logistics_tracking)
   - 全流程物流跟踪
   - 支持多种物流类型（原料入库、半成品出库、成品入库、成品出库）
   - 包含时间节点和异常处理
   - 状态流转：PENDING → SHIPPED → IN_TRANSIT → ARRIVED → RECEIVED

6. 工人技能表 (jsh_worker_skill)
   - 工人技能管理和评估
   - 支持多技能认证（掐丝点蓝、配饰制作、后工处理、质量检验）
   - 包含效率和质量系数
   - 支持小时工资和计件工资双重薪酬体系

7. 生产统计表 (jsh_production_statistics)
   - 多维度生产数据统计
   - 支持日/周/月/年统计
   - 包含效率、质量、成本分析
   - 自动计算各种统计指标

设计特点：
- 严格遵循jshERP多租户架构
- 完整的审计字段支持（create_time、create_user、update_time、update_user）
- 灵活的状态管理
- 丰富的关联关系
- 高效的索引设计
- 扩展性良好的字段设计
- 支持JSON格式存储复杂数据（照片、追踪信息等）

字段命名规范：
- 主键统一使用 id
- 外键使用 表名_id 格式
- 时间字段使用 _time 后缀
- 数量字段使用 _quantity 后缀
- 金额字段使用 _cost、_fee 后缀
- 比率字段使用 _rate 后缀
- 标记字段使用 _flag 后缀

索引设计原则：
- 主键自动创建聚集索引
- 外键字段建立非聚集索引
- 租户ID和删除标记建立复合索引
- 状态字段建立索引
- 时间字段建立索引
- 根据查询需求建立复合索引

数据类型选择：
- ID类型：bigint(20)
- 金额数量：decimal(24,6)
- 百分比：decimal(5,2)
- 评分：decimal(3,1)
- 字符串：varchar(长度)
- 长文本：text
- 日期时间：datetime
- 布尔值：tinyint(1)

多租户支持：
- 所有业务表包含 tenant_id 字段
- 查询时必须包含租户条件过滤
- 唯一约束包含租户ID
- 索引设计考虑租户隔离

审计支持：
- create_time：记录创建时间
- create_user：记录创建人
- update_time：记录更新时间
- update_user：记录更新人
- delete_flag：软删除标记

扩展性考虑：
- 预留remark字段用于备注
- 使用JSON格式存储复杂数据
- 状态字段使用varchar支持扩展
- 数值字段精度足够支持业务增长
*/
