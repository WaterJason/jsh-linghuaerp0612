package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.QualityInspection;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.QualityInspectionMapper;
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
import java.math.RoundingMode;
import java.util.Date;

/**
 * 质量检验Service
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class QualityInspectionService {
    
    private Logger logger = LoggerFactory.getLogger(QualityInspectionService.class);

    @Resource
    private QualityInspectionMapper qualityInspectionMapper;
    
    @Resource
    private UserService userService;
    
    @Resource
    private LogService logService;
    
    @Resource
    private ProductionTaskService productionTaskService;

    /**
     * 根据ID获取质量检验
     */
    public QualityInspection getQualityInspection(Long id) throws Exception {
        QualityInspection result = null;
        try {
            result = qualityInspectionMapper.selectById(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增质量检验
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertQualityInspection(JSONObject obj, HttpServletRequest request) throws Exception {
        QualityInspection inspection = JSONObject.parseObject(obj.toJSONString(), QualityInspection.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置基础信息
            inspection.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            inspection.setCreateUser(userInfo != null ? userInfo.getId() : null);
            inspection.setCreateTime(new Date());
            inspection.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 生成质检编号
            if (StringUtil.isEmpty(inspection.getInspectionNumber())) {
                inspection.setInspectionNumber(generateInspectionNumber());
            }
            
            // 设置质检时间
            if (inspection.getInspectionTime() == null) {
                inspection.setInspectionTime(new Date());
            }
            
            // 设置质检员信息
            if (inspection.getInspectorId() == null) {
                inspection.setInspectorId(userInfo != null ? userInfo.getId() : null);
                inspection.setInspectorName(userInfo != null ? userInfo.getUsername() : null);
            }
            
            // 设置默认质检类型
            if (StringUtil.isEmpty(inspection.getInspectionType())) {
                inspection.setInspectionType("FINAL");
            }
            
            // 初始化数量
            if (inspection.getQualifiedQuantity() == null) {
                inspection.setQualifiedQuantity(BigDecimal.ZERO);
            }
            if (inspection.getDefectiveQuantity() == null) {
                inspection.setDefectiveQuantity(BigDecimal.ZERO);
            }
            
            // 计算合格率
            if (inspection.getInspectionQuantity() != null && inspection.getInspectionQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal qualificationRate = inspection.getQualifiedQuantity()
                    .divide(inspection.getInspectionQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
                inspection.setQualificationRate(qualificationRate);
            } else {
                inspection.setQualificationRate(BigDecimal.ZERO);
            }
            
            // 计算综合评分
            calculateOverallScore(inspection);
            
            // 根据综合评分确定质量等级
            determineQualityGrade(inspection);
            
            // 根据合格率确定总体结果
            determineOverallResult(inspection);
            
            result = qualityInspectionMapper.insert(inspection);
            
            // 如果质检通过，更新任务状态为已质检
            if (result > 0 && "PASS".equals(inspection.getOverallResult())) {
                productionTaskService.updateTaskStatus(inspection.getTaskId(), "QUALITY_CHECKED", request);
                productionTaskService.updateTaskQuality(inspection.getTaskId(), 
                    inspection.getQualityGrade(), inspection.getOverallScore(), request);
            }
            
            // 记录日志
            logService.insertLog("质量检验",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append("质检任务：").append(inspection.getTaskNumber()).toString(),
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
     * 更新质量检验
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateQualityInspection(JSONObject obj, HttpServletRequest request) throws Exception {
        QualityInspection inspection = JSONObject.parseObject(obj.toJSONString(), QualityInspection.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置更新信息
            inspection.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            inspection.setUpdateTime(new Date());
            
            // 重新计算合格率
            if (inspection.getInspectionQuantity() != null && inspection.getInspectionQuantity().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal qualificationRate = inspection.getQualifiedQuantity()
                    .divide(inspection.getInspectionQuantity(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
                inspection.setQualificationRate(qualificationRate);
            }
            
            // 重新计算综合评分
            calculateOverallScore(inspection);
            
            // 重新确定质量等级
            determineQualityGrade(inspection);
            
            // 重新确定总体结果
            determineOverallResult(inspection);
            
            result = qualityInspectionMapper.updateById(inspection);
            
            // 记录日志
            logService.insertLog("质量检验",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append("质检任务：").append(inspection.getTaskNumber()).toString(),
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
     * 删除质量检验
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteQualityInspection(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            QualityInspection inspection = new QualityInspection();
            inspection.setId(id);
            inspection.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
            inspection.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            inspection.setUpdateTime(new Date());
            
            result = qualityInspectionMapper.updateById(inspection);
            
            // 记录日志
            logService.insertLog("质量检验",
                "删除质量检验",
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 生成质检编号
     */
    private String generateInspectionNumber() {
        // 生成格式：QI + YYYYMMDD + 6位序号
        String dateStr = Tools.dateToStr(new Date(), "yyyyMMdd");
        String prefix = "QI";
        
        // 这里可以调用序号服务生成唯一序号
        // 暂时使用时间戳后6位
        String suffix = String.format("%06d", System.currentTimeMillis() % 1000000);
        
        return prefix + dateStr + suffix;
    }

    /**
     * 计算综合评分
     */
    private void calculateOverallScore(QualityInspection inspection) {
        BigDecimal totalScore = BigDecimal.ZERO;
        int scoreCount = 0;
        
        if (inspection.getAppearanceScore() != null) {
            totalScore = totalScore.add(inspection.getAppearanceScore());
            scoreCount++;
        }
        if (inspection.getSizeScore() != null) {
            totalScore = totalScore.add(inspection.getSizeScore());
            scoreCount++;
        }
        if (inspection.getColorScore() != null) {
            totalScore = totalScore.add(inspection.getColorScore());
            scoreCount++;
        }
        if (inspection.getTextureScore() != null) {
            totalScore = totalScore.add(inspection.getTextureScore());
            scoreCount++;
        }
        if (inspection.getDetailScore() != null) {
            totalScore = totalScore.add(inspection.getDetailScore());
            scoreCount++;
        }
        if (inspection.getOverallEffectScore() != null) {
            totalScore = totalScore.add(inspection.getOverallEffectScore());
            scoreCount++;
        }
        
        if (scoreCount > 0) {
            BigDecimal overallScore = totalScore.divide(new BigDecimal(scoreCount), 1, RoundingMode.HALF_UP);
            inspection.setOverallScore(overallScore);
        } else {
            inspection.setOverallScore(BigDecimal.ZERO);
        }
    }

    /**
     * 确定质量等级
     */
    private void determineQualityGrade(QualityInspection inspection) {
        if (inspection.getOverallScore() == null) {
            inspection.setQualityGrade("D");
            return;
        }
        
        BigDecimal score = inspection.getOverallScore();
        if (score.compareTo(new BigDecimal("4.8")) >= 0) {
            inspection.setQualityGrade("A+");
        } else if (score.compareTo(new BigDecimal("4.5")) >= 0) {
            inspection.setQualityGrade("A");
        } else if (score.compareTo(new BigDecimal("4.0")) >= 0) {
            inspection.setQualityGrade("B");
        } else if (score.compareTo(new BigDecimal("3.0")) >= 0) {
            inspection.setQualityGrade("C");
        } else {
            inspection.setQualityGrade("D");
        }
    }

    /**
     * 确定总体结果
     */
    private void determineOverallResult(QualityInspection inspection) {
        if (inspection.getQualificationRate() == null) {
            inspection.setOverallResult("FAIL");
            return;
        }
        
        BigDecimal qualificationRate = inspection.getQualificationRate();
        BigDecimal overallScore = inspection.getOverallScore() != null ? inspection.getOverallScore() : BigDecimal.ZERO;
        
        // 合格率100%且综合评分>=4.0为合格
        if (qualificationRate.compareTo(new BigDecimal("100")) == 0 && overallScore.compareTo(new BigDecimal("4.0")) >= 0) {
            inspection.setOverallResult("PASS");
        } 
        // 合格率>=95%且综合评分>=3.5为需返工
        else if (qualificationRate.compareTo(new BigDecimal("95")) >= 0 && overallScore.compareTo(new BigDecimal("3.5")) >= 0) {
            inspection.setOverallResult("REWORK");
        } 
        // 其他情况为不合格
        else {
            inspection.setOverallResult("FAIL");
        }
    }

    /**
     * 质检确认
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int confirmQualityInspection(Long taskId, String overallResult, String qualityGrade,
                                       BigDecimal appearanceScore, BigDecimal sizeScore, BigDecimal colorScore,
                                       BigDecimal textureScore, BigDecimal detailScore, BigDecimal overallEffectScore,
                                       String improvementSuggestion, String qualityPhotos, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 创建质检记录
            QualityInspection inspection = new QualityInspection();
            inspection.setTaskId(taskId);
            inspection.setInspectorId(userInfo != null ? userInfo.getId() : null);
            inspection.setInspectorName(userInfo != null ? userInfo.getUsername() : null);
            inspection.setInspectionTime(new Date());
            inspection.setInspectionType("FINAL");
            inspection.setOverallResult(overallResult);
            inspection.setQualityGrade(qualityGrade);
            inspection.setAppearanceScore(appearanceScore);
            inspection.setSizeScore(sizeScore);
            inspection.setColorScore(colorScore);
            inspection.setTextureScore(textureScore);
            inspection.setDetailScore(detailScore);
            inspection.setOverallEffectScore(overallEffectScore);
            inspection.setImprovementSuggestion(improvementSuggestion);
            inspection.setQualityPhotos(qualityPhotos);
            
            // 计算综合评分
            calculateOverallScore(inspection);
            
            JSONObject obj = (JSONObject) JSONObject.toJSON(inspection);
            result = insertQualityInspection(obj, request);
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
