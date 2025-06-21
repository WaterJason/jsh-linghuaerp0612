package com.linghua.plugin.schedule.service;

import com.linghua.plugin.schedule.constants.ScheduleConstants;
import com.linghua.plugin.schedule.datasource.entities.ScheduleShift;
import com.linghua.plugin.schedule.datasource.mappers.ScheduleShiftMapper;
import com.linghua.plugin.schedule.datasource.mappers.ScheduleShiftMapperEx;
import com.linghua.plugin.schedule.exception.PluginBusinessException;
import com.linghua.plugin.schedule.exception.PluginValidationException;
import com.linghua.plugin.schedule.utils.ScheduleUtils;
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
 * Schedule Shift Service
 * 排班班次服务类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@Service
public class ScheduleShiftService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleShiftService.class);
    
    @Resource
    private ScheduleShiftMapper scheduleShiftMapper;
    
    @Resource
    private ScheduleShiftMapperEx scheduleShiftMapperEx;
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * Add new shift
     * 新增班次
     * 
     * @param shift shift data
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int addShift(ScheduleShift shift, HttpServletRequest request) throws Exception {
        try {
            // 1. Parameter validation
            validateShift(shift);
            
            // 2. Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // 3. Check for duplicate shift names
            boolean exists = scheduleShiftMapper.existsByName(shift.getShiftName(), null, tenantId);
            if (exists) {
                throw new PluginBusinessException("班次名称已存在: " + shift.getShiftName());
            }
            
            // 4. Check time conflict
            if (shift.getStartTime() != null && shift.getEndTime() != null) {
                List<ScheduleShift> conflicts = scheduleShiftMapperEx.checkTimeConflict(
                    shift.getStartTime(), shift.getEndTime(), null, tenantId);
                if (!conflicts.isEmpty()) {
                    throw new PluginBusinessException("班次时间与现有班次冲突: " + conflicts.get(0).getShiftName());
                }
            }
            
            // 5. Set default values and audit fields
            setCreateFields(shift, tenantId, userId);
            
            // 6. Set default sort order if not provided
            if (shift.getSortOrder() == null) {
                Integer maxSortOrder = scheduleShiftMapperEx.getMaxSortOrder(tenantId);
                shift.setSortOrder(maxSortOrder + 1);
            }
            
            // 7. Save to database
            int result = scheduleShiftMapper.insertSelective(shift);
            
            if (result <= 0) {
                throw new PluginBusinessException("保存班次失败");
            }
            
            // 8. Log the operation
            logger.info("班次新增成功 - ID: {}, 名称: {}, 操作人: {}", 
                       shift.getId(), shift.getShiftName(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("新增班次失败", e);
            throw e;
        }
    }
    
    /**
     * Update shift
     * 更新班次
     * 
     * @param shift shift data
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateShift(ScheduleShift shift, HttpServletRequest request) throws Exception {
        try {
            // 1. Parameter validation
            if (shift.getId() == null) {
                throw new PluginValidationException("班次ID不能为空");
            }
            validateShift(shift);
            
            // 2. Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // 3. Check if shift exists
            ScheduleShift existingShift = scheduleShiftMapper.selectByPrimaryKey(shift.getId());
            if (existingShift == null) {
                throw new PluginBusinessException("班次不存在");
            }
            
            // 4. Check for duplicate shift names
            boolean exists = scheduleShiftMapper.existsByName(shift.getShiftName(), shift.getId(), tenantId);
            if (exists) {
                throw new PluginBusinessException("班次名称已存在: " + shift.getShiftName());
            }
            
            // 5. Check time conflict
            if (shift.getStartTime() != null && shift.getEndTime() != null) {
                List<ScheduleShift> conflicts = scheduleShiftMapperEx.checkTimeConflict(
                    shift.getStartTime(), shift.getEndTime(), shift.getId(), tenantId);
                if (!conflicts.isEmpty()) {
                    throw new PluginBusinessException("班次时间与现有班次冲突: " + conflicts.get(0).getShiftName());
                }
            }
            
            // 6. Set update fields
            setUpdateFields(shift, userId);
            
            // 7. Update database
            int result = scheduleShiftMapper.updateByPrimaryKeySelective(shift);
            
            if (result <= 0) {
                throw new PluginBusinessException("更新班次失败");
            }
            
            // 8. Log the operation
            logger.info("班次更新成功 - ID: {}, 名称: {}, 操作人: {}", 
                       shift.getId(), shift.getShiftName(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("更新班次失败", e);
            throw e;
        }
    }
    
    /**
     * Delete shift (soft delete)
     * 删除班次（软删除）
     * 
     * @param id shift ID
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteShift(Long id, HttpServletRequest request) throws Exception {
        try {
            // 1. Parameter validation
            if (id == null) {
                throw new PluginValidationException("班次ID不能为空");
            }
            
            // 2. Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // 3. Check if shift exists
            ScheduleShift existingShift = scheduleShiftMapper.selectByPrimaryKey(id);
            if (existingShift == null) {
                throw new PluginBusinessException("班次不存在");
            }
            
            // 4. Check if shift is being used
            long usageCount = scheduleShiftMapperEx.getShiftUsageCount(id, tenantId);
            if (usageCount > 0) {
                throw new PluginBusinessException("班次正在使用中，无法删除");
            }
            
            // 5. Perform soft delete
            List<Long> ids = List.of(id);
            int result = scheduleShiftMapper.batchSoftDelete(ids, tenantId, userId);
            
            if (result <= 0) {
                throw new PluginBusinessException("删除班次失败");
            }
            
            // 6. Log the operation
            logger.info("班次删除成功 - ID: {}, 名称: {}, 操作人: {}", 
                       id, existingShift.getShiftName(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("删除班次失败", e);
            throw e;
        }
    }
    
    /**
     * Get shift by ID
     * 根据ID获取班次
     * 
     * @param id shift ID
     * @return shift entity or null if not found
     */
    public ScheduleShift getShiftById(Long id) {
        if (id == null) {
            return null;
        }
        return scheduleShiftMapper.selectByPrimaryKey(id);
    }
    
    /**
     * Get shift list for management page
     * 获取班次管理页面列表
     * 
     * @param request HTTP request
     * @return shift list
     * @throws Exception if operation fails
     */
    public List<Map<String, Object>> getShiftList(HttpServletRequest request) throws Exception {
        try {
            // Get search parameters
            Long tenantId = getCurrentTenantId(request);
            String shiftName = request.getParameter("shiftName");
            String shiftType = request.getParameter("shiftType");
            String isActiveStr = request.getParameter("isActive");
            Boolean isActive = null;
            if (isActiveStr != null && !isActiveStr.isEmpty()) {
                isActive = "1".equals(isActiveStr) || "true".equalsIgnoreCase(isActiveStr);
            }
            
            // Query data
            return scheduleShiftMapperEx.getShiftListForManagement(tenantId, shiftName, shiftType, isActive);
            
        } catch (Exception e) {
            logger.error("获取班次列表失败", e);
            throw e;
        }
    }
    
    /**
     * Get active shifts for dropdown
     * 获取启用班次下拉选项
     * 
     * @param request HTTP request
     * @return shift options
     * @throws Exception if operation fails
     */
    public List<Map<String, Object>> getShiftOptions(HttpServletRequest request) throws Exception {
        try {
            Long tenantId = getCurrentTenantId(request);
            return scheduleShiftMapperEx.getShiftOptions(tenantId);
        } catch (Exception e) {
            logger.error("获取班次选项失败", e);
            throw e;
        }
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * Batch update shift status
     * 批量更新班次状态
     * 
     * @param ids shift IDs
     * @param isActive new active status
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdateStatus(List<Long> ids, Boolean isActive, HttpServletRequest request) throws Exception {
        try {
            if (ids == null || ids.isEmpty()) {
                throw new PluginValidationException("班次ID列表不能为空");
            }
            
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            int result = scheduleShiftMapperEx.batchUpdateStatus(ids, isActive, tenantId, userId);
            
            logger.info("批量更新班次状态成功 - 数量: {}, 状态: {}, 操作人: {}", 
                       ids.size(), isActive, userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("批量更新班次状态失败", e);
            throw e;
        }
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * Validate shift data
     * 验证班次数据
     * 
     * @param shift shift data
     * @throws PluginValidationException if validation fails
     */
    private void validateShift(ScheduleShift shift) throws PluginValidationException {
        if (shift == null) {
            throw new PluginValidationException("班次数据不能为空");
        }
        
        if (shift.getShiftName() == null || shift.getShiftName().trim().isEmpty()) {
            throw new PluginValidationException("班次名称不能为空");
        }
        
        if (shift.getShiftName().length() > 50) {
            throw new PluginValidationException("班次名称不能超过50个字符");
        }
        
        if (shift.getShiftType() == null || shift.getShiftType().trim().isEmpty()) {
            throw new PluginValidationException("班次类型不能为空");
        }
        
        // Validate time format and logic
        if (shift.getStartTime() != null && shift.getEndTime() != null) {
            if (shift.getStartTime().compareTo(shift.getEndTime()) >= 0) {
                throw new PluginValidationException("开始时间必须早于结束时间");
            }
        }
        
        // Validate duration hours
        if (shift.getDurationHours() != null) {
            if (shift.getDurationHours().compareTo(BigDecimal.ZERO) <= 0 || 
                shift.getDurationHours().compareTo(new BigDecimal("24")) > 0) {
                throw new PluginValidationException("班次时长必须在0-24小时之间");
            }
        }
    }
    
    /**
     * Set create fields
     * 设置创建字段
     */
    private void setCreateFields(ScheduleShift shift, Long tenantId, Long userId) {
        shift.setId(null); // Ensure it's a new record
        shift.setTenantId(tenantId);
        shift.setDeleteFlag(ScheduleConstants.DELETE_FLAG_EXISTS);
        shift.setCreateTime(new Date());
        shift.setCreateBy(userId);
        
        // Set default values
        if (shift.getIsActive() == null) {
            shift.setIsActive(true);
        }
        if (shift.getColor() == null || shift.getColor().trim().isEmpty()) {
            shift.setColor("#1890ff");
        }
    }
    
    /**
     * Set update fields
     * 设置更新字段
     */
    private void setUpdateFields(ScheduleShift shift, Long userId) {
        shift.setUpdateTime(new Date());
        shift.setUpdateBy(userId);
    }
    
    /**
     * Get current tenant ID from request
     * 从请求中获取当前租户ID
     */
    private Long getCurrentTenantId(HttpServletRequest request) {
        // TODO: Implement actual tenant ID extraction logic
        // This should integrate with jshERP's tenant management
        return 0L; // Default tenant for now
    }
    
    /**
     * Get current user ID from request
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // TODO: Implement actual user ID extraction logic
        // This should integrate with jshERP's user management
        return 0L; // Default user for now
    }
}
