package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.ProductionOrderMapper;
import com.jsh.erp.datasource.mappers.ProductionOrderMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 生产管理服务实现类
 * 
 * @author jshERP
 * @date 2025-06-21
 */
@Service
public class ProductionServiceImpl implements ProductionService {
    private Logger logger = LoggerFactory.getLogger(ProductionServiceImpl.class);

    @Resource
    private ProductionOrderMapper productionOrderMapper;

    @Resource
    private ProductionOrderMapperEx productionOrderMapperEx;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private DepotHeadService depotHeadService;

    @Resource
    private MaterialService materialService;

    @Resource
    private DepotService depotService;

    @Resource
    private DepotItemService depotItemService;

    @Override
    public ProductionOrder getProductionOrder(Long id) throws Exception {
        ProductionOrder productionOrder = null;
        try {
            productionOrder = productionOrderMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return productionOrder;
    }

    @Override
    public ProductionOrder getProductionOrderByNumber(String orderNo) throws Exception {
        ProductionOrder productionOrder = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            productionOrder = productionOrderMapperEx.selectByOrderNo(orderNo, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return productionOrder;
    }

    @Override
    public List<ProductionOrder> select(String orderNo, String status, String materialId, 
                                       String salesOrderId, String beginTime, String endTime) throws Exception {
        List<ProductionOrder> list = null;
        try {
            PageUtils.startPage();
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            Long mId = StringUtil.isNotEmpty(materialId) ? Long.parseLong(materialId) : null;
            Long sId = StringUtil.isNotEmpty(salesOrderId) ? Long.parseLong(salesOrderId) : null;
            
            list = productionOrderMapperEx.selectByCondition(orderNo, status, mId, sId, 
                    beginTime, endTime, tenantId, null, null);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertProductionOrder(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionOrder productionOrder = JSONObject.parseObject(obj.toJSONString(), ProductionOrder.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            productionOrder.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            productionOrder.setCreateBy(userInfo != null ? userInfo.getId() : null);
            productionOrder.setCreateTime(new Date());
            productionOrder.setDeleteFlag("0");
            
            // 生成订单号
            if (StringUtil.isEmpty(productionOrder.getOrderNo())) {
                productionOrder.setOrderNo(generateOrderNo());
            }
            
            // 设置默认状态
            if (StringUtil.isEmpty(productionOrder.getStatus())) {
                productionOrder.setStatus("PENDING");
            }
            
            result = productionOrderMapper.insertSelective(productionOrder);
            logService.insertLog("生产管理", 
                    BusinessConstants.LOG_OPERATION_TYPE_ADD + productionOrder.getOrderNo(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateProductionOrder(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionOrder productionOrder = JSONObject.parseObject(obj.toJSONString(), ProductionOrder.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            productionOrder.setUpdateBy(userInfo != null ? userInfo.getId() : null);
            productionOrder.setUpdateTime(new Date());
            
            result = productionOrderMapper.updateByPrimaryKeySelective(productionOrder);
            logService.insertLog("生产管理", 
                    BusinessConstants.LOG_OPERATION_TYPE_EDIT + productionOrder.getOrderNo(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteProductionOrder(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            ProductionOrder productionOrder = getProductionOrder(id);
            if (productionOrder == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_READ_FAIL_CODE, "生产订单不存在");
            }
            
            User userInfo = userService.getCurrentUser();
            productionOrder.setDeleteFlag("1");
            productionOrder.setUpdateBy(userInfo != null ? userInfo.getId() : null);
            productionOrder.setUpdateTime(new Date());
            
            result = productionOrderMapper.updateByPrimaryKeySelective(productionOrder);
            logService.insertLog("生产管理", 
                    BusinessConstants.LOG_OPERATION_TYPE_DELETE + productionOrder.getOrderNo(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public Map<String, Object> generateFromSalesOrder(Long salesOrderId, HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 获取销售订单信息
            DepotHead salesOrder = depotHeadService.getDepotHead(salesOrderId);
            if (salesOrder == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_READ_FAIL_CODE, "销售订单不存在");
            }
            
            // 2. 在 jsh_production_order 表中创建一条新记录
            ProductionOrder productionOrder = new ProductionOrder();
            productionOrder.setOrderNo(generateOrderNo());
            productionOrder.setSalesOrderId(salesOrderId);
            productionOrder.setStatus("PENDING");
            productionOrder.setDeliveryDate(salesOrder.getOperTime()); // 使用销售订单的操作时间作为交付期限
            productionOrder.setRemark("从销售订单 " + salesOrder.getNumber() + " 智能生成");
            
            User userInfo = userService.getCurrentUser();
            productionOrder.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            productionOrder.setCreateBy(userInfo != null ? userInfo.getId() : null);
            productionOrder.setCreateTime(new Date());
            productionOrder.setDeleteFlag("0");
            
            // 这里需要根据销售订单的商品信息设置materialId和quantity
            // 暂时设置为默认值，实际应该从销售订单明细中获取
            productionOrder.setMaterialId(1L); // 需要从销售订单明细获取
            productionOrder.setQuantity(BigDecimal.ONE); // 需要从销售订单明细获取
            
            int insertResult = productionOrderMapper.insertSelective(productionOrder);
            
            if (insertResult > 0) {
                result.put("productionOrderId", productionOrder.getId());
                result.put("productionOrderNo", productionOrder.getOrderNo());
                result.put("message", "生产订单创建成功");
                
                // 3. 检查'底胎'的库存
                String stockCheckResult = checkBaseStockAvailability(productionOrder);
                result.put("stockCheckResult", stockCheckResult);

                // 4. 为'制作服务'创建采购订单
                String purchaseOrderResult = createServicePurchaseOrder(productionOrder, request);
                result.put("purchaseOrderResult", purchaseOrderResult);

                // 5. 为'底胎'创建内部调拨单
                String transferOrderResult = createBaseTransferOrder(productionOrder, request);
                result.put("transferOrderResult", transferOrderResult);
                
                logService.insertLog("生产管理", 
                        "智能生成工单：" + productionOrder.getOrderNo(), request);
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "生产订单创建失败");
            }
            
        } catch (Exception e) {
            logger.error("智能生成工单失败", e);
            throw e;
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateStatus(Long id, String status, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = productionOrderMapperEx.updateStatus(id, status, 
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                logService.insertLog("生产管理", "更新生产订单状态：" + status, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateProgress(Long id, String progress, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = productionOrderMapperEx.updateProgress(id, progress, 
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                logService.insertLog("生产管理", "更新生产订单进度：" + progress + "%", request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    public Map<String, Object> getStatistics() throws Exception {
        Map<String, Object> statistics = new HashMap<>();
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            // 获取各状态的生产订单统计
            List<Object> statusStats = productionOrderMapperEx.getStatusStatistics(tenantId);
            statistics.put("statusStatistics", statusStats);
            
            // 获取成本统计
            String beginTime = new SimpleDateFormat("yyyy-MM-01").format(new Date());
            String endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            List<Object> costStats = productionOrderMapperEx.getCostStatistics(beginTime, endTime, tenantId);
            statistics.put("costStatistics", costStats);
            
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return statistics;
    }

    @Override
    public String generateOrderNo() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "PO" + dateStr;
        
        // 查询当日最大序号
        String maxNo = productionOrderMapperEx.findMaxOrderNoByPrefix(prefix);
        
        int sequence = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            String seqStr = maxNo.substring(prefix.length());
            try {
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return prefix + String.format("%04d", sequence);
    }

    @Override
    public boolean checkOrderNoExists(String orderNo) throws Exception {
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            ProductionOrder order = productionOrderMapperEx.selectByOrderNo(orderNo, tenantId);
            return order != null;
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return false;
        }
    }

    @Override
    public List<ProductionOrder> getProductionOrdersBySalesOrderId(Long salesOrderId) throws Exception {
        List<ProductionOrder> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            list = productionOrderMapperEx.selectBySalesOrderId(salesOrderId, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    public List<ProductionOrder> getProductionOrdersByMaterialId(Long materialId) throws Exception {
        List<ProductionOrder> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            list = productionOrderMapperEx.selectByMaterialId(materialId, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProductionOrder(String ids, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            String[] idArray = ids.split(",");
            User userInfo = userService.getCurrentUser();
            result = productionOrderMapperEx.batchDeleteByIds(new Date(), 
                    userInfo != null ? userInfo.getId() : null, idArray);
            
            if (result > 0) {
                logService.insertLog("生产管理", "批量删除生产订单：" + ids, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    public List<ProductionOrder> getExpiringSoonOrders(Integer days) throws Exception {
        List<ProductionOrder> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            list = productionOrderMapperEx.selectExpiringSoon(days, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateCost(Long id, String totalCost, String materialCost, String laborCost, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = productionOrderMapperEx.updateCost(id, totalCost, materialCost, laborCost,
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                logService.insertLog("生产管理", "更新生产订单成本", request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 检查底胎库存可用性
     * 根据生产订单查询底胎库存是否充足
     */
    private String checkBaseStockAvailability(ProductionOrder productionOrder) throws Exception {
        try {
            // 假设底胎的materialId为固定值，实际应该从业务配置中获取
            Long baseMaterialId = 1L; // 底胎的商品ID，需要根据实际业务配置
            Long mainDepotId = 1L;    // 主仓库ID，需要根据实际业务配置

            // 查询底胎的当前库存
            BigDecimal currentStock = materialService.getCurrentStockByMaterialIdAndDepotId(baseMaterialId, mainDepotId);

            // 获取生产订单需要的底胎数量（假设1:1比例）
            BigDecimal requiredQuantity = productionOrder.getQuantity() != null ? productionOrder.getQuantity() : BigDecimal.ONE;

            if (currentStock.compareTo(requiredQuantity) >= 0) {
                return "底胎库存充足，当前库存：" + currentStock + "，需要数量：" + requiredQuantity;
            } else {
                return "底胎库存不足，当前库存：" + currentStock + "，需要数量：" + requiredQuantity + "，缺少：" + requiredQuantity.subtract(currentStock);
            }
        } catch (Exception e) {
            logger.error("检查底胎库存失败", e);
            return "底胎库存检查失败：" + e.getMessage();
        }
    }

    /**
     * 为制作服务创建采购订单
     * 当底胎库存不足时，创建采购订单
     */
    private String createServicePurchaseOrder(ProductionOrder productionOrder, HttpServletRequest request) throws Exception {
        try {
            // 检查是否需要创建采购订单（库存不足时）
            Long baseMaterialId = 1L; // 底胎的商品ID
            Long mainDepotId = 1L;    // 主仓库ID
            BigDecimal currentStock = materialService.getCurrentStockByMaterialIdAndDepotId(baseMaterialId, mainDepotId);
            BigDecimal requiredQuantity = productionOrder.getQuantity() != null ? productionOrder.getQuantity() : BigDecimal.ONE;

            if (currentStock.compareTo(requiredQuantity) < 0) {
                // 库存不足，创建采购订单
                BigDecimal purchaseQuantity = requiredQuantity.subtract(currentStock);

                // 构建采购订单数据
                JSONObject purchaseOrderData = new JSONObject();
                purchaseOrderData.put("type", BusinessConstants.DEPOTHEAD_TYPE_OTHER);
                purchaseOrderData.put("subType", BusinessConstants.SUB_TYPE_PURCHASE_ORDER);
                purchaseOrderData.put("operTime", new Date());
                purchaseOrderData.put("remark", "生产订单 " + productionOrder.getOrderNo() + " 自动生成的采购订单");

                // 构建采购明细
                JSONObject purchaseItem = new JSONObject();
                purchaseItem.put("materialId", baseMaterialId);
                purchaseItem.put("operNumber", purchaseQuantity);
                purchaseItem.put("unitPrice", BigDecimal.ZERO); // 需要根据实际业务设置价格

                // 调用DepotHeadService创建采购订单
                String purchaseOrderJson = purchaseOrderData.toJSONString();
                String itemsJson = "[" + purchaseItem.toJSONString() + "]";

                depotHeadService.addDepotHeadAndDetail(purchaseOrderJson, itemsJson, request);

                return "已创建采购订单，采购数量：" + purchaseQuantity;
            } else {
                return "库存充足，无需创建采购订单";
            }
        } catch (Exception e) {
            logger.error("创建制作服务采购订单失败", e);
            return "创建采购订单失败：" + e.getMessage();
        }
    }

    /**
     * 为底胎创建内部调拨单
     * 从主仓库调拨到崇左仓库
     */
    private String createBaseTransferOrder(ProductionOrder productionOrder, HttpServletRequest request) throws Exception {
        try {
            Long baseMaterialId = 1L; // 底胎的商品ID
            Long mainDepotId = 1L;    // 主仓库ID
            Long chongzuoDepotId = 2L; // 崇左仓库ID，需要根据实际配置
            BigDecimal transferQuantity = productionOrder.getQuantity() != null ? productionOrder.getQuantity() : BigDecimal.ONE;

            // 再次检查主仓库库存
            BigDecimal currentStock = materialService.getCurrentStockByMaterialIdAndDepotId(baseMaterialId, mainDepotId);

            if (currentStock.compareTo(transferQuantity) >= 0) {
                // 构建调拨单数据
                JSONObject transferOrderData = new JSONObject();
                transferOrderData.put("type", BusinessConstants.DEPOTHEAD_TYPE_OTHER);
                transferOrderData.put("subType", BusinessConstants.SUB_TYPE_TRANSFER);
                transferOrderData.put("operTime", new Date());
                transferOrderData.put("remark", "生产订单 " + productionOrder.getOrderNo() + " 自动生成的调拨单");

                // 构建调拨明细
                JSONObject transferItem = new JSONObject();
                transferItem.put("materialId", baseMaterialId);
                transferItem.put("operNumber", transferQuantity);
                transferItem.put("depotId", mainDepotId);        // 调出仓库
                transferItem.put("anotherDepotId", chongzuoDepotId); // 调入仓库
                transferItem.put("unitPrice", BigDecimal.ZERO);

                // 调用DepotHeadService创建调拨单
                String transferOrderJson = transferOrderData.toJSONString();
                String itemsJson = "[" + transferItem.toJSONString() + "]";

                depotHeadService.addDepotHeadAndDetail(transferOrderJson, itemsJson, request);

                return "已创建调拨单，调拨数量：" + transferQuantity + "，从主仓库调拨到崇左仓库";
            } else {
                return "主仓库库存不足，无法创建调拨单。当前库存：" + currentStock + "，需要数量：" + transferQuantity;
            }
        } catch (Exception e) {
            logger.error("创建底胎调拨单失败", e);
            return "创建调拨单失败：" + e.getMessage();
        }
    }
}
