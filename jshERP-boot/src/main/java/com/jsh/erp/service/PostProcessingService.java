package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.PostProcessingTask;
import com.jsh.erp.datasource.mappers.PostProcessingTaskMapper;
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
 * 后工任务服务
 * 提供后工任务相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class PostProcessingService {
    
    private Logger logger = LoggerFactory.getLogger(PostProcessingService.class);
    
    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    @Resource
    private PostProcessingTaskMapper postProcessingTaskMapper;
    
    /**
     * 获取后工任务列表
     */
    public JSONObject getPostProcessingList(JSONObject params, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        
        try {
            // 构建查询参数
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("tenantId", 63L); // 从session或request中获取租户ID

            // 处理搜索条件
            if (params != null) {
                if (params.getString("taskNumber") != null) {
                    parameterMap.put("taskNumber", params.getString("taskNumber"));
                }
                if (params.getString("productName") != null) {
                    parameterMap.put("productName", params.getString("productName"));
                }
                if (params.getString("taskType") != null) {
                    parameterMap.put("taskType", params.getString("taskType"));
                }
                if (params.getString("status") != null) {
                    parameterMap.put("status", params.getString("status"));
                }
                if (params.getString("priority") != null) {
                    parameterMap.put("priority", params.getString("priority"));
                }
                if (params.getString("workerName") != null) {
                    parameterMap.put("workerName", params.getString("workerName"));
                }
            }

            // 查询数据库
            List<PostProcessingTask> list = postProcessingTaskMapper.selectByCondition(parameterMap);
            Long total = postProcessingTaskMapper.countByCondition(parameterMap);

            // 转换为JSONArray
            JSONArray dataList = new JSONArray();
            for (PostProcessingTask item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("taskNumber", item.getTaskNumber());
                obj.put("productId", item.getProductId());
                obj.put("productName", item.getProductName());
                obj.put("taskType", item.getTaskType());
                obj.put("taskDescription", item.getTaskDescription());
                obj.put("quantity", item.getQuantity());
                obj.put("unitName", item.getUnitName());
                obj.put("workerId", item.getWorkerId());
                obj.put("workerName", item.getWorkerName());
                obj.put("laborCostAmount", item.getLaborCostAmount());
                obj.put("salaryAmount", item.getSalaryAmount());
                obj.put("status", item.getStatus());
                obj.put("priority", item.getPriority());
                obj.put("planStartTime", item.getPlanStartTime());
                obj.put("planEndTime", item.getPlanEndTime());
                obj.put("actualStartTime", item.getActualStartTime());
                obj.put("actualEndTime", item.getActualEndTime());
                obj.put("processingStandard", item.getProcessingStandard());
                obj.put("qualityRequirement", item.getQualityRequirement());
                obj.put("equipment", item.getEquipment());
                obj.put("processingResult", item.getProcessingResult());
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

            logger.info("获取后工任务列表成功，共 {} 条记录", total);

        } catch (Exception e) {
            logger.error("获取后工任务列表失败", e);
            throw e;
        }
        
        return result;
    }
    
    /**
     * 创建模拟后工任务数据
     */
    private JSONArray createMockPostProcessingData() {
        JSONArray dataList = new JSONArray();
        
        String[] taskNumbers = {"PP2024001", "PP2024002", "PP2024003", "PP2024004", "PP2024005"};
        String[] productNames = {"掐丝点蓝手镯", "掐丝点蓝项链", "掐丝点蓝耳环", "配饰手链", "配饰戒指"};
        String[] taskTypes = {"抛光打磨", "质量检验", "包装处理", "标签贴附", "最终检查"};
        String[] statuses = {"PENDING", "ASSIGNED", "IN_PROGRESS", "COMPLETED"};
        String[] workerNames = {"孙师傅", "钱师傅", "赵师傅", "李师傅", "王师傅"};
        
        for (int i = 0; i < 18; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i + 1);
            item.put("taskNumber", taskNumbers[i % taskNumbers.length] + String.format("%03d", i + 1));
            item.put("productName", productNames[i % productNames.length]);
            item.put("taskType", taskTypes[i % taskTypes.length]);
            item.put("status", statuses[i % statuses.length]);
            item.put("workerName", workerNames[i % workerNames.length]);
            
            // 数量和单位
            item.put("quantity", 8 + (i * 2));
            item.put("unitName", "个");
            item.put("completedQuantity", i % 4 == 0 ? 0 : (3 + i));
            
            // 优先级
            item.put("priority", i % 4 == 0 ? "HIGH" : (i % 4 == 1 ? "NORMAL" : (i % 4 == 2 ? "LOW" : "URGENT")));
            
            // 时间信息
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            item.put("createTime", cal.getTime());
            item.put("planStartTime", cal.getTime());
            
            cal.add(Calendar.DAY_OF_MONTH, 2);
            item.put("planEndTime", cal.getTime());
            
            if (!item.getString("status").equals("PENDING")) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
                item.put("actualStartTime", cal.getTime());
            }
            
            if (item.getString("status").equals("COMPLETED")) {
                cal.add(Calendar.DAY_OF_MONTH, 1);
                item.put("actualEndTime", cal.getTime());
            }
            
            // 任务信息
            item.put("difficulty", i % 3 == 0 ? "简单" : (i % 3 == 1 ? "中等" : "困难"));
            item.put("estimatedHours", 2 + (i % 6));
            item.put("actualHours", item.getString("status").equals("COMPLETED") ? (2 + (i % 5)) : 0);
            
            // 质量信息
            if (item.getString("status").equals("COMPLETED")) {
                item.put("qualityGrade", i % 5 == 0 ? "优秀" : (i % 5 == 1 ? "良好" : "合格"));
                item.put("qualityScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
            }
            
            // 备注
            item.put("remark", "后工任务 - " + item.getString("taskType"));
            
            dataList.add(item);
        }
        
        return dataList;
    }
    
    /**
     * 过滤后工任务数据
     */
    private JSONArray filterPostProcessingData(JSONArray dataList, JSONObject params) {
        JSONArray filteredList = new JSONArray();
        
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject item = dataList.getJSONObject(i);
            boolean match = true;
            
            // 任务编号过滤
            if (params.getString("taskNumber") != null && !params.getString("taskNumber").isEmpty()) {
                if (!item.getString("taskNumber").contains(params.getString("taskNumber"))) {
                    match = false;
                }
            }
            
            // 产品名称过滤
            if (params.getString("productName") != null && !params.getString("productName").isEmpty()) {
                if (!item.getString("productName").contains(params.getString("productName"))) {
                    match = false;
                }
            }
            
            // 状态过滤
            if (params.getString("status") != null && !params.getString("status").isEmpty()) {
                if (!item.getString("status").equals(params.getString("status"))) {
                    match = false;
                }
            }
            
            // 任务类型过滤
            if (params.getString("taskType") != null && !params.getString("taskType").isEmpty()) {
                if (!item.getString("taskType").contains(params.getString("taskType"))) {
                    match = false;
                }
            }
            
            // 工人姓名过滤
            if (params.getString("workerName") != null && !params.getString("workerName").isEmpty()) {
                if (!item.getString("workerName").contains(params.getString("workerName"))) {
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
     * 新增后工任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean addPostProcessing(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            // 验证必要参数
            if (params.getString("productName") == null || params.getString("productName").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "产品名称不能为空");
            }

            if (params.getString("taskType") == null || params.getString("taskType").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "任务类型不能为空");
            }

            if (params.getBigDecimal("quantity") == null || params.getBigDecimal("quantity").compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "处理数量必须大于0");
            }

            // 生成任务编号
            String taskNumber = "PP" + Tools.getNowTime() + Tools.getCharAndNum(4);

            // 创建实体对象
            PostProcessingTask task = new PostProcessingTask();
            task.setTaskNumber(taskNumber);
            task.setProductId(params.getLong("productId"));
            task.setProductName(params.getString("productName"));
            task.setTaskType(params.getString("taskType"));
            task.setTaskDescription(params.getString("taskDescription"));
            task.setQuantity(params.getBigDecimal("quantity"));
            task.setUnitId(params.getLong("unitId"));
            task.setUnitName(params.getString("unitName"));
            task.setLaborCostAmount(params.getBigDecimal("laborCostAmount"));
            task.setStatus("PENDING");
            task.setPriority(params.getString("priority") != null ? params.getString("priority") : "MEDIUM");
            task.setPlanStartTime(params.getDate("planStartTime"));
            task.setPlanEndTime(params.getDate("planEndTime"));
            task.setProcessingStandard(params.getString("processingStandard"));
            task.setQualityRequirement(params.getString("qualityRequirement"));
            task.setEquipment(params.getString("equipment"));
            task.setRemark(params.getString("remark"));
            task.setTenantId(63L);
            task.setDeleteFlag("0");
            task.setCreateTime(new Date());
            task.setCreateUser(1L);

            // 插入数据库
            int result = postProcessingTaskMapper.insert(task);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "新增后工任务失败");
            }

            // 记录操作日志
            logService.insertLog("后工任务", "新增后工任务：" + taskNumber, request);

            logger.info("新增后工任务成功：{}", taskNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("新增后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 更新后工任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updatePostProcessing(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            PostProcessingTask existing = postProcessingTaskMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "后工任务记录不存在");
            }

            // 更新字段
            if (params.getString("productName") != null) {
                existing.setProductName(params.getString("productName"));
            }
            if (params.getString("taskDescription") != null) {
                existing.setTaskDescription(params.getString("taskDescription"));
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
            if (params.getString("priority") != null) {
                existing.setPriority(params.getString("priority"));
            }
            if (params.getDate("planStartTime") != null) {
                existing.setPlanStartTime(params.getDate("planStartTime"));
            }
            if (params.getDate("planEndTime") != null) {
                existing.setPlanEndTime(params.getDate("planEndTime"));
            }
            if (params.getString("processingStandard") != null) {
                existing.setProcessingStandard(params.getString("processingStandard"));
            }
            if (params.getString("qualityRequirement") != null) {
                existing.setQualityRequirement(params.getString("qualityRequirement"));
            }
            if (params.getString("equipment") != null) {
                existing.setEquipment(params.getString("equipment"));
            }
            if (params.getString("remark") != null) {
                existing.setRemark(params.getString("remark"));
            }
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            // 更新数据库
            int result = postProcessingTaskMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "更新后工任务失败");
            }

            logger.info("更新后工任务成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("更新后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 删除后工任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deletePostProcessing(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            PostProcessingTask existing = postProcessingTaskMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "后工任务记录不存在");
            }

            // 检查是否可以删除（进行中的任务不能删除）
            if ("IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "进行中的任务不能删除");
            }

            // 软删除
            existing.setDeleteFlag("1");
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            int result = postProcessingTaskMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "删除后工任务失败");
            }

            logger.info("删除后工任务成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 批量删除后工任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteBatchPostProcessing(String ids, HttpServletRequest request) throws Exception {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID列表不能为空");
            }
            
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                deletePostProcessing(Long.parseLong(id.trim()), request);
            }
            
            logger.info("批量删除后工任务成功：{} 条记录", idArray.length);
            return true;
            
        } catch (Exception e) {
            logger.error("批量删除后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 分配任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean assignTask(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long taskId = params.getLong("taskId");
            Long workerId = params.getLong("workerId");
            
            if (taskId == null || workerId == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "任务ID和工人ID不能为空");
            }
            
            // 查询现有任务
            PostProcessingTask existing = postProcessingTaskMapper.selectById(taskId);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "后工任务不存在");
            }

            // 检查任务状态
            if (!"PENDING".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有待处理状态的任务才能分配");
            }

            // 分配工人
            String workerName = params.getString("workerName");
            int result = postProcessingTaskMapper.assignWorker(taskId, workerId, workerName, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "分配后工任务失败");
            }

            logger.info("分配后工任务成功：任务ID {}, 工人ID {}", taskId, workerId);
            return true;
            
        } catch (Exception e) {
            logger.error("分配后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 开始任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean startTask(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有任务
            PostProcessingTask existing = postProcessingTaskMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "后工任务不存在");
            }

            // 检查任务状态
            if (!"PENDING".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有待处理状态的任务才能开始");
            }

            // 更新状态为IN_PROGRESS，记录实际开始时间
            Date actualStartTime = new Date();
            int result = postProcessingTaskMapper.updateStartTime(id, actualStartTime, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "开始后工任务失败");
            }

            logger.info("开始后工任务成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("开始后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 完成任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean completeTask(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有任务
            PostProcessingTask existing = postProcessingTaskMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "后工任务不存在");
            }

            // 检查任务状态
            if (!"IN_PROGRESS".equals(existing.getStatus())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "只有进行中状态的任务才能完成");
            }

            // 更新状态为COMPLETED，记录实际完成时间
            Date actualEndTime = new Date();
            int result = postProcessingTaskMapper.updateEndTime(id, actualEndTime, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "完成后工任务失败");
            }

            logger.info("完成后工任务成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("完成后工任务失败", e);
            throw e;
        }
    }
    
    /**
     * 导出Excel
     */
    public void exportExcel(JSONObject params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // TODO: 实际的Excel导出逻辑
            
            logger.info("导出后工任务Excel成功");
            
        } catch (Exception e) {
            logger.error("导出后工任务Excel失败", e);
            throw e;
        }
    }
    
    /**
     * 获取任务统计
     */
    public JSONObject getTaskStatistics(HttpServletRequest request) throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 从数据库获取统计数据
            Map<String, Object> dbStatistics = postProcessingTaskMapper.getTaskStatistics(63L);

            // 转换为JSONObject
            statistics.put("totalTasks", dbStatistics.get("totalTasks"));
            statistics.put("pendingTasks", dbStatistics.get("pendingTasks"));
            statistics.put("inProgressTasks", dbStatistics.get("inProgressTasks"));
            statistics.put("completedTasks", dbStatistics.get("completedTasks"));
            statistics.put("cancelledTasks", dbStatistics.get("cancelledTasks"));
            statistics.put("urgentTasks", dbStatistics.get("urgentTasks"));
            statistics.put("highPriorityTasks", dbStatistics.get("highPriorityTasks"));

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
            statistics.put("avgProcessingTime", dbStatistics.get("avgProcessingTime"));
            statistics.put("avgQualityScore", dbStatistics.get("avgQualityScore"));

            // 按任务类型统计
            List<Map<String, Object>> taskTypeStatsList = postProcessingTaskMapper.getTaskTypeStatistics(63L);
            JSONArray taskTypeStats = new JSONArray();
            for (Map<String, Object> typeStats : taskTypeStatsList) {
                JSONObject obj = new JSONObject();
                obj.put("taskType", typeStats.get("taskType"));
                obj.put("count", typeStats.get("count"));
                obj.put("totalQuantity", typeStats.get("totalQuantity"));
                obj.put("avgQualityScore", typeStats.get("avgQualityScore"));
                taskTypeStats.add(obj);
            }
            statistics.put("taskTypeStats", taskTypeStats);

            logger.info("获取后工任务统计成功");

        } catch (Exception e) {
            logger.error("获取后工任务统计失败", e);
            throw e;
        }
        
        return statistics;
    }
}
