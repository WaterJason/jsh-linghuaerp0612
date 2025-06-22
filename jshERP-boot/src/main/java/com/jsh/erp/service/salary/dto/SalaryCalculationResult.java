package com.jsh.erp.service.salary.dto;

import java.math.BigDecimal;

/**
 * 薪酬计算结果DTO
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public class SalaryCalculationResult {

    private Long salaryItemId;
    private String itemCode;
    private String itemName;
    private String itemType;
    private BigDecimal calculationAmount;
    private BigDecimal finalAmount;
    private String baseData;
    private String calculationFormula;
    private BigDecimal rateUsed;
    private BigDecimal amountUsed;
    private String dataSource;
    private String calculationDetail;
    private String remark;
    private boolean success;
    private String errorMessage;

    public SalaryCalculationResult() {
        this.success = true;
        this.calculationAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
    }

    public SalaryCalculationResult(Long salaryItemId, BigDecimal amount, String baseData) {
        this();
        this.salaryItemId = salaryItemId;
        this.calculationAmount = amount;
        this.finalAmount = amount;
        this.baseData = baseData;
    }

    public SalaryCalculationResult(String errorMessage) {
        this();
        this.success = false;
        this.errorMessage = errorMessage;
    }

    // Getter and Setter methods
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "SalaryCalculationResult{" +
                "salaryItemId=" + salaryItemId +
                ", itemCode='" + itemCode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", calculationAmount=" + calculationAmount +
                ", finalAmount=" + finalAmount +
                ", success=" + success +
                '}';
    }
}
