# jshERP薪酬管理模块开发方案

## 项目概述

### 项目背景
基于jshERP系统，为珐琅馆业务开发专业的薪酬管理模块，支持复杂的薪酬计算规则和多数据源集成。

### 项目目标
1. 建立完整的员工薪酬档案管理系统
2. 实现复杂的薪酬计算引擎
3. 支持多种收入类型的自动计算
4. 与现有业务模块无缝集成
5. 提供完整的薪酬发放和查询功能

### 业务需求
#### 1. 员工薪酬配置
- 连接用户模块的用户数据
- 薪酬计算规则引擎设置

#### 2. 薪酬计算规则
**固定薪酬**：
- 日薪标准

**销售提成**：
- 咖啡店值班销售额提成（按比例）
- 项目收入：讲师费（外出/在馆）、助理费（外出/在馆）
- 渠道开发提成（按比例）
- 馆内销售提成（按比例）
- 艺术作品销售（按次填报）
- 报销（按次填报）

**生产提成**：
- 掐丝点蓝制作费
- 配饰制作费

#### 3. 薪酬自动计算系统
- 数据源集成：销售模块、生产模块、珐琅馆模块
- 计算规则引擎：根据员工薪酬结构自动计算
- 月度薪酬生成：按月汇总生成详细薪酬清单
- 异常处理：数据缺失、计算错误处理

#### 4. 薪酬发放管理
- 薪资条生成：自动汇总各项收入明细
- 财务系统集成：与财务模块同步
- 发放记录管理：记录发放时间、方式、状态
- 历史查询功能：员工和管理员查询功能

## 技术架构

### 技术栈
- **后端**：Spring Boot 2.x + MyBatis + MySQL
- **前端**：Vue.js 2.7.16 + Ant Design Vue 1.5.2
- **缓存**：Redis 6.2.1
- **认证**：JWT Token
- **多租户**：基于tenant_id的数据隔离

### 系统架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端界面层     │    │   业务逻辑层     │    │   数据访问层     │
│                │    │                │    │                │
│ - 薪酬档案管理   │    │ - 薪酬计算引擎   │    │ - 薪酬数据表     │
│ - 薪酬计算      │◄──►│ - 数据源集成     │◄──►│ - 用户数据表     │
│ - 薪酬发放      │    │ - 审批流程      │    │ - 业务数据表     │
│ - 薪酬查询      │    │ - 权限控制      │    │ - 日志记录表     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 模块划分
1. **薪酬档案模块**：员工薪酬基础信息管理
2. **薪酬配置模块**：薪酬计算规则配置
3. **薪酬计算模块**：自动计算和手动调整
4. **薪酬发放模块**：薪资条生成和发放管理
5. **薪酬查询模块**：历史记录查询和统计

## 数据库设计

### 核心数据表

#### 1. 薪酬档案表 (jsh_salary_profile)
```sql
CREATE TABLE jsh_salary_profile (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  user_id BIGINT(20) NOT NULL COMMENT '关联用户ID',
  employee_code VARCHAR(50) COMMENT '员工编号',
  employee_name VARCHAR(100) NOT NULL COMMENT '员工姓名',
  phone VARCHAR(20) COMMENT '联系电话',
  department VARCHAR(100) COMMENT '部门',
  position VARCHAR(100) COMMENT '职位',
  entry_date DATE COMMENT '入职时间',
  daily_wage DECIMAL(10,2) DEFAULT 0 COMMENT '日薪标准',
  salary_status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态',
  remark TEXT COMMENT '备注',
  delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  create_user BIGINT(20),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  update_user BIGINT(20),
  INDEX idx_tenant_user (tenant_id, user_id),
  INDEX idx_tenant_delete (tenant_id, delete_flag)
);
```

#### 2. 薪酬配置表 (jsh_salary_config)
```sql
CREATE TABLE jsh_salary_config (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  config_type VARCHAR(50) NOT NULL COMMENT '配置类型',
  config_key VARCHAR(100) NOT NULL COMMENT '配置键',
  config_value TEXT COMMENT '配置值',
  description VARCHAR(500) COMMENT '配置说明',
  delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  create_user BIGINT(20),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  update_user BIGINT(20),
  INDEX idx_tenant_type (tenant_id, config_type),
  INDEX idx_tenant_delete (tenant_id, delete_flag)
);
```

#### 3. 薪酬计算记录表 (jsh_salary_calculation)
```sql
CREATE TABLE jsh_salary_calculation (
  id BIGINT(20) PRIMARY KEY AUTO_INCREMENT,
  tenant_id BIGINT(20) NOT NULL COMMENT '租户ID',
  employee_id BIGINT(20) NOT NULL COMMENT '员工ID',
  calculation_month VARCHAR(7) NOT NULL COMMENT '计算月份(YYYY-MM)',
  fixed_salary DECIMAL(10,2) DEFAULT 0 COMMENT '固定薪酬',
  sales_commission DECIMAL(10,2) DEFAULT 0 COMMENT '销售提成',
  project_income DECIMAL(10,2) DEFAULT 0 COMMENT '项目收入',
  production_commission DECIMAL(10,2) DEFAULT 0 COMMENT '生产提成',
  reimbursement DECIMAL(10,2) DEFAULT 0 COMMENT '报销',
  total_salary DECIMAL(10,2) DEFAULT 0 COMMENT '总薪酬',
  calculation_status VARCHAR(20) DEFAULT 'DRAFT' COMMENT '计算状态',
  calculation_detail TEXT COMMENT '计算明细(JSON)',
  delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  create_user BIGINT(20),
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  update_user BIGINT(20),
  INDEX idx_tenant_employee_month (tenant_id, employee_id, calculation_month),
  INDEX idx_tenant_delete (tenant_id, delete_flag)
);
```

## API接口设计

### 薪酬档案接口
- `GET /salary/profile/list` - 获取薪酬档案列表
- `POST /salary/profile/add` - 新增薪酬档案
- `PUT /salary/profile/update` - 更新薪酬档案
- `DELETE /salary/profile/delete` - 删除薪酬档案

### 薪酬配置接口
- `GET /salary/config/get` - 获取薪酬配置
- `POST /salary/config/save` - 保存薪酬配置

### 薪酬计算接口
- `POST /salary/calculation/calculate` - 执行薪酬计算
- `GET /salary/calculation/list` - 获取计算记录列表
- `POST /salary/calculation/approve` - 审批薪酬计算

### 薪酬发放接口
- `POST /salary/payment/generate` - 生成薪资条
- `GET /salary/payment/list` - 获取发放记录
- `POST /salary/payment/confirm` - 确认发放

## 开发计划

### 第一阶段：前端UI重构 ✅ 已完成
- [x] SalaryProfileList.vue 重构（薪酬档案列表页面）
- [x] SalaryProfileModal.vue 优化（新增/编辑薪酬档案弹窗）
- [x] SalaryConfigModal.vue 重构（薪酬配置弹窗）
- [x] SalaryCalculation.vue 标准化（薪酬计算页面）
- [x] SalaryConfig.vue 重构（薪酬系统配置页面）
- [x] SalaryPayment.vue 重构（薪酬发放管理页面）
- [x] SalaryInquiry.vue 重构（薪酬查询页面）

**重构成果**：
- 所有页面采用jshERP标准架构和JeecgListMixin
- 统一的UI风格和交互体验
- 标准化的查询、操作、表格显示
- 完善的响应式设计和表单验证
- 与用户模块的无缝集成

### 第二阶段：数据库设计和后端基础架构
- [ ] 数据库表结构创建
- [ ] 基础Entity和Mapper创建
- [ ] 基础Service和Controller创建
- [ ] 权限配置和多租户支持

### 第三阶段：核心业务逻辑开发
- [ ] 薪酬档案CRUD功能
- [ ] 薪酬配置管理功能
- [ ] 基础数据验证和处理

### 第四阶段：计算引擎开发
- [ ] 薪酬计算引擎核心逻辑
- [ ] 数据源集成（销售、生产、珐琅馆）
- [ ] 计算规则配置和执行

### 第五阶段：模块集成和测试
- [ ] 与现有模块集成测试
- [ ] 完整业务流程测试
- [ ] 性能优化和bug修复

### 第六阶段：部署和优化
- [ ] 生产环境部署
- [ ] 用户培训和文档
- [ ] 持续优化和维护

## 风险评估

### 技术风险
1. **数据集成复杂性**：多模块数据源集成可能存在数据一致性问题
2. **计算引擎性能**：复杂计算可能影响系统性能
3. **多租户数据隔离**：确保数据安全和隔离

### 业务风险
1. **需求变更**：业务规则可能频繁调整
2. **数据准确性**：薪酬计算错误可能造成严重后果
3. **用户接受度**：新系统的学习成本

### 应对措施
1. 分阶段开发，及时验证
2. 完善的测试覆盖
3. 详细的文档和培训
4. 灵活的配置机制

## 下一步行动

1. **立即开始第二阶段开发**
2. **创建数据库表结构**
3. **建立后端基础架构**
4. **实现核心API接口**
5. **集成前后端功能**

---

*文档版本：v1.0*  
*创建时间：2025-01-23*  
*最后更新：2025-01-23*
