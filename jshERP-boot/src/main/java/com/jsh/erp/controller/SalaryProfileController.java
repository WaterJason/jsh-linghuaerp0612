package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.datasource.entities.SalaryProfile;
import com.jsh.erp.service.salary.SalaryProfileService;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.PageUtils;
import com.jsh.erp.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工薪酬档案控制器
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/salary/profile")
@Api(tags = {"薪酬档案管理"})
public class SalaryProfileController extends BaseController {

    @Resource
    private SalaryProfileService salaryProfileService;

    /**
     * 获取薪酬档案列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取薪酬档案列表")
    public TableDataInfo getList(@RequestParam(value = "search", required = false) String search,
                                HttpServletRequest request) throws Exception {
        PageUtils.startPage();
        
        String employeeName = StringUtil.getInfo(search, "employeeName");
        String department = StringUtil.getInfo(search, "department");
        String position = StringUtil.getInfo(search, "position");
        String salaryStatus = StringUtil.getInfo(search, "salaryStatus");
        
        List<SalaryProfile> list = salaryProfileService.select(employeeName, department, position, salaryStatus, request);
        return getDataTable(list);
    }

    /**
     * 根据ID获取薪酬档案详情
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据ID获取薪酬档案详情")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        SalaryProfile profile = salaryProfileService.getById(id);
        if (profile != null) {
            objectMap.put("info", profile);
            return returnJson(objectMap, "查询成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "数据不存在", ErpInfo.ERROR.code);
        }
    }

    /**
     * 根据员工ID获取薪酬档案
     */
    @GetMapping(value = "/getByEmployeeId")
    @ApiOperation(value = "根据员工ID获取薪酬档案")
    public String getByEmployeeId(@RequestParam("employeeId") Long employeeId, 
                                 HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        SalaryProfile profile = salaryProfileService.getSalaryProfileByEmployeeId(employeeId, request);
        if (profile != null) {
            objectMap.put("info", profile);
            return returnJson(objectMap, "查询成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "该员工暂无薪酬档案", ErpInfo.ERROR.code);
        }
    }

    /**
     * 新增薪酬档案
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增薪酬档案")
    public String add(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        SalaryProfile profile = JSONObject.parseObject(obj.toJSONString(), SalaryProfile.class);
        int result = salaryProfileService.insertSalaryProfile(profile, request);
        
        if (result > 0) {
            return returnJson(objectMap, "新增成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "新增失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 更新薪酬档案
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新薪酬档案")
    public String update(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        SalaryProfile profile = JSONObject.parseObject(obj.toJSONString(), SalaryProfile.class);
        int result = salaryProfileService.updateSalaryProfile(profile, request);
        
        if (result > 0) {
            return returnJson(objectMap, "更新成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "更新失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 删除薪酬档案
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除薪酬档案")
    public String delete(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        // 检查是否可以删除
        int canDelete = salaryProfileService.checkIsCanDelete(id, request);
        if (canDelete != 1) {
            return returnJson(objectMap, "该档案存在关联数据，不能删除", ErpInfo.ERROR.code);
        }

        int result = salaryProfileService.deleteSalaryProfile(id, request);
        if (result > 0) {
            return returnJson(objectMap, "删除成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "删除失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 批量删除薪酬档案
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除薪酬档案")
    public String deleteBatch(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        int result = salaryProfileService.batchDeleteSalaryProfile(ids, request);
        if (result > 0) {
            return returnJson(objectMap, "批量删除成功", ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, "批量删除失败", ErpInfo.ERROR.code);
        }
    }

    /**
     * 启用/禁用薪酬档案
     */
    @PutMapping(value = "/updateStatus")
    @ApiOperation(value = "启用/禁用薪酬档案")
    public String updateStatus(@RequestParam("id") Long id, 
                              @RequestParam("status") String status,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        
        SalaryProfile profile = salaryProfileService.getById(id);
        if (profile != null) {
            profile.setSalaryStatus(status);
            int result = salaryProfileService.updateSalaryProfile(profile, request);
            if (result > 0) {
                return returnJson(objectMap, "状态更新成功", ErpInfo.OK.code);
            }
        }

        return returnJson(objectMap, "状态更新失败", ErpInfo.ERROR.code);
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
