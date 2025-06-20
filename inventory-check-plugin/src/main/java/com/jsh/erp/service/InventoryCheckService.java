package com.jsh.erp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.InventoryCheckMapper;
import com.jsh.erp.datasource.mappers.InventoryCheckMapperEx;
import com.jsh.erp.datasource.mappers.InventoryCheckItemMapper;
import com.jsh.erp.datasource.mappers.InventoryCheckItemMapperEx;
import com.jsh.erp.datasource.vo.*;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static com.jsh.erp.utils.Tools.getCenternTime;
import static com.jsh.erp.utils.Tools.getNow3;

@Service
public class InventoryCheckService {
    private Logger logger = LoggerFactory.getLogger(InventoryCheckService.class);

    @Resource
    private InventoryCheckMapper inventoryCheckMapper;
    @Resource
    private InventoryCheckMapperEx inventoryCheckMapperEx;
    @Resource
    private InventoryCheckItemMapper inventoryCheckItemMapper;
    @Resource
    private InventoryCheckItemMapperEx inventoryCheckItemMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private OrganizationService organizationService;
    @Resource
    private DepotService depotService;
    @Resource
    private MaterialService materialService;

    public InventoryCheck getInventoryCheck(long id) throws Exception {
        InventoryCheck result = null;
        try {
            result = inventoryCheckMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    public List<InventoryCheck> getInventoryCheck() throws Exception {
        InventoryCheckExample example = new InventoryCheckExample();
        List<InventoryCheck> list = null;
        try {
            list = inventoryCheckMapper.selectByExample(example);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    public List<InventoryCheckVo4List> select(String type, String subType, String number, String linkNumber,
                                              String beginTime, String endTime, String materialParam,
                                              String depotList, String creator, String status, String remark,
                                              int offset, int rows) throws Exception {
        List<InventoryCheckVo4List> resList = new ArrayList<>();
        try {
            String[] creatorArray = null;
            if (StringUtil.isNotEmpty(creator)) {
                creatorArray = creator.split(",");
            }
            String[] statusArray = null;
            if (StringUtil.isNotEmpty(status)) {
                statusArray = status.split(",");
            }
            List<InventoryCheckVo4List> list = inventoryCheckMapperEx.selectByConditionInventoryCheck(type, subType,
                    creatorArray, statusArray, number, linkNumber, beginTime, endTime, materialParam,
                    depotList, remark, offset, rows);
            if (null != list) {
                for (InventoryCheckVo4List ic : list) {
                    resList.add(ic);
                }
            }
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return resList;
    }

    public Long countInventoryCheck(String type, String subType, String number, String linkNumber,
                                    String beginTime, String endTime, String materialParam,
                                    String depotList, String creator, String status, String remark) throws Exception {
        Long result = null;
        try {
            String[] creatorArray = null;
            if (StringUtil.isNotEmpty(creator)) {
                creatorArray = creator.split(",");
            }
            String[] statusArray = null;
            if (StringUtil.isNotEmpty(status)) {
                statusArray = status.split(",");
            }
            result = inventoryCheckMapperEx.countsByInventoryCheck(type, subType, creatorArray, statusArray,
                    number, linkNumber, beginTime, endTime, materialParam, depotList, remark);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertInventoryCheck(JSONObject obj, HttpServletRequest request) throws Exception {
        InventoryCheck inventoryCheck = JSONObject.parseObject(obj.toJSONString(), InventoryCheck.class);
        int result = 0;
        try {
            // 填充基本信息
            inventoryCheck.setId((long) (Math.random() * 9 + 1) * 100000 + System.currentTimeMillis());
            inventoryCheck.setCreateTime(new Date());
            inventoryCheck.setStatus(BusinessConstants.BILLS_STATUS_AUDIT);
            inventoryCheck.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 填充租户信息
            String tenantId = obj.getString("tenantId");
            if (StringUtil.isNotEmpty(tenantId)) {
                inventoryCheck.setTenantId(Long.parseLong(tenantId));
            }
            
            result = inventoryCheckMapper.insertSelective(inventoryCheck);
            logService.insertLog("盘点", 
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(inventoryCheck.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateInventoryCheck(JSONObject obj, HttpServletRequest request) throws Exception {
        InventoryCheck inventoryCheck = JSONObject.parseObject(obj.toJSONString(), InventoryCheck.class);
        int result = 0;
        try {
            inventoryCheck.setOperTime(new Date());
            result = inventoryCheckMapper.updateByPrimaryKeySelective(inventoryCheck);
            logService.insertLog("盘点",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(inventoryCheck.getNumber()).toString(),
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteInventoryCheck(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            InventoryCheck inventoryCheck = getInventoryCheck(id);
            if (inventoryCheck == null) {
                throw new BusinessRunTimeException(ExceptionConstants.INVENTORY_CHECK_NOT_EXIST_CODE,
                        ExceptionConstants.INVENTORY_CHECK_NOT_EXIST_MSG);
            }
            inventoryCheck.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
            result = inventoryCheckMapper.updateByPrimaryKeySelective(inventoryCheck);
            logService.insertLog("盘点",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_DELETE).append(inventoryCheck.getNumber()).toString(),
                request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteInventoryCheck(String ids, HttpServletRequest request) throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        int result = 0;
        try {
            for (Long id : idList) {
                result += deleteInventoryCheck(id, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    // 在这里添加 logService 注入
    @Resource
    private LogService logService;
}