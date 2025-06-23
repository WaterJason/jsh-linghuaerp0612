<template>
  <div class="schedule-calendar-container">
    <!-- 页面标题和操作按钮 -->
    <div class="page-header">
      <div class="page-title">
        <h2>掐丝珐琅馆排班</h2>
        <p>员工排班管理和日历视图</p>
      </div>
      <div class="page-actions">
        <a-button type="primary" @click="exportSchedule" icon="download">
          导出排班
        </a-button>
        <a-button @click="addSchedule" icon="plus">
          新增排班
        </a-button>
      </div>
    </div>

    <!-- 简化的日历视图 -->
    <div class="calendar-wrapper">
      <a-card title="排班日历" :bordered="false">
        <a-calendar v-model="selectedDate" @select="onDateSelect">
          <template slot="dateCellRender" slot-scope="value">
            <div class="schedule-events">
              <div 
                v-for="schedule in getSchedulesForDate(value)" 
                :key="schedule.id"
                class="schedule-event"
                @click="showScheduleDetail(schedule)"
              >
                {{ schedule.employeeName }}
              </div>
            </div>
          </template>
        </a-calendar>
      </a-card>
    </div>

    <!-- 排班列表 -->
    <div class="schedule-list">
      <a-card title="排班列表" :bordered="false">
        <a-table 
          :columns="columns" 
          :dataSource="scheduleList" 
          :loading="loading"
          :pagination="pagination"
          @change="handleTableChange"
        >
          <template slot="action" slot-scope="text, record">
            <a-button size="small" @click="editSchedule(record)" style="margin-right: 8px;">
              编辑
            </a-button>
            <a-button size="small" type="danger" @click="deleteSchedule(record)">
              删除
            </a-button>
          </template>
        </a-table>
      </a-card>
    </div>

    <!-- 新增排班弹窗 -->
    <a-modal
      :visible="addScheduleVisible"
      title="新增排班"
      width="500px"
      @ok="handleAddSchedule"
      @cancel="cancelAddSchedule"
      :confirmLoading="addScheduleLoading"
    >
      <a-form
        ref="addScheduleForm"
        :model="scheduleForm"
        :rules="scheduleRules"
        layout="vertical"
      >
        <a-form-item label="排班日期" name="scheduleDate">
          <a-date-picker
            v-model="scheduleForm.scheduleDate"
            style="width: 100%"
            placeholder="选择排班日期"
          />
        </a-form-item>

        <a-form-item label="选择班次" name="shiftId">
          <a-select
            v-model="scheduleForm.shiftId"
            placeholder="请选择班次"
            style="width: 100%"
          >
            <a-select-option
              v-for="shift in shiftList"
              :key="shift.id"
              :value="shift.id"
            >
              {{ shift.shiftName }} ({{ shift.startTime }}-{{ shift.endTime }})
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="选择员工" name="employeeIds">
          <a-select
            v-model="scheduleForm.employeeIds"
            mode="multiple"
            placeholder="请选择员工（支持多选）"
            style="width: 100%"
            show-search
            :filter-option="filterEmployee"
          >
            <a-select-option
              v-for="employee in employeeList"
              :key="employee.id"
              :value="employee.id"
            >
              {{ employee.username }}
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="备注" name="remark">
          <a-textarea
            v-model="scheduleForm.remark"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 排班详情弹窗 -->
    <a-modal
      :visible="scheduleDetailVisible"
      title="排班详情"
      width="500px"
      :footer="null"
      @cancel="scheduleDetailVisible = false"
    >
      <div v-if="currentSchedule" class="schedule-detail">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="排班日期">
            {{ currentSchedule.scheduleDate }}
          </a-descriptions-item>
          <a-descriptions-item label="员工姓名">
            {{ currentSchedule.employeeName }}
          </a-descriptions-item>
          <a-descriptions-item label="班次信息">
            {{ currentSchedule.shiftName }}
          </a-descriptions-item>
          <a-descriptions-item label="工作时间">
            {{ currentSchedule.shiftStartTime }} - {{ currentSchedule.shiftEndTime }}
          </a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(currentSchedule.status)">
              {{ currentSchedule.statusName }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="备注" v-if="currentSchedule.remark">
            {{ currentSchedule.remark }}
          </a-descriptions-item>
        </a-descriptions>

        <div class="schedule-actions" style="margin-top: 16px; text-align: right;">
          <a-button @click="editSchedule(currentSchedule)" style="margin-right: 8px;">
            编辑
          </a-button>
          <a-button type="danger" @click="deleteSchedule(currentSchedule)">
            删除
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script>
import dayjs from 'dayjs'
import { getAction, postAction, deleteAction } from '@/api/manage'

export default {
  name: 'ScheduleCalendarSimple',
  data() {
    return {
      selectedDate: dayjs(),
      
      // 数据列表
      scheduleList: [],
      employeeList: [],
      shiftList: [],

      // 表格配置
      columns: [
        { title: '排班日期', dataIndex: 'scheduleDate', key: 'scheduleDate' },
        { title: '员工姓名', dataIndex: 'employeeName', key: 'employeeName' },
        { title: '班次', dataIndex: 'shiftName', key: 'shiftName' },
        { title: '工作时间', dataIndex: 'workTime', key: 'workTime' },
        { title: '状态', dataIndex: 'status', key: 'status' },
        { title: '操作', key: 'action', scopedSlots: { customRender: 'action' } }
      ],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0
      },

      // 新增排班弹窗
      addScheduleVisible: false,
      addScheduleLoading: false,
      scheduleForm: {
        scheduleDate: null,
        employeeIds: [],
        shiftId: null,
        remark: ''
      },
      scheduleRules: {
        scheduleDate: [{ required: true, message: '请选择排班日期' }],
        employeeIds: [{ required: true, message: '请选择员工' }],
        shiftId: [{ required: true, message: '请选择班次' }]
      },

      // 排班详情弹窗
      scheduleDetailVisible: false,
      currentSchedule: null,

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
      await this.loadEmployeeList()
      await this.loadShiftList()
      await this.loadScheduleData()
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

    async loadScheduleData() {
      try {
        this.loading = true
        const startDate = dayjs().startOf('month').format('YYYY-MM-DD')
        const endDate = dayjs().endOf('month').format('YYYY-MM-DD')

        const res = await getAction('/schedule/entries', {
          start_date: startDate,
          end_date: endDate,
          page: this.pagination.current,
          size: this.pagination.pageSize
        })

        if (res.code === 200 && res.data) {
          this.scheduleList = res.data.rows || res.data
          this.pagination.total = res.data.total || this.scheduleList.length
        } else {
          this.$message.warning(res.data || '暂无排班数据')
        }
      } catch (error) {
        console.error('加载排班数据失败:', error)
        this.$message.error('加载排班数据失败')
      } finally {
        this.loading = false
      }
    },

    // ==================== 日历事件处理 ====================
    onDateSelect(date) {
      this.selectedDate = date
      this.scheduleForm.scheduleDate = date
      this.addScheduleVisible = true
    },

    getSchedulesForDate(date) {
      const dateStr = date.format('YYYY-MM-DD')
      return this.scheduleList.filter(schedule => 
        schedule.scheduleDate === dateStr
      )
    },

    // ==================== 表格事件处理 ====================
    handleTableChange(pagination) {
      this.pagination = pagination
      this.loadScheduleData()
    },

    // ==================== 排班操作方法 ====================
    addSchedule() {
      this.scheduleForm = {
        scheduleDate: this.selectedDate,
        employeeIds: [],
        shiftId: null,
        remark: ''
      }
      this.addScheduleVisible = true
    },

    async handleAddSchedule() {
      try {
        await this.$refs.addScheduleForm.validate()
        this.addScheduleLoading = true

        if (!this.scheduleForm.employeeIds || this.scheduleForm.employeeIds.length === 0) {
          this.$message.warning('请选择至少一个员工')
          return
        }

        const scheduleDate = this.scheduleForm.scheduleDate.format('YYYY-MM-DD')
        const entries = this.scheduleForm.employeeIds.map(employeeId => ({
          scheduleDate: scheduleDate,
          employeeId: employeeId,
          shiftId: this.scheduleForm.shiftId,
          status: 'SCHEDULED',
          remark: this.scheduleForm.remark
        }))

        const res = await postAction('/schedule/entry', {
          entries: entries
        })

        if (res.code === 200) {
          this.$message.success('新增排班成功')
          this.addScheduleVisible = false
          this.loadScheduleData()
        } else {
          this.$message.error(res.data || '新增排班失败')
        }
      } catch (error) {
        console.error('新增排班失败:', error)
        this.$message.error('新增排班失败')
      } finally {
        this.addScheduleLoading = false
      }
    },

    cancelAddSchedule() {
      this.addScheduleVisible = false
      this.scheduleForm = {
        scheduleDate: null,
        employeeIds: [],
        shiftId: null,
        remark: ''
      }
      if (this.$refs.addScheduleForm) {
        this.$refs.addScheduleForm.resetFields()
      }
    },

    showScheduleDetail(schedule) {
      this.currentSchedule = schedule
      this.scheduleDetailVisible = true
    },

    editSchedule(schedule) {
      this.$message.info('编辑功能开发中')
    },

    async deleteSchedule(schedule) {
      try {
        await this.$confirm({
          title: '确认删除',
          content: '确定要删除这条排班记录吗？',
          okText: '确定',
          cancelText: '取消'
        })

        const res = await deleteAction(`/schedule/entry/${schedule.id}`)
        
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.scheduleDetailVisible = false
          this.loadScheduleData()
        } else {
          this.$message.error(res.data || '删除失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除排班失败:', error)
          this.$message.error('删除失败')
        }
      }
    },

    async exportSchedule() {
      try {
        const startDate = dayjs().startOf('month').format('YYYY-MM-DD')
        const endDate = dayjs().endOf('month').format('YYYY-MM-DD')
        
        const exportUrl = `/schedule/export?start_date=${startDate}&end_date=${endDate}&format=excel`
        
        const link = document.createElement('a')
        link.href = this.$http.defaults.baseURL + exportUrl
        link.download = `排班表_${startDate}_${endDate}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        
        this.$message.success('导出成功')
      } catch (error) {
        console.error('导出失败:', error)
        this.$message.error('导出失败')
      }
    },

    // ==================== 辅助方法 ====================
    filterEmployee(input, option) {
      return option.children[0].children.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },

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
.schedule-calendar-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding: 24px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .page-title {
      h2 {
        margin: 0;
        color: #262626;
        font-size: 24px;
        font-weight: 600;
      }

      p {
        margin: 4px 0 0 0;
        color: #8c8c8c;
        font-size: 14px;
      }
    }

    .page-actions {
      display: flex;
      gap: 12px;
    }
  }

  .calendar-wrapper {
    margin-bottom: 24px;
  }

  .schedule-events {
    .schedule-event {
      background: #1890ff;
      color: white;
      padding: 2px 6px;
      margin: 1px 0;
      border-radius: 3px;
      font-size: 12px;
      cursor: pointer;
      
      &:hover {
        background: #40a9ff;
      }
    }
  }
}
</style>
