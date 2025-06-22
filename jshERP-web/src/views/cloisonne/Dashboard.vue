<template>
  <div class="cloisonne-dashboard">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h2>掐丝珐琅馆总览</h2>
        <p>实时监控馆内运营状况，掌握关键业务指标</p>
      </div>
      <div class="header-actions">
        <a-button @click="refreshData" :loading="loading">
          <a-icon type="reload" />
          刷新数据
        </a-button>
        <a-button type="primary" @click="showQuickActions">
          <a-icon type="plus" />
          快速操作
        </a-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <a-row :gutter="[24, 24]" class="metrics-row">
      <a-col :xs="24" :sm="12" :lg="6">
        <metric-card
          title="今日值班人员"
          :value="overviewData.onDutyCount || 0"
          suffix="人"
          icon="team"
          color="#1890ff"
          :loading="loading">
          <template #extra>
            <a-button size="small" type="link" @click="viewOnDutyStaff">
              查看详情
            </a-button>
          </template>
        </metric-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <metric-card
          title="今日咖啡店销售"
          :value="(overviewData.salesOverview && overviewData.salesOverview.coffeeSales) || 0"
          prefix="¥"
          icon="coffee"
          color="#52c41a"
          :loading="loading">
          <template #extra>
            <a-button size="small" type="link" @click="addCoffeeSales">
              录入销售
            </a-button>
          </template>
        </metric-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <metric-card
          title="今日博物馆销售"
          :value="(overviewData.salesOverview && overviewData.salesOverview.museumSales) || 0"
          prefix="¥"
          icon="shop"
          color="#fa8c16"
          :loading="loading">
          <template #extra>
            <a-button size="small" type="link" @click="openPOS">
              POS销售
            </a-button>
          </template>
        </metric-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <metric-card
          title="今日任务完成率"
          :value="(overviewData.taskStats && overviewData.taskStats.completionRate) || 0"
          suffix="%"
          icon="check-circle"
          color="#722ed1"
          :loading="loading">
          <template #extra>
            <a-button size="small" type="link" @click="viewTasks">
              查看任务
            </a-button>
          </template>
        </metric-card>
      </a-col>
    </a-row>

    <!-- 主要内容区域 -->
    <a-row :gutter="[24, 24]" class="content-row">
      <!-- 当前值班信息 -->
      <a-col :xs="24" :lg="8">
        <a-card title="当前值班信息" :bordered="false" class="current-shift-card">
          <div v-if="overviewData.currentShiftInfo && overviewData.currentShiftInfo.status === 'online'" class="shift-info">
            <div class="employee-avatar">
              <a-avatar 
                :size="64" 
                :src="overviewData.currentShiftInfo.avatar"
                :style="{ backgroundColor: '#1890ff' }">
                {{ overviewData.currentShiftInfo.employeeName && overviewData.currentShiftInfo.employeeName.charAt(0) }}
              </a-avatar>
              <div class="online-indicator"></div>
            </div>
            
            <div class="employee-details">
              <h3>{{ overviewData.currentShiftInfo.employeeName }}</h3>
              <p class="role">{{ overviewData.currentShiftInfo.employeeRole }}</p>
              <p class="shift">{{ overviewData.currentShiftInfo.shift }}</p>
              
              <div class="contact-info">
                <a-button 
                  v-if="overviewData.currentShiftInfo.phone"
                  size="small" 
                  type="link"
                  @click="callEmployee(overviewData.currentShiftInfo.phone)">
                  <a-icon type="phone" />
                  {{ overviewData.currentShiftInfo.phone }}
                </a-button>
              </div>
            </div>
          </div>
          
          <div v-else class="no-shift">
            <a-empty description="暂无值班人员" />
            <a-button type="primary" @click="addSchedule">
              安排值班
            </a-button>
          </div>
        </a-card>
      </a-col>

      <!-- 今日值班人员列表 -->
      <a-col :xs="24" :lg="8">
        <a-card title="今日值班人员" :bordered="false" class="staff-list-card">
          <template #extra>
            <a-button size="small" type="link" @click="viewAllSchedule">
              查看排班
            </a-button>
          </template>
          
          <div class="staff-list">
            <div 
              v-for="staff in overviewData.onDutyStaff" 
              :key="staff.id"
              class="staff-item">
              <a-avatar 
                :size="40" 
                :src="staff.avatar"
                :style="{ backgroundColor: getShiftColor(staff.shift) }">
                {{ staff.name && staff.name.charAt(0) }}
              </a-avatar>
              
              <div class="staff-info">
                <div class="name">{{ staff.name }}</div>
                <div class="shift">{{ staff.shift }} - {{ staff.role }}</div>
              </div>
              
              <div class="staff-status">
                <a-tag :color="getShiftColor(staff.shift)">
                  {{ staff.shift }}
                </a-tag>
              </div>
            </div>
            
            <div v-if="!overviewData.onDutyStaff || !overviewData.onDutyStaff.length" class="empty-staff">
              <a-empty description="今日暂无排班" />
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 快速操作面板 -->
      <a-col :xs="24" :lg="8">
        <a-card title="快速操作" :bordered="false" class="quick-actions-card">
          <div class="action-grid">
            <div class="action-item" @click="addCoffeeSales">
              <div class="action-icon coffee">
                <a-icon type="coffee" />
              </div>
              <div class="action-text">
                <div class="title">录入咖啡店销售</div>
                <div class="desc">记录今日销售数据</div>
              </div>
            </div>
            
            <div class="action-item" @click="openPOS">
              <div class="action-icon pos">
                <a-icon type="shopping-cart" />
              </div>
              <div class="action-text">
                <div class="title">POS销售</div>
                <div class="desc">博物馆商品销售</div>
              </div>
            </div>
            
            <div class="action-item" @click="addSchedule">
              <div class="action-icon schedule">
                <a-icon type="calendar" />
              </div>
              <div class="action-text">
                <div class="title">安排排班</div>
                <div class="desc">员工排班管理</div>
              </div>
            </div>
            
            <div class="action-item" @click="addTask">
              <div class="action-icon task">
                <a-icon type="check-square" />
              </div>
              <div class="action-text">
                <div class="title">创建任务</div>
                <div class="desc">分配工作任务</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 销售趋势图表 -->
    <a-row :gutter="[24, 24]" class="charts-row">
      <a-col :xs="24" :lg="12">
        <a-card title="销售趋势" :bordered="false">
          <template #extra>
            <a-radio-group v-model="salesChartPeriod" size="small" @change="loadSalesChart">
              <a-radio-button value="week">本周</a-radio-button>
              <a-radio-button value="month">本月</a-radio-button>
              <a-radio-button value="quarter">本季度</a-radio-button>
            </a-radio-group>
          </template>
          
          <sales-trend-chart 
            :data="salesChartData" 
            :loading="chartsLoading" 
            height="300px" />
        </a-card>
      </a-col>
      
      <a-col :xs="24" :lg="12">
        <a-card title="排班统计" :bordered="false">
          <template #extra>
            <a-button size="small" type="link" @click="viewScheduleStatistics">
              详细统计
            </a-button>
          </template>
          
          <schedule-distribution-chart 
            :data="scheduleChartData" 
            :loading="chartsLoading" 
            height="300px" />
        </a-card>
      </a-col>
    </a-row>

    <!-- 快速操作弹窗 -->
    <quick-actions-modal
      ref="quickActionsModal"
      :visible="quickActionsVisible"
      @close="quickActionsVisible = false"
      @action="handleQuickAction" />

    <!-- 咖啡店销售录入弹窗 -->
    <coffee-sales-modal
      ref="coffeeSalesModal"
      :visible="coffeeSalesVisible"
      @submit="handleCoffeeSalesSubmit"
      @cancel="coffeeSalesVisible = false" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import MetricCard from './components/MetricCard.vue'
import SalesTrendChart from './components/SalesTrendChart.vue'
import ScheduleDistributionChart from './components/ScheduleDistributionChart.vue'
import QuickActionsModal from './components/QuickActionsModal.vue'
import CoffeeSalesModal from './components/CoffeeSalesModal.vue'
import { getDashboardOverview, createCoffeeSales } from '@/api/cloisonne'

export default {
  name: 'CloisonneDashboard',
  
  components: {
    MetricCard,
    SalesTrendChart,
    ScheduleDistributionChart,
    QuickActionsModal,
    CoffeeSalesModal
  },
  
  data() {
    return {
      loading: false,
      chartsLoading: false,
      
      // 概览数据
      overviewData: {},
      
      // 图表数据
      salesChartData: [],
      scheduleChartData: [],
      salesChartPeriod: 'week',
      
      // 弹窗状态
      quickActionsVisible: false,
      coffeeSalesVisible: false,
      
      // 自动刷新定时器
      refreshTimer: null
    }
  },
  
  computed: {
    ...mapGetters(['userInfo', 'permissions'])
  },
  
  created() {
    this.loadOverviewData()
    this.loadChartsData()
    this.startAutoRefresh()
  },
  
  beforeDestroy() {
    this.stopAutoRefresh()
  },
  
  methods: {
    // 加载概览数据
    async loadOverviewData() {
      this.loading = true
      try {
        const response = await getDashboardOverview()
        if (response.code === 200) {
          this.overviewData = response.data
        } else {
          this.$message.error(response.data || '加载数据失败')
        }
      } catch (error) {
        this.$message.error('加载数据失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载图表数据
    async loadChartsData() {
      this.chartsLoading = true
      try {
        // 这里应该调用具体的图表数据接口
        // 暂时使用模拟数据
        this.salesChartData = this.generateMockSalesData()
        this.scheduleChartData = this.generateMockScheduleData()
      } catch (error) {
        this.$message.error('加载图表数据失败')
      } finally {
        this.chartsLoading = false
      }
    },
    
    // 刷新数据
    refreshData() {
      this.loadOverviewData()
      this.loadChartsData()
    },
    
    // 开始自动刷新
    startAutoRefresh() {
      this.refreshTimer = setInterval(() => {
        this.loadOverviewData()
      }, 30000) // 30秒刷新一次
    },
    
    // 停止自动刷新
    stopAutoRefresh() {
      if (this.refreshTimer) {
        clearInterval(this.refreshTimer)
        this.refreshTimer = null
      }
    },
    
    // 获取班次颜色
    getShiftColor(shift) {
      const colors = {
        '早班': '#52c41a',
        '中班': '#1890ff',
        '晚班': '#fa8c16',
        '夜班': '#722ed1',
        '全天': '#f5222d'
      }
      return colors[shift] || '#666'
    },
    
    // 显示快速操作
    showQuickActions() {
      this.quickActionsVisible = true
    },
    
    // 处理快速操作
    handleQuickAction(action) {
      switch (action) {
        case 'coffee-sales':
          this.addCoffeeSales()
          break
        case 'pos-sales':
          this.openPOS()
          break
        case 'schedule':
          this.addSchedule()
          break
        case 'task':
          this.addTask()
          break
      }
      this.quickActionsVisible = false
    },
    
    // 录入咖啡店销售
    addCoffeeSales() {
      this.coffeeSalesVisible = true
    },
    
    // 处理咖啡店销售提交
    async handleCoffeeSalesSubmit(salesData) {
      try {
        const response = await createCoffeeSales(salesData)
        if (response.code === 200) {
          this.$message.success('销售记录创建成功')
          this.coffeeSalesVisible = false
          this.loadOverviewData()
        } else {
          this.$message.error(response.data || '创建失败')
        }
      } catch (error) {
        this.$message.error('创建失败')
      }
    },
    
    // 打开POS销售
    openPOS() {
      this.$router.push('/cloisonne/pos')
    },
    
    // 添加排班
    addSchedule() {
      this.$router.push('/cloisonne/schedule')
    },
    
    // 添加任务
    addTask() {
      this.$router.push('/cloisonne/task')
    },
    
    // 查看值班人员
    viewOnDutyStaff() {
      this.$router.push('/cloisonne/schedule?view=today')
    },
    
    // 查看所有排班
    viewAllSchedule() {
      this.$router.push('/cloisonne/schedule')
    },
    
    // 查看任务
    viewTasks() {
      this.$router.push('/cloisonne/task')
    },
    
    // 查看排班统计
    viewScheduleStatistics() {
      this.$router.push('/cloisonne/schedule?view=statistics')
    },
    
    // 拨打电话
    callEmployee(phone) {
      window.open(`tel:${phone}`)
    },
    
    // 加载销售图表
    loadSalesChart() {
      this.salesChartData = this.generateMockSalesData()
    },
    
    // 生成模拟销售数据
    generateMockSalesData() {
      const data = []
      const days = this.salesChartPeriod === 'week' ? 7 : 
                   this.salesChartPeriod === 'month' ? 30 : 90
      
      for (let i = days - 1; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        
        data.push({
          date: date.toISOString().split('T')[0],
          coffee: Math.floor(Math.random() * 2000) + 500,
          museum: Math.floor(Math.random() * 3000) + 1000
        })
      }
      
      return data
    },
    
    // 生成模拟排班数据
    generateMockScheduleData() {
      return [
        { name: '早班', value: 35, color: '#52c41a' },
        { name: '中班', value: 25, color: '#1890ff' },
        { name: '晚班', value: 20, color: '#fa8c16' },
        { name: '夜班', value: 15, color: '#722ed1' },
        { name: '全天', value: 5, color: '#f5222d' }
      ]
    }
  }
}
</script>

<style lang="less" scoped>
.cloisonne-dashboard {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-content {
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
      gap: 12px;
    }
  }
  
  .metrics-row {
    margin-bottom: 24px;
  }
  
  .content-row {
    margin-bottom: 24px;
  }
  
  .charts-row {
    margin-bottom: 24px;
  }
  
  // 当前值班信息卡片
  .current-shift-card {
    .shift-info {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .employee-avatar {
        position: relative;
        
        .online-indicator {
          position: absolute;
          bottom: 4px;
          right: 4px;
          width: 12px;
          height: 12px;
          background-color: #52c41a;
          border: 2px solid #fff;
          border-radius: 50%;
        }
      }
      
      .employee-details {
        flex: 1;
        
        h3 {
          margin: 0 0 4px 0;
          font-size: 16px;
          font-weight: 600;
        }
        
        .role {
          margin: 0 0 4px 0;
          color: #666;
          font-size: 14px;
        }
        
        .shift {
          margin: 0 0 8px 0;
          color: #1890ff;
          font-size: 14px;
          font-weight: 500;
        }
        
        .contact-info {
          .ant-btn {
            padding: 0;
            height: auto;
          }
        }
      }
    }
    
    .no-shift {
      text-align: center;
      padding: 20px 0;
    }
  }
  
  // 值班人员列表卡片
  .staff-list-card {
    .staff-list {
      .staff-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;
        
        &:last-child {
          border-bottom: none;
        }
        
        .staff-info {
          flex: 1;
          
          .name {
            font-weight: 500;
            margin-bottom: 4px;
          }
          
          .shift {
            font-size: 12px;
            color: #666;
          }
        }
      }
      
      .empty-staff {
        padding: 20px 0;
        text-align: center;
      }
    }
  }
  
  // 快速操作卡片
  .quick-actions-card {
    .action-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
      
      .action-item {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 16px;
        border: 1px solid #f0f0f0;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s;
        
        &:hover {
          border-color: #1890ff;
          background-color: #f6f8ff;
        }
        
        .action-icon {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          color: #fff;
          
          &.coffee { background-color: #52c41a; }
          &.pos { background-color: #fa8c16; }
          &.schedule { background-color: #1890ff; }
          &.task { background-color: #722ed1; }
        }
        
        .action-text {
          flex: 1;
          
          .title {
            font-weight: 500;
            margin-bottom: 4px;
          }
          
          .desc {
            font-size: 12px;
            color: #666;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .cloisonne-dashboard {
    .page-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      
      .header-actions {
        width: 100%;
        justify-content: space-between;
      }
    }
    
    .quick-actions-card {
      .action-grid {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
