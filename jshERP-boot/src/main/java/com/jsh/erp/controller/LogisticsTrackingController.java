package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.LogisticsTrackingService;
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
 * 物流追踪管理控制器
 * 提供物流追踪相关的API接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/logistics/tracking")
@Api(tags = "物流追踪管理")
public class LogisticsTrackingController {
    
    private Logger logger = LoggerFactory.getLogger(LogisticsTrackingController.class);
    
    @Resource
    private LogisticsTrackingService logisticsTrackingService;
    
    /**
     * 分页查询物流追踪列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "分页查询物流追踪列表")
    public BaseResponseInfo getLogisticsTrackingList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "trackingNumber", required = false) String trackingNumber,
            @RequestParam(name = "orderNumber", required = false) String orderNumber,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "logisticsCompany", required = false) String logisticsCompany,
            @RequestParam(name = "recipientName", required = false) String recipientName,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("trackingNumber", trackingNumber);
            params.put("orderNumber", orderNumber);
            params.put("status", status);
            params.put("logisticsCompany", logisticsCompany);
            params.put("recipientName", recipientName);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            PageUtils.startPage(pageNo, pageSize);
            JSONObject result = logisticsTrackingService.getLogisticsTrackingList(params, request);
            
            res.code = 200;
            res.data = result;
        } catch (Exception e) {
            logger.error("查询物流追踪列表失败", e);
            res.code = 500;
            res.data = "查询失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 新增物流追踪
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增物流追踪")
    public BaseResponseInfo addLogisticsTracking(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = logisticsTrackingService.addLogisticsTracking(params, request);
            if (success) {
                res.code = 200;
                res.data = "新增成功";
            } else {
                res.code = 500;
                res.data = "新增失败";
            }
        } catch (Exception e) {
            logger.error("新增物流追踪失败", e);
            res.code = 500;
            res.data = "新增失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 更新物流追踪
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "更新物流追踪")
    public BaseResponseInfo updateLogisticsTracking(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = logisticsTrackingService.updateLogisticsTracking(params, request);
            if (success) {
                res.code = 200;
                res.data = "更新成功";
            } else {
                res.code = 500;
                res.data = "更新失败";
            }
        } catch (Exception e) {
            logger.error("更新物流追踪失败", e);
            res.code = 500;
            res.data = "更新失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 删除物流追踪
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除物流追踪")
    public BaseResponseInfo deleteLogisticsTracking(@RequestParam("id") Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = logisticsTrackingService.deleteLogisticsTracking(id, request);
            if (success) {
                res.code = 200;
                res.data = "删除成功";
            } else {
                res.code = 500;
                res.data = "删除失败";
            }
        } catch (Exception e) {
            logger.error("删除物流追踪失败", e);
            res.code = 500;
            res.data = "删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 批量删除物流追踪
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除物流追踪")
    public BaseResponseInfo deleteBatchLogisticsTracking(@RequestParam("ids") String ids, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = logisticsTrackingService.deleteBatchLogisticsTracking(ids, request);
            if (success) {
                res.code = 200;
                res.data = "批量删除成功";
            } else {
                res.code = 500;
                res.data = "批量删除失败";
            }
        } catch (Exception e) {
            logger.error("批量删除物流追踪失败", e);
            res.code = 500;
            res.data = "批量删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 更新物流状态
     */
    @PostMapping(value = "/updateStatus")
    @ApiOperation(value = "更新物流状态")
    public BaseResponseInfo updateLogisticsStatus(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = logisticsTrackingService.updateLogisticsStatus(params, request);
            if (success) {
                res.code = 200;
                res.data = "状态更新成功";
            } else {
                res.code = 500;
                res.data = "状态更新失败";
            }
        } catch (Exception e) {
            logger.error("更新物流状态失败", e);
            res.code = 500;
            res.data = "状态更新失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取物流详情
     */
    @GetMapping(value = "/detail/{id}")
    @ApiOperation(value = "获取物流详情")
    public BaseResponseInfo getLogisticsDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject detail = logisticsTrackingService.getLogisticsDetail(id, request);
            res.code = 200;
            res.data = detail;
        } catch (Exception e) {
            logger.error("获取物流详情失败", e);
            res.code = 500;
            res.data = "获取详情失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取物流轨迹
     */
    @GetMapping(value = "/trace/{trackingNumber}")
    @ApiOperation(value = "获取物流轨迹")
    public BaseResponseInfo getLogisticsTrace(@PathVariable("trackingNumber") String trackingNumber, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject trace = logisticsTrackingService.getLogisticsTrace(trackingNumber, request);
            res.code = 200;
            res.data = trace;
        } catch (Exception e) {
            logger.error("获取物流轨迹失败", e);
            res.code = 500;
            res.data = "获取轨迹失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 导出Excel
     */
    @GetMapping(value = "/exportXls")
    @ApiOperation(value = "导出Excel")
    public void exportExcel(
            @RequestParam(name = "trackingNumber", required = false) String trackingNumber,
            @RequestParam(name = "orderNumber", required = false) String orderNumber,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "logisticsCompany", required = false) String logisticsCompany,
            @RequestParam(name = "recipientName", required = false) String recipientName,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request, HttpServletResponse response) {
        
        try {
            JSONObject params = new JSONObject();
            params.put("trackingNumber", trackingNumber);
            params.put("orderNumber", orderNumber);
            params.put("status", status);
            params.put("logisticsCompany", logisticsCompany);
            params.put("recipientName", recipientName);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            logisticsTrackingService.exportExcel(params, request, response);
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
        }
    }
    
    /**
     * 获取物流统计
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取物流统计")
    public BaseResponseInfo getLogisticsStatistics(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject statistics = logisticsTrackingService.getLogisticsStatistics(request);
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error("获取物流统计失败", e);
            res.code = 500;
            res.data = "获取统计失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 批量更新物流状态
     */
    @PostMapping(value = "/batchUpdateStatus")
    @ApiOperation(value = "批量更新物流状态")
    public BaseResponseInfo batchUpdateLogisticsStatus(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = logisticsTrackingService.batchUpdateLogisticsStatus(params, request);
            if (success) {
                res.code = 200;
                res.data = "批量更新状态成功";
            } else {
                res.code = 500;
                res.data = "批量更新状态失败";
            }
        } catch (Exception e) {
            logger.error("批量更新物流状态失败", e);
            res.code = 500;
            res.data = "批量更新状态失败：" + e.getMessage();
        }
        return res;
    }
}
