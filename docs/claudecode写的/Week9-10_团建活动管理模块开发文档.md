# Week 9-10: 团建活动管理模块开发文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第二阶段 - 业务支撑模块
- **估算工期**: 10天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第3、4、5章规范

---

## 概述

本文档为Week 9-10的团建活动管理模块开发提供详细的实施指导。主要实现团建活动管理、场地预订、人员分配、预算核算等核心功能，建立完整的团建活动管理体系，为聆花文化的非遗团建业务提供专业的管理工具。

---

## 数据库设计 (1天)

### 1. 团建活动主表设计

**表名**: `jsh_teambuilding_activity`

```sql
-- 团建活动主表
CREATE TABLE `jsh_teambuilding_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_no` varchar(32) NOT NULL COMMENT '活动编号',
  `activity_name` varchar(200) NOT NULL COMMENT '活动名称',
  `activity_type` varchar(50) NOT NULL COMMENT '活动类型(非遗体验/传统文化/定制活动)',
  `client_company` varchar(200) NOT NULL COMMENT '客户企业',
  `contact_person` varchar(100) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `participant_count` int(11) NOT NULL DEFAULT '0' COMMENT '参与人数',
  `activity_date` datetime NOT NULL COMMENT '活动日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `venue_id` bigint(20) DEFAULT NULL COMMENT '场地ID',
  `venue_address` varchar(500) DEFAULT NULL COMMENT '场地地址',
  `total_budget` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '总预算',
  `actual_cost` decimal(10,2) DEFAULT '0.00' COMMENT '实际费用',
  `profit_margin` decimal(5,2) DEFAULT '0.00' COMMENT '利润率%',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0-待确认,1-已确认,2-进行中,3-已完成,4-已取消)',
  `instructor_id` bigint(20) DEFAULT NULL COMMENT '主讲师ID',
  `assistant_id` bigint(20) DEFAULT NULL COMMENT '助理ID',
  `activity_content` text COMMENT '活动内容描述',
  `special_requirements` text COMMENT '特殊要求',
  `materials_needed` text COMMENT '所需材料清单',
  `risk_assessment` text COMMENT '风险评估',
  `feedback_score` decimal(3,1) DEFAULT NULL COMMENT '客户评分',
  `feedback_content` text COMMENT '客户反馈内容',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_no` (`activity_no`, `tenant_id`),
  KEY `idx_activity_date` (`activity_date`),
  KEY `idx_client_company` (`client_company`),
  KEY `idx_status` (`status`),
  KEY `idx_instructor` (`instructor_id`),
  KEY `idx_venue` (`venue_id`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团建活动主表';
```

### 2. 参与人员表设计

**表名**: `jsh_teambuilding_participant`

```sql
-- 团建参与人员表
CREATE TABLE `jsh_teambuilding_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `participant_name` varchar(100) NOT NULL COMMENT '参与者姓名',
  `participant_phone` varchar(20) DEFAULT NULL COMMENT '参与者电话',
  `participant_role` varchar(50) DEFAULT NULL COMMENT '参与者角色(主讲师/助理/学员)',
  `hourly_rate` decimal(8,2) DEFAULT '0.00' COMMENT '时薪标准',
  `work_hours` decimal(4,1) DEFAULT '0.0' COMMENT '工作时长',
  `commission_rate` decimal(5,2) DEFAULT '0.00' COMMENT '提成比例%',
  `base_salary` decimal(8,2) DEFAULT '0.00' COMMENT '基础工资',
  `commission_amount` decimal(8,2) DEFAULT '0.00' COMMENT '提成金额',
  `total_salary` decimal(8,2) DEFAULT '0.00' COMMENT '总薪酬',
  `attendance_status` char(1) DEFAULT '0' COMMENT '出勤状态(0-待确认,1-已确认,2-缺席)',
  `performance_rating` char(1) DEFAULT NULL COMMENT '表现评分(1-5分)',
  `special_contribution` varchar(500) DEFAULT NULL COMMENT '特殊贡献',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  KEY `idx_activity` (`activity_id`),
  KEY `idx_participant_name` (`participant_name`),
  KEY `idx_role` (`participant_role`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`),
  CONSTRAINT `fk_participant_activity` FOREIGN KEY (`activity_id`) REFERENCES `jsh_teambuilding_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团建参与人员表';
```

### 3. 费用明细表设计

**表名**: `jsh_teambuilding_expense`

```sql
-- 团建费用明细表
CREATE TABLE `jsh_teambuilding_expense` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `expense_type` varchar(50) NOT NULL COMMENT '费用类型(场地费/材料费/人工费/交通费/餐饮费/其他)',
  `expense_name` varchar(200) NOT NULL COMMENT '费用名称',
  `expense_description` varchar(500) DEFAULT NULL COMMENT '费用描述',
  `planned_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '计划金额',
  `actual_amount` decimal(10,2) DEFAULT '0.00' COMMENT '实际金额',
  `supplier_name` varchar(200) DEFAULT NULL COMMENT '供应商名称',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `payment_status` char(1) DEFAULT '0' COMMENT '支付状态(0-未支付,1-已支付,2-部分支付)',
  `payment_date` date DEFAULT NULL COMMENT '支付日期',
  `invoice_no` varchar(100) DEFAULT NULL COMMENT '发票号码',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件URL',
  `approval_status` char(1) DEFAULT '0' COMMENT '审批状态(0-待审批,1-已审批,2-已拒绝)',
  `approver_id` bigint(20) DEFAULT NULL COMMENT '审批人ID',
  `approval_date` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_comment` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  KEY `idx_activity` (`activity_id`),
  KEY `idx_expense_type` (`expense_type`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`),
  CONSTRAINT `fk_expense_activity` FOREIGN KEY (`activity_id`) REFERENCES `jsh_teambuilding_activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团建费用明细表';
```

### 4. 场地管理表设计

**表名**: `jsh_venue_management`

```sql
-- 场地管理表
CREATE TABLE `jsh_venue_management` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `venue_code` varchar(32) NOT NULL COMMENT '场地编码',
  `venue_name` varchar(200) NOT NULL COMMENT '场地名称',
  `venue_type` varchar(50) NOT NULL COMMENT '场地类型(室内/室外/多功能厅/工作坊)',
  `venue_address` varchar(500) NOT NULL COMMENT '场地地址',
  `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `capacity` int(11) NOT NULL DEFAULT '0' COMMENT '容纳人数',
  `hourly_rate` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '时租价格',
  `daily_rate` decimal(8,2) DEFAULT '0.00' COMMENT '日租价格',
  `facilities` text COMMENT '设施设备描述',
  `available_times` varchar(500) DEFAULT NULL COMMENT '可用时间段',
  `booking_rules` text COMMENT '预订规则',
  `cancellation_policy` text COMMENT '取消政策',
  `transportation_info` text COMMENT '交通信息',
  `photos` text COMMENT '场地照片URL列表(JSON)',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '状态(0-停用,1-可用,2-维护中)',
  `rating` decimal(3,1) DEFAULT NULL COMMENT '场地评分',
  `preferred_venue` char(1) DEFAULT '0' COMMENT '是否优选场地(0-否,1-是)',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_venue_code` (`venue_code`, `tenant_id`),
  KEY `idx_venue_type` (`venue_type`),
  KEY `idx_capacity` (`capacity`),
  KEY `idx_status` (`status`),
  KEY `idx_preferred` (`preferred_venue`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='场地管理表';
```

---

## 后端开发 (4天)

### 1. TeambuildingActivityService.java - 团建活动服务

**文件路径**: `com.jsh.erp.teambuilding.service.TeambuildingActivityService`

```java
package com.jsh.erp.teambuilding.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.teambuilding.datasource.entities.TeambuildingActivity;
import com.jsh.erp.teambuilding.datasource.entities.TeambuildingParticipant;
import com.jsh.erp.teambuilding.datasource.entities.TeambuildingExpense;
import com.jsh.erp.teambuilding.datasource.mappers.TeambuildingActivityMapper;
import com.jsh.erp.teambuilding.datasource.mappers.TeambuildingActivityMapperEx;
import com.jsh.erp.teambuilding.datasource.mappers.TeambuildingParticipantMapper;
import com.jsh.erp.teambuilding.datasource.mappers.TeambuildingExpenseMapper;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 团建活动服务
 * 提供团建活动管理的核心业务逻辑
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class TeambuildingActivityService {
    private Logger logger = LoggerFactory.getLogger(TeambuildingActivityService.class);

    @Resource
    private TeambuildingActivityMapper teambuildingActivityMapper;
    @Resource
    private TeambuildingActivityMapperEx teambuildingActivityMapperEx;
    @Resource
    private TeambuildingParticipantMapper participantMapper;
    @Resource
    private TeambuildingExpenseMapper expenseMapper;
    @Resource
    private VenueManagementService venueManagementService;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;

    /**
     * 创建团建活动
     * 包含资源冲突检测和预算初步核算
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createActivity(JSONObject activityInfo, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证活动信息
            validateActivityInfo(activityInfo);
            
            // 2. 检查资源冲突
            boolean hasConflict = checkResourceConflict(activityInfo);
            if (hasConflict) {
                throw new BusinessRunTimeException("选定时间和场地存在资源冲突，请重新选择");
            }
            
            // 3. 生成活动编号
            String activityNo = generateActivityNo();
            
            // 4. 创建活动记录
            TeambuildingActivity activity = new TeambuildingActivity();
            activity.setActivityNo(activityNo);
            activity.setActivityName(activityInfo.getString("activityName"));
            activity.setActivityType(activityInfo.getString("activityType"));
            activity.setClientCompany(activityInfo.getString("clientCompany"));
            activity.setContactPerson(activityInfo.getString("contactPerson"));
            activity.setContactPhone(activityInfo.getString("contactPhone"));
            activity.setParticipantCount(activityInfo.getInteger("participantCount"));
            activity.setActivityDate(activityInfo.getDate("activityDate"));
            activity.setStartTime(activityInfo.getTime("startTime"));
            activity.setEndTime(activityInfo.getTime("endTime"));
            activity.setVenueId(activityInfo.getLong("venueId"));
            activity.setVenueAddress(activityInfo.getString("venueAddress"));
            activity.setActivityContent(activityInfo.getString("activityContent"));
            activity.setSpecialRequirements(activityInfo.getString("specialRequirements"));
            activity.setMaterialsNeeded(activityInfo.getString("materialsNeeded"));
            activity.setStatus("0"); // 待确认
            
            // 5. 初步预算计算
            BigDecimal estimatedBudget = calculateBudget(activityInfo);
            activity.setTotalBudget(estimatedBudget);
            
            // 6. 设置多租户字段
            activity.setTenantId(getCurrentTenantId());
            activity.setDeleteFlag("0");
            activity.setCreateTime(new Date());
            activity.setCreateUser(getCurrentUserId(request));
            activity.setUpdateTime(new Date());
            activity.setUpdateUser(getCurrentUserId(request));
            
            // 7. 保存活动
            int result = teambuildingActivityMapper.insertSelective(activity);
            if (result <= 0) {
                throw new BusinessRunTimeException("活动创建失败");
            }
            
            // 8. 记录操作日志
            String logContent = "创建团建活动: " + activity.getActivityName() + 
                             ", 客户: " + activity.getClientCompany() +
                             ", 参与人数: " + activity.getParticipantCount();
            logService.insertLog("团建活动", logContent, request);
            
            logger.info("团建活动创建成功, ID: {}, 活动编号: {}", activity.getId(), activityNo);
            return String.valueOf(activity.getId());
            
        } catch (Exception e) {
            logger.error("创建团建活动失败", e);
            throw new BusinessRunTimeException("创建团建活动失败: " + e.getMessage());
        }
    }

    /**
     * 检查资源冲突
     * 检查指定时间段内场地和讲师的可用性
     */
    public boolean checkResourceConflict(JSONObject activityInfo) throws Exception {
        try {
            Date activityDate = activityInfo.getDate("activityDate");
            String startTime = activityInfo.getString("startTime");
            String endTime = activityInfo.getString("endTime");
            Long venueId = activityInfo.getLong("venueId");
            Long instructorId = activityInfo.getLong("instructorId");
            
            // 1. 检查场地冲突
            if (venueId != null) {
                boolean venueConflict = venueManagementService.checkVenueAvailability(
                    venueId, activityDate, startTime, endTime);
                if (venueConflict) {
                    logger.warn("场地资源冲突: venueId={}, date={}, time={}-{}", 
                        venueId, activityDate, startTime, endTime);
                    return true;
                }
            }
            
            // 2. 检查讲师冲突
            if (instructorId != null) {
                List<TeambuildingActivity> conflictActivities = 
                    teambuildingActivityMapperEx.findConflictActivitiesByInstructor(
                        instructorId, activityDate, startTime, endTime);
                if (!conflictActivities.isEmpty()) {
                    logger.warn("讲师资源冲突: instructorId={}, date={}, time={}-{}", 
                        instructorId, activityDate, startTime, endTime);
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("资源冲突检查失败", e);
            throw new BusinessRunTimeException("资源冲突检查失败: " + e.getMessage());
        }
    }

    /**
     * 计算活动预算
     * 基于活动类型、人数、时长等因素计算预算
     */
    public BigDecimal calculateBudget(JSONObject activityInfo) throws Exception {
        try {
            BigDecimal totalBudget = BigDecimal.ZERO;
            String activityType = activityInfo.getString("activityType");
            Integer participantCount = activityInfo.getInteger("participantCount");
            Long venueId = activityInfo.getLong("venueId");
            
            // 1. 计算场地费用
            if (venueId != null) {
                BigDecimal venueCost = venueManagementService.calculateVenueCost(
                    venueId, activityInfo.getString("startTime"), activityInfo.getString("endTime"));
                totalBudget = totalBudget.add(venueCost);
            }
            
            // 2. 计算人工费用
            BigDecimal laborCost = calculateLaborCost(activityType, participantCount);
            totalBudget = totalBudget.add(laborCost);
            
            // 3. 计算材料费用
            BigDecimal materialCost = calculateMaterialCost(activityType, participantCount);
            totalBudget = totalBudget.add(materialCost);
            
            // 4. 其他费用(交通、餐饮等)
            BigDecimal otherCost = calculateOtherCost(participantCount);
            totalBudget = totalBudget.add(otherCost);
            
            // 5. 加上利润空间(20%)
            BigDecimal profitMargin = totalBudget.multiply(new BigDecimal("0.2"));
            totalBudget = totalBudget.add(profitMargin);
            
            logger.info("活动预算计算完成: 场地费={}, 人工费={}, 材料费={}, 其他费用={}, 总预算={}", 
                venueCost, laborCost, materialCost, otherCost, totalBudget);
            
            return totalBudget.setScale(2, RoundingMode.HALF_UP);
            
        } catch (Exception e) {
            logger.error("预算计算失败", e);
            throw new BusinessRunTimeException("预算计算失败: " + e.getMessage());
        }
    }

    /**
     * 确认活动
     * 将活动状态更新为已确认，并进行最终预算确认
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean confirmActivity(Long activityId, JSONObject confirmInfo, HttpServletRequest request) throws Exception {
        try {
            // 1. 获取活动信息
            TeambuildingActivity activity = teambuildingActivityMapper.selectByPrimaryKey(activityId);
            if (activity == null) {
                throw new BusinessRunTimeException("活动不存在: " + activityId);
            }
            
            if (!"0".equals(activity.getStatus())) {
                throw new BusinessRunTimeException("只有待确认状态的活动才能确认");
            }
            
            // 2. 更新活动信息
            activity.setStatus("1"); // 已确认
            activity.setInstructorId(confirmInfo.getLong("instructorId"));
            activity.setAssistantId(confirmInfo.getLong("assistantId"));
            activity.setTotalBudget(confirmInfo.getBigDecimal("finalBudget"));
            activity.setRiskAssessment(confirmInfo.getString("riskAssessment"));
            activity.setUpdateTime(new Date());
            activity.setUpdateUser(getCurrentUserId(request));
            
            // 3. 保存更新
            int result = teambuildingActivityMapper.updateByPrimaryKeySelective(activity);
            if (result <= 0) {
                throw new BusinessRunTimeException("活动确认失败");
            }
            
            // 4. 预订场地
            if (activity.getVenueId() != null) {
                venueManagementService.reserveVenue(
                    activity.getVenueId(), 
                    activity.getActivityDate(),
                    activity.getStartTime().toString(),
                    activity.getEndTime().toString(),
                    activityId
                );
            }
            
            // 5. 创建参与人员记录
            createParticipantRecords(activityId, confirmInfo.getJSONArray("participants"), request);
            
            // 6. 创建费用明细记录
            createExpenseRecords(activityId, confirmInfo.getJSONArray("expenses"), request);
            
            // 7. 记录操作日志
            String logContent = "确认团建活动: " + activity.getActivityName() + 
                             ", 最终预算: " + activity.getTotalBudget();
            logService.insertLog("团建活动", logContent, request);
            
            logger.info("团建活动确认成功, ID: {}", activityId);
            return true;
            
        } catch (Exception e) {
            logger.error("确认团建活动失败", e);
            throw new BusinessRunTimeException("确认团建活动失败: " + e.getMessage());
        }
    }

    /**
     * 计算人工费用
     */
    private BigDecimal calculateLaborCost(String activityType, Integer participantCount) {
        // 基础人工费用计算逻辑
        BigDecimal baseCost = new BigDecimal("200"); // 基础时薪
        
        // 根据活动类型调整
        if ("非遗体验".equals(activityType)) {
            baseCost = baseCost.multiply(new BigDecimal("1.5"));
        } else if ("传统文化".equals(activityType)) {
            baseCost = baseCost.multiply(new BigDecimal("1.3"));
        }
        
        // 根据人数调整(每20人配一个助理)
        int assistantCount = (participantCount - 1) / 20 + 1;
        BigDecimal assistantCost = new BigDecimal("100").multiply(new BigDecimal(assistantCount));
        
        return baseCost.add(assistantCost);
    }

    /**
     * 计算材料费用
     */
    private BigDecimal calculateMaterialCost(String activityType, Integer participantCount) {
        BigDecimal unitCost = new BigDecimal("50"); // 每人材料费
        
        // 根据活动类型调整
        if ("非遗体验".equals(activityType)) {
            unitCost = new BigDecimal("80");
        } else if ("定制活动".equals(activityType)) {
            unitCost = new BigDecimal("100");
        }
        
        return unitCost.multiply(new BigDecimal(participantCount));
    }

    /**
     * 计算其他费用
     */
    private BigDecimal calculateOtherCost(Integer participantCount) {
        // 交通费、餐饮费等
        return new BigDecimal("30").multiply(new BigDecimal(participantCount));
    }

    /**
     * 创建参与人员记录
     */
    private void createParticipantRecords(Long activityId, JSONArray participants, HttpServletRequest request) throws Exception {
        if (participants == null || participants.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < participants.size(); i++) {
            JSONObject participant = participants.getJSONObject(i);
            
            TeambuildingParticipant record = new TeambuildingParticipant();
            record.setActivityId(activityId);
            record.setParticipantName(participant.getString("participantName"));
            record.setParticipantPhone(participant.getString("participantPhone"));
            record.setParticipantRole(participant.getString("participantRole"));
            record.setHourlyRate(participant.getBigDecimal("hourlyRate"));
            record.setCommissionRate(participant.getBigDecimal("commissionRate"));
            record.setAttendanceStatus("0"); // 待确认
            
            // 设置标准字段
            record.setTenantId(getCurrentTenantId());
            record.setDeleteFlag("0");
            record.setCreateTime(new Date());
            record.setCreateUser(getCurrentUserId(request));
            
            participantMapper.insertSelective(record);
        }
    }

    /**
     * 创建费用明细记录
     */
    private void createExpenseRecords(Long activityId, JSONArray expenses, HttpServletRequest request) throws Exception {
        if (expenses == null || expenses.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < expenses.size(); i++) {
            JSONObject expense = expenses.getJSONObject(i);
            
            TeambuildingExpense record = new TeambuildingExpense();
            record.setActivityId(activityId);
            record.setExpenseType(expense.getString("expenseType"));
            record.setExpenseName(expense.getString("expenseName"));
            record.setExpenseDescription(expense.getString("expenseDescription"));
            record.setPlannedAmount(expense.getBigDecimal("plannedAmount"));
            record.setSupplierName(expense.getString("supplierName"));
            record.setPaymentStatus("0"); // 未支付
            record.setApprovalStatus("0"); // 待审批
            
            // 设置标准字段
            record.setTenantId(getCurrentTenantId());
            record.setDeleteFlag("0");
            record.setCreateTime(new Date());
            record.setCreateUser(getCurrentUserId(request));
            
            expenseMapper.insertSelective(record);
        }
    }

    /**
     * 生成活动编号
     */
    private String generateActivityNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "TB" + dateStr;
        
        // 查询当日最大序号
        String maxNo = teambuildingActivityMapperEx.findMaxActivityNoByPrefix(prefix);
        
        int sequence = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            String seqStr = maxNo.substring(prefix.length());
            try {
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return prefix + String.format("%04d", sequence);
    }

    /**
     * 验证活动信息
     */
    private void validateActivityInfo(JSONObject activityInfo) throws Exception {
        if (activityInfo == null) {
            throw new BusinessRunTimeException("活动信息不能为空");
        }
        
        if (activityInfo.getString("activityName") == null || activityInfo.getString("activityName").trim().isEmpty()) {
            throw new BusinessRunTimeException("活动名称不能为空");
        }
        
        if (activityInfo.getString("clientCompany") == null || activityInfo.getString("clientCompany").trim().isEmpty()) {
            throw new BusinessRunTimeException("客户企业不能为空");
        }
        
        if (activityInfo.getInteger("participantCount") == null || activityInfo.getInteger("participantCount") <= 0) {
            throw new BusinessRunTimeException("参与人数必须大于0");
        }
        
        if (activityInfo.getDate("activityDate") == null) {
            throw new BusinessRunTimeException("活动日期不能为空");
        }
        
        // 检查活动日期不能是过去时间
        Date activityDate = activityInfo.getDate("activityDate");
        Date today = new Date();
        if (activityDate.before(today)) {
            throw new BusinessRunTimeException("活动日期不能早于当前日期");
        }
    }

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        // 从当前会话获取租户ID
        return 1L; // 临时返回值，实际应从用户会话获取
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // 从当前会话获取用户ID
        return 1L; // 临时返回值，实际应从用户会话获取
    }
}
```

### 2. VenueManagementService.java - 场地管理服务

**文件路径**: `com.jsh.erp.teambuilding.service.VenueManagementService`

```java
package com.jsh.erp.teambuilding.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.teambuilding.datasource.entities.VenueManagement;
import com.jsh.erp.teambuilding.datasource.mappers.VenueManagementMapper;
import com.jsh.erp.teambuilding.datasource.mappers.VenueManagementMapperEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * 场地管理服务
 * 提供场地预订、冲突检测、费用计算等功能
 */
@Service
public class VenueManagementService {
    private Logger logger = LoggerFactory.getLogger(VenueManagementService.class);

    @Resource
    private VenueManagementMapper venueManagementMapper;
    @Resource
    private VenueManagementMapperEx venueManagementMapperEx;

    /**
     * 检查场地可用性
     * 检查指定时间段内场地是否可用
     */
    public boolean checkVenueAvailability(Long venueId, Date activityDate, String startTime, String endTime) throws Exception {
        try {
            // 1. 检查场地是否存在且可用
            VenueManagement venue = venueManagementMapper.selectByPrimaryKey(venueId);
            if (venue == null || !"1".equals(venue.getStatus())) {
                throw new BusinessRunTimeException("场地不存在或不可用");
            }
            
            // 2. 检查时间段冲突
            List<Long> conflictActivities = venueManagementMapperEx.findConflictActivitiesByVenue(
                venueId, activityDate, startTime, endTime);
            
            if (!conflictActivities.isEmpty()) {
                logger.warn("场地时间冲突: venueId={}, date={}, time={}-{}, 冲突活动数={}", 
                    venueId, activityDate, startTime, endTime, conflictActivities.size());
                return false; // 有冲突
            }
            
            return true; // 可用
            
        } catch (Exception e) {
            logger.error("检查场地可用性失败", e);
            throw new BusinessRunTimeException("检查场地可用性失败: " + e.getMessage());
        }
    }

    /**
     * 预订场地
     * 创建场地预订记录
     */
    public boolean reserveVenue(Long venueId, Date activityDate, String startTime, String endTime, Long activityId) throws Exception {
        try {
            // 再次检查可用性
            boolean available = checkVenueAvailability(venueId, activityDate, startTime, endTime);
            if (!available) {
                throw new BusinessRunTimeException("场地在指定时间段不可用");
            }
            
            // 创建预订记录（这里可以扩展为独立的预订表）
            // 目前通过活动表关联实现
            
            logger.info("场地预订成功: venueId={}, activityId={}, date={}, time={}-{}", 
                venueId, activityId, activityDate, startTime, endTime);
            
            return true;
            
        } catch (Exception e) {
            logger.error("场地预订失败", e);
            throw new BusinessRunTimeException("场地预订失败: " + e.getMessage());
        }
    }

    /**
     * 计算场地费用
     * 基于时长和费率计算场地费用
     */
    public BigDecimal calculateVenueCost(Long venueId, String startTime, String endTime) throws Exception {
        try {
            // 1. 获取场地信息
            VenueManagement venue = venueManagementMapper.selectByPrimaryKey(venueId);
            if (venue == null) {
                throw new BusinessRunTimeException("场地不存在");
            }
            
            // 2. 计算使用时长
            LocalTime start = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HH:mm"));
            long hours = ChronoUnit.HOURS.between(start, end);
            if (hours <= 0) {
                hours = 1; // 最少按1小时计算
            }
            
            // 3. 计算费用
            BigDecimal hourlyRate = venue.getHourlyRate();
            BigDecimal totalCost = hourlyRate.multiply(new BigDecimal(hours));
            
            logger.info("场地费用计算: venueId={}, 时长={}小时, 时租={}元, 总费用={}元", 
                venueId, hours, hourlyRate, totalCost);
            
            return totalCost;
            
        } catch (Exception e) {
            logger.error("计算场地费用失败", e);
            throw new BusinessRunTimeException("计算场地费用失败: " + e.getMessage());
        }
    }
}
```

### 3. ParticipantManagementService.java - 参与人员管理服务

**文件路径**: `com.jsh.erp.teambuilding.service.ParticipantManagementService`

```java
package com.jsh.erp.teambuilding.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.teambuilding.datasource.entities.TeambuildingParticipant;
import com.jsh.erp.teambuilding.datasource.mappers.TeambuildingParticipantMapper;
import com.jsh.erp.teambuilding.datasource.mappers.TeambuildingParticipantMapperEx;
import com.jsh.erp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 参与人员管理服务
 * 提供讲师分配、提成计算等功能
 */
@Service
public class ParticipantManagementService {
    private Logger logger = LoggerFactory.getLogger(ParticipantManagementService.class);

    @Resource
    private TeambuildingParticipantMapper participantMapper;
    @Resource
    private TeambuildingParticipantMapperEx participantMapperEx;
    @Resource
    private UserService userService;

    /**
     * 分配讲师
     * 为活动分配主讲师
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean assignInstructor(Long activityId, Long instructorId, JSONObject assignInfo, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证讲师信息
            JSONObject instructor = userService.getUser(instructorId);
            if (instructor == null) {
                throw new BusinessRunTimeException("讲师不存在: " + instructorId);
            }
            
            // 2. 检查讲师是否已分配
            TeambuildingParticipant existing = participantMapperEx.findByActivityAndRole(activityId, "主讲师");
            if (existing != null) {
                throw new BusinessRunTimeException("该活动已分配主讲师");
            }
            
            // 3. 创建讲师参与记录
            TeambuildingParticipant participant = new TeambuildingParticipant();
            participant.setActivityId(activityId);
            participant.setParticipantName(instructor.getString("username"));
            participant.setParticipantPhone(instructor.getString("phoneNumber"));
            participant.setParticipantRole("主讲师");
            participant.setHourlyRate(assignInfo.getBigDecimal("hourlyRate"));
            participant.setCommissionRate(assignInfo.getBigDecimal("commissionRate"));
            participant.setAttendanceStatus("1"); // 已确认
            
            // 设置标准字段
            participant.setTenantId(getCurrentTenantId());
            participant.setDeleteFlag("0");
            participant.setCreateTime(new Date());
            participant.setCreateUser(getCurrentUserId(request));
            
            int result = participantMapper.insertSelective(participant);
            if (result <= 0) {
                throw new BusinessRunTimeException("讲师分配失败");
            }
            
            logger.info("讲师分配成功: activityId={}, instructorId={}", activityId, instructorId);
            return true;
            
        } catch (Exception e) {
            logger.error("分配讲师失败", e);
            throw new BusinessRunTimeException("分配讲师失败: " + e.getMessage());
        }
    }

    /**
     * 分配助理
     * 为活动分配助理人员
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean assignAssistant(Long activityId, Long assistantId, JSONObject assignInfo, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证助理信息
            JSONObject assistant = userService.getUser(assistantId);
            if (assistant == null) {
                throw new BusinessRunTimeException("助理不存在: " + assistantId);
            }
            
            // 2. 创建助理参与记录
            TeambuildingParticipant participant = new TeambuildingParticipant();
            participant.setActivityId(activityId);
            participant.setParticipantName(assistant.getString("username"));
            participant.setParticipantPhone(assistant.getString("phoneNumber"));
            participant.setParticipantRole("助理");
            participant.setHourlyRate(assignInfo.getBigDecimal("hourlyRate"));
            participant.setCommissionRate(assignInfo.getBigDecimal("commissionRate"));
            participant.setAttendanceStatus("1"); // 已确认
            
            // 设置标准字段
            participant.setTenantId(getCurrentTenantId());
            participant.setDeleteFlag("0");
            participant.setCreateTime(new Date());
            participant.setCreateUser(getCurrentUserId(request));
            
            int result = participantMapper.insertSelective(participant);
            if (result <= 0) {
                throw new BusinessRunTimeException("助理分配失败");
            }
            
            logger.info("助理分配成功: activityId={}, assistantId={}", activityId, assistantId);
            return true;
            
        } catch (Exception e) {
            logger.error("分配助理失败", e);
            throw new BusinessRunTimeException("分配助理失败: " + e.getMessage());
        }
    }

    /**
     * 计算提成
     * 根据活动收入和提成比例计算参与人员提成
     */
    public BigDecimal calculateCommission(Long participantId, BigDecimal activityRevenue) throws Exception {
        try {
            // 1. 获取参与人员信息
            TeambuildingParticipant participant = participantMapper.selectByPrimaryKey(participantId);
            if (participant == null) {
                throw new BusinessRunTimeException("参与人员不存在: " + participantId);
            }
            
            // 2. 计算基础工资
            BigDecimal baseSalary = participant.getHourlyRate().multiply(participant.getWorkHours());
            
            // 3. 计算提成金额
            BigDecimal commissionAmount = BigDecimal.ZERO;
            if (participant.getCommissionRate() != null && participant.getCommissionRate().compareTo(BigDecimal.ZERO) > 0) {
                commissionAmount = activityRevenue
                    .multiply(participant.getCommissionRate())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            }
            
            // 4. 计算总薪酬
            BigDecimal totalSalary = baseSalary.add(commissionAmount);
            
            // 5. 更新参与人员记录
            participant.setBaseSalary(baseSalary);
            participant.setCommissionAmount(commissionAmount);
            participant.setTotalSalary(totalSalary);
            participant.setUpdateTime(new Date());
            
            participantMapper.updateByPrimaryKeySelective(participant);
            
            logger.info("提成计算完成: participantId={}, 基础工资={}, 提成={}, 总薪酬={}", 
                participantId, baseSalary, commissionAmount, totalSalary);
            
            return totalSalary;
            
        } catch (Exception e) {
            logger.error("计算提成失败", e);
            throw new BusinessRunTimeException("计算提成失败: " + e.getMessage());
        }
    }

    /**
     * 批量计算活动所有参与人员薪酬
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void calculateAllParticipantSalary(Long activityId, BigDecimal activityRevenue) throws Exception {
        try {
            // 获取活动所有参与人员
            List<TeambuildingParticipant> participants = participantMapperEx.findByActivity(activityId);
            
            for (TeambuildingParticipant participant : participants) {
                calculateCommission(participant.getId(), activityRevenue);
            }
            
            logger.info("活动薪酬计算完成: activityId={}, 参与人数={}", activityId, participants.size());
            
        } catch (Exception e) {
            logger.error("批量计算薪酬失败", e);
            throw new BusinessRunTimeException("批量计算薪酬失败: " + e.getMessage());
        }
    }

    private Long getCurrentTenantId() {
        return 1L; // 临时实现
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        return 1L; // 临时实现
    }
}
```

---

## 前端开发 (3天)

### 1. TeambuildingActivityList.vue - 活动列表页面

**文件路径**: `jshERP-web/src/views/teambuilding/TeambuildingActivityList.vue`

```vue
<template>
  <a-card :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="活动名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-model="queryParam.activityName" placeholder="请输入活动名称" />
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="客户企业" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-model="queryParam.clientCompany" placeholder="请输入客户企业" />
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="活动状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="0">待确认</a-select-option>
                <a-select-option value="1">已确认</a-select-option>
                <a-select-option value="2">进行中</a-select-option>
                <a-select-option value="3">已完成</a-select-option>
                <a-select-option value="4">已取消</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="活动类型" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.activityType" placeholder="请选择活动类型" allowClear>
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="非遗体验">非遗体验</a-select-option>
                <a-select-option value="传统文化">传统文化</a-select-option>
                <a-select-option value="定制活动">定制活动</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <a-col :md="8" :sm="24">
            <a-form-item label="活动日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-range-picker 
                v-model="queryParam.dateRange"
                format="YYYY-MM-DD"
                placeholder="['开始日期', '结束日期']" />
            </a-form-item>
          </a-col>
          
          <a-col :md="8" :sm="24">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
              <a-button style="margin-left: 8px" @click="searchReset" icon="reload">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button v-has="'teambuilding:activity:add'" type="primary" icon="plus" @click="handleAdd">创建活动</a-button>
      <a-button v-has="'teambuilding:activity:import'" type="default" icon="import" @click="handleImport">批量导入</a-button>
      <a-button v-has="'teambuilding:activity:export'" type="default" icon="export" @click="handleExport">导出数据</a-button>
      
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchConfirm">
            <a-icon type="check-circle" />批量确认
          </a-menu-item>
          <a-menu-item key="2" @click="batchCancel">
            <a-icon type="close-circle" />批量取消
          </a-menu-item>
          <a-menu-item key="3" @click="batchDelete">
            <a-icon type="delete" />批量删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- 活动统计卡片 -->
    <div class="activity-stats-cards" style="margin-bottom: 16px;">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="本月活动总数"
              :value="stats.totalActivities"
              value-style="color: #3f8600"
              prefix-icon="calendar" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="待确认活动"
              :value="stats.pendingActivities"
              value-style="color: #cf1322"
              prefix-icon="clock-circle" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="本月收入"
              :value="stats.monthlyRevenue"
              :precision="2"
              suffix="元"
              value-style="color: #3f8600"
              prefix-icon="dollar" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="客户满意度"
              :value="stats.satisfaction"
              :precision="1"
              suffix="%"
              value-style="color: #3f8600"
              prefix-icon="smile" />
          </a-card>
        </a-col>
      </a-row>
    </div>

    <!-- 数据表格 -->
    <a-table
      ref="table"
      size="small"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="rowSelection"
      @change="handleTableChange"
      bordered>
      
      <!-- 活动名称 -->
      <template slot="activityName" slot-scope="text, record">
        <a @click="handleDetail(record)">{{ text }}</a>
      </template>
      
      <!-- 活动类型 -->
      <template slot="activityType" slot-scope="text">
        <a-tag :color="getActivityTypeColor(text)">{{ text }}</a-tag>
      </template>
      
      <!-- 活动状态 -->
      <template slot="status" slot-scope="text">
        <a-badge :status="getStatusBadge(text).status" :text="getStatusBadge(text).text" />
      </template>
      
      <!-- 参与人数 -->
      <template slot="participantCount" slot-scope="text">
        <a-tag color="blue">{{ text }}人</a-tag>
      </template>
      
      <!-- 预算金额 -->
      <template slot="totalBudget" slot-scope="text">
        <span style="color: #f50;">￥{{ text | currency }}</span>
      </template>
      
      <!-- 利润率 -->
      <template slot="profitMargin" slot-scope="text">
        <a-progress 
          :percent="text" 
          :stroke-color="text > 20 ? '#52c41a' : text > 10 ? '#faad14' : '#f5222d'"
          size="small" />
      </template>
      
      <!-- 客户评分 -->
      <template slot="feedbackScore" slot-scope="text">
        <a-rate :value="text" disabled allow-half />
      </template>
      
      <!-- 操作列 -->
      <template slot="action" slot-scope="text, record">
        <a-dropdown>
          <a class="ant-dropdown-link">
            操作 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item v-has="'teambuilding:activity:edit'">
              <a @click="handleEdit(record)"><a-icon type="edit" />编辑</a>
            </a-menu-item>
            <a-menu-item v-has="'teambuilding:activity:confirm'" v-if="record.status === '0'">
              <a @click="handleConfirm(record)"><a-icon type="check-circle" />确认活动</a>
            </a-menu-item>
            <a-menu-item v-has="'teambuilding:activity:participant'">
              <a @click="handleParticipant(record)"><a-icon type="team" />人员管理</a>
            </a-menu-item>
            <a-menu-item v-has="'teambuilding:activity:expense'">
              <a @click="handleExpense(record)"><a-icon type="dollar" />费用管理</a>
            </a-menu-item>
            <a-menu-item v-has="'teambuilding:activity:feedback'" v-if="record.status === '3'">
              <a @click="handleFeedback(record)"><a-icon type="message" />客户反馈</a>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item v-has="'teambuilding:activity:delete'">
              <a @click="handleDelete(record)" style="color: #ff4d4f;">
                <a-icon type="delete" />删除
              </a>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
    </a-table>

    <!-- 活动编辑弹窗 -->
    <teambuilding-activity-modal 
      ref="modalForm" 
      @ok="modalFormOk"
      :venueList="venueList"
      :instructorList="instructorList">
    </teambuilding-activity-modal>

    <!-- 资源日历组件 -->
    <resource-calendar 
      ref="resourceCalendar"
      @dateSelect="handleDateSelect">
    </resource-calendar>

    <!-- 预算计算器 -->
    <budget-calculator 
      ref="budgetCalculator"
      @budgetCalculated="handleBudgetCalculated">
    </budget-calculator>
  </a-card>
</template>

<script>
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import TeambuildingActivityModal from './modules/TeambuildingActivityModal'
import ResourceCalendar from './modules/ResourceCalendar'
import BudgetCalculator from './modules/BudgetCalculator'
import { getTeambuildingActivityList, deleteTeambuildingActivity, batchDeleteTeambuildingActivity } from '@/api/teambuilding'

export default {
  name: 'TeambuildingActivityList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    TeambuildingActivityModal,
    ResourceCalendar,
    BudgetCalculator
  },
  data() {
    return {
      description: '团建活动管理',
      // 表格列定义
      columns: [
        {
          title: '#',
          dataIndex: '',
          key: 'rowIndex',
          width: 60,
          align: 'center',
          customRender: (text, record, index) => index + 1
        },
        {
          title: '活动编号',
          align: 'center',
          dataIndex: 'activityNo',
          width: 120
        },
        {
          title: '活动名称',
          align: 'left',
          dataIndex: 'activityName',
          width: 200,
          scopedSlots: { customRender: 'activityName' }
        },
        {
          title: '活动类型',
          align: 'center',
          dataIndex: 'activityType',
          width: 100,
          scopedSlots: { customRender: 'activityType' }
        },
        {
          title: '客户企业',
          align: 'left',
          dataIndex: 'clientCompany',
          width: 160
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'contactPerson',
          width: 100
        },
        {
          title: '参与人数',
          align: 'center',
          dataIndex: 'participantCount',
          width: 100,
          scopedSlots: { customRender: 'participantCount' }
        },
        {
          title: '活动日期',
          align: 'center',
          dataIndex: 'activityDate',
          width: 110,
          customRender: (text) => text ? this.$moment(text).format('YYYY-MM-DD') : ''
        },
        {
          title: '活动时间',
          align: 'center',
          dataIndex: 'timeRange',
          width: 120,
          customRender: (text, record) => `${record.startTime}-${record.endTime}`
        },
        {
          title: '预算金额',
          align: 'right',
          dataIndex: 'totalBudget',
          width: 120,
          scopedSlots: { customRender: 'totalBudget' }
        },
        {
          title: '利润率',
          align: 'center',
          dataIndex: 'profitMargin',
          width: 100,
          scopedSlots: { customRender: 'profitMargin' }
        },
        {
          title: '活动状态',
          align: 'center',
          dataIndex: 'status',
          width: 100,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '客户评分',
          align: 'center',
          dataIndex: 'feedbackScore',
          width: 120,
          scopedSlots: { customRender: 'feedbackScore' }
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 120,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: '/teambuilding/activity/list',
        delete: '/teambuilding/activity/delete',
        deleteBatch: '/teambuilding/activity/deleteBatch',
        exportXlsUrl: '/teambuilding/activity/exportXls',
        importExcelUrl: '/teambuilding/activity/importExcel'
      },
      dictOptions: {},
      // 统计数据
      stats: {
        totalActivities: 0,
        pendingActivities: 0,
        monthlyRevenue: 0,
        satisfaction: 0
      },
      // 下拉选项数据
      venueList: [],
      instructorList: []
    }
  },
  created() {
    this.getSuperFieldList()
    this.loadVenueList()
    this.loadInstructorList()
    this.loadStats()
  },
  methods: {
    initDictConfig() {
      // 初始化字典配置
    },
    getSuperFieldList() {
      // 获取字段配置
    },
    
    // 获取活动类型颜色
    getActivityTypeColor(type) {
      const colorMap = {
        '非遗体验': 'volcano',
        '传统文化': 'geekblue',
        '定制活动': 'purple'
      }
      return colorMap[type] || 'default'
    },
    
    // 获取状态徽章
    getStatusBadge(status) {
      const badgeMap = {
        '0': { status: 'default', text: '待确认' },
        '1': { status: 'processing', text: '已确认' },
        '2': { status: 'warning', text: '进行中' },
        '3': { status: 'success', text: '已完成' },
        '4': { status: 'error', text: '已取消' }
      }
      return badgeMap[status] || { status: 'default', text: '未知' }
    },

    // 确认活动
    handleConfirm(record) {
      this.$confirm({
        title: '确认活动',
        content: `确定要确认活动 "${record.activityName}" 吗？`,
        onOk: () => {
          // 调用确认接口
          this.$http.post('/teambuilding/activity/confirm', { id: record.id })
            .then((res) => {
              if (res.success) {
                this.$message.success('活动确认成功')
                this.loadData()
              } else {
                this.$message.error(res.message || '活动确认失败')
              }
            })
        }
      })
    },

    // 人员管理
    handleParticipant(record) {
      this.$router.push({
        name: 'TeambuildingParticipant',
        params: { activityId: record.id }
      })
    },

    // 费用管理
    handleExpense(record) {
      this.$router.push({
        name: 'TeambuildingExpense',
        params: { activityId: record.id }
      })
    },

    // 客户反馈
    handleFeedback(record) {
      this.$router.push({
        name: 'TeambuildingFeedback',
        params: { activityId: record.id }
      })
    },

    // 日期选择
    handleDateSelect(date) {
      this.queryParam.activityDate = date
      this.loadData()
    },

    // 预算计算完成
    handleBudgetCalculated(budget) {
      this.$message.success(`预算计算完成: ￥${budget}`)
    },

    // 批量确认
    batchConfirm() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要确认的活动')
        return
      }
      
      this.$confirm({
        title: '批量确认',
        content: `确定要确认选中的 ${this.selectedRowKeys.length} 个活动吗？`,
        onOk: () => {
          this.$http.post('/teambuilding/activity/batchConfirm', {
            ids: this.selectedRowKeys.join(',')
          }).then((res) => {
            if (res.success) {
              this.$message.success('批量确认成功')
              this.loadData()
              this.onClearSelected()
            } else {
              this.$message.error(res.message || '批量确认失败')
            }
          })
        }
      })
    },

    // 批量取消
    batchCancel() {
      if (this.selectedRowKeys.length === 0) {
        this.$message.warning('请选择要取消的活动')
        return
      }
      
      this.$confirm({
        title: '批量取消',
        content: `确定要取消选中的 ${this.selectedRowKeys.length} 个活动吗？`,
        onOk: () => {
          this.$http.post('/teambuilding/activity/batchCancel', {
            ids: this.selectedRowKeys.join(',')
          }).then((res) => {
            if (res.success) {
              this.$message.success('批量取消成功')
              this.loadData()
              this.onClearSelected()
            } else {
              this.$message.error(res.message || '批量取消失败')
            }
          })
        }
      })
    },

    // 加载场地列表
    loadVenueList() {
      this.$http.get('/teambuilding/venue/listAll').then((res) => {
        if (res.success) {
          this.venueList = res.result
        }
      })
    },

    // 加载讲师列表
    loadInstructorList() {
      this.$http.get('/sys/user/instructorList').then((res) => {
        if (res.success) {
          this.instructorList = res.result
        }
      })
    },

    // 加载统计数据
    loadStats() {
      this.$http.get('/teambuilding/activity/stats').then((res) => {
        if (res.success) {
          this.stats = res.result
        }
      })
    }
  }
}
</script>

<style scoped>
.activity-stats-cards {
  margin-bottom: 16px;
}

.ant-statistic-content {
  font-size: 20px;
  font-weight: bold;
}

.table-operator {
  margin-bottom: 16px;
}

.table-operator .ant-btn {
  margin-right: 8px;
}
</style>
```

---

## 验收标准

### 功能验收标准
1. **活动管理功能**: 支持活动创建、编辑、确认、取消等完整流程
2. **资源冲突检测**: 能够准确检测场地和人员的时间冲突
3. **预算核算功能**: 支持多维度费用计算和利润分析
4. **人员管理**: 支持讲师和助理的分配及薪酬计算

### 质量验收标准
1. **数据完整性**: 所有业务数据完整准确
2. **多租户支持**: 正确实现租户数据隔离
3. **权限控制**: 按角色控制操作权限
4. **性能要求**: 页面响应时间不超过2秒

### 业务验收标准
1. **业务流程完整**: 涵盖团建活动管理的完整业务流程
2. **用户体验良好**: 界面友好，操作便捷
3. **数据统计准确**: 提供准确的统计分析功能
4. **集成性良好**: 与其他模块良好集成

---

## 交付物清单

### 代码交付物
1. **后端代码**: Service、Controller、Mapper等Java类
2. **前端代码**: Vue组件和页面
3. **数据库脚本**: 表结构和初始数据
4. **接口文档**: API接口说明文档

### 配置交付物
1. **权限配置**: 功能权限和数据权限配置
2. **菜单配置**: 系统菜单结构配置
3. **字典配置**: 业务字典数据配置
4. **流程配置**: 业务流程配置文件

---

## 后续优化建议

### 功能优化
1. **智能推荐**: 基于历史数据推荐场地和讲师
2. **自动化流程**: 实现活动流程的自动化处理
3. **移动端支持**: 开发移动端管理应用
4. **数据分析**: 增强数据分析和报表功能

### 技术优化
1. **缓存优化**: 实现数据缓存提升性能
2. **异步处理**: 使用消息队列处理耗时操作
3. **微服务化**: 考虑拆分为独立的微服务
4. **AI集成**: 集成AI技术提升业务效率