package com.jsh.erp.service.salary.strategy;

import com.jsh.erp.service.salary.dto.EmployeeData;
import com.jsh.erp.service.salary.dto.SalaryCalculationResult;
import com.jsh.erp.datasource.entities.SalaryItem;

import java.util.Date;

/**
 * 薪酬计算策略接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface SalaryCalculationStrategy {

    /**
     * 计算薪酬
     * 
     * @param employeeData 员工数据
     * @param salaryItem 薪酬项目
     * @param calculationMonth 计算月份
     * @param tenantId 租户ID
     * @return 计算结果
     */
    SalaryCalculationResult calculate(EmployeeData employeeData, 
                                    SalaryItem salaryItem, 
                                    String calculationMonth,
                                    Long tenantId) throws Exception;

    /**
     * 获取策略支持的薪酬项目类型
     * 
     * @return 薪酬项目类型
     */
    String getSupportedItemType();

    /**
     * 获取策略支持的薪酬项目编码
     * 
     * @return 薪酬项目编码
     */
    String getSupportedItemCode();

    /**
     * 验证计算参数
     * 
     * @param employeeData 员工数据
     * @param salaryItem 薪酬项目
     * @param calculationMonth 计算月份
     * @return 是否有效
     */
    default boolean validateParameters(EmployeeData employeeData, 
                                     SalaryItem salaryItem, 
                                     String calculationMonth) {
        return employeeData != null && 
               salaryItem != null && 
               calculationMonth != null && 
               calculationMonth.matches("\\d{4}-\\d{2}");
    }
}
