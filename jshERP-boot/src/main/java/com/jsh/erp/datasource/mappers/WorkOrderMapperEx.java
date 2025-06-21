package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.WorkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工单扩展Mapper接口
 * 用于自定义查询方法
 *
 * @author jshERP
 * @date 2025-06-21
 */
public interface WorkOrderMapperEx {

    /**
     * 查询看板工单列表 - 关联产品和用户信息
     * 返回结果包含工单ID、工单号、产品名称、产品图片URL、负责人姓名和当前状态
     */
    List<Map<String, Object>> selectWorkOrderListForKanban(
            @Param("status") String status,
            @Param("type") String type,
            @Param("tenantId") Long tenantId);

    /**
     * 根据条件查询工单列表（分页）
     */
    List<WorkOrder> selectByCondition(
            @Param("workOrderNo") String workOrderNo,
            @Param("productionOrderId") Long productionOrderId,
            @Param("workType") String workType,
            @Param("status") String status,
            @Param("handlerId") Long handlerId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("tenantId") Long tenantId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    /**
     * 根据条件统计工单数量
     */
    Long countByCondition(
            @Param("workOrderNo") String workOrderNo,
            @Param("productionOrderId") Long productionOrderId,
            @Param("workType") String workType,
            @Param("status") String status,
            @Param("handlerId") Long handlerId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("tenantId") Long tenantId);

    /**
     * 根据工单号查询工单
     */
    WorkOrder selectByWorkOrderNo(@Param("workOrderNo") String workOrderNo, @Param("tenantId") Long tenantId);

    /**
     * 根据生产订单ID查询工单列表
     */
    List<WorkOrder> selectByProductionOrderId(@Param("productionOrderId") Long productionOrderId, @Param("tenantId") Long tenantId);

    /**
     * 根据处理人ID查询工单列表
     */
    List<WorkOrder> selectByHandlerId(@Param("handlerId") Long handlerId, @Param("tenantId") Long tenantId);

    /**
     * 根据工单类型查询工单列表
     */
    List<WorkOrder> selectByWorkType(@Param("workType") String workType, @Param("tenantId") Long tenantId);

    /**
     * 根据状态查询工单列表
     */
    List<WorkOrder> selectByStatus(@Param("status") String status, @Param("tenantId") Long tenantId);

    /**
     * 更新工单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 更新工单处理人
     */
    int updateHandler(@Param("id") Long id, @Param("handlerId") Long handlerId, @Param("handlerName") String handlerName, 
                      @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 更新工单成本
     */
    int updateCost(@Param("id") Long id, @Param("cost") String cost, @Param("laborCost") String laborCost, 
                   @Param("materialCost") String materialCost, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 更新工单工时
     */
    int updateWorkHours(@Param("id") Long id, @Param("actualHours") String actualHours, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 批量删除工单（逻辑删除）
     */
    int batchDeleteByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String[] ids);

    /**
     * 查询当日最大工单号（用于生成新工单号）
     */
    String findMaxWorkOrderNoByPrefix(@Param("prefix") String prefix);

    /**
     * 统计各状态的工单数量
     */
    List<Object> getStatusStatistics(@Param("tenantId") Long tenantId);

    /**
     * 统计各工单类型的数量
     */
    List<Object> getWorkTypeStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询工单的成本统计
     */
    List<Object> getCostStatistics(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId);

    /**
     * 查询工单的工时统计
     */
    List<Object> getWorkHoursStatistics(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId);

    /**
     * 根据处理人统计工单完成情况
     */
    List<Object> getHandlerStatistics(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId);
}
