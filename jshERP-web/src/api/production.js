import { axios } from '@/utils/request'

const api = {
  // 生产订单相关API
  productionOrder: '/production',
  productionOrderList: '/production/list',
  productionOrderAdd: '/production/add',
  productionOrderUpdate: '/production/update',
  productionOrderDelete: '/production/delete',
  productionOrderBatchDelete: '/production/batchDelete',
  productionOrderInfo: '/production/info',
  productionOrderGenerateFromOrder: '/production/generateFromOrder',
  productionOrderUpdateStatus: '/production/updateStatus',
  productionOrderStatistics: '/production/statistics',
  productionOrderGenerateOrderNumber: '/production/generateOrderNumber',

  // 工单相关API
  workOrder: '/workOrder',
  workOrderList: '/workOrder/list',
  workOrderAdd: '/workOrder/add',
  workOrderUpdate: '/workOrder/update',
  workOrderDelete: '/workOrder/delete',
  workOrderInfo: '/workOrder/info',
  workOrderKanbanData: '/workOrder/kanbanData',
  workOrderKanbanList: '/workOrder/kanbanList',
  workOrderAssign: '/workOrder/assign',
  workOrderComplete: '/workOrder/complete',
  workOrderUpdateStatus: '/workOrder/updateStatus',
  workOrderByProductionOrder: '/workOrder/byProductionOrder',

  // 智能生产管理API
  productionWorkOrder: '/productionWorkOrder',
  productionTask: '/productionTask',
  productionReport: '/productionReport',
  qualityInspection: '/qualityInspection',
  workerSkill: '/workerSkill'
}

// 生产订单相关API方法
export default api

/**
 * 获取生产订单列表
 * @param parameter
 * @returns {*}
 */
export function getProductionOrderList(parameter) {
  return axios({
    url: api.productionOrderList,
    method: 'get',
    params: parameter
  })
}

/**
 * 获取生产订单详情
 * @param id
 * @returns {*}
 */
export function getProductionOrderInfo(id) {
  return axios({
    url: api.productionOrderInfo,
    method: 'get',
    params: { id }
  })
}

/**
 * 新增生产订单
 * @param parameter
 * @returns {*}
 */
export function addProductionOrder(parameter) {
  return axios({
    url: api.productionOrderAdd,
    method: 'post',
    data: parameter
  })
}

/**
 * 更新生产订单
 * @param parameter
 * @returns {*}
 */
export function updateProductionOrder(parameter) {
  return axios({
    url: api.productionOrderUpdate,
    method: 'put',
    data: parameter
  })
}

/**
 * 删除生产订单
 * @param id
 * @returns {*}
 */
export function deleteProductionOrder(id) {
  return axios({
    url: api.productionOrderDelete,
    method: 'delete',
    params: { id }
  })
}

/**
 * 批量删除生产订单
 * @param ids
 * @returns {*}
 */
export function batchDeleteProductionOrder(ids) {
  return axios({
    url: api.productionOrderBatchDelete,
    method: 'post',
    params: { ids }
  })
}

/**
 * 智能生成工单
 * @param saleOrderId
 * @returns {*}
 */
export function generateFromOrder(saleOrderId) {
  return axios({
    url: api.productionOrderGenerateFromOrder,
    method: 'post',
    params: { saleOrderId }
  })
}

/**
 * 更新生产订单状态
 * @param id
 * @param status
 * @returns {*}
 */
export function updateProductionOrderStatus(id, status) {
  return axios({
    url: api.productionOrderUpdateStatus,
    method: 'post',
    params: { id, status }
  })
}

/**
 * 获取生产统计信息
 * @returns {*}
 */
export function getProductionStatistics() {
  return axios({
    url: api.productionOrderStatistics,
    method: 'get'
  })
}

/**
 * 生成生产订单号
 * @returns {*}
 */
export function generateOrderNumber() {
  return axios({
    url: api.productionOrderGenerateOrderNumber,
    method: 'get'
  })
}

// 工单相关API方法

/**
 * 获取工单列表
 * @param parameter
 * @returns {*}
 */
export function getWorkOrderList(parameter) {
  return axios({
    url: api.workOrderList,
    method: 'get',
    params: parameter
  })
}

/**
 * 获取工单详情
 * @param id
 * @returns {*}
 */
export function getWorkOrderInfo(id) {
  return axios({
    url: api.workOrderInfo,
    method: 'get',
    params: { id }
  })
}

/**
 * 新增工单
 * @param parameter
 * @returns {*}
 */
export function addWorkOrder(parameter) {
  return axios({
    url: api.workOrderAdd,
    method: 'post',
    data: parameter
  })
}

/**
 * 更新工单
 * @param parameter
 * @returns {*}
 */
export function updateWorkOrder(parameter) {
  return axios({
    url: api.workOrderUpdate,
    method: 'put',
    data: parameter
  })
}

/**
 * 删除工单
 * @param id
 * @returns {*}
 */
export function deleteWorkOrder(id) {
  return axios({
    url: api.workOrderDelete,
    method: 'delete',
    params: { id }
  })
}

/**
 * 获取看板数据
 * @returns {*}
 */
export function getKanbanData() {
  return axios({
    url: api.workOrderKanbanData,
    method: 'get'
  })
}

/**
 * 获取看板工单列表
 * @param parameter
 * @returns {*}
 */
export function getKanbanList(parameter) {
  return axios({
    url: api.workOrderKanbanList,
    method: 'get',
    params: parameter
  })
}

/**
 * 派单
 * @param parameter
 * @returns {*}
 */
export function assignWorkOrder(parameter) {
  return axios({
    url: api.workOrderAssign,
    method: 'post',
    data: parameter
  })
}

/**
 * 完工
 * @param parameter
 * @returns {*}
 */
export function completeWorkOrder(parameter) {
  return axios({
    url: api.workOrderComplete,
    method: 'post',
    data: parameter
  })
}

/**
 * 更新工单状态
 * @param parameter
 * @returns {*}
 */
export function updateWorkOrderStatus(parameter) {
  return axios({
    url: api.workOrderUpdateStatus,
    method: 'post',
    data: parameter
  })
}

/**
 * 根据生产订单ID获取工单列表
 * @param productionOrderId
 * @returns {*}
 */
export function getWorkOrdersByProductionOrderId(productionOrderId) {
  return axios({
    url: api.workOrderByProductionOrder,
    method: 'get',
    params: { productionOrderId }
  })
}

// =====================================================
// 智能生产管理API
// =====================================================

// 生产任务管理API

/**
 * 获取生产任务列表
 */
export function getProductionTaskList(parameter) {
  return axios({
    url: api.productionTask + '/list',
    method: 'get',
    params: parameter
  })
}

/**
 * 分配任务给工人
 */
export function assignTaskToWorker(parameter) {
  return axios({
    url: api.productionTask + '/assign',
    method: 'put',
    params: parameter
  })
}

/**
 * 开始任务
 */
export function startTask(taskId) {
  return axios({
    url: api.productionTask + '/start',
    method: 'put',
    params: { taskId }
  })
}

/**
 * 完成任务
 */
export function completeTask(parameter) {
  return axios({
    url: api.productionTask + '/complete',
    method: 'put',
    params: parameter
  })
}

/**
 * 获取可领取的任务
 */
export function getAvailableTasks(taskType, priority) {
  return axios({
    url: api.productionTask + '/available',
    method: 'get',
    params: { taskType, priority }
  })
}

/**
 * 获取工人的任务
 */
export function getWorkerTasks(workerId, status) {
  return axios({
    url: api.productionTask + '/worker',
    method: 'get',
    params: { workerId, status }
  })
}

// 工人技能管理API

/**
 * 获取工人的技能列表
 */
export function getWorkerSkills(workerId, isActive) {
  return axios({
    url: api.workerSkill + '/workerSkills',
    method: 'get',
    params: { workerId, isActive }
  })
}

/**
 * 获取技能类型的工人列表
 */
export function getSkillWorkers(skillType, minLevel, isActive) {
  return axios({
    url: api.workerSkill + '/skillWorkers',
    method: 'get',
    params: { skillType, minLevel, isActive }
  })
}

/**
 * 获取具备指定技能的可用工人
 */
export function getAvailableWorkersBySkill(skillTypes) {
  // 模拟数据，实际应该调用后端API
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockWorkers = [
        {
          id: 1,
          workerId: 1,
          workerName: '张师傅',
          position: '掐丝点蓝师傅',
          skills: [
            { skillType: 'QISI_DIANLIAN', skillLevel: 'EXPERT', skillScore: 4.8 },
            { skillType: 'PEISHI_ZHIZUO', skillLevel: 'ADVANCED', skillScore: 4.2 }
          ],
          currentTasks: 3,
          maxTasks: 8,
          efficiencyRate: 95
        },
        {
          id: 2,
          workerId: 2,
          workerName: '李师傅',
          position: '配饰制作师傅',
          skills: [
            { skillType: 'PEISHI_ZHIZUO', skillLevel: 'MASTER', skillScore: 4.9 },
            { skillType: 'HOUGONG_CHULI', skillLevel: 'ADVANCED', skillScore: 4.3 }
          ],
          currentTasks: 2,
          maxTasks: 6,
          efficiencyRate: 88
        },
        {
          id: 3,
          workerId: 3,
          workerName: '王师傅',
          position: '后工处理师傅',
          skills: [
            { skillType: 'HOUGONG_CHULI', skillLevel: 'EXPERT', skillScore: 4.6 },
            { skillType: 'ZHILIANG_JIANYAN', skillLevel: 'INTERMEDIATE', skillScore: 3.8 }
          ],
          currentTasks: 4,
          maxTasks: 10,
          efficiencyRate: 92
        },
        {
          id: 4,
          workerId: 4,
          workerName: '赵师傅',
          position: '质量检验师',
          skills: [
            { skillType: 'ZHILIANG_JIANYAN', skillLevel: 'EXPERT', skillScore: 4.7 }
          ],
          currentTasks: 1,
          maxTasks: 5,
          efficiencyRate: 96
        }
      ];

      resolve({
        code: 200,
        data: mockWorkers,
        message: '获取成功'
      });
    }, 500);
  });
}

// 生产报工管理API

/**
 * 获取生产报工信息
 */
export function getProductionReport(id) {
  return axios({
    url: api.productionReport + '/info',
    method: 'get',
    params: { id }
  })
}

/**
 * 新增生产报工
 */
export function addProductionReport(parameter) {
  return axios({
    url: api.productionReport + '/add',
    method: 'post',
    data: parameter
  })
}

/**
 * 进度报工
 */
export function progressReport(parameter) {
  // 模拟API调用
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('进度报工数据:', parameter);
      resolve({
        code: 200,
        data: {
          reportId: Date.now(),
          reportNumber: 'RPT' + Date.now(),
          ...parameter
        },
        message: '进度报工成功'
      });
    }, 1000);
  });
}

/**
 * 完工报工
 */
export function completeReport(parameter) {
  // 模拟API调用
  return new Promise((resolve) => {
    setTimeout(() => {
      console.log('完工报工数据:', parameter);
      resolve({
        code: 200,
        data: {
          reportId: Date.now(),
          reportNumber: 'RPT' + Date.now(),
          ...parameter
        },
        message: '完工报工成功'
      });
    }, 1000);
  });
}

/**
 * 获取任务报工历史
 */
export function getTaskReportHistory(taskId) {
  return axios({
    url: api.productionReport + '/taskHistory',
    method: 'get',
    params: { taskId }
  })
}

/**
 * 获取工人报工统计
 */
export function getWorkerReportStatistics(workerId, startDate, endDate) {
  return axios({
    url: api.productionReport + '/workerStatistics',
    method: 'get',
    params: { workerId, startDate, endDate }
  })
}

// 生产统计管理API

/**
 * 获取生产任务统计数据
 */
export function getProductionTaskStatistics(dateRange) {
  // 模拟API调用
  return new Promise((resolve) => {
    setTimeout(() => {
      const mockData = {
        // 基础统计
        totalTasks: 156,
        completedCount: 89,
        inProgressCount: 45,
        pendingCount: 15,
        assignedCount: 7,

        // 优先级分布
        urgentCount: 8,
        highCount: 23,
        normalCount: 98,
        lowCount: 27,

        // 质量统计
        qualifiedCount: 82,
        defectiveCount: 7,
        avgQualityScore: 4.2,

        // 效率统计
        avgEfficiency: 87,
        maxEfficiency: 96,
        activeWorkers: 12,

        // 今日概况
        todayNewTasks: 8,
        todayStartedTasks: 12,
        todayCompletedTasks: 15,

        // 趋势数据
        taskTrend: 12.5,
        efficiencyTrend: -2.3,
        costTrend: 8.7,

        // 时间统计
        weekCompletedTasks: 67,
        lastWeekCompletedTasks: 59,

        // 成本统计
        monthTotalCost: 45680.50,
        avgUnitCost: 125.30,
        laborCost: 32450.00,
        materialCost: 13230.50
      };

      resolve({
        code: 200,
        data: mockData,
        message: '获取成功'
      });
    }, 800);
  });
}

/**
 * 获取生产趋势数据
 */
export function getProductionTrendData(days = 30) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const trendData = [];
      for (let i = days - 1; i >= 0; i--) {
        const date = new Date();
        date.setDate(date.getDate() - i);
        trendData.push({
          date: date.toISOString().split('T')[0],
          completed: Math.floor(Math.random() * 20) + 5,
          started: Math.floor(Math.random() * 15) + 3,
          quality: Math.random() * 20 + 80
        });
      }

      resolve({
        code: 200,
        data: trendData,
        message: '获取成功'
      });
    }, 500);
  });
}

/**
 * 获取工人效率数据
 */
export function getWorkerEfficiencyData() {
  return new Promise((resolve) => {
    setTimeout(() => {
      const workerData = [
        { name: '张师傅', efficiency: 96, tasks: 23, quality: 4.8 },
        { name: '李师傅', efficiency: 92, tasks: 19, quality: 4.6 },
        { name: '王师傅', efficiency: 89, tasks: 21, quality: 4.4 },
        { name: '赵师傅', efficiency: 87, tasks: 18, quality: 4.7 },
        { name: '刘师傅', efficiency: 85, tasks: 16, quality: 4.3 },
        { name: '陈师傅', efficiency: 83, tasks: 15, quality: 4.2 },
        { name: '杨师傅', efficiency: 81, tasks: 14, quality: 4.1 },
        { name: '周师傅', efficiency: 78, tasks: 12, quality: 4.0 }
      ];

      resolve({
        code: 200,
        data: workerData,
        message: '获取成功'
      });
    }, 600);
  });
}
