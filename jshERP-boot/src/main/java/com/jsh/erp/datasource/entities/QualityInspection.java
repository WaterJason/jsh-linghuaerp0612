package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 质量检验实体类
 * 对应数据库表：jsh_quality_inspection
 *
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_quality_inspection")
public class QualityInspection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String inspectionNumber;
    
    private Long taskId;
    
    private String taskNumber;
    
    private Long workOrderId;
    
    private String workOrderNumber;
    
    private Long productId;
    
    private String productName;
    
    private Long inspectorId;
    
    private String inspectorName;
    
    private Date inspectionTime;
    
    private String inspectionType;
    
    private BigDecimal inspectionQuantity;
    
    private BigDecimal qualifiedQuantity;
    
    private BigDecimal defectiveQuantity;
    
    private String unitName;
    
    private BigDecimal qualificationRate;
    
    private String overallResult;
    
    private String qualityGrade;
    
    private BigDecimal overallScore;
    
    private BigDecimal appearanceScore;
    
    private BigDecimal sizeScore;
    
    private BigDecimal colorScore;
    
    private BigDecimal textureScore;
    
    private BigDecimal detailScore;
    
    private BigDecimal overallEffectScore;
    
    private String problemDescription;
    
    private String improvementSuggestion;
    
    private String qualityPhotos;
    
    private String inspectionStandard;
    
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

    public String getInspectionNumber() {
        return inspectionNumber;
    }

    public void setInspectionNumber(String inspectionNumber) {
        this.inspectionNumber = inspectionNumber == null ? null : inspectionNumber.trim();
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskNumber() {
        return taskNumber;
    }

    public void setTaskNumber(String taskNumber) {
        this.taskNumber = taskNumber == null ? null : taskNumber.trim();
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public String getWorkOrderNumber() {
        return workOrderNumber;
    }

    public void setWorkOrderNumber(String workOrderNumber) {
        this.workOrderNumber = workOrderNumber == null ? null : workOrderNumber.trim();
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

    public Long getInspectorId() {
        return inspectorId;
    }

    public void setInspectorId(Long inspectorId) {
        this.inspectorId = inspectorId;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName == null ? null : inspectorName.trim();
    }

    public Date getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(Date inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public String getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(String inspectionType) {
        this.inspectionType = inspectionType == null ? null : inspectionType.trim();
    }

    public BigDecimal getInspectionQuantity() {
        return inspectionQuantity;
    }

    public void setInspectionQuantity(BigDecimal inspectionQuantity) {
        this.inspectionQuantity = inspectionQuantity;
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

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName == null ? null : unitName.trim();
    }

    public BigDecimal getQualificationRate() {
        return qualificationRate;
    }

    public void setQualificationRate(BigDecimal qualificationRate) {
        this.qualificationRate = qualificationRate;
    }

    public String getOverallResult() {
        return overallResult;
    }

    public void setOverallResult(String overallResult) {
        this.overallResult = overallResult == null ? null : overallResult.trim();
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade == null ? null : qualityGrade.trim();
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore;
    }

    public BigDecimal getAppearanceScore() {
        return appearanceScore;
    }

    public void setAppearanceScore(BigDecimal appearanceScore) {
        this.appearanceScore = appearanceScore;
    }

    public BigDecimal getSizeScore() {
        return sizeScore;
    }

    public void setSizeScore(BigDecimal sizeScore) {
        this.sizeScore = sizeScore;
    }

    public BigDecimal getColorScore() {
        return colorScore;
    }

    public void setColorScore(BigDecimal colorScore) {
        this.colorScore = colorScore;
    }

    public BigDecimal getTextureScore() {
        return textureScore;
    }

    public void setTextureScore(BigDecimal textureScore) {
        this.textureScore = textureScore;
    }

    public BigDecimal getDetailScore() {
        return detailScore;
    }

    public void setDetailScore(BigDecimal detailScore) {
        this.detailScore = detailScore;
    }

    public BigDecimal getOverallEffectScore() {
        return overallEffectScore;
    }

    public void setOverallEffectScore(BigDecimal overallEffectScore) {
        this.overallEffectScore = overallEffectScore;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription == null ? null : problemDescription.trim();
    }

    public String getImprovementSuggestion() {
        return improvementSuggestion;
    }

    public void setImprovementSuggestion(String improvementSuggestion) {
        this.improvementSuggestion = improvementSuggestion == null ? null : improvementSuggestion.trim();
    }

    public String getQualityPhotos() {
        return qualityPhotos;
    }

    public void setQualityPhotos(String qualityPhotos) {
        this.qualityPhotos = qualityPhotos == null ? null : qualityPhotos.trim();
    }

    public String getInspectionStandard() {
        return inspectionStandard;
    }

    public void setInspectionStandard(String inspectionStandard) {
        this.inspectionStandard = inspectionStandard == null ? null : inspectionStandard.trim();
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
