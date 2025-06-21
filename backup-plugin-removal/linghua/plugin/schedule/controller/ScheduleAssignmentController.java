package com.linghua.plugin.schedule.controller;

import com.gitee.starblues.annotation.Extract;
import com.linghua.plugin.schedule.datasource.entities.ScheduleAssignment;
import com.linghua.plugin.schedule.service.ScheduleAssignmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Schedule Assignment Controller
 * 排班分配记录控制器
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@RestController
@RequestMapping("/api/plugin/calendar-schedule/assignment")
@Api(tags = "排班管理")
@Extract
public class ScheduleAssignmentController {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleAssignmentController.class);
    
    @Resource
    private ScheduleAssignmentService scheduleAssignmentService;
    
    // ==================== 基础CRUD接口 ====================
    
    /**
     * Get assignments by date
     * 根据日期获取排班记录
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/by-date")
    @ApiOperation("根据日期获取排班记录")
    public Map<String, Object> getByDate(HttpServletRequest request) {
        try {
            List<Map<String, Object>> assignments = scheduleAssignmentService.getAssignmentsByDate(request);
            return success(assignments);
        } catch (Exception e) {
            logger.error("获取排班记录失败", e);
            return error("获取排班记录失败: " + e.getMessage());
        }
    }
    
    /**
     * Get assignment by ID
     * 根据ID获取排班记录
     * 
     * @param id assignment ID
     * @return response data
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取排班记录")
    public Map<String, Object> getById(@ApiParam("排班记录ID") @PathVariable Long id) {
        try {
            ScheduleAssignment assignment = scheduleAssignmentService.getAssignmentById(id);
            if (assignment == null) {
                return error("排班记录不存在");
            }
            return success(assignment);
        } catch (Exception e) {
            logger.error("获取排班记录详情失败", e);
            return error("获取排班记录详情失败: " + e.getMessage());
        }
    }
    
    /**
     * Add new assignment
     * 新增排班记录
     * 
     * @param assignment assignment data
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/add")
    @ApiOperation("新增排班记录")
    public Map<String, Object> add(
            @ApiParam("排班记录数据") @RequestBody ScheduleAssignment assignment,
            HttpServletRequest request) {
        try {
            int result = scheduleAssignmentService.addAssignment(assignment, request);
            if (result > 0) {
                return success(assignment, "排班记录新增成功");
            } else {
                return error("排班记录新增失败");
            }
        } catch (Exception e) {
            logger.error("新增排班记录失败", e);
            return error("新增排班记录失败: " + e.getMessage());
        }
    }
    
    /**
     * Update assignment
     * 更新排班记录
     * 
     * @param assignment assignment data
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/update")
    @ApiOperation("更新排班记录")
    public Map<String, Object> update(
            @ApiParam("排班记录数据") @RequestBody ScheduleAssignment assignment,
            HttpServletRequest request) {
        try {
            int result = scheduleAssignmentService.updateAssignment(assignment, request);
            if (result > 0) {
                return success(assignment, "排班记录更新成功");
            } else {
                return error("排班记录更新失败");
            }
        } catch (Exception e) {
            logger.error("更新排班记录失败", e);
            return error("更新排班记录失败: " + e.getMessage());
        }
    }
    
    /**
     * Delete assignment
     * 删除排班记录
     * 
     * @param id assignment ID
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除排班记录")
    public Map<String, Object> delete(
            @ApiParam("排班记录ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            int result = scheduleAssignmentService.deleteAssignment(id, request);
            if (result > 0) {
                return success(null, "排班记录删除成功");
            } else {
                return error("排班记录删除失败");
            }
        } catch (Exception e) {
            logger.error("删除排班记录失败", e);
            return error("删除排班记录失败: " + e.getMessage());
        }
    }
    
    // ==================== 批量操作接口 ====================
    
    /**
     * Batch assign schedule
     * 批量排班
     * 
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/batch-assign")
    @ApiOperation("批量排班")
    public Map<String, Object> batchAssign(@RequestBody Map<String, Object> requestData, HttpServletRequest request) {
        try {
            // Get parameters from request body
            @SuppressWarnings("unchecked")
            List<String> dates = (List<String>) requestData.get("dates");
            @SuppressWarnings("unchecked")
            List<Integer> userIdInts = (List<Integer>) requestData.get("userIds");
            Integer shiftIdInt = (Integer) requestData.get("shiftId");
            
            if (dates == null || dates.isEmpty()) {
                return error("日期列表不能为空");
            }
            
            if (userIdInts == null || userIdInts.isEmpty()) {
                return error("用户列表不能为空");
            }
            
            if (shiftIdInt == null) {
                return error("班次ID不能为空");
            }
            
            // Convert Integer to Long
            List<Long> userIds = new java.util.ArrayList<>();
            for (Integer userId : userIdInts) {
                userIds.add(userId.longValue());
            }
            Long shiftId = shiftIdInt.longValue();
            
            int result = scheduleAssignmentService.batchAssignSchedule(dates, userIds, shiftId, request);
            
            if (result > 0) {
                return success(null, "批量排班成功，创建记录数: " + result);
            } else {
                return error("批量排班失败");
            }
            
        } catch (Exception e) {
            logger.error("批量排班失败", e);
            return error("批量排班失败: " + e.getMessage());
        }
    }
    
    /**
     * Batch add assignments
     * 批量新增排班记录
     * 
     * @param assignments assignment list
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/batch-add")
    @ApiOperation("批量新增排班记录")
    public Map<String, Object> batchAdd(
            @ApiParam("排班记录列表") @RequestBody List<ScheduleAssignment> assignments,
            HttpServletRequest request) {
        try {
            int result = scheduleAssignmentService.batchAddAssignments(assignments, request);
            if (result > 0) {
                return success(null, "批量新增排班记录成功，创建记录数: " + result);
            } else {
                return error("批量新增排班记录失败");
            }
        } catch (Exception e) {
            logger.error("批量新增排班记录失败", e);
            return error("批量新增排班记录失败: " + e.getMessage());
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
