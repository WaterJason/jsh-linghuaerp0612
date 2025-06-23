<template>
  <div class="schedule-calendar-view">
    <a-card :bordered="false" :loading="loading">
      <!-- 日历头部控制 -->
      <div class="calendar-header">
        <div class="calendar-controls">
          <a-button-group>
            <a-button @click="previousMonth">
              <a-icon type="left" />
            </a-button>
            <a-button @click="goToday">今天</a-button>
            <a-button @click="nextMonth">
              <a-icon type="right" />
            </a-button>
          </a-button-group>
          
          <h3 class="current-month">{{ currentMonthText }}</h3>
          
          <a-button-group>
            <a-button :type="viewType === 'month' ? 'primary' : 'default'" @click="setViewType('month')">
              月视图
            </a-button>
            <a-button :type="viewType === 'week' ? 'primary' : 'default'" @click="setViewType('week')">
              周视图
            </a-button>
          </a-button-group>
        </div>
        
        <!-- 图例 -->
        <div class="calendar-legend">
          <div class="legend-item">
            <span class="legend-color normal"></span>
            <span>正常</span>
          </div>
          <div class="legend-item">
            <span class="legend-color leave"></span>
            <span>请假</span>
          </div>
          <div class="legend-item">
            <span class="legend-color swap"></span>
            <span>调班</span>
          </div>
          <div class="legend-item">
            <span class="legend-color absent"></span>
            <span>缺勤</span>
          </div>
        </div>
      </div>

      <!-- 日历主体 -->
      <div class="calendar-body">
        <!-- 星期标题 -->
        <div class="calendar-weekdays">
          <div 
            v-for="weekday in weekdays" 
            :key="weekday"
            class="weekday-header">
            {{ weekday }}
          </div>
        </div>

        <!-- 日历网格 -->
        <div class="calendar-grid">
          <div
            v-for="day in calendarDays"
            :key="day.date"
            :class="getDayClass(day)"
            @click="handleDayClick(day)">
            
            <!-- 日期数字 -->
            <div class="day-number">
              {{ day.dayNumber }}
            </div>

            <!-- 排班信息 -->
            <div class="day-schedules">
              <div
                v-for="schedule in day.schedules"
                :key="schedule.id"
                :class="getScheduleClass(schedule)"
                @click.stop="handleScheduleClick(schedule)">
                
                <div class="schedule-info">
                  <div class="employee-name">{{ schedule.employeeName }}</div>
                  <div class="shift-time">
                    {{ schedule.shiftType }}
                    <span v-if="schedule.startTime && schedule.endTime">
                      ({{ formatTime(schedule.startTime) }}-{{ formatTime(schedule.endTime) }})
                    </span>
                  </div>
                  <div class="work-area" v-if="schedule.workArea">
                    {{ schedule.workArea }}
                  </div>
                </div>

                <!-- 操作按钮 -->
                <div class="schedule-actions" @click.stop>
                  <a-dropdown :trigger="['click']">
                    <a-button size="small" type="link">
                      <a-icon type="more" />
                    </a-button>
                    <a-menu slot="overlay">
                      <a-menu-item @click="editSchedule(schedule)">
                        <a-icon type="edit" />
                        编辑
                      </a-menu-item>
                      <a-menu-item @click="deleteSchedule(schedule)">
                        <a-icon type="delete" />
                        删除
                      </a-menu-item>
                    </a-menu>
                  </a-dropdown>
                </div>
              </div>

              <!-- 添加排班按钮 -->
              <div 
                v-if="day.isCurrentMonth && day.schedules.length < 3"
                class="add-schedule-btn"
                @click.stop="addSchedule(day)">
                <a-icon type="plus" />
                添加排班
              </div>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 快速添加排班弹窗 -->
    <a-modal
      title="快速添加排班"
      :visible="quickAddVisible"
      @ok="handleQuickAdd"
      @cancel="quickAddVisible = false"
      :confirmLoading="quickAddLoading">
      
      <a-form :form="quickAddForm" layout="vertical">
        <a-form-item label="日期">
          <a-input :value="selectedDate" disabled />
        </a-form-item>
        
        <a-form-item label="员工">
          <a-select
            v-decorator="['employeeId', { rules: [{ required: true, message: '请选择员工' }] }]"
            placeholder="选择员工">
            <a-select-option 
              v-for="employee in availableEmployees"
              :key="employee.id"
              :value="employee.id">
              {{ employee.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="班次">
          <a-select
            v-decorator="['shiftType', { rules: [{ required: true, message: '请选择班次' }] }]"
            placeholder="选择班次">
            <a-select-option value="早班">早班 (08:00-16:00)</a-select-option>
            <a-select-option value="中班">中班 (12:00-20:00)</a-select-option>
            <a-select-option value="晚班">晚班 (16:00-24:00)</a-select-option>
            <a-select-option value="夜班">夜班 (00:00-08:00)</a-select-option>
            <a-select-option value="全天">全天 (08:00-24:00)</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="工作区域">
          <a-select
            v-decorator="['workArea']"
            placeholder="选择工作区域">
            <a-select-option value="咖啡店">咖啡店</a-select-option>
            <a-select-option value="展厅">展厅</a-select-option>
            <a-select-option value="收银台">收银台</a-select-option>
            <a-select-option value="全部">全部</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="备注">
          <a-textarea
            v-decorator="['notes']"
            placeholder="输入备注信息"
            :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'ScheduleCalendarView',
  
  props: {
    scheduleData: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  
  data() {
    return {
      // 日历状态
      currentDate: dayjs(),
      viewType: 'month',
      
      // 快速添加
      quickAddVisible: false,
      quickAddLoading: false,
      quickAddForm: this.$form.createForm(this),
      selectedDate: '',
      
      // 星期标题
      weekdays: ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    }
  },
  
  computed: {
    currentMonthText() {
      return this.currentDate.format('YYYY年MM月')
    },
    
    calendarDays() {
      const days = []
      const startOfMonth = this.currentDate.clone().startOf('month')
      const endOfMonth = this.currentDate.clone().endOf('month')
      const startOfCalendar = startOfMonth.clone().startOf('week')
      const endOfCalendar = endOfMonth.clone().endOf('week')
      
      let current = startOfCalendar.clone()
      
      while (current.isSame(endOfCalendar) || current.isBefore(endOfCalendar)) {
        const dayData = {
          date: current.format('YYYY-MM-DD'),
          dayNumber: current.date(),
          isCurrentMonth: current.isSame(this.currentDate, 'month'),
          isToday: current.isSame(dayjs(), 'day'),
          isWeekend: current.day() === 0 || current.day() === 6,
          schedules: this.getSchedulesForDate(current.format('YYYY-MM-DD'))
        }
        
        days.push(dayData)
        current.add(1, 'day')
      }
      
      return days
    },
    
    availableEmployees() {
      // 这里应该从父组件传入员工列表
      return this.$parent.employeeList || []
    }
  },
  
  methods: {
    // 获取指定日期的排班数据
    getSchedulesForDate(date) {
      return this.scheduleData.filter(schedule => 
        dayjs(schedule.scheduleDate).format('YYYY-MM-DD') === date
      )
    },
    
    // 获取日期样式类
    getDayClass(day) {
      return {
        'calendar-day': true,
        'current-month': day.isCurrentMonth,
        'other-month': !day.isCurrentMonth,
        'today': day.isToday,
        'weekend': day.isWeekend,
        'has-schedules': day.schedules.length > 0
      }
    },
    
    // 获取排班样式类
    getScheduleClass(schedule) {
      return {
        'schedule-item': true,
        [`status-${schedule.status}`]: true
      }
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return ''
      return dayjs(time, 'HH:mm:ss').format('HH:mm')
    },
    
    // 上一月
    previousMonth() {
      this.currentDate = this.currentDate.clone().subtract(1, 'month')
    },
    
    // 下一月
    nextMonth() {
      this.currentDate = this.currentDate.clone().add(1, 'month')
    },
    
    // 回到今天
    goToday() {
      this.currentDate = dayjs()
    },
    
    // 设置视图类型
    setViewType(type) {
      this.viewType = type
    },
    
    // 点击日期
    handleDayClick(day) {
      if (!day.isCurrentMonth) return
      
      // 如果是空白日期，显示添加排班
      if (day.schedules.length === 0) {
        this.addSchedule(day)
      }
    },
    
    // 点击排班
    handleScheduleClick(schedule) {
      this.$emit('edit-schedule', schedule)
    },
    
    // 添加排班
    addSchedule(day) {
      this.selectedDate = day.date
      this.quickAddVisible = true
      this.$nextTick(() => {
        this.quickAddForm.resetFields()
      })
    },
    
    // 编辑排班
    editSchedule(schedule) {
      this.$emit('edit-schedule', schedule)
    },
    
    // 删除排班
    deleteSchedule(schedule) {
      this.$emit('delete-schedule', schedule)
    },
    
    // 快速添加排班
    handleQuickAdd() {
      this.quickAddForm.validateFields((err, values) => {
        if (!err) {
          this.quickAddLoading = true
          
          const scheduleData = {
            scheduleDate: this.selectedDate,
            employeeId: values.employeeId,
            employeeName: this.getEmployeeName(values.employeeId),
            shiftType: values.shiftType,
            workArea: values.workArea,
            notes: values.notes,
            status: 'normal'
          }
          
          this.$emit('add-schedule', scheduleData)
          
          // 模拟提交延迟
          setTimeout(() => {
            this.quickAddLoading = false
            this.quickAddVisible = false
          }, 1000)
        }
      })
    },
    
    // 获取员工姓名
    getEmployeeName(employeeId) {
      const employee = this.availableEmployees.find(emp => emp.id === employeeId)
      return employee ? employee.name : ''
    }
  }
}
</script>

<style lang="less" scoped>
.schedule-calendar-view {
  .calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
    
    .calendar-controls {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .current-month {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }
    }
    
    .calendar-legend {
      display: flex;
      gap: 16px;
      
      .legend-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        
        .legend-color {
          width: 12px;
          height: 12px;
          border-radius: 2px;
          
          &.normal { background-color: #52c41a; }
          &.leave { background-color: #faad14; }
          &.swap { background-color: #1890ff; }
          &.absent { background-color: #ff4d4f; }
        }
      }
    }
  }
  
  .calendar-body {
    .calendar-weekdays {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 1px;
      margin-bottom: 1px;
      
      .weekday-header {
        padding: 12px 8px;
        text-align: center;
        font-weight: 600;
        background-color: #fafafa;
        color: #666;
        border: 1px solid #f0f0f0;
      }
    }
    
    .calendar-grid {
      display: grid;
      grid-template-columns: repeat(7, 1fr);
      gap: 1px;
      
      .calendar-day {
        min-height: 120px;
        padding: 8px;
        border: 1px solid #f0f0f0;
        background-color: #fff;
        cursor: pointer;
        transition: all 0.2s;
        
        &:hover {
          background-color: #fafafa;
        }
        
        &.other-month {
          background-color: #f9f9f9;
          color: #ccc;
        }
        
        &.today {
          background-color: #e6f7ff;
          border-color: #1890ff;
        }
        
        &.weekend {
          background-color: #fff7e6;
        }
        
        .day-number {
          font-size: 14px;
          font-weight: 600;
          margin-bottom: 4px;
        }
        
        .day-schedules {
          .schedule-item {
            margin-bottom: 4px;
            padding: 4px 6px;
            border-radius: 4px;
            font-size: 12px;
            position: relative;
            cursor: pointer;
            
            &.status-normal {
              background-color: #f6ffed;
              border: 1px solid #b7eb8f;
              color: #389e0d;
            }
            
            &.status-leave {
              background-color: #fffbe6;
              border: 1px solid #ffe58f;
              color: #d48806;
            }
            
            &.status-swap {
              background-color: #e6f7ff;
              border: 1px solid #91d5ff;
              color: #0958d9;
            }
            
            &.status-absent {
              background-color: #fff2f0;
              border: 1px solid #ffccc7;
              color: #cf1322;
            }
            
            .schedule-info {
              .employee-name {
                font-weight: 600;
                margin-bottom: 2px;
              }
              
              .shift-time {
                font-size: 11px;
                opacity: 0.8;
              }
              
              .work-area {
                font-size: 11px;
                opacity: 0.6;
              }
            }
            
            .schedule-actions {
              position: absolute;
              top: 2px;
              right: 2px;
              opacity: 0;
              transition: opacity 0.2s;
            }
            
            &:hover .schedule-actions {
              opacity: 1;
            }
          }
          
          .add-schedule-btn {
            padding: 4px 6px;
            border: 1px dashed #d9d9d9;
            border-radius: 4px;
            text-align: center;
            font-size: 12px;
            color: #666;
            cursor: pointer;
            transition: all 0.2s;
            
            &:hover {
              border-color: #1890ff;
              color: #1890ff;
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .schedule-calendar-view {
    .calendar-header {
      flex-direction: column;
      gap: 16px;
      
      .calendar-controls {
        width: 100%;
        justify-content: space-between;
      }
      
      .calendar-legend {
        flex-wrap: wrap;
        gap: 8px;
      }
    }
    
    .calendar-body {
      .calendar-grid {
        .calendar-day {
          min-height: 80px;
          padding: 4px;
          
          .day-schedules {
            .schedule-item {
              font-size: 10px;
              padding: 2px 4px;
              
              .schedule-info {
                .employee-name {
                  font-size: 10px;
                }
                
                .shift-time,
                .work-area {
                  font-size: 9px;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
