<template>
  <div class="statistics-container">
    <div v-if="loading" class="loading-container">
      <a-spin size="large" />
    </div>

    <div v-else-if="staffWorkDays.length === 0" class="empty-container">
      <a-empty description="本月暂无员工值班记录" />
    </div>

    <div v-else class="statistics-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">{{ statisticsTitle }}</h2>
        <p class="page-subtitle">员工值班数据分析与统计</p>
      </div>

      <!-- 统计概览卡片 -->
      <div class="overview-cards">
        <div class="stat-card total-days">
          <div class="stat-icon">
            <a-icon type="calendar" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ totalWorkDays }}</div>
            <div class="stat-label">总值班天数</div>
          </div>
          <div class="stat-trend">
            <a-icon type="arrow-up" style="color: #52c41a;" />
            <span>较上月增长</span>
          </div>
        </div>

        <div class="stat-card total-employees">
          <div class="stat-icon">
            <a-icon type="team" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ staffWorkDays.length }}</div>
            <div class="stat-label">参与员工数</div>
          </div>
          <div class="stat-trend">
            <a-icon type="minus" style="color: #faad14;" />
            <span>与上月持平</span>
          </div>
        </div>

        <div class="stat-card average-days">
          <div class="stat-icon">
            <a-icon type="bar-chart" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ averageWorkDays.toFixed(1) }}</div>
            <div class="stat-label">平均工作天数</div>
          </div>
          <div class="stat-trend">
            <a-icon type="arrow-up" style="color: #52c41a;" />
            <span>效率提升</span>
          </div>
        </div>

        <div class="stat-card max-days">
          <div class="stat-icon">
            <a-icon type="crown" />
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ maxWorkDays }}</div>
            <div class="stat-label">最多工作天数</div>
          </div>
          <div class="stat-trend">
            <a-icon type="star" style="color: #1890ff;" />
            <span>表现优秀</span>
          </div>
        </div>
      </div>

      <!-- 员工排行榜 -->
      <div class="employee-ranking">
        <div class="section-header">
          <h3 class="section-title">员工排行榜</h3>
          <p class="section-subtitle">按工作天数排序</p>
        </div>

        <div class="ranking-list">
          <div
            v-for="(employee, index) in staffWorkDays"
            :key="employee.name"
            class="ranking-item"
            :class="{ 'top-performer': index < 3 }"
          >
            <!-- 排名徽章 -->
            <div class="rank-badge" :class="`rank-${index + 1}`">
              <span v-if="index < 3" class="rank-icon">
                <a-icon v-if="index === 0" type="trophy" />
                <a-icon v-else-if="index === 1" type="medal" />
                <a-icon v-else type="star" />
              </span>
              <span v-else class="rank-number">{{ index + 1 }}</span>
            </div>

            <!-- 员工信息 -->
            <div class="employee-profile">
              <div class="employee-avatar" :style="{ backgroundColor: getAvatarColor(employee.name) }">
                {{ employee.name.charAt(0) }}
              </div>
              <div class="employee-details">
                <h4 class="employee-name">{{ employee.name }}</h4>
                <div class="employee-stats">
                  <span class="work-days">{{ employee.days }}天</span>
                  <span class="shift-summary">{{ formatShiftSummary(employee.shifts) }}</span>
                </div>
              </div>
            </div>

            <!-- 工作分布 -->
            <div class="work-distribution">
              <div class="distribution-header">
                <span class="distribution-title">班次分布</span>
                <span class="total-percentage">100%</span>
              </div>
              <div class="distribution-bars">
                <div
                  v-for="(count, shift) in employee.shifts"
                  :key="shift"
                  class="distribution-bar"
                >
                  <div class="bar-info">
                    <span class="shift-name">{{ shift }}</span>
                    <span class="shift-count">{{ count }}次</span>
                  </div>
                  <div class="bar-container">
                    <div
                      class="bar-fill"
                      :style="{
                        width: `${(count / employee.days) * 100}%`,
                        backgroundColor: getShiftColor(shift)
                      }"
                    ></div>
                  </div>
                  <span class="bar-percentage">{{ Math.round((count / employee.days) * 100) }}%</span>
                </div>
              </div>
            </div>

            <!-- 表现评价 -->
            <div class="performance-badge">
              <a-tag
                :color="getPerformanceColor(employee.days)"
                class="performance-tag"
              >
                {{ getPerformanceText(employee.days) }}
              </a-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 班次分布分析 -->
      <div class="shift-analysis">
        <div class="section-header">
          <h3 class="section-title">班次分布分析</h3>
          <p class="section-subtitle">各班次工作量统计</p>
        </div>

        <div class="analysis-grid">
          <div
            v-for="(count, shift) in shiftDistribution"
            :key="shift"
            class="analysis-card"
          >
            <div class="card-header">
              <div class="shift-icon" :style="{ backgroundColor: getShiftColor(shift) }">
                <a-icon :type="getShiftIcon(shift)" />
              </div>
              <div class="shift-info">
                <h4 class="shift-name">{{ shift }}</h4>
                <p class="shift-description">{{ getShiftDescription(shift) }}</p>
              </div>
            </div>

            <div class="card-content">
              <div class="count-display">
                <span class="count-number">{{ count }}</span>
                <span class="count-unit">次</span>
              </div>
              <div class="percentage-display">
                <span class="percentage-text">{{ getShiftPercentage(count) }}%</span>
                <span class="percentage-label">占比</span>
              </div>
            </div>

            <div class="card-footer">
              <div class="progress-bar">
                <div
                  class="progress-fill"
                  :style="{
                    width: `${(count / maxShiftCount) * 100}%`,
                    backgroundColor: getShiftColor(shift)
                  }"
                ></div>
              </div>
              <div class="comparison-text">
                <span v-if="count === maxShiftCount" class="highest">最高</span>
                <span v-else-if="count === minShiftCount" class="lowest">最低</span>
                <span v-else class="normal">正常</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据洞察 -->
      <div class="insights-section">
        <div class="section-header">
          <h3 class="section-title">数据洞察</h3>
          <p class="section-subtitle">基于当前数据的分析建议</p>
        </div>

        <div class="insights-grid">
          <div class="insight-card workload">
            <div class="insight-icon">
              <a-icon type="pie-chart" />
            </div>
            <div class="insight-content">
              <h4>工作负载分析</h4>
              <p>{{ getWorkloadInsight() }}</p>
            </div>
          </div>

          <div class="insight-card efficiency">
            <div class="insight-icon">
              <a-icon type="rocket" />
            </div>
            <div class="insight-content">
              <h4>效率评估</h4>
              <p>{{ getEfficiencyInsight() }}</p>
            </div>
          </div>

          <div class="insight-card recommendation">
            <div class="insight-icon">
              <a-icon type="bulb" />
            </div>
            <div class="insight-content">
              <h4>优化建议</h4>
              <p>{{ getRecommendation() }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DutyStatisticsView',
  props: {
    schedules: {
      type: Array,
      default: () => []
    },
    currentDate: {
      type: Date,
      required: true
    }
  },
  data() {
    return {
      loading: false,
      monthNames: ['一月', '二月', '三月', '四月', '五月', '六月', 
                   '七月', '八月', '九月', '十月', '十一月', '十二月']
    }
  },
  computed: {
    // 统计标题
    statisticsTitle() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      return `${year}年 ${this.monthNames[month]} 员工值班统计`
    },
    
    // 员工工作天数统计
    staffWorkDays() {
      const year = this.currentDate.getFullYear()
      const month = this.currentDate.getMonth()
      const summary = {}
      
      // 统计每个员工的工作情况
      this.schedules.forEach(schedule => {
        const scheduleDate = new Date(schedule.dutyDate)
        if (scheduleDate.getFullYear() === year && scheduleDate.getMonth() === month) {
          if (!summary[schedule.employeeId]) {
            summary[schedule.employeeId] = {
              name: schedule.employeeName,
              days: 0,
              shifts: {}
            }
          }
          
          summary[schedule.employeeId].days++
          const shift = schedule.shiftType
          summary[schedule.employeeId].shifts[shift] = (summary[schedule.employeeId].shifts[shift] || 0) + 1
        }
      })
      
      // 转换为数组并按工作天数排序
      return Object.values(summary)
        .filter(s => s.days > 0)
        .sort((a, b) => b.days - a.days)
    },
    
    // 总工作天数
    totalWorkDays() {
      return this.staffWorkDays.reduce((total, staff) => total + staff.days, 0)
    },
    
    // 平均工作天数
    averageWorkDays() {
      if (this.staffWorkDays.length === 0) return 0
      return this.totalWorkDays / this.staffWorkDays.length
    },
    
    // 最多工作天数
    maxWorkDays() {
      if (this.staffWorkDays.length === 0) return 0
      return Math.max(...this.staffWorkDays.map(staff => staff.days))
    },
    
    // 班次分布统计
    shiftDistribution() {
      const distribution = {}
      this.staffWorkDays.forEach(staff => {
        Object.entries(staff.shifts).forEach(([shift, count]) => {
          distribution[shift] = (distribution[shift] || 0) + count
        })
      })
      return distribution
    },
    
    // 最大班次数量（用于图表比例）
    maxShiftCount() {
      const counts = Object.values(this.shiftDistribution)
      return counts.length > 0 ? Math.max(...counts) : 1
    },

    // 最小班次数量
    minShiftCount() {
      const counts = Object.values(this.shiftDistribution)
      return counts.length > 0 ? Math.min(...counts) : 0
    }
  },
  methods: {
    // 获取头像颜色
    getAvatarColor(name) {
      const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068']
      const index = name.charCodeAt(0) % colors.length
      return colors[index]
    },
    
    // 格式化班次详情
    formatShiftDetails(shifts) {
      return Object.entries(shifts)
        .map(([shift, count]) => `${shift}:${count}次`)
        .join(', ')
    },

    // 格式化班次摘要
    formatShiftSummary(shifts) {
      const entries = Object.entries(shifts)
      if (entries.length === 1) {
        return `专职${entries[0][0]}`
      } else {
        return `${entries.length}种班次`
      }
    },
    
    // 获取班次颜色
    getShiftColor(shift) {
      const colorMap = {
        '早班': '#fa8c16',
        '晚班': '#52c41a',
        '全天': '#1890ff'
      }
      return colorMap[shift] || '#d9d9d9'
    },

    // 获取班次图标
    getShiftIcon(shift) {
      const iconMap = {
        '早班': 'sun',
        '晚班': 'moon',
        '全天': 'clock-circle'
      }
      return iconMap[shift] || 'schedule'
    },

    // 获取班次描述
    getShiftDescription(shift) {
      const descMap = {
        '早班': '上午时段值班',
        '晚班': '下午时段值班',
        '全天': '全天候值班'
      }
      return descMap[shift] || '值班时段'
    },

    // 获取班次百分比
    getShiftPercentage(count) {
      const total = Object.values(this.shiftDistribution).reduce((sum, c) => sum + c, 0)
      return total > 0 ? Math.round((count / total) * 100) : 0
    },

    // 获取表现颜色
    getPerformanceColor(days) {
      if (days >= this.maxWorkDays * 0.8) return 'red'
      if (days >= this.averageWorkDays) return 'orange'
      return 'green'
    },

    // 获取表现文本
    getPerformanceText(days) {
      if (days >= this.maxWorkDays * 0.8) return '表现优秀'
      if (days >= this.averageWorkDays) return '表现良好'
      return '表现一般'
    },

    // 获取工作负载洞察
    getWorkloadInsight() {
      const total = this.totalWorkDays
      const employees = this.staffWorkDays.length
      if (employees === 0) return '暂无数据分析'

      const avgPerEmployee = total / employees
      if (avgPerEmployee > 20) {
        return '员工工作负载较重，建议增加人手或优化排班'
      } else if (avgPerEmployee < 10) {
        return '员工工作负载较轻，可以考虑承担更多工作'
      } else {
        return '员工工作负载适中，排班安排合理'
      }
    },

    // 获取效率洞察
    getEfficiencyInsight() {
      const shiftTypes = Object.keys(this.shiftDistribution).length
      if (shiftTypes >= 3) {
        return '班次类型丰富，能够灵活应对不同时段需求'
      } else if (shiftTypes === 2) {
        return '班次安排较为合理，覆盖主要时段'
      } else {
        return '班次类型单一，建议增加班次灵活性'
      }
    },

    // 获取优化建议
    getRecommendation() {
      const maxEmployee = this.staffWorkDays[0]
      const minEmployee = this.staffWorkDays[this.staffWorkDays.length - 1]

      if (!maxEmployee || !minEmployee) return '数据不足，无法提供建议'

      const diff = maxEmployee.days - minEmployee.days
      if (diff > 10) {
        return '员工工作天数差异较大，建议平衡各员工工作量'
      } else if (diff > 5) {
        return '员工工作分配基本合理，可适当调整优化'
      } else {
        return '员工工作分配均衡，排班安排优秀'
      }
    }
  }
}
</script>

<style scoped>
.statistics-container {
  background: #f5f5f5;
  min-height: 100vh;
  padding: 16px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.statistics-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 页面头部 */
.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #666;
  margin: 0;
}

/* 统计概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1890ff, #40a9ff);
}

.stat-card.total-days::before {
  background: linear-gradient(90deg, #52c41a, #73d13d);
}

.stat-card.total-employees::before {
  background: linear-gradient(90deg, #1890ff, #40a9ff);
}

.stat-card.average-days::before {
  background: linear-gradient(90deg, #722ed1, #b37feb);
}

.stat-card.max-days::before {
  background: linear-gradient(90deg, #fa541c, #ff7a45);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  margin-bottom: 16px;
}

.total-days .stat-icon {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.total-employees .stat-icon {
  background: linear-gradient(135deg, #1890ff, #40a9ff);
}

.average-days .stat-icon {
  background: linear-gradient(135deg, #722ed1, #b37feb);
}

.max-days .stat-icon {
  background: linear-gradient(135deg, #fa541c, #ff7a45);
}

.stat-content {
  margin-bottom: 12px;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #999;
}

/* 区域标题 */
.section-header {
  margin-bottom: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.section-subtitle {
  font-size: 14px;
  color: #666;
  margin: 0;
}

/* 员工排行榜 */
.employee-ranking {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-bottom: 24px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ranking-item {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.ranking-item:hover {
  background: #f0f9ff;
  transform: translateX(4px);
}

.ranking-item.top-performer {
  background: linear-gradient(135deg, #fff7e6 0%, #fff2e8 100%);
  border-color: #ffa940;
}

.rank-badge {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  flex-shrink: 0;
}

.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffed4e);
  color: #d48806;
}

.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
  color: #8c8c8c;
}

.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #daa520);
  color: #d46b08;
}

.rank-badge:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #f0f0f0;
  color: #666;
}

.rank-icon {
  font-size: 20px;
}

.rank-number {
  font-size: 18px;
}

.employee-profile {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.employee-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.employee-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.employee-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.employee-stats {
  display: flex;
  align-items: center;
  gap: 12px;
}

.work-days {
  font-size: 14px;
  font-weight: 600;
  color: #1890ff;
}

.shift-summary {
  font-size: 12px;
  color: #666;
}

.work-distribution {
  flex: 2;
  max-width: 300px;
}

.distribution-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.distribution-title {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

.total-percentage {
  font-size: 12px;
  color: #999;
}

.distribution-bars {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.distribution-bar {
  display: flex;
  align-items: center;
  gap: 8px;
}

.bar-info {
  width: 60px;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.shift-name {
  font-size: 11px;
  color: #666;
  font-weight: 500;
}

.shift-count {
  font-size: 10px;
  color: #999;
}

.bar-container {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.bar-percentage {
  width: 32px;
  font-size: 10px;
  color: #666;
  text-align: right;
}

.performance-badge {
  flex-shrink: 0;
}

.performance-tag {
  font-size: 12px;
  font-weight: 500;
}

/* 班次分析 */
.shift-analysis {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  margin-bottom: 24px;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.analysis-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.analysis-card:hover {
  background: #f0f9ff;
  border-color: #1890ff;
  transform: translateY(-2px);
}

.analysis-card .card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.shift-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
}

.shift-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.shift-info p {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.analysis-card .card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.count-display {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.count-number {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.count-unit {
  font-size: 12px;
  color: #666;
}

.percentage-display {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
}

.percentage-text {
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
}

.percentage-label {
  font-size: 10px;
  color: #999;
}

.analysis-card .card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #f0f0f0;
  border-radius: 3px;
  overflow: hidden;
  margin-right: 12px;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.comparison-text {
  font-size: 11px;
  font-weight: 500;
}

.comparison-text .highest {
  color: #52c41a;
}

.comparison-text .lowest {
  color: #ff4d4f;
}

.comparison-text .normal {
  color: #666;
}

/* 数据洞察 */
.insights-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.insights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.insight-card {
  background: linear-gradient(135deg, #f6f9fc 0%, #ffffff 100%);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e8f4fd;
  transition: all 0.3s ease;
}

.insight-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.15);
}

.insight-card.workload {
  border-color: #d3f261;
  background: linear-gradient(135deg, #fcffe6 0%, #ffffff 100%);
}

.insight-card.efficiency {
  border-color: #ffadd2;
  background: linear-gradient(135deg, #fff0f6 0%, #ffffff 100%);
}

.insight-card.recommendation {
  border-color: #87e8de;
  background: linear-gradient(135deg, #e6fffb 0%, #ffffff 100%);
}

.insight-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: #fff;
  margin-bottom: 16px;
}

.workload .insight-icon {
  background: linear-gradient(135deg, #52c41a, #73d13d);
}

.efficiency .insight-icon {
  background: linear-gradient(135deg, #eb2f96, #f759ab);
}

.recommendation .insight-icon {
  background: linear-gradient(135deg, #13c2c2, #36cfc9);
}

.insight-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.insight-content p {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .analysis-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .insights-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 8px;
  }

  .overview-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .stat-card {
    padding: 20px;
  }

  .page-title {
    font-size: 24px;
  }

  .ranking-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 16px;
  }

  .employee-profile {
    width: 100%;
  }

  .work-distribution {
    width: 100%;
    max-width: none;
  }

  .analysis-grid {
    grid-template-columns: 1fr;
  }

  .analysis-card {
    padding: 16px;
  }

  .insights-grid {
    gap: 16px;
  }

  .insight-card {
    padding: 16px;
  }
}

@media (max-width: 480px) {
  .ranking-item {
    padding: 12px;
  }

  .rank-badge {
    width: 40px;
    height: 40px;
  }

  .employee-avatar {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }

  .employee-name {
    font-size: 16px;
  }

  .stat-value {
    font-size: 28px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }
}
</style>
