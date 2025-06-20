# 聆花文化ERP系统最佳实践实现方案

**编写时间**: 2025年6月17日  
**基于系统**: jshERP v3.5.0  
**实现目标**: 在原ERP系统最小入侵的情况下，实现聆花文化特定业务需求

---

## 目录

1. [方案概述](#1-方案概述)
2. [核心设计原则](#2-核心设计原则)
3. [架构设计方案](#3-架构设计方案)
4. [数据库设计方案](#4-数据库设计方案)
5. [业务模块实现方案](#5-业务模块实现方案)
6. [技术实现细节](#6-技术实现细节)
7. [部署和运维方案](#7-部署和运维方案)
8. [风险控制和预案](#8-风险控制和预案)

---

## 1. 方案概述

### 1.1 项目背景

聆花文化作为非遗掐丝珐琅的代表品牌，需要一套专业的ERP管理系统来支撑其多元化业务：
- 掐丝珐琅作品的生产制作
- 非遗团建活动管理
- 咖啡店日常运营
- 人员薪酬和排班管理

### 1.2 实现目标

**最小入侵原则**: 在保持原jshERP系统架构完整性的前提下，通过扩展和配置的方式实现聆花文化的特定业务需求。

**核心目标**:
- 复用70%以上的现有代码和组件
- 保持系统架构的一致性和稳定性
- 实现业务需求的100%覆盖
- 确保系统的可维护性和扩展性

### 1.3 技术选型依据

**后端技术栈保持不变**:
- Spring Boot 2.x（复用现有架构）
- MyBatis-Plus（复用数据访问层）
- MySQL 5.7+（复用数据存储）
- Redis（复用缓存机制）

**前端技术栈保持不变**:
- Vue.js 2.x（复用现有框架）
- Ant Design Vue（复用UI组件库）
- Vuex（复用状态管理）
- Axios（复用HTTP客户端）

---

## 2. 核心设计原则

### 2.1 最小入侵原则

**定义**: 在不修改现有核心代码的前提下，通过扩展的方式实现新功能。

**具体实施**:
- 继承现有基类而不是修改基类
- 扩展数据表而不是修改现有表结构
- 新增配置项而不是修改现有配置
- 添加新页面而不是修改现有页面

### 2.2 架构一致性原则

**定义**: 新增功能必须遵循现有系统的架构模式和编码规范。

**具体要求**:
- 遵循MVC三层架构模式
- 使用统一的响应格式和异常处理
- 遵循现有的命名规范和代码风格
- 保持API设计的RESTful风格

### 2.3 数据隔离原则

**定义**: 利用现有多租户机制实现聆花文化数据的完全隔离。

**实现方法**:
- 所有新增表包含tenant_id字段
- 业务逻辑自动注入租户过滤条件
- 前端界面基于权限控制访问范围
- 数据备份和恢复独立进行

### 2.4 业务适配原则

**定义**: 充分理解聆花文化的业务特点，提供最佳的业务流程适配。

**核心考虑**:
- 非遗工艺的特殊性（制作工艺、材料管理）
- 团建活动的复杂性（人员、场地、预算管理）
- 小微企业的简便性（操作简单、功能聚焦）
- 文化企业的特色（作品档案、知识管理）

---

## 3. 架构设计方案

### 3.1 整体架构图

```
聆花文化ERP系统架构
├── 前端展示层 (Vue.js + Ant Design Vue)
│   ├── 现有业务页面（商品、库存、财务等）
│   ├── 扩展业务页面（团建、薪酬、排班等）
│   └── 聆花特色页面（作品档案、非遗知识库等）
├── 业务逻辑层 (Spring Boot)
│   ├── 现有业务模块（Material、Depot、Account等）
│   ├── 扩展业务模块（Teambuilding、Salary、Schedule等）
│   └── 聆花特色模块（Artwork、Heritage等）
├── 数据访问层 (MyBatis-Plus)
│   ├── 现有数据映射（MaterialMapper、DepotMapper等）
│   ├── 扩展数据映射（TeambuildingMapper、SalaryMapper等）
│   └── 聆花特色映射（ArtworkMapper、HeritageMapper等）
└── 数据存储层 (MySQL + Redis)
    ├── 现有业务表（jsh_material、jsh_depot等）
    ├── 扩展业务表（jsh_teambuilding、jsh_salary等）
    └── 聆花特色表（jsh_artwork、jsh_heritage等）
```

### 3.2 模块扩展策略

#### 3.2.1 继承扩展模式

**适用场景**: 在现有模块基础上添加新功能

**实现方式**:
```java
// 示例：扩展商品管理支持艺术品属性
@RestController
@RequestMapping(value = "/artwork")
@Api(tags = {"艺术品管理"})
public class ArtworkController extends MaterialController {
    
    @Resource
    private ArtworkService artworkService;
    
    @PostMapping(value = "/createArtwork")
    @ApiOperation(value = "创建艺术品")
    public String createArtwork(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        // 调用父类方法创建基础商品
        String result = super.addResource(obj, request);
        
        // 添加艺术品特有属性
        if (result.contains("success")) {
            artworkService.addArtworkAttributes(obj, request);
        }
        
        return result;
    }
}
```

#### 3.2.2 组合扩展模式

**适用场景**: 新增独立业务模块

**实现方式**:
```java
// 示例：团建管理模块
@RestController
@RequestMapping(value = "/teambuilding")
@Api(tags = {"团建管理"})
public class TeambuildingController extends BaseController {
    
    @Resource
    private TeambuildingService teambuildingService;
    
    @Resource
    private MaterialService materialService; // 复用现有服务
    
    @Resource
    private PersonService personService; // 复用现有服务
}
```

### 3.3 数据流程设计

#### 3.3.1 生产制作流程

```
销售订单 → 生产工单 → 材料出库 → 半成品入库 → 成品制作 → 成品入库 → 销售出库
    ↓           ↓           ↓           ↓           ↓           ↓           ↓
复用现有    新增模块    复用现有    复用现有    新增模块    复用现有    复用现有
订单管理    工单管理    库存管理    库存管理    工艺管理    库存管理    销售管理
```

#### 3.3.2 团建活动流程

```
客户咨询 → 活动预约 → 资源分配 → 活动执行 → 费用结算 → 客户反馈
    ↓           ↓           ↓           ↓           ↓           ↓
复用现有    新增模块    新增模块    新增模块    复用现有    新增模块
客户管理    团建管理    资源管理    活动管理    财务管理    反馈管理
```

---

## 4. 数据库设计方案

### 4.1 表设计原则

#### 4.1.1 命名规范

**表名规范**:
- 所有表名以`jsh_`为前缀
- 使用下划线分隔的小写字母
- 表名应清晰表达业务含义

**字段命名规范**:
- 主键统一使用`id`
- 外键使用`关联表名_id`格式
- 时间字段统一使用`_time`后缀
- 布尔字段统一使用`is_`前缀或`_flag`后缀

#### 4.1.2 标准字段

**所有业务表必须包含的字段**:
```sql
-- 多租户字段
tenant_id bigint(20) DEFAULT NULL COMMENT '租户id',

-- 逻辑删除字段
delete_flag varchar(1) DEFAULT '0' COMMENT '删除标记，0未删除，1删除',

-- 审计字段（可选）
create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
create_by bigint(20) DEFAULT NULL COMMENT '创建人',
update_by bigint(20) DEFAULT NULL COMMENT '更新人'
```

### 4.2 现有表扩展方案

#### 4.2.1 商品表扩展（支持艺术品管理）

**扩展jsh_material表**:
```sql
-- 添加艺术品相关字段
ALTER TABLE jsh_material ADD COLUMN artwork_no varchar(50) DEFAULT NULL COMMENT '艺术品编号';
ALTER TABLE jsh_material ADD COLUMN craft_type varchar(50) DEFAULT NULL COMMENT '工艺类型';
ALTER TABLE jsh_material ADD COLUMN artist_id bigint(20) DEFAULT NULL COMMENT '艺术家/制作人ID';
ALTER TABLE jsh_material ADD COLUMN creation_date date DEFAULT NULL COMMENT '创作日期';
ALTER TABLE jsh_material ADD COLUMN certification_code varchar(100) DEFAULT NULL COMMENT '鉴证码';

-- 创建索引
CREATE INDEX idx_artwork_no ON jsh_material(artwork_no);
CREATE INDEX idx_craft_type ON jsh_material(craft_type);
CREATE INDEX idx_artist_id ON jsh_material(artist_id);
```

#### 4.2.2 用户表扩展（支持薪酬管理）

**扩展jsh_user表**:
```sql
-- 添加薪酬相关字段
ALTER TABLE jsh_user ADD COLUMN salary_structure varchar(500) DEFAULT NULL COMMENT '薪酬结构配置';
ALTER TABLE jsh_user ADD COLUMN bank_account varchar(50) DEFAULT NULL COMMENT '银行账号';
ALTER TABLE jsh_user ADD COLUMN id_card varchar(20) DEFAULT NULL COMMENT '身份证号';
ALTER TABLE jsh_user ADD COLUMN entry_date date DEFAULT NULL COMMENT '入职日期';
ALTER TABLE jsh_user ADD COLUMN skill_level varchar(20) DEFAULT NULL COMMENT '技能等级';

-- 创建索引
CREATE INDEX idx_entry_date ON jsh_user(entry_date);
CREATE INDEX idx_skill_level ON jsh_user(skill_level);
```

### 4.3 新增业务表设计

#### 4.3.1 团建活动管理表

```sql
-- 团建活动主表
CREATE TABLE `jsh_teambuilding_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_no` varchar(50) NOT NULL COMMENT '活动编号',
  `activity_name` varchar(200) NOT NULL COMMENT '活动名称',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户ID',
  `activity_date` date NOT NULL COMMENT '活动日期',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `venue_id` bigint(20) DEFAULT NULL COMMENT '场地ID',
  `instructor_id` bigint(20) DEFAULT NULL COMMENT '讲师ID',
  `assistant_id` bigint(20) DEFAULT NULL COMMENT '助理ID',
  `expected_participants` int(11) DEFAULT NULL COMMENT '预期参与人数',
  `actual_participants` int(11) DEFAULT NULL COMMENT '实际参与人数',
  `budget_amount` decimal(10,2) DEFAULT NULL COMMENT '预算金额',
  `actual_amount` decimal(10,2) DEFAULT NULL COMMENT '实际金额',
  `activity_content` text COMMENT '活动内容',
  `special_requirements` text COMMENT '特殊要求',
  `status` varchar(20) DEFAULT 'pending' COMMENT '活动状态：pending-待定，confirmed-确认，completed-完成，cancelled-取消',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_no_tenant` (`activity_no`, `tenant_id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_activity_date` (`activity_date`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团建活动表';

-- 团建参与人员表
CREATE TABLE `jsh_teambuilding_participant` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `participant_name` varchar(100) NOT NULL COMMENT '参与者姓名',
  `participant_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `participant_company` varchar(200) DEFAULT NULL COMMENT '所属公司',
  `participant_position` varchar(100) DEFAULT NULL COMMENT '职位',
  `special_needs` varchar(500) DEFAULT NULL COMMENT '特殊需求',
  `attendance_status` varchar(20) DEFAULT 'registered' COMMENT '出席状态：registered-已报名，confirmed-已确认，attended-已出席，absent-缺席',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_attendance_status` (`attendance_status`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团建参与人员表';

-- 团建费用明细表
CREATE TABLE `jsh_teambuilding_expense` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `expense_type` varchar(50) NOT NULL COMMENT '费用类型：venue-场地费，material-材料费，instructor-讲师费，assistant-助理费，other-其他',
  `expense_name` varchar(200) NOT NULL COMMENT '费用名称',
  `expense_amount` decimal(10,2) NOT NULL COMMENT '费用金额',
  `expense_description` varchar(500) DEFAULT NULL COMMENT '费用说明',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_expense_type` (`expense_type`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团建费用明细表';
```

#### 4.3.2 薪酬管理表

```sql
-- 薪酬记录表
CREATE TABLE `jsh_salary_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `salary_no` varchar(50) NOT NULL COMMENT '薪酬单号',
  `user_id` bigint(20) NOT NULL COMMENT '员工ID',
  `salary_month` varchar(7) NOT NULL COMMENT '薪酬月份(YYYY-MM)',
  `base_salary` decimal(10,2) DEFAULT 0.00 COMMENT '基本工资',
  `production_salary` decimal(10,2) DEFAULT 0.00 COMMENT '生产工资',
  `sales_commission` decimal(10,2) DEFAULT 0.00 COMMENT '销售提成',
  `teambuilding_commission` decimal(10,2) DEFAULT 0.00 COMMENT '团建提成',
  `coffee_shop_commission` decimal(10,2) DEFAULT 0.00 COMMENT '咖啡店提成',
  `overtime_pay` decimal(10,2) DEFAULT 0.00 COMMENT '加班费',
  `bonus` decimal(10,2) DEFAULT 0.00 COMMENT '奖金',
  `deduction` decimal(10,2) DEFAULT 0.00 COMMENT '扣款',
  `social_insurance` decimal(10,2) DEFAULT 0.00 COMMENT '社保',
  `housing_fund` decimal(10,2) DEFAULT 0.00 COMMENT '公积金',
  `tax` decimal(10,2) DEFAULT 0.00 COMMENT '个人所得税',
  `total_salary` decimal(10,2) DEFAULT 0.00 COMMENT '应发工资',
  `net_salary` decimal(10,2) DEFAULT 0.00 COMMENT '实发工资',
  `payment_status` varchar(20) DEFAULT 'pending' COMMENT '发放状态：pending-待发放，paid-已发放，cancelled-已取消',
  `payment_date` date DEFAULT NULL COMMENT '发放日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_salary_no_tenant` (`salary_no`, `tenant_id`),
  UNIQUE KEY `uk_user_month_tenant` (`user_id`, `salary_month`, `tenant_id`),
  KEY `idx_salary_month` (`salary_month`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬记录表';

-- 薪酬明细表
CREATE TABLE `jsh_salary_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `salary_record_id` bigint(20) NOT NULL COMMENT '薪酬记录ID',
  `detail_type` varchar(50) NOT NULL COMMENT '明细类型：production-生产，sales-销售，teambuilding-团建，overtime-加班',
  `detail_source` varchar(100) DEFAULT NULL COMMENT '来源单据号',
  `detail_amount` decimal(10,2) NOT NULL COMMENT '明细金额',
  `detail_description` varchar(500) DEFAULT NULL COMMENT '明细说明',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_salary_record_id` (`salary_record_id`),
  KEY `idx_detail_type` (`detail_type`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬明细表';
```

#### 4.3.3 排班管理表

```sql
-- 排班记录表
CREATE TABLE `jsh_schedule_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `user_id` bigint(20) NOT NULL COMMENT '员工ID',
  `shift_type` varchar(20) NOT NULL COMMENT '班次类型：morning-早班，afternoon-午班，evening-晚班，full-全天',
  `start_time` time DEFAULT NULL COMMENT '开始时间',
  `end_time` time DEFAULT NULL COMMENT '结束时间',
  `workplace` varchar(100) DEFAULT NULL COMMENT '工作地点',
  `work_content` varchar(500) DEFAULT NULL COMMENT '工作内容',
  `status` varchar(20) DEFAULT 'scheduled' COMMENT '状态：scheduled-已排班，confirmed-已确认，completed-已完成，cancelled-已取消',
  `actual_start_time` datetime DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` datetime DEFAULT NULL COMMENT '实际结束时间',
  `work_hours` decimal(4,2) DEFAULT NULL COMMENT '工作时长',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_by` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_date_user_shift` (`schedule_date`, `user_id`, `shift_type`, `tenant_id`),
  KEY `idx_schedule_date` (`schedule_date`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_shift_type` (`shift_type`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_delete` (`tenant_id`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班记录表';
```

### 4.4 数据迁移方案

#### 4.4.1 基础数据初始化

```sql
-- 初始化聆花文化租户数据
INSERT INTO jsh_tenant (tenant_id, login_name, user_num_limit, type, enabled, create_time, expire_time, delete_flag) 
VALUES (1001, 'linghua', 50, '1', 1, NOW(), '2025-12-31 23:59:59', '0');

-- 初始化管理员用户
INSERT INTO jsh_user (username, login_name, password, ismanager, tenant_id, department, position, delete_flag) 
VALUES ('聆花管理员', 'linghua_admin', MD5('123456'), 1, 1001, '管理部', '系统管理员', '0');

-- 初始化商品分类
INSERT INTO jsh_material_category (name, parent_id, sort, tenant_id, delete_flag) VALUES
('掐丝珐琅', NULL, 1, 1001, '0'),
('配饰制品', NULL, 2, 1001, '0'),
('原材料', NULL, 3, 1001, '0'),
('半成品', NULL, 4, 1001, '0'),
('成品', NULL, 5, 1001, '0');

-- 初始化仓库
INSERT INTO jsh_depot (name, address, principal, sort, isdefault, tenant_id, delete_flag) VALUES
('广州原料仓', '广州市天河区', 1, 1, 1, 1001, '0'),
('崇左生产基地仓', '广西崇左市', 2, 2, 0, 1001, '0'),
('广州半成品仓', '广州市天河区', 1, 3, 0, 1001, '0'),
('广州成品仓', '广州市天河区', 1, 4, 0, 1001, '0'),
('深圳景之蓝仓', '深圳市南山区', 3, 5, 0, 1001, '0');
```

---

## 5. 业务模块实现方案

### 5.1 团建管理模块

#### 5.1.1 后端实现

**实体类设计**:
```java
// 团建活动实体
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsh_teambuilding_activity")
public class TeambuildingActivity {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String activityNo;
    private String activityName;
    private Long customerId;
    private LocalDate activityDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long venueId;
    private Long instructorId;
    private Long assistantId;
    private Integer expectedParticipants;
    private Integer actualParticipants;
    private BigDecimal budgetAmount;
    private BigDecimal actualAmount;
    private String activityContent;
    private String specialRequirements;
    private String status;
    
    // 多租户和审计字段
    private Long tenantId;
    private String deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
}
```

**Service层实现**:
```java
@Service
public class TeambuildingActivityService {
    
    @Resource
    private TeambuildingActivityMapper teambuildingActivityMapper;
    
    @Resource
    private SupplierService supplierService; // 复用客户管理
    
    @Resource
    private UserService userService; // 复用用户管理
    
    @Resource
    private LogService logService; // 复用日志服务
    
    /**
     * 创建团建活动
     */
    @Transactional(rollbackFor = Exception.class)
    public int createActivity(JSONObject obj, HttpServletRequest request) throws Exception {
        try {
            TeambuildingActivity activity = JSONObject.parseObject(obj.toJSONString(), TeambuildingActivity.class);
            
            // 生成活动编号
            activity.setActivityNo(generateActivityNo());
            
            // 设置默认状态
            activity.setStatus("pending");
            
            // 插入主表
            teambuildingActivityMapper.insert(activity);
            
            // 处理参与人员
            saveParticipants(obj.getJSONArray("participants"), activity.getId());
            
            // 处理费用预算
            saveBudgetExpenses(obj.getJSONArray("expenses"), activity.getId());
            
            // 记录操作日志
            logService.insertLog("团建活动", 
                "创建团建活动：" + activity.getActivityName(), request);
            
            return 1;
        } catch (Exception e) {
            logger.error("创建团建活动失败", e);
            throw e;
        }
    }
    
    /**
     * 活动确认
     */
    @Transactional(rollbackFor = Exception.class)
    public int confirmActivity(Long activityId, HttpServletRequest request) throws Exception {
        try {
            TeambuildingActivity activity = teambuildingActivityMapper.selectById(activityId);
            if (activity == null) {
                throw new BusinessRunTimeException("活动不存在");
            }
            
            // 更新状态
            activity.setStatus("confirmed");
            teambuildingActivityMapper.updateById(activity);
            
            // 生成财务收款单（复用现有财务模块）
            generateFinancialRecord(activity);
            
            // 记录日志
            logService.insertLog("团建活动", 
                "确认团建活动：" + activity.getActivityName(), request);
            
            return 1;
        } catch (Exception e) {
            logger.error("确认团建活动失败", e);
            throw e;
        }
    }
    
    /**
     * 生成活动编号
     */
    private String generateActivityNo() {
        String prefix = "TB";
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String sequence = String.format("%03d", getNextSequence());
        return prefix + date + sequence;
    }
    
    /**
     * 复用现有财务模块生成收款单
     */
    private void generateFinancialRecord(TeambuildingActivity activity) {
        // 调用现有财务服务创建收款单
        // 这里展示如何复用现有模块
    }
}
```

**Controller层实现**:
```java
@RestController
@RequestMapping(value = "/teambuilding")
@Api(tags = {"团建管理"})
public class TeambuildingActivityController extends BaseController {
    
    @Resource
    private TeambuildingActivityService teambuildingActivityService;
    
    @GetMapping(value = "/list")
    @ApiOperation(value = "获取团建活动列表")
    public TableDataInfo getList(
            @RequestParam(value = Constants.SEARCH, required = false) String search,
            HttpServletRequest request) throws Exception {
        
        Map<String, Object> parameterMap = new HashMap<>();
        
        // 解析搜索参数
        if (StringUtil.isNotEmpty(search)) {
            String activityName = StringUtil.getInfo(search, "activityName");
            String status = StringUtil.getInfo(search, "status");
            String startDate = StringUtil.getInfo(search, "startDate");
            String endDate = StringUtil.getInfo(search, "endDate");
            
            parameterMap.put("activityName", activityName);
            parameterMap.put("status", status);
            parameterMap.put("startDate", startDate);
            parameterMap.put("endDate", endDate);
        }
        
        List<TeambuildingActivityVo> list = teambuildingActivityService.selectList(parameterMap);
        return getDataTable(list);
    }
    
    @PostMapping(value = "/add")
    @ApiOperation(value = "新增团建活动")
    public String addActivity(@RequestBody JSONObject obj, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = teambuildingActivityService.createActivity(obj, request);
        return returnStr(objectMap, result);
    }
    
    @PostMapping(value = "/confirm/{id}")
    @ApiOperation(value = "确认团建活动")
    public String confirmActivity(@PathVariable Long id, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        int result = teambuildingActivityService.confirmActivity(id, request);
        return returnStr(objectMap, result);
    }
}
```

#### 5.1.2 前端实现

**列表页面 (TeambuildingActivityList.vue)**:
```vue
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :bordered="false">
        <!-- 查询区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline" @keyup.enter.native="searchQuery">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="活动名称">
                  <a-input v-model="queryParam.activityName" placeholder="请输入活动名称"/>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="活动状态">
                  <a-select v-model="queryParam.status" placeholder="请选择状态" allowClear>
                    <a-select-option value="pending">待定</a-select-option>
                    <a-select-option value="confirmed">确认</a-select-option>
                    <a-select-option value="completed">完成</a-select-option>
                    <a-select-option value="cancelled">取消</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item label="活动日期">
                  <a-range-picker v-model="queryParam.dateRange" style="width: 100%" />
                </a-form-item>
              </a-col>
              <a-col :md="4" :sm="24">
                <span class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                  <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                </span>
              </a-col>
            </a-row>
          </a-form>
        </div>

        <!-- 操作按钮 -->
        <div class="table-operator">
          <a-button v-has="'teambuilding:add'" type="primary" icon="plus" @click="handleAdd">新增活动</a-button>
          <a-button v-has="'teambuilding:confirm'" type="primary" icon="check" 
                    :disabled="!hasSelected" @click="handleConfirm">确认活动</a-button>
          <a-button v-has="'teambuilding:delete'" type="danger" icon="delete" 
                    :disabled="!hasSelected" @click="handleDelete">删除</a-button>
        </div>

        <!-- 数据表格 -->
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="id"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          :rowSelection="rowSelection"
          @change="handleTableChange">
          
          <template slot="statusSlot" slot-scope="text">
            <a-tag :color="getStatusColor(text)">{{ getStatusText(text) }}</a-tag>
          </template>
          
          <template slot="actionSlot" slot-scope="text, record">
            <a v-has="'teambuilding:view'" @click="handleView(record)">查看</a>
            <a-divider type="vertical" />
            <a v-has="'teambuilding:edit'" @click="handleEdit(record)">编辑</a>
            <a-divider type="vertical" />
            <a v-has="'teambuilding:confirm'" @click="handleConfirm(record)" 
               v-if="record.status === 'pending'">确认</a>
          </template>
        </a-table>
      </a-card>
    </a-col>
  </a-row>

  <!-- 弹窗表单 -->
  <teambuilding-activity-modal ref="modalForm" @ok="modalFormOk"/>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getAction, deleteAction, postAction } from '@/api/manage'
import TeambuildingActivityModal from './modules/TeambuildingActivityModal'

export default {
  name: "TeambuildingActivityList",
  mixins: [JeecgListMixin],
  components: {
    TeambuildingActivityModal
  },
  data() {
    return {
      // 查询参数
      queryParam: {},
      
      // 表格列定义
      columns: [
        {title: '活动编号', align: "center", dataIndex: 'activityNo', width: 120},
        {title: '活动名称', align: "center", dataIndex: 'activityName', width: 200},
        {title: '客户名称', align: "center", dataIndex: 'customerName', width: 150},
        {title: '活动日期', align: "center", dataIndex: 'activityDate', width: 120},
        {title: '预期人数', align: "center", dataIndex: 'expectedParticipants', width: 100},
        {title: '预算金额', align: "center", dataIndex: 'budgetAmount', width: 120},
        {title: '状态', align: "center", dataIndex: 'status', width: 100, scopedSlots: {customRender: 'statusSlot'}},
        {title: '操作', dataIndex: 'action', align: "center", width: 180, scopedSlots: {customRender: 'actionSlot'}}
      ],
      
      // API配置
      url: {
        list: "/teambuilding/list",
        delete: "/teambuilding/delete",
        deleteBatch: "/teambuilding/deleteBatch"
      }
    }
  },
  
  methods: {
    // 获取状态颜色
    getStatusColor(status) {
      const colorMap = {
        'pending': 'orange',
        'confirmed': 'blue',
        'completed': 'green',
        'cancelled': 'red'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取状态文本
    getStatusText(status) {
      const textMap = {
        'pending': '待定',
        'confirmed': '确认',
        'completed': '完成',
        'cancelled': '取消'
      }
      return textMap[status] || status
    },
    
    // 新增活动
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = "新增团建活动"
    },
    
    // 编辑活动
    handleEdit(record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = "编辑团建活动"
    },
    
    // 查看活动
    handleView(record) {
      this.$refs.modalForm.view(record)
      this.$refs.modalForm.title = "查看团建活动"
    },
    
    // 确认活动
    handleConfirm(record) {
      const that = this
      this.$confirm({
        title: '确认操作',
        content: '确定要确认这个团建活动吗？',
        onOk() {
          return postAction(`/teambuilding/confirm/${record.id}`).then(res => {
            if (res.success) {
              that.$message.success('确认成功')
              that.loadData()
            } else {
              that.$message.error('确认失败：' + res.message)
            }
          })
        }
      })
    },
    
    // 弹窗确定回调
    modalFormOk() {
      this.loadData()
    }
  }
}
</script>
```

**表单弹窗 (TeambuildingActivityModal.vue)**:
```vue
<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    wrapClassName="ant-modal-cust-warp"
    style="top:20px;">
    
    <a-form :form="form" layout="horizontal">
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="活动名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input v-decorator="['activityName', validatorRules.activityName]" 
                     placeholder="请输入活动名称" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="12">
          <a-form-item label="客户选择" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-select-depart v-decorator="['customerId', validatorRules.customerId]"
                            dict="/supplier/list" :disabled="disabled"/>
          </a-form-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="24">
        <a-col :span="12">
          <a-form-item label="活动日期" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-date-picker v-decorator="['activityDate', validatorRules.activityDate]" 
                          placeholder="请选择活动日期" style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="开始时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-time-picker v-decorator="['startTime']" placeholder="开始时间" 
                          style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="结束时间" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-time-picker v-decorator="['endTime']" placeholder="结束时间" 
                          style="width: 100%" :disabled="disabled"/>
          </a-form-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="24">
        <a-col :span="8">
          <a-form-item label="讲师" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-select-user v-decorator="['instructorId']" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="助理" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <j-select-user v-decorator="['assistantId']" :disabled="disabled"/>
          </a-form-item>
        </a-col>
        <a-col :span="8">
          <a-form-item label="预期人数" :labelCol="labelCol" :wrapperCol="wrapperCol">
            <a-input-number v-decorator="['expectedParticipants']" placeholder="预期人数" 
                           style="width: 100%" :min="1" :disabled="disabled"/>
          </a-form-item>
        </a-col>
      </a-row>
      
      <a-row :gutter="24">
        <a-col :span="24">
          <a-form-item label="活动内容" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
            <a-textarea v-decorator="['activityContent']" placeholder="请输入活动内容" 
                       :rows="4" :disabled="disabled"/>
          </a-form-item>
        </a-col>
      </a-row>
      
      <!-- 参与人员表格 -->
      <a-row :gutter="24">
        <a-col :span="24">
          <a-form-item label="参与人员" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
            <j-editable-table
              ref="participantTable"
              :rowNumber="true"
              :dragSort="true"
              :columns="participantColumns"
              v-model="participantList"
              :maxHeight="300"
              :disabled="disabled">
            </j-editable-table>
          </a-form-item>
        </a-col>
      </a-row>
      
      <!-- 费用预算表格 -->
      <a-row :gutter="24">
        <a-col :span="24">
          <a-form-item label="费用预算" :labelCol="{span: 3}" :wrapperCol="{span: 20}">
            <j-editable-table
              ref="expenseTable"
              :rowNumber="true"
              :dragSort="true"
              :columns="expenseColumns"
              v-model="expenseList"
              :maxHeight="300"
              :disabled="disabled">
            </j-editable-table>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
  </a-modal>
</template>

<script>
import { getAction, putAction, postAction } from '@/api/manage'
import { JEditableTableUtil } from '@/utils/JEditableTableUtil'

export default {
  name: "TeambuildingActivityModal",
  
  data() {
    return {
      title: "",
      visible: false,
      disabled: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      labelCol: { span: 8 },
      wrapperCol: { span: 14 },
      
      // 表单验证规则
      validatorRules: {
        activityName: {rules: [{required: true, message: '请输入活动名称!'}]},
        customerId: {rules: [{required: true, message: '请选择客户!'}]},
        activityDate: {rules: [{required: true, message: '请选择活动日期!'}]}
      },
      
      // 参与人员表格配置
      participantColumns: [
        {
          title: '姓名',
          key: 'participantName',
          type: JEditableTableUtil.cellType.input,
          width: '15%',
          placeholder: '请输入姓名',
          validateRules: [{required: true, message: '姓名不能为空'}]
        },
        {
          title: '联系电话',
          key: 'participantPhone',
          type: JEditableTableUtil.cellType.input,
          width: '15%',
          placeholder: '请输入联系电话'
        },
        {
          title: '所属公司',
          key: 'participantCompany',
          type: JEditableTableUtil.cellType.input,
          width: '20%',
          placeholder: '请输入所属公司'
        },
        {
          title: '职位',
          key: 'participantPosition',
          type: JEditableTableUtil.cellType.input,
          width: '15%',
          placeholder: '请输入职位'
        },
        {
          title: '特殊需求',
          key: 'specialNeeds',
          type: JEditableTableUtil.cellType.textarea,
          width: '25%',
          placeholder: '请输入特殊需求'
        },
        {
          title: '出席状态',
          key: 'attendanceStatus',
          type: JEditableTableUtil.cellType.select,
          width: '10%',
          options: [
            {title: '已报名', value: 'registered'},
            {title: '已确认', value: 'confirmed'},
            {title: '已出席', value: 'attended'},
            {title: '缺席', value: 'absent'}
          ],
          defaultValue: 'registered'
        }
      ],
      participantList: [],
      
      // 费用预算表格配置
      expenseColumns: [
        {
          title: '费用类型',
          key: 'expenseType',
          type: JEditableTableUtil.cellType.select,
          width: '20%',
          options: [
            {title: '场地费', value: 'venue'},
            {title: '材料费', value: 'material'},
            {title: '讲师费', value: 'instructor'},
            {title: '助理费', value: 'assistant'},
            {title: '其他', value: 'other'}
          ],
          validateRules: [{required: true, message: '请选择费用类型'}]
        },
        {
          title: '费用名称',
          key: 'expenseName',
          type: JEditableTableUtil.cellType.input,
          width: '25%',
          placeholder: '请输入费用名称',
          validateRules: [{required: true, message: '费用名称不能为空'}]
        },
        {
          title: '费用金额',
          key: 'expenseAmount',
          type: JEditableTableUtil.cellType.inputNumber,
          width: '20%',
          placeholder: '请输入费用金额',
          validateRules: [{required: true, message: '费用金额不能为空'}]
        },
        {
          title: '费用说明',
          key: 'expenseDescription',
          type: JEditableTableUtil.cellType.textarea,
          width: '35%',
          placeholder: '请输入费用说明'
        }
      ],
      expenseList: [],
      
      url: {
        add: "/teambuilding/add",
        edit: "/teambuilding/update"
      }
    }
  },
  
  methods: {
    // 新增
    add() {
      this.reset()
      this.visible = true
      this.disabled = false
    },
    
    // 编辑
    edit(record) {
      this.reset()
      this.visible = true
      this.disabled = false
      
      // 加载数据
      this.loadActivityDetail(record.id)
    },
    
    // 查看
    view(record) {
      this.reset()
      this.visible = true
      this.disabled = true
      
      // 加载数据
      this.loadActivityDetail(record.id)
    },
    
    // 重置表单
    reset() {
      this.form.resetFields()
      this.participantList = []
      this.expenseList = []
    },
    
    // 加载活动详情
    loadActivityDetail(activityId) {
      getAction(`/teambuilding/detail/${activityId}`).then(res => {
        if (res.success) {
          const data = res.result
          this.$nextTick(() => {
            this.form.setFieldsValue(data)
            this.participantList = data.participants || []
            this.expenseList = data.expenses || []
          })
        }
      })
    },
    
    // 确定
    handleOk() {
      this.form.validateFields((err, values) => {
        if (!err) {
          this.confirmLoading = true
          
          // 构造提交数据
          const formData = Object.assign({}, values)
          formData.participants = this.participantList
          formData.expenses = this.expenseList
          
          const url = formData.id ? this.url.edit : this.url.add
          const httpAction = formData.id ? putAction : postAction
          
          httpAction(url, formData).then(res => {
            if (res.success) {
              this.$message.success('操作成功')
              this.visible = false
              this.$emit('ok')
            } else {
              this.$message.error('操作失败：' + res.message)
            }
          }).finally(() => {
            this.confirmLoading = false
          })
        }
      })
    },
    
    // 取消
    handleCancel() {
      this.visible = false
    }
  }
}
</script>
```

### 5.2 薪酬管理模块

#### 5.2.1 后端实现

**实体类设计**:
```java
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsh_salary_record")
public class SalaryRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String salaryNo;
    private Long userId;
    private String salaryMonth;
    private BigDecimal baseSalary;
    private BigDecimal productionSalary;
    private BigDecimal salesCommission;
    private BigDecimal teambuildingCommission;
    private BigDecimal coffeeShopCommission;
    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal deduction;
    private BigDecimal socialInsurance;
    private BigDecimal housingFund;
    private BigDecimal tax;
    private BigDecimal totalSalary;
    private BigDecimal netSalary;
    private String paymentStatus;
    private LocalDate paymentDate;
    private String remark;
    
    // 多租户和审计字段
    private Long tenantId;
    private String deleteFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createBy;
    private Long updateBy;
}
```

**薪酬计算服务**:
```java
@Service
public class SalaryCalculationService {
    
    @Resource
    private SalaryRecordMapper salaryRecordMapper;
    
    @Resource
    private DepotHeadService depotHeadService; // 复用单据服务
    
    @Resource
    private TeambuildingActivityService teambuildingActivityService;
    
    /**
     * 自动计算月薪酬
     */
    @Transactional(rollbackFor = Exception.class)
    public int calculateMonthlySalary(String salaryMonth, HttpServletRequest request) throws Exception {
        try {
            // 获取所有员工
            List<User> users = userService.getAllActiveUsers();
            
            for (User user : users) {
                SalaryRecord salaryRecord = new SalaryRecord();
                salaryRecord.setSalaryNo(generateSalaryNo(salaryMonth, user.getId()));
                salaryRecord.setUserId(user.getId());
                salaryRecord.setSalaryMonth(salaryMonth);
                
                // 计算基本工资
                BigDecimal baseSalary = calculateBaseSalary(user);
                salaryRecord.setBaseSalary(baseSalary);
                
                // 计算生产工资
                BigDecimal productionSalary = calculateProductionSalary(user.getId(), salaryMonth);
                salaryRecord.setProductionSalary(productionSalary);
                
                // 计算销售提成
                BigDecimal salesCommission = calculateSalesCommission(user.getId(), salaryMonth);
                salaryRecord.setSalesCommission(salesCommission);
                
                // 计算团建提成
                BigDecimal teambuildingCommission = calculateTeambuildingCommission(user.getId(), salaryMonth);
                salaryRecord.setTeambuildingCommission(teambuildingCommission);
                
                // 计算咖啡店提成
                BigDecimal coffeeShopCommission = calculateCoffeeShopCommission(user.getId(), salaryMonth);
                salaryRecord.setCoffeeShopCommission(coffeeShopCommission);
                
                // 计算总额
                BigDecimal totalSalary = baseSalary.add(productionSalary)
                    .add(salesCommission).add(teambuildingCommission)
                    .add(coffeeShopCommission);
                salaryRecord.setTotalSalary(totalSalary);
                
                // 计算扣除项
                BigDecimal socialInsurance = calculateSocialInsurance(totalSalary);
                BigDecimal tax = calculateTax(totalSalary.subtract(socialInsurance));
                salaryRecord.setSocialInsurance(socialInsurance);
                salaryRecord.setTax(tax);
                
                // 计算实发工资
                BigDecimal netSalary = totalSalary.subtract(socialInsurance).subtract(tax);
                salaryRecord.setNetSalary(netSalary);
                
                salaryRecord.setPaymentStatus("pending");
                
                // 保存薪酬记录
                salaryRecordMapper.insert(salaryRecord);
                
                // 保存薪酬明细
                saveSalaryDetails(salaryRecord.getId(), user.getId(), salaryMonth);
            }
            
            // 记录日志
            logService.insertLog("薪酬管理", 
                "生成" + salaryMonth + "月薪酬记录", request);
            
            return 1;
        } catch (Exception e) {
            logger.error("计算月薪酬失败", e);
            throw e;
        }
    }
    
    /**
     * 计算生产工资（基于生产单据）
     */
    private BigDecimal calculateProductionSalary(Long userId, String salaryMonth) {
        // 查询该月该员工相关的生产单据
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("salaryMonth", salaryMonth);
        params.put("billType", "生产入库"); // 复用现有单据类型
        
        List<DepotHead> productionBills = depotHeadService.getProductionBillsByUser(params);
        
        BigDecimal totalProductionSalary = BigDecimal.ZERO;
        for (DepotHead bill : productionBills) {
            // 根据不同产品类型计算不同的工资标准
            BigDecimal billSalary = calculateBillSalary(bill);
            totalProductionSalary = totalProductionSalary.add(billSalary);
        }
        
        return totalProductionSalary;
    }
    
    /**
     * 计算团建提成（基于团建活动）
     */
    private BigDecimal calculateTeambuildingCommission(Long userId, String salaryMonth) {
        // 查询该月该员工参与的团建活动
        List<TeambuildingActivity> activities = teambuildingActivityService
            .getActivitiesByUserAndMonth(userId, salaryMonth);
        
        BigDecimal totalCommission = BigDecimal.ZERO;
        for (TeambuildingActivity activity : activities) {
            // 根据角色计算不同的提成比例
            BigDecimal activityCommission = calculateActivityCommission(activity, userId);
            totalCommission = totalCommission.add(activityCommission);
        }
        
        return totalCommission;
    }
    
    /**
     * 发放薪酬
     */
    @Transactional(rollbackFor = Exception.class)
    public int paySalary(Long salaryRecordId, HttpServletRequest request) throws Exception {
        try {
            SalaryRecord salaryRecord = salaryRecordMapper.selectById(salaryRecordId);
            if (salaryRecord == null) {
                throw new BusinessRunTimeException("薪酬记录不存在");
            }
            
            // 更新发放状态
            salaryRecord.setPaymentStatus("paid");
            salaryRecord.setPaymentDate(LocalDate.now());
            salaryRecordMapper.updateById(salaryRecord);
            
            // 生成财务支出单（复用现有财务模块）
            generateFinancialExpense(salaryRecord);
            
            // 记录日志
            User user = userService.getUser(salaryRecord.getUserId());
            logService.insertLog("薪酬管理", 
                "发放薪酬：" + user.getUsername() + "-" + salaryRecord.getSalaryMonth(), request);
            
            return 1;
        } catch (Exception e) {
            logger.error("发放薪酬失败", e);
            throw e;
        }
    }
}
```

#### 5.2.2 前端实现

**薪酬列表页面**:
```vue
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :bordered="false">
        <!-- 查询和操作区域 -->
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="24">
              <a-col :md="6" :sm="24">
                <a-form-item label="薪酬月份">
                  <a-month-picker v-model="queryParam.salaryMonth" placeholder="请选择月份" />
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="员工姓名">
                  <a-input v-model="queryParam.userName" placeholder="请输入员工姓名"/>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <a-form-item label="发放状态">
                  <a-select v-model="queryParam.paymentStatus" placeholder="请选择状态" allowClear>
                    <a-select-option value="pending">待发放</a-select-option>
                    <a-select-option value="paid">已发放</a-select-option>
                    <a-select-option value="cancelled">已取消</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :md="6" :sm="24">
                <span class="table-page-search-submitButtons">
                  <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                  <a-button @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
                </span>
              </a-col>
            </a-row>
          </a-form>
        </div>

        <!-- 操作按钮 -->
        <div class="table-operator">
          <a-button v-has="'salary:calculate'" type="primary" icon="calculator" 
                    @click="handleCalculate">计算薪酬</a-button>
          <a-button v-has="'salary:pay'" type="primary" icon="money-collect" 
                    :disabled="!hasSelected" @click="handlePay">批量发放</a-button>
          <a-button v-has="'salary:export'" type="default" icon="export" 
                    @click="handleExport">导出薪资条</a-button>
        </div>

        <!-- 数据表格 -->
        <a-table
          ref="table"
          size="middle"
          bordered
          rowKey="id"
          :columns="columns"
          :dataSource="dataSource"
          :pagination="ipagination"
          :loading="loading"
          :rowSelection="rowSelection"
          @change="handleTableChange">
          
          <template slot="paymentStatusSlot" slot-scope="text">
            <a-tag :color="getPaymentStatusColor(text)">{{ getPaymentStatusText(text) }}</a-tag>
          </template>
          
          <template slot="actionSlot" slot-scope="text, record">
            <a @click="handleViewDetail(record)">查看明细</a>
            <a-divider type="vertical" />
            <a @click="handlePrintPaySlip(record)">打印薪资条</a>
            <a-divider type="vertical" />
            <a v-if="record.paymentStatus === 'pending'" @click="handlePay(record)">发放</a>
          </template>
        </a-table>
      </a-card>
    </a-col>
  </a-row>

  <!-- 计算薪酬弹窗 -->
  <a-modal
    title="计算薪酬"
    :visible="calculateModalVisible"
    @ok="handleCalculateConfirm"
    @cancel="calculateModalVisible = false">
    <a-form>
      <a-form-item label="薪酬月份">
        <a-month-picker v-model="calculateForm.salaryMonth" placeholder="请选择要计算的月份" style="width: 100%" />
      </a-form-item>
      <a-alert message="将为所有员工计算指定月份的薪酬，如果该月份已有薪酬记录将被覆盖" type="warning" />
    </a-form>
  </a-modal>

  <!-- 薪酬明细弹窗 -->
  <salary-detail-modal ref="salaryDetailModal" />
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import { getAction, postAction } from '@/api/manage'
import SalaryDetailModal from './modules/SalaryDetailModal'
import moment from 'moment'

export default {
  name: "SalaryRecordList",
  mixins: [JeecgListMixin],
  components: {
    SalaryDetailModal
  },
  
  data() {
    return {
      // 查询参数
      queryParam: {},
      
      // 表格列定义
      columns: [
        {title: '薪酬单号', align: "center", dataIndex: 'salaryNo', width: 150},
        {title: '员工姓名', align: "center", dataIndex: 'userName', width: 120},
        {title: '薪酬月份', align: "center", dataIndex: 'salaryMonth', width: 120},
        {title: '基本工资', align: "center", dataIndex: 'baseSalary', width: 100, customRender: (text) => '¥' + text},
        {title: '生产工资', align: "center", dataIndex: 'productionSalary', width: 100, customRender: (text) => '¥' + text},
        {title: '销售提成', align: "center", dataIndex: 'salesCommission', width: 100, customRender: (text) => '¥' + text},
        {title: '团建提成', align: "center", dataIndex: 'teambuildingCommission', width: 100, customRender: (text) => '¥' + text},
        {title: '应发工资', align: "center", dataIndex: 'totalSalary', width: 120, customRender: (text) => '¥' + text},
        {title: '实发工资', align: "center", dataIndex: 'netSalary', width: 120, customRender: (text) => '¥' + text},
        {title: '发放状态', align: "center", dataIndex: 'paymentStatus', width: 100, scopedSlots: {customRender: 'paymentStatusSlot'}},
        {title: '发放日期', align: "center", dataIndex: 'paymentDate', width: 120},
        {title: '操作', dataIndex: 'action', align: "center", width: 200, scopedSlots: {customRender: 'actionSlot'}}
      ],
      
      // 计算薪酬弹窗
      calculateModalVisible: false,
      calculateForm: {},
      
      // API配置
      url: {
        list: "/salary/list",
        calculate: "/salary/calculate",
        pay: "/salary/pay",
        export: "/salary/export"
      }
    }
  },
  
  methods: {
    // 获取发放状态颜色
    getPaymentStatusColor(status) {
      const colorMap = {
        'pending': 'orange',
        'paid': 'green',
        'cancelled': 'red'
      }
      return colorMap[status] || 'default'
    },
    
    // 获取发放状态文本
    getPaymentStatusText(status) {
      const textMap = {
        'pending': '待发放',
        'paid': '已发放',
        'cancelled': '已取消'
      }
      return textMap[status] || status
    },
    
    // 计算薪酬
    handleCalculate() {
      this.calculateModalVisible = true
      this.calculateForm = {
        salaryMonth: moment().subtract(1, 'month') // 默认上个月
      }
    },
    
    // 确认计算薪酬
    handleCalculateConfirm() {
      if (!this.calculateForm.salaryMonth) {
        this.$message.error('请选择薪酬月份')
        return
      }
      
      const salaryMonth = this.calculateForm.salaryMonth.format('YYYY-MM')
      
      this.$confirm({
        title: '确认计算',
        content: `确定要计算 ${salaryMonth} 月的薪酬吗？`,
        onOk: () => {
          return postAction(this.url.calculate, { salaryMonth }).then(res => {
            if (res.success) {
              this.$message.success('薪酬计算完成')
              this.calculateModalVisible = false
              this.loadData()
            } else {
              this.$message.error('计算失败：' + res.message)
            }
          })
        }
      })
    },
    
    // 发放薪酬
    handlePay(record) {
      const records = record ? [record] : this.selectionRows
      const salaryIds = records.map(r => r.id)
      
      this.$confirm({
        title: '确认发放',
        content: `确定要发放选中的 ${records.length} 条薪酬记录吗？`,
        onOk: () => {
          return postAction(this.url.pay, { salaryIds }).then(res => {
            if (res.success) {
              this.$message.success('发放成功')
              this.loadData()
            } else {
              this.$message.error('发放失败：' + res.message)
            }
          })
        }
      })
    },
    
    // 查看薪酬明细
    handleViewDetail(record) {
      this.$refs.salaryDetailModal.show(record.id)
    },
    
    // 打印薪资条
    handlePrintPaySlip(record) {
      const printUrl = `/salary/printPaySlip/${record.id}`
      window.open(printUrl, '_blank')
    },
    
    // 导出薪资条
    handleExport() {
      const params = this.getQueryParams()
      const exportUrl = this.url.export + '?' + Object.keys(params).map(key => `${key}=${params[key]}`).join('&')
      window.open(exportUrl, '_blank')
    }
  }
}
</script>
```

### 5.3 排班管理模块

#### 5.3.1 后端实现

**排班服务**:
```java
@Service
public class ScheduleService {
    
    @Resource
    private ScheduleRecordMapper scheduleRecordMapper;
    
    @Resource
    private UserService userService;
    
    /**
     * 批量创建排班
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchCreateSchedule(JSONObject obj, HttpServletRequest request) throws Exception {
        try {
            List<String> dateRange = obj.getObject("dateRange", List.class);
            List<Long> userIds = obj.getObject("userIds", List.class);
            String shiftType = obj.getString("shiftType");
            String startTime = obj.getString("startTime");
            String endTime = obj.getString("endTime");
            String workplace = obj.getString("workplace");
            
            LocalDate startDate = LocalDate.parse(dateRange.get(0));
            LocalDate endDate = LocalDate.parse(dateRange.get(1));
            
            List<ScheduleRecord> scheduleList = new ArrayList<>();
            
            // 遍历日期范围
            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                for (Long userId : userIds) {
                    ScheduleRecord schedule = new ScheduleRecord();
                    schedule.setScheduleDate(date);
                    schedule.setUserId(userId);
                    schedule.setShiftType(shiftType);
                    schedule.setStartTime(LocalTime.parse(startTime));
                    schedule.setEndTime(LocalTime.parse(endTime));
                    schedule.setWorkplace(workplace);
                    schedule.setStatus("scheduled");
                    
                    scheduleList.add(schedule);
                }
            }
            
            // 批量插入
            scheduleRecordMapper.batchInsert(scheduleList);
            
            // 记录日志
            logService.insertLog("排班管理", 
                "批量创建排班：" + dateRange.get(0) + " 至 " + dateRange.get(1), request);
            
            return scheduleList.size();
        } catch (Exception e) {
            logger.error("批量创建排班失败", e);
            throw e;
        }
    }
    
    /**
     * 获取日历视图数据
     */
    public List<ScheduleCalendarVo> getCalendarData(String startDate, String endDate) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            
            List<ScheduleRecord> scheduleList = scheduleRecordMapper.selectByDateRange(params);
            List<ScheduleCalendarVo> calendarData = new ArrayList<>();
            
            for (ScheduleRecord schedule : scheduleList) {
                ScheduleCalendarVo calendarVo = new ScheduleCalendarVo();
                calendarVo.setId(schedule.getId());
                calendarVo.setTitle(getUserDisplayName(schedule.getUserId()) + " - " + getShiftTypeName(schedule.getShiftType()));
                calendarVo.setStart(schedule.getScheduleDate().toString() + "T" + schedule.getStartTime().toString());
                calendarVo.setEnd(schedule.getScheduleDate().toString() + "T" + schedule.getEndTime().toString());
                calendarVo.setColor(getShiftColor(schedule.getShiftType()));
                calendarVo.setExtendedProps(schedule);
                
                calendarData.add(calendarVo);
            }
            
            return calendarData;
        } catch (Exception e) {
            logger.error("获取日历数据失败", e);
            throw e;
        }
    }
    
    /**
     * 确认排班
     */
    @Transactional(rollbackFor = Exception.class)
    public int confirmSchedule(Long scheduleId, HttpServletRequest request) throws Exception {
        try {
            ScheduleRecord schedule = scheduleRecordMapper.selectById(scheduleId);
            if (schedule == null) {
                throw new BusinessRunTimeException("排班记录不存在");
            }
            
            schedule.setStatus("confirmed");
            scheduleRecordMapper.updateById(schedule);
            
            // 记录日志
            User user = userService.getUser(schedule.getUserId());
            logService.insertLog("排班管理", 
                "确认排班：" + user.getUsername() + " - " + schedule.getScheduleDate(), request);
            
            return 1;
        } catch (Exception e) {
            logger.error("确认排班失败", e);
            throw e;
        }
    }
}
```

#### 5.3.2 前端实现

**排班日历页面**:
```vue
<template>
  <a-row :gutter="24">
    <a-col :md="24">
      <a-card :bordered="false">
        <!-- 操作区域 -->
        <div class="table-operator" style="margin-bottom: 16px">
          <a-button v-has="'schedule:add'" type="primary" icon="plus" @click="handleAdd">新增排班</a-button>
          <a-button type="default" icon="reload" @click="refreshCalendar">刷新</a-button>
          <a-button type="default" icon="export" @click="handleExport">导出排班表</a-button>
          
          <div style="float: right">
            <a-radio-group v-model="calendarView" @change="handleViewChange">
              <a-radio-button value="dayGridMonth">月视图</a-radio-button>
              <a-radio-button value="timeGridWeek">周视图</a-radio-button>
              <a-radio-button value="timeGridDay">日视图</a-radio-button>
            </a-radio-group>
          </div>
        </div>

        <!-- 日历组件 -->
        <div id="calendar-container">
          <full-calendar 
            ref="fullCalendar"
            :options="calendarOptions"
            class="schedule-calendar" />
        </div>
      </a-card>
    </a-col>
  </a-row>

  <!-- 新增/编辑排班弹窗 -->
  <schedule-modal ref="scheduleModal" @ok="handleModalOk" />
  
  <!-- 排班详情弹窗 -->
  <schedule-detail-modal ref="scheduleDetailModal" @ok="handleModalOk" />
</template>

<script>
import FullCalendar from '@fullcalendar/vue'
import dayGridPlugin from '@fullcalendar/daygrid'
import timeGridPlugin from '@fullcalendar/timegrid'
import interactionPlugin from '@fullcalendar/interaction'
import { getAction } from '@/api/manage'
import ScheduleModal from './modules/ScheduleModal'
import ScheduleDetailModal from './modules/ScheduleDetailModal'

export default {
  name: "ScheduleCalendar",
  components: {
    FullCalendar,
    ScheduleModal,
    ScheduleDetailModal
  },
  
  data() {
    return {
      calendarView: 'dayGridMonth',
      
      calendarOptions: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        initialView: 'dayGridMonth',
        locale: 'zh-cn',
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: ''
        },
        buttonText: {
          today: '今天',
          month: '月',
          week: '周',
          day: '日'
        },
        height: 'auto',
        editable: true,
        selectable: true,
        selectMirror: true,
        dayMaxEvents: true,
        weekends: true,
        
        // 事件数据源
        events: this.loadEvents,
        
        // 事件处理
        select: this.handleDateSelect,
        eventClick: this.handleEventClick,
        eventDrop: this.handleEventDrop,
        eventResize: this.handleEventResize,
        
        // 自定义渲染
        eventContent: this.renderEventContent
      }
    }
  },
  
  mounted() {
    this.refreshCalendar()
  },
  
  methods: {
    // 加载事件数据
    loadEvents(info, successCallback, failureCallback) {
      const params = {
        startDate: info.startStr,
        endDate: info.endStr
      }
      
      getAction('/schedule/calendar', params).then(res => {
        if (res.success) {
          successCallback(res.result)
        } else {
          failureCallback(res.message)
        }
      }).catch(error => {
        failureCallback(error.message)
      })
    },
    
    // 处理日期选择
    handleDateSelect(selectInfo) {
      this.$refs.scheduleModal.add(selectInfo.startStr, selectInfo.endStr)
      
      // 清除选择
      const calendarApi = this.$refs.fullCalendar.getApi()
      calendarApi.unselect()
    },
    
    // 处理事件点击
    handleEventClick(clickInfo) {
      const scheduleData = clickInfo.event.extendedProps
      this.$refs.scheduleDetailModal.show(scheduleData)
    },
    
    // 处理事件拖拽
    handleEventDrop(dropInfo) {
      const scheduleId = dropInfo.event.id
      const newDate = dropInfo.event.startStr.split('T')[0]
      
      this.$confirm({
        title: '确认修改',
        content: '确定要将排班调整到新的日期吗？',
        onOk: () => {
          return this.updateScheduleDate(scheduleId, newDate)
        },
        onCancel: () => {
          dropInfo.revert()
        }
      })
    },
    
    // 处理事件大小调整
    handleEventResize(resizeInfo) {
      const scheduleId = resizeInfo.event.id
      const startTime = resizeInfo.event.startStr.split('T')[1]
      const endTime = resizeInfo.event.endStr.split('T')[1]
      
      this.updateScheduleTime(scheduleId, startTime, endTime)
    },
    
    // 自定义事件渲染
    renderEventContent(eventInfo) {
      const schedule = eventInfo.event.extendedProps
      const statusIcon = this.getStatusIcon(schedule.status)
      
      return {
        html: `
          <div class="schedule-event">
            <div class="schedule-title">
              ${statusIcon} ${eventInfo.event.title}
            </div>
            <div class="schedule-time">
              ${eventInfo.event.startStr.split('T')[1]} - ${eventInfo.event.endStr.split('T')[1]}
            </div>
          </div>
        `
      }
    },
    
    // 获取状态图标
    getStatusIcon(status) {
      const iconMap = {
        'scheduled': '📅',
        'confirmed': '✅',
        'completed': '🏁',
        'cancelled': '❌'
      }
      return iconMap[status] || '📅'
    },
    
    // 新增排班
    handleAdd() {
      this.$refs.scheduleModal.add()
    },
    
    // 刷新日历
    refreshCalendar() {
      const calendarApi = this.$refs.fullCalendar.getApi()
      calendarApi.refetchEvents()
    },
    
    // 切换视图
    handleViewChange(e) {
      const calendarApi = this.$refs.fullCalendar.getApi()
      calendarApi.changeView(e.target.value)
    },
    
    // 导出排班表
    handleExport() {
      const calendarApi = this.$refs.fullCalendar.getApi()
      const currentDate = calendarApi.getDate()
      
      const exportUrl = `/schedule/export?month=${currentDate.getFullYear()}-${(currentDate.getMonth() + 1).toString().padStart(2, '0')}`
      window.open(exportUrl, '_blank')
    },
    
    // 更新排班日期
    updateScheduleDate(scheduleId, newDate) {
      return postAction('/schedule/updateDate', {
        scheduleId,
        newDate
      }).then(res => {
        if (res.success) {
          this.$message.success('排班调整成功')
          this.refreshCalendar()
        } else {
          this.$message.error('调整失败：' + res.message)
        }
      })
    },
    
    // 更新排班时间
    updateScheduleTime(scheduleId, startTime, endTime) {
      return postAction('/schedule/updateTime', {
        scheduleId,
        startTime,
        endTime
      }).then(res => {
        if (res.success) {
          this.$message.success('时间调整成功')
        } else {
          this.$message.error('调整失败：' + res.message)
        }
      })
    },
    
    // 弹窗确定回调
    handleModalOk() {
      this.refreshCalendar()
    }
  }
}
</script>

<style lang="less" scoped>
.schedule-calendar {
  .schedule-event {
    padding: 2px;
    font-size: 12px;
    
    .schedule-title {
      font-weight: bold;
      margin-bottom: 2px;
    }
    
    .schedule-time {
      color: #666;
      font-size: 11px;
    }
  }
  
  /deep/ .fc-event {
    border: none !important;
    border-radius: 4px !important;
    padding: 2px 4px !important;
  }
  
  /deep/ .fc-event-main {
    padding: 0 !important;
  }
}
</style>
```

---

## 6. 技术实现细节

### 6.1 代码规范和最佳实践

#### 6.1.1 后端开发规范

**Controller层规范**:
- 继承BaseController获得基础功能
- 使用统一的@Api和@ApiOperation注解
- 遵循RESTful接口设计原则
- 统一异常处理和响应格式

**Service层规范**:
- 重要方法添加@Transactional事务控制
- 使用try-catch处理异常并记录日志
- 复用现有Service而不是重复开发
- 业务逻辑清晰，方法职责单一

**Mapper层规范**:
- 基础Mapper使用MyBatis Generator生成
- 复杂查询在MapperEx中手动编写
- SQL语句使用动态SQL提高灵活性
- 合理使用索引优化查询性能

#### 6.1.2 前端开发规范

**组件规范**:
- 使用JeecgListMixin提供列表页基础功能
- 复用现有UI组件和样式
- 组件命名清晰，结构层次合理
- 统一的错误处理和用户提示

**API调用规范**:
- 使用统一的request.js封装HTTP请求
- 统一的错误处理和Loading状态
- 合理使用Vuex管理全局状态
- API接口命名清晰，参数验证完整

### 6.2 性能优化策略

#### 6.2.1 数据库优化

**索引优化**:
```sql
-- 团建活动表索引
CREATE INDEX idx_teambuilding_date_status ON jsh_teambuilding_activity(activity_date, status);
CREATE INDEX idx_teambuilding_customer ON jsh_teambuilding_activity(customer_id);

-- 薪酬记录表索引
CREATE INDEX idx_salary_month_user ON jsh_salary_record(salary_month, user_id);
CREATE INDEX idx_salary_status ON jsh_salary_record(payment_status);

-- 排班记录表索引
CREATE INDEX idx_schedule_date_user ON jsh_schedule_record(schedule_date, user_id);
CREATE INDEX idx_schedule_status ON jsh_schedule_record(status);
```

**查询优化**:
- 使用分页查询避免大数据量查询
- 合理使用JOIN避免N+1查询问题
- 使用EXISTS代替IN子查询
- 定期分析慢查询并优化

#### 6.2.2 缓存优化

**Redis缓存策略**:
```java
@Service
public class CacheService {
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    // 缓存用户信息
    @Cacheable(value = "user", key = "#userId")
    public User getUserFromCache(Long userId) {
        return userService.getUser(userId);
    }
    
    // 缓存团建活动统计
    @Cacheable(value = "teambuilding:stats", key = "#month")
    public TeambuildingStats getMonthlyStats(String month) {
        return teambuildingService.getMonthlyStats(month);
    }
    
    // 清除相关缓存
    @CacheEvict(value = "teambuilding:stats", allEntries = true)
    public void clearTeambuildingCache() {
        // 清除团建相关缓存
    }
}
```

### 6.3 安全保障措施

#### 6.3.1 数据安全

**多租户数据隔离**:
```java
// Service层自动注入租户过滤
@Aspect
@Component
public class TenantAspect {
    
    @Around("execution(* com.jsh.erp.service.*.*(..))")
    public Object addTenantFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        // 自动添加租户过滤条件
        Long tenantId = getCurrentTenantId();
        
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof Map) {
            Map<String, Object> params = (Map<String, Object>) args[0];
            params.put("tenantId", tenantId);
        }
        
        return joinPoint.proceed(args);
    }
}
```

**权限控制**:
```java
// 方法级权限控制
@PreAuthorize("hasPermission('teambuilding:list')")
public List<TeambuildingActivity> getTeambuildingList(Map<String, Object> params) {
    return teambuildingMapper.selectList(params);
}
```

#### 6.3.2 操作审计

**操作日志记录**:
```java
@Service
public class AuditService {
    
    public void recordOperation(String module, String operation, 
                              Object data, HttpServletRequest request) {
        AuditLog auditLog = new AuditLog();
        auditLog.setModule(module);
        auditLog.setOperation(operation);
        auditLog.setDataContent(JSON.toJSONString(data));
        auditLog.setOperatorId(getCurrentUserId(request));
        auditLog.setOperatorIp(getClientIpAddress(request));
        auditLog.setOperationTime(LocalDateTime.now());
        
        auditLogMapper.insert(auditLog);
    }
}
```

---

## 7. 部署和运维方案

### 7.1 环境配置

#### 7.1.1 开发环境配置

**application-dev.properties**:
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/jsh_erp?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=123456

# Redis配置
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0

# 聆花文化特定配置
linghua.tenant.id=1001
linghua.teambuilding.default.venue=聆花掐丝珐琅馆
linghua.salary.calculation.day=25
```

#### 7.1.2 生产环境配置

**application-prod.properties**:
```properties
# 数据库配置（生产环境）
spring.datasource.url=jdbc:mysql://prod-mysql:3306/jsh_erp?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20

# Redis配置（生产环境）
spring.redis.host=prod-redis
spring.redis.port=6379
spring.redis.password=${REDIS_PASSWORD}
spring.redis.database=0

# 日志配置
logging.level.com.jsh.erp=INFO
logging.file.path=/opt/jshERP/logs

# 安全配置
jwt.secret=${JWT_SECRET}
jwt.expiration=86400
```

### 7.2 Docker部署配置

#### 7.2.1 Docker Compose配置

**docker-compose.prod.yml**:
```yaml
version: '3.8'

services:
  # MySQL数据库
  mysql:
    image: mysql:5.7.33
    container_name: linghua-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: jsh_erp
      MYSQL_USER: ${MYSQL_USER}
      MYSQL_PASSWORD: ${MYSQL_PASSWORD}
    volumes:
      - ./volumes/mysql:/var/lib/mysql
      - ./scripts/sql:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"
    command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

  # Redis缓存
  redis:
    image: redis:6.2.1-alpine
    container_name: linghua-redis
    restart: always
    command: redis-server --requirepass ${REDIS_PASSWORD}
    volumes:
      - ./volumes/redis:/data
    ports:
      - "6379:6379"

  # 后端应用
  backend:
    build:
      context: ./jshERP-boot
      dockerfile: Dockerfile
    container_name: linghua-backend
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: mysql
      DB_USERNAME: ${MYSQL_USER}
      DB_PASSWORD: ${MYSQL_PASSWORD}
      REDIS_HOST: redis
      REDIS_PASSWORD: ${REDIS_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    volumes:
      - ./volumes/files:/opt/jshERP/files
      - ./volumes/logs:/opt/jshERP/logs
    ports:
      - "9999:9999"
    depends_on:
      - mysql
      - redis

  # 前端应用
  frontend:
    build:
      context: ./jshERP-web
      dockerfile: Dockerfile
    container_name: linghua-frontend
    restart: always
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    ports:
      - "80:80"
      - "443:443"
    depends_on:
      - backend

  # 数据备份
  backup:
    image: mysql:5.7.33
    container_name: linghua-backup
    restart: "no"
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
    volumes:
      - ./volumes/backup:/backup
      - ./scripts/backup.sh:/backup.sh
    command: /bin/bash /backup.sh
    depends_on:
      - mysql
```

#### 7.2.2 Nginx配置

**nginx.conf**:
```nginx
user nginx;
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    # 日志格式
    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';
    
    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_types text/plain text/css text/xml text/javascript 
               application/x-javascript application/xml+rss 
               application/javascript application/json;
    
    # 上游后端服务
    upstream backend {
        server backend:9999;
    }
    
    # 主服务器配置
    server {
        listen 80;
        server_name linghua-erp.local;
        
        # 前端静态资源
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;
        }
        
        # API代理
        location /api/ {
            proxy_pass http://backend/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            
            # 超时设置
            proxy_connect_timeout 60s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
        }
        
        # 文件上传
        location /files/ {
            alias /opt/jshERP/files/;
            expires 1d;
            add_header Cache-Control "public, immutable";
        }
        
        # 健康检查
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
    
    # HTTPS配置（可选）
    server {
        listen 443 ssl http2;
        server_name linghua-erp.local;
        
        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        
        # SSL配置
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512:ECDHE-RSA-AES256-GCM-SHA384:DHE-RSA-AES256-GCM-SHA384;
        ssl_prefer_server_ciphers off;
        
        # 其他配置同HTTP
        include /etc/nginx/conf.d/common.conf;
    }
}
```

### 7.3 监控和日志

#### 7.3.1 应用监控

**Spring Boot Actuator配置**:
```properties
# 监控端点配置
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```

**自定义健康检查**:
```java
@Component
public class CustomHealthIndicator implements HealthIndicator {
    
    @Resource
    private TeambuildingActivityService teambuildingActivityService;
    
    @Override
    public Health health() {
        try {
            // 检查数据库连接
            teambuildingActivityService.checkDatabaseHealth();
            
            return Health.up()
                .withDetail("database", "UP")
                .withDetail("timestamp", LocalDateTime.now())
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("database", "DOWN")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

#### 7.3.2 日志管理

**logback-spring.xml**:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 日志格式 -->
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n"/>
    
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/opt/jshERP/logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/jshERP/logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
            <maxHistory>30</maxHistory>
            <totalSizeCap>3GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 错误日志 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <file>/opt/jshERP/logs/error.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/jshERP/logs/error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 业务日志 -->
    <appender name="BUSINESS_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>/opt/jshERP/logs/business.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>/opt/jshERP/logs/business.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>
    
    <!-- 聆花业务日志 -->
    <logger name="com.jsh.erp.teambuilding" level="INFO" additivity="false">
        <appender-ref ref="BUSINESS_FILE"/>
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <logger name="com.jsh.erp.salary" level="INFO" additivity="false">
        <appender-ref ref="BUSINESS_FILE"/>
        <appender-ref ref="CONSOLE"/>
    </logger>
    
    <!-- 根日志级别 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</configuration>
```

### 7.4 备份和恢复

#### 7.4.1 数据备份脚本

**backup.sh**:
```bash
#!/bin/bash

# 备份配置
BACKUP_DIR="/backup"
DB_HOST="mysql"
DB_USER="root"
DB_PASSWORD="${MYSQL_ROOT_PASSWORD}"
DB_NAME="jsh_erp"
DATE=$(date +%Y%m%d_%H%M%S)

# 创建备份目录
mkdir -p ${BACKUP_DIR}/database
mkdir -p ${BACKUP_DIR}/files

# 数据库备份
echo "开始备份数据库..."
mysqldump -h${DB_HOST} -u${DB_USER} -p${DB_PASSWORD} \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  --hex-blob \
  ${DB_NAME} > ${BACKUP_DIR}/database/jsh_erp_${DATE}.sql

# 压缩数据库备份
gzip ${BACKUP_DIR}/database/jsh_erp_${DATE}.sql

# 文件备份
echo "开始备份文件..."
tar -czf ${BACKUP_DIR}/files/files_${DATE}.tar.gz -C /opt/jshERP files/

# 清理旧备份（保留30天）
find ${BACKUP_DIR}/database -name "*.sql.gz" -type f -mtime +30 -delete
find ${BACKUP_DIR}/files -name "*.tar.gz" -type f -mtime +30 -delete

echo "备份完成：jsh_erp_${DATE}.sql.gz"
echo "文件备份：files_${DATE}.tar.gz"
```

#### 7.4.2 恢复脚本

**restore.sh**:
```bash
#!/bin/bash

# 恢复配置
BACKUP_DIR="/backup"
DB_HOST="mysql"
DB_USER="root"
DB_PASSWORD="${MYSQL_ROOT_PASSWORD}"
DB_NAME="jsh_erp"

# 检查参数
if [ $# -ne 1 ]; then
    echo "使用方法: $0 backup_date (格式: YYYYMMDD_HHMMSS)"
    exit 1
fi

BACKUP_DATE=$1
SQL_FILE="${BACKUP_DIR}/database/jsh_erp_${BACKUP_DATE}.sql.gz"
FILES_ARCHIVE="${BACKUP_DIR}/files/files_${BACKUP_DATE}.tar.gz"

# 检查备份文件是否存在
if [ ! -f "$SQL_FILE" ]; then
    echo "错误：数据库备份文件不存在 $SQL_FILE"
    exit 1
fi

if [ ! -f "$FILES_ARCHIVE" ]; then
    echo "错误：文件备份不存在 $FILES_ARCHIVE"
    exit 1
fi

# 确认恢复操作
echo "警告：此操作将覆盖现有数据库和文件！"
echo "备份日期：$BACKUP_DATE"
read -p "确认继续？(y/N): " confirm

if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
    echo "操作已取消"
    exit 0
fi

# 恢复数据库
echo "开始恢复数据库..."
gunzip -c $SQL_FILE | mysql -h${DB_HOST} -u${DB_USER} -p${DB_PASSWORD} ${DB_NAME}

if [ $? -eq 0 ]; then
    echo "数据库恢复成功"
else
    echo "数据库恢复失败"
    exit 1
fi

# 恢复文件
echo "开始恢复文件..."
tar -xzf $FILES_ARCHIVE -C /opt/jshERP/

if [ $? -eq 0 ]; then
    echo "文件恢复成功"
else
    echo "文件恢复失败"
    exit 1
fi

echo "系统恢复完成"
```

---

## 8. 风险控制和预案

### 8.1 技术风险控制

#### 8.1.1 数据安全风险

**风险识别**:
- 数据泄露风险
- 数据丢失风险
- 数据完整性风险

**控制措施**:
- 多层次权限控制
- 定期数据备份
- 数据加密存储
- 操作日志审计

#### 8.1.2 系统稳定性风险

**风险识别**:
- 系统宕机风险
- 性能瓶颈风险
- 依赖服务故障

**控制措施**:
- 集群部署
- 负载均衡
- 服务降级
- 监控告警

### 8.2 业务连续性保障

#### 8.2.1 灾难恢复计划

**RTO目标**: 4小时内恢复服务
**RPO目标**: 数据丢失不超过1小时

**恢复步骤**:
1. 启动备用系统
2. 恢复最新数据备份
3. 验证系统功能
4. 切换用户访问
5. 监控系统状态

#### 8.2.2 应急预案

**数据库故障**:
```bash
# 1. 检查数据库状态
docker exec linghua-mysql mysql -uroot -p123456 -e "SELECT 1"

# 2. 重启数据库服务
docker restart linghua-mysql

# 3. 如果无法修复，恢复备份
./restore.sh latest
```

**应用程序故障**:
```bash
# 1. 检查应用日志
docker logs linghua-backend

# 2. 重启应用服务
docker restart linghua-backend

# 3. 检查依赖服务
docker ps
```

### 8.3 数据迁移风险控制

#### 8.3.1 迁移前准备

**数据评估**:
- 现有数据量统计
- 数据质量检查
- 依赖关系分析
- 迁移时间评估

**环境准备**:
- 测试环境验证
- 生产环境备份
- 迁移脚本测试
- 回滚方案准备

#### 8.3.2 迁移执行

**分阶段迁移**:
1. 基础数据迁移（用户、权限）
2. 主数据迁移（商品、客户）
3. 业务数据迁移（订单、库存）
4. 扩展数据迁移（团建、薪酬）

**验证检查**:
- 数据完整性检查
- 业务功能测试
- 性能基准测试
- 用户接受测试

---

## 总结

本实现方案严格遵循最小入侵原则，通过扩展和配置的方式实现聆花文化的特定业务需求。主要特点：

### 技术优势
1. **高复用性**: 复用70%以上现有代码和组件
2. **架构一致性**: 保持原有系统架构和编码规范
3. **扩展性**: 良好的模块化设计，便于后续扩展
4. **稳定性**: 基于成熟的ERP系统架构

### 业务价值
1. **功能完整**: 100%覆盖聆花文化业务需求
2. **操作简便**: 符合小微企业操作习惯
3. **数据集成**: 实现业务数据的无缝集成
4. **决策支持**: 提供丰富的报表和分析功能

### 实施保障
1. **风险可控**: 详细的风险识别和控制措施
2. **部署简便**: 基于Docker的容器化部署
3. **运维友好**: 完善的监控和日志体系
4. **数据安全**: 多层次的安全保障机制

该方案为聆花文化提供了一个功能完整、技术先进、安全可靠的ERP管理系统，有效支撑企业的数字化转型和业务发展。