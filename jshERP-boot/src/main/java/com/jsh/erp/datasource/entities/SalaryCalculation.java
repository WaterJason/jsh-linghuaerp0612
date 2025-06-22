package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 薪酬计算记录实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_salary_calculation")
public class SalaryCalculation {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("calculation_number")
    private String calculationNumber;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("calculation_month")
    private String calculationMonth;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("fixed_amount")
    private BigDecimal fixedAmount;

    @TableField("commission_amount")
    private BigDecimal commissionAmount;

    @TableField("allowance_amount")
    private BigDecimal allowanceAmount;

    @TableField("deduction_amount")
    private BigDecimal deductionAmount;

    @TableField("actual_amount")
    private BigDecimal actualAmount;

    @TableField("status")
    private String status;

    @TableField("calculation_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date calculationDate;

    @TableField("approver_id")
    private Long approverId;

    @TableField("approve_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveDate;

    @TableField("approve_remark")
    private String approveRemark;

    @TableField("remark")
    private String remark;

    @TableField("tenant_id")
    private Long tenantId;

    @TableLogic
    @TableField("delete_flag")
    private String deleteFlag;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @TableField("creator")
    private Long creator;

    @TableField("updater")
    private Long updater;

    // 构造函数
    public SalaryCalculation() {
    }

    public SalaryCalculation(Long employeeId, String employeeName, String calculationMonth) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.calculationMonth = calculationMonth;
        this.status = "DRAFT";
        this.totalAmount = BigDecimal.ZERO;
        this.fixedAmount = BigDecimal.ZERO;
        this.commissionAmount = BigDecimal.ZERO;
        this.allowanceAmount = BigDecimal.ZERO;
        this.deductionAmount = BigDecimal.ZERO;
        this.actualAmount = BigDecimal.ZERO;
        this.deleteFlag = "0";
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalculationNumber() {
        return calculationNumber;
    }

    public void setCalculationNumber(String calculationNumber) {
        this.calculationNumber = calculationNumber;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getCalculationMonth() {
        return calculationMonth;
    }

    public void setCalculationMonth(String calculationMonth) {
        this.calculationMonth = calculationMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getFixedAmount() {
        return fixedAmount;
    }

    public void setFixedAmount(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }

    public BigDecimal getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(BigDecimal commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public BigDecimal getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(BigDecimal allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public BigDecimal getDeductionAmount() {
        return deductionAmount;
    }

    public void setDeductionAmount(BigDecimal deductionAmount) {
        this.deductionAmount = deductionAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCalculationDate() {
        return calculationDate;
    }

    public void setCalculationDate(Date calculationDate) {
        this.calculationDate = calculationDate;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public Date getApproveDate() {
        return approveDate;
    }

    public void setApproveDate(Date approveDate) {
        this.approveDate = approveDate;
    }

    public String getApproveRemark() {
        return approveRemark;
    }

    public void setApproveRemark(String approveRemark) {
        this.approveRemark = approveRemark;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public Long getUpdater() {
        return updater;
    }

    public void setUpdater(Long updater) {
        this.updater = updater;
    }

    @Override
    public String toString() {
        return "SalaryCalculation{" +
                "id=" + id +
                ", calculationNumber='" + calculationNumber + '\'' +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", calculationMonth='" + calculationMonth + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
