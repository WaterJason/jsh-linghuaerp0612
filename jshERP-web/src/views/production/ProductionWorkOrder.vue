<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="工单编号">
              <a-input v-model="queryParam.workOrderNumber" placeholder="请输入工单编号" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="生产订单">
              <a-input v-model="queryParam.productionOrderNumber" placeholder="请输入生产订单编号" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="工单状态">
              <a-select v-model="queryParam.status" placeholder="请选择状态" allow-clear>
                <a-select-option value="pending">待开始</a-select-option>
                <a-select-option value="processing">进行中</a-select-option>
                <a-select-option value="completed">已完成</a-select-option>
                <a-select-option value="cancelled">已取消</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="工序">
              <a-select v-model="queryParam.process" placeholder="请选择工序" allow-clear>
                <a-select-option value="design">设计</a-select-option>
                <a-select-option value="cutting">切割</a-select-option>
                <a-select-option value="welding">焊接</a-select-option>
                <a-select-option value="polishing">抛光</a-select-option>
                <a-select-option value="painting">上色</a-select-option>
                <a-select-option value="assembly">组装</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="负责人">
              <a-input v-model="queryParam.assignee" placeholder="请输入负责人" allow-clear/>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增工单</a-button>
      <a-button @click="batchDel" type="primary" icon="delete">批量删除</a-button>
      <a-button @click="exportData" type="primary" icon="export">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
          <a-menu-item key="2" @click="batchStart"><a-icon type="play-circle"/>批量开始</a-menu-item>
          <a-menu-item key="3" @click="batchAssign"><a-icon type="user"/>批量分配</a-menu-item>
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

        <template slot="processSlot" slot-scope="text">
          <a-tag color="blue">{{ getProcessText(text) }}</a-tag>
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
                <a @click="handleStart(record)">开始工单</a>
              </a-menu-item>
              <a-menu-item v-if="record.status === 'processing'">
                <a @click="handleComplete(record)">完成工单</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleAssign(record)">分配人员</a>
              </a-menu-item>
              <a-menu-item>
                <a @click="handleReport(record)">工时报告</a>
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

    <work-order-modal ref="modalForm" @ok="modalFormOk"></work-order-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'ProductionWorkOrder',
  mixins:[JeecgListMixin, mixinDevice],
  components: {
    WorkOrderModal: () => import('./modules/WorkOrderModal')
  },
  data () {
    return {
      description: '生产工单管理页面',
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
          title:'工单编号',
          align:"center",
          dataIndex: 'workOrderNumber'
        },
        {
          title:'生产订单',
          align:"center",
          dataIndex: 'productionOrderNumber'
        },
        {
          title:'工序',
          align:"center",
          dataIndex: 'process',
          scopedSlots: { customRender: 'processSlot' }
        },
        {
          title:'产品名称',
          align:"center",
          dataIndex: 'productName'
        },
        {
          title:'数量',
          align:"center",
          dataIndex: 'quantity'
        },
        {
          title:'负责人',
          align:"center",
          dataIndex: 'assignee'
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
          title:'计划开始时间',
          align:"center",
          dataIndex: 'plannedStartTime'
        },
        {
          title:'计划完成时间',
          align:"center",
          dataIndex: 'plannedEndTime'
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
        list: "/production/workorder/list",
        delete: "/production/workorder/delete",
        deleteBatch: "/production/workorder/deleteBatch",
        exportXlsUrl: "/production/workorder/exportXls",
        importExcelUrl: "production/workorder/importExcel",
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
      fieldList.push({type:'string',value:'workOrderNumber',text:'工单编号',dictCode:''})
      fieldList.push({type:'string',value:'productionOrderNumber',text:'生产订单',dictCode:''})
      fieldList.push({type:'string',value:'process',text:'工序',dictCode:'production_process'})
      fieldList.push({type:'string',value:'productName',text:'产品名称',dictCode:''})
      fieldList.push({type:'int',value:'quantity',text:'数量',dictCode:''})
      fieldList.push({type:'string',value:'assignee',text:'负责人',dictCode:''})
      fieldList.push({type:'string',value:'status',text:'状态',dictCode:'workorder_status'})
      fieldList.push({type:'int',value:'progress',text:'进度',dictCode:''})
      fieldList.push({type:'datetime',value:'plannedStartTime',text:'计划开始时间',dictCode:''})
      fieldList.push({type:'datetime',value:'plannedEndTime',text:'计划完成时间',dictCode:''})
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
        pending: '待开始',
        processing: '进行中',
        completed: '已完成',
        cancelled: '已取消'
      };
      return texts[status] || status;
    },
    getProcessText(process) {
      const texts = {
        design: '设计',
        cutting: '切割',
        welding: '焊接',
        polishing: '抛光',
        painting: '上色',
        assembly: '组装'
      };
      return texts[process] || process;
    },
    handleStart(record) {
      this.$confirm({
        title: '确认开始工单',
        content: `确定要开始工单"${record.workOrderNumber}"吗？`,
        onOk: () => {
          this.$message.success('工单已开始');
          this.loadData();
        }
      });
    },
    handleComplete(record) {
      this.$confirm({
        title: '确认完成工单',
        content: `确定要完成工单"${record.workOrderNumber}"吗？`,
        onOk: () => {
          this.$message.success('工单已完成');
          this.loadData();
        }
      });
    },
    handleAssign(record) {
      this.$message.info('打开人员分配界面...');
    },
    handleReport(record) {
      this.$message.info('查看工时报告...');
    },
    batchStart() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要开始的工单');
        return;
      }
      this.$confirm({
        title: '确认批量开始',
        content: `确定要开始选中的 ${this.selectedRowKeys.length} 个工单吗？`,
        onOk: () => {
          this.$message.success('工单已批量开始');
          this.loadData();
          this.selectedRowKeys = [];
        }
      });
    },
    batchAssign() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要分配的工单');
        return;
      }
      this.$message.info('打开批量分配界面...');
    }
  }
}
</script>
<style scoped>
  @import '~@assets/less/common.less';
</style>
