package com.jsh.erp.service.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.SalaryCalculation;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.SalaryCalculationMapper;
import com.jsh.erp.datasource.mappers.SalaryCalculationMapperEx;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.LogService;
import com.jsh.erp.service.UserService;
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
 * 薪酬计算服务类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class SalaryCalculationService extends ServiceImpl<SalaryCalculationMapper, SalaryCalculation> {

    private Logger logger = LoggerFactory.getLogger(SalaryCalculationService.class);

    @Resource
    private SalaryCalculationMapper salaryCalculationMapper;

    @Resource
    private SalaryCalculationMapperEx salaryCalculationMapperEx;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Resource
    private SalaryCalculationEngineService calculationEngineService;

    /**
     * 根据条件查询薪酬计算记录
     */
    public List<SalaryCalculation> select(String employeeName, String calculationMonth, 
                                         String status, HttpServletRequest request) throws Exception {
        List<SalaryCalculation> result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            result = salaryCalculationMapperEx.selectByCondition(employeeName, calculationMonth, status, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 执行薪酬计算
     */
    @Transactional(rollbackFor = Exception.class)
    public int calculateSalary(String calculationMonth, List<Long> employeeIds, 
                              HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            User currentUser = userService.getCurrentUser();
            
            for (Long employeeId : employeeIds) {
                // 检查是否已存在计算记录
                SalaryCalculation existRecord = salaryCalculationMapperEx.selectByEmployeeAndMonth(
                    employeeId, calculationMonth, tenantId);
                
                if (existRecord != null && !"DRAFT".equals(existRecord.getStatus())) {
                    continue; // 跳过已计算的记录
                }
                
                // 调用计算引擎进行薪酬计算
                SalaryCalculation calculation = calculationEngineService.calculateEmployeeSalary(
                    employeeId, calculationMonth, tenantId);
                
                if (calculation != null) {
                    calculation.setTenantId(tenantId);
                    calculation.setCreator(currentUser.getId());
                    calculation.setCreateTime(new Date());
                    calculation.setCalculationDate(new Date());
                    
                    if (existRecord != null) {
                        calculation.setId(existRecord.getId());
                        salaryCalculationMapper.updateById(calculation);
                    } else {
                        salaryCalculationMapper.insert(calculation);
                    }
                    result++;
                }
            }
            
            // 记录操作日志
            logService.insertLog("薪酬计算", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 审批薪酬计算
     */
    @Transactional(rollbackFor = Exception.class)
    public int approveSalaryCalculation(List<Long> ids, String status, String approveRemark,
                                       HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            User currentUser = userService.getCurrentUser();
            
            result = salaryCalculationMapperEx.batchUpdateStatus(ids, status, currentUser.getId(), tenantId);
            
            // 更新审批信息
            for (Long id : ids) {
                SalaryCalculation calculation = salaryCalculationMapper.selectById(id);
                if (calculation != null) {
                    calculation.setApproveDate(new Date());
                    calculation.setApproveRemark(approveRemark);
                    salaryCalculationMapper.updateById(calculation);
                }
            }
            
            // 记录操作日志
            logService.insertLog("薪酬计算审批", BusinessConstants.LOG_OPERATION_TYPE_EDIT, request);
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 统计指定月份薪酬总额
     */
    public Map<String, Object> getSalaryStatistics(String calculationMonth, 
                                                  HttpServletRequest request) throws Exception {
        Map<String, Object> result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            result = salaryCalculationMapperEx.sumByMonth(calculationMonth, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 查询待审批的薪酬计算记录
     */
    public List<SalaryCalculation> getPendingApprovalList(HttpServletRequest request) throws Exception {
        List<SalaryCalculation> result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            result = salaryCalculationMapperEx.selectPendingApproval(tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 删除薪酬计算记录
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalaryCalculation(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            SalaryCalculation calculation = salaryCalculationMapper.selectById(id);
            if (calculation != null && "DRAFT".equals(calculation.getStatus())) {
                calculation.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
                calculation.setUpdateTime(new Date());
                
                User currentUser = userService.getCurrentUser();
                calculation.setUpdater(currentUser.getId());
                
                result = salaryCalculationMapper.updateById(calculation);
                
                // 记录操作日志
                logService.insertLog("薪酬计算", BusinessConstants.LOG_OPERATION_TYPE_DELETE, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
