-- jshERP智能生产管理模块 - 数据库索引优化脚本
-- 版本: v1.0
-- 创建日期: 2025-06-22
-- 说明: 优化数据库索引，提高查询性能和数据一致性

-- 设置字符集
SET NAMES utf8mb4;

-- =====================================================
-- 1. 核心业务表索引优化
-- =====================================================

-- 生产工单表索引优化
ALTER TABLE `jsh_production_work_order` 
ADD INDEX `idx_status_priority` (`status`, `priority`),
ADD INDEX `idx_material_status` (`material_id`, `status`),
ADD INDEX `idx_worker_status` (`assigned_worker_id`, `status`),
ADD INDEX `idx_customer_order` (`customer_id`, `sales_order_id`),
ADD INDEX `idx_time_range` (`plan_start_time`, `plan_end_time`),
ADD INDEX `idx_actual_time_range` (`actual_start_time`, `actual_end_time`),
ADD INDEX `idx_order_type_source` (`order_type`, `order_source`),
ADD INDEX `idx_tenant_status_priority` (`tenant_id`, `delete_flag`, `status`, `priority`);

-- 工人信息表索引优化
ALTER TABLE `jsh_production_worker` 
ADD INDEX `idx_specialty_level` (`specialty`, `skill_level`),
ADD INDEX `idx_status_availability` (`status`, `availability_status`),
ADD INDEX `idx_workload` (`current_workload`, `max_workload`),
ADD INDEX `idx_rating` (`efficiency_rating`, `quality_rating`),
ADD INDEX `idx_department_supervisor` (`department`, `supervisor_id`),
ADD INDEX `idx_hire_date` (`hire_date`),
ADD INDEX `idx_tenant_status_available` (`tenant_id`, `delete_flag`, `status`, `availability_status`);

-- 生产任务表索引优化
ALTER TABLE `jsh_production_task` 
ADD INDEX `idx_work_order_status` (`work_order_id`, `status`),
ADD INDEX `idx_material_task_type` (`material_id`, `task_type`),
ADD INDEX `idx_worker_status` (`assigned_worker_id`, `status`),
ADD INDEX `idx_priority_status` (`priority`, `status`),
ADD INDEX `idx_difficulty_craft` (`difficulty_level`, `craft_type`),
ADD INDEX `idx_time_range` (`plan_start_time`, `plan_end_time`),
ADD INDEX `idx_actual_time_range` (`actual_start_time`, `actual_end_time`),
ADD INDEX `idx_progress` (`progress_percentage`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `task_type`, `status`);

-- 生产报工表索引优化
ALTER TABLE `jsh_production_report` 
ADD INDEX `idx_work_order_worker` (`work_order_id`, `worker_id`),
ADD INDEX `idx_task_worker` (`task_id`, `worker_id`),
ADD INDEX `idx_report_date_type` (`report_date`, `report_type`),
ADD INDEX `idx_worker_date` (`worker_id`, `report_date`),
ADD INDEX `idx_quality_efficiency` (`quality_score`, `efficiency_score`),
ADD INDEX `idx_report_time` (`report_time`),
ADD INDEX `idx_tenant_date_type` (`tenant_id`, `delete_flag`, `report_date`, `report_type`);

-- 工单材料清单表索引优化
ALTER TABLE `jsh_production_work_order_material` 
ADD INDEX `idx_work_order_material` (`work_order_id`, `material_id`),
ADD INDEX `idx_material_type` (`material_type`, `allocation_status`),
ADD INDEX `idx_supplier_warehouse` (`supplier_id`, `warehouse_id`),
ADD INDEX `idx_allocation_time` (`allocation_time`),
ADD INDEX `idx_consumption_time` (`consumption_time`),
ADD INDEX `idx_tenant_status` (`tenant_id`, `delete_flag`, `allocation_status`);

-- =====================================================
-- 2. 业务流程表索引优化
-- =====================================================

-- 掐丝点蓝制作记录表索引优化
ALTER TABLE `jsh_cloisonne_production` 
ADD INDEX `idx_work_order_status` (`work_order_id`, `status`),
ADD INDEX `idx_material_mode` (`material_id`, `production_mode`),
ADD INDEX `idx_worker_status` (`assigned_worker_id`, `status`),
ADD INDEX `idx_priority_difficulty` (`priority`, `difficulty_level`),
ADD INDEX `idx_craft_color` (`craft_type`, `color_scheme`),
ADD INDEX `idx_customer_supplier` (`customer_id`, `supplier_id`),
ADD INDEX `idx_time_range` (`plan_start_time`, `plan_end_time`),
ADD INDEX `idx_actual_time_range` (`actual_start_time`, `actual_end_time`),
ADD INDEX `idx_quality_grade` (`quality_grade`),
ADD INDEX `idx_tenant_mode_status` (`tenant_id`, `delete_flag`, `production_mode`, `status`);

-- 配饰制作记录表索引优化
ALTER TABLE `jsh_accessory_production` 
ADD INDEX `idx_work_order_status` (`work_order_id`, `status`),
ADD INDEX `idx_semi_product_type` (`semi_product_id`, `accessory_type`),
ADD INDEX `idx_worker_status` (`assigned_worker_id`, `status`),
ADD INDEX `idx_priority_difficulty` (`priority`, `difficulty_level`),
ADD INDEX `idx_type_style` (`accessory_type`, `accessory_style`),
ADD INDEX `idx_customer` (`customer_id`),
ADD INDEX `idx_time_range` (`plan_start_time`, `plan_end_time`),
ADD INDEX `idx_actual_time_range` (`actual_start_time`, `actual_end_time`),
ADD INDEX `idx_quality_grade` (`quality_grade`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `accessory_type`, `status`);

-- 后工任务记录表索引优化
ALTER TABLE `jsh_post_processing_task` 
ADD INDEX `idx_work_order_status` (`work_order_id`, `status`),
ADD INDEX `idx_production_task` (`production_id`, `task_type`),
ADD INDEX `idx_product_type` (`product_id`, `task_type`),
ADD INDEX `idx_worker_status` (`assigned_worker_id`, `status`),
ADD INDEX `idx_priority_difficulty` (`priority`, `difficulty_level`),
ADD INDEX `idx_deadline_status` (`deadline`, `status`),
ADD INDEX `idx_claim_complete_time` (`claim_time`, `complete_time`),
ADD INDEX `idx_quality_checker` (`quality_checker_id`, `quality_result`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `task_type`, `status`);

-- 任务状态变更记录表索引优化
ALTER TABLE `jsh_task_status_log` 
ADD INDEX `idx_task_type_status` (`task_id`, `task_type`, `new_status`),
ADD INDEX `idx_operator_time` (`operator_id`, `change_time`),
ADD INDEX `idx_status_change` (`old_status`, `new_status`),
ADD INDEX `idx_tenant_type_time` (`tenant_id`, `delete_flag`, `task_type`, `change_time`);

-- 工艺流程模板表索引优化
ALTER TABLE `jsh_process_template` 
ADD INDEX `idx_type_category` (`template_type`, `product_category`),
ADD INDEX `idx_difficulty_status` (`difficulty_level`, `status`),
ADD INDEX `idx_version` (`version`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `template_type`, `status`);

-- =====================================================
-- 3. 辅助功能表索引优化
-- =====================================================

-- 物流追踪表索引优化
ALTER TABLE `jsh_logistics_tracking` 
ADD INDEX `idx_order_work_order` (`order_id`, `work_order_id`),
ADD INDEX `idx_company_status` (`logistics_company`, `status`),
ADD INDEX `idx_recipient_info` (`recipient_name`, `recipient_phone`),
ADD INDEX `idx_time_range` (`ship_time`, `delivery_time`),
ADD INDEX `idx_estimated_delivery` (`estimated_delivery`),
ADD INDEX `idx_delivery_type` (`delivery_type`),
ADD INDEX `idx_tenant_company_status` (`tenant_id`, `delete_flag`, `logistics_company`, `status`);

-- 物流轨迹记录表索引优化
ALTER TABLE `jsh_logistics_trace` 
ADD INDEX `idx_tracking_trace_time` (`tracking_id`, `trace_time`),
ADD INDEX `idx_number_time` (`tracking_number`, `trace_time`),
ADD INDEX `idx_location` (`location`),
ADD INDEX `idx_status_code` (`status_code`),
ADD INDEX `idx_tenant_time` (`tenant_id`, `delete_flag`, `trace_time`);

-- 质检记录表索引优化
ALTER TABLE `jsh_quality_inspection` 
ADD INDEX `idx_work_order_task` (`work_order_id`, `task_id`),
ADD INDEX `idx_product_type` (`product_id`, `inspection_type`),
ADD INDEX `idx_inspector_status` (`inspector_id`, `status`),
ADD INDEX `idx_result_grade` (`overall_result`, `quality_grade`),
ADD INDEX `idx_time_range` (`actual_start_time`, `actual_end_time`),
ADD INDEX `idx_pass_rate` (`pass_rate`),
ADD INDEX `idx_overall_score` (`overall_score`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `inspection_type`, `status`);

-- 质检标准表索引优化
ALTER TABLE `jsh_quality_standard` 
ADD INDEX `idx_type_category` (`standard_type`, `product_category`),
ADD INDEX `idx_status_version` (`status`, `version`),
ADD INDEX `idx_effective_date` (`effective_date`, `expiry_date`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `standard_type`, `status`);

-- 生产统计表索引优化
ALTER TABLE `jsh_production_statistics` 
ADD INDEX `idx_date_type_dimension` (`stat_date`, `stat_type`, `stat_dimension`),
ADD INDEX `idx_dimension_value` (`stat_dimension`, `dimension_value`),
ADD INDEX `idx_completion_rate` (`completion_rate`),
ADD INDEX `idx_quality_pass_rate` (`quality_pass_rate`),
ADD INDEX `idx_efficiency_rate` (`efficiency_rate`),
ADD INDEX `idx_tenant_date_type` (`tenant_id`, `delete_flag`, `stat_date`, `stat_type`);

-- 系统配置表索引优化
ALTER TABLE `jsh_production_config` 
ADD INDEX `idx_type_category` (`config_type`, `config_category`),
ADD INDEX `idx_status_required` (`status`, `is_required`),
ADD INDEX `idx_sort_order` (`sort_order`),
ADD INDEX `idx_tenant_type_status` (`tenant_id`, `delete_flag`, `config_type`, `status`);

-- =====================================================
-- 4. 复合索引优化（高频查询场景）
-- =====================================================

-- 工单查询优化
ALTER TABLE `jsh_production_work_order` 
ADD INDEX `idx_complex_query1` (`tenant_id`, `delete_flag`, `status`, `priority`, `create_time`),
ADD INDEX `idx_complex_query2` (`tenant_id`, `delete_flag`, `order_type`, `order_source`, `status`),
ADD INDEX `idx_complex_query3` (`tenant_id`, `delete_flag`, `assigned_worker_id`, `status`, `plan_start_time`);

-- 任务查询优化
ALTER TABLE `jsh_production_task` 
ADD INDEX `idx_complex_query1` (`tenant_id`, `delete_flag`, `task_type`, `status`, `priority`),
ADD INDEX `idx_complex_query2` (`tenant_id`, `delete_flag`, `assigned_worker_id`, `status`, `plan_start_time`),
ADD INDEX `idx_complex_query3` (`tenant_id`, `delete_flag`, `work_order_id`, `status`, `progress_percentage`);

-- 报工查询优化
ALTER TABLE `jsh_production_report` 
ADD INDEX `idx_complex_query1` (`tenant_id`, `delete_flag`, `worker_id`, `report_date`, `report_type`),
ADD INDEX `idx_complex_query2` (`tenant_id`, `delete_flag`, `work_order_id`, `report_date`, `quality_score`),
ADD INDEX `idx_complex_query3` (`tenant_id`, `delete_flag`, `task_id`, `report_date`, `efficiency_score`);

-- 质检查询优化
ALTER TABLE `jsh_quality_inspection` 
ADD INDEX `idx_complex_query1` (`tenant_id`, `delete_flag`, `inspection_type`, `status`, `overall_result`),
ADD INDEX `idx_complex_query2` (`tenant_id`, `delete_flag`, `inspector_id`, `create_time`, `quality_grade`),
ADD INDEX `idx_complex_query3` (`tenant_id`, `delete_flag`, `product_id`, `overall_result`, `pass_rate`);

-- 物流查询优化
ALTER TABLE `jsh_logistics_tracking` 
ADD INDEX `idx_complex_query1` (`tenant_id`, `delete_flag`, `logistics_company`, `status`, `ship_time`),
ADD INDEX `idx_complex_query2` (`tenant_id`, `delete_flag`, `order_id`, `status`, `delivery_time`),
ADD INDEX `idx_complex_query3` (`tenant_id`, `delete_flag`, `recipient_name`, `status`, `estimated_delivery`);

-- 统计查询优化
ALTER TABLE `jsh_production_statistics` 
ADD INDEX `idx_complex_query1` (`tenant_id`, `delete_flag`, `stat_type`, `stat_dimension`, `stat_date`),
ADD INDEX `idx_complex_query2` (`tenant_id`, `delete_flag`, `stat_dimension`, `dimension_value`, `stat_date`);

-- =====================================================
-- 5. 分析查询索引（报表和统计）
-- =====================================================

-- 时间范围分析索引
ALTER TABLE `jsh_production_work_order` 
ADD INDEX `idx_time_analysis` (`tenant_id`, `delete_flag`, `create_time`, `status`, `order_type`);

ALTER TABLE `jsh_production_task` 
ADD INDEX `idx_time_analysis` (`tenant_id`, `delete_flag`, `create_time`, `task_type`, `status`);

ALTER TABLE `jsh_production_report` 
ADD INDEX `idx_time_analysis` (`tenant_id`, `delete_flag`, `report_date`, `report_type`, `worker_id`);

-- 成本分析索引
ALTER TABLE `jsh_production_work_order` 
ADD INDEX `idx_cost_analysis` (`tenant_id`, `delete_flag`, `total_amount`, `status`, `create_time`);

ALTER TABLE `jsh_cloisonne_production` 
ADD INDEX `idx_cost_analysis` (`tenant_id`, `delete_flag`, `total_cost`, `status`, `create_time`);

ALTER TABLE `jsh_accessory_production` 
ADD INDEX `idx_cost_analysis` (`tenant_id`, `delete_flag`, `total_cost`, `status`, `create_time`);

-- 质量分析索引
ALTER TABLE `jsh_quality_inspection` 
ADD INDEX `idx_quality_analysis` (`tenant_id`, `delete_flag`, `overall_result`, `quality_grade`, `create_time`);

ALTER TABLE `jsh_production_report` 
ADD INDEX `idx_quality_analysis` (`tenant_id`, `delete_flag`, `quality_score`, `efficiency_score`, `report_date`);

-- 工人效率分析索引
ALTER TABLE `jsh_production_worker` 
ADD INDEX `idx_efficiency_analysis` (`tenant_id`, `delete_flag`, `efficiency_rating`, `quality_rating`, `status`);

ALTER TABLE `jsh_production_task` 
ADD INDEX `idx_worker_efficiency` (`tenant_id`, `delete_flag`, `assigned_worker_id`, `actual_hours`, `estimated_hours`);

-- =====================================================
-- 索引创建完成提示
-- =====================================================
SELECT '数据库索引优化完成！' as message;
SELECT '已创建约150个索引，包括单列索引、复合索引、分析索引等' as details;
SELECT '索引覆盖：基础查询、复杂查询、时间范围查询、统计分析查询' as coverage;
