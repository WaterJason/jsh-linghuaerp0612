package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工人技能实体类
 * 对应数据库表：jsh_worker_skill
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public class WorkerSkill {
    
    private Long id;
    
    private Long workerId;
    
    private String workerName;
    
    private String skillType;
    
    private String skillLevel;
    
    private BigDecimal skillScore;
    
    private String certificationLevel;
    
    private Date certificationDate;
    
    private BigDecimal experienceYears;
    
    private BigDecimal hourlyRate;
    
    private BigDecimal pieceRate;
    
    private BigDecimal efficiencyRate;
    
    private BigDecimal qualityRate;
    
    private Boolean isActive;
    
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

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType == null ? null : skillType.trim();
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel == null ? null : skillLevel.trim();
    }

    public BigDecimal getSkillScore() {
        return skillScore;
    }

    public void setSkillScore(BigDecimal skillScore) {
        this.skillScore = skillScore;
    }

    public String getCertificationLevel() {
        return certificationLevel;
    }

    public void setCertificationLevel(String certificationLevel) {
        this.certificationLevel = certificationLevel == null ? null : certificationLevel.trim();
    }

    public Date getCertificationDate() {
        return certificationDate;
    }

    public void setCertificationDate(Date certificationDate) {
        this.certificationDate = certificationDate;
    }

    public BigDecimal getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(BigDecimal experienceYears) {
        this.experienceYears = experienceYears;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getPieceRate() {
        return pieceRate;
    }

    public void setPieceRate(BigDecimal pieceRate) {
        this.pieceRate = pieceRate;
    }

    public BigDecimal getEfficiencyRate() {
        return efficiencyRate;
    }

    public void setEfficiencyRate(BigDecimal efficiencyRate) {
        this.efficiencyRate = efficiencyRate;
    }

    public BigDecimal getQualityRate() {
        return qualityRate;
    }

    public void setQualityRate(BigDecimal qualityRate) {
        this.qualityRate = qualityRate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
