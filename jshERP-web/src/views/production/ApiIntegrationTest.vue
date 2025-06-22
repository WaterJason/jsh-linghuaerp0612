<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false" class="api-integration-test">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-left">
            <h2 class="page-title">
              <a-icon type="api" />
              API集成测试
            </h2>
            <div class="page-subtitle">测试生产管理系统各模块API接口的连通性和功能</div>
          </div>
          <div class="header-right">
            <a-button @click="testAllApis" type="primary" :loading="testingAll">
              <a-icon type="thunderbolt" />
              全面测试
            </a-button>
          </div>
        </div>

        <!-- 测试结果概览 -->
        <div class="test-overview">
          <a-row :gutter="16">
            <a-col :span="6">
              <div class="test-stat-card success">
                <div class="stat-icon">
                  <a-icon type="check-circle" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">成功</div>
                  <div class="stat-value">{{ testResults.success }}</div>
                </div>
              </div>
            </a-col>
            <a-col :span="6">
              <div class="test-stat-card error">
                <div class="stat-icon">
                  <a-icon type="close-circle" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">失败</div>
                  <div class="stat-value">{{ testResults.error }}</div>
                </div>
              </div>
            </a-col>
            <a-col :span="6">
              <div class="test-stat-card warning">
                <div class="stat-icon">
                  <a-icon type="exclamation-circle" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">警告</div>
                  <div class="stat-value">{{ testResults.warning }}</div>
                </div>
              </div>
            </a-col>
            <a-col :span="6">
              <div class="test-stat-card info">
                <div class="stat-icon">
                  <a-icon type="info-circle" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">总计</div>
                  <div class="stat-value">{{ testResults.total }}</div>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>

        <!-- API测试模块 -->
        <div class="api-test-modules">
          <a-tabs defaultActiveKey="cloisonne" @change="onTabChange">
            <!-- 掐丝点蓝制作API测试 -->
            <a-tab-pane key="cloisonne" tab="掐丝点蓝制作">
              <div class="test-module">
                <h4>掐丝点蓝制作API测试</h4>
                <div class="api-test-list">
                  <div class="api-test-item" v-for="api in cloisonneApis" :key="api.name">
                    <div class="api-info">
                      <span class="api-method" :class="api.method.toLowerCase()">{{ api.method }}</span>
                      <span class="api-path">{{ api.path }}</span>
                      <span class="api-desc">{{ api.description }}</span>
                    </div>
                    <div class="api-actions">
                      <a-button 
                        size="small" 
                        @click="testSingleApi('cloisonne', api)"
                        :loading="api.testing">
                        测试
                      </a-button>
                      <a-tag 
                        :color="getStatusColor(api.status)" 
                        v-if="api.status">
                        {{ getStatusText(api.status) }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 配饰制作API测试 -->
            <a-tab-pane key="accessory" tab="配饰制作">
              <div class="test-module">
                <h4>配饰制作API测试</h4>
                <div class="api-test-list">
                  <div class="api-test-item" v-for="api in accessoryApis" :key="api.name">
                    <div class="api-info">
                      <span class="api-method" :class="api.method.toLowerCase()">{{ api.method }}</span>
                      <span class="api-path">{{ api.path }}</span>
                      <span class="api-desc">{{ api.description }}</span>
                    </div>
                    <div class="api-actions">
                      <a-button 
                        size="small" 
                        @click="testSingleApi('accessory', api)"
                        :loading="api.testing">
                        测试
                      </a-button>
                      <a-tag 
                        :color="getStatusColor(api.status)" 
                        v-if="api.status">
                        {{ getStatusText(api.status) }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 后工任务API测试 -->
            <a-tab-pane key="postProcessing" tab="后工任务">
              <div class="test-module">
                <h4>后工任务API测试</h4>
                <div class="api-test-list">
                  <div class="api-test-item" v-for="api in postProcessingApis" :key="api.name">
                    <div class="api-info">
                      <span class="api-method" :class="api.method.toLowerCase()">{{ api.method }}</span>
                      <span class="api-path">{{ api.path }}</span>
                      <span class="api-desc">{{ api.description }}</span>
                    </div>
                    <div class="api-actions">
                      <a-button 
                        size="small" 
                        @click="testSingleApi('postProcessing', api)"
                        :loading="api.testing">
                        测试
                      </a-button>
                      <a-tag 
                        :color="getStatusColor(api.status)" 
                        v-if="api.status">
                        {{ getStatusText(api.status) }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 物流追踪API测试 -->
            <a-tab-pane key="logistics" tab="物流追踪">
              <div class="test-module">
                <h4>物流追踪API测试</h4>
                <div class="api-test-list">
                  <div class="api-test-item" v-for="api in logisticsApis" :key="api.name">
                    <div class="api-info">
                      <span class="api-method" :class="api.method.toLowerCase()">{{ api.method }}</span>
                      <span class="api-path">{{ api.path }}</span>
                      <span class="api-desc">{{ api.description }}</span>
                    </div>
                    <div class="api-actions">
                      <a-button 
                        size="small" 
                        @click="testSingleApi('logistics', api)"
                        :loading="api.testing">
                        测试
                      </a-button>
                      <a-tag 
                        :color="getStatusColor(api.status)" 
                        v-if="api.status">
                        {{ getStatusText(api.status) }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>

            <!-- 质检管理API测试 -->
            <a-tab-pane key="quality" tab="质检管理">
              <div class="test-module">
                <h4>质检管理API测试</h4>
                <div class="api-test-list">
                  <div class="api-test-item" v-for="api in qualityApis" :key="api.name">
                    <div class="api-info">
                      <span class="api-method" :class="api.method.toLowerCase()">{{ api.method }}</span>
                      <span class="api-path">{{ api.path }}</span>
                      <span class="api-desc">{{ api.description }}</span>
                    </div>
                    <div class="api-actions">
                      <a-button 
                        size="small" 
                        @click="testSingleApi('quality', api)"
                        :loading="api.testing">
                        测试
                      </a-button>
                      <a-tag 
                        :color="getStatusColor(api.status)" 
                        v-if="api.status">
                        {{ getStatusText(api.status) }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </div>
            </a-tab-pane>
          </a-tabs>
        </div>

        <!-- 测试日志 -->
        <div class="test-logs">
          <h3 class="section-title">
            <a-icon type="file-text" />
            测试日志
            <a-button size="small" @click="clearLogs" style="margin-left: 16px;">
              <a-icon type="delete" />清空日志
            </a-button>
          </h3>
          
          <div class="log-container">
            <div 
              v-for="(log, index) in testLogs" 
              :key="index" 
              class="log-item"
              :class="log.level">
              <span class="log-time">{{ formatTime(log.time) }}</span>
              <span class="log-level">{{ log.level.toUpperCase() }}</span>
              <span class="log-message">{{ log.message }}</span>
            </div>
            <div v-if="testLogs.length === 0" class="no-logs">
              暂无测试日志
            </div>
          </div>
        </div>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import { getAction, postAction } from '@/api/manage'

export default {
  name: "ApiIntegrationTest",
  data() {
    return {
      cardStyle: {
        marginBottom: '24px'
      },
      
      testingAll: false,
      
      // 测试结果统计
      testResults: {
        success: 0,
        error: 0,
        warning: 0,
        total: 0
      },
      
      // 测试日志
      testLogs: [],
      
      // 掐丝点蓝制作API列表
      cloisonneApis: [
        { name: 'list', method: 'GET', path: '/cloisonne/list', description: '获取制作列表', testing: false, status: null },
        { name: 'statistics', method: 'GET', path: '/cloisonne/statistics', description: '获取制作统计', testing: false, status: null },
        { name: 'add', method: 'POST', path: '/cloisonne/add', description: '新增制作订单', testing: false, status: null },
        { name: 'start', method: 'POST', path: '/cloisonne/start', description: '开始制作', testing: false, status: null },
        { name: 'complete', method: 'POST', path: '/cloisonne/complete', description: '完成制作', testing: false, status: null }
      ],
      
      // 配饰制作API列表
      accessoryApis: [
        { name: 'list', method: 'GET', path: '/accessory/list', description: '获取配饰列表', testing: false, status: null },
        { name: 'statistics', method: 'GET', path: '/accessory/statistics', description: '获取配饰统计', testing: false, status: null },
        { name: 'add', method: 'POST', path: '/accessory/add', description: '新增配饰订单', testing: false, status: null },
        { name: 'start', method: 'POST', path: '/accessory/start', description: '开始制作', testing: false, status: null },
        { name: 'complete', method: 'POST', path: '/accessory/complete', description: '完成制作', testing: false, status: null }
      ],
      
      // 后工任务API列表
      postProcessingApis: [
        { name: 'list', method: 'GET', path: '/postProcessing/list', description: '获取任务列表', testing: false, status: null },
        { name: 'statistics', method: 'GET', path: '/postProcessing/statistics', description: '获取任务统计', testing: false, status: null },
        { name: 'add', method: 'POST', path: '/postProcessing/add', description: '新增任务', testing: false, status: null },
        { name: 'assign', method: 'POST', path: '/postProcessing/assign', description: '分配任务', testing: false, status: null },
        { name: 'start', method: 'POST', path: '/postProcessing/start', description: '开始任务', testing: false, status: null }
      ],
      
      // 物流追踪API列表
      logisticsApis: [
        { name: 'list', method: 'GET', path: '/logistics/tracking/list', description: '获取物流列表', testing: false, status: null },
        { name: 'statistics', method: 'GET', path: '/logistics/tracking/statistics', description: '获取物流统计', testing: false, status: null },
        { name: 'add', method: 'POST', path: '/logistics/tracking/add', description: '新增物流记录', testing: false, status: null },
        { name: 'updateStatus', method: 'POST', path: '/logistics/tracking/updateStatus', description: '更新物流状态', testing: false, status: null },
        { name: 'trace', method: 'GET', path: '/logistics/tracking/trace/TEST123', description: '获取物流轨迹', testing: false, status: null }
      ],
      
      // 质检管理API列表
      qualityApis: [
        { name: 'list', method: 'GET', path: '/production/quality/list', description: '获取质检列表', testing: false, status: null },
        { name: 'statistics', method: 'GET', path: '/production/quality/statistics', description: '获取质检统计', testing: false, status: null },
        { name: 'standards', method: 'GET', path: '/production/quality/standards', description: '获取质检标准', testing: false, status: null },
        { name: 'add', method: 'POST', path: '/production/quality/add', description: '新增质检记录', testing: false, status: null },
        { name: 'start', method: 'POST', path: '/production/quality/start', description: '开始质检', testing: false, status: null }
      ]
    }
  },

  created() {
    this.updateTestResults();
  },

  methods: {
    // 测试单个API
    async testSingleApi(module, api) {
      api.testing = true;
      const startTime = Date.now();

      try {
        let response;

        if (api.method === 'GET') {
          response = await getAction(api.path);
        } else {
          // POST请求需要提供测试数据
          const testData = this.getTestData(module, api.name);
          response = await postAction(api.path, testData);
        }

        const endTime = Date.now();
        const responseTime = endTime - startTime;

        if (response && response.code === 200) {
          api.status = 'success';
          this.addLog('info', `${api.description} 测试成功 (${responseTime}ms)`);
        } else {
          api.status = 'warning';
          this.addLog('warning', `${api.description} 返回异常: ${(response && response.data) || '未知错误'}`);
        }

      } catch (error) {
        api.status = 'error';
        this.addLog('error', `${api.description} 测试失败: ${error.message}`);
      } finally {
        api.testing = false;
        this.updateTestResults();
      }
    },

    // 测试所有API
    async testAllApis() {
      this.testingAll = true;
      this.clearLogs();

      try {
        const allApis = [
          ...this.cloisonneApis,
          ...this.accessoryApis,
          ...this.postProcessingApis,
          ...this.logisticsApis,
          ...this.qualityApis
        ];

        this.addLog('info', `开始测试 ${allApis.length} 个API接口...`);

        for (const api of allApis) {
          await this.testSingleApi(this.getModuleByApi(api), api);
          // 添加小延迟避免请求过于频繁
          await new Promise(resolve => setTimeout(resolve, 100));
        }

        this.addLog('info', '所有API测试完成');

      } catch (error) {
        this.addLog('error', `批量测试失败: ${error.message}`);
      } finally {
        this.testingAll = false;
      }
    },

    // 获取API所属模块
    getModuleByApi(api) {
      if (this.cloisonneApis.includes(api)) return 'cloisonne';
      if (this.accessoryApis.includes(api)) return 'accessory';
      if (this.postProcessingApis.includes(api)) return 'postProcessing';
      if (this.logisticsApis.includes(api)) return 'logistics';
      if (this.qualityApis.includes(api)) return 'quality';
      return 'unknown';
    },

    // 获取测试数据
    getTestData(module, apiName) {
      const testDataMap = {
        cloisonne: {
          add: {
            materialName: '测试掐丝点蓝手镯',
            quantity: 10,
            unitName: '个',
            supplierName: '测试供应商',
            remark: 'API测试数据'
          },
          start: { id: 1 },
          complete: { id: 1 }
        },
        accessory: {
          add: {
            semiProductName: '测试半成品',
            accessoryType: '测试配饰',
            quantity: 5,
            unitName: '套',
            remark: 'API测试数据'
          },
          start: { id: 1 },
          complete: { id: 1 }
        },
        postProcessing: {
          add: {
            productName: '测试产品',
            taskType: '测试任务',
            quantity: 8,
            unitName: '个',
            remark: 'API测试数据'
          },
          assign: { taskId: 1, workerId: 1 },
          start: { id: 1 }
        },
        logistics: {
          add: {
            orderNumber: 'TEST001',
            logisticsCompany: '测试快递',
            recipientName: '测试收件人',
            recipientPhone: '13800138000',
            recipientAddress: '测试地址',
            remark: 'API测试数据'
          },
          updateStatus: { id: 1, status: 'IN_TRANSIT' }
        },
        quality: {
          add: {
            productName: '测试产品',
            inspectionQuantity: 10,
            inspectorName: '测试质检员',
            remark: 'API测试数据'
          },
          start: { id: 1 }
        }
      };

      return (testDataMap[module] && testDataMap[module][apiName]) || {};
    },

    // 更新测试结果统计
    updateTestResults() {
      const allApis = [
        ...this.cloisonneApis,
        ...this.accessoryApis,
        ...this.postProcessingApis,
        ...this.logisticsApis,
        ...this.qualityApis
      ];

      this.testResults.total = allApis.length;
      this.testResults.success = allApis.filter(api => api.status === 'success').length;
      this.testResults.error = allApis.filter(api => api.status === 'error').length;
      this.testResults.warning = allApis.filter(api => api.status === 'warning').length;
    },

    // 添加日志
    addLog(level, message) {
      this.testLogs.unshift({
        time: new Date(),
        level,
        message
      });

      // 保持最多100条日志
      if (this.testLogs.length > 100) {
        this.testLogs = this.testLogs.slice(0, 100);
      }
    },

    // 清空日志
    clearLogs() {
      this.testLogs = [];
    },

    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'success': 'green',
        'error': 'red',
        'warning': 'orange'
      };
      return colorMap[status] || 'default';
    },

    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'success': '成功',
        'error': '失败',
        'warning': '警告'
      };
      return textMap[status] || '未知';
    },

    // 格式化时间
    formatTime(time) {
      const date = new Date(time);
      return date.toLocaleTimeString('zh-CN', {
        hour12: false,
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    },

    // 标签页切换
    onTabChange(activeKey) {
      // 可以在这里添加切换逻辑
    }
  }
}
</script>

<style scoped>
.api-integration-test {
  background: #fff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  display: flex;
  align-items: center;
}

.page-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

.page-subtitle {
  margin-top: 4px;
  color: #8c8c8c;
  font-size: 14px;
}

.header-right {
  flex-shrink: 0;
}

/* 测试概览样式 */
.test-overview {
  margin-bottom: 32px;
}

.test-stat-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
}

.test-stat-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.test-stat-card.success {
  border-color: #52c41a;
  background: linear-gradient(135deg, #f6ffed 0%, #fff 100%);
}

.test-stat-card.error {
  border-color: #ff4d4f;
  background: linear-gradient(135deg, #fff2f0 0%, #fff 100%);
}

.test-stat-card.warning {
  border-color: #faad14;
  background: linear-gradient(135deg, #fffbe6 0%, #fff 100%);
}

.test-stat-card.info {
  border-color: #1890ff;
  background: linear-gradient(135deg, #e6f7ff 0%, #fff 100%);
}

.stat-icon {
  font-size: 32px;
  margin-right: 16px;
}

.test-stat-card.success .stat-icon {
  color: #52c41a;
}

.test-stat-card.error .stat-icon {
  color: #ff4d4f;
}

.test-stat-card.warning .stat-icon {
  color: #faad14;
}

.test-stat-card.info .stat-icon {
  color: #1890ff;
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
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

/* API测试模块样式 */
.api-test-modules {
  margin-bottom: 32px;
}

.test-module h4 {
  color: #262626;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.api-test-list {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
}

.api-test-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.api-test-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.api-test-item:last-child {
  margin-bottom: 0;
}

.api-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.api-method {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  margin-right: 12px;
  min-width: 50px;
  text-align: center;
}

.api-method.get {
  background: #52c41a;
}

.api-method.post {
  background: #1890ff;
}

.api-method.put {
  background: #faad14;
}

.api-method.delete {
  background: #ff4d4f;
}

.api-path {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  color: #262626;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 3px;
  margin-right: 12px;
  min-width: 200px;
}

.api-desc {
  color: #8c8c8c;
  font-size: 14px;
}

.api-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 测试日志样式 */
.test-logs {
  margin-bottom: 24px;
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

.log-container {
  background: #f5f5f5;
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  max-height: 300px;
  overflow-y: auto;
}

.log-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 4px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
}

.log-item:last-child {
  margin-bottom: 0;
}

.log-item.info {
  border-left: 4px solid #1890ff;
}

.log-item.warning {
  border-left: 4px solid #faad14;
}

.log-item.error {
  border-left: 4px solid #ff4d4f;
}

.log-time {
  color: #8c8c8c;
  margin-right: 12px;
  min-width: 60px;
}

.log-level {
  font-weight: 600;
  margin-right: 12px;
  min-width: 50px;
}

.log-item.info .log-level {
  color: #1890ff;
}

.log-item.warning .log-level {
  color: #faad14;
}

.log-item.error .log-level {
  color: #ff4d4f;
}

.log-message {
  color: #262626;
  flex: 1;
}

.no-logs {
  text-align: center;
  color: #8c8c8c;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-right {
    margin-top: 16px;
    width: 100%;
  }

  .api-test-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .api-actions {
    margin-top: 8px;
    width: 100%;
    justify-content: flex-end;
  }

  .api-info {
    flex-direction: column;
    align-items: flex-start;
    width: 100%;
  }

  .api-method, .api-path, .api-desc {
    margin-bottom: 4px;
  }
}
</style>
