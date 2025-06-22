package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.CloisonneSchedule;
import com.jsh.erp.datasource.vo.CloisonneScheduleVo4List;
import com.jsh.erp.datasource.vo.CloisonneScheduleStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 掐丝珐琅馆排班扩展Mapper接口
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@Mapper
public interface CloisonneScheduleMapperEx {

    /**
     * 根据条件查询排班列表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param employeeId 员工ID
     * @param shiftType 班次类型
     * @param status 状态
     * @param tenantId 租户ID
     * @return 排班列表
     */
    List<CloisonneScheduleVo4List> selectScheduleList(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("employeeId") Long employeeId,
            @Param("shiftType") String shiftType,
            @Param("status") String status,
            @Param("tenantId") Long tenantId
    );

    /**
     * 查询指定日期范围内的排班统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenantId 租户ID
     * @return 排班统计
     */
    CloisonneScheduleStatistics selectScheduleStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") Long tenantId
    );

    /**
     * 查询指定日期的值班人员
     * 
     * @param scheduleDate 排班日期
     * @param tenantId 租户ID
     * @return 值班人员列表
     */
    List<CloisonneScheduleVo4List> selectOnDutyStaff(
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("tenantId") Long tenantId
    );

    /**
     * 查询员工在指定日期范围内的工作时长统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenantId 租户ID
     * @return 员工工时统计列表
     */
    List<CloisonneScheduleStatistics> selectEmployeeWorkHours(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") Long tenantId
    );

    /**
     * 查询每日排班数量统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenantId 租户ID
     * @return 每日排班数量统计
     */
    List<CloisonneScheduleStatistics> selectDailyScheduleCount(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") Long tenantId
    );

    /**
     * 查询班次分布统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param tenantId 租户ID
     * @return 班次分布统计
     */
    List<CloisonneScheduleStatistics> selectShiftDistribution(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("tenantId") Long tenantId
    );

    /**
     * 检查员工在指定日期是否已有排班
     * 
     * @param employeeId 员工ID
     * @param scheduleDate 排班日期
     * @param excludeId 排除的排班ID(用于更新时检查)
     * @param tenantId 租户ID
     * @return 排班记录数量
     */
    int checkEmployeeScheduleExists(
            @Param("employeeId") Long employeeId,
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("excludeId") Long excludeId,
            @Param("tenantId") Long tenantId
    );

    /**
     * 批量插入排班记录
     * 
     * @param scheduleList 排班记录列表
     * @return 插入记录数
     */
    int batchInsertSchedule(@Param("list") List<CloisonneSchedule> scheduleList);

    /**
     * 批量更新排班状态
     * 
     * @param ids 排班ID列表
     * @param status 新状态
     * @param updateUser 更新人ID
     * @param tenantId 租户ID
     * @return 更新记录数
     */
    int batchUpdateScheduleStatus(
            @Param("ids") List<Long> ids,
            @Param("status") String status,
            @Param("updateUser") Long updateUser,
            @Param("tenantId") Long tenantId
    );

    /**
     * 根据员工ID查询最近的排班记录
     * 
     * @param employeeId 员工ID
     * @param limit 限制数量
     * @param tenantId 租户ID
     * @return 排班记录列表
     */
    List<CloisonneScheduleVo4List> selectRecentScheduleByEmployee(
            @Param("employeeId") Long employeeId,
            @Param("limit") Integer limit,
            @Param("tenantId") Long tenantId
    );

    /**
     * 查询冲突的排班记录
     * 
     * @param employeeId 员工ID
     * @param scheduleDate 排班日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的排班ID
     * @param tenantId 租户ID
     * @return 冲突的排班记录列表
     */
    List<CloisonneSchedule> selectConflictSchedule(
            @Param("employeeId") Long employeeId,
            @Param("scheduleDate") LocalDate scheduleDate,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("excludeId") Long excludeId,
            @Param("tenantId") Long tenantId
    );
}
