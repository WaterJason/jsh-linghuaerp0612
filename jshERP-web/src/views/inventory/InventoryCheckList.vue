<template>
  <div class="inventory-check-list">
    <a-card :bordered="false" title="库存盘点管理">
      <!-- 查询条件 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="盘点单号">
                <a-input v-model="queryParam.checkNo" placeholder="请输入盘点单号" />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                  <a-select-option value="DRAFT">草稿</a-select-option>
                  <a-select-option value="CHECKING">盘点中</a-select-option>
                  <a-select-option value="COMPLETED">已完成</a-select-option>
                  <a-select-option value="CANCELLED">已取消</a-select-option>
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
        <a-button type="primary" icon="plus" @click="handleAdd" v-has="'add'">新增盘点</a-button>
        <a-button type="primary" icon="download" @click="handleExport" v-has="'export'">导出</a-button>
        <a-dropdown v-if="selectedRowKeys.length > 0">
          <a-menu slot="overlay">
            <a-menu-item key="1" @click="batchDel">
              <a-icon type="delete" />删除
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
        :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        @change="handleTableChange"
      >
        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)" v-has="'edit'">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleDetail(record)">详情</a>
          <a-divider type="vertical" />
          <a @click="handleStart(record)" v-if="record.status === 'DRAFT'" v-has="'start'">开始盘点</a>
          <a @click="handleCheck(record)" v-if="record.status === 'CHECKING'" v-has="'check'">盘点录入</a>
          <a-divider type="vertical" v-if="record.status === 'DRAFT' || record.status === 'CHECKING'" />
          <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)" v-has="'delete'">
            <a>删除</a>
          </a-popconfirm>
        </span>

        <span slot="status" slot-scope="text">
          <a-tag :color="getStatusColor(text)">{{ getStatusText(text) }}</a-tag>
        </span>

        <span slot="progress" slot-scope="text">
          <a-progress :percent="parseFloat(text || 0)" size="small" />
        </span>
      </a-table>
    </a-card>

    <!-- 新增/编辑对话框 -->
    <inventory-check-modal
      ref="modalForm"
      @ok="modalFormOk"
    />
  </div>
</template>

<script>
import { getAction, deleteAction, postAction } from '@/api/manage'

export default {
  name: 'InventoryCheckList',
  components: {
    // InventoryCheckModal
  },
  data() {
    return {
      // 查询参数
      queryParam: {},
      // 表格数据
      dataSource: [],
      // 表格列配置
      columns: [
        {
          title: '盘点单号',
          dataIndex: 'checkNo',
          width: 150,
          fixed: 'left'
        },
        {
          title: '盘点名称',
          dataIndex: 'checkName',
          width: 200
        },
        {
          title: '仓库',
          dataIndex: 'depotName',
          width: 120
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '盘点进度',
          dataIndex: 'progress',
          width: 120,
          scopedSlots: { customRender: 'progress' }
        },
        {
          title: '创建人',
          dataIndex: 'createUser',
          width: 100
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          width: 150,
          customRender: (text) => {
            return text ? this.$moment(text).format('YYYY-MM-DD HH:mm') : '-'
          }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: 200,
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
      // 选中的行
      selectedRowKeys: []
    }
  },
  
  mounted() {
    this.loadData()
  },
  
  methods: {
    // 加载数据
    loadData(arg) {
      if (arg === 1) {
        this.ipagination.current = 1
      }
      const params = Object.assign({}, this.queryParam, this.isorter)
      params.pageNo = this.ipagination.current
      params.pageSize = this.ipagination.pageSize
      
      this.loading = true
      // 模拟API调用
      setTimeout(() => {
        this.dataSource = []
        this.ipagination.total = 0
        this.loading = false
        this.$message.info('盘点模块功能开发中...')
      }, 500)
    },
    
    // 查询
    searchQuery() {
      this.loadData(1)
    },
    
    // 重置
    searchReset() {
      this.queryParam = {}
      this.loadData(1)
    },
    
    // 新增
    handleAdd() {
      this.$message.info('新增盘点功能开发中...')
    },
    
    // 编辑
    handleEdit(record) {
      this.$message.info('编辑盘点功能开发中...')
    },
    
    // 详情
    handleDetail(record) {
      this.$message.info('盘点详情功能开发中...')
    },
    
    // 开始盘点
    handleStart(record) {
      this.$message.info('开始盘点功能开发中...')
    },
    
    // 盘点录入
    handleCheck(record) {
      this.$message.info('盘点录入功能开发中...')
    },
    
    // 删除
    handleDelete(id) {
      this.$message.info('删除盘点功能开发中...')
    },
    
    // 批量删除
    batchDel() {
      this.$message.info('批量删除功能开发中...')
    },
    
    // 导出
    handleExport() {
      this.$message.info('导出功能开发中...')
    },
    
    // 表格变化
    handleTableChange(pagination, filters, sorter) {
      this.ipagination = pagination
      this.isorter = sorter
      this.loadData()
    },
    
    // 选择变化
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys
    },
    
    // 模态框确认
    modalFormOk() {
      this.loadData()
    },
    
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'DRAFT': 'orange',
        'CHECKING': 'blue',
        'COMPLETED': 'green',
        'CANCELLED': 'red'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'DRAFT': '草稿',
        'CHECKING': '盘点中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return textMap[status] || status
    }
  }
}
</script>

<style scoped>
.inventory-check-list {
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
</style>
