<template>
  <div class="salary-payment-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <a-card :bordered="false" style="margin-bottom: 24px;">
        <a-form layout="inline" :model="queryParam">
          <a-form-item label="员工姓名">
            <a-input 
              v-model="queryParam.employeeName" 
              placeholder="请输入员工姓名" 
              style="width: 200px;"
              @pressEnter="handleSearch" />
          </a-form-item>
          <a-form-item label="发放月份">
            <a-month-picker 
              v-model="queryParam.calculationMonth" 
              placeholder="请选择月份"
              format="YYYY-MM" />
          </a-form-item>
          <a-form-item label="发放状态">
            <a-select 
              v-model="queryParam.paymentStatus" 
              placeholder="请选择状态" 
              style="width: 150px;"
              allowClear>
              <a-select-option value="">全部</a-select-option>
              <a-select-option value="PENDING">待发放</a-select-option>
              <a-select-option value="PROCESSING">发放中</a-select-option>
              <a-select-option value="PAID">已发放</a-select-option>
              <a-select-option value="FAILED">发放失败</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleSearch" icon="search">查询</a-button>
            <a-button @click="handleReset" style="margin-left: 8px;">重置</a-button>
          </a-form-item>
        </a-form>
      </a-card>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons" style="margin-bottom: 16px;">
      <a-button 
        type="primary" 
        @click="handleCreatePayment" 
        icon="plus">
        创建发放记录
      </a-button>
      <a-button 
        @click="handleBatchConfirm" 
        :disabled="selectedRowKeys.length === 0"
        style="margin-left: 8px;"
        icon="check">
        批量确认发放
      </a-button>
      <a-button @click="handleExport" style="margin-left: 8px;" icon="download">导出</a-button>
    </div>
    
    <!-- 数据表格 -->
    <a-card :bordered="false">
      <a-table 
        :columns="columns" 
        :data-source="dataList" 
        :pagination="pagination"
        :loading="loading"
        :row-selection="rowSelection"
        @change="handleTableChange"
        size="middle">
        
        <template slot="paymentStatus" slot-scope="text">
          <a-tag :color="getStatusColor(text)">
            {{ getStatusText(text) }}
          </a-tag>
        </template>
        
        <template slot="paymentAmount" slot-scope="text">
          <span style="font-weight: bold; color: #1890ff;">¥{{ text || 0 }}</span>
        </template>
        
        <template slot="paymentMethod" slot-scope="text">
          <span>{{ getPaymentMethodText(text) }}</span>
        </template>
        
        <template slot="action" slot-scope="text, record">
          <a-button size="small" @click="handleViewDetail(record)" style="margin-right: 8px;">详情</a-button>
          <a-dropdown v-if="record.paymentStatus === 'PENDING'">
            <a-menu slot="overlay">
              <a-menu-item @click="handleConfirmPayment(record)">确认发放</a-menu-item>
              <a-menu-item @click="handleGeneratePayslip(record)">生成薪资条</a-menu-item>
              <a-menu-item @click="handleEdit(record)">编辑</a-menu-item>
            </a-menu>
            <a-button size="small">
              操作 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-dropdown v-else-if="record.paymentStatus === 'PAID'">
            <a-menu slot="overlay">
              <a-menu-item @click="handleGeneratePayslip(record)">查看薪资条</a-menu-item>
              <a-menu-item @click="handleViewVoucher(record)">查看凭证</a-menu-item>
            </a-menu>
            <a-button size="small">
              查看 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-button 
            v-else-if="record.paymentStatus === 'FAILED'"
            size="small" 
            @click="handleRetryPayment(record)">
            重试发放
          </a-button>
        </template>
      </a-table>
    </a-card>
    
    <!-- 创建发放记录弹窗 -->
    <a-modal
      title="创建发放记录"
      :visible="createModalVisible"
      @ok="handleCreateModalOk"
      @cancel="handleCreateModalCancel"
      width="800px">
      <a-form :form="createForm" layout="vertical">
        <a-form-item label="选择已审批的薪酬计算记录">
          <a-table
            :columns="calculationColumns"
            :data-source="approvedCalculations"
            :row-selection="calculationRowSelection"
            :pagination="false"
            size="small">
            <template slot="totalAmount" slot-scope="text">
              <span>¥{{ text }}</span>
            </template>
          </a-table>
        </a-form-item>
        <a-form-item label="发放方式">
          <a-radio-group v-model="paymentMethod">
            <a-radio value="BANK_TRANSFER">银行转账</a-radio>
            <a-radio value="CASH">现金发放</a-radio>
            <a-radio value="OTHER">其他方式</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
    
    <!-- 发放详情弹窗 -->
    <payment-detail-modal 
      :visible="detailModalVisible" 
      :record="currentRecord"
      @cancel="handleDetailModalCancel" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import { getSalaryPaymentList, paySalary, confirmPayment, generatePayslip } from '@/api/salary'
import PaymentDetailModal from './components/PaymentDetailModal'

export default {
  name: 'SalaryPayment',
  components: {
    PaymentDetailModal
  },
  data() {
    return {
      // 查询参数
      queryParam: {
        employeeName: '',
        calculationMonth: null,
        paymentStatus: ''
      },
      
      // 表格数据
      dataList: [],
      loading: false,
      
      // 分页
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total) => `共 ${total} 条记录`
      },
      
      // 表格列定义
      columns: [
        {
          title: '发放单号',
          dataIndex: 'paymentNumber',
          key: 'paymentNumber',
          width: 180
        },
        {
          title: '员工姓名',
          dataIndex: 'employeeName',
          key: 'employeeName',
          width: 120
        },
        {
          title: '发放月份',
          dataIndex: 'calculationMonth',
          key: 'calculationMonth',
          width: 100
        },
        {
          title: '发放金额',
          dataIndex: 'paymentAmount',
          key: 'paymentAmount',
          width: 120,
          scopedSlots: { customRender: 'paymentAmount' }
        },
        {
          title: '发放方式',
          dataIndex: 'paymentMethod',
          key: 'paymentMethod',
          width: 100,
          scopedSlots: { customRender: 'paymentMethod' }
        },
        {
          title: '发放日期',
          dataIndex: 'paymentDate',
          key: 'paymentDate',
          width: 120
        },
        {
          title: '发放状态',
          dataIndex: 'paymentStatus',
          key: 'paymentStatus',
          width: 100,
          scopedSlots: { customRender: 'paymentStatus' }
        },
        {
          title: '操作',
          key: 'action',
          width: 150,
          scopedSlots: { customRender: 'action' }
        }
      ],
      
      // 行选择
      selectedRowKeys: [],
      rowSelection: {
        onChange: (selectedRowKeys) => {
          this.selectedRowKeys = selectedRowKeys
        }
      },
      
      // 弹窗状态
      createModalVisible: false,
      detailModalVisible: false,
      currentRecord: {},
      
      // 创建发放记录相关
      createForm: this.$form.createForm(this),
      paymentMethod: 'BANK_TRANSFER',
      approvedCalculations: [],
      selectedCalculationIds: [],
      calculationRowSelection: {
        onChange: (selectedRowKeys) => {
          this.selectedCalculationIds = selectedRowKeys
        }
      },
      calculationColumns: [
        {
          title: '员工姓名',
          dataIndex: 'employeeName',
          key: 'employeeName'
        },
        {
          title: '计算月份',
          dataIndex: 'calculationMonth',
          key: 'calculationMonth'
        },
        {
          title: '薪酬总额',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
          scopedSlots: { customRender: 'totalAmount' }
        }
      ]
    }
  },
  
  mounted() {
    this.loadData()
  },
  
  methods: {
    // 加载数据
    loadData() {
      this.loading = true
      const params = {
        ...this.queryParam,
        calculationMonth: this.queryParam.calculationMonth ? this.queryParam.calculationMonth.format('YYYY-MM') : '',
        current: this.pagination.current,
        size: this.pagination.pageSize
      }
      
      getSalaryPaymentList(params).then(res => {
        if (res.success) {
          this.dataList = res.data.records || []
          this.pagination.total = res.data.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'PENDING': 'orange',
        'PROCESSING': 'blue',
        'PAID': 'green',
        'FAILED': 'red'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'PENDING': '待发放',
        'PROCESSING': '发放中',
        'PAID': '已发放',
        'FAILED': '发放失败'
      }
      return textMap[status] || status
    },
    
    // 获取发放方式文本
    getPaymentMethodText(method) {
      const textMap = {
        'BANK_TRANSFER': '银行转账',
        'CASH': '现金发放',
        'OTHER': '其他方式'
      }
      return textMap[method] || method
    },
    
    // 搜索
    handleSearch() {
      this.pagination.current = 1
      this.loadData()
    },
    
    // 重置
    handleReset() {
      this.queryParam = {
        employeeName: '',
        calculationMonth: null,
        paymentStatus: ''
      }
      this.handleSearch()
    },
    
    // 表格变化
    handleTableChange(pagination) {
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      this.loadData()
    },
    
    // 创建发放记录
    handleCreatePayment() {
      this.loadApprovedCalculations()
      this.createModalVisible = true
    },
    
    // 加载已审批的计算记录
    loadApprovedCalculations() {
      // TODO: 调用API获取已审批的计算记录
      this.approvedCalculations = []
    },
    
    // 创建发放记录确定
    handleCreateModalOk() {
      if (this.selectedCalculationIds.length === 0) {
        this.$message.warning('请选择要发放的计算记录')
        return
      }
      
      const params = {
        calculationIds: this.selectedCalculationIds.join(','),
        paymentMethod: this.paymentMethod
      }
      
      paySalary(params).then(res => {
        if (res.success) {
          this.$message.success('发放记录创建成功')
          this.createModalVisible = false
          this.loadData()
        } else {
          this.$message.error(res.message || '创建失败')
        }
      })
    },
    
    // 创建发放记录取消
    handleCreateModalCancel() {
      this.createModalVisible = false
      this.selectedCalculationIds = []
    },
    
    // 确认发放
    handleConfirmPayment(record) {
      this.$confirm({
        title: '确认发放',
        content: `确定要发放员工 ${record.employeeName} 的薪酬吗？`,
        onOk: () => {
          confirmPayment({ ids: record.id.toString() }).then(res => {
            if (res.success) {
              this.$message.success('确认发放成功')
              this.loadData()
            } else {
              this.$message.error(res.message || '确认发放失败')
            }
          })
        }
      })
    },
    
    // 批量确认发放
    handleBatchConfirm() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要确认发放的记录')
        return
      }
      
      confirmPayment({ ids: this.selectedRowKeys.join(',') }).then(res => {
        if (res.success) {
          this.$message.success('批量确认发放成功')
          this.selectedRowKeys = []
          this.loadData()
        } else {
          this.$message.error(res.message || '批量确认发放失败')
        }
      })
    },
    
    // 生成薪资条
    handleGeneratePayslip(record) {
      generatePayslip(record.id).then(res => {
        if (res.success) {
          // 下载或预览薪资条
          window.open(res.data.payslipUrl)
        } else {
          this.$message.error(res.message || '薪资条生成失败')
        }
      })
    },
    
    // 查看详情
    handleViewDetail(record) {
      this.currentRecord = { ...record }
      this.detailModalVisible = true
    },
    
    // 详情弹窗取消
    handleDetailModalCancel() {
      this.detailModalVisible = false
    },
    
    // 重试发放
    handleRetryPayment(record) {
      // TODO: 实现重试发放功能
      this.$message.info('重试发放功能开发中...')
    },
    
    // 查看凭证
    handleViewVoucher(record) {
      // TODO: 实现查看财务凭证功能
      this.$message.info('查看凭证功能开发中...')
    },
    
    // 编辑
    handleEdit(record) {
      // TODO: 实现编辑功能
      this.$message.info('编辑功能开发中...')
    },
    
    // 导出
    handleExport() {
      // TODO: 实现导出功能
      this.$message.info('导出功能开发中...')
    }
  }
}
</script>

<style scoped>
.salary-payment-container {
  padding: 24px;
  background: #f7f8fa;
  min-height: 100vh;
}

.search-form .ant-form-item {
  margin-bottom: 16px;
}

.action-buttons {
  text-align: left;
}
</style>
