package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.ProductionWorkOrder;
import com.jsh.erp.datasource.entities.ProductionWorkOrderEx;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.mappers.ProductionWorkOrderMapper;
import com.jsh.erp.datasource.mappers.ProductionWorkOrderMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 生产工单Service
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@Service
public class ProductionWorkOrderService {
    
    private Logger logger = LoggerFactory.getLogger(ProductionWorkOrderService.class);

    @Resource
    private ProductionWorkOrderMapper productionWorkOrderMapper;
    
    @Resource
    private ProductionWorkOrderMapperEx productionWorkOrderMapperEx;
    
    @Resource
    private UserService userService;
    
    @Resource
    private LogService logService;
    
    @Resource
    private SequenceService sequenceService;

    /**
     * 根据ID获取生产工单
     */
    public ProductionWorkOrder getProductionWorkOrder(Long id) throws Exception {
        ProductionWorkOrder result = null;
        try {
            result = productionWorkOrderMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据ID获取生产工单详情（包含关联信息）
     */
    public ProductionWorkOrderEx getProductionWorkOrderDetail(Long id) throws Exception {
        ProductionWorkOrderEx result = null;
        try {
            result = productionWorkOrderMapperEx.findById(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 根据条件查询生产工单列表
     */
    public List<ProductionWorkOrderEx> select(String workOrderNumber, String workOrderName, String productName,
                                              String productionMode, String productionType, String priority,
                                              String status, String responsiblePerson, String workshopName,
                                              Date planStartTime, Date planEndTime, String sourceType,
                                              String sourceNumber, Integer offset, Integer rows) throws Exception {
        List<ProductionWorkOrderEx> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            list = productionWorkOrderMapperEx.selectByConditionWorkOrder(
                workOrderNumber, workOrderName, productName, productionMode, productionType,
                priority, status, responsiblePerson, workshopName, planStartTime, planEndTime,
                sourceType, sourceNumber, tenantId, offset, rows);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    /**
     * 根据条件统计生产工单数量
     */
    public Long countWorkOrder(String workOrderNumber, String workOrderName, String productName,
                               String productionMode, String productionType, String priority,
                               String status, String responsiblePerson, String workshopName,
                               Date planStartTime, Date planEndTime, String sourceType,
                               String sourceNumber) throws Exception {
        Long result = 0L;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionWorkOrderMapperEx.countByConditionWorkOrder(
                workOrderNumber, workOrderName, productName, productionMode, productionType,
                priority, status, responsiblePerson, workshopName, planStartTime, planEndTime,
                sourceType, sourceNumber, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 新增生产工单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertProductionWorkOrder(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionWorkOrder workOrder = JSONObject.parseObject(obj.toJSONString(), ProductionWorkOrder.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置基础信息
            workOrder.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            workOrder.setCreateUser(userInfo != null ? userInfo.getId() : null);
            workOrder.setCreateTime(new Date());
            workOrder.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
            
            // 生成工单编号
            if (StringUtil.isEmpty(workOrder.getWorkOrderNumber())) {
                workOrder.setWorkOrderNumber(generateWorkOrderNumber());
            }
            
            // 检查工单编号是否重复
            if (checkWorkOrderNumberExist(null, workOrder.getWorkOrderNumber()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_CODE,
                    String.format(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_MSG, workOrder.getWorkOrderNumber()));
            }
            
            // 设置默认状态
            if (StringUtil.isEmpty(workOrder.getStatus())) {
                workOrder.setStatus("PENDING");
            }
            
            // 设置默认优先级
            if (StringUtil.isEmpty(workOrder.getPriority())) {
                workOrder.setPriority("NORMAL");
            }
            
            // 设置默认制作模式
            if (StringUtil.isEmpty(workOrder.getProductionMode())) {
                workOrder.setProductionMode("STOCK_DRIVEN");
            }
            
            // 设置默认制作类型
            if (StringUtil.isEmpty(workOrder.getProductionType())) {
                workOrder.setProductionType("NORMAL");
            }
            
            // 初始化数量
            if (workOrder.getActualQuantity() == null) {
                workOrder.setActualQuantity(BigDecimal.ZERO);
            }
            
            // 初始化成本
            if (workOrder.getEstimatedCost() == null) {
                workOrder.setEstimatedCost(BigDecimal.ZERO);
            }
            if (workOrder.getActualCost() == null) {
                workOrder.setActualCost(BigDecimal.ZERO);
            }
            
            // 初始化工时
            if (workOrder.getEstimatedHours() == null) {
                workOrder.setEstimatedHours(BigDecimal.ZERO);
            }
            if (workOrder.getActualHours() == null) {
                workOrder.setActualHours(BigDecimal.ZERO);
            }
            
            result = productionWorkOrderMapper.insertSelective(workOrder);
            
            // 记录日志
            logService.insertLog("生产工单",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_ADD).append(workOrder.getWorkOrderName()).toString(),
                request);
                
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
        return result;
    }

    /**
     * 更新生产工单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateProductionWorkOrder(JSONObject obj, HttpServletRequest request) throws Exception {
        ProductionWorkOrder workOrder = JSONObject.parseObject(obj.toJSONString(), ProductionWorkOrder.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            
            // 设置更新信息
            workOrder.setUpdateUser(userInfo != null ? userInfo.getId() : null);
            workOrder.setUpdateTime(new Date());
            
            // 检查工单编号是否重复
            if (checkWorkOrderNumberExist(workOrder.getId(), workOrder.getWorkOrderNumber()) > 0) {
                throw new BusinessRunTimeException(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_CODE,
                    String.format(ExceptionConstants.MATERIAL_SERIAL_NUMBERE_ALREADY_EXISTS_MSG, workOrder.getWorkOrderNumber()));
            }
            
            result = productionWorkOrderMapper.updateByPrimaryKeySelective(workOrder);
            
            // 记录日志
            logService.insertLog("生产工单",
                new StringBuffer(BusinessConstants.LOG_OPERATION_TYPE_EDIT).append(workOrder.getWorkOrderName()).toString(),
                request);
                
        } catch (BusinessRunTimeException ex) {
            throw new BusinessRunTimeException(ex.getCode(), ex.getMessage());
        } catch (Exception e) {
            JshException.writeFail(logger, e);
            return 0;
        }
        return result;
    }

    /**
     * 删除生产工单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteProductionWorkOrder(Long id, HttpServletRequest request) throws Exception {
        return batchDeleteProductionWorkOrderByIds(id.toString(), request);
    }

    /**
     * 批量删除生产工单
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteProductionWorkOrder(String ids, HttpServletRequest request) throws Exception {
        return batchDeleteProductionWorkOrderByIds(ids, request);
    }

    /**
     * 批量删除生产工单（内部方法）
     */
    private int batchDeleteProductionWorkOrderByIds(String ids, HttpServletRequest request) throws Exception {
        String[] idArray = ids.split(",");
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updater = userInfo != null ? userInfo.getId() : null;
            
            result = productionWorkOrderMapperEx.batchDeleteWorkOrderByIds(updateTime, updater, idArray);
            
            // 记录日志
            logService.insertLog("生产工单",
                "批量删除生产工单，数量：" + idArray.length,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 检查工单编号是否存在
     */
    public int checkWorkOrderNumberExist(Long id, String workOrderNumber) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            result = productionWorkOrderMapperEx.checkWorkOrderNumberExist(id, workOrderNumber, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 生成工单编号
     */
    private String generateWorkOrderNumber() {
        // 生成格式：WO + YYYYMMDD + 4位序号
        String dateStr = Tools.dateToStr(new Date(), "yyyyMMdd");
        String prefix = "WO" + dateStr;
        
        // 这里可以调用序号服务生成唯一序号
        // 暂时使用时间戳后4位
        String suffix = String.format("%04d", System.currentTimeMillis() % 10000);
        
        return prefix + suffix;
    }

    /**
     * 更新工单状态
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateWorkOrderStatus(Long id, String status, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;
            
            result = productionWorkOrderMapperEx.updateWorkOrderStatus(id, status, updateTime, updateUser);
            
            // 记录日志
            logService.insertLog("生产工单",
                "更新工单状态：" + status,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 更新工单实际数量
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateActualQuantity(Long id, BigDecimal actualQuantity, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            Date updateTime = new Date();
            Long updateUser = userInfo != null ? userInfo.getId() : null;
            
            result = productionWorkOrderMapperEx.updateActualQuantity(id, actualQuantity, updateTime, updateUser);
            
            // 记录日志
            logService.insertLog("生产工单",
                "更新工单实际数量：" + actualQuantity,
                request);
                
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    /**
     * 获取工单统计信息
     */
    public Map<String, Object> getWorkOrderStatistics(Date startDate, Date endDate) throws Exception {
        Map<String, Object> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionWorkOrderMapperEx.getWorkOrderStatistics(tenantId, startDate, endDate);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取即将到期的工单列表
     */
    public List<ProductionWorkOrderEx> getExpiringWorkOrders(Integer days) throws Exception {
        List<ProductionWorkOrderEx> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionWorkOrderMapperEx.getExpiringWorkOrders(tenantId, days);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    /**
     * 获取超期的工单列表
     */
    public List<ProductionWorkOrderEx> getOverdueWorkOrders() throws Exception {
        List<ProductionWorkOrderEx> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            result = productionWorkOrderMapperEx.getOverdueWorkOrders(tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }
}
