package com.jsh.erp.service.salary;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.SalaryPayment;
import com.jsh.erp.datasource.entities.SalaryCalculation;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.SalaryPaymentMapper;
import com.jsh.erp.datasource.mappers.SalaryCalculationMapper;
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
 * 薪酬发放服务类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class SalaryPaymentService extends ServiceImpl<SalaryPaymentMapper, SalaryPayment> {

    private Logger logger = LoggerFactory.getLogger(SalaryPaymentService.class);

    @Resource
    private SalaryPaymentMapper salaryPaymentMapper;

    @Resource
    private SalaryCalculationMapper salaryCalculationMapper;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    /**
     * 根据条件查询薪酬发放记录
     */
    public List<SalaryPayment> select(String employeeName, String calculationMonth, 
                                     String paymentStatus, HttpServletRequest request) throws Exception {
        List<SalaryPayment> result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            // 这里需要创建SalaryPaymentMapperEx来实现复杂查询
            // 暂时使用基础查询
            result = salaryPaymentMapper.selectList(null);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 创建薪酬发放记录
     */
    @Transactional(rollbackFor = Exception.class)
    public int createPaymentRecord(Long calculationId, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            User currentUser = userService.getCurrentUser();
            
            // 获取薪酬计算记录
            SalaryCalculation calculation = salaryCalculationMapper.selectById(calculationId);
            if (calculation == null) {
                throw new Exception("薪酬计算记录不存在");
            }
            
            if (!"APPROVED".equals(calculation.getStatus())) {
                throw new Exception("只能对已审批的薪酬计算记录创建发放记录");
            }
            
            // 检查是否已存在发放记录
            // 这里需要SalaryPaymentMapperEx来实现查询
            
            // 创建发放记录
            SalaryPayment payment = new SalaryPayment();
            payment.setPaymentNumber(generatePaymentNumber());
            payment.setCalculationId(calculationId);
            payment.setEmployeeId(calculation.getEmployeeId());
            payment.setEmployeeName(calculation.getEmployeeName());
            payment.setCalculationMonth(calculation.getCalculationMonth());
            payment.setPaymentAmount(calculation.getTotalAmount());
            payment.setPaymentStatus("PENDING");
            payment.setPaymentMethod("BANK_TRANSFER");
            payment.setTenantId(tenantId);
            payment.setCreator(currentUser.getId());
            payment.setCreateTime(new Date());
            payment.setDeleteFlag("0");
            
            result = salaryPaymentMapper.insert(payment);
            
            // 记录操作日志
            logService.insertLog("薪酬发放", BusinessConstants.LOG_OPERATION_TYPE_ADD, request);
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 批量创建薪酬发放记录
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchCreatePaymentRecords(List<Long> calculationIds, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            for (Long calculationId : calculationIds) {
                result += createPaymentRecord(calculationId, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 执行薪酬发放
     */
    @Transactional(rollbackFor = Exception.class)
    public int executePayment(List<Long> paymentIds, Date paymentDate, 
                             HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            User currentUser = userService.getCurrentUser();
            
            for (Long paymentId : paymentIds) {
                SalaryPayment payment = salaryPaymentMapper.selectById(paymentId);
                if (payment != null && "PENDING".equals(payment.getPaymentStatus())) {
                    payment.setPaymentStatus("PROCESSING");
                    payment.setPaymentDate(paymentDate);
                    payment.setUpdater(currentUser.getId());
                    payment.setUpdateTime(new Date());
                    
                    result += salaryPaymentMapper.updateById(payment);
                    
                    // 这里可以集成第三方支付接口
                    // 模拟发放成功
                    payment.setPaymentStatus("PAID");
                    salaryPaymentMapper.updateById(payment);
                }
            }
            
            // 记录操作日志
            logService.insertLog("薪酬发放执行", BusinessConstants.LOG_OPERATION_TYPE_EDIT, request);
            
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 更新发放状态
     */
    @Transactional(rollbackFor = Exception.class)
    public int updatePaymentStatus(Long paymentId, String status, String failureReason,
                                  HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User currentUser = userService.getCurrentUser();
            
            SalaryPayment payment = salaryPaymentMapper.selectById(paymentId);
            if (payment != null) {
                payment.setPaymentStatus(status);
                payment.setFailureReason(failureReason);
                payment.setUpdater(currentUser.getId());
                payment.setUpdateTime(new Date());
                
                result = salaryPaymentMapper.updateById(payment);
                
                // 记录操作日志
                logService.insertLog("薪酬发放状态更新", BusinessConstants.LOG_OPERATION_TYPE_EDIT, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 删除薪酬发放记录
     */
    @Transactional(rollbackFor = Exception.class)
    public int deletePaymentRecord(Long paymentId, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            SalaryPayment payment = salaryPaymentMapper.selectById(paymentId);
            if (payment != null && "PENDING".equals(payment.getPaymentStatus())) {
                payment.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
                payment.setUpdateTime(new Date());
                
                User currentUser = userService.getCurrentUser();
                payment.setUpdater(currentUser.getId());
                
                result = salaryPaymentMapper.updateById(payment);
                
                // 记录操作日志
                logService.insertLog("薪酬发放", BusinessConstants.LOG_OPERATION_TYPE_DELETE, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 生成发放单号
     */
    private String generatePaymentNumber() {
        // 生成格式：PAY + YYYYMMDD + 4位序号
        String dateStr = Tools.dateToStr(new Date(), "yyyyMMdd");
        String prefix = "PAY" + dateStr;
        
        // 这里应该查询当天的最大序号，简化处理
        long timestamp = System.currentTimeMillis();
        String sequence = String.format("%04d", timestamp % 10000);
        
        return prefix + sequence;
    }

    /**
     * 获取发放统计信息
     */
    public Map<String, Object> getPaymentStatistics(String calculationMonth, 
                                                   HttpServletRequest request) throws Exception {
        Map<String, Object> result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            // 这里需要SalaryPaymentMapperEx来实现统计查询
            // 暂时返回空
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }
}
