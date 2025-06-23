<template>
  <a-modal
    title="任务管理"
    :visible="visible"
    :width="700"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
  >
    <a-form-model
      ref="form"
      :model="form"
      :rules="rules"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 16 }"
    >
      <a-form-model-item label="任务标题" prop="title">
        <a-input v-model="form.title" placeholder="请输入任务标题" />
      </a-form-model-item>
      
      <a-form-model-item label="任务描述" prop="description">
        <a-textarea
          v-model="form.description"
          :rows="4"
          placeholder="请输入任务描述"
        />
      </a-form-model-item>
      
      <a-form-model-item label="负责人" prop="assignee">
        <a-select v-model="form.assignee" placeholder="请选择负责人">
          <a-select-option value="emp001">张三</a-select-option>
          <a-select-option value="emp002">李四</a-select-option>
          <a-select-option value="emp003">王五</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="优先级" prop="priority">
        <a-select v-model="form.priority" placeholder="请选择优先级">
          <a-select-option value="high">高</a-select-option>
          <a-select-option value="medium">中</a-select-option>
          <a-select-option value="low">低</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="截止日期" prop="dueDate">
        <a-date-picker
          v-model="form.dueDate"
          format="YYYY-MM-DD"
          placeholder="请选择截止日期"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="状态" prop="status">
        <a-select v-model="form.status" placeholder="请选择状态">
          <a-select-option value="pending">待处理</a-select-option>
          <a-select-option value="in_progress">进行中</a-select-option>
          <a-select-option value="completed">已完成</a-select-option>
          <a-select-option value="cancelled">已取消</a-select-option>
        </a-select>
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
export default {
  name: 'TaskModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    record: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      confirmLoading: false,
      form: {
        id: null,
        title: '',
        description: '',
        assignee: '',
        priority: 'medium',
        dueDate: null,
        status: 'pending'
      },
      rules: {
        title: [
          { required: true, message: '请输入任务标题', trigger: 'blur' }
        ],
        assignee: [
          { required: true, message: '请选择负责人', trigger: 'change' }
        ],
        priority: [
          { required: true, message: '请选择优先级', trigger: 'change' }
        ]
      }
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.initForm()
      }
    }
  },
  methods: {
    initForm() {
      if (this.record && this.record.id) {
        this.form = { ...this.record }
      } else {
        this.form = {
          id: null,
          title: '',
          description: '',
          assignee: '',
          priority: 'medium',
          dueDate: null,
          status: 'pending'
        }
      }
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          setTimeout(() => {
            this.confirmLoading = false
            this.$emit('ok', this.form)
            this.$message.success('任务保存成功')
          }, 1000)
        }
      })
    },
    handleCancel() {
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.ant-form-item {
  margin-bottom: 16px;
}
</style>
