package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 生产管理系统集成服务
 * 负责与jshERP现有模块的集成，包括商品管理、库存管理、采购管理、财务管理
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ProductionIntegrationService {
    
    private Logger logger = LoggerFactory.getLogger(ProductionIntegrationService.class);
    
    @Resource
    private MaterialService materialService;
    
    @Resource
    private DepotService depotService;
    
    @Resource
    private DepotHeadService depotHeadService;
    
    @Resource
    private DepotItemService depotItemService;
    
    @Resource
    private AccountHeadService accountHeadService;
    
    @Resource
    private AccountItemService accountItemService;
    
    @Resource
    private SupplierService supplierService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private LogService logService;
    
    // ==================== 商品管理集成 ====================
    
    /**
     * 获取生产用商品BOM信息
     * 支持多级BOM展开和成本计算
     */
    public JSONObject getProductionBOM(Long materialId) throws Exception {
        JSONObject result = new JSONObject();
        try {
            // 获取商品基本信息
            Material material = materialService.getMaterial(materialId);
            if (material == null) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_NOT_EXISTS_CODE, "商品不存在");
            }
            
            result.put("materialId", material.getId());
            result.put("materialName", material.getName());
            result.put("materialModel", material.getModel());
            result.put("materialStandard", material.getStandard());
            result.put("materialColor", material.getColor());
            result.put("unitName", material.getUnit());
            
            // 获取BOM清单
            JSONArray bomList = getBOMComponents(materialId);
            result.put("bomComponents", bomList);
            
            // 计算BOM总成本
            BigDecimal totalCost = calculateBOMCost(bomList);
            result.put("totalCost", totalCost);
            
            logger.info("获取生产BOM成功：商品ID {}, 组件数量 {}", materialId, bomList.size());
            
        } catch (Exception e) {
            logger.error("获取生产BOM失败：商品ID {}", materialId, e);
            throw e;
        }
        
        return result;
    }
    
    /**
     * 获取BOM组件清单
     */
    private JSONArray getBOMComponents(Long materialId) throws Exception {
        JSONArray components = new JSONArray();
        
        // 获取商品扩展信息中的BOM数据
        // TODO: 实现BOM组件获取逻辑，这里使用模拟数据
        // List<MaterialExtend> extendList = materialService.getMaterialExtendByMId(materialId);

        // 模拟BOM组件数据
        JSONObject component1 = new JSONObject();
        component1.put("componentId", 1L);
        component1.put("componentName", "银丝");
        component1.put("componentModel", "0.5mm");
        component1.put("componentStandard", "999银");
        component1.put("componentColor", "银色");
        component1.put("unitName", "克");
        component1.put("requiredQuantity", new BigDecimal("10"));
        component1.put("currentStock", new BigDecimal("100"));
        component1.put("latestPrice", new BigDecimal("8.5"));
        component1.put("componentCost", new BigDecimal("85"));
        components.add(component1);

        JSONObject component2 = new JSONObject();
        component2.put("componentId", 2L);
        component2.put("componentName", "珐琅釉");
        component2.put("componentModel", "蓝色");
        component2.put("componentStandard", "高温釉");
        component2.put("componentColor", "蓝色");
        component2.put("unitName", "克");
        component2.put("requiredQuantity", new BigDecimal("5"));
        component2.put("currentStock", new BigDecimal("50"));
        component2.put("latestPrice", new BigDecimal("12"));
        component2.put("componentCost", new BigDecimal("60"));
        components.add(component2);
        
        return components;
    }
    
    /**
     * 计算BOM总成本
     */
    private BigDecimal calculateBOMCost(JSONArray bomList) {
        BigDecimal totalCost = BigDecimal.ZERO;
        
        for (int i = 0; i < bomList.size(); i++) {
            JSONObject component = bomList.getJSONObject(i);
            BigDecimal componentCost = component.getBigDecimal("componentCost");
            if (componentCost != null) {
                totalCost = totalCost.add(componentCost);
            }
        }
        
        return totalCost;
    }
    
    /**
     * 获取商品最新采购价格
     */
    private BigDecimal getLatestPurchasePrice(Long materialId) throws Exception {
        try {
            // TODO: 实现从最近的采购单据中获取价格的逻辑
            // BigDecimal latestPrice = depotItemService.getLatestPurchasePrice(materialId);
            // 临时返回模拟价格
            return new BigDecimal("10.00");
        } catch (Exception e) {
            logger.warn("获取商品最新采购价格失败：商品ID {}", materialId);
            return BigDecimal.ZERO;
        }
    }
    
    // ==================== 库存管理集成 ====================
    
    /**
     * 生产领料出库
     * 根据生产任务自动生成领料单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createMaterialOutbound(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            String taskNumber = params.getString("taskNumber");
            JSONArray materialList = params.getJSONArray("materialList");
            Long depotId = params.getLong("depotId");
            String remark = params.getString("remark");
            
            // 创建出库单头
            JSONObject depotHeadData = new JSONObject();
            depotHeadData.put("type", BusinessConstants.DEPOTHEAD_TYPE_OUT);
            depotHeadData.put("subType", "生产领料");
            depotHeadData.put("number", generateOutboundNumber());
            depotHeadData.put("defaultNumber", taskNumber);
            depotHeadData.put("operTime", new Date());
            depotHeadData.put("remark", "生产任务领料：" + (remark != null ? remark : taskNumber));
            depotHeadData.put("status", "0"); // 未审核
            
            // 创建出库单明细
            JSONArray itemList = new JSONArray();
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (int i = 0; i < materialList.size(); i++) {
                JSONObject material = materialList.getJSONObject(i);
                
                JSONObject item = new JSONObject();
                item.put("materialId", material.getLong("materialId"));
                item.put("operNumber", material.getBigDecimal("quantity"));
                item.put("unitPrice", material.getBigDecimal("unitPrice"));
                item.put("allPrice", material.getBigDecimal("quantity").multiply(material.getBigDecimal("unitPrice")));
                item.put("depotId", depotId);
                item.put("remark", "生产领料");
                
                itemList.add(item);
                totalAmount = totalAmount.add(item.getBigDecimal("allPrice"));
            }
            
            depotHeadData.put("totalPrice", totalAmount);
            depotHeadData.put("info", itemList.toJSONString());
            
            // 调用库存服务创建出库单
            int result = depotHeadService.insertDepotHead(depotHeadData, request);
            
            if (result > 0) {
                logger.info("生产领料出库单创建成功：任务ID {}, 单据编号 {}", taskId, depotHeadData.getString("number"));
                return depotHeadData.getString("number");
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "创建出库单失败");
            }
            
        } catch (Exception e) {
            logger.error("生产领料出库失败", e);
            throw e;
        }
    }
    
    /**
     * 生产完工入库
     * 根据生产任务完工情况自动生成入库单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createProductInbound(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            String taskNumber = params.getString("taskNumber");
            Long materialId = params.getLong("materialId");
            BigDecimal completedQuantity = params.getBigDecimal("completedQuantity");
            BigDecimal unitCost = params.getBigDecimal("unitCost");
            Long depotId = params.getLong("depotId");
            String remark = params.getString("remark");
            
            // 创建入库单头
            JSONObject depotHeadData = new JSONObject();
            depotHeadData.put("type", BusinessConstants.DEPOTHEAD_TYPE_IN);
            depotHeadData.put("subType", "生产完工");
            depotHeadData.put("number", generateInboundNumber());
            depotHeadData.put("defaultNumber", taskNumber);
            depotHeadData.put("operTime", new Date());
            depotHeadData.put("remark", "生产完工入库：" + (remark != null ? remark : taskNumber));
            depotHeadData.put("status", "0"); // 未审核
            
            // 创建入库单明细
            JSONArray itemList = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("materialId", materialId);
            item.put("operNumber", completedQuantity);
            item.put("unitPrice", unitCost);
            item.put("allPrice", completedQuantity.multiply(unitCost));
            item.put("depotId", depotId);
            item.put("remark", "生产完工入库");
            
            itemList.add(item);
            
            depotHeadData.put("totalPrice", item.getBigDecimal("allPrice"));
            depotHeadData.put("info", itemList.toJSONString());
            
            // 调用库存服务创建入库单
            int result = depotHeadService.insertDepotHead(depotHeadData, request);
            
            if (result > 0) {
                logger.info("生产完工入库单创建成功：任务ID {}, 单据编号 {}", taskId, depotHeadData.getString("number"));
                return depotHeadData.getString("number");
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "创建入库单失败");
            }
            
        } catch (Exception e) {
            logger.error("生产完工入库失败", e);
            throw e;
        }
    }
    
    /**
     * 检查库存是否充足
     */
    public JSONObject checkMaterialStock(JSONArray materialList, Long depotId) throws Exception {
        JSONObject result = new JSONObject();
        JSONArray insufficientList = new JSONArray();
        boolean stockSufficient = true;
        
        try {
            for (int i = 0; i < materialList.size(); i++) {
                JSONObject material = materialList.getJSONObject(i);
                Long materialId = material.getLong("materialId");
                BigDecimal requiredQuantity = material.getBigDecimal("quantity");
                
                // 获取当前库存
                // TODO: 实现获取当前库存的逻辑
                BigDecimal currentStock = BigDecimal.ZERO;
                
                if (currentStock.compareTo(requiredQuantity) < 0) {
                    stockSufficient = false;
                    
                    JSONObject insufficient = new JSONObject();
                    insufficient.put("materialId", materialId);
                    insufficient.put("materialName", material.getString("materialName"));
                    insufficient.put("requiredQuantity", requiredQuantity);
                    insufficient.put("currentStock", currentStock);
                    insufficient.put("shortageQuantity", requiredQuantity.subtract(currentStock));
                    
                    insufficientList.add(insufficient);
                }
            }
            
            result.put("stockSufficient", stockSufficient);
            result.put("insufficientList", insufficientList);
            
        } catch (Exception e) {
            logger.error("检查库存失败", e);
            throw e;
        }
        
        return result;
    }
    
    // ==================== 辅助方法 ====================
    
    /**
     * 生成出库单号
     */
    private String generateOutboundNumber() {
        return "CL" + Tools.getNowTime() + Tools.getCharAndNum(4);
    }

    /**
     * 生成入库单号
     */
    private String generateInboundNumber() {
        return "CR" + Tools.getNowTime() + Tools.getCharAndNum(4);
    }

    // ==================== 采购管理集成 ====================

    /**
     * 根据生产计划自动生成采购订单
     * 分析库存不足的原材料，自动创建采购申请
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createPurchaseOrder(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            JSONArray materialList = params.getJSONArray("materialList");
            Long supplierId = params.getLong("supplierId");
            String remark = params.getString("remark");
            Date expectedDate = params.getDate("expectedDate");

            // 创建采购单头
            JSONObject depotHeadData = new JSONObject();
            depotHeadData.put("type", BusinessConstants.DEPOTHEAD_TYPE_IN);
            depotHeadData.put("subType", "采购入库");
            depotHeadData.put("number", generatePurchaseNumber());
            depotHeadData.put("operTime", new Date());
            depotHeadData.put("organId", supplierId);
            depotHeadData.put("remark", "生产采购：" + (remark != null ? remark : "自动生成"));
            depotHeadData.put("status", "0"); // 未审核

            // 创建采购单明细
            JSONArray itemList = new JSONArray();
            BigDecimal totalAmount = BigDecimal.ZERO;

            for (int i = 0; i < materialList.size(); i++) {
                JSONObject material = materialList.getJSONObject(i);

                JSONObject item = new JSONObject();
                item.put("materialId", material.getLong("materialId"));
                item.put("operNumber", material.getBigDecimal("quantity"));
                item.put("unitPrice", material.getBigDecimal("unitPrice"));
                item.put("allPrice", material.getBigDecimal("quantity").multiply(material.getBigDecimal("unitPrice")));
                item.put("depotId", material.getLong("depotId"));
                item.put("remark", "生产采购");

                itemList.add(item);
                totalAmount = totalAmount.add(item.getBigDecimal("allPrice"));
            }

            depotHeadData.put("totalPrice", totalAmount);
            depotHeadData.put("info", itemList.toJSONString());

            // 调用库存服务创建采购单
            int result = depotHeadService.insertDepotHead(depotHeadData, request);

            if (result > 0) {
                logger.info("生产采购单创建成功：供应商ID {}, 单据编号 {}", supplierId, depotHeadData.getString("number"));
                return depotHeadData.getString("number");
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "创建采购单失败");
            }

        } catch (Exception e) {
            logger.error("生产采购单创建失败", e);
            throw e;
        }
    }

    /**
     * 获取推荐供应商
     * 根据历史采购记录推荐最优供应商
     */
    public JSONArray getRecommendedSuppliers(Long materialId) throws Exception {
        JSONArray suppliers = new JSONArray();

        try {
            // 获取该商品的历史采购记录，按供应商统计
            // TODO: 实现获取供应商统计的逻辑
            List<JSONObject> supplierStats = new ArrayList<>();

            for (JSONObject stat : supplierStats) {
                JSONObject supplier = new JSONObject();
                supplier.put("supplierId", stat.getLong("supplierId"));
                supplier.put("supplierName", stat.getString("supplierName"));
                supplier.put("avgPrice", stat.getBigDecimal("avgPrice"));
                supplier.put("totalQuantity", stat.getBigDecimal("totalQuantity"));
                supplier.put("orderCount", stat.getInteger("orderCount"));
                supplier.put("lastOrderDate", stat.getDate("lastOrderDate"));

                // 计算供应商评分（价格权重40%，数量权重30%，频次权重30%）
                BigDecimal score = calculateSupplierScore(stat);
                supplier.put("score", score);

                suppliers.add(supplier);
            }

            // 按评分排序
            suppliers.sort((o1, o2) -> {
                JSONObject s1 = (JSONObject) o1;
                JSONObject s2 = (JSONObject) o2;
                return s2.getBigDecimal("score").compareTo(s1.getBigDecimal("score"));
            });

        } catch (Exception e) {
            logger.error("获取推荐供应商失败：商品ID {}", materialId, e);
        }

        return suppliers;
    }

    /**
     * 计算供应商评分
     */
    private BigDecimal calculateSupplierScore(JSONObject stat) {
        // 简化的评分算法，实际可以更复杂
        BigDecimal priceScore = BigDecimal.valueOf(100).subtract(stat.getBigDecimal("avgPrice").multiply(BigDecimal.valueOf(0.1)));
        BigDecimal quantityScore = stat.getBigDecimal("totalQuantity").multiply(BigDecimal.valueOf(0.01));
        BigDecimal frequencyScore = BigDecimal.valueOf(stat.getInteger("orderCount")).multiply(BigDecimal.valueOf(5));

        return priceScore.multiply(BigDecimal.valueOf(0.4))
                .add(quantityScore.multiply(BigDecimal.valueOf(0.3)))
                .add(frequencyScore.multiply(BigDecimal.valueOf(0.3)));
    }

    // ==================== 财务管理集成 ====================

    /**
     * 推送生产成本到财务系统
     * 根据生产任务完成情况，自动计算并推送成本数据
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String pushProductionCost(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            String taskNumber = params.getString("taskNumber");
            BigDecimal materialCost = params.getBigDecimal("materialCost");
            BigDecimal laborCost = params.getBigDecimal("laborCost");
            BigDecimal overheadCost = params.getBigDecimal("overheadCost");
            BigDecimal totalCost = materialCost.add(laborCost).add(overheadCost);

            // 创建财务单据头
            JSONObject accountHeadData = new JSONObject();
            accountHeadData.put("type", "生产成本");
            accountHeadData.put("billNo", generateCostNumber());
            accountHeadData.put("billTime", new Date());
            accountHeadData.put("totalPrice", totalCost);
            accountHeadData.put("remark", "生产任务成本：" + taskNumber);
            accountHeadData.put("status", "0"); // 未审核

            // 创建财务单据明细
            JSONArray itemList = new JSONArray();

            // 材料成本明细
            if (materialCost.compareTo(BigDecimal.ZERO) > 0) {
                JSONObject materialItem = new JSONObject();
                materialItem.put("accountId", getMaterialCostAccountId());
                materialItem.put("inOutItem", "支出");
                materialItem.put("eachAmount", materialCost);
                materialItem.put("remark", "材料成本");
                itemList.add(materialItem);
            }

            // 人工成本明细
            if (laborCost.compareTo(BigDecimal.ZERO) > 0) {
                JSONObject laborItem = new JSONObject();
                laborItem.put("accountId", getLaborCostAccountId());
                laborItem.put("inOutItem", "支出");
                laborItem.put("eachAmount", laborCost);
                laborItem.put("remark", "人工成本");
                itemList.add(laborItem);
            }

            // 制造费用明细
            if (overheadCost.compareTo(BigDecimal.ZERO) > 0) {
                JSONObject overheadItem = new JSONObject();
                overheadItem.put("accountId", getOverheadCostAccountId());
                overheadItem.put("inOutItem", "支出");
                overheadItem.put("eachAmount", overheadCost);
                overheadItem.put("remark", "制造费用");
                itemList.add(overheadItem);
            }

            accountHeadData.put("info", itemList.toJSONString());

            // 调用财务服务创建成本单据
            int result = accountHeadService.insertAccountHead(accountHeadData, request);

            if (result > 0) {
                logger.info("生产成本推送成功：任务ID {}, 单据编号 {}, 总成本 {}", taskId, accountHeadData.getString("billNo"), totalCost);
                return accountHeadData.getString("billNo");
            } else {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "推送生产成本失败");
            }

        } catch (Exception e) {
            logger.error("推送生产成本失败", e);
            throw e;
        }
    }

    /**
     * 计算生产任务总成本
     */
    public JSONObject calculateProductionCost(Long taskId) throws Exception {
        JSONObject result = new JSONObject();

        try {
            // 计算材料成本
            BigDecimal materialCost = calculateMaterialCost(taskId);

            // 计算人工成本
            BigDecimal laborCost = calculateLaborCost(taskId);

            // 计算制造费用
            BigDecimal overheadCost = calculateOverheadCost(taskId);

            // 计算总成本
            BigDecimal totalCost = materialCost.add(laborCost).add(overheadCost);

            result.put("materialCost", materialCost);
            result.put("laborCost", laborCost);
            result.put("overheadCost", overheadCost);
            result.put("totalCost", totalCost);

            // 计算单位成本
            // TODO: 从生产任务中获取完工数量
            BigDecimal completedQuantity = BigDecimal.ONE; // 临时值
            BigDecimal unitCost = totalCost.divide(completedQuantity, 2, RoundingMode.HALF_UP);
            result.put("unitCost", unitCost);

            logger.info("生产成本计算完成：任务ID {}, 总成本 {}", taskId, totalCost);

        } catch (Exception e) {
            logger.error("计算生产成本失败：任务ID {}", taskId, e);
            throw e;
        }

        return result;
    }

    /**
     * 计算材料成本
     */
    private BigDecimal calculateMaterialCost(Long taskId) throws Exception {
        // TODO: 根据任务ID查询材料消耗记录，计算材料成本
        return BigDecimal.ZERO;
    }

    /**
     * 计算人工成本
     */
    private BigDecimal calculateLaborCost(Long taskId) throws Exception {
        // TODO: 根据任务ID查询工时记录，计算人工成本
        return BigDecimal.ZERO;
    }

    /**
     * 计算制造费用
     */
    private BigDecimal calculateOverheadCost(Long taskId) throws Exception {
        // TODO: 根据任务ID和制造费用分摊规则，计算制造费用
        return BigDecimal.ZERO;
    }

    // ==================== 更多辅助方法 ====================

    /**
     * 生成采购单号
     */
    private String generatePurchaseNumber() {
        return "CG" + Tools.getNowTime() + Tools.getCharAndNum(4);
    }

    /**
     * 生成成本单号
     */
    private String generateCostNumber() {
        return "CB" + Tools.getNowTime() + Tools.getCharAndNum(4);
    }

    /**
     * 获取材料成本科目ID
     */
    private Long getMaterialCostAccountId() {
        // TODO: 从系统配置中获取材料成本科目ID
        return 1L;
    }

    /**
     * 获取人工成本科目ID
     */
    private Long getLaborCostAccountId() {
        // TODO: 从系统配置中获取人工成本科目ID
        return 2L;
    }

    /**
     * 获取制造费用科目ID
     */
    private Long getOverheadCostAccountId() {
        // TODO: 从系统配置中获取制造费用科目ID
        return 3L;
    }
}
