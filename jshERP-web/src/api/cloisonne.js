/**
 * 掐丝珐琅馆模块API接口
 * 
 * @author jshERP
 * @since 2025-01-22
 */

import { axios } from '@/utils/request'

const API_BASE = '/cloisonne'

// ==================== 排班管理 ====================

/**
 * 获取排班列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getScheduleList(params) {
  return axios({
    url: `${API_BASE}/schedule/list`,
    method: 'get',
    params
  })
}

/**
 * 获取排班统计数据
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getScheduleStatistics(params) {
  return axios({
    url: `${API_BASE}/schedule/statistics`,
    method: 'get',
    params
  })
}

/**
 * 获取值班人员
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getOnDutyStaff(params) {
  return axios({
    url: `${API_BASE}/schedule/on-duty`,
    method: 'get',
    params
  })
}

/**
 * 创建排班记录
 * @param {Object} data 排班数据
 * @returns {Promise}
 */
export function addSchedule(data) {
  return axios({
    url: `${API_BASE}/schedule/add`,
    method: 'post',
    data
  })
}

/**
 * 更新排班记录
 * @param {Object} data 排班数据
 * @returns {Promise}
 */
export function updateSchedule(data) {
  return axios({
    url: `${API_BASE}/schedule/update`,
    method: 'put',
    data
  })
}

/**
 * 删除排班记录
 * @param {Number} id 排班ID
 * @returns {Promise}
 */
export function deleteSchedule(id) {
  return axios({
    url: `${API_BASE}/schedule/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 批量创建排班记录
 * @param {Object} data 批量排班数据
 * @returns {Promise}
 */
export function batchAddSchedule(data) {
  return axios({
    url: `${API_BASE}/schedule/batch-add`,
    method: 'post',
    data
  })
}

/**
 * 批量更新排班状态
 * @param {Object} data 批量操作数据
 * @returns {Promise}
 */
export function batchUpdateScheduleStatus(data) {
  return axios({
    url: `${API_BASE}/schedule/batch-status`,
    method: 'put',
    data
  })
}

/**
 * 获取员工列表
 * @returns {Promise}
 */
export function getEmployees() {
  return axios({
    url: `${API_BASE}/schedule/employees`,
    method: 'get'
  })
}

// ==================== 总览仪表板 ====================

/**
 * 获取仪表板概览数据
 * @returns {Promise}
 */
export function getDashboardOverview() {
  return axios({
    url: `${API_BASE}/dashboard/overview`,
    method: 'get'
  })
}

/**
 * 创建咖啡店销售记录
 * @param {Object} data 销售数据
 * @returns {Promise}
 */
export function createCoffeeSales(data) {
  return axios({
    url: `${API_BASE}/dashboard/coffee-sales`,
    method: 'post',
    data
  })
}

/**
 * 创建POS订单
 * @param {Object} data 订单数据
 * @returns {Promise}
 */
export function createPOSOrder(data) {
  return axios({
    url: `${API_BASE}/dashboard/pos-order`,
    method: 'post',
    data
  })
}

/**
 * 同步商品到POS
 * @param {Object} data 商品数据
 * @returns {Promise}
 */
export function syncMaterialToPOS(data) {
  return axios({
    url: `${API_BASE}/dashboard/sync-material`,
    method: 'post',
    data
  })
}

// ==================== 咖啡店管理 ====================

/**
 * 获取咖啡店销售列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getCoffeeSalesList(params) {
  return axios({
    url: `${API_BASE}/coffee/sales/list`,
    method: 'get',
    params
  })
}

/**
 * 获取咖啡店销售统计
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getCoffeeSalesStatistics(params) {
  return axios({
    url: `${API_BASE}/coffee/sales/statistics`,
    method: 'get',
    params
  })
}

/**
 * 创建咖啡店销售记录
 * @param {Object} data 销售数据
 * @returns {Promise}
 */
export function addCoffeeSales(data) {
  return axios({
    url: `${API_BASE}/coffee/sales/add`,
    method: 'post',
    data
  })
}

/**
 * 更新咖啡店销售记录
 * @param {Object} data 销售数据
 * @returns {Promise}
 */
export function updateCoffeeSales(data) {
  return axios({
    url: `${API_BASE}/coffee/sales/update`,
    method: 'put',
    data
  })
}

/**
 * 删除咖啡店销售记录
 * @param {Number} id 销售记录ID
 * @returns {Promise}
 */
export function deleteCoffeeSales(id) {
  return axios({
    url: `${API_BASE}/coffee/sales/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 上传咖啡店销售图片
 * @param {FormData} formData 图片数据
 * @returns {Promise}
 */
export function uploadCoffeeSalesImage(formData) {
  return axios({
    url: `${API_BASE}/coffee/sales/upload`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// ==================== POS销售管理 ====================

/**
 * 获取POS商品列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getPOSProductList(params) {
  return axios({
    url: `${API_BASE}/pos/product/list`,
    method: 'get',
    params
  })
}

/**
 * 获取POS商品分类
 * @returns {Promise}
 */
export function getPOSProductCategories() {
  return axios({
    url: `${API_BASE}/pos/product/categories`,
    method: 'get'
  })
}

/**
 * 创建POS商品
 * @param {Object} data 商品数据
 * @returns {Promise}
 */
export function addPOSProduct(data) {
  return axios({
    url: `${API_BASE}/pos/product/add`,
    method: 'post',
    data
  })
}

/**
 * 更新POS商品
 * @param {Object} data 商品数据
 * @returns {Promise}
 */
export function updatePOSProduct(data) {
  return axios({
    url: `${API_BASE}/pos/product/update`,
    method: 'put',
    data
  })
}

/**
 * 删除POS商品
 * @param {Number} id 商品ID
 * @returns {Promise}
 */
export function deletePOSProduct(id) {
  return axios({
    url: `${API_BASE}/pos/product/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 获取POS订单列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getPOSOrderList(params) {
  return axios({
    url: `${API_BASE}/pos/order/list`,
    method: 'get',
    params
  })
}

/**
 * 获取POS订单详情
 * @param {Number} id 订单ID
 * @returns {Promise}
 */
export function getPOSOrderDetail(id) {
  return axios({
    url: `${API_BASE}/pos/order/detail/${id}`,
    method: 'get'
  })
}

/**
 * 创建POS订单
 * @param {Object} data 订单数据
 * @returns {Promise}
 */
export function addPOSOrder(data) {
  return axios({
    url: `${API_BASE}/pos/order/add`,
    method: 'post',
    data
  })
}

/**
 * 更新POS订单
 * @param {Object} data 订单数据
 * @returns {Promise}
 */
export function updatePOSOrder(data) {
  return axios({
    url: `${API_BASE}/pos/order/update`,
    method: 'put',
    data
  })
}

/**
 * 取消POS订单
 * @param {Number} id 订单ID
 * @returns {Promise}
 */
export function cancelPOSOrder(id) {
  return axios({
    url: `${API_BASE}/pos/order/cancel/${id}`,
    method: 'put'
  })
}

/**
 * 退款POS订单
 * @param {Object} data 退款数据
 * @returns {Promise}
 */
export function refundPOSOrder(data) {
  return axios({
    url: `${API_BASE}/pos/order/refund`,
    method: 'post',
    data
  })
}

/**
 * 获取POS销售统计
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getPOSSalesStatistics(params) {
  return axios({
    url: `${API_BASE}/pos/order/statistics`,
    method: 'get',
    params
  })
}

// ==================== 任务管理 ====================

/**
 * 获取任务列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getTaskList(params) {
  return axios({
    url: `${API_BASE}/task/list`,
    method: 'get',
    params
  })
}

/**
 * 创建任务
 * @param {Object} data 任务数据
 * @returns {Promise}
 */
export function addTask(data) {
  return axios({
    url: `${API_BASE}/task/add`,
    method: 'post',
    data
  })
}

/**
 * 更新任务
 * @param {Object} data 任务数据
 * @returns {Promise}
 */
export function updateTask(data) {
  return axios({
    url: `${API_BASE}/task/update`,
    method: 'put',
    data
  })
}

/**
 * 删除任务
 * @param {Number} id 任务ID
 * @returns {Promise}
 */
export function deleteTask(id) {
  return axios({
    url: `${API_BASE}/task/delete/${id}`,
    method: 'delete'
  })
}

/**
 * 完成任务
 * @param {Object} data 完成数据
 * @returns {Promise}
 */
export function completeTask(data) {
  return axios({
    url: `${API_BASE}/task/complete`,
    method: 'put',
    data
  })
}

// ==================== 配置管理 ====================

/**
 * 获取模块配置
 * @returns {Promise}
 */
export function getCloisonneConfig() {
  return axios({
    url: `${API_BASE}/config`,
    method: 'get'
  })
}

/**
 * 更新模块配置
 * @param {Object} data 配置数据
 * @returns {Promise}
 */
export function updateCloisonneConfig(data) {
  return axios({
    url: `${API_BASE}/config`,
    method: 'put',
    data
  })
}

// ==================== 报表导出 ====================

/**
 * 导出排班报表
 * @param {Object} params 导出参数
 * @returns {Promise}
 */
export function exportScheduleReport(params) {
  return axios({
    url: `${API_BASE}/report/schedule/export`,
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出销售报表
 * @param {Object} params 导出参数
 * @returns {Promise}
 */
export function exportSalesReport(params) {
  return axios({
    url: `${API_BASE}/report/sales/export`,
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 导出POS报表
 * @param {Object} params 导出参数
 * @returns {Promise}
 */
export function exportPOSReport(params) {
  return axios({
    url: `${API_BASE}/report/pos/export`,
    method: 'get',
    params,
    responseType: 'blob'
  })
}
