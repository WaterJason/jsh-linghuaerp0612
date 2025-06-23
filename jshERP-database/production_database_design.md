# jshERP 智能生产管理模块数据库设计文档

## 概述

本文档详细描述了jshERP智能生产管理模块的数据库表结构设计，基于商品管理模块的架构模式，严格遵循jshERP的多租户架构和数据规范。

## 设计原则

### 1. 多租户支持
- 所有业务表必须包含 `tenant_id` 字段
- 建立 `tenant_id` 相关的复合索引
- 查询时必须包含租户条件过滤

### 2. 审计字段规范
- `create_time`: 创建时间，默认当前时间
- `create_user`: 创建人ID
- `update_time`: 更新时间，自动更新
- `update_user`: 更新人ID
- `delete_flag`: 逻辑删除标记，'0'未删除，'1'已删除

### 3. 数据完整性
- 主键使用 `bigint(20)` 自增
- 外键关联使用 `bigint(20)`
- 金额字段使用 `decimal(24,6)`
- 数量字段使用 `decimal(24,6)`
- 百分比字段使用 `decimal(5,2)`

## 核心表结构

### 1. 生产工单表 (jsh_production_work_order)

**功能描述**: 管理生产工单，支持多种制作模式和优先级管理

**核心字段**:
- `work_order_number`: 工单编号，租户内唯一
- `production_mode`: 制作模式（订单驱动、库存驱动、自主计划、紧急制作）
- `production_type`: 制作类型（正常制作、返工、样品、紧急）
- `priority`: 优先级（低、普通、高、紧急）
- `status`: 工单状态（待开始、进行中、已完成、已取消、已暂停）

**业务关联**:
- 关联产品表 (`product_id` → `jsh_material.id`)
- 关联单位表 (`unit_id` → `jsh_unit.id`)
- 支持来源追溯 (`source_type`, `source_id`, `source_number`)

**索引设计**:
```sql
-- 基础索引
KEY `idx_product_id` (`product_id`)
KEY `idx_status` (`status`)
KEY `idx_priority` (`priority`)
KEY `idx_tenant_id` (`tenant_id`)

-- 复合索引建议
KEY `idx_status_priority_tenant` (`status`, `priority`, `tenant_id`)
KEY `idx_product_status_tenant` (`product_id`, `status`, `tenant_id`)
```

### 2. 生产任务表 (jsh_production_task)

**功能描述**: 管理具体的生产任务，一个工单可以分解为多个任务

**核心字段**:
- `task_number`: 任务编号，租户内唯一
- `task_type`: 任务类型（生产、抛光、上色、组装、包装、质检）
- `process_step`: 工序步骤
- `status`: 任务状态（待派单、已派单、进行中、已完成、已质检、已取消）
- `worker_id`: 工人ID，关联用户表

**数量管理**:
- `task_quantity`: 任务数量
- `completed_quantity`: 已完成数量
- `qualified_quantity`: 合格数量
- `defective_quantity`: 不合格数量

**时间管理**:
- `assign_time`: 派单时间
- `start_time`: 开始时间
- `complete_time`: 完成时间
- `plan_start_time`: 计划开始时间
- `plan_end_time`: 计划完成时间

**费用管理**:
- `unit_fee`: 单价工费
- `total_fee`: 总工费
- `estimated_hours`: 预估工时
- `actual_hours`: 实际工时

### 3. 生产报工表 (jsh_production_report)

**功能描述**: 记录生产过程中的详细报工信息

**核心字段**:
- `report_number`: 报工编号，租户内唯一
- `report_type`: 报工类型（进度报工、完工报工、暂停报工、恢复报工）
- `work_date`: 工作日期
- `work_hours`: 工作时长

**质量记录**:
- `completed_quantity`: 完成数量
- `qualified_quantity`: 合格数量
- `defective_quantity`: 不合格数量
- `quality_level`: 质量等级

**内容记录**:
- `work_content`: 工作内容描述
- `problem_description`: 问题描述
- `improvement_suggestion`: 改进建议
- `work_photos`: 工作照片(JSON格式)

### 4. 质量检验表 (jsh_quality_inspection)

**功能描述**: 记录完整的质量检验信息和多维度评分

**核心字段**:
- `inspection_number`: 质检编号，租户内唯一
- `inspection_type`: 质检类型（来料质检、过程质检、最终质检、返工质检）
- `overall_result`: 总体结果（合格、不合格、需返工）
- `quality_grade`: 质量等级（A+、A、B、C、D）

**评分体系**:
- `overall_score`: 综合评分(1-5分)
- `appearance_score`: 外观质量评分
- `size_score`: 尺寸精度评分
- `color_score`: 颜色效果评分
- `texture_score`: 表面质感评分
- `detail_score`: 细节处理评分
- `overall_effect_score`: 整体效果评分

**数量统计**:
- `inspection_quantity`: 质检数量
- `qualified_quantity`: 合格数量
- `defective_quantity`: 不合格数量
- `qualification_rate`: 合格率(%)

### 5. 物流追踪表 (jsh_logistics_tracking)

**功能描述**: 记录全流程物流追踪信息

**核心字段**:
- `tracking_number`: 追踪编号，租户内唯一
- `logistics_type`: 物流类型（原料入库、半成品转移、成品出库、退货）
- `status`: 状态（待发货、运输中、已送达、已收货、异常）

**位置信息**:
- `from_location`: 起始位置
- `to_location`: 目标位置
- `carrier_name`: 承运人/物流公司
- `tracking_code`: 物流单号

**时间节点**:
- `ship_time`: 发货时间
- `estimated_arrival_time`: 预计到达时间
- `actual_arrival_time`: 实际到达时间
- `receive_time`: 收货时间

### 6. 工人技能表 (jsh_worker_skill)

**功能描述**: 管理工人的技能信息和能力评估

**核心字段**:
- `skill_type`: 技能类型（掐丝、点蓝、抛光、上色、组装、包装、质检）
- `skill_level`: 技能等级（新手、中级、高级、专家、大师）
- `skill_score`: 技能评分(1-5分)

**认证信息**:
- `certification_level`: 认证等级
- `certification_date`: 认证日期
- `experience_years`: 经验年限

**薪酬体系**:
- `hourly_rate`: 小时工资
- `piece_rate`: 计件工资
- `efficiency_rate`: 效率系数(%)
- `quality_rate`: 质量系数(%)

### 7. 生产统计表 (jsh_production_statistics)

**功能描述**: 记录多维度的生产统计数据

**统计维度**:
- `stat_date`: 统计日期
- `stat_type`: 统计类型（日统计、周统计、月统计、年统计）
- `workshop_id`: 车间ID
- `worker_id`: 工人ID
- `product_id`: 产品ID

**工单统计**:
- `total_work_orders`: 总工单数
- `completed_work_orders`: 完成工单数
- `pending_work_orders`: 待处理工单数
- `cancelled_work_orders`: 取消工单数

**任务统计**:
- `total_tasks`: 总任务数
- `completed_tasks`: 完成任务数
- `in_progress_tasks`: 进行中任务数
- `pending_tasks`: 待派单任务数

**产量统计**:
- `planned_quantity`: 计划产量
- `actual_quantity`: 实际产量
- `qualified_quantity`: 合格产量
- `defective_quantity`: 不合格产量

**效率统计**:
- `production_efficiency`: 生产效率(%)
- `quality_rate`: 质量合格率(%)
- `on_time_delivery_rate`: 按时交付率(%)

**成本统计**:
- `material_cost`: 材料成本
- `labor_cost`: 人工成本
- `overhead_cost`: 制造费用
- `total_cost`: 总成本

## 表关系图

```
jsh_production_work_order (工单)
    ↓ 1:N
jsh_production_task (任务)
    ↓ 1:N
jsh_production_report (报工)
    ↓ 1:1
jsh_quality_inspection (质检)

jsh_production_work_order (工单)
    ↓ 1:N
jsh_logistics_tracking (物流)

jsh_user (用户)
    ↓ 1:N
jsh_worker_skill (技能)

所有表 → jsh_production_statistics (统计)
```

## 索引优化策略

### 1. 基础索引
- 主键索引：自动创建
- 外键索引：所有外键字段
- 租户索引：tenant_id字段
- 状态索引：status字段
- 时间索引：create_time、update_time等

### 2. 复合索引
- 租户+状态：`(tenant_id, status)`
- 租户+删除标记：`(tenant_id, delete_flag)`
- 业务查询：根据常用查询条件组合

### 3. 唯一索引
- 业务编号：`(编号字段, tenant_id, delete_flag)`
- 确保租户内业务编号唯一

## 数据完整性约束

### 1. 外键约束
- 产品ID → jsh_material.id
- 单位ID → jsh_unit.id
- 用户ID → jsh_user.id
- 工单ID → jsh_production_work_order.id

### 2. 检查约束
- 数量字段 >= 0
- 百分比字段 0-100
- 评分字段 1-5
- 状态字段枚举值

### 3. 业务规则
- 合格数量 + 不合格数量 <= 总数量
- 实际完成时间 >= 实际开始时间
- 计划完成时间 >= 计划开始时间

## 性能优化建议

### 1. 分区策略
- 按时间分区：报工表、统计表
- 按租户分区：大数据量表

### 2. 归档策略
- 历史数据归档：超过1年的数据
- 统计数据保留：永久保留

### 3. 查询优化
- 避免全表扫描
- 合理使用索引
- 分页查询优化
- 统计查询缓存

## 扩展性设计

### 1. 字段扩展
- 预留扩展字段
- JSON字段存储灵活数据
- 支持自定义属性

### 2. 表结构扩展
- 支持新增业务表
- 支持表结构变更
- 向下兼容原则

### 3. 业务扩展
- 支持新的生产模式
- 支持新的任务类型
- 支持新的质检标准
