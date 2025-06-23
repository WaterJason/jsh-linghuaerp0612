package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionReport;
import com.jsh.erp.datasource.entities.ProductionReportExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 生产报工基础Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionReportMapper {
    
    long countByExample(ProductionReportExample example);

    int deleteByExample(ProductionReportExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ProductionReport record);

    int insertSelective(ProductionReport record);

    List<ProductionReport> selectByExample(ProductionReportExample example);

    ProductionReport selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ProductionReport record, @Param("example") ProductionReportExample example);

    int updateByExample(@Param("record") ProductionReport record, @Param("example") ProductionReportExample example);

    int updateByPrimaryKeySelective(ProductionReport record);

    int updateByPrimaryKey(ProductionReport record);
}
