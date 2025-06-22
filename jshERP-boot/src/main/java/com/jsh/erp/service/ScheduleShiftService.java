package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.ScheduleShift;
import com.jsh.erp.datasource.entities.ScheduleShiftExample;
import com.jsh.erp.datasource.mappers.ScheduleShiftMapper;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 班次定义Service
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
@Service
public class ScheduleShiftService {
    private Logger logger = LoggerFactory.getLogger(ScheduleShiftService.class);

    @Resource
    private ScheduleShiftMapper scheduleShiftMapper;
    
    @Resource
    private LogService logService;
    
    @Resource
    private UserService userService;

    /**
     * 根据ID获取班次信息
     */
    public ScheduleShift getScheduleShift(Long id) throws Exception {
        ScheduleShift result = null;
        try {
            result = scheduleShiftMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 查询班次列表
     */
    public List<ScheduleShift> select(String shiftName, String shiftType, Long tenantId) throws Exception {
        List<ScheduleShift> list = null;
        try {
            PageUtils.startPage();
            ScheduleShiftExample example = new ScheduleShiftExample();
            ScheduleShiftExample.Criteria criteria = example.createCriteria();
            
            if (StringUtil.isNotEmpty(shiftName)) {
                criteria.andShiftNameLike("%" + shiftName + "%");
            }
            if (StringUtil.isNotEmpty(shiftType)) {
                criteria.andShiftTypeEqualTo(shiftType);
            }
            if (tenantId != null) {
                criteria.andTenantIdEqualTo(tenantId);
            }
            criteria.andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            
            example.setOrderByClause("sort_order asc, id desc");
            list = scheduleShiftMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 获取所有启用的班次
     */
    public List<ScheduleShift> getActiveShifts(Long tenantId) throws Exception {
        List<ScheduleShift> list = null;
        try {
            ScheduleShiftExample example = new ScheduleShiftExample();
            ScheduleShiftExample.Criteria criteria = example.createCriteria();
            criteria.andIsActiveEqualTo((byte) 1);
            if (tenantId != null) {
                criteria.andTenantIdEqualTo(tenantId);
            }
            criteria.andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            
            example.setOrderByClause("sort_order asc");
            list = scheduleShiftMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 统计班次数量
     */
    public Long countScheduleShift(String shiftName, String shiftType, Long tenantId) throws Exception {
        Long result = null;
        try {
            ScheduleShiftExample example = new ScheduleShiftExample();
            ScheduleShiftExample.Criteria criteria = example.createCriteria();
            
            if (StringUtil.isNotEmpty(shiftName)) {
                criteria.andShiftNameLike("%" + shiftName + "%");
            }
            if (StringUtil.isNotEmpty(shiftType)) {
                criteria.andShiftTypeEqualTo(shiftType);
            }
            if (tenantId != null) {
                criteria.andTenantIdEqualTo(tenantId);
            }
            criteria.andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
            
            result = scheduleShiftMapper.countByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增班次
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertScheduleShift(JSONObject obj, HttpServletRequest request) throws Exception {
        ScheduleShift scheduleShift = JSONObject.parseObject(obj.toJSONString(), ScheduleShift.class);
        
        // 设置默认值
        if (scheduleShift.getIsActive() == null) {
            scheduleShift.setIsActive((byte) 1);
        }
        if (scheduleShift.getSortOrder() == null) {
            scheduleShift.setSortOrder(0);
        }
        if (StringUtil.isEmpty(scheduleShift.getColor())) {
            scheduleShift.setColor("#1890ff");
        }
        if (StringUtil.isEmpty(scheduleShift.getDeleteFlag())) {
            scheduleShift.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        }
        
        // 设置租户ID和创建信息
        Long userId = userService.getUserId(request);
        Long tenantId = userService.getCurrentUser().getTenantId();
        scheduleShift.setTenantId(tenantId);
        scheduleShift.setCreateBy(userId);
        scheduleShift.setCreateTime(new Date());
        
        int result = 0;
        try {
            result = scheduleShiftMapper.insertSelective(scheduleShift);
            logService.insertLog("班次管理",
                    BusinessConstants.LOG_OPERATION_TYPE_ADD + scheduleShift.getShiftName(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 修改班次
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateScheduleShift(JSONObject obj, HttpServletRequest request) throws Exception {
        ScheduleShift scheduleShift = JSONObject.parseObject(obj.toJSONString(), ScheduleShift.class);
        
        // 设置更新信息
        Long userId = userService.getUserId(request);
        scheduleShift.setUpdateBy(userId);
        scheduleShift.setUpdateTime(new Date());
        
        int result = 0;
        try {
            result = scheduleShiftMapper.updateByPrimaryKeySelective(scheduleShift);
            logService.insertLog("班次管理",
                    BusinessConstants.LOG_OPERATION_TYPE_EDIT + scheduleShift.getShiftName(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 删除班次
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteScheduleShift(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteScheduleShift(id.toString(), request);
    }

    /**
     * 批量删除班次
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteScheduleShift(String ids, HttpServletRequest request) throws Exception {
        String[] idArray = ids.split(",");
        int result = 0;
        
        try {
            for (String idStr : idArray) {
                Long id = Long.parseLong(idStr);
                ScheduleShift scheduleShift = new ScheduleShift();
                scheduleShift.setId(id);
                scheduleShift.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
                scheduleShift.setUpdateTime(new Date());
                scheduleShift.setUpdateBy(userService.getUserId(request));
                
                scheduleShiftMapper.updateByPrimaryKeySelective(scheduleShift);
                result++;
            }
            
            logService.insertLog("班次管理",
                    BusinessConstants.LOG_OPERATION_TYPE_DELETE + "批量删除" + result + "条记录", request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 检查班次名称是否存在
     */
    public int checkIsNameExist(Long id, String name, Long tenantId) throws Exception {
        ScheduleShiftExample example = new ScheduleShiftExample();
        ScheduleShiftExample.Criteria criteria = example.createCriteria();
        
        if (id != null) {
            criteria.andIdNotEqualTo(id);
        }
        criteria.andShiftNameEqualTo(name);
        if (tenantId != null) {
            criteria.andTenantIdEqualTo(tenantId);
        }
        criteria.andDeleteFlagNotEqualTo(BusinessConstants.DELETE_FLAG_DELETED);
        
        List<ScheduleShift> list = null;
        try {
            list = scheduleShiftMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list == null ? 0 : list.size();
    }
}
