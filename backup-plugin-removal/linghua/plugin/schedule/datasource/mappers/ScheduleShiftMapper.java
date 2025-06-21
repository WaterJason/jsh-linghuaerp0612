package com.linghua.plugin.schedule.datasource.mappers;

import com.linghua.plugin.schedule.datasource.entities.ScheduleShift;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Schedule Shift Basic Mapper Interface
 * 排班班次基础Mapper接口
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public interface ScheduleShiftMapper {
    
    /**
     * Select by primary key
     * 根据主键查询
     * 
     * @param id primary key
     * @return entity or null if not found
     */
    ScheduleShift selectByPrimaryKey(Long id);
    
    /**
     * Insert entity selectively
     * 选择性插入实体（只插入非空字段）
     * 
     * @param record entity to insert
     * @return number of affected rows
     */
    int insertSelective(ScheduleShift record);
    
    /**
     * Update by primary key selectively
     * 根据主键选择性更新（只更新非空字段）
     * 
     * @param record entity to update
     * @return number of affected rows
     */
    int updateByPrimaryKeySelective(ScheduleShift record);
    
    /**
     * Delete by primary key (physical delete)
     * 根据主键删除（物理删除，谨慎使用）
     * 
     * @param id primary key
     * @return number of affected rows
     */
    int deleteByPrimaryKey(Long id);
    
    /**
     * Count by conditions
     * 根据条件统计数量
     * 
     * @param tenantId tenant ID
     * @param shiftName shift name (fuzzy search)
     * @param shiftType shift type
     * @param isActive is active
     * @return count
     */
    long countByConditions(@Param("tenantId") Long tenantId,
                          @Param("shiftName") String shiftName,
                          @Param("shiftType") String shiftType,
                          @Param("isActive") Boolean isActive);
    
    /**
     * Select by conditions with pagination
     * 根据条件分页查询
     * 
     * @param tenantId tenant ID
     * @param shiftName shift name (fuzzy search)
     * @param shiftType shift type
     * @param isActive is active
     * @return shift list
     */
    List<ScheduleShift> selectByConditions(@Param("tenantId") Long tenantId,
                                          @Param("shiftName") String shiftName,
                                          @Param("shiftType") String shiftType,
                                          @Param("isActive") Boolean isActive);
    
    /**
     * Select active shifts by tenant
     * 查询租户下的启用班次
     * 
     * @param tenantId tenant ID
     * @return active shift list
     */
    List<ScheduleShift> selectActiveByTenant(@Param("tenantId") Long tenantId);
    
    /**
     * Check if shift name exists
     * 检查班次名称是否存在
     * 
     * @param shiftName shift name
     * @param excludeId exclude ID (for update operations)
     * @param tenantId tenant ID
     * @return true if exists, false otherwise
     */
    boolean existsByName(@Param("shiftName") String shiftName,
                        @Param("excludeId") Long excludeId,
                        @Param("tenantId") Long tenantId);
    
    /**
     * Batch soft delete by IDs
     * 批量软删除
     * 
     * @param ids shift IDs
     * @param tenantId tenant ID
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int batchSoftDelete(@Param("ids") List<Long> ids,
                       @Param("tenantId") Long tenantId,
                       @Param("updateBy") Long updateBy);
    
    /**
     * Update sort order
     * 更新排序
     * 
     * @param id shift ID
     * @param sortOrder new sort order
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int updateSortOrder(@Param("id") Long id,
                       @Param("sortOrder") Integer sortOrder,
                       @Param("updateBy") Long updateBy);
    
    /**
     * Update active status
     * 更新启用状态
     * 
     * @param id shift ID
     * @param isActive new active status
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int updateActiveStatus(@Param("id") Long id,
                          @Param("isActive") Boolean isActive,
                          @Param("updateBy") Long updateBy);
}
