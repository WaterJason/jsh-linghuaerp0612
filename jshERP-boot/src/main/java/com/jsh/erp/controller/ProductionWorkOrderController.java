package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.datasource.entities.ProductionWorkOrder;
import com.jsh.erp.datasource.entities.ProductionWorkOrderEx;
import com.jsh.erp.service.ProductionWorkOrderService;
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
 * 生产工单Controller
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/productionWorkOrder")
@Api(tags = {"生产工单管理"})
public class ProductionWorkOrderController extends BaseController {
    
    private Logger logger = LoggerFactory.getLogger(ProductionWorkOrderController.class);

    @Resource
    private ProductionWorkOrderService productionWorkOrderService;

    /**
     * 根据ID获取生产工单信息
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取生产工单信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        ProductionWorkOrder workOrder = productionWorkOrderService.getProductionWorkOrder(id);
        Map<String, Object> objectMap = new HashMap<>();
        if (workOrder != null) {
            objectMap.put("info", workOrder);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 根据ID获取生产工单详情（包含关联信息）
     */
    @GetMapping(value = "/detail")
    @ApiOperation(value = "根据id获取生产工单详情")
    public BaseResponseInfo getDetail(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            ProductionWorkOrderEx workOrder = productionWorkOrderService.getProductionWorkOrderDetail(id);
            res.code = 200;
            res.data = workOrder;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取数据失败";
        }
        return res;
    }

    /**
     * 获取生产工单列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取生产工单列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        // 解析查询参数
        String workOrderNumber = StringUtil.getInfo(search, "workOrderNumber");
        String workOrderName = StringUtil.getInfo(search, "workOrderName");
        String productName = StringUtil.getInfo(search, "productName");
        String productionMode = StringUtil.getInfo(search, "productionMode");
        String productionType = StringUtil.getInfo(search, "productionType");
        String priority = StringUtil.getInfo(search, "priority");
        String status = StringUtil.getInfo(search, "status");
        String responsiblePerson = StringUtil.getInfo(search, "responsiblePerson");
        String workshopName = StringUtil.getInfo(search, "workshopName");
        String sourceType = StringUtil.getInfo(search, "sourceType");
        String sourceNumber = StringUtil.getInfo(search, "sourceNumber");
        
        // 解析时间参数
        Date planStartTime = null;
        Date planEndTime = null;
        String planStartTimeStr = StringUtil.getInfo(search, "planStartTime");
        String planEndTimeStr = StringUtil.getInfo(search, "planEndTime");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (StringUtil.isNotEmpty(planStartTimeStr)) {
            planStartTime = sdf.parse(planStartTimeStr);
        }
        if (StringUtil.isNotEmpty(planEndTimeStr)) {
            planEndTime = sdf.parse(planEndTimeStr);
        }
        
        // 分页参数
        Integer offset = StringUtil.parseInteger(StringUtil.getInfo(search, "offset"));
        Integer rows = StringUtil.parseInteger(StringUtil.getInfo(search, "rows"));
        
        List<ProductionWorkOrderEx> list = productionWorkOrderService.select(
            workOrderNumber, workOrderName, productName, productionMode, productionType,
            priority, status, responsiblePerson, workshopName, planStartTime, planEndTime,
            sourceType, sourceNumber, offset, rows);
        
        return getDataTable(list);
    }

    /**
     * 新增生产工单
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增生产工单")
    public String addWorkOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = productionWorkOrderService.insertProductionWorkOrder(obj, request);
        return returnStr(objectMap, insert);
    }

    /**
     * 修改生产工单
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "修改生产工单")
    public String updateWorkOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionWorkOrderService.updateProductionWorkOrder(obj, request);
        return returnStr(objectMap, update);
    }

    /**
     * 删除生产工单
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除生产工单")
    public String deleteWorkOrder(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = productionWorkOrderService.deleteProductionWorkOrder(id, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 批量删除生产工单
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除生产工单")
    public String batchDeleteWorkOrder(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = productionWorkOrderService.batchDeleteProductionWorkOrder(ids, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 检查工单编号是否存在
     */
    @GetMapping(value = "/checkWorkOrderNumber")
    @ApiOperation(value = "检查工单编号是否存在")
    public BaseResponseInfo checkWorkOrderNumber(@RequestParam(value = "id", required = false) Long id,
                                                 @RequestParam("workOrderNumber") String workOrderNumber,
                                                 HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            int exist = productionWorkOrderService.checkWorkOrderNumberExist(id, workOrderNumber);
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
     * 更新工单状态
     */
    @PutMapping(value = "/updateStatus")
    @ApiOperation(value = "更新工单状态")
    public String updateWorkOrderStatus(@RequestParam("id") Long id,
                                       @RequestParam("status") String status,
                                       HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionWorkOrderService.updateWorkOrderStatus(id, status, request);
        return returnStr(objectMap, update);
    }

    /**
     * 更新工单实际数量
     */
    @PutMapping(value = "/updateActualQuantity")
    @ApiOperation(value = "更新工单实际数量")
    public String updateActualQuantity(@RequestParam("id") Long id,
                                      @RequestParam("actualQuantity") BigDecimal actualQuantity,
                                      HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = productionWorkOrderService.updateActualQuantity(id, actualQuantity, request);
        return returnStr(objectMap, update);
    }

    /**
     * 获取工单统计信息
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取工单统计信息")
    public BaseResponseInfo getWorkOrderStatistics(@RequestParam(value = "startDate", required = false) String startDateStr,
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
            
            Map<String, Object> statistics = productionWorkOrderService.getWorkOrderStatistics(startDate, endDate);
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
     * 获取即将到期的工单列表
     */
    @GetMapping(value = "/expiring")
    @ApiOperation(value = "获取即将到期的工单列表")
    public BaseResponseInfo getExpiringWorkOrders(@RequestParam(value = "days", defaultValue = "3") Integer days,
                                                  HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ProductionWorkOrderEx> list = productionWorkOrderService.getExpiringWorkOrders(days);
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
     * 获取超期的工单列表
     */
    @GetMapping(value = "/overdue")
    @ApiOperation(value = "获取超期的工单列表")
    public BaseResponseInfo getOverdueWorkOrders(HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ProductionWorkOrderEx> list = productionWorkOrderService.getOverdueWorkOrders();
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
