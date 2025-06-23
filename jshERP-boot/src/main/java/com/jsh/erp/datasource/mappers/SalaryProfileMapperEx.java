package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.SalaryProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 员工薪酬档案扩展Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Mapper
public interface SalaryProfileMapperEx {

    /**
     * 根据条件查询薪酬档案列表
     * 
     * @param employeeName 员工姓名
     * @param department 部门
     * @param position 职位
     * @param salaryStatus 薪酬状态
     * @param tenantId 租户ID
     * @return 薪酬档案列表
     */
    List<SalaryProfile> selectByCondition(@Param("employeeName") String employeeName,
                                         @Param("department") String department,
                                         @Param("position") String position,
                                         @Param("salaryStatus") String salaryStatus,
                                         @Param("tenantId") Long tenantId);

    /**
     * 根据员工ID查询薪酬档案
     * 
     * @param employeeId 员工ID
     * @param tenantId 租户ID
     * @return 薪酬档案
     */
    SalaryProfile selectByEmployeeId(@Param("employeeId") Long employeeId,
                                    @Param("tenantId") Long tenantId);

    /**
     * 批量查询员工薪酬档案
     * 
     * @param employeeIds 员工ID列表
     * @param tenantId 租户ID
     * @return 薪酬档案列表
     */
    List<SalaryProfile> selectByEmployeeIds(@Param("employeeIds") List<Long> employeeIds,
                                           @Param("tenantId") Long tenantId);

    /**
     * 统计部门薪酬档案数量
     * 
     * @param tenantId 租户ID
     * @return 部门统计结果
     */
    List<Object> countByDepartment(@Param("tenantId") Long tenantId);

    /**
     * 查询即将到期的薪酬档案（用于提醒）
     * 
     * @param days 提前天数
     * @param tenantId 租户ID
     * @return 即将到期的档案列表
     */
    List<SalaryProfile> selectExpiringProfiles(@Param("days") Integer days,
                                              @Param("tenantId") Long tenantId);
}
