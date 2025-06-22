-- =====================================================
-- jshERP 智能生产管理模块数据库表结构设计
-- 基于商品管理模块架构，支持多租户和完整审计
-- 创建时间：2025-06-22
-- =====================================================

-- 1. 生产工单表 (jsh_production_work_order)
-- 用于管理生产工单，支持多种制作模式和优先级
DROP TABLE IF EXISTS `jsh_production_work_order`;
CREATE TABLE `jsh_production_work_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `work_order_name` varchar(100) NOT NULL COMMENT '工单名称',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID，关联jsh_material表',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_spec` varchar(200) DEFAULT NULL COMMENT '产品规格',
  `production_mode` varchar(20) NOT NULL DEFAULT 'STOCK_DRIVEN' COMMENT '制作模式：ORDER_DRIVEN(订单驱动)、STOCK_DRIVEN(库存驱动)、AUTONOMOUS_PLAN(自主计划)、EMERGENCY(紧急制作)',
  `production_type` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '制作类型：NORMAL(正常制作)、REWORK(返工)、SAMPLE(样品)、URGENT(紧急)',
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
  `estimated_hours` decimal(10,2) DEFAULT 0 COMMENT '预估工时',
  `actual_hours` decimal(10,2) DEFAULT 0 COMMENT '实际工时',
  `quality_standard` text DEFAULT NULL COMMENT '质量标准',
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
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_plan_start_time` (`plan_start_time`),
  KEY `idx_plan_end_time` (`plan_end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工单表';

-- 2. 生产任务表 (jsh_production_task)
-- 用于管理具体的生产任务，一个工单可以分解为多个任务
DROP TABLE IF EXISTS `jsh_production_task`;
CREATE TABLE `jsh_production_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID，关联jsh_production_work_order表',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `task_type` varchar(20) NOT NULL DEFAULT 'PRODUCTION' COMMENT '任务类型：PRODUCTION(生产)、POLISHING(抛光)、PAINTING(上色)、ASSEMBLY(组装)、PACKAGING(包装)、QUALITY_CHECK(质检)',
  `process_step` varchar(50) DEFAULT NULL COMMENT '工序步骤',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
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
  `worker_specialty` varchar(50) DEFAULT NULL COMMENT '工人专业技能',
  `assign_time` datetime DEFAULT NULL COMMENT '派单时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `plan_start_time` datetime DEFAULT NULL COMMENT '计划开始时间',
  `plan_end_time` datetime DEFAULT NULL COMMENT '计划完成时间',
  `estimated_hours` decimal(10,2) DEFAULT 0 COMMENT '预估工时',
  `actual_hours` decimal(10,2) DEFAULT 0 COMMENT '实际工时',
  `unit_fee` decimal(24,6) DEFAULT 0 COMMENT '单价工费',
  `total_fee` decimal(24,6) DEFAULT 0 COMMENT '总工费',
  `quality_level` varchar(20) DEFAULT NULL COMMENT '质量等级：EXCELLENT(优秀)、GOOD(良好)、AVERAGE(一般)、POOR(较差)',
  `quality_score` decimal(3,1) DEFAULT NULL COMMENT '质量评分(1-5分)',
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
  KEY `idx_product_id` (`product_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_task_type` (`task_type`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_assign_time` (`assign_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产任务表';

-- 3. 生产报工表 (jsh_production_report)
-- 用于记录生产过程中的报工信息
DROP TABLE IF EXISTS `jsh_production_report`;
CREATE TABLE `jsh_production_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `report_number` varchar(50) NOT NULL COMMENT '报工编号',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID，关联jsh_production_task表',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `report_type` varchar(20) NOT NULL DEFAULT 'PROGRESS' COMMENT '报工类型：PROGRESS(进度报工)、COMPLETE(完工报工)、PAUSE(暂停报工)、RESUME(恢复报工)',
  `report_time` datetime NOT NULL COMMENT '报工时间',
  `work_date` date NOT NULL COMMENT '工作日期',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `work_hours` decimal(10,2) NOT NULL DEFAULT 0 COMMENT '工作时长(小时)',
  `completed_quantity` decimal(24,6) NOT NULL DEFAULT 0 COMMENT '完成数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `unit_name` varchar(20) DEFAULT NULL COMMENT '单位名称',
  `quality_level` varchar(20) DEFAULT NULL COMMENT '质量等级：EXCELLENT(优秀)、GOOD(良好)、AVERAGE(一般)、POOR(较差)',
  `work_content` text DEFAULT NULL COMMENT '工作内容描述',
  `problem_description` text DEFAULT NULL COMMENT '问题描述',
  `improvement_suggestion` text DEFAULT NULL COMMENT '改进建议',
  `work_photos` text DEFAULT NULL COMMENT '工作照片(JSON格式存储)',
  `unit_fee` decimal(24,6) DEFAULT 0 COMMENT '单价工费',
  `total_fee` decimal(24,6) DEFAULT 0 COMMENT '本次工费',
  `is_completed` tinyint(1) DEFAULT 0 COMMENT '是否完成：0否 1是',
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
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_report_time` (`report_time`),
  KEY `idx_work_date` (`work_date`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产报工表';

-- 4. 质量检验表 (jsh_quality_inspection)
-- 用于记录质量检验信息
DROP TABLE IF EXISTS `jsh_quality_inspection`;
CREATE TABLE `jsh_quality_inspection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inspection_number` varchar(50) NOT NULL COMMENT '质检编号',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID，关联jsh_production_task表',
  `task_number` varchar(50) NOT NULL COMMENT '任务编号',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `inspector_id` bigint(20) NOT NULL COMMENT '质检员ID',
  `inspector_name` varchar(50) NOT NULL COMMENT '质检员姓名',
  `inspection_time` datetime NOT NULL COMMENT '质检时间',
  `inspection_type` varchar(20) NOT NULL DEFAULT 'FINAL' COMMENT '质检类型：INCOMING(来料质检)、PROCESS(过程质检)、FINAL(最终质检)、REWORK(返工质检)',
  `inspection_quantity` decimal(24,6) NOT NULL COMMENT '质检数量',
  `qualified_quantity` decimal(24,6) NOT NULL DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) NOT NULL DEFAULT 0 COMMENT '不合格数量',
  `unit_name` varchar(20) DEFAULT NULL COMMENT '单位名称',
  `qualification_rate` decimal(5,2) DEFAULT 0 COMMENT '合格率(%)',
  `overall_result` varchar(20) NOT NULL COMMENT '总体结果：PASS(合格)、FAIL(不合格)、REWORK(需返工)',
  `quality_grade` varchar(10) DEFAULT NULL COMMENT '质量等级：A+、A、B、C、D',
  `overall_score` decimal(3,1) DEFAULT NULL COMMENT '综合评分(1-5分)',

  -- 质检项目评分
  `appearance_score` decimal(3,1) DEFAULT NULL COMMENT '外观质量评分',
  `size_score` decimal(3,1) DEFAULT NULL COMMENT '尺寸精度评分',
  `color_score` decimal(3,1) DEFAULT NULL COMMENT '颜色效果评分',
  `texture_score` decimal(3,1) DEFAULT NULL COMMENT '表面质感评分',
  `detail_score` decimal(3,1) DEFAULT NULL COMMENT '细节处理评分',
  `overall_effect_score` decimal(3,1) DEFAULT NULL COMMENT '整体效果评分',

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
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_inspector_id` (`inspector_id`),
  KEY `idx_inspection_time` (`inspection_time`),
  KEY `idx_overall_result` (`overall_result`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质量检验表';

-- 5. 物流追踪表 (jsh_logistics_tracking)
-- 用于记录物流追踪信息
DROP TABLE IF EXISTS `jsh_logistics_tracking`;
CREATE TABLE `jsh_logistics_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tracking_number` varchar(50) NOT NULL COMMENT '追踪编号',
  `work_order_id` bigint(20) NOT NULL COMMENT '工单ID，关联jsh_production_work_order表',
  `work_order_number` varchar(50) NOT NULL COMMENT '工单编号',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID，关联jsh_production_task表',
  `task_number` varchar(50) DEFAULT NULL COMMENT '任务编号',
  `logistics_type` varchar(20) NOT NULL COMMENT '物流类型：MATERIAL_IN(原料入库)、SEMI_PRODUCT_TRANSFER(半成品转移)、FINISHED_PRODUCT_OUT(成品出库)、RETURN(退货)',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `quantity` decimal(24,6) NOT NULL COMMENT '数量',
  `unit_name` varchar(20) DEFAULT NULL COMMENT '单位名称',
  `from_location` varchar(100) DEFAULT NULL COMMENT '起始位置',
  `to_location` varchar(100) DEFAULT NULL COMMENT '目标位置',
  `carrier_name` varchar(100) DEFAULT NULL COMMENT '承运人/物流公司',
  `carrier_contact` varchar(50) DEFAULT NULL COMMENT '承运人联系方式',
  `tracking_code` varchar(100) DEFAULT NULL COMMENT '物流单号',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING(待发货)、IN_TRANSIT(运输中)、DELIVERED(已送达)、RECEIVED(已收货)、EXCEPTION(异常)',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `estimated_arrival_time` datetime DEFAULT NULL COMMENT '预计到达时间',
  `actual_arrival_time` datetime DEFAULT NULL COMMENT '实际到达时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人姓名',
  `receiver_contact` varchar(50) DEFAULT NULL COMMENT '收货人联系方式',
  `logistics_cost` decimal(24,6) DEFAULT 0 COMMENT '物流费用',
  `tracking_info` text DEFAULT NULL COMMENT '追踪信息(JSON格式存储)',
  `exception_description` text DEFAULT NULL COMMENT '异常描述',
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
  KEY `idx_task_id` (`task_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_logistics_type` (`logistics_type`),
  KEY `idx_status` (`status`),
  KEY `idx_ship_time` (`ship_time`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流追踪表';

-- 6. 工人技能表 (jsh_worker_skill)
-- 用于记录工人的技能信息
DROP TABLE IF EXISTS `jsh_worker_skill`;
CREATE TABLE `jsh_worker_skill` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID，关联jsh_user表',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `skill_type` varchar(50) NOT NULL COMMENT '技能类型：CLOISONNE_WIRE(掐丝)、CLOISONNE_ENAMEL(点蓝)、POLISHING(抛光)、PAINTING(上色)、ASSEMBLY(组装)、PACKAGING(包装)、QUALITY_CHECK(质检)',
  `skill_level` varchar(20) NOT NULL COMMENT '技能等级：BEGINNER(新手)、INTERMEDIATE(中级)、ADVANCED(高级)、EXPERT(专家)、MASTER(大师)',
  `skill_score` decimal(3,1) DEFAULT NULL COMMENT '技能评分(1-5分)',
  `certification_level` varchar(50) DEFAULT NULL COMMENT '认证等级',
  `certification_date` date DEFAULT NULL COMMENT '认证日期',
  `experience_years` decimal(4,1) DEFAULT 0 COMMENT '经验年限',
  `hourly_rate` decimal(24,6) DEFAULT 0 COMMENT '小时工资',
  `piece_rate` decimal(24,6) DEFAULT 0 COMMENT '计件工资',
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
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_skill_type` (`skill_type`),
  KEY `idx_skill_level` (`skill_level`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工人技能表';

-- 7. 生产统计表 (jsh_production_statistics)
-- 用于记录生产统计数据
DROP TABLE IF EXISTS `jsh_production_statistics`;
CREATE TABLE `jsh_production_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` varchar(20) NOT NULL COMMENT '统计类型：DAILY(日统计)、WEEKLY(周统计)、MONTHLY(月统计)、YEARLY(年统计)',
  `workshop_id` bigint(20) DEFAULT NULL COMMENT '车间ID',
  `workshop_name` varchar(50) DEFAULT NULL COMMENT '车间名称',
  `worker_id` bigint(20) DEFAULT NULL COMMENT '工人ID',
  `worker_name` varchar(50) DEFAULT NULL COMMENT '工人姓名',
  `product_id` bigint(20) DEFAULT NULL COMMENT '产品ID',
  `product_name` varchar(100) DEFAULT NULL COMMENT '产品名称',

  -- 工单统计
  `total_work_orders` int(11) DEFAULT 0 COMMENT '总工单数',
  `completed_work_orders` int(11) DEFAULT 0 COMMENT '完成工单数',
  `pending_work_orders` int(11) DEFAULT 0 COMMENT '待处理工单数',
  `cancelled_work_orders` int(11) DEFAULT 0 COMMENT '取消工单数',

  -- 任务统计
  `total_tasks` int(11) DEFAULT 0 COMMENT '总任务数',
  `completed_tasks` int(11) DEFAULT 0 COMMENT '完成任务数',
  `in_progress_tasks` int(11) DEFAULT 0 COMMENT '进行中任务数',
  `pending_tasks` int(11) DEFAULT 0 COMMENT '待派单任务数',

  -- 产量统计
  `planned_quantity` decimal(24,6) DEFAULT 0 COMMENT '计划产量',
  `actual_quantity` decimal(24,6) DEFAULT 0 COMMENT '实际产量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格产量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格产量',

  -- 时间统计
  `planned_hours` decimal(10,2) DEFAULT 0 COMMENT '计划工时',
  `actual_hours` decimal(10,2) DEFAULT 0 COMMENT '实际工时',
  `overtime_hours` decimal(10,2) DEFAULT 0 COMMENT '加班工时',

  -- 效率统计
  `production_efficiency` decimal(5,2) DEFAULT 0 COMMENT '生产效率(%)',
  `quality_rate` decimal(5,2) DEFAULT 0 COMMENT '质量合格率(%)',
  `on_time_delivery_rate` decimal(5,2) DEFAULT 0 COMMENT '按时交付率(%)',

  -- 成本统计
  `material_cost` decimal(24,6) DEFAULT 0 COMMENT '材料成本',
  `labor_cost` decimal(24,6) DEFAULT 0 COMMENT '人工成本',
  `overhead_cost` decimal(24,6) DEFAULT 0 COMMENT '制造费用',
  `total_cost` decimal(24,6) DEFAULT 0 COMMENT '总成本',

  `remark` text DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记：0未删除 1已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date_type_tenant` (`stat_date`, `stat_type`, `workshop_id`, `worker_id`, `product_id`, `tenant_id`, `delete_flag`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`),
  KEY `idx_workshop_id` (`workshop_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产统计表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 初始化工人技能数据（示例数据）
INSERT INTO `jsh_worker_skill` (`worker_id`, `worker_name`, `skill_type`, `skill_level`, `skill_score`, `experience_years`, `hourly_rate`, `piece_rate`, `efficiency_rate`, `quality_rate`, `tenant_id`, `create_user`) VALUES
(1, '李师傅', 'CLOISONNE_WIRE', 'EXPERT', 4.8, 8.5, 35.00, 25.00, 120.0, 98.5, 1, 1),
(1, '李师傅', 'POLISHING', 'ADVANCED', 4.2, 5.0, 30.00, 20.00, 110.0, 95.0, 1, 1),
(2, '王师傅', 'CLOISONNE_ENAMEL', 'EXPERT', 4.9, 10.0, 40.00, 30.00, 125.0, 99.0, 1, 1),
(2, '王师傅', 'ASSEMBLY', 'ADVANCED', 4.3, 6.0, 32.00, 22.00, 115.0, 96.0, 1, 1),
(3, '张师傅', 'QUALITY_CHECK', 'EXPERT', 4.7, 12.0, 38.00, 28.00, 105.0, 99.5, 1, 1),
(3, '张师傅', 'PACKAGING', 'ADVANCED', 4.0, 4.0, 25.00, 18.00, 100.0, 92.0, 1, 1),
(4, '赵师傅', 'PAINTING', 'INTERMEDIATE', 3.8, 2.5, 28.00, 15.00, 95.0, 88.0, 1, 1),
(4, '赵师傅', 'POLISHING', 'BEGINNER', 3.2, 1.0, 22.00, 12.00, 85.0, 85.0, 1, 1);

-- =====================================================
-- 索引优化建议
-- =====================================================

-- 为提高查询性能，建议创建以下复合索引：

-- 生产工单表复合索引
-- ALTER TABLE `jsh_production_work_order` ADD INDEX `idx_status_priority_tenant` (`status`, `priority`, `tenant_id`);
-- ALTER TABLE `jsh_production_work_order` ADD INDEX `idx_product_status_tenant` (`product_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_production_work_order` ADD INDEX `idx_plan_time_status` (`plan_start_time`, `plan_end_time`, `status`);

-- 生产任务表复合索引
-- ALTER TABLE `jsh_production_task` ADD INDEX `idx_worker_status_tenant` (`worker_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_production_task` ADD INDEX `idx_workorder_status_tenant` (`work_order_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_production_task` ADD INDEX `idx_type_status_priority` (`task_type`, `status`, `priority`);

-- 生产报工表复合索引
-- ALTER TABLE `jsh_production_report` ADD INDEX `idx_worker_date_tenant` (`worker_id`, `work_date`, `tenant_id`);
-- ALTER TABLE `jsh_production_report` ADD INDEX `idx_task_report_time` (`task_id`, `report_time`);

-- 质量检验表复合索引
-- ALTER TABLE `jsh_quality_inspection` ADD INDEX `idx_product_result_tenant` (`product_id`, `overall_result`, `tenant_id`);
-- ALTER TABLE `jsh_quality_inspection` ADD INDEX `idx_inspector_time_tenant` (`inspector_id`, `inspection_time`, `tenant_id`);

-- 物流追踪表复合索引
-- ALTER TABLE `jsh_logistics_tracking` ADD INDEX `idx_workorder_status_tenant` (`work_order_id`, `status`, `tenant_id`);
-- ALTER TABLE `jsh_logistics_tracking` ADD INDEX `idx_type_status_time` (`logistics_type`, `status`, `ship_time`);

-- =====================================================
-- 数据库表结构说明
-- =====================================================

/*
表结构设计说明：

1. 生产工单表 (jsh_production_work_order)
   - 主要用于管理生产工单，支持多种制作模式
   - 包含完整的时间管理、成本管理、状态管理
   - 支持与销售订单、库存计划的关联

2. 生产任务表 (jsh_production_task)
   - 工单的具体执行单元，支持任务分解
   - 包含工人分配、进度跟踪、质量管理
   - 支持多种任务类型和工序步骤

3. 生产报工表 (jsh_production_report)
   - 记录详细的生产过程信息
   - 支持进度报工、完工报工等多种类型
   - 包含工时统计、质量记录、费用计算

4. 质量检验表 (jsh_quality_inspection)
   - 完整的质量管理体系
   - 支持多维度质量评分
   - 包含问题跟踪和改进建议

5. 物流追踪表 (jsh_logistics_tracking)
   - 全流程物流跟踪
   - 支持多种物流类型
   - 包含时间节点和异常处理

6. 工人技能表 (jsh_worker_skill)
   - 工人技能管理和评估
   - 支持多技能认证
   - 包含效率和质量系数

7. 生产统计表 (jsh_production_statistics)
   - 多维度生产数据统计
   - 支持日/周/月/年统计
   - 包含效率、质量、成本分析

设计特点：
- 严格遵循jshERP多租户架构
- 完整的审计字段支持
- 灵活的状态管理
- 丰富的关联关系
- 高效的索引设计
- 扩展性良好的字段设计
*/
