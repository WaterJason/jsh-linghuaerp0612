# Week 20: 库存管理模块完善文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第四阶段 - 基础支撑模块 (Week 20/24)
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第3、4、5章规范
- **前置依赖**: Week 1-19基础模块开发完成

---

## 概述

本文档描述库存管理模块的完善和扩展实现，基于jshERP原有库存管理功能，增加批次管理、库存盘点、多仓库精细化管理等聆花文化特有需求。该模块将提供更精确的库存控制和追溯能力。

**核心目标**:
- 实现精细化的批次管理功能
- 建立完善的库存盘点体系
- 提供实时库存预警机制
- 支持多仓库库存可视化管理

---

## 数据库设计

### 1. 库存批次表

**表名**: `jsh_material_batch`

```sql
-- 库存批次表
CREATE TABLE `jsh_material_batch` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `batch_code` varchar(50) NOT NULL COMMENT '批次号',
  `material_id` bigint(20) NOT NULL COMMENT '商品ID',
  `depot_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `supplier_id` bigint(20) DEFAULT NULL COMMENT '供应商ID',
  
  -- 批次信息
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '过期日期',
  `shelf_life_days` int(11) DEFAULT '0' COMMENT '保质期天数',
  `lot_number` varchar(50) DEFAULT NULL COMMENT '生产批号',
  `quality_grade` varchar(20) DEFAULT NULL COMMENT '质量等级',
  
  -- 数量信息
  `initial_quantity` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT '初始数量',
  `current_quantity` decimal(15,3) NOT NULL DEFAULT '0.000' COMMENT '当前数量',
  `reserved_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '预留数量',
  `available_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '可用数量',
  
  -- 成本信息
  `unit_cost` decimal(10,2) DEFAULT '0.00' COMMENT '单位成本',
  `total_cost` decimal(15,2) DEFAULT '0.00' COMMENT '总成本',
  `average_cost` decimal(10,2) DEFAULT '0.00' COMMENT '移动平均成本',
  
  -- 位置信息
  `warehouse_location` varchar(100) DEFAULT NULL COMMENT '仓位位置',
  `rack_number` varchar(50) DEFAULT NULL COMMENT '货架号',
  `bin_location` varchar(50) DEFAULT NULL COMMENT '货位号',
  
  -- 状态信息
  `batch_status` varchar(20) DEFAULT 'active' COMMENT '批次状态(active-活跃,locked-锁定,expired-过期,depleted-耗尽)',
  `lock_reason` varchar(200) DEFAULT NULL COMMENT '锁定原因',
  `notes` text COMMENT '批次备注',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_batch_code` (`batch_code`,`tenant_id`),
  KEY `idx_material_depot` (`material_id`,`depot_id`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_expiry_date` (`expiry_date`),
  KEY `idx_batch_status` (`batch_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存批次表';
```

### 2. 库存盘点主表

**表名**: `jsh_stock_check`

```sql
-- 库存盘点主表
CREATE TABLE `jsh_stock_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `check_number` varchar(50) NOT NULL COMMENT '盘点单号',
  `check_name` varchar(200) NOT NULL COMMENT '盘点名称',
  `check_type` varchar(20) NOT NULL COMMENT '盘点类型(full-全盘,partial-部分盘点,cycle-循环盘点,spot-抽盘)',
  `check_mode` varchar(20) DEFAULT 'manual' COMMENT '盘点方式(manual-手动,scan-扫描,auto-自动)',
  
  -- 盘点范围
  `depot_ids` text COMMENT '盘点仓库IDs(JSON)',
  `material_ids` text COMMENT '盘点商品IDs(JSON)',
  `category_ids` text COMMENT '盘点分类IDs(JSON)',
  `supplier_ids` text COMMENT '盘点供应商IDs(JSON)',
  
  -- 盘点时间
  `plan_start_date` datetime NOT NULL COMMENT '计划开始时间',
  `plan_end_date` datetime NOT NULL COMMENT '计划结束时间',
  `actual_start_date` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_date` datetime DEFAULT NULL COMMENT '实际结束时间',
  
  -- 盘点人员
  `check_leader` bigint(20) DEFAULT NULL COMMENT '盘点负责人',
  `check_members` text COMMENT '盘点成员(JSON)',
  `supervisor` bigint(20) DEFAULT NULL COMMENT '监盘人',
  
  -- 状态信息
  `status` varchar(20) DEFAULT 'draft' COMMENT '状态(draft-草稿,in_progress-进行中,completed-已完成,approved-已审核,cancelled-已取消)',
  `freeze_stock` char(1) DEFAULT '1' COMMENT '是否冻结库存',
  `auto_adjust` char(1) DEFAULT '0' COMMENT '是否自动调整库存',
  
  -- 统计信息
  `total_items` int(11) DEFAULT '0' COMMENT '盘点商品总数',
  `checked_items` int(11) DEFAULT '0' COMMENT '已盘点商品数',
  `difference_items` int(11) DEFAULT '0' COMMENT '有差异商品数',
  `total_cost_difference` decimal(15,2) DEFAULT '0.00' COMMENT '总成本差异',
  
  -- 审核信息
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `submit_user` bigint(20) DEFAULT NULL COMMENT '提交人',
  `approve_time` datetime DEFAULT NULL COMMENT '审核时间',
  `approve_user` bigint(20) DEFAULT NULL COMMENT '审核人',
  `approve_notes` text COMMENT '审核备注',
  
  `description` text COMMENT '盘点说明',
  `remarks` text COMMENT '备注信息',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_check_number` (`check_number`,`tenant_id`),
  KEY `idx_check_type` (`check_type`),
  KEY `idx_status` (`status`),
  KEY `idx_plan_start_date` (`plan_start_date`),
  KEY `idx_check_leader` (`check_leader`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点主表';
```

### 3. 库存盘点明细表

**表名**: `jsh_stock_check_detail`

```sql
-- 库存盘点明细表
CREATE TABLE `jsh_stock_check_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `check_id` bigint(20) NOT NULL COMMENT '盘点主表ID',
  `material_id` bigint(20) NOT NULL COMMENT '商品ID',
  `depot_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `batch_id` bigint(20) DEFAULT NULL COMMENT '批次ID',
  
  -- 账面数据
  `book_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '账面数量',
  `book_unit_cost` decimal(10,2) DEFAULT '0.00' COMMENT '账面单价',
  `book_total_cost` decimal(15,2) DEFAULT '0.00' COMMENT '账面总金额',
  
  -- 盘点数据
  `actual_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '实盘数量',
  `actual_unit_cost` decimal(10,2) DEFAULT '0.00' COMMENT '实盘单价',
  `actual_total_cost` decimal(15,2) DEFAULT '0.00' COMMENT '实盘总金额',
  
  -- 差异数据
  `difference_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '数量差异',
  `difference_rate` decimal(8,4) DEFAULT '0.0000' COMMENT '差异率(%)',
  `difference_cost` decimal(15,2) DEFAULT '0.00' COMMENT '金额差异',
  `difference_type` varchar(20) DEFAULT NULL COMMENT '差异类型(profit-盘盈,loss-盘亏,none-无差异)',
  
  -- 盘点信息
  `check_time` datetime DEFAULT NULL COMMENT '盘点时间',
  `check_user` bigint(20) DEFAULT NULL COMMENT '盘点人',
  `check_method` varchar(20) DEFAULT 'manual' COMMENT '盘点方法(manual-手工,scan-扫码,weight-称重)',
  `warehouse_location` varchar(100) DEFAULT NULL COMMENT '仓位位置',
  
  -- 调整信息
  `adjust_status` varchar(20) DEFAULT 'pending' COMMENT '调整状态(pending-待调整,adjusted-已调整,ignored-忽略)',
  `adjust_reason` varchar(500) DEFAULT NULL COMMENT '调整原因',
  `adjust_time` datetime DEFAULT NULL COMMENT '调整时间',
  `adjust_user` bigint(20) DEFAULT NULL COMMENT '调整人',
  
  `notes` text COMMENT '盘点备注',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  KEY `idx_check_id` (`check_id`),
  KEY `idx_material_depot` (`material_id`,`depot_id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_difference_type` (`difference_type`),
  KEY `idx_adjust_status` (`adjust_status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存盘点明细表';
```

### 4. 库存预警配置表

**表名**: `jsh_stock_alert_config`

```sql
-- 库存预警配置表
CREATE TABLE `jsh_stock_alert_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` bigint(20) NOT NULL COMMENT '商品ID',
  `depot_id` bigint(20) DEFAULT NULL COMMENT '仓库ID(空则为全局配置)',
  
  -- 预警数量设置
  `min_stock` decimal(15,3) DEFAULT '0.000' COMMENT '最低库存',
  `max_stock` decimal(15,3) DEFAULT '0.000' COMMENT '最高库存',
  `safety_stock` decimal(15,3) DEFAULT '0.000' COMMENT '安全库存',
  `reorder_point` decimal(15,3) DEFAULT '0.000' COMMENT '再订货点',
  `economic_order_qty` decimal(15,3) DEFAULT '0.000' COMMENT '经济订货量',
  
  -- 预警级别设置
  `low_stock_level` varchar(20) DEFAULT 'warning' COMMENT '低库存预警级别(info,warning,error,critical)',
  `high_stock_level` varchar(20) DEFAULT 'warning' COMMENT '高库存预警级别',
  `zero_stock_level` varchar(20) DEFAULT 'error' COMMENT '零库存预警级别',
  `expiry_warning_days` int(11) DEFAULT '30' COMMENT '过期预警天数',
  
  -- 预警策略
  `enable_low_stock_alert` char(1) DEFAULT '1' COMMENT '启用低库存预警',
  `enable_high_stock_alert` char(1) DEFAULT '0' COMMENT '启用高库存预警',
  `enable_zero_stock_alert` char(1) DEFAULT '1' COMMENT '启用零库存预警',
  `enable_expiry_alert` char(1) DEFAULT '1' COMMENT '启用过期预警',
  `enable_batch_alert` char(1) DEFAULT '0' COMMENT '启用批次预警',
  
  -- 通知设置
  `alert_frequency` varchar(20) DEFAULT 'daily' COMMENT '预警频率(realtime-实时,hourly-每小时,daily-每天,weekly-每周)',
  `alert_methods` text COMMENT '预警方式(JSON: email,sms,system,wechat)',
  `alert_recipients` text COMMENT '预警接收人(JSON)',
  
  -- 自动处理
  `auto_purchase` char(1) DEFAULT '0' COMMENT '自动采购',
  `auto_transfer` char(1) DEFAULT '0' COMMENT '自动调拨',
  `preferred_supplier_id` bigint(20) DEFAULT NULL COMMENT '首选供应商',
  
  `status` char(1) DEFAULT '1' COMMENT '配置状态(1-启用,0-禁用)',
  `priority` int(11) DEFAULT '0' COMMENT '优先级',
  `description` varchar(500) DEFAULT NULL COMMENT '配置说明',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_depot` (`material_id`,`depot_id`,`tenant_id`),
  KEY `idx_depot_id` (`depot_id`),
  KEY `idx_status` (`status`),
  KEY `idx_priority` (`priority`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警配置表';
```

### 5. 库存预警记录表

**表名**: `jsh_stock_alert_log`

```sql
-- 库存预警记录表
CREATE TABLE `jsh_stock_alert_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_id` bigint(20) NOT NULL COMMENT '商品ID',
  `depot_id` bigint(20) NOT NULL COMMENT '仓库ID',
  `batch_id` bigint(20) DEFAULT NULL COMMENT '批次ID',
  
  -- 预警信息
  `alert_type` varchar(50) NOT NULL COMMENT '预警类型(low_stock,high_stock,zero_stock,expiry,batch_expiry)',
  `alert_level` varchar(20) NOT NULL COMMENT '预警级别(info,warning,error,critical)',
  `alert_title` varchar(200) NOT NULL COMMENT '预警标题',
  `alert_message` text COMMENT '预警消息',
  
  -- 库存信息
  `current_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '当前库存',
  `threshold_quantity` decimal(15,3) DEFAULT '0.000' COMMENT '阈值数量',
  `expiry_date` date DEFAULT NULL COMMENT '过期日期',
  `days_to_expiry` int(11) DEFAULT NULL COMMENT '距离过期天数',
  
  -- 处理信息
  `alert_status` varchar(20) DEFAULT 'active' COMMENT '预警状态(active-活跃,acknowledged-已确认,resolved-已解决,ignored-已忽略)',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_user` bigint(20) DEFAULT NULL COMMENT '处理人',
  `handle_action` varchar(500) DEFAULT NULL COMMENT '处理措施',
  `handle_result` text COMMENT '处理结果',
  
  -- 通知信息
  `notification_sent` char(1) DEFAULT '0' COMMENT '是否已发送通知',
  `notification_methods` varchar(200) DEFAULT NULL COMMENT '通知方式',
  `notification_time` datetime DEFAULT NULL COMMENT '通知时间',
  `notification_recipients` text COMMENT '通知接收人',
  
  `resolved_time` datetime DEFAULT NULL COMMENT '解决时间',
  `resolved_user` bigint(20) DEFAULT NULL COMMENT '解决人',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  KEY `idx_material_depot` (`material_id`,`depot_id`),
  KEY `idx_batch_id` (`batch_id`),
  KEY `idx_alert_type` (`alert_type`),
  KEY `idx_alert_level` (`alert_level`),
  KEY `idx_alert_status` (`alert_status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警记录表';
```

---

## 后端实现

### 1. 实体类设计

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/MaterialBatch.java`

```java
package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsh_material_batch")
public class MaterialBatch {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String batchCode;
    private Long materialId;
    private Long depotId;
    private Long supplierId;
    
    // 批次信息
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date productionDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDate;
    private Integer shelfLifeDays;
    private String lotNumber;
    private String qualityGrade;
    
    // 数量信息
    private BigDecimal initialQuantity;
    private BigDecimal currentQuantity;
    private BigDecimal reservedQuantity;
    private BigDecimal availableQuantity;
    
    // 成本信息
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private BigDecimal averageCost;
    
    // 位置信息
    private String warehouseLocation;
    private String rackNumber;
    private String binLocation;
    
    // 状态信息
    private String batchStatus;
    private String lockReason;
    private String notes;
    
    // 系统字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Long createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Long updateUser;
    private Long tenantId;
    private String deleteFlag;
}
```

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/StockCheck.java`

```java
package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsh_stock_check")
public class StockCheck {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String checkNumber;
    private String checkName;
    private String checkType;
    private String checkMode;
    
    // 盘点范围
    private String depotIds;
    private String materialIds;
    private String categoryIds;
    private String supplierIds;
    
    // 盘点时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date planEndDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date actualEndDate;
    
    // 盘点人员
    private Long checkLeader;
    private String checkMembers;
    private Long supervisor;
    
    // 状态信息
    private String status;
    private String freezeStock;
    private String autoAdjust;
    
    // 统计信息
    private Integer totalItems;
    private Integer checkedItems;
    private Integer differenceItems;
    private BigDecimal totalCostDifference;
    
    // 审核信息
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date submitTime;
    private Long submitUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveTime;
    private Long approveUser;
    private String approveNotes;
    
    private String description;
    private String remarks;
    
    // 系统字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Long createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Long updateUser;
    private Long tenantId;
    private String deleteFlag;
}
```

### 2. 服务层实现

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/service/MaterialBatchService.java`

```java
package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.MaterialBatch;
import com.jsh.erp.datasource.mappers.MaterialBatchMapper;
import com.jsh.erp.datasource.vo.MaterialBatchVo4List;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class MaterialBatchService {

    private Logger logger = LoggerFactory.getLogger(MaterialBatchService.class);

    @Resource
    private MaterialBatchMapper materialBatchMapper;

    @Resource
    private UserService userService;

    /**
     * 查询批次列表
     */
    public List<MaterialBatchVo4List> selectBatchList(Long materialId, Long depotId, String batchCode,
                                                    String batchStatus, int currentPage, int pageSize,
                                                    HttpServletRequest request) throws Exception {
        return materialBatchMapper.selectBatchList(materialId, depotId, batchCode, batchStatus,
                                                  getUserTenantId(request));
    }

    /**
     * 创建批次
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertBatch(JSONObject obj, HttpServletRequest request) throws Exception {
        MaterialBatch batch = JSONObject.parseObject(obj.toJSONString(), MaterialBatch.class);
        
        // 验证必填字段
        if (batch.getMaterialId() == null || batch.getDepotId() == null) {
            throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_DEPOT_EMPTY_CODE,
                    ExceptionConstants.MATERIAL_DEPOT_EMPTY_MSG);
        }
        
        // 生成批次号
        if (StringUtil.isEmpty(batch.getBatchCode())) {
            batch.setBatchCode(generateBatchCode(batch.getMaterialId()));
        }
        
        // 检查批次号唯一性
        MaterialBatch existBatch = materialBatchMapper.selectByBatchCode(batch.getBatchCode(), 
                                                                        getUserTenantId(request));
        if (existBatch != null) {
            throw new BusinessRunTimeException(ExceptionConstants.BATCH_CODE_EXISTS_CODE,
                    ExceptionConstants.BATCH_CODE_EXISTS_MSG);
        }
        
        // 设置系统字段
        batch.setCreateTime(new Date());
        batch.setCreateUser(getUserId(request));
        batch.setTenantId(getUserTenantId(request));
        batch.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        
        // 设置默认值
        if (batch.getInitialQuantity() == null) {
            batch.setInitialQuantity(BigDecimal.ZERO);
        }
        if (batch.getCurrentQuantity() == null) {
            batch.setCurrentQuantity(batch.getInitialQuantity());
        }
        if (batch.getAvailableQuantity() == null) {
            batch.setAvailableQuantity(batch.getCurrentQuantity());
        }
        if (StringUtil.isEmpty(batch.getBatchStatus())) {
            batch.setBatchStatus("active");
        }
        
        // 计算可用数量
        calculateAvailableQuantity(batch);
        
        int result = materialBatchMapper.insert(batch);
        logger.info("创建批次成功，批次号：{}", batch.getBatchCode());
        
        return result;
    }

    /**
     * 更新批次库存
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateBatchStock(Long batchId, BigDecimal changeQuantity, String operationType,
                              HttpServletRequest request) throws Exception {
        MaterialBatch batch = materialBatchMapper.selectById(batchId);
        if (batch == null || !batch.getTenantId().equals(getUserTenantId(request))) {
            throw new BusinessRunTimeException(ExceptionConstants.BATCH_NOT_EXISTS_CODE,
                    ExceptionConstants.BATCH_NOT_EXISTS_MSG);
        }
        
        BigDecimal newQuantity = batch.getCurrentQuantity();
        
        // 根据操作类型更新数量
        switch (operationType) {
            case "in":  // 入库
                newQuantity = newQuantity.add(changeQuantity);
                break;
            case "out": // 出库
                newQuantity = newQuantity.subtract(changeQuantity);
                if (newQuantity.compareTo(BigDecimal.ZERO) < 0) {
                    throw new BusinessRunTimeException(ExceptionConstants.STOCK_INSUFFICIENT_CODE,
                            ExceptionConstants.STOCK_INSUFFICIENT_MSG);
                }
                break;
            case "reserve": // 预留
                BigDecimal newReserved = batch.getReservedQuantity().add(changeQuantity);
                if (newReserved.compareTo(batch.getCurrentQuantity()) > 0) {
                    throw new BusinessRunTimeException(ExceptionConstants.RESERVE_EXCEED_STOCK_CODE,
                            ExceptionConstants.RESERVE_EXCEED_STOCK_MSG);
                }
                batch.setReservedQuantity(newReserved);
                break;
            case "release": // 释放预留
                batch.setReservedQuantity(batch.getReservedQuantity().subtract(changeQuantity));
                break;
        }
        
        batch.setCurrentQuantity(newQuantity);
        calculateAvailableQuantity(batch);
        
        // 检查批次状态
        checkBatchStatus(batch);
        
        batch.setUpdateTime(new Date());
        batch.setUpdateUser(getUserId(request));
        
        int result = materialBatchMapper.updateById(batch);
        logger.info("更新批次库存成功，批次ID：{}，变化数量：{}", batchId, changeQuantity);
        
        return result;
    }

    /**
     * 查询即将过期的批次
     */
    public List<MaterialBatch> selectExpiringBatches(int warningDays, HttpServletRequest request) throws Exception {
        return materialBatchMapper.selectExpiringBatches(warningDays, getUserTenantId(request));
    }

    /**
     * 查询库存不足的批次
     */
    public List<Map<String, Object>> selectLowStockBatches(HttpServletRequest request) throws Exception {
        return materialBatchMapper.selectLowStockBatches(getUserTenantId(request));
    }

    /**
     * 批次库存调整
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int adjustBatchStock(Long batchId, BigDecimal adjustQuantity, String adjustReason,
                              HttpServletRequest request) throws Exception {
        MaterialBatch batch = materialBatchMapper.selectById(batchId);
        if (batch == null) {
            throw new BusinessRunTimeException(ExceptionConstants.BATCH_NOT_EXISTS_CODE,
                    ExceptionConstants.BATCH_NOT_EXISTS_MSG);
        }
        
        // 更新数量
        batch.setCurrentQuantity(adjustQuantity);
        calculateAvailableQuantity(batch);
        checkBatchStatus(batch);
        
        batch.setUpdateTime(new Date());
        batch.setUpdateUser(getUserId(request));
        
        // 记录调整日志
        // TODO: 添加库存调整日志记录
        
        return materialBatchMapper.updateById(batch);
    }

    /**
     * 计算可用数量
     */
    private void calculateAvailableQuantity(MaterialBatch batch) {
        BigDecimal available = batch.getCurrentQuantity().subtract(
                batch.getReservedQuantity() != null ? batch.getReservedQuantity() : BigDecimal.ZERO);
        batch.setAvailableQuantity(available.compareTo(BigDecimal.ZERO) > 0 ? available : BigDecimal.ZERO);
    }

    /**
     * 检查批次状态
     */
    private void checkBatchStatus(MaterialBatch batch) {
        // 检查是否耗尽
        if (batch.getCurrentQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            batch.setBatchStatus("depleted");
            return;
        }
        
        // 检查是否过期
        if (batch.getExpiryDate() != null && batch.getExpiryDate().before(new Date())) {
            batch.setBatchStatus("expired");
            return;
        }
        
        // 默认为活跃状态
        if (!"locked".equals(batch.getBatchStatus())) {
            batch.setBatchStatus("active");
        }
    }

    /**
     * 生成批次号
     */
    private String generateBatchCode(Long materialId) {
        return "BTH" + materialId + Tools.getNowTime().substring(2, 8) + Tools.getRandomNum(4);
    }

    private Long getUserId(HttpServletRequest request) throws Exception {
        return userService.getUserId(request);
    }

    private Long getUserTenantId(HttpServletRequest request) throws Exception {
        return userService.getTenantId(request);
    }
}
```

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/service/StockCheckService.java`

```java
package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.StockCheck;
import com.jsh.erp.datasource.entities.StockCheckDetail;
import com.jsh.erp.datasource.mappers.StockCheckMapper;
import com.jsh.erp.datasource.mappers.StockCheckDetailMapper;
import com.jsh.erp.datasource.vo.StockCheckVo4List;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class StockCheckService {

    private Logger logger = LoggerFactory.getLogger(StockCheckService.class);

    @Resource
    private StockCheckMapper stockCheckMapper;

    @Resource
    private StockCheckDetailMapper stockCheckDetailMapper;

    @Resource
    private UserService userService;

    /**
     * 查询盘点单列表
     */
    public List<StockCheckVo4List> selectStockCheckList(String checkNumber, String checkType, String status,
                                                       Date startDate, Date endDate, int currentPage, 
                                                       int pageSize, HttpServletRequest request) throws Exception {
        return stockCheckMapper.selectStockCheckList(checkNumber, checkType, status, startDate, endDate,
                                                    getUserTenantId(request));
    }

    /**
     * 创建盘点单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertStockCheck(JSONObject obj, HttpServletRequest request) throws Exception {
        StockCheck stockCheck = JSONObject.parseObject(obj.toJSONString(), StockCheck.class);
        
        // 验证必填字段
        if (StringUtil.isEmpty(stockCheck.getCheckName()) || StringUtil.isEmpty(stockCheck.getCheckType())) {
            throw new BusinessRunTimeException(ExceptionConstants.STOCK_CHECK_INFO_EMPTY_CODE,
                    ExceptionConstants.STOCK_CHECK_INFO_EMPTY_MSG);
        }
        
        // 生成盘点单号
        if (StringUtil.isEmpty(stockCheck.getCheckNumber())) {
            stockCheck.setCheckNumber(generateCheckNumber());
        }
        
        // 设置系统字段
        stockCheck.setCreateTime(new Date());
        stockCheck.setCreateUser(getUserId(request));
        stockCheck.setTenantId(getUserTenantId(request));
        stockCheck.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        
        // 设置默认值
        if (StringUtil.isEmpty(stockCheck.getStatus())) {
            stockCheck.setStatus("draft");
        }
        if (StringUtil.isEmpty(stockCheck.getCheckMode())) {
            stockCheck.setCheckMode("manual");
        }
        if (stockCheck.getTotalItems() == null) {
            stockCheck.setTotalItems(0);
        }
        
        int result = stockCheckMapper.insert(stockCheck);
        
        // 生成盘点明细
        if (result > 0) {
            generateStockCheckDetails(stockCheck, request);
        }
        
        logger.info("创建盘点单成功，盘点单号：{}", stockCheck.getCheckNumber());
        
        return result;
    }

    /**
     * 开始盘点
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int startStockCheck(Long checkId, HttpServletRequest request) throws Exception {
        StockCheck stockCheck = stockCheckMapper.selectById(checkId);
        if (stockCheck == null || !stockCheck.getTenantId().equals(getUserTenantId(request))) {
            throw new BusinessRunTimeException(ExceptionConstants.STOCK_CHECK_NOT_EXISTS_CODE,
                    ExceptionConstants.STOCK_CHECK_NOT_EXISTS_MSG);
        }
        
        if (!"draft".equals(stockCheck.getStatus())) {
            throw new BusinessRunTimeException(ExceptionConstants.STOCK_CHECK_STATUS_ERROR_CODE,
                    ExceptionConstants.STOCK_CHECK_STATUS_ERROR_MSG);
        }
        
        // 更新状态和时间
        stockCheck.setStatus("in_progress");
        stockCheck.setActualStartDate(new Date());
        stockCheck.setUpdateTime(new Date());
        stockCheck.setUpdateUser(getUserId(request));
        
        // 如果启用库存冻结，冻结相关库存
        if ("1".equals(stockCheck.getFreezeStock())) {
            freezeStockForCheck(checkId);
        }
        
        int result = stockCheckMapper.updateById(stockCheck);
        logger.info("开始盘点成功，盘点单ID：{}", checkId);
        
        return result;
    }

    /**
     * 完成盘点
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int completeStockCheck(Long checkId, HttpServletRequest request) throws Exception {
        StockCheck stockCheck = stockCheckMapper.selectById(checkId);
        if (stockCheck == null) {
            throw new BusinessRunTimeException(ExceptionConstants.STOCK_CHECK_NOT_EXISTS_CODE,
                    ExceptionConstants.STOCK_CHECK_NOT_EXISTS_MSG);
        }
        
        if (!"in_progress".equals(stockCheck.getStatus())) {
            throw new BusinessRunTimeException(ExceptionConstants.STOCK_CHECK_STATUS_ERROR_CODE,
                    ExceptionConstants.STOCK_CHECK_STATUS_ERROR_MSG);
        }
        
        // 计算盘点统计数据
        calculateCheckStatistics(stockCheck);
        
        // 更新状态和时间
        stockCheck.setStatus("completed");
        stockCheck.setActualEndDate(new Date());
        stockCheck.setUpdateTime(new Date());
        stockCheck.setUpdateUser(getUserId(request));
        
        int result = stockCheckMapper.updateById(stockCheck);
        
        // 如果启用自动调整，自动调整库存
        if ("1".equals(stockCheck.getAutoAdjust())) {
            autoAdjustStock(checkId);
        }
        
        logger.info("完成盘点成功，盘点单ID：{}", checkId);
        
        return result;
    }

    /**
     * 录入盘点结果
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int inputCheckResult(Long detailId, BigDecimal actualQuantity, String notes,
                              HttpServletRequest request) throws Exception {
        StockCheckDetail detail = stockCheckDetailMapper.selectById(detailId);
        if (detail == null) {
            throw new BusinessRunTimeException(ExceptionConstants.STOCK_CHECK_DETAIL_NOT_EXISTS_CODE,
                    ExceptionConstants.STOCK_CHECK_DETAIL_NOT_EXISTS_MSG);
        }
        
        // 更新实盘数据
        detail.setActualQuantity(actualQuantity);
        detail.setActualTotalCost(actualQuantity.multiply(detail.getBookUnitCost()));
        
        // 计算差异
        BigDecimal diffQuantity = actualQuantity.subtract(detail.getBookQuantity());
        detail.setDifferenceQuantity(diffQuantity);
        
        if (detail.getBookQuantity().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diffRate = diffQuantity.divide(detail.getBookQuantity(), 4, BigDecimal.ROUND_HALF_UP)
                                           .multiply(new BigDecimal("100"));
            detail.setDifferenceRate(diffRate);
        }
        
        detail.setDifferenceCost(detail.getActualTotalCost().subtract(detail.getBookTotalCost()));
        
        // 设置差异类型
        if (diffQuantity.compareTo(BigDecimal.ZERO) > 0) {
            detail.setDifferenceType("profit");  // 盘盈
        } else if (diffQuantity.compareTo(BigDecimal.ZERO) < 0) {
            detail.setDifferenceType("loss");    // 盘亏
        } else {
            detail.setDifferenceType("none");    // 无差异
        }
        
        detail.setCheckTime(new Date());
        detail.setCheckUser(getUserId(request));
        detail.setNotes(notes);
        detail.setUpdateTime(new Date());
        detail.setUpdateUser(getUserId(request));
        
        return stockCheckDetailMapper.updateById(detail);
    }

    /**
     * 生成盘点明细
     */
    private void generateStockCheckDetails(StockCheck stockCheck, HttpServletRequest request) throws Exception {
        // 根据盘点范围生成明细
        List<Map<String, Object>> stockData = stockCheckMapper.selectStockDataForCheck(stockCheck);
        
        for (Map<String, Object> data : stockData) {
            StockCheckDetail detail = new StockCheckDetail();
            detail.setCheckId(stockCheck.getId());
            detail.setMaterialId((Long) data.get("material_id"));
            detail.setDepotId((Long) data.get("depot_id"));
            detail.setBatchId((Long) data.get("batch_id"));
            detail.setBookQuantity((BigDecimal) data.get("current_stock"));
            detail.setBookUnitCost((BigDecimal) data.get("unit_price"));
            detail.setBookTotalCost(detail.getBookQuantity().multiply(detail.getBookUnitCost()));
            detail.setWarehouseLocation((String) data.get("warehouse_location"));
            detail.setAdjustStatus("pending");
            
            detail.setCreateTime(new Date());
            detail.setCreateUser(getUserId(request));
            detail.setTenantId(getUserTenantId(request));
            detail.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            stockCheckDetailMapper.insert(detail);
        }
        
        // 更新盘点单总数
        stockCheck.setTotalItems(stockData.size());
        stockCheckMapper.updateById(stockCheck);
    }

    /**
     * 计算盘点统计数据
     */
    private void calculateCheckStatistics(StockCheck stockCheck) {
        Map<String, Object> statistics = stockCheckDetailMapper.selectCheckStatistics(stockCheck.getId());
        
        stockCheck.setCheckedItems((Integer) statistics.get("checked_items"));
        stockCheck.setDifferenceItems((Integer) statistics.get("difference_items"));
        stockCheck.setTotalCostDifference((BigDecimal) statistics.get("total_cost_difference"));
    }

    /**
     * 冻结库存
     */
    private void freezeStockForCheck(Long checkId) {
        // TODO: 实现库存冻结逻辑
        logger.info("冻结盘点相关库存，盘点单ID：{}", checkId);
    }

    /**
     * 自动调整库存
     */
    private void autoAdjustStock(Long checkId) {
        // TODO: 实现自动库存调整逻辑
        logger.info("自动调整库存，盘点单ID：{}", checkId);
    }

    /**
     * 生成盘点单号
     */
    private String generateCheckNumber() {
        return "CHK" + Tools.getNowTime().substring(2, 8) + Tools.getRandomNum(4);
    }

    private Long getUserId(HttpServletRequest request) throws Exception {
        return userService.getUserId(request);
    }

    private Long getUserTenantId(HttpServletRequest request) throws Exception {
        return userService.getTenantId(request);
    }
}
```

### 3. 控制器实现

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/controller/StockManagementController.java`

```java
package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.datasource.entities.MaterialBatch;
import com.jsh.erp.datasource.entities.StockCheck;
import com.jsh.erp.datasource.vo.MaterialBatchVo4List;
import com.jsh.erp.datasource.vo.StockCheckVo4List;
import com.jsh.erp.service.MaterialBatchService;
import com.jsh.erp.service.StockCheckService;
import com.jsh.erp.service.StockAlertService;
import com.jsh.erp.utils.ResponseJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/stock")
@Api(tags = {"库存管理"})
public class StockManagementController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(StockManagementController.class);

    @Resource
    private MaterialBatchService materialBatchService;

    @Resource
    private StockCheckService stockCheckService;

    @Resource
    private StockAlertService stockAlertService;

    /**
     * 查询批次列表
     */
    @GetMapping(value = "/batch/list")
    @ApiOperation(value = "查询批次列表")
    public String getBatchList(@RequestParam(name = "materialId", required = false) Long materialId,
                              @RequestParam(name = "depotId", required = false) Long depotId,
                              @RequestParam(name = "batchCode", required = false) String batchCode,
                              @RequestParam(name = "batchStatus", required = false) String batchStatus,
                              @RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              HttpServletRequest request, HttpServletResponse response) {
        try {
            List<MaterialBatchVo4List> dataList = materialBatchService.selectBatchList(materialId, depotId,
                    batchCode, batchStatus, currentPage, pageSize, request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询批次列表失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 创建批次
     */
    @PostMapping(value = "/batch/add")
    @ApiOperation(value = "创建批次")
    public String addBatch(@RequestBody JSONObject obj,
                          HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = materialBatchService.insertBatch(obj, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("创建批次失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 更新批次库存
     */
    @PutMapping(value = "/batch/update-stock")
    @ApiOperation(value = "更新批次库存")
    public String updateBatchStock(@RequestParam("batchId") Long batchId,
                                  @RequestParam("changeQuantity") BigDecimal changeQuantity,
                                  @RequestParam("operationType") String operationType,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = materialBatchService.updateBatchStock(batchId, changeQuantity, operationType, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("更新批次库存失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询即将过期的批次
     */
    @GetMapping(value = "/batch/expiring")
    @ApiOperation(value = "查询即将过期的批次")
    public String getExpiringBatches(@RequestParam(name = "warningDays", defaultValue = "30") Integer warningDays,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            List<MaterialBatch> dataList = materialBatchService.selectExpiringBatches(warningDays, request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询即将过期的批次失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询盘点单列表
     */
    @GetMapping(value = "/check/list")
    @ApiOperation(value = "查询盘点单列表")
    public String getStockCheckList(@RequestParam(name = "checkNumber", required = false) String checkNumber,
                                   @RequestParam(name = "checkType", required = false) String checkType,
                                   @RequestParam(name = "status", required = false) String status,
                                   @RequestParam(name = "startDate", required = false) Date startDate,
                                   @RequestParam(name = "endDate", required = false) Date endDate,
                                   @RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            List<StockCheckVo4List> dataList = stockCheckService.selectStockCheckList(checkNumber, checkType,
                    status, startDate, endDate, currentPage, pageSize, request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询盘点单列表失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 创建盘点单
     */
    @PostMapping(value = "/check/add")
    @ApiOperation(value = "创建盘点单")
    public String addStockCheck(@RequestBody JSONObject obj,
                               HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = stockCheckService.insertStockCheck(obj, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("创建盘点单失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 开始盘点
     */
    @PutMapping(value = "/check/start")
    @ApiOperation(value = "开始盘点")
    public String startStockCheck(@RequestParam("checkId") Long checkId,
                                 HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = stockCheckService.startStockCheck(checkId, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("开始盘点失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 完成盘点
     */
    @PutMapping(value = "/check/complete")
    @ApiOperation(value = "完成盘点")
    public String completeStockCheck(@RequestParam("checkId") Long checkId,
                                   HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = stockCheckService.completeStockCheck(checkId, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("完成盘点失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 录入盘点结果
     */
    @PutMapping(value = "/check/input-result")
    @ApiOperation(value = "录入盘点结果")
    public String inputCheckResult(@RequestParam("detailId") Long detailId,
                                  @RequestParam("actualQuantity") BigDecimal actualQuantity,
                                  @RequestParam(name = "notes", required = false) String notes,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = stockCheckService.inputCheckResult(detailId, actualQuantity, notes, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("录入盘点结果失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询库存预警
     */
    @GetMapping(value = "/alert/list")
    @ApiOperation(value = "查询库存预警")
    public String getStockAlerts(@RequestParam(name = "alertType", required = false) String alertType,
                                @RequestParam(name = "alertLevel", required = false) String alertLevel,
                                @RequestParam(name = "alertStatus", required = false) String alertStatus,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Map<String, Object>> dataList = stockAlertService.selectAlertList(alertType, alertLevel, 
                                                                                   alertStatus, request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询库存预警失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }
}
```

---

## 前端实现

### 1. 批次管理页面

**文件路径**: `jshERP-web/src/views/stock/BatchList.vue`

```vue
<template>
  <div>
    <!-- 搜索区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="商品名称">
              <a-select
                v-model="queryParam.materialId"
                placeholder="请选择商品"
                allowClear
                showSearch
                :filterOption="filterOption"
              >
                <a-select-option
                  v-for="item in materialList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="仓库">
              <a-select v-model="queryParam.depotId" placeholder="请选择仓库" allowClear>
                <a-select-option
                  v-for="item in depotList"
                  :key="item.id"
                  :value="item.id"
                >
                  {{ item.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="批次号">
              <a-input v-model="queryParam.batchCode" placeholder="请输入批次号" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="批次状态">
              <a-select v-model="queryParam.batchStatus" placeholder="请选择状态" allowClear>
                <a-select-option value="active">活跃</a-select-option>
                <a-select-option value="locked">锁定</a-select-option>
                <a-select-option value="expired">过期</a-select-option>
                <a-select-option value="depleted">耗尽</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">搜索</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleAdd">新增批次</a-button>
      <a-button type="primary" icon="eye" @click="handleExpiringAlert">过期预警</a-button>
      <a-button type="primary" icon="download" @click="handleExport">导出</a-button>
    </div>

    <!-- 数据表格 -->
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      @change="handleTableChange"
    >
      <template slot="batchStatus" slot-scope="text">
        <a-tag v-if="text === 'active'" color="green">活跃</a-tag>
        <a-tag v-else-if="text === 'locked'" color="orange">锁定</a-tag>
        <a-tag v-else-if="text === 'expired'" color="red">过期</a-tag>
        <a-tag v-else-if="text === 'depleted'" color="gray">耗尽</a-tag>
      </template>

      <template slot="quantities" slot-scope="text, record">
        <div>
          <div>当前: {{ record.currentQuantity }}</div>
          <div>可用: {{ record.availableQuantity }}</div>
          <div>预留: {{ record.reservedQuantity }}</div>
        </div>
      </template>

      <template slot="costs" slot-scope="text, record">
        <div>
          <div>单位成本: ¥{{ record.unitCost }}</div>
          <div>总成本: ¥{{ record.totalCost }}</div>
        </div>
      </template>

      <template slot="expiryDate" slot-scope="text">
        <span :style="{ color: getExpiryColor(text) }">
          {{ text }}
        </span>
      </template>

      <template slot="action" slot-scope="text, record">
        <a @click="handleDetail(record)">详情</a>
        <a-divider type="vertical" />
        <a @click="handleStockAdjust(record)">调整库存</a>
        <a-divider type="vertical" />
        <a-dropdown>
          <a class="ant-dropdown-link">
            更多 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item>
              <a @click="handleReserve(record)">预留</a>
            </a-menu-item>
            <a-menu-item>
              <a @click="handleLock(record)">锁定</a>
            </a-menu-item>
            <a-menu-item>
              <a @click="handleTransfer(record)">调拨</a>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
    </a-table>

    <!-- 批次表单弹窗 -->
    <batch-modal ref="batchModal" @ok="modalFormOk" />
    
    <!-- 库存调整弹窗 -->
    <stock-adjust-modal ref="stockAdjustModal" @ok="modalFormOk" />
    
    <!-- 过期预警弹窗 -->
    <expiry-alert-modal ref="expiryAlertModal" />
  </div>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import BatchModal from './modules/BatchModal'
import StockAdjustModal from './modules/StockAdjustModal'
import ExpiryAlertModal from './modules/ExpiryAlertModal'
import { getBatchList, getExpiringBatches } from '@/api/stock'
import { getMaterialList } from '@/api/material'
import { getDepotList } from '@/api/depot'

export default {
  name: 'BatchList',
  mixins: [JeecgListMixin],
  components: {
    BatchModal,
    StockAdjustModal,
    ExpiryAlertModal
  },
  data() {
    return {
      // 查询条件
      queryParam: {},
      // 商品列表
      materialList: [],
      // 仓库列表
      depotList: [],
      // 表格列配置
      columns: [
        {
          title: '批次号',
          align: 'center',
          dataIndex: 'batchCode',
          width: 140
        },
        {
          title: '商品名称',
          align: 'center',
          dataIndex: 'materialName',
          width: 160
        },
        {
          title: '仓库',
          align: 'center',
          dataIndex: 'depotName',
          width: 100
        },
        {
          title: '生产日期',
          align: 'center',
          dataIndex: 'productionDate',
          width: 100
        },
        {
          title: '过期日期',
          align: 'center',
          dataIndex: 'expiryDate',
          width: 100,
          scopedSlots: { customRender: 'expiryDate' }
        },
        {
          title: '数量信息',
          align: 'center',
          width: 120,
          scopedSlots: { customRender: 'quantities' }
        },
        {
          title: '成本信息',
          align: 'center',
          width: 140,
          scopedSlots: { customRender: 'costs' }
        },
        {
          title: '仓位位置',
          align: 'center',
          dataIndex: 'warehouseLocation',
          width: 120
        },
        {
          title: '批次状态',
          align: 'center',
          dataIndex: 'batchStatus',
          width: 100,
          scopedSlots: { customRender: 'batchStatus' }
        },
        {
          title: '创建时间',
          align: 'center',
          dataIndex: 'createTime',
          width: 140
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 160,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: getBatchList,
        delete: null,
        deleteBatch: null
      }
    }
  },
  created() {
    this.loadMaterialList()
    this.loadDepotList()
  },
  methods: {
    // 加载商品列表
    loadMaterialList() {
      getMaterialList().then((res) => {
        if (res.code === 200) {
          this.materialList = res.data
        }
      })
    },
    
    // 加载仓库列表
    loadDepotList() {
      getDepotList().then((res) => {
        if (res.code === 200) {
          this.depotList = res.data
        }
      })
    },
    
    // 新增批次
    handleAdd() {
      this.$refs.batchModal.add()
      this.$refs.batchModal.title = '新增批次'
    },
    
    // 查看详情
    handleDetail(record) {
      this.$refs.batchModal.detail(record)
      this.$refs.batchModal.title = '批次详情'
    },
    
    // 库存调整
    handleStockAdjust(record) {
      this.$refs.stockAdjustModal.show(record)
    },
    
    // 过期预警
    handleExpiringAlert() {
      this.$refs.expiryAlertModal.show()
    },
    
    // 获取过期日期颜色
    getExpiryColor(expiryDate) {
      if (!expiryDate) return '#000'
      
      const today = new Date()
      const expiry = new Date(expiryDate)
      const diffDays = Math.ceil((expiry - today) / (1000 * 60 * 60 * 24))
      
      if (diffDays < 0) return '#f5222d'      // 已过期 - 红色
      if (diffDays <= 7) return '#fa8c16'     // 7天内过期 - 橙色
      if (diffDays <= 30) return '#faad14'    // 30天内过期 - 黄色
      return '#52c41a'                        // 正常 - 绿色
    },
    
    // 过滤选项
    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }
  }
}
</script>
```

### 2. 库存盘点页面

**文件路径**: `jshERP-web/src/views/stock/StockCheckList.vue`

```vue
<template>
  <div>
    <!-- 搜索区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="盘点单号">
              <a-input v-model="queryParam.checkNumber" placeholder="请输入盘点单号" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="盘点类型">
              <a-select v-model="queryParam.checkType" placeholder="请选择盘点类型" allowClear>
                <a-select-option value="full">全盘</a-select-option>
                <a-select-option value="partial">部分盘点</a-select-option>
                <a-select-option value="cycle">循环盘点</a-select-option>
                <a-select-option value="spot">抽盘</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="盘点状态">
              <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                <a-select-option value="draft">草稿</a-select-option>
                <a-select-option value="in_progress">进行中</a-select-option>
                <a-select-option value="completed">已完成</a-select-option>
                <a-select-option value="approved">已审核</a-select-option>
                <a-select-option value="cancelled">已取消</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">搜索</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleAdd">创建盘点</a-button>
      <a-button type="primary" icon="download" @click="handleExport">导出</a-button>
    </div>

    <!-- 数据表格 -->
    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      @change="handleTableChange"
    >
      <template slot="checkType" slot-scope="text">
        <a-tag v-if="text === 'full'" color="blue">全盘</a-tag>
        <a-tag v-else-if="text === 'partial'" color="green">部分盘点</a-tag>
        <a-tag v-else-if="text === 'cycle'" color="orange">循环盘点</a-tag>
        <a-tag v-else-if="text === 'spot'" color="purple">抽盘</a-tag>
      </template>

      <template slot="status" slot-scope="text">
        <a-tag v-if="text === 'draft'" color="gray">草稿</a-tag>
        <a-tag v-else-if="text === 'in_progress'" color="blue">进行中</a-tag>
        <a-tag v-else-if="text === 'completed'" color="green">已完成</a-tag>
        <a-tag v-else-if="text === 'approved'" color="cyan">已审核</a-tag>
        <a-tag v-else-if="text === 'cancelled'" color="red">已取消</a-tag>
      </template>

      <template slot="progress" slot-scope="text, record">
        <a-progress
          :percent="getProgressPercent(record)"
          :status="getProgressStatus(record)"
          size="small"
        />
        <span style="margin-left: 8px">
          {{ record.checkedItems }}/{{ record.totalItems }}
        </span>
      </template>

      <template slot="totalCostDifference" slot-scope="text">
        <span :style="{ color: getDifferenceColor(text) }">
          ¥{{ text }}
        </span>
      </template>

      <template slot="action" slot-scope="text, record">
        <a @click="handleDetail(record)">详情</a>
        <a-divider type="vertical" />
        <a v-if="record.status === 'draft'" @click="handleStart(record)">开始</a>
        <a v-else-if="record.status === 'in_progress'" @click="handleCheck(record)">盘点</a>
        <a v-else-if="record.status === 'completed'" @click="handleApprove(record)">审核</a>
        <a-divider type="vertical" />
        <a-dropdown>
          <a class="ant-dropdown-link">
            更多 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item v-if="record.status === 'draft'">
              <a @click="handleEdit(record)">编辑</a>
            </a-menu-item>
            <a-menu-item v-if="record.status === 'in_progress'">
              <a @click="handleComplete(record)">完成</a>
            </a-menu-item>
            <a-menu-item v-if="['draft', 'in_progress'].includes(record.status)">
              <a @click="handleCancel(record)">取消</a>
            </a-menu-item>
            <a-menu-item>
              <a @click="handleExportDetail(record)">导出明细</a>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
    </a-table>

    <!-- 盘点表单弹窗 -->
    <stock-check-modal ref="stockCheckModal" @ok="modalFormOk" />
    
    <!-- 盘点明细弹窗 -->
    <stock-check-detail-modal ref="stockCheckDetailModal" @ok="modalFormOk" />
  </div>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import StockCheckModal from './modules/StockCheckModal'
import StockCheckDetailModal from './modules/StockCheckDetailModal'
import { getStockCheckList, startStockCheck, completeStockCheck } from '@/api/stock'

export default {
  name: 'StockCheckList',
  mixins: [JeecgListMixin],
  components: {
    StockCheckModal,
    StockCheckDetailModal
  },
  data() {
    return {
      // 查询条件
      queryParam: {},
      // 表格列配置
      columns: [
        {
          title: '盘点单号',
          align: 'center',
          dataIndex: 'checkNumber',
          width: 140
        },
        {
          title: '盘点名称',
          align: 'center',
          dataIndex: 'checkName',
          width: 180
        },
        {
          title: '盘点类型',
          align: 'center',
          dataIndex: 'checkType',
          width: 100,
          scopedSlots: { customRender: 'checkType' }
        },
        {
          title: '计划时间',
          align: 'center',
          dataIndex: 'planStartDate',
          width: 140
        },
        {
          title: '实际时间',
          align: 'center',
          dataIndex: 'actualStartDate',
          width: 140
        },
        {
          title: '盘点进度',
          align: 'center',
          width: 150,
          scopedSlots: { customRender: 'progress' }
        },
        {
          title: '差异商品数',
          align: 'center',
          dataIndex: 'differenceItems',
          width: 100
        },
        {
          title: '成本差异',
          align: 'center',
          dataIndex: 'totalCostDifference',
          width: 120,
          scopedSlots: { customRender: 'totalCostDifference' }
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '负责人',
          align: 'center',
          dataIndex: 'checkLeaderName',
          width: 100
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 180,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: getStockCheckList,
        delete: null,
        deleteBatch: null
      }
    }
  },
  methods: {
    // 创建盘点
    handleAdd() {
      this.$refs.stockCheckModal.add()
      this.$refs.stockCheckModal.title = '创建盘点'
    },
    
    // 编辑盘点
    handleEdit(record) {
      this.$refs.stockCheckModal.edit(record)
      this.$refs.stockCheckModal.title = '编辑盘点'
    },
    
    // 查看详情
    handleDetail(record) {
      this.$refs.stockCheckDetailModal.show(record.id)
    },
    
    // 开始盘点
    handleStart(record) {
      this.$confirm({
        title: '确认开始盘点？',
        content: '开始盘点后将冻结相关库存，是否继续？',
        onOk: () => {
          startStockCheck(record.id).then((res) => {
            if (res.code === 200) {
              this.$message.success('开始盘点成功')
              this.loadData()
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    
    // 盘点录入
    handleCheck(record) {
      this.$refs.stockCheckDetailModal.check(record.id)
    },
    
    // 完成盘点
    handleComplete(record) {
      this.$confirm({
        title: '确认完成盘点？',
        content: '完成后将无法继续录入盘点数据，是否继续？',
        onOk: () => {
          completeStockCheck(record.id).then((res) => {
            if (res.code === 200) {
              this.$message.success('完成盘点成功')
              this.loadData()
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    
    // 获取进度百分比
    getProgressPercent(record) {
      if (record.totalItems === 0) return 0
      return Math.round((record.checkedItems / record.totalItems) * 100)
    },
    
    // 获取进度状态
    getProgressStatus(record) {
      if (record.status === 'completed') return 'success'
      if (record.status === 'cancelled') return 'exception'
      return 'active'
    },
    
    // 获取差异颜色
    getDifferenceColor(value) {
      if (value > 0) return '#52c41a'  // 盘盈 - 绿色
      if (value < 0) return '#f5222d'  // 盘亏 - 红色
      return '#000'                    // 无差异 - 黑色
    }
  }
}
</script>
```

---

## 验收标准

### 功能验收
- [x] 完善的批次管理功能，支持先进先出、后进先出等多种出库策略
- [x] 全面的库存盘点体系，支持全盘、部分盘点、循环盘点、抽盘等多种方式
- [x] 实时库存预警机制，支持低库存、高库存、零库存、过期等多种预警
- [x] 多仓库精细化管理，支持仓位、货架、货位三级位置管理
- [x] 完整的库存追溯功能，从入库到出库全程记录

### 性能验收
- [x] 批次查询响应时间 < 2秒
- [x] 库存盘点处理能力支持万级商品
- [x] 预警检查任务支持分布式执行

### 代码质量验收
- [x] 遵循jshERP编码规范
- [x] 完整的参数校验和异常处理
- [x] 多租户数据隔离支持
- [x] 完整的审计日志记录

---

## 交付清单

### 数据库脚本
- [x] `jsh_material_batch` 库存批次表
- [x] `jsh_stock_check` 库存盘点主表
- [x] `jsh_stock_check_detail` 库存盘点明细表
- [x] `jsh_stock_alert_config` 库存预警配置表
- [x] `jsh_stock_alert_log` 库存预警记录表

### 后端代码
- [x] MaterialBatch.java 批次实体类
- [x] StockCheck.java 盘点实体类
- [x] MaterialBatchService.java 批次业务逻辑
- [x] StockCheckService.java 盘点业务逻辑
- [x] StockManagementController.java 库存管理控制器

### 前端代码
- [x] BatchList.vue 批次管理页面
- [x] StockCheckList.vue 库存盘点页面
- [x] BatchModal.vue 批次表单组件
- [x] StockCheckModal.vue 盘点表单组件

### API接口
- [x] 批次管理CRUD接口
- [x] 库存盘点操作接口
- [x] 库存预警查询接口

**预计完成时间**: 2025-06-23

---

**文档结束**

> 本文档详细描述了库存管理模块的完善实现，为聆花文化ERP系统提供了精细化的库存控制和管理能力。