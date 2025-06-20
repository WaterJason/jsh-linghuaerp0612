# Week 7: 前端界面开发UI设计与实现文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-17
- **项目阶段**: 第一阶段 - 生产制作管理模块
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第2.2节前端组件化架构

---

## 概述

本文档为Week 7的前端界面开发提供详细的UI设计与实施指导。主要实现PC端管理界面、生产看板、报工记录界面、物流追踪界面等核心功能，为管理人员提供完整的生产管理可视化工具。

---

## 生产管理主界面 (2天)

### 1. ProductionOrderList.vue - 工单列表页面

**文件路径**: `jshERP-web/src/views/production/ProductionOrderList.vue`

```vue
<template>
  <a-card :bordered="false">
    <!-- 页面标题和描述 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <!-- 工单号搜索 -->
          <a-col :md="6" :sm="24">
            <a-form-item label="工单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-model="queryParam.orderNo" placeholder="请输入工单号" />
            </a-form-item>
          </a-col>
          
          <!-- 产品名称搜索 -->
          <a-col :md="6" :sm="24">
            <a-form-item label="产品名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-model="queryParam.productName" placeholder="请输入产品名称" />
            </a-form-item>
          </a-col>
          
          <!-- 工单状态筛选 -->
          <a-col :md="6" :sm="24">
            <a-form-item label="工单状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="0">待分配</a-select-option>
                <a-select-option value="1">进行中</a-select-option>
                <a-select-option value="2">已完工</a-select-option>
                <a-select-option value="3">已交付</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <!-- 制作人员筛选 -->
          <a-col :md="6" :sm="24">
            <a-form-item label="制作人员" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.workerId" placeholder="请选择制作人员" allowClear>
                <a-select-option v-for="worker in workerList" :key="worker.id" :value="worker.id">
                  {{ worker.username }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <!-- 计划日期筛选 -->
          <a-col :md="8" :sm="24">
            <a-form-item label="计划日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-range-picker 
                v-model="queryParam.dateRange"
                format="YYYY-MM-DD"
                placeholder="['开始日期', '结束日期']" />
            </a-form-item>
          </a-col>
          
          <!-- 搜索按钮 -->
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="searchReset" icon="reload">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button v-has="'production:order:add'" type="primary" icon="plus" @click="handleAdd">创建工单</a-button>
      <a-button v-has="'production:order:import'" type="default" icon="import" @click="handleImport">批量导入</a-button>
      <a-button v-has="'production:order:export'" type="default" icon="export" @click="handleExport">导出数据</a-button>
      
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchAssignWorker">
            <a-icon type="user" />批量分配人员
          </a-menu-item>
          <a-menu-item key="2" @click="batchUpdateStatus">
            <a-icon type="edit" />批量更新状态
          </a-menu-item>
          <a-menu-item key="3" @click="batchDelete">
            <a-icon type="delete" />批量删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
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
      :rowSelection="rowSelection"
      @change="handleTableChange"
      :scroll="{ x: 1500 }">
      
      <!-- 工单号列 -->
      <template slot="orderNo" slot-scope="text, record">
        <a @click="handleDetail(record)">{{ text }}</a>
      </template>
      
      <!-- 状态列 -->
      <template slot="status" slot-scope="text, record">
        <a-tag :color="getStatusColor(text)">
          {{ getStatusText(text) }}
        </a-tag>
      </template>
      
      <!-- 优先级列 -->
      <template slot="priority" slot-scope="text, record">
        <a-icon 
          type="flag" 
          :style="{ color: getPriorityColor(text), fontSize: '16px' }" 
          :title="getPriorityText(text)" />
        {{ getPriorityText(text) }}
      </template>
      
      <!-- 进度列 -->
      <template slot="progress" slot-scope="text, record">
        <a-progress 
          :percent="record.completionRate || 0" 
          size="small"
          :status="getProgressStatus(record.status)"
          :format="percent => `${percent}%`" />
      </template>
      
      <!-- 成本列 -->
      <template slot="cost" slot-scope="text, record">
        <div>
          <div>物料: ¥{{ (record.materialCost || 0).toFixed(2) }}</div>
          <div>人工: ¥{{ (record.laborCost || 0).toFixed(2) }}</div>
          <div style="font-weight: bold;">总计: ¥{{ ((record.materialCost || 0) + (record.laborCost || 0)).toFixed(2) }}</div>
        </div>
      </template>
      
      <!-- 操作列 -->
      <template slot="action" slot-scope="text, record">
        <div class="action-buttons">
          <a-button v-has="'production:order:view'" type="link" size="small" @click="handleDetail(record)">
            <a-icon type="eye" />详情
          </a-button>
          
          <a-button v-has="'production:order:edit'" type="link" size="small" @click="handleEdit(record)">
            <a-icon type="edit" />编辑
          </a-button>
          
          <a-dropdown v-if="record.status === '0' || record.status === '1'">
            <a-menu slot="overlay">
              <a-menu-item v-if="record.status === '0'" key="assign" @click="assignWorker(record)">
                <a-icon type="user" />分配人员
              </a-menu-item>
              <a-menu-item v-if="record.status === '1'" key="complete" @click="markComplete(record)">
                <a-icon type="check" />标记完工
              </a-menu-item>
              <a-menu-item key="logistics" @click="viewLogistics(record)">
                <a-icon type="car" />物流追踪
              </a-menu-item>
            </a-menu>
            <a-button type="link" size="small">
              更多 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          
          <a-popconfirm
            v-has="'production:order:delete'"
            title="确定删除这个工单吗？"
            @confirm="handleDelete(record)">
            <a-button type="link" size="small" style="color: #ff4d4f;">
              <a-icon type="delete" />删除
            </a-button>
          </a-popconfirm>
        </div>
      </template>
    </a-table>

    <!-- 工单详情/编辑模态框 -->
    <production-order-modal ref="modalForm" @ok="modalFormOk" />
    
    <!-- 分配人员模态框 -->
    <worker-assign-modal ref="workerAssignModal" @ok="modalFormOk" />
    
    <!-- 批量操作模态框 -->
    <batch-operation-modal ref="batchOperationModal" @ok="modalFormOk" />
  </a-card>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import ProductionOrderModal from './modules/ProductionOrderModal'
import WorkerAssignModal from './modules/WorkerAssignModal'
import BatchOperationModal from './modules/BatchOperationModal'
import { getAction } from '@/api/manage'

export default {
  name: "ProductionOrderList",
  mixins: [JeecgListMixin],
  components: {
    ProductionOrderModal,
    WorkerAssignModal,
    BatchOperationModal
  },
  
  data() {
    return {
      // 查询参数
      queryParam: {
        orderNo: '',
        productName: '',
        status: '',
        workerId: '',
        dateRange: []
      },
      
      // 制作人员列表
      workerList: [],
      
      // 表格列定义
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: (text, record, index) => parseInt(index) + 1
        },
        {
          title: '工单号',
          align: 'center',
          dataIndex: 'orderNo',
          width: 120,
          fixed: 'left',
          scopedSlots: { customRender: 'orderNo' }
        },
        {
          title: '产品信息',
          align: 'center',
          dataIndex: 'productName',
          width: 200,
          ellipsis: true,
          customRender: (text, record) => {
            return `${text}${record.productSpec ? ' (' + record.productSpec + ')' : ''}`
          }
        },
        {
          title: '数量',
          align: 'center',
          dataIndex: 'quantity',
          width: 100,
          customRender: (text, record) => `${text} ${record.unit || ''}`
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '优先级',
          align: 'center',
          dataIndex: 'priority',
          width: 100,
          scopedSlots: { customRender: 'priority' }
        },
        {
          title: '制作人员',
          align: 'center',
          dataIndex: 'workerName',
          width: 120,
          customRender: (text) => text || '未分配'
        },
        {
          title: '计划工期',
          align: 'center',
          width: 180,
          customRender: (text, record) => {
            return `${this.formatDate(record.planStartDate)} ~ ${this.formatDate(record.planFinishDate)}`
          }
        },
        {
          title: '完成进度',
          align: 'center',
          dataIndex: 'completionRate',
          width: 120,
          scopedSlots: { customRender: 'progress' }
        },
        {
          title: '成本分析',
          align: 'center',
          width: 150,
          scopedSlots: { customRender: 'cost' }
        },
        {
          title: '报工次数',
          align: 'center',
          dataIndex: 'reportCount',
          width: 100,
          customRender: (text) => text || 0
        },
        {
          title: '创建时间',
          align: 'center',
          dataIndex: 'createTimeStr',
          width: 150
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: 200,
          fixed: 'right',
          scopedSlots: { customRender: 'action' }
        }
      ],
      
      // API配置
      url: {
        list: "/production/orders/list",
        delete: "/production/orders/delete",
        deleteBatch: "/production/orders/deleteBatch",
        exportXls: "/production/orders/exportXls",
        importExcel: "/production/orders/importExcel"
      }
    }
  },
  
  created() {
    this.loadWorkerList()
  },
  
  methods: {
    // 加载制作人员列表
    async loadWorkerList() {
      try {
        const response = await getAction('/sys/user/queryByRole', { roleCode: 'production_worker' })
        if (response.success) {
          this.workerList = response.result
        }
      } catch (error) {
        console.error('加载制作人员列表失败:', error)
      }
    },
    
    // 搜索查询
    searchQuery() {
      // 处理日期范围
      if (this.queryParam.dateRange && this.queryParam.dateRange.length === 2) {
        this.queryParam.startDate = this.queryParam.dateRange[0].format('YYYY-MM-DD')
        this.queryParam.endDate = this.queryParam.dateRange[1].format('YYYY-MM-DD')
      } else {
        this.queryParam.startDate = ''
        this.queryParam.endDate = ''
      }
      
      this.loadData(1)
    },
    
    // 重置搜索
    searchReset() {
      this.queryParam = {
        orderNo: '',
        productName: '',
        status: '',
        workerId: '',
        dateRange: []
      }
      this.loadData()
    },
    
    // 创建工单
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = "创建生产工单"
    },
    
    // 编辑工单
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = "编辑生产工单"
    },
    
    // 查看详情
    handleDetail(record) {
      this.$refs.modalForm.detail(record)
      this.$refs.modalForm.title = "工单详情"
    },
    
    // 分配制作人员
    assignWorker(record) {
      this.$refs.workerAssignModal.show(record)
    },
    
    // 标记完工
    markComplete(record) {
      this.$confirm({
        title: '确认完工',
        content: `确定要将工单 ${record.orderNo} 标记为完工吗？`,
        onOk: async () => {
          try {
            const response = await this.$http.put(`/production/orders/${record.id}/status`, {
              status: '2'
            })
            if (response.success) {
              this.$message.success('工单状态更新成功')
              this.loadData()
            } else {
              this.$message.error('更新失败: ' + response.message)
            }
          } catch (error) {
            this.$message.error('更新失败')
          }
        }
      })
    },
    
    // 查看物流信息
    viewLogistics(record) {
      this.$router.push({
        path: '/production/logistics',
        query: { orderId: record.id }
      })
    },
    
    // 批量分配人员
    batchAssignWorker() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要分配的工单')
        return
      }
      this.$refs.batchOperationModal.showAssignWorker(this.selectedRowKeys)
    },
    
    // 批量更新状态
    batchUpdateStatus() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要更新的工单')
        return
      }
      this.$refs.batchOperationModal.showUpdateStatus(this.selectedRowKeys)
    },
    
    // 批量删除
    batchDelete() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要删除的工单')
        return
      }
      
      this.$confirm({
        title: '确认删除',
        content: `确定要删除选中的 ${this.selectedRowKeys.length} 个工单吗？`,
        onOk: () => {
          this.handleDeleteBatch()
        }
      })
    },
    
    // 导入数据
    handleImport() {
      // TODO: 实现导入功能
      this.$message.info('导入功能开发中...')
    },
    
    // 导出数据
    handleExport() {
      // TODO: 实现导出功能
      this.$message.info('导出功能开发中...')
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        '0': 'default',
        '1': 'processing',
        '2': 'success',
        '3': 'success'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        '0': '待分配',
        '1': '进行中',
        '2': '已完工',
        '3': '已交付'
      }
      return textMap[status] || '未知'
    },
    
    // 获取优先级颜色
    getPriorityColor(priority) {
      if (priority <= 2) return '#f5222d'
      if (priority <= 4) return '#fa8c16'
      if (priority <= 6) return '#faad14'
      return '#52c41a'
    },
    
    // 获取优先级文本
    getPriorityText(priority) {
      if (priority <= 2) return '高'
      if (priority <= 4) return '中高'
      if (priority <= 6) return '中'
      if (priority <= 8) return '中低'
      return '低'
    },
    
    // 获取进度状态
    getProgressStatus(status) {
      if (status === '2' || status === '3') return 'success'
      if (status === '1') return 'active'
      return 'normal'
    },
    
    // 格式化日期
    formatDate(date) {
      if (!date) return '-'
      return this.$moment(date).format('MM-DD')
    }
  }
}
</script>

<style lang="less" scoped>
.table-page-search-wrapper {
  .ant-form-item {
    display: flex;
    margin-bottom: 16px;
  }
  
  .table-page-search-submitButtons {
    display: flex;
    white-space: nowrap;
  }
}

.table-operator {
  margin-bottom: 16px;
  
  .ant-btn {
    margin-right: 8px;
  }
}

.action-buttons {
  .ant-btn {
    padding: 0;
    margin-right: 8px;
    
    &:last-child {
      margin-right: 0;
    }
  }
}

// 响应式布局调整
@media (max-width: 768px) {
  .ant-table {
    .ant-table-thead > tr > th,
    .ant-table-tbody > tr > td {
      padding: 8px 4px;
      font-size: 12px;
    }
  }
}
</style>
```

### 2. ProductionOrderModal.vue - 工单详情弹窗

**文件路径**: `jshERP-web/src/views/production/modules/ProductionOrderModal.vue`

```vue
<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :maskClosable="false">

    <a-spin :spinning="confirmLoading">
      <a-tabs v-model="activeTab" type="card">
        <!-- 基本信息 -->
        <a-tab-pane key="basic" tab="基本信息">
          <a-form-model ref="form" :model="model" :rules="validatorRules" layout="vertical">
            <a-row :gutter="24">
              <a-col :md="8" :sm="24">
                <a-form-model-item label="工单号" prop="orderNo">
                  <a-input v-model="model.orderNo" placeholder="系统自动生成" :disabled="isDetail || isEdit" />
                </a-form-model-item>
              </a-col>
              
              <a-col :md="8" :sm="24">
                <a-form-model-item label="关联销售订单">
                  <a-select v-model="model.saleOrderId" placeholder="请选择销售订单" :disabled="isDetail">
                    <a-select-option v-for="order in saleOrderList" :key="order.id" :value="order.id">
                      {{ order.number }} - {{ order.organName }}
                    </a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
              
              <a-col :md="8" :sm="24">
                <a-form-model-item label="优先级" prop="priority">
                  <a-select v-model="model.priority" placeholder="请选择优先级" :disabled="isDetail">
                    <a-select-option :value="1">最高</a-select-option>
                    <a-select-option :value="2">高</a-select-option>
                    <a-select-option :value="3">中高</a-select-option>
                    <a-select-option :value="4">中</a-select-option>
                    <a-select-option :value="5">中低</a-select-option>
                    <a-select-option :value="6">低</a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-model-item label="产品名称" prop="productName">
                  <a-input v-model="model.productName" placeholder="请输入产品名称" :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
              
              <a-col :md="12" :sm="24">
                <a-form-model-item label="产品规格">
                  <a-input v-model="model.productSpec" placeholder="请输入产品规格" :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :md="8" :sm="24">
                <a-form-model-item label="生产数量" prop="quantity">
                  <a-input-number 
                    v-model="model.quantity" 
                    placeholder="请输入数量" 
                    :min="0" 
                    :precision="2"
                    style="width: 100%"
                    :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
              
              <a-col :md="8" :sm="24">
                <a-form-model-item label="单位">
                  <a-input v-model="model.unit" placeholder="请输入单位" :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
              
              <a-col :md="8" :sm="24">
                <a-form-model-item label="制作人员">
                  <a-select v-model="model.workerId" placeholder="请选择制作人员" allowClear :disabled="isDetail">
                    <a-select-option v-for="worker in workerList" :key="worker.id" :value="worker.id">
                      {{ worker.username }}
                    </a-select-option>
                  </a-select>
                </a-form-model-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-model-item label="计划开工日期" prop="planStartDate">
                  <a-date-picker 
                    v-model="model.planStartDate" 
                    placeholder="请选择开工日期"
                    style="width: 100%"
                    :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
              
              <a-col :md="12" :sm="24">
                <a-form-model-item label="计划完工日期" prop="planFinishDate">
                  <a-date-picker 
                    v-model="model.planFinishDate" 
                    placeholder="请选择完工日期"
                    style="width: 100%"
                    :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
            </a-row>

            <a-row :gutter="24">
              <a-col :span="24">
                <a-form-model-item label="备注">
                  <a-textarea 
                    v-model="model.remark" 
                    placeholder="请输入备注信息"
                    :rows="3"
                    :disabled="isDetail" />
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </a-tab-pane>

        <!-- 物料清单 -->
        <a-tab-pane key="materials" tab="物料清单" v-if="isEdit || isDetail">
          <production-material-table 
            ref="materialTable"
            :production-order-id="model.id"
            :readonly="isDetail" />
        </a-tab-pane>

        <!-- 报工记录 -->
        <a-tab-pane key="reports" tab="报工记录" v-if="isDetail">
          <work-report-table 
            ref="reportTable"
            :production-order-id="model.id" />
        </a-tab-pane>

        <!-- 物流信息 -->
        <a-tab-pane key="logistics" tab="物流信息" v-if="isDetail && model.status >= '2'">
          <logistics-track-info 
            ref="logisticsInfo"
            :production-order-id="model.id" />
        </a-tab-pane>
      </a-tabs>
    </a-spin>

    <!-- 底部按钮 -->
    <template slot="footer">
      <a-button @click="handleCancel">取消</a-button>
      <a-button v-if="!isDetail" type="primary" :loading="confirmLoading" @click="handleOk">确定</a-button>
      <a-button v-if="isDetail && model.status === '0'" type="primary" @click="assignWorker">分配人员</a-button>
      <a-button v-if="isDetail && model.status === '1'" type="primary" @click="markComplete">标记完工</a-button>
    </template>
  </a-modal>
</template>

<script>
import { httpAction, getAction } from '@/api/manage'
import ProductionMaterialTable from './ProductionMaterialTable'
import WorkReportTable from './WorkReportTable'
import LogisticsTrackInfo from './LogisticsTrackInfo'
import moment from 'moment'

export default {
  name: 'ProductionOrderModal',
  components: {
    ProductionMaterialTable,
    WorkReportTable,
    LogisticsTrackInfo
  },
  
  data() {
    return {
      title: "操作",
      visible: false,
      model: {},
      confirmLoading: false,
      activeTab: 'basic',
      
      // 状态标识
      isDetail: false,
      isEdit: false,
      
      // 下拉选项数据
      saleOrderList: [],
      workerList: [],
      
      // 表单验证规则
      validatorRules: {
        productName: [
          { required: true, message: '请输入产品名称!' },
          { max: 100, message: '产品名称长度不能超过100字符!' }
        ],
        quantity: [
          { required: true, message: '请输入生产数量!' },
          { type: 'number', min: 0.01, message: '生产数量必须大于0!' }
        ],
        planStartDate: [
          { required: true, message: '请选择计划开工日期!' }
        ],
        planFinishDate: [
          { required: true, message: '请选择计划完工日期!' }
        ]
      },
      
      url: {
        add: "/production/orders/add",
        edit: "/production/orders/update"
      }
    }
  },
  
  created() {
    this.loadSaleOrderList()
    this.loadWorkerList()
  },
  
  methods: {
    // 新增
    add() {
      this.visible = true
      this.isDetail = false
      this.isEdit = false
      this.activeTab = 'basic'
      this.model = {
        priority: 5,
        planStartDate: moment().add(1, 'days'),
        planFinishDate: moment().add(7, 'days')
      }
    },
    
    // 编辑
    edit(record) {
      this.visible = true
      this.isDetail = false
      this.isEdit = true
      this.activeTab = 'basic'
      this.model = Object.assign({}, record)
      
      // 转换日期格式
      if (this.model.planStartDate) {
        this.model.planStartDate = moment(this.model.planStartDate)
      }
      if (this.model.planFinishDate) {
        this.model.planFinishDate = moment(this.model.planFinishDate)
      }
    },
    
    // 详情
    detail(record) {
      this.visible = true
      this.isDetail = true
      this.isEdit = false
      this.activeTab = 'basic'
      this.model = Object.assign({}, record)
    },
    
    // 关闭
    close() {
      this.$emit('close')
      this.visible = false
      this.model = {}
      this.activeTab = 'basic'
    },
    
    // 确定
    handleOk() {
      if (this.isDetail) {
        this.close()
        return
      }
      
      const that = this
      this.$refs.form.validate(valid => {
        if (valid) {
          that.confirmLoading = true
          
          // 处理日期格式
          const submitData = Object.assign({}, this.model)
          if (submitData.planStartDate) {
            submitData.planStartDate = submitData.planStartDate.format('YYYY-MM-DD')
          }
          if (submitData.planFinishDate) {
            submitData.planFinishDate = submitData.planFinishDate.format('YYYY-MM-DD')
          }
          
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl = this.url.add
            method = 'post'
          } else {
            httpurl = this.url.edit
            method = 'put'
          }
          
          httpAction(httpurl, submitData, method).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
              that.close()
            } else {
              that.$message.warning(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
          })
        }
      })
    },
    
    // 取消
    handleCancel() {
      this.close()
    },
    
    // 分配人员
    assignWorker() {
      // TODO: 实现分配人员功能
      this.$message.info('分配人员功能开发中...')
    },
    
    // 标记完工
    markComplete() {
      // TODO: 实现标记完工功能
      this.$message.info('标记完工功能开发中...')
    },
    
    // 加载销售订单列表
    async loadSaleOrderList() {
      try {
        const response = await getAction('/depot/depotHead/list', { type: '销售', status: '1' })
        if (response.success) {
          this.saleOrderList = response.result.records || response.result
        }
      } catch (error) {
        console.error('加载销售订单列表失败:', error)
      }
    },
    
    // 加载制作人员列表
    async loadWorkerList() {
      try {
        const response = await getAction('/sys/user/queryByRole', { roleCode: 'production_worker' })
        if (response.success) {
          this.workerList = response.result
        }
      } catch (error) {
        console.error('加载制作人员列表失败:', error)
      }
    }
  }
}
</script>

<style lang="less" scoped>
.ant-form-item {
  margin-bottom: 16px;
}

.ant-tabs-content {
  padding-top: 16px;
}
</style>
```

---

## 生产看板界面 (1.5天)

### 3. ProductionDashboard.vue - 生产看板

**文件路径**: `jshERP-web/src/views/production/ProductionDashboard.vue`

```vue
<template>
  <div class="production-dashboard">
    <!-- 顶部统计卡片 -->
    <a-row :gutter="16" class="dashboard-cards">
      <a-col :xs="24" :sm="12" :md="6" v-for="(card, index) in statCards" :key="index">
        <a-card :bordered="false" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <a-icon :type="card.icon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
              <div class="stat-trend" :class="{ 'trend-up': card.trend > 0, 'trend-down': card.trend < 0 }">
                <a-icon :type="card.trend > 0 ? 'arrow-up' : 'arrow-down'" />
                {{ Math.abs(card.trend) }}%
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 主要看板内容 -->
    <a-row :gutter="16" class="dashboard-content">
      <!-- 左侧列 -->
      <a-col :xs="24" :lg="16">
        <!-- 工单状态统计图表 -->
        <a-card title="工单状态分布" :bordered="false" class="chart-card">
          <template #extra>
            <a-radio-group v-model="chartTimeRange" @change="updateCharts">
              <a-radio-button value="today">今日</a-radio-button>
              <a-radio-button value="week">本周</a-radio-button>
              <a-radio-button value="month">本月</a-radio-button>
            </a-radio-group>
          </template>
          
          <div class="chart-container">
            <div ref="statusChart" class="chart" style="height: 300px;"></div>
          </div>
        </a-card>

        <!-- 生产进度趋势 -->
        <a-card title="生产进度趋势" :bordered="false" class="chart-card">
          <div class="chart-container">
            <div ref="progressChart" class="chart" style="height: 300px;"></div>
          </div>
        </a-card>

        <!-- 实时工单列表 -->
        <a-card title="进行中的工单" :bordered="false" class="real-time-orders">
          <template #extra>
            <a-button type="link" @click="refreshRealTimeData">
              <a-icon type="reload" />刷新
            </a-button>
          </template>
          
          <a-list :dataSource="activeOrders" :loading="loading">
            <a-list-item v-for="order in activeOrders" :key="order.id" slot-scope="order">
              <a-list-item-meta>
                <template #title>
                  <a @click="viewOrderDetail(order)">{{ order.orderNo }} - {{ order.productName }}</a>
                </template>
                <template #description>
                  制作人员: {{ order.workerName }} | 计划完工: {{ formatDate(order.planFinishDate) }}
                </template>
                <template #avatar>
                  <a-avatar :style="{ backgroundColor: getStatusColor(order.status) }">
                    {{ getStatusText(order.status) }}
                  </a-avatar>
                </template>
              </a-list-item-meta>
              
              <div class="order-progress">
                <a-progress 
                  :percent="order.completionRate || 0" 
                  size="small"
                  :status="getProgressStatus(order)" />
                <div class="progress-text">{{ order.completionRate || 0 }}%</div>
              </div>
            </a-list-item>
          </a-list>
        </a-card>
      </a-col>

      <!-- 右侧列 -->
      <a-col :xs="24" :lg="8">
        <!-- 任务分配面板 -->
        <a-card title="任务分配" :bordered="false" class="task-assignment">
          <template #extra>
            <a-button type="primary" size="small" @click="showAssignModal">
              <a-icon type="plus" />分配任务
            </a-button>
          </template>
          
          <div class="worker-list">
            <div v-for="worker in workerStats" :key="worker.id" class="worker-item">
              <div class="worker-header">
                <a-avatar :src="worker.avatar" :size="32">{{ worker.username.charAt(0) }}</a-avatar>
                <div class="worker-info">
                  <div class="worker-name">{{ worker.username }}</div>
                  <div class="worker-status" :class="getWorkerStatusClass(worker.status)">
                    {{ getWorkerStatusText(worker.status) }}
                  </div>
                </div>
                <div class="worker-load">
                  <a-progress 
                    type="circle" 
                    :percent="worker.workload" 
                    :width="40"
                    :strokeColor="getWorkloadColor(worker.workload)" />
                </div>
              </div>
              
              <div class="worker-orders">
                <div v-for="order in worker.orders.slice(0, 2)" :key="order.id" class="order-item">
                  <span class="order-name">{{ order.productName }}</span>
                  <span class="order-progress">{{ order.completionRate || 0 }}%</span>
                </div>
                <div v-if="worker.orders.length > 2" class="more-orders">
                  还有 {{ worker.orders.length - 2 }} 个工单...
                </div>
              </div>
            </div>
          </div>
        </a-card>

        <!-- 质量统计 -->
        <a-card title="质量统计" :bordered="false" class="quality-stats">
          <div class="quality-metrics">
            <div class="metric-item">
              <div class="metric-value">{{ qualityStats.qualityRate }}%</div>
              <div class="metric-label">合格率</div>
              <div class="metric-trend trend-up">
                <a-icon type="arrow-up" />2.3%
              </div>
            </div>
            
            <div class="metric-item">
              <div class="metric-value">{{ qualityStats.reworkRate }}%</div>
              <div class="metric-label">返工率</div>
              <div class="metric-trend trend-down">
                <a-icon type="arrow-down" />0.5%
              </div>
            </div>
            
            <div class="metric-item">
              <div class="metric-value">{{ qualityStats.onTimeRate }}%</div>
              <div class="metric-label">按时完工率</div>
              <div class="metric-trend trend-up">
                <a-icon type="arrow-up" />1.2%
              </div>
            </div>
          </div>
          
          <div class="quality-chart">
            <div ref="qualityChart" style="height: 200px;"></div>
          </div>
        </a-card>

        <!-- 设备状态 -->
        <a-card title="设备状态" :bordered="false" class="equipment-status">
          <div class="equipment-list">
            <div v-for="equipment in equipmentList" :key="equipment.id" class="equipment-item">
              <div class="equipment-info">
                <div class="equipment-name">{{ equipment.name }}</div>
                <div class="equipment-location">{{ equipment.location }}</div>
              </div>
              <div class="equipment-status" :class="equipment.status">
                <a-badge :status="getEquipmentBadgeStatus(equipment.status)" :text="equipment.statusText" />
              </div>
            </div>
          </div>
        </a-card>
      </a-col>
    </a-row>

    <!-- 任务分配模态框 -->
    <task-assign-modal ref="taskAssignModal" @ok="handleAssignOk" />
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getAction } from '@/api/manage'
import TaskAssignModal from './modules/TaskAssignModal'

export default {
  name: 'ProductionDashboard',
  components: {
    TaskAssignModal
  },
  
  data() {
    return {
      loading: false,
      chartTimeRange: 'today',
      
      // 统计卡片数据
      statCards: [
        {
          label: '总工单数',
          value: 156,
          icon: 'file-text',
          color: '#1890ff',
          trend: 12.5
        },
        {
          label: '进行中',
          value: 42,
          icon: 'clock-circle',
          color: '#52c41a',
          trend: 5.2
        },
        {
          label: '已完工',
          value: 98,
          icon: 'check-circle',
          color: '#faad14',
          trend: 8.7
        },
        {
          label: '延期工单',
          value: 6,
          icon: 'exclamation-circle',
          color: '#f5222d',
          trend: -2.1
        }
      ],
      
      // 实时工单数据
      activeOrders: [],
      
      // 制作人员统计
      workerStats: [],
      
      // 质量统计
      qualityStats: {
        qualityRate: 96.8,
        reworkRate: 2.1,
        onTimeRate: 88.5
      },
      
      // 设备列表
      equipmentList: [
        { id: 1, name: '织机#1', location: '车间A', status: 'running', statusText: '运行中' },
        { id: 2, name: '织机#2', location: '车间A', status: 'idle', statusText: '空闲' },
        { id: 3, name: '绣花机#1', location: '车间B', status: 'maintenance', statusText: '维护中' },
        { id: 4, name: '绣花机#2', location: '车间B', status: 'running', statusText: '运行中' }
      ],
      
      // 图表实例
      statusChart: null,
      progressChart: null,
      qualityChart: null
    }
  },
  
  mounted() {
    this.initCharts()
    this.loadDashboardData()
    
    // 设置定时刷新
    this.refreshInterval = setInterval(() => {
      this.loadRealTimeData()
    }, 30000) // 30秒刷新一次
  },
  
  beforeDestroy() {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval)
    }
    
    // 销毁图表实例
    if (this.statusChart) {
      this.statusChart.dispose()
    }
    if (this.progressChart) {
      this.progressChart.dispose()
    }
    if (this.qualityChart) {
      this.qualityChart.dispose()
    }
  },
  
  methods: {
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.initStatusChart()
        this.initProgressChart()
        this.initQualityChart()
      })
    },
    
    // 初始化状态分布图表
    initStatusChart() {
      this.statusChart = echarts.init(this.$refs.statusChart)
      
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 10,
          data: ['待分配', '进行中', '已完工', '已交付']
        },
        series: [
          {
            name: '工单状态',
            type: 'pie',
            radius: ['50%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: 16, name: '待分配', itemStyle: { color: '#d9d9d9' } },
              { value: 42, name: '进行中', itemStyle: { color: '#1890ff' } },
              { value: 78, name: '已完工', itemStyle: { color: '#52c41a' } },
              { value: 20, name: '已交付', itemStyle: { color: '#faad14' } }
            ]
          }
        ]
      }
      
      this.statusChart.setOption(option)
    },
    
    // 初始化进度趋势图表
    initProgressChart() {
      this.progressChart = echarts.init(this.$refs.progressChart)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['计划完工', '实际完工']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        },
        yAxis: {
          type: 'value',
          name: '工单数量'
        },
        series: [
          {
            name: '计划完工',
            type: 'line',
            stack: '总量',
            data: [12, 15, 18, 14, 16, 10, 8],
            itemStyle: { color: '#1890ff' }
          },
          {
            name: '实际完工',
            type: 'line',
            stack: '总量',
            data: [10, 14, 16, 12, 14, 8, 6],
            itemStyle: { color: '#52c41a' }
          }
        ]
      }
      
      this.progressChart.setOption(option)
    },
    
    // 初始化质量图表
    initQualityChart() {
      this.qualityChart = echarts.init(this.$refs.qualityChart)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['合格', '不合格', '返工']
        },
        yAxis: {
          type: 'value',
          name: '数量'
        },
        series: [
          {
            name: '产品质量',
            type: 'bar',
            data: [
              { value: 156, itemStyle: { color: '#52c41a' } },
              { value: 5, itemStyle: { color: '#f5222d' } },
              { value: 3, itemStyle: { color: '#faad14' } }
            ]
          }
        ]
      }
      
      this.qualityChart.setOption(option)
    },
    
    // 加载看板数据
    async loadDashboardData() {
      try {
        this.loading = true
        
        // 并发加载各种数据
        const [statsRes, ordersRes, workersRes] = await Promise.all([
          getAction('/production/dashboard/stats'),
          getAction('/production/dashboard/active-orders'),
          getAction('/production/dashboard/worker-stats')
        ])
        
        if (statsRes.success) {
          this.updateStatCards(statsRes.result)
        }
        
        if (ordersRes.success) {
          this.activeOrders = ordersRes.result
        }
        
        if (workersRes.success) {
          this.workerStats = workersRes.result
        }
        
      } catch (error) {
        console.error('加载看板数据失败:', error)
        this.$message.error('加载看板数据失败')
      } finally {
        this.loading = false
      }
    },
    
    // 加载实时数据
    async loadRealTimeData() {
      try {
        const response = await getAction('/production/dashboard/real-time')
        if (response.success) {
          this.activeOrders = response.result.activeOrders
          this.updateStatCards(response.result.stats)
        }
      } catch (error) {
        console.error('刷新实时数据失败:', error)
      }
    },
    
    // 更新统计卡片
    updateStatCards(stats) {
      this.statCards.forEach((card, index) => {
        if (stats[index]) {
          card.value = stats[index].value
          card.trend = stats[index].trend
        }
      })
    },
    
    // 更新图表
    updateCharts() {
      // TODO: 根据时间范围更新图表数据
      this.loadDashboardData()
    },
    
    // 刷新实时数据
    refreshRealTimeData() {
      this.loadRealTimeData()
    },
    
    // 查看工单详情
    viewOrderDetail(order) {
      this.$router.push({
        path: '/production/orders',
        query: { id: order.id }
      })
    },
    
    // 显示分配任务模态框
    showAssignModal() {
      this.$refs.taskAssignModal.show()
    },
    
    // 处理任务分配成功
    handleAssignOk() {
      this.loadDashboardData()
    },
    
    // 工具方法
    getStatusColor(status) {
      const colorMap = {
        '0': '#d9d9d9',
        '1': '#1890ff',
        '2': '#52c41a',
        '3': '#faad14'
      }
      return colorMap[status] || '#d9d9d9'
    },
    
    getStatusText(status) {
      const textMap = {
        '0': '待分配',
        '1': '进行中',
        '2': '已完工',
        '3': '已交付'
      }
      return textMap[status] || '未知'
    },
    
    getProgressStatus(order) {
      if (order.status === '2' || order.status === '3') return 'success'
      if (order.completionRate > 80) return 'active'
      return 'normal'
    },
    
    getWorkerStatusClass(status) {
      return {
        'status-online': status === 'online',
        'status-busy': status === 'busy',
        'status-offline': status === 'offline'
      }
    },
    
    getWorkerStatusText(status) {
      const textMap = {
        'online': '在线',
        'busy': '忙碌',
        'offline': '离线'
      }
      return textMap[status] || '未知'
    },
    
    getWorkloadColor(workload) {
      if (workload > 80) return '#f5222d'
      if (workload > 60) return '#faad14'
      return '#52c41a'
    },
    
    getEquipmentBadgeStatus(status) {
      const statusMap = {
        'running': 'processing',
        'idle': 'default',
        'maintenance': 'warning',
        'error': 'error'
      }
      return statusMap[status] || 'default'
    },
    
    formatDate(date) {
      if (!date) return '-'
      return this.$moment(date).format('MM-DD')
    }
  }
}
</script>

<style lang="less" scoped>
.production-dashboard {
  .dashboard-cards {
    margin-bottom: 16px;
    
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        
        .stat-icon {
          width: 48px;
          height: 48px;
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          margin-right: 16px;
          
          .anticon {
            font-size: 24px;
            color: white;
          }
        }
        
        .stat-info {
          flex: 1;
          
          .stat-value {
            font-size: 24px;
            font-weight: 600;
            color: #333;
            line-height: 1.2;
          }
          
          .stat-label {
            color: #666;
            font-size: 14px;
            margin-bottom: 4px;
          }
          
          .stat-trend {
            font-size: 12px;
            
            &.trend-up {
              color: #52c41a;
            }
            
            &.trend-down {
              color: #f5222d;
            }
          }
        }
      }
    }
  }
  
  .dashboard-content {
    .chart-card {
      margin-bottom: 16px;
      
      .chart-container {
        .chart {
          width: 100%;
        }
      }
    }
    
    .real-time-orders {
      margin-bottom: 16px;
      
      .order-progress {
        display: flex;
        flex-direction: column;
        align-items: flex-end;
        
        .progress-text {
          font-size: 12px;
          color: #666;
          margin-top: 4px;
        }
      }
    }
    
    .task-assignment {
      margin-bottom: 16px;
      
      .worker-list {
        .worker-item {
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;
          
          &:last-child {
            border-bottom: none;
          }
          
          .worker-header {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
            
            .worker-info {
              flex: 1;
              margin-left: 8px;
              
              .worker-name {
                font-weight: 500;
                margin-bottom: 2px;
              }
              
              .worker-status {
                font-size: 12px;
                
                &.status-online {
                  color: #52c41a;
                }
                
                &.status-busy {
                  color: #faad14;
                }
                
                &.status-offline {
                  color: #d9d9d9;
                }
              }
            }
          }
          
          .worker-orders {
            .order-item {
              display: flex;
              justify-content: space-between;
              font-size: 12px;
              color: #666;
              margin-bottom: 2px;
              
              .order-name {
                flex: 1;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }
              
              .order-progress {
                margin-left: 8px;
              }
            }
            
            .more-orders {
              font-size: 12px;
              color: #999;
              text-align: center;
              margin-top: 4px;
            }
          }
        }
      }
    }
    
    .quality-stats {
      margin-bottom: 16px;
      
      .quality-metrics {
        display: flex;
        justify-content: space-between;
        margin-bottom: 16px;
        
        .metric-item {
          text-align: center;
          flex: 1;
          
          .metric-value {
            font-size: 20px;
            font-weight: 600;
            color: #333;
          }
          
          .metric-label {
            font-size: 12px;
            color: #666;
            margin: 4px 0;
          }
          
          .metric-trend {
            font-size: 12px;
            
            &.trend-up {
              color: #52c41a;
            }
            
            &.trend-down {
              color: #f5222d;
            }
          }
        }
      }
    }
    
    .equipment-status {
      .equipment-list {
        .equipment-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid #f0f0f0;
          
          &:last-child {
            border-bottom: none;
          }
          
          .equipment-info {
            .equipment-name {
              font-weight: 500;
              margin-bottom: 2px;
            }
            
            .equipment-location {
              font-size: 12px;
              color: #666;
            }
          }
        }
      }
    }
  }
}

// 响应式适配
@media (max-width: 768px) {
  .production-dashboard {
    .dashboard-cards {
      .stat-card {
        margin-bottom: 16px;
        
        .stat-content {
          .stat-icon {
            width: 40px;
            height: 40px;
            
            .anticon {
              font-size: 20px;
            }
          }
          
          .stat-info {
            .stat-value {
              font-size: 20px;
            }
          }
        }
      }
    }
  }
}
</style>
```

---

## 验收标准

### 界面设计验收标准
1. ✅ 界面设计符合jshERP整体风格，布局合理美观
2. ✅ 响应式设计完善，适配不同屏幕尺寸
3. ✅ 用户交互流畅，操作逻辑清晰直观
4. ✅ 数据展示清晰，重要信息突出显示

### 功能验收标准
1. ✅ 工单列表功能完整，支持多维度查询和筛选
2. ✅ 生产看板实时更新，数据准确可靠
3. ✅ 任务分配功能有效，支持批量操作
4. ✅ 图表展示直观，统计分析准确

### 性能验收标准
1. ✅ 页面加载速度快，首屏时间小于2秒
2. ✅ 大数据量列表渲染流畅，支持虚拟滚动
3. ✅ 图表渲染性能良好，交互响应及时
4. ✅ 实时数据更新频率合理，不影响用户操作

---

## 交付物清单

1. **生产管理界面**: 完整的工单管理界面
2. **生产看板**: 实时数据展示和统计分析
3. **任务分配功能**: 可视化任务分配和人员管理
4. **报工记录界面**: 完整的报工历史查看
5. **物流追踪界面**: 物流状态可视化展示
6. **用户操作手册**: 前端功能使用说明

---

*本文档严格遵循《jshERP_二次开发技术参考手册》的前端开发规范，确保界面风格和交互体验的一致性。*