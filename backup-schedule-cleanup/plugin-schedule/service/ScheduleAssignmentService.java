package com.linghua.plugin.schedule.service;

import com.linghua.plugin.schedule.constants.ScheduleConstants;
import com.linghua.plugin.schedule.datasource.entities.ScheduleAssignment;
import com.linghua.plugin.schedule.datasource.entities.ScheduleShift;
import com.linghua.plugin.schedule.datasource.mappers.ScheduleAssignmentMapper;
import com.linghua.plugin.schedule.datasource.mappers.ScheduleAssignmentMapperEx;
import com.linghua.plugin.schedule.datasource.mappers.ScheduleShiftMapper;
import com.linghua.plugin.schedule.exception.PluginBusinessException;
import com.linghua.plugin.schedule.exception.PluginValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Schedule Assignment Service
 * 排班分配记录服务类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@Service
public class ScheduleAssignmentService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleAssignmentService.class);
    
    @Resource
    private ScheduleAssignmentMapper scheduleAssignmentMapper;
    
    @Resource
    private ScheduleAssignmentMapperEx scheduleAssignmentMapperEx;
    
    @Resource
    private ScheduleShiftMapper scheduleShiftMapper;
    
    // ==================== 基础CRUD操作 ====================
    
    /**
     * Add new assignment
     * 新增排班记录
     * 
     * @param assignment assignment data
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int addAssignment(ScheduleAssignment assignment, HttpServletRequest request) throws Exception {
        try {
            // 1. Parameter validation
            validateAssignment(assignment);
            
            // 2. Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // 3. Check if shift exists
            ScheduleShift shift = scheduleShiftMapper.selectByPrimaryKey(assignment.getShiftId());
            if (shift == null) {
                throw new PluginBusinessException("班次不存在");
            }
            
            // 4. Check for assignment conflict
            boolean conflict = scheduleAssignmentMapper.checkConflict(
                assignment.getScheduleDate(), assignment.getShiftId(), 
                assignment.getUserId(), null, tenantId);
            if (conflict) {
                throw new PluginBusinessException("该用户在此日期此班次已有排班记录");
            }
            
            // 5. Set default values and audit fields
            setCreateFields(assignment, tenantId, userId);
            
            // 6. Save to database
            int result = scheduleAssignmentMapper.insertSelective(assignment);
            
            if (result <= 0) {
                throw new PluginBusinessException("保存排班记录失败");
            }
            
            // 7. Log the operation
            logger.info("排班记录新增成功 - ID: {}, 用户: {}, 日期: {}, 班次: {}, 操作人: {}", 
                       assignment.getId(), assignment.getUserName(), assignment.getScheduleDate(), 
                       shift.getShiftName(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("新增排班记录失败", e);
            throw e;
        }
    }
    
    /**
     * Batch add assignments
     * 批量新增排班记录
     * 
     * @param assignments assignment list
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchAddAssignments(List<ScheduleAssignment> assignments, HttpServletRequest request) throws Exception {
        try {
            if (assignments == null || assignments.isEmpty()) {
                throw new PluginValidationException("排班记录列表不能为空");
            }
            
            // Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // Validate and prepare assignments
            for (ScheduleAssignment assignment : assignments) {
                validateAssignment(assignment);
                
                // Check if shift exists
                ScheduleShift shift = scheduleShiftMapper.selectByPrimaryKey(assignment.getShiftId());
                if (shift == null) {
                    throw new PluginBusinessException("班次不存在: " + assignment.getShiftId());
                }
                
                // Check for assignment conflict
                boolean conflict = scheduleAssignmentMapper.checkConflict(
                    assignment.getScheduleDate(), assignment.getShiftId(), 
                    assignment.getUserId(), null, tenantId);
                if (conflict) {
                    throw new PluginBusinessException(
                        String.format("用户 %s 在 %s 的 %s 班次已有排班记录", 
                                    assignment.getUserName(), assignment.getScheduleDate(), shift.getShiftName()));
                }
                
                // Set default values and audit fields
                setCreateFields(assignment, tenantId, userId);
            }
            
            // Batch insert
            int result = scheduleAssignmentMapper.batchInsert(assignments);
            
            // Log the operation
            logger.info("批量排班记录新增成功 - 数量: {}, 操作人: {}", assignments.size(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("批量新增排班记录失败", e);
            throw e;
        }
    }
    
    /**
     * Update assignment
     * 更新排班记录
     * 
     * @param assignment assignment data
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateAssignment(ScheduleAssignment assignment, HttpServletRequest request) throws Exception {
        try {
            // 1. Parameter validation
            if (assignment.getId() == null) {
                throw new PluginValidationException("排班记录ID不能为空");
            }
            validateAssignment(assignment);
            
            // 2. Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // 3. Check if assignment exists
            ScheduleAssignment existingAssignment = scheduleAssignmentMapper.selectByPrimaryKey(assignment.getId());
            if (existingAssignment == null) {
                throw new PluginBusinessException("排班记录不存在");
            }
            
            // 4. Check if shift exists
            ScheduleShift shift = scheduleShiftMapper.selectByPrimaryKey(assignment.getShiftId());
            if (shift == null) {
                throw new PluginBusinessException("班次不存在");
            }
            
            // 5. Check for assignment conflict (exclude current record)
            boolean conflict = scheduleAssignmentMapper.checkConflict(
                assignment.getScheduleDate(), assignment.getShiftId(), 
                assignment.getUserId(), assignment.getId(), tenantId);
            if (conflict) {
                throw new PluginBusinessException("该用户在此日期此班次已有其他排班记录");
            }
            
            // 6. Set update fields
            setUpdateFields(assignment, userId);
            
            // 7. Update database
            int result = scheduleAssignmentMapper.updateByPrimaryKeySelective(assignment);
            
            if (result <= 0) {
                throw new PluginBusinessException("更新排班记录失败");
            }
            
            // 8. Log the operation
            logger.info("排班记录更新成功 - ID: {}, 用户: {}, 日期: {}, 操作人: {}", 
                       assignment.getId(), assignment.getUserName(), assignment.getScheduleDate(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("更新排班记录失败", e);
            throw e;
        }
    }
    
    /**
     * Delete assignment (soft delete)
     * 删除排班记录（软删除）
     * 
     * @param id assignment ID
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteAssignment(Long id, HttpServletRequest request) throws Exception {
        try {
            // 1. Parameter validation
            if (id == null) {
                throw new PluginValidationException("排班记录ID不能为空");
            }
            
            // 2. Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // 3. Check if assignment exists
            ScheduleAssignment existingAssignment = scheduleAssignmentMapper.selectByPrimaryKey(id);
            if (existingAssignment == null) {
                throw new PluginBusinessException("排班记录不存在");
            }
            
            // 4. Perform soft delete
            List<Long> ids = List.of(id);
            int result = scheduleAssignmentMapper.batchSoftDelete(ids, tenantId, userId);
            
            if (result <= 0) {
                throw new PluginBusinessException("删除排班记录失败");
            }
            
            // 5. Log the operation
            logger.info("排班记录删除成功 - ID: {}, 用户: {}, 日期: {}, 操作人: {}", 
                       id, existingAssignment.getUserName(), existingAssignment.getScheduleDate(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("删除排班记录失败", e);
            throw e;
        }
    }
    
    /**
     * Get assignment by ID
     * 根据ID获取排班记录
     * 
     * @param id assignment ID
     * @return assignment entity or null if not found
     */
    public ScheduleAssignment getAssignmentById(Long id) {
        if (id == null) {
            return null;
        }
        return scheduleAssignmentMapper.selectByPrimaryKey(id);
    }
    
    /**
     * Get assignments by date
     * 根据日期获取排班记录
     * 
     * @param request HTTP request
     * @return assignment list
     * @throws Exception if operation fails
     */
    public List<Map<String, Object>> getAssignmentsByDate(HttpServletRequest request) throws Exception {
        try {
            Long tenantId = getCurrentTenantId(request);
            String scheduleDate = request.getParameter("scheduleDate");
            
            if (scheduleDate == null || scheduleDate.trim().isEmpty()) {
                throw new PluginValidationException("排班日期不能为空");
            }
            
            return scheduleAssignmentMapperEx.getAssignmentsByDate(tenantId, scheduleDate);
            
        } catch (Exception e) {
            logger.error("获取排班记录失败", e);
            throw e;
        }
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * Batch assign schedule
     * 批量排班
     * 
     * @param dates date list (YYYY-MM-DD format)
     * @param userIds user ID list
     * @param shiftId shift ID
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchAssignSchedule(List<String> dates, List<Long> userIds, Long shiftId, 
                                  HttpServletRequest request) throws Exception {
        try {
            // Parameter validation
            if (dates == null || dates.isEmpty()) {
                throw new PluginValidationException("日期列表不能为空");
            }
            if (userIds == null || userIds.isEmpty()) {
                throw new PluginValidationException("用户列表不能为空");
            }
            if (shiftId == null) {
                throw new PluginValidationException("班次ID不能为空");
            }
            
            // Get current user and tenant information
            Long tenantId = getCurrentTenantId(request);
            Long userId = getCurrentUserId(request);
            
            // Check if shift exists
            ScheduleShift shift = scheduleShiftMapper.selectByPrimaryKey(shiftId);
            if (shift == null) {
                throw new PluginBusinessException("班次不存在");
            }
            
            // Prepare assignment list
            List<ScheduleAssignment> assignments = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (String dateStr : dates) {
                Date scheduleDate;
                try {
                    scheduleDate = dateFormat.parse(dateStr);
                } catch (ParseException e) {
                    throw new PluginValidationException("日期格式错误: " + dateStr);
                }
                
                for (Long assignUserId : userIds) {
                    // Check for conflict
                    boolean conflict = scheduleAssignmentMapper.checkConflict(
                        scheduleDate, shiftId, assignUserId, null, tenantId);
                    if (conflict) {
                        logger.warn("跳过冲突的排班记录 - 用户: {}, 日期: {}, 班次: {}", 
                                   assignUserId, dateStr, shift.getShiftName());
                        continue;
                    }
                    
                    // Create assignment
                    ScheduleAssignment assignment = new ScheduleAssignment();
                    assignment.setScheduleDate(scheduleDate);
                    assignment.setShiftId(shiftId);
                    assignment.setUserId(assignUserId);
                    assignment.setUserName("用户" + assignUserId); // TODO: Get actual user name
                    assignment.setStatus(ScheduleConstants.ASSIGNMENT_STATUS_SCHEDULED);
                    
                    setCreateFields(assignment, tenantId, userId);
                    assignments.add(assignment);
                }
            }
            
            if (assignments.isEmpty()) {
                throw new PluginBusinessException("没有可以排班的记录（可能存在冲突）");
            }
            
            // Batch insert
            int result = scheduleAssignmentMapper.batchInsert(assignments);
            
            // Log the operation
            logger.info("批量排班成功 - 日期数: {}, 用户数: {}, 班次: {}, 实际创建: {}, 操作人: {}", 
                       dates.size(), userIds.size(), shift.getShiftName(), assignments.size(), userId);
            
            return result;
            
        } catch (Exception e) {
            logger.error("批量排班失败", e);
            throw e;
        }
    }
    
    // ==================== 私有方法 ====================
    
    /**
     * Validate assignment data
     * 验证排班记录数据
     */
    private void validateAssignment(ScheduleAssignment assignment) throws PluginValidationException {
        if (assignment == null) {
            throw new PluginValidationException("排班记录数据不能为空");
        }
        
        if (assignment.getScheduleDate() == null) {
            throw new PluginValidationException("排班日期不能为空");
        }
        
        if (assignment.getShiftId() == null) {
            throw new PluginValidationException("班次ID不能为空");
        }
        
        if (assignment.getUserId() == null) {
            throw new PluginValidationException("用户ID不能为空");
        }
        
        // Check if schedule date is in the past (optional business rule)
        Date today = new Date();
        if (assignment.getScheduleDate().before(today)) {
            // Allow past dates for now, but log a warning
            logger.warn("排班日期为过去时间: {}", assignment.getScheduleDate());
        }
    }
    
    /**
     * Set create fields
     */
    private void setCreateFields(ScheduleAssignment assignment, Long tenantId, Long userId) {
        assignment.setId(null); // Ensure it's a new record
        assignment.setTenantId(tenantId);
        assignment.setDeleteFlag(ScheduleConstants.DELETE_FLAG_EXISTS);
        assignment.setCreateTime(new Date());
        assignment.setCreateBy(userId);
        
        // Set default status if not provided
        if (assignment.getStatus() == null || assignment.getStatus().trim().isEmpty()) {
            assignment.setStatus(ScheduleConstants.ASSIGNMENT_STATUS_SCHEDULED);
        }
    }
    
    /**
     * Set update fields
     */
    private void setUpdateFields(ScheduleAssignment assignment, Long userId) {
        assignment.setUpdateTime(new Date());
        assignment.setUpdateBy(userId);
    }
    
    /**
     * Get current tenant ID from request
     */
    private Long getCurrentTenantId(HttpServletRequest request) {
        // TODO: Implement actual tenant ID extraction logic
        return 0L; // Default tenant for now
    }
    
    /**
     * Get current user ID from request
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // TODO: Implement actual user ID extraction logic
        return 0L; // Default user for now
    }
}
