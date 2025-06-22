package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
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
import java.util.*;

/**
 * 崇左生产看板服务
 * 提供生产看板相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ChongzuoProductionService {
    
    private Logger logger = LoggerFactory.getLogger(ChongzuoProductionService.class);
    
    @Resource
    private LogService logService;
    
    @Resource
    private UserService userService;
    
    /**
     * 获取生产看板统计数据
     */
    public JSONObject getProductionStatistics() throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 模拟统计数据
            statistics.put("totalTasks", 156);
            statistics.put("pendingCount", 23);
            statistics.put("assignedCount", 18);
            statistics.put("inProgressCount", 45);
            statistics.put("completedCount", 70);
            statistics.put("taskTrend", 12.5); // 较昨日增长12.5%
            
            // 优先级分布
            statistics.put("urgentCount", 8);
            statistics.put("highCount", 25);
            statistics.put("normalCount", 98);
            statistics.put("lowCount", 25);
            
            // 工人效率
            statistics.put("activeWorkers", 28);
            statistics.put("avgEfficiency", 87.5);
            statistics.put("maxEfficiency", 95.2);
            
            // 质量统计
            statistics.put("qualifiedCount", 142);
            statistics.put("defectiveCount", 14);
            statistics.put("avgQualityScore", 4.2);
            
            // 今日概况
            statistics.put("todayNewTasks", 12);
            statistics.put("todayStartedTasks", 15);
            statistics.put("todayCompletedTasks", 18);
            
            // 生产趋势
            statistics.put("weekCompletedTasks", 85);
            statistics.put("lastWeekCompletedTasks", 78);
            
            // 成本统计
            statistics.put("monthTotalCost", new BigDecimal("125680.50"));
            statistics.put("avgUnitCost", new BigDecimal("45.20"));
            statistics.put("laborCost", new BigDecimal("68500.00"));
            statistics.put("materialCost", new BigDecimal("57180.50"));
            statistics.put("costTrend", -3.2); // 较上月下降3.2%
            
            logger.info("获取生产统计数据成功");
            
        } catch (Exception e) {
            logger.error("获取生产统计数据失败", e);
            throw e;
        }
        
        return statistics;
    }
    
    /**
     * 获取任务看板数据
     */
    public JSONObject getTaskBoardData(JSONObject params) throws Exception {
        JSONObject taskData = new JSONObject();
        
        try {
            // 模拟任务数据
            JSONArray pendingTasks = createMockTasks("PENDING", 5);
            JSONArray assignedTasks = createMockTasks("ASSIGNED", 4);
            JSONArray inProgressTasks = createMockTasks("IN_PROGRESS", 6);
            JSONArray completedTasks = createMockTasks("COMPLETED", 8);
            
            taskData.put("pendingTasks", pendingTasks);
            taskData.put("assignedTasks", assignedTasks);
            taskData.put("inProgressTasks", inProgressTasks);
            taskData.put("completedTasks", completedTasks);
            
            // 统计信息
            taskData.put("totalCount", pendingTasks.size() + assignedTasks.size() + 
                        inProgressTasks.size() + completedTasks.size());
            taskData.put("lastUpdate", new Date());
            
            logger.info("获取任务看板数据成功");
            
        } catch (Exception e) {
            logger.error("获取任务看板数据失败", e);
            throw e;
        }
        
        return taskData;
    }
    
    /**
     * 创建模拟任务数据
     */
    private JSONArray createMockTasks(String status, int count) {
        JSONArray tasks = new JSONArray();
        
        String[] productNames = {"掐丝点蓝手镯", "掐丝点蓝项链", "掐丝点蓝耳环", "配饰手链", "配饰戒指"};
        String[] workerNames = {"张师傅", "李师傅", "王师傅", "赵师傅", "陈师傅"};
        String[] priorities = {"LOW", "NORMAL", "HIGH", "URGENT"};
        String[] specialties = {"掐丝工艺", "点蓝工艺", "配饰制作", "抛光打磨", "质量检验"};
        
        for (int i = 0; i < count; i++) {
            JSONObject task = new JSONObject();
            task.put("id", System.currentTimeMillis() + i);
            task.put("taskNumber", "T" + Tools.getNowTime() + String.format("%03d", i + 1));
            task.put("taskName", productNames[i % productNames.length] + "制作");
            task.put("productName", productNames[i % productNames.length]);
            task.put("quantity", 10 + (i * 5));
            task.put("unitName", "个");
            task.put("priority", priorities[i % priorities.length]);
            task.put("status", status);
            
            // 工人信息
            if (!status.equals("PENDING")) {
                task.put("workerId", i + 1);
                task.put("workerName", workerNames[i % workerNames.length]);
                task.put("workerSpecialty", specialties[i % specialties.length]);
                task.put("workerAvatar", "");
            }
            
            // 时间信息
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            task.put("planStartTime", cal.getTime());
            
            cal.add(Calendar.HOUR_OF_DAY, 8);
            task.put("planEndTime", cal.getTime());
            
            if (!status.equals("PENDING") && !status.equals("ASSIGNED")) {
                cal.add(Calendar.HOUR_OF_DAY, -6);
                task.put("actualStartTime", cal.getTime());
            }
            
            if (status.equals("COMPLETED")) {
                cal.add(Calendar.HOUR_OF_DAY, 7);
                task.put("completeTime", cal.getTime());
                task.put("qualityStatus", i % 4 == 0 ? "FAIL" : "PASS");
            }
            
            // 进度信息
            if (status.equals("IN_PROGRESS")) {
                task.put("completedQuantity", (i + 1) * 3);
                task.put("estimatedHours", 8);
            }
            
            tasks.add(task);
        }
        
        return tasks;
    }
    
    /**
     * 更新任务状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateTaskStatus(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            String newStatus = params.getString("newStatus");
            String oldStatus = params.getString("oldStatus");
            String remark = params.getString("remark");
            
            if (taskId == null || newStatus == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "参数不完整");
            }
            
            // 验证状态转换的合法性
            if (!isValidStatusTransition(oldStatus, newStatus)) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "无效的状态转换");
            }
            
            // TODO: 实际的数据库更新逻辑
            // 这里应该调用相应的Mapper更新任务状态
            
            // 记录操作日志
            String logContent = String.format("任务状态更新：%s -> %s", oldStatus, newStatus);
            if (remark != null && !remark.isEmpty()) {
                logContent += "，备注：" + remark;
            }
            
            // logService.insertLog("任务管理", logContent, request);
            
            logger.info("任务状态更新成功：任务ID {}, {} -> {}", taskId, oldStatus, newStatus);
            return true;
            
        } catch (Exception e) {
            logger.error("更新任务状态失败", e);
            throw e;
        }
    }
    
    /**
     * 验证状态转换的合法性
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        // 定义合法的状态转换
        Map<String, List<String>> validTransitions = new HashMap<>();
        validTransitions.put("PENDING", Arrays.asList("ASSIGNED", "CANCELLED"));
        validTransitions.put("ASSIGNED", Arrays.asList("IN_PROGRESS", "PENDING"));
        validTransitions.put("IN_PROGRESS", Arrays.asList("COMPLETED", "PAUSED", "ASSIGNED"));
        validTransitions.put("PAUSED", Arrays.asList("IN_PROGRESS", "CANCELLED"));
        validTransitions.put("COMPLETED", Arrays.asList("QUALITY_CHECK"));
        
        List<String> allowedTransitions = validTransitions.get(oldStatus);
        return allowedTransitions != null && allowedTransitions.contains(newStatus);
    }
    
    /**
     * 任务派工
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean assignTask(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            Long workerId = params.getLong("workerId");
            String remark = params.getString("remark");
            
            if (taskId == null || workerId == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "参数不完整");
            }
            
            // TODO: 实际的派工逻辑
            // 1. 检查任务状态是否为PENDING
            // 2. 检查工人是否可用
            // 3. 更新任务状态为ASSIGNED
            // 4. 记录派工信息
            
            logger.info("任务派工成功：任务ID {}, 工人ID {}", taskId, workerId);
            return true;
            
        } catch (Exception e) {
            logger.error("任务派工失败", e);
            throw e;
        }
    }
    
    /**
     * 开始任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean startTask(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            
            if (taskId == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "任务ID不能为空");
            }
            
            // TODO: 实际的开始任务逻辑
            // 1. 检查任务状态是否为ASSIGNED
            // 2. 更新任务状态为IN_PROGRESS
            // 3. 记录实际开始时间
            
            logger.info("任务开始成功：任务ID {}", taskId);
            return true;
            
        } catch (Exception e) {
            logger.error("任务开始失败", e);
            throw e;
        }
    }
    
    /**
     * 完成任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean completeTask(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            BigDecimal completedQuantity = params.getBigDecimal("completedQuantity");
            String qualityNote = params.getString("qualityNote");
            
            if (taskId == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "任务ID不能为空");
            }
            
            // TODO: 实际的完成任务逻辑
            // 1. 检查任务状态是否为IN_PROGRESS
            // 2. 更新任务状态为COMPLETED
            // 3. 记录完成时间和完成数量
            // 4. 触发质检流程
            
            logger.info("任务完成成功：任务ID {}, 完成数量 {}", taskId, completedQuantity);
            return true;
            
        } catch (Exception e) {
            logger.error("任务完成失败", e);
            throw e;
        }
    }
    
    /**
     * 获取工人列表
     */
    public JSONArray getWorkerList(JSONObject params) throws Exception {
        JSONArray workers = new JSONArray();
        
        try {
            // 模拟工人数据
            String[] workerNames = {"张师傅", "李师傅", "王师傅", "赵师傅", "陈师傅", "刘师傅", "周师傅", "吴师傅"};
            String[] specialties = {"掐丝工艺", "点蓝工艺", "配饰制作", "抛光打磨", "质量检验"};
            String[] statuses = {"AVAILABLE", "BUSY", "OFFLINE"};
            
            for (int i = 0; i < workerNames.length; i++) {
                JSONObject worker = new JSONObject();
                worker.put("id", i + 1);
                worker.put("name", workerNames[i]);
                worker.put("specialty", specialties[i % specialties.length]);
                worker.put("status", statuses[i % statuses.length]);
                worker.put("efficiency", 75 + (i * 3));
                worker.put("currentTasks", i % 3);
                worker.put("completedToday", i * 2);
                worker.put("avatar", "");
                
                workers.add(worker);
            }
            
            logger.info("获取工人列表成功，共 {} 人", workers.size());
            
        } catch (Exception e) {
            logger.error("获取工人列表失败", e);
            throw e;
        }
        
        return workers;
    }
    
    /**
     * 获取实时生产数据
     */
    public JSONObject getRealtimeData() throws Exception {
        JSONObject realtimeData = new JSONObject();
        
        try {
            realtimeData.put("timestamp", new Date());
            realtimeData.put("onlineWorkers", 28);
            realtimeData.put("runningTasks", 45);
            realtimeData.put("todayOutput", 156);
            realtimeData.put("todayTarget", 180);
            realtimeData.put("efficiency", 86.7);
            realtimeData.put("qualityRate", 94.2);
            
            // 设备状态
            JSONArray equipmentStatus = new JSONArray();
            String[] equipmentNames = {"掐丝工作台1", "掐丝工作台2", "点蓝炉1", "点蓝炉2", "抛光机1", "抛光机2"};
            String[] statuses = {"RUNNING", "IDLE", "MAINTENANCE"};
            
            for (int i = 0; i < equipmentNames.length; i++) {
                JSONObject equipment = new JSONObject();
                equipment.put("name", equipmentNames[i]);
                equipment.put("status", statuses[i % statuses.length]);
                equipment.put("utilization", 70 + (i * 5));
                equipmentStatus.add(equipment);
            }
            
            realtimeData.put("equipmentStatus", equipmentStatus);
            
            logger.info("获取实时生产数据成功");
            
        } catch (Exception e) {
            logger.error("获取实时生产数据失败", e);
            throw e;
        }
        
        return realtimeData;
    }
    
    /**
     * 获取生产报告
     */
    public JSONObject getProductionReport(JSONObject params) throws Exception {
        JSONObject report = new JSONObject();
        
        try {
            // 模拟报告数据
            report.put("period", params.getString("startDate") + " 至 " + params.getString("endDate"));
            report.put("totalTasks", 245);
            report.put("completedTasks", 198);
            report.put("completionRate", 80.8);
            report.put("avgCycleTime", 6.5);
            report.put("qualityRate", 94.2);
            report.put("efficiency", 87.3);
            
            logger.info("获取生产报告成功");
            
        } catch (Exception e) {
            logger.error("获取生产报告失败", e);
            throw e;
        }
        
        return report;
    }
    
    /**
     * 获取质量统计
     */
    public JSONObject getQualityStatistics(String period) throws Exception {
        JSONObject qualityStats = new JSONObject();
        
        try {
            qualityStats.put("period", period);
            qualityStats.put("totalInspected", 198);
            qualityStats.put("qualified", 186);
            qualityStats.put("defective", 12);
            qualityStats.put("qualityRate", 93.9);
            qualityStats.put("avgScore", 4.2);
            
            logger.info("获取质量统计成功");
            
        } catch (Exception e) {
            logger.error("获取质量统计失败", e);
            throw e;
        }
        
        return qualityStats;
    }
    
    /**
     * 获取设备状态
     */
    public JSONArray getEquipmentStatus() throws Exception {
        JSONArray equipmentList = new JSONArray();
        
        try {
            // 模拟设备数据
            String[] equipmentNames = {"掐丝工作台1", "掐丝工作台2", "点蓝炉1", "点蓝炉2", "抛光机1", "抛光机2"};
            String[] statuses = {"RUNNING", "IDLE", "MAINTENANCE", "RUNNING", "RUNNING", "IDLE"};
            
            for (int i = 0; i < equipmentNames.length; i++) {
                JSONObject equipment = new JSONObject();
                equipment.put("id", i + 1);
                equipment.put("name", equipmentNames[i]);
                equipment.put("status", statuses[i]);
                equipment.put("utilization", 70 + (i * 5));
                equipment.put("lastMaintenance", "2024-01-10");
                equipment.put("nextMaintenance", "2024-02-10");
                
                equipmentList.add(equipment);
            }
            
            logger.info("获取设备状态成功，共 {} 台设备", equipmentList.size());
            
        } catch (Exception e) {
            logger.error("获取设备状态失败", e);
            throw e;
        }
        
        return equipmentList;
    }
}
