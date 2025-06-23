-- jshERP智能生产管理模块 - 辅助功能表创建脚本
-- 版本: v1.0
-- 创建日期: 2025-06-22
-- 说明: 创建生产管理系统的辅助功能表

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- 1. 物流追踪表
-- =====================================================
DROP TABLE IF EXISTS `jsh_logistics_tracking`;
CREATE TABLE `jsh_logistics_tracking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tracking_number` varchar(50) NOT NULL COMMENT '快递单号',
  `order_id` bigint(20) COMMENT '订单ID',
  `order_number` varchar(50) COMMENT '订单号',
  `work_order_id` bigint(20) COMMENT '工单ID',
  `work_order_number` varchar(50) COMMENT '工单号',
  `logistics_company` varchar(50) NOT NULL COMMENT '物流公司',
  `logistics_company_code` varchar(20) COMMENT '物流公司编码',
  `sender_name` varchar(50) COMMENT '发件人姓名',
  `sender_phone` varchar(20) COMMENT '发件人电话',
  `sender_address` varchar(200) COMMENT '发件人地址',
  `recipient_name` varchar(50) NOT NULL COMMENT '收件人姓名',
  `recipient_phone` varchar(20) NOT NULL COMMENT '收件人电话',
  `recipient_address` varchar(200) NOT NULL COMMENT '收件人地址',
  `package_weight` decimal(10,3) COMMENT '包裹重量(kg)',
  `package_volume` decimal(10,3) COMMENT '包裹体积(m³)',
  `package_count` int(11) DEFAULT 1 COMMENT '包裹数量',
  `shipping_fee` decimal(24,6) COMMENT '运费',
  `insurance_fee` decimal(24,6) COMMENT '保险费',
  `total_fee` decimal(24,6) COMMENT '总费用',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待发货,PICKED_UP-已揽收,IN_TRANSIT-运输中,DELIVERED-已签收,EXCEPTION-异常',
  `ship_time` datetime COMMENT '发货时间',
  `pickup_time` datetime COMMENT '揽收时间',
  `delivery_time` datetime COMMENT '签收时间',
  `signed_by` varchar(50) COMMENT '签收人',
  `current_location` varchar(100) COMMENT '当前位置',
  `estimated_delivery` datetime COMMENT '预计送达时间',
  `delivery_type` varchar(20) COMMENT '配送方式：HOME-送货上门,PICKUP-自提,STATION-代收点',
  `special_instructions` text COMMENT '特殊说明',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tracking_number` (`tracking_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_status` (`status`),
  KEY `idx_logistics_company` (`logistics_company`),
  KEY `idx_ship_time` (`ship_time`),
  KEY `idx_delivery_time` (`delivery_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流追踪表';

-- =====================================================
-- 2. 物流轨迹记录表
-- =====================================================
DROP TABLE IF EXISTS `jsh_logistics_trace`;
CREATE TABLE `jsh_logistics_trace` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tracking_id` bigint(20) NOT NULL COMMENT '物流追踪ID',
  `tracking_number` varchar(50) NOT NULL COMMENT '快递单号',
  `trace_time` datetime NOT NULL COMMENT '轨迹时间',
  `location` varchar(100) COMMENT '所在地点',
  `description` varchar(200) NOT NULL COMMENT '轨迹描述',
  `operator` varchar(50) COMMENT '操作员',
  `status_code` varchar(20) COMMENT '状态码',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_tracking_id` (`tracking_id`),
  KEY `idx_tracking_number` (`tracking_number`),
  KEY `idx_trace_time` (`trace_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流轨迹记录表';

-- =====================================================
-- 3. 质检记录表
-- =====================================================
DROP TABLE IF EXISTS `jsh_quality_inspection`;
CREATE TABLE `jsh_quality_inspection` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inspection_number` varchar(50) NOT NULL COMMENT '质检编号',
  `work_order_id` bigint(20) COMMENT '工单ID',
  `work_order_number` varchar(50) COMMENT '工单号',
  `task_id` bigint(20) COMMENT '任务ID',
  `task_number` varchar(50) COMMENT '任务编号',
  `product_id` bigint(20) NOT NULL COMMENT '产品ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_model` varchar(50) COMMENT '产品型号',
  `inspection_type` varchar(20) NOT NULL COMMENT '质检类型：INCOMING-来料检验,PROCESS-过程检验,FINAL-最终检验,RANDOM-抽检',
  `inspection_method` varchar(50) COMMENT '检验方法',
  `inspection_standard` varchar(100) COMMENT '检验标准',
  `inspection_quantity` decimal(24,6) NOT NULL COMMENT '检验数量',
  `passed_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `rejected_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `unit_name` varchar(10) NOT NULL COMMENT '单位',
  `pass_rate` decimal(5,2) COMMENT '合格率',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待检验,IN_PROGRESS-检验中,COMPLETED-已完成,REJECTED-不合格',
  `plan_inspection_time` datetime COMMENT '计划检验时间',
  `actual_start_time` datetime COMMENT '实际开始时间',
  `actual_end_time` datetime COMMENT '实际结束时间',
  `inspector_id` bigint(20) COMMENT '质检员ID',
  `inspector_name` varchar(50) COMMENT '质检员姓名',
  `quality_grade` varchar(10) COMMENT '质量等级：A-优秀,B-良好,C-合格,D-不合格',
  `overall_result` varchar(10) COMMENT '总体结果：PASS-合格,FAIL-不合格,CONDITIONAL-有条件通过',
  `overall_score` decimal(5,2) COMMENT '总体评分',
  `appearance_score` decimal(5,2) COMMENT '外观评分',
  `size_score` decimal(5,2) COMMENT '尺寸评分',
  `color_score` decimal(5,2) COMMENT '颜色评分',
  `texture_score` decimal(5,2) COMMENT '质感评分',
  `detail_score` decimal(5,2) COMMENT '细节评分',
  `defect_description` text COMMENT '缺陷描述',
  `improvement_suggestion` text COMMENT '改进建议',
  `inspection_notes` text COMMENT '检验说明',
  `photo_urls` text COMMENT '照片URL列表（JSON格式）',
  `attachment_urls` text COMMENT '附件URL列表（JSON格式）',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inspection_number` (`inspection_number`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_inspection_type` (`inspection_type`),
  KEY `idx_inspector_id` (`inspector_id`),
  KEY `idx_overall_result` (`overall_result`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检记录表';

-- =====================================================
-- 4. 质检标准表
-- =====================================================
DROP TABLE IF EXISTS `jsh_quality_standard`;
CREATE TABLE `jsh_quality_standard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `standard_code` varchar(50) NOT NULL COMMENT '标准编码',
  `standard_name` varchar(100) NOT NULL COMMENT '标准名称',
  `standard_type` varchar(20) NOT NULL COMMENT '标准类型：PRODUCT-产品标准,PROCESS-工艺标准,MATERIAL-材料标准',
  `product_category` varchar(50) COMMENT '适用产品类别',
  `inspection_items` text COMMENT '检验项目（JSON格式）',
  `quality_criteria` text COMMENT '质量标准（JSON格式）',
  `measurement_methods` text COMMENT '测量方法',
  `acceptance_criteria` text COMMENT '验收标准',
  `rejection_criteria` text COMMENT '拒收标准',
  `sampling_plan` text COMMENT '抽样方案',
  `required_tools` varchar(200) COMMENT '所需工具',
  `reference_documents` varchar(200) COMMENT '参考文件',
  `status` varchar(10) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用,INACTIVE-停用,DRAFT-草稿',
  `version` varchar(10) NOT NULL DEFAULT '1.0' COMMENT '版本号',
  `effective_date` date COMMENT '生效日期',
  `expiry_date` date COMMENT '失效日期',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_standard_code` (`standard_code`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_standard_type` (`standard_type`),
  KEY `idx_product_category` (`product_category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检标准表';

-- =====================================================
-- 5. 生产统计表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_statistics`;
CREATE TABLE `jsh_production_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` varchar(20) NOT NULL COMMENT '统计类型：DAILY-日统计,WEEKLY-周统计,MONTHLY-月统计,YEARLY-年统计',
  `stat_dimension` varchar(20) NOT NULL COMMENT '统计维度：OVERALL-整体,WORKER-工人,PRODUCT-产品,DEPARTMENT-部门',
  `dimension_value` varchar(100) COMMENT '维度值',
  `work_order_count` int(11) DEFAULT 0 COMMENT '工单数量',
  `task_count` int(11) DEFAULT 0 COMMENT '任务数量',
  `completed_work_orders` int(11) DEFAULT 0 COMMENT '完成工单数',
  `completed_tasks` int(11) DEFAULT 0 COMMENT '完成任务数',
  `production_quantity` decimal(24,6) DEFAULT 0 COMMENT '生产数量',
  `qualified_quantity` decimal(24,6) DEFAULT 0 COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT 0 COMMENT '不合格数量',
  `total_work_hours` decimal(10,2) DEFAULT 0 COMMENT '总工时',
  `total_labor_cost` decimal(24,6) DEFAULT 0 COMMENT '总人工成本',
  `total_material_cost` decimal(24,6) DEFAULT 0 COMMENT '总材料成本',
  `total_production_cost` decimal(24,6) DEFAULT 0 COMMENT '总生产成本',
  `completion_rate` decimal(5,2) DEFAULT 0 COMMENT '完成率',
  `quality_pass_rate` decimal(5,2) DEFAULT 0 COMMENT '质量合格率',
  `efficiency_rate` decimal(5,2) DEFAULT 0 COMMENT '效率率',
  `on_time_delivery_rate` decimal(5,2) DEFAULT 0 COMMENT '准时交付率',
  `average_cycle_time` decimal(10,2) DEFAULT 0 COMMENT '平均周期时间',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_unique` (`stat_date`, `stat_type`, `stat_dimension`, `dimension_value`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`),
  KEY `idx_stat_dimension` (`stat_dimension`),
  KEY `idx_dimension_value` (`dimension_value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产统计表';

-- =====================================================
-- 6. 系统配置表
-- =====================================================
DROP TABLE IF EXISTS `jsh_production_config`;
CREATE TABLE `jsh_production_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(20) NOT NULL COMMENT '配置类型：SYSTEM-系统配置,BUSINESS-业务配置,UI-界面配置',
  `config_category` varchar(50) COMMENT '配置分类',
  `config_description` varchar(200) COMMENT '配置描述',
  `data_type` varchar(20) DEFAULT 'STRING' COMMENT '数据类型：STRING-字符串,NUMBER-数字,BOOLEAN-布尔,JSON-JSON对象',
  `default_value` text COMMENT '默认值',
  `is_required` varchar(1) DEFAULT '0' COMMENT '是否必需：0-否,1-是',
  `is_editable` varchar(1) DEFAULT '1' COMMENT '是否可编辑：0-否,1-是',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序顺序',
  `status` varchar(10) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用,INACTIVE-停用',
  `remark` varchar(500) COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) NOT NULL DEFAULT '0' COMMENT '删除标记：0-正常,1-删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_config_type` (`config_type`),
  KEY `idx_config_category` (`config_category`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产管理系统配置表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入说明
SELECT '辅助功能表创建完成！' as message;
SELECT '包含表：物流追踪表、物流轨迹记录表、质检记录表、质检标准表、生产统计表、系统配置表' as tables;
