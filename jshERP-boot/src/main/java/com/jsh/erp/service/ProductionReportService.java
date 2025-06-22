package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.ProductionReport;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.ProductionReportMapper;
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

/**
 * 生产报工Service
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ProductionReportService {
    
    private Logger logger = LoggerFactory.getLogger(ProductionReportService.class);

    @Resource
    private ProductionReportMapper productionReportMapper;
    
    @Resource
    private UserService userService;
    
    @Resource
    private LogService logService;
    
    @Resource
    private ProductionTaskService productionTaskService;

    /**
     * 根据ID获取生产报工
     */
    public ProductionReport getProductionReport(Long id) throws Exception {
        ProductionReport result = null;
        try {
            result = productionReportMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增生产报工
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertProductionReport(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionReport report = JSONObject.parseObject(obj.toJSONString(), ProductionReport.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置基础信息
            report.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            report.setCreateUser(userInfo != null ? userInfo.getId() : null);
            report.setCreateTime(new Date());
            report.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 生成报工编号
            if (StringUtil.isEmpty(report.getReportNumber())) {
                report.setReportNumber(generateReportNumber());
            }
            
            // 设置报工时间
            if (report.getReportTime() == null) {
                report.setReportTime(new Date());
            }
            
            // 设置工作日期
            if (report.getWorkDate() == null) {
                report.setWorkDate(new Date());
            }
            
            // 设置默认报工类型
            if (StringUtil.isEmpty(report.getReportType())) {
                report.setReportType("PROGRESS");
            }
            
            // 初始化数量
            if (report.getCompletedQuantity() == null) {
                report.getCompletedQuantity();
            }
            if (report.getQualifiedQuantity() == null) {
                report.setQualifiedQuantity(BigDecimal.ZERO);
            }
            if (report.getDefectiveQuantity() == null) {
                report.setDefectiveQuantity(BigDecimal.ZERO);
            }
            
            // 初始化工时
            if (report.getWorkHours() == null) {
                report.setWorkHours(BigDecimal.ZERO);
            }
            
            // 初始化费用
            if (report.getUnitFee() == null) {
                report.setUnitFee(BigDecimal.ZERO);
            }
            if (report.getTotalFee() == null) {
                report.setTotalFee(BigDecimal.ZERO);
            }
            
            // 设置完成标记
            if (report.getIsCompleted() == null) {
                report.setIsCompleted(false);
            }
            
            result = productionReportMapper.insertSelective(report);
            
            // 如果是完工报工，更新任务状态
            if ("COMPLETE".equals(report.getReportType()) && report.getIsCompleted()) {
                productionTaskService.completeTask(
                    report.getTaskId(),
                    report.getCompletedQuantity(),
                    report.getQualifiedQuantity(),
                    report.getDefectiveQuantity(),
                    report.getWorkHours(),
                    request
                );
            }
            
            // 记录日志
            logService.insertLog("生产报工",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append("任务报工：").append(report.getTaskNumber()).toString(),
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
     * 更新生产报工
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateProductionReport(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionReport report = JSONObject.parseObject(obj.toJSONString(), ProductionReport.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置更新信息
            report.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            report.setUpdateTime(new Date());
            
            result = productionReportMapper.updateByPrimaryKeySelective(report);
            
            // 记录日志
            logService.insertLog("生产报工",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append("任务报工：").append(report.getTaskNumber()).toString(),
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
     * 删除生产报工
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteProductionReport(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            ProductionReport report = new ProductionReport();
            report.setId(id);
            report.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
            report.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            report.setUpdateTime(new Date());
            
            result = productionReportMapper.updateByPrimaryKeySelective(report);
            
            // 记录日志
            logService.insertLog("生产报工",
                "删除生产报工",
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 生成报工编号
     */
    private String generateReportNumber() {
        // 生成格式：PR + YYYYMMDD + 6位序号
        String dateStr = Tools.dateToStr(new Date(), "yyyyMMdd");
        String prefix = "PR";
        
        // 这里可以调用序号服务生成唯一序号
        // 暂时使用时间戳后6位
        String suffix = String.format("%06d", System.currentTimeMillis() % 1000000);
        
        return prefix + dateStr + suffix;
    }

    /**
     * 进度报工
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int progressReport(Long taskId, BigDecimal completedQuantity, BigDecimal qualifiedQuantity,
                             BigDecimal defectiveQuantity, BigDecimal workHours, String qualityLevel,
                             String workContent, String workPhotos, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 创建报工记录
            ProductionReport report = new ProductionReport();
            report.setTaskId(taskId);
            report.setWorkerId(userInfo != null ? userInfo.getId() : null);
            report.setWorkerName(userInfo != null ? userInfo.getUsername() : null);
            report.setReportType("PROGRESS");
            report.setReportTime(new Date());
            report.setWorkDate(new Date());
            report.setCompletedQuantity(completedQuantity);
            report.setQualifiedQuantity(qualifiedQuantity);
            report.setDefectiveQuantity(defectiveQuantity);
            report.setWorkHours(workHours);
            report.setQualityLevel(qualityLevel);
            report.setWorkContent(workContent);
            report.setWorkPhotos(workPhotos);
            report.setIsCompleted(false);
            
            JSONObject obj = (JSONObject) JSONObject.toJSON(report);
            result = insertProductionReport(obj, request);
            
            // 更新任务进度
            if (result > 0) {
                productionTaskService.updateTaskProgress(taskId, completedQuantity, qualifiedQuantity, defectiveQuantity, request);
            }
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 完工报工
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int completeReport(Long taskId, BigDecimal completedQuantity, BigDecimal qualifiedQuantity,
                             BigDecimal defectiveQuantity, BigDecimal workHours, String qualityLevel,
                             String workContent, String workPhotos, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 创建完工报工记录
            ProductionReport report = new ProductionReport();
            report.setTaskId(taskId);
            report.setWorkerId(userInfo != null ? userInfo.getId() : null);
            report.setWorkerName(userInfo != null ? userInfo.getUsername() : null);
            report.setReportType("COMPLETE");
            report.setReportTime(new Date());
            report.setWorkDate(new Date());
            report.setCompletedQuantity(completedQuantity);
            report.setQualifiedQuantity(qualifiedQuantity);
            report.setDefectiveQuantity(defectiveQuantity);
            report.setWorkHours(workHours);
            report.setQualityLevel(qualityLevel);
            report.setWorkContent(workContent);
            report.setWorkPhotos(workPhotos);
            report.setIsCompleted(true);
            
            JSONObject obj = (JSONObject) JSONObject.toJSON(report);
            result = insertProductionReport(obj, request);
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
