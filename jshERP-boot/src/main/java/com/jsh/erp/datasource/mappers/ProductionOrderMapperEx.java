package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 主生产订单扩展Mapper接口
 * 用于自定义查询方法
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface ProductionOrderMapperEx {

    /**
     * 根据条件查询生产订单列表（分页）
     */
    List<ProductionOrder> selectByCondition(
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("materialId") Long materialId,
            @Param("salesOrderId") Long salesOrderId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("tenantId") Long tenantId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    /**
     * 根据条件统计生产订单数量
     */
    Long countByCondition(
            @Param("orderNo") String orderNo,
            @Param("status") String status,
            @Param("materialId") Long materialId,
            @Param("salesOrderId") Long salesOrderId,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("tenantId") Long tenantId);

    /**
     * 根据订单号查询生产订单
     */
    ProductionOrder selectByOrderNo(@Param("orderNo") String orderNo, @Param("tenantId") Long tenantId);

    /**
     * 根据销售订单ID查询生产订单列表
     */
    List<ProductionOrder> selectBySalesOrderId(@Param("salesOrderId") Long salesOrderId, @Param("tenantId") Long tenantId);

    /**
     * 根据状态查询生产订单列表
     */
    List<ProductionOrder> selectByStatus(@Param("status") String status, @Param("tenantId") Long tenantId);

    /**
     * 查询即将到期的生产订单（交付期限在指定天数内）
     */
    List<ProductionOrder> selectExpiringSoon(@Param("days") Integer days, @Param("tenantId") Long tenantId);

    /**
     * 更新生产订单状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 更新生产订单进度
     */
    int updateProgress(@Param("id") Long id, @Param("progress") String progress, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 更新生产订单成本
     */
    int updateCost(@Param("id") Long id, @Param("totalCost") String totalCost, @Param("materialCost") String materialCost, 
                   @Param("laborCost") String laborCost, @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);

    /**
     * 批量删除生产订单（逻辑删除）
     */
    int batchDeleteByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String[] ids);

    /**
     * 根据物料ID查询相关的生产订单
     */
    List<ProductionOrder> selectByMaterialId(@Param("materialId") Long materialId, @Param("tenantId") Long tenantId);

    /**
     * 查询当日最大订单号（用于生成新订单号）
     */
    String findMaxOrderNoByPrefix(@Param("prefix") String prefix);

    /**
     * 统计各状态的生产订单数量
     */
    List<Object> getStatusStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询生产订单的成本统计
     */
    List<Object> getCostStatistics(@Param("beginTime") String beginTime, @Param("endTime") String endTime, @Param("tenantId") Long tenantId);
}
