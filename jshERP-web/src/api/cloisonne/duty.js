import { getAction, postAction, putAction, deleteAction } from '@/api/manage'

// API接口基础路径
const API_BASE_URL = '/cloisonne/duty'

/**
 * 获取值班列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getDutyList = (params) => {
  return getAction(`${API_BASE_URL}/list`, params)
}

/**
 * 根据ID获取值班详情
 * @param {Object} params 查询参数 {id}
 * @returns {Promise}
 */
export const getDutyById = (params) => {
  return getAction(`${API_BASE_URL}/queryById`, params)
}

/**
 * 新增值班记录
 * @param {Object} data 值班数据
 * @returns {Promise}
 */
export const addDuty = (data) => {
  return postAction(`${API_BASE_URL}/add`, data)
}

/**
 * 编辑值班记录
 * @param {Object} data 值班数据
 * @returns {Promise}
 */
export const editDuty = (data) => {
  return putAction(`${API_BASE_URL}/update`, data)
}

/**
 * 删除值班记录
 * @param {Object} params 删除参数 {id}
 * @returns {Promise}
 */
export const deleteDuty = (params) => {
  return deleteAction(`${API_BASE_URL}/delete`, params)
}

/**
 * 批量删除值班记录
 * @param {Object} params 删除参数 {ids}
 * @returns {Promise}
 */
export const deleteBatchDuty = (params) => {
  return deleteAction(`${API_BASE_URL}/deleteBatch`, params)
}

/**
 * 批量新增值班记录
 * @param {Object} data 批量数据
 * @returns {Promise}
 */
export const batchAddDuty = (data) => {
  return postAction(`${API_BASE_URL}/batchAdd`, data)
}

/**
 * 批量更新值班状态
 * @param {Object} data 批量更新数据
 * @returns {Promise}
 */
export const batchUpdateDutyStatus = (data) => {
  return putAction(`${API_BASE_URL}/batchUpdateStatus`, data)
}

/**
 * 检查值班冲突
 * @param {Object} params 检查参数
 * @returns {Promise}
 */
export const checkDutyConflict = (params) => {
  return postAction(`${API_BASE_URL}/checkConflict`, params)
}

/**
 * 获取值班统计数据
 * @param {Object} params 统计参数
 * @returns {Promise}
 */
export const getDutyStatistics = (params) => {
  return getAction(`${API_BASE_URL}/statistics`, params)
}

/**
 * 获取员工列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getEmployeeList = (params) => {
  return getAction(`${API_BASE_URL}/employees`, params)
}

/**
 * 获取员工值班历史
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getEmployeeDutyHistory = (params) => {
  return getAction(`${API_BASE_URL}/employeeHistory`, params)
}

/**
 * 获取班次类型列表
 * @returns {Promise}
 */
export const getShiftTypes = () => {
  return getAction(`${API_BASE_URL}/shiftTypes`)
}

/**
 * 获取值班日历数据
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getDutyCalendar = (params) => {
  return getAction(`${API_BASE_URL}/calendar`, params)
}

/**
 * 导出值班数据
 * @param {Object} params 导出参数
 * @returns {Promise}
 */
export const exportDutyData = (params) => {
  return getAction(`${API_BASE_URL}/export`, params)
}

/**
 * 导入值班数据
 * @param {Object} data 导入数据
 * @returns {Promise}
 */
export const importDutyData = (data) => {
  return postAction(`${API_BASE_URL}/import`, data)
}

/**
 * 获取值班模板
 * @param {Object} params 模板参数
 * @returns {Promise}
 */
export const getDutyTemplate = (params) => {
  return getAction(`${API_BASE_URL}/template`, params)
}

/**
 * 保存值班模板
 * @param {Object} data 模板数据
 * @returns {Promise}
 */
export const saveDutyTemplate = (data) => {
  return postAction(`${API_BASE_URL}/template`, data)
}

/**
 * 复制值班记录
 * @param {Object} data 复制数据
 * @returns {Promise}
 */
export const copyDuty = (data) => {
  return postAction(`${API_BASE_URL}/copy`, data)
}

/**
 * 申请调班
 * @param {Object} data 调班申请数据
 * @returns {Promise}
 */
export const applyDutySwap = (data) => {
  return postAction(`${API_BASE_URL}/applySwap`, data)
}

/**
 * 审批调班
 * @param {Object} data 调班审批数据
 * @returns {Promise}
 */
export const approveDutySwap = (data) => {
  return putAction(`${API_BASE_URL}/approveSwap`, data)
}

/**
 * 获取调班申请列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getDutySwapList = (params) => {
  return getAction(`${API_BASE_URL}/swapList`, params)
}

/**
 * 申请请假
 * @param {Object} data 请假申请数据
 * @returns {Promise}
 */
export const applyLeave = (data) => {
  return postAction(`${API_BASE_URL}/applyLeave`, data)
}

/**
 * 审批请假
 * @param {Object} data 请假审批数据
 * @returns {Promise}
 */
export const approveLeave = (data) => {
  return putAction(`${API_BASE_URL}/approveLeave`, data)
}

/**
 * 获取请假申请列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getLeaveList = (params) => {
  return getAction(`${API_BASE_URL}/leaveList`, params)
}

/**
 * 获取员工工作时长统计
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getEmployeeWorkHours = (params) => {
  return getAction(`${API_BASE_URL}/workHours`, params)
}

/**
 * 发送值班通知
 * @param {Object} data 通知数据
 * @returns {Promise}
 */
export const sendDutyNotification = (data) => {
  return postAction(`${API_BASE_URL}/sendNotification`, data)
}

/**
 * 获取值班配置
 * @returns {Promise}
 */
export const getDutyConfig = () => {
  return getAction(`${API_BASE_URL}/config`)
}

/**
 * 更新值班配置
 * @param {Object} data 配置数据
 * @returns {Promise}
 */
export const updateDutyConfig = (data) => {
  return putAction(`${API_BASE_URL}/config`, data)
}

/**
 * 自动排班
 * @param {Object} data 自动排班参数
 * @returns {Promise}
 */
export const autoSchedule = (data) => {
  return postAction(`${API_BASE_URL}/autoSchedule`, data)
}

/**
 * 获取排班建议
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export const getDutySuggestions = (params) => {
  return getAction(`${API_BASE_URL}/suggestions`, params)
}

// 导出所有API方法
export default {
  getDutyList,
  getDutyById,
  addDuty,
  editDuty,
  deleteDuty,
  deleteBatchDuty,
  batchAddDuty,
  batchUpdateDutyStatus,
  checkDutyConflict,
  getDutyStatistics,
  getEmployeeList,
  getEmployeeDutyHistory,
  getShiftTypes,
  getDutyCalendar,
  exportDutyData,
  importDutyData,
  getDutyTemplate,
  saveDutyTemplate,
  copyDuty,
  applyDutySwap,
  approveDutySwap,
  getDutySwapList,
  applyLeave,
  approveLeave,
  getLeaveList,
  getEmployeeWorkHours,
  sendDutyNotification,
  getDutyConfig,
  updateDutyConfig,
  autoSchedule,
  getDutySuggestions
}
