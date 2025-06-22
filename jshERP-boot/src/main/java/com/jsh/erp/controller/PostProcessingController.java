package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.PostProcessingService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后工任务管理控制器
 * 提供后工任务相关的API接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/postProcessing")
@Api(tags = "后工任务管理")
public class PostProcessingController {
    
    private Logger logger = LoggerFactory.getLogger(PostProcessingController.class);
    
    @Resource
    private PostProcessingService postProcessingService;
    
    /**
     * 分页查询后工任务列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "分页查询后工任务列表")
    public BaseResponseInfo getPostProcessingList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "taskNumber", required = false) String taskNumber,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "taskType", required = false) String taskType,
            @RequestParam(name = "workerName", required = false) String workerName,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("taskNumber", taskNumber);
            params.put("productName", productName);
            params.put("status", status);
            params.put("taskType", taskType);
            params.put("workerName", workerName);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            PageUtils.startPage(pageNo, pageSize);
            JSONObject result = postProcessingService.getPostProcessingList(params, request);
            
            res.code = 200;
            res.data = result;
        } catch (Exception e) {
            logger.error("查询后工任务列表失败", e);
            res.code = 500;
            res.data = "查询失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 新增后工任务
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增后工任务")
    public BaseResponseInfo addPostProcessing(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = postProcessingService.addPostProcessing(params, request);
            if (success) {
                res.code = 200;
                res.data = "新增成功";
            } else {
                res.code = 500;
                res.data = "新增失败";
            }
        } catch (Exception e) {
            logger.error("新增后工任务失败", e);
            res.code = 500;
            res.data = "新增失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 更新后工任务
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "更新后工任务")
    public BaseResponseInfo updatePostProcessing(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = postProcessingService.updatePostProcessing(params, request);
            if (success) {
                res.code = 200;
                res.data = "更新成功";
            } else {
                res.code = 500;
                res.data = "更新失败";
            }
        } catch (Exception e) {
            logger.error("更新后工任务失败", e);
            res.code = 500;
            res.data = "更新失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 删除后工任务
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除后工任务")
    public BaseResponseInfo deletePostProcessing(@RequestParam("id") Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = postProcessingService.deletePostProcessing(id, request);
            if (success) {
                res.code = 200;
                res.data = "删除成功";
            } else {
                res.code = 500;
                res.data = "删除失败";
            }
        } catch (Exception e) {
            logger.error("删除后工任务失败", e);
            res.code = 500;
            res.data = "删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 批量删除后工任务
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除后工任务")
    public BaseResponseInfo deleteBatchPostProcessing(@RequestParam("ids") String ids, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = postProcessingService.deleteBatchPostProcessing(ids, request);
            if (success) {
                res.code = 200;
                res.data = "批量删除成功";
            } else {
                res.code = 500;
                res.data = "批量删除失败";
            }
        } catch (Exception e) {
            logger.error("批量删除后工任务失败", e);
            res.code = 500;
            res.data = "批量删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 分配任务
     */
    @PostMapping(value = "/assign")
    @ApiOperation(value = "分配任务")
    public BaseResponseInfo assignTask(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = postProcessingService.assignTask(params, request);
            if (success) {
                res.code = 200;
                res.data = "分配任务成功";
            } else {
                res.code = 500;
                res.data = "分配任务失败";
            }
        } catch (Exception e) {
            logger.error("分配任务失败", e);
            res.code = 500;
            res.data = "分配任务失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 开始任务
     */
    @PostMapping(value = "/start")
    @ApiOperation(value = "开始任务")
    public BaseResponseInfo startTask(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long id = params.getLong("id");
            boolean success = postProcessingService.startTask(id, request);
            if (success) {
                res.code = 200;
                res.data = "开始任务成功";
            } else {
                res.code = 500;
                res.data = "开始任务失败";
            }
        } catch (Exception e) {
            logger.error("开始任务失败", e);
            res.code = 500;
            res.data = "开始任务失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 完成任务
     */
    @PostMapping(value = "/complete")
    @ApiOperation(value = "完成任务")
    public BaseResponseInfo completeTask(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long id = params.getLong("id");
            boolean success = postProcessingService.completeTask(id, request);
            if (success) {
                res.code = 200;
                res.data = "完成任务成功";
            } else {
                res.code = 500;
                res.data = "完成任务失败";
            }
        } catch (Exception e) {
            logger.error("完成任务失败", e);
            res.code = 500;
            res.data = "完成任务失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 导出Excel
     */
    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "导出Excel")
    public void exportExcel(
            @RequestParam(name = "taskNumber", required = false) String taskNumber,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "taskType", required = false) String taskType,
            @RequestParam(name = "workerName", required = false) String workerName,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request, HttpServletResponse response) {
        
        try {
            JSONObject params = new JSONObject();
            params.put("taskNumber", taskNumber);
            params.put("productName", productName);
            params.put("status", status);
            params.put("taskType", taskType);
            params.put("workerName", workerName);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            postProcessingService.exportExcel(params, request, response);
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
        }
    }
    
    /**
     * 获取任务统计
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取任务统计")
    public BaseResponseInfo getTaskStatistics(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject statistics = postProcessingService.getTaskStatistics(request);
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error("获取任务统计失败", e);
            res.code = 500;
            res.data = "获取统计失败：" + e.getMessage();
        }
        return res;
    }
}
