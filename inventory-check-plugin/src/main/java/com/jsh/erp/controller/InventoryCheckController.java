package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.InventoryCheck;
import com.jsh.erp.datasource.vo.InventoryCheckVo4List;
import com.jsh.erp.service.InventoryCheckService;
import com.jsh.erp.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnStr;

/**
 * 盘点管理控制器
 * 
 * @author jshERP Team
 * @version 1.0.0
 */
@RestController
@RequestMapping(value = "/inventoryCheck")
@Api(tags = {"盘点管理"})
public class InventoryCheckController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(InventoryCheckController.class);

    @Resource
    private InventoryCheckService inventoryCheckService;

    /**
     * 查询盘点单列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "查询盘点单列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 @RequestParam(value = "type", required = false) String type,
                                 @RequestParam(value = "subType", required = false) String subType,
                                 @RequestParam(value = "number", required = false) String number,
                                 @RequestParam(value = "linkNumber", required = false) String linkNumber,
                                 @RequestParam(value = "beginTime", required = false) String beginTime,
                                 @RequestParam(value = "endTime", required = false) String endTime,
                                 @RequestParam(value = "materialParam", required = false) String materialParam,
                                 @RequestParam(value = "depotList", required = false) String depotList,
                                 @RequestParam(value = "creator", required = false) String creator,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "remark", required = false) String remark,
                                 HttpServletRequest request) throws Exception {
        Map<String, Object> parameterMap = new HashMap<>();
        if (StringUtil.isNotEmpty(search)) {
            JSONObject obj = JSONObject.parseObject(search);
            type = StringUtil.getInfo(obj, "type");
            subType = StringUtil.getInfo(obj, "subType");
            number = StringUtil.getInfo(obj, "number");
            linkNumber = StringUtil.getInfo(obj, "linkNumber");
            beginTime = StringUtil.getInfo(obj, "beginTime");
            endTime = StringUtil.getInfo(obj, "endTime");
            materialParam = StringUtil.getInfo(obj, "materialParam");
            depotList = StringUtil.getInfo(obj, "depotList");
            creator = StringUtil.getInfo(obj, "creator");
            status = StringUtil.getInfo(obj, "status");
            remark = StringUtil.getInfo(obj, "remark");
        }
        
        startPage();
        List<InventoryCheckVo4List> list = inventoryCheckService.select(type, subType, number, linkNumber,
                beginTime, endTime, materialParam, depotList, creator, status, remark,
                PageUtils.getStart(), PageUtils.getLimit());
        return getDataTable(list);
    }

    /**
     * 新增盘点单
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增盘点单")
    public String addInventoryCheck(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = inventoryCheckService.insertInventoryCheck(obj, request);
        return returnStr(objectMap, insert);
    }

    /**
     * 修改盘点单
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "修改盘点单")
    public String updateInventoryCheck(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = inventoryCheckService.updateInventoryCheck(obj, request);
        return returnStr(objectMap, update);
    }

    /**
     * 删除盘点单
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除盘点单")
    public String deleteInventoryCheck(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = inventoryCheckService.deleteInventoryCheck(id, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 批量删除盘点单
     */
    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除盘点单")
    public String batchDeleteInventoryCheck(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = inventoryCheckService.batchDeleteInventoryCheck(ids, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 根据ID查询盘点单
     */
    @GetMapping(value = "/findById")
    @ApiOperation(value = "根据ID查询盘点单")
    public String findById(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        InventoryCheck inventoryCheck = null;
        try {
            inventoryCheck = inventoryCheckService.getInventoryCheck(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> objectMap = new HashMap<>();
        if (inventoryCheck != null) {
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }
}