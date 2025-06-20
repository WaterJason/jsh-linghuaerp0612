package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

/**
 * 盘点明细表实体类
 * 
 * @author jshERP Team
 * @version 1.0.0
 */
public class InventoryCheckItem {
    private Long id;

    private Long headerId;

    private String type;

    private Long materialId;

    private Long materialExtendId;

    private String materialUnit;

    private String sku;

    private BigDecimal prevNumber;

    private BigDecimal checkNumber;

    private BigDecimal diffNumber;

    private BigDecimal unitPrice;

    private BigDecimal taxUnitPrice;

    private BigDecimal allPrice;

    private String remark;

    private Long tenantId;

    private String deleteFlag;

    // 构造函数
    public InventoryCheckItem() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(Long headerId) {
        this.headerId = headerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    public Long getMaterialExtendId() {
        return materialExtendId;
    }

    public void setMaterialExtendId(Long materialExtendId) {
        this.materialExtendId = materialExtendId;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit == null ? null : materialUnit.trim();
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku == null ? null : sku.trim();
    }

    public BigDecimal getPrevNumber() {
        return prevNumber;
    }

    public void setPrevNumber(BigDecimal prevNumber) {
        this.prevNumber = prevNumber;
    }

    public BigDecimal getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(BigDecimal checkNumber) {
        this.checkNumber = checkNumber;
    }

    public BigDecimal getDiffNumber() {
        return diffNumber;
    }

    public void setDiffNumber(BigDecimal diffNumber) {
        this.diffNumber = diffNumber;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTaxUnitPrice() {
        return taxUnitPrice;
    }

    public void setTaxUnitPrice(BigDecimal taxUnitPrice) {
        this.taxUnitPrice = taxUnitPrice;
    }

    public BigDecimal getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(BigDecimal allPrice) {
        this.allPrice = allPrice;
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
}