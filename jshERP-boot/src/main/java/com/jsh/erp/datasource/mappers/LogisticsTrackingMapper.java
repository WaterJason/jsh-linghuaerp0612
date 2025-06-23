package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.LogisticsTracking;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 物流追踪记录Mapper接口
 *
 * @author jshERP
 * @date 2025-06-22
 */
public interface LogisticsTrackingMapper extends BaseMapper<LogisticsTracking> {

    /**
     * 根据条件查询物流追踪列表
     *
     * @param parameterMap 查询参数
     * @return 追踪列表
     */
    List<LogisticsTracking> selectByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据条件统计物流追踪数量
     *
     * @param parameterMap 查询参数
     * @return 追踪数量
     */
    Long countByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据追踪号查询物流追踪信息
     *
     * @param trackingNumber 追踪号
     * @param tenantId 租户ID
     * @return 追踪信息
     */
    LogisticsTracking selectByTrackingNumber(@Param("trackingNumber") String trackingNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据工单查询物流追踪列表
     *
     * @param workOrderId 工单ID
     * @param tenantId 租户ID
     * @return 追踪列表
     */
    List<LogisticsTracking> selectByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("tenantId") Long tenantId);

    /**
     * 根据任务查询物流追踪列表
     *
     * @param taskId 任务ID
     * @param tenantId 租户ID
     * @return 追踪列表
     */
    List<LogisticsTracking> selectByTaskId(@Param("taskId") Long taskId, @Param("tenantId") Long tenantId);

    /**
     * 根据状态查询物流追踪列表
     *
     * @param status 状态
     * @param tenantId 租户ID
     * @return 追踪列表
     */
    List<LogisticsTracking> selectByStatus(@Param("status") String status, @Param("tenantId") Long tenantId);

    /**
     * 更新物流状态
     *
     * @param id 追踪ID
     * @param status 状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updateUser") Long updateUser);

    /**
     * 更新发货时间
     *
     * @param id 追踪ID
     * @param shipTime 发货时间
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateShipTime(@Param("id") Long id, @Param("shipTime") java.util.Date shipTime, @Param("updateUser") Long updateUser);

    /**
     * 更新到达时间
     *
     * @param id 追踪ID
     * @param actualArrivalTime 实际到达时间
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateArrivalTime(@Param("id") Long id, @Param("actualArrivalTime") java.util.Date actualArrivalTime, @Param("updateUser") Long updateUser);

    /**
     * 获取物流统计信息
     *
     * @param tenantId 租户ID
     * @return 统计信息
     */
    Map<String, Object> getLogisticsStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据状态统计物流数量
     *
     * @param tenantId 租户ID
     * @return 状态统计
     */
    List<Map<String, Object>> getStatusStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询在途物流
     *
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 在途物流列表
     */
    List<LogisticsTracking> getInTransitLogistics(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询超期物流
     *
     * @param tenantId 租户ID
     * @return 超期物流列表
     */
    List<LogisticsTracking> getOverdueLogistics(@Param("tenantId") Long tenantId);

    /**
     * 批量删除追踪记录（软删除）
     *
     * @param ids 追踪ID列表
     * @param updateUser 更新人
     * @return 删除结果
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("updateUser") Long updateUser);
}
