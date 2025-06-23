<template>
  <a-modal
    title="物流追踪详情"
    :width="1000"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel">
    
    <div class="tracking-detail" v-if="trackingData">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="info-circle" />
          基本信息
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">追踪编号：</span>
              <span class="value">{{ trackingData.trackingNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">工单编号：</span>
              <span class="value">{{ trackingData.workOrderNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">物流类型：</span>
              <a-tag :color="getLogisticsTypeColor(trackingData.logisticsType)">
                {{ getLogisticsTypeText(trackingData.logisticsType) }}
              </a-tag>
            </div>
          </a-col>
        </a-row>
        
        <a-row :gutter="24" style="margin-top: 16px;">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">产品名称：</span>
              <span class="value">{{ trackingData.productName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">数量：</span>
              <span class="value">{{ trackingData.quantity }} {{ trackingData.unitName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">当前状态：</span>
              <a-tag :color="getStatusColor(trackingData.status)">
                <a-icon :type="getStatusIcon(trackingData.status)" />
                {{ getStatusText(trackingData.status) }}
              </a-tag>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 物流进度 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="car" />
          物流进度
        </h3>
        
        <div class="progress-timeline">
          <a-steps :current="getCurrentStep()" direction="horizontal" size="small">
            <a-step 
              title="待发货" 
              :description="getStepDescription('PENDING')"
              :icon="getStepIcon('PENDING')" />
            <a-step 
              title="已发货" 
              :description="getStepDescription('SHIPPED')"
              :icon="getStepIcon('SHIPPED')" />
            <a-step 
              title="运输中" 
              :description="getStepDescription('IN_TRANSIT')"
              :icon="getStepIcon('IN_TRANSIT')" />
            <a-step 
              title="已到达" 
              :description="getStepDescription('ARRIVED')"
              :icon="getStepIcon('ARRIVED')" />
            <a-step 
              title="已收货" 
              :description="getStepDescription('RECEIVED')"
              :icon="getStepIcon('RECEIVED')" />
          </a-steps>
        </div>
      </div>

      <!-- 承运信息 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="user" />
          承运信息
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="6">
            <div class="info-item">
              <span class="label">承运人：</span>
              <span class="value">{{ trackingData.carrierName || '未设置' }}</span>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="info-item">
              <span class="label">联系方式：</span>
              <span class="value">{{ trackingData.carrierPhone || '未设置' }}</span>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="info-item">
              <span class="label">运输方式：</span>
              <span class="value">{{ getTransportMethodText(trackingData.transportMethod) }}</span>
            </div>
          </a-col>
          <a-col :span="6">
            <div class="info-item">
              <span class="label">运费：</span>
              <span class="value">¥{{ trackingData.shippingCost || 0 }}</span>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 地址信息 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="environment" />
          地址信息
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <div class="address-card">
              <div class="address-title">
                <a-icon type="export" />
                发货地址
              </div>
              <div class="address-content">
                {{ trackingData.fromAddress || '未设置' }}
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="address-card">
              <div class="address-title">
                <a-icon type="import" />
                收货地址
              </div>
              <div class="address-content">
                {{ trackingData.toAddress || '未设置' }}
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 时间信息 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="clock-circle" />
          时间信息
        </h3>
        
        <a-row :gutter="24">
          <a-col :span="12">
            <div class="time-table">
              <div class="time-row">
                <span class="time-label">计划发货时间：</span>
                <span class="time-value">{{ formatTime(trackingData.planShipTime) }}</span>
              </div>
              <div class="time-row">
                <span class="time-label">实际发货时间：</span>
                <span class="time-value">{{ formatTime(trackingData.shipTime) }}</span>
              </div>
              <div class="time-row">
                <span class="time-label">预计到达时间：</span>
                <span class="time-value">{{ formatTime(trackingData.estimatedArrivalTime) }}</span>
              </div>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="time-table">
              <div class="time-row">
                <span class="time-label">实际到达时间：</span>
                <span class="time-value">{{ formatTime(trackingData.arrivalTime) }}</span>
              </div>
              <div class="time-row">
                <span class="time-label">收货时间：</span>
                <span class="time-value">{{ formatTime(trackingData.receiveTime) }}</span>
              </div>
              <div class="time-row">
                <span class="time-label">创建时间：</span>
                <span class="time-value">{{ formatTime(trackingData.createTime) }}</span>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 追踪记录 -->
      <div class="detail-section">
        <h3 class="section-title">
          <a-icon type="history" />
          追踪记录
        </h3>
        
        <a-timeline>
          <a-timeline-item 
            v-for="(record, index) in trackingRecords" 
            :key="index"
            :color="getTimelineColor(record.status)">
            <div class="timeline-content">
              <div class="timeline-title">
                <a-icon :type="getStatusIcon(record.status)" />
                {{ getStatusText(record.status) }}
              </div>
              <div class="timeline-time">{{ formatTime(record.updateTime) }}</div>
              <div class="timeline-description" v-if="record.description">
                {{ record.description }}
              </div>
            </div>
          </a-timeline-item>
        </a-timeline>
      </div>

      <!-- 备注信息 -->
      <div class="detail-section" v-if="trackingData.remark">
        <h3 class="section-title">
          <a-icon type="file-text" />
          备注信息
        </h3>
        
        <div class="remark-content">
          {{ trackingData.remark }}
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: "LogisticsTrackingDetail",
  data() {
    return {
      visible: false,
      trackingData: null,
      trackingRecords: []
    }
  },
  
  methods: {
    // 显示详情
    show(record) {
      this.trackingData = record;
      this.visible = true;
      this.loadTrackingRecords(record.id);
    },
    
    // 关闭
    handleCancel() {
      this.visible = false;
      this.trackingData = null;
      this.trackingRecords = [];
    },
    
    // 加载追踪记录
    loadTrackingRecords(trackingId) {
      // 模拟追踪记录数据
      this.trackingRecords = [
        {
          status: 'PENDING',
          updateTime: '2024-01-15 09:00:00',
          description: '物流单创建，等待发货'
        },
        {
          status: 'SHIPPED',
          updateTime: '2024-01-15 14:30:00',
          description: '货物已发出，承运人：张师傅'
        },
        {
          status: 'IN_TRANSIT',
          updateTime: '2024-01-15 16:45:00',
          description: '货物运输中，预计明日到达'
        },
        {
          status: 'ARRIVED',
          updateTime: '2024-01-16 10:20:00',
          description: '货物已到达目的地'
        },
        {
          status: 'RECEIVED',
          updateTime: '2024-01-16 11:00:00',
          description: '货物已签收，签收人：李经理'
        }
      ].filter(record => {
        const statusOrder = ['PENDING', 'SHIPPED', 'IN_TRANSIT', 'ARRIVED', 'RECEIVED'];
        const currentIndex = statusOrder.indexOf(this.trackingData.status);
        const recordIndex = statusOrder.indexOf(record.status);
        return recordIndex <= currentIndex;
      });
    },
    
    // 获取当前步骤
    getCurrentStep() {
      const statusMap = {
        'PENDING': 0,
        'SHIPPED': 1,
        'IN_TRANSIT': 2,
        'ARRIVED': 3,
        'RECEIVED': 4
      };
      return statusMap[this.trackingData.status] || 0;
    },
    
    // 获取步骤描述
    getStepDescription(status) {
      const timeField = {
        'PENDING': 'planShipTime',
        'SHIPPED': 'shipTime',
        'IN_TRANSIT': 'shipTime',
        'ARRIVED': 'arrivalTime',
        'RECEIVED': 'receiveTime'
      };
      
      const time = this.trackingData[timeField[status]];
      return time ? this.formatTime(time) : '';
    },
    
    // 获取步骤图标
    getStepIcon(status) {
      const iconMap = {
        'PENDING': 'clock-circle',
        'SHIPPED': 'car',
        'IN_TRANSIT': 'loading',
        'ARRIVED': 'environment',
        'RECEIVED': 'check-circle'
      };
      return iconMap[status];
    },
    
    // 获取时间线颜色
    getTimelineColor(status) {
      const colorMap = {
        'PENDING': 'gray',
        'SHIPPED': 'blue',
        'IN_TRANSIT': 'orange',
        'ARRIVED': 'purple',
        'RECEIVED': 'green'
      };
      return colorMap[status] || 'gray';
    },
    
    // 获取物流类型文本
    getLogisticsTypeText(type) {
      const typeMap = {
        'MATERIAL_IN': '原料入库',
        'SEMI_PRODUCT_OUT': '半成品出库',
        'PRODUCT_IN': '成品入库',
        'PRODUCT_OUT': '成品出库'
      };
      return typeMap[type] || type;
    },
    
    // 获取物流类型颜色
    getLogisticsTypeColor(type) {
      const colorMap = {
        'MATERIAL_IN': 'blue',
        'SEMI_PRODUCT_OUT': 'orange',
        'PRODUCT_IN': 'green',
        'PRODUCT_OUT': 'purple'
      };
      return colorMap[type] || 'default';
    },
    
    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待发货',
        'SHIPPED': '已发货',
        'IN_TRANSIT': '运输中',
        'ARRIVED': '已到达',
        'RECEIVED': '已收货'
      };
      return statusMap[status] || status;
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'PENDING': 'default',
        'SHIPPED': 'blue',
        'IN_TRANSIT': 'processing',
        'ARRIVED': 'warning',
        'RECEIVED': 'success'
      };
      return colorMap[status] || 'default';
    },
    
    // 获取状态图标
    getStatusIcon(status) {
      const iconMap = {
        'PENDING': 'clock-circle',
        'SHIPPED': 'car',
        'IN_TRANSIT': 'loading',
        'ARRIVED': 'environment',
        'RECEIVED': 'check-circle'
      };
      return iconMap[status] || 'question-circle';
    },
    
    // 获取运输方式文本
    getTransportMethodText(method) {
      const methodMap = {
        'SELF_DELIVERY': '自送',
        'EXPRESS': '快递',
        'LOGISTICS': '物流',
        'PICKUP': '自提'
      };
      return methodMap[method] || '未设置';
    },
    
    // 格式化时间
    formatTime(time) {
      if (!time) return '未设置';
      return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
    }
  }
}
</script>

<style scoped>
.tracking-detail {
  padding: 0;
}

.detail-section {
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

.info-item {
  margin-bottom: 8px;
}

.info-item .label {
  color: #8c8c8c;
  margin-right: 8px;
}

.info-item .value {
  color: #262626;
  font-weight: 500;
}

.progress-timeline {
  padding: 20px 0;
}

.address-card {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  background: #fafafa;
}

.address-title {
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.address-title .anticon {
  margin-right: 6px;
  color: #1890ff;
}

.address-content {
  color: #595959;
  line-height: 1.5;
}

.time-table {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
}

.time-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.time-row:last-child {
  margin-bottom: 0;
}

.time-label {
  color: #8c8c8c;
}

.time-value {
  color: #262626;
  font-weight: 500;
}

.timeline-content {
  padding-left: 8px;
}

.timeline-title {
  font-weight: 600;
  color: #262626;
  display: flex;
  align-items: center;
}

.timeline-title .anticon {
  margin-right: 6px;
}

.timeline-time {
  color: #8c8c8c;
  font-size: 12px;
  margin-top: 4px;
}

.timeline-description {
  color: #595959;
  margin-top: 4px;
}

.remark-content {
  background: #fafafa;
  border-radius: 6px;
  padding: 16px;
  color: #595959;
  line-height: 1.6;
}
</style>
