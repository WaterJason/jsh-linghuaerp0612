package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.WorkOrderItem;
import com.jsh.erp.datasource.entities.WorkOrderItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 工单物料消耗Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface WorkOrderItemMapper {
    /**
     * 根据条件统计记录数
     */
    long countByExample(WorkOrderItemExample example);

    /**
     * 根据条件删除记录
     */
    int deleteByExample(WorkOrderItemExample example);

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录（所有字段）
     */
    int insert(WorkOrderItem record);

    /**
     * 插入记录（选择性插入）
     */
    int insertSelective(WorkOrderItem record);

    /**
     * 根据条件查询记录列表
     */
    List<WorkOrderItem> selectByExample(WorkOrderItemExample example);

    /**
     * 根据主键查询记录
     */
    WorkOrderItem selectByPrimaryKey(Long id);

    /**
     * 根据条件更新记录（选择性更新）
     */
    int updateByExampleSelective(@Param("record") WorkOrderItem record, @Param("example") WorkOrderItemExample example);

    /**
     * 根据条件更新记录（所有字段）
     */
    int updateByExample(@Param("record") WorkOrderItem record, @Param("example") WorkOrderItemExample example);

    /**
     * 根据主键更新记录（选择性更新）
     */
    int updateByPrimaryKeySelective(WorkOrderItem record);

    /**
     * 根据主键更新记录（所有字段）
     */
    int updateByPrimaryKey(WorkOrderItem record);
}
