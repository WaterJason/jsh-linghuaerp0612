package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 薪酬明细实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_salary_detail")
public class SalaryDetail {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("calculation_id")
    private Long calculationId;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("salary_item_id")
    private Long salaryItemId;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("item_type")
    private String itemType;

    @TableField("base_data")
    private String baseData;

    @TableField("calculation_formula")
    private String calculationFormula;

    @TableField("rate_used")
    private BigDecimal rateUsed;

    @TableField("amount_used")
    private BigDecimal amountUsed;

    @TableField("calculation_amount")
    private BigDecimal calculationAmount;

    @TableField("final_amount")
    private BigDecimal finalAmount;

    @TableField("data_source")
    private String dataSource;

    @TableField("calculation_detail")
    private String calculationDetail;

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
    public SalaryDetail() {
    }

    public SalaryDetail(Long calculationId, Long employeeId, Long salaryItemId, 
                       String itemCode, String itemName, String itemType) {
        this.calculationId = calculationId;
        this.employeeId = employeeId;
        this.salaryItemId = salaryItemId;
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemType = itemType;
        this.calculationAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
        this.deleteFlag = "0";
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getSalaryItemId() {
        return salaryItemId;
    }

    public void setSalaryItemId(Long salaryItemId) {
        this.salaryItemId = salaryItemId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getBaseData() {
        return baseData;
    }

    public void setBaseData(String baseData) {
        this.baseData = baseData;
    }

    public String getCalculationFormula() {
        return calculationFormula;
    }

    public void setCalculationFormula(String calculationFormula) {
        this.calculationFormula = calculationFormula;
    }

    public BigDecimal getRateUsed() {
        return rateUsed;
    }

    public void setRateUsed(BigDecimal rateUsed) {
        this.rateUsed = rateUsed;
    }

    public BigDecimal getAmountUsed() {
        return amountUsed;
    }

    public void setAmountUsed(BigDecimal amountUsed) {
        this.amountUsed = amountUsed;
    }

    public BigDecimal getCalculationAmount() {
        return calculationAmount;
    }

    public void setCalculationAmount(BigDecimal calculationAmount) {
        this.calculationAmount = calculationAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getCalculationDetail() {
        return calculationDetail;
    }

    public void setCalculationDetail(String calculationDetail) {
        this.calculationDetail = calculationDetail;
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
        return "SalaryDetail{" +
                "id=" + id +
                ", calculationId=" + calculationId +
                ", employeeId=" + employeeId +
                ", salaryItemId=" + salaryItemId +
                ", itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemType='" + itemType + '\'' +
                ", calculationAmount=" + calculationAmount +
                ", finalAmount=" + finalAmount +
                ", tenantId=" + tenantId +
                '}';
    }
}
