package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.ScheduleShift;
import com.jsh.erp.datasource.entities.ScheduleShiftExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 班次定义Mapper接口
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
public interface ScheduleShiftMapper {
    long countByExample(ScheduleShiftExample example);

    int deleteByExample(ScheduleShiftExample example);

    int deleteByPrimaryKey(Long id);

    int insert(ScheduleShift record);

    int insertSelective(ScheduleShift record);

    List<ScheduleShift> selectByExample(ScheduleShiftExample example);

    ScheduleShift selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ScheduleShift record, @Param("example") ScheduleShiftExample example);

    int updateByExample(@Param("record") ScheduleShift record, @Param("example") ScheduleShiftExample example);

    int updateByPrimaryKeySelective(ScheduleShift record);

    int updateByPrimaryKey(ScheduleShift record);
}
