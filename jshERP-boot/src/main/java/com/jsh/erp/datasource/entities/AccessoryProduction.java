package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 配饰制作记录实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_accessory_production")
public class AccessoryProduction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNumber;

    /**
     * 半成品ID
     */
    private Long semiProductId;

    /**
     * 半成品名称
     */
    private String semiProductName;

    /**
     * 配饰材料ID
     */
    private Long accessoryMaterialId;

    /**
     * 配饰材料名称
     */
    private String accessoryMaterialName;

    /**
     * 制作数量
     */
    private BigDecimal quantity;

    /**
     * 单位ID
     */
    private Long unitId;

    /**
     * 单位名称
     */
    private String unitName;

    /**
     * 人工成本
     */
    private BigDecimal laborCostAmount;

    /**
     * 工人ID
     */
    private Long workerId;

    /**
     * 工人姓名
     */
    private String workerName;

    /**
     * 工资金额
     */
    private BigDecimal salaryAmount;

    /**
     * 状态：PENDING-待开始,IN_PROGRESS-进行中,COMPLETED-已完成,CANCELLED-已取消
     */
    private String status;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 配饰类型
     */
    private String accessoryType;

    /**
     * 配饰风格
     */
    private String accessoryStyle;

    /**
     * 组装方式
     */
    private String assemblyMethod;

    /**
     * 后处理工艺
     */
    private String finishingProcess;

    /**
     * 难度等级
     */
    private String difficultyLevel;

    /**
     * 质量等级
     */
    private String qualityGrade;

    /**
     * 质量评分
     */
    private BigDecimal qualityScore;

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
    public AccessoryProduction() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getSemiProductId() {
        return semiProductId;
    }

    public void setSemiProductId(Long semiProductId) {
        this.semiProductId = semiProductId;
    }

    public String getSemiProductName() {
        return semiProductName;
    }

    public void setSemiProductName(String semiProductName) {
        this.semiProductName = semiProductName;
    }

    public Long getAccessoryMaterialId() {
        return accessoryMaterialId;
    }

    public void setAccessoryMaterialId(Long accessoryMaterialId) {
        this.accessoryMaterialId = accessoryMaterialId;
    }

    public String getAccessoryMaterialName() {
        return accessoryMaterialName;
    }

    public void setAccessoryMaterialName(String accessoryMaterialName) {
        this.accessoryMaterialName = accessoryMaterialName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public BigDecimal getLaborCostAmount() {
        return laborCostAmount;
    }

    public void setLaborCostAmount(BigDecimal laborCostAmount) {
        this.laborCostAmount = laborCostAmount;
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
        this.workerName = workerName;
    }

    public BigDecimal getSalaryAmount() {
        return salaryAmount;
    }

    public void setSalaryAmount(BigDecimal salaryAmount) {
        this.salaryAmount = salaryAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAccessoryType() {
        return accessoryType;
    }

    public void setAccessoryType(String accessoryType) {
        this.accessoryType = accessoryType;
    }

    public String getAccessoryStyle() {
        return accessoryStyle;
    }

    public void setAccessoryStyle(String accessoryStyle) {
        this.accessoryStyle = accessoryStyle;
    }

    public String getAssemblyMethod() {
        return assemblyMethod;
    }

    public void setAssemblyMethod(String assemblyMethod) {
        this.assemblyMethod = assemblyMethod;
    }

    public String getFinishingProcess() {
        return finishingProcess;
    }

    public void setFinishingProcess(String finishingProcess) {
        this.finishingProcess = finishingProcess;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getQualityGrade() {
        return qualityGrade;
    }

    public void setQualityGrade(String qualityGrade) {
        this.qualityGrade = qualityGrade;
    }

    public BigDecimal getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(BigDecimal qualityScore) {
        this.qualityScore = qualityScore;
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
        return "AccessoryProduction{" +
                "id=" + id +
                ", orderNumber='" + orderNumber + '\'' +
                ", semiProductName='" + semiProductName + '\'' +
                ", accessoryMaterialName='" + accessoryMaterialName + '\'' +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                ", workerName='" + workerName + '\'' +
                '}';
    }
}
