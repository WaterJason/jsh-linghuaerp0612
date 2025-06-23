package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ScheduleEntry;
import com.jsh.erp.datasource.entities.ScheduleEntryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 排班记录Mapper接口
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
public interface ScheduleEntryMapper {
    long countByExample(ScheduleEntryExample example);

    int deleteByExample(ScheduleEntryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ScheduleEntry record);

    int insertSelective(ScheduleEntry record);

    List<ScheduleEntry> selectByExample(ScheduleEntryExample example);

    ScheduleEntry selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ScheduleEntry record, @Param("example") ScheduleEntryExample example);

    int updateByExample(@Param("record") ScheduleEntry record, @Param("example") ScheduleEntryExample example);

    int updateByPrimaryKeySelective(ScheduleEntry record);

    int updateByPrimaryKey(ScheduleEntry record);
}
