package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.ScheduleEntry;
import com.jsh.erp.datasource.entities.ScheduleEntryExample;
import com.jsh.erp.datasource.entities.ScheduleEntryEx;
import com.jsh.erp.datasource.mappers.ScheduleEntryMapper;
import com.jsh.erp.datasource.mappers.ScheduleEntryMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 排班记录Service
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
@Service
public class ScheduleEntryService {
    private Logger logger = LoggerFactory.getLogger(ScheduleEntryService.class);

    @Resource
    private ScheduleEntryMapper scheduleEntryMapper;
    
    @Resource
    private ScheduleEntryMapperEx scheduleEntryMapperEx;
    
    @Resource
    private LogService logService;
    
    @Resource
    private UserService userService;

    /**
     * 根据ID获取排班记录
     */
    public ScheduleEntry getScheduleEntry(Long id) throws Exception {
        ScheduleEntry result = null;
        try {
            result = scheduleEntryMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 查询排班记录列表（包含关联信息）
     */
    public List<ScheduleEntryEx> select(Long employeeId, String startDate, String endDate,
                                       String status, HttpServletRequest request) throws Exception {
        List<ScheduleEntryEx> list = null;
        try {
            // 获取当前用户的租户ID
            Long tenantId = userService.getCurrentUser().getTenantId();
            
            PageUtils.startPage();
            
            // 转换日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateObj = StringUtil.isNotEmpty(startDate) ? sdf.parse(startDate) : null;
            Date endDateObj = StringUtil.isNotEmpty(endDate) ? sdf.parse(endDate) : null;
            
            list = scheduleEntryMapperEx.selectByCondition(employeeId, startDateObj, endDateObj, status, tenantId);
            
            // 处理状态显示名称
            for (ScheduleEntryEx entry : list) {
                entry.setStatusName(getStatusName(entry.getStatus()));
                if (entry.getScheduleDate() != null) {
                    entry.setScheduleDateStr(sdf.format(entry.getScheduleDate()));
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 根据日期范围查询排班记录
     */
    public List<ScheduleEntryEx> selectByDateRange(String startDate, String endDate,
                                                  HttpServletRequest request) throws Exception {
        List<ScheduleEntryEx> list = null;
        try {
            Long tenantId = userService.getCurrentUser().getTenantId();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateObj = sdf.parse(startDate);
            Date endDateObj = sdf.parse(endDate);
            
            list = scheduleEntryMapperEx.selectByDateRange(startDateObj, endDateObj, tenantId);
            
            // 处理显示信息
            for (ScheduleEntryEx entry : list) {
                entry.setStatusName(getStatusName(entry.getStatus()));
                if (entry.getScheduleDate() != null) {
                    entry.setScheduleDateStr(sdf.format(entry.getScheduleDate()));
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 统计排班记录数量
     */
    public Long countScheduleEntry(Long employeeId, String startDate, String endDate,
                                  String status, HttpServletRequest request) throws Exception {
        Long result = null;
        try {
            Long tenantId = userService.getCurrentUser().getTenantId();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateObj = StringUtil.isNotEmpty(startDate) ? sdf.parse(startDate) : null;
            Date endDateObj = StringUtil.isNotEmpty(endDate) ? sdf.parse(endDate) : null;
            
            result = scheduleEntryMapperEx.countByCondition(employeeId, startDateObj, endDateObj, status, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增排班记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertScheduleEntry(JSONObject obj, HttpServletRequest request) throws Exception {
        ScheduleEntry scheduleEntry = JSONObject.parseObject(obj.toJSONString(), ScheduleEntry.class);

        // 时间冲突检测：检查员工在该日期是否已有排班
        Long tenantId = userService.getCurrentUser().getTenantId();
        int conflictCount = scheduleEntryMapperEx.checkEmployeeScheduleConflict(
                scheduleEntry.getEmployeeId(), scheduleEntry.getScheduleDate(), null, tenantId);

        if (conflictCount > 0) {
            // 获取员工姓名用于错误提示
            String employeeName = "未知员工";
            String dateStr = "";
            try {
                // 获取员工信息
                if (scheduleEntry.getEmployeeId() != null) {
                    com.jsh.erp.datasource.entities.User employee = userService.getUser(scheduleEntry.getEmployeeId());
                    if (employee != null && StringUtil.isNotEmpty(employee.getUsername())) {
                        employeeName = employee.getUsername();
                    }
                }
                // 格式化日期
                if (scheduleEntry.getScheduleDate() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dateStr = sdf.format(scheduleEntry.getScheduleDate());
                }
            } catch (Exception e) {
                logger.warn("获取员工信息或格式化日期失败: " + e.getMessage());
            }

            String errorMsg = String.format("排班失败：员工[%s]在[%s]已有排班。", employeeName, dateStr);
            throw new BusinessRunTimeException(ExceptionConstants.SCHEDULE_CONFLICT_CODE, errorMsg);
        }
        
        // 设置默认值
        if (StringUtil.isEmpty(scheduleEntry.getStatus())) {
            scheduleEntry.setStatus("SCHEDULED");
        }
        if (scheduleEntry.getBreakDuration() == null) {
            scheduleEntry.setBreakDuration(0);
        }
        if (scheduleEntry.getOvertimeDuration() == null) {
            scheduleEntry.setOvertimeDuration(0);
        }
        if (StringUtil.isEmpty(scheduleEntry.getDeleteFlag())) {
            scheduleEntry.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        }
        
        // 设置租户ID和创建信息
        Long userId = userService.getUserId(request);
        scheduleEntry.setTenantId(tenantId);
        scheduleEntry.setCreateUser(userId);
        scheduleEntry.setCreateTime(new Date());
        
        int result = 0;
        try {
            result = scheduleEntryMapper.insertSelective(scheduleEntry);
            logService.insertLog("排班管理",
                    BusinessConstants.LOG_OPERATION_TYPE_ADD + "新增排班记录", request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 修改排班记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateScheduleEntry(JSONObject obj, HttpServletRequest request) throws Exception {
        ScheduleEntry scheduleEntry = JSONObject.parseObject(obj.toJSONString(), ScheduleEntry.class);
        
        // 检查员工在该日期是否已有其他排班
        Long tenantId = userService.getCurrentUser().getTenantId();
        int conflictCount = scheduleEntryMapperEx.checkEmployeeScheduleConflict(
                scheduleEntry.getEmployeeId(), scheduleEntry.getScheduleDate(), 
                scheduleEntry.getId(), tenantId);
        
        if (conflictCount > 0) {
            throw new BusinessRunTimeException(ExceptionConstants.SCHEDULE_CONFLICT_CODE,
                    "该员工在此日期已有其他排班安排");
        }
        
        // 设置更新信息
        Long userId = userService.getUserId(request);
        scheduleEntry.setUpdateUser(userId);
        scheduleEntry.setUpdateTime(new Date());
        
        int result = 0;
        try {
            result = scheduleEntryMapper.updateByPrimaryKeySelective(scheduleEntry);
            logService.insertLog("排班管理",
                    BusinessConstants.LOG_OPERATION_TYPE_EDIT + "修改排班记录", request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 删除排班记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteScheduleEntry(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteScheduleEntry(id.toString(), request);
    }

    /**
     * 批量删除排班记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteScheduleEntry(String ids, HttpServletRequest request) throws Exception {
        String[] idArray = ids.split(",");
        int result = 0;
        
        try {
            result = scheduleEntryMapperEx.batchDeleteByIds(idArray);
            logService.insertLog("排班管理",
                    BusinessConstants.LOG_OPERATION_TYPE_DELETE + "批量删除" + result + "条记录", request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 批量更新排班状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchUpdateStatus(String ids, String status, HttpServletRequest request) throws Exception {
        String[] idArray = ids.split(",");
        int result = 0;
        
        try {
            Long userId = userService.getUserId(request);
            result = scheduleEntryMapperEx.batchUpdateStatus(idArray, status, userId);
            logService.insertLog("排班管理",
                    BusinessConstants.LOG_OPERATION_TYPE_EDIT + "批量更新状态为" + status, request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 统计员工工作时长
     */
    public Double sumWorkHoursByEmployee(Long employeeId, String startDate, String endDate,
                                        HttpServletRequest request) throws Exception {
        Double result = 0.0;
        try {
            Long tenantId = userService.getCurrentUser().getTenantId();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDateObj = sdf.parse(startDate);
            Date endDateObj = sdf.parse(endDate);
            
            result = scheduleEntryMapperEx.sumWorkHoursByEmployee(employeeId, startDateObj, endDateObj, tenantId);
            if (result == null) {
                result = 0.0;
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取状态显示名称
     */
    private String getStatusName(String status) {
        if (StringUtil.isEmpty(status)) {
            return "";
        }
        switch (status) {
            case "SCHEDULED":
                return "已排班";
            case "CONFIRMED":
                return "已确认";
            case "CANCELLED":
                return "已取消";
            default:
                return status;
        }
    }
}
