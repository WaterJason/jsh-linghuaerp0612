<template>
  <div class="schedule-management-container">
    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧周视图 -->
      <div class="week-section">
        <!-- 周导航 -->
        <div class="week-navigation">
          <a-button @click="previousWeek" icon="left" size="small"></a-button>
          <span class="week-range">{{ currentWeekRange }}</span>
          <a-button @click="nextWeek" icon="right" size="small"></a-button>
        </div>

        <!-- 周历表格 -->
        <div class="week-calendar">
          <!-- 星期标题行 -->
          <div class="week-header">
            <div class="week-day-header" v-for="(day, index) in currentWeekDays" :key="index">
              <div class="day-name">{{ day.dayName }}</div>
              <div class="day-date" :class="{ 'today': isToday(day.date) }">{{ day.date.getDate() }}</div>
            </div>
          </div>

          <!-- 排班内容区域 -->
          <div class="week-content">
            <div class="week-day-column" v-for="(day, index) in currentWeekDays" :key="index">
              <!-- 每天的排班卡片 -->
              <div
                v-for="duty in getDayDuties(day.date)"
                :key="duty.id"
                class="duty-card"
                :class="getDutyCardClass(duty)"
                @click="handleEditDuty(duty)"
                @dblclick="handleAddDuty(day.date)"
              >
                <div class="duty-employee">{{ duty.employeeName }}</div>
                <div class="duty-shift">{{ duty.shiftType }}</div>
                <div class="duty-time">{{ duty.startTime }}-{{ duty.endTime }}</div>
              </div>

              <!-- 空白区域，可以添加排班 -->
              <div
                v-if="getDayDuties(day.date).length === 0"
                class="empty-slot"
                @dblclick="handleAddDuty(day.date)"
              >
                <div class="add-duty-hint">双击添加排班</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧信息面板 -->
      <div class="info-panel">
        <!-- 月历 -->
        <div class="month-calendar">
          <div class="month-header">
            <span class="month-title">{{ currentMonthYear }}</span>
          </div>

          <div class="month-grid">
            <!-- 星期标题 -->
            <div class="month-weekdays">
              <div v-for="day in weekDaysShort" :key="day" class="month-weekday">{{ day }}</div>
            </div>

            <!-- 日期网格 -->
            <div class="month-days">
              <div
                v-for="day in monthCalendarDays"
                :key="day.key"
                class="month-day"
                :class="{
                  'today': isToday(day.date),
                  'other-month': !day.isCurrentMonth,
                  'has-duties': day.dutiesCount > 0,
                  'selected-week': isInSelectedWeek(day.date)
                }"
                @click="handleMonthDayClick(day.date)"
              >
                <span class="month-day-number">{{ day.date.getDate() }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 员工工时统计 -->
        <div class="employee-stats">
          <div class="stats-header">
            <h4>本月员工排班统计</h4>
          </div>

          <div class="stats-list">
            <div
              v-for="employee in employeeStats"
              :key="employee.id"
              class="employee-stat-item"
            >
              <div class="employee-info">
                <div class="employee-avatar" :style="{ backgroundColor: employee.color }">
                  {{ employee.name.charAt(0) }}
                </div>
                <span class="employee-name">{{ employee.name }}</span>
              </div>
              <div class="employee-hours">
                <span class="hours-text">{{ employee.totalHours }}小时</span>
                <div class="hours-bar">
                  <div
                    class="hours-progress"
                    :style="{ width: employee.progressPercent + '%', backgroundColor: employee.color }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DutyWeekMonthView',
  props: {
    schedules: {
      type: Array,
      default: () => []
    },
    currentDate: {
      type: Date,
      required: true
    }
  },
  data() {
    return {
      weekDays: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      weekDaysShort: ['一', '二', '三', '四', '五', '六', '日'],
      selectedWeekStart: null,
      // 员工颜色配置
      employeeColors: ['#ff7875', '#ffa940', '#fadb14', '#73d13d', '#40a9ff', '#b37feb', '#f759ab']
    }
  },
  computed: {
    // 当前周的日期范围
    currentWeekRange() {
      const start = this.getWeekStart(this.selectedWeekStart || this.currentDate)
      const end = new Date(start)
      end.setDate(start.getDate() + 6)

      return `${start.getMonth() + 1}月${start.getDate()}日 - ${this.currentDate.getFullYear()}年${end.getMonth() + 1}月${end.getDate()}日`
    },
    
    // 当前月年
    currentMonthYear() {
      return `${this.currentDate.getFullYear()}年 ${this.currentDate.getMonth() + 1}月`
    },
    
    // 当前周的天数
    currentWeekDays() {
      const start = this.getWeekStart(this.selectedWeekStart || this.currentDate)
      const days = []
      
      for (let i = 0; i < 7; i++) {
        const date = new Date(start)
        date.setDate(start.getDate() + i)
        days.push({
          date: date,
          dayName: this.weekDays[i]
        })
      }
      
      return days
    },
    
    // 月历天数
    monthCalendarDays() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      
      // 获取当月天数
      const daysInMonth = new Date(year, month + 1, 0).getDate()
      
      // 获取当月第一天是星期几
      let firstDay = new Date(year, month, 1).getDay()
      firstDay = firstDay === 0 ? 6 : firstDay - 1
      
      const days = []
      
      // 添加上个月的尾部天数
      const prevMonth = new Date(year, month - 1, 0).getDate()
      for (let i = firstDay - 1; i >= 0; i--) {
        const date = new Date(year, month - 1, prevMonth - i)
        days.push({
          key: `prev-${prevMonth - i}`,
          date: date,
          isCurrentMonth: false,
          dutiesCount: this.getDayDutiesCount(date)
        })
      }
      
      // 添加当月的所有天数
      for (let i = 1; i <= daysInMonth; i++) {
        const date = new Date(year, month, i)
        days.push({
          key: `current-${i}`,
          date: date,
          isCurrentMonth: true,
          dutiesCount: this.getDayDutiesCount(date)
        })
      }
      
      // 添加下个月的开头天数，补齐6行
      const totalCells = 42 // 6行 × 7列
      const remainingCells = totalCells - days.length
      for (let i = 1; i <= remainingCells; i++) {
        const date = new Date(year, month + 1, i)
        days.push({
          key: `next-${i}`,
          date: date,
          isCurrentMonth: false,
          dutiesCount: this.getDayDutiesCount(date)
        })
      }
      
      return days
    },

    // 员工统计
    employeeStats() {
      const stats = {}
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()

      // 统计当月每个员工的工时
      this.schedules.forEach(duty => {
        const dutyDate = new Date(duty.dutyDate)
        if (dutyDate.getFullYear() === year && dutyDate.getMonth() === month) {
          if (!stats[duty.employeeId]) {
            stats[duty.employeeId] = {
              id: duty.employeeId,
              name: duty.employeeName,
              totalHours: 0,
              dutyCount: 0
            }
          }
          stats[duty.employeeId].totalHours += duty.workHours || 0
          stats[duty.employeeId].dutyCount += 1
        }
      })

      // 转换为数组并添加颜色和进度
      const employeeList = Object.values(stats)
      const maxHours = Math.max(...employeeList.map(emp => emp.totalHours), 1)

      return employeeList.map((employee, index) => ({
        ...employee,
        color: this.employeeColors[index % this.employeeColors.length],
        progressPercent: Math.round((employee.totalHours / maxHours) * 100)
      })).sort((a, b) => b.totalHours - a.totalHours)
    }
  },
  methods: {
    // 获取周的开始日期（周一）
    getWeekStart(date) {
      const d = new Date(date)
      const day = d.getDay()
      const diff = d.getDate() - day + (day === 0 ? -6 : 1) // 调整为周一开始
      return new Date(d.setDate(diff))
    },
    
    // 判断是否为今天
    isToday(date) {
      const today = new Date()
      return date.toDateString() === today.toDateString()
    },
    
    // 判断是否在选中的周内
    isInSelectedWeek(date) {
      if (!this.selectedWeekStart) return false
      const weekStart = this.getWeekStart(this.selectedWeekStart)
      const weekEnd = new Date(weekStart)
      weekEnd.setDate(weekStart.getDate() + 6)
      
      return date >= weekStart && date <= weekEnd
    },
    
    // 格式化日期
    formatDate(date) {
      return date.toISOString().split('T')[0]
    },
    
    // 获取指定日期的值班数量
    getDayDutiesCount(date) {
      const dateStr = this.formatDate(date)
      return this.schedules.filter(duty => duty.dutyDate === dateStr).length
    },

    // 获取指定日期的值班记录
    getDayDuties(date) {
      const dateStr = this.formatDate(date)
      return this.schedules.filter(duty => duty.dutyDate === dateStr)
    },

    // 获取值班卡片的样式类
    getDutyCardClass(duty) {
      const baseClass = 'duty-card'
      const shiftClass = `duty-${duty.shiftType}`
      const priorityClass = duty.priority === 'high' ? 'high-priority' : ''
      return [baseClass, shiftClass, priorityClass].filter(Boolean).join(' ')
    },
    
    // 获取值班密度颜色
    getDutiesDensityColor(count) {
      if (count <= 2) return '#52c41a' // 绿色 - 轻度
      if (count <= 4) return '#faad14' // 橙色 - 中度
      return '#f5222d' // 红色 - 密集
    },
    
    // 获取值班块的样式类
    getDutyClass(duty) {
      return `duty-${duty.shiftType}`
    },
    
    // 周导航
    previousWeek() {
      const current = this.selectedWeekStart || this.currentDate
      const prev = new Date(current)
      prev.setDate(current.getDate() - 7)
      this.selectedWeekStart = prev
    },
    
    currentWeek() {
      this.selectedWeekStart = new Date()
    },
    
    nextWeek() {
      const current = this.selectedWeekStart || this.currentDate
      const next = new Date(current)
      next.setDate(current.getDate() + 7)
      this.selectedWeekStart = next
    },
    
    // 月导航
    previousMonth() {
      this.$emit('month-change', -1)
    },
    
    currentMonth() {
      this.$emit('month-change', 0)
    },
    
    nextMonth() {
      this.$emit('month-change', 1)
    },
    
    // 月历日期点击
    handleMonthDayClick(date) {
      this.selectedWeekStart = this.getWeekStart(date)
    },
    
    // 添加值班
    handleAddDuty(date) {
      const dateStr = this.formatDate(date)
      this.$emit('add-duty', { date: dateStr })
    },
    
    // 编辑值班
    handleEditDuty(duty) {
      this.$emit('edit-duty', duty)
    }
  }
}
</script>

<style scoped>
.schedule-management-container {
  background: #f5f5f5;
  min-height: 600px;
  padding: 16px;
}

.main-content {
  display: flex;
  gap: 16px;
  height: 100%;
}

.week-section {
  flex: 2;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.info-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 300px;
}

/* 周导航样式 */
.week-navigation {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  gap: 12px;
}

.week-range {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  min-width: 200px;
  text-align: center;
}

/* 周历样式 */
.week-calendar {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
}

.week-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.week-day-header {
  padding: 12px 8px;
  text-align: center;
  border-right: 1px solid #e8e8e8;
}

.week-day-header:last-child {
  border-right: none;
}

.day-name {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.day-date {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.day-date.today {
  color: #1890ff;
  background: #e6f7ff;
  border-radius: 50%;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
}

.week-content {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  min-height: 400px;
  gap: 1px;
  background: #e8e8e8;
}

.week-day-column {
  background: #fff;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 值班卡片样式 */
.duty-card {
  background: #fff;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  padding: 8px;
  cursor: pointer;
  transition: all 0.2s;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.duty-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

.duty-card.duty-早班 {
  background: linear-gradient(135deg, #fff2e8 0%, #ffd8bf 100%);
  border-color: #ffbb96;
}

.duty-card.duty-晚班 {
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
  border-color: #b7eb8f;
}

.duty-card.duty-全天 {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border-color: #91d5ff;
}

.duty-employee {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.duty-shift {
  font-size: 12px;
  color: #666;
  margin-bottom: 2px;
}

.duty-time {
  font-size: 11px;
  color: #999;
}

/* 空白区域样式 */
.empty-slot {
  min-height: 60px;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
}

.empty-slot:hover {
  border-color: #1890ff;
  background: #f0f9ff;
}

.add-duty-hint {
  color: #999;
  font-size: 12px;
}

/* 月历样式 */
.month-calendar {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.month-header {
  text-align: center;
  margin-bottom: 16px;
}

.month-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.month-grid {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  overflow: hidden;
}

.month-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #fafafa;
}

.month-weekday {
  padding: 8px 4px;
  text-align: center;
  font-size: 12px;
  font-weight: 600;
  color: #666;
  border-right: 1px solid #e8e8e8;
}

.month-weekday:last-child {
  border-right: none;
}

.month-days {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
  background: #e8e8e8;
}

.month-day {
  height: 32px;
  background: #fff;
  cursor: pointer;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.month-day:hover {
  background: #f0f0f0;
}

.month-day.today {
  background: #e6f7ff;
  color: #1890ff;
  font-weight: 600;
}

.month-day.other-month {
  background: #f5f5f5;
  color: #ccc;
}

.month-day.selected-week {
  background: #fff2e8;
  border: 2px solid #ffa940;
}

.month-day.has-duties {
  background: #f6ffed;
}

.month-day-number {
  font-size: 12px;
}

/* 员工统计样式 */
.employee-stats {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.stats-header h4 {
  margin: 0 0 16px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.stats-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.employee-stat-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.employee-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
}

.employee-name {
  font-size: 13px;
  color: #333;
}

.employee-hours {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.hours-text {
  font-size: 12px;
  color: #666;
}

.hours-bar {
  width: 60px;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.hours-progress {
  height: 100%;
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }

  .week-section,
  .info-panel {
    flex: none;
  }

  .info-panel {
    flex-direction: row;
    gap: 16px;
  }

  .month-calendar,
  .employee-stats {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .schedule-management-container {
    padding: 8px;
  }

  .main-content {
    gap: 8px;
  }

  .week-section {
    padding: 12px;
  }

  .info-panel {
    flex-direction: column;
    gap: 8px;
  }

  .week-content {
    min-height: 300px;
  }

  .duty-card {
    padding: 6px;
  }

  .duty-employee {
    font-size: 12px;
  }

  .duty-shift,
  .duty-time {
    font-size: 10px;
  }

  .month-day {
    height: 28px;
  }

  .employee-avatar {
    width: 20px;
    height: 20px;
    font-size: 10px;
  }
}

@media (max-width: 480px) {
  .week-header {
    grid-template-columns: repeat(7, 1fr);
  }

  .day-name {
    display: none;
  }

  .day-date {
    font-size: 14px;
  }

  .duty-card {
    padding: 4px;
  }

  .duty-employee {
    font-size: 11px;
  }

  .duty-shift {
    display: none;
  }
}
</style>
