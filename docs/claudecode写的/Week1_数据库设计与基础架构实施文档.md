# Week 1: 数据库设计与基础架构实施文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-17
- **项目阶段**: 第一阶段 - 生产制作管理模块
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》

---

## 概述

本文档为聆花文化ERP生产制作管理模块第一周的开发任务提供详细的实施指导。本周主要完成数据库表结构设计和基础项目架构搭建，为后续开发工作奠定坚实基础。

---

## 数据库设计任务 (3天)

### 1. 生产工单主表设计

**表名**: `jsh_production_order`

```sql
CREATE TABLE `jsh_production_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_no` varchar(50) NOT NULL COMMENT '生产工单号',
  `sale_order_id` bigint(20) DEFAULT NULL COMMENT '关联销售订单ID',
  `product_name` varchar(100) NOT NULL COMMENT '产品名称',
  `product_spec` varchar(200) DEFAULT NULL COMMENT '产品规格',
  `quantity` decimal(24,6) NOT NULL COMMENT '生产数量',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `production_cost` decimal(24,6) DEFAULT NULL COMMENT '生产成本',
  `worker_id` bigint(20) DEFAULT NULL COMMENT '分配制作人员ID',
  `workshop_id` bigint(20) DEFAULT NULL COMMENT '车间ID',
  `plan_start_date` datetime DEFAULT NULL COMMENT '计划开工日期',
  `plan_finish_date` datetime DEFAULT NULL COMMENT '计划完工日期',
  `actual_start_date` datetime DEFAULT NULL COMMENT '实际开工日期',
  `actual_finish_date` datetime DEFAULT NULL COMMENT '实际完工日期',
  `status` varchar(1) DEFAULT '0' COMMENT '状态：0待分配，1进行中，2已完工，3已交付',
  `priority` int(2) DEFAULT '5' COMMENT '优先级：1最高，10最低',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no_tenant` (`order_no`, `tenant_id`),
  KEY `idx_sale_order` (`sale_order_id`),
  KEY `idx_worker` (`worker_id`),
  KEY `idx_status_tenant` (`status`, `tenant_id`),
  KEY `idx_plan_date` (`plan_start_date`, `plan_finish_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产工单主表';
```

**设计要点**:
- 遵循jshERP多租户架构，包含`tenant_id`和`delete_flag`字段
- 工单号采用`order_no + tenant_id`联合唯一索引确保租户内唯一
- 支持与销售订单关联，实现订单驱动生产
- 状态字段支持完整的生产流程管理
- 优先级字段支持生产调度优化

### 2. 工单物料清单表设计

**表名**: `jsh_production_material`

```sql
CREATE TABLE `jsh_production_material` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `production_order_id` bigint(20) NOT NULL COMMENT '生产工单ID',
  `material_id` bigint(20) NOT NULL COMMENT '物料ID',
  `material_name` varchar(100) NOT NULL COMMENT '物料名称',
  `material_spec` varchar(200) DEFAULT NULL COMMENT '物料规格',
  `required_quantity` decimal(24,6) NOT NULL COMMENT '需求数量',
  `allocated_quantity` decimal(24,6) DEFAULT '0' COMMENT '已分配数量',
  `consumed_quantity` decimal(24,6) DEFAULT '0' COMMENT '已消耗数量',
  `unit` varchar(20) DEFAULT NULL COMMENT '单位',
  `unit_cost` decimal(24,6) DEFAULT NULL COMMENT '单位成本',
  `total_cost` decimal(24,6) DEFAULT NULL COMMENT '总成本',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID（委外采购）',
  `source_type` varchar(1) DEFAULT '1' COMMENT '来源类型：1库存，2采购，3委外',
  `reserve_status` varchar(1) DEFAULT '0' COMMENT '预留状态：0未预留，1已预留',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_production_order` (`production_order_id`),
  KEY `idx_material` (`material_id`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工单物料清单表';
```

**设计要点**:
- 支持物料需求分解和成本核算
- 区分需求、分配、消耗三种数量状态
- 支持多种物料来源：库存、采购、委外
- 预留状态支持库存管理集成

### 3. 移动端报工记录表设计

**表名**: `jsh_work_report`

```sql
CREATE TABLE `jsh_work_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `report_no` varchar(50) NOT NULL COMMENT '报工单号',
  `production_order_id` bigint(20) NOT NULL COMMENT '生产工单ID',
  `worker_id` bigint(20) NOT NULL COMMENT '报工人员ID',
  `worker_name` varchar(50) NOT NULL COMMENT '报工人员姓名',
  `work_date` date NOT NULL COMMENT '作业日期',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `work_hours` decimal(8,2) DEFAULT NULL COMMENT '工作时长（小时）',
  `completed_quantity` decimal(24,6) DEFAULT NULL COMMENT '完成数量',
  `qualified_quantity` decimal(24,6) DEFAULT NULL COMMENT '合格数量',
  `defective_quantity` decimal(24,6) DEFAULT NULL COMMENT '不良数量',
  `work_content` varchar(500) DEFAULT NULL COMMENT '工作内容',
  `quality_notes` varchar(500) DEFAULT NULL COMMENT '质量说明',
  `photo_urls` text DEFAULT NULL COMMENT '制作照片URLs（JSON数组）',
  `location_info` varchar(200) DEFAULT NULL COMMENT '位置信息',
  `device_info` varchar(100) DEFAULT NULL COMMENT '设备信息',
  `is_completed` varchar(1) DEFAULT '0' COMMENT '是否完工：0进行中，1已完工',
  `approval_status` varchar(1) DEFAULT '0' COMMENT '审核状态：0待审核，1已审核，2已驳回',
  `approval_user` bigint(20) DEFAULT NULL COMMENT '审核人',
  `approval_time` datetime DEFAULT NULL COMMENT '审核时间',
  `approval_notes` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_report_no_tenant` (`report_no`, `tenant_id`),
  KEY `idx_production_order` (`production_order_id`),
  KEY `idx_worker_date` (`worker_id`, `work_date`),
  KEY `idx_approval_status` (`approval_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='移动端报工记录表';
```

**设计要点**:
- 支持移动端实时报工和照片上传
- 记录详细的作业时间和质量信息
- 支持位置和设备信息追踪
- 包含完整的审核流程

### 4. 物流追踪表设计

**表名**: `jsh_logistics_track`

```sql
CREATE TABLE `jsh_logistics_track` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tracking_no` varchar(50) NOT NULL COMMENT '追踪单号',
  `production_order_id` bigint(20) DEFAULT NULL COMMENT '生产工单ID',
  `shipment_type` varchar(1) NOT NULL COMMENT '发货类型：1半成品发出，2成品发出，3物料配送',
  `sender_info` varchar(200) NOT NULL COMMENT '发货方信息',
  `receiver_info` varchar(200) NOT NULL COMMENT '收货方信息',
  `carrier_company` varchar(50) DEFAULT NULL COMMENT '承运公司',
  `carrier_contact` varchar(100) DEFAULT NULL COMMENT '承运联系方式',
  `estimated_delivery` datetime DEFAULT NULL COMMENT '预计送达时间',
  `actual_delivery` datetime DEFAULT NULL COMMENT '实际送达时间',
  `tracking_status` varchar(1) DEFAULT '0' COMMENT '物流状态：0待发货，1已发货，2运输中，3已到达，4已签收',
  `package_info` text DEFAULT NULL COMMENT '包装信息（JSON）',
  `tracking_details` text DEFAULT NULL COMMENT '物流详情（JSON数组）',
  `delivery_photos` text DEFAULT NULL COMMENT '送货照片URLs（JSON数组）',
  `signature_photo` varchar(200) DEFAULT NULL COMMENT '签收照片URL',
  `notes` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tracking_no` (`tracking_no`),
  KEY `idx_production_order` (`production_order_id`),
  KEY `idx_status` (`tracking_status`),
  KEY `idx_delivery_date` (`estimated_delivery`, `actual_delivery`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流追踪表';
```

**设计要点**:
- 支持多种发货类型的物流追踪
- 集成第三方物流API的数据存储
- 支持照片和签收信息记录
- JSON字段存储复杂的物流详情

### 5. 工艺流程模板表设计

**表名**: `jsh_process_template`

```sql
CREATE TABLE `jsh_process_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_code` varchar(50) NOT NULL COMMENT '模板编码',
  `product_category` varchar(50) DEFAULT NULL COMMENT '适用产品类别',
  `process_steps` text NOT NULL COMMENT '工艺步骤（JSON数组）',
  `estimated_hours` decimal(8,2) DEFAULT NULL COMMENT '预计工时',
  `skill_requirements` varchar(500) DEFAULT NULL COMMENT '技能要求',
  `equipment_requirements` varchar(500) DEFAULT NULL COMMENT '设备要求',
  `quality_standards` text DEFAULT NULL COMMENT '质量标准（JSON）',
  `safety_notes` varchar(500) DEFAULT NULL COMMENT '安全注意事项',
  `version` varchar(10) DEFAULT '1.0' COMMENT '版本号',
  `status` varchar(1) DEFAULT '1' COMMENT '状态：0禁用，1启用',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code_tenant` (`template_code`, `tenant_id`),
  KEY `idx_category` (`product_category`),
  KEY `idx_status_tenant` (`status`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺流程模板表';
```

**设计要点**:
- 支持工艺流程标准化管理
- JSON字段存储复杂的工艺步骤和质量标准
- 版本控制支持工艺流程优化迭代
- 支持产品类别关联

### 6. 数据库索引优化策略

```sql
-- 性能优化索引
-- 生产工单查询优化
CREATE INDEX idx_production_status_date ON jsh_production_order (status, plan_start_date, tenant_id);
CREATE INDEX idx_production_worker_status ON jsh_production_order (worker_id, status, tenant_id);

-- 物料需求查询优化
CREATE INDEX idx_material_source_reserve ON jsh_production_material (source_type, reserve_status, tenant_id);

-- 报工记录查询优化
CREATE INDEX idx_work_report_date_worker ON jsh_work_report (work_date, worker_id, tenant_id);
CREATE INDEX idx_work_report_approval ON jsh_work_report (approval_status, create_time, tenant_id);

-- 物流追踪查询优化
CREATE INDEX idx_logistics_status_time ON jsh_logistics_track (tracking_status, create_time);
```

### 7. 数据库初始化脚本

**文件路径**: `jshERP-boot/docs/production_module_init.sql`

```sql
-- 权限配置
INSERT INTO jsh_function (id, name, type, url, component, icon, push, parent_id, sort, status, enabled, create_time, update_time, tenant_id) VALUES
(NULL, '生产管理', 0, '/production', 'BasicLayout', 'tool', '0', 0, 15, '1', '1', NOW(), NOW(), NULL),
(NULL, '生产工单', 1, '/production/order', 'views/production/ProductionOrderList', 'file-text', '0', LAST_INSERT_ID(), 1, '1', '1', NOW(), NOW(), NULL),
(NULL, '移动报工', 1, '/production/mobile', 'views/production/mobile/WorkReport', 'mobile', '0', LAST_INSERT_ID()-1, 2, '1', '1', NOW(), NOW(), NULL),
(NULL, '物流追踪', 1, '/production/logistics', 'views/production/LogisticsTrack', 'car', '0', LAST_INSERT_ID()-2, 3, '1', '1', NOW(), NOW(), NULL);

-- 系统配置
INSERT INTO jsh_system_config (config_key, config_value, config_desc, tenant_id) VALUES
('production.auto_generate_order_no', 'true', '是否自动生成工单号', NULL),
('production.order_no_prefix', 'PO', '工单号前缀', NULL),
('production.default_work_hours', '8', '默认工作时长（小时）', NULL),
('production.photo_upload_limit', '10', '报工照片上传数量限制', NULL);

-- 示例数据（可选）
INSERT INTO jsh_process_template (template_name, template_code, product_category, process_steps, estimated_hours, skill_requirements, equipment_requirements, quality_standards, version, status, tenant_id) VALUES
('壮锦基础工艺', 'ZJ_BASIC', '壮锦', '[{"step":1,"name":"原料准备","description":"选择优质棉线","estimated_time":2},{"step":2,"name":"染色处理","description":"按照传统工艺染色","estimated_time":4},{"step":3,"name":"织造工艺","description":"使用传统织机织造","estimated_time":12},{"step":4,"name":"质量检验","description":"检查织物质量","estimated_time":1}]', 19, '传统织造技能', '传统织机', '{"color_consistency":"色彩一致性要求95%以上","pattern_accuracy":"图案准确度要求98%以上"}', '1.0', '1', NULL);
```

---

## 基础架构搭建 (2天)

### 1. 创建生产模块包结构

按照jshERP标准架构创建模块包：

```
jshERP-boot/src/main/java/com/jsh/erp/production/
├── controller/              # 控制层
│   ├── ProductionOrderController.java
│   ├── WorkReportController.java
│   ├── LogisticsTrackController.java
│   └── ProcessTemplateController.java
├── service/                 # 服务层
│   ├── ProductionOrderService.java
│   ├── ProductionMaterialService.java
│   ├── WorkReportService.java
│   ├── LogisticsTrackService.java
│   └── ProcessTemplateService.java
├── datasource/              # 数据源层
│   ├── entities/            # 实体类
│   │   ├── ProductionOrder.java
│   │   ├── ProductionMaterial.java
│   │   ├── WorkReport.java
│   │   ├── LogisticsTrack.java
│   │   └── ProcessTemplate.java
│   ├── mappers/             # Mapper接口
│   │   ├── ProductionOrderMapper.java
│   │   ├── ProductionOrderMapperEx.java
│   │   ├── ProductionMaterialMapper.java
│   │   ├── ProductionMaterialMapperEx.java
│   │   ├── WorkReportMapper.java
│   │   ├── WorkReportMapperEx.java
│   │   ├── LogisticsTrackMapper.java
│   │   ├── LogisticsTrackMapperEx.java
│   │   ├── ProcessTemplateMapper.java
│   │   └── ProcessTemplateMapperEx.java
│   └── vo/                  # 视图对象
│       ├── ProductionOrderVo4List.java
│       ├── WorkReportVo4List.java
│       └── LogisticsTrackVo4List.java
└── utils/                   # 工具类
    ├── ProductionOrderNoGenerator.java
    ├── WorkTimeCalculator.java
    └── LogisticsApiClient.java
```

### 2. MyBatis映射文件配置

**配置路径**: `jshERP-boot/src/main/resources/mapper_xml/production/`

```
mapper_xml/production/
├── ProductionOrderMapperEx.xml
├── ProductionMaterialMapperEx.xml
├── WorkReportMapperEx.xml
├── LogisticsTrackMapperEx.xml
└── ProcessTemplateMapperEx.xml
```

### 3. 多租户支持配置

**配置文件更新**: `application.properties`

```properties
# 生产模块多租户配置
tenant.isolation.production.tables=jsh_production_order,jsh_production_material,jsh_work_report,jsh_logistics_track,jsh_process_template
tenant.isolation.production.enabled=true

# 生产模块特定配置
production.module.enabled=true
production.mobile.enabled=true
production.logistics.api.enabled=true
```

### 4. 基础实体类设计

**ProductionOrder.java示例**:

```java
package com.jsh.erp.production.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产工单实体类
 * 遵循jshERP实体设计规范
 */
public class ProductionOrder {
    private Long id;
    private String orderNo;
    private Long saleOrderId;
    private String productName;
    private String productSpec;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal productionCost;
    private Long workerId;
    private Long workshopId;
    private Date planStartDate;
    private Date planFinishDate;
    private Date actualStartDate;
    private Date actualFinishDate;
    private String status;
    private Integer priority;
    private String remark;
    
    // 多租户和基础字段
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Long createUser;
    private Date updateTime;
    private Long updateUser;
    
    // getter和setter方法...
}
```

### 5. 基础枚举类定义

**ProductionOrderStatus.java**:

```java
package com.jsh.erp.production.constants;

/**
 * 生产工单状态枚举
 */
public enum ProductionOrderStatus {
    PENDING("0", "待分配"),
    IN_PROGRESS("1", "进行中"),
    COMPLETED("2", "已完工"),
    DELIVERED("3", "已交付");
    
    private final String code;
    private final String description;
    
    ProductionOrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() { return code; }
    public String getDescription() { return description; }
}
```

---

## 验收标准

### 数据库设计验收标准
1. ✅ 所有表结构符合jshERP命名规范
2. ✅ 多租户字段(`tenant_id`)和逻辑删除字段(`delete_flag`)完整
3. ✅ 索引设计合理，查询性能良好
4. ✅ 外键关系正确，数据一致性约束完善
5. ✅ 初始化脚本可正常执行

### 基础架构验收标准
1. ✅ 包结构符合jshERP三层架构标准
2. ✅ 实体类包含完整的多租户支持
3. ✅ MyBatis映射文件配置正确
4. ✅ 基础枚举和常量定义完善
5. ✅ 项目可正常编译和启动

---

## 风险控制

### 技术风险
- **数据库设计变更风险**: 采用版本化SQL脚本，支持增量更新
- **多租户数据隔离风险**: 严格按照jshERP标准实现，确保数据安全
- **性能风险**: 提前设计索引策略，关键查询进行性能测试

### 进度风险
- **依赖风险**: 确保MySQL和Redis环境准备就绪
- **资源风险**: 数据库设计需要业务专家参与评审
- **变更风险**: 冻结需求，避免频繁变更影响进度

---

## 交付物清单

1. **数据库设计文档**: 包含完整的表结构和索引设计
2. **数据库初始化脚本**: `production_module_init.sql`
3. **基础项目结构**: 完整的包结构和基础类
4. **配置文件更新**: 多租户和模块配置
5. **技术文档**: 实体类和枚举设计说明

---

## 下周准备工作

1. **环境准备**: 确保开发环境数据库已更新至最新结构
2. **代码生成**: 使用MyBatis Generator生成基础CRUD代码
3. **团队培训**: 组织团队学习生产模块业务逻辑
4. **接口设计**: 准备第二周的API接口设计文档

---

*本文档严格遵循《jshERP_二次开发技术参考手册》的架构规范和最佳实践，确保与现有系统的完美集成。*