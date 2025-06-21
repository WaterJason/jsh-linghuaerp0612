package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.WorkOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 工单物料消耗扩展Mapper接口
 * 用于自定义查询方法
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface WorkOrderItemMapperEx {

    /**
     * 根据条件查询工单物料消耗列表（分页）
     */
    List<WorkOrderItem> selectByCondition(
            @Param("workOrderId") Long workOrderId,
            @Param("materialId") Long materialId,
            @Param("itemType") String itemType,
            @Param("depotId") Long depotId,
            @Param("tenantId") Long tenantId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    /**
     * 根据条件统计工单物料消耗数量
     */
    Long countByCondition(
            @Param("workOrderId") Long workOrderId,
            @Param("materialId") Long materialId,
            @Param("itemType") String itemType,
            @Param("depotId") Long depotId,
            @Param("tenantId") Long tenantId);

    /**
     * 根据工单ID查询物料消耗列表
     */
    List<WorkOrderItem> selectByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("tenantId") Long tenantId);

    /**
     * 根据物料ID查询相关的工单消耗记录
     */
    List<WorkOrderItem> selectByMaterialId(@Param("materialId") Long materialId, @Param("tenantId") Long tenantId);

    /**
     * 根据物料类型查询工单消耗列表
     */
    List<WorkOrderItem> selectByItemType(@Param("itemType") String itemType, @Param("tenantId") Long tenantId);

    /**
     * 根据仓库ID查询工单消耗列表
     */
    List<WorkOrderItem> selectByDepotId(@Param("depotId") Long depotId, @Param("tenantId") Long tenantId);

    /**
     * 批量插入工单物料消耗记录
     */
    int batchInsert(@Param("list") List<WorkOrderItem> list);

    /**
     * 批量删除工单物料消耗记录（逻辑删除）
     */
    int batchDeleteByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String[] ids);

    /**
     * 根据工单ID批量删除物料消耗记录（逻辑删除）
     */
    int batchDeleteByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("updateTime") Date updateTime, @Param("updater") Long updater);

    /**
     * 统计工单的物料消耗总成本
     */
    Object getTotalCostByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("tenantId") Long tenantId);

    /**
     * 统计各物料类型的消耗情况
     */
    List<Object> getItemTypeStatistics(@Param("workOrderId") Long workOrderId, @Param("tenantId") Long tenantId);

    /**
     * 查询物料消耗统计（按时间范围）
     */
    List<Object> getMaterialConsumptionStatistics(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId);

    /**
     * 查询物料消耗明细（带物料信息）
     */
    List<Object> selectWithMaterialInfo(@Param("workOrderId") Long workOrderId, @Param("tenantId") Long tenantId);

    /**
     * 查询仓库物料消耗统计
     */
    List<Object> getDepotConsumptionStatistics(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId);

    /**
     * 更新物料消耗数量和价格
     */
    int updateQuantityAndPrice(@Param("id") Long id, @Param("quantity") String quantity, @Param("unitPrice") String unitPrice, 
                               @Param("totalPrice") String totalPrice, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 根据批次号查询物料消耗记录
     */
    List<WorkOrderItem> selectByBatchNumber(@Param("batchNumber") String batchNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据序列号查询物料消耗记录
     */
    List<WorkOrderItem> selectBySerialNumber(@Param("serialNumber") String serialNumber, @Param("tenantId") Long tenantId);
}
