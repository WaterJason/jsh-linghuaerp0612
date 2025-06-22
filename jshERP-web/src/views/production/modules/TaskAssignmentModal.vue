<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false">

    <div class="assignment-content">
      <!-- 任务信息展示 -->
      <div class="task-info-section" v-if="selectedTasks.length > 0">
        <h4 class="section-title">
          <a-icon type="file-text" />
          {{ selectedTasks.length === 1 ? '任务信息' : `批量派单 (${selectedTasks.length}个任务)` }}
        </h4>

        <!-- 单个任务详情 -->
        <div v-if="selectedTasks.length === 1" class="single-task-info">
          <a-row :gutter="16">
            <a-col :span="8">
              <div class="info-item">
                <span class="label">任务名称：</span>
                <span class="value">{{ selectedTasks[0].taskName }}</span>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="info-item">
                <span class="label">任务编号：</span>
                <span class="value">{{ selectedTasks[0].taskNumber }}</span>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="info-item">
                <span class="label">任务类型：</span>
                <span class="value">{{ getTaskTypeText(selectedTasks[0].taskType) }}</span>
              </div>
            </a-col>
          </a-row>
          <a-row :gutter="16" style="margin-top: 12px;">
            <a-col :span="8">
              <div class="info-item">
                <span class="label">制作数量：</span>
                <span class="value">{{ selectedTasks[0].taskQuantity }} {{ selectedTasks[0].unitName }}</span>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="info-item">
                <span class="label">优先级：</span>
                <a-tag :color="getPriorityColor(selectedTasks[0].priority)">
                  {{ getPriorityText(selectedTasks[0].priority) }}
                </a-tag>
              </div>
            </a-col>
            <a-col :span="8">
              <div class="info-item">
                <span class="label">预估工时：</span>
                <span class="value">{{ selectedTasks[0].estimatedHours || 0 }} 小时</span>
              </div>
            </a-col>
          </a-row>
        </div>

        <!-- 批量任务列表 -->
        <div v-else class="batch-task-list">
          <a-table
            :columns="taskColumns"
            :dataSource="selectedTasks"
            :pagination="false"
            size="small"
            :scroll="{ y: 200 }">
            <template slot="taskType" slot-scope="text">
              {{ getTaskTypeText(text) }}
            </template>
            <template slot="priority" slot-scope="text">
              <a-tag :color="getPriorityColor(text)">
                {{ getPriorityText(text) }}
              </a-tag>
            </template>
          </a-table>
        </div>
      </div>

      <!-- 工人选择区域 -->
      <div class="worker-selection-section">
        <h4 class="section-title">
          <a-icon type="team" />
          工人选择
        </h4>

        <!-- 筛选条件 -->
        <div class="filter-section">
          <a-row :gutter="16">
            <a-col :span="8">
              <a-select
                v-model="filterSkillType"
                placeholder="按技能类型筛选"
                allowClear
                @change="filterWorkers">
                <a-select-option value="QISI_DIANLIAN">掐丝点蓝</a-select-option>
                <a-select-option value="PEISHI_ZHIZUO">配饰制作</a-select-option>
                <a-select-option value="HOUGONG_CHULI">后工处理</a-select-option>
                <a-select-option value="ZHILIANG_JIANYAN">质量检验</a-select-option>
              </a-select>
            </a-col>
            <a-col :span="8">
              <a-select
                v-model="filterSkillLevel"
                placeholder="按技能等级筛选"
                allowClear
                @change="filterWorkers">
                <a-select-option value="MASTER">大师</a-select-option>
                <a-select-option value="EXPERT">专家</a-select-option>
                <a-select-option value="ADVANCED">高级</a-select-option>
                <a-select-option value="INTERMEDIATE">中级</a-select-option>
                <a-select-option value="BEGINNER">新手</a-select-option>
              </a-select>
            </a-col>
            <a-col :span="8">
              <a-input
                v-model="filterWorkerName"
                placeholder="搜索工人姓名"
                @input="filterWorkers">
                <a-icon slot="prefix" type="search" />
              </a-input>
            </a-col>
          </a-row>
        </div>

        <!-- 工人列表 -->
        <div class="worker-list">
          <a-table
            :columns="workerColumns"
            :dataSource="filteredWorkers"
            :pagination="{ pageSize: 5, size: 'small' }"
            :rowSelection="workerRowSelection"
            size="small">
            <template slot="workerName" slot-scope="text, record">
              <div class="worker-info">
                <a-avatar :size="32" icon="user" />
                <div class="worker-details">
                  <div class="name">{{ text }}</div>
                  <div class="position">{{ record.position || '生产工人' }}</div>
                </div>
              </div>
            </template>
            <template slot="skillInfo" slot-scope="text, record">
              <div class="skill-tags">
                <a-tag
                  v-for="skill in record.skills"
                  :key="skill.skillType"
                  :color="getSkillLevelColor(skill.skillLevel)">
                  {{ getTaskTypeText(skill.skillType) }} - {{ getSkillLevelText(skill.skillLevel) }}
                </a-tag>
              </div>
            </template>
            <template slot="workload" slot-scope="text, record">
              <div class="workload-info">
                <a-progress
                  :percent="record.workloadPercent"
                  :status="record.workloadPercent > 80 ? 'exception' : 'normal'"
                  size="small" />
                <div class="workload-text">
                  {{ record.currentTasks }}/{{ record.maxTasks }} 任务
                </div>
              </div>
            </template>
            <template slot="efficiency" slot-scope="text">
              <a-rate :value="text / 20" disabled allow-half />
              <span style="margin-left: 8px;">{{ text }}%</span>
            </template>
          </a-table>
        </div>
      </div>

      <!-- 派单设置 -->
      <div class="assignment-settings-section" v-if="selectedWorkers.length > 0">
        <h4 class="section-title">
          <a-icon type="setting" />
          派单设置
        </h4>

        <a-form :form="form" layout="horizontal">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="计划开始时间" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
                <a-date-picker
                  v-decorator="['planStartTime', { rules: [{ required: true, message: '请选择计划开始时间' }] }]"
                  showTime
                  format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择开始时间"
                  style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="计划完成时间" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
                <a-date-picker
                  v-decorator="['planEndTime', { rules: [{ required: true, message: '请选择计划完成时间' }] }]"
                  showTime
                  format="YYYY-MM-DD HH:mm:ss"
                  placeholder="选择完成时间"
                  style="width: 100%" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="单价工费" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
                <a-input-number
                  v-decorator="['unitFee', { initialValue: 0 }]"
                  :min="0"
                  :precision="2"
                  placeholder="输入单价工费"
                  style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="备注说明" :labelCol="{ span: 8 }" :wrapperCol="{ span: 16 }">
                <a-textarea
                  v-decorator="['remark']"
                  placeholder="输入备注说明"
                  :rows="2" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <!-- 选中的工人信息 -->
      <div class="selected-workers-section" v-if="selectedWorkers.length > 0">
        <h4 class="section-title">
          <a-icon type="check-circle" />
          已选择工人 ({{ selectedWorkers.length }})
        </h4>
        <div class="selected-worker-cards">
          <a-card
            v-for="worker in selectedWorkers"
            :key="worker.id"
            size="small"
            class="worker-card">
            <div class="worker-card-content">
              <a-avatar :size="40" icon="user" />
              <div class="worker-info">
                <div class="name">{{ worker.workerName }}</div>
                <div class="skills">
                  <a-tag
                    v-for="skill in worker.skills.slice(0, 2)"
                    :key="skill.skillType"
                    size="small">
                    {{ getTaskTypeText(skill.skillType) }}
                  </a-tag>
                </div>
                <div class="workload">
                  工作负荷: {{ worker.workloadPercent }}%
                </div>
              </div>
              <a-button
                type="link"
                icon="close"
                @click="removeWorker(worker.id)"
                class="remove-btn" />
            </div>
          </a-card>
        </div>
      </div>
    </div>

  </a-modal>
</template>

<script>
import { assignTaskToWorker, getAvailableWorkersBySkill } from '@/api/production'

export default {
  name: "TaskAssignmentModal",
  data() {
    return {
      title: "任务派单",
      visible: false,
      confirmLoading: false,
      selectedTasks: [],
      allWorkers: [],
      filteredWorkers: [],
      selectedWorkers: [],
      selectedWorkerIds: [],

      // 筛选条件
      filterSkillType: undefined,
      filterSkillLevel: undefined,
      filterWorkerName: '',

      // 表单
      form: this.$form.createForm(this),

      // 任务列表表格列
      taskColumns: [
        {
          title: '任务名称',
          dataIndex: 'taskName',
          width: 150,
        },
        {
          title: '任务编号',
          dataIndex: 'taskNumber',
          width: 120,
        },
        {
          title: '任务类型',
          dataIndex: 'taskType',
          scopedSlots: { customRender: 'taskType' },
          width: 100,
        },
        {
          title: '数量',
          dataIndex: 'taskQuantity',
          width: 80,
        },
        {
          title: '优先级',
          dataIndex: 'priority',
          scopedSlots: { customRender: 'priority' },
          width: 80,
        },
        {
          title: '预估工时',
          dataIndex: 'estimatedHours',
          width: 80,
          customRender: (text) => `${text || 0}h`
        }
      ],

      // 工人列表表格列
      workerColumns: [
        {
          title: '工人信息',
          dataIndex: 'workerName',
          scopedSlots: { customRender: 'workerName' },
          width: 150,
        },
        {
          title: '技能信息',
          dataIndex: 'skills',
          scopedSlots: { customRender: 'skillInfo' },
          width: 200,
        },
        {
          title: '工作负荷',
          dataIndex: 'workload',
          scopedSlots: { customRender: 'workload' },
          width: 120,
        },
        {
          title: '效率评分',
          dataIndex: 'efficiencyRate',
          scopedSlots: { customRender: 'efficiency' },
          width: 120,
        }
      ]
    }
  },

  computed: {
    // 工人选择配置
    workerRowSelection() {
      return {
        selectedRowKeys: this.selectedWorkerIds,
        onChange: this.onWorkerSelectionChange,
        getCheckboxProps: (record) => ({
          disabled: record.workloadPercent > 90, // 工作负荷超过90%的工人不能选择
        }),
      }
    }
  },

  methods: {
    // 打开单个任务派单
    assign(task) {
      this.selectedTasks = [task];
      this.visible = true;
      this.title = `任务派单 - ${task.taskName}`;
      this.loadWorkers();
      this.resetForm();
    },

    // 打开批量任务派单
    batchAssign(tasks) {
      this.selectedTasks = tasks;
      this.visible = true;
      this.title = `批量派单 - ${tasks.length}个任务`;
      this.loadWorkers();
      this.resetForm();
    },

    // 重置表单
    resetForm() {
      this.form.resetFields();
      this.selectedWorkers = [];
      this.selectedWorkerIds = [];
      this.filterSkillType = undefined;
      this.filterSkillLevel = undefined;
      this.filterWorkerName = '';
    },

    // 加载可用工人列表
    async loadWorkers() {
      try {
        // 根据任务类型获取对应技能的工人
        const skillTypes = [...new Set(this.selectedTasks.map(task => task.taskType))];
        const response = await getAvailableWorkersBySkill(skillTypes.join(','));

        if (response.code === 200) {
          this.allWorkers = response.data.map(worker => ({
            ...worker,
            key: worker.id,
            workloadPercent: this.calculateWorkloadPercent(worker),
            currentTasks: worker.currentTasks || 0,
            maxTasks: worker.maxTasks || 10
          }));
          this.filteredWorkers = [...this.allWorkers];
        }
      } catch (error) {
        console.error('加载工人列表失败:', error);
        this.$message.error('加载工人列表失败');
      }
    },

    // 计算工作负荷百分比
    calculateWorkloadPercent(worker) {
      const currentTasks = worker.currentTasks || 0;
      const maxTasks = worker.maxTasks || 10;
      return Math.round((currentTasks / maxTasks) * 100);
    },

    // 筛选工人
    filterWorkers() {
      let filtered = [...this.allWorkers];

      // 按技能类型筛选
      if (this.filterSkillType) {
        filtered = filtered.filter(worker =>
          worker.skills && worker.skills.some(skill => skill.skillType === this.filterSkillType)
        );
      }

      // 按技能等级筛选
      if (this.filterSkillLevel) {
        filtered = filtered.filter(worker =>
          worker.skills && worker.skills.some(skill => skill.skillLevel === this.filterSkillLevel)
        );
      }

      // 按姓名搜索
      if (this.filterWorkerName) {
        filtered = filtered.filter(worker =>
          worker.workerName.toLowerCase().includes(this.filterWorkerName.toLowerCase())
        );
      }

      this.filteredWorkers = filtered;
    },

    // 工人选择变更
    onWorkerSelectionChange(selectedRowKeys, selectedRows) {
      this.selectedWorkerIds = selectedRowKeys;
      this.selectedWorkers = selectedRows;
    },

    // 移除选中的工人
    removeWorker(workerId) {
      this.selectedWorkerIds = this.selectedWorkerIds.filter(id => id !== workerId);
      this.selectedWorkers = this.selectedWorkers.filter(worker => worker.id !== workerId);
    },

    // 确认派单
    handleOk() {
      if (this.selectedWorkers.length === 0) {
        this.$message.warning('请选择至少一个工人');
        return;
      }

      this.form.validateFields(async (err, values) => {
        if (!err) {
          this.confirmLoading = true;
          try {
            // 执行派单操作
            for (const task of this.selectedTasks) {
              for (const worker of this.selectedWorkers) {
                await assignTaskToWorker({
                  taskId: task.id,
                  workerId: worker.id,
                  workerName: worker.workerName,
                  planStartTime: values.planStartTime.format('YYYY-MM-DD HH:mm:ss'),
                  planEndTime: values.planEndTime.format('YYYY-MM-DD HH:mm:ss'),
                  unitFee: values.unitFee || 0,
                  remark: values.remark || ''
                });
              }
            }

            this.$message.success('派单成功！');
            this.visible = false;
            this.$emit('ok');
          } catch (error) {
            console.error('派单失败:', error);
            this.$message.error('派单失败，请重试');
          } finally {
            this.confirmLoading = false;
          }
        }
      });
    },

    // 取消派单
    handleCancel() {
      this.visible = false;
      this.resetForm();
    },

    // 获取任务类型文本
    getTaskTypeText(type) {
      const typeMap = {
        'QISI_DIANLIAN': '掐丝点蓝',
        'PEISHI_ZHIZUO': '配饰制作',
        'HOUGONG_CHULI': '后工处理',
        'ZHILIANG_JIANYAN': '质量检验'
      };
      return typeMap[type] || type;
    },

    // 获取优先级文本
    getPriorityText(priority) {
      const priorityMap = {
        'LOW': '低',
        'NORMAL': '普通',
        'HIGH': '高',
        'URGENT': '紧急'
      };
      return priorityMap[priority] || priority;
    },

    // 获取优先级颜色
    getPriorityColor(priority) {
      const colorMap = {
        'LOW': 'green',
        'NORMAL': 'blue',
        'HIGH': 'orange',
        'URGENT': 'red'
      };
      return colorMap[priority] || 'default';
    },

    // 获取技能等级文本
    getSkillLevelText(level) {
      const levelMap = {
        'BEGINNER': '新手',
        'INTERMEDIATE': '中级',
        'ADVANCED': '高级',
        'EXPERT': '专家',
        'MASTER': '大师'
      };
      return levelMap[level] || level;
    },

    // 获取技能等级颜色
    getSkillLevelColor(level) {
      const colorMap = {
        'BEGINNER': 'default',
        'INTERMEDIATE': 'blue',
        'ADVANCED': 'green',
        'EXPERT': 'orange',
        'MASTER': 'red'
      };
      return colorMap[level] || 'default';
    }
  }
}
</script>

<style scoped>
.assignment-content {
  padding: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.section-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

/* 任务信息区域 */
.task-info-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.single-task-info .info-item {
  display: flex;
  align-items: center;
}

.single-task-info .label {
  color: #666;
  margin-right: 8px;
  min-width: 80px;
}

.single-task-info .value {
  color: #262626;
  font-weight: 500;
}

.batch-task-list {
  max-height: 200px;
  overflow-y: auto;
}

/* 工人选择区域 */
.worker-selection-section {
  margin-bottom: 24px;
}

.filter-section {
  margin-bottom: 16px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
}

.worker-list {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}

.worker-info {
  display: flex;
  align-items: center;
}

.worker-details {
  margin-left: 12px;
}

.worker-details .name {
  font-weight: 500;
  color: #262626;
}

.worker-details .position {
  font-size: 12px;
  color: #8c8c8c;
}

.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.workload-info {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.workload-text {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
}

/* 派单设置区域 */
.assignment-settings-section {
  margin-bottom: 24px;
  padding: 16px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
}

/* 选中工人区域 */
.selected-workers-section {
  margin-bottom: 16px;
}

.selected-worker-cards {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.worker-card {
  width: 280px;
  border: 1px solid #1890ff;
  border-radius: 6px;
}

.worker-card-content {
  display: flex;
  align-items: center;
  position: relative;
}

.worker-card .worker-info {
  margin-left: 12px;
  flex: 1;
}

.worker-card .name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.worker-card .skills {
  margin-bottom: 4px;
}

.worker-card .workload {
  font-size: 12px;
  color: #8c8c8c;
}

.remove-btn {
  position: absolute;
  top: -8px;
  right: -8px;
  color: #ff4d4f;
}

.remove-btn:hover {
  color: #ff7875;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .worker-card {
    width: 100%;
  }

  .selected-worker-cards {
    flex-direction: column;
  }
}
</style>
