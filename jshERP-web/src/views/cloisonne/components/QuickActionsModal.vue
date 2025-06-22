<template>
  <a-modal
    title="快速操作"
    :visible="visible"
    :width="600"
    :footer="null"
    @cancel="handleCancel"
  >
    <div class="quick-actions-container">
      <a-row :gutter="[16, 16]">
        <!-- 销售相关 -->
        <a-col :span="12">
          <a-card title="销售管理" size="small" class="action-card">
            <div class="action-list">
              <a-button
                type="primary"
                block
                @click="handleAction('newSale')"
                class="action-btn"
              >
                <a-icon type="plus" />
                新增销售
              </a-button>
              <a-button
                block
                @click="handleAction('saleReport')"
                class="action-btn"
              >
                <a-icon type="bar-chart" />
                销售报表
              </a-button>
              <a-button
                block
                @click="handleAction('refund')"
                class="action-btn"
              >
                <a-icon type="rollback" />
                退款处理
              </a-button>
            </div>
          </a-card>
        </a-col>
        
        <!-- 库存相关 -->
        <a-col :span="12">
          <a-card title="库存管理" size="small" class="action-card">
            <div class="action-list">
              <a-button
                type="primary"
                block
                @click="handleAction('stockIn')"
                class="action-btn"
              >
                <a-icon type="plus-circle" />
                入库登记
              </a-button>
              <a-button
                block
                @click="handleAction('stockOut')"
                class="action-btn"
              >
                <a-icon type="minus-circle" />
                出库登记
              </a-button>
              <a-button
                block
                @click="handleAction('stockCheck')"
                class="action-btn"
              >
                <a-icon type="audit" />
                库存盘点
              </a-button>
            </div>
          </a-card>
        </a-col>
        
        <!-- 生产相关 -->
        <a-col :span="12">
          <a-card title="生产管理" size="small" class="action-card">
            <div class="action-list">
              <a-button
                type="primary"
                block
                @click="handleAction('newProduction')"
                class="action-btn"
              >
                <a-icon type="tool" />
                新建生产单
              </a-button>
              <a-button
                block
                @click="handleAction('productionProgress')"
                class="action-btn"
              >
                <a-icon type="clock-circle" />
                生产进度
              </a-button>
              <a-button
                block
                @click="handleAction('qualityCheck')"
                class="action-btn"
              >
                <a-icon type="safety-certificate" />
                质量检查
              </a-button>
            </div>
          </a-card>
        </a-col>
        
        <!-- 员工相关 -->
        <a-col :span="12">
          <a-card title="员工管理" size="small" class="action-card">
            <div class="action-list">
              <a-button
                type="primary"
                block
                @click="handleAction('attendance')"
                class="action-btn"
              >
                <a-icon type="user-add" />
                考勤打卡
              </a-button>
              <a-button
                block
                @click="handleAction('schedule')"
                class="action-btn"
              >
                <a-icon type="calendar" />
                排班管理
              </a-button>
              <a-button
                block
                @click="handleAction('salary')"
                class="action-btn"
              >
                <a-icon type="dollar" />
                薪资管理
              </a-button>
            </div>
          </a-card>
        </a-col>
        
        <!-- 客户相关 -->
        <a-col :span="12">
          <a-card title="客户管理" size="small" class="action-card">
            <div class="action-list">
              <a-button
                type="primary"
                block
                @click="handleAction('newCustomer')"
                class="action-btn"
              >
                <a-icon type="user-add" />
                新增客户
              </a-button>
              <a-button
                block
                @click="handleAction('customerService')"
                class="action-btn"
              >
                <a-icon type="customer-service" />
                客户服务
              </a-button>
              <a-button
                block
                @click="handleAction('memberCard')"
                class="action-btn"
              >
                <a-icon type="credit-card" />
                会员卡管理
              </a-button>
            </div>
          </a-card>
        </a-col>
        
        <!-- 财务相关 -->
        <a-col :span="12">
          <a-card title="财务管理" size="small" class="action-card">
            <div class="action-list">
              <a-button
                type="primary"
                block
                @click="handleAction('dailyReport')"
                class="action-btn"
              >
                <a-icon type="file-text" />
                日报生成
              </a-button>
              <a-button
                block
                @click="handleAction('expense')"
                class="action-btn"
              >
                <a-icon type="money-collect" />
                费用登记
              </a-button>
              <a-button
                block
                @click="handleAction('profit')"
                class="action-btn"
              >
                <a-icon type="rise" />
                利润分析
              </a-button>
            </div>
          </a-card>
        </a-col>
      </a-row>
      
      <!-- 最近操作 -->
      <a-card title="最近操作" size="small" class="recent-actions">
        <a-list
          :data-source="recentActions"
          size="small"
        >
          <a-list-item slot="renderItem" slot-scope="item">
            <a-list-item-meta>
              <a slot="title" @click="handleRecentAction(item)">
                <a-icon :type="item.icon" />
                {{ item.title }}
              </a>
              <span slot="description">{{ item.time }}</span>
            </a-list-item-meta>
          </a-list-item>
        </a-list>
      </a-card>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'QuickActionsModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      recentActions: [
        {
          id: 1,
          title: '新增销售记录',
          icon: 'shopping-cart',
          time: '2分钟前',
          action: 'newSale'
        },
        {
          id: 2,
          title: '库存盘点',
          icon: 'audit',
          time: '10分钟前',
          action: 'stockCheck'
        },
        {
          id: 3,
          title: '员工考勤',
          icon: 'clock-circle',
          time: '30分钟前',
          action: 'attendance'
        },
        {
          id: 4,
          title: '生产进度更新',
          icon: 'tool',
          time: '1小时前',
          action: 'productionProgress'
        }
      ]
    }
  },
  methods: {
    handleAction(actionType) {
      this.$emit('action', actionType)
      this.handleCancel()
      
      // 根据操作类型显示不同的消息
      const actionMessages = {
        newSale: '正在打开销售页面...',
        saleReport: '正在生成销售报表...',
        refund: '正在打开退款页面...',
        stockIn: '正在打开入库页面...',
        stockOut: '正在打开出库页面...',
        stockCheck: '正在打开库存盘点页面...',
        newProduction: '正在创建生产单...',
        productionProgress: '正在查看生产进度...',
        qualityCheck: '正在打开质量检查页面...',
        attendance: '正在打开考勤页面...',
        schedule: '正在打开排班管理页面...',
        salary: '正在打开薪资管理页面...',
        newCustomer: '正在打开客户管理页面...',
        customerService: '正在打开客户服务页面...',
        memberCard: '正在打开会员卡管理页面...',
        dailyReport: '正在生成日报...',
        expense: '正在打开费用登记页面...',
        profit: '正在打开利润分析页面...'
      }
      
      const message = actionMessages[actionType] || '正在执行操作...'
      this.$message.info(message)
    },
    handleRecentAction(item) {
      this.handleAction(item.action)
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.quick-actions-container {
  max-height: 70vh;
  overflow-y: auto;
}

.action-card {
  height: 100%;
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.3s;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.recent-actions {
  margin-top: 16px;
}

.recent-actions .ant-list-item {
  padding: 8px 0;
}

.recent-actions .ant-list-item-meta-title a {
  color: #1890ff;
  text-decoration: none;
}

.recent-actions .ant-list-item-meta-title a:hover {
  color: #40a9ff;
}

.recent-actions .ant-list-item-meta-description {
  color: #999;
  font-size: 12px;
}
</style>
