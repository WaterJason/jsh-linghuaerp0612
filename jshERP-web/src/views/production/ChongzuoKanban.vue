<template>
  <div class="chongzuo-kanban">
    <a-card :bordered="false" title="崇左生产看板">
      <div class="kanban-container">
        <!-- 待生产列 -->
        <div class="kanban-column">
          <div class="column-header pending">
            <a-icon type="clock-circle" />
            <span>待生产</span>
            <a-badge :count="pendingOrders.length" :number-style="{backgroundColor: '#f5222d'}" />
          </div>
          <div class="column-content">
            <div 
              v-for="order in pendingOrders" 
              :key="order.id" 
              class="work-order-card"
            >
              <a-card size="small" :hoverable="true">
                <div class="card-header">
                  <span class="order-no">{{ order.workOrderNo }}</span>
                  <a-tag :color="getWorkTypeColor(order.workType)">
                    {{ getWorkTypeName(order.workType) }}
                  </a-tag>
                </div>
                <div class="card-content">
                  <p><strong>生产订单:</strong> {{ order.productionOrderId }}</p>
                  <p><strong>创建时间:</strong> {{ formatDate(order.createTime) }}</p>
                  <p v-if="order.remark"><strong>备注:</strong> {{ order.remark }}</p>
                </div>
                <div class="card-actions">
                  <a-button 
                    type="primary" 
                    size="small" 
                    @click="showAssignModal(order)"
                    icon="user"
                  >
                    派单
                  </a-button>
                </div>
              </a-card>
            </div>
          </div>
        </div>

        <!-- 生产中列 -->
        <div class="kanban-column">
          <div class="column-header in-progress">
            <a-icon type="loading" />
            <span>生产中</span>
            <a-badge :count="inProgressOrders.length" :number-style="{backgroundColor: '#1890ff'}" />
          </div>
          <div class="column-content">
            <div 
              v-for="order in inProgressOrders" 
              :key="order.id" 
              class="work-order-card"
            >
              <a-card size="small" :hoverable="true">
                <div class="card-header">
                  <span class="order-no">{{ order.workOrderNo }}</span>
                  <a-tag :color="getWorkTypeColor(order.workType)">
                    {{ getWorkTypeName(order.workType) }}
                  </a-tag>
                </div>
                <div class="card-content">
                  <p><strong>处理人:</strong> {{ order.handlerName || '未分配' }}</p>
                  <p><strong>开始时间:</strong> {{ formatDate(order.startTime) }}</p>
                  <p v-if="order.estimatedHours"><strong>预计工时:</strong> {{ order.estimatedHours }}小时</p>
                </div>
                <div class="card-actions">
                  <a-button 
                    type="primary" 
                    size="small" 
                    @click="showCompleteModal(order)"
                    icon="check"
                  >
                    完工
                  </a-button>
                </div>
              </a-card>
            </div>
          </div>
        </div>

        <!-- 待入库列 -->
        <div class="kanban-column">
          <div class="column-header completed">
            <a-icon type="check-circle" />
            <span>待入库</span>
            <a-badge :count="completedOrders.length" :number-style="{backgroundColor: '#52c41a'}" />
          </div>
          <div class="column-content">
            <div 
              v-for="order in completedOrders" 
              :key="order.id" 
              class="work-order-card"
            >
              <a-card size="small" :hoverable="true">
                <div class="card-header">
                  <span class="order-no">{{ order.workOrderNo }}</span>
                  <a-tag :color="getWorkTypeColor(order.workType)">
                    {{ getWorkTypeName(order.workType) }}
                  </a-tag>
                </div>
                <div class="card-content">
                  <p><strong>处理人:</strong> {{ order.handlerName }}</p>
                  <p><strong>完成时间:</strong> {{ formatDate(order.completeTime) }}</p>
                  <p v-if="order.actualHours"><strong>实际工时:</strong> {{ order.actualHours }}小时</p>
                </div>
                <div class="card-actions">
                  <a-button 
                    type="default" 
                    size="small" 
                    @click="viewCompleteImages(order)"
                    icon="picture"
                    v-if="order.completeImages"
                  >
                    查看图片
                  </a-button>
                </div>
              </a-card>
            </div>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 派单对话框 -->
    <a-modal
      title="派单"
      :visible="assignModalVisible"
      @ok="handleAssign"
      @cancel="assignModalVisible = false"
      :confirmLoading="assignLoading"
    >
      <a-form :form="assignForm" layout="vertical">
        <a-form-item label="工单号">
          <a-input :value="currentOrder.workOrderNo" disabled />
        </a-form-item>
        <a-form-item label="选择处理人">
          <a-select
            v-decorator="['handlerId', { rules: [{ required: true, message: '请选择处理人' }] }]"
            placeholder="请选择处理人"
            @change="onHandlerChange"
          >
            <a-select-option v-for="user in userList" :key="user.id" :value="user.id">
              {{ user.username }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 完工对话框 -->
    <a-modal
      title="完工确认"
      :visible="completeModalVisible"
      @ok="handleComplete"
      @cancel="completeModalVisible = false"
      :confirmLoading="completeLoading"
      width="600px"
    >
      <a-form :form="completeForm" layout="vertical">
        <a-form-item label="工单号">
          <a-input :value="currentOrder.workOrderNo" disabled />
        </a-form-item>
        <a-form-item label="上传完工图片">
          <a-upload
            v-decorator="['completeImages']"
            :file-list="fileList"
            :before-upload="beforeUpload"
            @change="handleUploadChange"
            list-type="picture-card"
            :multiple="true"
            accept="image/*"
          >
            <div v-if="fileList.length < 8">
              <a-icon type="plus" />
              <div class="ant-upload-text">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>
        <a-form-item label="质检备注">
          <a-textarea
            v-decorator="['qualityNotes']"
            placeholder="请输入质检备注"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 图片预览对话框 -->
    <a-modal
      title="完工图片"
      :visible="imagePreviewVisible"
      @cancel="imagePreviewVisible = false"
      :footer="null"
      width="800px"
    >
      <div class="image-preview-container">
        <img 
          v-for="(image, index) in previewImages" 
          :key="index" 
          :src="image" 
          :alt="`完工图片${index + 1}`"
          class="preview-image"
        />
      </div>
    </a-modal>
  </div>
</template>

<script>
import { getAction, postAction } from '@/api/manage'

export default {
  name: 'ChongzuoKanban',
  data() {
    return {
      // 看板数据
      pendingOrders: [],
      inProgressOrders: [],
      completedOrders: [],
      
      // 派单相关
      assignModalVisible: false,
      assignLoading: false,
      assignForm: this.$form.createForm(this),
      userList: [],
      
      // 完工相关
      completeModalVisible: false,
      completeLoading: false,
      completeForm: this.$form.createForm(this),
      fileList: [],
      
      // 图片预览
      imagePreviewVisible: false,
      previewImages: [],
      
      // 当前操作的工单
      currentOrder: {},
      
      // 定时刷新
      refreshTimer: null
    }
  },
  
  mounted() {
    this.loadKanbanData()
    this.loadUserList()
    // 每30秒自动刷新数据
    this.refreshTimer = setInterval(() => {
      this.loadKanbanData()
    }, 30000)
  },
  
  beforeDestroy() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer)
    }
  },
  
  methods: {
    // 加载看板数据
    loadKanbanData() {
      getAction('/workOrder/kanbanData').then(res => {
        if (res.code === 200) {
          this.pendingOrders = res.data.pendingOrders || []
          this.inProgressOrders = res.data.inProgressOrders || []
          this.completedOrders = res.data.completedOrders || []
        } else {
          this.$message.error('加载看板数据失败：' + res.data.message)
        }
      }).catch(err => {
        this.$message.error('加载看板数据失败')
        console.error(err)
      })
    },
    
    // 加载用户列表
    loadUserList() {
      getAction('/user/list').then(res => {
        if (res.code === 200) {
          this.userList = res.data.rows || []
        }
      }).catch(err => {
        console.error('加载用户列表失败', err)
      })
    },
    
    // 显示派单对话框
    showAssignModal(order) {
      this.currentOrder = order
      this.assignModalVisible = true
      this.assignForm.resetFields()
    },
    
    // 处理派单
    handleAssign() {
      this.assignForm.validateFields((err, values) => {
        if (!err) {
          this.assignLoading = true
          const selectedUser = this.userList.find(user => user.id === values.handlerId)
          
          postAction('/workOrder/assign', {
            id: this.currentOrder.id,
            handlerId: values.handlerId,
            handlerName: selectedUser ? selectedUser.username : ''
          }).then(res => {
            if (res.code === 200) {
              this.$message.success('派单成功')
              this.assignModalVisible = false
              this.loadKanbanData()
            } else {
              this.$message.error('派单失败：' + res.data.message)
            }
          }).catch(err => {
            this.$message.error('派单失败')
            console.error(err)
          }).finally(() => {
            this.assignLoading = false
          })
        }
      })
    },
    
    // 处理人选择变化
    onHandlerChange(value) {
      // 可以在这里添加额外的逻辑
    },
    
    // 显示完工对话框
    showCompleteModal(order) {
      this.currentOrder = order
      this.completeModalVisible = true
      this.completeForm.resetFields()
      this.fileList = []
    },
    
    // 处理完工
    handleComplete() {
      this.completeForm.validateFields((err, values) => {
        if (!err) {
          this.completeLoading = true
          
          // 处理上传的图片
          const imageUrls = this.fileList.map(file => file.response ? file.response.url : file.url).filter(url => url)
          
          postAction('/workOrder/complete', {
            id: this.currentOrder.id,
            completeImages: JSON.stringify(imageUrls),
            qualityNotes: values.qualityNotes
          }).then(res => {
            if (res.code === 200) {
              this.$message.success('完工确认成功')
              this.completeModalVisible = false
              this.loadKanbanData()
            } else {
              this.$message.error('完工确认失败：' + res.data.message)
            }
          }).catch(err => {
            this.$message.error('完工确认失败')
            console.error(err)
          }).finally(() => {
            this.completeLoading = false
          })
        }
      })
    },
    
    // 文件上传前处理
    beforeUpload(file) {
      const isImage = file.type.indexOf('image/') === 0
      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      const isLt20M = file.size / 1024 / 1024 < 20
      if (!isLt20M) {
        this.$message.error('图片大小不能超过20MB!')
        return false
      }
      return true
    },
    
    // 文件上传变化处理
    handleUploadChange({ fileList }) {
      this.fileList = fileList
    },
    
    // 查看完工图片
    viewCompleteImages(order) {
      try {
        this.previewImages = JSON.parse(order.completeImages || '[]')
        this.imagePreviewVisible = true
      } catch (e) {
        this.$message.error('图片数据格式错误')
      }
    },
    
    // 获取工单类型颜色
    getWorkTypeColor(workType) {
      const colorMap = {
        'CLOISONNE': 'blue',
        'ACCESSORY': 'green',
        'POST_PROCESS': 'orange'
      }
      return colorMap[workType] || 'default'
    },
    
    // 获取工单类型名称
    getWorkTypeName(workType) {
      const nameMap = {
        'CLOISONNE': '掐丝点蓝',
        'ACCESSORY': '配饰制作',
        'POST_PROCESS': '后工'
      }
      return nameMap[workType] || workType
    },
    
    // 格式化日期
    formatDate(dateStr) {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.chongzuo-kanban {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.kanban-container {
  display: flex;
  gap: 24px;
  overflow-x: auto;
  padding-bottom: 16px;
}

.kanban-column {
  flex: 1;
  min-width: 320px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.column-header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.column-header.pending {
  color: #f5222d;
  background: #fff2f0;
}

.column-header.in-progress {
  color: #1890ff;
  background: #f0f8ff;
}

.column-header.completed {
  color: #52c41a;
  background: #f6ffed;
}

.column-content {
  padding: 16px;
  max-height: 70vh;
  overflow-y: auto;
}

.work-order-card {
  margin-bottom: 12px;
}

.work-order-card:last-child {
  margin-bottom: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 600;
  color: #1890ff;
}

.card-content p {
  margin: 4px 0;
  font-size: 12px;
  color: #666;
}

.card-actions {
  margin-top: 12px;
  text-align: center;
}

.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.preview-image {
  max-width: 200px;
  max-height: 200px;
  object-fit: cover;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .kanban-container {
    flex-direction: column;
  }
  
  .kanban-column {
    min-width: auto;
  }
}
</style>
