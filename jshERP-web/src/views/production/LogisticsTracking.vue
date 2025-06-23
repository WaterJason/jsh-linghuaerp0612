<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false" class="logistics-tracking">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-left">
            <h2 class="page-title">
              <a-icon type="car" />
              物流追踪管理
            </h2>
            <div class="page-subtitle">生产物流全流程追踪与管理</div>
          </div>
          <div class="header-right">
            <a-button-group>
              <a-button @click="loadData(1)" :loading="loading">
                <a-icon type="reload" />刷新
              </a-button>
              <a-button @click="handleAdd" type="primary">
                <a-icon type="plus" />新增物流单
              </a-button>
              <a-button @click="handleBatchUpdate">
                <a-icon type="edit" />批量更新
              </a-button>
            </a-button-group>
          </div>
        </div>

        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="追踪编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入追踪编号" v-model="queryParam.trackingNumber"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="物流类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择物流类型" v-model="queryParam.logisticsType" allowClear>
                    <a-select-option value="MATERIAL_IN">原料入库</a-select-option>
                    <a-select-option value="SEMI_PRODUCT_OUT">半成品出库</a-select-option>
                    <a-select-option value="PRODUCT_IN">成品入库</a-select-option>
                    <a-select-option value="PRODUCT_OUT">成品出库</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="物流状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择物流状态" v-model="queryParam.status" allowClear>
                    <a-select-option value="PENDING">待发货</a-select-option>
                    <a-select-option value="SHIPPED">已发货</a-select-option>
                    <a-select-option value="IN_TRANSIT">运输中</a-select-option>
                    <a-select-option value="ARRIVED">已到达</a-select-option>
                    <a-select-option value="RECEIVED">已收货</a-select-option>
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
                  <a-form-item label="承运人" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入承运人" v-model="queryParam.carrierName"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="发货时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-range-picker 
                      v-model="queryParam.shipTimeRange"
                      format="YYYY-MM-DD"
                      placeholder="['开始时间', '结束时间']" />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="到达时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-range-picker 
                      v-model="queryParam.arrivalTimeRange"
                      format="YYYY-MM-DD"
                      placeholder="['开始时间', '结束时间']" />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="工单编号/产品名称" v-model="queryParam.keyword"></a-input>
                  </a-form-item>
                </a-col>
              </a-row>
            </template>
          </a-form>
        </div>

        <!-- 操作按钮区域 -->
        <div class="table-operator" style="margin-top: 5px">
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="handleAdd" type="primary" icon="plus">新增</a-button>
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="batchDel" icon="delete">删除</a-button>
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="batchUpdateStatus('SHIPPED')" icon="car">批量发货</a-button>
          <a-button v-if="btnEnableList.indexOf(1)>-1" @click="batchUpdateStatus('RECEIVED')" icon="check">批量收货</a-button>
          <a-button v-if="btnEnableList.indexOf(3)>-1" @click="handleExportXls('物流追踪信息')" icon="download">导出</a-button>
          <a-button @click="handlePrintLabels" icon="printer">打印标签</a-button>
          <a-popover trigger="click" placement="right">
            <template slot="content">
              <a-checkbox-group @change="onColChange" v-model="settingDataIndex" :defaultValue="settingDataIndex">
                <a-row style="width: 500px">
                  <template v-for="(item,index) in defColumns">
                    <template>
                      <a-col :span="8">
                        <a-checkbox :value="item.dataIndex">
                          <j-ellipsis :value="item.title" :length="10"></j-ellipsis>
                        </a-checkbox>
                      </a-col>
                    </template>
                  </template>
                </a-row>
                <a-row style="padding-top: 10px;">
                  <a-col>
                    恢复默认列配置：<a-button @click="handleRestDefault" type="link" size="small">恢复默认</a-button>
                  </a-col>
                </a-row>
              </a-checkbox-group>
            </template>
            <a-button icon="setting">列设置</a-button>
          </a-popover>
        </div>

        <!-- 表格区域 -->
        <div>
          <a-table
            ref="table"
            size="middle"
            bordered
            rowKey="id"
            :columns="columns"
            :dataSource="dataSource"
            :pagination="ipagination"
            :scroll="scroll"
            :loading="loading"
            :rowSelection="{selectedRowKeys: selectedRowKeys, onChange: onSelectChange, columnWidth:'40px'}"
            @change="handleTableChange">
            
            <!-- 物流类型渲染 -->
            <template slot="logisticsType" slot-scope="text">
              <a-tag :color="getLogisticsTypeColor(text)">
                {{ getLogisticsTypeText(text) }}
              </a-tag>
            </template>
            
            <!-- 状态渲染 -->
            <template slot="status" slot-scope="text">
              <a-tag :color="getStatusColor(text)">
                <a-icon :type="getStatusIcon(text)" />
                {{ getStatusText(text) }}
              </a-tag>
            </template>
            
            <!-- 进度渲染 -->
            <template slot="progress" slot-scope="text, record">
              <div class="progress-wrapper">
                <a-progress 
                  :percent="getProgressPercent(record)" 
                  :status="getProgressStatus(record)"
                  size="small" />
                <div class="progress-text">{{ getProgressText(record) }}</div>
              </div>
            </template>
            
            <!-- 时间信息渲染 -->
            <template slot="timeInfo" slot-scope="text, record">
              <div class="time-info">
                <div v-if="record.shipTime" class="time-item">
                  <a-icon type="export" />
                  发货: {{ formatTime(record.shipTime) }}
                </div>
                <div v-if="record.arrivalTime" class="time-item">
                  <a-icon type="environment" />
                  到达: {{ formatTime(record.arrivalTime) }}
                </div>
                <div v-if="record.receiveTime" class="time-item">
                  <a-icon type="check-circle" />
                  收货: {{ formatTime(record.receiveTime) }}
                </div>
              </div>
            </template>
            
            <!-- 操作按钮 -->
            <span slot="action" slot-scope="text, record">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a @click="handleTrack(record)">追踪</a>
              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link">
                  更多 <a-icon type="down" />
                </a>
                <a-menu slot="overlay">
                  <a-menu-item v-if="record.status === 'PENDING'">
                    <a @click="updateStatus(record, 'SHIPPED')">
                      <a-icon type="car" />发货
                    </a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 'SHIPPED'">
                    <a @click="updateStatus(record, 'IN_TRANSIT')">
                      <a-icon type="loading" />运输中
                    </a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 'IN_TRANSIT'">
                    <a @click="updateStatus(record, 'ARRIVED')">
                      <a-icon type="environment" />已到达
                    </a>
                  </a-menu-item>
                  <a-menu-item v-if="record.status === 'ARRIVED'">
                    <a @click="updateStatus(record, 'RECEIVED')">
                      <a-icon type="check" />确认收货
                    </a>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item>
                    <a @click="handlePrintLabel(record)">
                      <a-icon type="printer" />打印标签
                    </a>
                  </a-menu-item>
                  <a-menu-item v-if="btnEnableList.indexOf(1)>-1">
                    <a @click="handleDelete(record.id)" style="color: #ff4d4f;">
                      <a-icon type="delete" />删除
                    </a>
                  </a-menu-item>
                </a-menu>
              </a-dropdown>
            </span>
          </a-table>
        </div>

        <!-- 模态框 -->
        <logistics-tracking-modal ref="modalForm" @ok="modalFormOk" />
        <logistics-tracking-detail ref="trackingDetail" />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import LogisticsTrackingModal from './modules/LogisticsTrackingModal'
import LogisticsTrackingDetail from './modules/LogisticsTrackingDetail'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import JEllipsis from '@/components/jeecg/JEllipsis'
import dayjs from 'dayjs'

export default {
  name: "LogisticsTracking",
  mixins: [JeecgListMixin],
  components: {
    LogisticsTrackingModal,
    LogisticsTrackingDetail,
    JEllipsis
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
      
      // 查询参数
      queryParam: {
        trackingNumber: '',
        logisticsType: '',
        status: '',
        carrierName: '',
        shipTimeRange: [],
        arrivalTimeRange: [],
        keyword: ''
      },
      
      // API URLs
      url: {
        list: "/logistics/tracking/list",
        delete: "/logistics/tracking/delete",
        deleteBatch: "/logistics/tracking/deleteBatch",
        exportXlsUrl: "/logistics/tracking/exportXls",
        updateStatus: "/logistics/tracking/updateStatus"
      },
      
      // 表格列定义
      defColumns: [
        {
          title: '追踪编号',
          dataIndex: 'trackingNumber',
          width: 150,
          fixed: 'left'
        },
        {
          title: '工单编号',
          dataIndex: 'workOrderNumber',
          width: 120
        },
        {
          title: '物流类型',
          dataIndex: 'logisticsType',
          scopedSlots: { customRender: 'logisticsType' },
          width: 100
        },
        {
          title: '产品信息',
          dataIndex: 'productName',
          width: 150
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          width: 80,
          customRender: (text, record) => `${text} ${record.unitName || ''}`
        },
        {
          title: '状态',
          dataIndex: 'status',
          scopedSlots: { customRender: 'status' },
          width: 100
        },
        {
          title: '进度',
          dataIndex: 'progress',
          scopedSlots: { customRender: 'progress' },
          width: 120
        },
        {
          title: '承运人',
          dataIndex: 'carrierName',
          width: 100
        },
        {
          title: '联系方式',
          dataIndex: 'carrierPhone',
          width: 120
        },
        {
          title: '时间信息',
          dataIndex: 'timeInfo',
          scopedSlots: { customRender: 'timeInfo' },
          width: 200
        },
        {
          title: '备注',
          dataIndex: 'remark',
          width: 150
        },
        {
          title: '操作',
          dataIndex: 'action',
          scopedSlots: { customRender: 'action' },
          fixed: 'right',
          width: 120
        }
      ],
      
      // 默认显示的列
      defDataIndex: [
        'trackingNumber', 'workOrderNumber', 'logisticsType', 'productName',
        'quantity', 'status', 'progress', 'carrierName', 'timeInfo', 'action'
      ]
    }
  },

  created() {
    this.initColumnsSetting();
  },

  methods: {
    // 初始化字典配置
    initDictConfig() {
      // 初始化字典数据
    },

    // 新增物流单
    handleAdd() {
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增物流单";
    },

    // 编辑物流单
    handleEdit(record) {
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑物流单";
    },

    // 查看追踪详情
    handleTrack(record) {
      this.$refs.trackingDetail.show(record);
    },

    // 更新状态
    async updateStatus(record, newStatus) {
      const statusTexts = {
        'PENDING': '待发货',
        'SHIPPED': '已发货',
        'IN_TRANSIT': '运输中',
        'ARRIVED': '已到达',
        'RECEIVED': '已收货'
      };

      this.$confirm({
        title: '确认状态变更',
        content: `确定要将物流单 "${record.trackingNumber}" 状态变更为 "${statusTexts[newStatus]}" 吗？`,
        okText: '确定',
        cancelText: '取消',
        onOk: async () => {
          try {
            const res = await this.$http.post(this.url.updateStatus, {
              id: record.id,
              status: newStatus,
              updateTime: new Date().toISOString()
            });

            if (res.code === 200) {
              this.$message.success('状态更新成功');
              this.loadData();
            } else {
              this.$message.error(res.message || '状态更新失败');
            }
          } catch (error) {
            this.$message.error('状态更新失败');
            console.error('Update status error:', error);
          }
        }
      });
    },

    // 批量更新状态
    batchUpdateStatus(status) {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要操作的记录');
        return;
      }

      const statusTexts = {
        'SHIPPED': '发货',
        'RECEIVED': '收货'
      };

      this.$confirm({
        title: '批量状态变更',
        content: `确定要将选中的 ${this.selectedRowKeys.length} 条记录批量${statusTexts[status]}吗？`,
        okText: '确定',
        cancelText: '取消',
        onOk: async () => {
          try {
            const res = await this.$http.post(this.url.updateStatus, {
              ids: this.selectedRowKeys,
              status: status,
              updateTime: new Date().toISOString()
            });

            if (res.code === 200) {
              this.$message.success(`批量${statusTexts[status]}成功`);
              this.loadData();
              this.onClearSelected();
            } else {
              this.$message.error(res.message || `批量${statusTexts[status]}失败`);
            }
          } catch (error) {
            this.$message.error(`批量${statusTexts[status]}失败`);
            console.error('Batch update status error:', error);
          }
        }
      });
    },

    // 批量更新
    handleBatchUpdate() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要操作的记录');
        return;
      }

      this.$refs.modalForm.batchEdit(this.selectionRows);
      this.$refs.modalForm.title = `批量编辑 (${this.selectedRowKeys.length}条记录)`;
    },

    // 打印标签
    handlePrintLabel(record) {
      this.$message.info('打印标签功能开发中...');
      // TODO: 实现打印标签功能
    },

    // 批量打印标签
    handlePrintLabels() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要打印的记录');
        return;
      }

      this.$message.info(`批量打印 ${this.selectedRowKeys.length} 个标签功能开发中...`);
      // TODO: 实现批量打印标签功能
    },

    // 获取物流类型文本
    getLogisticsTypeText(type) {
      const typeMap = {
        'MATERIAL_IN': '原料入库',
        'SEMI_PRODUCT_OUT': '半成品出库',
        'PRODUCT_IN': '成品入库',
        'PRODUCT_OUT': '成品出库'
      };
      return typeMap[type] || type;
    },

    // 获取物流类型颜色
    getLogisticsTypeColor(type) {
      const colorMap = {
        'MATERIAL_IN': 'blue',
        'SEMI_PRODUCT_OUT': 'orange',
        'PRODUCT_IN': 'green',
        'PRODUCT_OUT': 'purple'
      };
      return colorMap[type] || 'default';
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待发货',
        'SHIPPED': '已发货',
        'IN_TRANSIT': '运输中',
        'ARRIVED': '已到达',
        'RECEIVED': '已收货'
      };
      return statusMap[status] || status;
    },

    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'PENDING': 'default',
        'SHIPPED': 'blue',
        'IN_TRANSIT': 'processing',
        'ARRIVED': 'warning',
        'RECEIVED': 'success'
      };
      return colorMap[status] || 'default';
    },

    // 获取状态图标
    getStatusIcon(status) {
      const iconMap = {
        'PENDING': 'clock-circle',
        'SHIPPED': 'car',
        'IN_TRANSIT': 'loading',
        'ARRIVED': 'environment',
        'RECEIVED': 'check-circle'
      };
      return iconMap[status] || 'question-circle';
    },

    // 获取进度百分比
    getProgressPercent(record) {
      const statusProgress = {
        'PENDING': 0,
        'SHIPPED': 25,
        'IN_TRANSIT': 50,
        'ARRIVED': 75,
        'RECEIVED': 100
      };
      return statusProgress[record.status] || 0;
    },

    // 获取进度状态
    getProgressStatus(record) {
      if (record.status === 'RECEIVED') return 'success';
      if (record.status === 'IN_TRANSIT') return 'active';
      return 'normal';
    },

    // 获取进度文本
    getProgressText(record) {
      const percent = this.getProgressPercent(record);
      return `${percent}%`;
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      return dayjs(time).format('MM-DD HH:mm');
    },

    // 搜索查询
    searchQuery() {
      this.loadData(1);
    },

    // 搜索重置
    searchReset() {
      this.queryParam = {
        trackingNumber: '',
        logisticsType: '',
        status: '',
        carrierName: '',
        shipTimeRange: [],
        arrivalTimeRange: [],
        keyword: ''
      };
      this.loadData(1);
    },

    // 获取查询参数
    getQueryParams() {
      let sqp = {};
      let param = Object.assign(sqp, this.queryParam, this.isorter, this.filters);
      param.current = this.ipagination.current;
      param.size = this.ipagination.pageSize;

      // 处理时间范围
      if (param.shipTimeRange && param.shipTimeRange.length === 2) {
        param.shipTimeStart = param.shipTimeRange[0].format('YYYY-MM-DD');
        param.shipTimeEnd = param.shipTimeRange[1].format('YYYY-MM-DD');
        delete param.shipTimeRange;
      }

      if (param.arrivalTimeRange && param.arrivalTimeRange.length === 2) {
        param.arrivalTimeStart = param.arrivalTimeRange[0].format('YYYY-MM-DD');
        param.arrivalTimeEnd = param.arrivalTimeRange[1].format('YYYY-MM-DD');
        delete param.arrivalTimeRange;
      }

      return param;
    }
  }
}
</script>

<style scoped>
.logistics-tracking {
  background: #fff;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.header-left {
  flex: 1;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  display: flex;
  align-items: center;
}

.page-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

.page-subtitle {
  margin-top: 4px;
  color: #8c8c8c;
  font-size: 14px;
}

.header-right {
  flex-shrink: 0;
}

/* 进度样式 */
.progress-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.progress-text {
  margin-top: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

/* 时间信息样式 */
.time-info {
  font-size: 12px;
}

.time-item {
  display: flex;
  align-items: center;
  margin-bottom: 2px;
  color: #595959;
}

.time-item .anticon {
  margin-right: 4px;
  color: #1890ff;
}

.time-item:last-child {
  margin-bottom: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-right {
    margin-top: 16px;
    width: 100%;
  }

  .header-right .ant-btn-group {
    width: 100%;
  }

  .header-right .ant-btn {
    flex: 1;
  }
}
</style>
