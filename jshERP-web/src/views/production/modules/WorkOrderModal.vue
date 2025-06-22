<template>
  <a-modal
    :title="title"
    :width="800"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel">
    
    <div class="workorder-content">
      <a-alert 
        message="工单管理功能" 
        description="此功能正在开发中，将支持工单创建、编辑、任务分解等功能。" 
        type="info" 
        showIcon />
      
      <div style="margin-top: 20px;" v-if="currentTask">
        <h4>当前工单信息：</h4>
        <p><strong>工单名称：</strong>{{ currentTask.taskName }}</p>
        <p><strong>工单编号：</strong>{{ currentTask.taskNumber }}</p>
      </div>
    </div>
    
  </a-modal>
</template>

<script>
export default {
  name: "WorkOrderModal",
  data() {
    return {
      title: "工单管理",
      visible: false,
      confirmLoading: false,
      currentTask: null,
      action: ""
    }
  },
  methods: {
    add() {
      this.action = "add";
      this.currentTask = null;
      this.visible = true;
      this.title = "新增工单";
    },
    
    edit(task) {
      this.action = "edit";
      this.currentTask = task;
      this.visible = true;
      this.title = `编辑工单 - ${task.taskName}`;
    },
    
    handleOk() {
      this.confirmLoading = true;
      setTimeout(() => {
        this.confirmLoading = false;
        this.visible = false;
        this.$message.success(this.action === 'add' ? '新增成功！' : '编辑成功！');
        this.$emit('ok');
      }, 1000);
    },
    
    handleCancel() {
      this.visible = false;
      this.currentTask = null;
      this.action = "";
    }
  }
}
</script>

<style scoped>
.workorder-content {
  padding: 20px 0;
}
</style>
