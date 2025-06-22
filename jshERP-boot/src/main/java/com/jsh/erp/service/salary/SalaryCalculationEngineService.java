package com.jsh.erp.service.salary;

import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.SalaryItemMapper;
import com.jsh.erp.datasource.mappers.SalaryProfileMapperEx;
import com.jsh.erp.datasource.mappers.EmployeeSalaryItemMapper;
import com.jsh.erp.service.salary.dto.EmployeeData;
import com.jsh.erp.service.salary.dto.SalaryCalculationResult;
import com.jsh.erp.service.salary.strategy.SalaryCalculationStrategy;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 薪酬计算引擎服务
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class SalaryCalculationEngineService {

    private Logger logger = LoggerFactory.getLogger(SalaryCalculationEngineService.class);

    @Resource
    private SalaryProfileMapperEx salaryProfileMapperEx;

    @Resource
    private SalaryItemMapper salaryItemMapper;

    @Resource
    private EmployeeSalaryItemMapper employeeSalaryItemMapper;

    @Resource
    private SalaryIntegrationService integrationService;

    // 策略映射
    private Map<String, SalaryCalculationStrategy> strategyMap = new HashMap<>();

    @Resource
    private List<SalaryCalculationStrategy> strategies;

    @PostConstruct
    public void initStrategies() {
        for (SalaryCalculationStrategy strategy : strategies) {
            strategyMap.put(strategy.getSupportedItemCode(), strategy);
        }
        logger.info("薪酬计算策略初始化完成，共加载{}个策略", strategyMap.size());
    }

    /**
     * 计算员工薪酬
     * 
     * @param employeeId 员工ID
     * @param calculationMonth 计算月份
     * @param tenantId 租户ID
     * @return 薪酬计算记录
     */
    public SalaryCalculation calculateEmployeeSalary(Long employeeId, String calculationMonth, Long tenantId) throws Exception {
        try {
            // 1. 获取员工薪酬档案
            SalaryProfile profile = salaryProfileMapperEx.selectByEmployeeId(employeeId, tenantId);
            if (profile == null || !"ACTIVE".equals(profile.getSalaryStatus())) {
                logger.warn("员工{}薪酬档案不存在或未激活", employeeId);
                return null;
            }

            // 2. 构建员工数据
            EmployeeData employeeData = buildEmployeeData(profile, calculationMonth, tenantId);

            // 3. 获取员工薪酬项目配置
            List<EmployeeSalaryItem> employeeSalaryItems = getEmployeeSalaryItems(employeeId, tenantId);
            if (employeeSalaryItems.isEmpty()) {
                logger.warn("员工{}未配置薪酬项目", employeeId);
                return null;
            }

            // 4. 执行薪酬计算
            List<SalaryCalculationResult> results = new ArrayList<>();
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal fixedAmount = BigDecimal.ZERO;
            BigDecimal commissionAmount = BigDecimal.ZERO;
            BigDecimal allowanceAmount = BigDecimal.ZERO;

            for (EmployeeSalaryItem item : employeeSalaryItems) {
                SalaryItem salaryItem = salaryItemMapper.selectById(item.getSalaryItemId());
                if (salaryItem == null || !"ACTIVE".equals(salaryItem.getStatus())) {
                    continue;
                }

                // 获取对应的计算策略
                SalaryCalculationStrategy strategy = strategyMap.get(salaryItem.getItemCode());
                if (strategy == null) {
                    logger.warn("薪酬项目{}未找到对应的计算策略", salaryItem.getItemCode());
                    continue;
                }

                // 执行计算
                SalaryCalculationResult result = strategy.calculate(employeeData, salaryItem, calculationMonth, tenantId);
                if (result.isSuccess()) {
                    results.add(result);
                    totalAmount = totalAmount.add(result.getFinalAmount());

                    // 按类型累计金额
                    switch (salaryItem.getItemType()) {
                        case "FIXED":
                            fixedAmount = fixedAmount.add(result.getFinalAmount());
                            break;
                        case "COMMISSION":
                            commissionAmount = commissionAmount.add(result.getFinalAmount());
                            break;
                        case "ALLOWANCE":
                            allowanceAmount = allowanceAmount.add(result.getFinalAmount());
                            break;
                    }
                } else {
                    logger.warn("员工{}薪酬项目{}计算失败：{}", employeeId, salaryItem.getItemCode(), result.getErrorMessage());
                }
            }

            // 5. 构建薪酬计算记录
            SalaryCalculation calculation = new SalaryCalculation();
            calculation.setCalculationNumber(generateCalculationNumber(calculationMonth, employeeId));
            calculation.setEmployeeId(employeeId);
            calculation.setEmployeeName(profile.getEmployeeName());
            calculation.setCalculationMonth(calculationMonth);
            calculation.setTotalAmount(totalAmount);
            calculation.setFixedAmount(fixedAmount);
            calculation.setCommissionAmount(commissionAmount);
            calculation.setAllowanceAmount(allowanceAmount);
            calculation.setDeductionAmount(BigDecimal.ZERO); // 暂不处理扣除项
            calculation.setActualAmount(totalAmount); // 实发金额 = 总额 - 扣除
            calculation.setStatus("PENDING_APPROVAL");
            calculation.setTenantId(tenantId);

            return calculation;

        } catch (Exception e) {
            logger.error("计算员工{}薪酬时发生异常", employeeId, e);
            throw e;
        }
    }

    /**
     * 构建员工数据
     */
    private EmployeeData buildEmployeeData(SalaryProfile profile, String calculationMonth, Long tenantId) throws Exception {
        EmployeeData employeeData = new EmployeeData();
        employeeData.setEmployeeId(profile.getEmployeeId());
        employeeData.setEmployeeName(profile.getEmployeeName());
        employeeData.setDepartment(profile.getDepartment());
        employeeData.setPosition(profile.getPosition());
        employeeData.setEntryDate(profile.getEntryDate());
        employeeData.setDailyWage(profile.getDailyWage());
        employeeData.setTenantId(tenantId);

        // 获取业务数据
        employeeData.setSalesData(integrationService.getSalesData(profile.getEmployeeId(), calculationMonth, tenantId));
        employeeData.setProductionData(integrationService.getProductionData(profile.getEmployeeId(), calculationMonth, tenantId));
        employeeData.setTrainingData(integrationService.getTrainingData(profile.getEmployeeId(), calculationMonth, tenantId));
        employeeData.setAttendanceData(integrationService.getAttendanceData(profile.getEmployeeId(), calculationMonth, tenantId));

        return employeeData;
    }

    /**
     * 获取员工薪酬项目配置
     */
    private List<EmployeeSalaryItem> getEmployeeSalaryItems(Long employeeId, Long tenantId) {
        // 这里应该查询员工的薪酬项目配置
        // 暂时返回空列表，实际实现时需要查询数据库
        return new ArrayList<>();
    }

    /**
     * 生成计算单号
     */
    private String generateCalculationNumber(String calculationMonth, Long employeeId) {
        return "SAL" + calculationMonth.replace("-", "") + String.format("%06d", employeeId);
    }
}
