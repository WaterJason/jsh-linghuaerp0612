package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ScheduleEntryEx;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 排班记录扩展Mapper接口
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
public interface ScheduleEntryMapperEx {
    
    /**
     * 根据条件查询排班记录（包含关联信息）
     * 
     * @param employeeId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 状态
     * @param tenantId 租户ID
     * @return 排班记录列表
     */
    List<ScheduleEntryEx> selectByCondition(@Param("employeeId") Long employeeId,
                                           @Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate,
                                           @Param("status") String status,
                                           @Param("tenantId") Long tenantId);
    
    /**
     * 根据日期范围查询排班记录
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenantId 租户ID
     * @return 排班记录列表
     */
    List<ScheduleEntryEx> selectByDateRange(@Param("startDate") Date startDate,
                                           @Param("endDate") Date endDate,
                                           @Param("tenantId") Long tenantId);
    
    /**
     * 根据员工ID和日期查询排班记录
     * 
     * @param employeeId 员工ID
     * @param scheduleDate 排班日期
     * @param tenantId 租户ID
     * @return 排班记录
     */
    ScheduleEntryEx selectByEmployeeAndDate(@Param("employeeId") Long employeeId,
                                           @Param("scheduleDate") Date scheduleDate,
                                           @Param("tenantId") Long tenantId);
    
    /**
     * 统计员工在指定时间范围内的工作时长
     * 
     * @param employeeId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenantId 租户ID
     * @return 总工作时长（小时）
     */
    Double sumWorkHoursByEmployee(@Param("employeeId") Long employeeId,
                                 @Param("startDate") Date startDate,
                                 @Param("endDate") Date endDate,
                                 @Param("tenantId") Long tenantId);
    
    /**
     * 批量删除排班记录（逻辑删除）
     * 
     * @param ids ID数组
     * @return 影响行数
     */
    int batchDeleteByIds(@Param("ids") String[] ids);
    
    /**
     * 批量更新排班状态
     * 
     * @param ids ID数组
     * @param status 新状态
     * @param updateUser 更新用户
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("ids") String[] ids,
                         @Param("status") String status,
                         @Param("updateUser") Long updateUser);
    
    /**
     * 检查员工在指定日期是否已有排班
     * 
     * @param employeeId 员工ID
     * @param scheduleDate 排班日期
     * @param excludeId 排除的记录ID（用于更新时检查）
     * @param tenantId 租户ID
     * @return 记录数量
     */
    int checkEmployeeScheduleConflict(@Param("employeeId") Long employeeId,
                                     @Param("scheduleDate") Date scheduleDate,
                                     @Param("excludeId") Long excludeId,
                                     @Param("tenantId") Long tenantId);
    
    /**
     * 根据条件统计排班记录数量
     * 
     * @param employeeId 员工ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param status 状态
     * @param tenantId 租户ID
     * @return 记录数量
     */
    Long countByCondition(@Param("employeeId") Long employeeId,
                         @Param("startDate") Date startDate,
                         @Param("endDate") Date endDate,
                         @Param("status") String status,
                         @Param("tenantId") Long tenantId);
}
