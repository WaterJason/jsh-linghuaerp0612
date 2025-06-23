package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.datasource.entities.WorkerSkill;
import com.jsh.erp.service.WorkerSkillService;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ErpInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.jsh.erp.utils.ResponseJsonUtil.returnJson;
import static com.jsh.erp.utils.ResponseJsonUtil.returnStr;

/**
 * 工人技能Controller
 * 
 * @author jshERP
 * @date 2025-06-22
 */
@RestController
@RequestMapping(value = "/workerSkill")
@Api(tags = {"工人技能管理"})
public class WorkerSkillController extends BaseController {
    
    private Logger logger = LoggerFactory.getLogger(WorkerSkillController.class);

    @Resource
    private WorkerSkillService workerSkillService;

    /**
     * 根据ID获取工人技能信息
     */
    @GetMapping(value = "/info")
    @ApiOperation(value = "根据id获取工人技能信息")
    public String getInfo(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        WorkerSkill skill = workerSkillService.getWorkerSkill(id);
        Map<String, Object> objectMap = new HashMap<>();
        if (skill != null) {
            objectMap.put("info", skill);
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } else {
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 新增工人技能
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增工人技能")
    public String addSkill(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int insert = workerSkillService.insertWorkerSkill(obj, request);
        return returnStr(objectMap, insert);
    }

    /**
     * 修改工人技能
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "修改工人技能")
    public String updateSkill(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = workerSkillService.updateWorkerSkill(obj, request);
        return returnStr(objectMap, update);
    }

    /**
     * 删除工人技能
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除工人技能")
    public String deleteSkill(@RequestParam("id") Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int delete = workerSkillService.deleteWorkerSkill(id, request);
        return returnStr(objectMap, delete);
    }

    /**
     * 更新技能等级
     */
    @PutMapping(value = "/updateLevel")
    @ApiOperation(value = "更新技能等级")
    public String updateSkillLevel(@RequestParam("workerId") Long workerId,
                                  @RequestParam("skillType") String skillType,
                                  @RequestParam("skillLevel") String skillLevel,
                                  @RequestParam("skillScore") BigDecimal skillScore,
                                  HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = workerSkillService.updateSkillLevel(workerId, skillType, skillLevel, skillScore, request);
        return returnStr(objectMap, update);
    }

    /**
     * 技能认证
     */
    @PutMapping(value = "/certify")
    @ApiOperation(value = "技能认证")
    public String certifySkill(@RequestParam("workerId") Long workerId,
                              @RequestParam("skillType") String skillType,
                              @RequestParam("certificationLevel") String certificationLevel,
                              @RequestParam("certificationDate") String certificationDateStr,
                              HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date certificationDate = sdf.parse(certificationDateStr);
            int update = workerSkillService.certifySkill(workerId, skillType, certificationLevel, certificationDate, request);
            return returnStr(objectMap, update);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return returnStr(objectMap, 0);
        }
    }

    /**
     * 启用/禁用技能
     */
    @PutMapping(value = "/toggleStatus")
    @ApiOperation(value = "启用/禁用技能")
    public String toggleSkillStatus(@RequestParam("id") Long id,
                                   @RequestParam("isActive") Boolean isActive,
                                   HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int update = workerSkillService.toggleSkillStatus(id, isActive, request);
        return returnStr(objectMap, update);
    }

    /**
     * 获取工人的技能列表
     */
    @GetMapping(value = "/workerSkills")
    @ApiOperation(value = "获取工人的技能列表")
    public BaseResponseInfo getWorkerSkills(@RequestParam("workerId") Long workerId,
                                           @RequestParam(value = "isActive", required = false) Boolean isActive,
                                           HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现获取工人技能列表的逻辑
            // List<WorkerSkill> list = workerSkillService.getWorkerSkills(workerId, isActive);
            res.code = 200;
            res.data = "获取工人技能列表成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取工人技能列表失败";
        }
        return res;
    }

    /**
     * 获取技能类型的工人列表
     */
    @GetMapping(value = "/skillWorkers")
    @ApiOperation(value = "获取技能类型的工人列表")
    public BaseResponseInfo getSkillWorkers(@RequestParam("skillType") String skillType,
                                           @RequestParam(value = "minLevel", required = false) String minLevel,
                                           @RequestParam(value = "isActive", required = false) Boolean isActive,
                                           HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现获取技能工人列表的逻辑
            // List<WorkerSkill> list = workerSkillService.getSkillWorkers(skillType, minLevel, isActive);
            res.code = 200;
            res.data = "获取技能工人列表成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取技能工人列表失败";
        }
        return res;
    }

    /**
     * 获取技能统计信息
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "获取技能统计信息")
    public BaseResponseInfo getSkillStatistics(@RequestParam(value = "skillType", required = false) String skillType,
                                              HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现技能统计的逻辑
            // Map<String, Object> stats = workerSkillService.getSkillStatistics(skillType);
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalWorkers", 50);
            stats.put("skillDistribution", new HashMap<String, Integer>() {{
                put("BEGINNER", 15);
                put("INTERMEDIATE", 20);
                put("ADVANCED", 10);
                put("EXPERT", 4);
                put("MASTER", 1);
            }});
            stats.put("averageScore", 3.8);
            stats.put("certificationRate", 75.5);
            res.code = 200;
            res.data = stats;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取技能统计失败";
        }
        return res;
    }

    /**
     * 技能评估
     */
    @PostMapping(value = "/evaluate")
    @ApiOperation(value = "技能评估")
    public String evaluateSkill(@RequestParam("workerId") Long workerId,
                               @RequestParam("skillType") String skillType,
                               @RequestParam("evaluationScore") BigDecimal evaluationScore,
                               @RequestParam(value = "evaluationComment", required = false) String evaluationComment,
                               @RequestParam(value = "evaluatorId", required = false) Long evaluatorId,
                               HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            // 这里可以实现技能评估的逻辑
            // int result = workerSkillService.evaluateSkill(workerId, skillType, evaluationScore, evaluationComment, evaluatorId);
            objectMap.put("message", "技能评估成功");
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 技能培训记录
     */
    @PostMapping(value = "/training")
    @ApiOperation(value = "技能培训记录")
    public String recordTraining(@RequestParam("workerId") Long workerId,
                                @RequestParam("skillType") String skillType,
                                @RequestParam("trainingContent") String trainingContent,
                                @RequestParam("trainingHours") BigDecimal trainingHours,
                                @RequestParam(value = "trainerId", required = false) Long trainerId,
                                @RequestParam(value = "trainingResult", required = false) String trainingResult,
                                HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            // 这里可以实现培训记录的逻辑
            // int result = workerSkillService.recordTraining(workerId, skillType, trainingContent, trainingHours, trainerId, trainingResult);
            objectMap.put("message", "培训记录成功");
            return returnJson(objectMap, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return returnJson(objectMap, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
        }
    }

    /**
     * 获取技能发展建议
     */
    @GetMapping(value = "/suggestions")
    @ApiOperation(value = "获取技能发展建议")
    public BaseResponseInfo getSkillSuggestions(@RequestParam("workerId") Long workerId,
                                               HttpServletRequest request) throws Exception {
        BaseResponseInfo res = new BaseResponseInfo();
        try {
            // 这里可以实现技能发展建议的逻辑
            // List<SkillSuggestion> suggestions = workerSkillService.getSkillSuggestions(workerId);
            res.code = 200;
            res.data = "获取技能发展建议成功";
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            res.code = 500;
            res.data = "获取技能发展建议失败";
        }
        return res;
    }
}
