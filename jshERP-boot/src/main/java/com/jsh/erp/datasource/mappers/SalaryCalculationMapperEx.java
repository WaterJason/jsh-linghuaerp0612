package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.SalaryCalculation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 薪酬计算记录扩展Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Mapper
public interface SalaryCalculationMapperEx {

    /**
     * 根据条件查询薪酬计算记录
     * 
     * @param employeeName 员工姓名
     * @param calculationMonth 计算月份
     * @param status 状态
     * @param tenantId 租户ID
     * @return 计算记录列表
     */
    List<SalaryCalculation> selectByCondition(@Param("employeeName") String employeeName,
                                             @Param("calculationMonth") String calculationMonth,
                                             @Param("status") String status,
                                             @Param("tenantId") Long tenantId);

    /**
     * 查询员工指定月份的薪酬计算记录
     * 
     * @param employeeId 员工ID
     * @param calculationMonth 计算月份
     * @param tenantId 租户ID
     * @return 计算记录
     */
    SalaryCalculation selectByEmployeeAndMonth(@Param("employeeId") Long employeeId,
                                              @Param("calculationMonth") String calculationMonth,
                                              @Param("tenantId") Long tenantId);

    /**
     * 统计指定月份的薪酬总额
     * 
     * @param calculationMonth 计算月份
     * @param tenantId 租户ID
     * @return 薪酬统计结果
     */
    Map<String, Object> sumByMonth(@Param("calculationMonth") String calculationMonth,
                                   @Param("tenantId") Long tenantId);

    /**
     * 查询待审批的薪酬计算记录
     * 
     * @param tenantId 租户ID
     * @return 待审批记录列表
     */
    List<SalaryCalculation> selectPendingApproval(@Param("tenantId") Long tenantId);

    /**
     * 统计员工年度薪酬
     * 
     * @param employeeId 员工ID
     * @param year 年份
     * @param tenantId 租户ID
     * @return 年度薪酬统计
     */
    List<Map<String, Object>> sumByEmployeeAndYear(@Param("employeeId") Long employeeId,
                                                   @Param("year") String year,
                                                   @Param("tenantId") Long tenantId);

    /**
     * 查询部门月度薪酬统计
     * 
     * @param calculationMonth 计算月份
     * @param tenantId 租户ID
     * @return 部门薪酬统计
     */
    List<Map<String, Object>> sumByDepartmentAndMonth(@Param("calculationMonth") String calculationMonth,
                                                      @Param("tenantId") Long tenantId);

    /**
     * 批量更新计算状态
     * 
     * @param ids 计算记录ID列表
     * @param status 新状态
     * @param approverId 审批人ID
     * @param tenantId 租户ID
     * @return 更新记录数
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids,
                         @Param("status") String status,
                         @Param("approverId") Long approverId,
                         @Param("tenantId") Long tenantId);
}
