import { axios } from '@/utils/request'

const api = {
  // 生产看板API
  getProductionStatistics: '/chongzuo/production/statistics',
  getTaskBoardData: '/chongzuo/production/tasks',
  updateTaskStatus: '/chongzuo/production/task/status',
  assignTask: '/chongzuo/production/task/assign',
  startTask: '/chongzuo/production/task/start',
  completeTask: '/chongzuo/production/task/complete',
  
  // 工人管理API
  getWorkerList: '/chongzuo/production/workers',
  
  // 实时数据API
  getRealtimeData: '/chongzuo/production/realtime',
  
  // 报告API
  getProductionReport: '/chongzuo/production/report',
  getQualityStatistics: '/chongzuo/production/quality',
  
  // 设备管理API
  getEquipmentStatus: '/chongzuo/production/equipment'
}

// ==================== 生产看板API ====================

/**
 * 获取生产看板统计数据
 * @returns {Promise} 统计数据
 */
export function getProductionStatistics() {
  return axios({
    url: api.getProductionStatistics,
    method: 'get'
  })
}

/**
 * 获取任务看板数据
 * @param {Object} params 查询参数
 * @param {string} params.status 任务状态
 * @param {string} params.priority 优先级
 * @param {string} params.workerId 工人ID
 * @returns {Promise} 任务看板数据
 */
export function getTaskBoardData(params = {}) {
  return axios({
    url: api.getTaskBoardData,
    method: 'get',
    params
  })
}

/**
 * 更新任务状态
 * @param {Object} params 更新参数
 * @param {number} params.taskId 任务ID
 * @param {string} params.newStatus 新状态
 * @param {string} params.oldStatus 旧状态
 * @param {string} params.remark 备注
 * @returns {Promise} 更新结果
 */
export function updateTaskStatus(params) {
  return axios({
    url: api.updateTaskStatus,
    method: 'post',
    data: params
  })
}

/**
 * 任务派工
 * @param {Object} params 派工参数
 * @param {number} params.taskId 任务ID
 * @param {number} params.workerId 工人ID
 * @param {string} params.remark 备注
 * @returns {Promise} 派工结果
 */
export function assignTask(params) {
  return axios({
    url: api.assignTask,
    method: 'post',
    data: params
  })
}

/**
 * 开始任务
 * @param {Object} params 开始参数
 * @param {number} params.taskId 任务ID
 * @returns {Promise} 开始结果
 */
export function startTask(params) {
  return axios({
    url: api.startTask,
    method: 'post',
    data: params
  })
}

/**
 * 完成任务
 * @param {Object} params 完成参数
 * @param {number} params.taskId 任务ID
 * @param {number} params.completedQuantity 完成数量
 * @param {string} params.qualityNote 质量备注
 * @returns {Promise} 完成结果
 */
export function completeTask(params) {
  return axios({
    url: api.completeTask,
    method: 'post',
    data: params
  })
}

// ==================== 工人管理API ====================

/**
 * 获取工人列表
 * @param {Object} params 查询参数
 * @param {string} params.specialty 专业技能
 * @param {string} params.status 工人状态
 * @returns {Promise} 工人列表
 */
export function getWorkerList(params = {}) {
  return axios({
    url: api.getWorkerList,
    method: 'get',
    params
  })
}

// ==================== 实时数据API ====================

/**
 * 获取实时生产数据
 * @returns {Promise} 实时数据
 */
export function getRealtimeData() {
  return axios({
    url: api.getRealtimeData,
    method: 'get'
  })
}

// ==================== 报告API ====================

/**
 * 获取生产报告
 * @param {Object} params 报告参数
 * @param {string} params.startDate 开始日期
 * @param {string} params.endDate 结束日期
 * @param {string} params.type 报告类型
 * @returns {Promise} 生产报告
 */
export function getProductionReport(params = {}) {
  return axios({
    url: api.getProductionReport,
    method: 'get',
    params
  })
}

/**
 * 获取质量统计
 * @param {string} period 统计周期
 * @returns {Promise} 质量统计
 */
export function getQualityStatistics(period = 'week') {
  return axios({
    url: api.getQualityStatistics,
    method: 'get',
    params: { period }
  })
}

// ==================== 设备管理API ====================

/**
 * 获取设备状态
 * @returns {Promise} 设备状态列表
 */
export function getEquipmentStatus() {
  return axios({
    url: api.getEquipmentStatus,
    method: 'get'
  })
}

// ==================== 工具函数 ====================

/**
 * 批量更新任务状态
 * @param {Array} tasks 任务列表
 * @param {string} newStatus 新状态
 * @returns {Promise} 批量更新结果
 */
export async function batchUpdateTaskStatus(tasks, newStatus) {
  const promises = tasks.map(task => 
    updateTaskStatus({
      taskId: task.id,
      newStatus,
      oldStatus: task.status,
      remark: `批量更新为${newStatus}`
    })
  )
  
  try {
    const results = await Promise.all(promises)
    return {
      success: true,
      results,
      successCount: results.filter(r => r.code === 200).length,
      totalCount: tasks.length
    }
  } catch (error) {
    console.error('批量更新任务状态失败:', error)
    throw error
  }
}

/**
 * 获取完整的看板数据
 * @returns {Promise} 完整看板数据
 */
export async function getCompleteBoardData() {
  try {
    const [statisticsResult, taskBoardResult, workersResult, realtimeResult] = await Promise.all([
      getProductionStatistics(),
      getTaskBoardData(),
      getWorkerList(),
      getRealtimeData()
    ])
    
    return {
      statistics: statisticsResult.data,
      taskBoard: taskBoardResult.data,
      workers: workersResult.data,
      realtime: realtimeResult.data,
      timestamp: new Date()
    }
  } catch (error) {
    console.error('获取完整看板数据失败:', error)
    throw error
  }
}

/**
 * 执行任务流转
 * @param {Object} task 任务对象
 * @param {string} action 操作类型 (assign|start|complete|pause)
 * @param {Object} params 额外参数
 * @returns {Promise} 流转结果
 */
export async function executeTaskFlow(task, action, params = {}) {
  const taskId = task.id
  const currentStatus = task.status
  
  try {
    let result
    
    switch (action) {
      case 'assign':
        if (currentStatus !== 'PENDING') {
          throw new Error('只有待派单的任务才能派工')
        }
        result = await assignTask({
          taskId,
          workerId: params.workerId,
          remark: params.remark
        })
        break
        
      case 'start':
        if (currentStatus !== 'ASSIGNED') {
          throw new Error('只有已派单的任务才能开始')
        }
        result = await startTask({ taskId })
        break
        
      case 'complete':
        if (currentStatus !== 'IN_PROGRESS') {
          throw new Error('只有进行中的任务才能完成')
        }
        result = await completeTask({
          taskId,
          completedQuantity: params.completedQuantity,
          qualityNote: params.qualityNote
        })
        break
        
      case 'pause':
        if (currentStatus !== 'IN_PROGRESS') {
          throw new Error('只有进行中的任务才能暂停')
        }
        result = await updateTaskStatus({
          taskId,
          newStatus: 'PAUSED',
          oldStatus: currentStatus,
          remark: params.remark || '任务暂停'
        })
        break
        
      default:
        throw new Error(`不支持的操作类型: ${action}`)
    }
    
    return {
      success: true,
      action,
      taskId,
      result: result.data
    }
  } catch (error) {
    console.error(`任务流转失败 [${action}]:`, error)
    throw error
  }
}

/**
 * 获取任务状态流转选项
 * @param {string} currentStatus 当前状态
 * @returns {Array} 可用的流转选项
 */
export function getTaskFlowOptions(currentStatus) {
  const flowOptions = {
    'PENDING': [
      { action: 'assign', label: '派工', icon: 'user-add', type: 'primary' },
      { action: 'edit', label: '编辑', icon: 'edit', type: 'default' },
      { action: 'delete', label: '删除', icon: 'delete', type: 'danger' }
    ],
    'ASSIGNED': [
      { action: 'start', label: '开始', icon: 'play-circle', type: 'primary' },
      { action: 'reassign', label: '重新派工', icon: 'swap', type: 'default' }
    ],
    'IN_PROGRESS': [
      { action: 'report', label: '报工', icon: 'file-text', type: 'primary' },
      { action: 'pause', label: '暂停', icon: 'pause-circle', type: 'default' },
      { action: 'complete', label: '完成', icon: 'check', type: 'dashed' }
    ],
    'COMPLETED': [
      { action: 'quality-check', label: '质检', icon: 'safety-certificate', type: 'primary' },
      { action: 'ship', label: '发货', icon: 'car', type: 'default' }
    ],
    'PAUSED': [
      { action: 'resume', label: '恢复', icon: 'play-circle', type: 'primary' },
      { action: 'cancel', label: '取消', icon: 'close', type: 'danger' }
    ]
  }
  
  return flowOptions[currentStatus] || []
}

export default {
  // 生产看板
  getProductionStatistics,
  getTaskBoardData,
  updateTaskStatus,
  assignTask,
  startTask,
  completeTask,
  
  // 工人管理
  getWorkerList,
  
  // 实时数据
  getRealtimeData,
  
  // 报告
  getProductionReport,
  getQualityStatistics,
  
  // 设备管理
  getEquipmentStatus,
  
  // 工具函数
  batchUpdateTaskStatus,
  getCompleteBoardData,
  executeTaskFlow,
  getTaskFlowOptions
}
