package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 薪酬发放记录实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_salary_payment")
public class SalaryPayment {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("payment_number")
    private String paymentNumber;

    @TableField("calculation_id")
    private Long calculationId;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("employee_name")
    private String employeeName;

    @TableField("calculation_month")
    private String calculationMonth;

    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    @TableField("payment_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date paymentDate;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("bank_account")
    private String bankAccount;

    @TableField("bank_name")
    private String bankName;

    @TableField("payment_status")
    private String paymentStatus;

    @TableField("financial_voucher_id")
    private Long financialVoucherId;

    @TableField("failure_reason")
    private String failureReason;

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
    public SalaryPayment() {
    }

    public SalaryPayment(Long calculationId, Long employeeId, String employeeName, 
                        String calculationMonth, BigDecimal paymentAmount) {
        this.calculationId = calculationId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.calculationMonth = calculationMonth;
        this.paymentAmount = paymentAmount;
        this.paymentStatus = "PENDING";
        this.paymentMethod = "BANK_TRANSFER";
        this.deleteFlag = "0";
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentNumber() {
        return paymentNumber;
    }

    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    public Long getCalculationId() {
        return calculationId;
    }

    public void setCalculationId(Long calculationId) {
        this.calculationId = calculationId;
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

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Long getFinancialVoucherId() {
        return financialVoucherId;
    }

    public void setFinancialVoucherId(Long financialVoucherId) {
        this.financialVoucherId = financialVoucherId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
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
        return "SalaryPayment{" +
                "id=" + id +
                ", paymentNumber='" + paymentNumber + '\'' +
                ", calculationId=" + calculationId +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", calculationMonth='" + calculationMonth + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
