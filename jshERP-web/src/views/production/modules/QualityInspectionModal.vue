<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false">
    
    <a-form :form="form" layout="horizontal">
      <!-- 基本信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="info-circle" />
          基本信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="质检编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['inspectionNumber', { rules: [{ required: true, message: '请输入质检编号' }] }]"
                placeholder="系统自动生成或手动输入" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="关联任务" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['taskId', { rules: [{ required: true, message: '请选择关联任务' }] }]"
                placeholder="请选择关联任务"
                showSearch
                optionFilterProp="children"
                @change="onTaskChange">
                <a-select-option 
                  v-for="task in taskList" 
                  :key="task.id" 
                  :value="task.id">
                  {{ task.taskNumber }} - {{ task.taskName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="质检类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['inspectionType', { rules: [{ required: true, message: '请选择质检类型' }] }]"
                placeholder="请选择质检类型">
                <a-select-option value="INCOMING">来料质检</a-select-option>
                <a-select-option value="PROCESS">过程质检</a-select-option>
                <a-select-option value="FINAL">最终质检</a-select-option>
                <a-select-option value="REWORK">返工质检</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="质检时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['inspectionTime', { rules: [{ required: true, message: '请选择质检时间' }] }]"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择质检时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 数量信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="number" />
          数量信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <a-form-item label="质检数量" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
              <a-input-number
                v-decorator="['inspectionQuantity', { rules: [{ required: true, message: '请输入质检数量' }] }]"
                :min="0"
                :precision="2"
                placeholder="质检数量"
                style="width: 100%"
                @change="onQuantityChange" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="合格数量" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
              <a-input-number
                v-decorator="['qualifiedQuantity', { rules: [{ required: true, message: '请输入合格数量' }] }]"
                :min="0"
                :precision="2"
                placeholder="合格数量"
                style="width: 100%"
                @change="onQuantityChange" />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="不合格数量" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
              <a-input-number
                v-decorator="['defectiveQuantity']"
                :min="0"
                :precision="2"
                placeholder="不合格数量"
                style="width: 100%"
                disabled />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="合格率" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <div class="rate-display">
                <a-progress 
                  :percent="calculatedRate" 
                  :status="getProgressStatus(calculatedRate)"
                  size="small" />
                <span class="rate-text">{{ calculatedRate }}%</span>
              </div>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="单位" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['unitName', { initialValue: '个' }]"
                placeholder="单位" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 质量评分 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="star" />
          质量评分
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="外观质量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-rate 
                v-decorator="['appearanceScore', { initialValue: 5 }]"
                allowHalf 
                @change="onScoreChange" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="尺寸精度" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-rate 
                v-decorator="['sizeScore', { initialValue: 5 }]"
                allowHalf 
                @change="onScoreChange" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="颜色效果" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-rate 
                v-decorator="['colorScore', { initialValue: 5 }]"
                allowHalf 
                @change="onScoreChange" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="表面质感" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-rate 
                v-decorator="['textureScore', { initialValue: 5 }]"
                allowHalf 
                @change="onScoreChange" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="细节处理" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-rate 
                v-decorator="['detailScore', { initialValue: 5 }]"
                allowHalf 
                @change="onScoreChange" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="整体效果" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-rate 
                v-decorator="['overallEffectScore', { initialValue: 5 }]"
                allowHalf 
                @change="onScoreChange" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="综合评分" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <div class="overall-score">
                <a-rate 
                  :value="calculatedOverallScore" 
                  disabled 
                  allowHalf />
                <span class="score-text">{{ calculatedOverallScore }}/5</span>
              </div>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="质量等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-tag :color="getGradeColor(calculatedGrade)" size="large">
                {{ calculatedGrade }}
              </a-tag>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 质检结果 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="check-circle" />
          质检结果
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="总体结果" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['overallResult']"
                placeholder="系统自动判定"
                disabled>
                <a-select-option value="PASS">合格</a-select-option>
                <a-select-option value="FAIL">不合格</a-select-option>
                <a-select-option value="REWORK">需返工</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="质检标准" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['inspectionStandard']"
                placeholder="请输入质检标准" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 质检照片 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="camera" />
          质检照片
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="质检照片" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <j-image-upload 
                v-model="qualityPhotos" 
                bizPath="quality" 
                text="上传质检照片" 
                :isMultiple="true" />
              <div class="upload-tips">
                <a-icon type="info-circle" />
                支持JPG、PNG、GIF格式，单张不超过20M，最多上传10张
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 问题描述和改进建议 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="file-text" />
          问题描述和改进建议
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="问题描述" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <a-textarea
                v-decorator="['problemDescription']"
                placeholder="详细描述发现的质量问题"
                :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="改进建议" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <a-textarea
                v-decorator="['improvementSuggestion']"
                placeholder="提出改进建议和解决方案"
                :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <a-textarea
                v-decorator="['remark']"
                placeholder="其他备注信息"
                :rows="2" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>
    </a-form>
  </a-modal>
</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
import JImageUpload from '@/components/jeecg/JImageUpload'
import dayjs from 'dayjs'

export default {
  name: "QualityInspectionModal",
  components: {
    JImageUpload
  },
  data() {
    return {
      title: "新增质检",
      visible: false,
      confirmLoading: false,
      isEdit: false,
      
      labelCol: {
        span: 6
      },
      wrapperCol: {
        span: 18
      },
      
      form: this.$form.createForm(this),
      qualityPhotos: '',
      
      // 任务列表
      taskList: [],
      
      // API URLs
      url: {
        add: "/qualityInspection/add",
        edit: "/qualityInspection/edit",
        taskList: "/production/task/list"
      }
    }
  },
  
  computed: {
    // 计算合格率
    calculatedRate() {
      const inspectionQty = this.form.getFieldValue('inspectionQuantity') || 0;
      const qualifiedQty = this.form.getFieldValue('qualifiedQuantity') || 0;
      
      if (inspectionQty === 0) return 0;
      return Math.round((qualifiedQty / inspectionQty) * 100);
    },
    
    // 计算综合评分
    calculatedOverallScore() {
      const scores = [
        this.form.getFieldValue('appearanceScore') || 0,
        this.form.getFieldValue('sizeScore') || 0,
        this.form.getFieldValue('colorScore') || 0,
        this.form.getFieldValue('textureScore') || 0,
        this.form.getFieldValue('detailScore') || 0,
        this.form.getFieldValue('overallEffectScore') || 0
      ];
      
      const total = scores.reduce((sum, score) => sum + score, 0);
      return Math.round((total / scores.length) * 10) / 10;
    },
    
    // 计算质量等级
    calculatedGrade() {
      const score = this.calculatedOverallScore;
      if (score >= 4.5) return 'A+';
      if (score >= 4.0) return 'A';
      if (score >= 3.5) return 'B';
      if (score >= 3.0) return 'C';
      return 'D';
    },
    
    // 计算总体结果
    calculatedResult() {
      const rate = this.calculatedRate;
      const score = this.calculatedOverallScore;
      
      if (rate === 100 && score >= 4.0) return 'PASS';
      if (rate >= 95 && score >= 3.5) return 'REWORK';
      return 'FAIL';
    }
  },
  
  watch: {
    calculatedResult(newVal) {
      this.form.setFieldsValue({
        overallResult: newVal
      });
    }
  },

  methods: {
    // 新增
    add() {
      this.isEdit = false;
      this.visible = true;
      this.loadTaskList();
      this.generateInspectionNumber();

      // 设置默认值
      this.$nextTick(() => {
        this.form.setFieldsValue({
          inspectionTime: dayjs(),
          inspectionType: 'FINAL',
          appearanceScore: 5,
          sizeScore: 5,
          colorScore: 5,
          textureScore: 5,
          detailScore: 5,
          overallEffectScore: 5
        });
      });
    },

    // 编辑
    edit(record) {
      this.isEdit = true;
      this.visible = true;
      this.loadTaskList();

      this.$nextTick(() => {
        this.form.setFieldsValue({
          ...record,
          inspectionTime: record.inspectionTime ? dayjs(record.inspectionTime) : null
        });
        this.qualityPhotos = record.qualityPhotos || '';
      });
    },

    // 批量确认
    batchConfirm(records) {
      this.isEdit = true;
      this.visible = true;
      this.loadTaskList();

      // 批量确认时只显示公共字段
      this.$message.info('批量确认模式，只能修改公共字段');
    },

    // 生成质检编号
    generateInspectionNumber() {
      const now = new Date();
      const dateStr = now.getFullYear().toString() +
                     (now.getMonth() + 1).toString().padStart(2, '0') +
                     now.getDate().toString().padStart(2, '0');
      const timeStr = now.getHours().toString().padStart(2, '0') +
                     now.getMinutes().toString().padStart(2, '0') +
                     now.getSeconds().toString().padStart(2, '0');
      const inspectionNumber = `QI${dateStr}${timeStr}`;

      this.$nextTick(() => {
        this.form.setFieldsValue({
          inspectionNumber: inspectionNumber
        });
      });
    },

    // 加载任务列表
    async loadTaskList() {
      try {
        const res = await getAction(this.url.taskList, {
          status: 'COMPLETED',
          size: 100
        });

        if (res.code === 200) {
          this.taskList = res.data.rows || [];
        }
      } catch (error) {
        console.error('Load task list error:', error);
      }
    },

    // 任务变更
    onTaskChange(taskId) {
      const task = this.taskList.find(t => t.id === taskId);
      if (task) {
        this.form.setFieldsValue({
          taskNumber: task.taskNumber,
          workOrderId: task.workOrderId,
          workOrderNumber: task.workOrderNumber,
          productId: task.productId,
          productName: task.productName,
          inspectionQuantity: task.completedQuantity,
          qualifiedQuantity: task.completedQuantity,
          unitName: task.unitName
        });
      }
    },

    // 数量变更
    onQuantityChange() {
      this.$nextTick(() => {
        const inspectionQty = this.form.getFieldValue('inspectionQuantity') || 0;
        const qualifiedQty = this.form.getFieldValue('qualifiedQuantity') || 0;
        const defectiveQty = Math.max(0, inspectionQty - qualifiedQty);

        this.form.setFieldsValue({
          defectiveQuantity: defectiveQty,
          qualificationRate: this.calculatedRate
        });
      });
    },

    // 评分变更
    onScoreChange() {
      this.$nextTick(() => {
        this.form.setFieldsValue({
          overallScore: this.calculatedOverallScore,
          qualityGrade: this.calculatedGrade,
          overallResult: this.calculatedResult
        });
      });
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

    // 确定
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true;

          // 处理时间字段和计算字段
          const formData = {
            ...values,
            inspectionTime: values.inspectionTime ? values.inspectionTime.format('YYYY-MM-DD HH:mm:ss') : null,
            qualityPhotos: this.qualityPhotos,
            qualificationRate: this.calculatedRate,
            overallScore: this.calculatedOverallScore,
            qualityGrade: this.calculatedGrade,
            overallResult: this.calculatedResult
          };

          const action = this.isEdit ? putAction : postAction;
          const url = this.isEdit ? this.url.edit : this.url.add;

          action(url, formData).then(res => {
            if (res.code === 200) {
              this.$message.success(this.isEdit ? '编辑成功' : '新增成功');
              this.handleCancel();
              this.$emit('ok');
            } else {
              this.$message.error(res.message || '操作失败');
            }
          }).catch(error => {
            this.$message.error('操作失败');
            console.error('Save error:', error);
          }).finally(() => {
            this.confirmLoading = false;
          });
        }
      });
    },

    // 取消
    handleCancel() {
      this.visible = false;
      this.form.resetFields();
      this.qualityPhotos = '';
      this.taskList = [];
    }
  }
}
</script>

<style scoped>
.form-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 8px;
}

.section-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

.rate-display {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rate-text {
  font-size: 14px;
  color: #8c8c8c;
}

.overall-score {
  display: flex;
  align-items: center;
  gap: 12px;
}

.score-text {
  font-size: 14px;
  color: #8c8c8c;
}

.upload-tips {
  margin-top: 8px;
  color: #8c8c8c;
  font-size: 12px;
}

.upload-tips .anticon {
  margin-right: 4px;
  color: #1890ff;
}
</style>
