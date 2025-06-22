package com.jsh.erp.datasource.mappers;

import com.jsh.erp.datasource.entities.WorkerSkill;
import com.jsh.erp.datasource.entities.WorkerSkillExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 工人技能基础Mapper接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public interface WorkerSkillMapper {
    
    long countByExample(WorkerSkillExample example);

    int deleteByExample(WorkerSkillExample example);

    int deleteByPrimaryKey(Long id);

    int insert(WorkerSkill record);

    int insertSelective(WorkerSkill record);

    List<WorkerSkill> selectByExample(WorkerSkillExample example);

    WorkerSkill selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") WorkerSkill record, @Param("example") WorkerSkillExample example);

    int updateByExample(@Param("record") WorkerSkill record, @Param("example") WorkerSkillExample example);

    int updateByPrimaryKeySelective(WorkerSkill record);

    int updateByPrimaryKey(WorkerSkill record);
}
