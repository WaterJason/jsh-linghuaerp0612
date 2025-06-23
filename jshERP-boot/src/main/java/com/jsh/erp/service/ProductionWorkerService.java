package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.ProductionWorker;
import com.jsh.erp.datasource.mappers.ProductionWorkerMapper;
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
 * 生产工人服务
 * 提供生产工人相关的业务逻辑
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ProductionWorkerService {
    
    private Logger logger = LoggerFactory.getLogger(ProductionWorkerService.class);
    
    @Resource
    private LogService logService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private ProductionWorkerMapper productionWorkerMapper;
    
    /**
     * 获取工人列表
     */
    public JSONObject getProductionWorkerList(JSONObject params, HttpServletRequest request) throws Exception {
        JSONObject result = new JSONObject();
        
        try {
            // 构建查询参数
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("tenantId", 63L); // 从session或request中获取租户ID
            
            // 处理搜索条件
            if (params != null) {
                if (params.getString("workerNumber") != null) {
                    parameterMap.put("workerNumber", params.getString("workerNumber"));
                }
                if (params.getString("workerName") != null) {
                    parameterMap.put("workerName", params.getString("workerName"));
                }
                if (params.getString("department") != null) {
                    parameterMap.put("department", params.getString("department"));
                }
                if (params.getString("position") != null) {
                    parameterMap.put("position", params.getString("position"));
                }
                if (params.getString("skillLevel") != null) {
                    parameterMap.put("skillLevel", params.getString("skillLevel"));
                }
                if (params.getString("status") != null) {
                    parameterMap.put("status", params.getString("status"));
                }
            }
            
            // 查询数据库
            List<ProductionWorker> list = productionWorkerMapper.selectByCondition(parameterMap);
            Long total = productionWorkerMapper.countByCondition(parameterMap);
            
            // 转换为JSONArray
            JSONArray dataList = new JSONArray();
            for (ProductionWorker item : list) {
                JSONObject obj = new JSONObject();
                obj.put("id", item.getId());
                obj.put("workerNumber", item.getWorkerNumber());
                obj.put("workerName", item.getWorkerName());
                obj.put("department", item.getDepartment());
                obj.put("position", item.getPosition());
                obj.put("skillLevel", item.getSkillLevel());
                obj.put("specialties", item.getSpecialties());
                obj.put("experienceYears", item.getExperienceYears());
                obj.put("status", item.getStatus());
                obj.put("phone", item.getPhone());
                obj.put("email", item.getEmail());
                obj.put("hireDate", item.getHireDate());
                obj.put("hourlyRate", item.getHourlyRate());
                obj.put("monthlyTarget", item.getMonthlyTarget());
                obj.put("currentWorkload", item.getCurrentWorkload());
                obj.put("maxWorkload", item.getMaxWorkload());
                obj.put("qualityRating", item.getQualityRating());
                obj.put("efficiencyRating", item.getEfficiencyRating());
                obj.put("createTime", item.getCreateTime());
                dataList.add(obj);
            }
            
            result.put("rows", dataList);
            result.put("total", total);
            result.put("size", dataList.size());
            result.put("current", 1);
            
            logger.info("获取工人列表成功，共 {} 条记录", total);
            
        } catch (Exception e) {
            logger.error("获取工人列表失败", e);
            throw e;
        }
        
        return result;
    }
    
    /**
     * 新增工人
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean addProductionWorker(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            // 验证必要参数
            if (params.getString("workerName") == null || params.getString("workerName").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "工人姓名不能为空");
            }
            
            if (params.getString("department") == null || params.getString("department").isEmpty()) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "部门不能为空");
            }
            
            // 生成工人编号
            String workerNumber = "W" + Tools.getNowTime() + Tools.getCharAndNum(4);
            
            // 创建实体对象
            ProductionWorker worker = new ProductionWorker();
            worker.setWorkerNumber(workerNumber);
            worker.setWorkerName(params.getString("workerName"));
            worker.setDepartment(params.getString("department"));
            worker.setPosition(params.getString("position"));
            worker.setSkillLevel(params.getString("skillLevel"));
            worker.setSpecialties(params.getString("specialties"));
            worker.setExperienceYears(params.getInteger("experienceYears"));
            worker.setStatus("ACTIVE");
            worker.setPhone(params.getString("phone"));
            worker.setEmail(params.getString("email"));
            worker.setHireDate(params.getDate("hireDate"));
            worker.setHourlyRate(params.getBigDecimal("hourlyRate"));
            worker.setMonthlyTarget(params.getBigDecimal("monthlyTarget"));
            worker.setCurrentWorkload(0);
            worker.setMaxWorkload(params.getInteger("maxWorkload"));
            worker.setQualityRating(new BigDecimal("5.0"));
            worker.setEfficiencyRating(new BigDecimal("5.0"));
            worker.setTenantId(63L);
            worker.setDeleteFlag("0");
            worker.setCreateTime(new Date());
            worker.setCreateUser(1L);
            
            // 插入数据库
            int result = productionWorkerMapper.insert(worker);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "新增工人失败");
            }
            
            // 记录操作日志
            logService.insertLog("工人管理", "新增工人：" + workerNumber, request);
            
            logger.info("新增工人成功：{}", workerNumber);
            return true;
            
        } catch (Exception e) {
            logger.error("新增工人失败", e);
            throw e;
        }
    }
    
    /**
     * 更新工人信息
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean updateProductionWorker(JSONObject params, HttpServletRequest request) throws Exception {
        try {
            Long id = params.getLong("id");
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            ProductionWorker existing = productionWorkerMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "工人记录不存在");
            }
            
            // 更新字段
            if (params.getString("workerName") != null) {
                existing.setWorkerName(params.getString("workerName"));
            }
            if (params.getString("department") != null) {
                existing.setDepartment(params.getString("department"));
            }
            if (params.getString("position") != null) {
                existing.setPosition(params.getString("position"));
            }
            if (params.getString("skillLevel") != null) {
                existing.setSkillLevel(params.getString("skillLevel"));
            }
            if (params.getString("specialties") != null) {
                existing.setSpecialties(params.getString("specialties"));
            }
            if (params.getInteger("experienceYears") != null) {
                existing.setExperienceYears(params.getInteger("experienceYears"));
            }
            if (params.getString("status") != null) {
                existing.setStatus(params.getString("status"));
            }
            if (params.getString("phone") != null) {
                existing.setPhone(params.getString("phone"));
            }
            if (params.getString("email") != null) {
                existing.setEmail(params.getString("email"));
            }
            if (params.getBigDecimal("hourlyRate") != null) {
                existing.setHourlyRate(params.getBigDecimal("hourlyRate"));
            }
            if (params.getBigDecimal("monthlyTarget") != null) {
                existing.setMonthlyTarget(params.getBigDecimal("monthlyTarget"));
            }
            if (params.getInteger("maxWorkload") != null) {
                existing.setMaxWorkload(params.getInteger("maxWorkload"));
            }
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);
            
            // 更新数据库
            int result = productionWorkerMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "更新工人信息失败");
            }
            
            logger.info("更新工人信息成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("更新工人信息失败", e);
            throw e;
        }
    }
    
    /**
     * 删除工人
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean deleteProductionWorker(Long id, HttpServletRequest request) throws Exception {
        try {
            if (id == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "ID不能为空");
            }
            
            // 查询现有记录
            ProductionWorker existing = productionWorkerMapper.selectById(id);
            if (existing == null || !"0".equals(existing.getDeleteFlag())) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "工人记录不存在");
            }
            
            // 检查是否有正在进行的任务
            if (existing.getCurrentWorkload() != null && existing.getCurrentWorkload() > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "工人有正在进行的任务，不能删除");
            }
            
            // 软删除
            existing.setDeleteFlag("1");
            existing.setUpdateTime(new Date());
            existing.setUpdateUser(1L);
            
            int result = productionWorkerMapper.updateById(existing);
            if (result <= 0) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_WRITE_FAIL_CODE, "删除工人失败");
            }
            
            logger.info("删除工人成功：ID {}", id);
            return true;
            
        } catch (Exception e) {
            logger.error("删除工人失败", e);
            throw e;
        }
    }
    
    /**
     * 获取工人统计
     */
    public JSONObject getWorkerStatistics(HttpServletRequest request) throws Exception {
        JSONObject statistics = new JSONObject();
        
        try {
            // 从数据库获取统计数据
            Map<String, Object> dbStatistics = productionWorkerMapper.getWorkerStatistics(63L);
            
            // 转换为JSONObject
            statistics.put("totalWorkers", dbStatistics.get("totalWorkers"));
            statistics.put("activeWorkers", dbStatistics.get("activeWorkers"));
            statistics.put("inactiveWorkers", dbStatistics.get("inactiveWorkers"));
            statistics.put("avgExperienceYears", dbStatistics.get("avgExperienceYears"));
            statistics.put("avgQualityRating", dbStatistics.get("avgQualityRating"));
            statistics.put("avgEfficiencyRating", dbStatistics.get("avgEfficiencyRating"));
            statistics.put("totalCurrentWorkload", dbStatistics.get("totalCurrentWorkload"));
            statistics.put("avgCurrentWorkload", dbStatistics.get("avgCurrentWorkload"));
            
            // 按部门统计
            List<Map<String, Object>> deptStatsList = productionWorkerMapper.getDepartmentStatistics(63L);
            JSONArray deptStats = new JSONArray();
            for (Map<String, Object> deptStat : deptStatsList) {
                JSONObject obj = new JSONObject();
                obj.put("department", deptStat.get("department"));
                obj.put("count", deptStat.get("count"));
                obj.put("avgQualityRating", deptStat.get("avgQualityRating"));
                obj.put("avgEfficiencyRating", deptStat.get("avgEfficiencyRating"));
                deptStats.add(obj);
            }
            statistics.put("deptStats", deptStats);
            
            // 按技能等级统计
            List<Map<String, Object>> skillStatsList = productionWorkerMapper.getSkillLevelStatistics(63L);
            JSONArray skillStats = new JSONArray();
            for (Map<String, Object> skillStat : skillStatsList) {
                JSONObject obj = new JSONObject();
                obj.put("skillLevel", skillStat.get("skill_level"));
                obj.put("count", skillStat.get("count"));
                obj.put("avgQualityRating", skillStat.get("avgQualityRating"));
                skillStats.add(obj);
            }
            statistics.put("skillStats", skillStats);
            
            logger.info("获取工人统计成功");
            
        } catch (Exception e) {
            logger.error("获取工人统计失败", e);
            throw e;
        }
        
        return statistics;
    }
}
