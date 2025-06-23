package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionTask;
import com.jsh.erp.datasource.entities.ProductionTaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 生产任务基础Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionTaskMapper {
    
    long countByExample(ProductionTaskExample example);

    int deleteByExample(ProductionTaskExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ProductionTask record);

    int insertSelective(ProductionTask record);

    List<ProductionTask> selectByExample(ProductionTaskExample example);

    ProductionTask selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ProductionTask record, @Param("example") ProductionTaskExample example);

    int updateByExample(@Param("record") ProductionTask record, @Param("example") ProductionTaskExample example);

    int updateByPrimaryKeySelective(ProductionTask record);

    int updateByPrimaryKey(ProductionTask record);
}
