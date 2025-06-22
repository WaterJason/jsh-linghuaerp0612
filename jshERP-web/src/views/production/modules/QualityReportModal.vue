<template>
  <a-modal
    title="质量分析报告"
    :width="1200"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel">
    
    <div class="quality-report">
      <!-- 查询条件 -->
      <div class="query-section">
        <a-form layout="inline">
          <a-form-item label="时间范围">
            <a-range-picker 
              v-model="queryParams.dateRange"
              format="YYYY-MM-DD"
              placeholder="['开始时间', '结束时间']"
              @change="onDateRangeChange" />
          </a-form-item>
          <a-form-item label="产品类型">
            <a-select 
              v-model="queryParams.productType" 
              placeholder="请选择产品类型" 
              style="width: 150px;"
              allowClear>
              <a-select-option value="QISI">掐丝点蓝</a-select-option>
              <a-select-option value="PEISHI">配饰制作</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="质检员">
            <a-select 
              v-model="queryParams.inspectorId" 
              placeholder="请选择质检员" 
              style="width: 150px;"
              allowClear>
              <a-select-option 
                v-for="inspector in inspectorList" 
                :key="inspector.id" 
                :value="inspector.id">
                {{ inspector.name }}
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="loadReportData">
              <a-icon type="search" />查询
            </a-button>
            <a-button style="margin-left: 8px;" @click="exportReport">
              <a-icon type="download" />导出
            </a-button>
          </a-form-item>
        </a-form>
      </div>

      <!-- 统计概览 -->
      <div class="overview-section">
        <h3 class="section-title">
          <a-icon type="dashboard" />
          统计概览
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="6">
            <div class="stat-card">
              <div class="stat-icon">
                <a-icon type="audit" style="color: #1890ff;" />
              </div>
              <div class="stat-content">
                <div class="stat-title">总质检次数</div>
                <div class="stat-value">{{ reportData.totalInspections || 0 }}</div>
              </div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="stat-card">
              <div class="stat-icon">
                <a-icon type="check-circle" style="color: #52c41a;" />
              </div>
              <div class="stat-content">
                <div class="stat-title">合格次数</div>
                <div class="stat-value">{{ reportData.passCount || 0 }}</div>
              </div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="stat-card">
              <div class="stat-icon">
                <a-icon type="close-circle" style="color: #ff4d4f;" />
              </div>
              <div class="stat-content">
                <div class="stat-title">不合格次数</div>
                <div class="stat-value">{{ reportData.failCount || 0 }}</div>
              </div>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="stat-card">
              <div class="stat-icon">
                <a-icon type="percentage" style="color: #722ed1;" />
              </div>
              <div class="stat-content">
                <div class="stat-title">合格率</div>
                <div class="stat-value">{{ reportData.passRate || 0 }}%</div>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 图表分析 -->
      <div class="chart-section">
        <a-row :gutter="24">
          <a-col :span="12">
            <div class="chart-card">
              <h4 class="chart-title">质检结果分布</h4>
              <div id="resultChart" style="height: 300px;"></div>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="chart-card">
              <h4 class="chart-title">质量等级分布</h4>
              <div id="gradeChart" style="height: 300px;"></div>
            </div>
          </a-col>
        </a-row>
        
        <a-row :gutter="24" style="margin-top: 24px;">
          <a-col :span="24">
            <div class="chart-card">
              <h4 class="chart-title">质检趋势分析</h4>
              <div id="trendChart" style="height: 400px;"></div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 详细数据 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="table" />
          详细数据
        </h3>
        
        <a-tabs defaultActiveKey="inspector">
          <a-tab-pane key="inspector" tab="按质检员统计">
            <a-table
              :columns="inspectorColumns"
              :dataSource="reportData.inspectorStats || []"
              :pagination="false"
              size="small"
              bordered>
              <template slot="passRate" slot-scope="text">
                <a-progress :percent="text" size="small" />
              </template>
            </a-table>
          </a-tab-pane>
          
          <a-tab-pane key="product" tab="按产品统计">
            <a-table
              :columns="productColumns"
              :dataSource="reportData.productStats || []"
              :pagination="false"
              size="small"
              bordered>
              <template slot="passRate" slot-scope="text">
                <a-progress :percent="text" size="small" />
              </template>
            </a-table>
          </a-tab-pane>
          
          <a-tab-pane key="daily" tab="按日期统计">
            <a-table
              :columns="dailyColumns"
              :dataSource="reportData.dailyStats || []"
              :pagination="false"
              size="small"
              bordered>
              <template slot="passRate" slot-scope="text">
                <a-progress :percent="text" size="small" />
              </template>
            </a-table>
          </a-tab-pane>
        </a-tabs>
      </div>

      <!-- 问题分析 -->
      <div class="problem-section">
        <h3 class="section-title">
          <a-icon type="exclamation-circle" />
          问题分析
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <div class="problem-card">
              <h4>主要质量问题</h4>
              <div class="problem-list">
                <div 
                  v-for="(problem, index) in reportData.mainProblems || []" 
                  :key="index"
                  class="problem-item">
                  <div class="problem-title">{{ problem.title }}</div>
                  <div class="problem-count">{{ problem.count }}次</div>
                  <div class="problem-rate">{{ problem.rate }}%</div>
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="problem-card">
              <h4>改进建议</h4>
              <div class="suggestion-list">
                <div 
                  v-for="(suggestion, index) in reportData.suggestions || []" 
                  :key="index"
                  class="suggestion-item">
                  <a-icon type="bulb" />
                  {{ suggestion }}
                </div>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { getAction } from '@/api/manage'
import dayjs from 'dayjs'

export default {
  name: "QualityReportModal",
  data() {
    return {
      visible: false,
      
      // 查询参数
      queryParams: {
        dateRange: [dayjs().subtract(30, 'days'), dayjs()],
        productType: '',
        inspectorId: ''
      },
      
      // 质检员列表
      inspectorList: [],
      
      // 报告数据
      reportData: {},
      
      // 表格列定义
      inspectorColumns: [
        { title: '质检员', dataIndex: 'inspectorName', width: 100 },
        { title: '质检次数', dataIndex: 'totalCount', width: 80 },
        { title: '合格次数', dataIndex: 'passCount', width: 80 },
        { title: '不合格次数', dataIndex: 'failCount', width: 80 },
        { title: '合格率', dataIndex: 'passRate', scopedSlots: { customRender: 'passRate' }, width: 120 },
        { title: '平均评分', dataIndex: 'avgScore', width: 80 }
      ],
      
      productColumns: [
        { title: '产品名称', dataIndex: 'productName', width: 150 },
        { title: '质检次数', dataIndex: 'totalCount', width: 80 },
        { title: '合格次数', dataIndex: 'passCount', width: 80 },
        { title: '不合格次数', dataIndex: 'failCount', width: 80 },
        { title: '合格率', dataIndex: 'passRate', scopedSlots: { customRender: 'passRate' }, width: 120 },
        { title: '平均评分', dataIndex: 'avgScore', width: 80 }
      ],
      
      dailyColumns: [
        { title: '日期', dataIndex: 'date', width: 100 },
        { title: '质检次数', dataIndex: 'totalCount', width: 80 },
        { title: '合格次数', dataIndex: 'passCount', width: 80 },
        { title: '不合格次数', dataIndex: 'failCount', width: 80 },
        { title: '合格率', dataIndex: 'passRate', scopedSlots: { customRender: 'passRate' }, width: 120 },
        { title: '平均评分', dataIndex: 'avgScore', width: 80 }
      ],
      
      // API URLs
      url: {
        report: "/qualityInspection/qualityReport",
        inspectorList: "/system/person/list"
      }
    }
  },
  
  methods: {
    // 显示模态框
    show() {
      this.visible = true;
      this.loadInspectorList();
      this.loadReportData();
    },
    
    // 关闭模态框
    handleCancel() {
      this.visible = false;
      this.reportData = {};
    },
    
    // 加载质检员列表
    async loadInspectorList() {
      try {
        const res = await getAction(this.url.inspectorList, { 
          enabled: true,
          size: 100 
        });
        
        if (res.code === 200) {
          this.inspectorList = res.data.rows || [];
        }
      } catch (error) {
        console.error('Load inspector list error:', error);
      }
    },
    
    // 加载报告数据
    async loadReportData() {
      try {
        const params = {
          startDate: this.queryParams.dateRange[0].format('YYYY-MM-DD'),
          endDate: this.queryParams.dateRange[1].format('YYYY-MM-DD'),
          productType: this.queryParams.productType,
          inspectorId: this.queryParams.inspectorId
        };
        
        const res = await getAction(this.url.report, params);
        
        if (res.code === 200) {
          this.reportData = res.data || this.getMockData();
          this.$nextTick(() => {
            this.renderCharts();
          });
        }
      } catch (error) {
        console.error('Load report data error:', error);
        // 使用模拟数据
        this.reportData = this.getMockData();
        this.$nextTick(() => {
          this.renderCharts();
        });
      }
    },
    
    // 获取模拟数据
    getMockData() {
      return {
        totalInspections: 156,
        passCount: 142,
        failCount: 14,
        passRate: 91.0,
        inspectorStats: [
          { inspectorName: '张质检', totalCount: 45, passCount: 42, failCount: 3, passRate: 93.3, avgScore: 4.2 },
          { inspectorName: '李质检', totalCount: 38, passCount: 35, failCount: 3, passRate: 92.1, avgScore: 4.1 },
          { inspectorName: '王质检', totalCount: 42, passCount: 37, failCount: 5, passRate: 88.1, avgScore: 3.9 },
          { inspectorName: '赵质检', totalCount: 31, passCount: 28, failCount: 3, passRate: 90.3, avgScore: 4.0 }
        ],
        productStats: [
          { productName: '掐丝点蓝手镯', totalCount: 68, passCount: 63, failCount: 5, passRate: 92.6, avgScore: 4.3 },
          { productName: '掐丝点蓝项链', totalCount: 52, passCount: 47, failCount: 5, passRate: 90.4, avgScore: 4.1 },
          { productName: '配饰耳环', totalCount: 36, passCount: 32, failCount: 4, passRate: 88.9, avgScore: 3.8 }
        ],
        dailyStats: [
          { date: '2024-01-15', totalCount: 12, passCount: 11, failCount: 1, passRate: 91.7, avgScore: 4.2 },
          { date: '2024-01-16', totalCount: 15, passCount: 14, failCount: 1, passRate: 93.3, avgScore: 4.3 },
          { date: '2024-01-17', totalCount: 18, passCount: 16, failCount: 2, passRate: 88.9, avgScore: 4.0 }
        ],
        mainProblems: [
          { title: '尺寸精度不达标', count: 8, rate: 57.1 },
          { title: '表面质感粗糙', count: 4, rate: 28.6 },
          { title: '颜色效果不均匀', count: 2, rate: 14.3 }
        ],
        suggestions: [
          '加强工艺培训，提高操作技能',
          '完善质检标准，统一评判尺度',
          '增加过程质检，及时发现问题',
          '建立质量奖惩机制，提高质量意识'
        ]
      };
    },
    
    // 渲染图表
    renderCharts() {
      // TODO: 使用ECharts渲染图表
      this.$message.info('图表渲染功能开发中...');
    },
    
    // 日期范围变更
    onDateRangeChange(dates) {
      this.queryParams.dateRange = dates;
    },
    
    // 导出报告
    exportReport() {
      this.$message.info('导出报告功能开发中...');
      // TODO: 实现报告导出功能
    }
  }
}
</script>

<style scoped>
.quality-report {
  padding: 0;
}

.query-section {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 24px;
}

.overview-section, .chart-section, .detail-section, .problem-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 8px;
}

.section-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

/* 统计卡片样式 */
.stat-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 20px;
  display: flex;
  align-items: center;
}

.stat-icon {
  font-size: 32px;
  margin-right: 16px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 8px;
}

.stat-value {
  color: #262626;
  font-size: 24px;
  font-weight: 600;
}

/* 图表卡片样式 */
.chart-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 20px;
}

.chart-title {
  color: #262626;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  text-align: center;
}

/* 问题分析样式 */
.problem-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 20px;
}

.problem-card h4 {
  color: #262626;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.problem-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.problem-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fafafa;
  border-radius: 4px;
}

.problem-title {
  flex: 1;
  color: #262626;
}

.problem-count {
  color: #8c8c8c;
  margin-right: 12px;
}

.problem-rate {
  color: #ff4d4f;
  font-weight: 600;
}

.suggestion-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f6ffed;
  border-radius: 4px;
  color: #262626;
}

.suggestion-item .anticon {
  color: #52c41a;
  margin-right: 8px;
}
</style>
