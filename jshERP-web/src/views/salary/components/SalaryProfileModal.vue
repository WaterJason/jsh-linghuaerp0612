<template>
  <a-modal
    :title="modalTitle"
    :visible="visible"
    :width="800"
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
      <a-form-model-item label="员工姓名" prop="employeeName">
        <a-input v-model="form.employeeName" placeholder="请输入员工姓名" />
      </a-form-model-item>
      
      <a-form-model-item label="员工编号" prop="employeeCode">
        <a-input v-model="form.employeeCode" placeholder="请输入员工编号" />
      </a-form-model-item>
      
      <a-form-model-item label="部门" prop="department">
        <a-select v-model="form.department" placeholder="请选择部门">
          <a-select-option value="cloisonne">掐丝珐琅馆</a-select-option>
          <a-select-option value="coffee">咖啡店</a-select-option>
          <a-select-option value="management">管理部门</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="职位" prop="position">
        <a-input v-model="form.position" placeholder="请输入职位" />
      </a-form-model-item>
      
      <a-form-model-item label="基础工资" prop="baseSalary">
        <a-input-number
          v-model="form.baseSalary"
          :min="0"
          :precision="2"
          placeholder="请输入基础工资"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="绩效工资" prop="performanceSalary">
        <a-input-number
          v-model="form.performanceSalary"
          :min="0"
          :precision="2"
          placeholder="请输入绩效工资"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="津贴补助" prop="allowance">
        <a-input-number
          v-model="form.allowance"
          :min="0"
          :precision="2"
          placeholder="请输入津贴补助"
          style="width: 100%"
        />
      </a-form-model-item>
      
      <a-form-model-item label="状态" prop="status">
        <a-select v-model="form.status" placeholder="请选择状态">
          <a-select-option value="active">启用</a-select-option>
          <a-select-option value="inactive">停用</a-select-option>
        </a-select>
      </a-form-model-item>
      
      <a-form-model-item label="备注" prop="remark">
        <a-textarea
          v-model="form.remark"
          :rows="3"
          placeholder="请输入备注信息"
        />
      </a-form-model-item>
    </a-form-model>
  </a-modal>
</template>

<script>
export default {
  name: 'SalaryProfileModal',
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
        employeeName: '',
        employeeCode: '',
        department: '',
        position: '',
        baseSalary: 0,
        performanceSalary: 0,
        allowance: 0,
        status: 'active',
        remark: ''
      },
      rules: {
        employeeName: [
          { required: true, message: '请输入员工姓名', trigger: 'blur' }
        ],
        employeeCode: [
          { required: true, message: '请输入员工编号', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        position: [
          { required: true, message: '请输入职位', trigger: 'blur' }
        ],
        baseSalary: [
          { required: true, message: '请输入基础工资', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    modalTitle() {
      return this.form.id ? '编辑薪资档案' : '新增薪资档案'
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
        // 编辑模式
        this.form = { ...this.record }
      } else {
        // 新增模式
        this.form = {
          id: null,
          employeeName: '',
          employeeCode: '',
          department: '',
          position: '',
          baseSalary: 0,
          performanceSalary: 0,
          allowance: 0,
          status: 'active',
          remark: ''
        }
      }
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true
          // 这里应该调用API保存数据
          // 暂时模拟异步操作
          setTimeout(() => {
            this.confirmLoading = false
            this.$emit('ok', this.form)
            this.$message.success('保存成功')
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
