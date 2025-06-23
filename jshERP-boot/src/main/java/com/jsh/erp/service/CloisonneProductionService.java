package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.CloisonneProduction;
import com.jsh.erp.datasource.mappers.CloisonneProductionMapper;
import com.jsh.erp.utils.Tools;
import com.jsh.erp.utils.StringUtil;
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
 * 掐丝点蓝制作服务
 * 提供掐丝点蓝制作相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class CloisonneProductionService {

    private Logger logger = LoggerFactory.getLogger(CloisonneProductionService.class);

    @Resource
    private LogService logService;

    @Resource
    private CloisonneProductionMapper cloisonneProductionMapper;
    
    @Resource
    private UserService userService;
    
    /**
     * 获取掐丝点蓝制作列表
     */
    public JSONObject getCloisonneProductionList(JSONObject params, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();

        try {
            // 构建查询参数
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("tenantId", 63L); // 从session或request中获取租户ID

            // 处理搜索条件
            if (params != null) {
                if (params.getString("materialName") != null) {
                    parameterMap.put("materialName", params.getString("materialName"));
                }
                if (params.getString("status") != null) {
                    parameterMap.put("status", params.getString("status"));
                }
                if (params.getString("supplierName") != null) {
                    parameterMap.put("supplierName", params.getString("supplierName"));
                }
                if (params.getString("orderNumber") != null) {
                    parameterMap.put("orderNumber", params.getString("orderNumber"));
                }
            }

            // 查询数据库
            List<CloisonneProduction> list = cloisonneProductionMapper.selectByCondition(parameterMap);
            Long total = cloisonneProductionMapper.countByCondition(parameterMap);

            // 转换为JSONArray
            JSONArray dataList = new JSONArray();
            for (CloisonneProduction item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("orderNumber", item.getOrderNumber());
                obj.put("materialId", item.getMaterialId());
                obj.put("materialName", item.getMaterialName());
                obj.put("quantity", item.getQuantity());
                obj.put("unitName", item.getUnitName());
                obj.put("laborCostAmount", item.getLaborCostAmount());
                obj.put("supplierId", item.getSupplierId());
                obj.put("supplierName", item.getSupplierName());
                obj.put("status", item.getStatus());
                obj.put("startTime", item.getStartTime());
                obj.put("endTime", item.getEndTime());
                obj.put("craftType", item.getCraftType());
                obj.put("colorScheme", item.getColorScheme());
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

            logger.info("获取掐丝点蓝制作列表成功，共 {} 条记录", total);

        } catch (Exception e) {
            logger.error("获取掐丝点蓝制作列表失败", e);
            throw e;
        }

        return result;
    }
    
    /**
     * 创建模拟掐丝点蓝制作数据
     */
    private JSONArray createMockCloisonneProductionData() {
        JSONArray dataList = new JSONArray();
        
        String[] orderNumbers = {"CL2024001", "CL2024002", "CL2024003", "CL2024004", "CL2024005"};
        String[] materialNames = {"掐丝点蓝手镯", "掐丝点蓝项链", "掐丝点蓝耳环", "掐丝点蓝戒指", "掐丝点蓝胸针"};
        String[] statuses = {"PENDING", "IN_PROGRESS", "COMPLETED", "CANCELLED"};
        String[] supplierNames = {"北京景泰蓝厂", "天津掐丝工艺厂", "河北点蓝制品厂"};
        String[] workerNames = {"张师傅", "李师傅", "王师傅", "赵师傅"};
        
        for (int i = 0; i < 15; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i + 1);
            item.put("orderNumber", orderNumbers[i % orderNumbers.length] + String.format("%03d", i + 1));
            item.put("materialName", materialNames[i % materialNames.length]);
            item.put("status", statuses[i % statuses.length]);
            item.put("supplierName", supplierNames[i % supplierNames.length]);
            item.put("workerName", workerNames[i % workerNames.length]);
            
            // 数量和单位
            item.put("quantity", 10 + (i * 2));
            item.put("unitName", "个");
            item.put("completedQuantity", i % 4 == 0 ? 0 : (5 + i));
            
            // 价格信息
            item.put("unitPrice", new BigDecimal("150.00").add(new BigDecimal(i * 10)));
            item.put("totalAmount", new BigDecimal("1500.00").add(new BigDecimal(i * 100)));
            
            // 时间信息
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            item.put("createTime", cal.getTime());
            item.put("planStartTime", cal.getTime());
            
            cal.add(Calendar.DAY_OF_MONTH, 7);
            item.put("planEndTime", cal.getTime());
            
            if (!item.getString("status").equals("PENDING")) {
                cal.add(Calendar.DAY_OF_MONTH, -5);
                item.put("actualStartTime", cal.getTime());
            }
            
            if (item.getString("status").equals("COMPLETED")) {
                cal.add(Calendar.DAY_OF_MONTH, 6);
                item.put("actualEndTime", cal.getTime());
            }
            
            // 工艺信息
            item.put("craftType", i % 2 == 0 ? "传统掐丝" : "现代掐丝");
            item.put("colorScheme", i % 3 == 0 ? "蓝色系" : (i % 3 == 1 ? "绿色系" : "红色系"));
            item.put("difficulty", i % 4 == 0 ? "简单" : (i % 4 == 1 ? "中等" : (i % 4 == 2 ? "困难" : "专家级")));
            
            // 质量信息
            if (item.getString("status").equals("COMPLETED")) {
                item.put("qualityGrade", i % 5 == 0 ? "优秀" : (i % 5 == 1 ? "良好" : "合格"));
                item.put("qualityScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
            }
            
            // 备注
            item.put("remark", "掐丝点蓝制作订单 - " + item.getString("materialName"));
            
            dataList.add(item);
        }
        
        return dataList;
    }
    
    /**
     * 过滤掐丝点蓝数据
     */
    private JSONArray filterCloisonneData(JSONArray dataList, JSONObject params) {
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
            
            // 商品名称过滤
            if (params.getString("materialName") != null && !params.getString("materialName").isEmpty()) {
                if (!item.getString("materialName").contains(params.getString("materialName"))) {
                    match = false;
                }
            }
            
            // 状态过滤
            if (params.getString("status") != null && !params.getString("status").isEmpty()) {
                if (!item.getString("status").equals(params.getString("status"))) {
                    match = false;
                }
            }
            
            // 供应商过滤
            if (params.getString("supplierName") != null && !params.getString("supplierName").isEmpty()) {
                if (!item.getString("supplierName").contains(params.getString("supplierName"))) {
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
     * 新增掐丝点蓝制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean addCloisonneProduction(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            // 验证必要参数
            if (params.getString("materialName") == null || params.getString("materialName").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "商品名称不能为空");
            }

            if (params.getBigDecimal("quantity") == null || params.getBigDecimal("quantity").compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "制作数量必须大于0");
            }

            // 生成订单号
            String orderNumber = "CL" + Tools.getNowTime() + Tools.getCharAndNum(4);

            // 创建实体对象
            CloisonneProduction production = new CloisonneProduction();
            production.setOrderNumber(orderNumber);
            production.setMaterialId(params.getLong("materialId"));
            production.setMaterialName(params.getString("materialName"));
            production.setQuantity(params.getBigDecimal("quantity"));
            production.setUnitId(params.getLong("unitId"));
            production.setLaborCostAmount(params.getBigDecimal("laborCostAmount"));
            production.setSupplierId(params.getLong("supplierId"));
            production.setSupplierName(params.getString("supplierName"));
            production.setStatus("PENDING");
            production.setRemark(params.getString("remark"));
            production.setTenantId(63L);
            production.setDeleteFlag("0");
            production.setCreateTime(new Date());
            production.setCreateUser(1L);

            // 插入数据库
            int result = cloisonneProductionMapper.insert(production);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "新增掐丝点蓝制作失败");
            }

            // 记录操作日志
            logService.insertLog("掐丝点蓝制作", "新增制作订单：" + orderNumber, request);

            logger.info("新增掐丝点蓝制作成功：{}", orderNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("新增掐丝点蓝制作失败", e);
            throw e;
        }
    }
    
    /**
     * 更新掐丝点蓝制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateCloisonneProduction(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            CloisonneProduction existing = cloisonneProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "掐丝点蓝制作记录不存在");
            }

            // 更新字段
            if (params.getString("materialName") != null) {
                existing.setMaterialName(params.getString("materialName"));
            }
            if (params.getBigDecimal("quantity") != null) {
                existing.setQuantity(params.getBigDecimal("quantity"));
            }
            if (params.getBigDecimal("laborCostAmount") != null) {
                existing.setLaborCostAmount(params.getBigDecimal("laborCostAmount"));
            }
            if (params.getString("supplierName") != null) {
                existing.setSupplierName(params.getString("supplierName"));
            }
            if (params.getString("remark") != null) {
                existing.setRemark(params.getString("remark"));
            }
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            // 更新数据库
            int result = cloisonneProductionMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "更新掐丝点蓝制作失败");
            }

            logger.info("更新掐丝点蓝制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("更新掐丝点蓝制作失败", e);
            throw e;
        }
    }
    
    /**
     * 删除掐丝点蓝制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteCloisonneProduction(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            CloisonneProduction existing = cloisonneProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "掐丝点蓝制作记录不存在");
            }

            // 检查是否可以删除（进行中的订单不能删除）
            if ("IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "进行中的制作订单不能删除");
            }

            // 软删除
            existing.setDeleteFlag("1");
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            int result = cloisonneProductionMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "删除掐丝点蓝制作失败");
            }

            logger.info("删除掐丝点蓝制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除掐丝点蓝制作失败", e);
            throw e;
        }
    }
    
    /**
     * 批量删除掐丝点蓝制作
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteBatchCloisonneProduction(String ids, HttpServletRequest request) throws Exception {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID列表不能为空");
            }
            
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                deleteCloisonneProduction(Long.parseLong(id.trim()), request);
            }
            
            logger.info("批量删除掐丝点蓝制作成功：{} 条记录", idArray.length);
            return true;
            
        } catch (Exception e) {
            logger.error("批量删除掐丝点蓝制作失败", e);
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
            CloisonneProduction existing = cloisonneProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "掐丝点蓝制作记录不存在");
            }

            // 检查状态是否可以开始
            if (!"PENDING".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有待开始状态的订单才能开始制作");
            }

            // 更新状态为IN_PROGRESS，记录实际开始时间
            Date startTime = new Date();
            int result = cloisonneProductionMapper.updateStartTime(id, startTime, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "开始掐丝点蓝制作失败");
            }

            logger.info("开始掐丝点蓝制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("开始掐丝点蓝制作失败", e);
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
            CloisonneProduction existing = cloisonneProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "掐丝点蓝制作记录不存在");
            }

            // 检查状态是否可以完成
            if (!"IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有进行中状态的订单才能完成制作");
            }

            // 更新状态为COMPLETED，记录实际完成时间
            Date endTime = new Date();
            int result = cloisonneProductionMapper.updateEndTime(id, endTime, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "完成掐丝点蓝制作失败");
            }

            logger.info("完成掐丝点蓝制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("完成掐丝点蓝制作失败", e);
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
            CloisonneProduction existing = cloisonneProductionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "掐丝点蓝制作记录不存在");
            }

            // 检查状态是否可以取消
            if ("COMPLETED".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "已完成的订单不能取消");
            }
            if ("CANCELLED".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "订单已经是取消状态");
            }

            // 更新状态为CANCELLED
            int result = cloisonneProductionMapper.updateStatus(id, "CANCELLED", 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "取消掐丝点蓝制作失败");
            }

            logger.info("取消掐丝点蓝制作成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("取消掐丝点蓝制作失败", e);
            throw e;
        }
    }
    
    /**
     * 导出Excel
     */
    public void exportExcel(JSONObject params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // TODO: 实际的Excel导出逻辑
            // 1. 获取数据
            // 2. 创建Excel工作簿
            // 3. 写入数据
            // 4. 设置响应头
            // 5. 输出到响应流
            
            logger.info("导出掐丝点蓝制作Excel成功");
            
        } catch (Exception e) {
            logger.error("导出掐丝点蓝制作Excel失败", e);
            throw e;
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNumber() {
        return "CL" + Tools.getNowTime() + Tools.getCharAndNum(4);
    }

    /**
     * 获取制作统计
     */
    public JSONObject getProductionStatistics(HttpServletRequest request) throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 从数据库获取统计数据
            Map<String, Object> dbStatistics = cloisonneProductionMapper.getProductionStatistics(63L);

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
                    BigDecimal completionRate = completed.divide(total, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
                    statistics.put("completionRate", completionRate);
                } else {
                    statistics.put("completionRate", 0);
                }
            }

            statistics.put("totalAmount", dbStatistics.get("totalAmount"));
            statistics.put("avgUnitPrice", dbStatistics.get("avgUnitPrice"));
            statistics.put("avgCycleTime", dbStatistics.get("avgCycleTime"));
            statistics.put("avgQualityScore", dbStatistics.get("avgQualityScore"));

            logger.info("获取掐丝点蓝制作统计成功");

        } catch (Exception e) {
            logger.error("获取掐丝点蓝制作统计失败", e);
            throw e;
        }
        
        return statistics;
    }
}
