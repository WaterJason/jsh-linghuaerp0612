package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionTask;
import com.jsh.erp.datasource.entities.ProductionTaskEx;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产任务扩展Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionTaskMapperEx {

    /**
     * 根据条件查询生产任务列表
     */
    List<ProductionTaskEx> selectByConditionTask(
            @Param("taskNumber") String taskNumber,
            @Param("taskName") String taskName,
            @Param("workOrderNumber") String workOrderNumber,
            @Param("taskType") String taskType,
            @Param("processStep") String processStep,
            @Param("productName") String productName,
            @Param("priority") String priority,
            @Param("status") String status,
            @Param("workerName") String workerName,
            @Param("workerSpecialty") String workerSpecialty,
            @Param("assignTime") Date assignTime,
            @Param("planStartTime") Date planStartTime,
            @Param("planEndTime") Date planEndTime,
            @Param("tenantId") Long tenantId,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    /**
     * 根据条件统计生产任务数量
     */
    Long countByConditionTask(
            @Param("taskNumber") String taskNumber,
            @Param("taskName") String taskName,
            @Param("workOrderNumber") String workOrderNumber,
            @Param("taskType") String taskType,
            @Param("processStep") String processStep,
            @Param("productName") String productName,
            @Param("priority") String priority,
            @Param("status") String status,
            @Param("workerName") String workerName,
            @Param("workerSpecialty") String workerSpecialty,
            @Param("assignTime") Date assignTime,
            @Param("planStartTime") Date planStartTime,
            @Param("planEndTime") Date planEndTime,
            @Param("tenantId") Long tenantId);

    /**
     * 根据ID查询任务详情（包含关联信息）
     */
    ProductionTaskEx findById(@Param("id") Long id);

    /**
     * 根据任务编号查询任务
     */
    ProductionTask findByTaskNumber(@Param("taskNumber") String taskNumber, @Param("tenantId") Long tenantId);

    /**
     * 根据工单ID查询任务列表
     */
    List<ProductionTaskEx> getTaskListByWorkOrderId(@Param("workOrderId") Long workOrderId);

    /**
     * 根据工人ID查询任务列表
     */
    List<ProductionTaskEx> getTaskListByWorkerId(@Param("workerId") Long workerId, @Param("status") String status);

    /**
     * 批量删除生产任务
     */
    int batchDeleteTaskByIds(@Param("updateTime") Date updateTime, @Param("updater") Long updater, @Param("ids") String[] ids);

    /**
     * 更新任务状态
     */
    int updateTaskStatus(@Param("id") Long id, @Param("status") String status, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 分配任务给工人
     */
    int assignTaskToWorker(@Param("id") Long id, @Param("workerId") Long workerId, @Param("workerName") String workerName, @Param("assignTime") Date assignTime, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 开始任务
     */
    int startTask(@Param("id") Long id, @Param("startTime") Date startTime, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 完成任务
     */
    int completeTask(@Param("id") Long id, @Param("completeTime") Date completeTime, @Param("completedQuantity") java.math.BigDecimal completedQuantity, @Param("qualifiedQuantity") java.math.BigDecimal qualifiedQuantity, @Param("defectiveQuantity") java.math.BigDecimal defectiveQuantity, @Param("actualHours") java.math.BigDecimal actualHours, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 更新任务进度
     */
    int updateTaskProgress(@Param("id") Long id, @Param("completedQuantity") java.math.BigDecimal completedQuantity, @Param("qualifiedQuantity") java.math.BigDecimal qualifiedQuantity, @Param("defectiveQuantity") java.math.BigDecimal defectiveQuantity, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 更新任务质量信息
     */
    int updateTaskQuality(@Param("id") Long id, @Param("qualityLevel") String qualityLevel, @Param("qualityScore") java.math.BigDecimal qualityScore, @Param("updateTime") Date updateTime, @Param("updateUser") Long updateUser);

    /**
     * 检查任务编号是否存在
     */
    int checkTaskNumberExist(@Param("id") Long id, @Param("taskNumber") String taskNumber, @Param("tenantId") Long tenantId);

    /**
     * 获取可领取的任务列表
     */
    List<ProductionTaskEx> getAvailableTasks(@Param("tenantId") Long tenantId, @Param("taskType") String taskType, @Param("priority") String priority);

    /**
     * 获取工人的任务列表
     */
    List<ProductionTaskEx> getWorkerTasks(@Param("workerId") Long workerId, @Param("status") String status, @Param("tenantId") Long tenantId);

    /**
     * 获取任务统计信息
     */
    Map<String, Object> getTaskStatistics(@Param("tenantId") Long tenantId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 根据状态统计任务数量
     */
    List<Map<String, Object>> getTaskCountByStatus(@Param("tenantId") Long tenantId);

    /**
     * 根据任务类型统计任务数量
     */
    List<Map<String, Object>> getTaskCountByType(@Param("tenantId") Long tenantId);

    /**
     * 根据工人统计任务数量
     */
    List<Map<String, Object>> getTaskCountByWorker(@Param("tenantId") Long tenantId);

    /**
     * 获取即将到期的任务列表
     */
    List<ProductionTaskEx> getExpiringTasks(@Param("tenantId") Long tenantId, @Param("days") Integer days);

    /**
     * 获取超期的任务列表
     */
    List<ProductionTaskEx> getOverdueTasks(@Param("tenantId") Long tenantId);

    /**
     * 导出任务数据
     */
    List<ProductionTaskEx> exportTasks(
            @Param("taskNumber") String taskNumber,
            @Param("taskType") String taskType,
            @Param("status") String status,
            @Param("workerName") String workerName,
            @Param("tenantId") Long tenantId);

    /**
     * 获取任务编号列表（用于下拉选择）
     */
    List<String> getTaskNumberList(@Param("tenantId") Long tenantId);

    /**
     * 获取工序步骤列表（用于下拉选择）
     */
    List<String> getProcessStepList(@Param("tenantId") Long tenantId);

    /**
     * 获取工人专业技能列表（用于下拉选择）
     */
    List<String> getWorkerSpecialtyList(@Param("tenantId") Long tenantId);

    /**
     * 根据技能类型查询可分配的工人
     */
    List<Map<String, Object>> getAvailableWorkersBySkill(@Param("skillType") String skillType, @Param("tenantId") Long tenantId);

    /**
     * 检查工人是否有时间冲突的任务
     */
    int checkWorkerTimeConflict(@Param("workerId") Long workerId, @Param("planStartTime") Date planStartTime, @Param("planEndTime") Date planEndTime, @Param("excludeTaskId") Long excludeTaskId);
}
