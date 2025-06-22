package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.entities.WorkerSkill;
import com.jsh.erp.datasource.mappers.WorkerSkillMapper;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工人技能Service
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class WorkerSkillService {
    
    private Logger logger = LoggerFactory.getLogger(WorkerSkillService.class);

    @Resource
    private WorkerSkillMapper workerSkillMapper;
    
    @Resource
    private UserService userService;
    
    @Resource
    private LogService logService;

    /**
     * 根据ID获取工人技能
     */
    public WorkerSkill getWorkerSkill(Long id) throws Exception {
        WorkerSkill result = null;
        try {
            result = workerSkillMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增工人技能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertWorkerSkill(JSONObject obj, HttpServletRequest request) throws Exception {
        WorkerSkill skill = JSONObject.parseObject(obj.toJSONString(), WorkerSkill.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置基础信息
            skill.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            skill.setCreateUser(userInfo != null ? userInfo.getId() : null);
            skill.setCreateTime(new Date());
            skill.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 设置默认技能等级
            if (skill.getSkillLevel() == null) {
                skill.setSkillLevel("BEGINNER");
            }
            
            // 初始化技能评分
            if (skill.getSkillScore() == null) {
                skill.setSkillScore(new BigDecimal("3.0"));
            }
            
            // 初始化经验年限
            if (skill.getExperienceYears() == null) {
                skill.setExperienceYears(BigDecimal.ZERO);
            }
            
            // 初始化薪酬
            if (skill.getHourlyRate() == null) {
                skill.setHourlyRate(BigDecimal.ZERO);
            }
            if (skill.getPieceRate() == null) {
                skill.setPieceRate(BigDecimal.ZERO);
            }
            
            // 初始化效率系数
            if (skill.getEfficiencyRate() == null) {
                skill.setEfficiencyRate(new BigDecimal("100"));
            }
            if (skill.getQualityRate() == null) {
                skill.setQualityRate(new BigDecimal("100"));
            }
            
            // 设置默认启用状态
            if (skill.getIsActive() == null) {
                skill.setIsActive(true);
            }
            
            result = workerSkillMapper.insertSelective(skill);
            
            // 记录日志
            logService.insertLog("工人技能",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(skill.getWorkerName()).append("-").append(skill.getSkillType()).toString(),
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
     * 更新工人技能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateWorkerSkill(JSONObject obj, HttpServletRequest request) throws Exception {
        WorkerSkill skill = JSONObject.parseObject(obj.toJSONString(), WorkerSkill.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置更新信息
            skill.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            skill.setUpdateTime(new Date());
            
            result = workerSkillMapper.updateByPrimaryKeySelective(skill);
            
            // 记录日志
            logService.insertLog("工人技能",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(skill.getWorkerName()).append("-").append(skill.getSkillType()).toString(),
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
     * 删除工人技能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteWorkerSkill(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            WorkerSkill skill = new WorkerSkill();
            skill.setId(id);
            skill.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
            skill.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            skill.setUpdateTime(new Date());
            
            result = workerSkillMapper.updateByPrimaryKeySelective(skill);
            
            // 记录日志
            logService.insertLog("工人技能",
                "删除工人技能",
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 更新技能等级
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateSkillLevel(Long workerId, String skillType, String skillLevel, 
                               BigDecimal skillScore, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 这里需要根据workerId和skillType查找对应的技能记录
            // 暂时简化处理，实际应该先查询再更新
            WorkerSkill skill = new WorkerSkill();
            skill.setWorkerId(workerId);
            skill.setSkillType(skillType);
            skill.setSkillLevel(skillLevel);
            skill.setSkillScore(skillScore);
            skill.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            skill.setUpdateTime(new Date());
            
            // 根据技能等级调整薪酬
            adjustSalaryBySkillLevel(skill);
            
            result = workerSkillMapper.updateByPrimaryKeySelective(skill);
            
            // 记录日志
            logService.insertLog("工人技能",
                "更新技能等级：" + skillLevel,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 技能认证
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int certifySkill(Long workerId, String skillType, String certificationLevel, 
                           Date certificationDate, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            WorkerSkill skill = new WorkerSkill();
            skill.setWorkerId(workerId);
            skill.setSkillType(skillType);
            skill.setCertificationLevel(certificationLevel);
            skill.setCertificationDate(certificationDate);
            skill.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            skill.setUpdateTime(new Date());
            
            result = workerSkillMapper.updateByPrimaryKeySelective(skill);
            
            // 记录日志
            logService.insertLog("工人技能",
                "技能认证：" + certificationLevel,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 根据技能等级调整薪酬
     */
    private void adjustSalaryBySkillLevel(WorkerSkill skill) {
        String skillLevel = skill.getSkillLevel();
        if (skillLevel == null) {
            return;
        }
        
        // 根据技能等级设置基础薪酬
        switch (skillLevel) {
            case "BEGINNER":
                if (skill.getHourlyRate() == null) {
                    skill.setHourlyRate(new BigDecimal("20"));
                }
                if (skill.getPieceRate() == null) {
                    skill.setPieceRate(new BigDecimal("10"));
                }
                break;
            case "INTERMEDIATE":
                if (skill.getHourlyRate() == null) {
                    skill.setHourlyRate(new BigDecimal("25"));
                }
                if (skill.getPieceRate() == null) {
                    skill.setPieceRate(new BigDecimal("15"));
                }
                break;
            case "ADVANCED":
                if (skill.getHourlyRate() == null) {
                    skill.setHourlyRate(new BigDecimal("30"));
                }
                if (skill.getPieceRate() == null) {
                    skill.setPieceRate(new BigDecimal("20"));
                }
                break;
            case "EXPERT":
                if (skill.getHourlyRate() == null) {
                    skill.setHourlyRate(new BigDecimal("35"));
                }
                if (skill.getPieceRate() == null) {
                    skill.setPieceRate(new BigDecimal("25"));
                }
                break;
            case "MASTER":
                if (skill.getHourlyRate() == null) {
                    skill.setHourlyRate(new BigDecimal("40"));
                }
                if (skill.getPieceRate() == null) {
                    skill.setPieceRate(new BigDecimal("30"));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 启用/禁用技能
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int toggleSkillStatus(Long id, Boolean isActive, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            WorkerSkill skill = new WorkerSkill();
            skill.setId(id);
            skill.setIsActive(isActive);
            skill.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            skill.setUpdateTime(new Date());
            
            result = workerSkillMapper.updateByPrimaryKeySelective(skill);
            
            // 记录日志
            logService.insertLog("工人技能",
                (isActive ? "启用" : "禁用") + "技能",
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
