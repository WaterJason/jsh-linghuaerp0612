<template>
  <div class="task-card" 
       :class="[
         `priority-${task.priority.toLowerCase()}`, 
         `status-${task.status.toLowerCase()}`,
         { 'dragging': isDragging }
       ]"
       :draggable="draggable"
       @dragstart="onDragStart"
       @dragend="onDragEnd">
    
    <!-- 卡片头部 -->
    <div class="card-header">
      <div class="task-info">
        <h4 class="task-title" :title="task.taskName">{{ task.taskName }}</h4>
        <span class="task-number">{{ task.taskNumber }}</span>
      </div>
      <div class="priority-badge">
        <a-tag :color="getPriorityColor(task.priority)" size="small">
          {{ getPriorityText(task.priority) }}
        </a-tag>
      </div>
    </div>
    
    <!-- 产品信息 -->
    <div class="product-info">
      <div class="product-name" :title="task.productName">
        <a-icon type="appstore" />
        {{ task.productName }}
      </div>
      <div class="quantity">
        <a-icon type="number" />
        数量: {{ task.quantity }} {{ task.unitName }}
      </div>
    </div>
    
    <!-- 工人信息 -->
    <div class="worker-info" v-if="task.workerName">
      <a-avatar size="small" :src="task.workerAvatar" class="worker-avatar">
        {{ task.workerName.charAt(0) }}
      </a-avatar>
      <span class="worker-name">{{ task.workerName }}</span>
      <span class="worker-specialty" v-if="task.workerSpecialty">{{ task.workerSpecialty }}</span>
    </div>
    
    <!-- 进度信息 -->
    <div class="progress-info" v-if="task.status === 'IN_PROGRESS'">
      <div class="progress-header">
        <span class="progress-label">制作进度</span>
        <span class="progress-percent">{{ getProgressPercent(task) }}%</span>
      </div>
      <a-progress 
        :percent="getProgressPercent(task)" 
        :size="'small'"
        :status="getProgressStatus(task)"
        :strokeColor="getProgressColor(task)" />
      <div class="time-info">
        <span class="used-time">
          <a-icon type="clock-circle" />
          已用时: {{ getUsedTime(task) }}
        </span>
        <span class="estimated-time">
          <a-icon type="hourglass" />
          预估: {{ task.estimatedHours }}h
        </span>
      </div>
    </div>
    
    <!-- 时间信息 -->
    <div class="time-info">
      <div v-if="task.planStartTime" class="time-item">
        <a-icon type="schedule" />
        <span class="time-label">计划:</span>
        <span class="time-value">{{ formatTime(task.planStartTime) }}</span>
      </div>
      <div v-if="task.actualStartTime" class="time-item">
        <a-icon type="play-circle" />
        <span class="time-label">开始:</span>
        <span class="time-value">{{ formatTime(task.actualStartTime) }}</span>
      </div>
      <div v-if="task.completeTime" class="time-item">
        <a-icon type="check-circle" />
        <span class="time-label">完成:</span>
        <span class="time-value">{{ formatTime(task.completeTime) }}</span>
      </div>
    </div>
    
    <!-- 质量状态 -->
    <div class="quality-status" v-if="task.qualityStatus">
      <a-tag :color="getQualityColor(task.qualityStatus)" size="small">
        <a-icon :type="getQualityIcon(task.qualityStatus)" />
        {{ getQualityText(task.qualityStatus) }}
      </a-tag>
    </div>
    
    <!-- 操作按钮 -->
    <div class="card-actions">
      <a-button-group size="small">
        <!-- 待派单状态按钮 -->
        <template v-if="task.status === 'PENDING'">
          <a-button @click="$emit('assign', task)" type="primary">
            <a-icon type="user-add" />派工
          </a-button>
          <a-button @click="$emit('edit', task)">
            <a-icon type="edit" />编辑
          </a-button>
        </template>
        
        <!-- 已派单状态按钮 -->
        <template v-if="task.status === 'ASSIGNED'">
          <a-button @click="$emit('start', task)" type="primary">
            <a-icon type="play-circle" />开始
          </a-button>
          <a-button @click="$emit('reassign', task)">
            <a-icon type="swap" />重新派工
          </a-button>
        </template>
        
        <!-- 进行中状态按钮 -->
        <template v-if="task.status === 'IN_PROGRESS'">
          <a-button @click="$emit('report', task)" type="primary">
            <a-icon type="file-text" />报工
          </a-button>
          <a-button @click="$emit('pause', task)">
            <a-icon type="pause-circle" />暂停
          </a-button>
          <a-button @click="$emit('complete', task)" type="dashed">
            <a-icon type="check" />完成
          </a-button>
        </template>
        
        <!-- 已完成状态按钮 -->
        <template v-if="task.status === 'COMPLETED'">
          <a-button @click="$emit('quality-check', task)" type="primary">
            <a-icon type="safety-certificate" />质检
          </a-button>
          <a-button @click="$emit('ship', task)">
            <a-icon type="car" />发货
          </a-button>
        </template>
        
        <!-- 通用按钮 -->
        <a-button @click="$emit('view', task)">
          <a-icon type="eye" />详情
        </a-button>
        
        <!-- 删除按钮（仅待派单状态显示） -->
        <a-button v-if="task.status === 'PENDING'" @click="$emit('delete', task)" type="danger">
          <a-icon type="delete" />
        </a-button>
      </a-button-group>
    </div>
    
    <!-- 紧急标识 -->
    <div v-if="task.priority === 'URGENT'" class="urgent-flag">
      <a-icon type="fire" />
      紧急
    </div>
    
    <!-- 延期标识 -->
    <div v-if="isOverdue(task)" class="overdue-flag">
      <a-icon type="exclamation-circle" />
      延期
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: "TaskCard",
  props: {
    task: {
      type: Object,
      required: true
    },
    draggable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      isDragging: false
    }
  },
  methods: {
    onDragStart(event) {
      this.isDragging = true;
      event.dataTransfer.setData('taskId', this.task.id);
      event.dataTransfer.setData('taskStatus', this.task.status);
      event.dataTransfer.setData('taskName', this.task.taskName);
      event.dataTransfer.effectAllowed = 'move';

      // 创建拖拽时的自定义图像
      this.createDragImage(event);

      // 添加拖拽开始的视觉反馈
      this.$emit('drag-start', this.task);
    },

    onDragEnd(event) {
      this.isDragging = false;

      // 添加拖拽结束的视觉反馈
      this.$emit('drag-end', this.task);
    },

    // 创建拖拽时的自定义图像
    createDragImage(event) {
      const dragImage = this.$el.cloneNode(true);
      dragImage.style.transform = 'rotate(5deg)';
      dragImage.style.opacity = '0.8';
      dragImage.style.width = this.$el.offsetWidth + 'px';
      dragImage.style.position = 'absolute';
      dragImage.style.top = '-1000px';
      dragImage.style.left = '-1000px';
      dragImage.style.pointerEvents = 'none';
      dragImage.style.zIndex = '1000';

      document.body.appendChild(dragImage);

      event.dataTransfer.setDragImage(dragImage,
        this.$el.offsetWidth / 2,
        this.$el.offsetHeight / 2
      );

      // 清理临时元素
      setTimeout(() => {
        if (dragImage.parentNode) {
          dragImage.parentNode.removeChild(dragImage);
        }
      }, 0);
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
        'LOW': '低',
        'NORMAL': '普通',
        'HIGH': '高',
        'URGENT': '紧急'
      };
      return texts[priority] || '普通';
    },
    
    getProgressPercent(task) {
      if (!task.quantity || !task.completedQuantity) return 0;
      return Math.round((task.completedQuantity / task.quantity) * 100);
    },
    
    getProgressStatus(task) {
      const percent = this.getProgressPercent(task);
      if (percent === 100) return 'success';
      if (this.isOverdue(task)) return 'exception';
      return 'active';
    },
    
    getProgressColor(task) {
      const percent = this.getProgressPercent(task);
      if (percent === 100) return '#52c41a';
      if (this.isOverdue(task)) return '#ff4d4f';
      if (percent >= 80) return '#1890ff';
      if (percent >= 50) return '#fa8c16';
      return '#faad14';
    },
    
    getUsedTime(task) {
      if (!task.actualStartTime) return '0h';
      const start = dayjs(task.actualStartTime);
      const now = dayjs();
      const hours = now.diff(start, 'hours', true);
      return `${hours.toFixed(1)}h`;
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
        'PASS': '合格',
        'FAIL': '不合格',
        'REWORK': '返工',
        'PENDING': '待检'
      };
      return texts[qualityStatus] || '未知';
    },
    
    getQualityIcon(qualityStatus) {
      const icons = {
        'PASS': 'check-circle',
        'FAIL': 'close-circle',
        'REWORK': 'redo',
        'PENDING': 'clock-circle'
      };
      return icons[qualityStatus] || 'question-circle';
    },
    
    formatTime(time) {
      if (!time) return '';
      return dayjs(time).format('MM-DD HH:mm');
    },
    
    isOverdue(task) {
      if (!task.planEndTime) return false;
      const now = dayjs();
      const planEnd = dayjs(task.planEndTime);
      return now.isAfter(planEnd) && task.status !== 'COMPLETED';
    }
  }
}
</script>

<style scoped>
.task-card {
  background: white;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-left: 4px solid #d9d9d9;
  cursor: move;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.task-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.task-card.dragging {
  opacity: 0.7;
  transform: rotate(5deg) scale(1.05);
  box-shadow: 0 8px 24px rgba(0,0,0,0.25);
  z-index: 1000;
  border: 2px solid #1890ff;
  background: linear-gradient(135deg, #fff 0%, #e6f7ff 100%);
}

.task-card.dragging::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(45deg, #1890ff, #722ed1, #eb2f96, #fa541c);
  border-radius: 10px;
  z-index: -1;
  animation: dragGlow 1s infinite alternate;
}

@keyframes dragGlow {
  0% { opacity: 0.5; }
  100% { opacity: 0.8; }
}

.task-card.priority-urgent {
  border-left-color: #ff4d4f;
  background: linear-gradient(135deg, #fff 0%, #fff2f0 100%);
}

.task-card.priority-high {
  border-left-color: #fa8c16;
}

.task-card.priority-normal {
  border-left-color: #1890ff;
}

.task-card.priority-low {
  border-left-color: #d9d9d9;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.task-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 150px;
}

.task-number {
  font-size: 12px;
  color: #8c8c8c;
  font-family: 'Courier New', monospace;
}

.product-info {
  margin-bottom: 12px;
}

.product-name {
  font-weight: 500;
  color: #595959;
  margin-bottom: 4px;
  display: flex;
  align-items: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-name .anticon {
  margin-right: 6px;
  color: #1890ff;
}

.quantity {
  font-size: 12px;
  color: #8c8c8c;
  display: flex;
  align-items: center;
}

.quantity .anticon {
  margin-right: 4px;
}

.worker-info {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px;
  background: #f6f8fa;
  border-radius: 6px;
}

.worker-avatar {
  margin-right: 8px;
  background: #1890ff;
}

.worker-name {
  font-size: 12px;
  font-weight: 500;
  color: #262626;
  margin-right: 8px;
}

.worker-specialty {
  font-size: 11px;
  color: #8c8c8c;
  background: #e6f7ff;
  padding: 2px 6px;
  border-radius: 10px;
}

.progress-info {
  margin-bottom: 12px;
  padding: 8px;
  background: #f0f9ff;
  border-radius: 6px;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.progress-label {
  font-size: 12px;
  color: #595959;
  font-weight: 500;
}

.progress-percent {
  font-size: 12px;
  color: #1890ff;
  font-weight: 600;
}

.time-info {
  font-size: 11px;
  color: #8c8c8c;
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
}

.time-info .anticon {
  margin-right: 4px;
}

.time-item {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  font-size: 11px;
}

.time-item .anticon {
  margin-right: 4px;
  color: #1890ff;
}

.time-label {
  margin-right: 4px;
  color: #8c8c8c;
}

.time-value {
  color: #595959;
  font-weight: 500;
}

.quality-status {
  margin-bottom: 12px;
}

.card-actions {
  margin-top: 12px;
}

.card-actions .ant-btn-group {
  width: 100%;
}

.card-actions .ant-btn {
  flex: 1;
  font-size: 11px;
  height: 28px;
  padding: 0 8px;
}

.urgent-flag {
  position: absolute;
  top: -1px;
  right: -1px;
  background: #ff4d4f;
  color: white;
  padding: 2px 8px;
  font-size: 10px;
  border-radius: 0 8px 0 8px;
  font-weight: 600;
}

.urgent-flag .anticon {
  margin-right: 2px;
}

.overdue-flag {
  position: absolute;
  top: 20px;
  right: -1px;
  background: #fa541c;
  color: white;
  padding: 2px 8px;
  font-size: 10px;
  border-radius: 8px 0 0 8px;
  font-weight: 600;
}

.overdue-flag .anticon {
  margin-right: 2px;
}

/* 状态特定样式 */
.task-card.status-pending {
  border-left-width: 4px;
}

.task-card.status-assigned {
  border-left-width: 4px;
  background: linear-gradient(135deg, #fff 0%, #f6ffed 100%);
}

.task-card.status-in_progress {
  border-left-width: 6px;
  background: linear-gradient(135deg, #fff 0%, #e6f7ff 100%);
}

.task-card.status-completed {
  border-left-width: 4px;
  background: linear-gradient(135deg, #fff 0%, #f6ffed 100%);
  opacity: 0.9;
}

.task-card.status-cancelled {
  border-left-width: 4px;
  background: linear-gradient(135deg, #fff 0%, #fff1f0 100%);
  opacity: 0.7;
}
</style>
