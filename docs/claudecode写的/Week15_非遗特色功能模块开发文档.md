# Week 15: 非遗特色功能模块开发文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第三阶段 - 优化增强
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第3、4、5章规范

---

## 概述

本文档为Week 15的非遗特色功能模块开发提供详细的实施指导。主要实现工艺知识库、艺术品档案管理、定制订单管理等具有聆花文化非遗特色的核心功能，建立完整的非遗文化传承与管理体系。

---

## 数据库设计扩展

### 1. 工艺知识库表设计

**表名**: `jsh_craft_knowledge`

```sql
-- 工艺知识库表
CREATE TABLE `jsh_craft_knowledge` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `craft_code` varchar(32) NOT NULL COMMENT '工艺编码',
  `craft_name` varchar(200) NOT NULL COMMENT '工艺名称',
  `craft_category` varchar(100) NOT NULL COMMENT '工艺分类(丝巾制作/刺绣工艺/染色技艺)',
  `craft_level` varchar(50) NOT NULL COMMENT '工艺等级(国家级/省级/市级/传统)',
  `heritage_number` varchar(100) DEFAULT NULL COMMENT '非遗项目编号',
  `craft_origin` varchar(200) DEFAULT NULL COMMENT '工艺起源地',
  `history_background` text COMMENT '历史背景',
  `craft_description` text NOT NULL COMMENT '工艺描述',
  `process_steps` text NOT NULL COMMENT '工艺流程步骤(JSON)',
  `required_materials` text COMMENT '所需材料清单(JSON)',
  `required_tools` text COMMENT '所需工具清单(JSON)',
  `skill_requirements` text COMMENT '技能要求',
  `difficulty_level` int(11) DEFAULT '1' COMMENT '难度等级(1-5)',
  `learning_time` int(11) DEFAULT '0' COMMENT '学习时长(小时)',
  `master_id` bigint(20) DEFAULT NULL COMMENT '传承大师ID',
  `master_name` varchar(100) DEFAULT NULL COMMENT '传承大师姓名',
  `video_urls` text COMMENT '教学视频URL列表(JSON)',
  `image_urls` text COMMENT '工艺图片URL列表(JSON)',
  `document_urls` text COMMENT '相关文档URL列表(JSON)',
  `certification_info` text COMMENT '认证信息(JSON)',
  `teaching_points` text COMMENT '教学要点',
  `common_problems` text COMMENT '常见问题(JSON)',
  `cultural_value` text COMMENT '文化价值描述',
  `market_value` decimal(10,2) DEFAULT '0.00' COMMENT '市场价值',
  `protection_level` varchar(50) DEFAULT NULL COMMENT '保护等级',
  `is_public` char(1) DEFAULT '1' COMMENT '是否公开(0-否,1-是)',
  `view_count` int(11) DEFAULT '0' COMMENT '查看次数',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞次数',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '状态(0-停用,1-启用)',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_craft_code` (`craft_code`, `tenant_id`),
  KEY `idx_craft_category` (`craft_category`),
  KEY `idx_craft_level` (`craft_level`),
  KEY `idx_heritage_number` (`heritage_number`),
  KEY `idx_master` (`master_id`),
  KEY `idx_difficulty` (`difficulty_level`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工艺知识库表';
```

### 2. 艺术品档案表设计

**表名**: `jsh_artwork_archive`

```sql
-- 艺术品档案表
CREATE TABLE `jsh_artwork_archive` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `archive_no` varchar(32) NOT NULL COMMENT '档案编号',
  `artwork_name` varchar(200) NOT NULL COMMENT '作品名称',
  `artwork_type` varchar(100) NOT NULL COMMENT '作品类型(丝巾/围巾/披肩/挂毯)',
  `artwork_category` varchar(100) DEFAULT NULL COMMENT '作品分类(传统/现代/融合)',
  `artwork_style` varchar(100) DEFAULT NULL COMMENT '艺术风格',
  `creation_date` date DEFAULT NULL COMMENT '创作日期',
  `creation_period` varchar(100) DEFAULT NULL COMMENT '创作年代',
  `creator_name` varchar(100) DEFAULT NULL COMMENT '创作者姓名',
  `creator_title` varchar(200) DEFAULT NULL COMMENT '创作者头衔',
  `master_info` text COMMENT '大师信息(JSON)',
  `dimensions` varchar(200) DEFAULT NULL COMMENT '尺寸规格',
  `materials_used` text COMMENT '使用材料(JSON)',
  `craft_techniques` text COMMENT '工艺技法(JSON)',
  `artwork_description` text COMMENT '作品描述',
  `cultural_significance` text COMMENT '文化意义',
  `historical_background` text COMMENT '历史背景',
  `artistic_features` text COMMENT '艺术特色',
  `color_scheme` varchar(500) DEFAULT NULL COMMENT '色彩方案',
  `pattern_description` text COMMENT '图案描述',
  `symbolic_meaning` text COMMENT '象征意义',
  `production_order_id` bigint(20) DEFAULT NULL COMMENT '关联生产工单ID',
  `estimated_value` decimal(12,2) DEFAULT '0.00' COMMENT '估值',
  `market_price` decimal(12,2) DEFAULT '0.00' COMMENT '市场价格',
  `collection_status` varchar(50) DEFAULT NULL COMMENT '收藏状态(个人收藏/博物馆收藏/私人订制)',
  `owner_info` text COMMENT '所有者信息(JSON)',
  `exhibition_history` text COMMENT '展览历史(JSON)',
  `award_history` text COMMENT '获奖历史(JSON)',
  `conservation_status` varchar(100) DEFAULT NULL COMMENT '保存状态',
  `conservation_notes` text COMMENT '保存说明',
  `high_res_images` text COMMENT '高清图片URL列表(JSON)',
  `detail_images` text COMMENT '细节图片URL列表(JSON)',
  `process_images` text COMMENT '制作过程图片URL列表(JSON)',
  `video_urls` text COMMENT '相关视频URL列表(JSON)',
  `document_urls` text COMMENT '相关文档URL列表(JSON)',
  `digital_certificate` text COMMENT '数字证书信息(JSON)',
  `qr_code_url` varchar(500) DEFAULT NULL COMMENT '二维码URL',
  `blockchain_hash` varchar(200) DEFAULT NULL COMMENT '区块链哈希值',
  `nft_token_id` varchar(200) DEFAULT NULL COMMENT 'NFT代币ID',
  `authenticity_verified` char(1) DEFAULT '0' COMMENT '真实性验证(0-未验证,1-已验证)',
  `verification_date` datetime DEFAULT NULL COMMENT '验证日期',
  `verifier_info` text COMMENT '验证人信息(JSON)',
  `public_display` char(1) DEFAULT '1' COMMENT '是否公开展示(0-否,1-是)',
  `view_count` int(11) DEFAULT '0' COMMENT '查看次数',
  `like_count` int(11) DEFAULT '0' COMMENT '点赞次数',
  `share_count` int(11) DEFAULT '0' COMMENT '分享次数',
  `status` char(1) NOT NULL DEFAULT '1' COMMENT '状态(0-停用,1-启用)',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_archive_no` (`archive_no`, `tenant_id`),
  KEY `idx_artwork_type` (`artwork_type`),
  KEY `idx_artwork_category` (`artwork_category`),
  KEY `idx_creator` (`creator_name`),
  KEY `idx_creation_date` (`creation_date`),
  KEY `idx_production_order` (`production_order_id`),
  KEY `idx_blockchain` (`blockchain_hash`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='艺术品档案表';
```

### 3. 定制订单扩展表设计

**表名**: `jsh_custom_order`

```sql
-- 定制订单表
CREATE TABLE `jsh_custom_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(32) NOT NULL COMMENT '定制订单号',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `customer_name` varchar(100) NOT NULL COMMENT '客户姓名',
  `customer_contact` varchar(20) NOT NULL COMMENT '客户联系方式',
  `order_type` varchar(50) NOT NULL COMMENT '订单类型(个人定制/企业定制/文创定制)',
  `custom_category` varchar(100) NOT NULL COMMENT '定制类别(丝巾/围巾/披肩/挂毯/其他)',
  `design_requirements` text NOT NULL COMMENT '设计需求描述',
  `size_specifications` varchar(500) DEFAULT NULL COMMENT '尺寸规格要求',
  `color_preferences` text COMMENT '色彩偏好(JSON)',
  `pattern_requirements` text COMMENT '图案要求',
  `material_preferences` text COMMENT '材质偏好(JSON)',
  `craft_requirements` text COMMENT '工艺要求',
  `cultural_elements` text COMMENT '文化元素要求',
  `inspiration_sources` text COMMENT '灵感来源',
  `reference_images` text COMMENT '参考图片URL列表(JSON)',
  `budget_range_min` decimal(10,2) DEFAULT '0.00' COMMENT '预算范围最小值',
  `budget_range_max` decimal(10,2) DEFAULT '0.00' COMMENT '预算范围最大值',
  `expected_delivery` date DEFAULT NULL COMMENT '期望交期',
  `urgency_level` varchar(50) DEFAULT '普通' COMMENT '紧急程度(普通/紧急/加急)',
  `design_phase` varchar(50) DEFAULT '需求确认' COMMENT '设计阶段(需求确认/初稿设计/修改完善/终稿确认)',
  `designer_id` bigint(20) DEFAULT NULL COMMENT '设计师ID',
  `designer_name` varchar(100) DEFAULT NULL COMMENT '设计师姓名',
  `design_start_date` date DEFAULT NULL COMMENT '设计开始日期',
  `design_completion_date` date DEFAULT NULL COMMENT '设计完成日期',
  `design_drafts` text COMMENT '设计稿信息(JSON)',
  `modification_history` text COMMENT '修改历史(JSON)',
  `customer_feedback` text COMMENT '客户反馈(JSON)',
  `final_design_url` varchar(500) DEFAULT NULL COMMENT '最终设计稿URL',
  `design_copyright` text COMMENT '设计版权信息(JSON)',
  `production_requirements` text COMMENT '生产要求',
  `quality_standards` text COMMENT '质量标准',
  `packaging_requirements` text COMMENT '包装要求',
  `delivery_requirements` text COMMENT '交付要求',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `design_fee` decimal(8,2) DEFAULT '0.00' COMMENT '设计费',
  `production_cost` decimal(10,2) DEFAULT '0.00' COMMENT '生产成本',
  `profit_margin` decimal(5,2) DEFAULT '0.00' COMMENT '利润率%',
  `payment_terms` text COMMENT '付款条件',
  `payment_status` varchar(50) DEFAULT '未付款' COMMENT '付款状态',
  `contract_signed` char(1) DEFAULT '0' COMMENT '是否签署合同(0-否,1-是)',
  `contract_url` varchar(500) DEFAULT NULL COMMENT '合同文件URL',
  `intellectual_property` text COMMENT '知识产权约定',
  `order_status` varchar(50) NOT NULL DEFAULT '待确认' COMMENT '订单状态(待确认/设计中/生产中/已完成/已取消)',
  `completion_date` date DEFAULT NULL COMMENT '完成日期',
  `customer_satisfaction` decimal(3,1) DEFAULT NULL COMMENT '客户满意度评分',
  `satisfaction_feedback` text COMMENT '满意度反馈',
  `follow_up_notes` text COMMENT '跟进记录',
  `special_notes` text COMMENT '特殊说明',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  
  -- 标准字段
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`, `tenant_id`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_order_type` (`order_type`),
  KEY `idx_custom_category` (`custom_category`),
  KEY `idx_design_phase` (`design_phase`),
  KEY `idx_designer` (`designer_id`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_expected_delivery` (`expected_delivery`),
  KEY `idx_tenant` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定制订单表';
```

---

## 工艺知识库开发 (2天)

### 1. CraftKnowledgeService.java - 工艺知识库服务

**文件路径**: `com.jsh.erp.craft.service.CraftKnowledgeService`

```java
package com.jsh.erp.craft.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.craft.datasource.entities.CraftKnowledge;
import com.jsh.erp.craft.datasource.mappers.CraftKnowledgeMapper;
import com.jsh.erp.craft.datasource.mappers.CraftKnowledgeMapperEx;
import com.jsh.erp.service.log.LogService;
import com.jsh.erp.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工艺知识库服务
 * 提供非遗工艺知识的管理和传承功能
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class CraftKnowledgeService {
    private Logger logger = LoggerFactory.getLogger(CraftKnowledgeService.class);

    @Resource
    private CraftKnowledgeMapper craftKnowledgeMapper;
    @Resource
    private CraftKnowledgeMapperEx craftKnowledgeMapperEx;
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;

    /**
     * 管理工艺流程
     * 创建和维护工艺制作流程
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String manageCraftProcess(JSONObject craftData, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证工艺数据
            validateCraftData(craftData);
            
            // 2. 生成工艺编码
            String craftCode = generateCraftCode(craftData.getString("craftCategory"));
            
            // 3. 创建工艺知识记录
            CraftKnowledge craft = new CraftKnowledge();
            craft.setCraftCode(craftCode);
            craft.setCraftName(craftData.getString("craftName"));
            craft.setCraftCategory(craftData.getString("craftCategory"));
            craft.setCraftLevel(craftData.getString("craftLevel"));
            craft.setHeritageNumber(craftData.getString("heritageNumber"));
            craft.setCraftOrigin(craftData.getString("craftOrigin"));
            craft.setHistoryBackground(craftData.getString("historyBackground"));
            craft.setCraftDescription(craftData.getString("craftDescription"));
            
            // 4. 处理工艺流程步骤
            JSONArray processSteps = craftData.getJSONArray("processSteps");
            if (processSteps != null) {
                craft.setProcessSteps(processSteps.toJSONString());
            }
            
            // 5. 处理材料和工具清单
            JSONArray materials = craftData.getJSONArray("requiredMaterials");
            JSONArray tools = craftData.getJSONArray("requiredTools");
            if (materials != null) {
                craft.setRequiredMaterials(materials.toJSONString());
            }
            if (tools != null) {
                craft.setRequiredTools(tools.toJSONString());
            }
            
            // 6. 设置技能要求和难度
            craft.setSkillRequirements(craftData.getString("skillRequirements"));
            craft.setDifficultyLevel(craftData.getInteger("difficultyLevel"));
            craft.setLearningTime(craftData.getInteger("learningTime"));
            
            // 7. 设置传承大师信息
            Long masterId = craftData.getLong("masterId");
            if (masterId != null) {
                JSONObject masterInfo = userService.getUser(masterId);
                if (masterInfo != null) {
                    craft.setMasterId(masterId);
                    craft.setMasterName(masterInfo.getString("username"));
                }
            }
            
            // 8. 处理多媒体资源
            JSONArray videoUrls = craftData.getJSONArray("videoUrls");
            JSONArray imageUrls = craftData.getJSONArray("imageUrls");
            JSONArray documentUrls = craftData.getJSONArray("documentUrls");
            
            if (videoUrls != null) {
                craft.setVideoUrls(videoUrls.toJSONString());
            }
            if (imageUrls != null) {
                craft.setImageUrls(imageUrls.toJSONString());
            }
            if (documentUrls != null) {
                craft.setDocumentUrls(documentUrls.toJSONString());
            }
            
            // 9. 设置教学信息
            craft.setTeachingPoints(craftData.getString("teachingPoints"));
            
            JSONArray commonProblems = craftData.getJSONArray("commonProblems");
            if (commonProblems != null) {
                craft.setCommonProblems(commonProblems.toJSONString());
            }
            
            // 10. 设置文化价值信息
            craft.setCulturalValue(craftData.getString("culturalValue"));
            craft.setMarketValue(craftData.getBigDecimal("marketValue"));
            craft.setProtectionLevel(craftData.getString("protectionLevel"));
            
            // 11. 设置可见性
            craft.setIsPublic(craftData.getString("isPublic"));
            craft.setStatus("1"); // 启用状态
            
            // 12. 设置标准字段
            craft.setTenantId(getCurrentTenantId());
            craft.setDeleteFlag("0");
            craft.setCreateTime(new Date());
            craft.setCreateUser(getCurrentUserId(request));
            craft.setUpdateTime(new Date());
            craft.setUpdateUser(getCurrentUserId(request));
            
            // 13. 保存工艺知识
            int result = craftKnowledgeMapper.insertSelective(craft);
            if (result <= 0) {
                throw new BusinessRunTimeException("工艺流程创建失败");
            }
            
            // 14. 记录操作日志
            String logContent = "创建工艺知识: " + craft.getCraftName() + 
                             ", 分类: " + craft.getCraftCategory() +
                             ", 等级: " + craft.getCraftLevel();
            logService.insertLog("工艺知识", logContent, request);
            
            logger.info("工艺流程管理完成: ID={}, 工艺名称={}", craft.getId(), craft.getCraftName());
            return String.valueOf(craft.getId());
            
        } catch (Exception e) {
            logger.error("管理工艺流程失败", e);
            throw new BusinessRunTimeException("管理工艺流程失败: " + e.getMessage());
        }
    }

    /**
     * 记录工艺历史
     * 记录工艺的传承历史和发展变迁
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String recordCraftHistory(Long craftId, JSONObject historyData, HttpServletRequest request) throws Exception {
        try {
            // 1. 获取工艺信息
            CraftKnowledge craft = craftKnowledgeMapper.selectByPrimaryKey(craftId);
            if (craft == null) {
                throw new BusinessRunTimeException("工艺不存在: " + craftId);
            }
            
            // 2. 更新历史背景信息
            String existingHistory = craft.getHistoryBackground();
            JSONObject historyInfo = new JSONObject();
            
            if (existingHistory != null && !existingHistory.isEmpty()) {
                try {
                    historyInfo = JSONObject.parseObject(existingHistory);
                } catch (Exception e) {
                    // 如果原来是文本格式，转换为JSON格式
                    historyInfo.put("originalText", existingHistory);
                }
            }
            
            // 3. 添加新的历史记录
            JSONArray historyRecords = historyInfo.getJSONArray("records");
            if (historyRecords == null) {
                historyRecords = new JSONArray();
            }
            
            JSONObject newRecord = new JSONObject();
            newRecord.put("recordDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            newRecord.put("recordType", historyData.getString("recordType"));
            newRecord.put("period", historyData.getString("period"));
            newRecord.put("description", historyData.getString("description"));
            newRecord.put("significance", historyData.getString("significance"));
            newRecord.put("relatedEvents", historyData.getJSONArray("relatedEvents"));
            newRecord.put("evidenceUrls", historyData.getJSONArray("evidenceUrls"));
            newRecord.put("recordBy", getCurrentUserId(request));
            
            historyRecords.add(newRecord);
            historyInfo.put("records", historyRecords);
            
            // 4. 更新工艺记录
            craft.setHistoryBackground(historyInfo.toJSONString());
            craft.setUpdateTime(new Date());
            craft.setUpdateUser(getCurrentUserId(request));
            
            int result = craftKnowledgeMapper.updateByPrimaryKeySelective(craft);
            if (result <= 0) {
                throw new BusinessRunTimeException("工艺历史记录失败");
            }
            
            // 5. 记录操作日志
            String logContent = "记录工艺历史: " + craft.getCraftName() + 
                             ", 记录类型: " + historyData.getString("recordType");
            logService.insertLog("工艺知识", logContent, request);
            
            logger.info("工艺历史记录完成: craftId={}, 记录类型={}", craftId, historyData.getString("recordType"));
            return String.valueOf(craft.getId());
            
        } catch (Exception e) {
            logger.error("记录工艺历史失败", e);
            throw new BusinessRunTimeException("记录工艺历史失败: " + e.getMessage());
        }
    }

    /**
     * 管理大师档案
     * 管理非遗传承大师的个人档案信息
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String manageMasterProfile(JSONObject masterData, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证大师数据
            validateMasterData(masterData);
            
            Long masterId = masterData.getLong("masterId");
            
            // 2. 获取或创建用户记录
            JSONObject userInfo = userService.getUser(masterId);
            if (userInfo == null) {
                throw new BusinessRunTimeException("大师用户不存在: " + masterId);
            }
            
            // 3. 更新大师档案信息
            JSONObject masterProfile = new JSONObject();
            masterProfile.put("masterId", masterId);
            masterProfile.put("masterName", masterData.getString("masterName"));
            masterProfile.put("title", masterData.getString("title"));
            masterProfile.put("heritageLevel", masterData.getString("heritageLevel"));
            masterProfile.put("certificationNumber", masterData.getString("certificationNumber"));
            masterProfile.put("birthDate", masterData.getString("birthDate"));
            masterProfile.put("birthPlace", masterData.getString("birthPlace"));
            masterProfile.put("specialties", masterData.getJSONArray("specialties"));
            masterProfile.put("achievements", masterData.getJSONArray("achievements"));
            masterProfile.put("awards", masterData.getJSONArray("awards"));
            masterProfile.put("biography", masterData.getString("biography"));
            masterProfile.put("learningExperience", masterData.getString("learningExperience"));
            masterProfile.put("teachingExperience", masterData.getString("teachingExperience"));
            masterProfile.put("representativeWorks", masterData.getJSONArray("representativeWorks"));
            masterProfile.put("disciples", masterData.getJSONArray("disciples"));
            masterProfile.put("mediaReports", masterData.getJSONArray("mediaReports"));
            masterProfile.put("contactInfo", masterData.getJSONObject("contactInfo"));
            masterProfile.put("socialMedia", masterData.getJSONObject("socialMedia"));
            masterProfile.put("profileImages", masterData.getJSONArray("profileImages"));
            masterProfile.put("workImages", masterData.getJSONArray("workImages"));
            masterProfile.put("videoIntroduction", masterData.getString("videoIntroduction"));
            
            // 4. 更新相关工艺的大师信息
            List<CraftKnowledge> relatedCrafts = craftKnowledgeMapperEx.findByMasterId(masterId);
            for (CraftKnowledge craft : relatedCrafts) {
                craft.setMasterName(masterData.getString("masterName"));
                craft.setUpdateTime(new Date());
                craft.setUpdateUser(getCurrentUserId(request));
                craftKnowledgeMapper.updateByPrimaryKeySelective(craft);
            }
            
            // 5. 记录操作日志
            String logContent = "管理大师档案: " + masterData.getString("masterName") + 
                             ", 传承等级: " + masterData.getString("heritageLevel");
            logService.insertLog("大师档案", logContent, request);
            
            logger.info("大师档案管理完成: masterId={}, masterName={}", masterId, masterData.getString("masterName"));
            return String.valueOf(masterId);
            
        } catch (Exception e) {
            logger.error("管理大师档案失败", e);
            throw new BusinessRunTimeException("管理大师档案失败: " + e.getMessage());
        }
    }

    /**
     * 搜索工艺知识
     * 支持多维度搜索和筛选
     */
    public List<CraftKnowledge> searchCraftKnowledge(JSONObject searchParams) throws Exception {
        try {
            String keyword = searchParams.getString("keyword");
            String craftCategory = searchParams.getString("craftCategory");
            String craftLevel = searchParams.getString("craftLevel");
            Integer difficultyLevel = searchParams.getInteger("difficultyLevel");
            Long masterId = searchParams.getLong("masterId");
            
            List<CraftKnowledge> results = craftKnowledgeMapperEx.searchCrafts(
                keyword, craftCategory, craftLevel, difficultyLevel, masterId);
            
            logger.debug("工艺知识搜索完成: 关键词={}, 结果数量={}", keyword, results.size());
            return results;
            
        } catch (Exception e) {
            logger.error("搜索工艺知识失败", e);
            throw new BusinessRunTimeException("搜索工艺知识失败: " + e.getMessage());
        }
    }

    /**
     * 增加查看次数
     */
    public void incrementViewCount(Long craftId) throws Exception {
        try {
            craftKnowledgeMapperEx.incrementViewCount(craftId);
            logger.debug("工艺查看次数增加: craftId={}", craftId);
        } catch (Exception e) {
            logger.warn("增加查看次数失败: craftId={}", craftId);
        }
    }

    /**
     * 点赞工艺
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public boolean likeCraft(Long craftId, HttpServletRequest request) throws Exception {
        try {
            // 检查是否已点赞
            Long userId = getCurrentUserId(request);
            boolean hasLiked = craftKnowledgeMapperEx.hasUserLiked(craftId, userId);
            
            if (hasLiked) {
                // 取消点赞
                craftKnowledgeMapperEx.removeLike(craftId, userId);
                craftKnowledgeMapperEx.decrementLikeCount(craftId);
                return false;
            } else {
                // 添加点赞
                craftKnowledgeMapperEx.addLike(craftId, userId);
                craftKnowledgeMapperEx.incrementLikeCount(craftId);
                return true;
            }
            
        } catch (Exception e) {
            logger.error("工艺点赞操作失败", e);
            throw new BusinessRunTimeException("工艺点赞操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取工艺统计信息
     */
    public JSONObject getCraftStatistics() throws Exception {
        try {
            JSONObject statistics = new JSONObject();
            
            // 总工艺数量
            int totalCrafts = craftKnowledgeMapperEx.countTotalCrafts();
            statistics.put("totalCrafts", totalCrafts);
            
            // 各分类工艺数量
            List<Map<String, Object>> categoryStats = craftKnowledgeMapperEx.countByCategory();
            statistics.put("categoryStats", categoryStats);
            
            // 各等级工艺数量
            List<Map<String, Object>> levelStats = craftKnowledgeMapperEx.countByLevel();
            statistics.put("levelStats", levelStats);
            
            // 最受欢迎的工艺
            List<Map<String, Object>> popularCrafts = craftKnowledgeMapperEx.findMostPopular(10);
            statistics.put("popularCrafts", popularCrafts);
            
            return statistics;
            
        } catch (Exception e) {
            logger.error("获取工艺统计信息失败", e);
            throw new BusinessRunTimeException("获取工艺统计信息失败: " + e.getMessage());
        }
    }

    /**
     * 验证工艺数据
     */
    private void validateCraftData(JSONObject craftData) throws Exception {
        if (craftData == null) {
            throw new BusinessRunTimeException("工艺数据不能为空");
        }
        
        if (craftData.getString("craftName") == null || craftData.getString("craftName").trim().isEmpty()) {
            throw new BusinessRunTimeException("工艺名称不能为空");
        }
        
        if (craftData.getString("craftCategory") == null || craftData.getString("craftCategory").trim().isEmpty()) {
            throw new BusinessRunTimeException("工艺分类不能为空");
        }
        
        if (craftData.getString("craftDescription") == null || craftData.getString("craftDescription").trim().isEmpty()) {
            throw new BusinessRunTimeException("工艺描述不能为空");
        }
        
        JSONArray processSteps = craftData.getJSONArray("processSteps");
        if (processSteps == null || processSteps.isEmpty()) {
            throw new BusinessRunTimeException("工艺流程步骤不能为空");
        }
    }

    /**
     * 验证大师数据
     */
    private void validateMasterData(JSONObject masterData) throws Exception {
        if (masterData == null) {
            throw new BusinessRunTimeException("大师数据不能为空");
        }
        
        if (masterData.getLong("masterId") == null) {
            throw new BusinessRunTimeException("大师ID不能为空");
        }
        
        if (masterData.getString("masterName") == null || masterData.getString("masterName").trim().isEmpty()) {
            throw new BusinessRunTimeException("大师姓名不能为空");
        }
    }

    /**
     * 生成工艺编码
     */
    private String generateCraftCode(String craftCategory) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        
        // 根据工艺分类生成前缀
        String prefix = "CF";
        switch (craftCategory) {
            case "丝巾制作":
                prefix = "CF_SJ";
                break;
            case "刺绣工艺":
                prefix = "CF_CX";
                break;
            case "染色技艺":
                prefix = "CF_RS";
                break;
            default:
                prefix = "CF_QT";
                break;
        }
        
        prefix = prefix + dateStr;
        
        // 查询当日最大序号
        String maxCode = craftKnowledgeMapperEx.findMaxCraftCodeByPrefix(prefix);
        
        int sequence = 1;
        if (maxCode != null && maxCode.length() > prefix.length()) {
            String seqStr = maxCode.substring(prefix.length());
            try {
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return prefix + String.format("%04d", sequence);
    }

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        return 1L; // 临时实现
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        return 1L; // 临时实现
    }
}
```

### 2. CraftKnowledgeList.vue - 工艺知识列表页面

**文件路径**: `jshERP-web/src/views/craft/CraftKnowledgeList.vue`

```vue
<template>
  <a-card :bordered="false">
    <!-- 搜索区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="工艺名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input v-model="queryParam.keyword" placeholder="请输入工艺名称" />
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="工艺分类" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.craftCategory" placeholder="请选择工艺分类" allowClear>
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="丝巾制作">丝巾制作</a-select-option>
                <a-select-option value="刺绣工艺">刺绣工艺</a-select-option>
                <a-select-option value="染色技艺">染色技艺</a-select-option>
                <a-select-option value="其他">其他</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="工艺等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.craftLevel" placeholder="请选择工艺等级" allowClear>
                <a-select-option value="">全部</a-select-option>
                <a-select-option value="国家级">国家级</a-select-option>
                <a-select-option value="省级">省级</a-select-option>
                <a-select-option value="市级">市级</a-select-option>
                <a-select-option value="传统">传统</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
            <a-form-item label="难度等级" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-select v-model="queryParam.difficultyLevel" placeholder="请选择难度等级" allowClear>
                <a-select-option value="">全部</a-select-option>
                <a-select-option :value="1">初级</a-select-option>
                <a-select-option :value="2">中级</a-select-option>
                <a-select-option :value="3">高级</a-select-option>
                <a-select-option :value="4">专家级</a-select-option>
                <a-select-option :value="5">大师级</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          
          <a-col :md="6" :sm="24">
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
      <a-button v-has="'craft:knowledge:add'" type="primary" icon="plus" @click="handleAdd">录入工艺</a-button>
      <a-button v-has="'craft:knowledge:import'" type="default" icon="import" @click="handleImport">批量导入</a-button>
      <a-button v-has="'craft:knowledge:export'" type="default" icon="export" @click="handleExport">导出数据</a-button>
      <a-button type="default" icon="bar-chart" @click="showStatistics">统计分析</a-button>
    </div>

    <!-- 工艺统计卡片 -->
    <div class="craft-stats-cards" style="margin-bottom: 16px;">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="工艺总数"
              :value="stats.totalCrafts"
              value-style="color: #3f8600"
              prefix-icon="gold" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="国家级工艺"
              :value="getStatsByLevel('国家级')"
              value-style="color: #cf1322"
              prefix-icon="crown" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="传承大师"
              :value="stats.masterCount"
              value-style="color: #1890ff"
              prefix-icon="user" />
          </a-card>
        </a-col>
        <a-col :span="6">
          <a-card size="small">
            <a-statistic
              title="本月新增"
              :value="stats.monthlyNew"
              value-style="color: #52c41a"
              prefix-icon="plus-circle" />
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
      
      <!-- 工艺信息 -->
      <template slot="craftInfo" slot-scope="text, record">
        <div class="craft-info">
          <div class="craft-name">
            <a @click="handleDetail(record)">{{ record.craftName }}</a>
          </div>
          <div class="craft-meta">
            <a-tag color="blue">{{ record.craftCategory }}</a-tag>
            <a-tag :color="getLevelColor(record.craftLevel)">{{ record.craftLevel }}</a-tag>
          </div>
        </div>
      </template>
      
      <!-- 难度等级 -->
      <template slot="difficultyLevel" slot-scope="text">
        <a-rate :value="text" disabled />
        <span style="margin-left: 8px;">{{ getDifficultyText(text) }}</span>
      </template>
      
      <!-- 传承大师 -->
      <template slot="masterInfo" slot-scope="text, record">
        <div v-if="record.masterName">
          <a-avatar size="small" icon="user" />
          <span style="margin-left: 8px;">{{ record.masterName }}</span>
        </div>
        <span v-else style="color: #999;">未指定</span>
      </template>
      
      <!-- 文化价值 -->
      <template slot="culturalValue" slot-scope="text">
        <a-tooltip :title="text">
          <span class="cultural-value-text">{{ text | truncate(50) }}</span>
        </a-tooltip>
      </template>
      
      <!-- 统计信息 -->
      <template slot="statistics" slot-scope="text, record">
        <div class="statistics-info">
          <a-icon type="eye" /> {{ record.viewCount }}
          <a-divider type="vertical" />
          <a-icon type="heart" /> {{ record.likeCount }}
        </div>
      </template>
      
      <!-- 状态 -->
      <template slot="status" slot-scope="text">
        <a-badge :status="text === '1' ? 'success' : 'default'" :text="text === '1' ? '启用' : '停用'" />
      </template>
      
      <!-- 操作列 -->
      <template slot="action" slot-scope="text, record">
        <a-dropdown>
          <a class="ant-dropdown-link">
            操作 <a-icon type="down" />
          </a>
          <a-menu slot="overlay">
            <a-menu-item v-has="'craft:knowledge:view'">
              <a @click="handleDetail(record)"><a-icon type="eye" />查看详情</a>
            </a-menu-item>
            <a-menu-item v-has="'craft:knowledge:edit'">
              <a @click="handleEdit(record)"><a-icon type="edit" />编辑</a>
            </a-menu-item>
            <a-menu-item v-has="'craft:knowledge:history'">
              <a @click="handleHistory(record)"><a-icon type="history" />工艺历史</a>
            </a-menu-item>
            <a-menu-item>
              <a @click="handleLike(record)">
                <a-icon :type="record.isLiked ? 'heart' : 'heart'" :style="{ color: record.isLiked ? '#f50' : '' }" />
                {{ record.isLiked ? '取消点赞' : '点赞' }}
              </a>
            </a-menu-item>
            <a-menu-item v-has="'craft:knowledge:share'">
              <a @click="handleShare(record)"><a-icon type="share-alt" />分享</a>
            </a-menu-item>
            <a-menu-divider />
            <a-menu-item v-has="'craft:knowledge:delete'">
              <a @click="handleDelete(record)" style="color: #ff4d4f;">
                <a-icon type="delete" />删除
              </a>
            </a-menu-item>
          </a-menu>
        </a-dropdown>
      </template>
    </a-table>

    <!-- 工艺知识弹窗 -->
    <craft-knowledge-modal 
      ref="modalForm" 
      @ok="modalFormOk"
      :masterList="masterList">
    </craft-knowledge-modal>

    <!-- 工艺流程编辑弹窗 -->
    <craft-process-modal 
      ref="processModal"
      @ok="processModalOk">
    </craft-process-modal>

    <!-- 工艺历史弹窗 -->
    <craft-history-modal 
      ref="historyModal"
      @ok="historyModalOk">
    </craft-history-modal>

    <!-- 统计分析弹窗 -->
    <craft-statistics-modal 
      ref="statisticsModal">
    </craft-statistics-modal>
  </a-card>
</template>

<script>
import { mixinDevice } from '@/utils/mixin'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import CraftKnowledgeModal from './modules/CraftKnowledgeModal'
import CraftProcessModal from './modules/CraftProcessModal'
import CraftHistoryModal from './modules/CraftHistoryModal'
import CraftStatisticsModal from './modules/CraftStatisticsModal'

export default {
  name: 'CraftKnowledgeList',
  mixins: [JeecgListMixin, mixinDevice],
  components: {
    CraftKnowledgeModal,
    CraftProcessModal,
    CraftHistoryModal,
    CraftStatisticsModal
  },
  data() {
    return {
      description: '工艺知识库管理',
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
          title: '工艺编码',
          align: 'center',
          dataIndex: 'craftCode',
          width: 120
        },
        {
          title: '工艺信息',
          align: 'left',
          dataIndex: 'craftInfo',
          width: 250,
          scopedSlots: { customRender: 'craftInfo' }
        },
        {
          title: '难度等级',
          align: 'center',
          dataIndex: 'difficultyLevel',
          width: 150,
          scopedSlots: { customRender: 'difficultyLevel' }
        },
        {
          title: '学习时长',
          align: 'center',
          dataIndex: 'learningTime',
          width: 100,
          customRender: (text) => text ? `${text}小时` : '-'
        },
        {
          title: '传承大师',
          align: 'center',
          dataIndex: 'masterInfo',
          width: 150,
          scopedSlots: { customRender: 'masterInfo' }
        },
        {
          title: '文化价值',
          align: 'left',
          dataIndex: 'culturalValue',
          width: 200,
          scopedSlots: { customRender: 'culturalValue' }
        },
        {
          title: '统计信息',
          align: 'center',
          dataIndex: 'statistics',
          width: 120,
          scopedSlots: { customRender: 'statistics' }
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'status',
          width: 80,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '创建时间',
          align: 'center',
          dataIndex: 'createTime',
          width: 110,
          customRender: (text) => text ? this.$moment(text).format('YYYY-MM-DD') : ''
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
        list: '/craft/knowledge/list',
        delete: '/craft/knowledge/delete',
        deleteBatch: '/craft/knowledge/deleteBatch',
        exportXlsUrl: '/craft/knowledge/exportXls',
        importExcelUrl: '/craft/knowledge/importExcel'
      },
      dictOptions: {},
      // 统计数据
      stats: {
        totalCrafts: 0,
        masterCount: 0,
        monthlyNew: 0,
        levelStats: [],
        categoryStats: []
      },
      // 下拉选项数据
      masterList: []
    }
  },
  filters: {
    truncate(text, length) {
      if (!text) return ''
      if (text.length <= length) return text
      return text.substring(0, length) + '...'
    }
  },
  created() {
    this.getSuperFieldList()
    this.loadMasterList()
    this.loadStats()
  },
  methods: {
    initDictConfig() {
      // 初始化字典配置
    },
    getSuperFieldList() {
      // 获取字段配置
    },
    
    // 获取等级颜色
    getLevelColor(level) {
      const colorMap = {
        '国家级': 'red',
        '省级': 'orange',
        '市级': 'blue',
        '传统': 'green'
      }
      return colorMap[level] || 'default'
    },
    
    // 获取难度文本
    getDifficultyText(level) {
      const textMap = {
        1: '初级',
        2: '中级', 
        3: '高级',
        4: '专家级',
        5: '大师级'
      }
      return textMap[level] || '未知'
    },

    // 根据等级获取统计数据
    getStatsByLevel(level) {
      if (!this.stats.levelStats) return 0
      const stat = this.stats.levelStats.find(s => s.level === level)
      return stat ? stat.count : 0
    },

    // 处理工艺历史
    handleHistory(record) {
      this.$refs.historyModal.show(record.id)
    },

    // 处理点赞
    handleLike(record) {
      this.$http.post('/craft/knowledge/like', { id: record.id })
        .then((res) => {
          if (res.success) {
            record.isLiked = res.result
            if (res.result) {
              record.likeCount = (record.likeCount || 0) + 1
              this.$message.success('点赞成功')
            } else {
              record.likeCount = Math.max((record.likeCount || 0) - 1, 0)
              this.$message.success('取消点赞')
            }
          } else {
            this.$message.error(res.message || '操作失败')
          }
        })
    },

    // 处理分享
    handleShare(record) {
      const shareUrl = `${window.location.origin}/craft/knowledge/detail/${record.id}`
      
      // 复制到剪贴板
      if (navigator.clipboard) {
        navigator.clipboard.writeText(shareUrl).then(() => {
          this.$message.success('分享链接已复制到剪贴板')
        })
      } else {
        // 降级方案
        const textArea = document.createElement('textarea')
        textArea.value = shareUrl
        document.body.appendChild(textArea)
        textArea.select()
        document.execCommand('copy')
        document.body.removeChild(textArea)
        this.$message.success('分享链接已复制到剪贴板')
      }
    },

    // 显示统计分析
    showStatistics() {
      this.$refs.statisticsModal.show()
    },

    // 工艺流程模态框确认
    processModalOk() {
      this.loadData()
    },

    // 工艺历史模态框确认
    historyModalOk() {
      this.loadData()
    },

    // 加载大师列表
    loadMasterList() {
      this.$http.get('/sys/user/masterList').then((res) => {
        if (res.success) {
          this.masterList = res.result
        }
      })
    },

    // 加载统计数据
    loadStats() {
      this.$http.get('/craft/knowledge/statistics').then((res) => {
        if (res.success) {
          this.stats = res.result
        }
      })
    }
  }
}
</script>

<style scoped>
.craft-stats-cards {
  margin-bottom: 16px;
}

.craft-info .craft-name {
  font-weight: bold;
  margin-bottom: 4px;
}

.craft-info .craft-meta {
  display: flex;
  gap: 4px;
}

.cultural-value-text {
  line-height: 1.4;
  word-break: break-all;
}

.statistics-info {
  color: #666;
  font-size: 12px;
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

## 艺术品档案管理开发 (2天)

### 1. ArtworkArchiveService.java - 艺术品档案服务

**文件路径**: `com.jsh.erp.artwork.service.ArtworkArchiveService`

```java
package com.jsh.erp.artwork.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.artwork.datasource.entities.ArtworkArchive;
import com.jsh.erp.artwork.datasource.mappers.ArtworkArchiveMapper;
import com.jsh.erp.artwork.datasource.mappers.ArtworkArchiveMapperEx;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 艺术品档案服务
 * 提供艺术品档案管理和数字认证功能
 */
@Service
public class ArtworkArchiveService {
    private Logger logger = LoggerFactory.getLogger(ArtworkArchiveService.class);

    @Resource
    private ArtworkArchiveMapper artworkArchiveMapper;
    @Resource
    private ArtworkArchiveMapperEx artworkArchiveMapperEx;
    @Resource
    private LogService logService;

    /**
     * 创建作品档案
     * 为艺术品创建完整的数字档案
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String createArtworkProfile(JSONObject artworkData, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证作品数据
            validateArtworkData(artworkData);
            
            // 2. 生成档案编号
            String archiveNo = generateArchiveNo();
            
            // 3. 创建艺术品档案
            ArtworkArchive artwork = new ArtworkArchive();
            artwork.setArchiveNo(archiveNo);
            artwork.setArtworkName(artworkData.getString("artworkName"));
            artwork.setArtworkType(artworkData.getString("artworkType"));
            artwork.setArtworkCategory(artworkData.getString("artworkCategory"));
            artwork.setArtworkStyle(artworkData.getString("artworkStyle"));
            artwork.setCreationDate(artworkData.getDate("creationDate"));
            artwork.setCreationPeriod(artworkData.getString("creationPeriod"));
            artwork.setCreatorName(artworkData.getString("creatorName"));
            artwork.setCreatorTitle(artworkData.getString("creatorTitle"));
            
            // 4. 设置大师信息
            JSONObject masterInfo = artworkData.getJSONObject("masterInfo");
            if (masterInfo != null) {
                artwork.setMasterInfo(masterInfo.toJSONString());
            }
            
            // 5. 设置作品规格和材料
            artwork.setDimensions(artworkData.getString("dimensions"));
            
            JSONArray materialsUsed = artworkData.getJSONArray("materialsUsed");
            if (materialsUsed != null) {
                artwork.setMaterialsUsed(materialsUsed.toJSONString());
            }
            
            JSONArray craftTechniques = artworkData.getJSONArray("craftTechniques");
            if (craftTechniques != null) {
                artwork.setCraftTechniques(craftTechniques.toJSONString());
            }
            
            // 6. 设置作品描述和文化信息
            artwork.setArtworkDescription(artworkData.getString("artworkDescription"));
            artwork.setCulturalSignificance(artworkData.getString("culturalSignificance"));
            artwork.setHistoricalBackground(artworkData.getString("historicalBackground"));
            artwork.setArtisticFeatures(artworkData.getString("artisticFeatures"));
            artwork.setColorScheme(artworkData.getString("colorScheme"));
            artwork.setPatternDescription(artworkData.getString("patternDescription"));
            artwork.setSymbolicMeaning(artworkData.getString("symbolicMeaning"));
            
            // 7. 设置价值信息
            artwork.setEstimatedValue(artworkData.getBigDecimal("estimatedValue"));
            artwork.setMarketPrice(artworkData.getBigDecimal("marketPrice"));
            artwork.setCollectionStatus(artworkData.getString("collectionStatus"));
            
            JSONObject ownerInfo = artworkData.getJSONObject("ownerInfo");
            if (ownerInfo != null) {
                artwork.setOwnerInfo(ownerInfo.toJSONString());
            }
            
            // 8. 设置历史记录
            JSONArray exhibitionHistory = artworkData.getJSONArray("exhibitionHistory");
            if (exhibitionHistory != null) {
                artwork.setExhibitionHistory(exhibitionHistory.toJSONString());
            }
            
            JSONArray awardHistory = artworkData.getJSONArray("awardHistory");
            if (awardHistory != null) {
                artwork.setAwardHistory(awardHistory.toJSONString());
            }
            
            // 9. 设置保存状态
            artwork.setConservationStatus(artworkData.getString("conservationStatus"));
            artwork.setConservationNotes(artworkData.getString("conservationNotes"));
            
            // 10. 设置多媒体资源
            JSONArray highResImages = artworkData.getJSONArray("highResImages");
            if (highResImages != null) {
                artwork.setHighResImages(highResImages.toJSONString());
            }
            
            JSONArray detailImages = artworkData.getJSONArray("detailImages");
            if (detailImages != null) {
                artwork.setDetailImages(detailImages.toJSONString());
            }
            
            JSONArray processImages = artworkData.getJSONArray("processImages");
            if (processImages != null) {
                artwork.setProcessImages(processImages.toJSONString());
            }
            
            JSONArray videoUrls = artworkData.getJSONArray("videoUrls");
            if (videoUrls != null) {
                artwork.setVideoUrls(videoUrls.toJSONString());
            }
            
            JSONArray documentUrls = artworkData.getJSONArray("documentUrls");
            if (documentUrls != null) {
                artwork.setDocumentUrls(documentUrls.toJSONString());
            }
            
            // 11. 设置显示设置
            artwork.setPublicDisplay(artworkData.getString("publicDisplay"));
            artwork.setStatus("1"); // 启用状态
            
            // 12. 关联生产工单
            Long productionOrderId = artworkData.getLong("productionOrderId");
            if (productionOrderId != null) {
                artwork.setProductionOrderId(productionOrderId);
            }
            
            // 13. 设置标准字段
            artwork.setTenantId(getCurrentTenantId());
            artwork.setDeleteFlag("0");
            artwork.setCreateTime(new Date());
            artwork.setCreateUser(getCurrentUserId(request));
            artwork.setUpdateTime(new Date());
            artwork.setUpdateUser(getCurrentUserId(request));
            
            // 14. 保存艺术品档案
            int result = artworkArchiveMapper.insertSelective(artwork);
            if (result <= 0) {
                throw new BusinessRunTimeException("作品档案创建失败");
            }
            
            // 15. 生成数字证书
            generateCertification(artwork.getId(), request);
            
            // 16. 记录操作日志
            String logContent = "创建艺术品档案: " + artwork.getArtworkName() + 
                             ", 类型: " + artwork.getArtworkType() +
                             ", 创作者: " + artwork.getCreatorName();
            logService.insertLog("艺术品档案", logContent, request);
            
            logger.info("艺术品档案创建完成: ID={}, 作品名称={}", artwork.getId(), artwork.getArtworkName());
            return String.valueOf(artwork.getId());
            
        } catch (Exception e) {
            logger.error("创建作品档案失败", e);
            throw new BusinessRunTimeException("创建作品档案失败: " + e.getMessage());
        }
    }

    /**
     * 生成数字鉴证
     * 为艺术品生成区块链数字证书
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String generateCertification(Long artworkId, HttpServletRequest request) throws Exception {
        try {
            // 1. 获取艺术品信息
            ArtworkArchive artwork = artworkArchiveMapper.selectByPrimaryKey(artworkId);
            if (artwork == null) {
                throw new BusinessRunTimeException("艺术品不存在: " + artworkId);
            }
            
            // 2. 生成数字证书信息
            JSONObject digitalCertificate = new JSONObject();
            digitalCertificate.put("certificateId", UUID.randomUUID().toString());
            digitalCertificate.put("artworkId", artworkId);
            digitalCertificate.put("archiveNo", artwork.getArchiveNo());
            digitalCertificate.put("artworkName", artwork.getArtworkName());
            digitalCertificate.put("creatorName", artwork.getCreatorName());
            digitalCertificate.put("creationDate", artwork.getCreationDate());
            digitalCertificate.put("certificationDate", new Date());
            digitalCertificate.put("issuer", "聆花文化艺术品认证中心");
            digitalCertificate.put("issuerSignature", generateDigitalSignature(artwork));
            
            // 3. 生成区块链哈希
            String blockchainHash = generateBlockchainHash(digitalCertificate);
            
            // 4. 生成NFT代币ID（模拟）
            String nftTokenId = "NFT_" + artwork.getArchiveNo() + "_" + System.currentTimeMillis();
            
            // 5. 生成二维码
            String qrCodeUrl = generateQRCode(artwork);
            
            // 6. 设置验证信息
            JSONObject verifierInfo = new JSONObject();
            verifierInfo.put("verifierId", getCurrentUserId(request));
            verifierInfo.put("verifierName", "系统管理员");
            verifierInfo.put("verificationMethod", "数字认证");
            verifierInfo.put("verificationStandard", "区块链数字签名");
            
            // 7. 更新艺术品档案
            artwork.setDigitalCertificate(digitalCertificate.toJSONString());
            artwork.setBlockchainHash(blockchainHash);
            artwork.setNftTokenId(nftTokenId);
            artwork.setQrCodeUrl(qrCodeUrl);
            artwork.setAuthenticityVerified("1");
            artwork.setVerificationDate(new Date());
            artwork.setVerifierInfo(verifierInfo.toJSONString());
            artwork.setUpdateTime(new Date());
            artwork.setUpdateUser(getCurrentUserId(request));
            
            int result = artworkArchiveMapper.updateByPrimaryKeySelective(artwork);
            if (result <= 0) {
                throw new BusinessRunTimeException("数字证书生成失败");
            }
            
            // 8. 记录操作日志
            String logContent = "生成数字鉴证: " + artwork.getArtworkName() + 
                             ", 证书ID: " + digitalCertificate.getString("certificateId");
            logService.insertLog("艺术品档案", logContent, request);
            
            logger.info("数字鉴证生成完成: artworkId={}, certificateId={}", 
                artworkId, digitalCertificate.getString("certificateId"));
            
            return digitalCertificate.getString("certificateId");
            
        } catch (Exception e) {
            logger.error("生成数字鉴证失败", e);
            throw new BusinessRunTimeException("生成数字鉴证失败: " + e.getMessage());
        }
    }

    /**
     * 追踪作品生命周期
     * 记录艺术品从创作到收藏的完整生命周期
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String trackArtworkLifecycle(Long artworkId, JSONObject lifecycleEvent, HttpServletRequest request) throws Exception {
        try {
            // 1. 获取艺术品信息
            ArtworkArchive artwork = artworkArchiveMapper.selectByPrimaryKey(artworkId);
            if (artwork == null) {
                throw new BusinessRunTimeException("艺术品不存在: " + artworkId);
            }
            
            // 2. 获取现有生命周期记录
            String existingEvents = artwork.getDocumentUrls(); // 复用字段存储生命周期事件
            JSONArray lifecycleEvents = new JSONArray();
            
            if (existingEvents != null && !existingEvents.isEmpty()) {
                try {
                    lifecycleEvents = JSONArray.parseArray(existingEvents);
                } catch (Exception e) {
                    logger.warn("解析现有生命周期事件失败", e);
                }
            }
            
            // 3. 添加新的生命周期事件
            JSONObject newEvent = new JSONObject();
            newEvent.put("eventId", UUID.randomUUID().toString());
            newEvent.put("eventType", lifecycleEvent.getString("eventType"));
            newEvent.put("eventDate", new Date());
            newEvent.put("eventDescription", lifecycleEvent.getString("eventDescription"));
            newEvent.put("location", lifecycleEvent.getString("location"));
            newEvent.put("participants", lifecycleEvent.getJSONArray("participants"));
            newEvent.put("evidenceUrls", lifecycleEvent.getJSONArray("evidenceUrls"));
            newEvent.put("significance", lifecycleEvent.getString("significance"));
            newEvent.put("recordBy", getCurrentUserId(request));
            newEvent.put("recordTime", new Date());
            
            // 4. 根据事件类型更新相关字段
            String eventType = lifecycleEvent.getString("eventType");
            switch (eventType) {
                case "展览":
                    updateExhibitionHistory(artwork, lifecycleEvent);
                    break;
                case "获奖":
                    updateAwardHistory(artwork, lifecycleEvent);
                    break;
                case "交易":
                    updateOwnerInfo(artwork, lifecycleEvent);
                    break;
                case "保养":
                    updateConservationStatus(artwork, lifecycleEvent);
                    break;
                default:
                    break;
            }
            
            lifecycleEvents.add(newEvent);
            
            // 5. 更新艺术品档案
            artwork.setDocumentUrls(lifecycleEvents.toJSONString());
            artwork.setUpdateTime(new Date());
            artwork.setUpdateUser(getCurrentUserId(request));
            
            int result = artworkArchiveMapper.updateByPrimaryKeySelective(artwork);
            if (result <= 0) {
                throw new BusinessRunTimeException("生命周期追踪失败");
            }
            
            // 6. 记录操作日志
            String logContent = "追踪作品生命周期: " + artwork.getArtworkName() + 
                             ", 事件类型: " + eventType +
                             ", 事件描述: " + lifecycleEvent.getString("eventDescription");
            logService.insertLog("艺术品档案", logContent, request);
            
            logger.info("作品生命周期追踪完成: artworkId={}, eventType={}", artworkId, eventType);
            return newEvent.getString("eventId");
            
        } catch (Exception e) {
            logger.error("追踪作品生命周期失败", e);
            throw new BusinessRunTimeException("追踪作品生命周期失败: " + e.getMessage());
        }
    }

    /**
     * 更新展览历史
     */
    private void updateExhibitionHistory(ArtworkArchive artwork, JSONObject lifecycleEvent) throws Exception {
        String existingHistory = artwork.getExhibitionHistory();
        JSONArray exhibitions = new JSONArray();
        
        if (existingHistory != null && !existingHistory.isEmpty()) {
            try {
                exhibitions = JSONArray.parseArray(existingHistory);
            } catch (Exception e) {
                logger.warn("解析展览历史失败", e);
            }
        }
        
        JSONObject exhibition = new JSONObject();
        exhibition.put("exhibitionName", lifecycleEvent.getString("exhibitionName"));
        exhibition.put("venue", lifecycleEvent.getString("location"));
        exhibition.put("startDate", lifecycleEvent.getString("startDate"));
        exhibition.put("endDate", lifecycleEvent.getString("endDate"));
        exhibition.put("organizer", lifecycleEvent.getString("organizer"));
        exhibition.put("significance", lifecycleEvent.getString("significance"));
        
        exhibitions.add(exhibition);
        artwork.setExhibitionHistory(exhibitions.toJSONString());
    }

    /**
     * 更新获奖历史
     */
    private void updateAwardHistory(ArtworkArchive artwork, JSONObject lifecycleEvent) throws Exception {
        String existingHistory = artwork.getAwardHistory();
        JSONArray awards = new JSONArray();
        
        if (existingHistory != null && !existingHistory.isEmpty()) {
            try {
                awards = JSONArray.parseArray(existingHistory);
            } catch (Exception e) {
                logger.warn("解析获奖历史失败", e);
            }
        }
        
        JSONObject award = new JSONObject();
        award.put("awardName", lifecycleEvent.getString("awardName"));
        award.put("awardLevel", lifecycleEvent.getString("awardLevel"));
        award.put("awardDate", lifecycleEvent.getString("eventDate"));
        award.put("awardOrganization", lifecycleEvent.getString("organization"));
        award.put("awardReason", lifecycleEvent.getString("awardReason"));
        
        awards.add(award);
        artwork.setAwardHistory(awards.toJSONString());
    }

    /**
     * 更新所有者信息
     */
    private void updateOwnerInfo(ArtworkArchive artwork, JSONObject lifecycleEvent) throws Exception {
        JSONObject ownerInfo = new JSONObject();
        ownerInfo.put("ownerName", lifecycleEvent.getString("newOwnerName"));
        ownerInfo.put("ownerType", lifecycleEvent.getString("newOwnerType"));
        ownerInfo.put("contactInfo", lifecycleEvent.getString("ownerContact"));
        ownerInfo.put("acquisitionDate", lifecycleEvent.getString("eventDate"));
        ownerInfo.put("acquisitionMethod", lifecycleEvent.getString("acquisitionMethod"));
        ownerInfo.put("transactionAmount", lifecycleEvent.getBigDecimal("transactionAmount"));
        
        artwork.setOwnerInfo(ownerInfo.toJSONString());
        artwork.setCollectionStatus(lifecycleEvent.getString("newCollectionStatus"));
    }

    /**
     * 更新保存状态
     */
    private void updateConservationStatus(ArtworkArchive artwork, JSONObject lifecycleEvent) throws Exception {
        artwork.setConservationStatus(lifecycleEvent.getString("newConservationStatus"));
        
        String existingNotes = artwork.getConservationNotes();
        String newNote = lifecycleEvent.getString("conservationNote");
        String updatedNotes = existingNotes != null ? existingNotes + "\n" + newNote : newNote;
        
        artwork.setConservationNotes(updatedNotes);
    }

    /**
     * 生成数字签名
     */
    private String generateDigitalSignature(ArtworkArchive artwork) throws Exception {
        String data = artwork.getArchiveNo() + artwork.getArtworkName() + 
                     artwork.getCreatorName() + artwork.getCreationDate();
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data.getBytes("UTF-8"));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }

    /**
     * 生成区块链哈希
     */
    private String generateBlockchainHash(JSONObject certificate) throws Exception {
        String data = certificate.toJSONString();
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(data.getBytes("UTF-8"));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return "0x" + hexString.toString();
    }

    /**
     * 生成二维码
     */
    private String generateQRCode(ArtworkArchive artwork) throws Exception {
        // 这里应该调用二维码生成服务
        // 临时返回模拟URL
        return "/qrcode/" + artwork.getArchiveNo() + ".png";
    }

    /**
     * 验证艺术品数据
     */
    private void validateArtworkData(JSONObject artworkData) throws Exception {
        if (artworkData == null) {
            throw new BusinessRunTimeException("艺术品数据不能为空");
        }
        
        if (artworkData.getString("artworkName") == null || artworkData.getString("artworkName").trim().isEmpty()) {
            throw new BusinessRunTimeException("作品名称不能为空");
        }
        
        if (artworkData.getString("artworkType") == null || artworkData.getString("artworkType").trim().isEmpty()) {
            throw new BusinessRunTimeException("作品类型不能为空");
        }
        
        if (artworkData.getString("creatorName") == null || artworkData.getString("creatorName").trim().isEmpty()) {
            throw new BusinessRunTimeException("创作者姓名不能为空");
        }
    }

    /**
     * 生成档案编号
     */
    private String generateArchiveNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "ART" + dateStr;
        
        // 查询当日最大序号
        String maxNo = artworkArchiveMapperEx.findMaxArchiveNoByPrefix(prefix);
        
        int sequence = 1;
        if (maxNo != null && maxNo.length() > prefix.length()) {
            String seqStr = maxNo.substring(prefix.length());
            try {
                sequence = Integer.parseInt(seqStr) + 1;
            } catch (NumberFormatException e) {
                sequence = 1;
            }
        }
        
        return prefix + String.format("%06d", sequence);
    }

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        return 1L; // 临时实现
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        return 1L; // 临时实现
    }
}
```

---

## 定制订单管理开发 (1天)

### 1. CustomOrderService.java - 定制订单服务

**文件路径**: `com.jsh.erp.custom.service.CustomOrderService`

```java
package com.jsh.erp.custom.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.custom.datasource.entities.CustomOrder;
import com.jsh.erp.custom.datasource.mappers.CustomOrderMapper;
import com.jsh.erp.custom.datasource.mappers.CustomOrderMapperEx;
import com.jsh.erp.service.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 定制订单服务
 * 提供个性化定制订单的管理功能
 */
@Service
public class CustomOrderService {
    private Logger logger = LoggerFactory.getLogger(CustomOrderService.class);

    @Resource
    private CustomOrderMapper customOrderMapper;
    @Resource
    private CustomOrderMapperEx customOrderMapperEx;
    @Resource
    private LogService logService;

    /**
     * 处理定制需求
     * 接收和处理客户的定制需求
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String handleCustomRequirement(JSONObject orderData, HttpServletRequest request) throws Exception {
        try {
            // 1. 验证订单数据
            validateOrderData(orderData);
            
            // 2. 生成订单编号
            String orderNo = generateOrderNo();
            
            // 3. 创建定制订单
            CustomOrder order = new CustomOrder();
            order.setOrderNo(orderNo);
            order.setCustomerId(orderData.getLong("customerId"));
            order.setCustomerName(orderData.getString("customerName"));
            order.setCustomerContact(orderData.getString("customerContact"));
            order.setOrderType(orderData.getString("orderType"));
            order.setCustomCategory(orderData.getString("customCategory"));
            order.setDesignRequirements(orderData.getString("designRequirements"));
            order.setSizeSpecifications(orderData.getString("sizeSpecifications"));
            
            // 4. 处理设计偏好
            JSONArray colorPreferences = orderData.getJSONArray("colorPreferences");
            if (colorPreferences != null) {
                order.setColorPreferences(colorPreferences.toJSONString());
            }
            
            order.setPatternRequirements(orderData.getString("patternRequirements"));
            
            JSONArray materialPreferences = orderData.getJSONArray("materialPreferences");
            if (materialPreferences != null) {
                order.setMaterialPreferences(materialPreferences.toJSONString());
            }
            
            order.setCraftRequirements(orderData.getString("craftRequirements"));
            order.setCulturalElements(orderData.getString("culturalElements"));
            order.setInspirationSources(orderData.getString("inspirationSources"));
            
            // 5. 处理参考图片
            JSONArray referenceImages = orderData.getJSONArray("referenceImages");
            if (referenceImages != null) {
                order.setReferenceImages(referenceImages.toJSONString());
            }
            
            // 6. 设置预算和交期
            order.setBudgetRangeMin(orderData.getBigDecimal("budgetRangeMin"));
            order.setBudgetRangeMax(orderData.getBigDecimal("budgetRangeMax"));
            order.setExpectedDelivery(orderData.getDate("expectedDelivery"));
            order.setUrgencyLevel(orderData.getString("urgencyLevel"));
            
            // 7. 初始化设计阶段
            order.setDesignPhase("需求确认");
            order.setOrderStatus("待确认");
            
            // 8. 设置标准字段
            order.setTenantId(getCurrentTenantId());
            order.setDeleteFlag("0");
            order.setCreateTime(new Date());
            order.setCreateUser(getCurrentUserId(request));
            order.setUpdateTime(new Date());
            order.setUpdateUser(getCurrentUserId(request));
            
            // 9. 保存定制订单
            int result = customOrderMapper.insertSelective(order);
            if (result <= 0) {
                throw new BusinessRunTimeException("定制订单创建失败");
            }
            
            // 10. 计算初步报价
            BigDecimal estimatedAmount = calculateEstimatedAmount(order);
            order.setTotalAmount(estimatedAmount);
            customOrderMapper.updateByPrimaryKeySelective(order);
            
            // 11. 记录操作日志
            String logContent = "处理定制需求: " + order.getCustomerName() + 
                             ", 定制类别: " + order.getCustomCategory() +
                             ", 预算范围: " + order.getBudgetRangeMin() + "-" + order.getBudgetRangeMax();
            logService.insertLog("定制订单", logContent, request);
            
            logger.info("定制需求处理完成: ID={}, 订单号={}", order.getId(), orderNo);
            return String.valueOf(order.getId());
            
        } catch (Exception e) {
            logger.error("处理定制需求失败", e);
            throw new BusinessRunTimeException("处理定制需求失败: " + e.getMessage());
        }
    }

    /**
     * 管理设计流程
     * 管理定制订单的设计开发流程
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public String manageDesignProcess(Long orderId, JSONObject designData, HttpServletRequest request) throws Exception {
        try {
            // 1. 获取订单信息
            CustomOrder order = customOrderMapper.selectByPrimaryKey(orderId);
            if (order == null) {
                throw new BusinessRunTimeException("定制订单不存在: " + orderId);
            }
            
            String action = designData.getString("action");
            
            switch (action) {
                case "ASSIGN_DESIGNER":
                    assignDesigner(order, designData, request);
                    break;
                case "SUBMIT_DRAFT":
                    submitDesignDraft(order, designData, request);
                    break;
                case "REQUEST_MODIFICATION":
                    requestModification(order, designData, request);
                    break;
                case "APPROVE_DESIGN":
                    approveDesign(order, designData, request);
                    break;
                case "FINALIZE_DESIGN":
                    finalizeDesign(order, designData, request);
                    break;
                default:
                    throw new BusinessRunTimeException("不支持的设计操作: " + action);
            }
            
            // 更新订单
            order.setUpdateTime(new Date());
            order.setUpdateUser(getCurrentUserId(request));
            
            int result = customOrderMapper.updateByPrimaryKeySelective(order);
            if (result <= 0) {
                throw new BusinessRunTimeException("设计流程管理失败");
            }
            
            // 记录操作日志
            String logContent = "管理设计流程: " + order.getOrderNo() + 
                             ", 操作: " + action +
                             ", 设计阶段: " + order.getDesignPhase();
            logService.insertLog("定制订单", logContent, request);
            
            logger.info("设计流程管理完成: orderId={}, action={}", orderId, action);
            return String.valueOf(order.getId());
            
        } catch (Exception e) {
            logger.error("管理设计流程失败", e);
            throw new BusinessRunTimeException("管理设计流程失败: " + e.getMessage());
        }
    }

    /**
     * 分配设计师
     */
    private void assignDesigner(CustomOrder order, JSONObject designData, HttpServletRequest request) throws Exception {
        Long designerId = designData.getLong("designerId");
        String designerName = designData.getString("designerName");
        
        order.setDesignerId(designerId);
        order.setDesignerName(designerName);
        order.setDesignStartDate(new Date());
        order.setDesignPhase("初稿设计");
        
        // 记录设计师分配信息
        JSONObject designerInfo = new JSONObject();
        designerInfo.put("designerId", designerId);
        designerInfo.put("designerName", designerName);
        designerInfo.put("assignDate", new Date());
        designerInfo.put("assignBy", getCurrentUserId(request));
        
        // 更新设计稿信息
        updateDesignDrafts(order, "DESIGNER_ASSIGNED", designerInfo);
    }

    /**
     * 提交设计稿
     */
    private void submitDesignDraft(CustomOrder order, JSONObject designData, HttpServletRequest request) throws Exception {
        JSONObject draftInfo = new JSONObject();
        draftInfo.put("draftVersion", designData.getString("draftVersion"));
        draftInfo.put("draftUrls", designData.getJSONArray("draftUrls"));
        draftInfo.put("designDescription", designData.getString("designDescription"));
        draftInfo.put("designFeatures", designData.getJSONArray("designFeatures"));
        draftInfo.put("submitDate", new Date());
        draftInfo.put("submitBy", getCurrentUserId(request));
        
        // 更新设计阶段
        String currentPhase = order.getDesignPhase();
        if ("初稿设计".equals(currentPhase)) {
            order.setDesignPhase("客户确认");
        } else if ("修改完善".equals(currentPhase)) {
            order.setDesignPhase("客户确认");
        }
        
        // 更新设计稿信息
        updateDesignDrafts(order, "DRAFT_SUBMITTED", draftInfo);
    }

    /**
     * 申请修改
     */
    private void requestModification(CustomOrder order, JSONObject designData, HttpServletRequest request) throws Exception {
        JSONObject modificationInfo = new JSONObject();
        modificationInfo.put("modificationRequests", designData.getJSONArray("modificationRequests"));
        modificationInfo.put("modificationReason", designData.getString("modificationReason"));
        modificationInfo.put("requestDate", new Date());
        modificationInfo.put("requestBy", getCurrentUserId(request));
        
        order.setDesignPhase("修改完善");
        
        // 更新修改历史
        updateModificationHistory(order, modificationInfo);
        
        // 记录客户反馈
        updateCustomerFeedback(order, designData);
    }

    /**
     * 批准设计
     */
    private void approveDesign(CustomOrder order, JSONObject designData, HttpServletRequest request) throws Exception {
        JSONObject approvalInfo = new JSONObject();
        approvalInfo.put("approvalComment", designData.getString("approvalComment"));
        approvalInfo.put("approvalDate", new Date());
        approvalInfo.put("approvalBy", getCurrentUserId(request));
        
        order.setDesignPhase("终稿确认");
        
        // 更新设计稿信息
        updateDesignDrafts(order, "DESIGN_APPROVED", approvalInfo);
    }

    /**
     * 确定最终设计
     */
    private void finalizeDesign(CustomOrder order, JSONObject designData, HttpServletRequest request) throws Exception {
        String finalDesignUrl = designData.getString("finalDesignUrl");
        order.setFinalDesignUrl(finalDesignUrl);
        order.setDesignCompletionDate(new Date());
        order.setDesignPhase("设计完成");
        order.setOrderStatus("生产准备");
        
        // 设置设计版权信息
        JSONObject copyrightInfo = new JSONObject();
        copyrightInfo.put("copyrightOwner", "聆花文化");
        copyrightInfo.put("designerId", order.getDesignerId());
        copyrightInfo.put("completionDate", new Date());
        copyrightInfo.put("exclusiveRights", designData.getBoolean("exclusiveRights"));
        
        order.setDesignCopyright(copyrightInfo.toJSONString());
        
        // 生成生产要求
        generateProductionRequirements(order, designData);
    }

    /**
     * 更新设计稿信息
     */
    private void updateDesignDrafts(CustomOrder order, String eventType, JSONObject eventData) throws Exception {
        String existingDrafts = order.getDesignDrafts();
        JSONArray drafts = new JSONArray();
        
        if (existingDrafts != null && !existingDrafts.isEmpty()) {
            try {
                drafts = JSONArray.parseArray(existingDrafts);
            } catch (Exception e) {
                logger.warn("解析设计稿信息失败", e);
            }
        }
        
        JSONObject draftEvent = new JSONObject();
        draftEvent.put("eventType", eventType);
        draftEvent.put("eventData", eventData);
        draftEvent.put("timestamp", new Date());
        
        drafts.add(draftEvent);
        order.setDesignDrafts(drafts.toJSONString());
    }

    /**
     * 更新修改历史
     */
    private void updateModificationHistory(CustomOrder order, JSONObject modificationInfo) throws Exception {
        String existingHistory = order.getModificationHistory();
        JSONArray history = new JSONArray();
        
        if (existingHistory != null && !existingHistory.isEmpty()) {
            try {
                history = JSONArray.parseArray(existingHistory);
            } catch (Exception e) {
                logger.warn("解析修改历史失败", e);
            }
        }
        
        history.add(modificationInfo);
        order.setModificationHistory(history.toJSONString());
    }

    /**
     * 更新客户反馈
     */
    private void updateCustomerFeedback(CustomOrder order, JSONObject designData) throws Exception {
        String existingFeedback = order.getCustomerFeedback();
        JSONArray feedback = new JSONArray();
        
        if (existingFeedback != null && !existingFeedback.isEmpty()) {
            try {
                feedback = JSONArray.parseArray(existingFeedback);
            } catch (Exception e) {
                logger.warn("解析客户反馈失败", e);
            }
        }
        
        JSONObject newFeedback = new JSONObject();
        newFeedback.put("feedbackContent", designData.getString("feedbackContent"));
        newFeedback.put("satisfactionLevel", designData.getInteger("satisfactionLevel"));
        newFeedback.put("suggestions", designData.getJSONArray("suggestions"));
        newFeedback.put("feedbackDate", new Date());
        
        feedback.add(newFeedback);
        order.setCustomerFeedback(feedback.toJSONString());
    }

    /**
     * 生成生产要求
     */
    private void generateProductionRequirements(CustomOrder order, JSONObject designData) throws Exception {
        JSONObject productionReq = new JSONObject();
        productionReq.put("materials", designData.getJSONArray("finalMaterials"));
        productionReq.put("techniques", designData.getJSONArray("finalTechniques"));
        productionReq.put("specifications", designData.getJSONObject("finalSpecifications"));
        productionReq.put("qualityStandards", designData.getJSONArray("qualityStandards"));
        productionReq.put("specialInstructions", designData.getString("specialInstructions"));
        
        order.setProductionRequirements(productionReq.toJSONString());
        
        // 设置质量标准和包装要求
        order.setQualityStandards(designData.getString("qualityStandards"));
        order.setPackagingRequirements(designData.getString("packagingRequirements"));
        order.setDeliveryRequirements(designData.getString("deliveryRequirements"));
    }

    /**
     * 计算估价
     */
    private BigDecimal calculateEstimatedAmount(CustomOrder order) throws Exception {
        BigDecimal baseAmount = BigDecimal.ZERO;
        
        // 根据定制类别设置基础价格
        String category = order.getCustomCategory();
        switch (category) {
            case "丝巾":
                baseAmount = new BigDecimal("800");
                break;
            case "围巾":
                baseAmount = new BigDecimal("1200");
                break;
            case "披肩":
                baseAmount = new BigDecimal("2000");
                break;
            case "挂毯":
                baseAmount = new BigDecimal("5000");
                break;
            default:
                baseAmount = new BigDecimal("1000");
                break;
        }
        
        // 根据紧急程度调整价格
        String urgency = order.getUrgencyLevel();
        if ("加急".equals(urgency)) {
            baseAmount = baseAmount.multiply(new BigDecimal("1.5"));
        } else if ("紧急".equals(urgency)) {
            baseAmount = baseAmount.multiply(new BigDecimal("1.3"));
        }
        
        // 设计费
        BigDecimal designFee = baseAmount.multiply(new BigDecimal("0.2"));
        order.setDesignFee(designFee);
        
        // 生产成本（估算）
        BigDecimal productionCost = baseAmount.multiply(new BigDecimal("0.6"));
        order.setProductionCost(productionCost);
        
        // 利润率
        order.setProfitMargin(new BigDecimal("30"));
        
        return baseAmount;
    }

    /**
     * 验证订单数据
     */
    private void validateOrderData(JSONObject orderData) throws Exception {
        if (orderData == null) {
            throw new BusinessRunTimeException("订单数据不能为空");
        }
        
        if (orderData.getString("customerName") == null || orderData.getString("customerName").trim().isEmpty()) {
            throw new BusinessRunTimeException("客户姓名不能为空");
        }
        
        if (orderData.getString("customerContact") == null || orderData.getString("customerContact").trim().isEmpty()) {
            throw new BusinessRunTimeException("客户联系方式不能为空");
        }
        
        if (orderData.getString("customCategory") == null || orderData.getString("customCategory").trim().isEmpty()) {
            throw new BusinessRunTimeException("定制类别不能为空");
        }
        
        if (orderData.getString("designRequirements") == null || orderData.getString("designRequirements").trim().isEmpty()) {
            throw new BusinessRunTimeException("设计需求不能为空");
        }
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());
        String prefix = "CO" + dateStr;
        
        // 查询当日最大序号
        String maxNo = customOrderMapperEx.findMaxOrderNoByPrefix(prefix);
        
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
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        return 1L; // 临时实现
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        return 1L; // 临时实现
    }
}
```

---

## 验收标准

### 功能验收标准
1. **工艺知识库功能**: 支持完整的非遗工艺知识管理和传承
2. **艺术品档案功能**: 支持艺术品的数字化档案管理和认证
3. **定制订单功能**: 支持个性化定制的完整业务流程
4. **数字认证功能**: 支持区块链数字证书和NFT生成

### 质量验收标准
1. **数据完整性**: 所有非遗特色数据完整准确
2. **文化传承性**: 正确体现非遗文化的传承价值
3. **多租户支持**: 正确实现租户数据隔离
4. **权限控制**: 按角色控制文化资产访问权限

### 业务验收标准
1. **文化特色突出**: 充分体现聆花文化的非遗特色
2. **用户体验优秀**: 界面美观，操作便捷
3. **知识传承有效**: 支持有效的工艺知识传承
4. **数字化程度高**: 实现文化资产的数字化管理

---

## 交付物清单

### 代码交付物
1. **后端代码**: Service、Controller、Mapper等Java类
2. **前端代码**: Vue组件和页面
3. **数据库脚本**: 表结构和初始数据
4. **数字认证**: 区块链集成代码

### 文档交付物
1. **开发文档**: 本文档
2. **接口文档**: API接口说明文档
3. **文化手册**: 非遗文化管理手册
4. **认证指南**: 数字认证操作指南

---

## 后续优化建议

### 功能优化
1. **AR展示**: 增加艺术品的AR展示功能
2. **虚拟博物馆**: 建立线上虚拟展览馆
3. **智能推荐**: 基于AI的工艺学习推荐
4. **社区功能**: 建立非遗爱好者社区

### 技术优化
1. **区块链集成**: 真正的区块链技术集成
2. **NFT市场**: 建立NFT交易平台
3. **多媒体优化**: 提升图片和视频处理能力
4. **搜索优化**: 实现更智能的文化内容搜索