package com.linghua.plugin.schedule.controller;

import com.gitee.starblues.annotation.Extract;
import com.linghua.plugin.schedule.service.ScheduleCalendarService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Schedule Calendar Controller
 * 日历排班控制器
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@RestController
@RequestMapping("/api/plugin/calendar-schedule/calendar")
@Api(tags = "日历排班")
@Extract
public class ScheduleCalendarController {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleCalendarController.class);
    
    @Resource
    private ScheduleCalendarService scheduleCalendarService;
    
    // ==================== 日历数据接口 ====================
    
    /**
     * Get calendar data for month view
     * 获取月视图日历数据
     * 
     * @param year year (YYYY)
     * @param month month (MM)
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/month")
    @ApiOperation("获取月视图日历数据")
    public Map<String, Object> getMonthData(
            @ApiParam("年份") @RequestParam Integer year,
            @ApiParam("月份") @RequestParam Integer month,
            HttpServletRequest request) {
        try {
            // Set parameters to request for service method
            request.setAttribute("year", year.toString());
            request.setAttribute("month", month.toString());
            
            Map<String, Object> calendarData = scheduleCalendarService.getCalendarDataForMonth(request);
            return success(calendarData);
        } catch (Exception e) {
            logger.error("获取月视图日历数据失败", e);
            return error("获取月视图日历数据失败: " + e.getMessage());
        }
    }
    
    /**
     * Get calendar data for month view (alternative method with request parameters)
     * 获取月视图日历数据（备用方法，使用请求参数）
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/month-data")
    @ApiOperation("获取月视图日历数据（备用接口）")
    public Map<String, Object> getMonthDataFromRequest(HttpServletRequest request) {
        try {
            Map<String, Object> calendarData = scheduleCalendarService.getCalendarDataForMonth(request);
            return success(calendarData);
        } catch (Exception e) {
            logger.error("获取月视图日历数据失败", e);
            return error("获取月视图日历数据失败: " + e.getMessage());
        }
    }
    
    /**
     * Get user assignments for date range
     * 获取用户在指定日期范围内的排班
     * 
     * @param userId user ID
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/user/{userId}")
    @ApiOperation("获取用户排班记录")
    public Map<String, Object> getUserAssignments(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("开始日期") @RequestParam String startDate,
            @ApiParam("结束日期") @RequestParam String endDate,
            HttpServletRequest request) {
        try {
            // Set parameters to request for service method
            request.setAttribute("userId", userId.toString());
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            
            Map<String, Object> userAssignments = scheduleCalendarService.getUserAssignments(request);
            return success(userAssignments);
        } catch (Exception e) {
            logger.error("获取用户排班记录失败", e);
            return error("获取用户排班记录失败: " + e.getMessage());
        }
    }
    
    /**
     * Get user assignments (alternative method with request parameters)
     * 获取用户排班记录（备用方法，使用请求参数）
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/user-assignments")
    @ApiOperation("获取用户排班记录（备用接口）")
    public Map<String, Object> getUserAssignmentsFromRequest(HttpServletRequest request) {
        try {
            Map<String, Object> userAssignments = scheduleCalendarService.getUserAssignments(request);
            return success(userAssignments);
        } catch (Exception e) {
            logger.error("获取用户排班记录失败", e);
            return error("获取用户排班记录失败: " + e.getMessage());
        }
    }
    
    /**
     * Get assignment statistics
     * 获取排班统计信息
     * 
     * @param startDate start date (YYYY-MM-DD)
     * @param endDate end date (YYYY-MM-DD)
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/statistics")
    @ApiOperation("获取排班统计信息")
    public Map<String, Object> getStatistics(
            @ApiParam("开始日期") @RequestParam String startDate,
            @ApiParam("结束日期") @RequestParam String endDate,
            HttpServletRequest request) {
        try {
            // Set parameters to request for service method
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            
            Map<String, Object> statistics = scheduleCalendarService.getAssignmentStatistics(request);
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取排班统计信息失败", e);
            return error("获取排班统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * Get assignment statistics (alternative method with request parameters)
     * 获取排班统计信息（备用方法，使用请求参数）
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/statistics-data")
    @ApiOperation("获取排班统计信息（备用接口）")
    public Map<String, Object> getStatisticsFromRequest(HttpServletRequest request) {
        try {
            Map<String, Object> statistics = scheduleCalendarService.getAssignmentStatistics(request);
            return success(statistics);
        } catch (Exception e) {
            logger.error("获取排班统计信息失败", e);
            return error("获取排班统计信息失败: " + e.getMessage());
        }
    }
    
    // ==================== 快捷接口 ====================
    
    /**
     * Get current month calendar data
     * 获取当前月份日历数据
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/current-month")
    @ApiOperation("获取当前月份日历数据")
    public Map<String, Object> getCurrentMonthData(HttpServletRequest request) {
        try {
            // Get current year and month
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar month is 0-based
            
            // Set parameters to request
            request.setAttribute("year", String.valueOf(year));
            request.setAttribute("month", String.valueOf(month));
            
            Map<String, Object> calendarData = scheduleCalendarService.getCalendarDataForMonth(request);
            return success(calendarData);
        } catch (Exception e) {
            logger.error("获取当前月份日历数据失败", e);
            return error("获取当前月份日历数据失败: " + e.getMessage());
        }
    }
    
    /**
     * Get today's assignments
     * 获取今日排班
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/today")
    @ApiOperation("获取今日排班")
    public Map<String, Object> getTodayAssignments(HttpServletRequest request) {
        try {
            // Get today's date
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String today = dateFormat.format(new java.util.Date());
            
            // Set parameter to request
            request.setAttribute("scheduleDate", today);
            
            // This would need to be implemented in ScheduleAssignmentService
            // For now, return a placeholder response
            Map<String, Object> todayData = new HashMap<>();
            todayData.put("date", today);
            todayData.put("assignments", new java.util.ArrayList<>());
            
            return success(todayData);
        } catch (Exception e) {
            logger.error("获取今日排班失败", e);
            return error("获取今日排班失败: " + e.getMessage());
        }
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * Create success response
     * 创建成功响应
     * 
     * @param data response data
     * @return response map
     */
    private Map<String, Object> success(Object data) {
        return success(data, "操作成功");
    }
    
    /**
     * Create success response with message
     * 创建带消息的成功响应
     * 
     * @param data response data
     * @param message success message
     * @return response map
     */
    private Map<String, Object> success(Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
    
    /**
     * Create error response
     * 创建错误响应
     * 
     * @param message error message
     * @return response map
     */
    private Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
