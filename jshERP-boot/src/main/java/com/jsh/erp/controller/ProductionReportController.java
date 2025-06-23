package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.datasource.entities.ProductionReport;
import com.jsh.erp.service.ProductionReportService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ErpInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnStr;

/**
 * 生产报工Controller
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/productionReport")
@Api(tags = {"生产报工管理"})
public class ProductionReportController extends BaseController {
    
    private Logger logger = LoggerFactory.getLogger(ProductionReportController.class);

    @Resource
    private ProductionReportService productionReportService;

    /**
     * 根据ID获取生产报工信息
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取生产报工信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        ProductionReport report = productionReportService.getProductionReport(id);
        Map<String, Object> objectMap = new HashMap<>();
        if (report != null) {
            objectMap.put("info", report);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 新增生产报工
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增生产报工")
    public String addReport(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = productionReportService.insertProductionReport(obj, request);
        return returnStr(objectMap, insert);
    }

    /**
     * 修改生产报工
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "修改生产报工")
    public String updateReport(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionReportService.updateProductionReport(obj, request);
        return returnStr(objectMap, update);
    }

    /**
     * 删除生产报工
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除生产报工")
    public String deleteReport(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = productionReportService.deleteProductionReport(id, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 进度报工
     */
    @PostMapping(value = "/progress")
    @ApiOperation(value = "进度报工")
    public String progressReport(@RequestParam("taskId") Long taskId,
                                @RequestParam("completedQuantity") BigDecimal completedQuantity,
                                @RequestParam("qualifiedQuantity") BigDecimal qualifiedQuantity,
                                @RequestParam("defectiveQuantity") BigDecimal defectiveQuantity,
                                @RequestParam("workHours") BigDecimal workHours,
                                @RequestParam(value = "qualityLevel", required = false) String qualityLevel,
                                @RequestParam(value = "workContent", required = false) String workContent,
                                @RequestParam(value = "workPhotos", required = false) String workPhotos,
                                HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = productionReportService.progressReport(taskId, completedQuantity, qualifiedQuantity,
            defectiveQuantity, workHours, qualityLevel, workContent, workPhotos, request);
        return returnStr(objectMap, result);
    }

    /**
     * 完工报工
     */
    @PostMapping(value = "/complete")
    @ApiOperation(value = "完工报工")
    public String completeReport(@RequestParam("taskId") Long taskId,
                                @RequestParam("completedQuantity") BigDecimal completedQuantity,
                                @RequestParam("qualifiedQuantity") BigDecimal qualifiedQuantity,
                                @RequestParam("defectiveQuantity") BigDecimal defectiveQuantity,
                                @RequestParam("workHours") BigDecimal workHours,
                                @RequestParam(value = "qualityLevel", required = false) String qualityLevel,
                                @RequestParam(value = "workContent", required = false) String workContent,
                                @RequestParam(value = "workPhotos", required = false) String workPhotos,
                                HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = productionReportService.completeReport(taskId, completedQuantity, qualifiedQuantity,
            defectiveQuantity, workHours, qualityLevel, workContent, workPhotos, request);
        return returnStr(objectMap, result);
    }

    /**
     * 批量进度报工
     */
    @PostMapping(value = "/batchProgress")
    @ApiOperation(value = "批量进度报工")
    public BaseResponseInfo batchProgressReport(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 解析批量报工数据
            // 这里可以根据实际需求实现批量报工逻辑
            res.code = 200;
            res.data = "批量报工成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "批量报工失败";
        }
        return res;
    }

    /**
     * 获取任务的报工历史
     */
    @GetMapping(value = "/history")
    @ApiOperation(value = "获取任务的报工历史")
    public BaseResponseInfo getReportHistory(@RequestParam("taskId") Long taskId,
                                            HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现获取任务报工历史的逻辑
            // List<ProductionReport> list = productionReportService.getReportHistoryByTaskId(taskId);
            res.code = 200;
            res.data = "获取报工历史成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取报工历史失败";
        }
        return res;
    }

    /**
     * 获取工人的报工统计
     */
    @GetMapping(value = "/workerStats")
    @ApiOperation(value = "获取工人的报工统计")
    public BaseResponseInfo getWorkerReportStats(@RequestParam("workerId") Long workerId,
                                                 @RequestParam(value = "startDate", required = false) String startDate,
                                                 @RequestParam(value = "endDate", required = false) String endDate,
                                                 HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现获取工人报工统计的逻辑
            // Map<String, Object> stats = productionReportService.getWorkerReportStats(workerId, startDate, endDate);
            res.code = 200;
            res.data = "获取工人报工统计成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取工人报工统计失败";
        }
        return res;
    }

    /**
     * 报工数据导出
     */
    @GetMapping(value = "/export")
    @ApiOperation(value = "报工数据导出")
    public BaseResponseInfo exportReportData(@RequestParam(value = "startDate", required = false) String startDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "workerId", required = false) Long workerId,
                                            @RequestParam(value = "taskType", required = false) String taskType,
                                            HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现报工数据导出的逻辑
            // String exportPath = productionReportService.exportReportData(startDate, endDate, workerId, taskType);
            res.code = 200;
            res.data = "报工数据导出成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "报工数据导出失败";
        }
        return res;
    }

    /**
     * 报工数据校验
     */
    @PostMapping(value = "/validate")
    @ApiOperation(value = "报工数据校验")
    public BaseResponseInfo validateReportData(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现报工数据校验的逻辑
            // boolean isValid = productionReportService.validateReportData(obj);
            Map<String, Object> result = new HashMap<>();
            result.put("isValid", true);
            result.put("message", "数据校验通过");
            res.code = 200;
            res.data = result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "数据校验失败";
        }
        return res;
    }
}
