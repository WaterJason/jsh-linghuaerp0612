package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.PostProcessingTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 后工任务记录Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface PostProcessingTaskMapper extends BaseMapper<PostProcessingTask> {

    /**
     * 根据条件查询后工任务列表
     * 
     * @param parameterMap 查询参数
     * @return 任务列表
     */
    List<PostProcessingTask> selectByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据条件统计后工任务数量
     * 
     * @param parameterMap 查询参数
     * @return 任务数量
     */
    Long countByCondition(@Param("parameterMap") Map<String, Object> parameterMap);

    /**
     * 根据任务编号查询后工任务信息
     * 
     * @param taskNumber 任务编号
     * @param tenantId 租户ID
     * @return 任务信息
     */
    PostProcessingTask selectByTaskNumber(@Param("taskNumber") String taskNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据状态查询后工任务列表
     * 
     * @param status 状态
     * @param tenantId 租户ID
     * @return 任务列表
     */
    List<PostProcessingTask> selectByStatus(@Param("status") String status, @Param("tenantId") Long tenantId);

    /**
     * 根据任务类型查询后工任务列表
     * 
     * @param taskType 任务类型
     * @param tenantId 租户ID
     * @return 任务列表
     */
    List<PostProcessingTask> selectByTaskType(@Param("taskType") String taskType, @Param("tenantId") Long tenantId);

    /**
     * 根据工人查询后工任务列表
     * 
     * @param workerId 工人ID
     * @param tenantId 租户ID
     * @return 任务列表
     */
    List<PostProcessingTask> selectByWorkerId(@Param("workerId") Long workerId, @Param("tenantId") Long tenantId);

    /**
     * 根据成品查询后工任务列表
     * 
     * @param productId 成品ID
     * @param tenantId 租户ID
     * @return 任务列表
     */
    List<PostProcessingTask> selectByProductId(@Param("productId") Long productId, @Param("tenantId") Long tenantId);

    /**
     * 根据优先级查询后工任务列表
     * 
     * @param priority 优先级
     * @param tenantId 租户ID
     * @return 任务列表
     */
    List<PostProcessingTask> selectByPriority(@Param("priority") String priority, @Param("tenantId") Long tenantId);

    /**
     * 更新任务状态
     * 
     * @param id 任务ID
     * @param status 状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updateUser") Long updateUser);

    /**
     * 更新任务开始时间
     * 
     * @param id 任务ID
     * @param actualStartTime 实际开始时间
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateStartTime(@Param("id") Long id, @Param("actualStartTime") java.util.Date actualStartTime, @Param("updateUser") Long updateUser);

    /**
     * 更新任务完成时间
     * 
     * @param id 任务ID
     * @param actualEndTime 实际完成时间
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateEndTime(@Param("id") Long id, @Param("actualEndTime") java.util.Date actualEndTime, @Param("updateUser") Long updateUser);

    /**
     * 更新任务优先级
     * 
     * @param id 任务ID
     * @param priority 优先级
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updatePriority(@Param("id") Long id, @Param("priority") String priority, @Param("updateUser") Long updateUser);

    /**
     * 分配工人
     * 
     * @param id 任务ID
     * @param workerId 工人ID
     * @param workerName 工人姓名
     * @param updateUser 更新人
     * @return 更新结果
     */
    int assignWorker(@Param("id") Long id, @Param("workerId") Long workerId, 
                    @Param("workerName") String workerName, @Param("updateUser") Long updateUser);

    /**
     * 更新处理结果
     * 
     * @param id 任务ID
     * @param processingResult 处理结果
     * @param qualityGrade 质量等级
     * @param qualityScore 质量评分
     * @param updateUser 更新人
     * @return 更新结果
     */
    int updateProcessingResult(@Param("id") Long id, @Param("processingResult") String processingResult,
                              @Param("qualityGrade") String qualityGrade, @Param("qualityScore") java.math.BigDecimal qualityScore,
                              @Param("updateUser") Long updateUser);

    /**
     * 批量更新状态
     * 
     * @param ids 任务ID列表
     * @param status 状态
     * @param updateUser 更新人
     * @return 更新结果
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("status") String status, @Param("updateUser") Long updateUser);

    /**
     * 批量分配工人
     * 
     * @param ids 任务ID列表
     * @param workerId 工人ID
     * @param workerName 工人姓名
     * @param updateUser 更新人
     * @return 更新结果
     */
    int batchAssignWorker(@Param("ids") List<Long> ids, @Param("workerId") Long workerId, 
                         @Param("workerName") String workerName, @Param("updateUser") Long updateUser);

    /**
     * 批量删除任务（软删除）
     * 
     * @param ids 任务ID列表
     * @param updateUser 更新人
     * @return 删除结果
     */
    int batchDelete(@Param("ids") List<Long> ids, @Param("updateUser") Long updateUser);

    /**
     * 获取任务统计信息
     * 
     * @param tenantId 租户ID
     * @return 统计信息
     */
    Map<String, Object> getTaskStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据状态统计任务数量
     * 
     * @param tenantId 租户ID
     * @return 状态统计
     */
    List<Map<String, Object>> getStatusStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据任务类型统计任务数量
     * 
     * @param tenantId 租户ID
     * @return 任务类型统计
     */
    List<Map<String, Object>> getTaskTypeStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据优先级统计任务数量
     * 
     * @param tenantId 租户ID
     * @return 优先级统计
     */
    List<Map<String, Object>> getPriorityStatistics(@Param("tenantId") Long tenantId);

    /**
     * 根据工人统计任务数量
     * 
     * @param tenantId 租户ID
     * @return 工人统计
     */
    List<Map<String, Object>> getWorkerStatistics(@Param("tenantId") Long tenantId);

    /**
     * 查询任务处理时间排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 处理时间排行
     */
    List<Map<String, Object>> getProcessingTimeRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询工人效率排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 工人效率排行
     */
    List<Map<String, Object>> getWorkerEfficiencyRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询质量排行
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 质量排行
     */
    List<Map<String, Object>> getQualityRanking(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询月度任务趋势
     * 
     * @param tenantId 租户ID
     * @param months 月份数
     * @return 月度趋势
     */
    List<Map<String, Object>> getMonthlyTrend(@Param("tenantId") Long tenantId, @Param("months") Integer months);

    /**
     * 查询任务详细信息（包含关联数据）
     * 
     * @param id 任务ID
     * @param tenantId 租户ID
     * @return 任务详细信息
     */
    Map<String, Object> getTaskDetail(@Param("id") Long id, @Param("tenantId") Long tenantId);

    /**
     * 查询待分配的任务
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 待分配任务列表
     */
    List<PostProcessingTask> getPendingAssignmentTasks(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询进行中的任务
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 进行中任务列表
     */
    List<PostProcessingTask> getInProgressTasks(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 查询超期任务
     * 
     * @param tenantId 租户ID
     * @return 超期任务列表
     */
    List<PostProcessingTask> getOverdueTasks(@Param("tenantId") Long tenantId);

    /**
     * 查询高优先级任务
     * 
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 高优先级任务列表
     */
    List<PostProcessingTask> getHighPriorityTasks(@Param("tenantId") Long tenantId, @Param("limit") Integer limit);

    /**
     * 推荐合适的工人
     * 
     * @param taskType 任务类型
     * @param tenantId 租户ID
     * @param limit 限制数量
     * @return 推荐工人列表
     */
    List<Map<String, Object>> recommendWorkers(@Param("taskType") String taskType, 
                                              @Param("tenantId") Long tenantId, 
                                              @Param("limit") Integer limit);

    /**
     * 查询工人当前任务负荷
     * 
     * @param tenantId 租户ID
     * @return 工人任务负荷
     */
    List<Map<String, Object>> getWorkerTaskLoad(@Param("tenantId") Long tenantId);
}
