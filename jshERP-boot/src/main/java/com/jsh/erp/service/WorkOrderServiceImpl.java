package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.datasource.entities.WorkOrder;
import com.jsh.erp.datasource.mappers.WorkOrderMapper;
import com.jsh.erp.datasource.mappers.WorkOrderMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工单管理服务实现类
 * 
 * @author jshERP
 * @date 2025-06-21
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {
    private Logger logger = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    @Resource
    private WorkOrderMapper workOrderMapper;

    @Resource
    private WorkOrderMapperEx workOrderMapperEx;

    @Resource
    private UserService userService;

    @Resource
    private LogService logService;

    @Override
    public WorkOrder getWorkOrder(Long id) throws Exception {
        WorkOrder workOrder = null;
        try {
            workOrder = workOrderMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return workOrder;
    }

    @Override
    public WorkOrder getWorkOrderByNumber(String workOrderNo) throws Exception {
        WorkOrder workOrder = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            workOrder = workOrderMapperEx.selectByWorkOrderNo(workOrderNo, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return workOrder;
    }

    @Override
    public List<WorkOrder> select(String workOrderNo, String productionOrderId, String workType, 
                                 String status, String handlerId, String beginTime, String endTime) throws Exception {
        List<WorkOrder> list = null;
        try {
            PageUtils.startPage();
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            Long pId = StringUtil.isNotEmpty(productionOrderId) ? Long.parseLong(productionOrderId) : null;
            Long hId = StringUtil.isNotEmpty(handlerId) ? Long.parseLong(handlerId) : null;
            
            list = workOrderMapperEx.selectByCondition(workOrderNo, pId, workType, status, hId, 
                    beginTime, endTime, tenantId, null, null);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertWorkOrder(JSONObject obj, HttpServletRequest request) throws Exception {
        WorkOrder workOrder = JSONObject.parseObject(obj.toJSONString(), WorkOrder.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            workOrder.setTenantId(userInfo != null ? userInfo.getTenantId() : null);
            workOrder.setCreateBy(userInfo != null ? userInfo.getId() : null);
            workOrder.setCreateTime(new Date());
            workOrder.setDeleteFlag("0");
            
            // 生成工单号
            if (StringUtil.isEmpty(workOrder.getWorkOrderNo())) {
                workOrder.setWorkOrderNo(generateWorkOrderNo());
            }
            
            // 设置默认状态
            if (StringUtil.isEmpty(workOrder.getStatus())) {
                workOrder.setStatus("PENDING");
            }
            
            result = workOrderMapper.insertSelective(workOrder);
            logService.insertLog("工单管理", 
                    BusinessConstants.LOG_OPERATION_TYPE_ADD + workOrder.getWorkOrderNo(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateWorkOrder(JSONObject obj, HttpServletRequest request) throws Exception {
        WorkOrder workOrder = JSONObject.parseObject(obj.toJSONString(), WorkOrder.class);
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            workOrder.setUpdateBy(userInfo != null ? userInfo.getId() : null);
            workOrder.setUpdateTime(new Date());
            
            result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
            logService.insertLog("工单管理", 
                    BusinessConstants.LOG_OPERATION_TYPE_EDIT + workOrder.getWorkOrderNo(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteWorkOrder(Long id, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            WorkOrder workOrder = getWorkOrder(id);
            if (workOrder == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_READ_FAIL_CODE, "工单不存在");
            }
            
            User userInfo = userService.getCurrentUser();
            workOrder.setDeleteFlag("1");
            workOrder.setUpdateBy(userInfo != null ? userInfo.getId() : null);
            workOrder.setUpdateTime(new Date());
            
            result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
            logService.insertLog("工单管理", 
                    BusinessConstants.LOG_OPERATION_TYPE_DELETE + workOrder.getWorkOrderNo(), request);
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    public Map<String, Object> getKanbanData() throws Exception {
        Map<String, Object> result = new HashMap<>();
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;

            // 使用新的看板查询方法，获取包含产品和用户信息的工单数据
            List<Map<String, Object>> pendingOrders = workOrderMapperEx.selectWorkOrderListForKanban("PENDING", null, tenantId);
            List<Map<String, Object>> inProgressOrders = workOrderMapperEx.selectWorkOrderListForKanban("IN_PROGRESS", null, tenantId);
            List<Map<String, Object>> completedOrders = workOrderMapperEx.selectWorkOrderListForKanban("COMPLETED", null, tenantId);

            result.put("pendingOrders", pendingOrders);
            result.put("inProgressOrders", inProgressOrders);
            result.put("completedOrders", completedOrders);

        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getWorkOrderListForKanban(String status, String type) throws Exception {
        List<Map<String, Object>> result = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;

            result = workOrderMapperEx.selectWorkOrderListForKanban(status, type, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int assignWorkOrder(Long id, Long handlerId, String handlerName, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = workOrderMapperEx.updateHandler(id, handlerId, handlerName, 
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                // 同时更新状态为进行中
                workOrderMapperEx.updateStatus(id, "IN_PROGRESS", 
                        userInfo != null ? userInfo.getId() : null, new Date());
                logService.insertLog("工单管理", "派单给：" + handlerName, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int completeWorkOrder(Long id, String completeImages, String qualityNotes, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            WorkOrder workOrder = getWorkOrder(id);
            if (workOrder == null) {
                throw new BusinessRunTimeException(ExceptionConstants.DATA_READ_FAIL_CODE, "工单不存在");
            }
            
            User userInfo = userService.getCurrentUser();
            workOrder.setStatus("COMPLETED");
            workOrder.setCompleteTime(new Date());
            workOrder.setCompleteImages(completeImages);
            workOrder.setQualityNotes(qualityNotes);
            workOrder.setUpdateBy(userInfo != null ? userInfo.getId() : null);
            workOrder.setUpdateTime(new Date());
            
            result = workOrderMapper.updateByPrimaryKeySelective(workOrder);
            
            if (result > 0) {
                logService.insertLog("工单管理", "完工：" + workOrder.getWorkOrderNo(), request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateStatus(Long id, String status, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = workOrderMapperEx.updateStatus(id, status, 
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                logService.insertLog("工单管理", "更新工单状态：" + status, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    public Map<String, Object> getStatistics() throws Exception {
        Map<String, Object> statistics = new HashMap<>();
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            
            // 获取各状态的工单统计
            List<Object> statusStats = workOrderMapperEx.getStatusStatistics(tenantId);
            statistics.put("statusStatistics", statusStats);
            
            // 获取各工单类型的统计
            List<Object> typeStats = workOrderMapperEx.getWorkTypeStatistics(tenantId);
            statistics.put("workTypeStatistics", typeStats);
            
            // 获取成本统计
            String beginTime = new SimpleDateFormat("yyyy-MM-01").format(new Date());
            String endTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            List<Object> costStats = workOrderMapperEx.getCostStatistics(beginTime, endTime, tenantId);
            statistics.put("costStatistics", costStats);
            
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return statistics;
    }

    @Override
    public String generateWorkOrderNo() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "WO" + dateStr;
        
        // 查询当日最大序号
        String maxNo = workOrderMapperEx.findMaxWorkOrderNoByPrefix(prefix);
        
        int sequence = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            String seqStr = maxNo.substring(prefix.length());
            try {
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return prefix + String.format("%04d", sequence);
    }

    @Override
    public boolean checkWorkOrderNoExists(String workOrderNo) throws Exception {
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            WorkOrder order = workOrderMapperEx.selectByWorkOrderNo(workOrderNo, tenantId);
            return order != null;
        } catch (Exception e) {
            JshException.readFail(logger, e);
            return false;
        }
    }

    @Override
    public List<WorkOrder> getWorkOrdersByProductionOrderId(Long productionOrderId) throws Exception {
        List<WorkOrder> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            list = workOrderMapperEx.selectByProductionOrderId(productionOrderId, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    public List<WorkOrder> getWorkOrdersByHandlerId(Long handlerId) throws Exception {
        List<WorkOrder> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            list = workOrderMapperEx.selectByHandlerId(handlerId, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    public List<WorkOrder> getWorkOrdersByWorkType(String workType) throws Exception {
        List<WorkOrder> list = null;
        try {
            User userInfo = userService.getCurrentUser();
            Long tenantId = userInfo != null ? userInfo.getTenantId() : null;
            list = workOrderMapperEx.selectByWorkType(workType, tenantId);
        } catch (Exception e) {
            JshException.readFail(logger, e);
        }
        return list;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteWorkOrder(String ids, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            String[] idArray = ids.split(",");
            User userInfo = userService.getCurrentUser();
            result = workOrderMapperEx.batchDeleteByIds(new Date(), 
                    userInfo != null ? userInfo.getId() : null, idArray);
            
            if (result > 0) {
                logService.insertLog("工单管理", "批量删除工单：" + ids, request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateCost(Long id, String cost, String laborCost, String materialCost, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = workOrderMapperEx.updateCost(id, cost, laborCost, materialCost,
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                logService.insertLog("工单管理", "更新工单成本", request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateWorkHours(Long id, String actualHours, HttpServletRequest request) throws Exception {
        int result = 0;
        try {
            User userInfo = userService.getCurrentUser();
            result = workOrderMapperEx.updateWorkHours(id, actualHours,
                    userInfo != null ? userInfo.getId() : null, new Date());
            
            if (result > 0) {
                logService.insertLog("工单管理", "更新工单工时：" + actualHours + "小时", request);
            }
        } catch (Exception e) {
            JshException.writeFail(logger, e);
        }
        return result;
    }
}
