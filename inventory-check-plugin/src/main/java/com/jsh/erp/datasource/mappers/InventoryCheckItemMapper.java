package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.InventoryCheckItem;
import com.jsh.erp.datasource.entities.InventoryCheckItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface InventoryCheckItemMapper {
    long countByExample(InventoryCheckItemExample example);

    int deleteByExample(InventoryCheckItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(InventoryCheckItem record);

    int insertSelective(InventoryCheckItem record);

    List<InventoryCheckItem> selectByExample(InventoryCheckItemExample example);

    InventoryCheckItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") InventoryCheckItem record, @Param("example") InventoryCheckItemExample example);

    int updateByExample(@Param("record") InventoryCheckItem record, @Param("example") InventoryCheckItemExample example);

    int updateByPrimaryKeySelective(InventoryCheckItem record);

    int updateByPrimaryKey(InventoryCheckItem record);
}