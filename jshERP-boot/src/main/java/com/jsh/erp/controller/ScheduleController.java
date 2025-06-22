package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.datasource.entities.ScheduleEntryEx;
import com.jsh.erp.datasource.entities.ScheduleShift;
import com.jsh.erp.service.ScheduleEntryService;
import com.jsh.erp.service.ScheduleShiftService;
import com.jsh.erp.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 排班管理统一Controller
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
@RestController
@RequestMapping(value = "/schedule")
@Api(tags = {"排班管理"})
public class ScheduleController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Resource
    private ScheduleShiftService scheduleShiftService;
    
    @Resource
    private ScheduleEntryService scheduleEntryService;
    
    @Resource
    private UserService userService;

    private static String SUCCESS = "操作成功";
    private static String ERROR = "操作失败";

    // ==================== 班次管理相关接口 ====================

    @GetMapping(value = "/shifts")
    @ApiOperation(value = "获取所有班次")
    public BaseResponseInfo getShifts(HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long tenantId = userService.getCurrentUser().getTenantId();
            List<ScheduleShift> list = scheduleShiftService.getActiveShifts(tenantId);
            
            JSONArray dataArray = new JSONArray();
            if (list != null) {
                for (ScheduleShift shift : list) {
                    JSONObject item = new JSONObject();
                    item.put("id", shift.getId());
                    item.put("shiftName", shift.getShiftName());
                    item.put("shiftType", shift.getShiftType());
                    item.put("startTime", shift.getStartTime());
                    item.put("endTime", shift.getEndTime());
                    item.put("durationHours", shift.getDurationHours());
                    item.put("color", shift.getColor());
                    item.put("description", shift.getDescription());
                    item.put("isActive", shift.getIsActive());
                    item.put("sortOrder", shift.getSortOrder());
                    dataArray.add(item);
                }
            }
            
            res.code = 200;
            res.data = dataArray;
        } catch (Exception e) {
            logger.error("获取班次列表失败", e);
            res.code = 500;
            res.data = "获取班次列表失败";
        }
        return res;
    }

    @PostMapping(value = "/shift")
    @ApiOperation(value = "新增或修改班次")
    public BaseResponseInfo saveShift(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            int result;
            if (obj.getLong("id") != null && obj.getLong("id") > 0) {
                // 修改班次
                result = scheduleShiftService.updateScheduleShift(obj, request);
            } else {
                // 新增班次
                result = scheduleShiftService.insertScheduleShift(obj, request);
            }
            
            if (result > 0) {
                res.code = 200;
                res.data = SUCCESS;
            } else {
                res.code = 500;
                res.data = ERROR;
            }
        } catch (Exception e) {
            logger.error("保存班次失败", e);
            res.code = 500;
            res.data = "保存班次失败: " + e.getMessage();
        }
        return res;
    }

    // ==================== 排班记录相关接口 ====================

    @GetMapping(value = "/entries")
    @ApiOperation(value = "根据日期范围查询排班记录")
    public BaseResponseInfo getEntries(@ApiParam("开始日期") @RequestParam("start_date") String startDate,
                                      @ApiParam("结束日期") @RequestParam("end_date") String endDate,
                                      @ApiParam("员工ID") @RequestParam(value = "employee_id", required = false) Long employeeId,
                                      @ApiParam("状态") @RequestParam(value = "status", required = false) String status,
                                      HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ScheduleEntryEx> list = scheduleEntryService.select(employeeId, startDate, endDate, status, request);
            
            JSONArray dataArray = new JSONArray();
            if (list != null) {
                for (ScheduleEntryEx entry : list) {
                    JSONObject item = new JSONObject();
                    item.put("id", entry.getId());
                    item.put("shiftId", entry.getShiftId());
                    item.put("shiftName", entry.getShiftName());
                    item.put("shiftType", entry.getShiftType());
                    item.put("shiftStartTime", entry.getShiftStartTime());
                    item.put("shiftEndTime", entry.getShiftEndTime());
                    item.put("shiftColor", entry.getShiftColor());
                    item.put("employeeId", entry.getEmployeeId());
                    item.put("employeeName", entry.getEmployeeName());
                    item.put("scheduleDate", entry.getScheduleDateStr());
                    item.put("status", entry.getStatus());
                    item.put("statusName", entry.getStatusName());
                    item.put("actualStartTime", entry.getActualStartTime());
                    item.put("actualEndTime", entry.getActualEndTime());
                    item.put("breakDuration", entry.getBreakDuration());
                    item.put("overtimeDuration", entry.getOvertimeDuration());
                    item.put("remark", entry.getRemark());
                    dataArray.add(item);
                }
            }
            
            res.code = 200;
            res.data = dataArray;
        } catch (Exception e) {
            logger.error("查询排班记录失败", e);
            res.code = 500;
            res.data = "查询排班记录失败";
        }
        return res;
    }

    @PostMapping(value = "/entry")
    @ApiOperation(value = "创建一条或多条排班记录")
    public BaseResponseInfo createEntry(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 检查是否为批量创建
            if (obj.containsKey("entries") && obj.getJSONArray("entries") != null) {
                // 批量创建排班记录
                JSONArray entries = obj.getJSONArray("entries");
                int successCount = 0;
                int totalCount = entries.size();
                
                for (int i = 0; i < totalCount; i++) {
                    JSONObject entryObj = entries.getJSONObject(i);
                    try {
                        int result = scheduleEntryService.insertScheduleEntry(entryObj, request);
                        if (result > 0) {
                            successCount++;
                        }
                    } catch (Exception e) {
                        logger.warn("创建排班记录失败: " + e.getMessage());
                    }
                }
                
                res.code = 200;
                res.data = "成功创建 " + successCount + "/" + totalCount + " 条排班记录";
            } else {
                // 单条创建排班记录
                int result = scheduleEntryService.insertScheduleEntry(obj, request);
                if (result > 0) {
                    res.code = 200;
                    res.data = SUCCESS;
                } else {
                    res.code = 500;
                    res.data = ERROR;
                }
            }
        } catch (Exception e) {
            logger.error("创建排班记录失败", e);
            res.code = 500;
            res.data = "创建排班记录失败: " + e.getMessage();
        }
        return res;
    }

    @PutMapping(value = "/entry/{id}")
    @ApiOperation(value = "根据ID更新排班记录")
    public BaseResponseInfo updateEntry(@ApiParam("排班记录ID") @PathVariable("id") Long id,
                                       @RequestBody JSONObject obj,
                                       HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 确保ID一致
            obj.put("id", id);
            
            int result = scheduleEntryService.updateScheduleEntry(obj, request);
            if (result > 0) {
                res.code = 200;
                res.data = SUCCESS;
            } else {
                res.code = 500;
                res.data = ERROR;
            }
        } catch (Exception e) {
            logger.error("更新排班记录失败", e);
            res.code = 500;
            res.data = "更新排班记录失败: " + e.getMessage();
        }
        return res;
    }

    @DeleteMapping(value = "/entry/{id}")
    @ApiOperation(value = "根据ID删除排班记录")
    public BaseResponseInfo deleteEntry(@ApiParam("排班记录ID") @PathVariable("id") Long id,
                                       HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            int result = scheduleEntryService.deleteScheduleEntry(id, request);
            if (result > 0) {
                res.code = 200;
                res.data = SUCCESS;
            } else {
                res.code = 500;
                res.data = ERROR;
            }
        } catch (Exception e) {
            logger.error("删除排班记录失败", e);
            res.code = 500;
            res.data = "删除排班记录失败: " + e.getMessage();
        }
        return res;
    }

    @GetMapping(value = "/export")
    @ApiOperation(value = "导出排班表")
    public void exportSchedule(@ApiParam("开始日期") @RequestParam("start_date") String startDate,
                              @ApiParam("结束日期") @RequestParam("end_date") String endDate,
                              @ApiParam("员工ID") @RequestParam(value = "employee_id", required = false) Long employeeId,
                              @ApiParam("导出格式") @RequestParam(value = "format", defaultValue = "excel") String format,
                              HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        try {
            // TODO: 实现排班表导出功能
            // 1. 查询排班数据
            List<ScheduleEntryEx> list = scheduleEntryService.select(employeeId, startDate, endDate, null, request);
            
            // 2. 根据format参数选择导出格式（Excel、PDF等）
            if ("excel".equalsIgnoreCase(format)) {
                // 导出Excel格式
                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader("Content-Disposition", "attachment; filename=schedule_" + startDate + "_" + endDate + ".xlsx");
                
                // TODO: 使用POI或EasyExcel生成Excel文件
                logger.info("导出Excel格式排班表: " + startDate + " 至 " + endDate);
                
            } else if ("pdf".equalsIgnoreCase(format)) {
                // 导出PDF格式
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=schedule_" + startDate + "_" + endDate + ".pdf");
                
                // TODO: 使用iText或其他PDF库生成PDF文件
                logger.info("导出PDF格式排班表: " + startDate + " 至 " + endDate);
                
            } else {
                // 默认导出CSV格式
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=schedule_" + startDate + "_" + endDate + ".csv");
                
                // TODO: 生成CSV文件
                logger.info("导出CSV格式排班表: " + startDate + " 至 " + endDate);
            }
            
            // 临时返回提示信息
            response.getWriter().write("排班表导出功能开发中，查询到 " + (list != null ? list.size() : 0) + " 条记录");
            
        } catch (Exception e) {
            logger.error("导出排班表失败", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("导出失败: " + e.getMessage());
        }
    }

    // ==================== 辅助接口 ====================

    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取排班统计信息")
    public BaseResponseInfo getStatistics(@RequestParam("start_date") String startDate,
                                         @RequestParam("end_date") String endDate,
                                         @RequestParam(value = "employee_id", required = false) Long employeeId,
                                         HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // TODO: 实现排班统计功能
            Map<String, Object> statistics = new HashMap<>();
            
            if (employeeId != null) {
                // 单个员工统计
                Double workHours = scheduleEntryService.sumWorkHoursByEmployee(employeeId, startDate, endDate, request);
                statistics.put("totalWorkHours", workHours);
                statistics.put("employeeId", employeeId);
            } else {
                // 全员统计
                Long totalCount = scheduleEntryService.countScheduleEntry(null, startDate, endDate, null, request);
                statistics.put("totalScheduleCount", totalCount);
            }
            
            statistics.put("startDate", startDate);
            statistics.put("endDate", endDate);
            
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error("获取排班统计失败", e);
            res.code = 500;
            res.data = "获取排班统计失败";
        }
        return res;
    }
}
