package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.ChongzuoProductionService;
import com.jsh.erp.utils.BaseResponseInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 崇左生产看板控制器
 * 提供生产看板相关的API接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/chongzuo/production")
@Api(tags = "崇左生产看板")
public class ChongzuoProductionController {
    
    private Logger logger = LoggerFactory.getLogger(ChongzuoProductionController.class);
    
    @Resource
    private ChongzuoProductionService chongzuoProductionService;
    
    /**
     * 获取生产看板统计数据
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取生产看板统计数据")
    public BaseResponseInfo getProductionStatistics() {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject statistics = chongzuoProductionService.getProductionStatistics();
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error("获取生产统计数据失败", e);
            res.code = 500;
            res.data = "获取统计数据失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取任务看板数据
     */
    @GetMapping(value = "/tasks")
    @ApiOperation(value = "获取任务看板数据")
    public BaseResponseInfo getTaskBoardData(@RequestParam(required = false) String status,
                                           @RequestParam(required = false) String priority,
                                           @RequestParam(required = false) String workerId) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("status", status);
            params.put("priority", priority);
            params.put("workerId", workerId);
            
            JSONObject taskData = chongzuoProductionService.getTaskBoardData(params);
            res.code = 200;
            res.data = taskData;
        } catch (Exception e) {
            logger.error("获取任务看板数据失败", e);
            res.code = 500;
            res.data = "获取任务数据失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 更新任务状态
     */
    @PostMapping(value = "/task/status")
    @ApiOperation(value = "更新任务状态")
    public BaseResponseInfo updateTaskStatus(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = chongzuoProductionService.updateTaskStatus(params, request);
            if (success) {
                res.code = 200;
                res.data = "任务状态更新成功";
            } else {
                res.code = 500;
                res.data = "任务状态更新失败";
            }
        } catch (Exception e) {
            logger.error("更新任务状态失败", e);
            res.code = 500;
            res.data = "更新任务状态失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 任务派工
     */
    @PostMapping(value = "/task/assign")
    @ApiOperation(value = "任务派工")
    public BaseResponseInfo assignTask(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = chongzuoProductionService.assignTask(params, request);
            if (success) {
                res.code = 200;
                res.data = "任务派工成功";
            } else {
                res.code = 500;
                res.data = "任务派工失败";
            }
        } catch (Exception e) {
            logger.error("任务派工失败", e);
            res.code = 500;
            res.data = "任务派工失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 开始任务
     */
    @PostMapping(value = "/task/start")
    @ApiOperation(value = "开始任务")
    public BaseResponseInfo startTask(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = chongzuoProductionService.startTask(params, request);
            if (success) {
                res.code = 200;
                res.data = "任务开始成功";
            } else {
                res.code = 500;
                res.data = "任务开始失败";
            }
        } catch (Exception e) {
            logger.error("任务开始失败", e);
            res.code = 500;
            res.data = "任务开始失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 完成任务
     */
    @PostMapping(value = "/task/complete")
    @ApiOperation(value = "完成任务")
    public BaseResponseInfo completeTask(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = chongzuoProductionService.completeTask(params, request);
            if (success) {
                res.code = 200;
                res.data = "任务完成成功";
            } else {
                res.code = 500;
                res.data = "任务完成失败";
            }
        } catch (Exception e) {
            logger.error("任务完成失败", e);
            res.code = 500;
            res.data = "任务完成失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取工人列表
     */
    @GetMapping(value = "/workers")
    @ApiOperation(value = "获取工人列表")
    public BaseResponseInfo getWorkerList(@RequestParam(required = false) String specialty,
                                        @RequestParam(required = false) String status) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("specialty", specialty);
            params.put("status", status);
            
            JSONArray workers = chongzuoProductionService.getWorkerList(params);
            res.code = 200;
            res.data = workers;
        } catch (Exception e) {
            logger.error("获取工人列表失败", e);
            res.code = 500;
            res.data = "获取工人列表失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取实时生产数据
     */
    @GetMapping(value = "/realtime")
    @ApiOperation(value = "获取实时生产数据")
    public BaseResponseInfo getRealtimeData() {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject realtimeData = chongzuoProductionService.getRealtimeData();
            res.code = 200;
            res.data = realtimeData;
        } catch (Exception e) {
            logger.error("获取实时生产数据失败", e);
            res.code = 500;
            res.data = "获取实时数据失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取生产报告
     */
    @GetMapping(value = "/report")
    @ApiOperation(value = "获取生产报告")
    public BaseResponseInfo getProductionReport(@RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate,
                                              @RequestParam(required = false) String type) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("type", type);
            
            JSONObject report = chongzuoProductionService.getProductionReport(params);
            res.code = 200;
            res.data = report;
        } catch (Exception e) {
            logger.error("获取生产报告失败", e);
            res.code = 500;
            res.data = "获取生产报告失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取质量统计
     */
    @GetMapping(value = "/quality")
    @ApiOperation(value = "获取质量统计")
    public BaseResponseInfo getQualityStatistics(@RequestParam(required = false) String period) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject qualityStats = chongzuoProductionService.getQualityStatistics(period);
            res.code = 200;
            res.data = qualityStats;
        } catch (Exception e) {
            logger.error("获取质量统计失败", e);
            res.code = 500;
            res.data = "获取质量统计失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取设备状态
     */
    @GetMapping(value = "/equipment")
    @ApiOperation(value = "获取设备状态")
    public BaseResponseInfo getEquipmentStatus() {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONArray equipmentList = chongzuoProductionService.getEquipmentStatus();
            res.code = 200;
            res.data = equipmentList;
        } catch (Exception e) {
            logger.error("获取设备状态失败", e);
            res.code = 500;
            res.data = "获取设备状态失败：" + e.getMessage();
        }
        return res;
    }
}
