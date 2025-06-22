package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.datasource.entities.ProductionTask;
import com.jsh.erp.datasource.entities.ProductionTaskEx;
import com.jsh.erp.service.ProductionTaskService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnStr;

/**
 * 生产任务Controller
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/productionTask")
@Api(tags = {"生产任务管理"})
public class ProductionTaskController extends BaseController {
    
    private Logger logger = LoggerFactory.getLogger(ProductionTaskController.class);

    @Resource
    private ProductionTaskService productionTaskService;

    /**
     * 根据ID获取生产任务信息
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取生产任务信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        ProductionTask task = productionTaskService.getProductionTask(id);
        Map<String, Object> objectMap = new HashMap<>();
        if (task != null) {
            objectMap.put("info", task);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 根据ID获取生产任务详情（包含关联信息）
     */
    @GetMapping(value = "/detail")
    @ApiOperation(value = "根据id获取生产任务详情")
    public BaseResponseInfo getDetail(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            ProductionTaskEx task = productionTaskService.getProductionTaskDetail(id);
            res.code = 200;
            res.data = task;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取生产任务列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取生产任务列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        // 解析查询参数
        String taskNumber = StringUtil.getInfo(search, "taskNumber");
        String taskName = StringUtil.getInfo(search, "taskName");
        String workOrderNumber = StringUtil.getInfo(search, "workOrderNumber");
        String taskType = StringUtil.getInfo(search, "taskType");
        String processStep = StringUtil.getInfo(search, "processStep");
        String productName = StringUtil.getInfo(search, "productName");
        String priority = StringUtil.getInfo(search, "priority");
        String status = StringUtil.getInfo(search, "status");
        String workerName = StringUtil.getInfo(search, "workerName");
        String workerSpecialty = StringUtil.getInfo(search, "workerSpecialty");
        
        // 解析时间参数
        Date assignTime = null;
        Date planStartTime = null;
        Date planEndTime = null;
        String assignTimeStr = StringUtil.getInfo(search, "assignTime");
        String planStartTimeStr = StringUtil.getInfo(search, "planStartTime");
        String planEndTimeStr = StringUtil.getInfo(search, "planEndTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtil.isNotEmpty(assignTimeStr)) {
            assignTime = sdf.parse(assignTimeStr);
        }
        if (StringUtil.isNotEmpty(planStartTimeStr)) {
            planStartTime = sdf.parse(planStartTimeStr);
        }
        if (StringUtil.isNotEmpty(planEndTimeStr)) {
            planEndTime = sdf.parse(planEndTimeStr);
        }
        
        // 分页参数
        Integer offset = StringUtil.parseInteger(StringUtil.getInfo(search, "offset"));
        Integer rows = StringUtil.parseInteger(StringUtil.getInfo(search, "rows"));
        
        List<ProductionTaskEx> list = productionTaskService.select(
            taskNumber, taskName, workOrderNumber, taskType, processStep, productName,
            priority, status, workerName, workerSpecialty, assignTime, planStartTime,
            planEndTime, offset, rows);
        
        return getDataTable(list);
    }

    /**
     * 新增生产任务
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增生产任务")
    public String addTask(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = productionTaskService.insertProductionTask(obj, request);
        return returnStr(objectMap, insert);
    }

    /**
     * 修改生产任务
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "修改生产任务")
    public String updateTask(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionTaskService.updateProductionTask(obj, request);
        return returnStr(objectMap, update);
    }

    /**
     * 删除生产任务
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除生产任务")
    public String deleteTask(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = productionTaskService.deleteProductionTask(id, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 批量删除生产任务
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除生产任务")
    public String batchDeleteTask(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = productionTaskService.batchDeleteProductionTask(ids, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 检查任务编号是否存在
     */
    @GetMapping(value = "/checkTaskNumber")
    @ApiOperation(value = "检查任务编号是否存在")
    public BaseResponseInfo checkTaskNumber(@RequestParam(value = "id", required = false) Long id,
                                           @RequestParam("taskNumber") String taskNumber,
                                           HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            int exist = productionTaskService.checkTaskNumberExist(id, taskNumber);
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("status", exist > 0);
            res.code = 200;
            res.data = objectMap;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "检查失败";
        }
        return res;
    }

    /**
     * 分配任务给工人
     */
    @PutMapping(value = "/assign")
    @ApiOperation(value = "分配任务给工人")
    public String assignTaskToWorker(@RequestParam("taskId") Long taskId,
                                    @RequestParam("workerId") Long workerId,
                                    @RequestParam("workerName") String workerName,
                                    HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionTaskService.assignTaskToWorker(taskId, workerId, workerName, request);
        return returnStr(objectMap, update);
    }

    /**
     * 开始任务
     */
    @PutMapping(value = "/start")
    @ApiOperation(value = "开始任务")
    public String startTask(@RequestParam("taskId") Long taskId, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionTaskService.startTask(taskId, request);
        return returnStr(objectMap, update);
    }

    /**
     * 完成任务
     */
    @PutMapping(value = "/complete")
    @ApiOperation(value = "完成任务")
    public String completeTask(@RequestParam("taskId") Long taskId,
                              @RequestParam("completedQuantity") BigDecimal completedQuantity,
                              @RequestParam("qualifiedQuantity") BigDecimal qualifiedQuantity,
                              @RequestParam("defectiveQuantity") BigDecimal defectiveQuantity,
                              @RequestParam("actualHours") BigDecimal actualHours,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionTaskService.completeTask(taskId, completedQuantity, qualifiedQuantity, defectiveQuantity, actualHours, request);
        return returnStr(objectMap, update);
    }

    /**
     * 获取可领取的任务列表
     */
    @GetMapping(value = "/available")
    @ApiOperation(value = "获取可领取的任务列表")
    public BaseResponseInfo getAvailableTasks(@RequestParam(value = "taskType", required = false) String taskType,
                                             @RequestParam(value = "priority", required = false) String priority,
                                             HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ProductionTaskEx> list = productionTaskService.getAvailableTasks(taskType, priority);
            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取工人的任务列表
     */
    @GetMapping(value = "/worker")
    @ApiOperation(value = "获取工人的任务列表")
    public BaseResponseInfo getWorkerTasks(@RequestParam("workerId") Long workerId,
                                          @RequestParam(value = "status", required = false) String status,
                                          HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ProductionTaskEx> list = productionTaskService.getWorkerTasks(workerId, status);
            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取任务统计信息
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取任务统计信息")
    public BaseResponseInfo getTaskStatistics(@RequestParam(value = "startDate", required = false) String startDateStr,
                                              @RequestParam(value = "endDate", required = false) String endDateStr,
                                              HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Date startDate = null;
            Date endDate = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            if (StringUtil.isNotEmpty(startDateStr)) {
                startDate = sdf.parse(startDateStr);
            }
            if (StringUtil.isNotEmpty(endDateStr)) {
                endDate = sdf.parse(endDateStr);
            }
            
            Map<String, Object> statistics = productionTaskService.getTaskStatistics(startDate, endDate);
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取统计数据失败";
        }
        return res;
    }

    /**
     * 获取即将到期的任务列表
     */
    @GetMapping(value = "/expiring")
    @ApiOperation(value = "获取即将到期的任务列表")
    public BaseResponseInfo getExpiringTasks(@RequestParam(value = "days", defaultValue = "3") Integer days,
                                            HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ProductionTaskEx> list = productionTaskService.getExpiringTasks(days);
            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取超期的任务列表
     */
    @GetMapping(value = "/overdue")
    @ApiOperation(value = "获取超期的任务列表")
    public BaseResponseInfo getOverdueTasks(HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ProductionTaskEx> list = productionTaskService.getOverdueTasks();
            res.code = 200;
            res.data = list;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }
}
