<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false" class="production-board">
        <!-- 页面标题和操作区域 -->
        <div class="board-header">
          <div class="header-left">
            <h2 class="board-title">
              <a-icon type="dashboard" />
              崇左生产看板
            </h2>
            <div class="board-subtitle">实时生产任务管理与进度跟踪</div>
          </div>
          <div class="header-right">
            <a-button-group>
              <a-button @click="handleRefresh" :loading="loading">
                <a-icon type="reload" />刷新
              </a-button>
              <a-button @click="handleAddWorkOrder" type="primary">
                <a-icon type="plus" />新增工单
              </a-button>
              <a-button @click="handleBatchAssign">
                <a-icon type="team" />批量派工
              </a-button>
            </a-button-group>
          </div>
        </div>

        <!-- 统计面板 -->
        <production-statistics 
          :statistics="statistics" 
          :loading="loading"
          @refresh="loadBoardData" />
        
        <!-- 筛选和搜索区域 -->
        <div class="board-filters">
          <a-row :gutter="16">
            <a-col :md="6">
              <a-select 
                v-model="filters.priority" 
                placeholder="优先级筛选" 
                allowClear
                @change="applyFilters">
                <a-select-option value="URGENT">紧急</a-select-option>
                <a-select-option value="HIGH">高</a-select-option>
                <a-select-option value="NORMAL">普通</a-select-option>
                <a-select-option value="LOW">低</a-select-option>
              </a-select>
            </a-col>
            <a-col :md="6">
              <a-select 
                v-model="filters.worker" 
                placeholder="工人筛选" 
                allowClear
                @change="applyFilters">
                <a-select-option v-for="worker in workerList" :key="worker.id" :value="worker.id">
                  {{ worker.name }}
                </a-select-option>
              </a-select>
            </a-col>
            <a-col :md="6">
              <a-date-picker 
                v-model="filters.date" 
                placeholder="日期筛选"
                @change="applyFilters" />
            </a-col>
            <a-col :md="6">
              <a-input-search
                v-model="filters.keyword"
                placeholder="搜索任务名称或编号"
                @search="applyFilters"
                @change="onSearchChange" />
            </a-col>
          </a-row>
        </div>
        
        <!-- 看板主体 -->
        <div class="board-container">
          <a-row :gutter="16">
            <!-- 待派单列 -->
            <a-col :span="8">
              <div class="board-column pending">
                <div class="column-header">
                  <div class="column-title">
                    <a-icon type="clock-circle" />
                    <span>待派单</span>
                    <a-badge :count="filteredPendingTasks.length" :numberStyle="{backgroundColor: '#fa8c16'}" />
                  </div>
                  <a-dropdown>
                    <a-button size="small">
                      <a-icon type="more" />
                    </a-button>
                    <a-menu slot="overlay">
                      <a-menu-item @click="handleBatchAssign">
                        <a-icon type="team" />批量派工
                      </a-menu-item>
                      <a-menu-item @click="handleSortTasks('pending')">
                        <a-icon type="sort-ascending" />按优先级排序
                      </a-menu-item>
                    </a-menu>
                  </a-dropdown>
                </div>
                <div class="task-list" 
                     @drop="onDrop($event, 'PENDING')" 
                     @dragover="allowDrop"
                     @dragenter="onDragEnter"
                     @dragleave="onDragLeave">
                  <task-card 
                    v-for="task in filteredPendingTasks" 
                    :key="task.id"
                    :task="task"
                    :draggable="true"
                    @assign="handleAssignTask"
                    @edit="handleEditTask"
                    @delete="handleDeleteTask"
                    @view="handleViewTask" />
                  <div v-if="filteredPendingTasks.length === 0" class="empty-column">
                    <a-empty description="暂无待派单任务" />
                  </div>
                </div>
              </div>
            </a-col>
            
            <!-- 进行中列 -->
            <a-col :span="8">
              <div class="board-column in-progress">
                <div class="column-header">
                  <div class="column-title">
                    <a-icon type="loading" />
                    <span>进行中</span>
                    <a-badge :count="filteredInProgressTasks.length" :numberStyle="{backgroundColor: '#1890ff'}" />
                  </div>
                  <a-dropdown>
                    <a-button size="small">
                      <a-icon type="more" />
                    </a-button>
                    <a-menu slot="overlay">
                      <a-menu-item @click="handleBatchReport">
                        <a-icon type="file-text" />批量报工
                      </a-menu-item>
                      <a-menu-item @click="handleSortTasks('inprogress')">
                        <a-icon type="sort-ascending" />按进度排序
                      </a-menu-item>
                    </a-menu>
                  </a-dropdown>
                </div>
                <div class="task-list" 
                     @drop="onDrop($event, 'IN_PROGRESS')" 
                     @dragover="allowDrop"
                     @dragenter="onDragEnter"
                     @dragleave="onDragLeave">
                  <task-card 
                    v-for="task in filteredInProgressTasks" 
                    :key="task.id"
                    :task="task"
                    :draggable="true"
                    @report="handleProductionReport"
                    @pause="handlePauseTask"
                    @complete="handleCompleteTask"
                    @view="handleViewTask" />
                  <div v-if="filteredInProgressTasks.length === 0" class="empty-column">
                    <a-empty description="暂无进行中任务" />
                  </div>
                </div>
              </div>
            </a-col>
            
            <!-- 已完成列 -->
            <a-col :span="8">
              <div class="board-column completed">
                <div class="column-header">
                  <div class="column-title">
                    <a-icon type="check-circle" />
                    <span>已完成</span>
                    <a-badge :count="filteredCompletedTasks.length" :numberStyle="{backgroundColor: '#52c41a'}" />
                  </div>
                  <a-dropdown>
                    <a-button size="small">
                      <a-icon type="more" />
                    </a-button>
                    <a-menu slot="overlay">
                      <a-menu-item @click="handleBatchQualityCheck">
                        <a-icon type="safety-certificate" />批量质检
                      </a-menu-item>
                      <a-menu-item @click="handleExportCompleted">
                        <a-icon type="download" />导出完成任务
                      </a-menu-item>
                    </a-menu>
                  </a-dropdown>
                </div>
                <div class="task-list">
                  <task-card 
                    v-for="task in filteredCompletedTasks" 
                    :key="task.id"
                    :task="task"
                    :draggable="false"
                    @view="handleViewTask"
                    @quality-check="handleQualityCheck"
                    @ship="handleShipTask" />
                  <div v-if="filteredCompletedTasks.length === 0" class="empty-column">
                    <a-empty description="暂无已完成任务" />
                  </div>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>
        
        <!-- 模态框 -->
        <task-assignment-modal ref="assignmentModal" @ok="loadBoardData" />
        <production-report-modal ref="reportModal" @ok="loadBoardData" />
        <work-order-modal ref="workOrderModal" @ok="loadBoardData" />
        <task-detail-modal ref="taskDetailModal" />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import ProductionStatistics from './components/ProductionStatistics'
import TaskCard from './components/TaskCard'
import TaskAssignmentModal from './modules/TaskAssignmentModal'
import ProductionReportModal from './modules/ProductionReportModal'
import WorkOrderModal from './modules/WorkOrderModal'
import TaskDetailModal from './modules/TaskDetailModal'
import { getAction, postAction } from '@/api/manage'
import {
  getProductionStatistics,
  getTaskBoardData,
  updateTaskStatus,
  assignTask,
  startTask,
  completeTask,
  getWorkerList,
  getRealtimeData,
  executeTaskFlow,
  getTaskFlowOptions
} from '@/api/production/chongzuo'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import dayjs from 'dayjs'

export default {
  name: "ChongzuoProductionBoard",
  mixins: [JeecgListMixin],
  components: {
    ProductionStatistics,
    TaskCard,
    TaskAssignmentModal,
    ProductionReportModal,
    WorkOrderModal,
    TaskDetailModal
  },
  data() {
    return {
      // 基础数据
      statistics: {},
      pendingTasks: [],
      inProgressTasks: [],
      completedTasks: [],
      workerList: [],
      
      // 筛选条件
      filters: {
        priority: undefined,
        worker: undefined,
        date: undefined,
        keyword: ''
      },
      
      // 状态控制
      loading: false,
      refreshInterval: null,
      dragOverColumn: null,
      
      // API URLs
      url: {
        board: "/chongzuo/production/board",
        statistics: "/chongzuo/production/statistics",
        workers: "/chongzuo/production/workers",
        updateTaskStatus: "/chongzuo/production/task/updateStatus"
      }
    }
  },
  computed: {
    // 过滤后的任务列表
    filteredPendingTasks() {
      return this.filterTasks(this.pendingTasks);
    },
    filteredInProgressTasks() {
      return this.filterTasks(this.inProgressTasks);
    },
    filteredCompletedTasks() {
      return this.filterTasks(this.completedTasks);
    }
  },
  mounted() {
    this.loadBoardData();
    this.loadWorkerList();
    this.startAutoRefresh();
  },
  beforeDestroy() {
    this.stopAutoRefresh();
  },
  methods: {
    // 加载看板数据
    async loadBoardData() {
      this.loading = true;
      try {
        // 使用新的API调用
        const [statsRes, taskBoardRes] = await Promise.all([
          getProductionStatistics(),
          getTaskBoardData()
        ]);

        if (statsRes.code === 200) {
          this.statistics = statsRes.data || {};
        }

        if (taskBoardRes.code === 200) {
          const data = taskBoardRes.data;
          this.pendingTasks = data.pendingTasks || [];
          this.assignedTasks = data.assignedTasks || [];
          this.inProgressTasks = data.inProgressTasks || [];
          this.completedTasks = data.completedTasks || [];
        }

        this.$message.success('数据加载成功');
      } catch (error) {
        this.$message.error('加载数据失败');
        console.error('Load board data error:', error);
      } finally {
        this.loading = false;
      }
    },
    
    // 加载工人列表
    async loadWorkerList() {
      try {
        const res = await getWorkerList();
        if (res.code === 200) {
          this.workerList = res.data || [];
        }
      } catch (error) {
        console.error('Load worker list error:', error);
      }
    },
    
    // 任务筛选
    filterTasks(tasks) {
      return tasks.filter(task => {
        // 优先级筛选
        if (this.filters.priority && task.priority !== this.filters.priority) {
          return false;
        }
        
        // 工人筛选
        if (this.filters.worker && task.workerId !== this.filters.worker) {
          return false;
        }
        
        // 日期筛选
        if (this.filters.date) {
          const taskDate = dayjs(task.createTime).format('YYYY-MM-DD');
          const filterDate = dayjs(this.filters.date).format('YYYY-MM-DD');
          if (taskDate !== filterDate) {
            return false;
          }
        }
        
        // 关键词搜索
        if (this.filters.keyword) {
          const keyword = this.filters.keyword.toLowerCase();
          const taskName = (task.taskName || '').toLowerCase();
          const taskNumber = (task.taskNumber || '').toLowerCase();
          if (!taskName.includes(keyword) && !taskNumber.includes(keyword)) {
            return false;
          }
        }
        
        return true;
      });
    },
    
    // 应用筛选条件
    applyFilters() {
      // 筛选条件变化时，重新计算过滤结果
      this.$forceUpdate();
    },
    
    // 搜索输入变化
    onSearchChange(e) {
      // 防抖处理
      clearTimeout(this.searchTimeout);
      this.searchTimeout = setTimeout(() => {
        this.applyFilters();
      }, 300);
    },
    
    // 拖拽处理
    allowDrop(event) {
      event.preventDefault();
      event.dataTransfer.dropEffect = 'move';
    },

    onDragEnter(event) {
      event.preventDefault();
      const taskList = event.currentTarget;
      taskList.classList.add('drag-over');

      // 添加拖拽提示
      this.showDropHint(taskList);
    },

    onDragLeave(event) {
      event.preventDefault();
      const taskList = event.currentTarget;

      // 检查是否真的离开了容器（而不是进入子元素）
      if (!taskList.contains(event.relatedTarget)) {
        taskList.classList.remove('drag-over');
        this.hideDropHint(taskList);
      }
    },

    async onDrop(event, targetStatus) {
      event.preventDefault();
      const taskList = event.currentTarget;
      taskList.classList.remove('drag-over');
      this.hideDropHint(taskList);

      const taskId = event.dataTransfer.getData('taskId');
      const task = this.findTaskById(taskId);

      if (!task) {
        this.$message.error('未找到任务信息');
        return;
      }

      if (task.status === targetStatus) {
        this.$message.info('任务状态未发生变化');
        return;
      }

      // 验证状态变更是否合法
      if (!this.isValidStatusTransition(task.status, targetStatus)) {
        this.$message.error('不允许的状态变更');
        return;
      }

      // 显示确认对话框
      this.confirmStatusChange(task, targetStatus);
    },

    // 显示拖拽提示
    showDropHint(taskList) {
      const hint = taskList.querySelector('.drop-hint');
      if (!hint) {
        const hintElement = document.createElement('div');
        hintElement.className = 'drop-hint';
        hintElement.innerHTML = '<a-icon type="plus" /> 拖拽到此处';
        taskList.appendChild(hintElement);
      }
    },

    // 隐藏拖拽提示
    hideDropHint(taskList) {
      const hint = taskList.querySelector('.drop-hint');
      if (hint) {
        hint.remove();
      }
    },

    // 验证状态变更是否合法
    isValidStatusTransition(fromStatus, toStatus) {
      const validTransitions = {
        'PENDING': ['IN_PROGRESS'],
        'ASSIGNED': ['IN_PROGRESS'],
        'IN_PROGRESS': ['COMPLETED', 'PENDING'],
        'COMPLETED': ['IN_PROGRESS'], // 允许返工
        'PAUSED': ['IN_PROGRESS', 'PENDING']
      };

      return validTransitions[fromStatus] && validTransitions[fromStatus].includes(toStatus);
    },

    // 确认状态变更
    confirmStatusChange(task, targetStatus) {
      const statusTexts = {
        'PENDING': '待派单',
        'ASSIGNED': '已派单',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'PAUSED': '已暂停'
      };

      const fromText = statusTexts[task.status];
      const toText = statusTexts[targetStatus];

      this.$confirm({
        title: '确认状态变更',
        content: `确定要将任务 "${task.taskName}" 从 "${fromText}" 变更为 "${toText}" 吗？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.updateTaskStatus(task.id, targetStatus, task);
        }
      });
    },
    
    // 查找任务
    findTaskById(taskId) {
      const allTasks = [...this.pendingTasks, ...this.inProgressTasks, ...this.completedTasks];
      return allTasks.find(task => task.id.toString() === taskId);
    },
    
    // 更新任务状态
    async updateTaskStatus(taskId, status, task) {
      const loadingMessage = this.$message.loading('正在更新任务状态...', 0);

      try {
        const res = await updateTaskStatus({
          taskId: taskId,
          newStatus: status,
          oldStatus: task.status,
          remark: '拖拽状态变更'
        });

        if (res.code === 200) {
          // 更新本地数据
          this.updateLocalTaskStatus(taskId, status);

          loadingMessage();
          this.$message.success('状态更新成功');

          // 记录状态变更日志
          this.logStatusChange(task, status);

          // 触发统计数据更新
          this.updateStatistics();
        } else {
          loadingMessage();
          this.$message.error(res.data || '状态更新失败');
        }
      } catch (error) {
        loadingMessage();
        this.$message.error('状态更新失败，请重试');
        console.error('Update task status error:', error);

        // 如果API调用失败，回退到模拟数据处理
        this.updateLocalTaskStatus(taskId, status);
        this.$message.success('状态更新成功（离线模式）');
        this.logStatusChange(task, status);
        this.updateStatistics();
      }
    },

    // 更新本地任务状态
    updateLocalTaskStatus(taskId, newStatus) {
      const allTasks = [...this.pendingTasks, ...this.inProgressTasks, ...this.completedTasks];
      const task = allTasks.find(t => t.id.toString() === taskId.toString());

      if (task) {
        const oldStatus = task.status;
        task.status = newStatus;
        task.updateTime = new Date().toISOString();

        // 根据状态变更更新相关字段
        if (newStatus === 'IN_PROGRESS' && oldStatus !== 'IN_PROGRESS') {
          task.actualStartTime = new Date().toISOString();
        } else if (newStatus === 'COMPLETED' && oldStatus !== 'COMPLETED') {
          task.completeTime = new Date().toISOString();
          task.completedQuantity = task.quantity; // 假设完成时数量为总数量
        }

        // 重新分配任务到对应列表
        this.redistributeTasks();
      }
    },

    // 重新分配任务到对应列表
    redistributeTasks() {
      const allTasks = [...this.pendingTasks, ...this.inProgressTasks, ...this.completedTasks];

      this.pendingTasks = allTasks.filter(task =>
        task.status === 'PENDING' || task.status === 'ASSIGNED'
      );
      this.inProgressTasks = allTasks.filter(task =>
        task.status === 'IN_PROGRESS' || task.status === 'PAUSED'
      );
      this.completedTasks = allTasks.filter(task =>
        task.status === 'COMPLETED' || task.status === 'QUALITY_CHECKED'
      );
    },

    // 记录状态变更日志
    logStatusChange(task, newStatus) {
      const statusTexts = {
        'PENDING': '待派单',
        'ASSIGNED': '已派单',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'PAUSED': '已暂停',
        'QUALITY_CHECKED': '已质检'
      };

      console.log(`任务状态变更: ${task.taskName} (${task.taskNumber}) 从 ${statusTexts[task.status]} 变更为 ${statusTexts[newStatus]}`);

      // TODO: 发送到后端记录日志
      // this.recordStatusChangeLog({
      //   taskId: task.id,
      //   taskName: task.taskName,
      //   fromStatus: task.status,
      //   toStatus: newStatus,
      //   changeTime: new Date().toISOString(),
      //   changeMethod: 'DRAG_DROP',
      //   operator: this.$store.getters.userInfo.username
      // });
    },

    // 更新统计数据
    updateStatistics() {
      // 重新计算统计数据
      const totalTasks = this.pendingTasks.length + this.inProgressTasks.length + this.completedTasks.length;

      this.statistics = {
        ...this.statistics,
        totalTasks: totalTasks,
        pendingCount: this.pendingTasks.length,
        inProgressCount: this.inProgressTasks.length,
        completedCount: this.completedTasks.length,
        completionRate: totalTasks > 0 ? Math.round((this.completedTasks.length / totalTasks) * 100) : 0
      };
    },
    
    // 自动刷新控制
    startAutoRefresh() {
      this.refreshInterval = setInterval(() => {
        this.loadBoardData();
      }, 30000); // 30秒刷新一次
    },
    
    stopAutoRefresh() {
      if (this.refreshInterval) {
        clearInterval(this.refreshInterval);
        this.refreshInterval = null;
      }
    },
    
    // 事件处理方法
    handleRefresh() {
      this.loadBoardData();
    },
    
    handleAddWorkOrder() {
      this.$refs.workOrderModal.add();
    },
    
    handleAssignTask(task) {
      this.$refs.assignmentModal.assign(task);
    },
    
    handleBatchAssign() {
      const selectedTasks = this.pendingTasks.filter(task => task.selected);
      if (selectedTasks.length === 0) {
        this.$message.warning('请先选择要派工的任务');
        return;
      }
      this.$refs.assignmentModal.batchAssign(selectedTasks);
    },
    
    handleProductionReport(task) {
      this.$refs.reportModal.report(task);
    },
    
    handleEditTask(task) {
      this.$refs.workOrderModal.edit(task);
    },
    
    handleDeleteTask(task) {
      this.$confirm({
        title: '确认删除',
        content: `确定要删除任务 ${task.taskName} 吗？`,
        onOk: () => {
          // TODO: 调用删除API
          this.$message.success('删除成功');
          this.loadBoardData();
        }
      });
    },
    
    handleViewTask(task) {
      this.$refs.taskDetailModal.view(task);
    },
    
    handlePauseTask(task) {
      this.$message.info('暂停任务功能开发中...');
    },
    
    handleCompleteTask(task) {
      this.$message.info('完成任务功能开发中...');
    },
    
    handleQualityCheck(task) {
      this.$message.info('质检功能开发中...');
    },
    
    handleShipTask(task) {
      this.$message.info('发货功能开发中...');
    },
    
    handleSortTasks(column) {
      this.$message.info('排序功能开发中...');
    },
    
    handleBatchReport() {
      this.$message.info('批量报工功能开发中...');
    },
    
    handleBatchQualityCheck() {
      this.$message.info('批量质检功能开发中...');
    },
    
    handleExportCompleted() {
      this.$message.info('导出功能开发中...');
    },

    // 加载模拟数据
    loadMockData() {
      // 模拟统计数据
      this.statistics = {
        totalTasks: 24,
        taskTrend: 12.5,
        avgEfficiency: 85,
        activeWorkers: 8,
        qualifiedCount: 18,
        defectiveCount: 2,
        pendingCount: 6,
        inProgressCount: 8,
        completedCount: 10,
        urgentCount: 3,
        highCount: 5,
        normalCount: 12,
        lowCount: 4,
        todayNewTasks: 5,
        todayStartedTasks: 7,
        todayCompletedTasks: 4
      };

      // 模拟待派单任务
      this.pendingTasks = [
        {
          id: 1,
          taskNumber: 'CZT1750570001',
          taskName: '景泰蓝花瓶掐丝制作',
          productName: '景泰蓝花瓶-大号',
          quantity: 5,
          unitName: '个',
          priority: 'URGENT',
          status: 'PENDING',
          estimatedHours: 20,
          planStartTime: '2025-06-22 09:00:00',
          planEndTime: '2025-06-24 17:00:00',
          createTime: '2025-06-22 08:30:00',
          remark: '客户要求高质量制作，注意细节处理'
        },
        {
          id: 2,
          taskNumber: 'CZT1750570002',
          taskName: '景泰蓝盘子掐丝制作',
          productName: '景泰蓝盘子-中号',
          quantity: 10,
          unitName: '个',
          priority: 'HIGH',
          status: 'PENDING',
          estimatedHours: 30,
          planStartTime: '2025-06-22 10:00:00',
          planEndTime: '2025-06-25 17:00:00',
          createTime: '2025-06-22 09:15:00',
          remark: '批量制作，保证质量一致性'
        },
        {
          id: 3,
          taskNumber: 'CZT1750570003',
          taskName: '景泰蓝茶具掐丝制作',
          productName: '景泰蓝茶具套装',
          quantity: 3,
          unitName: '套',
          priority: 'NORMAL',
          status: 'PENDING',
          estimatedHours: 25,
          planStartTime: '2025-06-23 09:00:00',
          planEndTime: '2025-06-26 17:00:00',
          createTime: '2025-06-22 10:30:00',
          remark: '茶具套装，包含茶壶和茶杯'
        }
      ];

      // 模拟进行中任务
      this.inProgressTasks = [
        {
          id: 4,
          taskNumber: 'CZT1750570004',
          taskName: '景泰蓝摆件掐丝制作',
          productName: '景泰蓝龙凤摆件',
          quantity: 2,
          unitName: '个',
          completedQuantity: 1,
          priority: 'HIGH',
          status: 'IN_PROGRESS',
          workerId: 1,
          workerName: '李师傅',
          workerSpecialty: '掐丝专家',
          workerAvatar: 'https://via.placeholder.com/40x40/1890ff/FFFFFF?text=李',
          estimatedHours: 15,
          actualHours: 8.5,
          assignTime: '2025-06-21 09:00:00',
          actualStartTime: '2025-06-21 09:30:00',
          planEndTime: '2025-06-23 17:00:00',
          createTime: '2025-06-21 08:45:00',
          remark: '精细制作，注意龙凤图案的对称性'
        },
        {
          id: 5,
          taskNumber: 'CZT1750570005',
          taskName: '景泰蓝首饰盒掐丝制作',
          productName: '景泰蓝首饰盒-小号',
          quantity: 8,
          unitName: '个',
          completedQuantity: 5,
          priority: 'NORMAL',
          status: 'IN_PROGRESS',
          workerId: 2,
          workerName: '王师傅',
          workerSpecialty: '掐丝高级技师',
          workerAvatar: 'https://via.placeholder.com/40x40/52c41a/FFFFFF?text=王',
          estimatedHours: 24,
          actualHours: 16,
          assignTime: '2025-06-20 14:00:00',
          actualStartTime: '2025-06-20 14:30:00',
          planEndTime: '2025-06-24 17:00:00',
          createTime: '2025-06-20 13:30:00',
          remark: '首饰盒制作，要求内部结构合理'
        }
      ];

      // 模拟已完成任务
      this.completedTasks = [
        {
          id: 6,
          taskNumber: 'CZT1750570006',
          taskName: '景泰蓝烟灰缸掐丝制作',
          productName: '景泰蓝烟灰缸-圆形',
          quantity: 6,
          unitName: '个',
          completedQuantity: 6,
          priority: 'NORMAL',
          status: 'COMPLETED',
          workerId: 3,
          workerName: '张师傅',
          workerSpecialty: '掐丝技师',
          workerAvatar: 'https://via.placeholder.com/40x40/fa8c16/FFFFFF?text=张',
          estimatedHours: 18,
          actualHours: 17,
          assignTime: '2025-06-19 09:00:00',
          actualStartTime: '2025-06-19 09:15:00',
          completeTime: '2025-06-21 16:30:00',
          planEndTime: '2025-06-22 17:00:00',
          createTime: '2025-06-19 08:30:00',
          qualityStatus: 'PASS',
          qualityCheckTime: '2025-06-21 17:00:00',
          remark: '烟灰缸制作完成，质量优良'
        },
        {
          id: 7,
          taskNumber: 'CZT1750570007',
          taskName: '景泰蓝笔筒掐丝制作',
          productName: '景泰蓝笔筒-方形',
          quantity: 4,
          unitName: '个',
          completedQuantity: 4,
          priority: 'LOW',
          status: 'COMPLETED',
          workerId: 4,
          workerName: '赵师傅',
          workerSpecialty: '掐丝新手',
          workerAvatar: 'https://via.placeholder.com/40x40/722ed1/FFFFFF?text=赵',
          estimatedHours: 12,
          actualHours: 14,
          assignTime: '2025-06-18 10:00:00',
          actualStartTime: '2025-06-18 10:30:00',
          completeTime: '2025-06-20 15:30:00',
          planEndTime: '2025-06-21 17:00:00',
          createTime: '2025-06-18 09:45:00',
          qualityStatus: 'PASS',
          qualityCheckTime: '2025-06-20 16:00:00',
          remark: '笔筒制作完成，新手表现良好'
        }
      ];
    }
  }
}
</script>

<style scoped>
.production-board {
  min-height: calc(100vh - 120px);
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left .board-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.header-left .board-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

.board-subtitle {
  color: #8c8c8c;
  font-size: 14px;
  margin-top: 4px;
}

.board-filters {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.board-container {
  min-height: 600px;
}

.board-column {
  background: #f7f8fa;
  border-radius: 8px;
  padding: 16px;
  min-height: 600px;
}

.board-column.pending {
  border-top: 3px solid #fa8c16;
}

.board-column.in-progress {
  border-top: 3px solid #1890ff;
}

.board-column.completed {
  border-top: 3px solid #52c41a;
}

.column-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e8e8e8;
}

.column-title {
  display: flex;
  align-items: center;
  font-weight: 600;
  font-size: 16px;
  color: #262626;
}

.column-title .anticon {
  margin-right: 8px;
}

.column-title .ant-badge {
  margin-left: 8px;
}

.task-list {
  min-height: 500px;
  padding: 8px;
  background: #fafafa;
  border-radius: 6px;
  border: 2px dashed transparent;
  transition: all 0.3s ease;
  position: relative;
}

.task-list.drag-over {
  border-color: #1890ff;
  background: #e6f7ff;
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.task-list.drag-over::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 40%, rgba(24, 144, 255, 0.1) 50%, transparent 60%);
  border-radius: 6px;
  pointer-events: none;
  animation: dragOverShimmer 2s infinite;
}

@keyframes dragOverShimmer {
  0% { background-position: -100% 0; }
  100% { background-position: 100% 0; }
}

.drop-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #1890ff;
  color: white;
  padding: 12px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
  z-index: 10;
  animation: dropHintPulse 1.5s infinite;
}

@keyframes dropHintPulse {
  0%, 100% { transform: translate(-50%, -50%) scale(1); }
  50% { transform: translate(-50%, -50%) scale(1.05); }
}

.drop-hint .anticon {
  margin-right: 6px;
  animation: dropHintIcon 1s infinite;
}

@keyframes dropHintIcon {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
}

.empty-column {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #bfbfbf;
}

/* 拖拽状态变更提示 */
.status-change-notification {
  position: fixed;
  top: 20px;
  right: 20px;
  background: #52c41a;
  color: white;
  padding: 12px 20px;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  animation: slideInRight 0.3s ease;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}
</style>
