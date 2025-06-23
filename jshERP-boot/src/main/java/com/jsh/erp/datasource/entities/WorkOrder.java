package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工单实体类
 * 对应数据库表：jsh_work_order
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public class WorkOrder {
    private Long id;

    private String workOrderNumber;

    private Long productionOrderId;

    private String workType;

    private Long handlerUserId;

    private String status;

    private BigDecimal cost;

    private String completionImageUrl;

    private String logisticsNumber;

    private Long tenantId;

    private String deleteFlag;

    private Date createTime;

    private Date updateTime;

    private Long createUser;

    private Long updateUser;

    // 新增字段用于支持WorkOrderServiceImpl
    private Date completeTime;
    private String completeImages;
    private String qualityNotes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber == null ? null : workOrderNumber.trim();
    }

    public Long getProductionOrderId() {
        return productionOrderId;
    }

    public void setProductionOrderId(Long productionOrderId) {
        this.productionOrderId = productionOrderId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType == null ? null : workType.trim();
    }

    public Long getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(Long handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getCompletionImageUrl() {
        return completionImageUrl;
    }

    public void setCompletionImageUrl(String completionImageUrl) {
        this.completionImageUrl = completionImageUrl == null ? null : completionImageUrl.trim();
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber == null ? null : logisticsNumber.trim();
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    public String getCompleteImages() {
        return completeImages;
    }

    public void setCompleteImages(String completeImages) {
        this.completeImages = completeImages == null ? null : completeImages.trim();
    }

    public String getQualityNotes() {
        return qualityNotes;
    }

    public void setQualityNotes(String qualityNotes) {
        this.qualityNotes = qualityNotes == null ? null : qualityNotes.trim();
    }

    // 别名方法，用于兼容WorkOrderServiceImpl中的方法调用
    public String getWorkOrderNo() {
        return this.workOrderNumber;
    }

    public void setWorkOrderNo(String workOrderNo) {
        this.workOrderNumber = workOrderNo == null ? null : workOrderNo.trim();
    }

    public void setCreateBy(Long createBy) {
        this.createUser = createBy;
    }

    public void setUpdateBy(Long updateBy) {
        this.updateUser = updateBy;
    }
}
