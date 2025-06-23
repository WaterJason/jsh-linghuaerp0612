package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.CloisonneProduction;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 掐丝点蓝制作记录Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface CloisonneProductionMapper extends BaseMapper<CloisonneProduction> {

    /**
     * 根据条件查询掐丝点蓝制作列表
     * 
     * @param parameterMap 查询参数
     * @return 制作列表
     */
    List<CloisonneProduction> selectByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据条件统计掐丝点蓝制作数量
     * 
     * @param parameterMap 查询参数
     * @return 制作数量
     */
    Long countByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据订单号查询掐丝点蓝制作信息
     * 
     * @param orderNumber 订单号
     * @param tenantId 租户ID
     * @return 制作信息
     */
    CloisonneProduction selectByOrderNumber(@Param("orderNumber") String orderNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据状态查询掐丝点蓝制作列表
     * 
     * @param status 状态
     * @param tenantId 租户ID
     * @return 制作列表
     */
    List<CloisonneProduction> selectByStatus(@Param("status") String status, @Param("tenantId") Long tenantId);

    /**
     * 根据供应商查询掐丝点蓝制作列表
     * 
     * @param supplierId 供应商ID
     * @param tenantId 租户ID
     * @return 制作列表
     */
    List<CloisonneProduction> selectBySupplierId(@Param("supplierId") Long supplierId, @Param("tenantId") Long tenantId);

    /**
     * 根据商品查询掐丝点蓝制作列表
     * 
     * @param materialId 商品ID
     * @param tenantId 租户ID
     * @return 制作列表
     */
    List<CloisonneProduction> selectByMaterialId(@Param("materialId") Long materialId, @Param("tenantId") Long tenantId);

    /**
     * 更新制作状态
     * 
     * @param id 制作ID
     * @param status 状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updateUser") Long updateUser);

    /**
     * 更新制作开始时间
     * 
     * @param id 制作ID
     * @param startTime 开始时间
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateStartTime(@Param("id") Long id, @Param("startTime") java.util.Date startTime, @Param("updateUser") Long updateUser);

    /**
     * 更新制作完成时间
     * 
     * @param id 制作ID
     * @param endTime 完成时间
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateEndTime(@Param("id") Long id, @Param("endTime") java.util.Date endTime, @Param("updateUser") Long updateUser);

    /**
     * 更新质量信息
     * 
     * @param id 制作ID
     * @param qualityGrade 质量等级
     * @param qualityScore 质量评分
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateQualityInfo(@Param("id") Long id, @Param("qualityGrade") String qualityGrade, 
                         @Param("qualityScore") java.math.BigDecimal qualityScore, @Param("updateUser") Long updateUser);

    /**
     * 批量更新状态
     * 
     * @param ids 制作ID列表
     * @param status 状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updateUser") Long updateUser);

    /**
     * 批量删除制作记录（软删除）
     * 
     * @param ids 制作ID列表
     * @param updateUser 更新人
     * @return 删除结果
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("updateUser") Long updateUser);

    /**
     * 获取制作统计信息
     * 
     * @param tenantId 租户ID
     * @return 统计信息
     */
    Map<String, Object> getProductionStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据状态统计制作数量
     * 
     * @param tenantId 租户ID
     * @return 状态统计
     */
    List<Map<String, Object>> getStatusStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据供应商统计制作数量
     * 
     * @param tenantId 租户ID
     * @return 供应商统计
     */
    List<Map<String, Object>> getSupplierStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据商品统计制作数量
     * 
     * @param tenantId 租户ID
     * @return 商品统计
     */
    List<Map<String, Object>> getMaterialStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据工艺类型统计制作数量
     * 
     * @param tenantId 租户ID
     * @return 工艺类型统计
     */
    List<Map<String, Object>> getCraftTypeStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据质量等级统计制作数量
     * 
     * @param tenantId 租户ID
     * @return 质量等级统计
     */
    List<Map<String, Object>> getQualityGradeStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询制作周期排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 制作周期排行
     */
    List<Map<String, Object>> getCycleTimeRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询成本排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 成本排行
     */
    List<Map<String, Object>> getCostRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询质量排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 质量排行
     */
    List<Map<String, Object>> getQualityRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询月度制作趋势
     * 
     * @param tenantId 租户ID
     * @param months 月份数
     * @return 月度趋势
     */
    List<Map<String, Object>> getMonthlyTrend(@Param("tenantId") Long tenantId, @Param("months") Integer months);

    /**
     * 查询制作详细信息（包含关联数据）
     * 
     * @param id 制作ID
     * @param tenantId 租户ID
     * @return 制作详细信息
     */
    Map<String, Object> getProductionDetail(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 查询待处理的制作订单
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 待处理订单列表
     */
    List<CloisonneProduction> getPendingOrders(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询进行中的制作订单
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 进行中订单列表
     */
    List<CloisonneProduction> getInProgressOrders(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询超期的制作订单
     * 
     * @param tenantId 租户ID
     * @return 超期订单列表
     */
    List<CloisonneProduction> getOverdueOrders(@Param("tenantId") Long tenantId);

    /**
     * 查询即将到期的制作订单
     * 
     * @param tenantId 租户ID
     * @param days 天数
     * @return 即将到期订单列表
     */
    List<CloisonneProduction> getUpcomingOrders(@Param("tenantId") Long tenantId, @Param("days") Integer days);
}
