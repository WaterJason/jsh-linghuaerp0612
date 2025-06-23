package com.jsh.erp.service.cloisonne;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jsh.erp.datasource.entities.CloisonneSchedule;
import com.jsh.erp.datasource.vo.CloisonneScheduleVo4List;
import com.jsh.erp.datasource.vo.CloisonneScheduleStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 掐丝珐琅馆排班服务接口
 * 
 * @author jshERP
 * @since 2025-01-22
 */
public interface CloisonneScheduleService extends IService<CloisonneSchedule> {

    /**
     * 根据条件查询排班列表
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param employeeId 员工ID
     * @param shiftType 班次类型
     * @param status 状态
     * @return 排班列表
     */
    List<CloisonneScheduleVo4List> findScheduleList(LocalDate startDate, LocalDate endDate, 
                                                   Long employeeId, String shiftType, String status);

    /**
     * 查询指定日期范围内的排班统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班统计
     */
    CloisonneScheduleStatistics findScheduleStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 查询指定日期的值班人员
     * 
     * @param scheduleDate 排班日期
     * @return 值班人员列表
     */
    List<CloisonneScheduleVo4List> findOnDutyStaff(LocalDate scheduleDate);

    /**
     * 查询当前值班人员信息
     * 
     * @return 当前值班人员信息
     */
    Map<String, Object> findCurrentShiftInfo();

    /**
     * 查询员工在指定日期范围内的工作时长统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 员工工时统计列表
     */
    List<CloisonneScheduleStatistics> findEmployeeWorkHours(LocalDate startDate, LocalDate endDate);

    /**
     * 查询每日排班数量统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每日排班数量统计
     */
    List<CloisonneScheduleStatistics> findDailyScheduleCount(LocalDate startDate, LocalDate endDate);

    /**
     * 查询班次分布统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 班次分布统计
     */
    List<CloisonneScheduleStatistics> findShiftDistribution(LocalDate startDate, LocalDate endDate);

    /**
     * 创建排班记录
     * 
     * @param schedule 排班信息
     * @return 创建结果
     */
    boolean createSchedule(CloisonneSchedule schedule);

    /**
     * 更新排班记录
     * 
     * @param schedule 排班信息
     * @return 更新结果
     */
    boolean updateSchedule(CloisonneSchedule schedule);

    /**
     * 删除排班记录
     * 
     * @param id 排班ID
     * @return 删除结果
     */
    boolean deleteSchedule(Long id);

    /**
     * 批量创建排班记录
     * 
     * @param scheduleList 排班记录列表
     * @return 创建结果
     */
    boolean batchCreateSchedule(List<CloisonneSchedule> scheduleList);

    /**
     * 批量更新排班状态
     * 
     * @param ids 排班ID列表
     * @param status 新状态
     * @return 更新结果
     */
    boolean batchUpdateScheduleStatus(List<Long> ids, String status);

    /**
     * 检查员工在指定日期是否已有排班
     * 
     * @param employeeId 员工ID
     * @param scheduleDate 排班日期
     * @param excludeId 排除的排班ID(用于更新时检查)
     * @return 是否存在排班
     */
    boolean checkEmployeeScheduleExists(Long employeeId, LocalDate scheduleDate, Long excludeId);

    /**
     * 检查排班时间冲突
     * 
     * @param employeeId 员工ID
     * @param scheduleDate 排班日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的排班ID
     * @return 冲突的排班记录列表
     */
    List<CloisonneSchedule> checkScheduleTimeConflict(Long employeeId, LocalDate scheduleDate, 
                                                     String startTime, String endTime, Long excludeId);

    /**
     * 根据员工ID查询最近的排班记录
     * 
     * @param employeeId 员工ID
     * @param limit 限制数量
     * @return 排班记录列表
     */
    List<CloisonneScheduleVo4List> findRecentScheduleByEmployee(Long employeeId, Integer limit);

    /**
     * 复制排班模板
     * 
     * @param sourceStartDate 源开始日期
     * @param sourceEndDate 源结束日期
     * @param targetStartDate 目标开始日期
     * @return 复制结果
     */
    boolean copyScheduleTemplate(LocalDate sourceStartDate, LocalDate sourceEndDate, LocalDate targetStartDate);

    /**
     * 生成排班报表数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param reportType 报表类型(summary/detail/statistics)
     * @return 报表数据
     */
    Map<String, Object> generateScheduleReport(LocalDate startDate, LocalDate endDate, String reportType);

    /**
     * 导出排班数据
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param exportType 导出类型(excel/pdf)
     * @return 导出文件路径
     */
    String exportScheduleData(LocalDate startDate, LocalDate endDate, String exportType);

    /**
     * 获取排班配置信息
     * 
     * @return 排班配置
     */
    Map<String, Object> getScheduleConfig();

    /**
     * 更新排班配置
     * 
     * @param config 配置信息
     * @return 更新结果
     */
    boolean updateScheduleConfig(Map<String, Object> config);
}
