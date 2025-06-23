package com.jsh.erp.service.salary.strategy.impl;

import com.jsh.erp.datasource.entities.SalaryItem;
import com.jsh.erp.service.salary.dto.EmployeeData;
import com.jsh.erp.service.salary.dto.SalaryCalculationResult;
import com.jsh.erp.service.salary.strategy.SalaryCalculationStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 销售提成计算策略
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Component
public class SalesCommissionStrategy implements SalaryCalculationStrategy {

    @Override
    public SalaryCalculationResult calculate(EmployeeData employeeData, 
                                           SalaryItem salaryItem, 
                                           String calculationMonth,
                                           Long tenantId) throws Exception {
        
        if (!validateParameters(employeeData, salaryItem, calculationMonth)) {
            return new SalaryCalculationResult("参数验证失败");
        }

        try {
            // 获取销售数据
            Map<String, Object> salesData = employeeData.getSalesData();
            if (salesData == null || salesData.isEmpty()) {
                return new SalaryCalculationResult("无销售数据");
            }

            // 获取销售额
            BigDecimal salesAmount = getSalesAmount(salesData, salaryItem.getItemCode());
            if (salesAmount == null || salesAmount.compareTo(BigDecimal.ZERO) <= 0) {
                // 没有销售额时返回0，不算错误
                SalaryCalculationResult result = new SalaryCalculationResult();
                result.setSalaryItemId(salaryItem.getId());
                result.setItemCode(salaryItem.getItemCode());
                result.setItemName(salaryItem.getItemName());
                result.setItemType(salaryItem.getItemType());
                result.setCalculationAmount(BigDecimal.ZERO);
                result.setFinalAmount(BigDecimal.ZERO);
                result.setBaseData("销售额：0元");
                result.setDataSource("销售模块");
                return result;
            }

            // 获取提成比例
            BigDecimal commissionRate = getCommissionRate(employeeData, salaryItem);
            if (commissionRate == null || commissionRate.compareTo(BigDecimal.ZERO) <= 0) {
                return new SalaryCalculationResult("提成比例未设置或无效");
            }

            // 计算提成：销售额 × 提成比例
            BigDecimal commissionAmount = salesAmount.multiply(commissionRate)
                                                   .setScale(2, RoundingMode.HALF_UP);

            // 构建计算结果
            SalaryCalculationResult result = new SalaryCalculationResult();
            result.setSalaryItemId(salaryItem.getId());
            result.setItemCode(salaryItem.getItemCode());
            result.setItemName(salaryItem.getItemName());
            result.setItemType(salaryItem.getItemType());
            result.setCalculationAmount(commissionAmount);
            result.setFinalAmount(commissionAmount);
            result.setBaseData("销售额：" + salesAmount + "元");
            result.setCalculationFormula("销售额(" + salesAmount + ") × 提成比例(" + 
                                       commissionRate.multiply(new BigDecimal(100)) + "%)");
            result.setRateUsed(commissionRate);
            result.setAmountUsed(salesAmount);
            result.setDataSource("销售模块");
            result.setCalculationDetail("{\"salesAmount\":" + salesAmount + 
                                      ",\"commissionRate\":" + commissionRate + "}");

            return result;

        } catch (Exception e) {
            return new SalaryCalculationResult("计算过程发生异常：" + e.getMessage());
        }
    }

    @Override
    public String getSupportedItemType() {
        return "COMMISSION";
    }

    @Override
    public String getSupportedItemCode() {
        return "SALES_COMMISSION";
    }

    /**
     * 获取销售额
     * 
     * @param salesData 销售数据
     * @param itemCode 薪酬项目编码
     * @return 销售额
     */
    private BigDecimal getSalesAmount(Map<String, Object> salesData, String itemCode) {
        try {
            // 根据不同的薪酬项目编码获取对应的销售额
            String dataKey = "SALES_COMMISSION".equals(itemCode) ? "totalSalesAmount" : "coffeeSalesAmount";
            
            Object amountObj = salesData.get(dataKey);
            if (amountObj instanceof Number) {
                return new BigDecimal(amountObj.toString());
            } else if (amountObj instanceof String) {
                return new BigDecimal((String) amountObj);
            }
            
            return BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取提成比例
     * 
     * @param employeeData 员工数据
     * @param salaryItem 薪酬项目
     * @return 提成比例
     */
    private BigDecimal getCommissionRate(EmployeeData employeeData, SalaryItem salaryItem) {
        try {
            // 优先使用员工个人定制比例
            Map<String, Object> customData = employeeData.getCustomData();
            if (customData != null && customData.containsKey("customRate_" + salaryItem.getItemCode())) {
                Object rateObj = customData.get("customRate_" + salaryItem.getItemCode());
                if (rateObj instanceof Number) {
                    return new BigDecimal(rateObj.toString());
                }
            }

            // 使用默认比例
            return salaryItem.getDefaultRate();
        } catch (Exception e) {
            return salaryItem.getDefaultRate();
        }
    }
}
