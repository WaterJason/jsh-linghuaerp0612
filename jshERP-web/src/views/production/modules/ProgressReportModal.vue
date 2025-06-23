<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    
    <div class="progress-report-content">
      <div v-if="currentTask" class="task-info-section">
        <h4>任务信息</h4>
        <a-row :gutter="16">
          <a-col :span="12">
            <div class="info-item">
              <span class="info-label">任务编号：</span>
              <span class="info-value">{{ currentTask.taskNumber }}</span>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="info-item">
              <span class="info-label">产品名称：</span>
              <span class="info-value">{{ currentTask.productName }}</span>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="info-item">
              <span class="info-label">任务类型：</span>
              <a-tag :color="getTaskTypeColor(currentTask.taskType)">
                {{ getTaskTypeText(currentTask.taskType) }}
              </a-tag>
            </div>
          </a-col>
          <a-col :span="12">
            <div class="info-item">
              <span class="info-label">总数量：</span>
              <span class="info-value">{{ currentTask.totalQuantity }} {{ currentTask.unitName }}</span>
            </div>
          </a-col>
        </a-row>
      </div>
      
      <a-divider />
      
      <a-form :form="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="本次完成数量" :required="true">
              <a-input-number
                v-decorator="['completedQuantity', {
                  rules: [
                    { required: true, message: '请输入完成数量' },
                    { type: 'number', min: 1, message: '完成数量必须大于0' }
                  ]
                }]"
                :min="1"
                :max="remainingQuantity"
                :precision="0"
                style="width: 100%"
                placeholder="请输入完成数量" />
              <div class="quantity-hint">
                剩余数量: {{ remainingQuantity }} {{ currentTask ? currentTask.unitName : '' }}
              </div>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="工作时长（小时）" :required="true">
              <a-input-number
                v-decorator="['workHours', {
                  rules: [
                    { required: true, message: '请输入工作时长' },
                    { type: 'number', min: 0.1, message: '工作时长必须大于0' }
                  ]
                }]"
                :min="0.1"
                :step="0.1"
                :precision="1"
                style="width: 100%"
                placeholder="请输入工作时长" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="质量等级">
              <a-select
                v-decorator="['qualityLevel', { initialValue: 'GOOD' }]"
                placeholder="请选择质量等级">
                <a-select-option value="EXCELLENT">优秀</a-select-option>
                <a-select-option value="GOOD">良好</a-select-option>
                <a-select-option value="AVERAGE">一般</a-select-option>
                <a-select-option value="POOR">较差</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="是否完成全部任务">
              <a-switch
                v-decorator="['isCompleted', { valuePropName: 'checked' }]"
                checkedChildren="是"
                unCheckedChildren="否"
                @change="onCompletedChange" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="工作照片">
          <a-upload
            v-decorator="['workPhotos']"
            :fileList="fileList"
            :beforeUpload="beforeUpload"
            @remove="handleRemove"
            @preview="handlePreview"
            listType="picture-card"
            accept="image/*"
            :multiple="true">
            <div v-if="fileList.length < 6">
              <a-icon type="plus" />
              <div class="ant-upload-text">上传照片</div>
            </div>
          </a-upload>
          <div class="upload-hint">
            支持上传JPG、PNG格式图片，最多6张，每张不超过5MB
          </div>
        </a-form-item>
        
        <a-form-item label="工作备注">
          <a-textarea
            v-decorator="['workRemark']"
            :rows="4"
            placeholder="请描述工作过程、遇到的问题、质量情况等..." />
        </a-form-item>
        
        <!-- 费用计算 -->
        <div class="fee-calculation">
          <h4>费用计算</h4>
          <a-row :gutter="16">
            <a-col :span="8">
              <div class="fee-item">
                <span class="fee-label">单价：</span>
                <span class="fee-value">¥{{ currentTask ? currentTask.unitFee : 0 }}</span>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="fee-item">
                <span class="fee-label">完成数量：</span>
                <span class="fee-value">{{ form.getFieldValue('completedQuantity') || 0 }}</span>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="fee-item">
                <span class="fee-label">本次收益：</span>
                <span class="fee-value total">¥{{ calculateFee() }}</span>
              </div>
            </a-col>
          </a-row>
        </div>
      </a-form>
    </div>
    
    <!-- 图片预览模态框 -->
    <a-modal :visible="previewVisible" :footer="null" @cancel="handlePreviewCancel">
      <img alt="预览" style="width: 100%" :src="previewImage" />
    </a-modal>
    
  </a-modal>
</template>

<script>
export default {
  name: "ProgressReportModal",
  data() {
    return {
      title: "进度报工",
      visible: false,
      confirmLoading: false,
      currentTask: null,
      form: this.$form.createForm(this),
      fileList: [],
      previewVisible: false,
      previewImage: ''
    }
  },
  computed: {
    remainingQuantity() {
      if (!this.currentTask) return 0;
      return this.currentTask.totalQuantity - (this.currentTask.completedQuantity || 0);
    }
  },
  methods: {
    report(task) {
      this.currentTask = { ...task };
      this.visible = true;
      this.title = `进度报工 - ${task.taskNumber}`;
      this.form.resetFields();
      this.fileList = [];
    },
    
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true;
          
          // 模拟API调用
          setTimeout(() => {
            this.confirmLoading = false;
            this.visible = false;
            this.$message.success('报工成功！');
            this.$emit('ok');
          }, 1000);
        }
      });
    },
    
    handleCancel() {
      this.visible = false;
      this.currentTask = null;
      this.form.resetFields();
      this.fileList = [];
    },
    
    onCompletedChange(checked) {
      if (checked && this.currentTask) {
        this.form.setFieldsValue({
          completedQuantity: this.remainingQuantity
        });
      }
    },
    
    beforeUpload(file) {
      const isImage = file.type.indexOf('image/') === 0;
      if (!isImage) {
        this.$message.error('只能上传图片文件！');
        return false;
      }
      
      const isLt5M = file.size / 1024 / 1024 < 5;
      if (!isLt5M) {
        this.$message.error('图片大小不能超过5MB！');
        return false;
      }
      
      // 添加到文件列表
      this.fileList = [...this.fileList, {
        uid: file.uid,
        name: file.name,
        status: 'done',
        url: URL.createObjectURL(file),
        originFileObj: file
      }];
      
      return false; // 阻止自动上传
    },
    
    handleRemove(file) {
      const index = this.fileList.indexOf(file);
      const newFileList = this.fileList.slice();
      newFileList.splice(index, 1);
      this.fileList = newFileList;
    },
    
    handlePreview(file) {
      this.previewImage = file.url || file.thumbUrl;
      this.previewVisible = true;
    },
    
    handlePreviewCancel() {
      this.previewVisible = false;
    },
    
    calculateFee() {
      const completedQuantity = this.form.getFieldValue('completedQuantity') || 0;
      const unitFee = this.currentTask ? this.currentTask.unitFee : 0;
      return (completedQuantity * unitFee).toFixed(2);
    },
    
    getTaskTypeColor(taskType) {
      const colors = {
        'POLISHING': 'blue',
        'PAINTING': 'green',
        'ASSEMBLY': 'orange',
        'PACKAGING': 'purple',
        'QUALITY_CHECK': 'red'
      };
      return colors[taskType] || 'default';
    },
    
    getTaskTypeText(taskType) {
      const texts = {
        'POLISHING': '抛光',
        'PAINTING': '上色',
        'ASSEMBLY': '组装',
        'PACKAGING': '包装',
        'QUALITY_CHECK': '质检'
      };
      return texts[taskType] || '未知';
    }
  }
}
</script>

<style scoped>
.progress-report-content {
  max-height: 70vh;
  overflow-y: auto;
}

.task-info-section {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.task-info-section h4 {
  margin: 0 0 12px 0;
  color: #262626;
  font-weight: 600;
}

.info-item {
  margin-bottom: 8px;
}

.info-label {
  color: #8c8c8c;
  font-size: 14px;
}

.info-value {
  color: #262626;
  font-weight: 500;
}

.quantity-hint {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
}

.upload-hint {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 8px;
}

.fee-calculation {
  background: #f0f9ff;
  padding: 16px;
  border-radius: 6px;
  margin-top: 16px;
}

.fee-calculation h4 {
  margin: 0 0 12px 0;
  color: #262626;
  font-weight: 600;
}

.fee-item {
  text-align: center;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.fee-label {
  display: block;
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.fee-value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.fee-value.total {
  color: #1890ff;
  font-size: 18px;
}
</style>
