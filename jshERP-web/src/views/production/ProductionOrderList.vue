<template>
  <div class="production-order-list">
    <a-card :bordered="false" title="生产订单管理">
      <!-- 查询条件 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="订单号">
                <a-input v-model="queryParam.orderNo" placeholder="请输入订单号" />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="状态">
                <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                  <a-select-option value="PENDING">待开始</a-select-option>
                  <a-select-option value="IN_PROGRESS">进行中</a-select-option>
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
        <a-button type="primary" icon="plus" @click="handleAdd" v-has="'add'">新增</a-button>
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
    <production-order-modal
      ref="modalForm"
      @ok="modalFormOk"
    />
  </div>
</template>

<script>
import { getAction, deleteAction, postAction } from '@/api/manage'
import ProductionOrderModal from './modules/ProductionOrderModal'

export default {
  name: 'ProductionOrderList',
  components: {
    ProductionOrderModal
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
          title: '订单号',
          dataIndex: 'orderNo',
          width: 150,
          fixed: 'left'
        },
        {
          title: '销售订单ID',
          dataIndex: 'salesOrderId',
          width: 120
        },
        {
          title: '产品ID',
          dataIndex: 'materialId',
          width: 100
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          width: 100
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '进度',
          dataIndex: 'progress',
          width: 120,
          scopedSlots: { customRender: 'progress' }
        },
        {
          title: '交付期限',
          dataIndex: 'deliveryDate',
          width: 150,
          customRender: (text) => {
            return text ? this.$moment(text).format('YYYY-MM-DD HH:mm') : '-'
          }
        },
        {
          title: '总成本',
          dataIndex: 'totalCost',
          width: 100,
          customRender: (text) => {
            return text ? `¥${parseFloat(text).toFixed(2)}` : '-'
          }
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
          width: 150,
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
      getAction('/production/list', params).then(res => {
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
      this.queryParam = {}
      this.loadData(1)
    },
    
    // 新增
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增生产订单'
    },
    
    // 编辑
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑生产订单'
    },
    
    // 详情
    handleDetail(record) {
      this.$refs.modalForm.detail(record)
      this.$refs.modalForm.title = '生产订单详情'
    },
    
    // 删除
    handleDelete(id) {
      deleteAction('/production/delete', { id }).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.error('删除失败：' + res.data.message)
        }
      }).catch(err => {
        this.$message.error('删除失败')
        console.error(err)
      })
    },
    
    // 批量删除
    batchDel() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要删除的记录')
        return
      }
      
      this.$confirm({
        title: '确认删除',
        content: `确定删除选中的 ${this.selectedRowKeys.length} 条记录吗？`,
        onOk: () => {
          const ids = this.selectedRowKeys.join(',')
          deleteAction('/production/batchDelete', { ids }).then(res => {
            if (res.code === 200) {
              this.$message.success('删除成功')
              this.selectedRowKeys = []
              this.loadData()
            } else {
              this.$message.error('删除失败：' + res.data.message)
            }
          }).catch(err => {
            this.$message.error('删除失败')
            console.error(err)
          })
        }
      })
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
        'PENDING': 'orange',
        'IN_PROGRESS': 'blue',
        'COMPLETED': 'green',
        'CANCELLED': 'red'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'PENDING': '待开始',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'CANCELLED': '已取消'
      }
      return textMap[status] || status
    }
  }
}
</script>

<style scoped>
.production-order-list {
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
