<template>
  <a-row :gutter="24">
    <a-col :span="24">
      <a-card :bordered="false">
        <!-- 页面标题 -->
        <div class="table-page-search-wrapper">
          <div class="mb-6 flex justify-between items-center">
            <h1 class="text-2xl font-bold text-gray-800">珐琅馆值班管理</h1>
            <div class="flex items-center space-x-2">
              <a-button @click="handleBatchDuty" icon="team" v-has="'cloisonneDuty:batchAdd'">批量排班</a-button>
              <a-button @click="handleAdd" type="primary" icon="plus" v-has="'cloisonneDuty:add'">新增值班</a-button>
            </div>
          </div>
        </div>

        <!-- 月份导航和视图切换 -->
        <a-card class="mb-4">
          <div class="flex flex-col sm:flex-row justify-between items-center">
            <!-- 月份导航 -->
            <div class="flex items-center space-x-2 mb-4 sm:mb-0">
              <a-button @click="handlePrevMonth" icon="left">上个月</a-button>
              <a-button @click="handleToday">今天</a-button>
              <a-button @click="handleNextMonth" icon="right">下个月</a-button>
              <h2 class="text-lg font-semibold text-gray-700 ml-4 hidden md:block">
                {{ currentYear }}年 {{ currentMonthName }}
              </h2>
            </div>
            
            <!-- 视图切换 -->
            <div class="flex items-center">
              <a-radio-group v-model="viewMode" button-style="solid">
                <a-radio-button value="calendar">
                  <a-icon type="calendar" /> 日历视图
                </a-radio-button>
                <a-radio-button value="list">
                  <a-icon type="unordered-list" /> 列表视图
                </a-radio-button>
                <a-radio-button value="statistics">
                  <a-icon type="bar-chart" /> 统计视图
                </a-radio-button>
              </a-radio-group>
            </div>
          </div>
          
          <!-- 移动端月份显示 -->
          <h2 class="text-lg font-semibold text-gray-700 text-center mb-4 block md:hidden">
            {{ currentYear }}年 {{ currentMonthName }}
          </h2>
        </a-card>

        <!-- 视图内容区域 -->
        <div v-show="viewMode === 'calendar'">
          <duty-calendar-view 
            :schedules="dutyList" 
            :current-date="currentDate"
            @date-click="handleDateClick"
            @edit-schedule="handleEdit"
          />
        </div>
        
        <div v-show="viewMode === 'list'">
          <duty-list-view 
            :schedules="currentMonthDutyList"
            :loading="loading"
            @edit="handleEdit"
            @delete="handleDelete"
          />
        </div>
        
        <div v-show="viewMode === 'statistics'">
          <duty-statistics-view 
            :schedules="dutyList"
            :current-date="currentDate"
          />
        </div>

        <!-- 表单弹窗 -->
        <duty-form-modal 
          ref="dutyFormModal"
          @ok="modalFormOk"
          @close="modalFormClose"
        />
        
        <!-- 批量操作弹窗 -->
        <batch-duty-modal
          ref="batchDutyModal"
          @ok="modalFormOk"
          @close="modalFormClose"
        />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { mixinDevice } from '@/utils/mixin'
import DutyCalendarView from './components/DutyCalendarView'
import DutyListView from './components/DutyListView'
import DutyStatisticsView from './components/DutyStatisticsView'
import DutyFormModal from './modules/DutyFormModal'
import BatchDutyModal from './modules/BatchDutyModal'
import { getDutyList, deleteDuty, getDutyStatistics } from '@/api/cloisonne/duty'

export default {
  name: 'CloisonneDutyList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    DutyCalendarView,
    DutyListView,
    DutyStatisticsView,
    DutyFormModal,
    BatchDutyModal
  },
  data() {
    return {
      description: '珐琅馆值班管理页面',
      // 视图模式：calendar-日历视图，list-列表视图，statistics-统计视图
      viewMode: 'calendar',
      // 当前日期
      currentDate: new Date(),
      // 值班记录列表
      dutyList: [],
      // 加载状态
      loading: false,
      // 月份名称映射
      monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', 
                   '七月', '八月', '九月', '十月', '十一月', '十二月'],
      // API URL配置
      url: {
        list: "/cloisonne/duty/list",
        delete: "/cloisonne/duty/delete",
        deleteBatch: "/cloisonne/duty/deleteBatch",
        statistics: "/cloisonne/duty/statistics"
      }
    }
  },
  computed: {
    // 当前年份
    currentYear() {
      return this.currentDate.getFullYear()
    },
    // 当前月份名称
    currentMonthName() {
      return this.monthNames[this.currentDate.getMonth()]
    },
    // 当前月份的值班记录
    currentMonthDutyList() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      
      return this.dutyList.filter(duty => {
        const dutyDate = new Date(duty.dutyDate)
        return dutyDate.getFullYear() === year && dutyDate.getMonth() === month
      })
    }
  },
  created() {
    this.loadDutyList()
  },
  methods: {
    // 加载值班数据
    loadDutyList() {
      this.loading = true
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth() + 1
      const yearMonth = `${year}-${month.toString().padStart(2, '0')}`
      
      getDutyList({ yearMonth }).then(res => {
        if (res.success) {
          this.dutyList = res.result || []
        } else {
          this.$message.error('加载值班数据失败：' + res.message)
        }
      }).catch(err => {
        this.$message.error('加载值班数据失败：' + err.message)
      }).finally(() => {
        this.loading = false
      })
    },
    
    // 月份导航
    handlePrevMonth() {
      this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() - 1, 1)
      this.loadDutyList()
    },
    
    handleNextMonth() {
      this.currentDate = new Date(this.currentDate.getFullYear(), this.currentDate.getMonth() + 1, 1)
      this.loadDutyList()
    },
    
    handleToday() {
      this.currentDate = new Date()
      this.loadDutyList()
    },
    
    // 新增值班
    handleAdd() {
      this.$refs.dutyFormModal.add()
    },
    
    // 批量排班
    handleBatchDuty() {
      this.$refs.batchDutyModal.show()
    },
    
    // 日历日期点击
    handleDateClick(dateStr) {
      this.$refs.dutyFormModal.add(dateStr)
    },
    
    // 编辑值班
    handleEdit(record) {
      this.$refs.dutyFormModal.edit(record)
    },
    
    // 删除值班
    handleDelete(record) {
      const that = this
      this.$confirm({
        title: '确认删除',
        content: '确定要删除这条值班记录吗？',
        onOk() {
          deleteDuty({ id: record.id }).then(res => {
            if (res.success) {
              that.$message.success('删除成功')
              that.loadDutyList()
            } else {
              that.$message.error('删除失败：' + res.message)
            }
          }).catch(err => {
            that.$message.error('删除失败：' + err.message)
          })
        }
      })
    },
    
    // 弹窗确认回调
    modalFormOk() {
      this.loadDutyList()
    },
    
    // 弹窗关闭回调
    modalFormClose() {
      // 弹窗关闭处理
    }
  }
}
</script>

<style scoped>
.table-page-search-wrapper {
  margin-bottom: 16px;
}

.flex {
  display: flex;
}

.justify-between {
  justify-content: space-between;
}

.items-center {
  align-items: center;
}

.space-x-2 > * + * {
  margin-left: 8px;
}

.mb-4 {
  margin-bottom: 16px;
}

.mb-6 {
  margin-bottom: 24px;
}

.text-2xl {
  font-size: 24px;
}

.text-lg {
  font-size: 18px;
}

.font-bold {
  font-weight: bold;
}

.font-semibold {
  font-weight: 600;
}

.text-gray-800 {
  color: #2d3748;
}

.text-gray-700 {
  color: #4a5568;
}

.text-center {
  text-align: center;
}

.ml-4 {
  margin-left: 16px;
}

@media (max-width: 768px) {
  .hidden.md\\:block {
    display: none !important;
  }
  
  .block.md\\:hidden {
    display: block !important;
  }
}

@media (min-width: 768px) {
  .hidden.md\\:block {
    display: block !important;
  }
  
  .block.md\\:hidden {
    display: none !important;
  }
}

.flex-col {
  flex-direction: column;
}

@media (min-width: 640px) {
  .sm\\:flex-row {
    flex-direction: row;
  }
  
  .sm\\:mb-0 {
    margin-bottom: 0;
  }
}
</style>
