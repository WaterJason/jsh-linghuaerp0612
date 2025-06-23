package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产统计实体类
 * 对应数据库表：jsh_production_statistics
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public class ProductionStatistics {
    
    private Long id;
    
    private Date statDate;
    
    private String statType;
    
    private Long workshopId;
    
    private String workshopName;
    
    private Long workerId;
    
    private String workerName;
    
    private Long productId;
    
    private String productName;
    
    // 工单统计
    private Integer totalWorkOrders;
    
    private Integer completedWorkOrders;
    
    private Integer pendingWorkOrders;
    
    private Integer cancelledWorkOrders;
    
    // 任务统计
    private Integer totalTasks;
    
    private Integer completedTasks;
    
    private Integer inProgressTasks;
    
    private Integer pendingTasks;
    
    // 产量统计
    private BigDecimal plannedQuantity;
    
    private BigDecimal actualQuantity;
    
    private BigDecimal qualifiedQuantity;
    
    private BigDecimal defectiveQuantity;
    
    // 时间统计
    private BigDecimal plannedHours;
    
    private BigDecimal actualHours;
    
    private BigDecimal overtimeHours;
    
    // 效率统计
    private BigDecimal productionEfficiency;
    
    private BigDecimal qualityRate;
    
    private BigDecimal onTimeDeliveryRate;
    
    // 成本统计
    private BigDecimal materialCost;
    
    private BigDecimal laborCost;
    
    private BigDecimal overheadCost;
    
    private BigDecimal totalCost;
    
    private String remark;
    
    private Long tenantId;
    
    private String deleteFlag;
    
    private Date createTime;
    
    private Long createUser;
    
    private Date updateTime;
    
    private Long updateUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public String getStatType() {
        return statType;
    }

    public void setStatType(String statType) {
        this.statType = statType == null ? null : statType.trim();
    }

    public Long getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
    }

    public String getWorkshopName() {
        return workshopName;
    }

    public void setWorkshopName(String workshopName) {
        this.workshopName = workshopName == null ? null : workshopName.trim();
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName == null ? null : workerName.trim();
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public Integer getTotalWorkOrders() {
        return totalWorkOrders;
    }

    public void setTotalWorkOrders(Integer totalWorkOrders) {
        this.totalWorkOrders = totalWorkOrders;
    }

    public Integer getCompletedWorkOrders() {
        return completedWorkOrders;
    }

    public void setCompletedWorkOrders(Integer completedWorkOrders) {
        this.completedWorkOrders = completedWorkOrders;
    }

    public Integer getPendingWorkOrders() {
        return pendingWorkOrders;
    }

    public void setPendingWorkOrders(Integer pendingWorkOrders) {
        this.pendingWorkOrders = pendingWorkOrders;
    }

    public Integer getCancelledWorkOrders() {
        return cancelledWorkOrders;
    }

    public void setCancelledWorkOrders(Integer cancelledWorkOrders) {
        this.cancelledWorkOrders = cancelledWorkOrders;
    }

    public Integer getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(Integer totalTasks) {
        this.totalTasks = totalTasks;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Integer getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(Integer inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public Integer getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Integer pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public BigDecimal getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(BigDecimal plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public BigDecimal getActualQuantity() {
        return actualQuantity;
    }

    public void setActualQuantity(BigDecimal actualQuantity) {
        this.actualQuantity = actualQuantity;
    }

    public BigDecimal getQualifiedQuantity() {
        return qualifiedQuantity;
    }

    public void setQualifiedQuantity(BigDecimal qualifiedQuantity) {
        this.qualifiedQuantity = qualifiedQuantity;
    }

    public BigDecimal getDefectiveQuantity() {
        return defectiveQuantity;
    }

    public void setDefectiveQuantity(BigDecimal defectiveQuantity) {
        this.defectiveQuantity = defectiveQuantity;
    }

    public BigDecimal getPlannedHours() {
        return plannedHours;
    }

    public void setPlannedHours(BigDecimal plannedHours) {
        this.plannedHours = plannedHours;
    }

    public BigDecimal getActualHours() {
        return actualHours;
    }

    public void setActualHours(BigDecimal actualHours) {
        this.actualHours = actualHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public BigDecimal getProductionEfficiency() {
        return productionEfficiency;
    }

    public void setProductionEfficiency(BigDecimal productionEfficiency) {
        this.productionEfficiency = productionEfficiency;
    }

    public BigDecimal getQualityRate() {
        return qualityRate;
    }

    public void setQualityRate(BigDecimal qualityRate) {
        this.qualityRate = qualityRate;
    }

    public BigDecimal getOnTimeDeliveryRate() {
        return onTimeDeliveryRate;
    }

    public void setOnTimeDeliveryRate(BigDecimal onTimeDeliveryRate) {
        this.onTimeDeliveryRate = onTimeDeliveryRate;
    }

    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(BigDecimal materialCost) {
        this.materialCost = materialCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public BigDecimal getOverheadCost() {
        return overheadCost;
    }

    public void setOverheadCost(BigDecimal overheadCost) {
        this.overheadCost = overheadCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag == null ? null : deleteFlag.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }
}
