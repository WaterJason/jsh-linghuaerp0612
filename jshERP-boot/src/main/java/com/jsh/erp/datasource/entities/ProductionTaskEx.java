package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

/**
 * 生产任务扩展实体类
 * 包含关联信息的查询结果
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public class ProductionTaskEx extends ProductionTask {
    
    // 工单相关信息
    private String workOrderName;
    
    private String workOrderStatus;
    
    private String workOrderPriority;
    
    // 产品相关信息
    private String materialName;
    
    private String materialModel;
    
    private String materialStandard;
    
    private String categoryName;
    
    // 工人相关信息
    private String workerLoginName;
    
    private String workerPosition;
    
    private String workerDepartment;
    
    private String workerPhone;
    
    // 工人技能信息
    private String skillLevel;
    
    private BigDecimal skillScore;
    
    private BigDecimal efficiencyRate;
    
    private BigDecimal workerQualityRate;
    
    // 进度信息
    private BigDecimal completionRate;
    
    private BigDecimal qualificationRate;
    
    private BigDecimal progressRate;
    
    // 时间信息
    private String assignTimeStr;
    
    private String startTimeStr;
    
    private String completeTimeStr;
    
    private String planStartTimeStr;
    
    private String planEndTimeStr;
    
    private String createTimeStr;
    
    private String updateTimeStr;
    
    // 用户信息
    private String createUserName;
    
    private String updateUserName;
    
    // 统计信息
    private Integer reportCount;
    
    private Integer qualityCheckCount;
    
    private BigDecimal totalReportedHours;
    
    private BigDecimal averageQualityScore;
    
    // 状态描述
    private String statusName;
    
    private String priorityName;
    
    private String taskTypeName;
    
    // 延期信息
    private Boolean isOverdue;
    
    private Integer overdueDays;
    
    private String overdueReason;

    public String getWorkOrderName() {
        return workOrderName;
    }

    public void setWorkOrderName(String workOrderName) {
        this.workOrderName = workOrderName;
    }

    public String getWorkOrderStatus() {
        return workOrderStatus;
    }

    public void setWorkOrderStatus(String workOrderStatus) {
        this.workOrderStatus = workOrderStatus;
    }

    public String getWorkOrderPriority() {
        return workOrderPriority;
    }

    public void setWorkOrderPriority(String workOrderPriority) {
        this.workOrderPriority = workOrderPriority;
    }

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

    public String getWorkerLoginName() {
        return workerLoginName;
    }

    public void setWorkerLoginName(String workerLoginName) {
        this.workerLoginName = workerLoginName;
    }

    public String getWorkerPosition() {
        return workerPosition;
    }

    public void setWorkerPosition(String workerPosition) {
        this.workerPosition = workerPosition;
    }

    public String getWorkerDepartment() {
        return workerDepartment;
    }

    public void setWorkerDepartment(String workerDepartment) {
        this.workerDepartment = workerDepartment;
    }

    public String getWorkerPhone() {
        return workerPhone;
    }

    public void setWorkerPhone(String workerPhone) {
        this.workerPhone = workerPhone;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public BigDecimal getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(BigDecimal skillScore) {
        this.skillScore = skillScore;
    }

    public BigDecimal getEfficiencyRate() {
        return efficiencyRate;
    }

    public void setEfficiencyRate(BigDecimal efficiencyRate) {
        this.efficiencyRate = efficiencyRate;
    }

    public BigDecimal getWorkerQualityRate() {
        return workerQualityRate;
    }

    public void setWorkerQualityRate(BigDecimal workerQualityRate) {
        this.workerQualityRate = workerQualityRate;
    }

    public BigDecimal getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(BigDecimal completionRate) {
        this.completionRate = completionRate;
    }

    public BigDecimal getQualificationRate() {
        return qualificationRate;
    }

    public void setQualificationRate(BigDecimal qualificationRate) {
        this.qualificationRate = qualificationRate;
    }

    public BigDecimal getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(BigDecimal progressRate) {
        this.progressRate = progressRate;
    }

    public String getAssignTimeStr() {
        return assignTimeStr;
    }

    public void setAssignTimeStr(String assignTimeStr) {
        this.assignTimeStr = assignTimeStr;
    }

    public String getStartTimeStr() {
        return startTimeStr;
    }

    public void setStartTimeStr(String startTimeStr) {
        this.startTimeStr = startTimeStr;
    }

    public String getCompleteTimeStr() {
        return completeTimeStr;
    }

    public void setCompleteTimeStr(String completeTimeStr) {
        this.completeTimeStr = completeTimeStr;
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

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public Integer getQualityCheckCount() {
        return qualityCheckCount;
    }

    public void setQualityCheckCount(Integer qualityCheckCount) {
        this.qualityCheckCount = qualityCheckCount;
    }

    public BigDecimal getTotalReportedHours() {
        return totalReportedHours;
    }

    public void setTotalReportedHours(BigDecimal totalReportedHours) {
        this.totalReportedHours = totalReportedHours;
    }

    public BigDecimal getAverageQualityScore() {
        return averageQualityScore;
    }

    public void setAverageQualityScore(BigDecimal averageQualityScore) {
        this.averageQualityScore = averageQualityScore;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public String getOverdueReason() {
        return overdueReason;
    }

    public void setOverdueReason(String overdueReason) {
        this.overdueReason = overdueReason;
    }
}
