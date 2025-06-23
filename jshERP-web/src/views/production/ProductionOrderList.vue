<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="订单编号">
              <a-input v-model="queryParam.orderNumber" placeholder="请输入订单编号" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="产品名称">
              <a-input v-model="queryParam.productName" placeholder="请输入产品名称" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="订单状态">
              <a-select v-model="queryParam.status" placeholder="请选择状态" allow-clear>
                <a-select-option value="pending">待生产</a-select-option>
                <a-select-option value="processing">生产中</a-select-option>
                <a-select-option value="completed">已完成</a-select-option>
                <a-select-option value="cancelled">已取消</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="创建时间">
              <a-range-picker
                v-model="queryParam.createTime"
                style="width: 100%"
                :placeholder="['开始时间', '结束时间']"
                format="YYYY-MM-DD"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="优先级">
              <a-select v-model="queryParam.priority" placeholder="请选择优先级" allow-clear>
                <a-select-option value="high">高</a-select-option>
                <a-select-option value="medium">中</a-select-option>
                <a-select-option value="low">低</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <div class="table-operator">
      <a-button @click="handleAdd" type="primary" icon="plus">新增订单</a-button>
      <a-button @click="batchDel" type="primary" icon="delete">批量删除</a-button>
      <a-button @click="exportData" type="primary" icon="export">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
          <a-menu-item key="2" @click="batchStart"><a-icon type="play-circle"/>批量开始生产</a-menu-item>
          <a-menu-item key="3" @click="batchComplete"><a-icon type="check-circle"/>批量完成</a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <div class="table-wrapper">
      <a-table
        ref="table"
        size="middle"
        :scroll="{x:true}"
        bordered
        rowKey="id"
        :columns="columns"
        :dataSource="dataSource"
        :pagination="ipagination"
        :loading="loading"
        :rowSelection="rowSelection"
        class="j-table-force-nowrap"
        @change="handleTableChange">

        <template slot="statusSlot" slot-scope="text">
          <a-tag :color="getStatusColor(text)">{{ getStatusText(text) }}</a-tag>
        </template>

        <template slot="prioritySlot" slot-scope="text">
          <a-tag :color="getPriorityColor(text)">{{ getPriorityText(text) }}</a-tag>
        </template>

        <template slot="progressSlot" slot-scope="text">
          <a-progress :percent="text" size="small" />
        </template>

        <span slot="action" slot-scope="text, record">
          <a @click="handleEdit(record)">编辑</a>
          <a-divider type="vertical" />
          <a @click="handleView(record)">查看</a>
          <a-divider type="vertical" />
          <a-dropdown>
            <a class="ant-dropdown-link">更多 <a-icon type="down" /></a>
            <a-menu slot="overlay">
              <a-menu-item v-if="record.status === 'pending'">
                <a @click="handleStart(record)">开始生产</a>
              </a-menu-item>
              <a-menu-item v-if="record.status === 'processing'">
                <a @click="handleComplete(record)">完成生产</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleWorkOrder(record)">生成工单</a>
              </a-menu-item>
              <a-menu-item>
                <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
                  <a>删除</a>
                </a-popconfirm>
              </a-menu-item>
            </a-menu>
          </a-dropdown>
        </span>

      </a-table>
    </div>

    <production-order-modal ref="modalForm" @ok="modalFormOk"></production-order-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'ProductionOrderList',
  mixins:[JeecgListMixin, mixinDevice],
  components: {
    ProductionOrderModal: () => import('./modules/ProductionOrderModal')
  },
  data () {
    return {
      description: '生产订单管理页面',
      // 表头
      columns: [
        {
          title: '#',
          dataIndex: '',
          key:'rowIndex',
          width:60,
          align:"center",
          customRender:function (t,r,index) {
            return parseInt(index)+1;
          }
        },
        {
          title:'订单编号',
          align:"center",
          dataIndex: 'orderNumber'
        },
        {
          title:'产品名称',
          align:"center",
          dataIndex: 'productName'
        },
        {
          title:'产品规格',
          align:"center",
          dataIndex: 'specification'
        },
        {
          title:'数量',
          align:"center",
          dataIndex: 'quantity'
        },
        {
          title:'优先级',
          align:"center",
          dataIndex: 'priority',
          scopedSlots: { customRender: 'prioritySlot' }
        },
        {
          title:'状态',
          align:"center",
          dataIndex: 'status',
          scopedSlots: { customRender: 'statusSlot' }
        },
        {
          title:'进度',
          align:"center",
          dataIndex: 'progress',
          scopedSlots: { customRender: 'progressSlot' }
        },
        {
          title:'预计完成时间',
          align:"center",
          dataIndex: 'expectedDate'
        },
        {
          title:'创建时间',
          align:"center",
          dataIndex: 'createTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          align:"center",
          fixed:"right",
          width:180,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: "/production/order/list",
        delete: "/production/order/delete",
        deleteBatch: "/production/order/deleteBatch",
        exportXlsUrl: "/production/order/exportXls",
        importExcelUrl: "production/order/importExcel",
      },
      dictOptions:{},
      superFieldList:[],
    }
  },
  created() {
    this.getSuperFieldList();
  },
  computed: {
    importExcelUrl: function(){
      return `${window._CONFIG['domianURL']}/${this.url.importExcelUrl}`;
    },
  },
  methods: {
    initDictConfig(){
    },
    getSuperFieldList(){
      let fieldList=[];
      fieldList.push({type:'string',value:'orderNumber',text:'订单编号',dictCode:''})
      fieldList.push({type:'string',value:'productName',text:'产品名称',dictCode:''})
      fieldList.push({type:'string',value:'specification',text:'产品规格',dictCode:''})
      fieldList.push({type:'int',value:'quantity',text:'数量',dictCode:''})
      fieldList.push({type:'string',value:'priority',text:'优先级',dictCode:'production_priority'})
      fieldList.push({type:'string',value:'status',text:'状态',dictCode:'production_status'})
      fieldList.push({type:'int',value:'progress',text:'进度',dictCode:''})
      fieldList.push({type:'date',value:'expectedDate',text:'预计完成时间',dictCode:''})
      this.superFieldList = fieldList
    },
    getStatusColor(status) {
      const colors = {
        pending: 'orange',
        processing: 'blue',
        completed: 'green',
        cancelled: 'red'
      };
      return colors[status] || 'default';
    },
    getStatusText(status) {
      const texts = {
        pending: '待生产',
        processing: '生产中',
        completed: '已完成',
        cancelled: '已取消'
      };
      return texts[status] || status;
    },
    getPriorityColor(priority) {
      const colors = {
        high: 'red',
        medium: 'orange',
        low: 'green'
      };
      return colors[priority] || 'default';
    },
    getPriorityText(priority) {
      const texts = {
        high: '高',
        medium: '中',
        low: '低'
      };
      return texts[priority] || priority;
    },
    handleStart(record) {
      this.$confirm({
        title: '确认开始生产',
        content: `确定要开始生产订单"${record.orderNumber}"吗？`,
        onOk: () => {
          this.$message.success('生产已开始');
          this.loadData();
        }
      });
    },
    handleComplete(record) {
      this.$confirm({
        title: '确认完成生产',
        content: `确定要完成生产订单"${record.orderNumber}"吗？`,
        onOk: () => {
          this.$message.success('生产已完成');
          this.loadData();
        }
      });
    },
    handleWorkOrder(record) {
      this.$message.info('正在生成工单...');
      // 这里可以调用生成工单的API
    },
    batchStart() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要开始生产的订单');
        return;
      }
      this.$confirm({
        title: '确认批量开始生产',
        content: `确定要开始生产选中的 ${this.selectedRowKeys.length} 个订单吗？`,
        onOk: () => {
          this.$message.success('批量生产已开始');
          this.loadData();
          this.selectedRowKeys = [];
        }
      });
    },
    batchComplete() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要完成的订单');
        return;
      }
      this.$confirm({
        title: '确认批量完成',
        content: `确定要完成选中的 ${this.selectedRowKeys.length} 个订单吗？`,
        onOk: () => {
          this.$message.success('订单已批量完成');
          this.loadData();
          this.selectedRowKeys = [];
        }
      });
    }
  }
}
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>
