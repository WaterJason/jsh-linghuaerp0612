package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.ProductionOrder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 生产管理服务接口
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface ProductionService {

    /**
     * 根据ID获取生产订单
     */
    ProductionOrder getProductionOrder(Long id) throws Exception;

    /**
     * 根据订单号获取生产订单
     */
    ProductionOrder getProductionOrderByNumber(String orderNo) throws Exception;

    /**
     * 根据条件查询生产订单列表
     */
    List<ProductionOrder> select(String orderNo, String status, String materialId, 
                                String salesOrderId, String beginTime, String endTime) throws Exception;

    /**
     * 新增生产订单
     */
    int insertProductionOrder(JSONObject obj, HttpServletRequest request) throws Exception;

    /**
     * 更新生产订单
     */
    int updateProductionOrder(JSONObject obj, HttpServletRequest request) throws Exception;

    /**
     * 删除生产订单
     */
    int deleteProductionOrder(Long id, HttpServletRequest request) throws Exception;

    /**
     * 智能生成工单 - 从销售订单生成生产订单
     * 核心业务逻辑：
     * 1. 在 jsh_production_order 表中创建一条新记录
     * 2. 通过查询 jsh_material 表，检查'底胎'的库存
     * 3. 通过调用项目中已有的采购模块服务，为'制作服务'创建一个采购订单
     * 4. 为'底胎'创建一个内部调拨单
     */
    Map<String, Object> generateFromSalesOrder(Long salesOrderId, HttpServletRequest request) throws Exception;

    /**
     * 更新生产订单状态
     */
    int updateStatus(Long id, String status, HttpServletRequest request) throws Exception;

    /**
     * 更新生产订单进度
     */
    int updateProgress(Long id, String progress, HttpServletRequest request) throws Exception;

    /**
     * 获取生产统计信息
     */
    Map<String, Object> getStatistics() throws Exception;

    /**
     * 根据销售订单ID查询生产订单列表
     */
    List<ProductionOrder> getProductionOrdersBySalesOrderId(Long salesOrderId) throws Exception;

    /**
     * 根据物料ID查询生产订单列表
     */
    List<ProductionOrder> getProductionOrdersByMaterialId(Long materialId) throws Exception;

    /**
     * 批量删除生产订单
     */
    int batchDeleteProductionOrder(String ids, HttpServletRequest request) throws Exception;

    /**
     * 生成生产订单号
     */
    String generateOrderNo() throws Exception;

    /**
     * 检查生产订单号是否存在
     */
    boolean checkOrderNoExists(String orderNo) throws Exception;

    /**
     * 获取即将到期的生产订单
     */
    List<ProductionOrder> getExpiringSoonOrders(Integer days) throws Exception;

    /**
     * 更新生产订单成本
     */
    int updateCost(Long id, String totalCost, String materialCost, String laborCost, HttpServletRequest request) throws Exception;
}
