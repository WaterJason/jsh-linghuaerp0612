<template>
  <a-modal
    :title="title"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false">

    <div class="report-content">
      <!-- 任务信息展示 -->
      <div class="task-info-section" v-if="currentTask">
        <h4 class="section-title">
          <a-icon type="file-text" />
          任务信息
        </h4>

        <a-row :gutter="16" class="task-info-row">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">任务名称：</span>
              <span class="value">{{ currentTask.taskName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">任务编号：</span>
              <span class="value">{{ currentTask.taskNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">任务类型：</span>
              <span class="value">{{ getTaskTypeText(currentTask.taskType) }}</span>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="16" class="task-info-row">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">制作数量：</span>
              <span class="value">{{ currentTask.taskQuantity }} {{ currentTask.unitName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">已完成：</span>
              <span class="value">{{ currentTask.completedQuantity || 0 }} {{ currentTask.unitName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">剩余数量：</span>
              <span class="value">{{ (currentTask.taskQuantity - (currentTask.completedQuantity || 0)) }} {{ currentTask.unitName }}</span>
            </div>
          </a-col>
        </a-row>

        <a-row :gutter="16" class="task-info-row">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">负责工人：</span>
              <span class="value">{{ currentTask.workerName || '未分配' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">计划完成：</span>
              <span class="value">{{ currentTask.planEndTime || '未设置' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">任务状态：</span>
              <a-tag :color="getStatusColor(currentTask.status)">
                {{ getStatusText(currentTask.status) }}
              </a-tag>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 报工表单 -->
      <div class="report-form-section">
        <h4 class="section-title">
          <a-icon type="form" />
          生产报工
        </h4>

        <a-form :form="form" layout="horizontal">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="报工类型" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                <a-select
                  v-decorator="['reportType', { rules: [{ required: true, message: '请选择报工类型' }] }]"
                  placeholder="选择报工类型"
                  @change="onReportTypeChange">
                  <a-select-option value="PROGRESS">进度报工</a-select-option>
                  <a-select-option value="COMPLETE">完工报工</a-select-option>
                  <a-select-option value="PAUSE">暂停报工</a-select-option>
                  <a-select-option value="RESUME">恢复报工</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="工作日期" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                <a-date-picker
                  v-decorator="['workDate', { rules: [{ required: true, message: '请选择工作日期' }] }]"
                  format="YYYY-MM-DD"
                  placeholder="选择工作日期"
                  style="width: 100%" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="完成数量" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                <a-input-number
                  v-decorator="['completedQuantity', {
                    rules: [
                      { required: true, message: '请输入完成数量' },
                      { validator: validateCompletedQuantity }
                    ]
                  }]"
                  :min="0"
                  :max="maxCompletedQuantity"
                  :precision="2"
                  placeholder="输入完成数量"
                  style="width: 100%"
                  @change="onCompletedQuantityChange">
                  <template slot="addonAfter">{{ currentTask ? currentTask.unitName : '' }}</template>
                </a-input-number>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="工作时长" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                <a-input-number
                  v-decorator="['workHours', { rules: [{ required: true, message: '请输入工作时长' }] }]"
                  :min="0"
                  :precision="1"
                  placeholder="输入工作时长"
                  style="width: 100%">
                  <template slot="addonAfter">小时</template>
                </a-input-number>
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="质量等级" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                <a-select
                  v-decorator="['qualityLevel', { initialValue: 'A' }]"
                  placeholder="选择质量等级">
                  <a-select-option value="A+">A+ (优秀)</a-select-option>
                  <a-select-option value="A">A (良好)</a-select-option>
                  <a-select-option value="B">B (合格)</a-select-option>
                  <a-select-option value="C">C (一般)</a-select-option>
                  <a-select-option value="D">D (不合格)</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="单价工费" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                <a-input-number
                  v-decorator="['unitFee', { initialValue: 0 }]"
                  :min="0"
                  :precision="2"
                  placeholder="输入单价工费"
                  style="width: 100%">
                  <template slot="addonAfter">元</template>
                </a-input-number>
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="16">
            <a-col :span="24">
              <a-form-item label="工作内容" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
                <a-textarea
                  v-decorator="['workContent', { rules: [{ required: true, message: '请输入工作内容' }] }]"
                  placeholder="详细描述本次工作内容、进度情况等"
                  :rows="3" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="16" v-if="showProblemField">
            <a-col :span="24">
              <a-form-item label="问题描述" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
                <a-textarea
                  v-decorator="['problemDescription']"
                  placeholder="如有问题或异常情况，请详细描述"
                  :rows="2" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 工作照片上传 -->
      <div class="photo-upload-section">
        <h4 class="section-title">
          <a-icon type="camera" />
          工作照片
        </h4>

        <div class="upload-area">
          <j-image-upload
            v-model="workPhotos"
            bizPath="production"
            text="上传工作照片"
            :isMultiple="true" />
          <div class="upload-tips">
            <a-icon type="info-circle" />
            支持JPG、PNG、GIF格式，单张不超过20M，最多上传10张
          </div>
        </div>
      </div>

      <!-- 费用计算 -->
      <div class="fee-calculation-section" v-if="showFeeCalculation">
        <h4 class="section-title">
          <a-icon type="calculator" />
          费用计算
        </h4>

        <a-row :gutter="16" class="fee-info">
          <a-col :span="8">
            <div class="fee-item">
              <span class="fee-label">完成数量：</span>
              <span class="fee-value">{{ calculatedQuantity }} {{ currentTask ? currentTask.unitName : '' }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="fee-item">
              <span class="fee-label">单价工费：</span>
              <span class="fee-value">{{ calculatedUnitFee }} 元</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="fee-item">
              <span class="fee-label">总工费：</span>
              <span class="fee-value total-fee">{{ calculatedTotalFee }} 元</span>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- 完工确认 -->
      <div class="completion-section" v-if="isCompleteReport">
        <a-alert
          message="完工确认"
          description="确认此任务已完全完成，完工后将无法继续报工。"
          type="warning"
          showIcon />

        <div style="margin-top: 16px;">
          <a-checkbox v-model="confirmCompletion">
            我确认此任务已完全完成，质量符合要求
          </a-checkbox>
        </div>
      </div>
    </div>

  </a-modal>
</template>

<script>
import { addProductionReport, progressReport, completeReport } from '@/api/production'
import JImageUpload from '@/components/jeecg/JImageUpload'

export default {
  name: "ProductionReportModal",
  components: {
    JImageUpload
  },
  data() {
    return {
      title: "生产报工",
      visible: false,
      confirmLoading: false,
      currentTask: null,
      workPhotos: '',
      confirmCompletion: false,

      // 表单
      form: this.$form.createForm(this)
    }
  },

  computed: {
    // 是否显示问题描述字段
    showProblemField() {
      const reportType = this.form.getFieldValue('reportType');
      return reportType === 'PAUSE' || this.form.getFieldValue('qualityLevel') === 'D';
    },

    // 是否显示费用计算
    showFeeCalculation() {
      const completedQuantity = this.form.getFieldValue('completedQuantity');
      const unitFee = this.form.getFieldValue('unitFee');
      return completedQuantity > 0 && unitFee > 0;
    },

    // 是否为完工报工
    isCompleteReport() {
      return this.form.getFieldValue('reportType') === 'COMPLETE';
    },

    // 最大完成数量
    maxCompletedQuantity() {
      if (!this.currentTask) return 0;
      return this.currentTask.taskQuantity - (this.currentTask.completedQuantity || 0);
    },

    // 计算的数量
    calculatedQuantity() {
      return this.form.getFieldValue('completedQuantity') || 0;
    },

    // 计算的单价
    calculatedUnitFee() {
      return this.form.getFieldValue('unitFee') || 0;
    },

    // 计算的总费用
    calculatedTotalFee() {
      return (this.calculatedQuantity * this.calculatedUnitFee).toFixed(2);
    }
  },

  methods: {
    // 打开报工模态框
    report(task) {
      this.currentTask = task;
      this.visible = true;
      this.title = `生产报工 - ${task.taskName}`;
      this.resetForm();
    },

    // 重置表单
    resetForm() {
      this.form.resetFields();
      this.workPhotos = '';
      this.confirmCompletion = false;

      // 设置默认值
      this.$nextTick(() => {
        this.form.setFieldsValue({
          workDate: this.$moment(),
          reportType: 'PROGRESS',
          qualityLevel: 'A',
          unitFee: this.currentTask ? this.currentTask.unitFee || 0 : 0
        });
      });
    },

    // 报工类型变更
    onReportTypeChange(value) {
      // 完工报工时，完成数量默认为剩余数量
      if (value === 'COMPLETE' && this.currentTask) {
        const remainingQuantity = this.currentTask.taskQuantity - (this.currentTask.completedQuantity || 0);
        this.form.setFieldsValue({
          completedQuantity: remainingQuantity
        });
      }
    },

    // 完成数量变更
    onCompletedQuantityChange(value) {
      // 如果完成数量等于剩余数量，自动设置为完工报工
      if (this.currentTask && value === this.maxCompletedQuantity) {
        this.form.setFieldsValue({
          reportType: 'COMPLETE'
        });
      }
    },

    // 验证完成数量
    validateCompletedQuantity(rule, value, callback) {
      if (value <= 0) {
        callback('完成数量必须大于0');
        return;
      }

      if (value > this.maxCompletedQuantity) {
        callback(`完成数量不能超过剩余数量 ${this.maxCompletedQuantity}`);
        return;
      }

      callback();
    },

    // 确认报工
    handleOk() {
      // 完工报工需要确认
      if (this.isCompleteReport && !this.confirmCompletion) {
        this.$message.warning('请确认任务已完全完成');
        return;
      }

      this.form.validateFields(async (err, values) => {
        if (!err) {
          this.confirmLoading = true;
          try {
            const reportData = {
              taskId: this.currentTask.id,
              taskNumber: this.currentTask.taskNumber,
              reportType: values.reportType,
              workDate: values.workDate.format('YYYY-MM-DD'),
              completedQuantity: values.completedQuantity,
              workHours: values.workHours,
              qualityLevel: values.qualityLevel,
              unitFee: values.unitFee,
              totalFee: this.calculatedTotalFee,
              workContent: values.workContent,
              problemDescription: values.problemDescription || '',
              workPhotos: this.workPhotos,
              isCompleted: values.reportType === 'COMPLETE'
            };

            // 根据报工类型调用不同的API
            let response;
            if (values.reportType === 'COMPLETE') {
              response = await completeReport(reportData);
            } else {
              response = await progressReport(reportData);
            }

            if (response.code === 200) {
              this.$message.success('报工成功！');
              this.visible = false;
              this.$emit('ok');
            } else {
              this.$message.error(response.message || '报工失败');
            }
          } catch (error) {
            console.error('报工失败:', error);
            this.$message.error('报工失败，请重试');
          } finally {
            this.confirmLoading = false;
          }
        }
      });
    },

    // 取消报工
    handleCancel() {
      this.visible = false;
      this.resetForm();
    },

    // 获取任务类型文本
    getTaskTypeText(type) {
      const typeMap = {
        'QISI_DIANLIAN': '掐丝点蓝',
        'PEISHI_ZHIZUO': '配饰制作',
        'HOUGONG_CHULI': '后工处理',
        'ZHILIANG_JIANYAN': '质量检验'
      };
      return typeMap[type] || type;
    },

    // 获取状态文本
    getStatusText(status) {
      const statusMap = {
        'PENDING': '待派单',
        'ASSIGNED': '已派单',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'QUALITY_CHECKED': '已质检',
        'PAUSED': '已暂停'
      };
      return statusMap[status] || status;
    },

    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'PENDING': 'default',
        'ASSIGNED': 'blue',
        'IN_PROGRESS': 'processing',
        'COMPLETED': 'success',
        'QUALITY_CHECKED': 'green',
        'PAUSED': 'warning'
      };
      return colorMap[status] || 'default';
    }
  }
}
</script>

<style scoped>
.report-content {
  padding: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.section-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

/* 任务信息区域 */
.task-info-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.task-info-row {
  margin-bottom: 12px;
}

.task-info-row:last-child {
  margin-bottom: 0;
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #666;
  margin-right: 8px;
  min-width: 80px;
}

.info-item .value {
  color: #262626;
  font-weight: 500;
}

/* 报工表单区域 */
.report-form-section {
  margin-bottom: 24px;
}

/* 照片上传区域 */
.photo-upload-section {
  margin-bottom: 24px;
}

.upload-area {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  padding: 16px;
  background: #fafafa;
}

.upload-tips {
  margin-top: 12px;
  color: #8c8c8c;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.upload-tips .anticon {
  margin-right: 4px;
}

/* 费用计算区域 */
.fee-calculation-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
  border-radius: 6px;
}

.fee-info {
  margin-top: 12px;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 8px;
}

.fee-item:last-child {
  margin-bottom: 0;
}

.fee-label {
  color: #666;
  font-size: 14px;
}

.fee-value {
  color: #262626;
  font-weight: 500;
  font-size: 14px;
}

.total-fee {
  color: #52c41a;
  font-size: 16px;
  font-weight: 600;
}

/* 完工确认区域 */
.completion-section {
  margin-bottom: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .task-info-row .ant-col {
    margin-bottom: 8px;
  }

  .fee-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .fee-label {
    margin-bottom: 4px;
  }
}
</style>
