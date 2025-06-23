<template>
  <a-modal
    :title="title"
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
      <!-- 基本信息 -->
      <a-divider orientation="left">基本信息</a-divider>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="员工姓名" prop="employeeName">
            <a-input v-model="form.employeeName" placeholder="请输入员工姓名" />
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="员工编号" prop="employeeCode">
            <a-input v-model="form.employeeCode" placeholder="请输入员工编号" />
          </a-form-model-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="联系电话" prop="phone">
            <a-input v-model="form.phone" placeholder="请输入联系电话" />
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="邮箱" prop="email">
            <a-input v-model="form.email" placeholder="请输入邮箱" />
          </a-form-model-item>
        </a-col>
      </a-row>

      <!-- 工作信息 -->
      <a-divider orientation="left">工作信息</a-divider>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="部门" prop="department">
            <a-select v-model="form.department" placeholder="请选择部门">
              <a-select-option value="珐琅制作">珐琅制作</a-select-option>
              <a-select-option value="咖啡服务">咖啡服务</a-select-option>
              <a-select-option value="培训教学">培训教学</a-select-option>
              <a-select-option value="业务拓展">业务拓展</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="职位" prop="position">
            <a-input v-model="form.position" placeholder="请输入职位" />
          </a-form-model-item>
        </a-col>
      </a-row>

      <a-row :gutter="16">
        <a-col :span="12">
          <a-form-model-item label="入职时间" prop="entryDate">
            <a-date-picker
              v-model="form.entryDate"
              format="YYYY-MM-DD"
              placeholder="请选择入职时间"
              style="width: 100%"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="12">
          <a-form-model-item label="员工状态" prop="status">
            <a-select v-model="form.status" placeholder="请选择状态">
              <a-select-option value="ACTIVE">在职</a-select-option>
              <a-select-option value="INACTIVE">离职</a-select-option>
              <a-select-option value="PROBATION">试用期</a-select-option>
            </a-select>
          </a-form-model-item>
        </a-col>
      </a-row>

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
  name: 'EmployeeModal',
  data() {
    return {
      visible: false,
      confirmLoading: false,
      title: '新增员工',
      form: {
        id: null,
        employeeName: '',
        employeeCode: '',
        phone: '',
        email: '',
        department: '',
        position: '',
        entryDate: null,
        status: 'ACTIVE',
        remark: ''
      },
      rules: {
        employeeName: [
          { required: true, message: '请输入员工姓名', trigger: 'blur' }
        ],
        employeeCode: [
          { required: true, message: '请输入员工编号', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请选择部门', trigger: 'change' }
        ],
        position: [
          { required: true, message: '请输入职位', trigger: 'blur' }
        ],
        entryDate: [
          { required: true, message: '请选择入职时间', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    // 新增
    add() {
      this.title = '新增员工'
      this.visible = true
      this.form = {
        id: null,
        employeeName: '',
        employeeCode: '',
        phone: '',
        email: '',
        department: '',
        position: '',
        entryDate: null,
        status: 'ACTIVE',
        remark: ''
      }
    },

    // 编辑
    edit(record) {
      this.title = '编辑员工'
      this.visible = true
      this.form = { ...record }
      if (this.form.entryDate) {
        this.form.entryDate = this.$moment(this.form.entryDate)
      }
    },

    handleOk() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.confirmLoading = true

          // 处理日期格式
          const formData = { ...this.form }
          if (formData.entryDate) {
            formData.entryDate = formData.entryDate.format('YYYY-MM-DD')
          }

          // 模拟API调用
          setTimeout(() => {
            this.confirmLoading = false
            this.visible = false
            this.$emit('ok', formData)
            this.$message.success('保存成功')
          }, 1000)
        }
      })
    },

    handleCancel() {
      this.visible = false
      this.$emit('cancel')
    }
  }
}
</script>

<style scoped>
.ant-form-item {
  margin-bottom: 16px;
}

.ant-divider {
  margin: 16px 0;
  font-weight: bold;
  color: #1890ff;
}
</style>
