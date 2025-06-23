<template>
  <div class="salary-config-container">
    <a-row :gutter="24">
      <!-- 左侧：薪酬项目配置 -->
      <a-col :span="12">
        <a-card title="薪酬项目配置" :bordered="false">
          <div class="action-buttons" style="margin-bottom: 16px;">
            <a-button type="primary" @click="handleAddItem" icon="plus">新增薪酬项目</a-button>
          </div>
          
          <a-table 
            :columns="itemColumns" 
            :data-source="salaryItems" 
            :pagination="false"
            :loading="itemLoading"
            size="middle">
            
            <template slot="itemType" slot-scope="text">
              <a-tag :color="getItemTypeColor(text)">
                {{ getItemTypeText(text) }}
              </a-tag>
            </template>
            
            <template slot="defaultRate" slot-scope="text">
              <span>{{ text ? (text * 100).toFixed(2) + '%' : '-' }}</span>
            </template>
            
            <template slot="baseAmount" slot-scope="text">
              <span>{{ text ? '¥' + text : '-' }}</span>
            </template>
            
            <template slot="status" slot-scope="text">
              <a-tag :color="text === 'ACTIVE' ? 'green' : 'red'">
                {{ text === 'ACTIVE' ? '启用' : '禁用' }}
              </a-tag>
            </template>
            
            <template slot="action" slot-scope="text, record">
              <a-button size="small" @click="handleEditItem(record)" style="margin-right: 8px;">编辑</a-button>
              <a-button 
                size="small" 
                @click="handleToggleItemStatus(record)"
                :type="record.status === 'ACTIVE' ? 'danger' : 'primary'">
                {{ record.status === 'ACTIVE' ? '禁用' : '启用' }}
              </a-button>
            </template>
          </a-table>
        </a-card>
      </a-col>
      
      <!-- 右侧：系统配置 -->
      <a-col :span="12">
        <a-card title="系统配置" :bordered="false" style="margin-bottom: 24px;">
          <a-form :form="configForm" layout="vertical">
            <a-form-item label="薪酬计算配置">
              <a-form-item label="默认工作天数/月">
                <a-input-number 
                  v-model="systemConfig.defaultWorkDays" 
                  :min="1" 
                  :max="31"
                  style="width: 100%;" />
              </a-form-item>
              <a-form-item label="计算超时时间(分钟)">
                <a-input-number 
                  v-model="systemConfig.calculationTimeout" 
                  :min="1" 
                  :max="120"
                  style="width: 100%;" />
              </a-form-item>
              <a-form-item label="批量计算大小">
                <a-input-number 
                  v-model="systemConfig.batchSize" 
                  :min="10" 
                  :max="1000"
                  style="width: 100%;" />
              </a-form-item>
            </a-form-item>
            
            <a-form-item label="审批流程配置">
              <a-checkbox v-model="systemConfig.approvalEnabled">启用审批流程</a-checkbox>
              <a-checkbox v-model="systemConfig.autoApproval">自动审批（小于阈值）</a-checkbox>
              <a-form-item label="自动审批阈值" v-if="systemConfig.autoApproval">
                <a-input-number 
                  v-model="systemConfig.autoApprovalThreshold" 
                  :min="0"
                  style="width: 100%;"
                  placeholder="金额阈值" />
              </a-form-item>
            </a-form-item>
            
            <a-form-item label="缓存配置">
              <a-form-item label="薪酬项目缓存时间(小时)">
                <a-input-number 
                  v-model="systemConfig.itemCacheTtl" 
                  :min="1" 
                  :max="168"
                  style="width: 100%;" />
              </a-form-item>
              <a-form-item label="计算结果缓存时间(天)">
                <a-input-number 
                  v-model="systemConfig.resultCacheTtl" 
                  :min="1" 
                  :max="30"
                  style="width: 100%;" />
              </a-form-item>
            </a-form-item>
            
            <a-form-item>
              <a-button type="primary" @click="handleSaveConfig">保存配置</a-button>
              <a-button @click="handleResetConfig" style="margin-left: 8px;">重置</a-button>
            </a-form-item>
          </a-form>
        </a-card>
        
        <!-- 数据权限配置 -->
        <a-card title="数据权限配置" :bordered="false">
          <a-form layout="vertical">
            <a-form-item label="默认数据权限级别">
              <a-radio-group v-model="systemConfig.defaultDataLevel">
                <a-radio value="PERSONAL">个人权限</a-radio>
                <a-radio value="DEPARTMENT">部门权限</a-radio>
                <a-radio value="ALL">全部权限</a-radio>
              </a-radio-group>
            </a-form-item>
            
            <a-form-item label="薪资查询权限">
              <a-checkbox-group v-model="systemConfig.inquiryPermissions">
                <a-checkbox value="VIEW_PERSONAL">查看个人薪资</a-checkbox>
                <a-checkbox value="VIEW_DEPARTMENT">查看部门薪资</a-checkbox>
                <a-checkbox value="VIEW_ALL">查看全部薪资</a-checkbox>
                <a-checkbox value="EXPORT_DATA">导出薪资数据</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
            
            <a-form-item label="操作权限">
              <a-checkbox-group v-model="systemConfig.operationPermissions">
                <a-checkbox value="CALCULATE_SALARY">执行薪酬计算</a-checkbox>
                <a-checkbox value="APPROVE_SALARY">审批薪酬计算</a-checkbox>
                <a-checkbox value="PAY_SALARY">执行薪酬发放</a-checkbox>
                <a-checkbox value="CONFIG_SYSTEM">系统配置管理</a-checkbox>
              </a-checkbox-group>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
    
    <!-- 薪酬项目编辑弹窗 -->
    <a-modal
      :title="itemModalTitle"
      :visible="itemModalVisible"
      @ok="handleItemModalOk"
      @cancel="handleItemModalCancel"
      width="600px">
      <a-form :form="itemForm" layout="vertical">
        <a-form-item label="项目编码">
          <a-input 
            v-decorator="['itemCode', { rules: [{ required: true, message: '请输入项目编码' }] }]"
            placeholder="请输入项目编码" />
        </a-form-item>
        <a-form-item label="项目名称">
          <a-input 
            v-decorator="['itemName', { rules: [{ required: true, message: '请输入项目名称' }] }]"
            placeholder="请输入项目名称" />
        </a-form-item>
        <a-form-item label="项目类型">
          <a-select 
            v-decorator="['itemType', { rules: [{ required: true, message: '请选择项目类型' }] }]"
            placeholder="请选择项目类型">
            <a-select-option value="FIXED">固定薪酬</a-select-option>
            <a-select-option value="COMMISSION">提成</a-select-option>
            <a-select-option value="ALLOWANCE">津贴</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="计算公式">
          <a-textarea 
            v-decorator="['calculationFormula']"
            placeholder="请输入计算公式"
            :rows="3" />
        </a-form-item>
        <a-form-item label="默认比例">
          <a-input-number 
            v-decorator="['defaultRate']"
            :min="0" 
            :max="1"
            :step="0.01"
            style="width: 100%;"
            placeholder="0.05 表示 5%" />
        </a-form-item>
        <a-form-item label="基础金额">
          <a-input-number 
            v-decorator="['baseAmount']"
            :min="0"
            style="width: 100%;"
            placeholder="基础金额" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea 
            v-decorator="['description']"
            placeholder="请输入描述"
            :rows="3" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number 
            v-decorator="['sortOrder']"
            :min="0"
            style="width: 100%;"
            placeholder="排序号" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { getSalaryItemList, addSalaryItem, updateSalaryItem, updateSalaryItemStatus } from '@/api/salary'

export default {
  name: 'SalaryConfig',
  data() {
    return {
      // 薪酬项目数据
      salaryItems: [],
      itemLoading: false,
      
      // 薪酬项目表格列定义
      itemColumns: [
        {
          title: '项目编码',
          dataIndex: 'itemCode',
          key: 'itemCode',
          width: 120
        },
        {
          title: '项目名称',
          dataIndex: 'itemName',
          key: 'itemName',
          width: 150
        },
        {
          title: '项目类型',
          dataIndex: 'itemType',
          key: 'itemType',
          width: 100,
          scopedSlots: { customRender: 'itemType' }
        },
        {
          title: '默认比例',
          dataIndex: 'defaultRate',
          key: 'defaultRate',
          width: 100,
          scopedSlots: { customRender: 'defaultRate' }
        },
        {
          title: '基础金额',
          dataIndex: 'baseAmount',
          key: 'baseAmount',
          width: 100,
          scopedSlots: { customRender: 'baseAmount' }
        },
        {
          title: '状态',
          dataIndex: 'status',
          key: 'status',
          width: 80,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '操作',
          key: 'action',
          width: 150,
          scopedSlots: { customRender: 'action' }
        }
      ],
      
      // 系统配置
      systemConfig: {
        defaultWorkDays: 22,
        calculationTimeout: 30,
        batchSize: 100,
        approvalEnabled: true,
        autoApproval: false,
        autoApprovalThreshold: 5000,
        itemCacheTtl: 24,
        resultCacheTtl: 7,
        defaultDataLevel: 'PERSONAL',
        inquiryPermissions: ['VIEW_PERSONAL'],
        operationPermissions: ['CALCULATE_SALARY']
      },
      
      // 表单
      configForm: this.$form.createForm(this),
      itemForm: this.$form.createForm(this),
      
      // 弹窗状态
      itemModalVisible: false,
      itemModalTitle: '新增薪酬项目',
      currentItem: null
    }
  },
  
  mounted() {
    this.loadSalaryItems()
    this.loadSystemConfig()
  },
  
  methods: {
    // 加载薪酬项目
    loadSalaryItems() {
      this.itemLoading = true
      getSalaryItemList().then(res => {
        if (res.success) {
          this.salaryItems = res.data || []
        }
      }).finally(() => {
        this.itemLoading = false
      })
    },
    
    // 加载系统配置
    loadSystemConfig() {
      // TODO: 从后端加载系统配置
    },
    
    // 获取项目类型颜色
    getItemTypeColor(type) {
      const colorMap = {
        'FIXED': 'blue',
        'COMMISSION': 'green',
        'ALLOWANCE': 'orange'
      }
      return colorMap[type] || 'default'
    },
    
    // 获取项目类型文本
    getItemTypeText(type) {
      const textMap = {
        'FIXED': '固定薪酬',
        'COMMISSION': '提成',
        'ALLOWANCE': '津贴'
      }
      return textMap[type] || type
    },
    
    // 新增薪酬项目
    handleAddItem() {
      this.currentItem = null
      this.itemModalTitle = '新增薪酬项目'
      this.itemForm.resetFields()
      this.itemModalVisible = true
    },
    
    // 编辑薪酬项目
    handleEditItem(record) {
      this.currentItem = record
      this.itemModalTitle = '编辑薪酬项目'
      this.$nextTick(() => {
        this.itemForm.setFieldsValue({
          itemCode: record.itemCode,
          itemName: record.itemName,
          itemType: record.itemType,
          calculationFormula: record.calculationFormula,
          defaultRate: record.defaultRate,
          baseAmount: record.baseAmount,
          description: record.description,
          sortOrder: record.sortOrder
        })
      })
      this.itemModalVisible = true
    },
    
    // 薪酬项目弹窗确定
    handleItemModalOk() {
      this.itemForm.validateFields((err, values) => {
        if (!err) {
          const apiCall = this.currentItem ? updateSalaryItem : addSalaryItem
          const data = this.currentItem ? { ...values, id: this.currentItem.id } : values
          
          apiCall(data).then(res => {
            if (res.success) {
              this.$message.success(this.currentItem ? '更新成功' : '新增成功')
              this.itemModalVisible = false
              this.loadSalaryItems()
            } else {
              this.$message.error(res.message || '操作失败')
            }
          })
        }
      })
    },
    
    // 薪酬项目弹窗取消
    handleItemModalCancel() {
      this.itemModalVisible = false
    },
    
    // 切换薪酬项目状态
    handleToggleItemStatus(record) {
      const newStatus = record.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
      
      updateSalaryItemStatus(record.id, newStatus).then(res => {
        if (res.success) {
          this.$message.success(`${action}成功`)
          this.loadSalaryItems()
        } else {
          this.$message.error(res.message || `${action}失败`)
        }
      })
    },
    
    // 保存系统配置
    handleSaveConfig() {
      // TODO: 保存系统配置到后端
      this.$message.success('配置保存成功')
    },
    
    // 重置系统配置
    handleResetConfig() {
      this.systemConfig = {
        defaultWorkDays: 22,
        calculationTimeout: 30,
        batchSize: 100,
        approvalEnabled: true,
        autoApproval: false,
        autoApprovalThreshold: 5000,
        itemCacheTtl: 24,
        resultCacheTtl: 7,
        defaultDataLevel: 'PERSONAL',
        inquiryPermissions: ['VIEW_PERSONAL'],
        operationPermissions: ['CALCULATE_SALARY']
      }
      this.$message.info('配置已重置')
    }
  }
}
</script>

<style scoped>
.salary-config-container {
  padding: 24px;
  background: #f7f8fa;
  min-height: 100vh;
}

.action-buttons {
  text-align: left;
}
</style>
