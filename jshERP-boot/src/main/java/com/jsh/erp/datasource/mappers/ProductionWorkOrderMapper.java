package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionWorkOrder;
import com.jsh.erp.datasource.entities.ProductionWorkOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 生产工单基础Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionWorkOrderMapper {
    
    long countByExample(ProductionWorkOrderExample example);

    int deleteByExample(ProductionWorkOrderExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ProductionWorkOrder record);

    int insertSelective(ProductionWorkOrder record);

    List<ProductionWorkOrder> selectByExample(ProductionWorkOrderExample example);

    ProductionWorkOrder selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ProductionWorkOrder record, @Param("example") ProductionWorkOrderExample example);

    int updateByExample(@Param("record") ProductionWorkOrder record, @Param("example") ProductionWorkOrderExample example);

    int updateByPrimaryKeySelective(ProductionWorkOrder record);

    int updateByPrimaryKey(ProductionWorkOrder record);
}
