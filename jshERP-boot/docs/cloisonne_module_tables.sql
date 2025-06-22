-- =============================================
-- jshERP 掐丝珐琅馆综合管理模块数据库表设计
-- 版本: v1.0
-- 创建时间: 2025-01-22
-- 说明: 严格遵循jshERP多租户架构和数据规范
-- =============================================

-- ----------------------------
-- 1. 掐丝珐琅馆配置表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_config`;
CREATE TABLE `jsh_cloisonne_config` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT COMMENT '配置值(JSON格式)',
  `config_type` VARCHAR(50) DEFAULT 'system' COMMENT '配置类型(system/business/display)',
  `description` VARCHAR(500) COMMENT '配置描述',
  `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_config` (`tenant_id`, `config_key`, `delete_flag`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_config_type` (`config_type`),
  INDEX `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆配置表';

-- ----------------------------
-- 2. 排班管理表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_schedule`;
CREATE TABLE `jsh_cloisonne_schedule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `schedule_date` DATE NOT NULL COMMENT '排班日期',
  `employee_id` BIGINT(20) NOT NULL COMMENT '员工ID(关联jsh_user.id)',
  `employee_name` VARCHAR(100) NOT NULL COMMENT '员工姓名',
  `shift_type` VARCHAR(20) NOT NULL COMMENT '班次类型(早班/中班/晚班/夜班/全天)',
  `start_time` TIME COMMENT '开始时间',
  `end_time` TIME COMMENT '结束时间',
  `work_hours` DECIMAL(4,2) DEFAULT 8.00 COMMENT '工作时长(小时)',
  `work_area` VARCHAR(50) COMMENT '工作区域(咖啡店/展厅/收银台/全部)',
  `status` VARCHAR(20) DEFAULT 'normal' COMMENT '状态(normal-正常/leave-请假/swap-调班/absent-缺勤)',
  `notes` TEXT COMMENT '备注信息',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_employee_date` (`tenant_id`, `employee_id`, `schedule_date`, `delete_flag`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_schedule_date` (`schedule_date`),
  INDEX `idx_employee_id` (`employee_id`),
  INDEX `idx_shift_type` (`shift_type`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆排班表';

-- ----------------------------
-- 3. 咖啡店销售记录表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_coffee_sales`;
CREATE TABLE `jsh_cloisonne_coffee_sales` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sales_date` DATE NOT NULL COMMENT '销售日期',
  `revenue` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售金额',
  `order_count` INT DEFAULT 0 COMMENT '订单数量',
  `customer_count` INT DEFAULT 0 COMMENT '服务客户数',
  `special_cases` JSON COMMENT '特殊情况标记(promotion/event/weather/maintenance)',
  `notes` TEXT COMMENT '备注说明',
  `images` JSON COMMENT '销售凭证图片(JSON数组)',
  `recorder_id` BIGINT(20) NOT NULL COMMENT '录入人ID(关联jsh_user.id)',
  `recorder_name` VARCHAR(100) NOT NULL COMMENT '录入人姓名',
  
  -- 对比数据
  `yesterday_comparison` DECIMAL(5,2) COMMENT '较昨日增长率(%)',
  `week_comparison` DECIMAL(5,2) COMMENT '较上周同期增长率(%)',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_sales_date` (`tenant_id`, `sales_date`, `delete_flag`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_sales_date` (`sales_date`),
  INDEX `idx_recorder_id` (`recorder_id`),
  INDEX `idx_revenue` (`revenue`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆咖啡店销售记录表';

-- ----------------------------
-- 4. POS商品表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_pos_product`;
CREATE TABLE `jsh_cloisonne_pos_product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` BIGINT(20) COMMENT '关联商品ID(jsh_material.id)',
  `product_code` VARCHAR(50) COMMENT '商品编码',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `category_id` BIGINT(20) COMMENT '分类ID',
  `category_name` VARCHAR(100) COMMENT '分类名称',
  `price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '销售价格',
  `original_price` DECIMAL(10,2) COMMENT '原价',
  `cost_price` DECIMAL(10,2) COMMENT '成本价',
  `stock` INT DEFAULT 0 COMMENT '库存数量',
  `max_stock` INT DEFAULT 999 COMMENT '最大库存',
  `min_stock` INT DEFAULT 0 COMMENT '最小库存',
  `unit` VARCHAR(20) DEFAULT '件' COMMENT '单位',
  `image_url` VARCHAR(500) COMMENT '商品图片URL',
  `description` TEXT COMMENT '商品描述',
  `is_hot` TINYINT(1) DEFAULT 0 COMMENT '是否热销(0-否,1-是)',
  `is_favorite` TINYINT(1) DEFAULT 0 COMMENT '是否收藏(0-否,1-是)',
  `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用(0-禁用,1-启用)',
  `sort_order` INT DEFAULT 0 COMMENT '排序',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_product_code` (`tenant_id`, `product_code`, `delete_flag`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_material_id` (`material_id`),
  INDEX `idx_category_id` (`category_id`),
  INDEX `idx_enabled` (`enabled`),
  INDEX `idx_is_hot` (`is_hot`),
  INDEX `idx_stock` (`stock`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆POS商品表';

-- ----------------------------
-- 5. POS订单表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_pos_order`;
CREATE TABLE `jsh_cloisonne_pos_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号',
  `order_date` DATE NOT NULL COMMENT '订单日期',
  `order_time` DATETIME NOT NULL COMMENT '订单时间',
  `total_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠金额',
  `actual_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  `payment_method` VARCHAR(20) NOT NULL COMMENT '支付方式(cash/alipay/wechat/card)',
  `payment_status` VARCHAR(20) DEFAULT 'paid' COMMENT '支付状态(pending/paid/refunded)',
  `cash_received` DECIMAL(10,2) COMMENT '收款金额(现金支付)',
  `change_amount` DECIMAL(10,2) COMMENT '找零金额(现金支付)',
  `coupon_id` BIGINT(20) COMMENT '优惠券ID',
  `customer_count` INT DEFAULT 1 COMMENT '客户人数',
  `cashier_id` BIGINT(20) NOT NULL COMMENT '收银员ID(关联jsh_user.id)',
  `cashier_name` VARCHAR(100) NOT NULL COMMENT '收银员姓名',
  `notes` TEXT COMMENT '订单备注',
  `status` VARCHAR(20) DEFAULT 'completed' COMMENT '订单状态(pending/completed/cancelled/refunded)',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_order_no` (`tenant_id`, `order_no`, `delete_flag`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_order_date` (`order_date`),
  INDEX `idx_cashier_id` (`cashier_id`),
  INDEX `idx_payment_method` (`payment_method`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆POS订单表';

-- ----------------------------
-- 6. POS订单明细表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_pos_order_item`;
CREATE TABLE `jsh_cloisonne_pos_order_item` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT(20) NOT NULL COMMENT '订单ID(关联jsh_cloisonne_pos_order.id)',
  `product_id` BIGINT(20) NOT NULL COMMENT '商品ID(关联jsh_cloisonne_pos_product.id)',
  `product_code` VARCHAR(50) COMMENT '商品编码',
  `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称',
  `unit_price` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
  `subtotal` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
  `discount_rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '折扣率(%)',
  `discount_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '折扣金额',
  `actual_amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实际金额',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_order_id` (`order_id`),
  INDEX `idx_product_id` (`product_id`),
  FOREIGN KEY (`order_id`) REFERENCES `jsh_cloisonne_pos_order`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆POS订单明细表';

-- ----------------------------
-- 7. 任务管理表
-- ----------------------------
DROP TABLE IF EXISTS `jsh_cloisonne_task`;
CREATE TABLE `jsh_cloisonne_task` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_title` VARCHAR(200) NOT NULL COMMENT '任务标题',
  `task_description` TEXT COMMENT '任务描述',
  `task_type` VARCHAR(50) DEFAULT 'daily' COMMENT '任务类型(daily/weekly/monthly/special)',
  `priority` VARCHAR(20) DEFAULT 'normal' COMMENT '优先级(low/normal/high/urgent)',
  `assigned_to` BIGINT(20) COMMENT '分配给(关联jsh_user.id)',
  `assigned_name` VARCHAR(100) COMMENT '分配给姓名',
  `due_date` DATE COMMENT '截止日期',
  `due_time` TIME COMMENT '截止时间',
  `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态(pending/in_progress/completed/cancelled)',
  `completed_time` DATETIME COMMENT '完成时间',
  `completion_notes` TEXT COMMENT '完成备注',
  
  -- 多租户字段
  `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
  
  -- 审计字段
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` BIGINT(20) COMMENT '创建人ID',
  `update_user` BIGINT(20) COMMENT '更新人ID',
  
  -- 软删除字段
  `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',
  
  PRIMARY KEY (`id`),
  INDEX `idx_tenant_id` (`tenant_id`),
  INDEX `idx_assigned_to` (`assigned_to`),
  INDEX `idx_due_date` (`due_date`),
  INDEX `idx_status` (`status`),
  INDEX `idx_task_type` (`task_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='掐丝珐琅馆任务管理表';

-- ----------------------------
-- 初始化权限菜单数据
-- ----------------------------
-- 注意：这些INSERT语句需要根据实际的jsh_function表结构调整
-- 主菜单：掐丝珐琅馆
INSERT INTO `jsh_function` (`number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) 
VALUES ('1001', '掐丝珐琅馆', '0', '/cloisonne', '/layouts/TabLayout', 0, '1001', 1, '电脑版', '', 'shop', '0');

-- 子菜单：总览仪表板
INSERT INTO `jsh_function` (`number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) 
VALUES ('100101', '总览仪表板', '1001', '/cloisonne/dashboard', '/cloisonne/Dashboard', 0, '100101', 1, '电脑版', '1', 'dashboard', '0');

-- 子菜单：排班管理
INSERT INTO `jsh_function` (`number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) 
VALUES ('100102', '排班管理', '1001', '/cloisonne/schedule', '/cloisonne/Schedule', 0, '100102', 1, '电脑版', '1,2,3,7', 'calendar', '0');

-- 子菜单：咖啡店管理
INSERT INTO `jsh_function` (`number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) 
VALUES ('100103', '咖啡店管理', '1001', '/cloisonne/coffee', '/cloisonne/Coffee', 0, '100103', 1, '电脑版', '1,2,3,7', 'coffee', '0');

-- 子菜单：POS销售
INSERT INTO `jsh_function` (`number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) 
VALUES ('100104', 'POS销售', '1001', '/cloisonne/pos', '/cloisonne/POS', 0, '100104', 1, '电脑版', '1,2,3,7', 'shopping-cart', '0');

-- ----------------------------
-- 初始化配置数据
-- ----------------------------
INSERT INTO `jsh_cloisonne_config` (`config_key`, `config_value`, `config_type`, `description`, `tenant_id`) VALUES
('dashboard.refresh_interval', '30000', 'system', '仪表板数据刷新间隔(毫秒)', 0),
('schedule.work_hours', '{"早班":"08:00-16:00","中班":"12:00-20:00","晚班":"16:00-24:00","夜班":"00:00-08:00","全天":"08:00-24:00"}', 'business', '班次工作时间配置', 0),
('coffee.upload_path', '/uploads/coffee/', 'system', '咖啡店图片上传路径', 0),
('pos.payment_methods', '["cash","alipay","wechat","card"]', 'business', 'POS支持的支付方式', 0);

-- ----------------------------
-- 创建统计视图
-- ----------------------------

-- 排班统计视图
CREATE OR REPLACE VIEW `v_cloisonne_schedule_stats` AS
SELECT
    s.tenant_id,
    s.schedule_date,
    COUNT(*) as total_schedules,
    COUNT(DISTINCT s.employee_id) as employee_count,
    SUM(s.work_hours) as total_work_hours,
    AVG(s.work_hours) as avg_work_hours,
    SUM(CASE WHEN s.status = 'normal' THEN 1 ELSE 0 END) as normal_count,
    SUM(CASE WHEN s.status = 'leave' THEN 1 ELSE 0 END) as leave_count,
    SUM(CASE WHEN s.status = 'swap' THEN 1 ELSE 0 END) as swap_count,
    SUM(CASE WHEN s.status = 'absent' THEN 1 ELSE 0 END) as absent_count,
    ROUND(SUM(CASE WHEN s.status = 'normal' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) as attendance_rate
FROM jsh_cloisonne_schedule s
WHERE s.delete_flag = '0'
GROUP BY s.tenant_id, s.schedule_date;

-- 咖啡店销售统计视图
CREATE OR REPLACE VIEW `v_cloisonne_coffee_stats` AS
SELECT
    cs.tenant_id,
    DATE_FORMAT(cs.sales_date, '%Y-%m') as sales_month,
    COUNT(*) as sales_days,
    SUM(cs.revenue) as total_revenue,
    SUM(cs.order_count) as total_orders,
    AVG(cs.revenue) as avg_daily_revenue,
    AVG(cs.order_count) as avg_daily_orders,
    AVG(cs.revenue / NULLIF(cs.order_count, 0)) as avg_order_amount
FROM jsh_cloisonne_coffee_sales cs
WHERE cs.delete_flag = '0'
GROUP BY cs.tenant_id, DATE_FORMAT(cs.sales_date, '%Y-%m');

-- POS销售统计视图
CREATE OR REPLACE VIEW `v_cloisonne_pos_stats` AS
SELECT
    po.tenant_id,
    DATE_FORMAT(po.order_date, '%Y-%m-%d') as order_date,
    COUNT(*) as order_count,
    SUM(po.total_amount) as total_amount,
    SUM(po.actual_amount) as actual_amount,
    AVG(po.actual_amount) as avg_order_amount,
    SUM(CASE WHEN po.payment_method = 'cash' THEN po.actual_amount ELSE 0 END) as cash_amount,
    SUM(CASE WHEN po.payment_method = 'alipay' THEN po.actual_amount ELSE 0 END) as alipay_amount,
    SUM(CASE WHEN po.payment_method = 'wechat' THEN po.actual_amount ELSE 0 END) as wechat_amount,
    SUM(CASE WHEN po.payment_method = 'card' THEN po.actual_amount ELSE 0 END) as card_amount
FROM jsh_cloisonne_pos_order po
WHERE po.delete_flag = '0' AND po.payment_status = 'paid'
GROUP BY po.tenant_id, DATE_FORMAT(po.order_date, '%Y-%m-%d');

-- ----------------------------
-- 创建触发器
-- ----------------------------

-- POS订单明细表触发器：自动计算小计和实际金额
DELIMITER $$
CREATE TRIGGER `tr_pos_order_item_calculate`
BEFORE INSERT ON `jsh_cloisonne_pos_order_item`
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.unit_price * NEW.quantity;
    SET NEW.actual_amount = NEW.subtotal - NEW.discount_amount;
END$$

CREATE TRIGGER `tr_pos_order_item_calculate_update`
BEFORE UPDATE ON `jsh_cloisonne_pos_order_item`
FOR EACH ROW
BEGIN
    SET NEW.subtotal = NEW.unit_price * NEW.quantity;
    SET NEW.actual_amount = NEW.subtotal - NEW.discount_amount;
END$$

DELIMITER ;

-- ----------------------------
-- 插入示例数据
-- ----------------------------

-- 插入示例配置数据
INSERT INTO `jsh_cloisonne_config` (`config_key`, `config_value`, `config_type`, `description`, `tenant_id`) VALUES
('module.version', '1.0.0', 'system', '模块版本号', 0),
('schedule.auto_conflict_check', 'true', 'business', '是否启用排班冲突自动检查', 0),
('coffee.auto_finance_record', 'true', 'business', '咖啡店销售是否自动生成财务记录', 0),
('pos.auto_stock_update', 'true', 'business', 'POS销售是否自动更新库存', 0),
('task.notification_enabled', 'true', 'business', '是否启用任务通知', 0);

-- 插入示例POS商品数据
INSERT INTO `jsh_cloisonne_pos_product` (`product_code`, `product_name`, `category_name`, `price`, `cost_price`, `stock`, `unit`, `description`, `tenant_id`) VALUES
('CF001', '美式咖啡', '饮品', 25.00, 8.00, 100, '杯', '经典美式咖啡，香浓醇厚', 0),
('CF002', '拿铁咖啡', '饮品', 30.00, 10.00, 100, '杯', '意式拿铁，奶香浓郁', 0),
('CF003', '卡布奇诺', '饮品', 28.00, 9.00, 100, '杯', '经典卡布奇诺，泡沫丰富', 0),
('SN001', '芝士蛋糕', '甜品', 35.00, 15.00, 50, '块', '免烤芝士蛋糕，口感顺滑', 0),
('SN002', '提拉米苏', '甜品', 38.00, 18.00, 30, '块', '意式提拉米苏，层次丰富', 0),
('GF001', '掐丝珐琅手镯', '工艺品', 288.00, 120.00, 20, '件', '精美掐丝珐琅手镯，传统工艺', 0),
('GF002', '掐丝珐琅花瓶', '工艺品', 588.00, 250.00, 10, '件', '掐丝珐琅花瓶，收藏佳品', 0),
('BK001', '掐丝珐琅工艺书籍', '图书', 68.00, 30.00, 50, '本', '掐丝珐琅工艺制作指南', 0);

-- 设置表注释和字符集
ALTER TABLE `jsh_cloisonne_config` COMMENT='掐丝珐琅馆配置表';
ALTER TABLE `jsh_cloisonne_schedule` COMMENT='掐丝珐琅馆排班表';
ALTER TABLE `jsh_cloisonne_coffee_sales` COMMENT='掐丝珐琅馆咖啡店销售记录表';
ALTER TABLE `jsh_cloisonne_pos_product` COMMENT='掐丝珐琅馆POS商品表';
ALTER TABLE `jsh_cloisonne_pos_order` COMMENT='掐丝珐琅馆POS订单表';
ALTER TABLE `jsh_cloisonne_pos_order_item` COMMENT='掐丝珐琅馆POS订单明细表';
ALTER TABLE `jsh_cloisonne_task` COMMENT='掐丝珐琅馆任务管理表';

-- ----------------------------
-- 脚本执行完成提示
-- ----------------------------
SELECT '掐丝珐琅馆模块数据库初始化完成！' as message,
       '共创建7张业务表、3个统计视图、2个触发器' as details,
       '已插入权限菜单、配置数据和示例商品数据' as config_status;
