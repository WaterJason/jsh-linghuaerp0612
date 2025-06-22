<template>
  <a-modal
    title="薪资发放详情"
    :visible="visible"
    :width="900"
    :footer="null"
    @cancel="handleCancel"
  >
    <div class="payment-detail-container">
      <!-- 发放信息 -->
      <a-card title="发放信息" size="small" class="info-card">
        <a-row :gutter="16">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">发放批次：</span>
              <span class="value">{{ detail.batchNo || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">发放月份：</span>
              <span class="value">{{ detail.paymentMonth || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">发放状态：</span>
              <a-tag :color="getStatusColor(detail.status)">
                {{ getStatusText(detail.status) }}
              </a-tag>
            </div>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">发放人员：</span>
              <span class="value">{{ detail.paymentOperator || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">发放时间：</span>
              <span class="value">{{ detail.paymentTime || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">发放方式：</span>
              <span class="value">{{ getPaymentMethodText(detail.paymentMethod) }}</span>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 员工薪资列表 -->
      <a-card title="员工薪资明细" size="small" class="salary-list-card">
        <div class="table-toolbar">
          <a-space>
            <a-input-search
              v-model="searchText"
              placeholder="搜索员工姓名或编号"
              style="width: 200px"
              @search="handleSearch"
            />
            <a-select
              v-model="filterDepartment"
              placeholder="筛选部门"
              style="width: 120px"
              @change="handleFilter"
            >
              <a-select-option value="">全部</a-select-option>
              <a-select-option value="cloisonne">掐丝珐琅馆</a-select-option>
              <a-select-option value="coffee">咖啡店</a-select-option>
              <a-select-option value="management">管理部门</a-select-option>
            </a-select>
          </a-space>
          
          <a-space>
            <a-button @click="handleExportAll">
              <a-icon type="download" />
              导出全部
            </a-button>
            <a-button type="primary" @click="handleBatchPayment">
              <a-icon type="dollar" />
              批量发放
            </a-button>
          </a-space>
        </div>
        
        <a-table
          :columns="salaryColumns"
          :data-source="filteredSalaryList"
          :pagination="pagination"
          :loading="loading"
          size="small"
          bordered
          :row-selection="rowSelection"
        >
          <template slot="netSalary" slot-scope="text">
            <span class="salary-amount">¥{{ text ? text.toFixed(2) : '0.00' }}</span>
          </template>
          
          <template slot="paymentStatus" slot-scope="text, record">
            <a-tag :color="getPaymentStatusColor(text)">
              {{ getPaymentStatusText(text) }}
            </a-tag>
          </template>
          
          <template slot="action" slot-scope="text, record">
            <a-space>
              <a @click="handleViewDetail(record)">查看详情</a>
              <a v-if="record.paymentStatus === 'pending'" @click="handlePay(record)">发放</a>
              <a @click="handlePrintSlip(record)">打印工资条</a>
            </a-space>
          </template>
        </a-table>
      </a-card>

      <!-- 统计信息 -->
      <a-card title="发放统计" size="small" class="statistics-card">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-statistic
              title="总人数"
              :value="statistics.totalEmployees"
              suffix="人"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="已发放人数"
              :value="statistics.paidEmployees"
              suffix="人"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="总发放金额"
              :value="statistics.totalAmount"
              prefix="¥"
              :precision="2"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="已发放金额"
              :value="statistics.paidAmount"
              prefix="¥"
              :precision="2"
            />
          </a-col>
        </a-row>
      </a-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-button @click="handleRefresh">
          <a-icon type="reload" />
          刷新数据
        </a-button>
        <a-button type="primary" @click="handleCancel">
          关闭
        </a-button>
      </div>
    </div>
  </a-modal>
</template>

<script>
export default {
  name: 'PaymentDetailModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    detail: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      loading: false,
      searchText: '',
      filterDepartment: '',
      selectedRowKeys: [],
      salaryList: [
        {
          id: 1,
          employeeName: '张三',
          employeeCode: 'EMP001',
          department: 'cloisonne',
          position: '工艺师',
          netSalary: 8500.00,
          paymentStatus: 'paid',
          paymentTime: '2024-01-15 10:30:00'
        },
        {
          id: 2,
          employeeName: '李四',
          employeeCode: 'EMP002',
          department: 'coffee',
          position: '咖啡师',
          netSalary: 6800.00,
          paymentStatus: 'pending',
          paymentTime: null
        }
      ],
      salaryColumns: [
        {
          title: '员工姓名',
          dataIndex: 'employeeName',
          key: 'employeeName',
          width: 100
        },
        {
          title: '员工编号',
          dataIndex: 'employeeCode',
          key: 'employeeCode',
          width: 100
        },
        {
          title: '部门',
          dataIndex: 'department',
          key: 'department',
          width: 100
        },
        {
          title: '职位',
          dataIndex: 'position',
          key: 'position',
          width: 100
        },
        {
          title: '实发工资',
          dataIndex: 'netSalary',
          key: 'netSalary',
          width: 120,
          align: 'right',
          scopedSlots: { customRender: 'netSalary' }
        },
        {
          title: '发放状态',
          dataIndex: 'paymentStatus',
          key: 'paymentStatus',
          width: 100,
          scopedSlots: { customRender: 'paymentStatus' }
        },
        {
          title: '发放时间',
          dataIndex: 'paymentTime',
          key: 'paymentTime',
          width: 150
        },
        {
          title: '操作',
          key: 'action',
          width: 150,
          scopedSlots: { customRender: 'action' }
        }
      ],
      pagination: {
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        showQuickJumper: true,
        showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
      }
    }
  },
  computed: {
    filteredSalaryList() {
      let list = this.salaryList
      
      if (this.searchText) {
        list = list.filter(item => 
          item.employeeName.includes(this.searchText) || 
          item.employeeCode.includes(this.searchText)
        )
      }
      
      if (this.filterDepartment) {
        list = list.filter(item => item.department === this.filterDepartment)
      }
      
      return list
    },
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.onSelectChange
      }
    },
    statistics() {
      const totalEmployees = this.salaryList.length
      const paidEmployees = this.salaryList.filter(item => item.paymentStatus === 'paid').length
      const totalAmount = this.salaryList.reduce((sum, item) => sum + (item.netSalary || 0), 0)
      const paidAmount = this.salaryList
        .filter(item => item.paymentStatus === 'paid')
        .reduce((sum, item) => sum + (item.netSalary || 0), 0)
      
      return {
        totalEmployees,
        paidEmployees,
        totalAmount,
        paidAmount
      }
    }
  },
  methods: {
    getStatusColor(status) {
      const colorMap = {
        'pending': 'orange',
        'processing': 'blue',
        'completed': 'green',
        'failed': 'red'
      }
      return colorMap[status] || 'default'
    },
    getStatusText(status) {
      const textMap = {
        'pending': '待发放',
        'processing': '发放中',
        'completed': '已完成',
        'failed': '发放失败'
      }
      return textMap[status] || '未知'
    },
    getPaymentMethodText(method) {
      const textMap = {
        'bank': '银行转账',
        'cash': '现金发放',
        'alipay': '支付宝',
        'wechat': '微信支付'
      }
      return textMap[method] || '未知'
    },
    getPaymentStatusColor(status) {
      const colorMap = {
        'pending': 'orange',
        'paid': 'green',
        'failed': 'red'
      }
      return colorMap[status] || 'default'
    },
    getPaymentStatusText(status) {
      const textMap = {
        'pending': '待发放',
        'paid': '已发放',
        'failed': '发放失败'
      }
      return textMap[status] || '未知'
    },
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    handleSearch() {
      // 搜索逻辑已在computed中实现
    },
    handleFilter() {
      // 筛选逻辑已在computed中实现
    },
    handleViewDetail(record) {
      this.$message.info(`查看 ${record.employeeName} 的薪资详情`)
    },
    handlePay(record) {
      this.$message.info(`正在为 ${record.employeeName} 发放薪资...`)
    },
    handlePrintSlip(record) {
      this.$message.info(`正在打印 ${record.employeeName} 的工资条...`)
    },
    handleExportAll() {
      this.$message.info('正在导出薪资发放明细...')
    },
    handleBatchPayment() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要发放的员工')
        return
      }
      this.$message.info(`正在为 ${this.selectedRowKeys.length} 名员工批量发放薪资...`)
    },
    handleRefresh() {
      this.loading = true
      setTimeout(() => {
        this.loading = false
        this.$message.success('数据刷新成功')
      }, 1000)
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.payment-detail-container {
  max-height: 80vh;
  overflow-y: auto;
}

.info-card,
.salary-list-card,
.statistics-card {
  margin-bottom: 16px;
}

.info-item {
  margin-bottom: 8px;
}

.info-item .label {
  color: #666;
  font-weight: 500;
}

.info-item .value {
  color: #333;
  margin-left: 8px;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.salary-amount {
  color: #1890ff;
  font-weight: bold;
}

.action-buttons {
  text-align: center;
  margin-top: 16px;
}

.action-buttons .ant-btn {
  margin: 0 8px;
}
</style>
