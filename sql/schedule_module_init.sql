-- jshERP 排班管理模块数据库初始化脚本
-- 创建日期: 2025-06-21
-- 作者: Augment Code
-- 描述: 创建排班管理相关的数据库表和初始数据

-- =====================================================
-- 1. 班次定义表 (jsh_schedule_shift)
-- =====================================================
-- 注意: 该表已存在，包含以下结构：
/*
CREATE TABLE `jsh_schedule_shift` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `shift_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班次名称',
  `shift_type` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'FULL_DAY' COMMENT '班次类型：FULL_DAY-全天班，MORNING-上午班，AFTERNOON-下午班，EVENING-晚班',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `duration_hours` decimal(4,2) DEFAULT NULL COMMENT '班次时长（小时）',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用：0-禁用，1-启用',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `color` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '#1890ff' COMMENT '显示颜色',
  `tenant_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户ID',
  `delete_flag` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '删除标记：0-存在，1-删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_delete_flag` (`delete_flag`),
  KEY `idx_shift_type` (`shift_type`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排班班次配置表';
*/

-- =====================================================
-- 2. 排班记录表 (jsh_schedule_entry)
-- =====================================================
CREATE TABLE IF NOT EXISTS `jsh_schedule_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `shift_id` bigint(20) NOT NULL COMMENT '班次ID，关联jsh_schedule_shift表',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `status` varchar(20) DEFAULT 'SCHEDULED' COMMENT '状态：SCHEDULED-已排班，CONFIRMED-已确认，CANCELLED-已取消',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `break_duration` int(11) DEFAULT '0' COMMENT '休息时长（分钟）',
  `overtime_duration` int(11) DEFAULT '0' COMMENT '加班时长（分钟）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建用户',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新用户',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  PRIMARY KEY (`id`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`),
  KEY `idx_schedule_employee` (`schedule_date`, `employee_id`),
  KEY `idx_shift_id` (`shift_id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_schedule_date` (`schedule_date`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_schedule_entry_shift` FOREIGN KEY (`shift_id`) REFERENCES `jsh_schedule_shift` (`id`),
  CONSTRAINT `fk_schedule_entry_employee` FOREIGN KEY (`employee_id`) REFERENCES `jsh_user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='排班记录表';

-- =====================================================
-- 3. 初始化班次数据 (如果不存在)
-- =====================================================
INSERT IGNORE INTO `jsh_schedule_shift` 
(`id`, `shift_name`, `shift_type`, `start_time`, `end_time`, `duration_hours`, `is_active`, `sort_order`, `description`, `color`, `tenant_id`, `delete_flag`, `create_by`) 
VALUES
(1, '全天班', 'FULL_DAY', '09:00:00', '18:00:00', 8.00, 1, 1, '标准全天工作班次，9:00-18:00', '#1890ff', 0, '0', 0),
(2, '上午班', 'MORNING', '09:00:00', '13:00:00', 4.00, 1, 2, '上午工作班次，9:00-13:00', '#52c41a', 0, '0', 0),
(3, '下午班', 'AFTERNOON', '14:00:00', '18:00:00', 4.00, 1, 3, '下午工作班次，14:00-18:00', '#faad14', 0, '0', 0),
(4, '晚班', 'EVENING', '18:00:00', '22:00:00', 4.00, 1, 4, '晚间工作班次，18:00-22:00', '#722ed1', 0, '0', 0);

-- =====================================================
-- 4. 示例排班数据
-- =====================================================
-- 注意：以下数据仅用于测试，实际使用时请根据需要调整
/*
INSERT INTO jsh_schedule_entry (shift_id, employee_id, schedule_date, status, remark, create_user, tenant_id) VALUES
(1, 120, '2025-06-22', 'SCHEDULED', '管理员全天班', 120, 0),
(2, 120, '2025-06-23', 'SCHEDULED', '管理员上午班', 120, 0),
(3, 120, '2025-06-24', 'CONFIRMED', '管理员下午班', 120, 0),
(1, 120, '2025-06-25', 'SCHEDULED', '管理员全天班', 120, 0),
(4, 120, '2025-06-26', 'SCHEDULED', '管理员晚班', 120, 0);
*/

-- =====================================================
-- 5. 验证查询
-- =====================================================
-- 查看班次配置
-- SELECT * FROM jsh_schedule_shift WHERE delete_flag = '0' ORDER BY sort_order;

-- 查看排班记录（关联查询）
-- SELECT 
--     se.id,
--     se.schedule_date,
--     ss.shift_name,
--     ss.start_time,
--     ss.end_time,
--     u.username,
--     se.status,
--     se.remark
-- FROM jsh_schedule_entry se
-- JOIN jsh_schedule_shift ss ON se.shift_id = ss.id
-- JOIN jsh_user u ON se.employee_id = u.id
-- WHERE se.delete_flag = '0'
-- ORDER BY se.schedule_date;

-- =====================================================
-- 6. 索引使用验证
-- =====================================================
-- 验证复合索引使用情况
-- EXPLAIN SELECT * FROM jsh_schedule_entry WHERE schedule_date = '2025-06-22' AND employee_id = 120;

-- =====================================================
-- 7. 表结构说明
-- =====================================================
/*
表设计说明：

1. jsh_schedule_shift (班次定义表)
   - 定义各种工作班次的基本信息
   - 支持全天班、上午班、下午班、晚班等类型
   - 包含班次时长、颜色标识等扩展字段
   - 支持多租户数据隔离

2. jsh_schedule_entry (排班记录表)
   - 记录具体的员工排班信息
   - 关联班次表和用户表
   - 支持排班状态管理（已排班、已确认、已取消）
   - 记录实际工作时间和加班信息
   - 建立了复合索引优化查询性能

索引设计：
- idx_schedule_employee: (schedule_date, employee_id) - 核心复合索引
- idx_tenant_delete: (tenant_id, delete_flag) - 多租户查询优化
- 其他单列索引支持各种查询场景

外键约束：
- fk_schedule_entry_shift: 确保班次ID的引用完整性
- fk_schedule_entry_employee: 确保员工ID的引用完整性
*/
