<template>
  <div class="production-statistics">
    <a-row :gutter="16">
      <!-- 任务统计卡片 -->
      <a-col :md="6" :sm="12" :xs="24">
        <div class="stat-card task-stats">
          <div class="stat-icon">
            <a-icon type="file-text" />
          </div>
          <div class="stat-content">
            <div class="stat-title">总任务数</div>
            <div class="stat-value">{{ statistics.totalTasks || 0 }}</div>
            <div class="stat-trend">
              <span :class="getTrendClass(statistics.taskTrend)">
                <a-icon :type="getTrendIcon(statistics.taskTrend)" />
                {{ Math.abs(statistics.taskTrend || 0) }}%
              </span>
              <span class="trend-label">较昨日</span>
            </div>
          </div>
        </div>
      </a-col>
      
      <!-- 完成率统计卡片 -->
      <a-col :md="6" :sm="12" :xs="24">
        <div class="stat-card completion-stats">
          <div class="stat-icon">
            <a-icon type="check-circle" />
          </div>
          <div class="stat-content">
            <div class="stat-title">完成率</div>
            <div class="stat-value">{{ getCompletionRate() }}%</div>
            <div class="stat-progress">
              <a-progress 
                :percent="getCompletionRate()" 
                :size="'small'"
                :strokeColor="getCompletionColor()"
                :showInfo="false" />
            </div>
          </div>
        </div>
      </a-col>
      
      <!-- 工人效率统计卡片 -->
      <a-col :md="6" :sm="12" :xs="24">
        <div class="stat-card efficiency-stats">
          <div class="stat-icon">
            <a-icon type="team" />
          </div>
          <div class="stat-content">
            <div class="stat-title">平均效率</div>
            <div class="stat-value">{{ statistics.avgEfficiency || 0 }}%</div>
            <div class="stat-detail">
              <span class="detail-item">
                <a-icon type="user" />
                {{ statistics.activeWorkers || 0 }}人在线
              </span>
            </div>
          </div>
        </div>
      </a-col>
      
      <!-- 质量统计卡片 -->
      <a-col :md="6" :sm="12" :xs="24">
        <div class="stat-card quality-stats">
          <div class="stat-icon">
            <a-icon type="safety-certificate" />
          </div>
          <div class="stat-content">
            <div class="stat-title">质量合格率</div>
            <div class="stat-value">{{ getQualityRate() }}%</div>
            <div class="stat-detail">
              <span class="detail-item good">
                <a-icon type="check" />
                {{ statistics.qualifiedCount || 0 }}合格
              </span>
              <span class="detail-item bad">
                <a-icon type="close" />
                {{ statistics.defectiveCount || 0 }}不良
              </span>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>
    
    <!-- 详细统计图表区域 -->
    <a-row :gutter="16" style="margin-top: 16px;">
      <!-- 任务状态分布饼图 -->
      <a-col :md="8" :sm="24">
        <a-card size="small" title="任务状态分布" :bordered="false">
          <div class="chart-container">
            <div class="status-chart">
              <div class="status-item">
                <div class="status-dot pending"></div>
                <span class="status-label">待派单</span>
                <span class="status-count">{{ statistics.pendingCount || 0 }}</span>
              </div>
              <div class="status-item">
                <div class="status-dot assigned"></div>
                <span class="status-label">已派单</span>
                <span class="status-count">{{ statistics.assignedCount || 0 }}</span>
              </div>
              <div class="status-item">
                <div class="status-dot in-progress"></div>
                <span class="status-label">进行中</span>
                <span class="status-count">{{ statistics.inProgressCount || 0 }}</span>
              </div>
              <div class="status-item">
                <div class="status-dot completed"></div>
                <span class="status-label">已完成</span>
                <span class="status-count">{{ statistics.completedCount || 0 }}</span>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <!-- 优先级分布 -->
      <a-col :md="8" :sm="24">
        <a-card size="small" title="优先级分布" :bordered="false">
          <div class="chart-container">
            <div class="priority-chart">
              <div class="priority-item">
                <div class="priority-bar urgent" :style="{width: getPriorityPercent('URGENT') + '%'}"></div>
                <span class="priority-label">紧急</span>
                <span class="priority-count">{{ statistics.urgentCount || 0 }}</span>
              </div>
              <div class="priority-item">
                <div class="priority-bar high" :style="{width: getPriorityPercent('HIGH') + '%'}"></div>
                <span class="priority-label">高</span>
                <span class="priority-count">{{ statistics.highCount || 0 }}</span>
              </div>
              <div class="priority-item">
                <div class="priority-bar normal" :style="{width: getPriorityPercent('NORMAL') + '%'}"></div>
                <span class="priority-label">普通</span>
                <span class="priority-count">{{ statistics.normalCount || 0 }}</span>
              </div>
              <div class="priority-item">
                <div class="priority-bar low" :style="{width: getPriorityPercent('LOW') + '%'}"></div>
                <span class="priority-label">低</span>
                <span class="priority-count">{{ statistics.lowCount || 0 }}</span>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
      
      <!-- 今日工作概况 -->
      <a-col :md="8" :sm="24">
        <a-card size="small" title="今日工作概况" :bordered="false">
          <div class="chart-container">
            <div class="today-overview">
              <div class="overview-item">
                <a-icon type="plus-circle" class="overview-icon new" />
                <div class="overview-content">
                  <div class="overview-value">{{ statistics.todayNewTasks || 0 }}</div>
                  <div class="overview-label">新增任务</div>
                </div>
              </div>
              <div class="overview-item">
                <a-icon type="play-circle" class="overview-icon started" />
                <div class="overview-content">
                  <div class="overview-value">{{ statistics.todayStartedTasks || 0 }}</div>
                  <div class="overview-label">开始任务</div>
                </div>
              </div>
              <div class="overview-item">
                <a-icon type="check-circle" class="overview-icon completed" />
                <div class="overview-content">
                  <div class="overview-value">{{ statistics.todayCompletedTasks || 0 }}</div>
                  <div class="overview-label">完成任务</div>
                </div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 高级图表区域 -->
    <a-row :gutter="16" style="margin-top: 16px;">
      <!-- 生产趋势图 -->
      <a-col :md="12" :sm="24">
        <a-card size="small" title="生产趋势" :bordered="false">
          <div class="chart-container-large">
            <div id="productionTrendChart" class="echarts-chart"></div>
          </div>
        </a-card>
      </a-col>

      <!-- 工人效率排行 -->
      <a-col :md="12" :sm="24">
        <a-card size="small" title="工人效率排行" :bordered="false">
          <div class="chart-container-large">
            <div id="workerEfficiencyChart" class="echarts-chart"></div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 质量分析和成本统计 -->
    <a-row :gutter="16" style="margin-top: 16px;">
      <!-- 质量分析 -->
      <a-col :md="12" :sm="24">
        <a-card size="small" title="质量分析" :bordered="false">
          <div class="chart-container-large">
            <div id="qualityAnalysisChart" class="echarts-chart"></div>
          </div>
        </a-card>
      </a-col>

      <!-- 成本统计 -->
      <a-col :md="12" :sm="24">
        <a-card size="small" title="成本统计" :bordered="false">
          <div class="chart-container-large">
            <div class="cost-statistics">
              <div class="cost-item">
                <div class="cost-label">本月总成本</div>
                <div class="cost-value">¥{{ formatCurrency(statistics.monthTotalCost) }}</div>
                <div class="cost-trend">
                  <span :class="getTrendClass(statistics.costTrend)">
                    <a-icon :type="getTrendIcon(statistics.costTrend)" />
                    {{ Math.abs(statistics.costTrend || 0) }}%
                  </span>
                </div>
              </div>
              <div class="cost-item">
                <div class="cost-label">平均单价工费</div>
                <div class="cost-value">¥{{ formatCurrency(statistics.avgUnitCost) }}</div>
              </div>
              <div class="cost-item">
                <div class="cost-label">工时成本</div>
                <div class="cost-value">¥{{ formatCurrency(statistics.laborCost) }}</div>
              </div>
              <div class="cost-item">
                <div class="cost-label">材料成本</div>
                <div class="cost-value">¥{{ formatCurrency(statistics.materialCost) }}</div>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 实时更新指示器 -->
    <div class="update-indicator">
      <a-icon type="sync" :spin="loading" />
      <span>{{ getUpdateTime() }}</span>
      <a-button size="small" type="link" @click="$emit('refresh')">
        <a-icon type="reload" />
        刷新
      </a-button>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: "ProductionStatistics",
  props: {
    statistics: {
      type: Object,
      default: () => ({})
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      lastUpdateTime: new Date(),
      charts: {
        productionTrend: null,
        workerEfficiency: null,
        qualityAnalysis: null
      }
    }
  },
  watch: {
    statistics: {
      handler() {
        this.lastUpdateTime = new Date();
        this.updateCharts();
      },
      deep: true
    }
  },
  mounted() {
    this.initCharts();
  },
  beforeDestroy() {
    // 销毁图表实例
    Object.values(this.charts).forEach(chart => {
      if (chart) {
        chart.dispose();
      }
    });
  },
  methods: {
    getCompletionRate() {
      const total = this.statistics.totalTasks || 0;
      const completed = this.statistics.completedCount || 0;
      if (total === 0) return 0;
      return Math.round((completed / total) * 100);
    },
    
    getCompletionColor() {
      const rate = this.getCompletionRate();
      if (rate >= 90) return '#52c41a';
      if (rate >= 70) return '#1890ff';
      if (rate >= 50) return '#fa8c16';
      return '#ff4d4f';
    },
    
    getQualityRate() {
      const total = (this.statistics.qualifiedCount || 0) + (this.statistics.defectiveCount || 0);
      const qualified = this.statistics.qualifiedCount || 0;
      if (total === 0) return 0;
      return Math.round((qualified / total) * 100);
    },
    
    getPriorityPercent(priority) {
      const total = this.statistics.totalTasks || 0;
      if (total === 0) return 0;
      
      const counts = {
        'URGENT': this.statistics.urgentCount || 0,
        'HIGH': this.statistics.highCount || 0,
        'NORMAL': this.statistics.normalCount || 0,
        'LOW': this.statistics.lowCount || 0
      };
      
      return Math.round((counts[priority] / total) * 100);
    },
    
    getTrendClass(trend) {
      if (!trend) return 'trend-neutral';
      return trend > 0 ? 'trend-up' : 'trend-down';
    },
    
    getTrendIcon(trend) {
      if (!trend) return 'minus';
      return trend > 0 ? 'arrow-up' : 'arrow-down';
    },
    
    getUpdateTime() {
      return `最后更新: ${dayjs(this.lastUpdateTime).format('HH:mm:ss')}`;
    },

    // 格式化货币
    formatCurrency(value) {
      if (!value) return '0.00';
      return Number(value).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      });
    },

    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.initProductionTrendChart();
        this.initWorkerEfficiencyChart();
        this.initQualityAnalysisChart();
      });
    },

    // 更新图表
    updateCharts() {
      this.$nextTick(() => {
        this.updateProductionTrendChart();
        this.updateWorkerEfficiencyChart();
        this.updateQualityAnalysisChart();
      });
    },

    // 初始化生产趋势图
    initProductionTrendChart() {
      const chartDom = document.getElementById('productionTrendChart');
      if (!chartDom) return;

      // 这里应该使用ECharts，暂时用简单的HTML展示
      chartDom.innerHTML = `
        <div style="text-align: center; line-height: 200px; color: #999;">
          <div>生产趋势图</div>
          <div style="font-size: 12px; margin-top: 8px;">
            本周完成任务: ${this.statistics.weekCompletedTasks || 0}个<br>
            上周完成任务: ${this.statistics.lastWeekCompletedTasks || 0}个
          </div>
        </div>
      `;
    },

    // 初始化工人效率图
    initWorkerEfficiencyChart() {
      const chartDom = document.getElementById('workerEfficiencyChart');
      if (!chartDom) return;

      chartDom.innerHTML = `
        <div style="text-align: center; line-height: 200px; color: #999;">
          <div>工人效率排行</div>
          <div style="font-size: 12px; margin-top: 8px;">
            最高效率: ${this.statistics.maxEfficiency || 0}%<br>
            平均效率: ${this.statistics.avgEfficiency || 0}%
          </div>
        </div>
      `;
    },

    // 初始化质量分析图
    initQualityAnalysisChart() {
      const chartDom = document.getElementById('qualityAnalysisChart');
      if (!chartDom) return;

      chartDom.innerHTML = `
        <div style="text-align: center; line-height: 200px; color: #999;">
          <div>质量分析</div>
          <div style="font-size: 12px; margin-top: 8px;">
            A级品率: ${this.getQualityRate()}%<br>
            平均评分: ${this.statistics.avgQualityScore || 0}分
          </div>
        </div>
      `;
    },

    // 更新生产趋势图
    updateProductionTrendChart() {
      this.initProductionTrendChart();
    },

    // 更新工人效率图
    updateWorkerEfficiencyChart() {
      this.initWorkerEfficiencyChart();
    },

    // 更新质量分析图
    updateQualityAnalysisChart() {
      this.initQualityAnalysisChart();
    }
  }
}
</script>

<style scoped>
.production-statistics {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.task-stats .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.completion-stats .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.efficiency-stats .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.quality-stats .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #262626;
  line-height: 1;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
}

.trend-up {
  color: #52c41a;
}

.trend-down {
  color: #ff4d4f;
}

.trend-neutral {
  color: #8c8c8c;
}

.trend-label {
  color: #8c8c8c;
  margin-left: 4px;
}

.stat-progress {
  margin-top: 8px;
}

.stat-detail {
  display: flex;
  gap: 12px;
}

.detail-item {
  font-size: 12px;
  display: flex;
  align-items: center;
}

.detail-item .anticon {
  margin-right: 4px;
}

.detail-item.good {
  color: #52c41a;
}

.detail-item.bad {
  color: #ff4d4f;
}

.chart-container {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.status-chart {
  width: 100%;
}

.status-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 8px;
}

.status-dot.pending {
  background: #fa8c16;
}

.status-dot.in-progress {
  background: #1890ff;
}

.status-dot.assigned {
  background: #722ed1;
}

.status-dot.completed {
  background: #52c41a;
}

.status-label {
  flex: 1;
  font-size: 12px;
  color: #595959;
}

.status-count {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
}

.priority-chart {
  width: 100%;
}

.priority-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  position: relative;
}

.priority-bar {
  height: 16px;
  border-radius: 8px;
  margin-right: 8px;
  min-width: 20px;
  transition: width 0.3s ease;
}

.priority-bar.urgent {
  background: #ff4d4f;
}

.priority-bar.high {
  background: #fa8c16;
}

.priority-bar.normal {
  background: #1890ff;
}

.priority-bar.low {
  background: #d9d9d9;
}

.priority-label {
  flex: 1;
  font-size: 12px;
  color: #595959;
  margin-left: 8px;
}

.priority-count {
  font-size: 12px;
  font-weight: 600;
  color: #262626;
}

.today-overview {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.overview-item {
  display: flex;
  align-items: center;
}

.overview-icon {
  font-size: 20px;
  margin-right: 12px;
}

.overview-icon.new {
  color: #1890ff;
}

.overview-icon.started {
  color: #fa8c16;
}

.overview-icon.completed {
  color: #52c41a;
}

.overview-content {
  flex: 1;
}

.overview-value {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  line-height: 1;
}

.overview-label {
  font-size: 12px;
  color: #8c8c8c;
}

.update-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
  padding: 8px;
  background: #f6f8fa;
  border-radius: 6px;
  font-size: 12px;
  color: #8c8c8c;
}

.update-indicator .anticon {
  margin-right: 8px;
}

.update-indicator .ant-btn {
  margin-left: 8px;
  padding: 0;
  height: auto;
}

/* 大图表容器 */
.chart-container-large {
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.echarts-chart {
  width: 100%;
  height: 100%;
  background: #fafafa;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

/* 成本统计样式 */
.cost-statistics {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  padding: 16px;
}

.cost-item {
  text-align: center;
  padding: 12px;
  background: #f6f8fa;
  border-radius: 6px;
}

.cost-label {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.cost-value {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.cost-trend {
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cost-statistics {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .chart-container-large {
    height: 150px;
  }

  .stat-card {
    margin-bottom: 12px;
  }
}
</style>
