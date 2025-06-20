package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.InventoryCheck;
import com.jsh.erp.datasource.vo.InventoryCheckVo4List;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 盘点单扩展Mapper
 * 
 * @author jshERP Team
 * @version 1.0.0
 */
public interface InventoryCheckMapperEx {
    List<InventoryCheckVo4List> selectByConditionInventoryCheck(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("creatorArray") String[] creatorArray,
            @Param("statusArray") String[] statusArray,
            @Param("number") String number,
            @Param("linkNumber") String linkNumber,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("materialParam") String materialParam,
            @Param("depotList") String depotList,
            @Param("remark") String remark,
            @Param("offset") Integer offset,
            @Param("rows") Integer rows);

    Long countsByInventoryCheck(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("creatorArray") String[] creatorArray,
            @Param("statusArray") String[] statusArray,
            @Param("number") String number,
            @Param("linkNumber") String linkNumber,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("materialParam") String materialParam,
            @Param("depotList") String depotList,
            @Param("remark") String remark);

    int batchDeleteInventoryCheckByIds(@Param("updateTime") String updateTime, @Param("updater") Long updater, @Param("ids") String ids[]);

    List<InventoryCheck> getInventoryCheckListByIds(@Param("ids") String[] ids);

    List<InventoryCheckVo4List> findByAll(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("depotList") String depotList,
            @Param("organId") Long organId);

    BigDecimal findAllMoney(
            @Param("type") String type,
            @Param("subType") String subType,
            @Param("beginTime") String beginTime,
            @Param("endTime") String endTime,
            @Param("depotList") String depotList,
            @Param("organId") Long organId);
}