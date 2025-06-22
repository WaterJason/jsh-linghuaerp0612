package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 员工薪酬项目关联实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_employee_salary_item")
public class EmployeeSalaryItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("salary_item_id")
    private Long salaryItemId;

    @TableField("custom_rate")
    private BigDecimal customRate;

    @TableField("custom_amount")
    private BigDecimal customAmount;

    @TableField("effective_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date effectiveDate;

    @TableField("expiry_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDate;

    @TableField("status")
    private String status;

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
    public EmployeeSalaryItem() {
    }

    public EmployeeSalaryItem(Long employeeId, Long salaryItemId, Date effectiveDate) {
        this.employeeId = employeeId;
        this.salaryItemId = salaryItemId;
        this.effectiveDate = effectiveDate;
        this.status = "ACTIVE";
        this.deleteFlag = "0";
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Long getSalaryItemId() {
        return salaryItemId;
    }

    public void setSalaryItemId(Long salaryItemId) {
        this.salaryItemId = salaryItemId;
    }

    public BigDecimal getCustomRate() {
        return customRate;
    }

    public void setCustomRate(BigDecimal customRate) {
        this.customRate = customRate;
    }

    public BigDecimal getCustomAmount() {
        return customAmount;
    }

    public void setCustomAmount(BigDecimal customAmount) {
        this.customAmount = customAmount;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "EmployeeSalaryItem{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", salaryItemId=" + salaryItemId +
                ", customRate=" + customRate +
                ", customAmount=" + customAmount +
                ", effectiveDate=" + effectiveDate +
                ", status='" + status + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
