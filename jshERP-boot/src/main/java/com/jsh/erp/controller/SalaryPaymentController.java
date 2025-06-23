package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.datasource.entities.SalaryPayment;
import com.jsh.erp.service.salary.SalaryPaymentService;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 薪酬发放控制器
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/salary/payment")
@Api(tags = {"薪酬发放管理"})
public class SalaryPaymentController extends BaseController {

    @Resource
    private SalaryPaymentService salaryPaymentService;

    /**
     * 获取薪酬发放记录列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取薪酬发放记录列表")
    public TableDataInfo getList(@RequestParam(value = "search", required = false) String search,
                                HttpServletRequest request) throws Exception {
        PageUtils.startPage();
        
        String employeeName = StringUtil.getInfo(search, "employeeName");
        String calculationMonth = StringUtil.getInfo(search, "calculationMonth");
        String paymentStatus = StringUtil.getInfo(search, "paymentStatus");
        
        List<SalaryPayment> list = salaryPaymentService.select(employeeName, calculationMonth, paymentStatus, request);
        return getDataTable(list);
    }

    /**
     * 根据ID获取薪酬发放记录详情
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据ID获取薪酬发放记录详情")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        SalaryPayment payment = salaryPaymentService.getById(id);
        if (payment != null) {
            objectMap.put("info", payment);
            return returnJson(objectMap, "查询成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "数据不存在", ErpInfo.ERROR.code);
        }
    }

    /**
     * 执行薪酬发放
     */
    @PostMapping(value = "/pay")
    @ApiOperation(value = "执行薪酬发放")
    public String pay(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        String calculationIdsStr = obj.getString("calculationIds");
        String paymentMethod = obj.getString("paymentMethod");
        
        if (StringUtil.isEmpty(calculationIdsStr)) {
            return returnJson(objectMap, "计算记录ID不能为空", ErpInfo.ERROR.code);
        }

        // TODO: 需要实现 createPaymentRecords 方法
        // int result = salaryPaymentService.createPaymentRecords(calculationIdsStr, paymentMethod, request);
        int result = 0; // 临时返回值

        if (result > 0) {
            objectMap.put("paymentCount", result);
            return returnJson(objectMap, "薪酬发放创建成功，共" + result + "条记录", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "薪酬发放创建失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 确认发放
     */
    @PutMapping(value = "/confirm")
    @ApiOperation(value = "确认发放")
    public String confirm(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        String idsStr = obj.getString("ids");
        
        if (StringUtil.isEmpty(idsStr)) {
            return returnJson(objectMap, "发放记录ID不能为空", ErpInfo.ERROR.code);
        }

        // TODO: 需要实现 confirmPayment 方法
        // int result = salaryPaymentService.confirmPayment(idsStr, request);
        int result = 0; // 临时返回值

        if (result > 0) {
            return returnJson(objectMap, "确认发放成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "确认发放失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 生成薪资条
     */
    @GetMapping(value = "/payslip")
    @ApiOperation(value = "生成薪资条")
    public String generatePayslip(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        // TODO: 需要实现 generatePayslip 方法
        // String payslipUrl = salaryPaymentService.generatePayslip(id, request);
        String payslipUrl = ""; // 临时返回值

        if (!StringUtil.isEmpty(payslipUrl)) {
            objectMap.put("payslipUrl", payslipUrl);
            return returnJson(objectMap, "薪资条生成成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "薪资条生成失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取待发放列表
     */
    @GetMapping(value = "/pending")
    @ApiOperation(value = "获取待发放列表")
    public TableDataInfo getPendingPayments(HttpServletRequest request) throws Exception {
        PageUtils.startPage();
        
        // TODO: 需要实现 getPendingPaymentList 方法
        // List<SalaryPayment> list = salaryPaymentService.getPendingPaymentList(request);
        List<SalaryPayment> list = new ArrayList<>(); // 临时返回空列表
        return getDataTable(list);
    }

    /**
     * 统一返回JSON格式
     */
    private String returnJson(Map<String, Object> objectMap, String message, int code) {
        JSONObject result = new JSONObject();
        result.put("message", message);
        result.put("code", code);
        result.put("data", objectMap);
        result.put("success", code == ErpInfo.OK.code);
        return result.toString();
    }
}
