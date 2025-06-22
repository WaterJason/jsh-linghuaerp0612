<template>
  <div class="salary-inquiry-container">
    <!-- 个人薪资查询 -->
    <a-card title="个人薪资查询" :bordered="false" style="margin-bottom: 24px;" v-if="isPersonalView">
      <a-form layout="inline" :model="personalQuery">
        <a-form-item label="查询月份">
          <a-month-picker 
            v-model="personalQuery.month" 
            placeholder="请选择月份"
            format="YYYY-MM" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handlePersonalSearch" icon="search">查询</a-button>
        </a-form-item>
      </a-form>
      
      <!-- 个人薪资详情 -->
      <div v-if="personalSalaryData" class="personal-salary-detail">
        <a-row :gutter="24" style="margin-top: 24px;">
          <a-col :span="6">
            <a-statistic title="薪酬总额" :value="personalSalaryData.totalAmount" prefix="¥" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="固定薪酬" :value="personalSalaryData.fixedAmount" prefix="¥" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="提成金额" :value="personalSalaryData.commissionAmount" prefix="¥" />
          </a-col>
          <a-col :span="6">
            <a-statistic title="津贴金额" :value="personalSalaryData.allowanceAmount" prefix="¥" />
          </a-col>
        </a-row>
        
        <!-- 薪资明细 -->
        <a-table 
          :columns="detailColumns" 
          :data-source="personalSalaryData.details" 
          :pagination="false"
          style="margin-top: 24px;"
          size="middle">
          <template slot="finalAmount" slot-scope="text">
            <span>¥{{ text }}</span>
          </template>
        </a-table>
      </div>
    </a-card>
    
    <!-- 薪资统计查询（管理员视图） -->
    <a-card title="薪资统计查询" :bordered="false" style="margin-bottom: 24px;" v-if="!isPersonalView">
      <a-form layout="inline" :model="queryParam">
        <a-form-item label="员工姓名">
          <a-input 
            v-model="queryParam.employeeName" 
            placeholder="请输入员工姓名" 
            style="width: 200px;"
            @pressEnter="handleSearch" />
        </a-form-item>
        <a-form-item label="部门">
          <a-select 
            v-model="queryParam.department" 
            placeholder="请选择部门" 
            style="width: 150px;"
            allowClear>
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="珐琅制作">珐琅制作</a-select-option>
            <a-select-option value="咖啡服务">咖啡服务</a-select-option>
            <a-select-option value="培训教学">培训教学</a-select-option>
            <a-select-option value="业务拓展">业务拓展</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="查询月份">
          <a-month-picker 
            v-model="queryParam.calculationMonth" 
            placeholder="请选择月份"
            format="YYYY-MM" />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch" icon="search">查询</a-button>
          <a-button @click="handleReset" style="margin-left: 8px;">重置</a-button>
        </a-form-item>
      </a-form>
    </a-card>
    
    <!-- 薪资统计图表 -->
    <a-row :gutter="24" style="margin-bottom: 24px;" v-if="!isPersonalView">
      <a-col :span="12">
        <a-card title="月度薪资趋势" :bordered="false">
          <div id="salaryTrendChart" style="height: 300px;"></div>
        </a-card>
      </a-col>
      <a-col :span="12">
        <a-card title="部门薪资分布" :bordered="false">
          <div id="departmentSalaryChart" style="height: 300px;"></div>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 薪资统计表格 -->
    <a-card :bordered="false" v-if="!isPersonalView">
      <div class="action-buttons" style="margin-bottom: 16px;">
        <a-button @click="handleExport" icon="download">导出数据</a-button>
        <a-button @click="handlePrint" style="margin-left: 8px;" icon="printer">打印报表</a-button>
      </div>
      
      <a-table 
        :columns="columns" 
        :data-source="dataList" 
        :pagination="pagination"
        :loading="loading"
        @change="handleTableChange"
        size="middle">
        
        <template slot="totalAmount" slot-scope="text">
          <span style="font-weight: bold; color: #1890ff;">¥{{ text || 0 }}</span>
        </template>
        
        <template slot="action" slot-scope="text, record">
          <a-button size="small" @click="handleViewDetail(record)">查看明细</a-button>
        </template>
      </a-table>
    </a-card>
    
    <!-- 薪资明细弹窗 -->
    <salary-detail-modal 
      :visible="detailModalVisible" 
      :record="currentRecord"
      @cancel="handleDetailModalCancel" />
  </div>
</template>

<script>
import dayjs from 'dayjs'
import * as echarts from 'echarts'
import { getSalaryInquiryList, getPersonalSalary, getSalaryStatistics } from '@/api/salary'
import SalaryDetailModal from './components/SalaryDetailModal'

export default {
  name: 'SalaryInquiry',
  components: {
    SalaryDetailModal
  },
  data() {
    return {
      // 是否为个人视图
      isPersonalView: true,
      
      // 个人查询参数
      personalQuery: {
        month: dayjs().subtract(1, 'month')
      },
      
      // 个人薪资数据
      personalSalaryData: null,
      
      // 查询参数
      queryParam: {
        employeeName: '',
        department: '',
        calculationMonth: dayjs().subtract(1, 'month')
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
          title: '员工姓名',
          dataIndex: 'employeeName',
          key: 'employeeName',
          width: 120
        },
        {
          title: '部门',
          dataIndex: 'department',
          key: 'department',
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
          title: '实发金额',
          dataIndex: 'actualAmount',
          key: 'actualAmount',
          width: 120
        },
        {
          title: '操作',
          key: 'action',
          width: 100,
          scopedSlots: { customRender: 'action' }
        }
      ],
      
      // 个人薪资明细列定义
      detailColumns: [
        {
          title: '薪酬项目',
          dataIndex: 'itemName',
          key: 'itemName'
        },
        {
          title: '项目类型',
          dataIndex: 'itemType',
          key: 'itemType'
        },
        {
          title: '基础数据',
          dataIndex: 'baseData',
          key: 'baseData'
        },
        {
          title: '计算公式',
          dataIndex: 'calculationFormula',
          key: 'calculationFormula'
        },
        {
          title: '金额',
          dataIndex: 'finalAmount',
          key: 'finalAmount',
          scopedSlots: { customRender: 'finalAmount' }
        }
      ],
      
      // 弹窗状态
      detailModalVisible: false,
      currentRecord: {},
      
      // 图表实例
      salaryTrendChart: null,
      departmentSalaryChart: null
    }
  },
  
  mounted() {
    this.checkUserPermission()
    if (this.isPersonalView) {
      this.handlePersonalSearch()
    } else {
      this.loadData()
      this.initCharts()
    }
  },
  
  beforeDestroy() {
    if (this.salaryTrendChart) {
      this.salaryTrendChart.dispose()
    }
    if (this.departmentSalaryChart) {
      this.departmentSalaryChart.dispose()
    }
  },
  
  methods: {
    // 检查用户权限
    checkUserPermission() {
      // TODO: 根据用户权限判断是否显示个人视图还是管理视图
      // 这里暂时设置为个人视图
      this.isPersonalView = true
    },
    
    // 个人薪资查询
    handlePersonalSearch() {
      if (!this.personalQuery.month) {
        this.$message.warning('请选择查询月份')
        return
      }
      
      const month = this.personalQuery.month.format('YYYY-MM')
      getPersonalSalary(month).then(res => {
        if (res.success) {
          this.personalSalaryData = res.data
        } else {
          this.$message.error(res.message || '查询失败')
        }
      })
    },
    
    // 加载数据
    loadData() {
      this.loading = true
      const params = {
        ...this.queryParam,
        calculationMonth: this.queryParam.calculationMonth ? this.queryParam.calculationMonth.format('YYYY-MM') : '',
        current: this.pagination.current,
        size: this.pagination.pageSize
      }
      
      getSalaryInquiryList(params).then(res => {
        if (res.success) {
          this.dataList = res.data.records || []
          this.pagination.total = res.data.total || 0
        }
      }).finally(() => {
        this.loading = false
      })
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
        department: '',
        calculationMonth: dayjs().subtract(1, 'month')
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
    
    // 初始化图表
    initCharts() {
      this.$nextTick(() => {
        this.initSalaryTrendChart()
        this.initDepartmentSalaryChart()
      })
    },
    
    // 初始化薪资趋势图表
    initSalaryTrendChart() {
      const chartDom = document.getElementById('salaryTrendChart')
      if (!chartDom) return
      
      this.salaryTrendChart = echarts.init(chartDom)
      
      const option = {
        title: {
          text: '近6个月薪资趋势'
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: ['2024-07', '2024-08', '2024-09', '2024-10', '2024-11', '2024-12']
        },
        yAxis: {
          type: 'value',
          name: '金额(元)'
        },
        series: [{
          name: '薪资总额',
          type: 'line',
          data: [120000, 132000, 101000, 134000, 90000, 230000],
          smooth: true
        }]
      }
      
      this.salaryTrendChart.setOption(option)
    },
    
    // 初始化部门薪资分布图表
    initDepartmentSalaryChart() {
      const chartDom = document.getElementById('departmentSalaryChart')
      if (!chartDom) return
      
      this.departmentSalaryChart = echarts.init(chartDom)
      
      const option = {
        title: {
          text: '部门薪资分布'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [{
          name: '薪资分布',
          type: 'pie',
          radius: '50%',
          data: [
            { value: 45000, name: '珐琅制作' },
            { value: 32000, name: '咖啡服务' },
            { value: 28000, name: '培训教学' },
            { value: 15000, name: '业务拓展' }
          ],
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }]
      }
      
      this.departmentSalaryChart.setOption(option)
    },
    
    // 导出数据
    handleExport() {
      // TODO: 实现导出功能
      this.$message.info('导出功能开发中...')
    },
    
    // 打印报表
    handlePrint() {
      // TODO: 实现打印功能
      this.$message.info('打印功能开发中...')
    }
  }
}
</script>

<style scoped>
.salary-inquiry-container {
  padding: 24px;
  background: #f7f8fa;
  min-height: 100vh;
}

.personal-salary-detail {
  margin-top: 24px;
}

.action-buttons {
  text-align: left;
}
</style>
