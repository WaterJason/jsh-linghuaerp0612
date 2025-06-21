package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.datasource.entities.WorkOrder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 工单管理服务接口
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface WorkOrderService {

    /**
     * 根据ID获取工单
     */
    WorkOrder getWorkOrder(Long id) throws Exception;

    /**
     * 根据工单号获取工单
     */
    WorkOrder getWorkOrderByNumber(String workOrderNo) throws Exception;

    /**
     * 根据条件查询工单列表
     */
    List<WorkOrder> select(String workOrderNo, String productionOrderId, String workType, 
                          String status, String handlerId, String beginTime, String endTime) throws Exception;

    /**
     * 新增工单
     */
    int insertWorkOrder(JSONObject obj, HttpServletRequest request) throws Exception;

    /**
     * 更新工单
     */
    int updateWorkOrder(JSONObject obj, HttpServletRequest request) throws Exception;

    /**
     * 删除工单
     */
    int deleteWorkOrder(Long id, HttpServletRequest request) throws Exception;

    /**
     * 获取看板数据
     * 返回状态为'待生产'、'生产中'和'待入库'的工单数据
     */
    Map<String, Object> getKanbanData() throws Exception;

    /**
     * 查询看板工单列表 - 关联产品和用户信息
     * 返回结果包含工单ID、工单号、产品名称、产品图片URL、负责人姓名和当前状态
     */
    List<Map<String, Object>> getWorkOrderListForKanban(String status, String type) throws Exception;

    /**
     * 派单
     */
    int assignWorkOrder(Long id, Long handlerId, String handlerName, HttpServletRequest request) throws Exception;

    /**
     * 完工
     */
    int completeWorkOrder(Long id, String completeImages, String qualityNotes, HttpServletRequest request) throws Exception;

    /**
     * 更新工单状态
     */
    int updateStatus(Long id, String status, HttpServletRequest request) throws Exception;

    /**
     * 获取工单统计信息
     */
    Map<String, Object> getStatistics() throws Exception;

    /**
     * 根据生产订单ID查询工单列表
     */
    List<WorkOrder> getWorkOrdersByProductionOrderId(Long productionOrderId) throws Exception;

    /**
     * 根据处理人ID查询工单列表
     */
    List<WorkOrder> getWorkOrdersByHandlerId(Long handlerId) throws Exception;

    /**
     * 根据工单类型查询工单列表
     */
    List<WorkOrder> getWorkOrdersByWorkType(String workType) throws Exception;

    /**
     * 批量删除工单
     */
    int batchDeleteWorkOrder(String ids, HttpServletRequest request) throws Exception;

    /**
     * 生成工单号
     */
    String generateWorkOrderNo() throws Exception;

    /**
     * 检查工单号是否存在
     */
    boolean checkWorkOrderNoExists(String workOrderNo) throws Exception;

    /**
     * 更新工单成本
     */
    int updateCost(Long id, String cost, String laborCost, String materialCost, HttpServletRequest request) throws Exception;

    /**
     * 更新工单工时
     */
    int updateWorkHours(Long id, String actualHours, HttpServletRequest request) throws Exception;
}
