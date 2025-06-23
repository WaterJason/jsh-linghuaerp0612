<template>
  <a-modal
    :title="title"
    :width="900"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    
    <div class="quality-check-content">
      <div v-if="currentTask" class="task-info-section">
        <h4>任务信息</h4>
        <a-row :gutter="16">
          <a-col :span="8">
            <div class="info-item">
              <span class="info-label">任务编号：</span>
              <span class="info-value">{{ currentTask.taskNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="info-label">产品名称：</span>
              <span class="info-value">{{ currentTask.productName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="info-label">完成数量：</span>
              <span class="info-value">{{ currentTask.completedQuantity }} {{ currentTask.unitName }}</span>
            </div>
          </a-col>
        </a-row>
      </div>
      
      <a-divider />
      
      <a-form :form="form" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="质检结果" :required="true">
              <a-radio-group
                v-decorator="['qualityResult', {
                  rules: [{ required: true, message: '请选择质检结果' }]
                }]"
                @change="onQualityResultChange">
                <a-radio value="PASS">合格</a-radio>
                <a-radio value="FAIL">不合格</a-radio>
                <a-radio value="REWORK">需返工</a-radio>
              </a-radio-group>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="质检等级">
              <a-select
                v-decorator="['qualityGrade', { initialValue: 'B' }]"
                placeholder="请选择质检等级">
                <a-select-option value="A+">A+级（优秀）</a-select-option>
                <a-select-option value="A">A级（良好）</a-select-option>
                <a-select-option value="B">B级（合格）</a-select-option>
                <a-select-option value="C">C级（勉强合格）</a-select-option>
                <a-select-option value="D">D级（不合格）</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <!-- 质检项目 -->
        <a-form-item label="质检项目">
          <div class="quality-items">
            <a-row :gutter="16">
              <a-col :span="8" v-for="item in qualityItems" :key="item.key">
                <div class="quality-item">
                  <div class="item-header">
                    <span class="item-name">{{ item.name }}</span>
                    <a-rate
                      v-model="item.score"
                      :count="5"
                      allowHalf
                      @change="updateOverallScore" />
                  </div>
                  <div class="item-score">{{ item.score }}/5</div>
                </div>
              </a-col>
            </a-row>
          </div>
        </a-form-item>
        
        <!-- 不合格数量（当质检结果为不合格或需返工时显示） -->
        <a-form-item 
          v-if="showDefectiveQuantity" 
          label="不合格数量" 
          :required="true">
          <a-input-number
            v-decorator="['defectiveQuantity', {
              rules: [
                { required: true, message: '请输入不合格数量' },
                { type: 'number', min: 1, message: '不合格数量必须大于0' }
              ]
            }]"
            :min="1"
            :max="currentTask ? currentTask.completedQuantity : 0"
            :precision="0"
            style="width: 200px"
            placeholder="请输入不合格数量" />
          <span class="quantity-unit">{{ currentTask ? currentTask.unitName : '' }}</span>
        </a-form-item>
        
        <!-- 质检照片 -->
        <a-form-item label="质检照片">
          <a-upload
            v-decorator="['qualityPhotos']"
            :fileList="fileList"
            :beforeUpload="beforeUpload"
            @remove="handleRemove"
            @preview="handlePreview"
            listType="picture-card"
            accept="image/*"
            :multiple="true">
            <div v-if="fileList.length < 8">
              <a-icon type="plus" />
              <div class="ant-upload-text">上传照片</div>
            </div>
          </a-upload>
          <div class="upload-hint">
            支持上传JPG、PNG格式图片，最多8张，每张不超过5MB
          </div>
        </a-form-item>
        
        <!-- 质检问题描述 -->
        <a-form-item 
          v-if="showProblemDescription" 
          label="问题描述" 
          :required="true">
          <a-textarea
            v-decorator="['problemDescription', {
              rules: [{ required: true, message: '请描述发现的问题' }]
            }]"
            :rows="4"
            placeholder="请详细描述发现的质量问题、缺陷位置、严重程度等..." />
        </a-form-item>
        
        <!-- 改进建议 -->
        <a-form-item label="改进建议">
          <a-textarea
            v-decorator="['improvementSuggestion']"
            :rows="3"
            placeholder="请提出改进建议或处理方案..." />
        </a-form-item>
        
        <!-- 质检备注 -->
        <a-form-item label="质检备注">
          <a-textarea
            v-decorator="['qualityRemark']"
            :rows="3"
            placeholder="其他质检相关备注..." />
        </a-form-item>
        
        <!-- 质检汇总 -->
        <div class="quality-summary">
          <h4>质检汇总</h4>
          <a-row :gutter="16">
            <a-col :span="6">
              <div class="summary-item">
                <span class="summary-label">综合评分：</span>
                <span class="summary-value">{{ overallScore.toFixed(1) }}/5.0</span>
              </div>
            </a-col>
            <a-col :span="6">
              <div class="summary-item">
                <span class="summary-label">合格数量：</span>
                <span class="summary-value">{{ getQualifiedQuantity() }} {{ currentTask ? currentTask.unitName : '' }}</span>
              </div>
            </a-col>
            <a-col :span="6">
              <div class="summary-item">
                <span class="summary-label">不合格数量：</span>
                <span class="summary-value">{{ getDefectiveQuantity() }} {{ currentTask ? currentTask.unitName : '' }}</span>
              </div>
            </a-col>
            <a-col :span="6">
              <div class="summary-item">
                <span class="summary-label">合格率：</span>
                <span class="summary-value">{{ getQualificationRate() }}%</span>
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
  name: "QualityCheckModal",
  data() {
    return {
      title: "质检确认",
      visible: false,
      confirmLoading: false,
      currentTask: null,
      form: this.$form.createForm(this),
      fileList: [],
      previewVisible: false,
      previewImage: '',
      qualityItems: [
        { key: 'appearance', name: '外观质量', score: 4 },
        { key: 'size', name: '尺寸精度', score: 4 },
        { key: 'color', name: '颜色效果', score: 4 },
        { key: 'texture', name: '表面质感', score: 4 },
        { key: 'detail', name: '细节处理', score: 4 },
        { key: 'overall', name: '整体效果', score: 4 }
      ],
      overallScore: 4.0
    }
  },
  computed: {
    showDefectiveQuantity() {
      const qualityResult = this.form.getFieldValue('qualityResult');
      return qualityResult === 'FAIL' || qualityResult === 'REWORK';
    },
    
    showProblemDescription() {
      const qualityResult = this.form.getFieldValue('qualityResult');
      return qualityResult === 'FAIL' || qualityResult === 'REWORK';
    }
  },
  methods: {
    check(task) {
      this.currentTask = { ...task };
      this.visible = true;
      this.title = `质检确认 - ${task.taskNumber}`;
      this.form.resetFields();
      this.fileList = [];
      this.resetQualityItems();
    },
    
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true;
          
          // 模拟API调用
          setTimeout(() => {
            this.confirmLoading = false;
            this.visible = false;
            this.$message.success('质检确认成功！');
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
      this.resetQualityItems();
    },
    
    onQualityResultChange(e) {
      const value = e.target.value;
      if (value === 'PASS') {
        this.form.setFieldsValue({ defectiveQuantity: 0 });
      }
    },
    
    resetQualityItems() {
      this.qualityItems.forEach(item => {
        item.score = 4;
      });
      this.updateOverallScore();
    },
    
    updateOverallScore() {
      const totalScore = this.qualityItems.reduce((sum, item) => sum + item.score, 0);
      this.overallScore = totalScore / this.qualityItems.length;
    },
    
    getQualifiedQuantity() {
      if (!this.currentTask) return 0;
      const defectiveQuantity = this.form.getFieldValue('defectiveQuantity') || 0;
      return this.currentTask.completedQuantity - defectiveQuantity;
    },
    
    getDefectiveQuantity() {
      return this.form.getFieldValue('defectiveQuantity') || 0;
    },
    
    getQualificationRate() {
      if (!this.currentTask || this.currentTask.completedQuantity === 0) return 0;
      const qualifiedQuantity = this.getQualifiedQuantity();
      return ((qualifiedQuantity / this.currentTask.completedQuantity) * 100).toFixed(1);
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
      
      this.fileList = [...this.fileList, {
        uid: file.uid,
        name: file.name,
        status: 'done',
        url: URL.createObjectURL(file),
        originFileObj: file
      }];
      
      return false;
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
    }
  }
}
</script>

<style scoped>
.quality-check-content {
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

.quality-items {
  background: #f9f9f9;
  padding: 16px;
  border-radius: 6px;
}

.quality-item {
  background: white;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 12px;
  border: 1px solid #e8e8e8;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.item-name {
  font-weight: 500;
  color: #262626;
}

.item-score {
  text-align: center;
  font-size: 12px;
  color: #1890ff;
  font-weight: 600;
}

.quantity-unit {
  margin-left: 8px;
  color: #8c8c8c;
}

.upload-hint {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 8px;
}

.quality-summary {
  background: #e6f7ff;
  padding: 16px;
  border-radius: 6px;
  margin-top: 16px;
}

.quality-summary h4 {
  margin: 0 0 12px 0;
  color: #262626;
  font-weight: 600;
}

.summary-item {
  text-align: center;
  padding: 8px;
  background: white;
  border-radius: 4px;
}

.summary-label {
  display: block;
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.summary-value {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #1890ff;
}
</style>
