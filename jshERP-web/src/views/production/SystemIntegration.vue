<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false" class="system-integration">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-left">
            <h2 class="page-title">
              <a-icon type="api" />
              系统集成管理
            </h2>
            <div class="page-subtitle">生产管理系统与jshERP各模块的集成状态和测试</div>
          </div>
          <div class="header-right">
            <a-button-group>
              <a-button @click="refreshStatus" :loading="loading">
                <a-icon type="reload" />刷新状态
              </a-button>
              <a-button @click="testAllIntegration" type="primary">
                <a-icon type="thunderbolt" />全面测试
              </a-button>
            </a-button-group>
          </div>
        </div>

        <!-- 集成状态概览 -->
        <div class="status-overview">
          <h3 class="section-title">
            <a-icon type="dashboard" />
            集成状态概览
          </h3>
          
          <a-row :gutter="24">
            <a-col :span="6">
              <div class="status-card" :class="getStatusClass('material')">
                <div class="status-icon">
                  <a-icon type="shopping" />
                </div>
                <div class="status-content">
                  <div class="status-title">商品管理</div>
                  <div class="status-value">{{ getStatusText('material') }}</div>
                  <div class="status-time">{{ formatTime(integrationStatus.lastUpdate) }}</div>
                </div>
                <div class="status-action">
                  <a-button size="small" @click="testSingleIntegration('material')">
                    <a-icon type="play-circle" />测试
                  </a-button>
                </div>
              </div>
            </a-col>
            
            <a-col :span="6">
              <div class="status-card" :class="getStatusClass('inventory')">
                <div class="status-icon">
                  <a-icon type="inbox" />
                </div>
                <div class="status-content">
                  <div class="status-title">库存管理</div>
                  <div class="status-value">{{ getStatusText('inventory') }}</div>
                  <div class="status-time">{{ formatTime(integrationStatus.lastUpdate) }}</div>
                </div>
                <div class="status-action">
                  <a-button size="small" @click="testSingleIntegration('inventory')">
                    <a-icon type="play-circle" />测试
                  </a-button>
                </div>
              </div>
            </a-col>
            
            <a-col :span="6">
              <div class="status-card" :class="getStatusClass('purchase')">
                <div class="status-icon">
                  <a-icon type="shopping-cart" />
                </div>
                <div class="status-content">
                  <div class="status-title">采购管理</div>
                  <div class="status-value">{{ getStatusText('purchase') }}</div>
                  <div class="status-time">{{ formatTime(integrationStatus.lastUpdate) }}</div>
                </div>
                <div class="status-action">
                  <a-button size="small" @click="testSingleIntegration('purchase')">
                    <a-icon type="play-circle" />测试
                  </a-button>
                </div>
              </div>
            </a-col>
            
            <a-col :span="6">
              <div class="status-card" :class="getStatusClass('finance')">
                <div class="status-icon">
                  <a-icon type="account-book" />
                </div>
                <div class="status-content">
                  <div class="status-title">财务管理</div>
                  <div class="status-value">{{ getStatusText('finance') }}</div>
                  <div class="status-time">{{ formatTime(integrationStatus.lastUpdate) }}</div>
                </div>
                <div class="status-action">
                  <a-button size="small" @click="testSingleIntegration('finance')">
                    <a-icon type="play-circle" />测试
                  </a-button>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>

        <!-- 集成功能测试 -->
        <div class="integration-test">
          <h3 class="section-title">
            <a-icon type="experiment" />
            集成功能测试
          </h3>
          
          <a-tabs defaultActiveKey="material" @change="onTabChange">
            <a-tab-pane key="material" tab="商品管理集成">
              <div class="test-section">
                <h4>BOM信息获取测试</h4>
                <a-form layout="inline">
                  <a-form-item label="商品ID">
                    <a-input-number 
                      v-model="testParams.materialId" 
                      placeholder="请输入商品ID" 
                      style="width: 200px;" />
                  </a-form-item>
                  <a-form-item>
                    <a-button @click="testGetBOM" :loading="testLoading.bom">
                      <a-icon type="search" />获取BOM
                    </a-button>
                  </a-form-item>
                </a-form>
                
                <div v-if="testResults.bom" class="test-result">
                  <h5>测试结果：</h5>
                  <pre>{{ JSON.stringify(testResults.bom, null, 2) }}</pre>
                </div>
              </div>
            </a-tab-pane>
            
            <a-tab-pane key="inventory" tab="库存管理集成">
              <div class="test-section">
                <h4>库存检查测试</h4>
                <a-form layout="inline">
                  <a-form-item label="商品ID">
                    <a-input-number 
                      v-model="testParams.checkMaterialId" 
                      placeholder="请输入商品ID" 
                      style="width: 150px;" />
                  </a-form-item>
                  <a-form-item label="数量">
                    <a-input-number 
                      v-model="testParams.checkQuantity" 
                      placeholder="检查数量" 
                      style="width: 120px;" />
                  </a-form-item>
                  <a-form-item label="仓库ID">
                    <a-input-number 
                      v-model="testParams.depotId" 
                      placeholder="仓库ID" 
                      style="width: 120px;" />
                  </a-form-item>
                  <a-form-item>
                    <a-button @click="testCheckStock" :loading="testLoading.stock">
                      <a-icon type="check" />检查库存
                    </a-button>
                  </a-form-item>
                </a-form>
                
                <div v-if="testResults.stock" class="test-result">
                  <h5>测试结果：</h5>
                  <pre>{{ JSON.stringify(testResults.stock, null, 2) }}</pre>
                </div>
              </div>
            </a-tab-pane>
            
            <a-tab-pane key="purchase" tab="采购管理集成">
              <div class="test-section">
                <h4>推荐供应商测试</h4>
                <a-form layout="inline">
                  <a-form-item label="商品ID">
                    <a-input-number 
                      v-model="testParams.supplierMaterialId" 
                      placeholder="请输入商品ID" 
                      style="width: 200px;" />
                  </a-form-item>
                  <a-form-item>
                    <a-button @click="testGetSuppliers" :loading="testLoading.suppliers">
                      <a-icon type="team" />获取供应商
                    </a-button>
                  </a-form-item>
                </a-form>
                
                <div v-if="testResults.suppliers" class="test-result">
                  <h5>测试结果：</h5>
                  <pre>{{ JSON.stringify(testResults.suppliers, null, 2) }}</pre>
                </div>
              </div>
            </a-tab-pane>
            
            <a-tab-pane key="finance" tab="财务管理集成">
              <div class="test-section">
                <h4>成本计算测试</h4>
                <a-form layout="inline">
                  <a-form-item label="任务ID">
                    <a-input-number 
                      v-model="testParams.taskId" 
                      placeholder="请输入任务ID" 
                      style="width: 200px;" />
                  </a-form-item>
                  <a-form-item>
                    <a-button @click="testCalculateCost" :loading="testLoading.cost">
                      <a-icon type="calculator" />计算成本
                    </a-button>
                  </a-form-item>
                </a-form>
                
                <div v-if="testResults.cost" class="test-result">
                  <h5>测试结果：</h5>
                  <pre>{{ JSON.stringify(testResults.cost, null, 2) }}</pre>
                </div>
              </div>
            </a-tab-pane>
          </a-tabs>
        </div>

        <!-- 测试历史记录 -->
        <div class="test-history">
          <h3 class="section-title">
            <a-icon type="history" />
            测试历史记录
          </h3>
          
          <a-table
            :columns="historyColumns"
            :dataSource="testHistory"
            :pagination="{ pageSize: 10 }"
            size="small"
            bordered>
            
            <template slot="module" slot-scope="text">
              <a-tag :color="getModuleColor(text)">
                {{ getModuleText(text) }}
              </a-tag>
            </template>
            
            <template slot="result" slot-scope="text">
              <a-tag :color="text === 'success' ? 'success' : 'error'">
                {{ text === 'success' ? '成功' : '失败' }}
              </a-tag>
            </template>
            
            <template slot="responseTime" slot-scope="text">
              <span :class="getResponseTimeClass(text)">{{ text }}ms</span>
            </template>
          </a-table>
        </div>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import {
  getIntegrationStatus,
  testIntegration,
  getProductionBOM,
  checkMaterialStock,
  getRecommendedSuppliers,
  calculateProductionCost
} from '@/api/production/integration'
import dayjs from 'dayjs'

export default {
  name: "SystemIntegration",
  data() {
    return {
      loading: false,
      
      // 集成状态
      integrationStatus: {
        materialIntegration: false,
        inventoryIntegration: false,
        purchaseIntegration: false,
        financeIntegration: false,
        version: '',
        lastUpdate: null
      },
      
      // 测试参数
      testParams: {
        materialId: 1,
        checkMaterialId: 1,
        checkQuantity: 10,
        depotId: 1,
        supplierMaterialId: 1,
        taskId: 1
      },
      
      // 测试加载状态
      testLoading: {
        bom: false,
        stock: false,
        suppliers: false,
        cost: false
      },
      
      // 测试结果
      testResults: {
        bom: null,
        stock: null,
        suppliers: null,
        cost: null
      },
      
      // 测试历史记录
      testHistory: [],
      
      // 表格列定义
      historyColumns: [
        { title: '测试时间', dataIndex: 'testTime', width: 150 },
        { title: '测试模块', dataIndex: 'module', scopedSlots: { customRender: 'module' }, width: 100 },
        { title: '测试功能', dataIndex: 'function', width: 150 },
        { title: '测试结果', dataIndex: 'result', scopedSlots: { customRender: 'result' }, width: 80 },
        { title: '响应时间', dataIndex: 'responseTime', scopedSlots: { customRender: 'responseTime' }, width: 100 },
        { title: '错误信息', dataIndex: 'errorMessage', ellipsis: true }
      ]
    }
  },

  created() {
    this.loadIntegrationStatus();
    this.loadTestHistory();
  },

  methods: {
    // 加载集成状态
    async loadIntegrationStatus() {
      try {
        const res = await getIntegrationStatus();
        if (res.code === 200) {
          this.integrationStatus = res.data;
        }
      } catch (error) {
        console.error('加载集成状态失败:', error);
        this.$message.error('加载集成状态失败');
      }
    },

    // 刷新状态
    async refreshStatus() {
      this.loading = true;
      try {
        await this.loadIntegrationStatus();
        this.$message.success('状态刷新成功');
      } catch (error) {
        this.$message.error('状态刷新失败');
      } finally {
        this.loading = false;
      }
    },

    // 测试单个模块集成
    async testSingleIntegration(module) {
      try {
        const startTime = Date.now();
        const res = await testIntegration({ module });
        const endTime = Date.now();
        const responseTime = endTime - startTime;

        if (res.code === 200) {
          this.$message.success(`${this.getModuleText(module)}集成测试成功`);

          // 记录测试历史
          this.addTestHistory({
            module,
            function: '连接测试',
            result: 'success',
            responseTime,
            errorMessage: ''
          });
        } else {
          throw new Error(res.data || '测试失败');
        }
      } catch (error) {
        this.$message.error(`${this.getModuleText(module)}集成测试失败: ${error.message}`);

        // 记录测试历史
        this.addTestHistory({
          module,
          function: '连接测试',
          result: 'error',
          responseTime: 0,
          errorMessage: error.message
        });
      }
    },

    // 全面测试
    async testAllIntegration() {
      this.loading = true;
      const modules = ['material', 'inventory', 'purchase', 'finance'];
      let successCount = 0;

      try {
        for (const module of modules) {
          try {
            await this.testSingleIntegration(module);
            successCount++;
          } catch (error) {
            console.error(`${module} 测试失败:`, error);
          }
        }

        this.$message.success(`全面测试完成，成功 ${successCount}/${modules.length} 个模块`);
      } finally {
        this.loading = false;
      }
    },

    // 测试BOM获取
    async testGetBOM() {
      if (!this.testParams.materialId) {
        this.$message.warning('请输入商品ID');
        return;
      }

      this.testLoading.bom = true;
      try {
        const startTime = Date.now();
        const res = await getProductionBOM(this.testParams.materialId);
        const endTime = Date.now();
        const responseTime = endTime - startTime;

        if (res.code === 200) {
          this.testResults.bom = res.data;
          this.$message.success('BOM信息获取成功');

          this.addTestHistory({
            module: 'material',
            function: 'BOM信息获取',
            result: 'success',
            responseTime,
            errorMessage: ''
          });
        } else {
          throw new Error(res.data || 'BOM获取失败');
        }
      } catch (error) {
        this.$message.error('BOM信息获取失败: ' + error.message);

        this.addTestHistory({
          module: 'material',
          function: 'BOM信息获取',
          result: 'error',
          responseTime: 0,
          errorMessage: error.message
        });
      } finally {
        this.testLoading.bom = false;
      }
    },

    // 测试库存检查
    async testCheckStock() {
      if (!this.testParams.checkMaterialId || !this.testParams.checkQuantity) {
        this.$message.warning('请输入完整的检查参数');
        return;
      }

      this.testLoading.stock = true;
      try {
        const startTime = Date.now();
        const res = await checkMaterialStock({
          materialList: [{
            materialId: this.testParams.checkMaterialId,
            quantity: this.testParams.checkQuantity
          }],
          depotId: this.testParams.depotId
        });
        const endTime = Date.now();
        const responseTime = endTime - startTime;

        if (res.code === 200) {
          this.testResults.stock = res.data;
          this.$message.success('库存检查成功');

          this.addTestHistory({
            module: 'inventory',
            function: '库存检查',
            result: 'success',
            responseTime,
            errorMessage: ''
          });
        } else {
          throw new Error(res.data || '库存检查失败');
        }
      } catch (error) {
        this.$message.error('库存检查失败: ' + error.message);

        this.addTestHistory({
          module: 'inventory',
          function: '库存检查',
          result: 'error',
          responseTime: 0,
          errorMessage: error.message
        });
      } finally {
        this.testLoading.stock = false;
      }
    },

    // 测试获取供应商
    async testGetSuppliers() {
      if (!this.testParams.supplierMaterialId) {
        this.$message.warning('请输入商品ID');
        return;
      }

      this.testLoading.suppliers = true;
      try {
        const startTime = Date.now();
        const res = await getRecommendedSuppliers(this.testParams.supplierMaterialId);
        const endTime = Date.now();
        const responseTime = endTime - startTime;

        if (res.code === 200) {
          this.testResults.suppliers = res.data;
          this.$message.success('推荐供应商获取成功');

          this.addTestHistory({
            module: 'purchase',
            function: '推荐供应商获取',
            result: 'success',
            responseTime,
            errorMessage: ''
          });
        } else {
          throw new Error(res.data || '供应商获取失败');
        }
      } catch (error) {
        this.$message.error('推荐供应商获取失败: ' + error.message);

        this.addTestHistory({
          module: 'purchase',
          function: '推荐供应商获取',
          result: 'error',
          responseTime: 0,
          errorMessage: error.message
        });
      } finally {
        this.testLoading.suppliers = false;
      }
    },

    // 测试成本计算
    async testCalculateCost() {
      if (!this.testParams.taskId) {
        this.$message.warning('请输入任务ID');
        return;
      }

      this.testLoading.cost = true;
      try {
        const startTime = Date.now();
        const res = await calculateProductionCost(this.testParams.taskId);
        const endTime = Date.now();
        const responseTime = endTime - startTime;

        if (res.code === 200) {
          this.testResults.cost = res.data;
          this.$message.success('成本计算成功');

          this.addTestHistory({
            module: 'finance',
            function: '成本计算',
            result: 'success',
            responseTime,
            errorMessage: ''
          });
        } else {
          throw new Error(res.data || '成本计算失败');
        }
      } catch (error) {
        this.$message.error('成本计算失败: ' + error.message);

        this.addTestHistory({
          module: 'finance',
          function: '成本计算',
          result: 'error',
          responseTime: 0,
          errorMessage: error.message
        });
      } finally {
        this.testLoading.cost = false;
      }
    },

    // 添加测试历史记录
    addTestHistory(record) {
      const historyRecord = {
        ...record,
        testTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
        key: Date.now()
      };

      this.testHistory.unshift(historyRecord);

      // 保持最多50条记录
      if (this.testHistory.length > 50) {
        this.testHistory = this.testHistory.slice(0, 50);
      }

      // 保存到本地存储
      this.saveTestHistory();
    },

    // 加载测试历史
    loadTestHistory() {
      try {
        const history = localStorage.getItem('production_integration_test_history');
        if (history) {
          this.testHistory = JSON.parse(history);
        }
      } catch (error) {
        console.error('加载测试历史失败:', error);
      }
    },

    // 保存测试历史
    saveTestHistory() {
      try {
        localStorage.setItem('production_integration_test_history', JSON.stringify(this.testHistory));
      } catch (error) {
        console.error('保存测试历史失败:', error);
      }
    },

    // 标签页切换
    onTabChange(activeKey) {
      // 清空之前的测试结果
      this.testResults = {
        bom: null,
        stock: null,
        suppliers: null,
        cost: null
      };
    },

    // 获取状态样式类
    getStatusClass(module) {
      const status = this.integrationStatus[module + 'Integration'];
      return status ? 'status-success' : 'status-error';
    },

    // 获取状态文本
    getStatusText(module) {
      const status = this.integrationStatus[module + 'Integration'];
      return status ? '连接正常' : '连接异常';
    },

    // 获取模块文本
    getModuleText(module) {
      const moduleMap = {
        'material': '商品管理',
        'inventory': '库存管理',
        'purchase': '采购管理',
        'finance': '财务管理'
      };
      return moduleMap[module] || module;
    },

    // 获取模块颜色
    getModuleColor(module) {
      const colorMap = {
        'material': 'blue',
        'inventory': 'green',
        'purchase': 'orange',
        'finance': 'purple'
      };
      return colorMap[module] || 'default';
    },

    // 获取响应时间样式类
    getResponseTimeClass(time) {
      if (time <= 100) return 'response-fast';
      if (time <= 500) return 'response-normal';
      return 'response-slow';
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return '未知';
      return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
    }
  }
}
</script>

<style scoped>
.system-integration {
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

.status-overview, .integration-test, .test-history {
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

/* 状态卡片样式 */
.status-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  position: relative;
}

.status-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  transform: translateY(-2px);
}

.status-card.status-success {
  border-color: #52c41a;
  background: linear-gradient(135deg, #f6ffed 0%, #fff 100%);
}

.status-card.status-error {
  border-color: #ff4d4f;
  background: linear-gradient(135deg, #fff2f0 0%, #fff 100%);
}

.status-icon {
  font-size: 32px;
  margin-right: 16px;
  color: #1890ff;
}

.status-success .status-icon {
  color: #52c41a;
}

.status-error .status-icon {
  color: #ff4d4f;
}

.status-content {
  flex: 1;
}

.status-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.status-value {
  font-size: 14px;
  color: #595959;
  margin-bottom: 4px;
}

.status-time {
  font-size: 12px;
  color: #8c8c8c;
}

.status-action {
  position: absolute;
  top: 12px;
  right: 12px;
}

/* 测试区域样式 */
.test-section {
  background: #fafafa;
  border-radius: 6px;
  padding: 20px;
  margin-bottom: 16px;
}

.test-section h4 {
  color: #262626;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
}

.test-result {
  margin-top: 16px;
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  padding: 16px;
}

.test-result h5 {
  color: #262626;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
}

.test-result pre {
  background: #f5f5f5;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 12px;
  font-size: 12px;
  color: #262626;
  max-height: 300px;
  overflow-y: auto;
}

/* 响应时间样式 */
.response-fast {
  color: #52c41a;
  font-weight: 600;
}

.response-normal {
  color: #1890ff;
  font-weight: 600;
}

.response-slow {
  color: #ff4d4f;
  font-weight: 600;
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

  .header-right .ant-btn-group {
    width: 100%;
  }

  .header-right .ant-btn {
    flex: 1;
  }

  .status-card {
    margin-bottom: 16px;
  }
}
</style>
