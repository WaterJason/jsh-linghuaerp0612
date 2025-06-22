<template>
  <div class="inventory-statistics">
    <a-card :bordered="false" title="盘点统计分析">
      <!-- 统计卡片 -->
      <a-row :gutter="16" style="margin-bottom: 24px;">
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="本月盘点次数"
              :value="statistics.monthlyCount"
              :value-style="{ color: '#3f8600' }"
            >
              <template #suffix>
                <a-icon type="rise" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="盘点差异率"
              :value="statistics.differenceRate"
              suffix="%"
              :value-style="{ color: '#cf1322' }"
            >
              <template #suffix>
                <a-icon type="fall" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="盘点商品数量"
              :value="statistics.itemCount"
              :value-style="{ color: '#1890ff' }"
            >
              <template #suffix>
                <a-icon type="inbox" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card>
            <a-statistic
              title="盘点金额"
              :value="statistics.totalAmount"
              prefix="¥"
              :value-style="{ color: '#722ed1' }"
            >
              <template #suffix>
                <a-icon type="dollar" />
              </template>
            </a-statistic>
          </a-card>
        </a-col>
      </a-row>

      <!-- 查询条件 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="统计时间">
                <a-range-picker
                  v-model="queryParam.dateRange"
                  format="YYYY-MM-DD"
                  placeholder="['开始时间', '结束时间']"
                  @change="onDateChange"
                />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="仓库">
                <a-select v-model="queryParam.depotId" placeholder="请选择仓库" allowClear>
                  <a-select-option value="1">主仓库</a-select-option>
                  <a-select-option value="2">分仓库</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button style="margin-left: 8px" @click="searchReset" icon="reload">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 图表区域 -->
      <a-row :gutter="16">
        <a-col :span="12">
          <a-card title="盘点趋势图" style="margin-bottom: 16px;">
            <div id="trendChart" style="height: 300px;"></div>
          </a-card>
        </a-col>
        <a-col :span="12">
          <a-card title="差异分析图" style="margin-bottom: 16px;">
            <div id="differenceChart" style="height: 300px;"></div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 详细统计表格 -->
      <a-card title="详细统计数据" style="margin-top: 16px;">
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="id"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          @change="handleTableChange"
        >
          <span slot="differenceAmount" slot-scope="text">
            <span :style="{ color: parseFloat(text) >= 0 ? '#3f8600' : '#cf1322' }">
              {{ parseFloat(text).toFixed(2) }}
            </span>
          </span>

          <span slot="differenceRate" slot-scope="text">
            <a-progress 
              :percent="Math.abs(parseFloat(text))" 
              :status="parseFloat(text) >= 0 ? 'success' : 'exception'"
              size="small" 
            />
          </span>
        </a-table>
      </a-card>
    </a-card>
  </div>
</template>

<script>
import { getAction } from '@/api/manage'

export default {
  name: 'InventoryStatistics',
  data() {
    return {
      // 统计数据
      statistics: {
        monthlyCount: 0,
        differenceRate: 0,
        itemCount: 0,
        totalAmount: 0
      },
      // 查询参数
      queryParam: {
        dateRange: []
      },
      // 表格数据
      dataSource: [],
      // 表格列配置
      columns: [
        {
          title: '盘点单号',
          dataIndex: 'checkNo',
          width: 150
        },
        {
          title: '盘点名称',
          dataIndex: 'checkName',
          width: 200
        },
        {
          title: '仓库',
          dataIndex: 'depotName',
          width: 120
        },
        {
          title: '盘点商品数',
          dataIndex: 'itemCount',
          width: 120,
          align: 'right'
        },
        {
          title: '账面金额',
          dataIndex: 'bookAmount',
          width: 120,
          align: 'right',
          customRender: (text) => {
            return '¥' + parseFloat(text || 0).toFixed(2)
          }
        },
        {
          title: '实盘金额',
          dataIndex: 'actualAmount',
          width: 120,
          align: 'right',
          customRender: (text) => {
            return '¥' + parseFloat(text || 0).toFixed(2)
          }
        },
        {
          title: '差异金额',
          dataIndex: 'differenceAmount',
          width: 120,
          align: 'right',
          scopedSlots: { customRender: 'differenceAmount' }
        },
        {
          title: '差异率',
          dataIndex: 'differenceRate',
          width: 120,
          scopedSlots: { customRender: 'differenceRate' }
        },
        {
          title: '盘点时间',
          dataIndex: 'checkTime',
          width: 150,
          customRender: (text) => {
            return text ? this.$moment(text).format('YYYY-MM-DD HH:mm') : '-'
          }
        }
      ],
      // 分页配置
      ipagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '30'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      // 加载状态
      loading: false
    }
  },
  
  mounted() {
    this.loadStatistics()
    this.loadData()
    this.initCharts()
  },
  
  methods: {
    // 加载统计数据
    loadStatistics() {
      // 模拟统计数据
      this.statistics = {
        monthlyCount: 12,
        differenceRate: 2.5,
        itemCount: 1580,
        totalAmount: 256800
      }
    },
    
    // 加载表格数据
    loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1
      }
      const params = Object.assign({}, this.queryParam)
      params.pageNo = this.ipagination.current
      params.pageSize = this.ipagination.pageSize
      
      this.loading = true
      // 模拟API调用
      setTimeout(() => {
        this.dataSource = []
        this.ipagination.total = 0
        this.loading = false
        this.$message.info('盘点统计功能开发中...')
      }, 500)
    },
    
    // 查询
    searchQuery() {
      this.loadData(1)
      this.loadStatistics()
      this.updateCharts()
    },
    
    // 重置
    searchReset() {
      this.queryParam = { dateRange: [] }
      this.loadData(1)
      this.loadStatistics()
      this.updateCharts()
    },
    
    // 日期变化
    onDateChange(dates, dateStrings) {
      this.queryParam.startDate = dateStrings[0]
      this.queryParam.endDate = dateStrings[1]
    },
    
    // 表格变化
    handleTableChange(pagination, filters, sorter) {
      this.ipagination = pagination
      this.loadData()
    },
    
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.initTrendChart()
        this.initDifferenceChart()
      })
    },
    
    // 初始化趋势图
    initTrendChart() {
      const chartDom = document.getElementById('trendChart')
      if (!chartDom) return
      
      // 这里应该使用echarts或其他图表库
      chartDom.innerHTML = '<div style="text-align: center; line-height: 300px; color: #999;">盘点趋势图开发中...</div>'
    },
    
    // 初始化差异分析图
    initDifferenceChart() {
      const chartDom = document.getElementById('differenceChart')
      if (!chartDom) return
      
      // 这里应该使用echarts或其他图表库
      chartDom.innerHTML = '<div style="text-align: center; line-height: 300px; color: #999;">差异分析图开发中...</div>'
    },
    
    // 更新图表
    updateCharts() {
      this.initTrendChart()
      this.initDifferenceChart()
    }
  }
}
</script>

<style scoped>
.inventory-statistics {
  padding: 24px;
}

.table-page-search-wrapper {
  margin-bottom: 16px;
}

.table-page-search-submitButtons {
  display: block;
  margin-bottom: 24px;
  white-space: nowrap;
}
</style>
