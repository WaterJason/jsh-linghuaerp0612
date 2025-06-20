# Week 2-3: 核心业务逻辑开发指南

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-17
- **项目阶段**: 第一阶段 - 生产制作管理模块
- **估算工期**: 10天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第3、4、5章规范

---

## 概述

本文档为Week 2-3的核心业务逻辑开发提供详细的实施指导。主要包括实体类开发、Mapper接口开发、Service层业务逻辑开发、Controller层API开发，完成生产工单管理的完整后端功能。

---

## 实体类开发 (1天)

### 1. ProductionOrder.java - 生产工单实体

**文件路径**: `com.jsh.erp.production.datasource.entities.ProductionOrder`

```java
package com.jsh.erp.production.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产工单实体类
 * 遵循jshERP实体设计规范
 * 
 * @author jshERP
 * @version 1.0
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
    
    // 多租户和审计字段（必需）
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Long createUser;
    private Date updateTime;
    private Long updateUser;
    
    // 构造函数
    public ProductionOrder() {}
    
    public ProductionOrder(String orderNo, String productName, BigDecimal quantity) {
        this.orderNo = orderNo;
        this.productName = productName;
        this.quantity = quantity;
        this.deleteFlag = "0";
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    // 标准getter/setter方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    
    public Long getSaleOrderId() { return saleOrderId; }
    public void setSaleOrderId(Long saleOrderId) { this.saleOrderId = saleOrderId; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public String getProductSpec() { return productSpec; }
    public void setProductSpec(String productSpec) { this.productSpec = productSpec; }
    
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public BigDecimal getProductionCost() { return productionCost; }
    public void setProductionCost(BigDecimal productionCost) { this.productionCost = productionCost; }
    
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    
    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }
    
    public Date getPlanStartDate() { return planStartDate; }
    public void setPlanStartDate(Date planStartDate) { this.planStartDate = planStartDate; }
    
    public Date getPlanFinishDate() { return planFinishDate; }
    public void setPlanFinishDate(Date planFinishDate) { this.planFinishDate = planFinishDate; }
    
    public Date getActualStartDate() { return actualStartDate; }
    public void setActualStartDate(Date actualStartDate) { this.actualStartDate = actualStartDate; }
    
    public Date getActualFinishDate() { return actualFinishDate; }
    public void setActualFinishDate(Date actualFinishDate) { this.actualFinishDate = actualFinishDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    // 多租户字段
    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }
    
    public String getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(String deleteFlag) { this.deleteFlag = deleteFlag; }
    
    // 审计字段
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    
    public Long getCreateUser() { return createUser; }
    public void setCreateUser(Long createUser) { this.createUser = createUser; }
    
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    
    public Long getUpdateUser() { return updateUser; }
    public void setUpdateUser(Long updateUser) { this.updateUser = updateUser; }
}
```

### 2. ProductionMaterial.java - 工单物料实体

**文件路径**: `com.jsh.erp.production.datasource.entities.ProductionMaterial`

```java
package com.jsh.erp.production.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工单物料实体类
 */
public class ProductionMaterial {
    private Long id;
    private Long productionOrderId;
    private Long materialId;
    private String materialName;
    private String materialSpec;
    private BigDecimal requiredQuantity;
    private BigDecimal allocatedQuantity;
    private BigDecimal consumedQuantity;
    private String unit;
    private BigDecimal unitCost;
    private BigDecimal totalCost;
    private Long supplierId;
    private String sourceType; // 1库存，2采购，3委外
    private String reserveStatus; // 0未预留，1已预留
    private String remark;
    
    // 多租户和审计字段
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Date updateTime;
    
    // 构造函数
    public ProductionMaterial() {
        this.allocatedQuantity = BigDecimal.ZERO;
        this.consumedQuantity = BigDecimal.ZERO;
        this.sourceType = "1"; // 默认来源库存
        this.reserveStatus = "0"; // 默认未预留
        this.deleteFlag = "0";
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    // getter/setter方法...
    // [省略标准getter/setter，与ProductionOrder类似]
}
```

### 3. WorkReport.java - 报工记录实体

**文件路径**: `com.jsh.erp.production.datasource.entities.WorkReport`

```java
package com.jsh.erp.production.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 移动端报工记录实体类
 */
public class WorkReport {
    private Long id;
    private String reportNo;
    private Long productionOrderId;
    private Long workerId;
    private String workerName;
    private Date workDate;
    private Date startTime;
    private Date endTime;
    private BigDecimal workHours;
    private BigDecimal completedQuantity;
    private BigDecimal qualifiedQuantity;
    private BigDecimal defectiveQuantity;
    private String workContent;
    private String qualityNotes;
    private String photoUrls; // JSON数组字符串
    private String locationInfo;
    private String deviceInfo;
    private String isCompleted; // 0进行中，1已完工
    private String approvalStatus; // 0待审核，1已审核，2已驳回
    private Long approvalUser;
    private Date approvalTime;
    private String approvalNotes;
    
    // 多租户和审计字段
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Date updateTime;
    
    // 构造函数
    public WorkReport() {
        this.completedQuantity = BigDecimal.ZERO;
        this.qualifiedQuantity = BigDecimal.ZERO;
        this.defectiveQuantity = BigDecimal.ZERO;
        this.isCompleted = "0";
        this.approvalStatus = "0";
        this.deleteFlag = "0";
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    // getter/setter方法...
    // [省略标准getter/setter]
}
```

### 4. ProcessTemplate.java - 工艺模板实体

**文件路径**: `com.jsh.erp.production.datasource.entities.ProcessTemplate`

```java
package com.jsh.erp.production.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工艺流程模板实体类
 */
public class ProcessTemplate {
    private Long id;
    private String templateName;
    private String templateCode;
    private String productCategory;
    private String processSteps; // JSON数组字符串
    private BigDecimal estimatedHours;
    private String skillRequirements;
    private String equipmentRequirements;
    private String qualityStandards; // JSON字符串
    private String safetyNotes;
    private String version;
    private String status; // 0禁用，1启用
    
    // 多租户和审计字段
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Long createUser;
    private Date updateTime;
    private Long updateUser;
    
    // 构造函数
    public ProcessTemplate() {
        this.version = "1.0";
        this.status = "1";
        this.deleteFlag = "0";
        this.createTime = new Date();
        this.updateTime = new Date();
    }
    
    // getter/setter方法...
    // [省略标准getter/setter]
}
```

### 5. VO类定义

**ProductionOrderVo4List.java** - 列表查询视图对象：

```java
package com.jsh.erp.production.datasource.vo;

import com.jsh.erp.production.datasource.entities.ProductionOrder;
import java.math.BigDecimal;

/**
 * 生产工单列表视图对象
 * 继承基础实体，增加关联查询字段
 */
public class ProductionOrderVo4List extends ProductionOrder {
    private String saleOrderNo; // 关联销售订单号
    private String workerName; // 制作人员姓名
    private String workshopName; // 车间名称
    private String statusName; // 状态名称
    private String priorityName; // 优先级名称
    private BigDecimal materialCost; // 物料成本
    private BigDecimal laborCost; // 人工成本
    private BigDecimal completionRate; // 完成率
    private Integer reportCount; // 报工次数
    private String createTimeStr; // 格式化创建时间
    private String planStartDateStr; // 格式化计划开工时间
    private String planFinishDateStr; // 格式化计划完工时间
    
    // getter/setter方法...
    public String getSaleOrderNo() { return saleOrderNo; }
    public void setSaleOrderNo(String saleOrderNo) { this.saleOrderNo = saleOrderNo; }
    
    public String getWorkerName() { return workerName; }
    public void setWorkerName(String workerName) { this.workerName = workerName; }
    
    public String getWorkshopName() { return workshopName; }
    public void setWorkshopName(String workshopName) { this.workshopName = workshopName; }
    
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    
    public String getPriorityName() { return priorityName; }
    public void setPriorityName(String priorityName) { this.priorityName = priorityName; }
    
    public BigDecimal getMaterialCost() { return materialCost; }
    public void setMaterialCost(BigDecimal materialCost) { this.materialCost = materialCost; }
    
    public BigDecimal getLaborCost() { return laborCost; }
    public void setLaborCost(BigDecimal laborCost) { this.laborCost = laborCost; }
    
    public BigDecimal getCompletionRate() { return completionRate; }
    public void setCompletionRate(BigDecimal completionRate) { this.completionRate = completionRate; }
    
    public Integer getReportCount() { return reportCount; }
    public void setReportCount(Integer reportCount) { this.reportCount = reportCount; }
    
    public String getCreateTimeStr() { return createTimeStr; }
    public void setCreateTimeStr(String createTimeStr) { this.createTimeStr = createTimeStr; }
    
    public String getPlanStartDateStr() { return planStartDateStr; }
    public void setPlanStartDateStr(String planStartDateStr) { this.planStartDateStr = planStartDateStr; }
    
    public String getPlanFinishDateStr() { return planFinishDateStr; }
    public void setPlanFinishDateStr(String planFinishDateStr) { this.planFinishDateStr = planFinishDateStr; }
}
```

---

## Mapper接口开发 (1天)

### 1. ProductionOrderMapper.java - 基础Mapper接口

使用MyBatis Generator生成基础CRUD接口（标准jshERP做法）

### 2. ProductionOrderMapperEx.java - 扩展Mapper接口

**文件路径**: `com.jsh.erp.production.datasource.mappers.ProductionOrderMapperEx`

```java
package com.jsh.erp.production.datasource.mappers;

import com.jsh.erp.production.datasource.entities.ProductionOrder;
import com.jsh.erp.production.datasource.vo.ProductionOrderVo4List;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 生产工单扩展Mapper接口
 * 包含复杂查询和业务逻辑查询
 */
public interface ProductionOrderMapperEx {
    
    /**
     * 根据条件查询生产工单列表
     * @param orderNo 工单号
     * @param productName 产品名称
     * @param status 工单状态
     * @param workerId 制作人员ID
     * @param startDate 计划开工日期开始
     * @param endDate 计划开工日期结束
     * @param tenantId 租户ID
     * @return 工单列表
     */
    List<ProductionOrderVo4List> selectByCondition(
            @Param("orderNo") String orderNo,
            @Param("productName") String productName,
            @Param("status") String status,
            @Param("workerId") Long workerId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("tenantId") Long tenantId
    );
    
    /**
     * 根据销售订单创建生产工单
     * @param productionOrder 生产工单对象
     * @return 影响行数
     */
    int insertSelective(ProductionOrder productionOrder);
    
    /**
     * 更新工单状态
     * @param id 工单ID
     * @param status 新状态
     * @param updateUser 更新人
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, 
                    @Param("status") String status,
                    @Param("updateUser") Long updateUser,
                    @Param("tenantId") Long tenantId);
    
    /**
     * 分配制作人员
     * @param id 工单ID
     * @param workerId 制作人员ID
     * @param updateUser 更新人
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int assignWorker(@Param("id") Long id,
                    @Param("workerId") Long workerId,
                    @Param("updateUser") Long updateUser,
                    @Param("tenantId") Long tenantId);
    
    /**
     * 更新实际开工时间
     * @param id 工单ID
     * @param actualStartDate 实际开工时间
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int updateActualStartDate(@Param("id") Long id,
                             @Param("actualStartDate") Date actualStartDate,
                             @Param("tenantId") Long tenantId);
    
    /**
     * 更新实际完工时间
     * @param id 工单ID
     * @param actualFinishDate 实际完工时间
     * @param tenantId 租户ID
     * @return 影响行数
     */
    int updateActualFinishDate(@Param("id") Long id,
                              @Param("actualFinishDate") Date actualFinishDate,
                              @Param("tenantId") Long tenantId);
    
    /**
     * 获取工单统计信息
     * @param tenantId 租户ID
     * @return 统计信息Map
     */
    List<java.util.Map<String, Object>> getOrderStatistics(@Param("tenantId") Long tenantId);
    
    /**
     * 根据制作人员查询工单
     * @param workerId 制作人员ID
     * @param status 状态
     * @param tenantId 租户ID
     * @return 工单列表
     */
    List<ProductionOrderVo4List> selectByWorker(@Param("workerId") Long workerId,
                                               @Param("status") String status,
                                               @Param("tenantId") Long tenantId);
}
```

### 3. ProductionOrderMapperEx.xml - 映射文件

**文件路径**: `jshERP-boot/src/main/resources/mapper_xml/production/ProductionOrderMapperEx.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.jsh.erp.production.datasource.mappers.ProductionOrderMapperEx">

    <!-- 扩展ResultMap，继承基础ResultMap -->
    <resultMap extends="com.jsh.erp.production.datasource.mappers.ProductionOrderMapper.BaseResultMap" 
               id="ProductionOrderVo4ListMap" 
               type="com.jsh.erp.production.datasource.vo.ProductionOrderVo4List">
        <result column="sale_order_no" jdbcType="VARCHAR" property="saleOrderNo" />
        <result column="worker_name" jdbcType="VARCHAR" property="workerName" />
        <result column="workshop_name" jdbcType="VARCHAR" property="workshopName" />
        <result column="status_name" jdbcType="VARCHAR" property="statusName" />
        <result column="priority_name" jdbcType="VARCHAR" property="priorityName" />
        <result column="material_cost" jdbcType="DECIMAL" property="materialCost" />
        <result column="labor_cost" jdbcType="DECIMAL" property="laborCost" />
        <result column="completion_rate" jdbcType="DECIMAL" property="completionRate" />
        <result column="report_count" jdbcType="INTEGER" property="reportCount" />
        <result column="create_time_str" jdbcType="VARCHAR" property="createTimeStr" />
        <result column="plan_start_date_str" jdbcType="VARCHAR" property="planStartDateStr" />
        <result column="plan_finish_date_str" jdbcType="VARCHAR" property="planFinishDateStr" />
    </resultMap>

    <!-- 根据条件查询生产工单列表 -->
    <select id="selectByCondition" resultMap="ProductionOrderVo4ListMap">
        SELECT 
            po.*,
            so.number as sale_order_no,
            u.username as worker_name,
            w.name as workshop_name,
            CASE po.status 
                WHEN '0' THEN '待分配'
                WHEN '1' THEN '进行中'
                WHEN '2' THEN '已完工'
                WHEN '3' THEN '已交付'
                ELSE '未知'
            END as status_name,
            CASE po.priority
                WHEN 1 THEN '最高'
                WHEN 2 THEN '高'
                WHEN 3 THEN '中高'
                WHEN 4 THEN '中'
                WHEN 5 THEN '中低'
                WHEN 6 THEN '低'
                ELSE '普通'
            END as priority_name,
            IFNULL(mc.total_cost, 0) as material_cost,
            IFNULL(lc.labor_cost, 0) as labor_cost,
            CASE 
                WHEN po.quantity > 0 THEN ROUND(IFNULL(wr.completed_qty, 0) / po.quantity * 100, 2)
                ELSE 0
            END as completion_rate,
            IFNULL(wr.report_count, 0) as report_count,
            DATE_FORMAT(po.create_time, '%Y-%m-%d %H:%i:%s') as create_time_str,
            DATE_FORMAT(po.plan_start_date, '%Y-%m-%d') as plan_start_date_str,
            DATE_FORMAT(po.plan_finish_date, '%Y-%m-%d') as plan_finish_date_str
        FROM jsh_production_order po
        LEFT JOIN jsh_depot_head so ON po.sale_order_id = so.id AND so.delete_flag != '1'
        LEFT JOIN jsh_user u ON po.worker_id = u.id AND u.delete_flag != '1'
        LEFT JOIN jsh_workshop w ON po.workshop_id = w.id AND w.delete_flag != '1'
        LEFT JOIN (
            SELECT production_order_id, SUM(total_cost) as total_cost
            FROM jsh_production_material 
            WHERE delete_flag != '1'
            GROUP BY production_order_id
        ) mc ON po.id = mc.production_order_id
        LEFT JOIN (
            SELECT production_order_id, SUM(work_hours * 50) as labor_cost
            FROM jsh_work_report 
            WHERE delete_flag != '1' AND approval_status = '1'
            GROUP BY production_order_id
        ) lc ON po.id = lc.production_order_id
        LEFT JOIN (
            SELECT production_order_id, SUM(completed_quantity) as completed_qty, COUNT(*) as report_count
            FROM jsh_work_report 
            WHERE delete_flag != '1' AND approval_status = '1'
            GROUP BY production_order_id
        ) wr ON po.id = wr.production_order_id
        WHERE po.delete_flag != '1'
        <if test="tenantId != null">
            AND po.tenant_id = #{tenantId}
        </if>
        <if test="orderNo != null and orderNo != ''">
            <bind name="bindOrderNo" value="'%' + orderNo + '%'"/>
            AND po.order_no LIKE #{bindOrderNo}
        </if>
        <if test="productName != null and productName != ''">
            <bind name="bindProductName" value="'%' + productName + '%'"/>
            AND po.product_name LIKE #{bindProductName}
        </if>
        <if test="status != null and status != ''">
            AND po.status = #{status}
        </if>
        <if test="workerId != null">
            AND po.worker_id = #{workerId}
        </if>
        <if test="startDate != null">
            AND po.plan_start_date >= #{startDate}
        </if>
        <if test="endDate != null">
            AND po.plan_start_date &lt;= #{endDate}
        </if>
        ORDER BY po.priority ASC, po.plan_start_date ASC, po.id DESC
    </select>

    <!-- 更新工单状态 -->
    <update id="updateStatus">
        UPDATE jsh_production_order 
        SET status = #{status},
            update_time = NOW(),
            update_user = #{updateUser}
        WHERE id = #{id} 
        AND delete_flag != '1'
        <if test="tenantId != null">
            AND tenant_id = #{tenantId}
        </if>
    </update>

    <!-- 分配制作人员 -->
    <update id="assignWorker">
        UPDATE jsh_production_order 
        SET worker_id = #{workerId},
            status = CASE WHEN status = '0' THEN '1' ELSE status END,
            update_time = NOW(),
            update_user = #{updateUser}
        WHERE id = #{id} 
        AND delete_flag != '1'
        <if test="tenantId != null">
            AND tenant_id = #{tenantId}
        </if>
    </update>

    <!-- 更新实际开工时间 -->
    <update id="updateActualStartDate">
        UPDATE jsh_production_order 
        SET actual_start_date = #{actualStartDate},
            update_time = NOW()
        WHERE id = #{id} 
        AND delete_flag != '1'
        <if test="tenantId != null">
            AND tenant_id = #{tenantId}
        </if>
    </update>

    <!-- 更新实际完工时间 -->
    <update id="updateActualFinishDate">
        UPDATE jsh_production_order 
        SET actual_finish_date = #{actualFinishDate},
            status = '2',
            update_time = NOW()
        WHERE id = #{id} 
        AND delete_flag != '1'
        <if test="tenantId != null">
            AND tenant_id = #{tenantId}
        </if>
    </update>

    <!-- 获取工单统计信息 -->
    <select id="getOrderStatistics" resultType="java.util.Map">
        SELECT 
            COUNT(*) as total_count,
            SUM(CASE WHEN status = '0' THEN 1 ELSE 0 END) as pending_count,
            SUM(CASE WHEN status = '1' THEN 1 ELSE 0 END) as in_progress_count,
            SUM(CASE WHEN status = '2' THEN 1 ELSE 0 END) as completed_count,
            SUM(CASE WHEN status = '3' THEN 1 ELSE 0 END) as delivered_count,
            SUM(CASE WHEN plan_start_date &lt; NOW() AND status IN ('0', '1') THEN 1 ELSE 0 END) as overdue_count
        FROM jsh_production_order 
        WHERE delete_flag != '1'
        <if test="tenantId != null">
            AND tenant_id = #{tenantId}
        </if>
    </select>

    <!-- 根据制作人员查询工单 -->
    <select id="selectByWorker" resultMap="ProductionOrderVo4ListMap">
        SELECT 
            po.*,
            so.number as sale_order_no,
            u.username as worker_name,
            CASE po.status 
                WHEN '0' THEN '待分配'
                WHEN '1' THEN '进行中'
                WHEN '2' THEN '已完工'
                WHEN '3' THEN '已交付'
                ELSE '未知'
            END as status_name,
            DATE_FORMAT(po.create_time, '%Y-%m-%d %H:%i:%s') as create_time_str,
            DATE_FORMAT(po.plan_start_date, '%Y-%m-%d') as plan_start_date_str,
            DATE_FORMAT(po.plan_finish_date, '%Y-%m-%d') as plan_finish_date_str
        FROM jsh_production_order po
        LEFT JOIN jsh_depot_head so ON po.sale_order_id = so.id AND so.delete_flag != '1'
        LEFT JOIN jsh_user u ON po.worker_id = u.id AND u.delete_flag != '1'
        WHERE po.delete_flag != '1'
        AND po.worker_id = #{workerId}
        <if test="status != null and status != ''">
            AND po.status = #{status}
        </if>
        <if test="tenantId != null">
            AND po.tenant_id = #{tenantId}
        </if>
        ORDER BY po.priority ASC, po.plan_start_date ASC
    </select>

</mapper>
```

---

## Service层开发 (3天)

### 1. ProductionOrderService.java - 工单管理核心服务

**文件路径**: `com.jsh.erp.production.service.ProductionOrderService`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.production.datasource.entities.ProductionOrder;
import com.jsh.erp.production.datasource.mappers.ProductionOrderMapper;
import com.jsh.erp.production.datasource.mappers.ProductionOrderMapperEx;
import com.jsh.erp.production.datasource.vo.ProductionOrderVo4List;
import com.jsh.erp.production.utils.ProductionOrderNoGenerator;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.depot.DepotHeadService;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生产工单管理服务类
 * 遵循jshERP Service层开发规范
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class ProductionOrderService {
    private Logger logger = LoggerFactory.getLogger(ProductionOrderService.class);

    @Resource
    private ProductionOrderMapper productionOrderMapper;
    @Resource
    private ProductionOrderMapperEx productionOrderMapperEx;
    @Resource
    private ProductionMaterialService productionMaterialService;
    @Resource
    private LogService logService;
    @Resource
    private DepotHeadService depotHeadService;
    @Resource
    private UserService userService;

    /**
     * 根据条件查询生产工单列表
     * 支持分页查询，遵循jshERP标准分页模式
     */
    public List<ProductionOrderVo4List> select(String search) throws Exception {
        List<ProductionOrderVo4List> list = new ArrayList<>();
        try {
            // 启动分页
            PageUtils.startPage();
            
            // 解析查询参数
            String orderNo = StringUtil.getInfo(search, "orderNo");
            String productName = StringUtil.getInfo(search, "productName");
            String status = StringUtil.getInfo(search, "status");
            String workerIdStr = StringUtil.getInfo(search, "workerId");
            String startDateStr = StringUtil.getInfo(search, "startDate");
            String endDateStr = StringUtil.getInfo(search, "endDate");
            
            Long workerId = StringUtil.isNotEmpty(workerIdStr) ? Long.parseLong(workerIdStr) : null;
            Date startDate = StringUtil.isNotEmpty(startDateStr) ? 
                new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr) : null;
            Date endDate = StringUtil.isNotEmpty(endDateStr) ? 
                new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr) : null;
            
            // 获取当前租户ID（从上下文获取）
            Long tenantId = getCurrentTenantId();
            
            // 执行查询
            list = productionOrderMapperEx.selectByCondition(
                orderNo, productName, status, workerId, startDate, endDate, tenantId);
                
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 从销售订单创建生产工单
     * 核心业务逻辑：自动分解销售订单，生成生产工单和物料清单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int createOrderFromSale(JSONObject obj, HttpServletRequest request) throws Exception {
        try {
            // 1. 解析销售订单信息
            Long saleOrderId = obj.getLong("saleOrderId");
            if (saleOrderId == null) {
                throw new BusinessRunTimeException("销售订单ID不能为空");
            }
            
            // 2. 验证销售订单存在性
            Object saleOrder = depotHeadService.getDepotHead(saleOrderId);
            if (saleOrder == null) {
                throw new BusinessRunTimeException("销售订单不存在");
            }
            
            // 3. 创建生产工单
            ProductionOrder productionOrder = new ProductionOrder();
            productionOrder.setOrderNo(ProductionOrderNoGenerator.generate());
            productionOrder.setSaleOrderId(saleOrderId);
            productionOrder.setProductName(obj.getString("productName"));
            productionOrder.setProductSpec(obj.getString("productSpec"));
            productionOrder.setQuantity(obj.getBigDecimal("quantity"));
            productionOrder.setUnit(obj.getString("unit"));
            productionOrder.setPlanStartDate(obj.getDate("planStartDate"));
            productionOrder.setPlanFinishDate(obj.getDate("planFinishDate"));
            productionOrder.setPriority(obj.getInteger("priority"));
            productionOrder.setRemark(obj.getString("remark"));
            productionOrder.setStatus("0"); // 待分配
            
            // 4. 设置多租户和审计字段
            Long tenantId = getCurrentTenantId();
            Long currentUserId = getCurrentUserId();
            productionOrder.setTenantId(tenantId);
            productionOrder.setCreateUser(currentUserId);
            productionOrder.setUpdateUser(currentUserId);
            
            // 5. 插入生产工单
            productionOrderMapperEx.insertSelective(productionOrder);
            
            // 6. 创建物料清单
            if (obj.containsKey("materialList")) {
                productionMaterialService.createMaterialList(
                    productionOrder.getId(), obj.getJSONArray("materialList"));
            }
            
            // 7. 记录操作日志
            logService.insertLog("生产工单", 
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD)
                    .append("工单号：").append(productionOrder.getOrderNo()).toString(), 
                request);
            
            return 1;
            
        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 分配制作人员
     * 业务逻辑：验证人员可用性，更新工单状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int assignWorker(Long id, Long workerId, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证工单存在性
            ProductionOrder order = productionOrderMapper.selectByPrimaryKey(id);
            if (order == null || "1".equals(order.getDeleteFlag())) {
                throw new BusinessRunTimeException("生产工单不存在");
            }
            
            // 2. 验证工单状态（只有待分配的工单才能分配人员）
            if (!"0".equals(order.getStatus())) {
                throw new BusinessRunTimeException("工单状态不允许分配人员");
            }
            
            // 3. 验证制作人员存在性
            if (userService.getUser(workerId) == null) {
                throw new BusinessRunTimeException("制作人员不存在");
            }
            
            // 4. 执行分配
            Long tenantId = getCurrentTenantId();
            Long currentUserId = getCurrentUserId();
            int result = productionOrderMapperEx.assignWorker(id, workerId, currentUserId, tenantId);
            
            if (result > 0) {
                // 5. 记录操作日志
                logService.insertLog("生产工单", 
                    new StringBuffer("分配制作人员：工单号 ")
                        .append(order.getOrderNo())
                        .append("，制作人员ID ").append(workerId).toString(), 
                    request);
            }
            
            return result;
            
        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 更新工单状态
     * 业务逻辑：验证状态转换合法性，更新相关时间字段
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateOrderStatus(Long id, String newStatus, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证工单存在性
            ProductionOrder order = productionOrderMapper.selectByPrimaryKey(id);
            if (order == null || "1".equals(order.getDeleteFlag())) {
                throw new BusinessRunTimeException("生产工单不存在");
            }
            
            // 2. 验证状态转换合法性
            String currentStatus = order.getStatus();
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                throw new BusinessRunTimeException("无效的状态转换：从 " + currentStatus + " 到 " + newStatus);
            }
            
            // 3. 更新状态
            Long tenantId = getCurrentTenantId();
            Long currentUserId = getCurrentUserId();
            int result = productionOrderMapperEx.updateStatus(id, newStatus, currentUserId, tenantId);
            
            // 4. 根据新状态更新相关时间字段
            if ("1".equals(newStatus) && order.getActualStartDate() == null) {
                // 开始生产，记录实际开工时间
                productionOrderMapperEx.updateActualStartDate(id, new Date(), tenantId);
            } else if ("2".equals(newStatus)) {
                // 完工，记录实际完工时间
                productionOrderMapperEx.updateActualFinishDate(id, new Date(), tenantId);
            }
            
            if (result > 0) {
                // 5. 记录操作日志
                String statusName = getStatusName(newStatus);
                logService.insertLog("生产工单", 
                    new StringBuffer("更新工单状态：工单号 ")
                        .append(order.getOrderNo())
                        .append("，状态更新为 ").append(statusName).toString(), 
                    request);
            }
            
            return result;
            
        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 计算生产成本
     * 业务逻辑：汇总物料成本和人工成本
     */
    public BigDecimal calculateCost(Long productionOrderId) throws Exception {
        try {
            // 1. 计算物料成本
            BigDecimal materialCost = productionMaterialService.calculateMaterialCost(productionOrderId);
            
            // 2. 计算人工成本（基于报工记录）
            BigDecimal laborCost = calculateLaborCost(productionOrderId);
            
            // 3. 返回总成本
            return materialCost.add(laborCost);
            
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 根据制作人员获取工单列表（移动端使用）
     */
    public List<ProductionOrderVo4List> selectByWorker(Long workerId, String status) throws Exception {
        List<ProductionOrderVo4List> list = new ArrayList<>();
        try {
            PageUtils.startPage();
            Long tenantId = getCurrentTenantId();
            list = productionOrderMapperEx.selectByWorker(workerId, status, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 获取工单统计信息
     */
    public List<java.util.Map<String, Object>> getOrderStatistics() throws Exception {
        try {
            Long tenantId = getCurrentTenantId();
            return productionOrderMapperEx.getOrderStatistics(tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return new ArrayList<>();
        }
    }

    // 私有辅助方法

    /**
     * 验证状态转换是否合法
     */
    private boolean isValidStatusTransition(String fromStatus, String toStatus) {
        // 定义合法的状态转换规则
        switch (fromStatus) {
            case "0": // 待分配 -> 进行中
                return "1".equals(toStatus);
            case "1": // 进行中 -> 已完工
                return "2".equals(toStatus);
            case "2": // 已完工 -> 已交付
                return "3".equals(toStatus);
            default:
                return false;
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(String status) {
        switch (status) {
            case "0": return "待分配";
            case "1": return "进行中";
            case "2": return "已完工";
            case "3": return "已交付";
            default: return "未知";
        }
    }

    /**
     * 计算人工成本
     */
    private BigDecimal calculateLaborCost(Long productionOrderId) {
        // 基于报工记录计算人工成本
        // 这里简化处理，实际项目中应根据具体业务规则计算
        return BigDecimal.ZERO;
    }

    /**
     * 获取当前租户ID
     * 实际实现中应从Spring Security上下文或ThreadLocal获取
     */
    private Long getCurrentTenantId() {
        // TODO: 从上下文获取当前租户ID
        return 1L;
    }

    /**
     * 获取当前用户ID
     * 实际实现中应从Spring Security上下文获取
     */
    private Long getCurrentUserId() {
        // TODO: 从上下文获取当前用户ID
        return 1L;
    }
}
```

### 2. ProductionMaterialService.java - 物料管理服务

**文件路径**: `com.jsh.erp.production.service.ProductionMaterialService`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.production.datasource.entities.ProductionMaterial;
import com.jsh.erp.production.datasource.mappers.ProductionMaterialMapper;
import com.jsh.erp.production.datasource.mappers.ProductionMaterialMapperEx;
import com.jsh.erp.service.material.MaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 生产物料管理服务类
 */
@Service
public class ProductionMaterialService {
    private Logger logger = LoggerFactory.getLogger(ProductionMaterialService.class);

    @Resource
    private ProductionMaterialMapper productionMaterialMapper;
    @Resource
    private ProductionMaterialMapperEx productionMaterialMapperEx;
    @Resource
    private MaterialService materialService;

    /**
     * 创建工单物料清单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void createMaterialList(Long productionOrderId, JSONArray materialArray) throws Exception {
        try {
            for (int i = 0; i < materialArray.size(); i++) {
                JSONObject materialObj = materialArray.getJSONObject(i);
                
                ProductionMaterial material = new ProductionMaterial();
                material.setProductionOrderId(productionOrderId);
                material.setMaterialId(materialObj.getLong("materialId"));
                material.setMaterialName(materialObj.getString("materialName"));
                material.setMaterialSpec(materialObj.getString("materialSpec"));
                material.setRequiredQuantity(materialObj.getBigDecimal("requiredQuantity"));
                material.setUnit(materialObj.getString("unit"));
                material.setUnitCost(materialObj.getBigDecimal("unitCost"));
                material.setTotalCost(material.getRequiredQuantity().multiply(material.getUnitCost()));
                material.setSourceType(materialObj.getString("sourceType"));
                
                // 设置多租户字段
                material.setTenantId(getCurrentTenantId());
                
                productionMaterialMapper.insertSelective(material);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            throw e;
        }
    }

    /**
     * 检查物料库存可用性
     */
    public boolean checkMaterialAvailability(Long productionOrderId) throws Exception {
        try {
            List<ProductionMaterial> materials = productionMaterialMapperEx
                .selectByProductionOrderId(productionOrderId);
            
            for (ProductionMaterial material : materials) {
                if ("1".equals(material.getSourceType())) { // 库存来源
                    // 检查库存是否充足
                    BigDecimal currentStock = getCurrentStock(material.getMaterialId());
                    if (currentStock.compareTo(material.getRequiredQuantity()) < 0) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return false;
        }
    }

    /**
     * 预留物料
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int reserveMaterials(Long productionOrderId) throws Exception {
        try {
            List<ProductionMaterial> materials = productionMaterialMapperEx
                .selectByProductionOrderId(productionOrderId);
            
            int reservedCount = 0;
            for (ProductionMaterial material : materials) {
                if ("1".equals(material.getSourceType()) && "0".equals(material.getReserveStatus())) {
                    // 预留库存
                    boolean reserved = reserveStock(material.getMaterialId(), material.getRequiredQuantity());
                    if (reserved) {
                        // 更新预留状态
                        productionMaterialMapperEx.updateReserveStatus(material.getId(), "1");
                        material.setReserveStatus("1");
                        reservedCount++;
                    }
                }
            }
            return reservedCount;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 消耗物料
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int consumeMaterials(Long productionOrderId, BigDecimal consumedQuantity) throws Exception {
        try {
            // 根据BOM比例消耗物料
            List<ProductionMaterial> materials = productionMaterialMapperEx
                .selectByProductionOrderId(productionOrderId);
            
            int consumedCount = 0;
            for (ProductionMaterial material : materials) {
                // 计算实际消耗量
                BigDecimal actualConsumption = material.getRequiredQuantity()
                    .multiply(consumedQuantity)
                    .divide(getOrderQuantity(productionOrderId), 6, BigDecimal.ROUND_HALF_UP);
                
                // 更新消耗量
                productionMaterialMapperEx.updateConsumedQuantity(
                    material.getId(), actualConsumption);
                consumedCount++;
            }
            return consumedCount;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 计算物料成本
     */
    public BigDecimal calculateMaterialCost(Long productionOrderId) throws Exception {
        try {
            List<ProductionMaterial> materials = productionMaterialMapperEx
                .selectByProductionOrderId(productionOrderId);
            
            BigDecimal totalCost = BigDecimal.ZERO;
            for (ProductionMaterial material : materials) {
                if (material.getTotalCost() != null) {
                    totalCost = totalCost.add(material.getTotalCost());
                }
            }
            return totalCost;
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return BigDecimal.ZERO;
        }
    }

    // 私有辅助方法
    
    private BigDecimal getCurrentStock(Long materialId) {
        // TODO: 集成库存服务获取当前库存
        return BigDecimal.ZERO;
    }
    
    private boolean reserveStock(Long materialId, BigDecimal quantity) {
        // TODO: 调用库存服务预留库存
        return true;
    }
    
    private BigDecimal getOrderQuantity(Long productionOrderId) {
        // TODO: 获取工单数量
        return BigDecimal.ONE;
    }
    
    private Long getCurrentTenantId() {
        // TODO: 从上下文获取租户ID
        return 1L;
    }
}
```

### 3. WorkReportService.java - 报工服务

**文件路径**: `com.jsh.erp.production.service.WorkReportService`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.production.datasource.entities.WorkReport;
import com.jsh.erp.production.datasource.mappers.WorkReportMapper;
import com.jsh.erp.production.datasource.mappers.WorkReportMapperEx;
import com.jsh.erp.production.utils.WorkReportNoGenerator;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 移动端报工服务类
 */
@Service
public class WorkReportService {
    private Logger logger = LoggerFactory.getLogger(WorkReportService.class);

    @Resource
    private WorkReportMapper workReportMapper;
    @Resource
    private WorkReportMapperEx workReportMapperEx;
    @Resource
    private ProductionOrderService productionOrderService;
    @Resource
    private LogService logService;

    /**
     * 提交报工记录
     * 移动端核心功能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int submitWorkReport(JSONObject obj, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证生产工单
            Long productionOrderId = obj.getLong("productionOrderId");
            if (productionOrderId == null) {
                throw new BusinessRunTimeException("生产工单ID不能为空");
            }
            
            // 2. 创建报工记录
            WorkReport workReport = new WorkReport();
            workReport.setReportNo(WorkReportNoGenerator.generate());
            workReport.setProductionOrderId(productionOrderId);
            workReport.setWorkerId(obj.getLong("workerId"));
            workReport.setWorkerName(obj.getString("workerName"));
            
            // 3. 设置作业信息
            String workDateStr = obj.getString("workDate");
            workReport.setWorkDate(new SimpleDateFormat("yyyy-MM-dd").parse(workDateStr));
            workReport.setStartTime(obj.getDate("startTime"));
            workReport.setEndTime(obj.getDate("endTime"));
            
            // 4. 计算工作时长
            if (workReport.getStartTime() != null && workReport.getEndTime() != null) {
                long diffInMillies = workReport.getEndTime().getTime() - workReport.getStartTime().getTime();
                BigDecimal hours = new BigDecimal(diffInMillies).divide(new BigDecimal(3600000), 2, BigDecimal.ROUND_HALF_UP);
                workReport.setWorkHours(hours);
            }
            
            // 5. 设置完成数量和质量信息
            workReport.setCompletedQuantity(obj.getBigDecimal("completedQuantity"));
            workReport.setQualifiedQuantity(obj.getBigDecimal("qualifiedQuantity"));
            workReport.setDefectiveQuantity(obj.getBigDecimal("defectiveQuantity"));
            workReport.setWorkContent(obj.getString("workContent"));
            workReport.setQualityNotes(obj.getString("qualityNotes"));
            
            // 6. 设置位置和设备信息
            workReport.setLocationInfo(obj.getString("locationInfo"));
            workReport.setDeviceInfo(obj.getString("deviceInfo"));
            
            // 7. 设置完工状态
            workReport.setIsCompleted(obj.getString("isCompleted"));
            
            // 8. 设置多租户字段
            workReport.setTenantId(getCurrentTenantId());
            
            // 9. 插入报工记录
            workReportMapper.insertSelective(workReport);
            
            // 10. 如果是完工报告，更新工单状态
            if ("1".equals(workReport.getIsCompleted())) {
                productionOrderService.updateOrderStatus(productionOrderId, "2", request);
            }
            
            // 11. 记录日志
            logService.insertLog("移动报工", 
                new StringBuffer("提交报工：报工单号 ")
                    .append(workReport.getReportNo()).toString(), 
                request);
            
            return 1;
            
        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 上传制作照片
     * 移动端照片上传功能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String uploadPhotos(Long workReportId, MultipartFile[] photos) throws Exception {
        try {
            List<String> photoUrls = new ArrayList<>();
            
            for (MultipartFile photo : photos) {
                if (!photo.isEmpty()) {
                    // 上传文件到文件服务器
                    String photoUrl = uploadFile(photo);
                    photoUrls.add(photoUrl);
                }
            }
            
            // 更新报工记录的照片URLs
            JSONArray photoArray = new JSONArray();
            photoArray.addAll(photoUrls);
            String photoUrlsJson = photoArray.toJSONString();
            
            workReportMapperEx.updatePhotoUrls(workReportId, photoUrlsJson);
            
            return photoUrlsJson;
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            throw e;
        }
    }

    /**
     * 完工确认
     * 移动端一键完工功能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int completeWork(Long workReportId, HttpServletRequest request) throws Exception {
        try {
            // 1. 获取报工记录
            WorkReport workReport = workReportMapper.selectByPrimaryKey(workReportId);
            if (workReport == null) {
                throw new BusinessRunTimeException("报工记录不存在");
            }
            
            // 2. 更新完工状态
            workReportMapperEx.updateCompleteStatus(workReportId, "1");
            
            // 3. 更新生产工单状态为已完工
            productionOrderService.updateOrderStatus(workReport.getProductionOrderId(), "2", request);
            
            // 4. 记录日志
            logService.insertLog("移动报工", 
                new StringBuffer("完工确认：报工单号 ")
                    .append(workReport.getReportNo()).toString(), 
                request);
            
            return 1;
            
        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
    }

    /**
     * 根据生产工单查询报工记录
     */
    public List<WorkReport> getReportsByOrderId(Long productionOrderId) throws Exception {
        try {
            return workReportMapperEx.selectByProductionOrderId(productionOrderId, getCurrentTenantId());
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return new ArrayList<>();
        }
    }

    /**
     * 根据制作人员查询报工记录
     */
    public List<WorkReport> getReportsByWorker(Long workerId, Date startDate, Date endDate) throws Exception {
        try {
            return workReportMapperEx.selectByWorkerAndDateRange(workerId, startDate, endDate, getCurrentTenantId());
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return new ArrayList<>();
        }
    }

    // 私有辅助方法
    
    private String uploadFile(MultipartFile file) throws Exception {
        // TODO: 实现文件上传逻辑
        return "/uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
    }
    
    private Long getCurrentTenantId() {
        // TODO: 从上下文获取租户ID
        return 1L;
    }
}
```

---

## Controller层开发 (2天)

### 1. ProductionOrderController.java

**文件路径**: `com.jsh.erp.production.controller.ProductionOrderController`

```java
package com.jsh.erp.production.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.constants.Constants;
import com.jsh.erp.production.datasource.vo.ProductionOrderVo4List;
import com.jsh.erp.production.service.ProductionOrderService;
import com.jsh.erp.utils.ResponseJsonUtil;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生产工单管理Controller
 * 遵循jshERP Controller开发规范
 * 
 * @author jshERP
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/production/orders")
@Api(tags = {"生产工单管理"})
public class ProductionOrderController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ProductionOrderController.class);

    @Resource
    private ProductionOrderService productionOrderService;

    /**
     * 获取生产工单列表
     * 标准列表查询接口
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取生产工单列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        List<ProductionOrderVo4List> list = productionOrderService.select(search);
        return getDataTable(list);
    }

    /**
     * 创建生产工单
     * 支持从销售订单创建和手动创建两种方式
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "创建生产工单")
    public String addResource(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = productionOrderService.createOrderFromSale(obj, request);
        return ResponseJsonUtil.returnStr(objectMap, insert);
    }

    /**
     * 分配制作人员
     * 专用于人员分配的接口
     */
    @PutMapping(value = "/{id}/assign")
    @ApiOperation(value = "分配制作人员")
    public String assignWorker(@PathVariable("id") Long id,
                              @RequestBody JSONObject obj,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        Long workerId = obj.getLong("workerId");
        int result = productionOrderService.assignWorker(id, workerId, request);
        return ResponseJsonUtil.returnStr(objectMap, result);
    }

    /**
     * 更新工单状态
     * 专用于状态更新的接口
     */
    @PutMapping(value = "/{id}/status")
    @ApiOperation(value = "更新工单状态")
    public String updateStatus(@PathVariable("id") Long id,
                              @RequestBody JSONObject obj,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        String status = obj.getString("status");
        int result = productionOrderService.updateOrderStatus(id, status, request);
        return ResponseJsonUtil.returnStr(objectMap, result);
    }

    /**
     * 获取工单详情
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "获取工单详情")
    public String getDetail(@PathVariable("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            // TODO: 实现获取详情逻辑
            objectMap.put("data", null);
        } catch (Exception e) {
            logger.error("获取工单详情失败", e);
        }
        return ResponseJsonUtil.returnJson(objectMap, "查询成功", 200);
    }

    /**
     * 根据制作人员获取工单列表（移动端使用）
     */
    @GetMapping(value = "/worker/{workerId}")
    @ApiOperation(value = "根据制作人员获取工单列表")
    public TableDataInfo getOrdersByWorker(@PathVariable("workerId") Long workerId,
                                          @RequestParam(value = "status", required = false) String status,
                                          HttpServletRequest request) throws Exception {
        List<ProductionOrderVo4List> list = productionOrderService.selectByWorker(workerId, status);
        return getDataTable(list);
    }

    /**
     * 获取工单统计信息
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取工单统计信息")
    public String getStatistics(HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            List<Map<String, Object>> statistics = productionOrderService.getOrderStatistics();
            objectMap.put("statistics", statistics);
        } catch (Exception e) {
            logger.error("获取工单统计失败", e);
        }
        return ResponseJsonUtil.returnJson(objectMap, "查询成功", 200);
    }

    /**
     * 删除生产工单
     * 逻辑删除，保留数据
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除生产工单")
    public String deleteResource(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        // TODO: 实现删除逻辑
        int delete = 0; // productionOrderService.deleteOrder(id, request);
        return ResponseJsonUtil.returnStr(objectMap, delete);
    }

    /**
     * 批量删除生产工单
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除生产工单")
    public String deleteBatch(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        // TODO: 实现批量删除逻辑
        int delete = 0; // productionOrderService.deleteBatch(ids, request);
        return ResponseJsonUtil.returnStr(objectMap, delete);
    }
}
```

### 2. WorkReportController.java

**文件路径**: `com.jsh.erp.production.controller.WorkReportController`

```java
package com.jsh.erp.production.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.production.datasource.entities.WorkReport;
import com.jsh.erp.production.service.WorkReportService;
import com.jsh.erp.utils.ResponseJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 移动端报工Controller
 * 专为移动端优化的接口设计
 * 
 * @author jshERP
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/production/reports")
@Api(tags = {"移动端报工管理"})
public class WorkReportController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(WorkReportController.class);

    @Resource
    private WorkReportService workReportService;

    /**
     * 提交报工记录
     * 移动端核心接口
     */
    @PostMapping(value = "/submit")
    @ApiOperation(value = "提交报工记录")
    public String submitReport(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = workReportService.submitWorkReport(obj, request);
        return ResponseJsonUtil.returnStr(objectMap, result);
    }

    /**
     * 上传制作照片
     * 移动端照片上传接口
     */
    @PostMapping(value = "/{reportId}/photos")
    @ApiOperation(value = "上传制作照片")
    public String uploadPhotos(@PathVariable("reportId") Long reportId,
                              @RequestParam("photos") MultipartFile[] photos,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            String photoUrls = workReportService.uploadPhotos(reportId, photos);
            objectMap.put("photoUrls", photoUrls);
        } catch (Exception e) {
            logger.error("上传照片失败", e);
        }
        return ResponseJsonUtil.returnJson(objectMap, "上传成功", 200);
    }

    /**
     * 完工确认
     * 移动端一键完工接口
     */
    @PutMapping(value = "/{reportId}/complete")
    @ApiOperation(value = "完工确认")
    public String completeWork(@PathVariable("reportId") Long reportId,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = workReportService.completeWork(reportId, request);
        return ResponseJsonUtil.returnStr(objectMap, result);
    }

    /**
     * 根据生产工单查询报工记录
     */
    @GetMapping(value = "/order/{orderId}")
    @ApiOperation(value = "根据生产工单查询报工记录")
    public TableDataInfo getReportsByOrder(@PathVariable("orderId") Long orderId,
                                          HttpServletRequest request) throws Exception {
        List<WorkReport> list = workReportService.getReportsByOrderId(orderId);
        return getDataTable(list);
    }

    /**
     * 根据制作人员查询报工记录
     * 移动端个人工作记录查询
     */
    @GetMapping(value = "/worker/{workerId}")
    @ApiOperation(value = "根据制作人员查询报工记录")
    public TableDataInfo getReportsByWorker(@PathVariable("workerId") Long workerId,
                                           @RequestParam(value = "startDate", required = false) String startDateStr,
                                           @RequestParam(value = "endDate", required = false) String endDateStr,
                                           HttpServletRequest request) throws Exception {
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = startDateStr != null ? sdf.parse(startDateStr) : null;
        Date endDate = endDateStr != null ? sdf.parse(endDateStr) : null;
        
        List<WorkReport> list = workReportService.getReportsByWorker(workerId, startDate, endDate);
        return getDataTable(list);
    }

    /**
     * 获取报工记录详情
     */
    @GetMapping(value = "/{reportId}")
    @ApiOperation(value = "获取报工记录详情")
    public String getReportDetail(@PathVariable("reportId") Long reportId,
                                 HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            // TODO: 实现获取详情逻辑
            objectMap.put("data", null);
        } catch (Exception e) {
            logger.error("获取报工详情失败", e);
        }
        return ResponseJsonUtil.returnJson(objectMap, "查询成功", 200);
    }
}
```

---

## 验收标准

### 实体类验收标准
1. ✅ 所有实体类包含完整的多租户和审计字段
2. ✅ getter/setter方法完整，遵循JavaBean规范
3. ✅ 构造函数合理，包含必要的默认值设置
4. ✅ VO类设计合理，满足前端展示需求

### Mapper接口验收标准
1. ✅ 基础Mapper由MyBatis Generator生成
2. ✅ 扩展Mapper包含所有业务查询方法
3. ✅ XML映射文件SQL语句正确，包含多租户过滤
4. ✅ 查询结果映射完整，支持关联查询

### Service层验收标准
1. ✅ 所有核心业务方法实现完整
2. ✅ 事务注解正确配置，异常处理完善
3. ✅ 日志记录规范，包含关键操作日志
4. ✅ 业务验证逻辑完整，错误提示友好

### Controller层验收标准
1. ✅ RESTful接口设计规范，URL命名标准
2. ✅ 请求参数验证完整，响应格式统一
3. ✅ API文档注解完善，支持Swagger生成
4. ✅ 异常处理机制完整，错误响应标准

---

## 交付物清单

1. **实体类文件**: 完整的实体类和VO类定义
2. **Mapper接口和XML**: 数据访问层完整实现
3. **Service层代码**: 核心业务逻辑实现
4. **Controller层代码**: 完整的API接口实现
5. **单元测试用例**: 关键方法的测试用例
6. **API接口文档**: Swagger生成的接口文档

---

## 下周准备工作

1. **集成测试**: 准备订单驱动生产流转的集成测试
2. **数据初始化**: 准备测试数据和基础配置数据
3. **接口联调**: 与采购、库存模块的接口联调准备
4. **性能测试**: 核心查询接口的性能测试

---

*本文档严格遵循《jshERP_二次开发技术参考手册》的开发规范，确保代码质量和系统集成性。*