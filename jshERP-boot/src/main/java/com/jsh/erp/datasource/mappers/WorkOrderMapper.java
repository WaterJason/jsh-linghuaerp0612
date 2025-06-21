package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.WorkOrder;
import com.jsh.erp.datasource.entities.WorkOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 工单Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface WorkOrderMapper {
    /**
     * 根据条件统计记录数
     */
    long countByExample(WorkOrderExample example);

    /**
     * 根据条件删除记录
     */
    int deleteByExample(WorkOrderExample example);

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录（所有字段）
     */
    int insert(WorkOrder record);

    /**
     * 插入记录（选择性插入）
     */
    int insertSelective(WorkOrder record);

    /**
     * 根据条件查询记录列表
     */
    List<WorkOrder> selectByExample(WorkOrderExample example);

    /**
     * 根据主键查询记录
     */
    WorkOrder selectByPrimaryKey(Long id);

    /**
     * 根据条件更新记录（选择性更新）
     */
    int updateByExampleSelective(@Param("record") WorkOrder record, @Param("example") WorkOrderExample example);

    /**
     * 根据条件更新记录（所有字段）
     */
    int updateByExample(@Param("record") WorkOrder record, @Param("example") WorkOrderExample example);

    /**
     * 根据主键更新记录（选择性更新）
     */
    int updateByPrimaryKeySelective(WorkOrder record);

    /**
     * 根据主键更新记录（所有字段）
     */
    int updateByPrimaryKey(WorkOrder record);
}
