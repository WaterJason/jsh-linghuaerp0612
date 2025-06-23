package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工人信息实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_production_worker")
public class ProductionWorker implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工人编号
     */
    private String workerNumber;

    /**
     * 工人姓名
     */
    private String workerName;

    /**
     * 工人类型：FULL_TIME-全职,PART_TIME-兼职,TEMPORARY-临时
     */
    private String workerType;

    /**
     * 性别：M-男,F-女
     */
    private String gender;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 地址
     */
    private String address;

    /**
     * 职位
     */
    private String position;

    /**
     * 专业技能
     */
    private String specialty;

    /**
     * 专业技能列表（多个技能用逗号分隔）
     */
    private String specialties;

    /**
     * 技能等级：JUNIOR-初级,INTERMEDIATE-中级,SENIOR-高级,EXPERT-专家
     */
    private String skillLevel;

    /**
     * 工作经验年数
     */
    private Integer experienceYears;

    /**
     * 小时工资
     */
    private BigDecimal hourlyRate;

    /**
     * 计件工资
     */
    private BigDecimal pieceRate;

    /**
     * 月度目标
     */
    private BigDecimal monthlyTarget;

    /**
     * 状态：ACTIVE-在职,INACTIVE-离职,SUSPENDED-停职
     */
    private String status;

    /**
     * 可用状态：AVAILABLE-可用,BUSY-忙碌,OFFLINE-离线
     */
    private String availabilityStatus;

    /**
     * 当前工作负荷
     */
    private Integer currentWorkload;

    /**
     * 最大工作负荷
     */
    private Integer maxWorkload;

    /**
     * 效率评级
     */
    private BigDecimal efficiencyRating;

    /**
     * 质量评级
     */
    private BigDecimal qualityRating;

    /**
     * 入职日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date hireDate;

    /**
     * 部门
     */
    private String department;

    /**
     * 主管ID
     */
    private Long supervisorId;

    /**
     * 主管姓名
     */
    private String supervisorName;

    /**
     * 紧急联系人
     */
    private String emergencyContact;

    /**
     * 紧急联系电话
     */
    private String emergencyPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 删除标记：0-正常,1-删除
     */
    private String deleteFlag;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 更新人
     */
    private Long updateUser;

    // 构造方法
    public ProductionWorker() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWorkerNumber() {
        return workerNumber;
    }

    public void setWorkerNumber(String workerNumber) {
        this.workerNumber = workerNumber;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getSpecialties() {
        return specialties;
    }

    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
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

    public BigDecimal getMonthlyTarget() {
        return monthlyTarget;
    }

    public void setMonthlyTarget(BigDecimal monthlyTarget) {
        this.monthlyTarget = monthlyTarget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Integer getCurrentWorkload() {
        return currentWorkload;
    }

    public void setCurrentWorkload(Integer currentWorkload) {
        this.currentWorkload = currentWorkload;
    }

    public Integer getMaxWorkload() {
        return maxWorkload;
    }

    public void setMaxWorkload(Integer maxWorkload) {
        this.maxWorkload = maxWorkload;
    }

    public BigDecimal getEfficiencyRating() {
        return efficiencyRating;
    }

    public void setEfficiencyRating(BigDecimal efficiencyRating) {
        this.efficiencyRating = efficiencyRating;
    }

    public BigDecimal getQualityRating() {
        return qualityRating;
    }

    public void setQualityRating(BigDecimal qualityRating) {
        this.qualityRating = qualityRating;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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
        this.deleteFlag = deleteFlag;
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

    @Override
    public String toString() {
        return "ProductionWorker{" +
                "id=" + id +
                ", workerNumber='" + workerNumber + '\'' +
                ", workerName='" + workerName + '\'' +
                ", workerType='" + workerType + '\'' +
                ", specialty='" + specialty + '\'' +
                ", skillLevel='" + skillLevel + '\'' +
                ", status='" + status + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
