package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.InventoryCheck;
import com.jsh.erp.datasource.entities.InventoryCheckExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InventoryCheckMapper {
    long countByExample(InventoryCheckExample example);

    int deleteByExample(InventoryCheckExample example);

    int deleteByPrimaryKey(Long id);

    int insert(InventoryCheck record);

    int insertSelective(InventoryCheck record);

    List<InventoryCheck> selectByExample(InventoryCheckExample example);

    InventoryCheck selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") InventoryCheck record, @Param("example") InventoryCheckExample example);

    int updateByExample(@Param("record") InventoryCheck record, @Param("example") InventoryCheckExample example);

    int updateByPrimaryKeySelective(InventoryCheck record);

    int updateByPrimaryKey(InventoryCheck record);
}