<template>
  <div class="sales-trend-chart">
    <div 
      ref="chartContainer" 
      :style="{ height: height }"
      v-loading="loading">
    </div>
    
    <!-- 图表为空时的占位符 -->
    <div v-if="!loading && (!data || data.length === 0)" class="chart-empty">
      <a-empty description="暂无销售数据" />
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'SalesTrendChart',
  
  props: {
    data: {
      type: Array,
      default: () => []
    },
    loading: {
      type: Boolean,
      default: false
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  
  data() {
    return {
      chart: null,
      resizeObserver: null
    }
  },
  
  watch: {
    data: {
      handler() {
        this.updateChart()
      },
      deep: true
    },
    
    loading(val) {
      if (!val) {
        this.$nextTick(() => {
          this.updateChart()
        })
      }
    }
  },
  
  mounted() {
    this.initChart()
    this.setupResize()
  },
  
  beforeDestroy() {
    this.destroyChart()
    this.cleanupResize()
  },
  
  methods: {
    // 初始化图表
    initChart() {
      if (!this.$refs.chartContainer) return
      
      this.chart = echarts.init(this.$refs.chartContainer)
      this.updateChart()
    },
    
    // 更新图表
    updateChart() {
      if (!this.chart || this.loading) return
      
      const option = this.getChartOption()
      this.chart.setOption(option, true)
    },
    
    // 获取图表配置
    getChartOption() {
      if (!this.data || this.data.length === 0) {
        return {}
      }
      
      // 处理数据
      const dates = this.data.map(item => this.formatDate(item.date))
      const coffeeData = this.data.map(item => item.coffee || 0)
      const museumData = this.data.map(item => item.museum || 0)
      const totalData = this.data.map(item => (item.coffee || 0) + (item.museum || 0))
      
      return {
        title: {
          show: false
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross',
            label: {
              backgroundColor: '#6a7985'
            }
          },
          formatter: (params) => {
            let result = `<div style="font-weight: 600; margin-bottom: 8px;">${params[0].axisValue}</div>`
            
            params.forEach(param => {
              const color = param.color
              const seriesName = param.seriesName
              const value = param.value
              result += `
                <div style="display: flex; align-items: center; margin-bottom: 4px;">
                  <span style="display: inline-block; width: 10px; height: 10px; background-color: ${color}; border-radius: 50%; margin-right: 8px;"></span>
                  <span style="flex: 1;">${seriesName}</span>
                  <span style="font-weight: 600; color: ${color};">¥${value.toLocaleString()}</span>
                </div>
              `
            })
            
            return result
          }
        },
        legend: {
          data: ['咖啡店销售', '博物馆销售', '总销售额'],
          top: 10,
          textStyle: {
            fontSize: 12
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLabel: {
            fontSize: 11,
            color: '#666'
          },
          axisLine: {
            lineStyle: {
              color: '#e8e8e8'
            }
          }
        },
        yAxis: {
          type: 'value',
          axisLabel: {
            fontSize: 11,
            color: '#666',
            formatter: (value) => {
              if (value >= 10000) {
                return (value / 10000).toFixed(1) + 'w'
              } else if (value >= 1000) {
                return (value / 1000).toFixed(1) + 'k'
              }
              return value
            }
          },
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '咖啡店销售',
            type: 'line',
            stack: false,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3
            },
            areaStyle: {
              opacity: 0.1
            },
            itemStyle: {
              color: '#52c41a'
            },
            data: coffeeData
          },
          {
            name: '博物馆销售',
            type: 'line',
            stack: false,
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3
            },
            areaStyle: {
              opacity: 0.1
            },
            itemStyle: {
              color: '#1890ff'
            },
            data: museumData
          },
          {
            name: '总销售额',
            type: 'line',
            stack: false,
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 4,
              type: 'dashed'
            },
            itemStyle: {
              color: '#fa8c16'
            },
            data: totalData
          }
        ],
        animation: true,
        animationDuration: 1000,
        animationEasing: 'cubicOut'
      }
    },
    
    // 格式化日期
    formatDate(dateStr) {
      const date = new Date(dateStr)
      const month = date.getMonth() + 1
      const day = date.getDate()
      return `${month}/${day}`
    },
    
    // 设置响应式
    setupResize() {
      // 监听窗口大小变化
      window.addEventListener('resize', this.handleResize)
      
      // 使用 ResizeObserver 监听容器大小变化
      if (window.ResizeObserver) {
        this.resizeObserver = new ResizeObserver(() => {
          this.handleResize()
        })
        this.resizeObserver.observe(this.$refs.chartContainer)
      }
    },
    
    // 处理大小变化
    handleResize() {
      if (this.chart) {
        this.chart.resize()
      }
    },
    
    // 清理响应式监听
    cleanupResize() {
      window.removeEventListener('resize', this.handleResize)
      if (this.resizeObserver) {
        this.resizeObserver.disconnect()
        this.resizeObserver = null
      }
    },
    
    // 销毁图表
    destroyChart() {
      if (this.chart) {
        this.chart.dispose()
        this.chart = null
      }
    }
  }
}
</script>

<style lang="less" scoped>
.sales-trend-chart {
  position: relative;
  width: 100%;
  
  .chart-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 300px;
    background-color: #fafafa;
    border-radius: 6px;
  }
}

// 加载状态样式
.sales-trend-chart[v-loading] {
  min-height: 300px;
}
</style>
