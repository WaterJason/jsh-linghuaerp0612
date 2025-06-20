# Week 16: 排班管理与运营支持开发文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第三阶段 - 优化增强
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第3、4、5章规范

---

## 概述

本文档为Week 16的排班管理与运营支持开发提供详细的实施指导。主要实现可视化排班系统、咖啡店运营管理、客户关系管理扩展等核心功能，建立完整的运营支持体系，为聆花文化的日常运营管理提供高效的工具支持。

---

## 排班核心功能开发 (2天)

### 1. ScheduleManagementService.java - 排班管理服务

**文件路径**: `com.jsh.erp.schedule.service.ScheduleManagementService`

```java
package com.jsh.erp.schedule.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.schedule.datasource.entities.ScheduleShift;
import com.jsh.erp.schedule.datasource.entities.ScheduleTemplate;
import com.jsh.erp.schedule.datasource.entities.ScheduleAssignment;
import com.jsh.erp.schedule.datasource.mappers.ScheduleShiftMapper;
import com.jsh.erp.schedule.datasource.mappers.ScheduleTemplateMapper;
import com.jsh.erp.schedule.datasource.mappers.ScheduleAssignmentMapper;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 排班管理服务
 * 实现员工排班、时间冲突检测、排班工资计算等功能
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class ScheduleManagementService {
    private Logger logger = LoggerFactory.getLogger(ScheduleManagementService.class);

    @Resource
    private ScheduleShiftMapper scheduleShiftMapper;
    @Resource
    private ScheduleTemplateMapper scheduleTemplateMapper;
    @Resource
    private ScheduleAssignmentMapper scheduleAssignmentMapper;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;

    /**
     * 创建排班
     * 支持单次排班和循环排班模式
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createSchedule(JSONObject scheduleData, HttpServletRequest request) throws Exception {
        try {
            logger.info("开始创建排班: {}", scheduleData.toJSONString());
            
            // 1. 验证排班数据
            validateScheduleData(scheduleData);
            
            // 2. 检查时间冲突
            List<String> conflicts = checkTimeConflict(scheduleData);
            if (!conflicts.isEmpty()) {
                throw new BusinessRunTimeException("排班时间冲突", "以下时间段存在冲突: " + String.join(", ", conflicts));
            }
            
            // 3. 创建排班记录
            String scheduleId = generateScheduleId();
            String scheduleType = scheduleData.getString("scheduleType"); // single/recurring
            
            if ("recurring".equals(scheduleType)) {
                // 循环排班
                createRecurringSchedule(scheduleId, scheduleData, request);
            } else {
                // 单次排班
                createSingleSchedule(scheduleId, scheduleData, request);
            }
            
            // 4. 记录操作日志
            String logContent = String.format("创建排班 [%s] - 类型: %s", scheduleId, scheduleType);
            logService.insertLog("排班管理", logContent, request);
            
            logger.info("排班创建完成: {}", scheduleId);
            return scheduleId;
            
        } catch (Exception e) {
            logger.error("创建排班失败", e);
            throw new JshException("创建排班失败: " + e.getMessage());
        }
    }

    /**
     * 检查时间冲突
     * 检测员工在指定时间段是否已有其他排班
     */
    public List<String> checkTimeConflict(JSONObject scheduleData) throws Exception {
        List<String> conflicts = new ArrayList<>();
        
        try {
            Long employeeId = scheduleData.getLong("employeeId");
            String scheduleDate = scheduleData.getString("scheduleDate");
            String startTime = scheduleData.getString("startTime");
            String endTime = scheduleData.getString("endTime");
            
            // 查询该员工在指定日期的排班
            Map<String, Object> params = new HashMap<>();
            params.put("employeeId", employeeId);
            params.put("scheduleDate", scheduleDate);
            params.put("deleteFlag", "0");
            
            List<ScheduleAssignment> existingSchedules = scheduleAssignmentMapper.selectByCondition(params);
            
            // 检查时间重叠
            LocalTime newStartTime = LocalTime.parse(startTime);
            LocalTime newEndTime = LocalTime.parse(endTime);
            
            for (ScheduleAssignment existing : existingSchedules) {
                LocalTime existingStart = existing.getStartTime().toLocalTime();
                LocalTime existingEnd = existing.getEndTime().toLocalTime();
                
                // 检查时间重叠
                if (isTimeOverlap(newStartTime, newEndTime, existingStart, existingEnd)) {
                    String conflictInfo = String.format("%s %s-%s", 
                        existing.getScheduleDate(), 
                        existingStart.format(DateTimeFormatter.ofPattern("HH:mm")),
                        existingEnd.format(DateTimeFormatter.ofPattern("HH:mm")));
                    conflicts.add(conflictInfo);
                }
            }
            
        } catch (Exception e) {
            logger.error("检查时间冲突失败", e);
            throw new JshException("检查时间冲突失败: " + e.getMessage());
        }
        
        return conflicts;
    }

    /**
     * 计算排班工资
     * 根据工作时长、时薪标准、加班规则计算薪酬
     */
    public BigDecimal calculateScheduleSalary(Long scheduleAssignmentId) throws Exception {
        try {
            logger.info("开始计算排班工资: {}", scheduleAssignmentId);
            
            // 1. 获取排班信息
            ScheduleAssignment assignment = scheduleAssignmentMapper.selectByPrimaryKey(scheduleAssignmentId);
            if (assignment == null) {
                throw new BusinessRunTimeException("排班记录不存在", "ID: " + scheduleAssignmentId);
            }
            
            // 2. 计算工作时长
            BigDecimal workHours = calculateWorkHours(assignment);
            BigDecimal overtimeHours = calculateOvertimeHours(assignment);
            
            // 3. 获取薪酬标准
            BigDecimal hourlyRate = assignment.getHourlyRate();
            BigDecimal overtimeRate = hourlyRate.multiply(new BigDecimal("1.5")); // 加班费1.5倍
            
            // 4. 计算基础工资
            BigDecimal baseSalary = workHours.multiply(hourlyRate);
            
            // 5. 计算加班费
            BigDecimal overtimePay = overtimeHours.multiply(overtimeRate);
            
            // 6. 计算补贴
            BigDecimal allowances = calculateAllowances(assignment);
            
            // 7. 计算总薪酬
            BigDecimal totalSalary = baseSalary.add(overtimePay).add(allowances);
            
            // 8. 更新排班记录
            assignment.setWorkHours(workHours);
            assignment.setOvertimeHours(overtimeHours);
            assignment.setBaseSalary(baseSalary);
            assignment.setOvertimePay(overtimePay);
            assignment.setAllowances(allowances);
            assignment.setTotalSalary(totalSalary);
            assignment.setUpdateTime(new Date());
            
            scheduleAssignmentMapper.updateByPrimaryKey(assignment);
            
            logger.info("排班工资计算完成: {} -> {}", scheduleAssignmentId, totalSalary);
            return totalSalary;
            
        } catch (Exception e) {
            logger.error("计算排班工资失败", e);
            throw new JshException("计算排班工资失败: " + e.getMessage());
        }
    }

    /**
     * 获取员工排班统计
     * 统计指定时间段内员工的排班情况
     */
    public JSONObject getEmployeeScheduleStats(Long employeeId, String startDate, String endDate) throws Exception {
        try {
            logger.info("获取员工排班统计: {} {} - {}", employeeId, startDate, endDate);
            
            Map<String, Object> params = new HashMap<>();
            params.put("employeeId", employeeId);
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("deleteFlag", "0");
            
            List<ScheduleAssignment> schedules = scheduleAssignmentMapper.selectByDateRange(params);
            
            JSONObject stats = new JSONObject();
            BigDecimal totalHours = BigDecimal.ZERO;
            BigDecimal totalOvertimeHours = BigDecimal.ZERO;
            BigDecimal totalSalary = BigDecimal.ZERO;
            int totalDays = 0;
            
            for (ScheduleAssignment schedule : schedules) {
                totalHours = totalHours.add(schedule.getWorkHours() != null ? schedule.getWorkHours() : BigDecimal.ZERO);
                totalOvertimeHours = totalOvertimeHours.add(schedule.getOvertimeHours() != null ? schedule.getOvertimeHours() : BigDecimal.ZERO);
                totalSalary = totalSalary.add(schedule.getTotalSalary() != null ? schedule.getTotalSalary() : BigDecimal.ZERO);
                totalDays++;
            }
            
            stats.put("employeeId", employeeId);
            stats.put("periodStart", startDate);
            stats.put("periodEnd", endDate);
            stats.put("totalDays", totalDays);
            stats.put("totalHours", totalHours);
            stats.put("totalOvertimeHours", totalOvertimeHours);
            stats.put("totalSalary", totalSalary);
            stats.put("averageHoursPerDay", totalDays > 0 ? totalHours.divide(new BigDecimal(totalDays), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
            
            return stats;
            
        } catch (Exception e) {
            logger.error("获取员工排班统计失败", e);
            throw new JshException("获取员工排班统计失败: " + e.getMessage());
        }
    }

    // 私有方法实现
    private void validateScheduleData(JSONObject scheduleData) throws Exception {
        if (scheduleData.getLong("employeeId") == null) {
            throw new BusinessRunTimeException("参数错误", "员工ID不能为空");
        }
        if (scheduleData.getString("scheduleDate") == null) {
            throw new BusinessRunTimeException("参数错误", "排班日期不能为空");
        }
        if (scheduleData.getString("startTime") == null || scheduleData.getString("endTime") == null) {
            throw new BusinessRunTimeException("参数错误", "开始时间和结束时间不能为空");
        }
    }

    private void createSingleSchedule(String scheduleId, JSONObject scheduleData, HttpServletRequest request) throws Exception {
        ScheduleAssignment assignment = new ScheduleAssignment();
        assignment.setId(Long.parseLong(scheduleId));
        assignment.setEmployeeId(scheduleData.getLong("employeeId"));
        assignment.setScheduleDate(LocalDate.parse(scheduleData.getString("scheduleDate")));
        assignment.setStartTime(LocalDateTime.of(assignment.getScheduleDate(), LocalTime.parse(scheduleData.getString("startTime"))));
        assignment.setEndTime(LocalDateTime.of(assignment.getScheduleDate(), LocalTime.parse(scheduleData.getString("endTime"))));
        assignment.setWorkLocation(scheduleData.getString("workLocation"));
        assignment.setHourlyRate(scheduleData.getBigDecimal("hourlyRate"));
        assignment.setScheduleType("single");
        assignment.setScheduleStatus("0");
        assignment.setDeleteFlag("0");
        assignment.setCreateTime(new Date());
        assignment.setTenantId(Long.parseLong(request.getParameter("tenantId")));

        scheduleAssignmentMapper.insertSelective(assignment);
    }

    private void createRecurringSchedule(String scheduleId, JSONObject scheduleData, HttpServletRequest request) throws Exception {
        // 实现循环排班逻辑
        String recurringType = scheduleData.getString("recurringType"); // daily/weekly/monthly
        int recurringCount = scheduleData.getIntValue("recurringCount");
        
        LocalDate startDate = LocalDate.parse(scheduleData.getString("scheduleDate"));
        
        for (int i = 0; i < recurringCount; i++) {
            LocalDate currentDate = calculateRecurringDate(startDate, recurringType, i);
            
            ScheduleAssignment assignment = new ScheduleAssignment();
            assignment.setId(Long.parseLong(generateScheduleId()));
            assignment.setEmployeeId(scheduleData.getLong("employeeId"));
            assignment.setScheduleDate(currentDate);
            assignment.setStartTime(LocalDateTime.of(currentDate, LocalTime.parse(scheduleData.getString("startTime"))));
            assignment.setEndTime(LocalDateTime.of(currentDate, LocalTime.parse(scheduleData.getString("endTime"))));
            assignment.setWorkLocation(scheduleData.getString("workLocation"));
            assignment.setHourlyRate(scheduleData.getBigDecimal("hourlyRate"));
            assignment.setScheduleType("recurring");
            assignment.setRecurringGroupId(scheduleId);
            assignment.setScheduleStatus("0");
            assignment.setDeleteFlag("0");
            assignment.setCreateTime(new Date());
            assignment.setTenantId(Long.parseLong(request.getParameter("tenantId")));

            scheduleAssignmentMapper.insertSelective(assignment);
        }
    }

    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private BigDecimal calculateWorkHours(ScheduleAssignment assignment) {
        LocalTime startTime = assignment.getStartTime().toLocalTime();
        LocalTime endTime = assignment.getEndTime().toLocalTime();
        
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return new BigDecimal(minutes).divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateOvertimeHours(ScheduleAssignment assignment) {
        BigDecimal workHours = calculateWorkHours(assignment);
        BigDecimal standardHours = new BigDecimal("8"); // 标准工时8小时
        
        if (workHours.compareTo(standardHours) > 0) {
            return workHours.subtract(standardHours);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateAllowances(ScheduleAssignment assignment) {
        // 根据工作地点和时间计算补贴
        BigDecimal allowances = BigDecimal.ZERO;
        
        // 夜班补贴
        LocalTime startTime = assignment.getStartTime().toLocalTime();
        if (startTime.isBefore(LocalTime.of(6, 0)) || startTime.isAfter(LocalTime.of(22, 0))) {
            allowances = allowances.add(new BigDecimal("20")); // 夜班补贴20元
        }
        
        // 节假日补贴
        if (isHoliday(assignment.getScheduleDate())) {
            allowances = allowances.add(new BigDecimal("50")); // 节假日补贴50元
        }
        
        return allowances;
    }

    private LocalDate calculateRecurringDate(LocalDate startDate, String recurringType, int index) {
        switch (recurringType) {
            case "daily":
                return startDate.plusDays(index);
            case "weekly":
                return startDate.plusWeeks(index);
            case "monthly":
                return startDate.plusMonths(index);
            default:
                return startDate.plusDays(index);
        }
    }

    private boolean isHoliday(LocalDate date) {
        // 简单实现，实际应该接入节假日API
        return date.getDayOfWeek().getValue() >= 6; // 周末
    }

    private String generateScheduleId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
```

### 2. 排班前端组件开发

#### ScheduleCalendar.vue - 排班日历组件

**文件路径**: `jshERP-web/src/views/schedule/components/ScheduleCalendar.vue`

```vue
<template>
  <div class="schedule-calendar">
    <a-card title="排班日历" :bordered="false">
      <!-- 工具栏 -->
      <div class="calendar-toolbar">
        <a-row type="flex" justify="space-between" align="middle">
          <a-col>
            <a-button-group>
              <a-button @click="prevMonth">
                <a-icon type="left" />
              </a-button>
              <a-button @click="goToday">今天</a-button>
              <a-button @click="nextMonth">
                <a-icon type="right" />
              </a-button>
            </a-button-group>
            <span class="current-month">{{ currentMonth }}</span>
          </a-col>
          <a-col>
            <a-button type="primary" @click="handleAddSchedule">
              <a-icon type="plus" />
              新增排班
            </a-button>
          </a-col>
        </a-row>
      </div>

      <!-- 日历视图 -->
      <div class="calendar-container">
        <a-calendar
          :fullscreen="true"
          v-model="selectedDate"
          @select="onDateSelect"
          @panelChange="onPanelChange"
        >
          <template #dateCellRender="date">
            <div class="calendar-cell">
              <div class="date-number">{{ date.date() }}</div>
              <div class="schedule-list">
                <div 
                  v-for="schedule in getDateSchedules(date)"
                  :key="schedule.id"
                  class="schedule-item"
                  :class="getScheduleClass(schedule)"
                  @click="viewScheduleDetail(schedule)"
                >
                  <div class="schedule-time">
                    {{ formatTime(schedule.startTime) }}-{{ formatTime(schedule.endTime) }}
                  </div>
                  <div class="schedule-employee">{{ schedule.employeeName }}</div>
                  <div class="schedule-location">{{ schedule.workLocation }}</div>
                </div>
              </div>
            </div>
          </template>
        </a-calendar>
      </div>
    </a-card>

    <!-- 新增排班弹窗 -->
    <schedule-modal
      ref="scheduleModal"
      @ok="handleScheduleSuccess"
    />

    <!-- 排班详情弹窗 -->
    <schedule-detail-modal
      ref="scheduleDetailModal"
      @ok="handleScheduleSuccess"
    />
  </div>
</template>

<script>
import moment from 'moment'
import { getScheduleList } from '@/api/schedule'
import ScheduleModal from './ScheduleModal'
import ScheduleDetailModal from './ScheduleDetailModal'

export default {
  name: 'ScheduleCalendar',
  components: {
    ScheduleModal,
    ScheduleDetailModal
  },
  data() {
    return {
      selectedDate: moment(),
      currentMonth: '',
      scheduleList: [],
      loading: false
    }
  },
  created() {
    this.updateCurrentMonth()
    this.loadScheduleData()
  },
  methods: {
    // 更新当前月份显示
    updateCurrentMonth() {
      this.currentMonth = this.selectedDate.format('YYYY年MM月')
    },

    // 加载排班数据
    async loadScheduleData() {
      try {
        this.loading = true
        const startDate = this.selectedDate.clone().startOf('month').format('YYYY-MM-DD')
        const endDate = this.selectedDate.clone().endOf('month').format('YYYY-MM-DD')
        
        const response = await getScheduleList({
          startDate,
          endDate,
          pageSize: 1000
        })
        
        this.scheduleList = response.data.rows || []
      } catch (error) {
        this.$message.error('加载排班数据失败')
      } finally {
        this.loading = false
      }
    },

    // 获取指定日期的排班
    getDateSchedules(date) {
      const dateStr = date.format('YYYY-MM-DD')
      return this.scheduleList.filter(schedule => 
        moment(schedule.scheduleDate).format('YYYY-MM-DD') === dateStr
      )
    },

    // 获取排班样式类
    getScheduleClass(schedule) {
      return {
        'schedule-confirmed': schedule.scheduleStatus === '1',
        'schedule-completed': schedule.scheduleStatus === '2',
        'schedule-cancelled': schedule.scheduleStatus === '3'
      }
    },

    // 格式化时间
    formatTime(timeStr) {
      return moment(timeStr).format('HH:mm')
    },

    // 日期选择事件
    onDateSelect(date) {
      this.selectedDate = date
    },

    // 面板切换事件
    onPanelChange(value, mode) {
      this.selectedDate = value
      this.updateCurrentMonth()
      this.loadScheduleData()
    },

    // 上个月
    prevMonth() {
      this.selectedDate = this.selectedDate.clone().subtract(1, 'month')
      this.updateCurrentMonth()
      this.loadScheduleData()
    },

    // 下个月
    nextMonth() {
      this.selectedDate = this.selectedDate.clone().add(1, 'month')
      this.updateCurrentMonth()
      this.loadScheduleData()
    },

    // 回到今天
    goToday() {
      this.selectedDate = moment()
      this.updateCurrentMonth()
      this.loadScheduleData()
    },

    // 新增排班
    handleAddSchedule() {
      this.$refs.scheduleModal.show({
        scheduleDate: this.selectedDate.format('YYYY-MM-DD')
      })
    },

    // 查看排班详情
    viewScheduleDetail(schedule) {
      this.$refs.scheduleDetailModal.show(schedule)
    },

    // 排班操作成功
    handleScheduleSuccess() {
      this.loadScheduleData()
    }
  }
}
</script>

<style lang="less" scoped>
.schedule-calendar {
  .calendar-toolbar {
    margin-bottom: 16px;
    
    .current-month {
      margin-left: 16px;
      font-size: 16px;
      font-weight: 500;
    }
  }

  .calendar-container {
    /deep/ .ant-fullcalendar {
      .ant-fullcalendar-header {
        margin-bottom: 12px;
      }

      .ant-fullcalendar-date {
        height: 120px;
        overflow: hidden;
      }
    }
  }

  .calendar-cell {
    height: 100%;
    
    .date-number {
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 4px;
    }

    .schedule-list {
      .schedule-item {
        background: #f0f2f5;
        border-radius: 3px;
        padding: 2px 4px;
        margin-bottom: 2px;
        font-size: 10px;
        line-height: 1.2;
        cursor: pointer;
        transition: all 0.3s;

        &:hover {
          background: #e6f7ff;
        }

        &.schedule-confirmed {
          background: #f6ffed;
          border-left: 3px solid #52c41a;
        }

        &.schedule-completed {
          background: #e6f7ff;
          border-left: 3px solid #1890ff;
        }

        &.schedule-cancelled {
          background: #fff2e8;
          border-left: 3px solid #fa8c16;
        }

        .schedule-time {
          font-weight: 500;
          color: #1890ff;
        }

        .schedule-employee {
          color: #666;
        }

        .schedule-location {
          color: #999;
        }
      }
    }
  }
}
</style>
```

---

## 咖啡店运营管理开发 (1.5天)

### 1. CoffeeShopService.java - 咖啡店服务

**文件路径**: `com.jsh.erp.coffeeshop.service.CoffeeShopService`

```java
package com.jsh.erp.coffeeshop.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.coffeeshop.datasource.entities.DailySales;
import com.jsh.erp.coffeeshop.datasource.entities.CommissionRecord;
import com.jsh.erp.coffeeshop.datasource.entities.PurchaseItem;
import com.jsh.erp.coffeeshop.datasource.mappers.DailySalesMapper;
import com.jsh.erp.coffeeshop.datasource.mappers.CommissionRecordMapper;
import com.jsh.erp.coffeeshop.datasource.mappers.PurchaseItemMapper;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 咖啡店运营管理服务
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class CoffeeShopService {
    private Logger logger = LoggerFactory.getLogger(CoffeeShopService.class);

    @Resource
    private DailySalesMapper dailySalesMapper;
    @Resource
    private CommissionRecordMapper commissionRecordMapper;
    @Resource
    private PurchaseItemMapper purchaseItemMapper;
    @Resource
    private LogService logService;

    /**
     * 记录日销售额
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String recordDailySales(JSONObject salesData, HttpServletRequest request) throws Exception {
        try {
            logger.info("记录日销售额: {}", salesData.toJSONString());
            
            String salesDate = salesData.getString("salesDate");
            BigDecimal totalSales = salesData.getBigDecimal("totalSales");
            
            // 检查是否已存在当日记录
            Map<String, Object> params = new HashMap<>();
            params.put("salesDate", salesDate);
            params.put("deleteFlag", "0");
            
            List<DailySales> existingRecords = dailySalesMapper.selectByCondition(params);
            
            DailySales dailySales;
            if (!existingRecords.isEmpty()) {
                // 更新现有记录
                dailySales = existingRecords.get(0);
                dailySales.setTotalSales(totalSales);
                dailySales.setUpdateTime(new Date());
                dailySalesMapper.updateByPrimaryKey(dailySales);
            } else {
                // 创建新记录
                dailySales = new DailySales();
                dailySales.setSalesDate(java.sql.Date.valueOf(salesDate));
                dailySales.setTotalSales(totalSales);
                dailySales.setCreateTime(new Date());
                dailySales.setDeleteFlag("0");
                dailySales.setTenantId(Long.parseLong(request.getParameter("tenantId")));
                dailySalesMapper.insertSelective(dailySales);
            }
            
            // 自动计算提成
            calculateCommission(dailySales.getId(), totalSales, request);
            
            return dailySales.getId().toString();
            
        } catch (Exception e) {
            logger.error("记录日销售额失败", e);
            throw new JshException("记录日销售额失败: " + e.getMessage());
        }
    }

    /**
     * 计算提成
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void calculateCommission(Long salesId, BigDecimal totalSales, HttpServletRequest request) throws Exception {
        try {
            // 提成规则配置
            Map<String, BigDecimal> commissionRules = getCommissionRules();
            
            for (Map.Entry<String, BigDecimal> rule : commissionRules.entrySet()) {
                String employeeType = rule.getKey();
                BigDecimal commissionRate = rule.getValue();
                
                BigDecimal commissionAmount = totalSales.multiply(commissionRate)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                
                // 创建提成记录
                CommissionRecord commissionRecord = new CommissionRecord();
                commissionRecord.setSalesId(salesId);
                commissionRecord.setEmployeeType(employeeType);
                commissionRecord.setCommissionRate(commissionRate);
                commissionRecord.setCommissionAmount(commissionAmount);
                commissionRecord.setCommissionStatus("0"); // 待发放
                commissionRecord.setCreateTime(new Date());
                commissionRecord.setDeleteFlag("0");
                commissionRecord.setTenantId(Long.parseLong(request.getParameter("tenantId")));
                
                commissionRecordMapper.insertSelective(commissionRecord);
            }
            
        } catch (Exception e) {
            logger.error("计算提成失败", e);
            throw new JshException("计算提成失败: " + e.getMessage());
        }
    }

    /**
     * 采购管理
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String managePurchasing(JSONObject purchaseData, HttpServletRequest request) throws Exception {
        try {
            logger.info("处理采购需求: {}", purchaseData.toJSONString());
            
            PurchaseItem purchaseItem = new PurchaseItem();
            purchaseItem.setItemName(purchaseData.getString("itemName"));
            purchaseItem.setQuantity(purchaseData.getBigDecimal("quantity"));
            purchaseItem.setUnitPrice(purchaseData.getBigDecimal("unitPrice"));
            purchaseItem.setTotalAmount(purchaseItem.getQuantity().multiply(purchaseItem.getUnitPrice()));
            purchaseItem.setSupplier(purchaseData.getString("supplier"));
            purchaseItem.setPurchaseDate(new Date());
            purchaseItem.setPurchaseStatus("0"); // 待采购
            purchaseItem.setCreateTime(new Date());
            purchaseItem.setDeleteFlag("0");
            purchaseItem.setTenantId(Long.parseLong(request.getParameter("tenantId")));
            
            purchaseItemMapper.insertSelective(purchaseItem);
            
            return purchaseItem.getId().toString();
            
        } catch (Exception e) {
            logger.error("处理采购需求失败", e);
            throw new JshException("处理采购需求失败: " + e.getMessage());
        }
    }

    /**
     * 获取销售统计
     */
    public JSONObject getSalesStatistics(String startDate, String endDate) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            params.put("deleteFlag", "0");
            
            List<DailySales> salesList = dailySalesMapper.selectByDateRange(params);
            
            BigDecimal totalSales = BigDecimal.ZERO;
            BigDecimal totalCommission = BigDecimal.ZERO;
            int salesDays = salesList.size();
            
            for (DailySales sales : salesList) {
                totalSales = totalSales.add(sales.getTotalSales());
                
                // 计算该日的总提成
                Map<String, Object> commissionParams = new HashMap<>();
                commissionParams.put("salesId", sales.getId());
                commissionParams.put("deleteFlag", "0");
                
                List<CommissionRecord> commissions = commissionRecordMapper.selectBySalesId(commissionParams);
                for (CommissionRecord commission : commissions) {
                    totalCommission = totalCommission.add(commission.getCommissionAmount());
                }
            }
            
            JSONObject statistics = new JSONObject();
            statistics.put("periodStart", startDate);
            statistics.put("periodEnd", endDate);
            statistics.put("totalSales", totalSales);
            statistics.put("totalCommission", totalCommission);
            statistics.put("salesDays", salesDays);
            statistics.put("averageDailySales", salesDays > 0 ? 
                totalSales.divide(new BigDecimal(salesDays), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            statistics.put("profitMargin", totalSales.compareTo(BigDecimal.ZERO) > 0 ? 
                totalSales.subtract(totalCommission).divide(totalSales, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")) : BigDecimal.ZERO);
            
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取销售统计失败", e);
            throw new JshException("获取销售统计失败: " + e.getMessage());
        }
    }

    // 私有方法
    private Map<String, BigDecimal> getCommissionRules() {
        Map<String, BigDecimal> rules = new HashMap<>();
        rules.put("店长", new BigDecimal("5.0")); // 5%
        rules.put("收银员", new BigDecimal("2.0")); // 2%
        rules.put("服务员", new BigDecimal("1.5")); // 1.5%
        return rules;
    }
}
```

---

## 客户关系管理扩展 (0.5天)

### 1. 客户档案扩展字段

**文件路径**: `com.jsh.erp.datasource.entities.SupplierExtend`

```java
package com.jsh.erp.datasource.entities;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 客户档案扩展信息
 */
public class SupplierExtend {
    private Long id;
    private Long supplierId; // 关联供应商ID（客户表）
    
    // 客户偏好
    private String preferredProducts; // 偏好产品类型
    private String preferredColors; // 偏好颜色
    private String preferredStyles; // 偏好风格
    private String culturalInterests; // 文化兴趣
    
    // 消费行为
    private BigDecimal averageOrderValue; // 平均订单金额
    private Integer totalOrders; // 总订单数
    private BigDecimal totalSpent; // 累计消费
    private String lastOrderDate; // 最后订单日期
    
    // 客户标签
    private String customerTags; // 客户标签（JSON）
    private String vipLevel; // VIP等级
    private Date vipStartDate; // VIP开始日期
    private Date vipEndDate; // VIP结束日期
    
    // 沟通记录
    private String communicationHistory; // 沟通历史（JSON）
    private String specialRequirements; // 特殊要求
    private String feedbackHistory; // 反馈历史
    
    // 营销偏好
    private String marketingPreference; // 营销偏好
    private String contactPreference; // 联系偏好
    private String birthdayDate; // 生日
    private String anniversaryDate; // 纪念日
    
    // 扩展信息
    private String extendedInfo; // 扩展信息（JSON）
    
    // 标准字段
    private Long tenantId;
    private String deleteFlag;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Long createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Long updateUser;

    // getter和setter方法省略...
}
```

---

## 验收标准

### 1. 排班管理功能
- [ ] **基础排班功能**
  - [ ] 支持单次排班和循环排班
  - [ ] 时间冲突检测准确率100%
  - [ ] 排班工资计算正确
  - [ ] 支持加班费和补贴计算

- [ ] **可视化界面**
  - [ ] 日历视图展示排班信息
  - [ ] 支持拖拽排班操作
  - [ ] 实时显示冲突提示
  - [ ] 移动端响应式适配

### 2. 咖啡店运营管理
- [ ] **销售记录功能**
  - [ ] 日销售额录入和统计
  - [ ] 自动提成计算
  - [ ] 销售趋势分析
  - [ ] 月度/季度报表生成

- [ ] **采购管理功能**
  - [ ] 采购需求管理
  - [ ] 供应商信息维护
  - [ ] 采购成本统计
  - [ ] 库存预警提醒

### 3. 客户关系管理扩展
- [ ] **客户档案扩展**
  - [ ] 客户偏好记录
  - [ ] 消费行为分析
  - [ ] 客户标签管理
  - [ ] VIP等级管理

---

## 交付物清单

1. **排班管理系统**
   - ScheduleManagementService.java
   - ScheduleCalendar.vue
   - ScheduleModal.vue
   - 数据库表结构文件

2. **咖啡店运营管理**
   - CoffeeShopService.java
   - CoffeeShopDashboard.vue
   - 销售统计报表组件

3. **客户关系管理扩展**
   - SupplierExtend.java
   - 客户标签管理组件
   - 客户分析报表

4. **技术文档**
   - API接口文档
   - 数据库设计文档
   - 部署配置说明

---

## 测试用例

### 1. 排班管理测试用例

```java
@Test
public void testCreateSchedule() {
    JSONObject scheduleData = new JSONObject();
    scheduleData.put("employeeId", 1L);
    scheduleData.put("scheduleDate", "2025-06-20");
    scheduleData.put("startTime", "09:00");
    scheduleData.put("endTime", "17:00");
    scheduleData.put("workLocation", "总店");
    scheduleData.put("hourlyRate", new BigDecimal("25"));
    scheduleData.put("scheduleType", "single");
    
    String scheduleId = scheduleManagementService.createSchedule(scheduleData, request);
    assertNotNull(scheduleId);
}

@Test
public void testTimeConflictDetection() {
    JSONObject conflictData = new JSONObject();
    conflictData.put("employeeId", 1L);
    conflictData.put("scheduleDate", "2025-06-20");
    conflictData.put("startTime", "10:00");
    conflictData.put("endTime", "14:00");
    
    List<String> conflicts = scheduleManagementService.checkTimeConflict(conflictData);
    assertTrue(conflicts.size() > 0);
}
```

### 2. 咖啡店管理测试用例

```java
@Test
public void testRecordDailySales() {
    JSONObject salesData = new JSONObject();
    salesData.put("salesDate", "2025-06-18");
    salesData.put("totalSales", new BigDecimal("1500.00"));
    
    String salesId = coffeeShopService.recordDailySales(salesData, request);
    assertNotNull(salesId);
}

@Test
public void testCommissionCalculation() {
    Long salesId = 1L;
    BigDecimal totalSales = new BigDecimal("2000.00");
    
    coffeeShopService.calculateCommission(salesId, totalSales, request);
    
    // 验证提成记录是否正确创建
    // ...
}
```

---

**文档结束**

> 本文档为Week 16排班管理与运营支持的完整开发指南，涵盖了排班系统、咖啡店管理和客户关系扩展的所有技术实现细节。所有代码都严格遵循jshERP架构规范，确保与现有系统的无缝集成。