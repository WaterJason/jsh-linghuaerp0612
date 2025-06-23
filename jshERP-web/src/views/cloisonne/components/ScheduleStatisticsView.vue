<template>
  <div class="schedule-statistics-view">
    <a-card title="排班统计分析" :bordered="false">
      <!-- 统计时间范围选择 -->
      <div class="statistics-header">
        <a-space>
          <a-range-picker
            v-model="dateRange"
            format="YYYY-MM-DD"
            @change="handleDateRangeChange"
          />
          <a-select
            v-model="statisticsType"
            style="width: 120px"
            @change="handleStatisticsTypeChange"
          >
            <a-select-option value="daily">按日统计</a-select-option>
            <a-select-option value="weekly">按周统计</a-select-option>
            <a-select-option value="monthly">按月统计</a-select-option>
          </a-select>
          <a-button type="primary" @click="loadStatistics">
            <a-icon type="search" />
            查询
          </a-button>
          <a-button @click="exportStatistics">
            <a-icon type="download" />
            导出
          </a-button>
        </a-space>
      </div>

      <!-- 统计概览 -->
      <div class="statistics-overview">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-statistic
              title="总排班数"
              :value="overviewData.totalSchedules"
              suffix="个"
              :loading="loading"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="参与员工"
              :value="overviewData.totalEmployees"
              suffix="人"
              :loading="loading"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="总工时"
              :value="overviewData.totalHours"
              suffix="小时"
              :loading="loading"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="平均工时"
              :value="overviewData.averageHours"
              suffix="小时/人"
              :precision="1"
              :loading="loading"
            />
          </a-col>
        </a-row>
      </div>

      <!-- 图表展示 -->
      <div class="statistics-charts">
        <a-row :gutter="16">
          <!-- 排班分布饼图 -->
          <a-col :span="12">
            <a-card title="排班类型分布" size="small">
              <div class="chart-container" style="height: 300px;">
                <div v-if="loading" class="chart-loading">
                  <a-spin size="large" />
                </div>
                <div v-else class="chart-placeholder">
                  <div class="chart-mock">
                    <h3>排班类型分布图</h3>
                    <div class="chart-data">
                      <div v-for="item in scheduleTypeData" :key="item.type" class="data-item">
                        <span class="type-name">{{ item.type }}:</span>
                        <span class="type-value">{{ item.count }}个 ({{ item.percentage }}%)</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a-card>
          </a-col>

          <!-- 工时趋势图 -->
          <a-col :span="12">
            <a-card title="工时变化趋势" size="small">
              <div class="chart-container" style="height: 300px;">
                <div v-if="loading" class="chart-loading">
                  <a-spin size="large" />
                </div>
                <div v-else class="chart-placeholder">
                  <div class="chart-mock">
                    <h3>工时趋势图</h3>
                    <div class="trend-data">
                      <div v-for="item in trendData" :key="item.date" class="trend-item">
                        {{ item.date }}: {{ item.hours }}小时
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <!-- 员工排班统计表 -->
      <div class="employee-statistics">
        <a-card title="员工排班统计" size="small">
          <a-table
            :columns="employeeColumns"
            :data-source="employeeStatistics"
            :pagination="pagination"
            :loading="loading"
            size="small"
            bordered
          >
            <template slot="workHours" slot-scope="text">
              <span>{{ text }}小时</span>
            </template>
            
            <template slot="efficiency" slot-scope="text">
              <a-progress
                :percent="text"
                size="small"
                :show-info="false"
                style="width: 80px"
              />
              <span style="margin-left: 8px">{{ text }}%</span>
            </template>
            
            <template slot="action" slot-scope="text, record">
              <a @click="viewEmployeeDetail(record)">查看详情</a>
            </template>
          </a-table>
        </a-card>
      </div>

      <!-- 部门对比 -->
      <div class="department-comparison">
        <a-card title="部门排班对比" size="small">
          <a-row :gutter="16">
            <a-col :span="8" v-for="dept in departmentData" :key="dept.name">
              <div class="department-card">
                <h4>{{ dept.name }}</h4>
                <a-statistic
                  :value="dept.scheduleCount"
                  suffix="个排班"
                />
                <div class="department-details">
                  <div>总工时: {{ dept.totalHours }}小时</div>
                  <div>参与人数: {{ dept.employeeCount }}人</div>
                  <div>平均工时: {{ dept.averageHours }}小时/人</div>
                </div>
              </div>
            </a-col>
          </a-row>
        </a-card>
      </div>
    </a-card>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'ScheduleStatisticsView',
  props: {
    visible: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      loading: false,
      dateRange: [dayjs().subtract(7, 'days'), dayjs()],
      statisticsType: 'daily',
      overviewData: {
        totalSchedules: 156,
        totalEmployees: 24,
        totalHours: 1248,
        averageHours: 52.0
      },
      scheduleTypeData: [
        { type: '早班', count: 45, percentage: 28.8 },
        { type: '中班', count: 52, percentage: 33.3 },
        { type: '晚班', count: 38, percentage: 24.4 },
        { type: '全天班', count: 21, percentage: 13.5 }
      ],
      trendData: [
        { date: '2024-01-15', hours: 168 },
        { date: '2024-01-16', hours: 172 },
        { date: '2024-01-17', hours: 165 },
        { date: '2024-01-18', hours: 178 },
        { date: '2024-01-19', hours: 182 },
        { date: '2024-01-20', hours: 175 },
        { date: '2024-01-21', hours: 168 }
      ],
      employeeStatistics: [
        {
          id: 1,
          employeeName: '张三',
          department: '掐丝珐琅馆',
          scheduleCount: 12,
          workHours: 96,
          efficiency: 85
        },
        {
          id: 2,
          employeeName: '李四',
          department: '咖啡店',
          scheduleCount: 10,
          workHours: 80,
          efficiency: 78
        },
        {
          id: 3,
          employeeName: '王五',
          department: '掐丝珐琅馆',
          scheduleCount: 14,
          workHours: 112,
          efficiency: 92
        }
      ],
      departmentData: [
        {
          name: '掐丝珐琅馆',
          scheduleCount: 89,
          totalHours: 712,
          employeeCount: 15,
          averageHours: 47.5
        },
        {
          name: '咖啡店',
          scheduleCount: 45,
          totalHours: 360,
          employeeCount: 6,
          averageHours: 60.0
        },
        {
          name: '管理部门',
          scheduleCount: 22,
          totalHours: 176,
          employeeCount: 3,
          averageHours: 58.7
        }
      ],
      employeeColumns: [
        {
          title: '员工姓名',
          dataIndex: 'employeeName',
          key: 'employeeName',
          width: 100
        },
        {
          title: '部门',
          dataIndex: 'department',
          key: 'department',
          width: 120
        },
        {
          title: '排班次数',
          dataIndex: 'scheduleCount',
          key: 'scheduleCount',
          width: 100,
          align: 'center'
        },
        {
          title: '工作时长',
          dataIndex: 'workHours',
          key: 'workHours',
          width: 100,
          align: 'center',
          scopedSlots: { customRender: 'workHours' }
        },
        {
          title: '工作效率',
          dataIndex: 'efficiency',
          key: 'efficiency',
          width: 120,
          align: 'center',
          scopedSlots: { customRender: 'efficiency' }
        },
        {
          title: '操作',
          key: 'action',
          width: 80,
          scopedSlots: { customRender: 'action' }
        }
      ],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true
      }
    }
  },
  mounted() {
    this.loadStatistics()
  },
  methods: {
    handleDateRangeChange() {
      // 日期范围变化时自动加载数据
      this.loadStatistics()
    },
    handleStatisticsTypeChange() {
      // 统计类型变化时重新加载数据
      this.loadStatistics()
    },
    loadStatistics() {
      this.loading = true
      // 模拟API调用
      setTimeout(() => {
        this.loading = false
        this.$message.success('统计数据加载完成')
      }, 1000)
    },
    exportStatistics() {
      this.$message.info('正在导出统计数据...')
    },
    viewEmployeeDetail(record) {
      this.$message.info(`查看员工 ${record.employeeName} 的详细排班信息`)
    }
  }
}
</script>

<style scoped>
.schedule-statistics-view {
  width: 100%;
}

.statistics-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.statistics-overview {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.statistics-charts,
.employee-statistics,
.department-comparison {
  margin-bottom: 24px;
}

.chart-container {
  position: relative;
}

.chart-loading {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 8px;
}

.chart-mock {
  text-align: center;
  color: #666;
}

.chart-data .data-item,
.trend-data .trend-item {
  margin: 8px 0;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 4px;
}

.type-name {
  font-weight: bold;
  margin-right: 8px;
}

.department-card {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
  text-align: center;
}

.department-card h4 {
  margin-bottom: 12px;
  color: #333;
}

.department-details {
  margin-top: 12px;
  font-size: 12px;
  color: #666;
}

.department-details div {
  margin: 4px 0;
}
</style>
