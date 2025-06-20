package com.linghua.plugin.schedule.controller;

import com.gitee.starblues.annotation.Extract;
import com.linghua.plugin.schedule.constants.ScheduleConstants;
import com.linghua.plugin.schedule.datasource.entities.ScheduleShift;
import com.linghua.plugin.schedule.service.ScheduleShiftService;
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
 * Schedule Shift Controller
 * 排班班次控制器
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@RestController
@RequestMapping("/api/plugin/calendar-schedule/shift")
@Api(tags = "班次管理")
@Extract
public class ScheduleShiftController {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleShiftController.class);
    
    @Resource
    private ScheduleShiftService scheduleShiftService;
    
    // ==================== 基础CRUD接口 ====================
    
    /**
     * Get shift list
     * 获取班次列表
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/list")
    @ApiOperation("获取班次列表")
    public Map<String, Object> getList(HttpServletRequest request) {
        try {
            List<Map<String, Object>> list = scheduleShiftService.getShiftList(request);
            return success(list);
        } catch (Exception e) {
            logger.error("获取班次列表失败", e);
            return error("获取班次列表失败: " + e.getMessage());
        }
    }
    
    /**
     * Get shift options for dropdown
     * 获取班次下拉选项
     * 
     * @param request HTTP request
     * @return response data
     */
    @GetMapping("/options")
    @ApiOperation("获取班次下拉选项")
    public Map<String, Object> getOptions(HttpServletRequest request) {
        try {
            List<Map<String, Object>> options = scheduleShiftService.getShiftOptions(request);
            return success(options);
        } catch (Exception e) {
            logger.error("获取班次选项失败", e);
            return error("获取班次选项失败: " + e.getMessage());
        }
    }
    
    /**
     * Get shift by ID
     * 根据ID获取班次
     * 
     * @param id shift ID
     * @return response data
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取班次")
    public Map<String, Object> getById(@ApiParam("班次ID") @PathVariable Long id) {
        try {
            ScheduleShift shift = scheduleShiftService.getShiftById(id);
            if (shift == null) {
                return error("班次不存在");
            }
            return success(shift);
        } catch (Exception e) {
            logger.error("获取班次详情失败", e);
            return error("获取班次详情失败: " + e.getMessage());
        }
    }
    
    /**
     * Add new shift
     * 新增班次
     * 
     * @param shift shift data
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/add")
    @ApiOperation("新增班次")
    public Map<String, Object> add(
            @ApiParam("班次数据") @RequestBody ScheduleShift shift,
            HttpServletRequest request) {
        try {
            int result = scheduleShiftService.addShift(shift, request);
            if (result > 0) {
                return success(shift, "班次新增成功");
            } else {
                return error("班次新增失败");
            }
        } catch (Exception e) {
            logger.error("新增班次失败", e);
            return error("新增班次失败: " + e.getMessage());
        }
    }
    
    /**
     * Update shift
     * 更新班次
     * 
     * @param shift shift data
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/update")
    @ApiOperation("更新班次")
    public Map<String, Object> update(
            @ApiParam("班次数据") @RequestBody ScheduleShift shift,
            HttpServletRequest request) {
        try {
            int result = scheduleShiftService.updateShift(shift, request);
            if (result > 0) {
                return success(shift, "班次更新成功");
            } else {
                return error("班次更新失败");
            }
        } catch (Exception e) {
            logger.error("更新班次失败", e);
            return error("更新班次失败: " + e.getMessage());
        }
    }
    
    /**
     * Delete shift
     * 删除班次
     * 
     * @param id shift ID
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/delete/{id}")
    @ApiOperation("删除班次")
    public Map<String, Object> delete(
            @ApiParam("班次ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            int result = scheduleShiftService.deleteShift(id, request);
            if (result > 0) {
                return success(null, "班次删除成功");
            } else {
                return error("班次删除失败");
            }
        } catch (Exception e) {
            logger.error("删除班次失败", e);
            return error("删除班次失败: " + e.getMessage());
        }
    }
    
    // ==================== 批量操作接口 ====================
    
    /**
     * Batch update shift status
     * 批量更新班次状态
     * 
     * @param request HTTP request
     * @return response data
     */
    @PostMapping("/batch-update-status")
    @ApiOperation("批量更新班次状态")
    public Map<String, Object> batchUpdateStatus(HttpServletRequest request) {
        try {
            // Get parameters from request body
            String idsStr = request.getParameter("ids");
            String isActiveStr = request.getParameter("isActive");
            
            if (idsStr == null || idsStr.trim().isEmpty()) {
                return error("班次ID列表不能为空");
            }
            
            if (isActiveStr == null || isActiveStr.trim().isEmpty()) {
                return error("状态参数不能为空");
            }
            
            // Parse parameters
            String[] idArray = idsStr.split(",");
            List<Long> ids = new java.util.ArrayList<>();
            for (String idStr : idArray) {
                ids.add(Long.parseLong(idStr.trim()));
            }
            
            Boolean isActive = "1".equals(isActiveStr) || "true".equalsIgnoreCase(isActiveStr);
            
            int result = scheduleShiftService.batchUpdateStatus(ids, isActive, request);
            
            if (result > 0) {
                return success(null, "批量更新状态成功，影响记录数: " + result);
            } else {
                return error("批量更新状态失败");
            }
            
        } catch (NumberFormatException e) {
            return error("ID格式错误");
        } catch (Exception e) {
            logger.error("批量更新班次状态失败", e);
            return error("批量更新班次状态失败: " + e.getMessage());
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
