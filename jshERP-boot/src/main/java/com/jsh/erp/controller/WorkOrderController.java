package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.datasource.entities.WorkOrder;
import com.jsh.erp.service.WorkOrderService;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnStr;

/**
 * 工单管理控制器
 * 
 * @author jshERP
 * @date 2025-06-21
 */
@RestController
@RequestMapping(value = "/workOrder")
@Api(tags = {"工单管理"})
public class WorkOrderController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(WorkOrderController.class);

    @Resource
    private WorkOrderService workOrderService;

    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取工单信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        WorkOrder workOrder = workOrderService.getWorkOrder(id);
        Map<String, Object> objectMap = new HashMap<>();
        if(workOrder != null) {
            objectMap.put("info", workOrder);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "获取工单列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        String workOrderNo = StringUtil.getInfo(search, "workOrderNo");
        String productionOrderId = StringUtil.getInfo(search, "productionOrderId");
        String workType = StringUtil.getInfo(search, "workType");
        String status = StringUtil.getInfo(search, "status");
        String handlerId = StringUtil.getInfo(search, "handlerId");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        
        List<WorkOrder> list = workOrderService.select(workOrderNo, productionOrderId, workType, status, handlerId, beginTime, endTime);
        return getDataTable(list);
    }

    @GetMapping(value = "/kanbanData")
    @ApiOperation(value = "获取看板数据")
    public String getKanbanData(HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Map<String, Object> kanbanData = workOrderService.getKanbanData();
            objectMap.putAll(kanbanData);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("获取看板数据失败", e);
            objectMap.put("message", "获取看板数据失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/kanbanList")
    @ApiOperation(value = "获取看板工单列表 - 包含产品和用户信息")
    public String getKanbanList(@RequestParam(value = "status", required = false) String status,
                               @RequestParam(value = "type", required = false) String type,
                               HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            List<Map<String, Object>> list = workOrderService.getWorkOrderListForKanban(status, type);
            objectMap.put("rows", list);
            objectMap.put("total", list.size());
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("获取看板工单列表失败", e);
            objectMap.put("message", "获取看板工单列表失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增工单")
    public String addWorkOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = workOrderService.insertWorkOrder(obj, request);
        return returnStr(objectMap, result);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "更新工单")
    public String updateWorkOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = workOrderService.updateWorkOrder(obj, request);
        return returnStr(objectMap, result);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除工单")
    public String deleteWorkOrder(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = workOrderService.deleteWorkOrder(id, request);
        return returnStr(objectMap, result);
    }

    @PostMapping(value = "/assign")
    @ApiOperation(value = "派单")
    public String assignWorkOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Long id = obj.getLong("id");
            Long handlerId = obj.getLong("handlerId");
            String handlerName = obj.getString("handlerName");
            
            if (id == null || handlerId == null) {
                objectMap.put("message", "参数不能为空");
                return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
            
            int result = workOrderService.assignWorkOrder(id, handlerId, handlerName, request);
            return returnStr(objectMap, result);
        } catch (Exception e) {
            logger.error("派单失败", e);
            objectMap.put("message", "派单失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @PostMapping(value = "/complete")
    @ApiOperation(value = "完工")
    public String completeWorkOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Long id = obj.getLong("id");
            String completeImages = obj.getString("completeImages");
            String qualityNotes = obj.getString("qualityNotes");
            
            if (id == null) {
                objectMap.put("message", "工单ID不能为空");
                return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
            
            int result = workOrderService.completeWorkOrder(id, completeImages, qualityNotes, request);
            return returnStr(objectMap, result);
        } catch (Exception e) {
            logger.error("完工操作失败", e);
            objectMap.put("message", "完工操作失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @PostMapping(value = "/updateStatus")
    @ApiOperation(value = "更新工单状态")
    public String updateStatus(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Long id = obj.getLong("id");
            String status = obj.getString("status");
            
            if (id == null || StringUtil.isEmpty(status)) {
                objectMap.put("message", "参数不能为空");
                return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
            
            int result = workOrderService.updateStatus(id, status, request);
            return returnStr(objectMap, result);
        } catch (Exception e) {
            logger.error("更新工单状态失败", e);
            objectMap.put("message", "更新状态失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/orderByNumber")
    @ApiOperation(value = "根据工单号获取工单")
    public String getOrderByNumber(@RequestParam("workOrderNo") String workOrderNo, HttpServletRequest request) throws Exception {
        WorkOrder workOrder = workOrderService.getWorkOrderByNumber(workOrderNo);
        Map<String, Object> objectMap = new HashMap<>();
        if(workOrder != null) {
            objectMap.put("info", workOrder);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取工单统计信息")
    public String getStatistics(HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Map<String, Object> statistics = workOrderService.getStatistics();
            objectMap.putAll(statistics);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("获取工单统计信息失败", e);
            objectMap.put("message", "获取统计信息失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
