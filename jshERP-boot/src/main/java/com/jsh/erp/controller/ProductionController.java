package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.datasource.entities.ProductionOrder;
import com.jsh.erp.service.ProductionService;
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
 * 生产管理控制器
 * 
 * @author jshERP
 * @date 2025-06-21
 */
@RestController
@RequestMapping(value = "/production")
@Api(tags = {"生产管理"})
public class ProductionController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ProductionController.class);

    @Resource
    private ProductionService productionService;

    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取生产订单信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        ProductionOrder productionOrder = productionService.getProductionOrder(id);
        Map<String, Object> objectMap = new HashMap<>();
        if(productionOrder != null) {
            objectMap.put("info", productionOrder);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "获取生产订单列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        String orderNo = StringUtil.getInfo(search, "orderNo");
        String status = StringUtil.getInfo(search, "status");
        String materialId = StringUtil.getInfo(search, "materialId");
        String salesOrderId = StringUtil.getInfo(search, "salesOrderId");
        String beginTime = StringUtil.getInfo(search, "beginTime");
        String endTime = StringUtil.getInfo(search, "endTime");
        
        List<ProductionOrder> list = productionService.select(orderNo, status, materialId, salesOrderId, beginTime, endTime);
        return getDataTable(list);
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增生产订单")
    public String addProductionOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = productionService.insertProductionOrder(obj, request);
        return returnStr(objectMap, result);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "更新生产订单")
    public String updateProductionOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = productionService.updateProductionOrder(obj, request);
        return returnStr(objectMap, result);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除生产订单")
    public String deleteProductionOrder(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = productionService.deleteProductionOrder(id, request);
        return returnStr(objectMap, result);
    }

    @PostMapping(value = "/generateFromOrder")
    @ApiOperation(value = "智能生成工单 - 从销售订单生成生产订单")
    public String generateFromOrder(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Long salesOrderId = obj.getLong("salesOrderId");
            if (salesOrderId == null) {
                objectMap.put("message", "销售订单ID不能为空");
                return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
            
            // 调用智能生成工单服务
            Map<String, Object> result = productionService.generateFromSalesOrder(salesOrderId, request);
            objectMap.putAll(result);
            
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("智能生成工单失败", e);
            objectMap.put("message", "智能生成工单失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @PostMapping(value = "/updateStatus")
    @ApiOperation(value = "更新生产订单状态")
    public String updateStatus(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Long id = obj.getLong("id");
            String status = obj.getString("status");
            
            if (id == null || StringUtil.isEmpty(status)) {
                objectMap.put("message", "参数不能为空");
                return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
            
            int result = productionService.updateStatus(id, status, request);
            return returnStr(objectMap, result);
        } catch (Exception e) {
            logger.error("更新生产订单状态失败", e);
            objectMap.put("message", "更新状态失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @PostMapping(value = "/updateProgress")
    @ApiOperation(value = "更新生产订单进度")
    public String updateProgress(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Long id = obj.getLong("id");
            String progress = obj.getString("progress");
            
            if (id == null || StringUtil.isEmpty(progress)) {
                objectMap.put("message", "参数不能为空");
                return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
            
            int result = productionService.updateProgress(id, progress, request);
            return returnStr(objectMap, result);
        } catch (Exception e) {
            logger.error("更新生产订单进度失败", e);
            objectMap.put("message", "更新进度失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取生产统计信息")
    public String getStatistics(HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            Map<String, Object> statistics = productionService.getStatistics();
            objectMap.putAll(statistics);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("获取生产统计信息失败", e);
            objectMap.put("message", "获取统计信息失败：" + e.getMessage());
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/orderByNumber")
    @ApiOperation(value = "根据订单号获取生产订单")
    public String getOrderByNumber(@RequestParam("orderNo") String orderNo, HttpServletRequest request) throws Exception {
        ProductionOrder productionOrder = productionService.getProductionOrderByNumber(orderNo);
        Map<String, Object> objectMap = new HashMap<>();
        if(productionOrder != null) {
            objectMap.put("info", productionOrder);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}
