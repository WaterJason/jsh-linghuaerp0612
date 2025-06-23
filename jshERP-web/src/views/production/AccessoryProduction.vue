<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="制作单号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入制作单号查询" v-model="queryParam.orderNumber"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="配饰类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择配饰类型" v-model="queryParam.accessoryType" allowClear>
                    <a-select-option value="NECKLACE">项链</a-select-option>
                    <a-select-option value="BRACELET">手镯</a-select-option>
                    <a-select-option value="EARRING">耳环</a-select-option>
                    <a-select-option value="RING">戒指</a-select-option>
                    <a-select-option value="PENDANT">吊坠</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择状态" v-model="queryParam.status" allowClear>
                    <a-select-option value="PENDING">待制作</a-select-option>
                    <a-select-option value="IN_PROGRESS">制作中</a-select-option>
                    <a-select-option value="COMPLETED">已完成</a-select-option>
                    <a-select-option value="CANCELLED">已取消</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                <a-col :md="6" :sm="24">
                  <a-button type="primary" @click="searchQuery">查询</a-button>
                  <a-button style="margin-left: 8px" @click="searchReset">重置</a-button>
                  <a @click="handleToggleSearch" style="margin-left: 8px">
                    {{ toggleSearchStatus ? '收起' : '展开' }}
                    <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
                  </a>
                </a-col>
              </span>
            </a-row>
            <template v-if="toggleSearchStatus">
              <a-row :gutter="24">
                <a-col :md="6" :sm="24">
                  <a-form-item label="制作工艺师" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入工艺师姓名查询" v-model="queryParam.craftsmanName"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-date-picker 
                      style="width: 100%" 
                      placeholder="请选择开始时间" 
                      v-model="queryParam.startTime"
                      format="YYYY-MM-DD">
                    </a-date-picker>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-date-picker 
                      style="width: 100%" 
                      placeholder="请选择结束时间" 
                      v-model="queryParam.endTime"
                      format="YYYY-MM-DD">
                    </a-date-picker>
                  </a-form-item>
                </a-col>
              </a-row>
            </template>
          </a-form>
        </div>
        
        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin-top: 5px">
          <a-button @click="handleAdd" type="primary" icon="plus">新增配饰制作</a-button>
          <a-button @click="batchDel" icon="delete">删除</a-button>
          <a-button @click="handleExportXls('配饰制作')" icon="download">导出</a-button>
          <a-button @click="handleBatchAssign" icon="team">批量派工</a-button>
        </div>
        
        <!-- table区域 -->
        <div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, columnWidth:'40px'}"
            @change="handleTableChange">
            
            <span slot="action" slot-scope="text, record">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a @click="handleView(record)">查看</a>
              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link">
                  更多 <a-icon type="down" />
                </a>
                <a-menu slot="overlay">
                  <a-menu-item v-if="record.status === 'PENDING'">
                    <a @click="assignCraftsman(record)">派工</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 'PENDING'">
                    <a @click="startProduction(record)">开始制作</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 'IN_PROGRESS'">
                    <a @click="completeProduction(record)">完成制作</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status !== 'CANCELLED'">
                    <a @click="cancelProduction(record)">取消制作</a>
                  </a-menu-item>
                  <a-menu-item>
                    <a @click="viewProgress(record)">查看进度</a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </span>
            
            <template slot="customAccessoryType" slot-scope="text, record">
              <a-tag v-if="record.accessoryType === 'NECKLACE'" color="blue">项链</a-tag>
              <a-tag v-if="record.accessoryType === 'BRACELET'" color="green">手镯</a-tag>
              <a-tag v-if="record.accessoryType === 'EARRING'" color="purple">耳环</a-tag>
              <a-tag v-if="record.accessoryType === 'RING'" color="orange">戒指</a-tag>
              <a-tag v-if="record.accessoryType === 'PENDANT'" color="cyan">吊坠</a-tag>
            </template>
            
            <template slot="customProductionType" slot-scope="text, record">
              <a-tag v-if="record.productionType === 'ORDER_DRIVEN'" color="blue">订单驱动</a-tag>
              <a-tag v-if="record.productionType === 'STOCK_DRIVEN'" color="green">库存驱动</a-tag>
              <a-tag v-if="record.productionType === 'PLAN_DRIVEN'" color="purple">计划驱动</a-tag>
              <a-tag v-if="record.productionType === 'URGENT'" color="red">紧急制作</a-tag>
            </template>
            
            <template slot="customPriority" slot-scope="text, record">
              <a-tag v-if="record.priority === 'LOW'" color="default">低</a-tag>
              <a-tag v-if="record.priority === 'NORMAL'" color="blue">普通</a-tag>
              <a-tag v-if="record.priority === 'HIGH'" color="orange">高</a-tag>
              <a-tag v-if="record.priority === 'URGENT'" color="red">紧急</a-tag>
            </template>
            
            <template slot="customStatus" slot-scope="text, record">
              <a-tag v-if="record.status === 'PENDING'" color="orange">待制作</a-tag>
              <a-tag v-if="record.status === 'IN_PROGRESS'" color="blue">制作中</a-tag>
              <a-tag v-if="record.status === 'COMPLETED'" color="green">已完成</a-tag>
              <a-tag v-if="record.status === 'CANCELLED'" color="red">已取消</a-tag>
            </template>
            
            <template slot="customQuantity" slot-scope="text, record">
              {{ text }} {{ record.unitName }}
            </template>
            
            <template slot="customSalary" slot-scope="text">
              ¥{{ text ? text.toFixed(2) : '0.00' }}
            </template>
            
            <template slot="customProgress" slot-scope="text, record">
              <a-progress 
                :percent="record.progressPercent || 0" 
                :size="'small'"
                :status="record.status === 'COMPLETED' ? 'success' : 'active'" />
            </template>
            
          </a-table>
        </div>
        
        <!-- 表单区域 -->
        <accessory-production-modal ref="modalForm" @ok="modalFormOk"></accessory-production-modal>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import AccessoryProductionModal from './modules/AccessoryProductionModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: "AccessoryProduction",
  mixins: [JeecgListMixin],
  components: {
    AccessoryProductionModal
  },
  data() {
    return {
      labelCol: {
        span: 5
      },
      wrapperCol: {
        span: 18,
        offset: 1
      },
      // 查询条件
      queryParam: {
        orderNumber: '',
        accessoryType: undefined,
        status: undefined,
        craftsmanName: '',
        startTime: undefined,
        endTime: undefined
      },
      // 表格列定义
      columns: [
        {
          title: '操作',
          dataIndex: 'action',
          align: "center",
          width: 120,
          scopedSlots: { customRender: 'action' },
        },
        {
          title: '制作单号',
          dataIndex: 'orderNumber',
          width: 150,
        },
        {
          title: '配饰类型',
          dataIndex: 'accessoryType',
          width: 100,
          scopedSlots: { customRender: 'customAccessoryType' }
        },
        {
          title: '制作类型',
          dataIndex: 'productionType',
          width: 100,
          scopedSlots: { customRender: 'customProductionType' }
        },
        {
          title: '优先级',
          dataIndex: 'priority',
          width: 80,
          scopedSlots: { customRender: 'customPriority' }
        },
        {
          title: '配饰名称',
          dataIndex: 'accessoryName',
          width: 180,
        },
        {
          title: '制作数量',
          dataIndex: 'quantity',
          width: 100,
          scopedSlots: { customRender: 'customQuantity' }
        },
        {
          title: '制作工艺师',
          dataIndex: 'craftsmanName',
          width: 120,
        },
        {
          title: '薪酬金额',
          dataIndex: 'salaryAmount',
          width: 100,
          scopedSlots: { customRender: 'customSalary' }
        },
        {
          title: '制作进度',
          dataIndex: 'progressPercent',
          width: 120,
          scopedSlots: { customRender: 'customProgress' }
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'customStatus' }
        },
        {
          title: '开始时间',
          dataIndex: 'startTime',
          width: 150,
        },
        {
          title: '完成时间',
          dataIndex: 'completeTime',
          width: 150,
        },
        {
          title: '创建时间',
          dataIndex: 'createTime',
          width: 150,
        }
      ],
      url: {
        list: "/accessory/list",
        delete: "/accessory/delete",
        deleteBatch: "/accessory/deleteBatch",
        exportXlsUrl: "/accessory/exportExcel",
      }
    }
  },
  created() {
    this.loadMockData();
  },
  methods: {
    loadMockData() {
      // 模拟配饰制作数据
      this.dataSource = [
        {
          id: 1,
          orderNumber: 'ACO1750565600001',
          accessoryType: 'NECKLACE',
          productionType: 'ORDER_DRIVEN',
          priority: 'HIGH',
          accessoryName: '景泰蓝花瓶项链',
          quantity: 2,
          unitName: '条',
          craftsmanName: '李师傅',
          salaryAmount: 800.00,
          progressPercent: 0,
          status: 'PENDING',
          startTime: null,
          completeTime: null,
          createTime: '2025-06-22 15:30:00',
          productionReason: 'CUSTOMER_ORDER',
          relatedOrderNumber: 'SO202506220005'
        },
        {
          id: 2,
          orderNumber: 'ACS1750565600002',
          accessoryType: 'BRACELET',
          productionType: 'STOCK_DRIVEN',
          priority: 'NORMAL',
          accessoryName: '景泰蓝盘子手镯',
          quantity: 5,
          unitName: '个',
          craftsmanName: '王师傅',
          salaryAmount: 1200.00,
          progressPercent: 65,
          status: 'IN_PROGRESS',
          startTime: '2025-06-22 09:00:00',
          completeTime: null,
          createTime: '2025-06-22 08:30:00',
          productionReason: 'STOCK_REPLENISH',
          relatedOrderNumber: null
        },
        {
          id: 3,
          orderNumber: 'ACP1750565600003',
          accessoryType: 'EARRING',
          productionType: 'PLAN_DRIVEN',
          priority: 'NORMAL',
          accessoryName: '景泰蓝茶具耳环',
          quantity: 10,
          unitName: '对',
          craftsmanName: '张师傅',
          salaryAmount: 1500.00,
          progressPercent: 100,
          status: 'COMPLETED',
          startTime: '2025-06-21 10:00:00',
          completeTime: '2025-06-22 16:30:00',
          createTime: '2025-06-21 09:15:00',
          productionReason: 'NEW_PRODUCT',
          relatedOrderNumber: null
        },
        {
          id: 4,
          orderNumber: 'ACU1750565600004',
          accessoryType: 'PENDANT',
          productionType: 'URGENT',
          priority: 'URGENT',
          accessoryName: '景泰蓝摆件吊坠',
          quantity: 1,
          unitName: '个',
          craftsmanName: '赵师傅',
          salaryAmount: 600.00,
          progressPercent: 30,
          status: 'IN_PROGRESS',
          startTime: '2025-06-22 14:00:00',
          completeTime: null,
          createTime: '2025-06-22 13:45:00',
          productionReason: 'SAMPLE_MAKING',
          relatedOrderNumber: null
        }
      ];
      this.ipagination.total = this.dataSource.length;
    },
    handleAdd() {
      this.$refs.modalForm.action = "add";
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增配饰制作";
      this.$refs.modalForm.disableSubmit = false;
    },
    handleEdit(record) {
      this.$refs.modalForm.action = "edit";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑配饰制作";
      this.$refs.modalForm.disableSubmit = false;
    },
    handleView(record) {
      this.$refs.modalForm.action = "view";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "查看配饰制作";
      this.$refs.modalForm.disableSubmit = true;
    },
    assignCraftsman(record) {
      this.$message.info('派工功能开发中...');
    },
    startProduction(record) {
      let that = this;
      this.$confirm({
        title: "确认操作",
        content: "确认开始制作吗？",
        onOk: function () {
          // TODO: 调用开始制作API
          that.$message.success('开始制作成功！');
          that.loadData();
        }
      });
    },
    completeProduction(record) {
      let that = this;
      this.$confirm({
        title: "确认操作",
        content: "确认完成制作吗？",
        onOk: function () {
          // TODO: 调用完成制作API
          that.$message.success('完成制作成功！');
          that.loadData();
        }
      });
    },
    cancelProduction(record) {
      let that = this;
      this.$confirm({
        title: "确认操作",
        content: "确认取消制作吗？",
        onOk: function () {
          // TODO: 调用取消制作API
          that.$message.success('取消制作成功！');
          that.loadData();
        }
      });
    },
    viewProgress(record) {
      this.$message.info('查看进度功能开发中...');
    },
    handleBatchAssign() {
      this.$message.info('批量派工功能开发中...');
    }
  }
}
</script>

<style scoped>
.ant-progress {
  margin: 0;
}
</style>
