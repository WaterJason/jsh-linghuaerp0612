package com.linghua.plugin.schedule.datasource.mappers;

import com.linghua.plugin.schedule.datasource.entities.ScheduleAssignment;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Schedule Assignment Basic Mapper Interface
 * 排班分配记录基础Mapper接口
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public interface ScheduleAssignmentMapper {
    
    /**
     * Select by primary key
     * 根据主键查询
     * 
     * @param id primary key
     * @return entity or null if not found
     */
    ScheduleAssignment selectByPrimaryKey(Long id);
    
    /**
     * Insert entity selectively
     * 选择性插入实体（只插入非空字段）
     * 
     * @param record entity to insert
     * @return number of affected rows
     */
    int insertSelective(ScheduleAssignment record);
    
    /**
     * Update by primary key selectively
     * 根据主键选择性更新（只更新非空字段）
     * 
     * @param record entity to update
     * @return number of affected rows
     */
    int updateByPrimaryKeySelective(ScheduleAssignment record);
    
    /**
     * Delete by primary key (physical delete)
     * 根据主键删除（物理删除，谨慎使用）
     * 
     * @param id primary key
     * @return number of affected rows
     */
    int deleteByPrimaryKey(Long id);
    
    /**
     * Count by conditions
     * 根据条件统计数量
     * 
     * @param tenantId tenant ID
     * @param startDate start date
     * @param endDate end date
     * @param userId user ID
     * @param shiftId shift ID
     * @param status assignment status
     * @return count
     */
    long countByConditions(@Param("tenantId") Long tenantId,
                          @Param("startDate") Date startDate,
                          @Param("endDate") Date endDate,
                          @Param("userId") Long userId,
                          @Param("shiftId") Long shiftId,
                          @Param("status") String status);
    
    /**
     * Select by conditions with pagination
     * 根据条件分页查询
     * 
     * @param tenantId tenant ID
     * @param startDate start date
     * @param endDate end date
     * @param userId user ID
     * @param shiftId shift ID
     * @param status assignment status
     * @return assignment list
     */
    List<ScheduleAssignment> selectByConditions(@Param("tenantId") Long tenantId,
                                               @Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate,
                                               @Param("userId") Long userId,
                                               @Param("shiftId") Long shiftId,
                                               @Param("status") String status);
    
    /**
     * Select by date range
     * 根据日期范围查询
     * 
     * @param tenantId tenant ID
     * @param startDate start date
     * @param endDate end date
     * @return assignment list
     */
    List<ScheduleAssignment> selectByDateRange(@Param("tenantId") Long tenantId,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate);
    
    /**
     * Select by user and date range
     * 根据用户和日期范围查询
     * 
     * @param userId user ID
     * @param tenantId tenant ID
     * @param startDate start date
     * @param endDate end date
     * @return assignment list
     */
    List<ScheduleAssignment> selectByUserAndDateRange(@Param("userId") Long userId,
                                                     @Param("tenantId") Long tenantId,
                                                     @Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate);
    
    /**
     * Check assignment conflict
     * 检查排班冲突
     * 
     * @param scheduleDate schedule date
     * @param shiftId shift ID
     * @param userId user ID
     * @param excludeId exclude assignment ID (for update operations)
     * @param tenantId tenant ID
     * @return true if conflict exists, false otherwise
     */
    boolean checkConflict(@Param("scheduleDate") Date scheduleDate,
                         @Param("shiftId") Long shiftId,
                         @Param("userId") Long userId,
                         @Param("excludeId") Long excludeId,
                         @Param("tenantId") Long tenantId);
    
    /**
     * Batch insert assignments
     * 批量插入排班记录
     * 
     * @param assignments assignment list
     * @return number of affected rows
     */
    int batchInsert(@Param("assignments") List<ScheduleAssignment> assignments);
    
    /**
     * Batch soft delete by IDs
     * 批量软删除
     * 
     * @param ids assignment IDs
     * @param tenantId tenant ID
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int batchSoftDelete(@Param("ids") List<Long> ids,
                       @Param("tenantId") Long tenantId,
                       @Param("updateBy") Long updateBy);
    
    /**
     * Update assignment status
     * 更新排班状态
     * 
     * @param id assignment ID
     * @param status new status
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int updateStatus(@Param("id") Long id,
                    @Param("status") String status,
                    @Param("updateBy") Long updateBy);
    
    /**
     * Batch update assignment status
     * 批量更新排班状态
     * 
     * @param ids assignment IDs
     * @param status new status
     * @param tenantId tenant ID
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids,
                         @Param("status") String status,
                         @Param("tenantId") Long tenantId,
                         @Param("updateBy") Long updateBy);
    
    /**
     * Update check-in time
     * 更新签到时间
     * 
     * @param id assignment ID
     * @param checkInTime check-in time
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int updateCheckInTime(@Param("id") Long id,
                         @Param("checkInTime") Date checkInTime,
                         @Param("updateBy") Long updateBy);
    
    /**
     * Update check-out time
     * 更新签退时间
     * 
     * @param id assignment ID
     * @param checkOutTime check-out time
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int updateCheckOutTime(@Param("id") Long id,
                          @Param("checkOutTime") Date checkOutTime,
                          @Param("updateBy") Long updateBy);
}
