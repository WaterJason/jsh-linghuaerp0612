<template>
  <a-card :bordered="false">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="48">
          <a-col :md="8" :sm="24">
            <a-form-item label="任务名称">
              <a-input v-model="queryParam.taskName" placeholder="请输入任务名称" allow-clear/>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item label="任务状态">
              <a-select v-model="queryParam.status" placeholder="请选择状态" allow-clear>
                <a-select-option value="pending">待处理</a-select-option>
                <a-select-option value="processing">进行中</a-select-option>
                <a-select-option value="completed">已完成</a-select-option>
                <a-select-option value="cancelled">已取消</a-select-option>
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
      <a-button @click="handleAdd" type="primary" icon="plus">新增任务</a-button>
      <a-button @click="batchDel" type="primary" icon="delete">批量删除</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel"><a-icon type="delete"/>删除</a-menu-item>
          <a-menu-item key="2" @click="batchComplete"><a-icon type="check"/>批量完成</a-menu-item>
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
              <a-menu-item v-if="record.status !== 'completed'">
                <a @click="handleComplete(record)">完成任务</a>
              </a-menu-item>
              <a-menu-item v-if="record.status === 'pending'">
                <a @click="handleStart(record)">开始任务</a>
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

    <task-modal ref="modalForm" @ok="modalFormOk"></task-modal>
  </a-card>
</template>

<script>
import '@/assets/less/TableExpand.less'
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: 'TaskList',
  mixins:[JeecgListMixin, mixinDevice],
  components: {
    TaskModal: () => import('./components/TaskModal')
  },
  data () {
    return {
      description: '任务管理页面',
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
          title:'任务名称',
          align:"center",
          dataIndex: 'taskName'
        },
        {
          title:'任务类型',
          align:"center",
          dataIndex: 'taskType'
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
          title:'负责人',
          align:"center",
          dataIndex: 'assignee'
        },
        {
          title:'截止时间',
          align:"center",
          dataIndex: 'dueDate'
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
        list: "/cloisonne/task/list",
        delete: "/cloisonne/task/delete",
        deleteBatch: "/cloisonne/task/deleteBatch",
        exportXlsUrl: "/cloisonne/task/exportXls",
        importExcelUrl: "cloisonne/task/importExcel",
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
      fieldList.push({type:'string',value:'taskName',text:'任务名称',dictCode:''})
      fieldList.push({type:'string',value:'taskType',text:'任务类型',dictCode:''})
      fieldList.push({type:'string',value:'priority',text:'优先级',dictCode:'task_priority'})
      fieldList.push({type:'string',value:'status',text:'状态',dictCode:'task_status'})
      fieldList.push({type:'int',value:'progress',text:'进度',dictCode:''})
      fieldList.push({type:'string',value:'assignee',text:'负责人',dictCode:''})
      fieldList.push({type:'date',value:'dueDate',text:'截止时间',dictCode:''})
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
        pending: '待处理',
        processing: '进行中',
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
        title: '确认开始任务',
        content: `确定要开始任务"${record.taskName}"吗？`,
        onOk: () => {
          // 调用开始任务API
          this.$message.success('任务已开始');
          this.loadData();
        }
      });
    },
    handleComplete(record) {
      this.$confirm({
        title: '确认完成任务',
        content: `确定要完成任务"${record.taskName}"吗？`,
        onOk: () => {
          // 调用完成任务API
          this.$message.success('任务已完成');
          this.loadData();
        }
      });
    },
    batchComplete() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要完成的任务');
        return;
      }
      this.$confirm({
        title: '确认批量完成',
        content: `确定要完成选中的 ${this.selectedRowKeys.length} 个任务吗？`,
        onOk: () => {
          // 调用批量完成API
          this.$message.success('任务已批量完成');
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
