<template>
  <div class="cloisonne-schedule">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-title">
        <h2>排班管理</h2>
        <p>掐丝珐琅馆员工排班计划管理</p>
      </div>
      <div class="header-actions">
        <a-button-group>
          <a-button :type="viewMode === 'calendar' ? 'primary' : 'default'" @click="setViewMode('calendar')">
            <a-icon type="calendar" />
            日历视图
          </a-button>
          <a-button :type="viewMode === 'list' ? 'primary' : 'default'" @click="setViewMode('list')">
            <a-icon type="unordered-list" />
            列表视图
          </a-button>
          <a-button :type="viewMode === 'statistics' ? 'primary' : 'default'" @click="setViewMode('statistics')">
            <a-icon type="bar-chart" />
            统计视图
          </a-button>
        </a-button-group>
        
        <a-button type="primary" @click="showAddModal" v-if="hasAddPermission">
          <a-icon type="plus" />
          新增排班
        </a-button>
      </div>
    </div>

    <!-- 查询条件 -->
    <a-card class="search-card" :bordered="false">
      <a-form layout="inline" @submit.prevent="handleSearch">
        <a-form-item label="日期范围">
          <a-range-picker
            v-model="searchForm.dateRange"
            :ranges="dateRangePresets"
            format="YYYY-MM-DD"
            @change="handleDateRangeChange" />
        </a-form-item>
        
        <a-form-item label="员工">
          <a-select
            v-model="searchForm.employeeId"
            placeholder="选择员工"
            style="width: 150px"
            allow-clear>
            <a-select-option 
              v-for="employee in employeeList"
              :key="employee.id"
              :value="employee.id">
              {{ employee.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="班次">
          <a-select
            v-model="searchForm.shiftType"
            placeholder="选择班次"
            style="width: 120px"
            allow-clear>
            <a-select-option value="早班">早班</a-select-option>
            <a-select-option value="中班">中班</a-select-option>
            <a-select-option value="晚班">晚班</a-select-option>
            <a-select-option value="夜班">夜班</a-select-option>
            <a-select-option value="全天">全天</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="状态">
          <a-select
            v-model="searchForm.status"
            placeholder="选择状态"
            style="width: 120px"
            allow-clear>
            <a-select-option value="normal">正常</a-select-option>
            <a-select-option value="leave">请假</a-select-option>
            <a-select-option value="swap">调班</a-select-option>
            <a-select-option value="absent">缺勤</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item>
          <div style="display: flex; gap: 8px;">
            <a-button type="primary" html-type="submit" :loading="loading">
              <a-icon type="search" />
              查询
            </a-button>
            <a-button @click="handleReset">
              <a-icon type="reload" />
              重置
            </a-button>
          </div>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 主要内容区域 -->
    <div class="content-area">
      <!-- 日历视图 -->
      <schedule-calendar-view
        v-if="viewMode === 'calendar'"
        :schedule-data="scheduleList"
        :loading="loading"
        @add-schedule="handleAddSchedule"
        @edit-schedule="handleEditSchedule"
        @delete-schedule="handleDeleteSchedule" />

      <!-- 列表视图 -->
      <schedule-list-view
        v-if="viewMode === 'list'"
        :schedule-data="scheduleList"
        :loading="loading"
        :has-edit-permission="hasEditPermission"
        :has-delete-permission="hasDeletePermission"
        @edit-schedule="handleEditSchedule"
        @delete-schedule="handleDeleteSchedule"
        @batch-update-status="handleBatchUpdateStatus" />

      <!-- 统计视图 -->
      <schedule-statistics-view
        v-if="viewMode === 'statistics'"
        :statistics-data="statisticsData"
        :loading="statisticsLoading" />
    </div>

    <!-- 新增/编辑排班弹窗 -->
    <schedule-form-modal
      ref="scheduleFormModal"
      :visible="formModalVisible"
      :form-data="currentSchedule"
      :employee-list="employeeList"
      :is-edit="isEditMode"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel" />

    <!-- 批量操作弹窗 -->
    <batch-operation-modal
      ref="batchOperationModal"
      :visible="batchModalVisible"
      :selected-records="selectedRecords"
      @submit="handleBatchSubmit"
      @cancel="handleBatchCancel" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import moment from 'moment'
import { mapGetters } from 'vuex'
import ScheduleCalendarView from './components/ScheduleCalendarView.vue'
import ScheduleListView from './components/ScheduleListView.vue'
import ScheduleStatisticsView from './components/ScheduleStatisticsView.vue'
import ScheduleFormModal from './components/ScheduleFormModal.vue'
import BatchOperationModal from './components/BatchOperationModal.vue'
import { getScheduleList, getScheduleStatistics, addSchedule, updateSchedule, deleteSchedule, batchUpdateScheduleStatus, getEmployees } from '@/api/cloisonne'

export default {
  name: 'CloisonneSchedule',
  
  components: {
    ScheduleCalendarView,
    ScheduleListView,
    ScheduleStatisticsView,
    ScheduleFormModal,
    BatchOperationModal
  },
  
  data() {
    return {
      // 视图模式
      viewMode: 'calendar',
      
      // 加载状态
      loading: false,
      statisticsLoading: false,
      
      // 搜索表单
      searchForm: {
        dateRange: [moment().startOf('month'), moment().endOf('month')],
        employeeId: null,
        shiftType: null,
        status: null
      },
      
      // 数据
      scheduleList: [],
      statisticsData: {},
      employeeList: [],
      
      // 弹窗状态
      formModalVisible: false,
      batchModalVisible: false,
      isEditMode: false,
      currentSchedule: {},
      selectedRecords: [],
      
      // 日期范围预设
      dateRangePresets: {
        '本周': [moment().startOf('week'), moment().endOf('week')],
        '本月': [moment().startOf('month'), moment().endOf('month')],
        '下月': [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')],
        '最近30天': [moment().subtract(30, 'days'), moment()],
        '最近90天': [moment().subtract(90, 'days'), moment()]
      }
    }
  },
  
  computed: {
    ...mapGetters(['userInfo', 'permissionList']),

    hasAddPermission() {
      return this.permissionList && this.permissionList.some(item => item.url === '/cloisonne/schedule' && item.btnStr && item.btnStr.includes('1'))
    },

    hasEditPermission() {
      return this.permissionList && this.permissionList.some(item => item.url === '/cloisonne/schedule' && item.btnStr && item.btnStr.includes('2'))
    },

    hasDeletePermission() {
      return this.permissionList && this.permissionList.some(item => item.url === '/cloisonne/schedule' && item.btnStr && item.btnStr.includes('3'))
    }
  },
  
  created() {
    this.loadEmployeeList()
    this.loadScheduleData()
    if (this.viewMode === 'statistics') {
      this.loadStatisticsData()
    }
  },
  
  methods: {
    // 设置视图模式
    setViewMode(mode) {
      this.viewMode = mode
      if (mode === 'statistics' && Object.keys(this.statisticsData).length === 0) {
        this.loadStatisticsData()
      }
    },
    
    // 加载员工列表
    async loadEmployeeList() {
      try {
        const response = await getEmployees()
        if (response.code === 200) {
          this.employeeList = response.data
        }
      } catch (error) {
        this.$message.error('加载员工列表失败')
      }
    },
    
    // 加载排班数据
    async loadScheduleData() {
      this.loading = true
      try {
        const params = {
          startDate: this.searchForm.dateRange && this.searchForm.dateRange[0] && this.searchForm.dateRange[0].format('YYYY-MM-DD'),
          endDate: this.searchForm.dateRange && this.searchForm.dateRange[1] && this.searchForm.dateRange[1].format('YYYY-MM-DD'),
          employeeId: this.searchForm.employeeId,
          shiftType: this.searchForm.shiftType,
          status: this.searchForm.status
        }
        
        const response = await getScheduleList(params)
        if (response.code === 200) {
          this.scheduleList = response.data
        } else {
          this.$message.error(response.data || '加载排班数据失败')
        }
      } catch (error) {
        this.$message.error('加载排班数据失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载统计数据
    async loadStatisticsData() {
      this.statisticsLoading = true
      try {
        const params = {
          startDate: this.searchForm.dateRange && this.searchForm.dateRange[0] && this.searchForm.dateRange[0].format('YYYY-MM-DD'),
          endDate: this.searchForm.dateRange && this.searchForm.dateRange[1] && this.searchForm.dateRange[1].format('YYYY-MM-DD')
        }
        
        const response = await getScheduleStatistics(params)
        if (response.code === 200) {
          this.statisticsData = response.data
        } else {
          this.$message.error(response.data || '加载统计数据失败')
        }
      } catch (error) {
        this.$message.error('加载统计数据失败')
      } finally {
        this.statisticsLoading = false
      }
    },
    
    // 搜索处理
    handleSearch() {
      this.loadScheduleData()
      if (this.viewMode === 'statistics') {
        this.loadStatisticsData()
      }
    },
    
    // 重置搜索
    handleReset() {
      this.searchForm = {
        dateRange: [moment().startOf('month'), moment().endOf('month')],
        employeeId: null,
        shiftType: null,
        status: null
      }
      this.handleSearch()
    },
    
    // 日期范围变化
    handleDateRangeChange() {
      this.handleSearch()
    },
    
    // 显示新增弹窗
    showAddModal() {
      this.isEditMode = false
      this.currentSchedule = {}
      this.formModalVisible = true
    },
    
    // 新增排班
    handleAddSchedule(scheduleData) {
      this.isEditMode = false
      this.currentSchedule = scheduleData
      this.formModalVisible = true
    },
    
    // 编辑排班
    handleEditSchedule(schedule) {
      this.isEditMode = true
      this.currentSchedule = { ...schedule }
      this.formModalVisible = true
    },
    
    // 删除排班
    handleDeleteSchedule(schedule) {
      this.$confirm({
        title: '确认删除',
        content: `确定要删除 ${schedule.employeeName} 在 ${schedule.scheduleDate} 的排班吗？`,
        onOk: async () => {
          try {
            const response = await deleteSchedule(schedule.id)
            if (response.code === 200) {
              this.$message.success('删除成功')
              this.loadScheduleData()
            } else {
              this.$message.error(response.data || '删除失败')
            }
          } catch (error) {
            this.$message.error('删除失败')
          }
        }
      })
    },
    
    // 表单提交
    async handleFormSubmit(formData) {
      try {
        let response
        if (this.isEditMode) {
          response = await updateSchedule(formData)
        } else {
          response = await addSchedule(formData)
        }
        
        if (response.code === 200) {
          this.$message.success(this.isEditMode ? '更新成功' : '创建成功')
          this.formModalVisible = false
          this.loadScheduleData()
        } else {
          this.$message.error(response.data || '操作失败')
        }
      } catch (error) {
        this.$message.error('操作失败')
      }
    },
    
    // 表单取消
    handleFormCancel() {
      this.formModalVisible = false
      this.currentSchedule = {}
    },
    
    // 批量更新状态
    handleBatchUpdateStatus(records) {
      this.selectedRecords = records
      this.batchModalVisible = true
    },
    
    // 批量操作提交
    async handleBatchSubmit(operationData) {
      try {
        const response = await batchUpdateScheduleStatus(operationData)
        if (response.code === 200) {
          this.$message.success('批量操作成功')
          this.batchModalVisible = false
          this.loadScheduleData()
        } else {
          this.$message.error(response.data || '批量操作失败')
        }
      } catch (error) {
        this.$message.error('批量操作失败')
      }
    },
    
    // 批量操作取消
    handleBatchCancel() {
      this.batchModalVisible = false
      this.selectedRecords = []
    }
  }
}
</script>

<style lang="less" scoped>
.cloisonne-schedule {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-title {
      h2 {
        margin: 0 0 4px 0;
        color: #333;
        font-size: 24px;
        font-weight: 600;
      }
      
      p {
        margin: 0;
        color: #666;
        font-size: 14px;
      }
    }
    
    .header-actions {
      display: flex;
      align-items: center;
      gap: 16px;
    }
  }
  
  .search-card {
    margin-bottom: 24px;
    
    .ant-form-item {
      margin-bottom: 0;
    }
  }
  
  .content-area {
    min-height: 500px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .cloisonne-schedule {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      
      .header-actions {
        width: 100%;
        justify-content: space-between;
      }
    }
    
    .search-card {
      .ant-form {
        .ant-form-item {
          margin-bottom: 16px;
        }
      }
    }
  }
}
</style>
