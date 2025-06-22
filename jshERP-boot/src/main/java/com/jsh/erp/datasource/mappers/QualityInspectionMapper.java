package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.QualityInspection;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 质量检验记录Mapper接口
 *
 * @author jshERP
 * @date 2025-06-22
 */
public interface QualityInspectionMapper extends BaseMapper<QualityInspection> {

    /**
     * 根据条件查询质检记录列表
     *
     * @param parameterMap 查询参数
     * @return 质检记录列表
     */
    List<QualityInspection> selectByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据条件统计质检记录数量
     *
     * @param parameterMap 查询参数
     * @return 质检记录数量
     */
    Long countByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据质检单号查询质检记录
     *
     * @param inspectionNumber 质检单号
     * @param tenantId 租户ID
     * @return 质检记录
     */
    QualityInspection selectByInspectionNumber(@Param("inspectionNumber") String inspectionNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据工单查询质检记录列表
     *
     * @param workOrderId 工单ID
     * @param tenantId 租户ID
     * @return 质检记录列表
     */
    List<QualityInspection> selectByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("tenantId") Long tenantId);

    /**
     * 根据任务查询质检记录列表
     *
     * @param taskId 任务ID
     * @param tenantId 租户ID
     * @return 质检记录列表
     */
    List<QualityInspection> selectByTaskId(@Param("taskId") Long taskId, @Param("tenantId") Long tenantId);

    /**
     * 更新质检结果
     *
     * @param id 质检记录ID
     * @param overallResult 质检结果
     * @param qualityGrade 质量等级
     * @param overallScore 总体评分
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateInspectionResult(@Param("id") Long id, @Param("overallResult") String overallResult,
                              @Param("qualityGrade") String qualityGrade, @Param("overallScore") java.math.BigDecimal overallScore,
                              @Param("updateUser") Long updateUser);

    /**
     * 获取质检统计信息
     *
     * @param tenantId 租户ID
     * @return 统计信息
     */
    Map<String, Object> getQualityStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据质检类型统计数量
     *
     * @param tenantId 租户ID
     * @return 质检类型统计
     */
    List<Map<String, Object>> getInspectionTypeStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据质检结果统计数量
     *
     * @param tenantId 租户ID
     * @return 质检结果统计
     */
    List<Map<String, Object>> getInspectionResultStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询待质检记录
     *
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 待质检记录列表
     */
    List<QualityInspection> getPendingInspections(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询不合格记录
     *
     * @param tenantId 租户ID
     * @return 不合格记录列表
     */
    List<QualityInspection> getUnqualifiedInspections(@Param("tenantId") Long tenantId);

    /**
     * 批量删除质检记录（软删除）
     *
     * @param ids 质检记录ID列表
     * @param updateUser 更新人
     * @return 删除结果
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("updateUser") Long updateUser);
}
