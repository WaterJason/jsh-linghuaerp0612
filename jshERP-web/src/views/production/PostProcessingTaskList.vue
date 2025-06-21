<template>
  <div class="post-processing-task-list">
    <a-card :bordered="false" title="后工任务列表">
      <!-- 查询条件 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="工单号">
                <a-input v-model="queryParam.workOrderNo" placeholder="请输入工单号" />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                  <a-select-option value="PENDING">待认领</a-select-option>
                  <a-select-option value="IN_PROGRESS">进行中</a-select-option>
                  <a-select-option value="COMPLETED">已完成</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <span class="table-page-search-submitButtons">
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button style="margin-left: 8px" @click="searchReset" icon="reload">重置</a-button>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 操作按钮 -->
      <div class="table-operator">
        <a-button type="primary" icon="reload" @click="loadData">刷新</a-button>
      </div>

      <!-- 数据表格 -->
      <a-table
        ref="table"
        size="middle"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        @change="handleTableChange"
      >
        <span slot="action" slot-scope="text, record">
          <a-button 
            v-if="record.status === 'PENDING'" 
            type="primary" 
            size="small" 
            @click="handleClaim(record)"
            v-has="'claim'"
          >
            认领
          </a-button>
          <a-button 
            v-if="record.status === 'IN_PROGRESS' && record.handlerId === currentUserId" 
            type="primary" 
            size="small" 
            @click="handleComplete(record)"
            v-has="'complete'"
          >
            完工
          </a-button>
          <a-button 
            v-if="record.status === 'COMPLETED'" 
            type="default" 
            size="small" 
            @click="handleViewImages(record)"
          >
            查看图片
          </a-button>
        </span>

        <span slot="status" slot-scope="text">
          <a-tag :color="getStatusColor(text)">{{ getStatusText(text) }}</a-tag>
        </span>

        <span slot="workType" slot-scope="text">
          <a-tag :color="getWorkTypeColor(text)">{{ getWorkTypeName(text) }}</a-tag>
        </span>
      </a-table>
    </a-card>

    <!-- 认领确认对话框 -->
    <a-modal
      title="认领任务"
      :visible="claimModalVisible"
      @ok="handleClaimConfirm"
      @cancel="claimModalVisible = false"
      :confirmLoading="claimLoading"
    >
      <p>确定要认领工单 <strong>{{ currentTask.workOrderNo }}</strong> 吗？</p>
      <p>认领后该任务将分配给您处理。</p>
    </a-modal>

    <!-- 完工对话框 -->
    <a-modal
      title="完工确认"
      :visible="completeModalVisible"
      @ok="handleCompleteConfirm"
      @cancel="completeModalVisible = false"
      :confirmLoading="completeLoading"
      width="600px"
    >
      <a-form :form="completeForm" layout="vertical">
        <a-form-item label="工单号">
          <a-input :value="currentTask.workOrderNo" disabled />
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
  name: 'PostProcessingTaskList',
  data() {
    return {
      // 查询参数
      queryParam: {
        workType: 'POST_PROCESS' // 只显示后工类型的工单
      },
      // 表格数据
      dataSource: [],
      // 表格列配置
      columns: [
        {
          title: '工单号',
          dataIndex: 'workOrderNo',
          width: 150,
          fixed: 'left'
        },
        {
          title: '生产订单ID',
          dataIndex: 'productionOrderId',
          width: 120
        },
        {
          title: '工单类型',
          dataIndex: 'workType',
          width: 120,
          scopedSlots: { customRender: 'workType' }
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '处理人',
          dataIndex: 'handlerName',
          width: 100,
          customRender: (text) => text || '未分配'
        },
        {
          title: '预计工时',
          dataIndex: 'estimatedHours',
          width: 100,
          customRender: (text) => text ? `${text}小时` : '-'
        },
        {
          title: '实际工时',
          dataIndex: 'actualHours',
          width: 100,
          customRender: (text) => text ? `${text}小时` : '-'
        },
        {
          title: '开始时间',
          dataIndex: 'startTime',
          width: 150,
          customRender: (text) => {
            return text ? this.$moment(text).format('YYYY-MM-DD HH:mm') : '-'
          }
        },
        {
          title: '完成时间',
          dataIndex: 'completeTime',
          width: 150,
          customRender: (text) => {
            return text ? this.$moment(text).format('YYYY-MM-DD HH:mm') : '-'
          }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: 120,
          fixed: 'right',
          scopedSlots: { customRender: 'action' }
        }
      ],
      // 分页配置
      ipagination: {
        current: 1,
        pageSize: 10,
        pageSizeOptions: ['10', '20', '30'],
        showTotal: (total, range) => {
          return range[0] + '-' + range[1] + ' 共' + total + '条'
        },
        showQuickJumper: true,
        showSizeChanger: true,
        total: 0
      },
      // 加载状态
      loading: false,
      
      // 认领相关
      claimModalVisible: false,
      claimLoading: false,
      
      // 完工相关
      completeModalVisible: false,
      completeLoading: false,
      completeForm: this.$form.createForm(this),
      fileList: [],
      
      // 图片预览
      imagePreviewVisible: false,
      previewImages: [],
      
      // 当前操作的任务
      currentTask: {},
      
      // 当前用户ID
      currentUserId: null
    }
  },
  
  mounted() {
    this.getCurrentUser()
    this.loadData()
  },
  
  methods: {
    // 获取当前用户信息
    getCurrentUser() {
      // 从localStorage或Vuex中获取当前用户ID
      const userInfo = this.$store.getters.userInfo
      this.currentUserId = userInfo ? userInfo.id : null
    },
    
    // 加载数据
    loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1
      }
      const params = Object.assign({}, this.queryParam, this.isorter)
      params.pageNo = this.ipagination.current
      params.pageSize = this.ipagination.pageSize
      
      this.loading = true
      getAction('/workOrder/list', params).then(res => {
        if (res.code === 200) {
          this.dataSource = res.data.rows
          this.ipagination.total = res.data.total
        } else {
          this.$message.error('查询失败：' + res.data.message)
        }
      }).catch(err => {
        this.$message.error('查询失败')
        console.error(err)
      }).finally(() => {
        this.loading = false
      })
    },
    
    // 查询
    searchQuery() {
      this.loadData(1)
    },
    
    // 重置
    searchReset() {
      this.queryParam = {
        workType: 'POST_PROCESS'
      }
      this.loadData(1)
    },
    
    // 认领任务
    handleClaim(record) {
      this.currentTask = record
      this.claimModalVisible = true
    },
    
    // 确认认领
    handleClaimConfirm() {
      this.claimLoading = true
      const userInfo = this.$store.getters.userInfo
      
      postAction('/workOrder/assign', {
        id: this.currentTask.id,
        handlerId: userInfo.id,
        handlerName: userInfo.username
      }).then(res => {
        if (res.code === 200) {
          this.$message.success('认领成功')
          this.claimModalVisible = false
          this.loadData()
        } else {
          this.$message.error('认领失败：' + res.data.message)
        }
      }).catch(err => {
        this.$message.error('认领失败')
        console.error(err)
      }).finally(() => {
        this.claimLoading = false
      })
    },
    
    // 完工
    handleComplete(record) {
      this.currentTask = record
      this.completeModalVisible = true
      this.completeForm.resetFields()
      this.fileList = []
    },
    
    // 确认完工
    handleCompleteConfirm() {
      this.completeForm.validateFields((err, values) => {
        if (!err) {
          this.completeLoading = true
          
          // 处理上传的图片
          const imageUrls = this.fileList.map(file => file.response ? file.response.url : file.url).filter(url => url)
          
          postAction('/workOrder/complete', {
            id: this.currentTask.id,
            completeImages: JSON.stringify(imageUrls),
            qualityNotes: values.qualityNotes
          }).then(res => {
            if (res.code === 200) {
              this.$message.success('完工确认成功')
              this.completeModalVisible = false
              this.loadData()
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
    
    // 查看图片
    handleViewImages(record) {
      try {
        this.previewImages = JSON.parse(record.completeImages || '[]')
        this.imagePreviewVisible = true
      } catch (e) {
        this.$message.error('图片数据格式错误')
      }
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
    
    // 表格变化
    handleTableChange(pagination, filters, sorter) {
      this.ipagination = pagination
      this.isorter = sorter
      this.loadData()
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'PENDING': 'orange',
        'IN_PROGRESS': 'blue',
        'COMPLETED': 'green'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'PENDING': '待认领',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成'
      }
      return textMap[status] || status
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
    }
  }
}
</script>

<style scoped>
.post-processing-task-list {
  padding: 24px;
}

.table-page-search-wrapper {
  margin-bottom: 16px;
}

.table-operator {
  margin-bottom: 16px;
}

.table-page-search-submitButtons {
  display: block;
  margin-bottom: 24px;
  white-space: nowrap;
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
</style>
