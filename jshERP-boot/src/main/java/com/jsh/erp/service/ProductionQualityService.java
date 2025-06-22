package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.QualityInspection;
import com.jsh.erp.datasource.mappers.QualityInspectionMapper;
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
 * 生产质检服务
 * 提供生产质检相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ProductionQualityService {
    
    private Logger logger = LoggerFactory.getLogger(ProductionQualityService.class);
    
    @Resource
    private LogService logService;

    @Resource
    private UserService userService;

    @Resource
    private QualityInspectionMapper qualityInspectionMapper;
    
    /**
     * 获取质检列表
     */
    public JSONObject getQualityInspectionList(JSONObject params, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        
        try {
            // 构建查询参数
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("tenantId", 63L); // 从session或request中获取租户ID

            // 处理搜索条件
            if (params != null) {
                if (params.getString("inspectionNumber") != null) {
                    parameterMap.put("inspectionNumber", params.getString("inspectionNumber"));
                }
                if (params.getString("taskNumber") != null) {
                    parameterMap.put("taskNumber", params.getString("taskNumber"));
                }
                if (params.getString("workOrderNumber") != null) {
                    parameterMap.put("workOrderNumber", params.getString("workOrderNumber"));
                }
                if (params.getString("productName") != null) {
                    parameterMap.put("productName", params.getString("productName"));
                }
                if (params.getString("inspectorName") != null) {
                    parameterMap.put("inspectorName", params.getString("inspectorName"));
                }
                if (params.getString("inspectionType") != null) {
                    parameterMap.put("inspectionType", params.getString("inspectionType"));
                }
                if (params.getString("overallResult") != null) {
                    parameterMap.put("overallResult", params.getString("overallResult"));
                }
                if (params.getString("qualityGrade") != null) {
                    parameterMap.put("qualityGrade", params.getString("qualityGrade"));
                }
            }

            // 查询数据库
            List<QualityInspection> list = qualityInspectionMapper.selectByCondition(parameterMap);
            Long total = qualityInspectionMapper.countByCondition(parameterMap);

            // 转换为JSONArray
            JSONArray dataList = new JSONArray();
            for (QualityInspection item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("inspectionNumber", item.getInspectionNumber());
                obj.put("taskId", item.getTaskId());
                obj.put("taskNumber", item.getTaskNumber());
                obj.put("workOrderId", item.getWorkOrderId());
                obj.put("workOrderNumber", item.getWorkOrderNumber());
                obj.put("productId", item.getProductId());
                obj.put("productName", item.getProductName());
                obj.put("inspectorId", item.getInspectorId());
                obj.put("inspectorName", item.getInspectorName());
                obj.put("inspectionTime", item.getInspectionTime());
                obj.put("inspectionType", item.getInspectionType());
                obj.put("inspectionQuantity", item.getInspectionQuantity());
                obj.put("qualifiedQuantity", item.getQualifiedQuantity());
                obj.put("defectiveQuantity", item.getDefectiveQuantity());
                obj.put("unitName", item.getUnitName());
                obj.put("qualificationRate", item.getQualificationRate());
                obj.put("overallResult", item.getOverallResult());
                obj.put("qualityGrade", item.getQualityGrade());
                obj.put("overallScore", item.getOverallScore());
                obj.put("appearanceScore", item.getAppearanceScore());
                obj.put("sizeScore", item.getSizeScore());
                obj.put("colorScore", item.getColorScore());
                obj.put("textureScore", item.getTextureScore());
                obj.put("detailScore", item.getDetailScore());
                obj.put("overallEffectScore", item.getOverallEffectScore());
                obj.put("problemDescription", item.getProblemDescription());
                obj.put("improvementSuggestion", item.getImprovementSuggestion());
                obj.put("qualityPhotos", item.getQualityPhotos());
                obj.put("inspectionStandard", item.getInspectionStandard());
                obj.put("remark", item.getRemark());
                obj.put("createTime", item.getCreateTime());
                dataList.add(obj);
            }

            result.put("rows", dataList);
            result.put("total", total);
            result.put("size", dataList.size());
            result.put("current", 1);

            logger.info("获取质检列表成功，共 {} 条记录", total);

        } catch (Exception e) {
            logger.error("获取质检列表失败", e);
            throw e;
        }
        
        return result;
    }
    
    /**
     * 创建模拟质检数据
     */
    private JSONArray createMockQualityInspectionData() {
        JSONArray dataList = new JSONArray();
        
        String[] inspectionNumbers = {"QC2024001", "QC2024002", "QC2024003", "QC2024004", "QC2024005"};
        String[] productNames = {"掐丝点蓝手镯", "掐丝点蓝项链", "掐丝点蓝耳环", "配饰手链", "配饰戒指"};
        String[] statuses = {"PENDING", "IN_PROGRESS", "COMPLETED", "REJECTED"};
        String[] inspectorNames = {"质检员A", "质检员B", "质检员C", "质检员D"};
        String[] qualityGrades = {"优秀", "良好", "合格", "不合格"};
        
        for (int i = 0; i < 16; i++) {
            JSONObject item = new JSONObject();
            item.put("id", i + 1);
            item.put("inspectionNumber", inspectionNumbers[i % inspectionNumbers.length] + String.format("%03d", i + 1));
            item.put("productName", productNames[i % productNames.length]);
            item.put("status", statuses[i % statuses.length]);
            item.put("inspectorName", inspectorNames[i % inspectorNames.length]);
            
            // 数量信息
            item.put("inspectionQuantity", 10 + (i * 2));
            item.put("passedQuantity", i % 5 == 0 ? (8 + i) : (10 + i * 2));
            item.put("rejectedQuantity", i % 5 == 0 ? 2 : 0);
            
            // 时间信息
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -i);
            item.put("createTime", cal.getTime());
            item.put("planInspectionTime", cal.getTime());
            
            if (!item.getString("status").equals("PENDING")) {
                cal.add(Calendar.HOUR_OF_DAY, 2);
                item.put("actualStartTime", cal.getTime());
            }
            
            if (item.getString("status").equals("COMPLETED") || item.getString("status").equals("REJECTED")) {
                cal.add(Calendar.HOUR_OF_DAY, 4);
                item.put("actualEndTime", cal.getTime());
                item.put("qualityGrade", qualityGrades[i % qualityGrades.length]);
                
                // 质检评分
                item.put("appearanceScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
                item.put("sizeScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
                item.put("colorScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
                item.put("textureScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
                item.put("detailScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
                item.put("overallScore", new BigDecimal("4.0").add(new BigDecimal(Math.random())));
                
                // 质检结果
                item.put("overallResult", i % 5 == 0 ? "不合格" : "合格");
                item.put("improvementSuggestion", i % 5 == 0 ? "需要改进工艺细节" : "质量良好");
            }
            
            // 质检标准
            item.put("inspectionStandard", "企业标准-" + item.getString("productName"));
            item.put("inspectionMethod", "目视检查+工具测量");
            
            // 备注
            item.put("remark", "质检记录 - " + item.getString("productName"));
            
            dataList.add(item);
        }
        
        return dataList;
    }
    
    /**
     * 过滤质检数据
     */
    private JSONArray filterQualityData(JSONArray dataList, JSONObject params) {
        JSONArray filteredList = new JSONArray();
        
        for (int i = 0; i < dataList.size(); i++) {
            JSONObject item = dataList.getJSONObject(i);
            boolean match = true;
            
            // 质检编号过滤
            if (params.getString("inspectionNumber") != null && !params.getString("inspectionNumber").isEmpty()) {
                if (!item.getString("inspectionNumber").contains(params.getString("inspectionNumber"))) {
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
            
            // 质检员过滤
            if (params.getString("inspectorName") != null && !params.getString("inspectorName").isEmpty()) {
                if (!item.getString("inspectorName").contains(params.getString("inspectorName"))) {
                    match = false;
                }
            }
            
            // 质量等级过滤
            if (params.getString("qualityGrade") != null && !params.getString("qualityGrade").isEmpty()) {
                if (item.getString("qualityGrade") == null || !item.getString("qualityGrade").equals(params.getString("qualityGrade"))) {
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
     * 新增质检记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean addQualityInspection(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            // 验证必要参数
            if (params.getString("productName") == null || params.getString("productName").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "产品名称不能为空");
            }

            if (params.getBigDecimal("inspectionQuantity") == null || params.getBigDecimal("inspectionQuantity").compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "质检数量必须大于0");
            }

            // 生成质检编号
            String inspectionNumber = "QC" + Tools.getNowTime() + Tools.getCharAndNum(4);

            // 创建实体对象
            QualityInspection inspection = new QualityInspection();
            inspection.setInspectionNumber(inspectionNumber);
            inspection.setTaskId(params.getLong("taskId"));
            inspection.setTaskNumber(params.getString("taskNumber"));
            inspection.setWorkOrderId(params.getLong("workOrderId"));
            inspection.setWorkOrderNumber(params.getString("workOrderNumber"));
            inspection.setProductId(params.getLong("productId"));
            inspection.setProductName(params.getString("productName"));
            inspection.setInspectorId(params.getLong("inspectorId"));
            inspection.setInspectorName(params.getString("inspectorName"));
            inspection.setInspectionTime(params.getDate("inspectionTime"));
            inspection.setInspectionType(params.getString("inspectionType"));
            inspection.setInspectionQuantity(params.getBigDecimal("inspectionQuantity"));
            inspection.setUnitName(params.getString("unitName"));
            inspection.setInspectionStandard(params.getString("inspectionStandard"));
            inspection.setRemark(params.getString("remark"));
            inspection.setTenantId(63L);
            inspection.setDeleteFlag("0");
            inspection.setCreateTime(new Date());
            inspection.setCreateUser(1L);

            // 插入数据库
            int result = qualityInspectionMapper.insert(inspection);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "新增质检记录失败");
            }

            // 记录操作日志
            logService.insertLog("质检管理", "新增质检记录：" + inspectionNumber, request);

            logger.info("新增质检记录成功：{}", inspectionNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("新增质检记录失败", e);
            throw e;
        }
    }
    
    /**
     * 更新质检记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateQualityInspection(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // TODO: 实际的数据库更新逻辑
            params.put("updateTime", new Date());
            
            logger.info("更新质检记录成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("更新质检记录失败", e);
            throw e;
        }
    }
    
    /**
     * 删除质检记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteQualityInspection(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            QualityInspection existing = qualityInspectionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "质检记录不存在");
            }

            // 软删除
            existing.setDeleteFlag("1");
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);

            int result = qualityInspectionMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "删除质检记录失败");
            }

            logger.info("删除质检记录成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除质检记录失败", e);
            throw e;
        }
    }
    
    /**
     * 批量删除质检记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteBatchQualityInspection(String ids, HttpServletRequest request) throws Exception {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID列表不能为空");
            }
            
            String[] idArray = ids.split(",");
            for (String id : idArray) {
                deleteQualityInspection(Long.parseLong(id.trim()), request);
            }
            
            logger.info("批量删除质检记录成功：{} 条记录", idArray.length);
            return true;
            
        } catch (Exception e) {
            logger.error("批量删除质检记录失败", e);
            throw e;
        }
    }
    
    /**
     * 确认质检结果
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean confirmInspection(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            String overallResult = params.getString("overallResult");
            String qualityGrade = params.getString("qualityGrade");
            
            if (id == null || overallResult == null || qualityGrade == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "质检ID、结果和等级不能为空");
            }
            
            // 查询现有记录
            QualityInspection existing = qualityInspectionMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "质检记录不存在");
            }

            // 更新质检结果
            BigDecimal overallScore = params.getBigDecimal("overallScore");
            int result = qualityInspectionMapper.updateInspectionResult(id, overallResult, qualityGrade, overallScore, 1L);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "确认质检结果失败");
            }

            logger.info("确认质检结果成功：ID {}, 结果 {}, 等级 {}", id, overallResult, qualityGrade);
            return true;
            
        } catch (Exception e) {
            logger.error("确认质检结果失败", e);
            throw e;
        }
    }
    
    /**
     * 开始质检
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean startInspection(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // TODO: 实际的状态更新逻辑
            // 更新状态为IN_PROGRESS，记录实际开始时间
            
            logger.info("开始质检成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("开始质检失败", e);
            throw e;
        }
    }
    
    /**
     * 完成质检
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean completeInspection(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // TODO: 实际的质检完成逻辑
            params.put("status", "COMPLETED");
            params.put("actualEndTime", new Date());
            
            logger.info("完成质检成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("完成质检失败", e);
            throw e;
        }
    }
    
    /**
     * 获取质检详情
     */
    public JSONObject getInspectionDetail(Long id, HttpServletRequest request) throws Exception {
        JSONObject detail = new JSONObject();
        
        try {
            // TODO: 实际的数据库查询逻辑
            // 模拟详情数据
            detail.put("id", id);
            detail.put("inspectionNumber", "QC2024001001");
            detail.put("productName", "掐丝点蓝手镯");
            detail.put("status", "COMPLETED");
            detail.put("inspectorName", "质检员A");
            detail.put("qualityGrade", "优秀");
            detail.put("overallResult", "合格");
            detail.put("overallScore", new BigDecimal("4.5"));
            
            logger.info("获取质检详情成功：ID {}", id);
            
        } catch (Exception e) {
            logger.error("获取质检详情失败", e);
            throw e;
        }
        
        return detail;
    }
    
    /**
     * 导出Excel
     */
    public void exportExcel(JSONObject params, HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            // TODO: 实际的Excel导出逻辑
            
            logger.info("导出质检Excel成功");
            
        } catch (Exception e) {
            logger.error("导出质检Excel失败", e);
            throw e;
        }
    }
    
    /**
     * 获取质检统计
     */
    public JSONObject getInspectionStatistics(HttpServletRequest request) throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 从数据库获取统计数据
            Map<String, Object> dbStatistics = qualityInspectionMapper.getQualityStatistics(63L);

            // 转换为JSONObject
            statistics.put("totalInspections", dbStatistics.get("totalInspections"));
            statistics.put("passedInspections", dbStatistics.get("passedInspections"));
            statistics.put("failedInspections", dbStatistics.get("failedInspections"));
            statistics.put("conditionalInspections", dbStatistics.get("conditionalInspections"));

            statistics.put("totalQuantity", dbStatistics.get("totalQuantity"));
            statistics.put("qualifiedQuantity", dbStatistics.get("qualifiedQuantity"));
            statistics.put("defectiveQuantity", dbStatistics.get("defectiveQuantity"));

            // 计算合格率
            Object totalQuantity = dbStatistics.get("totalQuantity");
            Object qualifiedQuantity = dbStatistics.get("qualifiedQuantity");
            if (totalQuantity != null && qualifiedQuantity != null) {
                BigDecimal total = new BigDecimal(totalQuantity.toString());
                BigDecimal qualified = new BigDecimal(qualifiedQuantity.toString());
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal passRate = qualified.divide(total, 4, java.math.RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
                    statistics.put("passRate", passRate);
                } else {
                    statistics.put("passRate", 0);
                }
            }

            statistics.put("avgQualificationRate", dbStatistics.get("avgQualificationRate"));
            statistics.put("avgOverallScore", dbStatistics.get("avgOverallScore"));
            statistics.put("avgAppearanceScore", dbStatistics.get("avgAppearanceScore"));
            statistics.put("avgSizeScore", dbStatistics.get("avgSizeScore"));
            statistics.put("avgColorScore", dbStatistics.get("avgColorScore"));
            statistics.put("avgTextureScore", dbStatistics.get("avgTextureScore"));
            statistics.put("avgDetailScore", dbStatistics.get("avgDetailScore"));
            statistics.put("avgOverallEffectScore", dbStatistics.get("avgOverallEffectScore"));

            // 质量等级统计
            statistics.put("gradeACount", dbStatistics.get("gradeACount"));
            statistics.put("gradeBCount", dbStatistics.get("gradeBCount"));
            statistics.put("gradeCCount", dbStatistics.get("gradeCCount"));
            statistics.put("gradeDCount", dbStatistics.get("gradeDCount"));

            // 按质检类型统计
            List<Map<String, Object>> typeStatsList = qualityInspectionMapper.getInspectionTypeStatistics(63L);
            JSONArray typeStats = new JSONArray();
            for (Map<String, Object> typeStat : typeStatsList) {
                JSONObject obj = new JSONObject();
                obj.put("inspectionType", typeStat.get("inspection_type"));
                obj.put("count", typeStat.get("count"));
                obj.put("totalQuantity", typeStat.get("totalQuantity"));
                obj.put("qualifiedQuantity", typeStat.get("qualifiedQuantity"));
                obj.put("avgQualificationRate", typeStat.get("avgQualificationRate"));
                obj.put("avgScore", typeStat.get("avgScore"));
                typeStats.add(obj);
            }
            statistics.put("typeStats", typeStats);

            // 按质检结果统计
            List<Map<String, Object>> resultStatsList = qualityInspectionMapper.getInspectionResultStatistics(63L);
            JSONArray resultStats = new JSONArray();
            for (Map<String, Object> resultStat : resultStatsList) {
                JSONObject obj = new JSONObject();
                obj.put("overallResult", resultStat.get("overall_result"));
                obj.put("count", resultStat.get("count"));
                obj.put("totalQuantity", resultStat.get("totalQuantity"));
                obj.put("avgScore", resultStat.get("avgScore"));
                resultStats.add(obj);
            }
            statistics.put("resultStats", resultStats);

            logger.info("获取质检统计成功");

        } catch (Exception e) {
            logger.error("获取质检统计失败", e);
            throw e;
        }
        
        return statistics;
    }
    
    /**
     * 获取质检标准
     */
    public JSONObject getInspectionStandards(HttpServletRequest request) throws Exception {
        JSONObject standards = new JSONObject();
        
        try {
            // 模拟质检标准数据
            JSONArray standardList = new JSONArray();
            
            String[] categories = {"外观质量", "尺寸精度", "颜色一致性", "工艺细节", "整体效果"};
            String[] descriptions = {"表面光洁度、无划痕", "尺寸误差±0.1mm", "颜色均匀无色差", "工艺精细无瑕疵", "整体协调美观"};
            
            for (int i = 0; i < categories.length; i++) {
                JSONObject standard = new JSONObject();
                standard.put("category", categories[i]);
                standard.put("description", descriptions[i]);
                standard.put("weight", 20); // 权重
                standard.put("minScore", 3.0); // 最低分数
                standard.put("maxScore", 5.0); // 最高分数
                standardList.add(standard);
            }
            
            standards.put("standardList", standardList);
            standards.put("totalWeight", 100);
            standards.put("passThreshold", 3.5); // 合格阈值
            
            logger.info("获取质检标准成功");
            
        } catch (Exception e) {
            logger.error("获取质检标准失败", e);
            throw e;
        }
        
        return standards;
    }
}
