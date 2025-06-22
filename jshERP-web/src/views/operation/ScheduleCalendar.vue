<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="员工">
              <a-select
                v-model="queryParam.employeeId"
                placeholder="请选择员工"
                allowClear
                show-search
                :filter-option="filterOption">
                <a-select-option
                  v-for="emp in employeeList"
                  :key="emp.id"
                  :value="emp.id">
                  {{ emp.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="班次">
              <a-select v-model="queryParam.shiftId" placeholder="请选择班次" allowClear>
                <a-select-option
                  v-for="shift in shiftList"
                  :key="shift.id"
                  :value="shift.id">
                  {{ shift.shiftName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增排班</a-button>
      <a-button @click="handleBatchAssign" icon="calendar" style="margin-left: 8px">批量排班</a-button>
      <a-button @click="exportSchedule" icon="download" style="margin-left: 8px">导出排班</a-button>
    </div>

    <!-- 日历区域 -->
    <div class="calendar-wrapper" style="margin-top: 16px;">
      <a-calendar
        v-model="selectedDate"
        @select="onDateSelect"
        @panelChange="onPanelChange"
        :fullscreen="true">
        <template slot="dateCellRender" slot-scope="value">
          <div class="schedule-cell">
            <div
              v-for="assignment in getAssignmentsForDate(value)"
              :key="assignment.id"
              class="assignment-tag"
              :style="{
                backgroundColor: assignment.shiftColor || '#3B82F6',
                color: 'white',
                fontSize: '12px',
                padding: '2px 6px',
                borderRadius: '4px',
                margin: '1px',
                display: 'inline-block'
              }"
              @click="handleAssignmentClick(assignment)">
              {{ assignment.employeeName }}
            </div>
          </div>
        </template>
      </a-calendar>
    </div>

    <!-- 排班弹窗 -->
    <schedule-assignment-modal
      ref="modalForm"
      @ok="modalFormOk"
      :employeeList="employeeList"
      :shiftList="shiftList">
    </schedule-assignment-modal>
  </a-card>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import ScheduleAssignmentModal from '../schedule/ScheduleAssignmentModal'
import dayjs from 'dayjs'
import { getAction, postAction, deleteAction } from '@/api/manage'

export default {
  name: 'ScheduleCalendar',
  mixins: [JeecgListMixin],
  components: {
    ScheduleAssignmentModal
  },
  data() {
    return {
      // 标准的JeecgListMixin数据结构
      description: '掐丝珐琅馆排班管理',
      columns: [], // 如果需要表格视图
      url: {
        list: '/schedule/entries',
        delete: '/schedule/entry',
        deleteBatch: '/schedule/entry/deleteBatch'
      },

      // 查询参数
      queryParam: {
        employeeId: null,
        shiftId: null
      },

      // 排班特有数据
      selectedDate: dayjs(),
      employeeList: [],
      shiftList: [],
      calendarData: {},

      // 当前日历视图的日期范围
      currentViewStart: null,
      currentViewEnd: null,

      // 排班详情弹窗相关
      scheduleDetailVisible: false,
      currentSchedule: null
    }
  },

  mounted() {
    this.initData()
  },

  methods: {
    // ==================== 初始化方法 ====================
    async initData() {
      await this.loadEmployeeList()
      await this.loadShiftList()
      this.loadData()
    },

    async loadEmployeeList() {
      try {
        const res = await getAction('/user/getUserList')
        if (res && res.length > 0) {
          this.employeeList = res
        }
      } catch (error) {
        console.error('加载员工列表失败:', error)
        this.$message.error('加载员工列表失败')
      }
    },

    async loadShiftList() {
      try {
        const res = await getAction('/schedule/shifts')
        if (res.code === 200 && res.data) {
          this.shiftList = res.data
        }
      } catch (error) {
        console.error('加载班次列表失败:', error)
        this.$message.error('加载班次列表失败')
      }
    },

    // 重写JeecgListMixin的loadData方法
    loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1
      }

      // 计算日期范围
      const startDate = this.selectedDate.clone().startOf('month').format('YYYY-MM-DD')
      const endDate = this.selectedDate.clone().endOf('month').format('YYYY-MM-DD')

      const params = Object.assign({}, this.queryParam)
      params.start_date = startDate
      params.end_date = endDate

      this.loading = true

      getAction(this.url.list, params).then(res => {
        if (res.code === 200 && res.data) {
          this.dataSource = res.data
          this.loadCalendarData()
        } else {
          this.$message.warning(res.data || '暂无排班数据')
        }
        this.loading = false
      }).catch(error => {
        console.error('加载排班数据失败:', error)
        this.$message.error('数据加载失败')
        this.loading = false
      })
    },

    loadCalendarData() {
      // 将排班数据转换为日历数据格式
      this.calendarData = {}
      if (this.dataSource && this.dataSource.length > 0) {
        this.dataSource.forEach(schedule => {
          const dateStr = schedule.scheduleDate
          if (!this.calendarData[dateStr]) {
            this.calendarData[dateStr] = []
          }
          this.calendarData[dateStr].push(schedule)
        })
      }
    },

    // ==================== 日历相关方法 ====================
    onDateSelect(date) {
      this.selectedDate = date
      this.handleAdd() // 调用标准的新增方法
    },

    onPanelChange(date, mode) {
      this.selectedDate = date
      this.loadData() // 重新加载数据
    },

    getAssignmentsForDate(date) {
      const dateStr = date.format('YYYY-MM-DD')
      return this.calendarData[dateStr] || []
    },

    handleAssignmentClick(assignment) {
      // 显示排班详情
      this.showScheduleDetail(assignment)
    },

    // ==================== 标准CRUD方法 ====================
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = "新增排班"
      this.$refs.modalForm.disableSubmit = false

      // 设置选中的日期
      if (this.selectedDate) {
        this.$nextTick(() => {
          this.$refs.modalForm.form.setFieldsValue({
            scheduleDate: this.selectedDate
          })
        })
      }
    },

    handleBatchAssign() {
      this.$message.info('批量排班功能开发中')
    },

    modalFormOk() {
      // Modal确认后重新加载数据
      this.loadData()
    },

    // ==================== 查询相关方法 ====================
    searchQuery() {
      this.loadData(1)
    },

    searchReset() {
      this.queryParam = {
        employeeId: null,
        shiftId: null
      }
      this.loadData(1)
    },

    filterOption(input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },

    // ==================== 排班详情和操作方法 ====================
    showScheduleDetail(schedule) {
      // 使用Ant Design的Modal显示详情
      this.$info({
        title: '排班详情',
        width: 500,
        content: h => {
          return h('div', [
            h('p', `排班日期: ${schedule.scheduleDate}`),
            h('p', `员工姓名: ${schedule.employeeName}`),
            h('p', `班次信息: ${schedule.shiftName}`),
            h('p', `工作时间: ${schedule.shiftStartTime} - ${schedule.shiftEndTime}`),
            h('p', `状态: ${schedule.statusName}`),
            schedule.remark ? h('p', `备注: ${schedule.remark}`) : null
          ])
        },
        onOk: () => {
          // 可以在这里添加编辑功能
        }
      })
    },

    async exportSchedule() {
      try {
        const startDate = this.selectedDate.clone().startOf('month').format('YYYY-MM-DD')
        const endDate = this.selectedDate.clone().endOf('month').format('YYYY-MM-DD')

        this.$message.success('导出功能开发中')
        console.log('导出排班表:', { startDate, endDate })
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败')
      }
    },

    // ==================== 辅助方法 ====================
    getStatusColor(status) {
      const colorMap = {
        'SCHEDULED': 'blue',
        'CONFIRMED': 'green',
        'CANCELLED': 'red'
      }
      return colorMap[status] || 'blue'
    }
  }
}
</script>

<style lang="less" scoped>
// 参考CloisonneProduction的样式结构
.schedule-calendar {
  .calendar-wrapper {
    .ant-fullcalendar {
      border: 1px solid #d9d9d9;
      border-radius: 6px;

      .ant-fullcalendar-date {
        height: 80px;

        .schedule-cell {
          height: 100%;
          overflow: hidden;

          .assignment-tag {
            background: #3B82F6;
            color: white;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 12px;
            margin: 1px;
            display: inline-block;
            cursor: pointer;

            &:hover {
              opacity: 0.8;
            }
          }
        }
      }
    }
  }

}
</style>
