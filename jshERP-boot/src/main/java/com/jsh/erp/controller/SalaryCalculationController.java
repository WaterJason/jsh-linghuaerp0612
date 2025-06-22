package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.datasource.entities.SalaryCalculation;
import com.jsh.erp.service.salary.SalaryCalculationService;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 薪酬计算控制器
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/salary/calculation")
@Api(tags = {"薪酬计算管理"})
public class SalaryCalculationController extends BaseController {

    @Resource
    private SalaryCalculationService salaryCalculationService;

    /**
     * 获取薪酬计算记录列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取薪酬计算记录列表")
    public TableDataInfo getList(@RequestParam(value = "search", required = false) String search,
                                HttpServletRequest request) throws Exception {
        PageUtils.startPage();
        
        String employeeName = StringUtil.getInfo(search, "employeeName");
        String calculationMonth = StringUtil.getInfo(search, "calculationMonth");
        String status = StringUtil.getInfo(search, "status");
        
        List<SalaryCalculation> list = salaryCalculationService.select(employeeName, calculationMonth, status, request);
        return getDataTable(list);
    }

    /**
     * 根据ID获取薪酬计算记录详情
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据ID获取薪酬计算记录详情")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        SalaryCalculation calculation = salaryCalculationService.getById(id);
        if (calculation != null) {
            objectMap.put("info", calculation);
            return returnJson(objectMap, "查询成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "数据不存在", ErpInfo.ERROR.code);
        }
    }

    /**
     * 执行薪酬计算
     */
    @PostMapping(value = "/calculate")
    @ApiOperation(value = "执行薪酬计算")
    public String calculate(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        String calculationMonth = obj.getString("calculationMonth");
        String employeeIdsStr = obj.getString("employeeIds");
        
        if (StringUtil.isEmpty(calculationMonth) || StringUtil.isEmpty(employeeIdsStr)) {
            return returnJson(objectMap, "计算月份和员工ID不能为空", ErpInfo.ERROR.code);
        }
        
        List<Long> employeeIds = Arrays.stream(employeeIdsStr.split(","))
                                      .map(Long::parseLong)
                                      .collect(Collectors.toList());
        
        int result = salaryCalculationService.calculateSalary(calculationMonth, employeeIds, request);
        
        if (result > 0) {
            objectMap.put("calculatedCount", result);
            return returnJson(objectMap, "薪酬计算完成，共计算" + result + "名员工", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "薪酬计算失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 审批薪酬计算
     */
    @PutMapping(value = "/approve")
    @ApiOperation(value = "审批薪酬计算")
    public String approve(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        String idsStr = obj.getString("ids");
        String status = obj.getString("status");
        String approveRemark = obj.getString("approveRemark");
        
        if (StringUtil.isEmpty(idsStr) || StringUtil.isEmpty(status)) {
            return returnJson(objectMap, "ID和状态不能为空", ErpInfo.ERROR.code);
        }
        
        List<Long> ids = Arrays.stream(idsStr.split(","))
                              .map(Long::parseLong)
                              .collect(Collectors.toList());
        
        int result = salaryCalculationService.approveSalaryCalculation(ids, status, approveRemark, request);
        
        if (result > 0) {
            String action = "APPROVED".equals(status) ? "审批通过" : "审批拒绝";
            return returnJson(objectMap, action + "成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "审批失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取薪酬统计信息
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取薪酬统计信息")
    public String getStatistics(@RequestParam("calculationMonth") String calculationMonth,
                               HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        Map<String, Object> statistics = salaryCalculationService.getSalaryStatistics(calculationMonth, request);
        if (statistics != null) {
            objectMap.put("statistics", statistics);
            return returnJson(objectMap, "查询成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "暂无统计数据", ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取待审批列表
     */
    @GetMapping(value = "/pendingApproval")
    @ApiOperation(value = "获取待审批列表")
    public TableDataInfo getPendingApproval(HttpServletRequest request) throws Exception {
        PageUtils.startPage();
        
        List<SalaryCalculation> list = salaryCalculationService.getPendingApprovalList(request);
        return getDataTable(list);
    }

    /**
     * 删除薪酬计算记录
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除薪酬计算记录")
    public String delete(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        int result = salaryCalculationService.deleteSalaryCalculation(id, request);
        if (result > 0) {
            return returnJson(objectMap, "删除成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "删除失败，只能删除草稿状态的记录", ErpInfo.ERROR.code);
        }
    }

    /**
     * 重新计算薪酬
     */
    @PutMapping(value = "/recalculate")
    @ApiOperation(value = "重新计算薪酬")
    public String recalculate(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        SalaryCalculation calculation = salaryCalculationService.getById(id);
        if (calculation == null) {
            return returnJson(objectMap, "计算记录不存在", ErpInfo.ERROR.code);
        }

        if (!"DRAFT".equals(calculation.getStatus()) && !"REJECTED".equals(calculation.getStatus())) {
            return returnJson(objectMap, "只能重新计算草稿或被拒绝的记录", ErpInfo.ERROR.code);
        }
        
        List<Long> employeeIds = Arrays.asList(calculation.getEmployeeId());
        int result = salaryCalculationService.calculateSalary(calculation.getCalculationMonth(), employeeIds, request);
        
        if (result > 0) {
            return returnJson(objectMap, "重新计算成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "重新计算失败", ErpInfo.ERROR.code);
        }
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
