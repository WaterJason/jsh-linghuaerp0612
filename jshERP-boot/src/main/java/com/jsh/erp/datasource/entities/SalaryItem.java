package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 薪酬项目配置实体类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@TableName("jsh_salary_item")
public class SalaryItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("item_code")
    private String itemCode;

    @TableField("item_name")
    private String itemName;

    @TableField("item_type")
    private String itemType;

    @TableField("calculation_formula")
    private String calculationFormula;

    @TableField("default_rate")
    private BigDecimal defaultRate;

    @TableField("base_amount")
    private BigDecimal baseAmount;

    @TableField("description")
    private String description;

    @TableField("status")
    private String status;

    @TableField("sort_order")
    private Integer sortOrder;

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
    public SalaryItem() {
    }

    public SalaryItem(String itemCode, String itemName, String itemType) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.itemType = itemType;
        this.status = "ACTIVE";
        this.defaultRate = BigDecimal.ZERO;
        this.baseAmount = BigDecimal.ZERO;
        this.sortOrder = 0;
        this.deleteFlag = "0";
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCalculationFormula() {
        return calculationFormula;
    }

    public void setCalculationFormula(String calculationFormula) {
        this.calculationFormula = calculationFormula;
    }

    public BigDecimal getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(BigDecimal defaultRate) {
        this.defaultRate = defaultRate;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(BigDecimal baseAmount) {
        this.baseAmount = baseAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
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
        return "SalaryItem{" +
                "id=" + id +
                ", itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", itemType='" + itemType + '\'' +
                ", defaultRate=" + defaultRate +
                ", baseAmount=" + baseAmount +
                ", status='" + status + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
