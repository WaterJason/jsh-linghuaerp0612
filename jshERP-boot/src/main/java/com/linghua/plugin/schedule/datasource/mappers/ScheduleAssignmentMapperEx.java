package com.linghua.plugin.schedule.datasource.mappers;

import com.linghua.plugin.schedule.datasource.entities.ScheduleCalendarVO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Schedule Assignment Extended Mapper Interface
 * 排班分配记录扩展Mapper接口
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public interface ScheduleAssignmentMapperEx {
    
    /**
     * Get calendar data for month view
     * 获取月视图日历数据
     * 
     * @param tenantId tenant ID
     * @param year year (YYYY)
     * @param month month (MM)
     * @return calendar data list
     */
    List<Map<String, Object>> getCalendarDataForMonth(@Param("tenantId") Long tenantId,
                                                      @Param("year") Integer year,
                                                      @Param("month") Integer month);
    
    /**
     * Get assignments by date
     * 根据日期获取排班记录
     * 
     * @param tenantId tenant ID
     * @param scheduleDate schedule date (YYYY-MM-DD)
     * @return assignment list with user and shift info
     */
    List<Map<String, Object>> getAssignmentsByDate(@Param("tenantId") Long tenantId,
                                                   @Param("scheduleDate") String scheduleDate);
    
    /**
     * Get user assignments for date range
     * 获取用户在指定日期范围内的排班
     * 
     * @param userId user ID
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return user assignment list
     */
    List<Map<String, Object>> getUserAssignments(@Param("userId") Long userId,
                                                 @Param("tenantId") Long tenantId,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);
    
    /**
     * Get assignment statistics
     * 获取排班统计信息
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return assignment statistics
     */
    Map<String, Object> getAssignmentStatistics(@Param("tenantId") Long tenantId,
                                               @Param("startDate") String startDate,
                                               @Param("endDate") String endDate);
    
    /**
     * Get user work hours statistics
     * 获取用户工时统计
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return user work hours statistics
     */
    List<Map<String, Object>> getUserWorkHoursStatistics(@Param("tenantId") Long tenantId,
                                                         @Param("startDate") String startDate,
                                                         @Param("endDate") String endDate);
    
    /**
     * Get shift usage statistics
     * 获取班次使用统计
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return shift usage statistics
     */
    List<Map<String, Object>> getShiftUsageStatistics(@Param("tenantId") Long tenantId,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate);
    
    /**
     * Get assignment conflicts
     * 获取排班冲突
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return conflict list
     */
    List<Map<String, Object>> getAssignmentConflicts(@Param("tenantId") Long tenantId,
                                                     @Param("startDate") String startDate,
                                                     @Param("endDate") String endDate);
    
    /**
     * Get assignments for export
     * 获取导出用的排班数据
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @param userId user ID (optional)
     * @param shiftId shift ID (optional)
     * @param status assignment status (optional)
     * @return export data
     */
    List<Map<String, Object>> getAssignmentsForExport(@Param("tenantId") Long tenantId,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate,
                                                      @Param("userId") Long userId,
                                                      @Param("shiftId") Long shiftId,
                                                      @Param("status") String status);
    
    /**
     * Get daily assignment summary
     * 获取每日排班汇总
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return daily summary
     */
    List<Map<String, Object>> getDailyAssignmentSummary(@Param("tenantId") Long tenantId,
                                                        @Param("startDate") String startDate,
                                                        @Param("endDate") String endDate);
    
    /**
     * Get user attendance summary
     * 获取用户考勤汇总
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return attendance summary
     */
    List<Map<String, Object>> getUserAttendanceSummary(@Param("tenantId") Long tenantId,
                                                       @Param("startDate") String startDate,
                                                       @Param("endDate") String endDate);
    
    /**
     * Get assignments with details
     * 获取排班详细信息（包含用户和班次信息）
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @param userName user name (fuzzy search)
     * @param shiftName shift name (fuzzy search)
     * @param status assignment status
     * @return assignment details
     */
    List<Map<String, Object>> getAssignmentsWithDetails(@Param("tenantId") Long tenantId,
                                                        @Param("startDate") String startDate,
                                                        @Param("endDate") String endDate,
                                                        @Param("userName") String userName,
                                                        @Param("shiftName") String shiftName,
                                                        @Param("status") String status);
    
    /**
     * Check user availability
     * 检查用户可用性（是否已有其他排班）
     * 
     * @param userId user ID
     * @param scheduleDate schedule date (YYYY-MM-DD)
     * @param excludeId exclude assignment ID (for update operations)
     * @param tenantId tenant ID
     * @return existing assignments
     */
    List<Map<String, Object>> checkUserAvailability(@Param("userId") Long userId,
                                                    @Param("scheduleDate") String scheduleDate,
                                                    @Param("excludeId") Long excludeId,
                                                    @Param("tenantId") Long tenantId);
    
    /**
     * Get popular time slots
     * 获取热门时间段
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return popular time slots
     */
    List<Map<String, Object>> getPopularTimeSlots(@Param("tenantId") Long tenantId,
                                                  @Param("startDate") String startDate,
                                                  @Param("endDate") String endDate);
    
    /**
     * Get workload distribution
     * 获取工作负荷分布
     * 
     * @param tenantId tenant ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @return workload distribution
     */
    List<Map<String, Object>> getWorkloadDistribution(@Param("tenantId") Long tenantId,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate);
}
