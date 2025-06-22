package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionWorkOrder;
import com.jsh.erp.datasource.entities.ProductionWorkOrderEx;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产工单扩展Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionWorkOrderMapperEx {

    /**
     * 根据条件查询生产工单列表
     */
    List<ProductionWorkOrderEx> selectByConditionWorkOrder(
            @Param("workOrderNumber") String workOrderNumber,
            @Param("workOrderName") String workOrderName,
            @Param("productName") String productName,
            @Param("productionMode") String productionMode,
            @Param("productionType") String productionType,
            @Param("priority") String priority,
            @Param("status") String status,
            @Param("responsiblePerson") String responsiblePerson,
            @Param("workshopName") String workshopName,
            @Param("planStartTime") Date planStartTime,
            @Param("planEndTime") Date planEndTime,
            @Param("sourceType") String sourceType,
            @Param("sourceNumber") String sourceNumber,
            @Param("tenantId") Long tenantId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    /**
     * 根据条件统计生产工单数量
     */
    Long countByConditionWorkOrder(
            @Param("workOrderNumber") String workOrderNumber,
            @Param("workOrderName") String workOrderName,
            @Param("productName") String productName,
            @Param("productionMode") String productionMode,
            @Param("productionType") String productionType,
            @Param("priority") String priority,
            @Param("status") String status,
            @Param("responsiblePerson") String responsiblePerson,
            @Param("workshopName") String workshopName,
            @Param("planStartTime") Date planStartTime,
            @Param("planEndTime") Date planEndTime,
            @Param("sourceType") String sourceType,
            @Param("sourceNumber") String sourceNumber,
            @Param("tenantId") Long tenantId);

    /**
     * 根据ID查询工单详情（包含关联信息）
     */
    ProductionWorkOrderEx findById(@Param("id") Long id);

    /**
     * 根据工单编号查询工单
     */
    ProductionWorkOrder findByWorkOrderNumber(@Param("workOrderNumber") String workOrderNumber, @Param("tenantId") Long tenantId);

    /**
     * 批量删除生产工单
     */
    int batchDeleteWorkOrderByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String[] ids);

    /**
     * 根据产品ID查询相关工单
     */
    List<ProductionWorkOrder> getWorkOrderListByProductIds(@Param("productIds") String[] productIds);

    /**
     * 根据来源信息查询工单
     */
    List<ProductionWorkOrder> getWorkOrderListBySource(@Param("sourceType") String sourceType, @Param("sourceId") Long sourceId);

    /**
     * 更新工单状态
     */
    int updateWorkOrderStatus(@Param("id") Long id, @Param("status") String status, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 更新工单实际数量
     */
    int updateActualQuantity(@Param("id") Long id, @Param("actualQuantity") java.math.BigDecimal actualQuantity, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 更新工单实际成本
     */
    int updateActualCost(@Param("id") Long id, @Param("actualCost") java.math.BigDecimal actualCost, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 更新工单实际工时
     */
    int updateActualHours(@Param("id") Long id, @Param("actualHours") java.math.BigDecimal actualHours, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 更新工单实际时间
     */
    int updateActualTime(@Param("id") Long id, @Param("actualStartTime") Date actualStartTime, @Param("actualEndTime") Date actualEndTime, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 检查工单编号是否存在
     */
    int checkWorkOrderNumberExist(@Param("id") Long id, @Param("workOrderNumber") String workOrderNumber, @Param("tenantId") Long tenantId);

    /**
     * 获取工单统计信息
     */
    Map<String, Object> getWorkOrderStatistics(@Param("tenantId") Long tenantId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 根据状态统计工单数量
     */
    List<Map<String, Object>> getWorkOrderCountByStatus(@Param("tenantId") Long tenantId);

    /**
     * 根据优先级统计工单数量
     */
    List<Map<String, Object>> getWorkOrderCountByPriority(@Param("tenantId") Long tenantId);

    /**
     * 根据制作模式统计工单数量
     */
    List<Map<String, Object>> getWorkOrderCountByProductionMode(@Param("tenantId") Long tenantId);

    /**
     * 获取即将到期的工单列表
     */
    List<ProductionWorkOrderEx> getExpiringWorkOrders(@Param("tenantId") Long tenantId, @Param("days") Integer days);

    /**
     * 获取超期的工单列表
     */
    List<ProductionWorkOrderEx> getOverdueWorkOrders(@Param("tenantId") Long tenantId);

    /**
     * 导出工单数据
     */
    List<ProductionWorkOrderEx> exportWorkOrders(
            @Param("workOrderNumber") String workOrderNumber,
            @Param("workOrderName") String workOrderName,
            @Param("productName") String productName,
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("tenantId") Long tenantId);

    /**
     * 获取工单编号列表（用于下拉选择）
     */
    List<String> getWorkOrderNumberList(@Param("tenantId") Long tenantId);

    /**
     * 获取负责人列表（用于下拉选择）
     */
    List<String> getResponsiblePersonList(@Param("tenantId") Long tenantId);

    /**
     * 获取车间名称列表（用于下拉选择）
     */
    List<String> getWorkshopNameList(@Param("tenantId") Long tenantId);
}
