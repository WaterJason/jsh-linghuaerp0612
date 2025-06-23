package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.base.TableDataInfo;
import com.jsh.erp.datasource.entities.ScheduleEntry;
import com.jsh.erp.datasource.entities.ScheduleEntryEx;
import com.jsh.erp.service.ScheduleEntryService;
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
 * 排班记录Controller
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
@RestController
@RequestMapping(value = "/scheduleEntry")
@Api(tags = {"排班管理"})
public class ScheduleEntryController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ScheduleEntryController.class);

    @Resource
    private ScheduleEntryService scheduleEntryService;
    
    @Resource
    private UserService userService;

    private static String SUCCESS = "操作成功";
    private static String ERROR = "操作失败";

    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取排班记录信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        ScheduleEntry scheduleEntry = scheduleEntryService.getScheduleEntry(id);
        Map<String, Object> objectMap = new HashMap<>();
        if (scheduleEntry != null) {
            objectMap.put("info", scheduleEntry);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "获取排班记录列表")
    public TableDataInfo getList(@RequestParam(value = Constants.SEARCH, required = false) String search,
                                 HttpServletRequest request) throws Exception {
        String employeeIdStr = StringUtil.getInfo(search, "employeeId");
        String startDate = StringUtil.getInfo(search, "startDate");
        String endDate = StringUtil.getInfo(search, "endDate");
        String status = StringUtil.getInfo(search, "status");
        
        Long employeeId = null;
        if (StringUtil.isNotEmpty(employeeIdStr)) {
            employeeId = Long.parseLong(employeeIdStr);
        }
        
        List<ScheduleEntryEx> list = scheduleEntryService.select(employeeId, startDate, endDate, status, request);
        return getDataTable(list);
    }

    @GetMapping(value = "/calendar")
    @ApiOperation(value = "获取日历视图排班数据")
    public BaseResponseInfo getCalendarData(@RequestParam("startDate") String startDate,
                                           @RequestParam("endDate") String endDate,
                                           HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            List<ScheduleEntryEx> list = scheduleEntryService.selectByDateRange(startDate, endDate, request);
            
            JSONArray dataArray = new JSONArray();
            for (ScheduleEntryEx entry : list) {
                JSONObject item = new JSONObject();
                item.put("id", entry.getId());
                item.put("title", entry.getEmployeeName() + " - " + entry.getShiftName());
                item.put("start", entry.getScheduleDateStr());
                item.put("backgroundColor", entry.getShiftColor());
                item.put("borderColor", entry.getShiftColor());
                item.put("employeeId", entry.getEmployeeId());
                item.put("employeeName", entry.getEmployeeName());
                item.put("shiftId", entry.getShiftId());
                item.put("shiftName", entry.getShiftName());
                item.put("shiftType", entry.getShiftType());
                item.put("startTime", entry.getShiftStartTime());
                item.put("endTime", entry.getShiftEndTime());
                item.put("status", entry.getStatus());
                item.put("statusName", entry.getStatusName());
                item.put("remark", entry.getRemark());
                dataArray.add(item);
            }
            
            res.code = 200;
            res.data = dataArray;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取日历数据失败";
        }
        return res;
    }

    @PostMapping(value = "/add")
    @ApiOperation(value = "新增排班记录")
    public String addScheduleEntry(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = scheduleEntryService.insertScheduleEntry(obj, request);
        return returnStr(objectMap, insert);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "修改排班记录")
    public String updateScheduleEntry(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = scheduleEntryService.updateScheduleEntry(obj, request);
        return returnStr(objectMap, update);
    }

    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除排班记录")
    public String deleteScheduleEntry(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = scheduleEntryService.deleteScheduleEntry(id, request);
        return returnStr(objectMap, delete);
    }

    @DeleteMapping(value = "/deleteBatch")
    @ApiOperation(value = "批量删除排班记录")
    public String batchDeleteScheduleEntry(@RequestParam("ids") String ids, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = scheduleEntryService.batchDeleteScheduleEntry(ids, request);
        return returnStr(objectMap, delete);
    }

    @PutMapping(value = "/batchUpdateStatus")
    @ApiOperation(value = "批量更新排班状态")
    public String batchUpdateStatus(@RequestParam("ids") String ids,
                                   @RequestParam("status") String status,
                                   HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = scheduleEntryService.batchUpdateStatus(ids, status, request);
        if (update > 0) {
            return returnJson(objectMap, SUCCESS, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ERROR, ErpInfo.ERROR.code);
        }
    }

    @GetMapping(value = "/workHours")
    @ApiOperation(value = "统计员工工作时长")
    public BaseResponseInfo getWorkHours(@RequestParam("employeeId") Long employeeId,
                                        @RequestParam("startDate") String startDate,
                                        @RequestParam("endDate") String endDate,
                                        HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            Double workHours = scheduleEntryService.sumWorkHoursByEmployee(employeeId, startDate, endDate, request);
            Map<String, Object> data = new HashMap<>();
            data.put("workHours", workHours);
            res.code = 200;
            res.data = data;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取工作时长失败";
        }
        return res;
    }

    @GetMapping(value = "/getStatusList")
    @ApiOperation(value = "获取排班状态列表")
    public JSONArray getStatusList() {
        JSONArray dataArray = new JSONArray();
        
        JSONObject scheduled = new JSONObject();
        scheduled.put("value", "SCHEDULED");
        scheduled.put("label", "已排班");
        dataArray.add(scheduled);
        
        JSONObject confirmed = new JSONObject();
        confirmed.put("value", "CONFIRMED");
        confirmed.put("label", "已确认");
        dataArray.add(confirmed);
        
        JSONObject cancelled = new JSONObject();
        cancelled.put("value", "CANCELLED");
        cancelled.put("label", "已取消");
        dataArray.add(cancelled);
        
        return dataArray;
    }
}
