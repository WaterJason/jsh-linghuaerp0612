package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionOrder;
import com.jsh.erp.datasource.entities.ProductionOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 主生产订单Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-21
 */
public interface ProductionOrderMapper {
    /**
     * 根据条件统计记录数
     */
    long countByExample(ProductionOrderExample example);

    /**
     * 根据条件删除记录
     */
    int deleteByExample(ProductionOrderExample example);

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入记录（所有字段）
     */
    int insert(ProductionOrder record);

    /**
     * 插入记录（选择性插入）
     */
    int insertSelective(ProductionOrder record);

    /**
     * 根据条件查询记录列表
     */
    List<ProductionOrder> selectByExample(ProductionOrderExample example);

    /**
     * 根据主键查询记录
     */
    ProductionOrder selectByPrimaryKey(Long id);

    /**
     * 根据条件更新记录（选择性更新）
     */
    int updateByExampleSelective(@Param("record") ProductionOrder record, @Param("example") ProductionOrderExample example);

    /**
     * 根据条件更新记录（所有字段）
     */
    int updateByExample(@Param("record") ProductionOrder record, @Param("example") ProductionOrderExample example);

    /**
     * 根据主键更新记录（选择性更新）
     */
    int updateByPrimaryKeySelective(ProductionOrder record);

    /**
     * 根据主键更新记录（所有字段）
     */
    int updateByPrimaryKey(ProductionOrder record);
}
