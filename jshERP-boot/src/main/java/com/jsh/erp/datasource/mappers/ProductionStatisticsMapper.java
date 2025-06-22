package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ProductionStatistics;
import com.jsh.erp.datasource.entities.ProductionStatisticsExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 生产统计基础Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface ProductionStatisticsMapper {
    
    long countByExample(ProductionStatisticsExample example);

    int deleteByExample(ProductionStatisticsExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ProductionStatistics record);

    int insertSelective(ProductionStatistics record);

    List<ProductionStatistics> selectByExample(ProductionStatisticsExample example);

    ProductionStatistics selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ProductionStatistics record, @Param("example") ProductionStatisticsExample example);

    int updateByExample(@Param("record") ProductionStatistics record, @Param("example") ProductionStatisticsExample example);

    int updateByPrimaryKeySelective(ProductionStatistics record);

    int updateByPrimaryKey(ProductionStatistics record);
}
