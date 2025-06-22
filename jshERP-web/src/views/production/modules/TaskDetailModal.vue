<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel">
    
    <a-spin :spinning="loading">
      <div v-if="task" class="task-detail">
        <!-- 任务基本信息 -->
        <a-card size="small" title="任务基本信息" style="margin-bottom: 16px;">
          <a-row :gutter="16">
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务编号：</span>
                <span class="detail-value">{{ task.taskNumber }}</span>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">任务名称：</span>
                <span class="detail-value">{{ task.taskName }}</span>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">产品名称：</span>
                <span class="detail-value">{{ task.productName }}</span>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">制作数量：</span>
                <span class="detail-value">{{ task.quantity }} {{ task.unitName }}</span>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">优先级：</span>
                <a-tag :color="getPriorityColor(task.priority)">
                  {{ getPriorityText(task.priority) }}
                </a-tag>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">状态：</span>
                <a-tag :color="getStatusColor(task.status)">
                  {{ getStatusText(task.status) }}
                </a-tag>
              </div>
            </a-col>
          </a-row>
        </a-card>
        
        <!-- 工人信息 -->
        <a-card size="small" title="工人信息" style="margin-bottom: 16px;" v-if="task.workerName">
          <a-row :gutter="16">
            <a-col :span="8">
              <div class="worker-card">
                <a-avatar size="large" :src="task.workerAvatar">
                  {{ task.workerName.charAt(0) }}
                </a-avatar>
                <div class="worker-info">
                  <div class="worker-name">{{ task.workerName }}</div>
                  <div class="worker-specialty">{{ task.workerSpecialty }}</div>
                </div>
              </div>
            </a-col>
            <a-col :span="16">
              <a-row :gutter="16">
                <a-col :span="12">
                  <div class="detail-item">
                    <span class="detail-label">派工时间：</span>
                    <span class="detail-value">{{ formatTime(task.assignTime) }}</span>
                  </div>
                </a-col>
                <a-col :span="12">
                  <div class="detail-item">
                    <span class="detail-label">预估工时：</span>
                    <span class="detail-value">{{ task.estimatedHours }}小时</span>
                  </div>
                </a-col>
                <a-col :span="12">
                  <div class="detail-item">
                    <span class="detail-label">实际工时：</span>
                    <span class="detail-value">{{ task.actualHours || 0 }}小时</span>
                  </div>
                </a-col>
                <a-col :span="12">
                  <div class="detail-item">
                    <span class="detail-label">工作效率：</span>
                    <span class="detail-value">{{ getEfficiency() }}%</span>
                  </div>
                </a-col>
              </a-row>
            </a-col>
          </a-row>
        </a-card>
        
        <!-- 时间信息 -->
        <a-card size="small" title="时间信息" style="margin-bottom: 16px;">
          <a-timeline>
            <a-timeline-item color="blue">
              <span class="timeline-time">{{ formatTime(task.createTime) }}</span>
              <div>任务创建</div>
            </a-timeline-item>
            <a-timeline-item color="orange" v-if="task.assignTime">
              <span class="timeline-time">{{ formatTime(task.assignTime) }}</span>
              <div>任务派工 - {{ task.workerName }}</div>
            </a-timeline-item>
            <a-timeline-item color="green" v-if="task.actualStartTime">
              <span class="timeline-time">{{ formatTime(task.actualStartTime) }}</span>
              <div>开始制作</div>
            </a-timeline-item>
            <a-timeline-item color="purple" v-if="task.completeTime">
              <span class="timeline-time">{{ formatTime(task.completeTime) }}</span>
              <div>制作完成</div>
            </a-timeline-item>
          </a-timeline>
        </a-card>
        
        <!-- 进度信息 -->
        <a-card size="small" title="制作进度" style="margin-bottom: 16px;" v-if="task.status === 'IN_PROGRESS'">
          <div class="progress-detail">
            <div class="progress-header">
              <span>完成进度</span>
              <span class="progress-percent">{{ getProgressPercent() }}%</span>
            </div>
            <a-progress 
              :percent="getProgressPercent()" 
              :status="getProgressStatus()"
              :strokeColor="getProgressColor()" />
            <a-row :gutter="16" style="margin-top: 16px;">
              <a-col :span="8">
                <div class="progress-item">
                  <div class="progress-value">{{ task.quantity }}</div>
                  <div class="progress-label">总数量</div>
                </div>
              </a-col>
              <a-col :span="8">
                <div class="progress-item">
                  <div class="progress-value">{{ task.completedQuantity || 0 }}</div>
                  <div class="progress-label">已完成</div>
                </div>
              </a-col>
              <a-col :span="8">
                <div class="progress-item">
                  <div class="progress-value">{{ task.quantity - (task.completedQuantity || 0) }}</div>
                  <div class="progress-label">剩余</div>
                </div>
              </a-col>
            </a-row>
          </div>
        </a-card>
        
        <!-- 质量信息 -->
        <a-card size="small" title="质量信息" style="margin-bottom: 16px;" v-if="task.qualityStatus">
          <a-row :gutter="16">
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">质检状态：</span>
                <a-tag :color="getQualityColor(task.qualityStatus)">
                  {{ getQualityText(task.qualityStatus) }}
                </a-tag>
              </div>
            </a-col>
            <a-col :span="12">
              <div class="detail-item">
                <span class="detail-label">质检时间：</span>
                <span class="detail-value">{{ formatTime(task.qualityCheckTime) }}</span>
              </div>
            </a-col>
            <a-col :span="24" v-if="task.qualityRemark">
              <div class="detail-item">
                <span class="detail-label">质检备注：</span>
                <div class="detail-value">{{ task.qualityRemark }}</div>
              </div>
            </a-col>
          </a-row>
        </a-card>
        
        <!-- 备注信息 -->
        <a-card size="small" title="备注信息" v-if="task.remark">
          <div class="remark-content">{{ task.remark }}</div>
        </a-card>
      </div>
    </a-spin>
    
  </a-modal>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: "TaskDetailModal",
  data() {
    return {
      title: "任务详情",
      visible: false,
      loading: false,
      task: null
    }
  },
  methods: {
    view(task) {
      this.task = { ...task };
      this.visible = true;
      this.title = `任务详情 - ${task.taskName}`;
    },
    
    handleCancel() {
      this.visible = false;
      this.task = null;
    },
    
    getPriorityColor(priority) {
      const colors = {
        'LOW': 'default',
        'NORMAL': 'blue',
        'HIGH': 'orange',
        'URGENT': 'red'
      };
      return colors[priority] || 'default';
    },
    
    getPriorityText(priority) {
      const texts = {
        'LOW': '低优先级',
        'NORMAL': '普通优先级',
        'HIGH': '高优先级',
        'URGENT': '紧急优先级'
      };
      return texts[priority] || '普通优先级';
    },
    
    getStatusColor(status) {
      const colors = {
        'PENDING': 'orange',
        'ASSIGNED': 'blue',
        'IN_PROGRESS': 'processing',
        'COMPLETED': 'success',
        'CANCELLED': 'error'
      };
      return colors[status] || 'default';
    },
    
    getStatusText(status) {
      const texts = {
        'PENDING': '待派单',
        'ASSIGNED': '已派单',
        'IN_PROGRESS': '制作中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      };
      return texts[status] || '未知状态';
    },
    
    getQualityColor(qualityStatus) {
      const colors = {
        'PASS': 'green',
        'FAIL': 'red',
        'REWORK': 'orange',
        'PENDING': 'blue'
      };
      return colors[qualityStatus] || 'default';
    },
    
    getQualityText(qualityStatus) {
      const texts = {
        'PASS': '质检合格',
        'FAIL': '质检不合格',
        'REWORK': '需要返工',
        'PENDING': '待质检'
      };
      return texts[qualityStatus] || '未知状态';
    },
    
    getProgressPercent() {
      if (!this.task || !this.task.quantity || !this.task.completedQuantity) return 0;
      return Math.round((this.task.completedQuantity / this.task.quantity) * 100);
    },
    
    getProgressStatus() {
      const percent = this.getProgressPercent();
      if (percent === 100) return 'success';
      if (this.isOverdue()) return 'exception';
      return 'active';
    },
    
    getProgressColor() {
      const percent = this.getProgressPercent();
      if (percent === 100) return '#52c41a';
      if (this.isOverdue()) return '#ff4d4f';
      if (percent >= 80) return '#1890ff';
      if (percent >= 50) return '#fa8c16';
      return '#faad14';
    },
    
    getEfficiency() {
      if (!this.task || !this.task.estimatedHours || !this.task.actualHours) return 0;
      return Math.round((this.task.estimatedHours / this.task.actualHours) * 100);
    },
    
    isOverdue() {
      if (!this.task || !this.task.planEndTime) return false;
      const now = dayjs();
      const planEnd = dayjs(this.task.planEndTime);
      return now.isAfter(planEnd) && this.task.status !== 'COMPLETED';
    },
    
    formatTime(time) {
      if (!time) return '-';
      return dayjs(time).format('YYYY-MM-DD HH:mm:ss');
    }
  }
}
</script>

<style scoped>
.task-detail {
  max-height: 70vh;
  overflow-y: auto;
}

.detail-item {
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.detail-label {
  font-weight: 500;
  color: #595959;
  min-width: 80px;
}

.detail-value {
  color: #262626;
  flex: 1;
}

.worker-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: #f6f8fa;
  border-radius: 8px;
}

.worker-info {
  margin-left: 12px;
}

.worker-name {
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.worker-specialty {
  font-size: 12px;
  color: #8c8c8c;
}

.timeline-time {
  font-size: 12px;
  color: #8c8c8c;
  margin-right: 8px;
}

.progress-detail {
  padding: 16px;
  background: #f6f8fa;
  border-radius: 8px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.progress-percent {
  font-size: 18px;
  font-weight: 600;
  color: #1890ff;
}

.progress-item {
  text-align: center;
  padding: 12px;
  background: white;
  border-radius: 6px;
}

.progress-value {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.progress-label {
  font-size: 12px;
  color: #8c8c8c;
}

.remark-content {
  padding: 12px;
  background: #f6f8fa;
  border-radius: 6px;
  color: #595959;
  line-height: 1.6;
}
</style>
