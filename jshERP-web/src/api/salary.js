import { axios } from '@/utils/request'

const api = {
  // 薪酬档案管理
  salaryProfile: '/salary/profile',
  salaryProfileList: '/salary/profile/list',
  salaryProfileInfo: '/salary/profile/info',
  salaryProfileAdd: '/salary/profile/add',
  salaryProfileUpdate: '/salary/profile/update',
  salaryProfileDelete: '/salary/profile/delete',
  salaryProfileDeleteBatch: '/salary/profile/deleteBatch',
  salaryProfileUpdateStatus: '/salary/profile/updateStatus',
  salaryProfileByEmployeeId: '/salary/profile/getByEmployeeId',
  
  // 薪酬计算管理
  salaryCalculation: '/salary/calculation',
  salaryCalculationList: '/salary/calculation/list',
  salaryCalculationInfo: '/salary/calculation/info',
  salaryCalculationCalculate: '/salary/calculation/calculate',
  salaryCalculationApprove: '/salary/calculation/approve',
  salaryCalculationStatistics: '/salary/calculation/statistics',
  salaryCalculationPendingApproval: '/salary/calculation/pendingApproval',
  salaryCalculationDelete: '/salary/calculation/delete',
  salaryCalculationRecalculate: '/salary/calculation/recalculate',
  
  // 薪酬发放管理
  salaryPayment: '/salary/payment',
  salaryPaymentList: '/salary/payment/list',
  salaryPaymentInfo: '/salary/payment/info',
  salaryPaymentPay: '/salary/payment/pay',
  salaryPaymentConfirm: '/salary/payment/confirm',
  salaryPaymentPayslip: '/salary/payment/payslip',
  salaryPaymentPending: '/salary/payment/pending'
}

// ==================== 薪酬档案管理 API ====================

/**
 * 获取薪酬档案列表
 * @param {Object} params 查询参数
 */
export function getSalaryProfileList(params) {
  return axios({
    url: api.salaryProfileList,
    method: 'get',
    params: params
  })
}

/**
 * 根据ID获取薪酬档案详情
 * @param {Number} id 档案ID
 */
export function getSalaryProfileInfo(id) {
  return axios({
    url: api.salaryProfileInfo,
    method: 'get',
    params: { id }
  })
}

/**
 * 根据员工ID获取薪酬档案
 * @param {Number} employeeId 员工ID
 */
export function getSalaryProfileByEmployeeId(employeeId) {
  return axios({
    url: api.salaryProfileByEmployeeId,
    method: 'get',
    params: { employeeId }
  })
}

/**
 * 新增薪酬档案
 * @param {Object} data 档案数据
 */
export function addSalaryProfile(data) {
  return axios({
    url: api.salaryProfileAdd,
    method: 'post',
    data: data
  })
}

/**
 * 更新薪酬档案
 * @param {Object} data 档案数据
 */
export function updateSalaryProfile(data) {
  return axios({
    url: api.salaryProfileUpdate,
    method: 'put',
    data: data
  })
}

/**
 * 删除薪酬档案
 * @param {Number} id 档案ID
 */
export function deleteSalaryProfile(id) {
  return axios({
    url: api.salaryProfileDelete,
    method: 'delete',
    params: { id }
  })
}

/**
 * 批量删除薪酬档案
 * @param {String} ids 档案ID列表，逗号分隔
 */
export function batchDeleteSalaryProfile(ids) {
  return axios({
    url: api.salaryProfileDeleteBatch,
    method: 'delete',
    params: { ids }
  })
}

/**
 * 更新薪酬档案状态
 * @param {Number} id 档案ID
 * @param {String} status 状态
 */
export function updateSalaryProfileStatus(id, status) {
  return axios({
    url: api.salaryProfileUpdateStatus,
    method: 'put',
    params: { id, status }
  })
}

// ==================== 薪酬计算管理 API ====================

/**
 * 获取薪酬计算记录列表
 * @param {Object} params 查询参数
 */
export function getSalaryCalculationList(params) {
  return axios({
    url: api.salaryCalculationList,
    method: 'get',
    params: params
  })
}

/**
 * 根据ID获取薪酬计算记录详情
 * @param {Number} id 计算记录ID
 */
export function getSalaryCalculationInfo(id) {
  return axios({
    url: api.salaryCalculationInfo,
    method: 'get',
    params: { id }
  })
}

/**
 * 执行薪酬计算
 * @param {Object} data 计算参数
 */
export function calculateSalary(data) {
  return axios({
    url: api.salaryCalculationCalculate,
    method: 'post',
    data: data
  })
}

/**
 * 审批薪酬计算
 * @param {Object} data 审批数据
 */
export function approveSalaryCalculation(data) {
  return axios({
    url: api.salaryCalculationApprove,
    method: 'put',
    data: data
  })
}

/**
 * 获取薪酬统计信息
 * @param {String} calculationMonth 计算月份
 */
export function getSalaryStatistics(calculationMonth) {
  return axios({
    url: api.salaryCalculationStatistics,
    method: 'get',
    params: { calculationMonth }
  })
}

/**
 * 获取待审批列表
 */
export function getPendingApprovalList() {
  return axios({
    url: api.salaryCalculationPendingApproval,
    method: 'get'
  })
}

/**
 * 删除薪酬计算记录
 * @param {Number} id 计算记录ID
 */
export function deleteSalaryCalculation(id) {
  return axios({
    url: api.salaryCalculationDelete,
    method: 'delete',
    params: { id }
  })
}

/**
 * 重新计算薪酬
 * @param {Number} id 计算记录ID
 */
export function recalculateSalary(id) {
  return axios({
    url: api.salaryCalculationRecalculate,
    method: 'put',
    params: { id }
  })
}

// ==================== 薪酬发放管理 API ====================

/**
 * 获取薪酬发放记录列表
 * @param {Object} params 查询参数
 */
export function getSalaryPaymentList(params) {
  return axios({
    url: api.salaryPaymentList,
    method: 'get',
    params: params
  })
}

/**
 * 根据ID获取薪酬发放记录详情
 * @param {Number} id 发放记录ID
 */
export function getSalaryPaymentInfo(id) {
  return axios({
    url: api.salaryPaymentInfo,
    method: 'get',
    params: { id }
  })
}

/**
 * 执行薪酬发放
 * @param {Object} data 发放数据
 */
export function paySalary(data) {
  return axios({
    url: api.salaryPaymentPay,
    method: 'post',
    data: data
  })
}

/**
 * 确认发放
 * @param {Object} data 确认数据
 */
export function confirmPayment(data) {
  return axios({
    url: api.salaryPaymentConfirm,
    method: 'put',
    data: data
  })
}

/**
 * 生成薪资条
 * @param {Number} id 发放记录ID
 */
export function generatePayslip(id) {
  return axios({
    url: api.salaryPaymentPayslip,
    method: 'get',
    params: { id }
  })
}

/**
 * 获取待发放列表
 */
export function getPendingPaymentList() {
  return axios({
    url: api.salaryPaymentPending,
    method: 'get'
  })
}

export default api
