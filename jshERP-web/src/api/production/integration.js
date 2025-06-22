import { axios } from '@/utils/request'

const api = {
  // 商品管理集成接口
  getProductionBOM: '/production/integration/material/bom',
  
  // 库存管理集成接口
  createMaterialOutbound: '/production/integration/inventory/outbound',
  createProductInbound: '/production/integration/inventory/inbound',
  checkMaterialStock: '/production/integration/inventory/check',
  
  // 采购管理集成接口
  createPurchaseOrder: '/production/integration/purchase/create',
  getRecommendedSuppliers: '/production/integration/purchase/suppliers',
  
  // 财务管理集成接口
  pushProductionCost: '/production/integration/finance/cost',
  calculateProductionCost: '/production/integration/finance/cost',
  
  // 综合集成接口
  getIntegrationStatus: '/production/integration/status',
  testIntegration: '/production/integration/test'
}

// ==================== 商品管理集成API ====================

/**
 * 获取生产用商品BOM信息
 * @param {number} materialId 商品ID
 * @returns {Promise} BOM信息
 */
export function getProductionBOM(materialId) {
  return axios({
    url: `${api.getProductionBOM}/${materialId}`,
    method: 'get'
  })
}

// ==================== 库存管理集成API ====================

/**
 * 生产领料出库
 * @param {Object} params 出库参数
 * @param {number} params.taskId 任务ID
 * @param {string} params.taskNumber 任务编号
 * @param {Array} params.materialList 物料清单
 * @param {number} params.depotId 仓库ID
 * @param {string} params.remark 备注
 * @returns {Promise} 出库单号
 */
export function createMaterialOutbound(params) {
  return axios({
    url: api.createMaterialOutbound,
    method: 'post',
    data: params
  })
}

/**
 * 生产完工入库
 * @param {Object} params 入库参数
 * @param {number} params.taskId 任务ID
 * @param {string} params.taskNumber 任务编号
 * @param {number} params.materialId 商品ID
 * @param {number} params.completedQuantity 完工数量
 * @param {number} params.unitCost 单位成本
 * @param {number} params.depotId 仓库ID
 * @param {string} params.remark 备注
 * @returns {Promise} 入库单号
 */
export function createProductInbound(params) {
  return axios({
    url: api.createProductInbound,
    method: 'post',
    data: params
  })
}

/**
 * 检查库存是否充足
 * @param {Object} params 检查参数
 * @param {Array} params.materialList 物料清单
 * @param {number} params.depotId 仓库ID
 * @returns {Promise} 库存检查结果
 */
export function checkMaterialStock(params) {
  return axios({
    url: api.checkMaterialStock,
    method: 'post',
    data: params
  })
}

// ==================== 采购管理集成API ====================

/**
 * 根据生产计划自动生成采购订单
 * @param {Object} params 采购参数
 * @param {Array} params.materialList 物料清单
 * @param {number} params.supplierId 供应商ID
 * @param {string} params.remark 备注
 * @param {Date} params.expectedDate 预期到货日期
 * @returns {Promise} 采购单号
 */
export function createPurchaseOrder(params) {
  return axios({
    url: api.createPurchaseOrder,
    method: 'post',
    data: params
  })
}

/**
 * 获取推荐供应商
 * @param {number} materialId 商品ID
 * @returns {Promise} 推荐供应商列表
 */
export function getRecommendedSuppliers(materialId) {
  return axios({
    url: `${api.getRecommendedSuppliers}/${materialId}`,
    method: 'get'
  })
}

// ==================== 财务管理集成API ====================

/**
 * 推送生产成本到财务系统
 * @param {Object} params 成本参数
 * @param {number} params.taskId 任务ID
 * @param {string} params.taskNumber 任务编号
 * @param {number} params.materialCost 材料成本
 * @param {number} params.laborCost 人工成本
 * @param {number} params.overheadCost 制造费用
 * @returns {Promise} 成本单号
 */
export function pushProductionCost(params) {
  return axios({
    url: api.pushProductionCost,
    method: 'post',
    data: params
  })
}

/**
 * 计算生产任务总成本
 * @param {number} taskId 任务ID
 * @returns {Promise} 成本计算结果
 */
export function calculateProductionCost(taskId) {
  return axios({
    url: `${api.calculateProductionCost}/${taskId}`,
    method: 'get'
  })
}

// ==================== 综合集成API ====================

/**
 * 获取生产管理集成状态
 * @returns {Promise} 集成状态信息
 */
export function getIntegrationStatus() {
  return axios({
    url: api.getIntegrationStatus,
    method: 'get'
  })
}

/**
 * 测试集成连接
 * @param {Object} params 测试参数
 * @param {string} params.module 模块名称 (material|inventory|purchase|finance)
 * @returns {Promise} 测试结果
 */
export function testIntegration(params) {
  return axios({
    url: api.testIntegration,
    method: 'post',
    data: params
  })
}

// ==================== 集成工具函数 ====================

/**
 * 批量检查多个商品的库存
 * @param {Array} materialIds 商品ID数组
 * @param {number} depotId 仓库ID
 * @returns {Promise} 批量库存检查结果
 */
export async function batchCheckMaterialStock(materialIds, depotId) {
  const materialList = materialIds.map(id => ({
    materialId: id,
    quantity: 1 // 默认检查数量为1
  }))
  
  return checkMaterialStock({
    materialList,
    depotId
  })
}

/**
 * 获取商品的完整生产信息（BOM + 库存 + 供应商）
 * @param {number} materialId 商品ID
 * @param {number} depotId 仓库ID
 * @returns {Promise} 完整生产信息
 */
export async function getCompleteProductionInfo(materialId, depotId) {
  try {
    const [bomResult, suppliersResult] = await Promise.all([
      getProductionBOM(materialId),
      getRecommendedSuppliers(materialId)
    ])
    
    const bomData = bomResult.data
    const suppliersData = suppliersResult.data
    
    // 检查BOM组件库存
    if (bomData.bomComponents && bomData.bomComponents.length > 0) {
      const materialList = bomData.bomComponents.map(component => ({
        materialId: component.componentId,
        quantity: component.requiredQuantity
      }))
      
      const stockResult = await checkMaterialStock({
        materialList,
        depotId
      })
      
      bomData.stockCheck = stockResult.data
    }
    
    return {
      bom: bomData,
      suppliers: suppliersData,
      timestamp: new Date()
    }
  } catch (error) {
    console.error('获取完整生产信息失败:', error)
    throw error
  }
}

/**
 * 执行完整的生产流程集成
 * @param {Object} params 生产流程参数
 * @param {number} params.taskId 任务ID
 * @param {string} params.taskNumber 任务编号
 * @param {number} params.materialId 产品ID
 * @param {Array} params.materialList 原料清单
 * @param {number} params.completedQuantity 完工数量
 * @param {number} params.depotId 仓库ID
 * @returns {Promise} 完整流程执行结果
 */
export async function executeCompleteProductionFlow(params) {
  const results = {
    outbound: null,
    inbound: null,
    cost: null,
    errors: []
  }
  
  try {
    // 1. 生产领料出库
    if (params.materialList && params.materialList.length > 0) {
      try {
        const outboundResult = await createMaterialOutbound({
          taskId: params.taskId,
          taskNumber: params.taskNumber,
          materialList: params.materialList,
          depotId: params.depotId,
          remark: '生产任务自动领料'
        })
        results.outbound = outboundResult.data
      } catch (error) {
        results.errors.push({ step: 'outbound', error: error.message })
      }
    }
    
    // 2. 生产完工入库
    if (params.materialId && params.completedQuantity) {
      try {
        // 计算单位成本
        const costResult = await calculateProductionCost(params.taskId)
        const unitCost = costResult.data.unitCost || 0
        
        const inboundResult = await createProductInbound({
          taskId: params.taskId,
          taskNumber: params.taskNumber,
          materialId: params.materialId,
          completedQuantity: params.completedQuantity,
          unitCost: unitCost,
          depotId: params.depotId,
          remark: '生产任务自动完工入库'
        })
        results.inbound = inboundResult.data
        
        // 3. 推送生产成本
        const costPushResult = await pushProductionCost({
          taskId: params.taskId,
          taskNumber: params.taskNumber,
          materialCost: costResult.data.materialCost || 0,
          laborCost: costResult.data.laborCost || 0,
          overheadCost: costResult.data.overheadCost || 0
        })
        results.cost = costPushResult.data
        
      } catch (error) {
        results.errors.push({ step: 'inbound_cost', error: error.message })
      }
    }
    
    return results
  } catch (error) {
    console.error('执行完整生产流程失败:', error)
    throw error
  }
}

export default {
  // 商品管理集成
  getProductionBOM,
  
  // 库存管理集成
  createMaterialOutbound,
  createProductInbound,
  checkMaterialStock,
  batchCheckMaterialStock,
  
  // 采购管理集成
  createPurchaseOrder,
  getRecommendedSuppliers,
  
  // 财务管理集成
  pushProductionCost,
  calculateProductionCost,
  
  // 综合集成
  getIntegrationStatus,
  testIntegration,
  
  // 工具函数
  getCompleteProductionInfo,
  executeCompleteProductionFlow
}
