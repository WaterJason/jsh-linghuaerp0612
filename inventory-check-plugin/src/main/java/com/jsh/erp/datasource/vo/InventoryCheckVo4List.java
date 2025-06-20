package com.jsh.erp.datasource.vo;

import com.jsh.erp.datasource.entities.InventoryCheck;

import java.math.BigDecimal;

public class InventoryCheckVo4List extends InventoryCheck {

    private String organName;

    private String creatorName;

    private String depotName;

    private String materialsList;

    private String operTimeStr;

    private BigDecimal totalDiffAmount;

    private Integer itemCount;

    // Getter和Setter方法
    public String getOrganName() {
        return organName;
    }

    public void setOrganName(String organName) {
        this.organName = organName == null ? null : organName.trim();
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName == null ? null : creatorName.trim();
    }

    public String getDepotName() {
        return depotName;
    }

    public void setDepotName(String depotName) {
        this.depotName = depotName == null ? null : depotName.trim();
    }

    public String getMaterialsList() {
        return materialsList;
    }

    public void setMaterialsList(String materialsList) {
        this.materialsList = materialsList == null ? null : materialsList.trim();
    }

    public String getOperTimeStr() {
        return operTimeStr;
    }

    public void setOperTimeStr(String operTimeStr) {
        this.operTimeStr = operTimeStr == null ? null : operTimeStr.trim();
    }

    public BigDecimal getTotalDiffAmount() {
        return totalDiffAmount;
    }

    public void setTotalDiffAmount(BigDecimal totalDiffAmount) {
        this.totalDiffAmount = totalDiffAmount;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
}