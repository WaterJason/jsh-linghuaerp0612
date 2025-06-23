package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.ScheduleShift;
import com.jsh.erp.service.ScheduleShiftService;
import com.jsh.erp.service.UserService;
import com.jsh.erp.utils.Constants;
import com.jsh.erp.utils.ErpInfo;
import com.jsh.erp.utils.StringUtil;
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
 * 班次定义Controller
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
@RestController
@RequestMapping(value = "/scheduleShift")
@Api(tags = {"班次管理"})
public class ScheduleShiftController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ScheduleShiftController.class);

    @Resource
    private ScheduleShiftService scheduleShiftService;
    
    @Resource
    private UserService userService;

    private static String SUCCESS = "操作成功";
    private static String ERROR = "操作失败";

    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取班次信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        ScheduleShift scheduleShift = scheduleShiftService.getScheduleShift(id);
        Map<String, Object> objectMap = new HashMap<>();
        if (scheduleShift != null) {
            objectMap.put("info", scheduleShift);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "获取班次列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        String shiftName = StringUtil.getInfo(search, "shiftName");
        String shiftType = StringUtil.getInfo(search, "shiftType");
        Long tenantId = userService.getCurrentUser().getTenantId();
        
        List<ScheduleShift> list = scheduleShiftService.select(shiftName, shiftType, tenantId);
        return getDataTable(list);
    }

    @GetMapping(value = "/activeList")
    @ApiOperation(value = "获取启用的班次列表")
    public JSONArray getActiveList(HttpServletRequest request) throws Exception {
        JSONArray dataArray = new JSONArray();
        try {
            Long tenantId = userService.getCurrentUser().getTenantId();
            List<ScheduleShift> dataList = scheduleShiftService.getActiveShifts(tenantId);
            if (dataList != null) {
                for (ScheduleShift shift : dataList) {
                    JSONObject item = new JSONObject();
                    item.put("id", shift.getId());
                    item.put("shiftName", shift.getShiftName());
                    item.put("shiftType", shift.getShiftType());
                    item.put("startTime", shift.getStartTime());
                    item.put("endTime", shift.getEndTime());
                    item.put("durationHours", shift.getDurationHours());
                    item.put("color", shift.getColor());
                    dataArray.add(item);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return dataArray;
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增班次")
    public String addScheduleShift(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = scheduleShiftService.insertScheduleShift(obj, request);
        return returnStr(objectMap, insert);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "修改班次")
    public String updateScheduleShift(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = scheduleShiftService.updateScheduleShift(obj, request);
        return returnStr(objectMap, update);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除班次")
    public String deleteScheduleShift(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = scheduleShiftService.deleteScheduleShift(id, request);
        return returnStr(objectMap, delete);
    }

    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除班次")
    public String batchDeleteScheduleShift(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = scheduleShiftService.batchDeleteScheduleShift(ids, request);
        return returnStr(objectMap, delete);
    }

    @GetMapping(value = "/checkIsNameExist")
    @ApiOperation(value = "检查班次名称是否存在")
    public String checkIsNameExist(@RequestParam(value = "id", required = false) Long id,
                                   @RequestParam(value = "name", required = false) String name,
                                   HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        Long tenantId = userService.getCurrentUser().getTenantId();
        int exist = scheduleShiftService.checkIsNameExist(id, name, tenantId);
        if (exist > 0) {
            objectMap.put("status", true);
        } else {
            objectMap.put("status", false);
        }
        return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
    }

    @GetMapping(value = "/getShiftTypes")
    @ApiOperation(value = "获取班次类型列表")
    public JSONArray getShiftTypes() {
        JSONArray dataArray = new JSONArray();
        
        JSONObject fullDay = new JSONObject();
        fullDay.put("value", "FULL_DAY");
        fullDay.put("label", "全天班");
        dataArray.add(fullDay);
        
        JSONObject morning = new JSONObject();
        morning.put("value", "MORNING");
        morning.put("label", "上午班");
        dataArray.add(morning);
        
        JSONObject afternoon = new JSONObject();
        afternoon.put("value", "AFTERNOON");
        afternoon.put("label", "下午班");
        dataArray.add(afternoon);
        
        JSONObject evening = new JSONObject();
        evening.put("value", "EVENING");
        evening.put("label", "晚班");
        dataArray.add(evening);
        
        return dataArray;
    }
}
