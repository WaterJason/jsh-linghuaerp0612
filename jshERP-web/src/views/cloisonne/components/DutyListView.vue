<template>
  <div class="duty-list-container">
    <!-- 搜索和筛选区域 -->
    <div class="search-section">
      <a-card :bordered="false" class="search-card">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-input-search
              v-model="searchText"
              placeholder="搜索员工姓名"
              @search="handleSearch"
              allowClear
            />
          </a-col>
          <a-col :span="6">
            <a-select
              v-model="filterShiftType"
              placeholder="筛选班次类型"
              allowClear
              style="width: 100%"
              @change="handleFilter"
            >
              <a-select-option value="早班">早班</a-select-option>
              <a-select-option value="晚班">晚班</a-select-option>
              <a-select-option value="全天">全天</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="6">
            <a-select
              v-model="filterStatus"
              placeholder="筛选状态"
              allowClear
              style="width: 100%"
              @change="handleFilter"
            >
              <a-select-option value="normal">正常</a-select-option>
              <a-select-option value="leave">请假</a-select-option>
              <a-select-option value="swap">调班</a-select-option>
            </a-select>
          </a-col>
          <a-col :span="6">
            <a-button @click="handleReset" icon="reload">重置</a-button>
          </a-col>
        </a-row>
      </a-card>
    </div>

    <!-- 卡片列表区域 -->
    <div class="cards-section">
      <a-spin :spinning="loading">
        <div v-if="filteredSchedules.length === 0" class="empty-state">
          <a-empty description="暂无值班记录" />
        </div>

        <div v-else class="duty-cards-grid">
          <div
            v-for="schedule in paginatedSchedules"
            :key="schedule.id"
            class="duty-card"
            :class="getDutyCardClass(schedule)"
          >
            <!-- 卡片头部 -->
            <div class="card-header">
              <div class="employee-info">
                <div class="employee-avatar" :style="{ backgroundColor: getEmployeeColor(schedule.employeeName) }">
                  {{ schedule.employeeName.charAt(0) }}
                </div>
                <div class="employee-details">
                  <h4 class="employee-name">{{ schedule.employeeName }}</h4>
                  <span class="duty-date">{{ formatDate(schedule.dutyDate) }}</span>
                </div>
              </div>
              <div class="card-actions">
                <a-dropdown>
                  <a-button type="text" icon="more" size="small" />
                  <a-menu slot="overlay">
                    <a-menu-item @click="handleEdit(schedule)" v-has="'cloisonneDuty:edit'">
                      <a-icon type="edit" /> 编辑
                    </a-menu-item>
                    <a-menu-item @click="handleDelete(schedule)" v-has="'cloisonneDuty:delete'">
                      <a-icon type="delete" /> 删除
                    </a-menu-item>
                  </a-menu>
                </a-dropdown>
              </div>
            </div>

            <!-- 卡片内容 -->
            <div class="card-content">
              <div class="shift-info">
                <div class="shift-badge" :class="`shift-${schedule.shiftType}`">
                  {{ schedule.shiftType }}
                </div>
                <div class="time-info">
                  <span class="time-text">{{ schedule.startTime }} - {{ schedule.endTime }}</span>
                  <span class="duration-text">{{ schedule.workHours }}小时</span>
                </div>
              </div>

              <div class="work-details">
                <div class="detail-item" v-if="schedule.workArea">
                  <a-icon type="environment" />
                  <span>{{ schedule.workArea }}</span>
                </div>
                <div class="detail-item" v-if="schedule.notes">
                  <a-icon type="file-text" />
                  <span>{{ schedule.notes }}</span>
                </div>
              </div>
            </div>

            <!-- 卡片底部 -->
            <div class="card-footer">
              <div class="status-info">
                <a-tag :color="getStatusColor(schedule.status)" size="small">
                  {{ getStatusText(schedule.status) }}
                </a-tag>
                <span class="priority-indicator" v-if="schedule.priority === 'high'">
                  <a-icon type="exclamation-circle" style="color: #ff4d4f;" />
                  重要
                </span>
              </div>
            </div>
          </div>
        </div>
      </a-spin>
    </div>

    <!-- 分页区域 -->
    <div class="pagination-section" v-if="filteredSchedules.length > 0">
      <a-pagination
        v-model="currentPage"
        :total="filteredSchedules.length"
        :page-size="pageSize"
        :show-size-changer="true"
        :show-quick-jumper="true"
        :show-total="(total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`"
        :page-size-options="['12', '24', '48', '96']"
        @change="handlePageChange"
        @showSizeChange="handlePageSizeChange"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'DutyListView',
  props: {
    schedules: {
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
      // 搜索和筛选
      searchText: '',
      filterShiftType: undefined,
      filterStatus: undefined,

      // 分页
      currentPage: 1,
      pageSize: 12,

      // 员工颜色配置
      employeeColors: ['#ff7875', '#ffa940', '#fadb14', '#73d13d', '#40a9ff', '#b37feb', '#f759ab']
    }
  },
  computed: {
    // 筛选后的数据
    filteredSchedules() {
      let filtered = [...this.schedules]

      // 搜索筛选
      if (this.searchText) {
        filtered = filtered.filter(item =>
          item.employeeName.toLowerCase().includes(this.searchText.toLowerCase())
        )
      }

      // 班次类型筛选
      if (this.filterShiftType) {
        filtered = filtered.filter(item => item.shiftType === this.filterShiftType)
      }

      // 状态筛选
      if (this.filterStatus) {
        filtered = filtered.filter(item => item.status === this.filterStatus)
      }

      return filtered
    },

    // 分页后的数据
    paginatedSchedules() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredSchedules.slice(start, end)
    }
  },
  methods: {
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'normal': 'green',
        'leave': 'orange',
        'swap': 'blue'
      }
      return colorMap[status] || 'default'
    },

    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'normal': '正常',
        'leave': '请假',
        'swap': '调班'
      }
      return textMap[status] || status
    },

    // 获取员工颜色
    getEmployeeColor(employeeName) {
      const index = employeeName.charCodeAt(0) % this.employeeColors.length
      return this.employeeColors[index]
    },

    // 获取值班卡片样式类
    getDutyCardClass(schedule) {
      const classes = ['duty-card']
      if (schedule.priority === 'high') {
        classes.push('high-priority')
      }
      return classes.join(' ')
    },

    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr)
      const month = date.getMonth() + 1
      const day = date.getDate()
      const weekDay = ['日', '一', '二', '三', '四', '五', '六'][date.getDay()]
      return `${month}月${day}日 周${weekDay}`
    },

    // 搜索处理
    handleSearch() {
      this.currentPage = 1
    },

    // 筛选处理
    handleFilter() {
      this.currentPage = 1
    },

    // 重置筛选
    handleReset() {
      this.searchText = ''
      this.filterShiftType = undefined
      this.filterStatus = undefined
      this.currentPage = 1
    },

    // 分页变化
    handlePageChange(page) {
      this.currentPage = page
    },

    // 页面大小变化
    handlePageSizeChange(current, size) {
      this.pageSize = size
      this.currentPage = 1
    },

    // 处理编辑
    handleEdit(record) {
      this.$emit('edit', record)
    },

    // 处理删除
    handleDelete(record) {
      this.$confirm({
        title: '确认删除',
        content: '确定要删除这条值班记录吗？',
        onOk: () => {
          this.$emit('delete', record)
        }
      })
    }
  }
}
</script>

<style scoped>
.duty-list-container {
  background: #f5f5f5;
  min-height: 600px;
  padding: 16px;
}

/* 搜索区域 */
.search-section {
  margin-bottom: 16px;
}

.search-card {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 卡片网格 */
.cards-section {
  margin-bottom: 16px;
}

.duty-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.duty-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 2px solid transparent;
}

.duty-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
}

.duty-card.high-priority {
  border-color: #ff4d4f;
}

/* 卡片头部 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.employee-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.employee-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.employee-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.employee-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.duty-date {
  font-size: 12px;
  color: #666;
}

.card-actions {
  opacity: 0;
  transition: opacity 0.2s;
}

.duty-card:hover .card-actions {
  opacity: 1;
}

/* 卡片内容 */
.card-content {
  margin-bottom: 12px;
}

.shift-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.shift-badge {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.shift-早班 {
  background: linear-gradient(135deg, #fa8c16 0%, #ffa940 100%);
}

.shift-晚班 {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.shift-全天 {
  background: linear-gradient(135deg, #1890ff 0%, #40a9ff 100%);
}

.time-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.time-text {
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.duration-text {
  font-size: 12px;
  color: #666;
}

.work-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #666;
}

.detail-item .anticon {
  color: #999;
}

/* 卡片底部 */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.priority-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #ff4d4f;
  font-weight: 600;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 分页区域 */
.pagination-section {
  display: flex;
  justify-content: center;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .duty-cards-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}

@media (max-width: 768px) {
  .duty-list-container {
    padding: 8px;
  }

  .duty-cards-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .duty-card {
    padding: 12px;
  }

  .employee-avatar {
    width: 32px;
    height: 32px;
    font-size: 14px;
  }

  .employee-name {
    font-size: 14px;
  }

  .shift-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .time-info {
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .search-section .ant-row {
    flex-direction: column;
  }

  .search-section .ant-col {
    width: 100%;
    margin-bottom: 8px;
  }
}
</style>
