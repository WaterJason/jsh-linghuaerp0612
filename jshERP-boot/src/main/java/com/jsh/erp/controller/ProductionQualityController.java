package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.service.ProductionQualityService;
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
 * 生产质检管理控制器
 * 提供生产质检相关的API接口
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/production/quality")
@Api(tags = "生产质检管理")
public class ProductionQualityController {
    
    private Logger logger = LoggerFactory.getLogger(ProductionQualityController.class);
    
    @Resource
    private ProductionQualityService productionQualityService;
    
    /**
     * 分页查询质检列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "分页查询质检列表")
    public BaseResponseInfo getQualityInspectionList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(name = "inspectionNumber", required = false) String inspectionNumber,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "inspectorName", required = false) String inspectorName,
            @RequestParam(name = "qualityGrade", required = false) String qualityGrade,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request) {
        
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject params = new JSONObject();
            params.put("inspectionNumber", inspectionNumber);
            params.put("productName", productName);
            params.put("status", status);
            params.put("inspectorName", inspectorName);
            params.put("qualityGrade", qualityGrade);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            PageUtils.startPage(pageNo, pageSize);
            JSONObject result = productionQualityService.getQualityInspectionList(params, request);
            
            res.code = 200;
            res.data = result;
        } catch (Exception e) {
            logger.error("查询质检列表失败", e);
            res.code = 500;
            res.data = "查询失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 新增质检记录
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增质检记录")
    public BaseResponseInfo addQualityInspection(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = productionQualityService.addQualityInspection(params, request);
            if (success) {
                res.code = 200;
                res.data = "新增成功";
            } else {
                res.code = 500;
                res.data = "新增失败";
            }
        } catch (Exception e) {
            logger.error("新增质检记录失败", e);
            res.code = 500;
            res.data = "新增失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 更新质检记录
     */
    @PostMapping(value = "/update")
    @ApiOperation(value = "更新质检记录")
    public BaseResponseInfo updateQualityInspection(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = productionQualityService.updateQualityInspection(params, request);
            if (success) {
                res.code = 200;
                res.data = "更新成功";
            } else {
                res.code = 500;
                res.data = "更新失败";
            }
        } catch (Exception e) {
            logger.error("更新质检记录失败", e);
            res.code = 500;
            res.data = "更新失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 删除质检记录
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除质检记录")
    public BaseResponseInfo deleteQualityInspection(@RequestParam("id") Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = productionQualityService.deleteQualityInspection(id, request);
            if (success) {
                res.code = 200;
                res.data = "删除成功";
            } else {
                res.code = 500;
                res.data = "删除失败";
            }
        } catch (Exception e) {
            logger.error("删除质检记录失败", e);
            res.code = 500;
            res.data = "删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 批量删除质检记录
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除质检记录")
    public BaseResponseInfo deleteBatchQualityInspection(@RequestParam("ids") String ids, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = productionQualityService.deleteBatchQualityInspection(ids, request);
            if (success) {
                res.code = 200;
                res.data = "批量删除成功";
            } else {
                res.code = 500;
                res.data = "批量删除失败";
            }
        } catch (Exception e) {
            logger.error("批量删除质检记录失败", e);
            res.code = 500;
            res.data = "批量删除失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 确认质检结果
     */
    @PostMapping(value = "/confirm")
    @ApiOperation(value = "确认质检结果")
    public BaseResponseInfo confirmInspection(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = productionQualityService.confirmInspection(params, request);
            if (success) {
                res.code = 200;
                res.data = "确认质检结果成功";
            } else {
                res.code = 500;
                res.data = "确认质检结果失败";
            }
        } catch (Exception e) {
            logger.error("确认质检结果失败", e);
            res.code = 500;
            res.data = "确认质检结果失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 开始质检
     */
    @PostMapping(value = "/start")
    @ApiOperation(value = "开始质检")
    public BaseResponseInfo startInspection(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Long id = params.getLong("id");
            boolean success = productionQualityService.startInspection(id, request);
            if (success) {
                res.code = 200;
                res.data = "开始质检成功";
            } else {
                res.code = 500;
                res.data = "开始质检失败";
            }
        } catch (Exception e) {
            logger.error("开始质检失败", e);
            res.code = 500;
            res.data = "开始质检失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 完成质检
     */
    @PostMapping(value = "/complete")
    @ApiOperation(value = "完成质检")
    public BaseResponseInfo completeInspection(@RequestBody JSONObject params, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            boolean success = productionQualityService.completeInspection(params, request);
            if (success) {
                res.code = 200;
                res.data = "完成质检成功";
            } else {
                res.code = 500;
                res.data = "完成质检失败";
            }
        } catch (Exception e) {
            logger.error("完成质检失败", e);
            res.code = 500;
            res.data = "完成质检失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取质检详情
     */
    @GetMapping(value = "/detail/{id}")
    @ApiOperation(value = "获取质检详情")
    public BaseResponseInfo getInspectionDetail(@PathVariable("id") Long id, HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject detail = productionQualityService.getInspectionDetail(id, request);
            res.code = 200;
            res.data = detail;
        } catch (Exception e) {
            logger.error("获取质检详情失败", e);
            res.code = 500;
            res.data = "获取详情失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 导出Excel
     */
    @GetMapping(value = "/exportXls")
    @ApiOperation(value = "导出Excel")
    public void exportExcel(
            @RequestParam(name = "inspectionNumber", required = false) String inspectionNumber,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "inspectorName", required = false) String inspectorName,
            @RequestParam(name = "qualityGrade", required = false) String qualityGrade,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime,
            HttpServletRequest request, HttpServletResponse response) {
        
        try {
            JSONObject params = new JSONObject();
            params.put("inspectionNumber", inspectionNumber);
            params.put("productName", productName);
            params.put("status", status);
            params.put("inspectorName", inspectorName);
            params.put("qualityGrade", qualityGrade);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            
            productionQualityService.exportExcel(params, request, response);
        } catch (Exception e) {
            logger.error("导出Excel失败", e);
        }
    }
    
    /**
     * 获取质检统计
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取质检统计")
    public BaseResponseInfo getInspectionStatistics(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject statistics = productionQualityService.getInspectionStatistics(request);
            res.code = 200;
            res.data = statistics;
        } catch (Exception e) {
            logger.error("获取质检统计失败", e);
            res.code = 500;
            res.data = "获取统计失败：" + e.getMessage();
        }
        return res;
    }
    
    /**
     * 获取质检标准
     */
    @GetMapping(value = "/standards")
    @ApiOperation(value = "获取质检标准")
    public BaseResponseInfo getInspectionStandards(HttpServletRequest request) {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            JSONObject standards = productionQualityService.getInspectionStandards(request);
            res.code = 200;
            res.data = standards;
        } catch (Exception e) {
            logger.error("获取质检标准失败", e);
            res.code = 500;
            res.data = "获取质检标准失败：" + e.getMessage();
        }
        return res;
    }
}
