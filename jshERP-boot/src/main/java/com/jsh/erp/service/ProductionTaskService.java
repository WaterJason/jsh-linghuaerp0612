package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.ProductionTask;
import com.jsh.erp.datasource.entities.ProductionTaskEx;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.ProductionTaskMapper;
import com.jsh.erp.datasource.mappers.ProductionTaskMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产任务Service
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ProductionTaskService {
    
    private Logger logger = LoggerFactory.getLogger(ProductionTaskService.class);

    @Resource
    private ProductionTaskMapper productionTaskMapper;
    
    @Resource
    private ProductionTaskMapperEx productionTaskMapperEx;
    
    @Resource
    private UserService userService;
    
    @Resource
    private LogService logService;
    
    @Resource
    private ProductionWorkOrderService productionWorkOrderService;

    /**
     * 根据ID获取生产任务
     */
    public ProductionTask getProductionTask(Long id) throws Exception {
        ProductionTask result = null;
        try {
            result = productionTaskMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据ID获取生产任务详情（包含关联信息）
     */
    public ProductionTaskEx getProductionTaskDetail(Long id) throws Exception {
        ProductionTaskEx result = null;
        try {
            result = productionTaskMapperEx.findById(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据条件查询生产任务列表
     */
    public List<ProductionTaskEx> select(String taskNumber, String taskName, String workOrderNumber,
                                         String taskType, String processStep, String productName,
                                         String priority, String status, String workerName,
                                         String workerSpecialty, Date assignTime, Date planStartTime,
                                         Date planEndTime, Integer offset, Integer rows) throws Exception {
        List<ProductionTaskEx> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            list = productionTaskMapperEx.selectByConditionTask(
                taskNumber, taskName, workOrderNumber, taskType, processStep, productName,
                priority, status, workerName, workerSpecialty, assignTime, planStartTime,
                planEndTime, tenantId, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 根据条件统计生产任务数量
     */
    public Long countTask(String taskNumber, String taskName, String workOrderNumber,
                          String taskType, String processStep, String productName,
                          String priority, String status, String workerName,
                          String workerSpecialty, Date assignTime, Date planStartTime,
                          Date planEndTime) throws Exception {
        Long result = 0L;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionTaskMapperEx.countByConditionTask(
                taskNumber, taskName, workOrderNumber, taskType, processStep, productName,
                priority, status, workerName, workerSpecialty, assignTime, planStartTime,
                planEndTime, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增生产任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertProductionTask(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionTask task = JSONObject.parseObject(obj.toJSONString(), ProductionTask.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置基础信息
            task.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            task.setCreateUser(userInfo != null ? userInfo.getId() : null);
            task.setCreateTime(new Date());
            task.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 生成任务编号
            if (StringUtil.isEmpty(task.getTaskNumber())) {
                task.setTaskNumber(generateTaskNumber());
            }
            
            // 检查任务编号是否重复
            if (checkTaskNumberExist(null, task.getTaskNumber()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_CODE,
                    String.format(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_MSG, task.getTaskNumber()));
            }
            
            // 设置默认状态
            if (StringUtil.isEmpty(task.getStatus())) {
                task.setStatus("PENDING");
            }
            
            // 设置默认优先级
            if (StringUtil.isEmpty(task.getPriority())) {
                task.setPriority("NORMAL");
            }
            
            // 初始化数量
            if (task.getCompletedQuantity() == null) {
                task.setCompletedQuantity(BigDecimal.ZERO);
            }
            if (task.getQualifiedQuantity() == null) {
                task.setQualifiedQuantity(BigDecimal.ZERO);
            }
            if (task.getDefectiveQuantity() == null) {
                task.setDefectiveQuantity(BigDecimal.ZERO);
            }
            
            // 初始化工时
            if (task.getEstimatedHours() == null) {
                task.setEstimatedHours(BigDecimal.ZERO);
            }
            if (task.getActualHours() == null) {
                task.setActualHours(BigDecimal.ZERO);
            }
            
            // 初始化费用
            if (task.getUnitFee() == null) {
                task.setUnitFee(BigDecimal.ZERO);
            }
            if (task.getTotalFee() == null) {
                task.setTotalFee(BigDecimal.ZERO);
            }
            
            result = productionTaskMapper.insertSelective(task);
            
            // 记录日志
            logService.insertLog("生产任务",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(task.getTaskName()).toString(),
                request);
                
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
        return result;
    }

    /**
     * 更新生产任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateProductionTask(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionTask task = JSONObject.parseObject(obj.toJSONString(), ProductionTask.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置更新信息
            task.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            task.setUpdateTime(new Date());
            
            // 检查任务编号是否重复
            if (checkTaskNumberExist(task.getId(), task.getTaskNumber()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_CODE,
                    String.format(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_MSG, task.getTaskNumber()));
            }
            
            result = productionTaskMapper.updateByPrimaryKeySelective(task);
            
            // 记录日志
            logService.insertLog("生产任务",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(task.getTaskName()).toString(),
                request);
                
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
        return result;
    }

    /**
     * 删除生产任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteProductionTask(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteProductionTaskByIds(id.toString(), request);
    }

    /**
     * 批量删除生产任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProductionTask(String ids, HttpServletRequest request) throws Exception {
        return batchDeleteProductionTaskByIds(ids, request);
    }

    /**
     * 批量删除生产任务（内部方法）
     */
    private int batchDeleteProductionTaskByIds(String ids, HttpServletRequest request) throws Exception {
        String[] idArray = ids.split(",");
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updater = userInfo != null ? userInfo.getId() : null;
            
            result = productionTaskMapperEx.batchDeleteTaskByIds(updateTime, updater, idArray);
            
            // 记录日志
            logService.insertLog("生产任务",
                "批量删除生产任务，数量：" + idArray.length,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 检查任务编号是否存在
     */
    public int checkTaskNumberExist(Long id, String taskNumber) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            result = productionTaskMapperEx.checkTaskNumberExist(id, taskNumber, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 生成任务编号
     */
    private String generateTaskNumber() {
        // 生成格式：PT + YYYYMMDD + 6位序号
        String dateStr = Tools.dateToStr(new Date(), "yyyyMMdd");
        String prefix = "PT";
        
        // 这里可以调用序号服务生成唯一序号
        // 暂时使用时间戳后6位
        String suffix = String.format("%06d", System.currentTimeMillis() % 1000000);
        
        return prefix + dateStr + suffix;
    }

    /**
     * 分配任务给工人
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int assignTaskToWorker(Long taskId, Long workerId, String workerName, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date assignTime = new Date();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;
            
            // 检查工人是否有时间冲突的任务
            ProductionTask task = getProductionTask(taskId);
            if (task != null && task.getPlanStartTime() != null && task.getPlanEndTime() != null) {
                int conflictCount = productionTaskMapperEx.checkWorkerTimeConflict(
                    workerId, task.getPlanStartTime(), task.getPlanEndTime(), taskId);
                if (conflictCount > 0) {
                    throw new BusinessRunTimeException(ExceptionConstants.DEPOT_HEAD_OPER_FAILED_CODE,
                        "工人在该时间段已有其他任务，存在时间冲突");
                }
            }
            
            result = productionTaskMapperEx.assignTaskToWorker(taskId, workerId, workerName, assignTime, updateTime, updateUser);
            
            // 记录日志
            logService.insertLog("生产任务",
                "分配任务给工人：" + workerName,
                request);
                
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 开始任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int startTask(Long taskId, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date startTime = new Date();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;
            
            result = productionTaskMapperEx.startTask(taskId, startTime, updateTime, updateUser);
            
            // 记录日志
            logService.insertLog("生产任务",
                "开始任务",
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 完成任务
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int completeTask(Long taskId, BigDecimal completedQuantity, BigDecimal qualifiedQuantity,
                           BigDecimal defectiveQuantity, BigDecimal actualHours, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date completeTime = new Date();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;
            
            result = productionTaskMapperEx.completeTask(taskId, completeTime, completedQuantity,
                qualifiedQuantity, defectiveQuantity, actualHours, updateTime, updateUser);
            
            // 记录日志
            logService.insertLog("生产任务",
                "完成任务，完成数量：" + completedQuantity,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 获取可领取的任务列表
     */
    public List<ProductionTaskEx> getAvailableTasks(String taskType, String priority) throws Exception {
        List<ProductionTaskEx> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionTaskMapperEx.getAvailableTasks(tenantId, taskType, priority);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取工人的任务列表
     */
    public List<ProductionTaskEx> getWorkerTasks(Long workerId, String status) throws Exception {
        List<ProductionTaskEx> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionTaskMapperEx.getWorkerTasks(workerId, status, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取任务统计信息
     */
    public Map<String, Object> getTaskStatistics(Date startDate, Date endDate) throws Exception {
        Map<String, Object> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionTaskMapperEx.getTaskStatistics(tenantId, startDate, endDate);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取即将到期的任务列表
     */
    public List<ProductionTaskEx> getExpiringTasks(Integer days) throws Exception {
        List<ProductionTaskEx> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionTaskMapperEx.getExpiringTasks(tenantId, days);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取超期的任务列表
     */
    public List<ProductionTaskEx> getOverdueTasks() throws Exception {
        List<ProductionTaskEx> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;

            result = productionTaskMapperEx.getOverdueTasks(tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 更新任务进度
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateTaskProgress(Long taskId, BigDecimal completedQuantity, BigDecimal qualifiedQuantity,
                                 BigDecimal defectiveQuantity, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;

            result = productionTaskMapperEx.updateTaskProgress(taskId, completedQuantity, qualifiedQuantity, defectiveQuantity, updateTime, updateUser);

            // 记录日志
            logService.insertLog("生产任务",
                "更新任务进度，完成数量：" + completedQuantity,
                request);

        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 更新任务质量信息
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateTaskQuality(Long taskId, String qualityLevel, BigDecimal qualityScore, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;

            result = productionTaskMapperEx.updateTaskQuality(taskId, qualityLevel, qualityScore, updateTime, updateUser);

            // 记录日志
            logService.insertLog("生产任务",
                "更新任务质量，质量等级：" + qualityLevel,
                request);

        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 更新任务状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateTaskStatus(Long taskId, String status, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;

            result = productionTaskMapperEx.updateTaskStatus(taskId, status, updateTime, updateUser);

            // 记录日志
            logService.insertLog("生产任务",
                "更新任务状态：" + status,
                request);

        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
