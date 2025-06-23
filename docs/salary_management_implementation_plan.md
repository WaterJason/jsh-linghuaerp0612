# jshERP薪酬管理模块实施计划

## 项目概述

### 项目目标
完整实现jshERP系统的薪酬管理模块，支持珐琅馆业务的复杂薪酬结构，包括员工薪酬档案管理、薪酬自动计算系统、薪酬发放管理等功能。

### 技术架构
- **后端**: Spring Boot 2.x + MyBatis Plus + MySQL 5.7 + Redis 6.2
- **前端**: Vue.js 2.7.16 + Ant Design Vue 1.5.2
- **架构模式**: 多租户架构 + RBAC权限控制 + 策略模式计算引擎

### 核心功能模块
1. **员工薪酬档案管理**: 基础信息维护 + 薪酬结构配置
2. **薪酬自动计算系统**: 数据源集成 + 计算规则引擎 + 月度薪酬生成
3. **薪酬发放管理**: 薪资条生成 + 财务系统集成 + 发放记录管理

## 数据库设计方案

### 核心数据表结构

#### 1. jsh_salary_profile (员工薪酬档案表)
```sql
CREATE TABLE `jsh_salary_profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID，关联jsh_user表',
  `employee_name` varchar(100) NOT NULL COMMENT '员工姓名',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `position` varchar(100) DEFAULT NULL COMMENT '职位',
  `entry_date` date DEFAULT NULL COMMENT '入职时间',
  `daily_wage` decimal(10,2) DEFAULT 0.00 COMMENT '日薪标准',
  `salary_status` varchar(20) DEFAULT 'ACTIVE' COMMENT '薪酬状态：ACTIVE-生效，INACTIVE-失效',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  KEY `idx_employee_tenant` (`employee_id`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_status` (`tenant_id`, `salary_status`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工薪酬档案表';
```

#### 2. jsh_salary_item (薪酬项目配置表)
```sql
CREATE TABLE `jsh_salary_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `item_code` varchar(50) NOT NULL COMMENT '薪酬项目编码',
  `item_name` varchar(100) NOT NULL COMMENT '薪酬项目名称',
  `item_type` varchar(20) NOT NULL COMMENT '项目类型：FIXED-固定薪酬，COMMISSION-提成，ALLOWANCE-津贴',
  `calculation_formula` varchar(500) DEFAULT NULL COMMENT '计算公式',
  `default_rate` decimal(10,4) DEFAULT 0.0000 COMMENT '默认比例',
  `base_amount` decimal(10,2) DEFAULT 0.00 COMMENT '基础金额',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-启用，INACTIVE-禁用',
  `sort_order` int(11) DEFAULT 0 COMMENT '排序',
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `delete_flag` varchar(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator` bigint(20) DEFAULT NULL COMMENT '创建人',
  `updater` bigint(20) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code_tenant` (`item_code`, `tenant_id`, `delete_flag`),
  KEY `idx_tenant_type` (`tenant_id`, `item_type`, `delete_flag`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='薪酬项目配置表';
```

### 薪酬项目基础数据
```sql
-- 插入8种薪酬项目类型
INSERT INTO `jsh_salary_item` (`item_code`, `item_name`, `item_type`, `description`, `tenant_id`) VALUES
('DAILY_WAGE', '日薪标准', 'FIXED', '固定日薪收入', 0),
('SALES_COMMISSION', '销售提成', 'COMMISSION', '销售业绩提成', 0),
('COFFEE_COMMISSION', '咖啡店提成', 'COMMISSION', '咖啡店销售提成', 0),
('CLOISONNE_FEE', '掐丝点蓝制作费', 'COMMISSION', '掐丝点蓝制作提成', 0),
('ACCESSORY_FEE', '配饰制作费', 'COMMISSION', '配饰制作提成', 0),
('TRAINING_COMMISSION', '培训提成', 'COMMISSION', '培训项目提成', 0),
('HANDCRAFT_COMMISSION', '散客手作提成', 'COMMISSION', '散客手作指导提成', 0),
('BUSINESS_DEVELOPMENT', '业务拓展提成', 'COMMISSION', '渠道开发和作品销售提成', 0),
('OUTSIDE_INSTRUCTOR', '讲师费（外出）', 'ALLOWANCE', '外出讲师费用', 0),
('OUTSIDE_ASSISTANT', '助理费（外出）', 'ALLOWANCE', '外出助理费用', 0),
('INSIDE_INSTRUCTOR', '讲师费（在馆）', 'ALLOWANCE', '在馆讲师费用', 0),
('INSIDE_ASSISTANT', '助理费（在馆）', 'ALLOWANCE', '在馆助理费用', 0);
```

## 后端架构设计

### 实体类设计
```java
// SalaryProfile.java - 员工薪酬档案实体
@TableName("jsh_salary_profile")
public class SalaryProfile {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("employee_id")
    private Long employeeId;
    
    @TableField("employee_name")
    private String employeeName;
    
    @TableField("daily_wage")
    private BigDecimal dailyWage;
    
    @TableField("salary_status")
    private String salaryStatus;
    
    @TableField("tenant_id")
    private Long tenantId;
    
    @TableLogic
    @TableField("delete_flag")
    private String deleteFlag;
    
    // 其他字段和getter/setter方法
}
```

### Service层架构
```java
// SalaryProfileService.java - 薪酬档案服务
@Service
public class SalaryProfileService extends BaseService<SalaryProfile> {
    
    @Autowired
    private SalaryProfileMapper salaryProfileMapper;
    
    /**
     * 创建员工薪酬档案
     */
    @Transactional(rollbackFor = Exception.class)
    public int createSalaryProfile(SalaryProfile profile, HttpServletRequest request) {
        // 设置租户ID和创建人信息
        setTenantId(profile);
        setCreateInfo(profile, request);
        
        return salaryProfileMapper.insert(profile);
    }
    
    /**
     * 配置员工薪酬结构
     */
    @Transactional(rollbackFor = Exception.class)
    public void configureSalaryStructure(Long employeeId, List<EmployeeSalaryItem> items) {
        // 实现薪酬结构配置逻辑
    }
}
```

### 薪酬计算引擎设计
```java
// SalaryCalculationStrategy.java - 计算策略接口
public interface SalaryCalculationStrategy {
    /**
     * 计算薪酬
     * @param employeeData 员工数据
     * @param salaryItem 薪酬项目
     * @param calculationMonth 计算月份
     * @return 计算结果
     */
    SalaryCalculationResult calculate(EmployeeData employeeData, 
                                    SalaryItem salaryItem, 
                                    Date calculationMonth);
}

// FixedSalaryStrategy.java - 固定薪酬计算策略
@Component
public class FixedSalaryStrategy implements SalaryCalculationStrategy {
    
    @Override
    public SalaryCalculationResult calculate(EmployeeData employeeData, 
                                           SalaryItem salaryItem, 
                                           Date calculationMonth) {
        // 固定薪酬 = 日薪 * 工作天数
        BigDecimal dailyWage = employeeData.getDailyWage();
        int workDays = getWorkDays(employeeData.getEmployeeId(), calculationMonth);
        BigDecimal amount = dailyWage.multiply(new BigDecimal(workDays));
        
        return new SalaryCalculationResult(salaryItem.getId(), amount, workDays + "天");
    }
}
```

## 前端页面设计

### 页面结构
1. **SalaryProfileList.vue** - 薪酬档案管理页面
2. **SalaryCalculation.vue** - 薪酬计算页面  
3. **SalaryPayment.vue** - 薪酬发放页面
4. **SalaryInquiry.vue** - 薪资查询页面
5. **SalaryConfig.vue** - 薪酬配置页面

### 核心组件设计
```vue
<!-- SalaryProfileList.vue -->
<template>
  <div class="salary-profile-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <a-form layout="inline">
        <a-form-item label="员工姓名">
          <a-input v-model="queryParam.employeeName" placeholder="请输入员工姓名" />
        </a-form-item>
        <a-form-item label="部门">
          <a-select v-model="queryParam.department" placeholder="请选择部门">
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="珐琅制作">珐琅制作</a-select-option>
            <a-select-option value="咖啡服务">咖啡服务</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button @click="handleReset">重置</a-button>
        </a-form-item>
      </a-form>
    </div>
    
    <!-- 操作按钮 -->
    <div class="action-buttons">
      <a-button type="primary" @click="handleAdd">新增薪酬档案</a-button>
      <a-button @click="handleBatchConfig">批量配置</a-button>
    </div>
    
    <!-- 数据表格 -->
    <a-table 
      :columns="columns" 
      :data-source="dataList" 
      :pagination="pagination"
      @change="handleTableChange">
      
      <template slot="action" slot-scope="text, record">
        <a-button size="small" @click="handleEdit(record)">编辑</a-button>
        <a-button size="small" @click="handleConfig(record)">配置薪酬</a-button>
        <a-button size="small" type="danger" @click="handleDelete(record)">删除</a-button>
      </template>
    </a-table>
    
    <!-- 新增/编辑弹窗 -->
    <salary-profile-modal 
      :visible="modalVisible" 
      :record="currentRecord"
      @ok="handleModalOk" 
      @cancel="handleModalCancel" />
  </div>
</template>
```

## 开发时间计划

### 第一阶段：数据库设计与创建 (2天)
- **Day 1**: 设计6张核心数据表结构，编写CREATE TABLE脚本
- **Day 2**: 创建数据库索引，初始化基础数据

### 第二阶段：后端基础架构开发 (3天)  
- **Day 3**: 创建实体类和Mapper接口
- **Day 4**: 创建Service基类，配置多租户支持
- **Day 5**: 创建MapperEx扩展接口，完善基础架构

### 第三阶段：薪酬计算引擎开发 (4天)
- **Day 6**: 设计计算策略接口，实现固定薪酬和销售提成策略
- **Day 7**: 实现生产提成和培训提成策略
- **Day 8**: 实现业务拓展和津贴类策略
- **Day 9**: 开发数据集成服务，实现计算引擎核心逻辑

### 第四阶段：Controller层API开发 (3天)
- **Day 10**: 开发薪酬档案管理API
- **Day 11**: 开发薪酬计算API
- **Day 12**: 开发薪酬发放和查询API

### 第五阶段：前端页面开发 (6天)
- **Day 13-14**: 开发薪酬档案管理页面
- **Day 15-16**: 开发薪酬计算和发放页面  
- **Day 17-18**: 开发薪资查询和配置页面

### 第六阶段：权限集成与配置 (2天)
- **Day 19**: 集成jshERP权限系统，配置菜单权限
- **Day 20**: 配置按钮权限和数据权限

### 第七阶段：模块集成与测试 (3天)
- **Day 21**: 与销售、生产、培训模块集成
- **Day 22**: 与财务模块集成，全面功能测试
- **Day 23**: 性能测试和优化

### 第八阶段：文档编写与部署 (2天)
- **Day 24**: 编写技术文档和用户手册
- **Day 25**: 系统部署和上线

## 风险评估与缓解措施

### 主要风险点
1. **数据一致性风险**: 多模块数据同步可能出现不一致
2. **计算准确性风险**: 复杂的薪酬计算规则容易出错  
3. **性能风险**: 大量数据计算可能影响系统性能
4. **权限安全风险**: 薪酬数据泄露风险

### 缓解措施
1. **数据一致性**: 实现数据校验和异常处理机制，使用事务控制
2. **计算准确性**: 充分的单元测试和集成测试，建立测试用例库
3. **性能优化**: Redis缓存，异步处理，数据库索引优化
4. **安全控制**: 严格的权限控制，操作日志记录，数据加密

## 成功标准

### 功能完整性
- ✅ 员工薪酬档案管理功能完整
- ✅ 薪酬自动计算系统正常运行
- ✅ 薪酬发放管理流程顺畅
- ✅ 与现有模块集成成功

### 性能指标
- ✅ 月度薪酬计算时间 < 5分钟（100员工）
- ✅ 页面响应时间 < 2秒
- ✅ 并发用户数 > 50

### 质量标准
- ✅ 代码覆盖率 > 80%
- ✅ 零安全漏洞
- ✅ 用户满意度 > 90%
