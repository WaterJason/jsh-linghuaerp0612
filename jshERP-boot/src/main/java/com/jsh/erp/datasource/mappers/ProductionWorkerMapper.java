package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.ProductionWorker;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 工人信息Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionWorkerMapper extends BaseMapper<ProductionWorker> {

    /**
     * 根据条件查询工人列表
     * 
     * @param parameterMap 查询参数
     * @return 工人列表
     */
    List<ProductionWorker> selectByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据条件统计工人数量
     * 
     * @param parameterMap 查询参数
     * @return 工人数量
     */
    Long countByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据工人编号查询工人信息
     * 
     * @param workerNumber 工人编号
     * @param tenantId 租户ID
     * @return 工人信息
     */
    ProductionWorker selectByWorkerNumber(@Param("workerNumber") String workerNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据专业技能查询工人列表
     * 
     * @param specialty 专业技能
     * @param tenantId 租户ID
     * @return 工人列表
     */
    List<ProductionWorker> selectBySpecialty(@Param("specialty") String specialty, @Param("tenantId") Long tenantId);

    /**
     * 根据技能等级查询工人列表
     * 
     * @param skillLevel 技能等级
     * @param tenantId 租户ID
     * @return 工人列表
     */
    List<ProductionWorker> selectBySkillLevel(@Param("skillLevel") String skillLevel, @Param("tenantId") Long tenantId);

    /**
     * 查询可用工人列表
     * 
     * @param tenantId 租户ID
     * @return 可用工人列表
     */
    List<ProductionWorker> selectAvailableWorkers(@Param("tenantId") Long tenantId);

    /**
     * 查询忙碌工人列表
     * 
     * @param tenantId 租户ID
     * @return 忙碌工人列表
     */
    List<ProductionWorker> selectBusyWorkers(@Param("tenantId") Long tenantId);

    /**
     * 根据部门查询工人列表
     * 
     * @param department 部门
     * @param tenantId 租户ID
     * @return 工人列表
     */
    List<ProductionWorker> selectByDepartment(@Param("department") String department, @Param("tenantId") Long tenantId);

    /**
     * 更新工人可用状态
     * 
     * @param id 工人ID
     * @param availabilityStatus 可用状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateAvailabilityStatus(@Param("id") Long id, @Param("availabilityStatus") String availabilityStatus, @Param("updateUser") Long updateUser);

    /**
     * 更新工人工作负荷
     * 
     * @param id 工人ID
     * @param currentWorkload 当前工作负荷
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateWorkload(@Param("id") Long id, @Param("currentWorkload") Integer currentWorkload, @Param("updateUser") Long updateUser);

    /**
     * 更新工人效率评级
     * 
     * @param id 工人ID
     * @param efficiencyRating 效率评级
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateEfficiencyRating(@Param("id") Long id, @Param("efficiencyRating") java.math.BigDecimal efficiencyRating, @Param("updateUser") Long updateUser);

    /**
     * 更新工人质量评级
     * 
     * @param id 工人ID
     * @param qualityRating 质量评级
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateQualityRating(@Param("id") Long id, @Param("qualityRating") java.math.BigDecimal qualityRating, @Param("updateUser") Long updateUser);

    /**
     * 批量更新工人状态
     * 
     * @param ids 工人ID列表
     * @param status 状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updateUser") Long updateUser);

    /**
     * 批量删除工人（软删除）
     * 
     * @param ids 工人ID列表
     * @param updateUser 更新人
     * @return 删除结果
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("updateUser") Long updateUser);

    /**
     * 获取工人统计信息
     * 
     * @param tenantId 租户ID
     * @return 统计信息
     */
    Map<String, Object> getWorkerStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据技能等级统计工人数量
     * 
     * @param tenantId 租户ID
     * @return 技能等级统计
     */
    List<Map<String, Object>> getSkillLevelStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据部门统计工人数量
     * 
     * @param tenantId 租户ID
     * @return 部门统计
     */
    List<Map<String, Object>> getDepartmentStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据可用状态统计工人数量
     * 
     * @param tenantId 租户ID
     * @return 可用状态统计
     */
    List<Map<String, Object>> getAvailabilityStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询工人工作负荷排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 工作负荷排行
     */
    List<Map<String, Object>> getWorkloadRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询工人效率排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 效率排行
     */
    List<Map<String, Object>> getEfficiencyRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询工人质量排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 质量排行
     */
    List<Map<String, Object>> getQualityRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 根据专业技能推荐工人
     * 
     * @param specialty 专业技能
     * @param skillLevel 最低技能等级
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 推荐工人列表
     */
    List<ProductionWorker> recommendWorkers(@Param("specialty") String specialty, @Param("skillLevel") String skillLevel, @Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询工人详细信息（包含统计数据）
     * 
     * @param id 工人ID
     * @param tenantId 租户ID
     * @return 工人详细信息
     */
    Map<String, Object> getWorkerDetail(@Param("id") Long id, @Param("tenantId") Long tenantId);
}
