package com.jsh.erp.service.cloisonne.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.datasource.entities.CloisonneSchedule;
import com.jsh.erp.datasource.mappers.CloisonneScheduleMapper;
import com.jsh.erp.datasource.mappers.CloisonneScheduleMapperEx;
import com.jsh.erp.datasource.vo.CloisonneScheduleVo4List;
import com.jsh.erp.datasource.vo.CloisonneScheduleStatistics;
import com.jsh.erp.service.cloisonne.CloisonneScheduleService;
import com.jsh.erp.service.cloisonne.CloisonneIntegrationService;
import com.jsh.erp.service.LogService;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 掐丝珐琅馆排班服务实现类
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@Service
public class CloisonneScheduleServiceImpl extends ServiceImpl<CloisonneScheduleMapper, CloisonneSchedule> 
        implements CloisonneScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(CloisonneScheduleServiceImpl.class);

    @Autowired
    private CloisonneScheduleMapperEx scheduleMapperEx;

    @Autowired
    private CloisonneIntegrationService integrationService;

    @Autowired
    private LogService logService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        String tenantIdStr = request.getHeader("tenantId");
        return StringUtil.isNotEmpty(tenantIdStr) ? Long.valueOf(tenantIdStr) : 0L;
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        String userIdStr = request.getHeader("userId");
        return StringUtil.isNotEmpty(userIdStr) ? Long.valueOf(userIdStr) : 0L;
    }

    @Override
    public List<CloisonneScheduleVo4List> findScheduleList(LocalDate startDate, LocalDate endDate, 
                                                          Long employeeId, String shiftType, String status) {
        try {
            Long tenantId = getCurrentTenantId();
            List<CloisonneScheduleVo4List> scheduleList = scheduleMapperEx.selectScheduleList(
                startDate, endDate, employeeId, shiftType, status, tenantId);

            // 增强数据：添加星期信息、是否今天等
            return scheduleList.stream().map(this::enhanceScheduleVo).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("查询排班列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public CloisonneScheduleStatistics findScheduleStatistics(LocalDate startDate, LocalDate endDate) {
        try {
            Long tenantId = getCurrentTenantId();
            return scheduleMapperEx.selectScheduleStatistics(startDate, endDate, tenantId);
        } catch (Exception e) {
            logger.error("查询排班统计失败", e);
            return new CloisonneScheduleStatistics();
        }
    }

    @Override
    public List<CloisonneScheduleVo4List> findOnDutyStaff(LocalDate scheduleDate) {
        try {
            Long tenantId = getCurrentTenantId();
            List<CloisonneScheduleVo4List> onDutyStaff = scheduleMapperEx.selectOnDutyStaff(scheduleDate, tenantId);

            // 增强员工信息
            return onDutyStaff.stream().map(staff -> {
                CloisonneScheduleVo4List employeeInfo = integrationService.getEmployeeForSchedule(
                    staff.getEmployeeId(), tenantId);
                if (employeeInfo != null) {
                    staff.setEmployeeRole(employeeInfo.getEmployeeRole());
                    staff.setEmployeeAvatar(employeeInfo.getEmployeeAvatar());
                    staff.setEmployeePhone(employeeInfo.getEmployeePhone());
                }
                return staff;
            }).collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("查询值班人员失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, Object> findCurrentShiftInfo() {
        try {
            LocalDate today = LocalDate.now();
            List<CloisonneScheduleVo4List> todayStaff = findOnDutyStaff(today);

            Map<String, Object> currentShiftInfo = new HashMap<>();
            
            if (!todayStaff.isEmpty()) {
                // 获取当前时间段的值班人员
                LocalTime now = LocalTime.now();
                CloisonneScheduleVo4List currentStaff = todayStaff.stream()
                    .filter(staff -> isCurrentShift(staff, now))
                    .findFirst()
                    .orElse(todayStaff.get(0)); // 如果没有匹配的，取第一个

                currentShiftInfo.put("employeeName", currentStaff.getEmployeeName());
                currentShiftInfo.put("employeeRole", currentStaff.getEmployeeRole());
                currentShiftInfo.put("shift", currentStaff.getShiftType());
                currentShiftInfo.put("avatar", currentStaff.getEmployeeAvatar());
                currentShiftInfo.put("phone", currentStaff.getEmployeePhone());
                currentShiftInfo.put("status", "online");
            } else {
                currentShiftInfo.put("employeeName", "暂无值班人员");
                currentShiftInfo.put("status", "offline");
            }

            return currentShiftInfo;
        } catch (Exception e) {
            logger.error("获取当前值班信息失败", e);
            return new HashMap<>();
        }
    }

    @Override
    public List<CloisonneScheduleStatistics> findEmployeeWorkHours(LocalDate startDate, LocalDate endDate) {
        try {
            Long tenantId = getCurrentTenantId();
            return scheduleMapperEx.selectEmployeeWorkHours(startDate, endDate, tenantId);
        } catch (Exception e) {
            logger.error("查询员工工时统计失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CloisonneScheduleStatistics> findDailyScheduleCount(LocalDate startDate, LocalDate endDate) {
        try {
            Long tenantId = getCurrentTenantId();
            return scheduleMapperEx.selectDailyScheduleCount(startDate, endDate, tenantId);
        } catch (Exception e) {
            logger.error("查询每日排班统计失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CloisonneScheduleStatistics> findShiftDistribution(LocalDate startDate, LocalDate endDate) {
        try {
            Long tenantId = getCurrentTenantId();
            List<CloisonneScheduleStatistics> distribution = scheduleMapperEx.selectShiftDistribution(
                startDate, endDate, tenantId);

            // 计算百分比
            int total = distribution.stream().mapToInt(CloisonneScheduleStatistics::getStatisticsCount).sum();
            if (total > 0) {
                distribution.forEach(item -> {
                    double percentage = (double) item.getStatisticsCount() / total * 100;
                    item.setPercentage(java.math.BigDecimal.valueOf(percentage).setScale(1, java.math.BigDecimal.ROUND_HALF_UP));
                });
            }

            return distribution;
        } catch (Exception e) {
            logger.error("查询班次分布统计失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public boolean createSchedule(CloisonneSchedule schedule) {
        try {
            Long tenantId = getCurrentTenantId();
            Long userId = getCurrentUserId();

            // 设置基础信息
            schedule.setTenantId(tenantId);
            schedule.setCreateUser(userId);
            schedule.setDeleteFlag("0");

            // 业务验证
            if (!validateSchedule(schedule)) {
                return false;
            }

            // 检查是否已存在排班
            if (checkEmployeeScheduleExists(schedule.getEmployeeId(), schedule.getScheduleDate(), null)) {
                logger.warn("员工在该日期已有排班记录: employeeId={}, date={}", 
                    schedule.getEmployeeId(), schedule.getScheduleDate());
                return false;
            }

            // 设置班次时间
            setShiftTime(schedule);

            // 保存排班记录
            boolean success = save(schedule);

            if (success) {
                // 记录操作日志
                logService.insertLogWithUserId(userId, tenantId, "排班管理",
                    "创建排班: " + schedule.getEmployeeName() + " - " + schedule.getScheduleDate() + " - " + schedule.getShiftType(),
                    request);
            }

            return success;
        } catch (Exception e) {
            logger.error("创建排班记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateSchedule(CloisonneSchedule schedule) {
        try {
            Long tenantId = getCurrentTenantId();
            Long userId = getCurrentUserId();

            // 设置更新信息
            schedule.setTenantId(tenantId);
            schedule.setUpdateUser(userId);

            // 业务验证
            if (!validateSchedule(schedule)) {
                return false;
            }

            // 检查是否已存在排班(排除当前记录)
            if (checkEmployeeScheduleExists(schedule.getEmployeeId(), schedule.getScheduleDate(), schedule.getId())) {
                logger.warn("员工在该日期已有其他排班记录: employeeId={}, date={}", 
                    schedule.getEmployeeId(), schedule.getScheduleDate());
                return false;
            }

            // 设置班次时间
            setShiftTime(schedule);

            // 更新排班记录
            boolean success = updateById(schedule);

            if (success) {
                // 记录操作日志
                logService.insertLogWithUserId(userId, tenantId, "排班管理",
                    "更新排班: " + schedule.getEmployeeName() + " - " + schedule.getScheduleDate() + " - " + schedule.getShiftType(),
                    request);
            }

            return success;
        } catch (Exception e) {
            logger.error("更新排班记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteSchedule(Long id) {
        try {
            Long tenantId = getCurrentTenantId();
            Long userId = getCurrentUserId();

            // 获取排班记录
            CloisonneSchedule schedule = getById(id);
            if (schedule == null || !schedule.getTenantId().equals(tenantId)) {
                return false;
            }

            // 软删除
            schedule.setDeleteFlag("1");
            schedule.setUpdateUser(userId);
            boolean success = updateById(schedule);

            if (success) {
                // 记录操作日志
                logService.insertLogWithUserId(userId, tenantId, "排班管理",
                    "删除排班: " + schedule.getEmployeeName() + " - " + schedule.getScheduleDate(),
                    request);
            }

            return success;
        } catch (Exception e) {
            logger.error("删除排班记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchCreateSchedule(List<CloisonneSchedule> scheduleList) {
        try {
            Long tenantId = getCurrentTenantId();
            Long userId = getCurrentUserId();

            // 设置基础信息
            scheduleList.forEach(schedule -> {
                schedule.setTenantId(tenantId);
                schedule.setCreateUser(userId);
                schedule.setDeleteFlag("0");
                setShiftTime(schedule);
            });

            // 批量保存
            boolean success = saveBatch(scheduleList);

            if (success) {
                // 记录操作日志
                logService.insertLogWithUserId(userId, tenantId, "排班管理",
                    "批量创建排班: " + scheduleList.size() + "条记录",
                    request);
            }

            return success;
        } catch (Exception e) {
            logger.error("批量创建排班记录失败", e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean batchUpdateScheduleStatus(List<Long> ids, String status) {
        try {
            Long tenantId = getCurrentTenantId();
            Long userId = getCurrentUserId();

            int updateCount = scheduleMapperEx.batchUpdateScheduleStatus(ids, status, userId, tenantId);

            if (updateCount > 0) {
                // 记录操作日志
                logService.insertLogWithUserId(userId, tenantId, "排班管理",
                    "批量更新排班状态: " + updateCount + "条记录更新为" + status,
                    request);
            }

            return updateCount > 0;
        } catch (Exception e) {
            logger.error("批量更新排班状态失败", e);
            return false;
        }
    }

    @Override
    public boolean checkEmployeeScheduleExists(Long employeeId, LocalDate scheduleDate, Long excludeId) {
        try {
            Long tenantId = getCurrentTenantId();
            int count = scheduleMapperEx.checkEmployeeScheduleExists(employeeId, scheduleDate, excludeId, tenantId);
            return count > 0;
        } catch (Exception e) {
            logger.error("检查员工排班是否存在失败", e);
            return false;
        }
    }

    @Override
    public List<CloisonneSchedule> checkScheduleTimeConflict(Long employeeId, LocalDate scheduleDate, 
                                                           String startTime, String endTime, Long excludeId) {
        try {
            Long tenantId = getCurrentTenantId();
            return scheduleMapperEx.selectConflictSchedule(employeeId, scheduleDate, startTime, endTime, excludeId, tenantId);
        } catch (Exception e) {
            logger.error("检查排班时间冲突失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<CloisonneScheduleVo4List> findRecentScheduleByEmployee(Long employeeId, Integer limit) {
        try {
            Long tenantId = getCurrentTenantId();
            return scheduleMapperEx.selectRecentScheduleByEmployee(employeeId, limit, tenantId);
        } catch (Exception e) {
            logger.error("查询员工最近排班失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 增强排班VO数据
     */
    private CloisonneScheduleVo4List enhanceScheduleVo(CloisonneScheduleVo4List vo) {
        if (vo.getScheduleDate() != null) {
            LocalDate date = vo.getScheduleDate();
            
            // 设置星期信息
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            vo.setDayOfWeek(dayOfWeek.getValue());
            vo.setDayOfWeekName(getDayOfWeekName(dayOfWeek));
            
            // 设置是否今天
            vo.setIsToday(date.equals(LocalDate.now()));
            
            // 设置是否周末
            vo.setIsWeekend(dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY);
        }

        // 设置状态显示名称
        vo.setStatusName(getStatusName(vo.getStatus()));

        // 设置优先级显示名称
        vo.setPriorityName(getPriorityName(vo.getPriority()));

        return vo;
    }

    /**
     * 判断是否当前班次
     */
    private boolean isCurrentShift(CloisonneScheduleVo4List staff, LocalTime now) {
        if (staff.getStartTime() != null && staff.getEndTime() != null) {
            return !now.isBefore(staff.getStartTime()) && !now.isAfter(staff.getEndTime());
        }
        return false;
    }

    /**
     * 验证排班数据
     */
    private boolean validateSchedule(CloisonneSchedule schedule) {
        if (schedule.getEmployeeId() == null) {
            logger.warn("员工ID不能为空");
            return false;
        }
        if (schedule.getScheduleDate() == null) {
            logger.warn("排班日期不能为空");
            return false;
        }
        if (StringUtil.isEmpty(schedule.getShiftType())) {
            logger.warn("班次类型不能为空");
            return false;
        }
        return true;
    }

    /**
     * 设置班次时间
     */
    private void setShiftTime(CloisonneSchedule schedule) {
        Map<String, String[]> shiftTimes = getShiftTimeConfig();
        String[] times = shiftTimes.get(schedule.getShiftType());
        
        if (times != null && times.length == 2) {
            schedule.setStartTime(LocalTime.parse(times[0]));
            schedule.setEndTime(LocalTime.parse(times[1]));
            
            // 计算工作时长
            long hours = java.time.Duration.between(schedule.getStartTime(), schedule.getEndTime()).toHours();
            schedule.setWorkHours(java.math.BigDecimal.valueOf(hours));
        }
    }

    /**
     * 获取班次时间配置
     */
    private Map<String, String[]> getShiftTimeConfig() {
        Map<String, String[]> config = new HashMap<>();
        config.put("早班", new String[]{"08:00", "16:00"});
        config.put("中班", new String[]{"12:00", "20:00"});
        config.put("晚班", new String[]{"16:00", "24:00"});
        config.put("夜班", new String[]{"00:00", "08:00"});
        config.put("全天", new String[]{"08:00", "24:00"});
        return config;
    }

    /**
     * 获取星期名称
     */
    private String getDayOfWeekName(DayOfWeek dayOfWeek) {
        switch (dayOfWeek) {
            case MONDAY: return "周一";
            case TUESDAY: return "周二";
            case WEDNESDAY: return "周三";
            case THURSDAY: return "周四";
            case FRIDAY: return "周五";
            case SATURDAY: return "周六";
            case SUNDAY: return "周日";
            default: return "";
        }
    }

    /**
     * 获取状态显示名称
     */
    private String getStatusName(String status) {
        if (StringUtil.isEmpty(status)) return "";

        switch (status) {
            case "normal": return "正常";
            case "leave": return "请假";
            case "swap": return "调班";
            case "absent": return "缺勤";
            default: return status;
        }
    }

    /**
     * 获取优先级显示名称
     */
    private String getPriorityName(String priority) {
        if (StringUtil.isEmpty(priority)) return "";

        switch (priority) {
            case "low": return "低";
            case "normal": return "普通";
            case "high": return "高";
            case "urgent": return "紧急";
            default: return priority;
        }
    }

    // 其他未实现的方法...
    @Override
    public boolean copyScheduleTemplate(LocalDate sourceStartDate, LocalDate sourceEndDate, LocalDate targetStartDate) {
        // TODO: 实现排班模板复制
        return false;
    }

    @Override
    public Map<String, Object> generateScheduleReport(LocalDate startDate, LocalDate endDate, String reportType) {
        // TODO: 实现排班报表生成
        return new HashMap<>();
    }

    @Override
    public String exportScheduleData(LocalDate startDate, LocalDate endDate, String exportType) {
        // TODO: 实现排班数据导出
        return "";
    }

    @Override
    public Map<String, Object> getScheduleConfig() {
        // TODO: 实现排班配置获取
        return new HashMap<>();
    }

    @Override
    public boolean updateScheduleConfig(Map<String, Object> config) {
        // TODO: 实现排班配置更新
        return false;
    }
}
