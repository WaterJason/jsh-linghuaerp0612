<template>
  <div class="schedule-distribution-chart">
    <a-card title="排班分布统计" :bordered="false">
      <div class="chart-container">
        <!-- 图表选项 -->
        <div class="chart-options">
          <a-radio-group v-model="chartType" @change="handleChartTypeChange">
            <a-radio-button value="pie">饼图</a-radio-button>
            <a-radio-button value="bar">柱状图</a-radio-button>
            <a-radio-button value="line">趋势图</a-radio-button>
          </a-radio-group>
          
          <a-select
            v-model="timeRange"
            style="width: 120px; margin-left: 16px"
            @change="handleTimeRangeChange"
          >
            <a-select-option value="week">本周</a-select-option>
            <a-select-option value="month">本月</a-select-option>
            <a-select-option value="quarter">本季度</a-select-option>
          </a-select>
        </div>

        <!-- 图表区域 -->
        <div class="chart-wrapper" :style="{ height: chartHeight + 'px' }">
          <div v-if="loading" class="chart-loading">
            <a-spin size="large" />
          </div>
          <div v-else-if="!hasData" class="chart-empty">
            <a-empty description="暂无数据" />
          </div>
          <div v-else ref="chartContainer" class="chart-content"></div>
        </div>

        <!-- 统计数据 -->
        <div class="statistics-panel">
          <a-row :gutter="16">
            <a-col :span="6">
              <a-statistic
                title="总排班数"
                :value="statistics.totalSchedules"
                suffix="个"
              />
            </a-col>
            <a-col :span="6">
              <a-statistic
                title="参与员工"
                :value="statistics.totalEmployees"
                suffix="人"
              />
            </a-col>
            <a-col :span="6">
              <a-statistic
                title="平均工时"
                :value="statistics.averageHours"
                suffix="小时"
                :precision="1"
              />
            </a-col>
            <a-col :span="6">
              <a-statistic
                title="覆盖率"
                :value="statistics.coverageRate"
                suffix="%"
                :precision="1"
              />
            </a-col>
          </a-row>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script>
export default {
  name: 'ScheduleDistributionChart',
  props: {
    height: {
      type: Number,
      default: 400
    },
    data: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      chartType: 'pie',
      timeRange: 'week',
      chartInstance: null,
      scheduleData: [
        { name: '早班', value: 25, hours: 8 },
        { name: '中班', value: 30, hours: 8 },
        { name: '晚班', value: 20, hours: 8 },
        { name: '全天班', value: 15, hours: 12 },
        { name: '休息', value: 10, hours: 0 }
      ]
    }
  },
  computed: {
    chartHeight() {
      return this.height - 100 // 减去选项和统计面板的高度
    },
    hasData() {
      return this.scheduleData && this.scheduleData.length > 0
    },
    statistics() {
      if (!this.hasData) {
        return {
          totalSchedules: 0,
          totalEmployees: 0,
          averageHours: 0,
          coverageRate: 0
        }
      }
      
      const totalSchedules = this.scheduleData.reduce((sum, item) => sum + item.value, 0)
      const totalHours = this.scheduleData.reduce((sum, item) => sum + (item.value * item.hours), 0)
      const averageHours = totalSchedules > 0 ? totalHours / totalSchedules : 0
      const workingSchedules = this.scheduleData.filter(item => item.hours > 0)
      const workingCount = workingSchedules.reduce((sum, item) => sum + item.value, 0)
      const coverageRate = totalSchedules > 0 ? (workingCount / totalSchedules) * 100 : 0
      
      return {
        totalSchedules,
        totalEmployees: Math.ceil(totalSchedules / 7), // 假设每人每周工作7天
        averageHours,
        coverageRate
      }
    }
  },
  mounted() {
    this.initChart()
  },
  beforeDestroy() {
    if (this.chartInstance) {
      this.chartInstance.dispose()
    }
  },
  methods: {
    initChart() {
      // 这里应该使用实际的图表库，如ECharts或G2
      // 暂时使用模拟实现
      this.$nextTick(() => {
        if (this.$refs.chartContainer) {
          this.renderChart()
        }
      })
    },
    renderChart() {
      // 模拟图表渲染
      const container = this.$refs.chartContainer
      if (!container) return
      
      // 清空容器
      container.innerHTML = ''
      
      // 创建简单的图表展示
      const chartDiv = document.createElement('div')
      chartDiv.style.cssText = `
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border-radius: 8px;
        color: white;
        font-size: 16px;
        text-align: center;
      `
      
      let content = ''
      if (this.chartType === 'pie') {
        content = `
          <div>
            <div style="font-size: 20px; margin-bottom: 10px;">排班分布饼图</div>
            <div>早班: ${this.scheduleData[0].value}个</div>
            <div>中班: ${this.scheduleData[1].value}个</div>
            <div>晚班: ${this.scheduleData[2].value}个</div>
          </div>
        `
      } else if (this.chartType === 'bar') {
        content = `
          <div>
            <div style="font-size: 20px; margin-bottom: 10px;">排班分布柱状图</div>
            <div>显示各时段排班数量对比</div>
          </div>
        `
      } else {
        content = `
          <div>
            <div style="font-size: 20px; margin-bottom: 10px;">排班趋势图</div>
            <div>显示排班数量变化趋势</div>
          </div>
        `
      }
      
      chartDiv.innerHTML = content
      container.appendChild(chartDiv)
    },
    handleChartTypeChange() {
      this.renderChart()
    },
    handleTimeRangeChange() {
      this.loadData()
    },
    loadData() {
      this.loading = true
      // 模拟数据加载
      setTimeout(() => {
        // 根据时间范围调整数据
        if (this.timeRange === 'month') {
          this.scheduleData = [
            { name: '早班', value: 120, hours: 8 },
            { name: '中班', value: 140, hours: 8 },
            { name: '晚班', value: 100, hours: 8 },
            { name: '全天班', value: 60, hours: 12 },
            { name: '休息', value: 40, hours: 0 }
          ]
        } else if (this.timeRange === 'quarter') {
          this.scheduleData = [
            { name: '早班', value: 360, hours: 8 },
            { name: '中班', value: 420, hours: 8 },
            { name: '晚班', value: 300, hours: 8 },
            { name: '全天班', value: 180, hours: 12 },
            { name: '休息', value: 120, hours: 0 }
          ]
        } else {
          this.scheduleData = [
            { name: '早班', value: 25, hours: 8 },
            { name: '中班', value: 30, hours: 8 },
            { name: '晚班', value: 20, hours: 8 },
            { name: '全天班', value: 15, hours: 12 },
            { name: '休息', value: 10, hours: 0 }
          ]
        }
        this.loading = false
        this.renderChart()
      }, 1000)
    }
  }
}
</script>

<style scoped>
.schedule-distribution-chart {
  width: 100%;
}

.chart-container {
  position: relative;
}

.chart-options {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-wrapper {
  position: relative;
  margin-bottom: 16px;
}

.chart-loading,
.chart-empty {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border-radius: 8px;
}

.chart-content {
  width: 100%;
  height: 100%;
}

.statistics-panel {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px;
}

.statistics-panel .ant-statistic {
  text-align: center;
}
</style>
