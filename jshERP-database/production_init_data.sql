-- =====================================================
-- jshERP 智能生产管理模块初始化数据脚本
-- 包含测试数据和基础配置数据
-- =====================================================

-- 清理现有数据（仅用于开发环境）
-- DELETE FROM jsh_production_statistics WHERE tenant_id = 1;
-- DELETE FROM jsh_logistics_tracking WHERE tenant_id = 1;
-- DELETE FROM jsh_quality_inspection WHERE tenant_id = 1;
-- DELETE FROM jsh_production_report WHERE tenant_id = 1;
-- DELETE FROM jsh_production_task WHERE tenant_id = 1;
-- DELETE FROM jsh_production_work_order WHERE tenant_id = 1;
-- DELETE FROM jsh_worker_skill WHERE tenant_id = 1;

-- =====================================================
-- 1. 工人技能数据初始化
-- =====================================================

INSERT INTO `jsh_worker_skill` (`worker_id`, `worker_name`, `skill_type`, `skill_level`, `skill_score`, `certification_level`, `experience_years`, `hourly_rate`, `piece_rate`, `efficiency_rate`, `quality_rate`, `tenant_id`, `create_user`) VALUES
-- 李师傅 - 掐丝点蓝专家
(1, '李师傅', 'CLOISONNE_WIRE', 'EXPERT', 4.8, '高级技师', 8.5, 35.00, 25.00, 120.0, 98.5, 1, 1),
(1, '李师傅', 'CLOISONNE_ENAMEL', 'ADVANCED', 4.5, '中级技师', 6.0, 32.00, 22.00, 115.0, 96.0, 1, 1),
(1, '李师傅', 'POLISHING', 'ADVANCED', 4.2, '中级技师', 5.0, 30.00, 20.00, 110.0, 95.0, 1, 1),

-- 王师傅 - 点蓝组装专家
(2, '王师傅', 'CLOISONNE_ENAMEL', 'EXPERT', 4.9, '高级技师', 10.0, 40.00, 30.00, 125.0, 99.0, 1, 1),
(2, '王师傅', 'ASSEMBLY', 'EXPERT', 4.6, '高级技师', 8.0, 35.00, 25.00, 118.0, 97.0, 1, 1),
(2, '王师傅', 'CLOISONNE_WIRE', 'INTERMEDIATE', 3.8, '初级技师', 4.0, 28.00, 18.00, 100.0, 90.0, 1, 1),

-- 张师傅 - 质检包装专家
(3, '张师傅', 'QUALITY_CHECK', 'EXPERT', 4.7, '质检师', 12.0, 38.00, 28.00, 105.0, 99.5, 1, 1),
(3, '张师傅', 'PACKAGING', 'EXPERT', 4.4, '高级技师', 6.0, 30.00, 20.00, 110.0, 95.0, 1, 1),
(3, '张师傅', 'ASSEMBLY', 'ADVANCED', 4.0, '中级技师', 4.0, 25.00, 18.00, 100.0, 92.0, 1, 1),

-- 赵师傅 - 上色抛光
(4, '赵师傅', 'PAINTING', 'INTERMEDIATE', 3.8, '初级技师', 2.5, 28.00, 15.00, 95.0, 88.0, 1, 1),
(4, '赵师傅', 'POLISHING', 'INTERMEDIATE', 3.5, '初级技师', 2.0, 25.00, 14.00, 90.0, 85.0, 1, 1),
(4, '赵师傅', 'PACKAGING', 'BEGINNER', 3.2, '学徒', 1.0, 22.00, 12.00, 85.0, 82.0, 1, 1);

-- =====================================================
-- 2. 生产工单数据初始化
-- =====================================================

INSERT INTO `jsh_production_work_order` (`work_order_number`, `work_order_name`, `product_id`, `product_name`, `product_spec`, `production_mode`, `production_type`, `priority`, `production_reason`, `planned_quantity`, `actual_quantity`, `unit_name`, `plan_start_time`, `plan_end_time`, `status`, `workshop_name`, `responsible_person`, `estimated_cost`, `estimated_hours`, `quality_standard`, `source_type`, `source_number`, `tenant_id`, `create_user`) VALUES

-- 景泰蓝花瓶生产工单
('WO202506220001', '景泰蓝花瓶-大号-蓝色生产', 1, '景泰蓝花瓶', '大号-蓝色-高度30cm', 'ORDER_DRIVEN', 'NORMAL', 'HIGH', '客户订单需求', 50.000000, 0.000000, '个', '2025-06-22 08:00:00', '2025-06-25 18:00:00', 'PENDING', '景泰蓝车间', '李师傅', 12500.00, 120.0, '按照景泰蓝工艺标准执行，外观无瑕疵，色彩饱满', 'SALES_ORDER', 'SO202506220001', 1, 1),

-- 景泰蓝盘子生产工单
('WO202506220002', '景泰蓝盘子-中号-红色生产', 2, '景泰蓝盘子', '中号-红色-直径25cm', 'STOCK_DRIVEN', 'NORMAL', 'NORMAL', '库存补充', 100.000000, 30.000000, '个', '2025-06-21 09:00:00', '2025-06-24 17:00:00', 'IN_PROGRESS', '景泰蓝车间', '王师傅', 18000.00, 200.0, '按照景泰蓝工艺标准执行，图案清晰，色彩均匀', 'STOCK_PLAN', 'SP202506210001', 1, 1),

-- 景泰蓝茶具生产工单
('WO202506220003', '景泰蓝茶具-套装-绿色生产', 3, '景泰蓝茶具', '套装-绿色-6件套', 'AUTONOMOUS_PLAN', 'NORMAL', 'NORMAL', '新品试制', 20.000000, 20.000000, '套', '2025-06-20 10:00:00', '2025-06-23 16:00:00', 'COMPLETED', '景泰蓝车间', '张师傅', 9000.00, 160.0, '按照景泰蓝工艺标准执行，整套协调统一', 'MANUAL', 'MP202506200001', 1, 1),

-- 景泰蓝摆件生产工单
('WO202506220004', '景泰蓝摆件-龙凤-金色生产', 4, '景泰蓝摆件', '龙凤-金色-高度20cm', 'EMERGENCY', 'URGENT', 'URGENT', '紧急订单', 10.000000, 10.000000, '个', '2025-06-21 14:00:00', '2025-06-22 20:00:00', 'COMPLETED', '景泰蓝车间', '李师傅', 6000.00, 80.0, '按照景泰蓝工艺标准执行，工艺精湛，细节完美', 'SALES_ORDER', 'SO202506210002', 1, 1),

-- 景泰蓝首饰盒生产工单
('WO202506220005', '景泰蓝首饰盒-小号-紫色生产', 5, '景泰蓝首饰盒', '小号-紫色-长宽高15x10x8cm', 'ORDER_DRIVEN', 'NORMAL', 'LOW', '常规订单', 80.000000, 0.000000, '个', '2025-06-22 11:00:00', '2025-06-26 18:00:00', 'PENDING', '景泰蓝车间', '赵师傅', 9600.00, 160.0, '按照景泰蓝工艺标准执行，开合顺畅，内部平整', 'SALES_ORDER', 'SO202506220003', 1, 1);

-- =====================================================
-- 3. 生产任务数据初始化
-- =====================================================

INSERT INTO `jsh_production_task` (`task_number`, `task_name`, `work_order_id`, `work_order_number`, `task_type`, `process_step`, `product_id`, `product_name`, `task_quantity`, `completed_quantity`, `qualified_quantity`, `unit_name`, `priority`, `status`, `worker_id`, `worker_name`, `worker_specialty`, `assign_time`, `start_time`, `complete_time`, `plan_start_time`, `plan_end_time`, `estimated_hours`, `actual_hours`, `unit_fee`, `total_fee`, `quality_level`, `quality_score`, `tenant_id`, `create_user`) VALUES

-- 景泰蓝花瓶抛光任务
('PT1750570001', '景泰蓝花瓶抛光', 1, 'WO202506220001', 'POLISHING', '表面抛光', 1, '景泰蓝花瓶', 5.000000, 0.000000, 0.000000, '个', 'URGENT', 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, '2025-06-23 08:00:00', '2025-06-23 18:00:00', 10.0, 0.0, 25.00, 125.00, NULL, NULL, 1, 1),

-- 景泰蓝盘子上色任务
('PT1750570002', '景泰蓝盘子上色', 2, 'WO202506220002', 'PAINTING', '釉料上色', 2, '景泰蓝盘子', 10.000000, 3.000000, 3.000000, '个', 'HIGH', 'IN_PROGRESS', 1, '李师傅', 'CLOISONNE_ENAMEL', '2025-06-22 10:00:00', '2025-06-22 10:30:00', NULL, '2025-06-22 10:00:00', '2025-06-24 18:00:00', 20.0, 6.0, 18.00, 180.00, 'GOOD', 4.2, 1, 1),

-- 景泰蓝茶具组装任务
('PT1750570003', '景泰蓝茶具组装', 3, 'WO202506220003', 'ASSEMBLY', '部件组装', 3, '景泰蓝茶具', 3.000000, 3.000000, 3.000000, '套', 'NORMAL', 'COMPLETED', 2, '王师傅', 'ASSEMBLY', '2025-06-21 14:00:00', '2025-06-21 14:30:00', '2025-06-22 11:30:00', '2025-06-21 14:00:00', '2025-06-25 18:00:00', 24.0, 21.0, 45.00, 135.00, 'EXCELLENT', 4.8, 1, 1),

-- 景泰蓝摆件质检任务
('PT1750570004', '景泰蓝摆件质检', 4, 'WO202506220004', 'QUALITY_CHECK', '最终质检', 4, '景泰蓝摆件', 2.000000, 2.000000, 2.000000, '个', 'HIGH', 'QUALITY_CHECKED', 3, '张师傅', 'QUALITY_CHECK', '2025-06-21 16:00:00', '2025-06-21 16:30:00', '2025-06-22 09:30:00', '2025-06-21 16:00:00', '2025-06-23 18:00:00', 8.0, 6.0, 30.00, 60.00, 'EXCELLENT', 4.9, 1, 1),

-- 景泰蓝首饰盒包装任务
('PT1750570005', '景泰蓝首饰盒包装', 5, 'WO202506220005', 'PACKAGING', '精美包装', 5, '景泰蓝首饰盒', 8.000000, 0.000000, 0.000000, '个', 'NORMAL', 'ASSIGNED', 4, '赵师傅', 'PACKAGING', '2025-06-22 11:00:00', NULL, NULL, '2025-06-22 11:00:00', '2025-06-26 18:00:00', 16.0, 0.0, 12.00, 96.00, NULL, NULL, 1, 1);

-- =====================================================
-- 4. 生产报工数据初始化
-- =====================================================

INSERT INTO `jsh_production_report` (`report_number`, `task_id`, `task_number`, `work_order_id`, `work_order_number`, `worker_id`, `worker_name`, `report_type`, `report_time`, `work_date`, `start_time`, `end_time`, `work_hours`, `completed_quantity`, `qualified_quantity`, `defective_quantity`, `unit_name`, `quality_level`, `work_content`, `unit_fee`, `total_fee`, `is_completed`, `tenant_id`, `create_user`) VALUES

-- 景泰蓝盘子上色报工记录
('PR202506220001', 2, 'PT1750570002', 2, 'WO202506220002', 1, '李师傅', 'PROGRESS', '2025-06-22 14:00:00', '2025-06-22', '2025-06-22 10:30:00', '2025-06-22 14:00:00', 3.5, 3.000000, 3.000000, 0.000000, '个', 'GOOD', '完成3个盘子的釉料上色，色彩饱满均匀', 18.00, 54.00, 0, 1, 1),

-- 景泰蓝茶具组装报工记录
('PR202506220002', 3, 'PT1750570003', 3, 'WO202506220003', 2, '王师傅', 'COMPLETE', '2025-06-22 11:30:00', '2025-06-22', '2025-06-21 14:30:00', '2025-06-22 11:30:00', 21.0, 3.000000, 3.000000, 0.000000, '套', 'EXCELLENT', '完成3套茶具的组装，各部件配合完美', 45.00, 135.00, 1, 1, 1),

-- 景泰蓝摆件质检报工记录
('PR202506220003', 4, 'PT1750570004', 4, 'WO202506220004', 3, '张师傅', 'COMPLETE', '2025-06-22 09:30:00', '2025-06-22', '2025-06-21 16:30:00', '2025-06-22 09:30:00', 6.0, 2.000000, 2.000000, 0.000000, '个', 'EXCELLENT', '完成2个摆件的质量检验，工艺精湛', 30.00, 60.00, 1, 1, 1);

-- =====================================================
-- 5. 质量检验数据初始化
-- =====================================================

INSERT INTO `jsh_quality_inspection` (`inspection_number`, `task_id`, `task_number`, `work_order_id`, `work_order_number`, `product_id`, `product_name`, `inspector_id`, `inspector_name`, `inspection_time`, `inspection_type`, `inspection_quantity`, `qualified_quantity`, `defective_quantity`, `unit_name`, `qualification_rate`, `overall_result`, `quality_grade`, `overall_score`, `appearance_score`, `size_score`, `color_score`, `texture_score`, `detail_score`, `overall_effect_score`, `improvement_suggestion`, `inspection_standard`, `tenant_id`, `create_user`) VALUES

-- 景泰蓝茶具质检记录
('QI202506220001', 3, 'PT1750570003', 3, 'WO202506220003', 3, '景泰蓝茶具', 3, '张师傅', '2025-06-22 11:30:00', 'FINAL', 3.000000, 3.000000, 0.000000, '套', 100.00, 'PASS', 'A', 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, 4.0, '整体质量优秀，建议在细节处理上进一步提升', '按照景泰蓝工艺标准执行质检', 1, 1),

-- 景泰蓝摆件质检记录
('QI202506220002', 4, 'PT1750570004', 4, 'WO202506220004', 4, '景泰蓝摆件', 3, '张师傅', '2025-06-22 09:30:00', 'FINAL', 2.000000, 2.000000, 0.000000, '个', 100.00, 'PASS', 'A+', 4.8, 5.0, 4.5, 5.0, 4.5, 5.0, 5.0, '工艺精湛，质量优秀，达到大师级水准', '按照景泰蓝工艺标准执行质检', 1, 1);

-- =====================================================
-- 6. 物流追踪数据初始化
-- =====================================================

INSERT INTO `jsh_logistics_tracking` (`tracking_number`, `work_order_id`, `work_order_number`, `task_id`, `task_number`, `logistics_type`, `product_id`, `product_name`, `quantity`, `unit_name`, `from_location`, `to_location`, `carrier_name`, `status`, `ship_time`, `estimated_arrival_time`, `actual_arrival_time`, `receive_time`, `receiver_name`, `logistics_cost`, `tenant_id`, `create_user`) VALUES

-- 景泰蓝茶具成品出库
('LT202506220001', 3, 'WO202506220003', 3, 'PT1750570003', 'FINISHED_PRODUCT_OUT', 3, '景泰蓝茶具', 3.000000, '套', '景泰蓝车间', '成品仓库', '内部转运', 'RECEIVED', '2025-06-22 12:00:00', '2025-06-22 12:30:00', '2025-06-22 12:25:00', '2025-06-22 12:25:00', '仓库管理员', 0.00, 1, 1),

-- 景泰蓝摆件成品出库
('LT202506220002', 4, 'WO202506220004', 4, 'PT1750570004', 'FINISHED_PRODUCT_OUT', 4, '景泰蓝摆件', 2.000000, '个', '景泰蓝车间', '成品仓库', '内部转运', 'RECEIVED', '2025-06-22 10:00:00', '2025-06-22 10:30:00', '2025-06-22 10:20:00', '2025-06-22 10:20:00', '仓库管理员', 0.00, 1, 1);
