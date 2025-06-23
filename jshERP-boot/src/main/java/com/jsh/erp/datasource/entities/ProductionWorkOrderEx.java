package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

/**
 * 生产工单扩展实体类
 * 包含关联信息的查询结果
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public class ProductionWorkOrderEx extends ProductionWorkOrder {
    
    // 产品相关信息
    private String materialName;
    
    private String materialModel;
    
    private String materialStandard;
    
    private String categoryName;
    
    // 单位相关信息
    private String basicUnit;
    
    private BigDecimal ratio;
    
    // 统计信息
    private Integer totalTasks;
    
    private Integer completedTasks;
    
    private Integer pendingTasks;
    
    private Integer inProgressTasks;
    
    // 进度信息
    private BigDecimal completionRate;
    
    private BigDecimal qualityRate;
    
    private BigDecimal costVariance;
    
    private BigDecimal scheduleVariance;
    
    // 时间格式化字段
    private String planStartTimeStr;
    
    private String planEndTimeStr;
    
    private String actualStartTimeStr;
    
    private String actualEndTimeStr;
    
    private String createTimeStr;
    
    private String updateTimeStr;
    
    // 用户信息
    private String createUserName;
    
    private String updateUserName;
    
    // 来源信息
    private String sourceTypeName;
    
    private String sourceInfo;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialModel() {
        return materialModel;
    }

    public void setMaterialModel(String materialModel) {
        this.materialModel = materialModel;
    }

    public String getMaterialStandard() {
        return materialStandard;
    }

    public void setMaterialStandard(String materialStandard) {
        this.materialStandard = materialStandard;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBasicUnit() {
        return basicUnit;
    }

    public void setBasicUnit(String basicUnit) {
        this.basicUnit = basicUnit;
    }

    public BigDecimal getRatio() {
        return ratio;
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
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

    public Integer getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Integer pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public Integer getInProgressTasks() {
        return inProgressTasks;
    }

    public void setInProgressTasks(Integer inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate) {
        this.completionRate = completionRate;
    }

    public BigDecimal getQualityRate() {
        return qualityRate;
    }

    public void setQualityRate(BigDecimal qualityRate) {
        this.qualityRate = qualityRate;
    }

    public BigDecimal getCostVariance() {
        return costVariance;
    }

    public void setCostVariance(BigDecimal costVariance) {
        this.costVariance = costVariance;
    }

    public BigDecimal getScheduleVariance() {
        return scheduleVariance;
    }

    public void setScheduleVariance(BigDecimal scheduleVariance) {
        this.scheduleVariance = scheduleVariance;
    }

    public String getPlanStartTimeStr() {
        return planStartTimeStr;
    }

    public void setPlanStartTimeStr(String planStartTimeStr) {
        this.planStartTimeStr = planStartTimeStr;
    }

    public String getPlanEndTimeStr() {
        return planEndTimeStr;
    }

    public void setPlanEndTimeStr(String planEndTimeStr) {
        this.planEndTimeStr = planEndTimeStr;
    }

    public String getActualStartTimeStr() {
        return actualStartTimeStr;
    }

    public void setActualStartTimeStr(String actualStartTimeStr) {
        this.actualStartTimeStr = actualStartTimeStr;
    }

    public String getActualEndTimeStr() {
        return actualEndTimeStr;
    }

    public void setActualEndTimeStr(String actualEndTimeStr) {
        this.actualEndTimeStr = actualEndTimeStr;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getUpdateTimeStr() {
        return updateTimeStr;
    }

    public void setUpdateTimeStr(String updateTimeStr) {
        this.updateTimeStr = updateTimeStr;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public String getSourceTypeName() {
        return sourceTypeName;
    }

    public void setSourceTypeName(String sourceTypeName) {
        this.sourceTypeName = sourceTypeName;
    }

    public String getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
}
