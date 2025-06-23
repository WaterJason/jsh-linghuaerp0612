<template>
  <a-modal
    title="不合格品处理"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false">
    
    <div class="defective-info" v-if="inspectionData">
      <!-- 质检信息 -->
      <div class="info-section">
        <h4 class="info-title">
          <a-icon type="exclamation-circle" />
          质检信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检编号：</span>
              <span class="value">{{ inspectionData.inspectionNumber }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">产品名称：</span>
              <span class="value">{{ inspectionData.productName }}</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">不合格数量：</span>
              <span class="value error">{{ inspectionData.defectiveQuantity || 0 }} {{ inspectionData.unitName || '个' }}</span>
            </div>
          </a-col>
        </a-row>
        
        <a-row :gutter="16" style="margin-top: 12px;">
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检结果：</span>
              <a-tag color="error">
                <a-icon type="close-circle" />
                不合格
              </a-tag>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">合格率：</span>
              <span class="value">{{ inspectionData.qualificationRate || 0 }}%</span>
            </div>
          </a-col>
          <a-col :span="8">
            <div class="info-item">
              <span class="label">质检员：</span>
              <span class="value">{{ inspectionData.inspectorName }}</span>
            </div>
          </a-col>
        </a-row>
      </div>
    </div>
    
    <a-form :form="form" layout="horizontal">
      <!-- 处理方式 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="tool" />
          处理方式
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="处理类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['handleType', { rules: [{ required: true, message: '请选择处理类型' }] }]"
                placeholder="请选择处理类型"
                @change="onHandleTypeChange">
                <a-select-option value="REWORK">返工处理</a-select-option>
                <a-select-option value="REPAIR">修复处理</a-select-option>
                <a-select-option value="DOWNGRADE">降级处理</a-select-option>
                <a-select-option value="SCRAP">报废处理</a-select-option>
                <a-select-option value="RETURN">退货处理</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="处理数量" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number
                v-decorator="['handleQuantity', { rules: [{ required: true, message: '请输入处理数量' }] }]"
                :min="0"
                :max="maxHandleQuantity"
                :precision="2"
                placeholder="处理数量"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="处理人员" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['handlerId', { rules: [{ required: true, message: '请选择处理人员' }] }]"
                placeholder="请选择处理人员"
                showSearch
                optionFilterProp="children">
                <a-select-option 
                  v-for="worker in workerList" 
                  :key="worker.id" 
                  :value="worker.id">
                  {{ worker.name }} - {{ worker.department }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 处理详情 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="file-text" />
          处理详情
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="处理原因" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
              <a-textarea
                v-decorator="['handleReason', { rules: [{ required: true, message: '请输入处理原因' }] }]"
                placeholder="详细描述不合格品的问题和处理原因"
                :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="处理方案" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
              <a-textarea
                v-decorator="['handlePlan']"
                placeholder="详细描述具体的处理方案和步骤"
                :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="预计完成时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['expectedCompleteTime']"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择预计完成时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="处理成本" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number
                v-decorator="['handleCost', { initialValue: 0 }]"
                :min="0"
                :precision="2"
                placeholder="处理成本"
                style="width: 100%">
                <template slot="addonAfter">元</template>
              </a-input-number>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 处理结果 -->
      <div class="form-section" v-if="showResultSection">
        <h4 class="section-title">
          <a-icon type="check-circle" />
          处理结果
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="实际完成时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['actualCompleteTime']"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择实际完成时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="实际成本" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number
                v-decorator="['actualCost', { initialValue: 0 }]"
                :min="0"
                :precision="2"
                placeholder="实际成本"
                style="width: 100%">
                <template slot="addonAfter">元</template>
              </a-input-number>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="处理结果" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
              <a-textarea
                v-decorator="['handleResult']"
                placeholder="描述处理结果和效果"
                :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 处理照片 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="camera" />
          处理照片
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="处理照片" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
              <j-image-upload 
                v-model="handlePhotos" 
                bizPath="defective" 
                text="上传处理照片" 
                :isMultiple="true" />
              <div class="upload-tips">
                <a-icon type="info-circle" />
                上传处理前后对比照片，支持JPG、PNG格式
              </div>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 备注信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="message" />
          备注信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="{ span: 4 }" :wrapperCol="{ span: 20 }">
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
import { getAction, postAction } from '@/api/manage'
import JImageUpload from '@/components/jeecg/JImageUpload'
import dayjs from 'dayjs'

export default {
  name: "DefectiveProductModal",
  components: {
    JImageUpload
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      inspectionData: null,
      showResultSection: false,
      
      labelCol: {
        span: 6
      },
      wrapperCol: {
        span: 18
      },
      
      form: this.$form.createForm(this),
      handlePhotos: '',
      
      // 工人列表
      workerList: [],
      
      // API URLs
      url: {
        handle: "/qualityInspection/handleDefective",
        workerList: "/system/person/list"
      }
    }
  },
  
  computed: {
    // 最大处理数量
    maxHandleQuantity() {
      return this.inspectionData ? (this.inspectionData.defectiveQuantity || 0) : 0;
    }
  },
  
  methods: {
    // 显示模态框
    show(record) {
      this.inspectionData = record;
      this.visible = true;
      this.loadWorkerList();
      
      // 设置默认值
      this.$nextTick(() => {
        this.form.setFieldsValue({
          handleQuantity: this.maxHandleQuantity,
          expectedCompleteTime: dayjs().add(1, 'days')
        });
      });
    },
    
    // 加载工人列表
    async loadWorkerList() {
      try {
        const res = await getAction(this.url.workerList, { 
          enabled: true,
          size: 100 
        });
        
        if (res.code === 200) {
          this.workerList = res.data.rows || [];
        }
      } catch (error) {
        console.error('Load worker list error:', error);
      }
    },
    
    // 处理类型变更
    onHandleTypeChange(value) {
      // 根据处理类型显示不同的表单项
      if (value === 'REWORK' || value === 'REPAIR') {
        this.showResultSection = true;
      } else {
        this.showResultSection = false;
      }
    },
    
    // 确定
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true;
          
          // 处理时间字段
          const formData = {
            ...values,
            inspectionId: this.inspectionData.id,
            expectedCompleteTime: values.expectedCompleteTime ? values.expectedCompleteTime.format('YYYY-MM-DD HH:mm:ss') : null,
            actualCompleteTime: values.actualCompleteTime ? values.actualCompleteTime.format('YYYY-MM-DD HH:mm:ss') : null,
            handlePhotos: this.handlePhotos
          };
          
          postAction(this.url.handle, formData).then(res => {
            if (res.code === 200) {
              this.$message.success('不合格品处理成功');
              this.handleCancel();
              this.$emit('ok');
            } else {
              this.$message.error(res.message || '处理失败');
            }
          }).catch(error => {
            this.$message.error('处理失败');
            console.error('Handle defective error:', error);
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
      this.handlePhotos = '';
      this.inspectionData = null;
      this.showResultSection = false;
      this.workerList = [];
    }
  }
}
</script>

<style scoped>
.defective-info {
  margin-bottom: 24px;
}

.info-section {
  background: #fff2f0;
  border: 1px solid #ffccc7;
  border-radius: 6px;
  padding: 16px;
  margin-bottom: 16px;
}

.info-title {
  font-size: 14px;
  font-weight: 600;
  color: #cf1322;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
}

.info-title .anticon {
  margin-right: 8px;
}

.info-item {
  margin-bottom: 8px;
}

.info-item .label {
  color: #8c8c8c;
  margin-right: 8px;
}

.info-item .value {
  color: #262626;
  font-weight: 500;
}

.info-item .value.error {
  color: #cf1322;
}

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
