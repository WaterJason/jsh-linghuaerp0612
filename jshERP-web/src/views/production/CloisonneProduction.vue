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
                <a-form-item label="原材料" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入原材料名称查询" v-model="queryParam.materialName"></a-input>
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
                  <a-form-item label="供应商" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入供应商名称查询" v-model="queryParam.supplierName"></a-input>
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
          <a-button @click="handleAdd" type="primary" icon="plus">新增制作</a-button>
          <a-button @click="batchDel" icon="delete">删除</a-button>
          <a-button @click="handleExportXls('掐丝点蓝制作')" icon="download">导出</a-button>
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
                    <a @click="startProduction(record)">开始制作</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 'IN_PROGRESS'">
                    <a @click="completeProduction(record)">完成制作</a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status !== 'CANCELLED'">
                    <a @click="cancelProduction(record)">取消制作</a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </span>
            
            <template slot="customMaterialImage" slot-scope="text, record">
              <a-popover placement="right" trigger="click" v-if="record.materialImage">
                <template slot="content">
                  <img :src="record.materialImage" width="300px" />
                </template>
                <img :src="record.materialImage" class="material-img" title="查看大图" />
              </a-popover>
              <span v-else>-</span>
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

            <template slot="customAmount" slot-scope="text">
              ¥{{ text ? text.toFixed(2) : '0.00' }}
            </template>
            
          </a-table>
        </div>
        
        <!-- 表单区域 -->
        <cloisonne-production-modal ref="modalForm" @ok="modalFormOk"></cloisonne-production-modal>
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import CloisonneProductionModal from './modules/CloisonneProductionModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'

export default {
  name: "CloisonneProduction",
  mixins: [JeecgListMixin],
  components: {
    CloisonneProductionModal
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
        materialName: '',
        status: undefined,
        supplierName: '',
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
          title: '制作类型',
          dataIndex: 'productionType',
          width: 120,
          scopedSlots: { customRender: 'customProductionType' }
        },
        {
          title: '优先级',
          dataIndex: 'priority',
          width: 100,
          scopedSlots: { customRender: 'customPriority' }
        },
        {
          title: '原材料图片',
          dataIndex: 'materialImage',
          width: 80,
          scopedSlots: { customRender: 'customMaterialImage' }
        },
        {
          title: '原材料名称',
          dataIndex: 'materialName',
          width: 180,
        },
        {
          title: '制作数量',
          dataIndex: 'quantity',
          width: 100,
          scopedSlots: { customRender: 'customQuantity' }
        },
        {
          title: '工费金额',
          dataIndex: 'laborCostAmount',
          width: 100,
          scopedSlots: { customRender: 'customAmount' }
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
        list: "/cloisonne/list",
        delete: "/cloisonne/delete",
        deleteBatch: "/cloisonne/deleteBatch",
        exportXlsUrl: "/cloisonne/exportExcel",
      }
    }
  },
  created() {
    // 添加模拟数据用于演示
    this.loadMockData();
  },
  methods: {
    loadMockData() {
      // 模拟数据 - 展示不同的制作类型和优先级
      this.dataSource = [
        {
          id: 1,
          orderNumber: 'CLO1750564425001',
          productionType: 'ORDER_DRIVEN',
          priority: 'HIGH',
          materialName: '景泰蓝底胎-花瓶',
          materialImage: 'https://via.placeholder.com/60x60/4A90E2/FFFFFF?text=花瓶',
          quantity: 5,
          unitName: '个',
          laborCostAmount: 1500.00,
          supplierName: '北京景泰蓝工艺厂',
          status: 'PENDING',
          startTime: null,
          completeTime: null,
          createTime: '2025-06-22 09:30:00',
          productionReason: 'CUSTOMER_ORDER',
          relatedOrderNumber: 'SO202506220001'
        },
        {
          id: 2,
          orderNumber: 'CLS1750564425002',
          productionType: 'STOCK_DRIVEN',
          priority: 'NORMAL',
          materialName: '景泰蓝底胎-盘子',
          materialImage: 'https://via.placeholder.com/60x60/50C878/FFFFFF?text=盘子',
          quantity: 10,
          unitName: '个',
          laborCostAmount: 2000.00,
          supplierName: '天津工艺品厂',
          status: 'IN_PROGRESS',
          startTime: '2025-06-22 10:00:00',
          completeTime: null,
          createTime: '2025-06-22 08:45:00',
          productionReason: 'STOCK_REPLENISH',
          relatedOrderNumber: null
        },
        {
          id: 3,
          orderNumber: 'CLP1750564425003',
          productionType: 'PLAN_DRIVEN',
          priority: 'NORMAL',
          materialName: '景泰蓝底胎-茶具',
          materialImage: 'https://via.placeholder.com/60x60/FF6B6B/FFFFFF?text=茶具',
          quantity: 3,
          unitName: '套',
          laborCostAmount: 3000.00,
          supplierName: '河北工艺美术厂',
          status: 'COMPLETED',
          startTime: '2025-06-21 14:00:00',
          completeTime: '2025-06-22 11:30:00',
          createTime: '2025-06-21 13:20:00',
          productionReason: 'NEW_PRODUCT',
          relatedOrderNumber: null
        },
        {
          id: 4,
          orderNumber: 'CLU1750564425004',
          productionType: 'URGENT',
          priority: 'URGENT',
          materialName: '景泰蓝底胎-摆件',
          materialImage: 'https://via.placeholder.com/60x60/9B59B6/FFFFFF?text=摆件',
          quantity: 2,
          unitName: '个',
          laborCostAmount: 800.00,
          supplierName: '北京景泰蓝工艺厂',
          status: 'PENDING',
          startTime: null,
          completeTime: null,
          createTime: '2025-06-22 14:15:00',
          productionReason: 'SAMPLE_MAKING',
          relatedOrderNumber: null
        }
      ];
      this.ipagination.total = this.dataSource.length;
    },
    handleAdd() {
      this.$refs.modalForm.action = "add";
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增掐丝点蓝制作";
      this.$refs.modalForm.disableSubmit = false;
    },
    handleEdit(record) {
      this.$refs.modalForm.action = "edit";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑掐丝点蓝制作";
      this.$refs.modalForm.disableSubmit = false;
    },
    handleView(record) {
      this.$refs.modalForm.action = "view";
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "查看掐丝点蓝制作";
      this.$refs.modalForm.disableSubmit = true;
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
    }
  }
}
</script>

<style scoped>
.material-img {
  width: 40px;
  height: 40px;
  object-fit: cover;
  cursor: pointer;
  border-radius: 4px;
}
</style>
