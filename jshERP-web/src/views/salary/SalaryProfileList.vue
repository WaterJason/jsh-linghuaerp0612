<template>
  <div class="salary-profile-container">
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
          <a-form-item label="职位">
            <a-input 
              v-model="queryParam.position" 
              placeholder="请输入职位" 
              style="width: 150px;"
              @pressEnter="handleSearch" />
          </a-form-item>
          <a-form-item label="状态">
            <a-select 
              v-model="queryParam.salaryStatus" 
              placeholder="请选择状态" 
              style="width: 120px;"
              allowClear>
              <a-select-option value="">全部</a-select-option>
              <a-select-option value="ACTIVE">生效</a-select-option>
              <a-select-option value="INACTIVE">失效</a-select-option>
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
      <a-button type="primary" @click="handleAdd" icon="plus">新增薪酬档案</a-button>
      <a-button @click="handleBatchConfig" style="margin-left: 8px;">批量配置</a-button>
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
        
        <template slot="salaryStatus" slot-scope="text">
          <a-tag :color="text === 'ACTIVE' ? 'green' : 'red'">
            {{ text === 'ACTIVE' ? '生效' : '失效' }}
          </a-tag>
        </template>
        
        <template slot="dailyWage" slot-scope="text">
          <span>{{ text ? '¥' + text : '-' }}</span>
        </template>
        
        <template slot="action" slot-scope="text, record">
          <a-button size="small" @click="handleEdit(record)" style="margin-right: 8px;">编辑</a-button>
          <a-button size="small" @click="handleConfig(record)" style="margin-right: 8px;">配置薪酬</a-button>
          <a-dropdown>
            <a-menu slot="overlay">
              <a-menu-item @click="handleUpdateStatus(record)">
                {{ record.salaryStatus === 'ACTIVE' ? '禁用' : '启用' }}
              </a-menu-item>
              <a-menu-item @click="handleDelete(record)">删除</a-menu-item>
            </a-menu>
            <a-button size="small">
              更多 <a-icon type="down" />
            </a-button>
          </a-dropdown>
        </template>
      </a-table>
    </a-card>
    
    <!-- 新增/编辑弹窗 -->
    <salary-profile-modal 
      :visible="modalVisible" 
      :record="currentRecord"
      @ok="handleModalOk" 
      @cancel="handleModalCancel" />
      
    <!-- 薪酬配置弹窗 -->
    <salary-config-modal
      :visible="configModalVisible"
      :employee-id="currentRecord.employeeId"
      @ok="handleConfigModalOk"
      @cancel="handleConfigModalCancel" />
  </div>
</template>

<script>
import { getSalaryProfileList, deleteSalaryProfile, updateSalaryProfileStatus } from '@/api/salary'
import SalaryProfileModal from './components/SalaryProfileModal'
import SalaryConfigModal from './components/SalaryConfigModal'

export default {
  name: 'SalaryProfileList',
  components: {
    SalaryProfileModal,
    SalaryConfigModal
  },
  data() {
    return {
      // 查询参数
      queryParam: {
        employeeName: '',
        department: '',
        position: '',
        salaryStatus: ''
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
          title: '职位',
          dataIndex: 'position',
          key: 'position',
          width: 120
        },
        {
          title: '联系电话',
          dataIndex: 'phone',
          key: 'phone',
          width: 130
        },
        {
          title: '日薪标准',
          dataIndex: 'dailyWage',
          key: 'dailyWage',
          width: 100,
          scopedSlots: { customRender: 'dailyWage' }
        },
        {
          title: '入职时间',
          dataIndex: 'entryDate',
          key: 'entryDate',
          width: 120
        },
        {
          title: '状态',
          dataIndex: 'salaryStatus',
          key: 'salaryStatus',
          width: 80,
          scopedSlots: { customRender: 'salaryStatus' }
        },
        {
          title: '操作',
          key: 'action',
          width: 200,
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
      modalVisible: false,
      configModalVisible: false,
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
        current: this.pagination.current,
        size: this.pagination.pageSize
      }
      
      getSalaryProfileList(params).then(res => {
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
        position: '',
        salaryStatus: ''
      }
      this.handleSearch()
    },
    
    // 表格变化
    handleTableChange(pagination) {
      this.pagination.current = pagination.current
      this.pagination.pageSize = pagination.pageSize
      this.loadData()
    },
    
    // 新增
    handleAdd() {
      this.currentRecord = {}
      this.modalVisible = true
    },
    
    // 编辑
    handleEdit(record) {
      this.currentRecord = { ...record }
      this.modalVisible = true
    },
    
    // 配置薪酬
    handleConfig(record) {
      this.currentRecord = { ...record }
      this.configModalVisible = true
    },
    
    // 删除
    handleDelete(record) {
      this.$confirm({
        title: '确认删除',
        content: `确定要删除员工 ${record.employeeName} 的薪酬档案吗？`,
        onOk: () => {
          deleteSalaryProfile(record.id).then(res => {
            if (res.success) {
              this.$message.success('删除成功')
              this.loadData()
            } else {
              this.$message.error(res.message || '删除失败')
            }
          })
        }
      })
    },
    
    // 更新状态
    handleUpdateStatus(record) {
      const newStatus = record.salaryStatus === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
      
      updateSalaryProfileStatus(record.id, newStatus).then(res => {
        if (res.success) {
          this.$message.success(`${action}成功`)
          this.loadData()
        } else {
          this.$message.error(res.message || `${action}失败`)
        }
      })
    },
    
    // 弹窗确定
    handleModalOk() {
      this.modalVisible = false
      this.loadData()
    },
    
    // 弹窗取消
    handleModalCancel() {
      this.modalVisible = false
    },
    
    // 配置弹窗确定
    handleConfigModalOk() {
      this.configModalVisible = false
      this.$message.success('薪酬配置保存成功')
    },
    
    // 配置弹窗取消
    handleConfigModalCancel() {
      this.configModalVisible = false
    },
    
    // 批量配置
    handleBatchConfig() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要配置的员工')
        return
      }
      // TODO: 实现批量配置功能
      this.$message.info('批量配置功能开发中...')
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
.salary-profile-container {
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
