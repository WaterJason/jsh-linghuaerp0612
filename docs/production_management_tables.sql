-- ========================================
-- jshERP 生产管理模块数据库表结构
-- 创建时间: 2025-06-21
-- 说明: 包含主生产订单表、工单表、工单物料消耗表
-- ========================================

-- ----------------------------
-- 1. 主生产订单表 (jsh_production_order)
-- 用于存放与销售订单关联的主生产订单
-- ----------------------------
DROP TABLE IF EXISTS `jsh_production_order`;
CREATE TABLE `jsh_production_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  -- 业务字段
  `order_no` VARCHAR(50) NOT NULL COMMENT '生产订单号，格式：PO+yyyyMMdd+4位序号',
  `sales_order_id` BIGINT(20) DEFAULT NULL COMMENT '原始销售订单ID，关联jsh_depot_head表',
  `material_id` BIGINT(20) NOT NULL COMMENT '产品ID，关联jsh_material表',
  `quantity` DECIMAL(24,6) NOT NULL DEFAULT 0.000000 COMMENT '生产数量',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '订单状态：PENDING-待开始，IN_PROGRESS-进行中，COMPLETED-已完成，CANCELLED-已取消',
  `delivery_date` DATETIME DEFAULT NULL COMMENT '交付期限',
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '备注',
  
  -- 成本统计字段
  `total_cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '总成本',
  `material_cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '物料成本',
  `labor_cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '人工成本',
  
  -- 进度跟踪字段
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `progress` DECIMAL(5,2) DEFAULT 0.00 COMMENT '完成进度百分比(0-100)',
  
  -- 多租户字段（必需）
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no_tenant` (`order_no`, `tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_delete_flag` (`delete_flag`),
  KEY `idx_sales_order_id` (`sales_order_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_status` (`status`),
  KEY `idx_delivery_date` (`delivery_date`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主生产订单表';

-- ----------------------------
-- 2. 工单表 (jsh_work_order)
-- 生产订单下的具体工单表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_work_order`;
CREATE TABLE `jsh_work_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  -- 业务字段
  `work_order_no` VARCHAR(50) NOT NULL COMMENT '工单号，格式：WO+yyyyMMdd+4位序号',
  `production_order_id` BIGINT(20) NOT NULL COMMENT '关联生产订单ID',
  `work_type` VARCHAR(20) NOT NULL COMMENT '工单类型：CLOISONNE-掐丝点蓝，ACCESSORY-配饰制作，POST_PROCESS-后工',
  `handler_id` BIGINT(20) DEFAULT NULL COMMENT '处理人ID，关联jsh_user表',
  `handler_name` VARCHAR(50) DEFAULT NULL COMMENT '处理人姓名（冗余字段）',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '工单状态：PENDING-待处理，IN_PROGRESS-进行中，COMPLETED-已完成，CANCELLED-已取消',
  
  -- 成本字段
  `cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '工单成本',
  `labor_cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '人工成本',
  `material_cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '物料成本',
  
  -- 时间字段
  `start_time` DATETIME DEFAULT NULL COMMENT '开始时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `estimated_hours` DECIMAL(8,2) DEFAULT 0.00 COMMENT '预计工时（小时）',
  `actual_hours` DECIMAL(8,2) DEFAULT 0.00 COMMENT '实际工时（小时）',
  
  -- 附件字段
  `complete_images` TEXT DEFAULT NULL COMMENT '完工图片URL列表，JSON格式存储',
  `process_images` TEXT DEFAULT NULL COMMENT '过程图片URL列表，JSON格式存储',
  `logistics_no` VARCHAR(100) DEFAULT NULL COMMENT '物流单号',
  
  -- 备注字段
  `remark` VARCHAR(1000) DEFAULT NULL COMMENT '工单备注',
  `quality_notes` VARCHAR(500) DEFAULT NULL COMMENT '质检备注',
  
  -- 多租户字段（必需）
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_order_no_tenant` (`work_order_no`, `tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_delete_flag` (`delete_flag`),
  KEY `idx_production_order_id` (`production_order_id`),
  KEY `idx_work_type` (`work_type`),
  KEY `idx_handler_id` (`handler_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- ----------------------------
-- 3. 工单物料消耗表 (jsh_work_order_item)
-- 记录每个工单消耗的物料
-- ----------------------------
DROP TABLE IF EXISTS `jsh_work_order_item`;
CREATE TABLE `jsh_work_order_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  
  -- 业务字段
  `work_order_id` BIGINT(20) NOT NULL COMMENT '关联工单ID',
  `material_id` BIGINT(20) NOT NULL COMMENT '物料ID，关联jsh_material表',
  `material_name` VARCHAR(100) DEFAULT NULL COMMENT '物料名称（冗余字段）',
  `material_unit` VARCHAR(20) DEFAULT NULL COMMENT '物料单位',
  `quantity` DECIMAL(24,6) NOT NULL DEFAULT 0.000000 COMMENT '消耗数量',
  `unit_price` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '单价',
  `total_price` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '总价',
  
  -- 物料类型字段
  `item_type` VARCHAR(20) NOT NULL COMMENT '物料类型：RAW_MATERIAL-原材料，SEMI_FINISHED-半成品，FINISHED-成品',
  
  -- 库存相关字段
  `depot_id` BIGINT(20) DEFAULT NULL COMMENT '出库仓库ID，关联jsh_depot表',
  `batch_number` VARCHAR(100) DEFAULT NULL COMMENT '批次号',
  `serial_number` VARCHAR(100) DEFAULT NULL COMMENT '序列号',
  
  -- 备注字段
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  
  -- 多租户字段（必需）
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 软删除字段（必需）
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',
  
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_delete_flag` (`delete_flag`),
  KEY `idx_work_order_id` (`work_order_id`),
  KEY `idx_material_id` (`material_id`),
  KEY `idx_item_type` (`item_type`),
  KEY `idx_depot_id` (`depot_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单物料消耗表';

-- ----------------------------
-- 创建外键约束（可选，根据实际需要决定是否启用）
-- ----------------------------
-- ALTER TABLE `jsh_production_order` ADD CONSTRAINT `fk_production_order_sales` FOREIGN KEY (`sales_order_id`) REFERENCES `jsh_depot_head` (`id`);
-- ALTER TABLE `jsh_production_order` ADD CONSTRAINT `fk_production_order_material` FOREIGN KEY (`material_id`) REFERENCES `jsh_material` (`id`);
-- ALTER TABLE `jsh_work_order` ADD CONSTRAINT `fk_work_order_production` FOREIGN KEY (`production_order_id`) REFERENCES `jsh_production_order` (`id`);
-- ALTER TABLE `jsh_work_order` ADD CONSTRAINT `fk_work_order_handler` FOREIGN KEY (`handler_id`) REFERENCES `jsh_user` (`id`);
-- ALTER TABLE `jsh_work_order_item` ADD CONSTRAINT `fk_work_order_item_work_order` FOREIGN KEY (`work_order_id`) REFERENCES `jsh_work_order` (`id`);
-- ALTER TABLE `jsh_work_order_item` ADD CONSTRAINT `fk_work_order_item_material` FOREIGN KEY (`material_id`) REFERENCES `jsh_material` (`id`);
-- ALTER TABLE `jsh_work_order_item` ADD CONSTRAINT `fk_work_order_item_depot` FOREIGN KEY (`depot_id`) REFERENCES `jsh_depot` (`id`);

-- ----------------------------
-- 初始化数据示例（可选）
-- ----------------------------
-- INSERT INTO `jsh_production_order` (`order_no`, `sales_order_id`, `material_id`, `quantity`, `status`, `delivery_date`, `remark`, `tenant_id`) 
-- VALUES ('PO202506210001', 1, 1, 10.000000, 'PENDING', '2025-07-01 00:00:00', '测试生产订单', 0);
