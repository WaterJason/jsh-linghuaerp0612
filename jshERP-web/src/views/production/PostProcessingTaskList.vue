<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :style="cardStyle" :bordered="false" class="post-processing-tasks">
        <!-- 页面标题和操作区域 -->
        <div class="page-header">
          <div class="header-left">
            <h2 class="page-title">
              <a-icon type="tool" />
              后工任务列表
            </h2>
            <div class="page-subtitle">任务领取、自动计费、质检确认</div>
          </div>
          <div class="header-right">
            <a-button-group>
              <a-button @click="handleRefresh" :loading="loading">
                <a-icon type="reload" />刷新
              </a-button>
              <a-button @click="handleBatchClaim" type="primary">
                <a-icon type="plus" />批量领取
              </a-button>
              <a-button @click="handleMyTasks">
                <a-icon type="user" />我的任务
              </a-button>
            </a-button-group>
          </div>
        </div>

        <!-- 统计面板 -->
        <div class="statistics-panel">
          <a-row :gutter="16">
            <a-col :md="6" :sm="12" :xs="24">
              <div class="stat-card available">
                <div class="stat-icon">
                  <a-icon type="inbox" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">可领取任务</div>
                  <div class="stat-value">{{ statistics.availableTasks || 0 }}</div>
                </div>
              </div>
            </a-col>
            <a-col :md="6" :sm="12" :xs="24">
              <div class="stat-card processing">
                <div class="stat-icon">
                  <a-icon type="loading" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">进行中任务</div>
                  <div class="stat-value">{{ statistics.processingTasks || 0 }}</div>
                </div>
              </div>
            </a-col>
            <a-col :md="6" :sm="12" :xs="24">
              <div class="stat-card completed">
                <div class="stat-icon">
                  <a-icon type="check-circle" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">已完成任务</div>
                  <div class="stat-value">{{ statistics.completedTasks || 0 }}</div>
                </div>
              </div>
            </a-col>
            <a-col :md="6" :sm="12" :xs="24">
              <div class="stat-card earnings">
                <div class="stat-icon">
                  <a-icon type="dollar" />
                </div>
                <div class="stat-content">
                  <div class="stat-title">今日收益</div>
                  <div class="stat-value">¥{{ statistics.todayEarnings || 0 }}</div>
                </div>
              </div>
            </a-col>
          </a-row>
        </div>
        
        <!-- 筛选和搜索区域 -->
        <div class="filter-section">
          <a-row :gutter="16">
            <a-col :md="6">
              <a-select 
                v-model="filters.status" 
                placeholder="任务状态" 
                allowClear
                @change="loadTaskList">
                <a-select-option value="AVAILABLE">可领取</a-select-option>
                <a-select-option value="CLAIMED">已领取</a-select-option>
                <a-select-option value="IN_PROGRESS">进行中</a-select-option>
                <a-select-option value="COMPLETED">已完成</a-select-option>
                <a-select-option value="QUALITY_CHECKED">已质检</a-select-option>
              </a-select>
            </a-col>
            <a-col :md="6">
              <a-select 
                v-model="filters.taskType" 
                placeholder="任务类型" 
                allowClear
                @change="loadTaskList">
                <a-select-option value="POLISHING">抛光</a-select-option>
                <a-select-option value="PAINTING">上色</a-select-option>
                <a-select-option value="ASSEMBLY">组装</a-select-option>
                <a-select-option value="PACKAGING">包装</a-select-option>
                <a-select-option value="QUALITY_CHECK">质检</a-select-option>
              </a-select>
            </a-col>
            <a-col :md="6">
              <a-select 
                v-model="filters.priority" 
                placeholder="优先级" 
                allowClear
                @change="loadTaskList">
                <a-select-option value="URGENT">紧急</a-select-option>
                <a-select-option value="HIGH">高</a-select-option>
                <a-select-option value="NORMAL">普通</a-select-option>
                <a-select-option value="LOW">低</a-select-option>
              </a-select>
            </a-col>
            <a-col :md="6">
              <a-input-search
                v-model="filters.keyword"
                placeholder="搜索任务编号或产品名称"
                @search="loadTaskList"
                @change="onSearchChange" />
            </a-col>
          </a-row>
        </div>
        
        <!-- 任务列表 -->
        <div class="task-list-section">
          <a-table
            :columns="columns"
            :dataSource="taskList"
            :pagination="pagination"
            :loading="loading"
            :rowSelection="rowSelection"
            :scroll="{ x: 1200 }"
            @change="handleTableChange"
            rowKey="id">
            
            <!-- 任务编号列 -->
            <template slot="taskNumber" slot-scope="text, record">
              <div class="task-number">
                <a @click="handleViewTask(record)">{{ text }}</a>
                <a-tag v-if="record.isUrgent" color="red" size="small">紧急</a-tag>
              </div>
            </template>
            
            <!-- 产品信息列 -->
            <template slot="productInfo" slot-scope="text, record">
              <div class="product-info">
                <div class="product-name">{{ record.productName }}</div>
                <div class="product-spec">{{ record.productSpec }}</div>
              </div>
            </template>
            
            <!-- 任务类型列 -->
            <template slot="taskType" slot-scope="text">
              <a-tag :color="getTaskTypeColor(text)">
                {{ getTaskTypeText(text) }}
              </a-tag>
            </template>
            
            <!-- 数量列 -->
            <template slot="quantity" slot-scope="text, record">
              <div class="quantity-info">
                <span class="total">{{ record.totalQuantity }}</span>
                <span class="unit">{{ record.unitName }}</span>
                <div v-if="record.completedQuantity > 0" class="completed">
                  已完成: {{ record.completedQuantity }}
                </div>
              </div>
            </template>
            
            <!-- 优先级列 -->
            <template slot="priority" slot-scope="text">
              <a-tag :color="getPriorityColor(text)">
                {{ getPriorityText(text) }}
              </a-tag>
            </template>
            
            <!-- 状态列 -->
            <template slot="status" slot-scope="text">
              <a-badge :status="getStatusBadge(text)" :text="getStatusText(text)" />
            </template>
            
            <!-- 工费列 -->
            <template slot="fee" slot-scope="text, record">
              <div class="fee-info">
                <div class="unit-fee">单价: ¥{{ record.unitFee }}</div>
                <div class="total-fee">总计: ¥{{ (record.unitFee * record.totalQuantity).toFixed(2) }}</div>
              </div>
            </template>
            
            <!-- 领取人列 -->
            <template slot="claimedBy" slot-scope="text, record">
              <div v-if="record.claimedBy" class="worker-info">
                <a-avatar size="small" :src="record.workerAvatar">
                  {{ record.claimedBy.charAt(0) }}
                </a-avatar>
                <span class="worker-name">{{ record.claimedBy }}</span>
              </div>
              <span v-else class="unclaimed">未领取</span>
            </template>
            
            <!-- 时间列 -->
            <template slot="timeInfo" slot-scope="text, record">
              <div class="time-info">
                <div v-if="record.claimTime">
                  <span class="time-label">领取:</span>
                  <span class="time-value">{{ formatTime(record.claimTime) }}</span>
                </div>
                <div v-if="record.completeTime">
                  <span class="time-label">完成:</span>
                  <span class="time-value">{{ formatTime(record.completeTime) }}</span>
                </div>
                <div v-if="record.deadline">
                  <span class="time-label">截止:</span>
                  <span class="time-value" :class="{ 'overdue': isOverdue(record.deadline) }">
                    {{ formatTime(record.deadline) }}
                  </span>
                </div>
              </div>
            </template>
            
            <!-- 操作列 -->
            <template slot="action" slot-scope="text, record">
              <div class="action-buttons">
                <!-- 可领取状态 -->
                <template v-if="record.status === 'AVAILABLE'">
                  <a-button size="small" type="primary" @click="handleClaimTask(record)">
                    <a-icon type="plus" />领取
                  </a-button>
                </template>
                
                <!-- 已领取状态 -->
                <template v-if="record.status === 'CLAIMED'">
                  <a-button size="small" type="primary" @click="handleStartTask(record)">
                    <a-icon type="play-circle" />开始
                  </a-button>
                  <a-button size="small" @click="handleReleaseTask(record)">
                    <a-icon type="rollback" />释放
                  </a-button>
                </template>
                
                <!-- 进行中状态 -->
                <template v-if="record.status === 'IN_PROGRESS'">
                  <a-button size="small" type="primary" @click="handleReportProgress(record)">
                    <a-icon type="file-text" />报工
                  </a-button>
                  <a-button size="small" @click="handleCompleteTask(record)">
                    <a-icon type="check" />完成
                  </a-button>
                </template>
                
                <!-- 已完成状态 -->
                <template v-if="record.status === 'COMPLETED'">
                  <a-button size="small" type="primary" @click="handleQualityCheck(record)">
                    <a-icon type="safety-certificate" />质检
                  </a-button>
                </template>
                
                <!-- 通用操作 -->
                <a-button size="small" @click="handleViewTask(record)">
                  <a-icon type="eye" />详情
                </a-button>
              </div>
            </template>
          </a-table>
        </div>
        
        <!-- 模态框 -->
        <task-detail-modal ref="taskDetailModal" />
        <progress-report-modal ref="progressReportModal" @ok="loadTaskList" />
        <quality-check-modal ref="qualityCheckModal" @ok="loadTaskList" />
      </a-card>
    </a-col>
  </a-row>
</template>

<script>
import TaskDetailModal from './modules/TaskDetailModal'
import ProgressReportModal from './modules/ProgressReportModal'
import QualityCheckModal from './modules/QualityCheckModal'
import { getAction, postAction } from '@/api/manage'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import dayjs from 'dayjs'

export default {
  name: "PostProcessingTaskList",
  mixins: [JeecgListMixin],
  components: {
    TaskDetailModal,
    ProgressReportModal,
    QualityCheckModal
  },
  data() {
    return {
      // 基础数据
      taskList: [],
      statistics: {},
      
      // 筛选条件
      filters: {
        status: undefined,
        taskType: undefined,
        priority: undefined,
        keyword: ''
      },
      
      // 表格配置
      columns: [
        {
          title: '任务编号',
          dataIndex: 'taskNumber',
          width: 150,
          scopedSlots: { customRender: 'taskNumber' }
        },
        {
          title: '产品信息',
          dataIndex: 'productInfo',
          width: 200,
          scopedSlots: { customRender: 'productInfo' }
        },
        {
          title: '任务类型',
          dataIndex: 'taskType',
          width: 100,
          scopedSlots: { customRender: 'taskType' }
        },
        {
          title: '数量',
          dataIndex: 'quantity',
          width: 120,
          scopedSlots: { customRender: 'quantity' }
        },
        {
          title: '优先级',
          dataIndex: 'priority',
          width: 80,
          scopedSlots: { customRender: 'priority' }
        },
        {
          title: '状态',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '工费',
          dataIndex: 'fee',
          width: 120,
          scopedSlots: { customRender: 'fee' }
        },
        {
          title: '领取人',
          dataIndex: 'claimedBy',
          width: 120,
          scopedSlots: { customRender: 'claimedBy' }
        },
        {
          title: '时间信息',
          dataIndex: 'timeInfo',
          width: 180,
          scopedSlots: { customRender: 'timeInfo' }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: 200,
          fixed: 'right',
          scopedSlots: { customRender: 'action' }
        }
      ],
      
      // 行选择
      selectedRowKeys: [],
      
      // 状态控制
      loading: false,
      
      // API URLs
      url: {
        list: "/postProcessing/list",
        statistics: "/postProcessing/statistics",
        assign: "/postProcessing/assign",
        start: "/postProcessing/start",
        complete: "/postProcessing/complete",
        delete: "/postProcessing/delete",
        deleteBatch: "/postProcessing/deleteBatch",
        exportXlsUrl: "/postProcessing/exportExcel"
      }
    }
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.onSelectChange,
        getCheckboxProps: record => ({
          disabled: record.status !== 'AVAILABLE'
        })
      };
    }
  },
  mounted() {
    this.loadTaskList();
    this.loadStatistics();
  },
  methods: {
    // 加载任务列表
    async loadTaskList() {
      this.loading = true;
      try {
        // 使用模拟数据，后续替换为真实API调用
        this.loadMockData();
        
        // TODO: 替换为真实API调用
        // const params = {
        //   ...this.filters,
        //   pageNo: this.ipagination.current,
        //   pageSize: this.ipagination.pageSize
        // };
        // const res = await getAction(this.url.list, params);
        // if (res.code === 200) {
        //   this.taskList = res.data.records || [];
        //   this.ipagination.total = res.data.total || 0;
        // }
      } catch (error) {
        this.$message.error('加载任务列表失败');
        console.error('Load task list error:', error);
      } finally {
        this.loading = false;
      }
    },
    
    // 加载统计数据
    async loadStatistics() {
      try {
        // 使用模拟数据
        this.statistics = {
          availableTasks: 15,
          processingTasks: 8,
          completedTasks: 23,
          todayEarnings: 1250.50
        };

        // TODO: 替换为真实API调用
        // const res = await getAction(this.url.statistics);
        // if (res.code === 200) {
        //   this.statistics = res.data || {};
        // }
      } catch (error) {
        console.error('Load statistics error:', error);
      }
    },

    // 加载模拟数据
    loadMockData() {
      this.taskList = [
        {
          id: 1,
          taskNumber: 'PT1750570001',
          productName: '景泰蓝花瓶',
          productSpec: '大号-蓝色',
          taskType: 'POLISHING',
          totalQuantity: 5,
          completedQuantity: 0,
          unitName: '个',
          priority: 'URGENT',
          status: 'AVAILABLE',
          unitFee: 25.00,
          isUrgent: true,
          deadline: '2025-06-23 18:00:00',
          createTime: '2025-06-22 09:00:00'
        },
        {
          id: 2,
          taskNumber: 'PT1750570002',
          productName: '景泰蓝盘子',
          productSpec: '中号-红色',
          taskType: 'PAINTING',
          totalQuantity: 10,
          completedQuantity: 3,
          unitName: '个',
          priority: 'HIGH',
          status: 'IN_PROGRESS',
          unitFee: 18.00,
          claimedBy: '李师傅',
          workerAvatar: 'https://via.placeholder.com/40x40/1890ff/FFFFFF?text=李',
          claimTime: '2025-06-22 10:00:00',
          deadline: '2025-06-24 18:00:00',
          createTime: '2025-06-22 08:30:00'
        },
        {
          id: 3,
          taskNumber: 'PT1750570003',
          productName: '景泰蓝茶具',
          productSpec: '套装-绿色',
          taskType: 'ASSEMBLY',
          totalQuantity: 3,
          completedQuantity: 3,
          unitName: '套',
          priority: 'NORMAL',
          status: 'COMPLETED',
          unitFee: 45.00,
          claimedBy: '王师傅',
          workerAvatar: 'https://via.placeholder.com/40x40/52c41a/FFFFFF?text=王',
          claimTime: '2025-06-21 14:00:00',
          completeTime: '2025-06-22 11:30:00',
          deadline: '2025-06-25 18:00:00',
          createTime: '2025-06-21 13:30:00'
        },
        {
          id: 4,
          taskNumber: 'PT1750570004',
          productName: '景泰蓝摆件',
          productSpec: '龙凤-金色',
          taskType: 'QUALITY_CHECK',
          totalQuantity: 2,
          completedQuantity: 2,
          unitName: '个',
          priority: 'HIGH',
          status: 'QUALITY_CHECKED',
          unitFee: 30.00,
          claimedBy: '张师傅',
          workerAvatar: 'https://via.placeholder.com/40x40/fa8c16/FFFFFF?text=张',
          claimTime: '2025-06-21 16:00:00',
          completeTime: '2025-06-22 09:30:00',
          deadline: '2025-06-23 18:00:00',
          createTime: '2025-06-21 15:30:00'
        },
        {
          id: 5,
          taskNumber: 'PT1750570005',
          productName: '景泰蓝首饰盒',
          productSpec: '小号-紫色',
          taskType: 'PACKAGING',
          totalQuantity: 8,
          completedQuantity: 0,
          unitName: '个',
          priority: 'NORMAL',
          status: 'CLAIMED',
          unitFee: 12.00,
          claimedBy: '赵师傅',
          workerAvatar: 'https://via.placeholder.com/40x40/722ed1/FFFFFF?text=赵',
          claimTime: '2025-06-22 11:00:00',
          deadline: '2025-06-26 18:00:00',
          createTime: '2025-06-22 10:30:00'
        }
      ];

      // 设置分页信息
      this.ipagination.total = this.taskList.length;
    },

    // 表格变化处理
    handleTableChange(pagination, filters, sorter) {
      this.ipagination = pagination;
      this.loadTaskList();
    },

    // 行选择变化
    onSelectChange(selectedRowKeys) {
      this.selectedRowKeys = selectedRowKeys;
    },

    // 搜索输入变化
    onSearchChange(e) {
      clearTimeout(this.searchTimeout);
      this.searchTimeout = setTimeout(() => {
        this.loadTaskList();
      }, 300);
    },

    // 刷新
    handleRefresh() {
      this.loadTaskList();
      this.loadStatistics();
    },

    // 批量领取
    handleBatchClaim() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请先选择要领取的任务');
        return;
      }

      this.$confirm({
        title: '确认批量领取',
        content: `确定要领取选中的 ${this.selectedRowKeys.length} 个任务吗？`,
        onOk: async () => {
          try {
            // TODO: 调用批量领取API
            this.$message.success('批量领取成功');
            this.selectedRowKeys = [];
            this.loadTaskList();
          } catch (error) {
            this.$message.error('批量领取失败');
          }
        }
      });
    },

    // 我的任务
    handleMyTasks() {
      this.filters.claimedBy = 'current_user'; // 当前用户
      this.loadTaskList();
    },

    // 领取任务
    async handleClaimTask(record) {
      try {
        // TODO: 调用领取任务API
        // const res = await postAction(this.url.claim, { taskId: record.id });
        // if (res.code === 200) {
        this.$message.success('任务领取成功');
        this.loadTaskList();
        // }
      } catch (error) {
        this.$message.error('任务领取失败');
      }
    },

    // 开始任务
    async handleStartTask(record) {
      try {
        // TODO: 调用开始任务API
        this.$message.success('任务已开始');
        this.loadTaskList();
      } catch (error) {
        this.$message.error('开始任务失败');
      }
    },

    // 释放任务
    handleReleaseTask(record) {
      this.$confirm({
        title: '确认释放任务',
        content: `确定要释放任务 ${record.taskNumber} 吗？释放后其他人可以领取此任务。`,
        onOk: async () => {
          try {
            // TODO: 调用释放任务API
            this.$message.success('任务已释放');
            this.loadTaskList();
          } catch (error) {
            this.$message.error('释放任务失败');
          }
        }
      });
    },

    // 报工进度
    handleReportProgress(record) {
      this.$refs.progressReportModal.report(record);
    },

    // 完成任务
    handleCompleteTask(record) {
      this.$confirm({
        title: '确认完成任务',
        content: `确定要完成任务 ${record.taskNumber} 吗？`,
        onOk: async () => {
          try {
            // TODO: 调用完成任务API
            this.$message.success('任务已完成');
            this.loadTaskList();
          } catch (error) {
            this.$message.error('完成任务失败');
          }
        }
      });
    },

    // 质检
    handleQualityCheck(record) {
      this.$refs.qualityCheckModal.check(record);
    },

    // 查看任务详情
    handleViewTask(record) {
      this.$refs.taskDetailModal.view(record);
    },

    // 获取任务类型颜色
    getTaskTypeColor(taskType) {
      const colors = {
        'POLISHING': 'blue',
        'PAINTING': 'green',
        'ASSEMBLY': 'orange',
        'PACKAGING': 'purple',
        'QUALITY_CHECK': 'red'
      };
      return colors[taskType] || 'default';
    },

    // 获取任务类型文本
    getTaskTypeText(taskType) {
      const texts = {
        'POLISHING': '抛光',
        'PAINTING': '上色',
        'ASSEMBLY': '组装',
        'PACKAGING': '包装',
        'QUALITY_CHECK': '质检'
      };
      return texts[taskType] || '未知';
    },

    // 获取优先级颜色
    getPriorityColor(priority) {
      const colors = {
        'LOW': 'default',
        'NORMAL': 'blue',
        'HIGH': 'orange',
        'URGENT': 'red'
      };
      return colors[priority] || 'default';
    },

    // 获取优先级文本
    getPriorityText(priority) {
      const texts = {
        'LOW': '低',
        'NORMAL': '普通',
        'HIGH': '高',
        'URGENT': '紧急'
      };
      return texts[priority] || '普通';
    },

    // 获取状态徽章
    getStatusBadge(status) {
      const badges = {
        'AVAILABLE': 'default',
        'CLAIMED': 'processing',
        'IN_PROGRESS': 'processing',
        'COMPLETED': 'success',
        'QUALITY_CHECKED': 'success'
      };
      return badges[status] || 'default';
    },

    // 获取状态文本
    getStatusText(status) {
      const texts = {
        'AVAILABLE': '可领取',
        'CLAIMED': '已领取',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成',
        'QUALITY_CHECKED': '已质检'
      };
      return texts[status] || '未知';
    },

    // 格式化时间
    formatTime(time) {
      if (!time) return '';
      return dayjs(time).format('MM-DD HH:mm');
    },

    // 判断是否逾期
    isOverdue(deadline) {
      if (!deadline) return false;
      return dayjs().isAfter(dayjs(deadline));
    }
  }
}
</script>

<style scoped>
.post-processing-tasks {
  min-height: calc(100vh - 120px);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-left .page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.header-left .page-title .anticon {
  margin-right: 8px;
  color: #1890ff;
}

.page-subtitle {
  color: #8c8c8c;
  font-size: 14px;
  margin-top: 4px;
}

.statistics-panel {
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.stat-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 20px;
  color: white;
}

.stat-card.available .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.processing .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-card.completed .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-card.earnings .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #262626;
  line-height: 1;
}

.filter-section {
  margin-bottom: 24px;
  padding: 16px;
  background: #fafafa;
  border-radius: 6px;
}

.task-list-section {
  background: white;
  border-radius: 8px;
}

.task-number a {
  color: #1890ff;
  text-decoration: none;
}

.task-number a:hover {
  text-decoration: underline;
}

.product-info .product-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.product-info .product-spec {
  font-size: 12px;
  color: #8c8c8c;
}

.quantity-info .total {
  font-weight: 600;
  color: #262626;
}

.quantity-info .unit {
  margin-left: 4px;
  color: #8c8c8c;
  font-size: 12px;
}

.quantity-info .completed {
  font-size: 12px;
  color: #52c41a;
  margin-top: 4px;
}

.fee-info .unit-fee {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 4px;
}

.fee-info .total-fee {
  font-weight: 600;
  color: #262626;
}

.worker-info {
  display: flex;
  align-items: center;
}

.worker-info .worker-name {
  margin-left: 8px;
  font-size: 12px;
  color: #262626;
}

.unclaimed {
  color: #8c8c8c;
  font-style: italic;
}

.time-info {
  font-size: 11px;
}

.time-info > div {
  margin-bottom: 4px;
}

.time-label {
  color: #8c8c8c;
  margin-right: 4px;
}

.time-value {
  color: #595959;
}

.time-value.overdue {
  color: #ff4d4f;
  font-weight: 600;
}

.action-buttons .ant-btn {
  margin-right: 8px;
  margin-bottom: 4px;
}

.action-buttons .ant-btn:last-child {
  margin-right: 0;
}
</style>
