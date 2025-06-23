package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.CloisonneSchedule;
import com.jsh.erp.datasource.vo.CloisonneScheduleVo4List;
import com.jsh.erp.datasource.vo.CloisonneScheduleStatistics;
import com.jsh.erp.service.cloisonne.CloisonneScheduleService;
import com.jsh.erp.service.cloisonne.CloisonneIntegrationService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 掐丝珐琅馆排班管理控制器
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@RestController
@RequestMapping(value = "/cloisonne/schedule")
@Api(tags = "掐丝珐琅馆排班管理")
public class CloisonneScheduleController {

    private static final Logger logger = LoggerFactory.getLogger(CloisonneScheduleController.class);

    @Autowired
    private CloisonneScheduleService scheduleService;

    @Autowired
    private CloisonneIntegrationService integrationService;

    /**
     * 查询排班列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "查询排班列表")
    public BaseResponseInfo getScheduleList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) String shiftType,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            if (!integrationService.hasCloisonnePermission(userId, "1002")) {
                res.code = 403;
                res.data = "无权限访问";
                return res;
            }

            // 参数处理
            LocalDate start = StringUtil.isNotEmpty(startDate) ? 
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE) : null;
            LocalDate end = StringUtil.isNotEmpty(endDate) ? 
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE) : null;

            // 查询排班列表
            List<CloisonneScheduleVo4List> scheduleList = scheduleService.findScheduleList(
                start, end, employeeId, shiftType, status);

            res.code = 200;
            res.data = scheduleList;

        } catch (Exception e) {
            logger.error("查询排班列表失败", e);
            res.code = 500;
            res.data = "查询失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 获取排班统计数据
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取排班统计数据")
    public BaseResponseInfo getScheduleStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            if (!integrationService.hasCloisonnePermission(userId, "1002")) {
                res.code = 403;
                res.data = "无权限访问";
                return res;
            }

            // 参数处理
            LocalDate start = StringUtil.isNotEmpty(startDate) ? 
                LocalDate.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now().minusDays(30);
            LocalDate end = StringUtil.isNotEmpty(endDate) ? 
                LocalDate.parse(endDate, DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();

            Map<String, Object> statistics = new HashMap<>();

            // 基础统计
            CloisonneScheduleStatistics basicStats = scheduleService.findScheduleStatistics(start, end);
            statistics.put("basicStats", basicStats);

            // 员工工时统计
            List<CloisonneScheduleStatistics> employeeWorkHours = scheduleService.findEmployeeWorkHours(start, end);
            statistics.put("employeeWorkHours", employeeWorkHours);

            // 每日排班统计
            List<CloisonneScheduleStatistics> dailyCount = scheduleService.findDailyScheduleCount(start, end);
            statistics.put("dailyCount", dailyCount);

            // 班次分布统计
            List<CloisonneScheduleStatistics> shiftDistribution = scheduleService.findShiftDistribution(start, end);
            statistics.put("shiftDistribution", shiftDistribution);

            res.code = 200;
            res.data = statistics;

        } catch (Exception e) {
            logger.error("获取排班统计数据失败", e);
            res.code = 500;
            res.data = "获取统计数据失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 获取今日值班人员
     */
    @GetMapping(value = "/on-duty")
    @ApiOperation(value = "获取今日值班人员")
    public BaseResponseInfo getOnDutyStaff(
            @RequestParam(required = false) String date,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 参数处理
            LocalDate scheduleDate = StringUtil.isNotEmpty(date) ? 
                LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE) : LocalDate.now();

            // 查询值班人员
            List<CloisonneScheduleVo4List> onDutyStaff = scheduleService.findOnDutyStaff(scheduleDate);

            res.code = 200;
            res.data = onDutyStaff;

        } catch (Exception e) {
            logger.error("获取值班人员失败", e);
            res.code = 500;
            res.data = "获取值班人员失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 创建排班记录
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "创建排班记录")
    public BaseResponseInfo addSchedule(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            String buttonPermissions = integrationService.getUserButtonPermissions(userId, "1002");
            if (!buttonPermissions.contains("1")) {
                res.code = 403;
                res.data = "无新增权限";
                return res;
            }

            // 构建排班对象
            CloisonneSchedule schedule = buildScheduleFromJson(obj);

            // 创建排班记录
            boolean success = scheduleService.createSchedule(schedule);

            if (success) {
                res.code = 200;
                res.data = "排班记录创建成功";
            } else {
                res.code = 500;
                res.data = "排班记录创建失败";
            }

        } catch (Exception e) {
            logger.error("创建排班记录失败", e);
            res.code = 500;
            res.data = "创建失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 更新排班记录
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新排班记录")
    public BaseResponseInfo updateSchedule(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            String buttonPermissions = integrationService.getUserButtonPermissions(userId, "1002");
            if (!buttonPermissions.contains("2")) {
                res.code = 403;
                res.data = "无修改权限";
                return res;
            }

            // 构建排班对象
            CloisonneSchedule schedule = buildScheduleFromJson(obj);
            schedule.setId(obj.getLong("id"));

            // 更新排班记录
            boolean success = scheduleService.updateSchedule(schedule);

            if (success) {
                res.code = 200;
                res.data = "排班记录更新成功";
            } else {
                res.code = 500;
                res.data = "排班记录更新失败";
            }

        } catch (Exception e) {
            logger.error("更新排班记录失败", e);
            res.code = 500;
            res.data = "更新失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 删除排班记录
     */
    @DeleteMapping(value = "/delete/{id}")
    @ApiOperation(value = "删除排班记录")
    public BaseResponseInfo deleteSchedule(@PathVariable Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            String buttonPermissions = integrationService.getUserButtonPermissions(userId, "1002");
            if (!buttonPermissions.contains("3")) {
                res.code = 403;
                res.data = "无删除权限";
                return res;
            }

            // 删除排班记录
            boolean success = scheduleService.deleteSchedule(id);

            if (success) {
                res.code = 200;
                res.data = "排班记录删除成功";
            } else {
                res.code = 500;
                res.data = "排班记录删除失败";
            }

        } catch (Exception e) {
            logger.error("删除排班记录失败", e);
            res.code = 500;
            res.data = "删除失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 批量创建排班记录
     */
    @PostMapping(value = "/batch-add")
    @ApiOperation(value = "批量创建排班记录")
    public BaseResponseInfo batchAddSchedule(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            String buttonPermissions = integrationService.getUserButtonPermissions(userId, "1002");
            if (!buttonPermissions.contains("1")) {
                res.code = 403;
                res.data = "无新增权限";
                return res;
            }

            // 解析批量数据
            JSONArray scheduleArray = obj.getJSONArray("schedules");
            List<CloisonneSchedule> scheduleList = new ArrayList<>();

            for (int i = 0; i < scheduleArray.size(); i++) {
                JSONObject scheduleObj = scheduleArray.getJSONObject(i);
                CloisonneSchedule schedule = buildScheduleFromJson(scheduleObj);
                scheduleList.add(schedule);
            }

            // 批量创建排班记录
            boolean success = scheduleService.batchCreateSchedule(scheduleList);

            if (success) {
                res.code = 200;
                res.data = "批量创建排班记录成功，共" + scheduleList.size() + "条";
            } else {
                res.code = 500;
                res.data = "批量创建排班记录失败";
            }

        } catch (Exception e) {
            logger.error("批量创建排班记录失败", e);
            res.code = 500;
            res.data = "批量创建失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 批量更新排班状态
     */
    @PutMapping(value = "/batch-status")
    @ApiOperation(value = "批量更新排班状态")
    public BaseResponseInfo batchUpdateStatus(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 权限检查
            Long userId = Long.valueOf(request.getHeader("userId"));
            String buttonPermissions = integrationService.getUserButtonPermissions(userId, "1002");
            if (!buttonPermissions.contains("2")) {
                res.code = 403;
                res.data = "无修改权限";
                return res;
            }

            // 解析参数
            JSONArray idsArray = obj.getJSONArray("ids");
            String status = obj.getString("status");

            List<Long> ids = new ArrayList<>();
            for (int i = 0; i < idsArray.size(); i++) {
                ids.add(idsArray.getLong(i));
            }

            // 批量更新状态
            boolean success = scheduleService.batchUpdateScheduleStatus(ids, status);

            if (success) {
                res.code = 200;
                res.data = "批量更新状态成功";
            } else {
                res.code = 500;
                res.data = "批量更新状态失败";
            }

        } catch (Exception e) {
            logger.error("批量更新排班状态失败", e);
            res.code = 500;
            res.data = "批量更新失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 获取员工列表(用于排班选择)
     */
    @GetMapping(value = "/employees")
    @ApiOperation(value = "获取员工列表")
    public BaseResponseInfo getEmployees(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long tenantId = Long.valueOf(request.getHeader("tenantId"));
            
            // 获取员工列表
            List<Map<String, Object>> employees = integrationService.getAllEmployees(tenantId);
            
            res.code = 200;
            res.data = employees;

        } catch (Exception e) {
            logger.error("获取员工列表失败", e);
            res.code = 500;
            res.data = "获取员工列表失败: " + e.getMessage();
        }
        return res;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 从JSON构建排班对象
     */
    private CloisonneSchedule buildScheduleFromJson(JSONObject obj) {
        CloisonneSchedule schedule = new CloisonneSchedule();
        
        // 基础信息
        if (obj.containsKey("scheduleDate")) {
            schedule.setScheduleDate(LocalDate.parse(obj.getString("scheduleDate")));
        }
        if (obj.containsKey("employeeId")) {
            schedule.setEmployeeId(obj.getLong("employeeId"));
        }
        if (obj.containsKey("employeeName")) {
            schedule.setEmployeeName(obj.getString("employeeName"));
        }
        if (obj.containsKey("shiftType")) {
            schedule.setShiftType(obj.getString("shiftType"));
        }
        if (obj.containsKey("workArea")) {
            schedule.setWorkArea(obj.getString("workArea"));
        }
        if (obj.containsKey("status")) {
            schedule.setStatus(obj.getString("status"));
        } else {
            schedule.setStatus("normal");
        }
        if (obj.containsKey("notes")) {
            schedule.setNotes(obj.getString("notes"));
        }

        return schedule;
    }
}
