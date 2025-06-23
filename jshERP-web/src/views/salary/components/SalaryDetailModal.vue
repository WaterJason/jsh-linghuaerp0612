<template>
  <a-modal
    title="薪资计算详情"
    :visible="visible"
    :width="1000"
    :footer="null"
    @cancel="handleCancel"
  >
    <div class="salary-detail-container">
      <!-- 员工基本信息 -->
      <a-card title="员工信息" size="small" class="info-card">
        <a-row :gutter="16">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">员工姓名：</span>
              <span class="value">{{ detail.employeeName || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">员工编号：</span>
              <span class="value">{{ detail.employeeCode || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">部门：</span>
              <span class="value">{{ detail.department || '-' }}</span>
            </div>
          </a-col>
        </a-row>
        <a-row :gutter="16">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">职位：</span>
              <span class="value">{{ detail.position || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">计算月份：</span>
              <span class="value">{{ detail.salaryMonth || '-' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">计算状态：</span>
              <a-tag :color="getStatusColor(detail.status)">
                {{ getStatusText(detail.status) }}
              </a-tag>
            </div>
          </a-col>
        </a-row>
      </a-card>

      <!-- 薪资构成详情 -->
      <a-card title="薪资构成" size="small" class="salary-card">
        <a-table
          :columns="salaryColumns"
          :data-source="salaryItems"
          :pagination="false"
          size="small"
          bordered
        >
          <template slot="amount" slot-scope="text, record">
            <span :class="{ 'negative-amount': record.amount < 0 }">
              ¥{{ record.amount ? record.amount.toFixed(2) : '0.00' }}
            </span>
          </template>
        </a-table>
      </a-card>

      <!-- 考勤统计 -->
      <a-card title="考勤统计" size="small" class="attendance-card">
        <a-row :gutter="16">
          <a-col :span="6">
            <a-statistic
              title="出勤天数"
              :value="detail.attendanceDays || 0"
              suffix="天"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="加班时长"
              :value="detail.overtimeHours || 0"
              suffix="小时"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="迟到次数"
              :value="detail.lateCount || 0"
              suffix="次"
            />
          </a-col>
          <a-col :span="6">
            <a-statistic
              title="早退次数"
              :value="detail.earlyLeaveCount || 0"
              suffix="次"
            />
          </a-col>
        </a-row>
      </a-card>

      <!-- 业绩统计 -->
      <a-card title="业绩统计" size="small" class="performance-card">
        <a-row :gutter="16">
          <a-col :span="8">
            <a-statistic
              title="销售业绩"
              :value="detail.salesAmount || 0"
              prefix="¥"
              :precision="2"
            />
          </a-col>
          <a-col :span="8">
            <a-statistic
              title="生产产值"
              :value="detail.productionAmount || 0"
              prefix="¥"
              :precision="2"
            />
          </a-col>
          <a-col :span="8">
            <a-statistic
              title="培训收入"
              :value="detail.trainingAmount || 0"
              prefix="¥"
              :precision="2"
            />
          </a-col>
        </a-row>
      </a-card>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <a-button @click="handlePrint">
          <a-icon type="printer" />
          打印工资条
        </a-button>
        <a-button @click="handleExport">
          <a-icon type="download" />
          导出详情
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
  name: 'SalaryDetailModal',
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
      salaryColumns: [
        {
          title: '项目',
          dataIndex: 'item',
          key: 'item',
          width: 200
        },
        {
          title: '说明',
          dataIndex: 'description',
          key: 'description'
        },
        {
          title: '金额',
          dataIndex: 'amount',
          key: 'amount',
          width: 120,
          align: 'right',
          scopedSlots: { customRender: 'amount' }
        }
      ]
    }
  },
  computed: {
    salaryItems() {
      const detail = this.detail
      return [
        { key: '1', item: '基础工资', description: '固定基础工资', amount: detail.baseSalary || 0 },
        { key: '2', item: '绩效工资', description: '绩效考核工资', amount: detail.performanceSalary || 0 },
        { key: '3', item: '销售提成', description: '销售业绩提成', amount: detail.salesCommission || 0 },
        { key: '4', item: '生产提成', description: '生产产值提成', amount: detail.productionCommission || 0 },
        { key: '5', item: '培训提成', description: '培训收入提成', amount: detail.trainingCommission || 0 },
        { key: '6', item: '加班费', description: '加班时长费用', amount: detail.overtimePay || 0 },
        { key: '7', item: '津贴补助', description: '各类津贴补助', amount: detail.allowance || 0 },
        { key: '8', item: '考勤扣款', description: '迟到早退扣款', amount: -(detail.attendanceDeduction || 0) },
        { key: '9', item: '社保扣款', description: '个人社保缴费', amount: -(detail.socialInsurance || 0) },
        { key: '10', item: '公积金扣款', description: '个人公积金缴费', amount: -(detail.housingFund || 0) },
        { key: '11', item: '个人所得税', description: '个人所得税', amount: -(detail.incomeTax || 0) },
        { 
          key: '12', 
          item: '实发工资', 
          description: '最终实际发放金额', 
          amount: detail.netSalary || 0,
          style: { fontWeight: 'bold', backgroundColor: '#f0f9ff' }
        }
      ]
    }
  },
  methods: {
    getStatusColor(status) {
      const colorMap = {
        'pending': 'orange',
        'calculated': 'blue',
        'approved': 'green',
        'paid': 'purple'
      }
      return colorMap[status] || 'default'
    },
    getStatusText(status) {
      const textMap = {
        'pending': '待计算',
        'calculated': '已计算',
        'approved': '已审核',
        'paid': '已发放'
      }
      return textMap[status] || '未知'
    },
    handlePrint() {
      this.$message.info('打印功能开发中...')
    },
    handleExport() {
      this.$message.info('导出功能开发中...')
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.salary-detail-container {
  max-height: 70vh;
  overflow-y: auto;
}

.info-card,
.salary-card,
.attendance-card,
.performance-card {
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

.negative-amount {
  color: #f5222d;
}

.action-buttons {
  text-align: center;
  margin-top: 16px;
}

.action-buttons .ant-btn {
  margin: 0 8px;
}

:deep(.ant-table-tbody > tr:last-child) {
  background-color: #f0f9ff;
  font-weight: bold;
}
</style>
