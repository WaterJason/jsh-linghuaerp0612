<template>
  <div class="config-container">
    <a-row :gutter="24">
      <!-- 基础配置 -->
      <a-col :span="12">
        <a-card title="基础配置" :bordered="false">
          <a-form-model
            ref="basicForm"
            :model="basicConfig"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }">
            
            <a-form-model-item label="店铺名称">
              <a-input v-model="basicConfig.shopName" placeholder="请输入店铺名称" />
            </a-form-model-item>
            
            <a-form-model-item label="营业时间">
              <a-time-picker
                v-model="basicConfig.openTime"
                format="HH:mm"
                placeholder="开始时间"
                style="width: 45%" />
              <span style="margin: 0 8px;">-</span>
              <a-time-picker
                v-model="basicConfig.closeTime"
                format="HH:mm"
                placeholder="结束时间"
                style="width: 45%" />
            </a-form-model-item>
            
            <a-form-model-item label="联系电话">
              <a-input v-model="basicConfig.phone" placeholder="请输入联系电话" />
            </a-form-model-item>
            
            <a-form-model-item label="地址">
              <a-textarea v-model="basicConfig.address" :rows="2" placeholder="请输入地址" />
            </a-form-model-item>
            
            <a-form-model-item label="自动接单">
              <a-switch v-model="basicConfig.autoAcceptOrder" />
            </a-form-model-item>
            
            <a-form-model-item label="库存预警">
              <a-switch v-model="basicConfig.stockAlert" />
            </a-form-model-item>
            
            <a-form-model-item :wrapper-col="{ span: 16, offset: 8 }">
              <a-button type="primary" @click="saveBasicConfig">保存基础配置</a-button>
            </a-form-model-item>
          </a-form-model>
        </a-card>
      </a-col>

      <!-- 支付配置 -->
      <a-col :span="12">
        <a-card title="支付配置" :bordered="false">
          <a-form-model
            ref="paymentForm"
            :model="paymentConfig"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }">
            
            <a-form-model-item label="支持现金">
              <a-switch v-model="paymentConfig.cashEnabled" />
            </a-form-model-item>
            
            <a-form-model-item label="支持银行卡">
              <a-switch v-model="paymentConfig.cardEnabled" />
            </a-form-model-item>
            
            <a-form-model-item label="支持微信支付">
              <a-switch v-model="paymentConfig.wechatEnabled" />
            </a-form-model-item>
            
            <a-form-model-item label="支持支付宝">
              <a-switch v-model="paymentConfig.alipayEnabled" />
            </a-form-model-item>
            
            <a-form-model-item label="微信商户号" v-if="paymentConfig.wechatEnabled">
              <a-input v-model="paymentConfig.wechatMerchantId" placeholder="请输入微信商户号" />
            </a-form-model-item>
            
            <a-form-model-item label="支付宝商户号" v-if="paymentConfig.alipayEnabled">
              <a-input v-model="paymentConfig.alipayMerchantId" placeholder="请输入支付宝商户号" />
            </a-form-model-item>
            
            <a-form-model-item :wrapper-col="{ span: 16, offset: 8 }">
              <a-button type="primary" @click="savePaymentConfig">保存支付配置</a-button>
            </a-form-model-item>
          </a-form-model>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="24" style="margin-top: 24px;">
      <!-- 打印配置 -->
      <a-col :span="12">
        <a-card title="打印配置" :bordered="false">
          <a-form-model
            ref="printForm"
            :model="printConfig"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }">
            
            <a-form-model-item label="自动打印">
              <a-switch v-model="printConfig.autoPrint" />
            </a-form-model-item>
            
            <a-form-model-item label="打印机名称">
              <a-select v-model="printConfig.printerName" placeholder="请选择打印机">
                <a-select-option value="default">默认打印机</a-select-option>
                <a-select-option value="thermal">热敏打印机</a-select-option>
                <a-select-option value="laser">激光打印机</a-select-option>
              </a-select>
            </a-form-model-item>
            
            <a-form-model-item label="小票宽度">
              <a-select v-model="printConfig.paperWidth" placeholder="请选择纸张宽度">
                <a-select-option value="58">58mm</a-select-option>
                <a-select-option value="80">80mm</a-select-option>
              </a-select>
            </a-form-model-item>
            
            <a-form-model-item label="打印份数">
              <a-input-number v-model="printConfig.copies" :min="1" :max="5" />
            </a-form-model-item>
            
            <a-form-model-item :wrapper-col="{ span: 16, offset: 8 }">
              <a-button type="primary" @click="savePrintConfig">保存打印配置</a-button>
              <a-button @click="testPrint" style="margin-left: 8px;">测试打印</a-button>
            </a-form-model-item>
          </a-form-model>
        </a-card>
      </a-col>

      <!-- 系统配置 -->
      <a-col :span="12">
        <a-card title="系统配置" :bordered="false">
          <a-form-model
            ref="systemForm"
            :model="systemConfig"
            :label-col="{ span: 8 }"
            :wrapper-col="{ span: 16 }">
            
            <a-form-model-item label="数据备份">
              <a-switch v-model="systemConfig.autoBackup" />
            </a-form-model-item>
            
            <a-form-model-item label="备份频率" v-if="systemConfig.autoBackup">
              <a-select v-model="systemConfig.backupFrequency" placeholder="请选择备份频率">
                <a-select-option value="daily">每日</a-select-option>
                <a-select-option value="weekly">每周</a-select-option>
                <a-select-option value="monthly">每月</a-select-option>
              </a-select>
            </a-form-model-item>
            
            <a-form-model-item label="日志保留">
              <a-input-number v-model="systemConfig.logRetentionDays" :min="1" :max="365" />
              <span style="margin-left: 8px;">天</span>
            </a-form-model-item>
            
            <a-form-model-item label="系统通知">
              <a-switch v-model="systemConfig.systemNotification" />
            </a-form-model-item>
            
            <a-form-model-item :wrapper-col="{ span: 16, offset: 8 }">
              <a-button type="primary" @click="saveSystemConfig">保存系统配置</a-button>
              <a-button @click="exportConfig" style="margin-left: 8px;">导出配置</a-button>
            </a-form-model-item>
          </a-form-model>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script>
import moment from 'moment'

export default {
  name: 'CloisonneConfig',
  data() {
    return {
      // 基础配置
      basicConfig: {
        shopName: '聆花掐丝珐琅馆',
        openTime: moment('09:00', 'HH:mm'),
        closeTime: moment('18:00', 'HH:mm'),
        phone: '020-12345678',
        address: '广州市天河区珠江新城',
        autoAcceptOrder: true,
        stockAlert: true
      },
      
      // 支付配置
      paymentConfig: {
        cashEnabled: true,
        cardEnabled: true,
        wechatEnabled: true,
        alipayEnabled: true,
        wechatMerchantId: '',
        alipayMerchantId: ''
      },
      
      // 打印配置
      printConfig: {
        autoPrint: false,
        printerName: 'default',
        paperWidth: '80',
        copies: 1
      },
      
      // 系统配置
      systemConfig: {
        autoBackup: true,
        backupFrequency: 'daily',
        logRetentionDays: 30,
        systemNotification: true
      }
    }
  },
  
  mounted() {
    this.loadConfig()
  },
  
  methods: {
    // 加载配置
    loadConfig() {
      // 这里应该调用API加载配置
      // 暂时使用默认值
    },
    
    // 保存基础配置
    saveBasicConfig() {
      this.$message.success('基础配置保存成功')
    },
    
    // 保存支付配置
    savePaymentConfig() {
      this.$message.success('支付配置保存成功')
    },
    
    // 保存打印配置
    savePrintConfig() {
      this.$message.success('打印配置保存成功')
    },
    
    // 保存系统配置
    saveSystemConfig() {
      this.$message.success('系统配置保存成功')
    },
    
    // 测试打印
    testPrint() {
      this.$message.info('正在测试打印...')
      setTimeout(() => {
        this.$message.success('测试打印完成')
      }, 2000)
    },
    
    // 导出配置
    exportConfig() {
      const config = {
        basic: this.basicConfig,
        payment: this.paymentConfig,
        print: this.printConfig,
        system: this.systemConfig
      }
      
      const dataStr = JSON.stringify(config, null, 2)
      const dataBlob = new Blob([dataStr], { type: 'application/json' })
      const url = URL.createObjectURL(dataBlob)
      const link = document.createElement('a')
      link.href = url
      link.download = 'cloisonne-config.json'
      link.click()
      URL.revokeObjectURL(url)
      
      this.$message.success('配置导出成功')
    }
  }
}
</script>

<style scoped>
.config-container {
  padding: 24px;
  background: #f7f8fa;
  min-height: 100vh;
}

.ant-card {
  margin-bottom: 24px;
}

.ant-form-item {
  margin-bottom: 16px;
}
</style>
