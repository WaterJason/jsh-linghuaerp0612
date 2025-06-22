<template>
  <a-modal
    :title="title"
    :width="900"
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
            <a-form-item label="追踪编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['trackingNumber', { rules: [{ required: true, message: '请输入追踪编号' }] }]"
                placeholder="系统自动生成或手动输入" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="工单编号" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['workOrderId', { rules: [{ required: true, message: '请选择工单' }] }]"
                placeholder="请选择关联工单"
                showSearch
                optionFilterProp="children"
                @change="onWorkOrderChange">
                <a-select-option 
                  v-for="order in workOrderList" 
                  :key="order.id" 
                  :value="order.id">
                  {{ order.workOrderNumber }} - {{ order.productName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="物流类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['logisticsType', { rules: [{ required: true, message: '请选择物流类型' }] }]"
                placeholder="请选择物流类型">
                <a-select-option value="MATERIAL_IN">原料入库</a-select-option>
                <a-select-option value="SEMI_PRODUCT_OUT">半成品出库</a-select-option>
                <a-select-option value="PRODUCT_IN">成品入库</a-select-option>
                <a-select-option value="PRODUCT_OUT">成品出库</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="物流状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['status', { initialValue: 'PENDING' }]"
                placeholder="请选择物流状态">
                <a-select-option value="PENDING">待发货</a-select-option>
                <a-select-option value="SHIPPED">已发货</a-select-option>
                <a-select-option value="IN_TRANSIT">运输中</a-select-option>
                <a-select-option value="ARRIVED">已到达</a-select-option>
                <a-select-option value="RECEIVED">已收货</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 产品信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="shopping" />
          产品信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="产品名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['productName', { rules: [{ required: true, message: '请输入产品名称' }] }]"
                placeholder="请输入产品名称" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="数量" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
              <a-input-number
                v-decorator="['quantity', { rules: [{ required: true, message: '请输入数量' }] }]"
                :min="0"
                :precision="2"
                placeholder="数量"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="单位" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
              <a-input
                v-decorator="['unitName', { initialValue: '个' }]"
                placeholder="单位" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="发货地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['fromAddress']"
                placeholder="请输入发货地址" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="收货地址" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['toAddress', { rules: [{ required: true, message: '请输入收货地址' }] }]"
                placeholder="请输入收货地址" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 承运信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="car" />
          承运信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="承运人" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['carrierName']"
                placeholder="请输入承运人姓名" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="联系方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input
                v-decorator="['carrierPhone']"
                placeholder="请输入联系方式" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="运输方式" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select
                v-decorator="['transportMethod']"
                placeholder="请选择运输方式">
                <a-select-option value="SELF_DELIVERY">自送</a-select-option>
                <a-select-option value="EXPRESS">快递</a-select-option>
                <a-select-option value="LOGISTICS">物流</a-select-option>
                <a-select-option value="PICKUP">自提</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="运费" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input-number
                v-decorator="['shippingCost', { initialValue: 0 }]"
                :min="0"
                :precision="2"
                placeholder="运费"
                style="width: 100%">
                <template slot="addonAfter">元</template>
              </a-input-number>
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 时间信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="clock-circle" />
          时间信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="计划发货时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['planShipTime']"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择计划发货时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="预计到达时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['estimatedArrivalTime']"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择预计到达时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16" v-if="isEdit">
          <a-col :span="12">
            <a-form-item label="实际发货时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['shipTime']"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择实际发货时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="实际到达时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-date-picker
                v-decorator="['arrivalTime']"
                showTime
                format="YYYY-MM-DD HH:mm:ss"
                placeholder="选择实际到达时间"
                style="width: 100%" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>

      <!-- 备注信息 -->
      <div class="form-section">
        <h4 class="section-title">
          <a-icon type="file-text" />
          备注信息
        </h4>
        
        <a-row :gutter="16">
          <a-col :span="24">
            <a-form-item label="备注" :labelCol="{ span: 3 }" :wrapperCol="{ span: 21 }">
              <a-textarea
                v-decorator="['remark']"
                placeholder="请输入备注信息"
                :rows="3" />
            </a-form-item>
          </a-col>
        </a-row>
      </div>
    </a-form>
  </a-modal>
</template>

<script>
import { getAction, postAction, putAction } from '@/api/manage'
import dayjs from 'dayjs'

export default {
  name: "LogisticsTrackingModal",
  data() {
    return {
      title: "新增物流单",
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
      
      // 工单列表
      workOrderList: [],
      
      // API URLs
      url: {
        add: "/logistics/tracking/add",
        edit: "/logistics/tracking/edit",
        workOrderList: "/production/workOrder/list"
      }
    }
  },
  
  methods: {
    // 新增
    add() {
      this.isEdit = false;
      this.visible = true;
      this.loadWorkOrderList();
      this.generateTrackingNumber();
    },
    
    // 编辑
    edit(record) {
      this.isEdit = true;
      this.visible = true;
      this.loadWorkOrderList();
      
      this.$nextTick(() => {
        this.form.setFieldsValue({
          ...record,
          planShipTime: record.planShipTime ? dayjs(record.planShipTime) : null,
          estimatedArrivalTime: record.estimatedArrivalTime ? dayjs(record.estimatedArrivalTime) : null,
          shipTime: record.shipTime ? dayjs(record.shipTime) : null,
          arrivalTime: record.arrivalTime ? dayjs(record.arrivalTime) : null
        });
      });
    },
    
    // 批量编辑
    batchEdit(records) {
      this.isEdit = true;
      this.visible = true;
      this.loadWorkOrderList();
      
      // 批量编辑时只显示公共字段
      this.$message.info('批量编辑模式，只能修改公共字段');
    },
    
    // 生成追踪编号
    generateTrackingNumber() {
      const now = new Date();
      const dateStr = now.getFullYear().toString() + 
                     (now.getMonth() + 1).toString().padStart(2, '0') + 
                     now.getDate().toString().padStart(2, '0');
      const timeStr = now.getHours().toString().padStart(2, '0') + 
                     now.getMinutes().toString().padStart(2, '0') + 
                     now.getSeconds().toString().padStart(2, '0');
      const trackingNumber = `LT${dateStr}${timeStr}`;
      
      this.$nextTick(() => {
        this.form.setFieldsValue({
          trackingNumber: trackingNumber
        });
      });
    },
    
    // 加载工单列表
    async loadWorkOrderList() {
      try {
        const res = await getAction(this.url.workOrderList, { 
          status: 'IN_PROGRESS,COMPLETED',
          size: 100 
        });
        
        if (res.code === 200) {
          this.workOrderList = res.data.rows || [];
        }
      } catch (error) {
        console.error('Load work order list error:', error);
      }
    },
    
    // 工单变更
    onWorkOrderChange(workOrderId) {
      const workOrder = this.workOrderList.find(order => order.id === workOrderId);
      if (workOrder) {
        this.form.setFieldsValue({
          productName: workOrder.productName,
          quantity: workOrder.quantity,
          unitName: workOrder.unitName
        });
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
            planShipTime: values.planShipTime ? values.planShipTime.format('YYYY-MM-DD HH:mm:ss') : null,
            estimatedArrivalTime: values.estimatedArrivalTime ? values.estimatedArrivalTime.format('YYYY-MM-DD HH:mm:ss') : null,
            shipTime: values.shipTime ? values.shipTime.format('YYYY-MM-DD HH:mm:ss') : null,
            arrivalTime: values.arrivalTime ? values.arrivalTime.format('YYYY-MM-DD HH:mm:ss') : null
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
      this.workOrderList = [];
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
</style>
