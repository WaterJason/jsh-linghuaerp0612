package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.LogisticsTracking;
import com.jsh.erp.datasource.mappers.LogisticsTrackingMapper;
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
 * 物流追踪服务
 * 提供物流追踪相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class LogisticsTrackingService {
    
    private Logger logger = LoggerFactory.getLogger(LogisticsTrackingService.class);
    
    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    @Resource
    private LogisticsTrackingMapper logisticsTrackingMapper;
    
    /**
     * 获取物流追踪列表
     */
    public JSONObject getLogisticsTrackingList(JSONObject params, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        
        try {
            // 构建查询参数
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("tenantId", 63L); // 从session或request中获取租户ID

            // 处理搜索条件
            if (params != null) {
                if (params.getString("trackingNumber") != null) {
                    parameterMap.put("trackingNumber", params.getString("trackingNumber"));
                }
                if (params.getString("workOrderNumber") != null) {
                    parameterMap.put("workOrderNumber", params.getString("workOrderNumber"));
                }
                if (params.getString("taskNumber") != null) {
                    parameterMap.put("taskNumber", params.getString("taskNumber"));
                }
                if (params.getString("logisticsType") != null) {
                    parameterMap.put("logisticsType", params.getString("logisticsType"));
                }
                if (params.getString("productName") != null) {
                    parameterMap.put("productName", params.getString("productName"));
                }
                if (params.getString("status") != null) {
                    parameterMap.put("status", params.getString("status"));
                }
                if (params.getString("carrierName") != null) {
                    parameterMap.put("carrierName", params.getString("carrierName"));
                }
            }

            // 查询数据库
            List<LogisticsTracking> list = logisticsTrackingMapper.selectByCondition(parameterMap);
            Long total = logisticsTrackingMapper.countByCondition(parameterMap);

            // 转换为JSONArray
            JSONArray dataList = new JSONArray();
            for (LogisticsTracking item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("trackingNumber", item.getTrackingNumber());
                obj.put("workOrderId", item.getWorkOrderId());
                obj.put("workOrderNumber", item.getWorkOrderNumber());
                obj.put("taskId", item.getTaskId());
                obj.put("taskNumber", item.getTaskNumber());
                obj.put("logisticsType", item.getLogisticsType());
                obj.put("productId", item.getProductId());
                obj.put("productName", item.getProductName());
                obj.put("quantity", item.getQuantity());
                obj.put("unitName", item.getUnitName());
                obj.put("fromLocation", item.getFromLocation());
                obj.put("toLocation", item.getToLocation());
                obj.put("carrierName", item.getCarrierName());
                obj.put("carrierContact", item.getCarrierContact());
                obj.put("trackingCode", item.getTrackingCode());
                obj.put("status", item.getStatus());
                obj.put("shipTime", item.getShipTime());
                obj.put("estimatedArrivalTime", item.getEstimatedArrivalTime());
                obj.put("actualArrivalTime", item.getActualArrivalTime());
                obj.put("receiveTime", item.getReceiveTime());
                obj.put("receiverName", item.getReceiverName());
                obj.put("receiverContact", item.getReceiverContact());
                obj.put("logisticsCost", item.getLogisticsCost());
                obj.put("trackingInfo", item.getTrackingInfo());
                obj.put("exceptionDescription", item.getExceptionDescription());
                obj.put("createTime", item.getCreateTime());
                dataList.add(obj);
            }

            result.put("rows", dataList);
            result.put("total", total);
            result.put("size", dataList.size());
            result.put("current", 1);

            logger.info("获取物流追踪列表成功，共 {} 条记录", total);

        } catch (Exception e) {
            logger.error("获取物流追踪列表失败", e);
            throw e;
        }
        
        return result;
    }
    
    /**
     * 创建模拟物流追踪数据
     */
    private JSONArray createMockLogisticsTrackingData() {
        JSONArray dataList = new JSONArray();
        
        String[] trackingNumbers = {"SF1234567890", "YT9876543210", "ZTO5555666677", "STO1111222233", "YD7777888899"};
        String[] orderNumbers = {"ORD2024001", "ORD2024002", "ORD2024003", "ORD2024004", "ORD2024005"};
        String[] logisticsCompanies = {"顺丰速运", "圆通速递", "中通快递", "申通快递", "韵达快递"};
        String[] statuses = {"PENDING", "PICKED_UP", "IN_TRANSIT", "DELIVERED", "EXCEPTION"};
        String[] recipientNames = {"张三", "李四", "王五", "赵六", "钱七"};
        String[] recipientPhones = {"13800138001", "13800138002", "13800138003", "13800138004", "13800138005"};
        String[] addresses = {"北京市朝阳区", "上海市浦东新区", "广州市天河区", "深圳市南山区", "杭州市西湖区"};
        
        for (int i = 0; i < 20; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i + 1);
            item.put("trackingNumber", trackingNumbers[i % trackingNumbers.length] + String.format("%03d", i + 1));
            item.put("orderNumber", orderNumbers[i % orderNumbers.length] + String.format("%03d", i + 1));
            item.put("logisticsCompany", logisticsCompanies[i % logisticsCompanies.length]);
            item.put("status", statuses[i % statuses.length]);
            item.put("recipientName", recipientNames[i % recipientNames.length]);
            item.put("recipientPhone", recipientPhones[i % recipientPhones.length]);
            item.put("recipientAddress", addresses[i % addresses.length] + "某某街道" + (i + 1) + "号");
            
            // 物流信息
            item.put("weight", new BigDecimal("0.5").add(new BigDecimal(i * 0.1)));
            item.put("volume", new BigDecimal("0.01").add(new BigDecimal(i * 0.001)));
            item.put("shippingFee", new BigDecimal("15.00").add(new BigDecimal(i * 2)));
            
            // 时间信息
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            item.put("createTime", cal.getTime());
            item.put("shipTime", cal.getTime());
            
            if (!item.getString("status").equals("PENDING")) {
                cal.add(Calendar.HOUR_OF_DAY, 2);
                item.put("pickupTime", cal.getTime());
            }
            
            if (item.getString("status").equals("DELIVERED")) {
                cal.add(Calendar.DAY_OF_MONTH, 2);
                item.put("deliveryTime", cal.getTime());
                item.put("signedBy", item.getString("recipientName"));
            }
            
            if (item.getString("status").equals("IN_TRANSIT")) {
                item.put("currentLocation", "运输途中 - " + addresses[(i + 2) % addresses.length]);
                item.put("estimatedDelivery", new Date(cal.getTimeInMillis() + 24 * 60 * 60 * 1000));
            }
            
            // 备注
            item.put("remark", "物流订单 - " + item.getString("logisticsCompany"));
            
            dataList.add(item);
        }
        
        return dataList;
    }
    
    /**
     * 过滤物流数据
     */
    private JSONArray filterLogisticsData(JSONArray dataList, JSONObject params) {
        JSONArray filteredList = new JSONArray();
        
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject item = dataList.getJSONObject(i);
            boolean match = true;
            
            // 快递单号过滤
            if (params.getString("trackingNumber") != null && !params.getString("trackingNumber").isEmpty()) {
                if (!item.getString("trackingNumber").contains(params.getString("trackingNumber"))) {
                    match = false;
                }
            }
            
            // 订单号过滤
            if (params.getString("orderNumber") != null && !params.getString("orderNumber").isEmpty()) {
                if (!item.getString("orderNumber").contains(params.getString("orderNumber"))) {
                    match = false;
                }
            }
            
            // 状态过滤
            if (params.getString("status") != null && !params.getString("status").isEmpty()) {
                if (!item.getString("status").equals(params.getString("status"))) {
                    match = false;
                }
            }
            
            // 物流公司过滤
            if (params.getString("logisticsCompany") != null && !params.getString("logisticsCompany").isEmpty()) {
                if (!item.getString("logisticsCompany").contains(params.getString("logisticsCompany"))) {
                    match = false;
                }
            }
            
            // 收件人过滤
            if (params.getString("recipientName") != null && !params.getString("recipientName").isEmpty()) {
                if (!item.getString("recipientName").contains(params.getString("recipientName"))) {
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
     * 新增物流追踪
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean addLogisticsTracking(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            // 验证必要参数
            if (params.getString("workOrderNumber") == null || params.getString("workOrderNumber").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "工单号不能为空");
            }

            if (params.getString("carrierName") == null || params.getString("carrierName").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "承运商不能为空");
            }

            // 生成追踪号
            String trackingNumber = "LT" + Tools.getNowTime() + Tools.getCharAndNum(4);

            // 创建实体对象
            LogisticsTracking tracking = new LogisticsTracking();
            tracking.setTrackingNumber(trackingNumber);
            tracking.setWorkOrderId(params.getLong("workOrderId"));
            tracking.setWorkOrderNumber(params.getString("workOrderNumber"));
            tracking.setTaskId(params.getLong("taskId"));
            tracking.setTaskNumber(params.getString("taskNumber"));
            tracking.setLogisticsType(params.getString("logisticsType"));
            tracking.setProductId(params.getLong("productId"));
            tracking.setProductName(params.getString("productName"));
            tracking.setQuantity(params.getBigDecimal("quantity"));
            tracking.setUnitName(params.getString("unitName"));
            tracking.setFromLocation(params.getString("fromLocation"));
            tracking.setToLocation(params.getString("toLocation"));
            tracking.setCarrierName(params.getString("carrierName"));
            tracking.setCarrierContact(params.getString("carrierContact"));
            tracking.setTrackingCode(params.getString("trackingCode"));
            tracking.setStatus("PENDING");
            tracking.setEstimatedArrivalTime(params.getDate("estimatedArrivalTime"));
            tracking.setReceiverName(params.getString("receiverName"));
            tracking.setReceiverContact(params.getString("receiverContact"));
            tracking.setLogisticsCost(params.getBigDecimal("logisticsCost"));
            tracking.setTenantId(63L);
            tracking.setDeleteFlag("0");
            tracking.setCreateTime(new Date());
            tracking.setCreateUser(1L);

            // 插入数据库
            int result = logisticsTrackingMapper.insert(tracking);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "新增物流追踪失败");
            }

            // 记录操作日志
            logService.insertLog("物流追踪", "新增物流追踪：" + trackingNumber, request);

            logger.info("新增物流追踪成功：{}", trackingNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("新增物流追踪失败", e);
            throw e;
        }
    }
    
    /**
     * 更新物流追踪
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateLogisticsTracking(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // TODO: 实际的数据库更新逻辑
            params.put("updateTime", new Date());
            
            logger.info("更新物流追踪成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("更新物流追踪失败", e);
            throw e;
        }
    }
    
    /**
     * 删除物流追踪
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteLogisticsTracking(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            LogisticsTracking existing = logisticsTrackingMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "物流追踪记录不存在");
            }

            // 检查是否可以删除（运输中的物流不能删除）
            if ("IN_TRANSIT".equals(existing.getStatus()) || "SHIPPED".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "运输中的物流不能删除");
            }

            // 软删除
            existing.setDeleteFlag("1");
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            int result = logisticsTrackingMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "删除物流追踪失败");
            }

            logger.info("删除物流追踪成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除物流追踪失败", e);
            throw e;
        }
    }
    
    /**
     * 批量删除物流追踪
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteBatchLogisticsTracking(String ids, HttpServletRequest request) throws Exception {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID列表不能为空");
            }
            
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                deleteLogisticsTracking(Long.parseLong(id.trim()), request);
            }
            
            logger.info("批量删除物流追踪成功：{} 条记录", idArray.length);
            return true;
            
        } catch (Exception e) {
            logger.error("批量删除物流追踪失败", e);
            throw e;
        }
    }
    
    /**
     * 更新物流状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateLogisticsStatus(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            String status = params.getString("status");
            
            if (id == null || status == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID和状态不能为空");
            }
            
            // 查询现有记录
            LogisticsTracking existing = logisticsTrackingMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "物流追踪记录不存在");
            }

            // 更新状态
            int result = logisticsTrackingMapper.updateStatus(id, status, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "更新物流状态失败");
            }

            logger.info("更新物流状态成功：ID {}, 状态 {}", id, status);
            return true;
            
        } catch (Exception e) {
            logger.error("更新物流状态失败", e);
            throw e;
        }
    }
    
    /**
     * 获取物流详情
     */
    public JSONObject getLogisticsDetail(Long id, HttpServletRequest request) throws Exception {
        JSONObject detail = new JSONObject();
        
        try {
            // TODO: 实际的数据库查询逻辑
            // 模拟详情数据
            detail.put("id", id);
            detail.put("trackingNumber", "SF1234567890001");
            detail.put("orderNumber", "ORD2024001001");
            detail.put("logisticsCompany", "顺丰速运");
            detail.put("status", "IN_TRANSIT");
            detail.put("recipientName", "张三");
            detail.put("recipientPhone", "13800138001");
            detail.put("recipientAddress", "北京市朝阳区某某街道1号");
            detail.put("currentLocation", "运输途中 - 上海市浦东新区");
            detail.put("estimatedDelivery", new Date());
            
            logger.info("获取物流详情成功：ID {}", id);
            
        } catch (Exception e) {
            logger.error("获取物流详情失败", e);
            throw e;
        }
        
        return detail;
    }
    
    /**
     * 获取物流轨迹
     */
    public JSONObject getLogisticsTrace(String trackingNumber, HttpServletRequest request) throws Exception {
        JSONObject trace = new JSONObject();
        
        try {
            // TODO: 实际的物流轨迹查询逻辑
            // 模拟轨迹数据
            JSONArray traceList = new JSONArray();
            
            String[] locations = {"北京市朝阳区", "北京分拣中心", "上海分拣中心", "上海市浦东新区"};
            String[] descriptions = {"已揽收", "已发出", "运输途中", "派送中"};
            
            for (int i = 0; i < locations.length; i++) {
                JSONObject traceItem = new JSONObject();
                traceItem.put("time", new Date(System.currentTimeMillis() - (locations.length - i - 1) * 24 * 60 * 60 * 1000));
                traceItem.put("location", locations[i]);
                traceItem.put("description", descriptions[i]);
                traceList.add(traceItem);
            }
            
            trace.put("trackingNumber", trackingNumber);
            trace.put("traceList", traceList);
            trace.put("currentStatus", "运输途中");
            
            logger.info("获取物流轨迹成功：{}", trackingNumber);
            
        } catch (Exception e) {
            logger.error("获取物流轨迹失败", e);
            throw e;
        }
        
        return trace;
    }
    
    /**
     * 导出Excel
     */
    public void exportExcel(JSONObject params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // TODO: 实际的Excel导出逻辑
            
            logger.info("导出物流追踪Excel成功");
            
        } catch (Exception e) {
            logger.error("导出物流追踪Excel失败", e);
            throw e;
        }
    }
    
    /**
     * 获取物流统计
     */
    public JSONObject getLogisticsStatistics(HttpServletRequest request) throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 从数据库获取统计数据
            Map<String, Object> dbStatistics = logisticsTrackingMapper.getLogisticsStatistics(63L);

            // 转换为JSONObject
            statistics.put("totalLogistics", dbStatistics.get("totalLogistics"));
            statistics.put("pendingLogistics", dbStatistics.get("pendingLogistics"));
            statistics.put("shippedLogistics", dbStatistics.get("shippedLogistics"));
            statistics.put("inTransitLogistics", dbStatistics.get("inTransitLogistics"));
            statistics.put("deliveredLogistics", dbStatistics.get("deliveredLogistics"));
            statistics.put("exceptionLogistics", dbStatistics.get("exceptionLogistics"));

            statistics.put("totalQuantity", dbStatistics.get("totalQuantity"));
            statistics.put("deliveredQuantity", dbStatistics.get("deliveredQuantity"));

            // 计算交付率
            Object totalQuantity = dbStatistics.get("totalQuantity");
            Object deliveredQuantity = dbStatistics.get("deliveredQuantity");
            if (totalQuantity != null && deliveredQuantity != null) {
                BigDecimal total = new BigDecimal(totalQuantity.toString());
                BigDecimal delivered = new BigDecimal(deliveredQuantity.toString());
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal deliveryRate = delivered.divide(total, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    statistics.put("deliveryRate", deliveryRate);
                } else {
                    statistics.put("deliveryRate", 0);
                }
            }

            statistics.put("totalCost", dbStatistics.get("totalCost"));
            statistics.put("avgCost", dbStatistics.get("avgCost"));
            statistics.put("avgTransportTime", dbStatistics.get("avgTransportTime"));

            // 按状态统计
            List<Map<String, Object>> statusStatsList = logisticsTrackingMapper.getStatusStatistics(63L);
            JSONArray statusStats = new JSONArray();
            for (Map<String, Object> statusStat : statusStatsList) {
                JSONObject obj = new JSONObject();
                obj.put("status", statusStat.get("status"));
                obj.put("count", statusStat.get("count"));
                obj.put("totalQuantity", statusStat.get("totalQuantity"));
                obj.put("totalCost", statusStat.get("totalCost"));
                statusStats.add(obj);
            }
            statistics.put("statusStats", statusStats);

            logger.info("获取物流统计成功");

        } catch (Exception e) {
            logger.error("获取物流统计失败", e);
            throw e;
        }
        
        return statistics;
    }
    
    /**
     * 批量更新物流状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean batchUpdateLogisticsStatus(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            String ids = params.getString("ids");
            String status = params.getString("status");
            
            if (ids == null || ids.isEmpty() || status == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID列表和状态不能为空");
            }
            
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                JSONObject updateParams = new JSONObject();
                updateParams.put("id", Long.parseLong(id.trim()));
                updateParams.put("status", status);
                updateLogisticsStatus(updateParams, request);
            }
            
            logger.info("批量更新物流状态成功：{} 条记录", idArray.length);
            return true;
            
        } catch (Exception e) {
            logger.error("批量更新物流状态失败", e);
            throw e;
        }
    }
}
