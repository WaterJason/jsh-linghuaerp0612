package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.service.cloisonne.CloisonneIntegrationService;
import com.jsh.erp.service.cloisonne.CloisonneScheduleService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 掐丝珐琅馆总览仪表板控制器
 * 展示如何集成jshERP现有模块的数据
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@RestController
@RequestMapping(value = "/cloisonne/dashboard")
@Api(tags = "掐丝珐琅馆总览仪表板")
public class CloisonneDashboardController {

    private static final Logger logger = LoggerFactory.getLogger(CloisonneDashboardController.class);

    @Autowired
    private CloisonneIntegrationService integrationService;

    @Autowired
    private CloisonneScheduleService scheduleService;

    /**
     * 获取仪表板概览数据
     * 集成多个模块的数据
     */
    @GetMapping(value = "/overview")
    @ApiOperation(value = "获取仪表板概览数据")
    public BaseResponseInfo getDashboardOverview(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 获取当前用户和租户信息
            Long userId = Long.valueOf(request.getHeader("userId"));
            Long tenantId = Long.valueOf(request.getHeader("tenantId"));

            // 检查用户权限
            if (!integrationService.hasCloisonnePermission(userId, "1001")) {
                res.code = 403;
                res.data = "无权限访问";
                return res;
            }

            Map<String, Object> overview = new HashMap<>();

            // 1. 获取今日值班人员信息(集成用户模块)
            List<Map<String, Object>> onDutyStaff = getOnDutyStaffWithUserInfo(tenantId);
            overview.put("onDutyStaff", onDutyStaff);
            overview.put("onDutyCount", onDutyStaff.size());

            // 2. 获取当前值班信息
            Map<String, Object> currentShiftInfo = scheduleService.findCurrentShiftInfo();
            overview.put("currentShiftInfo", currentShiftInfo);

            // 3. 获取今日任务统计
            Map<String, Object> taskStats = getTodayTaskStatistics(tenantId);
            overview.put("taskStats", taskStats);

            // 4. 获取销售数据概览(模拟数据，实际应该从销售模块获取)
            Map<String, Object> salesOverview = getSalesOverview(tenantId);
            overview.put("salesOverview", salesOverview);

            // 5. 获取用户按钮权限
            String buttonPermissions = integrationService.getUserButtonPermissions(userId, "1001");
            overview.put("buttonPermissions", buttonPermissions);

            res.code = 200;
            res.data = overview;

        } catch (Exception e) {
            logger.error("获取仪表板概览数据失败", e);
            res.code = 500;
            res.data = "获取数据失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 获取员工列表(集成用户模块)
     */
    @GetMapping(value = "/employees")
    @ApiOperation(value = "获取员工列表")
    public BaseResponseInfo getEmployees(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long tenantId = Long.valueOf(request.getHeader("tenantId"));
            
            // 从集成服务获取员工列表
            List<Map<String, Object>> employees = integrationService.getAllEmployees(tenantId);
            
            res.code = 200;
            res.data = employees;

        } catch (Exception e) {
            logger.error("获取员工列表失败", e);
            res.code = 500;
            res.data = "获取员工列表失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 创建咖啡店销售记录并生成财务记录
     */
    @PostMapping(value = "/coffee-sales")
    @ApiOperation(value = "创建咖啡店销售记录")
    public BaseResponseInfo createCoffeeSales(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long userId = Long.valueOf(request.getHeader("userId"));
            Long tenantId = Long.valueOf(request.getHeader("tenantId"));

            // 检查权限
            if (!integrationService.hasCloisonnePermission(userId, "1003")) {
                res.code = 403;
                res.data = "无权限操作";
                return res;
            }

            // 准备销售数据
            Map<String, Object> salesData = new HashMap<>();
            salesData.put("date", obj.getString("date"));
            salesData.put("revenue", obj.getBigDecimal("revenue"));
            salesData.put("orderCount", obj.getInteger("orderCount"));
            salesData.put("recorderId", userId);

            // 保存销售记录并生成财务记录(集成财务模块)
            boolean success = integrationService.generateCoffeeSalesFinanceRecord(salesData, tenantId);

            if (success) {
                res.code = 200;
                res.data = "销售记录创建成功，财务记录已自动生成";
            } else {
                res.code = 500;
                res.data = "销售记录创建失败";
            }

        } catch (Exception e) {
            logger.error("创建咖啡店销售记录失败", e);
            res.code = 500;
            res.data = "创建失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 创建POS订单并集成库存和财务
     */
    @PostMapping(value = "/pos-order")
    @ApiOperation(value = "创建POS订单")
    public BaseResponseInfo createPOSOrder(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long userId = Long.valueOf(request.getHeader("userId"));
            Long tenantId = Long.valueOf(request.getHeader("tenantId"));

            // 检查权限
            if (!integrationService.hasCloisonnePermission(userId, "1004")) {
                res.code = 403;
                res.data = "无权限操作";
                return res;
            }

            // 准备订单数据
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("orderNo", generateOrderNo());
            orderData.put("totalAmount", obj.getBigDecimal("totalAmount"));
            orderData.put("actualAmount", obj.getBigDecimal("actualAmount"));
            orderData.put("paymentMethod", obj.getString("paymentMethod"));
            orderData.put("cashierId", userId);

            // 创建订单并生成财务记录(集成财务模块)
            boolean success = integrationService.generatePOSSalesFinanceRecord(orderData, tenantId);

            if (success) {
                res.code = 200;
                res.data = "订单创建成功，财务记录已自动生成";
            } else {
                res.code = 500;
                res.data = "订单创建失败";
            }

        } catch (Exception e) {
            logger.error("创建POS订单失败", e);
            res.code = 500;
            res.data = "创建失败: " + e.getMessage();
        }
        return res;
    }

    /**
     * 同步商品到POS
     */
    @PostMapping(value = "/sync-material")
    @ApiOperation(value = "同步商品到POS")
    public BaseResponseInfo syncMaterialToPOS(@RequestBody JSONObject obj, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long tenantId = Long.valueOf(request.getHeader("tenantId"));
            Long materialId = obj.getLong("materialId");

            // 从商品模块同步商品信息
            Map<String, Object> posProduct = integrationService.syncMaterialToPOS(materialId, tenantId);

            if (posProduct != null) {
                res.code = 200;
                res.data = posProduct;
            } else {
                res.code = 404;
                res.data = "商品不存在或无权限";
            }

        } catch (Exception e) {
            logger.error("同步商品到POS失败", e);
            res.code = 500;
            res.data = "同步失败: " + e.getMessage();
        }
        return res;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取今日值班人员信息(集成用户信息)
     */
    private List<Map<String, Object>> getOnDutyStaffWithUserInfo(Long tenantId) {
        // 获取今日值班人员
        List<Map<String, Object>> onDutyStaff = scheduleService.findOnDutyStaff(LocalDate.now())
            .stream()
            .map(schedule -> {
                Map<String, Object> staff = new HashMap<>();
                staff.put("id", schedule.getEmployeeId());
                staff.put("name", schedule.getEmployeeName());
                staff.put("role", schedule.getEmployeeRole());
                staff.put("shift", schedule.getShiftType());
                staff.put("avatar", schedule.getEmployeeAvatar());
                staff.put("phone", schedule.getEmployeePhone());
                return staff;
            })
            .collect(java.util.stream.Collectors.toList());

        return onDutyStaff;
    }

    /**
     * 获取今日任务统计
     */
    private Map<String, Object> getTodayTaskStatistics(Long tenantId) {
        Map<String, Object> taskStats = new HashMap<>();
        // 这里应该从任务模块获取真实数据
        taskStats.put("totalTasks", 20);
        taskStats.put("completedTasks", 17);
        taskStats.put("completionRate", 85);
        return taskStats;
    }

    /**
     * 获取销售数据概览
     */
    private Map<String, Object> getSalesOverview(Long tenantId) {
        Map<String, Object> salesOverview = new HashMap<>();
        // 这里应该从销售模块获取真实数据
        salesOverview.put("coffeeSales", 1250.50);
        salesOverview.put("museumSales", 2380.00);
        salesOverview.put("totalSales", 3630.50);
        return salesOverview;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        return "POS" + System.currentTimeMillis();
    }
}
