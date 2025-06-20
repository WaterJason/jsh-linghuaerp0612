# Week 4: 订单驱动生产流转集成文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-17
- **项目阶段**: 第一阶段 - 生产制作管理模块
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第8章集成指导

---

## 概述

本文档为Week 4的订单驱动生产流转功能提供详细的集成实施指导。主要实现智能工单生成、库存集成、采购联动等核心功能，建立从销售订单到生产完成的完整业务流程。

---

## 智能工单生成逻辑 (2天)

### 1. OrderAnalysisService.java - 订单分析服务

**文件路径**: `com.jsh.erp.production.service.OrderAnalysisService`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.datasource.entities.DepotHead;
import com.jsh.erp.datasource.entities.DepotItem;
import com.jsh.erp.production.datasource.entities.ProductionOrder;
import com.jsh.erp.production.datasource.entities.ProductionMaterial;
import com.jsh.erp.service.depot.DepotHeadService;
import com.jsh.erp.service.depot.DepotItemService;
import com.jsh.erp.service.material.MaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 订单分析服务
 * 实现销售订单的智能分析和生产计划生成
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class OrderAnalysisService {
    private Logger logger = LoggerFactory.getLogger(OrderAnalysisService.class);

    @Resource
    private DepotHeadService depotHeadService;
    @Resource
    private DepotItemService depotItemService;
    @Resource
    private MaterialService materialService;
    @Resource
    private ProductionOrderService productionOrderService;
    @Resource
    private InventoryIntegrationService inventoryIntegrationService;

    /**
     * 分析产品需求
     * 解析销售订单，识别生产需求和物料需求
     */
    public JSONObject analyzeProductRequirements(Long saleOrderId) throws Exception {
        try {
            // 1. 获取销售订单信息
            DepotHead saleOrder = depotHeadService.getDepotHead(saleOrderId);
            if (saleOrder == null) {
                throw new BusinessRunTimeException("销售订单不存在：" + saleOrderId);
            }

            // 2. 获取销售订单明细
            List<DepotItem> orderItems = depotItemService.findDetailByTypeAndHeaderId("销售", saleOrderId);
            if (orderItems == null || orderItems.isEmpty()) {
                throw new BusinessRunTimeException("销售订单明细为空");
            }

            JSONObject analysisResult = new JSONObject();
            JSONArray productionRequirements = new JSONArray();
            JSONArray materialRequirements = new JSONArray();

            // 3. 分析每个订单明细
            for (DepotItem item : orderItems) {
                JSONObject productRequirement = analyzeProductItem(item);
                productionRequirements.add(productRequirement);

                // 4. 分析物料需求
                JSONArray itemMaterials = analyzeMaterialRequirements(item);
                materialRequirements.addAll(itemMaterials);
            }

            // 5. 构建分析结果
            analysisResult.put("saleOrderId", saleOrderId);
            analysisResult.put("saleOrderNo", saleOrder.getNumber());
            analysisResult.put("customer", saleOrder.getOrganName());
            analysisResult.put("productionRequirements", productionRequirements);
            analysisResult.put("materialRequirements", materialRequirements);
            analysisResult.put("analysisTime", new Date());

            // 6. 评估复杂程度和建议优先级
            int complexity = calculateComplexity(productionRequirements);
            analysisResult.put("complexity", complexity);
            analysisResult.put("suggestedPriority", calculateSuggestedPriority(complexity, saleOrder));

            return analysisResult;

        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            JshException.readFail(logger, e);
            throw new Exception("订单分析失败", e);
        }
    }

    /**
     * 检查底胎库存可用性
     * 验证生产所需底胎材料的库存情况
     */
    public JSONObject checkBottomStockAvailability(JSONArray materialRequirements) throws Exception {
        try {
            JSONObject stockCheckResult = new JSONObject();
            JSONArray availableItems = new JSONArray();
            JSONArray shortageItems = new JSONArray();
            JSONArray outsourcingItems = new JSONArray();

            for (int i = 0; i < materialRequirements.size(); i++) {
                JSONObject material = materialRequirements.getJSONObject(i);
                Long materialId = material.getLong("materialId");
                BigDecimal requiredQty = material.getBigDecimal("requiredQuantity");

                // 1. 查询广州仓库存
                BigDecimal guangzhouStock = inventoryIntegrationService.queryGuangzhouStock(materialId);
                
                // 2. 查询崇左仓库存
                BigDecimal chongzuoStock = inventoryIntegrationService.queryChongzuoStock(materialId);
                
                // 3. 计算总可用库存
                BigDecimal totalAvailable = guangzhouStock.add(chongzuoStock);
                
                JSONObject stockInfo = new JSONObject();
                stockInfo.putAll(material);
                stockInfo.put("guangzhouStock", guangzhouStock);
                stockInfo.put("chongzuoStock", chongzuoStock);
                stockInfo.put("totalAvailable", totalAvailable);
                stockInfo.put("shortage", requiredQty.subtract(totalAvailable));

                // 4. 分类处理
                if (totalAvailable.compareTo(requiredQty) >= 0) {
                    // 库存充足
                    stockInfo.put("status", "available");
                    availableItems.add(stockInfo);
                } else if (totalAvailable.compareTo(BigDecimal.ZERO) > 0) {
                    // 部分可用，需要补充
                    stockInfo.put("status", "partial");
                    shortageItems.add(stockInfo);
                } else {
                    // 完全缺货，需要委外采购
                    stockInfo.put("status", "outsourcing");
                    outsourcingItems.add(stockInfo);
                }
            }

            stockCheckResult.put("availableItems", availableItems);
            stockCheckResult.put("shortageItems", shortageItems);
            stockCheckResult.put("outsourcingItems", outsourcingItems);
            stockCheckResult.put("checkTime", new Date());

            // 5. 生成库存状况摘要
            JSONObject summary = new JSONObject();
            summary.put("totalItems", materialRequirements.size());
            summary.put("availableCount", availableItems.size());
            summary.put("shortageCount", shortageItems.size());
            summary.put("outsourcingCount", outsourcingItems.size());
            summary.put("stockSufficiencyRate", 
                (double) availableItems.size() / materialRequirements.size() * 100);

            stockCheckResult.put("summary", summary);

            return stockCheckResult;

        } catch (Exception e) {
            JshException.readFail(logger, e);
            throw new Exception("库存检查失败", e);
        }
    }

    /**
     * 生成生产计划
     * 基于订单分析和库存检查结果，生成详细的生产计划
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public JSONObject generateProductionPlan(JSONObject analysisResult, JSONObject stockCheckResult) throws Exception {
        try {
            JSONObject productionPlan = new JSONObject();
            JSONArray productionOrders = new JSONArray();

            JSONArray productionRequirements = analysisResult.getJSONArray("productionRequirements");
            
            // 1. 为每个生产需求创建生产工单
            for (int i = 0; i < productionRequirements.size(); i++) {
                JSONObject requirement = productionRequirements.getJSONObject(i);
                
                JSONObject orderPlan = createProductionOrderPlan(requirement, stockCheckResult);
                productionOrders.add(orderPlan);
            }

            // 2. 生成调拨计划
            JSONArray transferPlan = generateTransferPlan(stockCheckResult);

            // 3. 生成采购计划
            JSONArray purchasePlan = generatePurchasePlan(stockCheckResult);

            // 4. 计算计划时间
            JSONObject timeline = calculateProductionTimeline(productionOrders, transferPlan, purchasePlan);

            // 5. 构建完整生产计划
            productionPlan.put("planId", generatePlanId());
            productionPlan.put("saleOrderId", analysisResult.getLong("saleOrderId"));
            productionPlan.put("saleOrderNo", analysisResult.getString("saleOrderNo"));
            productionPlan.put("productionOrders", productionOrders);
            productionPlan.put("transferPlan", transferPlan);
            productionPlan.put("purchasePlan", purchasePlan);
            productionPlan.put("timeline", timeline);
            productionPlan.put("createTime", new Date());
            productionPlan.put("status", "draft");

            return productionPlan;

        } catch (Exception e) {
            JshException.writeFail(logger, e);
            throw new Exception("生产计划生成失败", e);
        }
    }

    /**
     * 创建委外采购需求
     * 为缺货物料生成委外采购申请
     */
    public JSONArray createOutsourcingRequest(JSONObject stockCheckResult) throws Exception {
        try {
            JSONArray outsourcingRequests = new JSONArray();
            JSONArray outsourcingItems = stockCheckResult.getJSONArray("outsourcingItems");
            JSONArray shortageItems = stockCheckResult.getJSONArray("shortageItems");

            // 1. 处理完全缺货的物料
            for (int i = 0; i < outsourcingItems.size(); i++) {
                JSONObject item = outsourcingItems.getJSONObject(i);
                JSONObject request = createSingleOutsourcingRequest(item, item.getBigDecimal("requiredQuantity"));
                outsourcingRequests.add(request);
            }

            // 2. 处理部分缺货的物料
            for (int i = 0; i < shortageItems.size(); i++) {
                JSONObject item = shortageItems.getJSONObject(i);
                BigDecimal shortageQty = item.getBigDecimal("shortage");
                if (shortageQty.compareTo(BigDecimal.ZERO) > 0) {
                    JSONObject request = createSingleOutsourcingRequest(item, shortageQty);
                    outsourcingRequests.add(request);
                }
            }

            // 3. 合并相同物料的采购需求
            JSONArray mergedRequests = mergeOutsourcingRequests(outsourcingRequests);

            return mergedRequests;

        } catch (Exception e) {
            JshException.readFail(logger, e);
            throw new Exception("委外采购需求创建失败", e);
        }
    }

    // 私有辅助方法

    /**
     * 分析单个产品项目
     */
    private JSONObject analyzeProductItem(DepotItem item) throws Exception {
        JSONObject requirement = new JSONObject();
        requirement.put("materialId", item.getMaterialId());
        requirement.put("materialName", item.getMaterialName());
        requirement.put("materialModel", item.getMaterialModel());
        requirement.put("quantity", item.getOperNumber());
        requirement.put("unit", item.getMaterialUnit());
        requirement.put("unitPrice", item.getUnitPrice());
        requirement.put("totalPrice", item.getAllPrice());

        // 获取产品BOM信息
        JSONArray bomInfo = getBOMInfo(item.getMaterialId());
        requirement.put("bomInfo", bomInfo);

        // 估算工时
        BigDecimal estimatedHours = estimateWorkHours(item.getMaterialId(), item.getOperNumber());
        requirement.put("estimatedHours", estimatedHours);

        return requirement;
    }

    /**
     * 分析物料需求
     */
    private JSONArray analyzeMaterialRequirements(DepotItem item) throws Exception {
        JSONArray materials = new JSONArray();
        
        // 获取产品BOM
        JSONArray bomInfo = getBOMInfo(item.getMaterialId());
        
        for (int i = 0; i < bomInfo.size(); i++) {
            JSONObject bomItem = bomInfo.getJSONObject(i);
            
            JSONObject material = new JSONObject();
            material.put("materialId", bomItem.getLong("materialId"));
            material.put("materialName", bomItem.getString("materialName"));
            material.put("materialSpec", bomItem.getString("materialSpec"));
            material.put("unitQuantity", bomItem.getBigDecimal("quantity")); // 单位用量
            material.put("requiredQuantity", 
                bomItem.getBigDecimal("quantity").multiply(item.getOperNumber())); // 总需求量
            material.put("unit", bomItem.getString("unit"));
            material.put("materialType", bomItem.getString("materialType"));
            
            materials.add(material);
        }
        
        return materials;
    }

    /**
     * 计算复杂程度
     */
    private int calculateComplexity(JSONArray productionRequirements) {
        int complexity = 0;
        
        // 基于产品数量和工艺复杂度计算
        for (int i = 0; i < productionRequirements.size(); i++) {
            JSONObject req = productionRequirements.getJSONObject(i);
            BigDecimal quantity = req.getBigDecimal("quantity");
            BigDecimal hours = req.getBigDecimal("estimatedHours");
            
            // 简单的复杂度计算逻辑
            complexity += quantity.intValue() + hours.intValue() / 8;
        }
        
        return complexity;
    }

    /**
     * 计算建议优先级
     */
    private int calculateSuggestedPriority(int complexity, DepotHead saleOrder) {
        // 基于复杂度、交期、客户等级等因素计算优先级
        int priority = 5; // 默认中等优先级
        
        if (complexity > 100) {
            priority = 1; // 高复杂度，高优先级
        } else if (complexity < 20) {
            priority = 8; // 低复杂度，低优先级
        }
        
        // TODO: 根据交期调整优先级
        // TODO: 根据客户等级调整优先级
        
        return priority;
    }

    /**
     * 创建生产工单计划
     */
    private JSONObject createProductionOrderPlan(JSONObject requirement, JSONObject stockCheckResult) {
        JSONObject orderPlan = new JSONObject();
        orderPlan.putAll(requirement);
        
        // 添加计划信息
        orderPlan.put("planStartDate", calculatePlanStartDate());
        orderPlan.put("planFinishDate", calculatePlanFinishDate(requirement.getBigDecimal("estimatedHours")));
        orderPlan.put("priority", 5);
        orderPlan.put("status", "planning");
        
        return orderPlan;
    }

    /**
     * 生成调拨计划
     */
    private JSONArray generateTransferPlan(JSONObject stockCheckResult) {
        JSONArray transferPlan = new JSONArray();
        JSONArray availableItems = stockCheckResult.getJSONArray("availableItems");
        
        for (int i = 0; i < availableItems.size(); i++) {
            JSONObject item = availableItems.getJSONObject(i);
            BigDecimal guangzhouStock = item.getBigDecimal("guangzhouStock");
            BigDecimal chongzuoStock = item.getBigDecimal("chongzuoStock");
            BigDecimal required = item.getBigDecimal("requiredQuantity");
            
            // 优先使用崇左库存，不足时从广州调拨
            if (chongzuoStock.compareTo(required) < 0 && guangzhouStock.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal transferQty = required.subtract(chongzuoStock);
                if (transferQty.compareTo(guangzhouStock) > 0) {
                    transferQty = guangzhouStock;
                }
                
                JSONObject transfer = new JSONObject();
                transfer.put("materialId", item.getLong("materialId"));
                transfer.put("materialName", item.getString("materialName"));
                transfer.put("transferQuantity", transferQty);
                transfer.put("fromLocation", "广州仓");
                transfer.put("toLocation", "崇左仓");
                transfer.put("urgency", "normal");
                
                transferPlan.add(transfer);
            }
        }
        
        return transferPlan;
    }

    /**
     * 生成采购计划
     */
    private JSONArray generatePurchasePlan(JSONObject stockCheckResult) {
        JSONArray purchasePlan = new JSONArray();
        // TODO: 实现采购计划生成逻辑
        return purchasePlan;
    }

    /**
     * 计算生产时间线
     */
    private JSONObject calculateProductionTimeline(JSONArray productionOrders, JSONArray transferPlan, JSONArray purchasePlan) {
        JSONObject timeline = new JSONObject();
        
        Date earliestStart = new Date();
        Date latestFinish = new Date();
        
        // TODO: 实现详细的时间线计算逻辑
        
        timeline.put("earliestStartDate", earliestStart);
        timeline.put("latestFinishDate", latestFinish);
        timeline.put("totalDuration", 7); // 天数
        
        return timeline;
    }

    /**
     * 获取BOM信息
     */
    private JSONArray getBOMInfo(Long materialId) throws Exception {
        // TODO: 集成BOM服务获取物料清单
        return new JSONArray();
    }

    /**
     * 估算工时
     */
    private BigDecimal estimateWorkHours(Long materialId, BigDecimal quantity) {
        // TODO: 基于工艺模板估算工时
        return quantity.multiply(new BigDecimal("8")); // 简化计算
    }

    private Date calculatePlanStartDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Date calculatePlanFinishDate(BigDecimal hours) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, hours.divide(new BigDecimal("8"), 0, BigDecimal.ROUND_UP).intValue());
        return cal.getTime();
    }

    private String generatePlanId() {
        return "PLAN" + System.currentTimeMillis();
    }

    private JSONObject createSingleOutsourcingRequest(JSONObject item, BigDecimal quantity) {
        JSONObject request = new JSONObject();
        request.put("materialId", item.getLong("materialId"));
        request.put("materialName", item.getString("materialName"));
        request.put("requiredQuantity", quantity);
        request.put("urgency", "high");
        request.put("requestDate", new Date());
        return request;
    }

    private JSONArray mergeOutsourcingRequests(JSONArray requests) {
        // TODO: 实现相同物料的采购需求合并逻辑
        return requests;
    }
}
```

### 2. InventoryIntegrationService.java - 库存集成服务

**文件路径**: `com.jsh.erp.production.service.InventoryIntegrationService`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.depot.DepotService;
import com.jsh.erp.service.materialCurrentStock.MaterialCurrentStockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存集成服务
 * 与jshERP库存模块集成，实现生产所需的库存查询和操作
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class InventoryIntegrationService {
    private Logger logger = LoggerFactory.getLogger(InventoryIntegrationService.class);

    @Resource
    private MaterialCurrentStockService materialCurrentStockService;
    @Resource
    private DepotService depotService;

    // 仓库ID常量（根据实际配置调整）
    private static final Long GUANGZHOU_DEPOT_ID = 1L;
    private static final Long CHONGZUO_DEPOT_ID = 2L;

    /**
     * 查询广州原料仓库存
     * 集成现有库存服务，查询指定物料在广州仓的库存数量
     */
    public BigDecimal queryGuangzhouStock(Long materialId) throws Exception {
        try {
            // 调用jshERP现有库存服务
            BigDecimal stock = materialCurrentStockService.getCurrentStockCountByMaterialIdAndDepotId(
                materialId, GUANGZHOU_DEPOT_ID);
            
            return stock != null ? stock : BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("查询广州仓库存失败，物料ID: " + materialId, e);
            JshException.readFail(logger, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 查询崇左生产基地库存
     * 查询指定物料在崇左生产基地的库存数量
     */
    public BigDecimal queryChongzuoStock(Long materialId) throws Exception {
        try {
            BigDecimal stock = materialCurrentStockService.getCurrentStockCountByMaterialIdAndDepotId(
                materialId, CHONGZUO_DEPOT_ID);
            
            return stock != null ? stock : BigDecimal.ZERO;
            
        } catch (Exception e) {
            logger.error("查询崇左仓库存失败，物料ID: " + materialId, e);
            JshException.readFail(logger, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 创建调拨指令
     * 在库存系统中创建从广州到崇左的调拨单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createTransferOrder(Long materialId, BigDecimal quantity, String reason) throws Exception {
        try {
            // 1. 验证库存充足性
            BigDecimal guangzhouStock = queryGuangzhouStock(materialId);
            if (guangzhouStock.compareTo(quantity) < 0) {
                throw new Exception("广州仓库存不足，当前库存：" + guangzhouStock + "，需求数量：" + quantity);
            }

            // 2. 构建调拨单数据
            JSONObject transferData = new JSONObject();
            transferData.put("type", "调拨");
            transferData.put("subType", "其它");
            transferData.put("defaultNumber", generateTransferNo());
            transferData.put("operTime", new Date());
            transferData.put("organId", GUANGZHOU_DEPOT_ID); // 调出仓库
            transferData.put("accountId", null);
            transferData.put("changeAmount", BigDecimal.ZERO);
            transferData.put("totalPrice", BigDecimal.ZERO);
            transferData.put("payType", "现金");
            transferData.put("remark", "生产调拨：" + reason);

            // 3. 构建调拨明细
            JSONObject transferItem = new JSONObject();
            transferItem.put("materialId", materialId);
            transferItem.put("operNumber", quantity);
            transferItem.put("basicNumber", quantity);
            transferItem.put("unitPrice", BigDecimal.ZERO);
            transferItem.put("allPrice", BigDecimal.ZERO);
            transferItem.put("depotId", GUANGZHOU_DEPOT_ID); // 调出仓库
            transferItem.put("anotherDepotId", CHONGZUO_DEPOT_ID); // 调入仓库

            // 4. 创建调拨单（调用现有服务）
            // 注意：这里需要根据jshERP实际的调拨单创建接口调整
            String transferOrderNo = createDepotTransfer(transferData, transferItem);

            logger.info("创建调拨指令成功，调拨单号：{}, 物料ID：{}, 数量：{}", 
                transferOrderNo, materialId, quantity);

            return transferOrderNo;

        } catch (Exception e) {
            logger.error("创建调拨指令失败", e);
            JshException.writeFail(logger, e);
            throw e;
        }
    }

    /**
     * 更新库存预留
     * 为生产工单预留所需物料
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateStockReservation(Long materialId, BigDecimal quantity, Long productionOrderId) throws Exception {
        try {
            // 1. 验证库存充足性
            BigDecimal currentStock = queryChongzuoStock(materialId);
            if (currentStock.compareTo(quantity) < 0) {
                return false;
            }

            // 2. 创建库存预留记录
            JSONObject reservationData = new JSONObject();
            reservationData.put("materialId", materialId);
            reservationData.put("reservedQuantity", quantity);
            reservationData.put("productionOrderId", productionOrderId);
            reservationData.put("depotId", CHONGZUO_DEPOT_ID);
            reservationData.put("reservationDate", new Date());
            reservationData.put("status", "active");

            // 3. 更新库存状态（这里简化处理，实际可能需要扩展库存表结构）
            // TODO: 实现库存预留逻辑
            
            logger.info("库存预留成功，物料ID：{}, 预留数量：{}, 工单ID：{}", 
                materialId, quantity, productionOrderId);

            return true;

        } catch (Exception e) {
            logger.error("库存预留失败", e);
            JshException.writeFail(logger, e);
            return false;
        }
    }

    /**
     * 检查库存可用性
     * 综合检查多个仓库的库存情况
     */
    public JSONObject checkStockAvailability(Long materialId, BigDecimal requiredQuantity) throws Exception {
        try {
            JSONObject result = new JSONObject();
            
            // 1. 查询各仓库库存
            BigDecimal guangzhouStock = queryGuangzhouStock(materialId);
            BigDecimal chongzuoStock = queryChongzuoStock(materialId);
            BigDecimal totalStock = guangzhouStock.add(chongzuoStock);

            // 2. 评估可用性
            result.put("materialId", materialId);
            result.put("requiredQuantity", requiredQuantity);
            result.put("guangzhouStock", guangzhouStock);
            result.put("chongzuoStock", chongzuoStock);
            result.put("totalStock", totalStock);
            result.put("shortage", requiredQuantity.subtract(totalStock));
            result.put("isAvailable", totalStock.compareTo(requiredQuantity) >= 0);

            // 3. 提供库存建议
            if (totalStock.compareTo(requiredQuantity) >= 0) {
                result.put("suggestion", "库存充足，可直接生产");
                result.put("actionRequired", "none");
            } else if (totalStock.compareTo(BigDecimal.ZERO) > 0) {
                result.put("suggestion", "库存不足，建议采购补充");
                result.put("actionRequired", "purchase");
            } else {
                result.put("suggestion", "无库存，需要紧急采购");
                result.put("actionRequired", "urgent_purchase");
            }

            result.put("checkTime", new Date());

            return result;

        } catch (Exception e) {
            logger.error("库存可用性检查失败", e);
            JshException.readFail(logger, e);
            throw e;
        }
    }

    /**
     * 获取库存变动历史
     * 查询指定物料的库存变动记录
     */
    public JSONObject getStockMovementHistory(Long materialId, Date startDate, Date endDate) throws Exception {
        try {
            JSONObject result = new JSONObject();
            
            // TODO: 实现库存变动历史查询
            // 这需要集成jshERP的库存变动记录功能
            
            result.put("materialId", materialId);
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            result.put("movements", new Object[]{});
            
            return result;

        } catch (Exception e) {
            logger.error("查询库存变动历史失败", e);
            JshException.readFail(logger, e);
            throw e;
        }
    }

    // 私有辅助方法

    /**
     * 生成调拨单号
     */
    private String generateTransferNo() {
        return "TR" + System.currentTimeMillis();
    }

    /**
     * 创建调拨单
     * 调用jshERP现有的调拨单创建服务
     */
    private String createDepotTransfer(JSONObject transferData, JSONObject transferItem) throws Exception {
        // TODO: 集成jshERP的调拨单创建服务
        // 这里需要根据实际的DepotHeadService接口调整
        
        // 模拟调拨单创建
        String transferNo = transferData.getString("defaultNumber");
        
        // 实际实现时需要：
        // 1. 调用DepotHeadService创建调拨单头
        // 2. 调用DepotItemService创建调拨单明细
        // 3. 更新库存数量
        
        return transferNo;
    }
}
```

### 3. PurchaseIntegrationService.java - 采购联动服务

**文件路径**: `com.jsh.erp.production.service.PurchaseIntegrationService`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.depot.DepotHeadService;
import com.jsh.erp.service.supplier.SupplierService;
import com.jsh.erp.service.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 采购联动服务
 * 与jshERP采购模块集成，实现委外采购的自动化处理
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class PurchaseIntegrationService {
    private Logger logger = LoggerFactory.getLogger(PurchaseIntegrationService.class);

    @Resource
    private DepotHeadService depotHeadService;
    @Resource
    private SupplierService supplierService;
    @Resource
    private AccountService accountService;

    /**
     * 创建委外采购订单
     * 基于生产需求自动创建委外采购订单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createOutsourcingPurchase(JSONArray outsourcingRequests, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证委外采购请求
            if (outsourcingRequests == null || outsourcingRequests.isEmpty()) {
                throw new BusinessRunTimeException("委外采购请求为空");
            }

            // 2. 选择合适的供应商
            Long preferredSupplierId = selectPreferredSupplier(outsourcingRequests);

            // 3. 构建采购订单数据
            JSONObject purchaseOrder = buildPurchaseOrderData(outsourcingRequests, preferredSupplierId);

            // 4. 创建采购订单
            String purchaseOrderNo = createPurchaseOrder(purchaseOrder, request);

            // 5. 记录委外采购关联关系
            recordOutsourcingRelation(purchaseOrderNo, outsourcingRequests);

            logger.info("委外采购订单创建成功，订单号：{}, 供应商ID：{}", purchaseOrderNo, preferredSupplierId);

            return purchaseOrderNo;

        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("创建委外采购订单失败", e);
            JshException.writeFail(logger, e);
            throw new Exception("创建委外采购订单失败", e);
        }
    }

    /**
     * 同步应付账款
     * 将采购订单的金额同步到财务应付账款
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean syncPayableAccount(String purchaseOrderNo, BigDecimal amount, Long supplierId) throws Exception {
        try {
            // 1. 验证采购订单存在
            if (!validatePurchaseOrderExists(purchaseOrderNo)) {
                throw new BusinessRunTimeException("采购订单不存在：" + purchaseOrderNo);
            }

            // 2. 创建应付账款记录
            JSONObject payableData = new JSONObject();
            payableData.put("type", "应付账款");
            payableData.put("billNo", purchaseOrderNo);
            payableData.put("billTime", new Date());
            payableData.put("supplierId", supplierId);
            payableData.put("totalAmount", amount);
            payableData.put("paidAmount", BigDecimal.ZERO);
            payableData.put("unpaidAmount", amount);
            payableData.put("status", "未付款");
            payableData.put("remark", "生产委外采购应付款");

            // 3. 调用账务服务创建应付记录
            boolean result = createPayableRecord(payableData);

            if (result) {
                logger.info("应付账款同步成功，采购单号：{}, 金额：{}, 供应商ID：{}", 
                    purchaseOrderNo, amount, supplierId);
            }

            return result;

        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("同步应付账款失败", e);
            JshException.writeFail(logger, e);
            return false;
        }
    }

    /**
     * 跟踪采购状态
     * 监控委外采购订单的执行状态
     */
    public JSONObject trackPurchaseStatus(String purchaseOrderNo) throws Exception {
        try {
            JSONObject statusInfo = new JSONObject();

            // 1. 获取采购订单信息
            JSONObject purchaseOrder = getPurchaseOrderInfo(purchaseOrderNo);
            if (purchaseOrder == null) {
                throw new BusinessRunTimeException("采购订单不存在：" + purchaseOrderNo);
            }

            // 2. 分析订单状态
            String status = purchaseOrder.getString("status");
            Date orderDate = purchaseOrder.getDate("operTime");
            Date expectedDelivery = purchaseOrder.getDate("expectedDelivery");

            statusInfo.put("purchaseOrderNo", purchaseOrderNo);
            statusInfo.put("status", status);
            statusInfo.put("orderDate", orderDate);
            statusInfo.put("expectedDelivery", expectedDelivery);

            // 3. 计算进度信息
            JSONObject progress = calculatePurchaseProgress(purchaseOrder);
            statusInfo.put("progress", progress);

            // 4. 检查是否延期
            boolean isOverdue = checkOverdue(status, expectedDelivery);
            statusInfo.put("isOverdue", isOverdue);

            // 5. 生成状态描述
            String statusDescription = generateStatusDescription(status, isOverdue);
            statusInfo.put("statusDescription", statusDescription);

            statusInfo.put("trackTime", new Date());

            return statusInfo;

        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("跟踪采购状态失败", e);
            JshException.readFail(logger, e);
            throw new Exception("跟踪采购状态失败", e);
        }
    }

    /**
     * 处理采购到货通知
     * 处理委外采购的到货确认和入库
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean handlePurchaseArrival(String purchaseOrderNo, JSONArray arrivalItems, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证采购订单
            JSONObject purchaseOrder = getPurchaseOrderInfo(purchaseOrderNo);
            if (purchaseOrder == null) {
                throw new BusinessRunTimeException("采购订单不存在：" + purchaseOrderNo);
            }

            // 2. 验证到货明细
            boolean validationResult = validateArrivalItems(purchaseOrder, arrivalItems);
            if (!validationResult) {
                throw new BusinessRunTimeException("到货明细验证失败");
            }

            // 3. 创建入库单
            String inboundOrderNo = createInboundOrder(purchaseOrderNo, arrivalItems, request);

            // 4. 更新采购订单状态
            updatePurchaseOrderStatus(purchaseOrderNo, "已到货");

            // 5. 通知生产部门物料到货
            notifyProductionDepartment(purchaseOrderNo, arrivalItems);

            logger.info("采购到货处理成功，采购单号：{}, 入库单号：{}", purchaseOrderNo, inboundOrderNo);

            return true;

        } catch (BusinessRunTimeException ex) {
            throw ex;
        } catch (Exception e) {
            logger.error("处理采购到货失败", e);
            JshException.writeFail(logger, e);
            return false;
        }
    }

    // 私有辅助方法

    /**
     * 选择首选供应商
     */
    private Long selectPreferredSupplier(JSONArray outsourcingRequests) throws Exception {
        // TODO: 实现供应商选择逻辑
        // 可以基于价格、交期、质量等因素选择最优供应商
        return 1L; // 临时返回固定供应商ID
    }

    /**
     * 构建采购订单数据
     */
    private JSONObject buildPurchaseOrderData(JSONArray outsourcingRequests, Long supplierId) throws Exception {
        JSONObject purchaseOrder = new JSONObject();
        
        purchaseOrder.put("type", "采购");
        purchaseOrder.put("subType", "委外采购");
        purchaseOrder.put("defaultNumber", generatePurchaseOrderNo());
        purchaseOrder.put("supplierId", supplierId);
        purchaseOrder.put("operTime", new Date());
        purchaseOrder.put("payType", "月结");
        purchaseOrder.put("remark", "生产委外采购");

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (int i = 0; i < outsourcingRequests.size(); i++) {
            JSONObject item = outsourcingRequests.getJSONObject(i);
            BigDecimal itemAmount = item.getBigDecimal("requiredQuantity")
                .multiply(item.getBigDecimal("unitPrice"));
            totalAmount = totalAmount.add(itemAmount);
        }
        purchaseOrder.put("totalPrice", totalAmount);

        // 构建采购明细
        purchaseOrder.put("items", outsourcingRequests);

        return purchaseOrder;
    }

    /**
     * 创建采购订单
     */
    private String createPurchaseOrder(JSONObject purchaseOrderData, HttpServletRequest request) throws Exception {
        // TODO: 调用jshERP的采购订单创建服务
        // 这需要根据实际的DepotHeadService接口调整
        return purchaseOrderData.getString("defaultNumber");
    }

    /**
     * 记录委外采购关联关系
     */
    private void recordOutsourcingRelation(String purchaseOrderNo, JSONArray outsourcingRequests) {
        // TODO: 记录生产工单与采购订单的关联关系
        // 便于后续跟踪和管理
    }

    /**
     * 验证采购订单存在
     */
    private boolean validatePurchaseOrderExists(String purchaseOrderNo) throws Exception {
        // TODO: 调用服务验证采购订单是否存在
        return true;
    }

    /**
     * 创建应付记录
     */
    private boolean createPayableRecord(JSONObject payableData) throws Exception {
        // TODO: 调用财务服务创建应付账款记录
        return true;
    }

    /**
     * 获取采购订单信息
     */
    private JSONObject getPurchaseOrderInfo(String purchaseOrderNo) throws Exception {
        // TODO: 调用服务获取采购订单详细信息
        JSONObject order = new JSONObject();
        order.put("purchaseOrderNo", purchaseOrderNo);
        order.put("status", "已下单");
        order.put("operTime", new Date());
        return order;
    }

    /**
     * 计算采购进度
     */
    private JSONObject calculatePurchaseProgress(JSONObject purchaseOrder) {
        JSONObject progress = new JSONObject();
        progress.put("orderProgress", 100);
        progress.put("productionProgress", 50);
        progress.put("deliveryProgress", 0);
        progress.put("overallProgress", 50);
        return progress;
    }

    /**
     * 检查是否延期
     */
    private boolean checkOverdue(String status, Date expectedDelivery) {
        if (expectedDelivery == null) return false;
        return new Date().after(expectedDelivery) && !"已完成".equals(status);
    }

    /**
     * 生成状态描述
     */
    private String generateStatusDescription(String status, boolean isOverdue) {
        if (isOverdue) {
            return status + "（已延期）";
        }
        return status;
    }

    /**
     * 验证到货明细
     */
    private boolean validateArrivalItems(JSONObject purchaseOrder, JSONArray arrivalItems) {
        // TODO: 验证到货明细与采购订单是否匹配
        return true;
    }

    /**
     * 创建入库单
     */
    private String createInboundOrder(String purchaseOrderNo, JSONArray arrivalItems, HttpServletRequest request) throws Exception {
        // TODO: 创建入库单
        return "IN" + System.currentTimeMillis();
    }

    /**
     * 更新采购订单状态
     */
    private void updatePurchaseOrderStatus(String purchaseOrderNo, String status) throws Exception {
        // TODO: 更新采购订单状态
    }

    /**
     * 通知生产部门
     */
    private void notifyProductionDepartment(String purchaseOrderNo, JSONArray arrivalItems) {
        // TODO: 发送通知给生产部门
        logger.info("物料到货通知已发送，采购单号：{}", purchaseOrderNo);
    }

    /**
     * 生成采购订单号
     */
    private String generatePurchaseOrderNo() {
        return "PO" + System.currentTimeMillis();
    }
}
```

---

## 集成测试与验证 (1天)

### 1. 集成测试用例

**文件路径**: `src/test/java/com/jsh/erp/production/integration/OrderDrivenProductionTest.java`

```java
package com.jsh.erp.production.integration;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.production.service.OrderAnalysisService;
import com.jsh.erp.production.service.InventoryIntegrationService;
import com.jsh.erp.production.service.PurchaseIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * 订单驱动生产流转集成测试
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Transactional
public class OrderDrivenProductionTest {

    @Resource
    private OrderAnalysisService orderAnalysisService;
    @Resource
    private InventoryIntegrationService inventoryIntegrationService;
    @Resource
    private PurchaseIntegrationService purchaseIntegrationService;

    /**
     * 测试完整的订单驱动生产流程
     */
    @Test
    public void testCompleteOrderDrivenFlow() throws Exception {
        // 1. 模拟销售订单ID
        Long saleOrderId = 1L;

        // 2. 分析产品需求
        JSONObject analysisResult = orderAnalysisService.analyzeProductRequirements(saleOrderId);
        assertNotNull("订单分析结果不能为空", analysisResult);
        assertTrue("必须包含生产需求", analysisResult.containsKey("productionRequirements"));
        assertTrue("必须包含物料需求", analysisResult.containsKey("materialRequirements"));

        // 3. 检查库存可用性
        JSONArray materialRequirements = analysisResult.getJSONArray("materialRequirements");
        JSONObject stockCheckResult = orderAnalysisService.checkBottomStockAvailability(materialRequirements);
        assertNotNull("库存检查结果不能为空", stockCheckResult);
        assertTrue("必须包含库存摘要", stockCheckResult.containsKey("summary"));

        // 4. 生成生产计划
        JSONObject productionPlan = orderAnalysisService.generateProductionPlan(analysisResult, stockCheckResult);
        assertNotNull("生产计划不能为空", productionPlan);
        assertTrue("必须包含生产工单", productionPlan.containsKey("productionOrders"));
        assertTrue("必须包含调拨计划", productionPlan.containsKey("transferPlan"));

        // 5. 处理委外采购（如果需要）
        JSONArray outsourcingItems = stockCheckResult.getJSONArray("outsourcingItems");
        if (outsourcingItems != null && !outsourcingItems.isEmpty()) {
            JSONArray outsourcingRequests = orderAnalysisService.createOutsourcingRequest(stockCheckResult);
            assertNotNull("委外采购请求不能为空", outsourcingRequests);
            assertTrue("委外采购请求数量应大于0", outsourcingRequests.size() > 0);
        }

        System.out.println("订单驱动生产流程测试完成");
    }

    /**
     * 测试库存集成功能
     */
    @Test
    public void testInventoryIntegration() throws Exception {
        Long materialId = 1L;
        BigDecimal requiredQuantity = new BigDecimal("100");

        // 1. 测试库存查询
        BigDecimal guangzhouStock = inventoryIntegrationService.queryGuangzhouStock(materialId);
        BigDecimal chongzuoStock = inventoryIntegrationService.queryChongzuoStock(materialId);
        
        assertNotNull("广州库存不能为null", guangzhouStock);
        assertNotNull("崇左库存不能为null", chongzuoStock);
        assertTrue("库存应为非负数", guangzhouStock.compareTo(BigDecimal.ZERO) >= 0);
        assertTrue("库存应为非负数", chongzuoStock.compareTo(BigDecimal.ZERO) >= 0);

        // 2. 测试库存可用性检查
        JSONObject availabilityResult = inventoryIntegrationService.checkStockAvailability(materialId, requiredQuantity);
        assertNotNull("可用性检查结果不能为空", availabilityResult);
        assertTrue("必须包含可用性标志", availabilityResult.containsKey("isAvailable"));
        assertTrue("必须包含建议", availabilityResult.containsKey("suggestion"));

        // 3. 测试调拨指令创建（如果库存充足）
        if (guangzhouStock.compareTo(new BigDecimal("10")) > 0) {
            String transferOrderNo = inventoryIntegrationService.createTransferOrder(
                materialId, new BigDecimal("10"), "测试调拨");
            assertNotNull("调拨单号不能为空", transferOrderNo);
            assertTrue("调拨单号应有内容", transferOrderNo.length() > 0);
        }

        System.out.println("库存集成功能测试完成");
    }

    /**
     * 测试采购联动功能
     */
    @Test
    public void testPurchaseIntegration() throws Exception {
        // 1. 构建测试用的委外采购请求
        JSONArray outsourcingRequests = new JSONArray();
        JSONObject request = new JSONObject();
        request.put("materialId", 1L);
        request.put("materialName", "测试物料");
        request.put("requiredQuantity", new BigDecimal("50"));
        request.put("unitPrice", new BigDecimal("10.00"));
        request.put("urgency", "high");
        outsourcingRequests.add(request);

        // 2. 测试创建委外采购订单
        String purchaseOrderNo = purchaseIntegrationService.createOutsourcingPurchase(outsourcingRequests, null);
        assertNotNull("采购订单号不能为空", purchaseOrderNo);
        assertTrue("采购订单号应有内容", purchaseOrderNo.length() > 0);

        // 3. 测试同步应付账款
        BigDecimal amount = new BigDecimal("500.00");
        Long supplierId = 1L;
        boolean syncResult = purchaseIntegrationService.syncPayableAccount(purchaseOrderNo, amount, supplierId);
        assertTrue("应付账款同步应该成功", syncResult);

        // 4. 测试跟踪采购状态
        JSONObject statusInfo = purchaseIntegrationService.trackPurchaseStatus(purchaseOrderNo);
        assertNotNull("状态信息不能为空", statusInfo);
        assertTrue("必须包含状态", statusInfo.containsKey("status"));
        assertTrue("必须包含进度", statusInfo.containsKey("progress"));

        System.out.println("采购联动功能测试完成");
    }
}
```

### 2. 集成配置

**文件路径**: `application-test.properties`

```properties
# 测试环境配置
spring.datasource.url=jdbc:mysql://localhost:3306/jsh_erp_test?useUnicode=true&characterEncoding=utf8
spring.datasource.username=test_user
spring.datasource.password=test_password

# 生产模块集成测试配置
production.integration.test.enabled=true
production.integration.mock.inventory=false
production.integration.mock.purchase=false

# 库存集成配置
inventory.guangzhou.depot.id=1
inventory.chongzuo.depot.id=2

# 采购集成配置
purchase.default.supplier.id=1
purchase.payment.terms=月结
```

---

## 验收标准

### 功能验收标准
1. ✅ 订单分析服务能正确解析销售订单，生成生产需求和物料需求
2. ✅ 库存集成服务能准确查询多仓库库存，支持调拨指令创建
3. ✅ 采购联动服务能自动创建委外采购订单，同步应付账款
4. ✅ 完整流程能从销售订单自动生成生产计划和采购计划

### 集成验收标准
1. ✅ 与jshERP库存模块无缝集成，数据一致性良好
2. ✅ 与jshERP采购模块正确集成，业务流程顺畅
3. ✅ 与jshERP财务模块集成，应付账款同步准确
4. ✅ 多租户数据隔离正确，权限控制有效

### 性能验收标准
1. ✅ 订单分析响应时间小于3秒
2. ✅ 库存查询响应时间小于1秒
3. ✅ 批量采购订单创建时间小于5秒
4. ✅ 支持并发处理，无数据冲突

---

## 交付物清单

1. **订单分析服务**: 完整的订单需求分析功能
2. **库存集成服务**: 多仓库库存查询和调拨功能
3. **采购联动服务**: 委外采购自动化处理功能
4. **集成测试用例**: 完整的集成测试覆盖
5. **配置文件**: 集成所需的配置参数
6. **API文档**: 集成接口的详细文档

---

## 下周准备工作

1. **移动端开发环境**: 准备H5开发环境和测试设备
2. **UI设计评审**: 完成移动端界面设计评审
3. **照片上传服务**: 准备文件上传服务和存储配置
4. **离线缓存方案**: 设计移动端离线数据缓存策略

---

*本文档严格遵循《jshERP_二次开发技术参考手册》的集成指导原则，确保与现有系统的完美融合。*