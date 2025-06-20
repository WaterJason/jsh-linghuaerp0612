package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.InventoryCheckItem;
import com.jsh.erp.datasource.vo.InventoryCheckItemVo4DetailByTypeAndId;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 盘点明细扩展Mapper
 * 
 * @author jshERP Team
 * @version 1.0.0
 */
public interface InventoryCheckItemMapperEx {
    List<InventoryCheckItem> selectByHeaderId(@Param("headerId") Long headerId);

    List<InventoryCheckItemVo4DetailByTypeAndId> getDetailByTypeAndId(
            @Param("type") String type,
            @Param("headerId") Long headerId);

    int insertInventoryCheckItemList(@Param("items") List<InventoryCheckItem> items);

    int batchDeleteInventoryCheckItemByIds(@Param("ids") String[] ids);

    BigDecimal findTotalPriceByHeaderId(@Param("headerId") Long headerId);

    List<InventoryCheckItem> getListByHeaderId(@Param("headerId") Long headerId);

    int batchDeleteInventoryCheckItemByHeaderIds(@Param("headerIds") String[] headerIds);
}