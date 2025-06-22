package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.AccessoryProductionService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.PageUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 配饰制作管理控制器
 * 提供配饰制作相关的API接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/accessory")
@Api(tags = "配饰制作管理")
public class AccessoryProductionController {
    
    private Logger logger = LoggerFactory.getLogger(AccessoryProductionController.class);
    
    @Resource
    private AccessoryProductionService accessoryProductionService;
    
    /**
     * 分页查询配饰制作列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "分页查询配饰制作列表")
    public BaseResponseInfo getAccessoryProductionList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "orderNumber", required = false) String orderNumber,
            @RequestParam(name = "semiProductName", required = false) String semiProductName,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "accessoryType", required = false) String accessoryType,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("orderNumber", orderNumber);
            params.put("semiProductName", semiProductName);
            params.put("status", status);
            params.put("accessoryType", accessoryType);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            PageUtils.startPage(pageNo, pageSize);
            JSONObject result = accessoryProductionService.getAccessoryProductionList(params, request);
            
            res.code = 200;
            res.data = result;
        } catch (Exception e) {
            logger.error("查询配饰制作列表失败", e);
            res.code = 500;
            res.data = "查询失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 新增配饰制作
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增配饰制作")
    public BaseResponseInfo addAccessoryProduction(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = accessoryProductionService.addAccessoryProduction(params, request);
            if (success) {
                res.code = 200;
                res.data = "新增成功";
            } else {
                res.code = 500;
                res.data = "新增失败";
            }
        } catch (Exception e) {
            logger.error("新增配饰制作失败", e);
            res.code = 500;
            res.data = "新增失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 更新配饰制作
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "更新配饰制作")
    public BaseResponseInfo updateAccessoryProduction(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = accessoryProductionService.updateAccessoryProduction(params, request);
            if (success) {
                res.code = 200;
                res.data = "更新成功";
            } else {
                res.code = 500;
                res.data = "更新失败";
            }
        } catch (Exception e) {
            logger.error("更新配饰制作失败", e);
            res.code = 500;
            res.data = "更新失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 删除配饰制作
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除配饰制作")
    public BaseResponseInfo deleteAccessoryProduction(@RequestParam("id") Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = accessoryProductionService.deleteAccessoryProduction(id, request);
            if (success) {
                res.code = 200;
                res.data = "删除成功";
            } else {
                res.code = 500;
                res.data = "删除失败";
            }
        } catch (Exception e) {
            logger.error("删除配饰制作失败", e);
            res.code = 500;
            res.data = "删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 批量删除配饰制作
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除配饰制作")
    public BaseResponseInfo deleteBatchAccessoryProduction(@RequestParam("ids") String ids, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = accessoryProductionService.deleteBatchAccessoryProduction(ids, request);
            if (success) {
                res.code = 200;
                res.data = "批量删除成功";
            } else {
                res.code = 500;
                res.data = "批量删除失败";
            }
        } catch (Exception e) {
            logger.error("批量删除配饰制作失败", e);
            res.code = 500;
            res.data = "批量删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 开始制作
     */
    @PostMapping(value = "/start")
    @ApiOperation(value = "开始制作")
    public BaseResponseInfo startProduction(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long id = params.getLong("id");
            boolean success = accessoryProductionService.startProduction(id, request);
            if (success) {
                res.code = 200;
                res.data = "开始制作成功";
            } else {
                res.code = 500;
                res.data = "开始制作失败";
            }
        } catch (Exception e) {
            logger.error("开始制作失败", e);
            res.code = 500;
            res.data = "开始制作失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 完成制作
     */
    @PostMapping(value = "/complete")
    @ApiOperation(value = "完成制作")
    public BaseResponseInfo completeProduction(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long id = params.getLong("id");
            boolean success = accessoryProductionService.completeProduction(id, request);
            if (success) {
                res.code = 200;
                res.data = "完成制作成功";
            } else {
                res.code = 500;
                res.data = "完成制作失败";
            }
        } catch (Exception e) {
            logger.error("完成制作失败", e);
            res.code = 500;
            res.data = "完成制作失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 取消制作
     */
    @PostMapping(value = "/cancel")
    @ApiOperation(value = "取消制作")
    public BaseResponseInfo cancelProduction(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long id = params.getLong("id");
            boolean success = accessoryProductionService.cancelProduction(id, request);
            if (success) {
                res.code = 200;
                res.data = "取消制作成功";
            } else {
                res.code = 500;
                res.data = "取消制作失败";
            }
        } catch (Exception e) {
            logger.error("取消制作失败", e);
            res.code = 500;
            res.data = "取消制作失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 导出Excel
     */
    @GetMapping(value = "/exportExcel")
    @ApiOperation(value = "导出Excel")
    public void exportExcel(
            @RequestParam(name = "orderNumber", required = false) String orderNumber,
            @RequestParam(name = "semiProductName", required = false) String semiProductName,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "accessoryType", required = false) String accessoryType,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request, HttpServletResponse response) {
        
        try {
            JSONObject params = new JSONObject();
            params.put("orderNumber", orderNumber);
            params.put("semiProductName", semiProductName);
            params.put("status", status);
            params.put("accessoryType", accessoryType);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            accessoryProductionService.exportExcel(params, request, response);
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
        }
    }
    
    /**
     * 获取制作统计
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取制作统计")
    public BaseResponseInfo getProductionStatistics(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject statistics = accessoryProductionService.getProductionStatistics(request);
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error("获取制作统计失败", e);
            res.code = 500;
            res.data = "获取统计失败：" + e.getMessage();
        }
        return res;
    }
}
