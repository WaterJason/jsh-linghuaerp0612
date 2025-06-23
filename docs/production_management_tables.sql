-- ========================================
-- jshERP 生产管理模块数据库表结构
-- 创建时间: 2025-06-21
-- 说明: 严格按照jshERP数据库规范创建生产管理相关表
-- ========================================

-- ----------------------------
-- 1. 主生产订单表 (jsh_production_order)
-- 用于存放与销售订单关联的主生产订单
-- ----------------------------
DROP TABLE IF EXISTS `jsh_production_order`;
CREATE TABLE `jsh_production_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 业务字段
  `order_number` VARCHAR(50) NOT NULL COMMENT '订单号，唯一标识',
  `original_sale_order_id` BIGINT(20) DEFAULT NULL COMMENT '原始销售订单ID，关联jsh_depot_head表',
  `material_id` BIGINT(20) NOT NULL COMMENT '产品ID，关联jsh_material表',
  `quantity` DECIMAL(24,6) NOT NULL DEFAULT 0.000000 COMMENT '生产数量',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待生产，IN_PROGRESS-生产中，COMPLETED-已完成，CANCELLED-已取消',
  `delivery_deadline` DATETIME DEFAULT NULL COMMENT '交付期限',
  `remark` TEXT DEFAULT NULL COMMENT '备注',

  -- 多租户字段（必需）
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',

  -- 软删除字段（必需）
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',

  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_number_tenant` (`order_number`, `tenant_id`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_delete_flag` (`delete_flag`),
  INDEX `idx_original_sale_order_id` (`original_sale_order_id`),
  INDEX `idx_material_id` (`material_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_delivery_deadline` (`delivery_deadline`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='主生产订单表';

-- ----------------------------
-- 2. 工单表 (jsh_work_order)
-- 生产订单下的具体工单表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_work_order`;
CREATE TABLE `jsh_work_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',

  -- 业务字段
  `work_order_number` VARCHAR(50) NOT NULL COMMENT '工单号，唯一标识',
  `production_order_id` BIGINT(20) NOT NULL COMMENT '关联主生产订单ID',
  `work_type` VARCHAR(20) NOT NULL COMMENT '工单类型：CLOISONNE-掐丝点蓝，ACCESSORY-配饰制作，POST_PROCESS-后工',
  `handler_user_id` BIGINT(20) DEFAULT NULL COMMENT '处理人ID，关联jsh_user表',
  `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待开始，IN_PROGRESS-进行中，COMPLETED-已完成，PAUSED-已暂停',
  `cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '成本',
  `completion_image_url` TEXT DEFAULT NULL COMMENT '完工图片URL',
  `logistics_number` VARCHAR(100) DEFAULT NULL COMMENT '物流单号',

  -- 多租户字段（必需）
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',

  -- 软删除字段（必需）
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',

  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',

  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_work_order_number_tenant` (`work_order_number`, `tenant_id`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_delete_flag` (`delete_flag`),
  INDEX `idx_production_order_id` (`production_order_id`),
  INDEX `idx_work_type` (`work_type`),
  INDEX `idx_handler_user_id` (`handler_user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_create_time` (`create_time`)
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
  `quantity` DECIMAL(24,6) NOT NULL DEFAULT 0.000000 COMMENT '消耗数量',
  `material_type` VARCHAR(20) NOT NULL COMMENT '物料类型：RAW_MATERIAL-原材料，SEMI_FINISHED-半成品',
  `unit_cost` DECIMAL(24,6) DEFAULT 0.000000 COMMENT '单位成本',

  -- 多租户字段（必需）
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',

  -- 软删除字段（必需）
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-存在，1-删除',

  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) DEFAULT NULL COMMENT '创建人ID',
  `update_user` BIGINT(20) DEFAULT NULL COMMENT '更新人ID',

  PRIMARY KEY (`id`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_delete_flag` (`delete_flag`),
  INDEX `idx_work_order_id` (`work_order_id`),
  INDEX `idx_material_id` (`material_id`),
  INDEX `idx_material_type` (`material_type`),
  INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单物料消耗表';

-- ----------------------------
-- 创建外键约束（可选，根据实际需要决定是否启用）
-- ----------------------------
-- ALTER TABLE `jsh_production_order` ADD CONSTRAINT `fk_production_order_sale` FOREIGN KEY (`original_sale_order_id`) REFERENCES `jsh_depot_head` (`id`);
-- ALTER TABLE `jsh_production_order` ADD CONSTRAINT `fk_production_order_material` FOREIGN KEY (`material_id`) REFERENCES `jsh_material` (`id`);
-- ALTER TABLE `jsh_work_order` ADD CONSTRAINT `fk_work_order_production` FOREIGN KEY (`production_order_id`) REFERENCES `jsh_production_order` (`id`);
-- ALTER TABLE `jsh_work_order` ADD CONSTRAINT `fk_work_order_handler` FOREIGN KEY (`handler_user_id`) REFERENCES `jsh_user` (`id`);
-- ALTER TABLE `jsh_work_order_item` ADD CONSTRAINT `fk_work_order_item_work_order` FOREIGN KEY (`work_order_id`) REFERENCES `jsh_work_order` (`id`);
-- ALTER TABLE `jsh_work_order_item` ADD CONSTRAINT `fk_work_order_item_material` FOREIGN KEY (`material_id`) REFERENCES `jsh_material` (`id`);

-- ----------------------------
-- 初始化数据示例（可选）
-- ----------------------------
-- INSERT INTO `jsh_production_order` (`order_number`, `original_sale_order_id`, `material_id`, `quantity`, `status`, `delivery_deadline`, `remark`, `tenant_id`)
-- VALUES ('PO202506210001', 1, 1, 10.000000, 'PENDING', '2025-07-01 00:00:00', '测试生产订单', 0);
