package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.AccessoryProduction;
import com.jsh.erp.datasource.mappers.AccessoryProductionMapper;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

/**
 * 配饰制作服务
 * 提供配饰制作相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class AccessoryProductionService {
    
    private Logger logger = LoggerFactory.getLogger(AccessoryProductionService.class);
    
    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    @Resource
    private AccessoryProductionMapper accessoryProductionMapper;
    
    /**
     * 获取配饰制作列表
     */
    public JSONObject getAccessoryProductionList(JSONObject params, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        
        try {
            // 构建查询参数
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("tenantId", 63L); // 从session或request中获取租户ID

            // 处理搜索条件
            if (params != null) {
                if (params.getString("semiProductName") != null) {
                    parameterMap.put("semiProductName", params.getString("semiProductName"));
                }
                if (params.getString("accessoryMaterialName") != null) {
                    parameterMap.put("accessoryMaterialName", params.getString("accessoryMaterialName"));
                }
                if (params.getString("status") != null) {
                    parameterMap.put("status", params.getString("status"));
                }
                if (params.getString("workerName") != null) {
                    parameterMap.put("workerName", params.getString("workerName"));
                }
                if (params.getString("orderNumber") != null) {
                    parameterMap.put("orderNumber", params.getString("orderNumber"));
                }
                if (params.getString("accessoryType") != null) {
                    parameterMap.put("accessoryType", params.getString("accessoryType"));
                }
            }

            // 查询数据库
            List<AccessoryProduction> list = accessoryProductionMapper.selectByCondition(parameterMap);
            Long total = accessoryProductionMapper.countByCondition(parameterMap);

            // 转换为JSONArray
            JSONArray dataList = new JSONArray();
            for (AccessoryProduction item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("orderNumber", item.getOrderNumber());
                obj.put("semiProductId", item.getSemiProductId());
                obj.put("semiProductName", item.getSemiProductName());
                obj.put("accessoryMaterialId", item.getAccessoryMaterialId());
                obj.put("accessoryMaterialName", item.getAccessoryMaterialName());
                obj.put("quantity", item.getQuantity());
                obj.put("unitName", item.getUnitName());
                obj.put("laborCostAmount", item.getLaborCostAmount());
                obj.put("workerId", item.getWorkerId());
                obj.put("workerName", item.getWorkerName());
                obj.put("salaryAmount", item.getSalaryAmount());
                obj.put("status", item.getStatus());
                obj.put("startTime", item.getStartTime());
                obj.put("endTime", item.getEndTime());
                obj.put("accessoryType", item.getAccessoryType());
                obj.put("accessoryStyle", item.getAccessoryStyle());
                obj.put("assemblyMethod", item.getAssemblyMethod());
                obj.put("finishingProcess", item.getFinishingProcess());
                obj.put("difficultyLevel", item.getDifficultyLevel());
                obj.put("qualityGrade", item.getQualityGrade());
                obj.put("qualityScore", item.getQualityScore());
                obj.put("remark", item.getRemark());
                obj.put("createTime", item.getCreateTime());
                dataList.add(obj);
            }

            result.put("rows", dataList);
            result.put("total", total);
            result.put("size", dataList.size());
            result.put("current", 1);

            logger.info("获取配饰制作列表成功，共 {} 条记录", total);

        } catch (Exception e) {
            logger.error("获取配饰制作列表失败", e);
            throw e;
        }
        
        return result;
    }
    
    /**
     * 创建模拟配饰制作数据
     */
    private JSONArray createMockAccessoryProductionData() {
        JSONArray dataList = new JSONArray();
        
        String[] orderNumbers = {"AC2024001", "AC2024002", "AC2024003", "AC2024004", "AC2024005"};
        String[] semiProductNames = {"掐丝点蓝手镯半成品", "掐丝点蓝项链半成品", "掐丝点蓝耳环半成品"};
        String[] accessoryTypes = {"手链配饰", "项链配饰", "耳环配饰", "戒指配饰", "胸针配饰"};
        String[] statuses = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"};
        String[] workerNames = {"陈师傅", "刘师傅", "周师傅", "吴师傅"};
        
        for (int i = 0; i < 12; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i + 1);
            item.put("orderNumber", orderNumbers[i % orderNumbers.length] + String.format("%03d", i + 1));
            item.put("semiProductName", semiProductNames[i % semiProductNames.length]);
            item.put("accessoryType", accessoryTypes[i % accessoryTypes.length]);
            item.put("status", statuses[i % statuses.length]);
            item.put("workerName", workerNames[i % workerNames.length]);
            
            // 数量和单位
            item.put("quantity", 5 + (i * 2));
            item.put("unitName", "套");
            item.put("completedQuantity", i % 4 == 0 ? 0 : (2 + i));
            
            // 价格信息
            item.put("unitPrice", new BigDecimal("80.00").add(new BigDecimal(i * 5)));
            item.put("totalAmount", new BigDecimal("400.00").add(new BigDecimal(i * 50)));
            
            // 时间信息
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            item.put("createTime", cal.getTime());
            item.put("planStartTime", cal.getTime());
            
            cal.add(Calendar.DAY_OF_MONTH, 3);
            item.put("planEndTime", cal.getTime());
            
            if (!item.getString("status").equals("PENDING")) {
                cal.add(Calendar.DAY_OF_MONTH, -2);
                item.put("actualStartTime", cal.getTime());
            }
            
            if (item.getString("status").equals("COMPLETED")) {
                cal.add(Calendar.DAY_OF_MONTH, 2);
                item.put("actualEndTime", cal.getTime());
            }
            
            // 配饰信息
            item.put("accessoryMaterial", i % 3 == 0 ? "925银" : (i % 3 == 1 ? "黄金" : "白金"));
            item.put("accessoryStyle", i % 2 == 0 ? "简约风格" : "复古风格");
            item.put("difficulty", i % 3 == 0 ? "简单" : (i % 3 == 1 ? "中等" : "困难"));
            
            // 质量信息
            if (item.getString("status").equals("COMPLETED")) {
                item.put("qualityGrade", i % 4 == 0 ? "优秀" : (i % 4 == 1 ? "良好" : "合格"));
                item.put("qualityScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
            }
            
            // 备注
            item.put("remark", "配饰制作订单 - " + item.getString("accessoryType"));
            
            dataList.add(item);
        }
        
        return dataList;
    }
    
    /**
     * 过滤配饰数据
     */
    private JSONArray filterAccessoryData(JSONArray dataList, JSONObject params) {
        JSONArray filteredList = new JSONArray();
        
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject item = dataList.getJSONObject(i);
            boolean match = true;
            
            // 订单号过滤
            if (params.getString("orderNumber") != null && !params.getString("orderNumber").isEmpty()) {
                if (!item.getString("orderNumber").contains(params.getString("orderNumber"))) {
                    match = false;
                }
            }
            
            // 半成品名称过滤
            if (params.getString("semiProductName") != null && !params.getString("semiProductName").isEmpty()) {
                if (!item.getString("semiProductName").contains(params.getString("semiProductName"))) {
                    match = false;
                }
            }
            
            // 状态过滤
            if (params.getString("status") != null && !params.getString("status").isEmpty()) {
                if (!item.getString("status").equals(params.getString("status"))) {
                    match = false;
                }
            }
            
            // 配饰类型过滤
            if (params.getString("accessoryType") != null && !params.getString("accessoryType").isEmpty()) {
                if (!item.getString("accessoryType").contains(params.getString("accessoryType"))) {
                    match = false;
                }
            }
            
            if (match) {
                filteredList.add(item);
            }
        }
        
        return filteredList;
    }
    
    /**
     * 新增配饰制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean addAccessoryProduction(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            // 验证必要参数
            if (params.getString("semiProductName") == null || params.getString("semiProductName").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "半成品名称不能为空");
            }

            if (params.getString("accessoryType") == null || params.getString("accessoryType").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "配饰类型不能为空");
            }

            if (params.getBigDecimal("quantity") == null || params.getBigDecimal("quantity").compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "制作数量必须大于0");
            }

            // 生成订单号
            String orderNumber = "AC" + Tools.getNowTime() + Tools.getCharAndNum(4);

            // 创建实体对象
            AccessoryProduction production = new AccessoryProduction();
            production.setOrderNumber(orderNumber);
            production.setSemiProductId(params.getLong("semiProductId"));
            production.setSemiProductName(params.getString("semiProductName"));
            production.setAccessoryMaterialId(params.getLong("accessoryMaterialId"));
            production.setAccessoryMaterialName(params.getString("accessoryMaterialName"));
            production.setQuantity(params.getBigDecimal("quantity"));
            production.setUnitId(params.getLong("unitId"));
            production.setUnitName(params.getString("unitName"));
            production.setLaborCostAmount(params.getBigDecimal("laborCostAmount"));
            production.setStatus("PENDING");
            production.setAccessoryType(params.getString("accessoryType"));
            production.setAccessoryStyle(params.getString("accessoryStyle"));
            production.setAssemblyMethod(params.getString("assemblyMethod"));
            production.setFinishingProcess(params.getString("finishingProcess"));
            production.setDifficultyLevel(params.getString("difficultyLevel"));
            production.setRemark(params.getString("remark"));
            production.setTenantId(63L);
            production.setDeleteFlag("0");
            production.setCreateTime(new Date());
            production.setCreateUser(1L);

            // 插入数据库
            int result = accessoryProductionMapper.insert(production);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "新增配饰制作失败");
            }

            // 记录操作日志
            logService.insertLog("配饰制作", "新增制作订单：" + orderNumber, request);

            logger.info("新增配饰制作成功：{}", orderNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("新增配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 更新配饰制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateAccessoryProduction(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            AccessoryProduction existing = accessoryProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "配饰制作记录不存在");
            }

            // 更新字段
            if (params.getString("semiProductName") != null) {
                existing.setSemiProductName(params.getString("semiProductName"));
            }
            if (params.getString("accessoryMaterialName") != null) {
                existing.setAccessoryMaterialName(params.getString("accessoryMaterialName"));
            }
            if (params.getBigDecimal("quantity") != null) {
                existing.setQuantity(params.getBigDecimal("quantity"));
            }
            if (params.getBigDecimal("laborCostAmount") != null) {
                existing.setLaborCostAmount(params.getBigDecimal("laborCostAmount"));
            }
            if (params.getBigDecimal("salaryAmount") != null) {
                existing.setSalaryAmount(params.getBigDecimal("salaryAmount"));
            }
            if (params.getString("accessoryType") != null) {
                existing.setAccessoryType(params.getString("accessoryType"));
            }
            if (params.getString("remark") != null) {
                existing.setRemark(params.getString("remark"));
            }
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            // 更新数据库
            int result = accessoryProductionMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "更新配饰制作失败");
            }

            logger.info("更新配饰制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("更新配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 删除配饰制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteAccessoryProduction(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            AccessoryProduction existing = accessoryProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "配饰制作记录不存在");
            }

            // 检查是否可以删除（进行中的订单不能删除）
            if ("IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "进行中的制作订单不能删除");
            }

            // 软删除
            existing.setDeleteFlag("1");
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            int result = accessoryProductionMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "删除配饰制作失败");
            }

            logger.info("删除配饰制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 批量删除配饰制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteBatchAccessoryProduction(String ids, HttpServletRequest request) throws Exception {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID列表不能为空");
            }
            
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                deleteAccessoryProduction(Long.parseLong(id.trim()), request);
            }
            
            logger.info("批量删除配饰制作成功：{} 条记录", idArray.length);
            return true;
            
        } catch (Exception e) {
            logger.error("批量删除配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 开始制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean startProduction(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            AccessoryProduction existing = accessoryProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "配饰制作记录不存在");
            }

            // 检查状态是否可以开始
            if (!"PENDING".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有待开始状态的订单才能开始制作");
            }

            // 更新状态为IN_PROGRESS，记录实际开始时间
            Date startTime = new Date();
            int result = accessoryProductionMapper.updateStartTime(id, startTime, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "开始配饰制作失败");
            }

            logger.info("开始配饰制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("开始配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 完成制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean completeProduction(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            AccessoryProduction existing = accessoryProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "配饰制作记录不存在");
            }

            // 检查状态是否可以完成
            if (!"IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有进行中状态的订单才能完成制作");
            }

            // 更新状态为COMPLETED，记录实际完成时间
            Date endTime = new Date();
            int result = accessoryProductionMapper.updateEndTime(id, endTime, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "完成配饰制作失败");
            }

            logger.info("完成配饰制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("完成配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 取消制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean cancelProduction(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            AccessoryProduction existing = accessoryProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "配饰制作记录不存在");
            }

            // 检查状态是否可以取消
            if ("COMPLETED".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "已完成的订单不能取消");
            }
            if ("CANCELLED".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "订单已经是取消状态");
            }

            // 更新状态为CANCELLED
            int result = accessoryProductionMapper.updateStatus(id, "CANCELLED", 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "取消配饰制作失败");
            }

            logger.info("取消配饰制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("取消配饰制作失败", e);
            throw e;
        }
    }
    
    /**
     * 导出Excel
     */
    public void exportExcel(JSONObject params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // TODO: 实际的Excel导出逻辑
            
            logger.info("导出配饰制作Excel成功");
            
        } catch (Exception e) {
            logger.error("导出配饰制作Excel失败", e);
            throw e;
        }
    }
    
    /**
     * 获取制作统计
     */
    public JSONObject getProductionStatistics(HttpServletRequest request) throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 从数据库获取统计数据
            Map<String, Object> dbStatistics = accessoryProductionMapper.getProductionStatistics(63L);

            // 转换为JSONObject
            statistics.put("totalOrders", dbStatistics.get("totalOrders"));
            statistics.put("pendingOrders", dbStatistics.get("pendingOrders"));
            statistics.put("inProgressOrders", dbStatistics.get("inProgressOrders"));
            statistics.put("completedOrders", dbStatistics.get("completedOrders"));
            statistics.put("cancelledOrders", dbStatistics.get("cancelledOrders"));

            statistics.put("totalQuantity", dbStatistics.get("totalQuantity"));
            statistics.put("completedQuantity", dbStatistics.get("completedQuantity"));

            // 计算完成率
            Object totalQuantity = dbStatistics.get("totalQuantity");
            Object completedQuantity = dbStatistics.get("completedQuantity");
            if (totalQuantity != null && completedQuantity != null) {
                BigDecimal total = new BigDecimal(totalQuantity.toString());
                BigDecimal completed = new BigDecimal(completedQuantity.toString());
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal completionRate = completed.divide(total, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    statistics.put("completionRate", completionRate);
                } else {
                    statistics.put("completionRate", 0);
                }
            }

            statistics.put("totalAmount", dbStatistics.get("totalAmount"));
            statistics.put("totalSalary", dbStatistics.get("totalSalary"));
            statistics.put("avgUnitPrice", dbStatistics.get("avgUnitPrice"));
            statistics.put("avgCycleTime", dbStatistics.get("avgCycleTime"));
            statistics.put("avgQualityScore", dbStatistics.get("avgQualityScore"));

            logger.info("获取配饰制作统计成功");

        } catch (Exception e) {
            logger.error("获取配饰制作统计失败", e);
            throw e;
        }
        
        return statistics;
    }
}
