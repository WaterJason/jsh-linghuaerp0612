<template>
  <div class="salary-calculation-container">
    <!-- 计算参数设置 -->
    <a-card title="薪酬计算" :bordered="false" style="margin-bottom: 24px;">
      <a-form layout="inline" :model="calculationParam">
        <a-form-item label="计算月份">
          <a-month-picker 
            v-model="calculationParam.calculationMonth" 
            placeholder="请选择计算月份"
            format="YYYY-MM" />
        </a-form-item>
        <a-form-item label="员工范围">
          <a-select 
            v-model="calculationParam.employeeScope" 
            placeholder="请选择员工范围" 
            style="width: 200px;">
            <a-select-option value="all">全部员工</a-select-option>
            <a-select-option value="department">按部门</a-select-option>
            <a-select-option value="custom">自定义选择</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item v-if="calculationParam.employeeScope === 'department'" label="部门">
          <a-select 
            v-model="calculationParam.department" 
            placeholder="请选择部门" 
            style="width: 150px;">
            <a-select-option value="珐琅制作">珐琅制作</a-select-option>
            <a-select-option value="咖啡服务">咖啡服务</a-select-option>
            <a-select-option value="培训教学">培训教学</a-select-option>
            <a-select-option value="业务拓展">业务拓展</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button 
            type="primary" 
            @click="handleCalculate" 
            :loading="calculating"
            icon="calculator">
            开始计算
          </a-button>
          <a-button @click="handlePreview" style="margin-left: 8px;">预览数据</a-button>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 计算进度 -->
    <a-card v-if="calculating" title="计算进度" :bordered="false" style="margin-bottom: 24px;">
      <a-progress 
        :percent="calculationProgress" 
        :status="calculationStatus"
        :show-info="true" />
      <p style="margin-top: 8px;">{{ calculationMessage }}</p>
    </a-card>

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
          <a-form-item label="计算月份">
            <a-month-picker 
              v-model="queryParam.calculationMonth" 
              placeholder="请选择月份"
              format="YYYY-MM" />
          </a-form-item>
          <a-form-item label="状态">
            <a-select 
              v-model="queryParam.status" 
              placeholder="请选择状态" 
              style="width: 150px;"
              allowClear>
              <a-select-option value="">全部</a-select-option>
              <a-select-option value="DRAFT">草稿</a-select-option>
              <a-select-option value="PENDING_APPROVAL">待审批</a-select-option>
              <a-select-option value="APPROVED">已审批</a-select-option>
              <a-select-option value="REJECTED">已拒绝</a-select-option>
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
        @click="handleBatchApprove" 
        :disabled="selectedRowKeys.length === 0"
        icon="check">
        批量审批
      </a-button>
      <a-button 
        @click="handleBatchReject" 
        :disabled="selectedRowKeys.length === 0"
        style="margin-left: 8px;"
        icon="close">
        批量拒绝
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
        
        <template slot="status" slot-scope="text">
          <a-tag :color="getStatusColor(text)">
            {{ getStatusText(text) }}
          </a-tag>
        </template>
        
        <template slot="totalAmount" slot-scope="text">
          <span style="font-weight: bold; color: #1890ff;">¥{{ text || 0 }}</span>
        </template>
        
        <template slot="action" slot-scope="text, record">
          <a-button size="small" @click="handleViewDetail(record)" style="margin-right: 8px;">详情</a-button>
          <a-dropdown v-if="record.status === 'PENDING_APPROVAL'">
            <a-menu slot="overlay">
              <a-menu-item @click="handleApprove(record)">审批通过</a-menu-item>
              <a-menu-item @click="handleReject(record)">审批拒绝</a-menu-item>
            </a-menu>
            <a-button size="small">
              审批 <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-dropdown v-else-if="record.status === 'DRAFT' || record.status === 'REJECTED'">
            <a-menu slot="overlay">
              <a-menu-item @click="handleRecalculate(record)">重新计算</a-menu-item>
              <a-menu-item @click="handleDelete(record)">删除</a-menu-item>
            </a-menu>
            <a-button size="small">
              操作 <a-icon type="down" />
            </a-button>
          </a-dropdown>
        </template>
      </a-table>
    </a-card>
    
    <!-- 计算详情弹窗 -->
    <salary-detail-modal 
      :visible="detailModalVisible" 
      :record="currentRecord"
      @cancel="handleDetailModalCancel" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import { getSalaryCalculationList, calculateSalary, approveSalaryCalculation } from '@/api/salary'
import SalaryDetailModal from './components/SalaryDetailModal'

export default {
  name: 'SalaryCalculation',
  components: {
    SalaryDetailModal
  },
  data() {
    return {
      // 计算参数
      calculationParam: {
        calculationMonth: dayjs().subtract(1, 'month'),
        employeeScope: 'all',
        department: ''
      },
      
      // 计算状态
      calculating: false,
      calculationProgress: 0,
      calculationStatus: 'normal',
      calculationMessage: '',
      
      // 查询参数
      queryParam: {
        employeeName: '',
        calculationMonth: null,
        status: ''
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
          title: '计算单号',
          dataIndex: 'calculationNumber',
          key: 'calculationNumber',
          width: 180
        },
        {
          title: '员工姓名',
          dataIndex: 'employeeName',
          key: 'employeeName',
          width: 120
        },
        {
          title: '计算月份',
          dataIndex: 'calculationMonth',
          key: 'calculationMonth',
          width: 100
        },
        {
          title: '薪酬总额',
          dataIndex: 'totalAmount',
          key: 'totalAmount',
          width: 120,
          scopedSlots: { customRender: 'totalAmount' }
        },
        {
          title: '固定薪酬',
          dataIndex: 'fixedAmount',
          key: 'fixedAmount',
          width: 100
        },
        {
          title: '提成金额',
          dataIndex: 'commissionAmount',
          key: 'commissionAmount',
          width: 100
        },
        {
          title: '津贴金额',
          dataIndex: 'allowanceAmount',
          key: 'allowanceAmount',
          width: 100
        },
        {
          title: '状态',
          dataIndex: 'status',
          key: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '计算时间',
          dataIndex: 'calculationDate',
          key: 'calculationDate',
          width: 150
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
      detailModalVisible: false,
      currentRecord: {}
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
      
      getSalaryCalculationList(params).then(res => {
        if (res.success) {
          this.dataList = res.data.records || []
          this.pagination.total = res.data.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
    },
    
    // 执行计算
    handleCalculate() {
      if (!this.calculationParam.calculationMonth) {
        this.$message.warning('请选择计算月份')
        return
      }
      
      this.calculating = true
      this.calculationProgress = 0
      this.calculationStatus = 'active'
      this.calculationMessage = '正在准备计算数据...'
      
      // 模拟计算进度
      const progressTimer = setInterval(() => {
        if (this.calculationProgress < 90) {
          this.calculationProgress += 10
          this.calculationMessage = `正在计算薪酬数据... ${this.calculationProgress}%`
        }
      }, 500)
      
      const params = {
        calculationMonth: this.calculationParam.calculationMonth.format('YYYY-MM'),
        employeeScope: this.calculationParam.employeeScope,
        department: this.calculationParam.department
      }
      
      calculateSalary(params).then(res => {
        clearInterval(progressTimer)
        this.calculationProgress = 100
        this.calculationStatus = 'success'
        
        if (res.success) {
          this.calculationMessage = `计算完成！共计算 ${res.data.calculatedCount} 名员工`
          this.$message.success('薪酬计算完成')
          setTimeout(() => {
            this.calculating = false
            this.loadData()
          }, 2000)
        } else {
          this.calculationStatus = 'exception'
          this.calculationMessage = res.message || '计算失败'
          this.$message.error(res.message || '计算失败')
          setTimeout(() => {
            this.calculating = false
          }, 2000)
        }
      }).catch(() => {
        clearInterval(progressTimer)
        this.calculationProgress = 100
        this.calculationStatus = 'exception'
        this.calculationMessage = '计算过程发生异常'
        this.$message.error('计算过程发生异常')
        setTimeout(() => {
          this.calculating = false
        }, 2000)
      })
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'DRAFT': 'default',
        'PENDING_APPROVAL': 'orange',
        'APPROVED': 'green',
        'REJECTED': 'red'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'DRAFT': '草稿',
        'PENDING_APPROVAL': '待审批',
        'APPROVED': '已审批',
        'REJECTED': '已拒绝'
      }
      return textMap[status] || status
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
        status: ''
      }
      this.handleSearch()
    },
    
    // 表格变化
    handleTableChange(pagination) {
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      this.loadData()
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
    
    // 审批通过
    handleApprove(record) {
      this.handleApprovalAction([record.id], 'APPROVED', '审批通过')
    },
    
    // 审批拒绝
    handleReject(record) {
      this.$confirm({
        title: '审批拒绝',
        content: '请输入拒绝原因',
        onOk: (reason) => {
          this.handleApprovalAction([record.id], 'REJECTED', reason || '审批拒绝')
        }
      })
    },
    
    // 批量审批
    handleBatchApprove() {
      this.handleApprovalAction(this.selectedRowKeys, 'APPROVED', '批量审批通过')
    },
    
    // 批量拒绝
    handleBatchReject() {
      this.handleApprovalAction(this.selectedRowKeys, 'REJECTED', '批量审批拒绝')
    },
    
    // 执行审批操作
    handleApprovalAction(ids, status, remark) {
      approveSalaryCalculation({
        ids: ids.join(','),
        status: status,
        approveRemark: remark
      }).then(res => {
        if (res.success) {
          this.$message.success('操作成功')
          this.selectedRowKeys = []
          this.loadData()
        } else {
          this.$message.error(res.message || '操作失败')
        }
      })
    },
    
    // 重新计算
    handleRecalculate(record) {
      // TODO: 实现重新计算功能
      this.$message.info('重新计算功能开发中...')
    },
    
    // 删除
    handleDelete(record) {
      // TODO: 实现删除功能
      this.$message.info('删除功能开发中...')
    },
    
    // 预览数据
    handlePreview() {
      // TODO: 实现数据预览功能
      this.$message.info('数据预览功能开发中...')
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
.salary-calculation-container {
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
