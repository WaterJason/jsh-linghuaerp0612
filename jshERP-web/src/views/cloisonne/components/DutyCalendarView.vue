<template>
  <a-card>
    <div class="calendar-container">
      <!-- 日历头部 - 星期标题 -->
      <div class="calendar-header">
        <div v-for="day in weekDays" :key="day" class="calendar-header-cell">
          {{ day }}
        </div>
      </div>
      
      <!-- 日历主体 -->
      <div class="calendar-body">
        <div 
          v-for="(day, index) in calendarDays" 
          :key="index"
          :class="[
            'calendar-cell',
            { 'calendar-cell-empty': !day },
            { 'calendar-cell-today': day && isToday(day) },
            { 'calendar-cell-hover': day }
          ]"
          @click="handleDateClick(day)"
        >
          <div v-if="day" class="calendar-cell-content">
            <!-- 日期数字 -->
            <div :class="['calendar-date', { 'calendar-date-today': isToday(day) }]">
              {{ day.getDate() }}
            </div>
            
            <!-- 值班记录列表 -->
            <div class="duty-list">
              <div 
                v-for="duty in getDayDuties(day)" 
                :key="duty.id"
                :class="[
                  'duty-item',
                  `duty-item-${duty.shiftType}`
                ]"
                @click.stop="handleEditDuty(duty)"
              >
                <a-tooltip :title="`${duty.employeeName} - ${duty.shiftType}`">
                  <div class="duty-content">
                    {{ duty.employeeName }} ({{ duty.shiftType }})
                  </div>
                </a-tooltip>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </a-card>
</template>

<script>
export default {
  name: 'DutyCalendarView',
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
      weekDays: ['一', '二', '三', '四', '五', '六', '日']
    }
  },
  computed: {
    // 计算日历天数数组
    calendarDays() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      
      // 获取当月天数
      const daysInMonth = new Date(year, month + 1, 0).getDate()
      
      // 获取当月第一天是星期几 (0=周日, 1=周一, ..., 6=周六)
      let firstDay = new Date(year, month, 1).getDay()
      // 转换为周一开始 (0=周一, 1=周二, ..., 6=周日)
      firstDay = firstDay === 0 ? 6 : firstDay - 1
      
      const days = []
      
      // 添加空白天数（上个月的尾部）
      for (let i = 0; i < firstDay; i++) {
        days.push(null)
      }
      
      // 添加当月的所有天数
      for (let i = 1; i <= daysInMonth; i++) {
        days.push(new Date(year, month, i))
      }
      
      return days
    }
  },
  methods: {
    // 格式化日期为字符串
    formatDate(date) {
      if (!date) return ''
      return date.toISOString().split('T')[0]
    },
    
    // 判断是否为今天
    isToday(date) {
      if (!date) return false
      const today = new Date()
      return date.toDateString() === today.toDateString()
    },
    
    // 获取指定日期的值班记录
    getDayDuties(date) {
      if (!date) return []
      const dateStr = this.formatDate(date)
      return this.schedules.filter(duty => duty.dutyDate === dateStr)
    },
    
    // 处理日期点击
    handleDateClick(date) {
      if (!date) return
      const dateStr = this.formatDate(date)
      this.$emit('date-click', dateStr)
    },
    
    // 处理值班记录编辑
    handleEditDuty(duty) {
      this.$emit('edit-schedule', duty)
    }
  }
}
</script>

<style scoped>
.calendar-container {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  overflow: hidden;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #fafafa;
  border-bottom: 1px solid #e8e8e8;
}

.calendar-header-cell {
  padding: 12px 8px;
  text-align: center;
  font-weight: 600;
  color: #666;
  font-size: 14px;
  border-right: 1px solid #e8e8e8;
}

.calendar-header-cell:last-child {
  border-right: none;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #e8e8e8;
  gap: 1px;
}

.calendar-cell {
  min-height: 100px;
  background-color: #fff;
  cursor: pointer;
  transition: background-color 0.2s;
}

.calendar-cell-empty {
  background-color: #f5f5f5;
  cursor: default;
}

.calendar-cell-hover:hover {
  background-color: #f0f0f0;
}

.calendar-cell-today {
  background-color: #e6f7ff;
}

.calendar-cell-content {
  padding: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.calendar-date {
  font-size: 14px;
  font-weight: 500;
  color: #666;
  margin-bottom: 4px;
}

.calendar-date-today {
  color: #1890ff;
  font-weight: bold;
}

.duty-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.duty-item {
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  overflow: hidden;
}

.duty-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.duty-content {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 不同班次的颜色 */
.duty-item-早班 {
  background-color: #fff2e8;
  color: #d46b08;
  border: 1px solid #ffbb96;
}

.duty-item-早班:hover {
  background-color: #ffd8bf;
}

.duty-item-晚班 {
  background-color: #f6ffed;
  color: #389e0d;
  border: 1px solid #b7eb8f;
}

.duty-item-晚班:hover {
  background-color: #d9f7be;
}

.duty-item-全天 {
  background-color: #e6f7ff;
  color: #0958d9;
  border: 1px solid #91d5ff;
}

.duty-item-全天:hover {
  background-color: #bae7ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .calendar-cell {
    min-height: 80px;
  }
  
  .calendar-cell-content {
    padding: 4px;
  }
  
  .calendar-date {
    font-size: 12px;
  }
  
  .duty-item {
    font-size: 10px;
    padding: 1px 4px;
  }
}

@media (max-width: 480px) {
  .calendar-cell {
    min-height: 60px;
  }
  
  .duty-content {
    font-size: 10px;
  }
}
</style>
