package com.jsh.erp.service.salary.strategy.impl;

import com.jsh.erp.datasource.entities.SalaryItem;
import com.jsh.erp.service.salary.dto.EmployeeData;
import com.jsh.erp.service.salary.dto.SalaryCalculationResult;
import com.jsh.erp.service.salary.strategy.SalaryCalculationStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 固定薪酬计算策略
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Component
public class FixedSalaryStrategy implements SalaryCalculationStrategy {

    @Override
    public SalaryCalculationResult calculate(EmployeeData employeeData, 
                                           SalaryItem salaryItem, 
                                           String calculationMonth,
                                           Long tenantId) throws Exception {
        
        if (!validateParameters(employeeData, salaryItem, calculationMonth)) {
            return new SalaryCalculationResult("参数验证失败");
        }

        try {
            // 获取员工日薪
            BigDecimal dailyWage = employeeData.getDailyWage();
            if (dailyWage == null || dailyWage.compareTo(BigDecimal.ZERO) <= 0) {
                return new SalaryCalculationResult("员工日薪未设置或无效");
            }

            // 获取工作天数
            int workDays = getWorkDays(employeeData, calculationMonth);
            if (workDays <= 0) {
                return new SalaryCalculationResult("工作天数为0");
            }

            // 计算固定薪酬：日薪 × 工作天数
            BigDecimal amount = dailyWage.multiply(new BigDecimal(workDays));

            // 构建计算结果
            SalaryCalculationResult result = new SalaryCalculationResult();
            result.setSalaryItemId(salaryItem.getId());
            result.setItemCode(salaryItem.getItemCode());
            result.setItemName(salaryItem.getItemName());
            result.setItemType(salaryItem.getItemType());
            result.setCalculationAmount(amount);
            result.setFinalAmount(amount);
            result.setBaseData(workDays + "天");
            result.setCalculationFormula("日薪(" + dailyWage + ") × 工作天数(" + workDays + ")");
            result.setRateUsed(BigDecimal.ONE);
            result.setAmountUsed(dailyWage);
            result.setDataSource("考勤模块");
            result.setCalculationDetail("{\"dailyWage\":" + dailyWage + ",\"workDays\":" + workDays + "}");

            return result;

        } catch (Exception e) {
            return new SalaryCalculationResult("计算过程发生异常：" + e.getMessage());
        }
    }

    @Override
    public String getSupportedItemType() {
        return "FIXED";
    }

    @Override
    public String getSupportedItemCode() {
        return "DAILY_WAGE";
    }

    /**
     * 获取员工指定月份的工作天数
     * 
     * @param employeeData 员工数据
     * @param calculationMonth 计算月份
     * @return 工作天数
     */
    private int getWorkDays(EmployeeData employeeData, String calculationMonth) {
        try {
            // 从考勤数据中获取工作天数
            Map<String, Object> attendanceData = employeeData.getAttendanceData();
            if (attendanceData != null && attendanceData.containsKey("workDays")) {
                Object workDaysObj = attendanceData.get("workDays");
                if (workDaysObj instanceof Number) {
                    return ((Number) workDaysObj).intValue();
                }
            }

            // 如果没有考勤数据，使用默认计算方式
            // 这里可以根据实际业务需求调整
            String[] monthParts = calculationMonth.split("-");
            int year = Integer.parseInt(monthParts[0]);
            int month = Integer.parseInt(monthParts[1]);

            // 简单计算：假设每月22个工作日（可根据实际情况调整）
            return 22;

        } catch (Exception e) {
            // 异常情况下返回默认工作天数
            return 22;
        }
    }
}
