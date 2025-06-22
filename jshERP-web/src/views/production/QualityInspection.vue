<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false" class="quality-inspection">
        <!-- 页面标题 -->
        <div class="page-header">
          <div class="header-left">
            <h2 class="page-title">
              <a-icon type="safety-certificate" />
              质量检验管理
            </h2>
            <div class="page-subtitle">生产质量检验与确认流程管理</div>
          </div>
          <div class="header-right">
            <a-button-group>
              <a-button @click="loadData(1)" :loading="loading">
                <a-icon type="reload" />刷新
              </a-button>
              <a-button @click="handleAdd" type="primary">
                <a-icon type="plus" />新增质检
              </a-button>
              <a-button @click="handleBatchConfirm">
                <a-icon type="check" />批量确认
              </a-button>
            </a-button-group>
          </div>
        </div>

        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="质检编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-input placeholder="请输入质检编号" v-model="queryParam.inspectionNumber"></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="质检类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择质检类型" v-model="queryParam.inspectionType" allowClear>
                    <a-select-option value="INCOMING">来料质检</a-select-option>
                    <a-select-option value="PROCESS">过程质检</a-select-option>
                    <a-select-option value="FINAL">最终质检</a-select-option>
                    <a-select-option value="REWORK">返工质检</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="质检结果" :labelCol="labelCol" :wrapperCol="wrapperCol">
                  <a-select placeholder="请选择质检结果" v-model="queryParam.overallResult" allowClear>
                    <a-select-option value="PASS">合格</a-select-option>
                    <a-select-option value="FAIL">不合格</a-select-option>
                    <a-select-option value="REWORK">需返工</a-select-option>
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
                  <a-form-item label="质检员" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="请输入质检员" v-model="queryParam.inspectorName"></a-input>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="质检时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-range-picker 
                      v-model="queryParam.inspectionTimeRange"
                      format="YYYY-MM-DD"
                      placeholder="['开始时间', '结束时间']" />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="质量等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-select placeholder="请选择质量等级" v-model="queryParam.qualityGrade" allowClear>
                      <a-select-option value="A+">A+ (优秀)</a-select-option>
                      <a-select-option value="A">A (良好)</a-select-option>
                      <a-select-option value="B">B (合格)</a-select-option>
                      <a-select-option value="C">C (一般)</a-select-option>
                      <a-select-option value="D">D (不合格)</a-select-option>
                    </a-select>
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="关键词" :labelCol="labelCol" :wrapperCol="wrapperCol">
                    <a-input placeholder="任务编号/产品名称" v-model="queryParam.keyword"></a-input>
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
          <a-button v-if="btnEnableList.indexOf(2)>-1" @click="batchConfirm('PASS')" icon="check">批量合格</a-button>
          <a-button v-if="btnEnableList.indexOf(2)>-1" @click="batchConfirm('FAIL')" icon="close">批量不合格</a-button>
          <a-button v-if="btnEnableList.indexOf(3)>-1" @click="handleExportXls('质量检验信息')" icon="download">导出</a-button>
          <a-button @click="handleQualityReport" icon="bar-chart">质量报告</a-button>
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
            
            <!-- 质检类型渲染 -->
            <template slot="inspectionType" slot-scope="text">
              <a-tag :color="getInspectionTypeColor(text)">
                {{ getInspectionTypeText(text) }}
              </a-tag>
            </template>
            
            <!-- 质检结果渲染 -->
            <template slot="overallResult" slot-scope="text">
              <a-tag :color="getResultColor(text)">
                <a-icon :type="getResultIcon(text)" />
                {{ getResultText(text) }}
              </a-tag>
            </template>
            
            <!-- 质量等级渲染 -->
            <template slot="qualityGrade" slot-scope="text">
              <a-tag :color="getGradeColor(text)">
                {{ text }}
              </a-tag>
            </template>
            
            <!-- 评分渲染 -->
            <template slot="overallScore" slot-scope="text">
              <div class="score-display">
                <a-rate 
                  :value="text" 
                  disabled 
                  allowHalf 
                  style="font-size: 14px;" />
                <span class="score-text">{{ text }}/5</span>
              </div>
            </template>
            
            <!-- 合格率渲染 -->
            <template slot="qualificationRate" slot-scope="text">
              <div class="rate-display">
                <a-progress 
                  :percent="text" 
                  :status="getProgressStatus(text)"
                  size="small" />
                <span class="rate-text">{{ text }}%</span>
              </div>
            </template>
            
            <!-- 数量信息渲染 -->
            <template slot="quantityInfo" slot-scope="text, record">
              <div class="quantity-info">
                <div class="quantity-item">
                  <span class="label">质检：</span>
                  <span class="value">{{ record.inspectionQuantity || 0 }}</span>
                </div>
                <div class="quantity-item">
                  <span class="label">合格：</span>
                  <span class="value success">{{ record.qualifiedQuantity || 0 }}</span>
                </div>
                <div class="quantity-item">
                  <span class="label">不合格：</span>
                  <span class="value error">{{ record.defectiveQuantity || 0 }}</span>
                </div>
              </div>
            </template>
            
            <!-- 操作按钮 -->
            <span slot="action" slot-scope="text, record">
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a @click="handleDetail(record)">详情</a>
              <a-divider type="vertical" />
              <a-dropdown>
                <a class="ant-dropdown-link">
                  更多 <a-icon type="down" />
                </a>
                <a-menu slot="overlay">
                  <a-menu-item v-if="record.overallResult !== 'PASS'">
                    <a @click="confirmInspection(record, 'PASS')">
                      <a-icon type="check" />确认合格
                    </a>
                  </a-menu-item>
                  <a-menu-item v-if="record.overallResult !== 'FAIL'">
                    <a @click="confirmInspection(record, 'FAIL')">
                      <a-icon type="close" />确认不合格
                    </a>
                  </a-menu-item>
                  <a-menu-item v-if="record.overallResult !== 'REWORK'">
                    <a @click="confirmInspection(record, 'REWORK')">
                      <a-icon type="redo" />需要返工
                    </a>
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item v-if="record.overallResult === 'FAIL'">
                    <a @click="handleDefective(record)">
                      <a-icon type="exclamation-circle" />不合格品处理
                    </a>
                  </a-menu-item>
                  <a-menu-item>
                    <a @click="handlePrintReport(record)">
                      <a-icon type="printer" />打印报告
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
        <quality-inspection-modal ref="modalForm" @ok="modalFormOk" />
        <quality-inspection-detail ref="inspectionDetail" />
        <defective-product-modal ref="defectiveModal" @ok="modalFormOk" />
        <quality-report-modal ref="qualityReportModal" />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import QualityInspectionModal from './modules/QualityInspectionModal'
import QualityInspectionDetail from './modules/QualityInspectionDetail'
import DefectiveProductModal from './modules/DefectiveProductModal'
import QualityReportModal from './modules/QualityReportModal'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import JEllipsis from '@/components/jeecg/JEllipsis'
import dayjs from 'dayjs'

export default {
  name: "QualityInspection",
  mixins: [JeecgListMixin],
  components: {
    QualityInspectionModal,
    QualityInspectionDetail,
    DefectiveProductModal,
    QualityReportModal,
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
        inspectionNumber: '',
        inspectionType: '',
        overallResult: '',
        inspectorName: '',
        inspectionTimeRange: [],
        qualityGrade: '',
        keyword: ''
      },
      
      // API URLs
      url: {
        list: "/production/quality/list",
        delete: "/production/quality/delete",
        deleteBatch: "/production/quality/deleteBatch",
        exportXlsUrl: "/production/quality/exportXls",
        confirm: "/production/quality/confirm",
        start: "/production/quality/start",
        complete: "/production/quality/complete",
        detail: "/production/quality/detail",
        statistics: "/production/quality/statistics",
        standards: "/production/quality/standards"
      },
      
      // 表格列定义
      defColumns: [
        {
          title: '质检编号',
          dataIndex: 'inspectionNumber',
          width: 150,
          fixed: 'left'
        },
        {
          title: '任务编号',
          dataIndex: 'taskNumber',
          width: 120
        },
        {
          title: '产品名称',
          dataIndex: 'productName',
          width: 150
        },
        {
          title: '质检类型',
          dataIndex: 'inspectionType',
          scopedSlots: { customRender: 'inspectionType' },
          width: 100
        },
        {
          title: '数量信息',
          dataIndex: 'quantityInfo',
          scopedSlots: { customRender: 'quantityInfo' },
          width: 150
        },
        {
          title: '合格率',
          dataIndex: 'qualificationRate',
          scopedSlots: { customRender: 'qualificationRate' },
          width: 120
        },
        {
          title: '质检结果',
          dataIndex: 'overallResult',
          scopedSlots: { customRender: 'overallResult' },
          width: 100
        },
        {
          title: '质量等级',
          dataIndex: 'qualityGrade',
          scopedSlots: { customRender: 'qualityGrade' },
          width: 80
        },
        {
          title: '综合评分',
          dataIndex: 'overallScore',
          scopedSlots: { customRender: 'overallScore' },
          width: 120
        },
        {
          title: '质检员',
          dataIndex: 'inspectorName',
          width: 100
        },
        {
          title: '质检时间',
          dataIndex: 'inspectionTime',
          width: 150,
          customRender: (text) => text ? dayjs(text).format('YYYY-MM-DD HH:mm') : ''
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
        'inspectionNumber', 'taskNumber', 'productName', 'inspectionType',
        'quantityInfo', 'qualificationRate', 'overallResult', 'qualityGrade',
        'overallScore', 'inspectorName', 'inspectionTime', 'action'
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

    // 新增质检
    handleAdd() {
      this.$refs.modalForm.add();
      this.$refs.modalForm.title = "新增质检";
    },

    // 编辑质检
    handleEdit(record) {
      this.$refs.modalForm.edit(record);
      this.$refs.modalForm.title = "编辑质检";
    },

    // 查看详情
    handleDetail(record) {
      this.$refs.inspectionDetail.show(record);
    },

    // 确认质检结果
    confirmInspection(record, result) {
      const resultTexts = {
        'PASS': '合格',
        'FAIL': '不合格',
        'REWORK': '需返工'
      };

      this.$confirm({
        title: '确认质检结果',
        content: `确定要将质检记录 "${record.inspectionNumber}" 确认为 "${resultTexts[result]}" 吗？`,
        okText: '确定',
        cancelText: '取消',
        onOk: async () => {
          try {
            const res = await this.$http.post(this.url.confirm, {
              id: record.id,
              taskId: record.taskId,
              overallResult: result,
              confirmTime: new Date().toISOString()
            });

            if (res.code === 200) {
              this.$message.success('质检确认成功');
              this.loadData();
            } else {
              this.$message.error(res.message || '质检确认失败');
            }
          } catch (error) {
            this.$message.error('质检确认失败');
            console.error('Confirm inspection error:', error);
          }
        }
      });
    },

    // 批量确认
    batchConfirm(result) {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要操作的记录');
        return;
      }

      const resultTexts = {
        'PASS': '合格',
        'FAIL': '不合格'
      };

      this.$confirm({
        title: '批量质检确认',
        content: `确定要将选中的 ${this.selectedRowKeys.length} 条记录批量确认为 "${resultTexts[result]}" 吗？`,
        okText: '确定',
        cancelText: '取消',
        onOk: async () => {
          try {
            const res = await this.$http.post(this.url.confirm, {
              ids: this.selectedRowKeys,
              overallResult: result,
              confirmTime: new Date().toISOString()
            });

            if (res.code === 200) {
              this.$message.success(`批量确认${resultTexts[result]}成功`);
              this.loadData();
              this.onClearSelected();
            } else {
              this.$message.error(res.message || `批量确认${resultTexts[result]}失败`);
            }
          } catch (error) {
            this.$message.error(`批量确认${resultTexts[result]}失败`);
            console.error('Batch confirm error:', error);
          }
        }
      });
    },

    // 批量确认
    handleBatchConfirm() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要操作的记录');
        return;
      }

      this.$refs.modalForm.batchConfirm(this.selectionRows);
      this.$refs.modalForm.title = `批量确认 (${this.selectedRowKeys.length}条记录)`;
    },

    // 不合格品处理
    handleDefective(record) {
      this.$refs.defectiveModal.show(record);
    },

    // 打印质检报告
    handlePrintReport(record) {
      this.$message.info('打印质检报告功能开发中...');
      // TODO: 实现打印质检报告功能
    },

    // 质量报告
    handleQualityReport() {
      this.$refs.qualityReportModal.show();
    },

    // 获取质检类型文本
    getInspectionTypeText(type) {
      const typeMap = {
        'INCOMING': '来料质检',
        'PROCESS': '过程质检',
        'FINAL': '最终质检',
        'REWORK': '返工质检'
      };
      return typeMap[type] || type;
    },

    // 获取质检类型颜色
    getInspectionTypeColor(type) {
      const colorMap = {
        'INCOMING': 'blue',
        'PROCESS': 'orange',
        'FINAL': 'green',
        'REWORK': 'purple'
      };
      return colorMap[type] || 'default';
    },

    // 获取结果文本
    getResultText(result) {
      const resultMap = {
        'PASS': '合格',
        'FAIL': '不合格',
        'REWORK': '需返工'
      };
      return resultMap[result] || result;
    },

    // 获取结果颜色
    getResultColor(result) {
      const colorMap = {
        'PASS': 'success',
        'FAIL': 'error',
        'REWORK': 'warning'
      };
      return colorMap[result] || 'default';
    },

    // 获取结果图标
    getResultIcon(result) {
      const iconMap = {
        'PASS': 'check-circle',
        'FAIL': 'close-circle',
        'REWORK': 'exclamation-circle'
      };
      return iconMap[result] || 'question-circle';
    },

    // 获取等级颜色
    getGradeColor(grade) {
      const colorMap = {
        'A+': 'red',
        'A': 'green',
        'B': 'blue',
        'C': 'orange',
        'D': 'default'
      };
      return colorMap[grade] || 'default';
    },

    // 获取进度状态
    getProgressStatus(rate) {
      if (rate >= 100) return 'success';
      if (rate >= 95) return 'normal';
      if (rate >= 80) return 'active';
      return 'exception';
    },

    // 搜索查询
    searchQuery() {
      this.loadData(1);
    },

    // 搜索重置
    searchReset() {
      this.queryParam = {
        inspectionNumber: '',
        inspectionType: '',
        overallResult: '',
        inspectorName: '',
        inspectionTimeRange: [],
        qualityGrade: '',
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
      if (param.inspectionTimeRange && param.inspectionTimeRange.length === 2) {
        param.inspectionTimeStart = param.inspectionTimeRange[0].format('YYYY-MM-DD');
        param.inspectionTimeEnd = param.inspectionTimeRange[1].format('YYYY-MM-DD');
        delete param.inspectionTimeRange;
      }

      return param;
    }
  }
}
</script>

<style scoped>
.quality-inspection {
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

/* 评分显示样式 */
.score-display {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-text {
  margin-top: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

/* 合格率显示样式 */
.rate-display {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.rate-text {
  margin-top: 4px;
  font-size: 12px;
  color: #8c8c8c;
}

/* 数量信息样式 */
.quantity-info {
  font-size: 12px;
}

.quantity-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 2px;
}

.quantity-item:last-child {
  margin-bottom: 0;
}

.quantity-item .label {
  color: #8c8c8c;
}

.quantity-item .value {
  font-weight: 500;
}

.quantity-item .value.success {
  color: #52c41a;
}

.quantity-item .value.error {
  color: #ff4d4f;
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
