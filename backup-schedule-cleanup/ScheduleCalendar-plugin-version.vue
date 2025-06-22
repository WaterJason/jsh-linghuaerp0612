<template>
  <div class="schedule-calendar">
    <!-- 日历头部 -->
    <div class="calendar-header">
      <div class="calendar-title">
        <a-button type="link" @click="prevMonth" icon="left" />
        <span class="month-title">{{ currentYear }}年{{ currentMonth }}月</span>
        <a-button type="link" @click="nextMonth" icon="right" />
      </div>
      <div class="calendar-actions">
        <a-button @click="goToday">今天</a-button>
        <a-button type="primary" @click="showBatchAssignModal">批量排班</a-button>
      </div>
    </div>

    <!-- 日历主体 -->
    <div class="calendar-body">
      <a-calendar 
        v-model:value="selectedDate" 
        @select="onDateSelect"
        @panelChange="onPanelChange"
        :fullscreen="true"
        class="schedule-calendar-main">
        
        <!-- 自定义日期单元格 -->
        <template #dateCellRender="{ current }">
          <div class="calendar-date-cell" :class="getDateCellClass(current)">
            <div class="date-number">{{ current.date() }}</div>
            <div class="assignments-container" v-if="getAssignments(current).length > 0">
              <div 
                v-for="assignment in getAssignments(current)" 
                :key="assignment.id"
                class="assignment-tag"
                :style="{ backgroundColor: assignment.shiftColor }"
                @click.stop="showAssignmentDetail(assignment)">
                {{ assignment.userName }}
              </div>
            </div>
            <div class="add-assignment-btn" @click.stop="showAddAssignmentModal(current)">
              <a-icon type="plus" />
            </div>
          </div>
        </template>

        <!-- 自定义月份单元格 -->
        <template #monthCellRender="{ current }">
          <div class="calendar-month-cell">
            <div class="month-name">{{ current.format('M月') }}</div>
            <div class="month-stats">
              <span>{{ getMonthAssignmentCount(current) }}次排班</span>
            </div>
          </div>
        </template>
      </a-calendar>
    </div>

    <!-- 批量排班弹窗 -->
    <a-modal
      v-model:visible="batchAssignVisible"
      title="批量排班"
      width="600px"
      @ok="handleBatchAssign"
      @cancel="cancelBatchAssign">
      
      <a-form :form="batchAssignForm" layout="vertical">
        <a-form-item label="选择日期">
          <div class="selected-dates">
            <a-tag 
              v-for="date in selectedDates" 
              :key="date"
              closable
              @close="removeSelectedDate(date)">
              {{ date }}
            </a-tag>
          </div>
          <p class="tip">在日历上点击日期来选择多个日期</p>
        </a-form-item>

        <a-form-item label="选择班次">
          <a-select 
            v-model:value="batchAssignForm.shiftId"
            placeholder="请选择班次"
            style="width: 100%">
            <a-select-option 
              v-for="shift in shiftOptions" 
              :key="shift.value" 
              :value="shift.value">
              {{ shift.displayName }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="选择人员">
          <a-select 
            v-model:value="batchAssignForm.userIds"
            mode="multiple"
            placeholder="请选择人员"
            style="width: 100%">
            <a-select-option 
              v-for="user in userOptions" 
              :key="user.id" 
              :value="user.id">
              {{ user.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 排班详情弹窗 -->
    <a-modal
      v-model:visible="assignmentDetailVisible"
      title="排班详情"
      width="500px"
      :footer="null">
      
      <div v-if="currentAssignment" class="assignment-detail">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="日期">
            {{ currentAssignment.scheduleDate }}
          </a-descriptions-item>
          <a-descriptions-item label="员工">
            {{ currentAssignment.userName }}
          </a-descriptions-item>
          <a-descriptions-item label="班次">
            {{ currentAssignment.shiftName }}
          </a-descriptions-item>
          <a-descriptions-item label="时间">
            {{ currentAssignment.shiftTime }}
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(currentAssignment.status)">
              {{ currentAssignment.statusName }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="备注" v-if="currentAssignment.notes">
            {{ currentAssignment.notes }}
          </a-descriptions-item>
        </a-descriptions>

        <div class="assignment-actions" style="margin-top: 16px; text-align: right;">
          <a-button @click="editAssignment(currentAssignment)" style="margin-right: 8px;">
            编辑
          </a-button>
          <a-button type="danger" @click="deleteAssignment(currentAssignment)">
            删除
          </a-button>
        </div>
      </div>
    </a-modal>

    <!-- 新增排班弹窗 -->
    <schedule-assignment-modal
      v-model:visible="addAssignmentVisible"
      :selected-date="selectedDateForAdd"
      :shift-options="shiftOptions"
      :user-options="userOptions"
      @success="onAssignmentSuccess" />
  </div>
</template>

<script>
import moment from 'moment'
import { getAction, postAction } from '@/api/manage'
import ScheduleAssignmentModal from './ScheduleAssignmentModal.vue'

export default {
  name: 'ScheduleCalendar',
  components: {
    ScheduleAssignmentModal
  },
  data() {
    return {
      // 日历状态
      selectedDate: moment(),
      currentYear: moment().year(),
      currentMonth: moment().month() + 1,
      
      // 数据
      calendarData: {},
      assignmentsData: [],
      shiftOptions: [],
      userOptions: [],
      
      // 批量排班
      batchAssignVisible: false,
      selectedDates: [],
      batchAssignForm: {
        shiftId: undefined,
        userIds: []
      },
      
      // 排班详情
      assignmentDetailVisible: false,
      currentAssignment: null,
      
      // 新增排班
      addAssignmentVisible: false,
      selectedDateForAdd: null,
      
      // 加载状态
      loading: false
    }
  },
  
  mounted() {
    this.initData()
  },
  
  methods: {
    // ==================== 初始化方法 ====================
    
    async initData() {
      await this.loadShiftOptions()
      await this.loadUserOptions()
      await this.loadCalendarData()
    },
    
    async loadShiftOptions() {
      try {
        const res = await getAction('/api/plugin/calendar-schedule/shift/options')
        if (res.code === 200) {
          this.shiftOptions = res.data || []
        }
      } catch (error) {
        console.error('加载班次选项失败:', error)
        this.$message.error('加载班次选项失败')
      }
    },
    
    async loadUserOptions() {
      try {
        // TODO: 集成jshERP用户数据
        // const res = await getAction('/user/list')
        // 临时模拟数据
        this.userOptions = [
          { id: 1, name: '张三' },
          { id: 2, name: '李四' },
          { id: 3, name: '王五' },
          { id: 4, name: '赵六' }
        ]
      } catch (error) {
        console.error('加载用户选项失败:', error)
        this.$message.error('加载用户选项失败')
      }
    },
    
    async loadCalendarData() {
      try {
        this.loading = true
        const res = await getAction('/api/plugin/calendar-schedule/calendar/month', {
          year: this.currentYear,
          month: this.currentMonth
        })
        
        if (res.code === 200) {
          this.calendarData = res.data || {}
          this.processCalendarData()
        }
      } catch (error) {
        console.error('加载日历数据失败:', error)
        this.$message.error('加载日历数据失败')
      } finally {
        this.loading = false
      }
    },
    
    processCalendarData() {
      // 处理日历数据，转换为组件需要的格式
      const calendarDays = this.calendarData.calendarDays || []
      this.assignmentsData = []
      
      calendarDays.forEach(day => {
        if (day.hasAssignments && day.assignmentInfo) {
          // 解析排班信息
          const assignments = this.parseAssignmentInfo(day.assignmentInfo, day.date)
          this.assignmentsData.push(...assignments)
        }
      })
    },
    
    parseAssignmentInfo(assignmentInfo, date) {
      // 解析排班信息字符串，转换为数组
      // 格式: "张三(全天班), 李四(上午班)"
      const assignments = []
      const parts = assignmentInfo.split(', ')
      
      parts.forEach((part, index) => {
        const match = part.match(/(.+)\((.+)\)/)
        if (match) {
          assignments.push({
            id: `${date}-${index}`,
            userName: match[1],
            shiftName: match[2],
            scheduleDate: date,
            shiftColor: this.getShiftColor(match[2])
          })
        }
      })
      
      return assignments
    },
    
    getShiftColor(shiftName) {
      // 根据班次名称返回颜色
      const colorMap = {
        '全天班': '#1890ff',
        '上午班': '#52c41a',
        '下午班': '#faad14',
        '晚班': '#722ed1'
      }
      return colorMap[shiftName] || '#1890ff'
    },
    
    // ==================== 日历事件处理 ====================
    
    onDateSelect(date) {
      this.selectedDate = date
      
      if (this.batchAssignVisible) {
        // 批量排班模式下，添加/移除选中日期
        const dateStr = date.format('YYYY-MM-DD')
        const index = this.selectedDates.indexOf(dateStr)
        
        if (index > -1) {
          this.selectedDates.splice(index, 1)
        } else {
          this.selectedDates.push(dateStr)
        }
      }
    },
    
    onPanelChange(date, mode) {
      if (mode === 'month') {
        this.currentYear = date.year()
        this.currentMonth = date.month() + 1
        this.loadCalendarData()
      }
    },
    
    prevMonth() {
      const prevDate = moment([this.currentYear, this.currentMonth - 1]).subtract(1, 'month')
      this.currentYear = prevDate.year()
      this.currentMonth = prevDate.month() + 1
      this.loadCalendarData()
    },
    
    nextMonth() {
      const nextDate = moment([this.currentYear, this.currentMonth - 1]).add(1, 'month')
      this.currentYear = nextDate.year()
      this.currentMonth = nextDate.month() + 1
      this.loadCalendarData()
    },
    
    goToday() {
      const today = moment()
      this.currentYear = today.year()
      this.currentMonth = today.month() + 1
      this.selectedDate = today
      this.loadCalendarData()
    },
    
    // ==================== 日历渲染方法 ====================
    
    getDateCellClass(date) {
      const classes = []
      
      if (date.isSame(moment(), 'day')) {
        classes.push('today')
      }
      
      if (date.day() === 0 || date.day() === 6) {
        classes.push('weekend')
      }
      
      if (this.getAssignments(date).length > 0) {
        classes.push('has-assignments')
      }
      
      if (this.batchAssignVisible && this.selectedDates.includes(date.format('YYYY-MM-DD'))) {
        classes.push('selected-for-batch')
      }
      
      return classes
    },
    
    getAssignments(date) {
      const dateStr = date.format('YYYY-MM-DD')
      return this.assignmentsData.filter(assignment => assignment.scheduleDate === dateStr)
    },
    
    getMonthAssignmentCount(date) {
      // 获取月份排班统计
      return 0 // TODO: 实现月份统计
    },
    
    // ==================== 排班操作方法 ====================
    
    showAddAssignmentModal(date) {
      this.selectedDateForAdd = date.format('YYYY-MM-DD')
      this.addAssignmentVisible = true
    },
    
    showAssignmentDetail(assignment) {
      this.currentAssignment = assignment
      this.assignmentDetailVisible = true
    },
    
    showBatchAssignModal() {
      this.batchAssignVisible = true
      this.selectedDates = []
      this.batchAssignForm = {
        shiftId: undefined,
        userIds: []
      }
    },
    
    async handleBatchAssign() {
      try {
        if (this.selectedDates.length === 0) {
          this.$message.warning('请选择日期')
          return
        }
        
        if (!this.batchAssignForm.shiftId) {
          this.$message.warning('请选择班次')
          return
        }
        
        if (this.batchAssignForm.userIds.length === 0) {
          this.$message.warning('请选择人员')
          return
        }
        
        const params = {
          dates: this.selectedDates,
          userIds: this.batchAssignForm.userIds,
          shiftId: this.batchAssignForm.shiftId
        }
        
        const res = await postAction('/api/plugin/calendar-schedule/assignment/batch-assign', params)
        
        if (res.code === 200) {
          this.$message.success('批量排班成功')
          this.batchAssignVisible = false
          this.loadCalendarData()
        } else {
          this.$message.error(res.message || '批量排班失败')
        }
      } catch (error) {
        console.error('批量排班失败:', error)
        this.$message.error('批量排班失败')
      }
    },
    
    cancelBatchAssign() {
      this.batchAssignVisible = false
      this.selectedDates = []
    },
    
    removeSelectedDate(date) {
      const index = this.selectedDates.indexOf(date)
      if (index > -1) {
        this.selectedDates.splice(index, 1)
      }
    },
    
    onAssignmentSuccess() {
      this.addAssignmentVisible = false
      this.loadCalendarData()
    },
    
    editAssignment(assignment) {
      // TODO: 实现编辑排班功能
      this.$message.info('编辑功能开发中')
    },
    
    async deleteAssignment(assignment) {
      try {
        const res = await postAction(`/api/plugin/calendar-schedule/assignment/delete/${assignment.id}`)
        
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.assignmentDetailVisible = false
          this.loadCalendarData()
        } else {
          this.$message.error(res.message || '删除失败')
        }
      } catch (error) {
        console.error('删除排班失败:', error)
        this.$message.error('删除失败')
      }
    },
    
    getStatusColor(status) {
      const colorMap = {
        'SCHEDULED': 'blue',
        'CONFIRMED': 'green',
        'CANCELLED': 'red',
        'COMPLETED': 'purple'
      }
      return colorMap[status] || 'default'
    }
  }
}
</script>
