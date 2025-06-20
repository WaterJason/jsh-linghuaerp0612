package com.linghua.plugin.schedule.datasource.mappers;

import com.linghua.plugin.schedule.datasource.entities.ScheduleShift;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Schedule Shift Extended Mapper Interface
 * 排班班次扩展Mapper接口
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public interface ScheduleShiftMapperEx {
    
    /**
     * Get shift list for management page
     * 获取班次管理页面列表
     * 
     * @param tenantId tenant ID
     * @param shiftName shift name (fuzzy search)
     * @param shiftType shift type
     * @param isActive is active
     * @return shift list with statistics
     */
    List<Map<String, Object>> getShiftListForManagement(@Param("tenantId") Long tenantId,
                                                        @Param("shiftName") String shiftName,
                                                        @Param("shiftType") String shiftType,
                                                        @Param("isActive") Boolean isActive);
    
    /**
     * Get shift options for dropdown
     * 获取班次下拉选项
     * 
     * @param tenantId tenant ID
     * @return shift options
     */
    List<Map<String, Object>> getShiftOptions(@Param("tenantId") Long tenantId);
    
    /**
     * Get shift statistics
     * 获取班次统计信息
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return shift statistics
     */
    List<Map<String, Object>> getShiftStatistics(@Param("tenantId") Long tenantId,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);
    
    /**
     * Get shift usage count
     * 获取班次使用次数
     * 
     * @param shiftId shift ID
     * @param tenantId tenant ID
     * @return usage count
     */
    long getShiftUsageCount(@Param("shiftId") Long shiftId,
                           @Param("tenantId") Long tenantId);
    
    /**
     * Get shifts with assignment count
     * 获取班次及其排班次数
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return shifts with assignment count
     */
    List<Map<String, Object>> getShiftsWithAssignmentCount(@Param("tenantId") Long tenantId,
                                                           @Param("startDate") String startDate,
                                                           @Param("endDate") String endDate);
    
    /**
     * Get popular shifts (most used)
     * 获取热门班次（使用最多的班次）
     * 
     * @param tenantId tenant ID
     * @param limit limit number
     * @return popular shifts
     */
    List<Map<String, Object>> getPopularShifts(@Param("tenantId") Long tenantId,
                                              @Param("limit") Integer limit);
    
    /**
     * Get shift details with extended info
     * 获取班次详细信息（包含扩展信息）
     * 
     * @param shiftId shift ID
     * @param tenantId tenant ID
     * @return shift details
     */
    Map<String, Object> getShiftDetails(@Param("shiftId") Long shiftId,
                                       @Param("tenantId") Long tenantId);
    
    /**
     * Check shift time conflict
     * 检查班次时间冲突
     * 
     * @param startTime start time (HH:mm:ss)
     * @param endTime end time (HH:mm:ss)
     * @param excludeId exclude shift ID (for update operations)
     * @param tenantId tenant ID
     * @return conflicting shifts
     */
    List<ScheduleShift> checkTimeConflict(@Param("startTime") String startTime,
                                         @Param("endTime") String endTime,
                                         @Param("excludeId") Long excludeId,
                                         @Param("tenantId") Long tenantId);
    
    /**
     * Get shifts by type
     * 根据类型获取班次
     * 
     * @param shiftType shift type
     * @param tenantId tenant ID
     * @return shifts of specified type
     */
    List<ScheduleShift> getShiftsByType(@Param("shiftType") String shiftType,
                                       @Param("tenantId") Long tenantId);
    
    /**
     * Get shift export data
     * 获取班次导出数据
     * 
     * @param tenantId tenant ID
     * @param shiftName shift name (fuzzy search)
     * @param shiftType shift type
     * @param isActive is active
     * @return export data
     */
    List<Map<String, Object>> getShiftExportData(@Param("tenantId") Long tenantId,
                                                 @Param("shiftName") String shiftName,
                                                 @Param("shiftType") String shiftType,
                                                 @Param("isActive") Boolean isActive);
    
    /**
     * Batch update shift status
     * 批量更新班次状态
     * 
     * @param ids shift IDs
     * @param isActive new active status
     * @param tenantId tenant ID
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids,
                         @Param("isActive") Boolean isActive,
                         @Param("tenantId") Long tenantId,
                         @Param("updateBy") Long updateBy);
    
    /**
     * Get max sort order
     * 获取最大排序值
     * 
     * @param tenantId tenant ID
     * @return max sort order
     */
    Integer getMaxSortOrder(@Param("tenantId") Long tenantId);
}
