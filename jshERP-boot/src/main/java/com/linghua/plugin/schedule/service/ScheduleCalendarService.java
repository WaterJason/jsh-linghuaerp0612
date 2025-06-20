package com.linghua.plugin.schedule.service;

import com.linghua.plugin.schedule.datasource.entities.ScheduleCalendarVO;
import com.linghua.plugin.schedule.datasource.mappers.ScheduleAssignmentMapperEx;
import com.linghua.plugin.schedule.exception.PluginValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Schedule Calendar Service
 * 日历排班服务类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@Service
public class ScheduleCalendarService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleCalendarService.class);
    
    @Resource
    private ScheduleAssignmentMapperEx scheduleAssignmentMapperEx;
    
    // ==================== 日历数据组装 ====================
    
    /**
     * Get calendar data for month view
     * 获取月视图日历数据
     * 
     * @param request HTTP request
     * @return calendar data
     * @throws Exception if operation fails
     */
    public Map<String, Object> getCalendarDataForMonth(HttpServletRequest request) throws Exception {
        try {
            // Get parameters
            Long tenantId = getCurrentTenantId(request);
            String yearStr = request.getParameter("year");
            String monthStr = request.getParameter("month");
            
            // Parameter validation
            if (yearStr == null || monthStr == null) {
                throw new PluginValidationException("年份和月份参数不能为空");
            }
            
            Integer year = Integer.parseInt(yearStr);
            Integer month = Integer.parseInt(monthStr);
            
            if (year < 2020 || year > 2030) {
                throw new PluginValidationException("年份必须在2020-2030之间");
            }
            
            if (month < 1 || month > 12) {
                throw new PluginValidationException("月份必须在1-12之间");
            }
            
            // Get calendar data from database
            List<Map<String, Object>> calendarData = scheduleAssignmentMapperEx.getCalendarDataForMonth(tenantId, year, month);
            
            // Generate complete calendar structure
            Map<String, Object> result = generateCalendarStructure(year, month, calendarData);
            
            // Add statistics
            Map<String, Object> statistics = calculateMonthStatistics(calendarData);
            result.put("statistics", statistics);
            
            logger.info("获取月视图日历数据成功 - 年月: {}-{}, 数据量: {}", year, month, calendarData.size());
            
            return result;
            
        } catch (NumberFormatException e) {
            throw new PluginValidationException("年份或月份格式错误");
        } catch (Exception e) {
            logger.error("获取月视图日历数据失败", e);
            throw e;
        }
    }
    
    /**
     * Get user assignments for date range
     * 获取用户在指定日期范围内的排班
     * 
     * @param request HTTP request
     * @return user assignment data
     * @throws Exception if operation fails
     */
    public Map<String, Object> getUserAssignments(HttpServletRequest request) throws Exception {
        try {
            // Get parameters
            Long tenantId = getCurrentTenantId(request);
            String userIdStr = request.getParameter("userId");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            
            // Parameter validation
            if (userIdStr == null || startDate == null || endDate == null) {
                throw new PluginValidationException("用户ID、开始日期和结束日期不能为空");
            }
            
            Long userId = Long.parseLong(userIdStr);
            
            // Get user assignments
            List<Map<String, Object>> assignments = scheduleAssignmentMapperEx.getUserAssignments(
                userId, tenantId, startDate, endDate);
            
            // Calculate user statistics
            Map<String, Object> userStats = calculateUserStatistics(assignments);
            
            Map<String, Object> result = new HashMap<>();
            result.put("assignments", assignments);
            result.put("statistics", userStats);
            
            logger.info("获取用户排班数据成功 - 用户ID: {}, 日期范围: {} 到 {}, 数据量: {}", 
                       userId, startDate, endDate, assignments.size());
            
            return result;
            
        } catch (NumberFormatException e) {
            throw new PluginValidationException("用户ID格式错误");
        } catch (Exception e) {
            logger.error("获取用户排班数据失败", e);
            throw e;
        }
    }
    
    /**
     * Get assignment statistics
     * 获取排班统计信息
     * 
     * @param request HTTP request
     * @return statistics data
     * @throws Exception if operation fails
     */
    public Map<String, Object> getAssignmentStatistics(HttpServletRequest request) throws Exception {
        try {
            // Get parameters
            Long tenantId = getCurrentTenantId(request);
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            
            // Parameter validation
            if (startDate == null || endDate == null) {
                throw new PluginValidationException("开始日期和结束日期不能为空");
            }
            
            // Get overall statistics
            Map<String, Object> overallStats = scheduleAssignmentMapperEx.getAssignmentStatistics(tenantId, startDate, endDate);
            
            // Get user work hours statistics
            List<Map<String, Object>> userStats = scheduleAssignmentMapperEx.getUserWorkHoursStatistics(tenantId, startDate, endDate);
            
            // Get shift usage statistics
            List<Map<String, Object>> shiftStats = scheduleAssignmentMapperEx.getShiftUsageStatistics(tenantId, startDate, endDate);
            
            // Get assignment conflicts
            List<Map<String, Object>> conflicts = scheduleAssignmentMapperEx.getAssignmentConflicts(tenantId, startDate, endDate);
            
            Map<String, Object> result = new HashMap<>();
            result.put("overall", overallStats);
            result.put("userStatistics", userStats);
            result.put("shiftStatistics", shiftStats);
            result.put("conflicts", conflicts);
            
            logger.info("获取排班统计信息成功 - 日期范围: {} 到 {}", startDate, endDate);
            
            return result;
            
        } catch (Exception e) {
            logger.error("获取排班统计信息失败", e);
            throw e;
        }
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * Generate complete calendar structure
     * 生成完整的日历结构
     */
    private Map<String, Object> generateCalendarStructure(Integer year, Integer month, List<Map<String, Object>> calendarData) {
        Map<String, Object> result = new HashMap<>();
        
        // Create calendar info
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1); // Month is 0-based in Calendar
        
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        
        result.put("year", year);
        result.put("month", month);
        result.put("daysInMonth", daysInMonth);
        result.put("firstDayOfWeek", firstDayOfWeek);
        
        // Convert database data to map for quick lookup
        Map<String, Map<String, Object>> dataMap = new HashMap<>();
        for (Map<String, Object> data : calendarData) {
            String dateKey = data.get("scheduleDate").toString();
            dataMap.put(dateKey, data);
        }
        
        // Generate calendar days
        List<Map<String, Object>> calendarDays = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (int day = 1; day <= daysInMonth; day++) {
            calendar.set(year, month - 1, day);
            String dateKey = dateFormat.format(calendar.getTime());
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dateKey);
            dayData.put("day", day);
            dayData.put("isToday", isToday(calendar.getTime()));
            dayData.put("isWeekend", isWeekend(calendar.get(Calendar.DAY_OF_WEEK)));
            
            // Add assignment data if exists
            if (dataMap.containsKey(dateKey)) {
                Map<String, Object> assignmentData = dataMap.get(dateKey);
                dayData.put("assignmentCount", assignmentData.get("assignmentCount"));
                dayData.put("assignmentInfo", assignmentData.get("assignmentInfo"));
                dayData.put("colors", assignmentData.get("colors"));
                dayData.put("hasAssignments", true);
            } else {
                dayData.put("assignmentCount", 0);
                dayData.put("assignmentInfo", "");
                dayData.put("colors", "");
                dayData.put("hasAssignments", false);
            }
            
            calendarDays.add(dayData);
        }
        
        result.put("calendarDays", calendarDays);
        
        return result;
    }
    
    /**
     * Calculate month statistics
     * 计算月度统计信息
     */
    private Map<String, Object> calculateMonthStatistics(List<Map<String, Object>> calendarData) {
        Map<String, Object> stats = new HashMap<>();
        
        int totalAssignments = 0;
        int daysWithAssignments = calendarData.size();
        
        for (Map<String, Object> data : calendarData) {
            Object countObj = data.get("assignmentCount");
            if (countObj != null) {
                totalAssignments += Integer.parseInt(countObj.toString());
            }
        }
        
        stats.put("totalAssignments", totalAssignments);
        stats.put("daysWithAssignments", daysWithAssignments);
        stats.put("averageAssignmentsPerDay", daysWithAssignments > 0 ? 
                 Math.round((double) totalAssignments / daysWithAssignments * 100.0) / 100.0 : 0);
        
        return stats;
    }
    
    /**
     * Calculate user statistics
     * 计算用户统计信息
     */
    private Map<String, Object> calculateUserStatistics(List<Map<String, Object>> assignments) {
        Map<String, Object> stats = new HashMap<>();
        
        int totalAssignments = assignments.size();
        int completedCount = 0;
        int scheduledCount = 0;
        int cancelledCount = 0;
        
        Set<String> uniqueDates = new HashSet<>();
        Set<String> uniqueShifts = new HashSet<>();
        
        for (Map<String, Object> assignment : assignments) {
            String status = (String) assignment.get("status");
            if ("COMPLETED".equals(status)) {
                completedCount++;
            } else if ("SCHEDULED".equals(status)) {
                scheduledCount++;
            } else if ("CANCELLED".equals(status)) {
                cancelledCount++;
            }
            
            uniqueDates.add(assignment.get("scheduleDate").toString());
            uniqueShifts.add(assignment.get("shiftName").toString());
        }
        
        stats.put("totalAssignments", totalAssignments);
        stats.put("completedCount", completedCount);
        stats.put("scheduledCount", scheduledCount);
        stats.put("cancelledCount", cancelledCount);
        stats.put("completionRate", totalAssignments > 0 ? 
                 Math.round((double) completedCount / totalAssignments * 100.0) / 100.0 : 0);
        stats.put("uniqueDates", uniqueDates.size());
        stats.put("uniqueShifts", uniqueShifts.size());
        
        return stats;
    }
    
    /**
     * Check if date is today
     * 检查是否是今天
     */
    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar checkDate = Calendar.getInstance();
        checkDate.setTime(date);
        
        return today.get(Calendar.YEAR) == checkDate.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == checkDate.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Check if day is weekend
     * 检查是否是周末
     */
    private boolean isWeekend(int dayOfWeek) {
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
    
    /**
     * Get current tenant ID from request
     */
    private Long getCurrentTenantId(HttpServletRequest request) {
        // TODO: Implement actual tenant ID extraction logic
        return 0L; // Default tenant for now
    }
}
