<template>
  <div class="kanban-container">
    <!-- 顶部统计卡片 -->
    <a-row :gutter="16" style="margin-bottom: 24px;">
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="今日生产订单"
            :value="statistics.todayOrders"
            :value-style="{ color: '#3f8600' }"
            suffix="个"
          />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="生产中订单"
            :value="statistics.processingOrders"
            :value-style="{ color: '#1890ff' }"
            suffix="个"
          />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="今日完成"
            :value="statistics.completedToday"
            :value-style="{ color: '#52c41a' }"
            suffix="个"
          />
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card>
          <a-statistic
            title="设备利用率"
            :value="statistics.equipmentUtilization"
            :value-style="{ color: '#722ed1' }"
            suffix="%"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 看板主体 -->
    <a-row :gutter="16">
      <!-- 待生产 -->
      <a-col :span="6">
        <a-card title="待生产" class="kanban-column">
          <template slot="extra">
            <a-badge :count="pendingTasks.length" />
          </template>
          <div class="task-list">
            <div
              v-for="task in pendingTasks"
              :key="task.id"
              class="task-card pending"
              @click="showTaskDetail(task)"
            >
              <div class="task-header">
                <span class="task-title">{{ task.orderNumber }}</span>
                <a-tag color="orange">{{ task.priority }}</a-tag>
              </div>
              <div class="task-content">
                <p><strong>产品：</strong>{{ task.productName }}</p>
                <p><strong>数量：</strong>{{ task.quantity }}</p>
                <p><strong>预计：</strong>{{ task.expectedDate }}</p>
              </div>
              <div class="task-footer">
                <a-button size="small" type="primary" @click.stop="startProduction(task)">
                  开始生产
                </a-button>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 生产中 -->
      <a-col :span="6">
        <a-card title="生产中" class="kanban-column">
          <template slot="extra">
            <a-badge :count="processingTasks.length" />
          </template>
          <div class="task-list">
            <div
              v-for="task in processingTasks"
              :key="task.id"
              class="task-card processing"
              @click="showTaskDetail(task)"
            >
              <div class="task-header">
                <span class="task-title">{{ task.orderNumber }}</span>
                <a-tag color="blue">{{ task.priority }}</a-tag>
              </div>
              <div class="task-content">
                <p><strong>产品：</strong>{{ task.productName }}</p>
                <p><strong>数量：</strong>{{ task.quantity }}</p>
                <p><strong>进度：</strong></p>
                <a-progress :percent="task.progress" size="small" />
                <p><strong>负责人：</strong>{{ task.assignee }}</p>
              </div>
              <div class="task-footer">
                <a-button size="small" @click.stop="reportProgress(task)">
                  报工
                </a-button>
                <a-button size="small" type="primary" @click.stop="completeProduction(task)">
                  完成
                </a-button>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 质检中 -->
      <a-col :span="6">
        <a-card title="质检中" class="kanban-column">
          <template slot="extra">
            <a-badge :count="qualityTasks.length" />
          </template>
          <div class="task-list">
            <div
              v-for="task in qualityTasks"
              :key="task.id"
              class="task-card quality"
              @click="showTaskDetail(task)"
            >
              <div class="task-header">
                <span class="task-title">{{ task.orderNumber }}</span>
                <a-tag color="purple">{{ task.priority }}</a-tag>
              </div>
              <div class="task-content">
                <p><strong>产品：</strong>{{ task.productName }}</p>
                <p><strong>数量：</strong>{{ task.quantity }}</p>
                <p><strong>质检员：</strong>{{ task.inspector }}</p>
              </div>
              <div class="task-footer">
                <a-button size="small" type="primary" @click.stop="passQuality(task)">
                  通过
                </a-button>
                <a-button size="small" danger @click.stop="rejectQuality(task)">
                  不合格
                </a-button>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>

      <!-- 已完成 -->
      <a-col :span="6">
        <a-card title="已完成" class="kanban-column">
          <template slot="extra">
            <a-badge :count="completedTasks.length" />
          </template>
          <div class="task-list">
            <div
              v-for="task in completedTasks"
              :key="task.id"
              class="task-card completed"
              @click="showTaskDetail(task)"
            >
              <div class="task-header">
                <span class="task-title">{{ task.orderNumber }}</span>
                <a-tag color="green">{{ task.priority }}</a-tag>
              </div>
              <div class="task-content">
                <p><strong>产品：</strong>{{ task.productName }}</p>
                <p><strong>数量：</strong>{{ task.quantity }}</p>
                <p><strong>完成时间：</strong>{{ task.completedTime }}</p>
              </div>
              <div class="task-footer">
                <a-button size="small" @click.stop="viewReport(task)">
                  查看报告
                </a-button>
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 任务详情弹窗 -->
    <a-modal
      title="任务详情"
      :visible="detailVisible"
      @cancel="detailVisible = false"
      :footer="null"
      width="800px"
    >
      <div v-if="selectedTask">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="订单编号">{{ selectedTask.orderNumber }}</a-descriptions-item>
          <a-descriptions-item label="产品名称">{{ selectedTask.productName }}</a-descriptions-item>
          <a-descriptions-item label="产品规格">{{ selectedTask.specification }}</a-descriptions-item>
          <a-descriptions-item label="生产数量">{{ selectedTask.quantity }}</a-descriptions-item>
          <a-descriptions-item label="优先级">
            <a-tag :color="getPriorityColor(selectedTask.priority)">{{ selectedTask.priority }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="当前状态">
            <a-tag :color="getStatusColor(selectedTask.status)">{{ getStatusText(selectedTask.status) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="生产进度">
            <a-progress :percent="selectedTask.progress" />
          </a-descriptions-item>
          <a-descriptions-item label="负责人">{{ selectedTask.assignee || '未分配' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ selectedTask.createTime }}</a-descriptions-item>
          <a-descriptions-item label="预计完成">{{ selectedTask.expectedDate }}</a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script>
export default {
  name: 'ChongzuoKanban',
  data() {
    return {
      detailVisible: false,
      selectedTask: null,
      statistics: {
        todayOrders: 15,
        processingOrders: 8,
        completedToday: 12,
        equipmentUtilization: 85
      },
      pendingTasks: [
        {
          id: 1,
          orderNumber: 'PO2024001',
          productName: '掐丝珐琅花瓶',
          specification: '高30cm',
          quantity: 10,
          priority: '高',
          status: 'pending',
          progress: 0,
          expectedDate: '2024-06-25',
          createTime: '2024-06-23 09:00:00'
        },
        {
          id: 2,
          orderNumber: 'PO2024002',
          productName: '珐琅首饰盒',
          specification: '15x10cm',
          quantity: 20,
          priority: '中',
          status: 'pending',
          progress: 0,
          expectedDate: '2024-06-26',
          createTime: '2024-06-23 10:30:00'
        }
      ],
      processingTasks: [
        {
          id: 3,
          orderNumber: 'PO2024003',
          productName: '掐丝珐琅盘',
          specification: '直径25cm',
          quantity: 5,
          priority: '高',
          status: 'processing',
          progress: 65,
          assignee: '张师傅',
          expectedDate: '2024-06-24',
          createTime: '2024-06-22 14:00:00'
        }
      ],
      qualityTasks: [
        {
          id: 4,
          orderNumber: 'PO2024004',
          productName: '珐琅茶具',
          specification: '一套6件',
          quantity: 3,
          priority: '中',
          status: 'quality',
          progress: 100,
          inspector: '李质检',
          expectedDate: '2024-06-23',
          createTime: '2024-06-21 09:00:00'
        }
      ],
      completedTasks: [
        {
          id: 5,
          orderNumber: 'PO2024005',
          productName: '掐丝珐琅摆件',
          specification: '高20cm',
          quantity: 8,
          priority: '低',
          status: 'completed',
          progress: 100,
          completedTime: '2024-06-23 16:30:00',
          createTime: '2024-06-20 11:00:00'
        }
      ]
    }
  },
  mounted() {
    this.loadData()
    // 设置定时刷新
    this.timer = setInterval(() => {
      this.loadData()
    }, 30000) // 30秒刷新一次
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    loadData() {
      // 这里应该调用API加载数据
      console.log('加载看板数据...')
    },
    showTaskDetail(task) {
      this.selectedTask = task
      this.detailVisible = true
    },
    startProduction(task) {
      this.$confirm({
        title: '确认开始生产',
        content: `确定要开始生产订单"${task.orderNumber}"吗？`,
        onOk: () => {
          this.$message.success('生产已开始')
          this.loadData()
        }
      })
    },
    completeProduction(task) {
      this.$confirm({
        title: '确认完成生产',
        content: `确定要完成生产订单"${task.orderNumber}"吗？`,
        onOk: () => {
          this.$message.success('生产已完成，进入质检环节')
          this.loadData()
        }
      })
    },
    reportProgress(task) {
      this.$message.info('打开报工界面...')
    },
    passQuality(task) {
      this.$confirm({
        title: '确认质检通过',
        content: `确定订单"${task.orderNumber}"质检通过吗？`,
        onOk: () => {
          this.$message.success('质检通过，订单已完成')
          this.loadData()
        }
      })
    },
    rejectQuality(task) {
      this.$confirm({
        title: '确认质检不合格',
        content: `确定订单"${task.orderNumber}"质检不合格吗？`,
        onOk: () => {
          this.$message.warning('质检不合格，返回生产环节')
          this.loadData()
        }
      })
    },
    viewReport(task) {
      this.$message.info('查看生产报告...')
    },
    getStatusColor(status) {
      const colors = {
        pending: 'orange',
        processing: 'blue',
        quality: 'purple',
        completed: 'green'
      }
      return colors[status] || 'default'
    },
    getStatusText(status) {
      const texts = {
        pending: '待生产',
        processing: '生产中',
        quality: '质检中',
        completed: '已完成'
      }
      return texts[status] || status
    },
    getPriorityColor(priority) {
      const colors = {
        高: 'red',
        中: 'orange',
        低: 'green'
      }
      return colors[priority] || 'default'
    }
  }
}
</script>

<style scoped>
.kanban-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
}

.kanban-column {
  height: 600px;
}

.task-list {
  height: 520px;
  overflow-y: auto;
}

.task-card {
  background: white;
  border-radius: 6px;
  padding: 12px;
  margin-bottom: 12px;
  border-left: 4px solid;
  cursor: pointer;
  transition: all 0.3s;
}

.task-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.task-card.pending {
  border-left-color: #faad14;
}

.task-card.processing {
  border-left-color: #1890ff;
}

.task-card.quality {
  border-left-color: #722ed1;
}

.task-card.completed {
  border-left-color: #52c41a;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.task-title {
  font-weight: 500;
  font-size: 14px;
}

.task-content {
  margin-bottom: 12px;
}

.task-content p {
  margin: 4px 0;
  font-size: 12px;
  color: #666;
}

.task-footer {
  display: flex;
  gap: 8px;
}

.task-footer .ant-btn {
  flex: 1;
}
</style>
