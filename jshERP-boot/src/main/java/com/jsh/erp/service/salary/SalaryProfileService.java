package com.jsh.erp.service.salary;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.SalaryProfile;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.SalaryProfileMapper;
import com.jsh.erp.datasource.mappers.SalaryProfileMapperEx;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.LogService;
import com.jsh.erp.service.UserService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 员工薪酬档案服务类
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class SalaryProfileService extends ServiceImpl<SalaryProfileMapper, SalaryProfile> {

    private Logger logger = LoggerFactory.getLogger(SalaryProfileService.class);

    @Resource
    private SalaryProfileMapper salaryProfileMapper;

    @Resource
    private SalaryProfileMapperEx salaryProfileMapperEx;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    /**
     * 根据条件查询薪酬档案列表
     * 
     * @param employeeName 员工姓名
     * @param department 部门
     * @param position 职位
     * @param salaryStatus 薪酬状态
     * @param request HTTP请求
     * @return 薪酬档案列表
     */
    public List<SalaryProfile> select(String employeeName, String department, 
                                     String position, String salaryStatus, 
                                     HttpServletRequest request) throws Exception {
        List<SalaryProfile> result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            result = salaryProfileMapperEx.selectByCondition(employeeName, department, 
                                                           position, salaryStatus, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据员工ID查询薪酬档案
     * 
     * @param employeeId 员工ID
     * @param request HTTP请求
     * @return 薪酬档案
     */
    public SalaryProfile getSalaryProfileByEmployeeId(Long employeeId, HttpServletRequest request) throws Exception {
        SalaryProfile result = null;
        try {
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            result = salaryProfileMapperEx.selectByEmployeeId(employeeId, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增薪酬档案
     * 
     * @param salaryProfile 薪酬档案
     * @param request HTTP请求
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int insertSalaryProfile(SalaryProfile salaryProfile, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            // 设置租户ID和创建人信息
            Long tenantId = Tools.getTenantIdByToken(request.getHeader("X-Access-Token"));
            User currentUser = userService.getCurrentUser();
            
            salaryProfile.setTenantId(tenantId);
            salaryProfile.setCreator(currentUser.getId());
            salaryProfile.setCreateTime(new Date());
            salaryProfile.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 检查员工是否已存在薪酬档案
            SalaryProfile existProfile = getSalaryProfileByEmployeeId(salaryProfile.getEmployeeId(), request);
            if (existProfile != null) {
                throw new Exception("该员工已存在薪酬档案，不能重复创建");
            }
            
            result = salaryProfileMapper.insert(salaryProfile);
            
            // 记录操作日志
            logService.insertLog("薪酬档案", 
                               BusinessConstants.LOG_OPERATION_TYPE_ADD,
                               request);
                               
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 更新薪酬档案
     * 
     * @param salaryProfile 薪酬档案
     * @param request HTTP请求
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateSalaryProfile(SalaryProfile salaryProfile, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            // 设置更新人信息
            User currentUser = userService.getCurrentUser();
            salaryProfile.setUpdater(currentUser.getId());
            salaryProfile.setUpdateTime(new Date());
            
            result = salaryProfileMapper.updateById(salaryProfile);
            
            // 记录操作日志
            logService.insertLog("薪酬档案", 
                               BusinessConstants.LOG_OPERATION_TYPE_EDIT,
                               request);
                               
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 删除薪酬档案（逻辑删除）
     * 
     * @param id 档案ID
     * @param request HTTP请求
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteSalaryProfile(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            SalaryProfile salaryProfile = salaryProfileMapper.selectById(id);
            if (salaryProfile != null) {
                salaryProfile.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
                salaryProfile.setUpdateTime(new Date());
                
                User currentUser = userService.getCurrentUser();
                salaryProfile.setUpdater(currentUser.getId());
                
                result = salaryProfileMapper.updateById(salaryProfile);
                
                // 记录操作日志
                logService.insertLog("薪酬档案", 
                                   BusinessConstants.LOG_OPERATION_TYPE_DELETE,
                                   request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 批量删除薪酬档案
     * 
     * @param ids 档案ID列表
     * @param request HTTP请求
     * @return 影响行数
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteSalaryProfile(String ids, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            List<Long> idList = StringUtil.strToLongList(ids);
            for (Long id : idList) {
                result += deleteSalaryProfile(id, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 检查删除权限
     * 
     * @param id 档案ID
     * @param request HTTP请求
     * @return 是否可删除
     */
    public int checkIsCanDelete(Long id, HttpServletRequest request) throws Exception {
        int result = 1; // 1表示可删除，0表示不可删除
        try {
            // 检查是否存在关联的薪酬计算记录
            // 这里可以添加具体的检查逻辑
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }
}
