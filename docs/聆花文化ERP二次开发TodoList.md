# 聆花文化ERP二次开发TodoList

**文档版本**: v1.0  
**创建日期**: 2025-06-17  
**项目阶段**: 开发任务规划  
**估算总工期**: 18周  

---

## 项目概述

基于《聆花文化ERP二次开发综合实施方案》，本TodoList将项目分解为具体的开发任务，确保每个任务都有明确的完成标准和交付物。所有开发工作将严格遵循jshERP现有架构规范，采用最小侵入原则。

---

## 第一阶段：生产制作管理模块 (8周)

### Week 1: 数据库设计与基础架构

#### 数据库设计任务
- [ ] **数据库表结构设计** (3天)
  - [ ] 设计生产工单主表 `jsh_production_order`
  - [ ] 设计工单物料清单表 `jsh_production_material`
  - [ ] 设计移动端报工记录表 `jsh_work_report`
  - [ ] 设计物流追踪表 `jsh_logistics_track`
  - [ ] 设计工艺流程模板表 `jsh_process_template`
  - [ ] 创建数据库索引和约束
  - [ ] 编写数据库初始化脚本

- [ ] **基础架构搭建** (2天)
  - [ ] 创建生产模块包结构 `com.jsh.erp.production`
  - [ ] 配置MyBatis映射文件目录
  - [ ] 设置多租户支持配置
  - [ ] 创建基础实体类和枚举

**交付物**: 
- 数据库设计文档
- 初始化SQL脚本
- 基础项目结构

---

### Week 2-3: 核心业务逻辑开发

#### 生产工单管理模块
- [ ] **实体类开发** (1天)
  - [ ] `ProductionOrder.java` - 生产工单实体
  - [ ] `ProductionMaterial.java` - 工单物料实体
  - [ ] `WorkReport.java` - 报工记录实体
  - [ ] `ProcessTemplate.java` - 工艺模板实体
  - [ ] 相关VO类和DTO类

- [ ] **Mapper接口开发** (1天)
  - [ ] `ProductionOrderMapper.java`
  - [ ] `ProductionMaterialMapper.java`
  - [ ] `WorkReportMapper.java`
  - [ ] `ProcessTemplateMapper.java`
  - [ ] 对应的XML映射文件

- [ ] **Service层开发** (3天)
  - [ ] `ProductionOrderService.java` - 工单管理核心服务
    - [ ] `createOrderFromSale()` - 从销售订单创建生产工单
    - [ ] `assignWorker()` - 分配制作人员
    - [ ] `updateOrderStatus()` - 更新工单状态
    - [ ] `calculateCost()` - 成本计算
  - [ ] `ProductionMaterialService.java` - 物料管理服务
    - [ ] `checkMaterialAvailability()` - 检查物料库存
    - [ ] `reserveMaterials()` - 预留物料
    - [ ] `consumeMaterials()` - 消耗物料
  - [ ] `WorkReportService.java` - 报工服务
    - [ ] `submitWorkReport()` - 提交报工记录
    - [ ] `uploadPhotos()` - 上传制作照片
    - [ ] `completeWork()` - 完工确认

- [ ] **Controller层开发** (2天)
  - [ ] `ProductionOrderController.java`
    - [ ] `POST /production/orders` - 创建工单
    - [ ] `GET /production/orders` - 查询工单列表
    - [ ] `PUT /production/orders/{id}/assign` - 分配人员
    - [ ] `PUT /production/orders/{id}/status` - 更新状态
  - [ ] `WorkReportController.java`
    - [ ] `POST /production/reports` - 提交报工
    - [ ] `POST /production/reports/photos` - 上传照片
    - [ ] `GET /production/reports/{orderId}` - 查询报工记录

**交付物**:
- 完整的后端API接口
- 单元测试用例
- API接口文档

---

### Week 4: 订单驱动生产流转

#### 智能工单生成逻辑
- [ ] **订单分析服务** (2天)
  - [ ] `OrderAnalysisService.java`
    - [ ] `analyzeProductRequirements()` - 分析产品需求
    - [ ] `checkBottomStockAvailability()` - 检查底胎库存
    - [ ] `generateProductionPlan()` - 生成生产计划
    - [ ] `createOutsourcingRequest()` - 创建委外采购需求

- [ ] **库存集成服务** (2天)
  - [ ] `InventoryIntegrationService.java`
    - [ ] `queryGuangzhouStock()` - 查询广州原料仓库存
    - [ ] `queryChongzuoStock()` - 查询崇左生产基地库存
    - [ ] `createTransferOrder()` - 创建调拨指令
    - [ ] `updateStockReservation()` - 更新库存预留

- [ ] **采购联动服务** (1天)
  - [ ] `PurchaseIntegrationService.java`
    - [ ] `createOutsourcingPurchase()` - 创建委外采购订单
    - [ ] `syncPayableAccount()` - 同步应付账款
    - [ ] `trackPurchaseStatus()` - 跟踪采购状态

**交付物**:
- 订单驱动生产流转功能
- 与采购模块集成测试
- 库存模块集成测试

---

### Week 5: 移动端报工系统

#### 移动端H5开发
- [ ] **移动端页面结构** (2天)
  - [ ] 创建移动端项目目录 `jshERP-web/src/views/mobile/production/`
  - [ ] `WorkOrderList.vue` - 工单列表页面
  - [ ] `WorkReport.vue` - 报工页面
  - [ ] `PhotoUpload.vue` - 照片上传组件
  - [ ] `QRCodeScanner.vue` - 二维码扫描组件

- [ ] **移动端功能开发** (2.5天)
  - [ ] 工单列表查询和筛选
  - [ ] 报工表单设计（简化版）
  - [ ] 照片拍摄和上传功能
  - [ ] 一键完工确认
  - [ ] 离线数据缓存机制
  - [ ] 网络状态检测和重试

- [ ] **移动端优化** (0.5天)
  - [ ] 响应式设计适配
  - [ ] 触摸操作优化
  - [ ] 加载性能优化
  - [ ] 移动端兼容性测试

**交付物**:
- 移动端H5应用
- 移动端用户操作手册
- 兼容性测试报告

---

### Week 6: 物流追踪与半成品回调

#### 物流集成开发
- [ ] **物流API集成** (2天)
  - [ ] `LogisticsService.java`
    - [ ] `trackPackage()` - 物流追踪
    - [ ] `updateDeliveryStatus()` - 更新配送状态
    - [ ] `notifyArrival()` - 到货通知
  - [ ] 集成顺丰、圆通物流API
  - [ ] 物流状态标准化处理

- [ ] **半成品回调系统** (2天)
  - [ ] `SemiProductCallbackService.java`
    - [ ] `generatePackingList()` - 生成打包清单
    - [ ] `createReturnOrder()` - 创建回调单
    - [ ] `trackReturnStatus()` - 追踪回调状态
  - [ ] 广州仓收货确认功能
  - [ ] 质检记录和问题反馈

- [ ] **后工调度系统** (1天)
  - [ ] `PostProcessService.java`
    - [ ] `generatePostProcessTask()` - 生成后工任务
    - [ ] `calculatePostProcessFee()` - 计算后工费用
    - [ ] `assignPostProcessor()` - 分配后工人员

**交付物**:
- 物流追踪功能
- 半成品回调系统
- 后工调度功能

---

### Week 7: 前端界面开发

#### PC端管理界面
- [ ] **生产管理主界面** (2天)
  - [ ] `ProductionOrderList.vue` - 工单列表页面
    - [ ] 工单查询和筛选
    - [ ] 工单状态展示
    - [ ] 批量操作功能
  - [ ] `ProductionOrderModal.vue` - 工单详情弹窗
    - [ ] 工单基本信息编辑
    - [ ] 物料清单管理
    - [ ] 报工记录查看

- [ ] **生产看板界面** (1.5天)
  - [ ] `ProductionDashboard.vue` - 生产看板
    - [ ] 工单状态统计
    - [ ] 进度可视化展示
    - [ ] 实时数据更新
    - [ ] 任务分配界面

- [ ] **报工记录界面** (1天)
  - [ ] `WorkReportList.vue` - 报工记录列表
  - [ ] `WorkReportDetail.vue` - 报工详情查看
  - [ ] 照片预览组件

- [ ] **物流追踪界面** (0.5天)
  - [ ] `LogisticsTrack.vue` - 物流追踪页面
  - [ ] 物流状态时间轴展示

**交付物**:
- 完整的PC端管理界面
- 界面交互测试
- 用户体验优化

---

### Week 8: 测试与集成

#### 系统测试
- [ ] **单元测试** (2天)
  - [ ] Service层单元测试覆盖率 > 80%
  - [ ] Controller层接口测试
  - [ ] 工具类和帮助方法测试

- [ ] **集成测试** (2天)
  - [ ] 与jshERP核心模块集成测试
  - [ ] 数据库事务一致性测试
  - [ ] 多租户数据隔离测试
  - [ ] API接口联调测试

- [ ] **端到端测试** (1天)
  - [ ] 完整业务流程测试
  - [ ] 移动端功能测试
  - [ ] 跨浏览器兼容性测试
  - [ ] 性能压力测试

**交付物**:
- 测试报告
- 问题修复记录
- 性能测试报告
- 第一阶段功能验收

---

## 第二阶段：业务支撑模块 (6周)

### Week 9-10: 团建活动管理模块

#### 团建活动核心功能
- [ ] **数据库设计** (1天)
  - [ ] `jsh_teambuilding_activity` - 团建活动主表
  - [ ] `jsh_teambuilding_participant` - 参与人员表
  - [ ] `jsh_teambuilding_expense` - 费用明细表
  - [ ] `jsh_venue_management` - 场地管理表

- [ ] **后端开发** (4天)
  - [ ] `TeambuildingActivityService.java`
    - [ ] `createActivity()` - 创建活动
    - [ ] `checkResourceConflict()` - 资源冲突检测
    - [ ] `calculateBudget()` - 预算核算
    - [ ] `confirmActivity()` - 活动确认
  - [ ] `VenueManagementService.java`
    - [ ] `checkVenueAvailability()` - 场地可用性检查
    - [ ] `reserveVenue()` - 场地预订
  - [ ] `ParticipantManagementService.java`
    - [ ] `assignInstructor()` - 分配讲师
    - [ ] `assignAssistant()` - 分配助理
    - [ ] `calculateCommission()` - 计算提成

- [ ] **前端开发** (3天)
  - [ ] `TeambuildingActivityList.vue` - 活动列表页面
  - [ ] `TeambuildingActivityModal.vue` - 活动编辑弹窗
  - [ ] `ResourceCalendar.vue` - 资源日历组件
  - [ ] `BudgetCalculator.vue` - 预算计算器

**交付物**:
- 团建活动管理完整功能
- 资源冲突检测算法
- 预算自动核算功能

---

### Week 11-12: 薪酬核算中心模块

#### 薪酬自动化核算
- [ ] **数据库设计** (1天)
  - [ ] `jsh_salary_calculation` - 薪酬核算主表
  - [ ] `jsh_salary_detail` - 薪酬明细表
  - [ ] `jsh_salary_config` - 薪酬配置表
  - [ ] 员工信息扩展表 `jsh_user_employee`

- [ ] **核算引擎开发** (4天)
  - [ ] `SalaryCalculationEngine.java`
    - [ ] `calculateMonthlySalary()` - 月度薪酬计算
    - [ ] `calculateProductionSalary()` - 生产工费计算
    - [ ] `calculateTeambuildingCommission()` - 团建提成计算
    - [ ] `calculateScheduleSalary()` - 排班工资计算
  - [ ] `SalaryDataCollectionService.java`
    - [ ] `collectProductionData()` - 收集生产数据
    - [ ] `collectTeambuildingData()` - 收集团建数据
    - [ ] `collectScheduleData()` - 收集排班数据
  - [ ] `SalaryReportService.java`
    - [ ] `generatePayroll()` - 生成薪资条
    - [ ] `exportSalaryReport()` - 导出薪酬报表

- [ ] **前端开发** (3天)
  - [ ] `SalaryCalculationList.vue` - 薪酬核算列表
  - [ ] `SalaryCalculationModal.vue` - 薪酬计算弹窗
  - [ ] `PayrollViewer.vue` - 薪资条查看器
  - [ ] `SalaryConfigModal.vue` - 薪酬配置界面

**交付物**:
- 薪酬自动化核算系统
- 多维度薪酬计算引擎
- 薪资条生成功能

---

### Week 13-14: 财务系统集成

#### 财务模块集成
- [ ] **数据同步服务** (3天)
  - [ ] `FinanceIntegrationService.java`
    - [ ] `syncProductionCosts()` - 同步生产成本
    - [ ] `syncTeambuildingRevenue()` - 同步团建收入
    - [ ] `syncPayrollExpenses()` - 同步薪酬支出
    - [ ] `generateAccountEntries()` - 生成会计分录

- [ ] **成本核算服务** (2天)
  - [ ] `CostCalculationService.java`
    - [ ] `calculateMaterialCost()` - 计算材料成本
    - [ ] `calculateLaborCost()` - 计算人工成本
    - [ ] `calculateOverheadCost()` - 计算制造费用
    - [ ] `updateProductCost()` - 更新产品成本

- [ ] **报表生成服务** (2天)
  - [ ] `ReportGenerationService.java`
    - [ ] `generateProfitLossReport()` - 生成损益报表
    - [ ] `generateCostAnalysisReport()` - 生成成本分析报表
    - [ ] `generateCashFlowReport()` - 生成现金流报表

- [ ] **前端报表界面** (1天)
  - [ ] `FinanceReportList.vue` - 财务报表列表
  - [ ] `ReportViewer.vue` - 报表查看器

**交付物**:
- 财务系统集成功能
- 自动化成本核算
- 财务报表生成

---

## 第三阶段：优化增强 (4周)

### Week 15: 非遗特色功能模块

#### 非遗文化特色功能
- [ ] **工艺知识库** (2天)
  - [ ] `CraftKnowledgeService.java`
    - [ ] `manageCraftProcess()` - 工艺流程管理
    - [ ] `recordCraftHistory()` - 工艺历史记录
    - [ ] `manageMasterProfile()` - 大师档案管理
  - [ ] `CraftKnowledgeList.vue` - 工艺知识列表
  - [ ] `CraftProcessModal.vue` - 工艺流程编辑

- [ ] **艺术品档案管理** (2天)
  - [ ] `ArtworkArchiveService.java`
    - [ ] `createArtworkProfile()` - 创建作品档案
    - [ ] `generateCertification()` - 生成数字鉴证
    - [ ] `trackArtworkLifecycle()` - 追踪作品生命周期
  - [ ] `ArtworkArchiveList.vue` - 作品档案列表
  - [ ] `ArtworkDetailModal.vue` - 作品详情编辑

- [ ] **定制订单管理** (1天)
  - [ ] `CustomOrderService.java`
    - [ ] `handleCustomRequirement()` - 处理定制需求
    - [ ] `manageDesignProcess()` - 管理设计流程
  - [ ] `CustomOrderModal.vue` - 定制订单编辑

**交付物**:
- 非遗工艺知识库
- 艺术品档案管理系统
- 定制订单管理功能

---

### Week 16: 排班管理与运营支持

#### 排班管理系统
- [ ] **排班核心功能** (2天)
  - [ ] `ScheduleManagementService.java`
    - [ ] `createSchedule()` - 创建排班
    - [ ] `checkTimeConflict()` - 时间冲突检测
    - [ ] `calculateScheduleSalary()` - 计算排班工资
  - [ ] `ScheduleCalendar.vue` - 排班日历组件
  - [ ] `ScheduleModal.vue` - 排班编辑弹窗

- [ ] **咖啡店运营管理** (1.5天)
  - [ ] `CoffeeShopService.java`
    - [ ] `recordDailySales()` - 记录日销售额
    - [ ] `calculateCommission()` - 计算提成
    - [ ] `managePurchasing()` - 采购管理
  - [ ] `CoffeeShopDashboard.vue` - 咖啡店仪表板

- [ ] **客户关系管理扩展** (0.5天)
  - [ ] 客户档案扩展字段
  - [ ] 客户标签管理

**交付物**:
- 可视化排班系统
- 咖啡店运营管理
- 客户关系管理扩展

---

### Week 17: 系统性能优化

#### 性能优化任务
- [ ] **数据库优化** (2天)
  - [ ] 查询性能优化
    - [ ] 分析慢查询并优化
    - [ ] 创建必要的数据库索引
    - [ ] 优化复杂查询语句
  - [ ] 缓存策略实施
    - [ ] 实施Redis缓存热点数据
    - [ ] 配置查询结果缓存
    - [ ] 优化缓存更新策略

- [ ] **接口性能优化** (1.5天)
  - [ ] API响应时间优化
  - [ ] 分页查询优化
  - [ ] 异步处理耗时操作
  - [ ] 接口并发性能测试

- [ ] **前端性能优化** (0.5天)
  - [ ] 组件懒加载优化
  - [ ] 静态资源压缩
  - [ ] 页面加载性能优化

**交付物**:
- 性能优化报告
- 系统响应时间提升证明
- 并发处理能力验证

---

### Week 18: 用户体验提升与最终测试

#### 用户体验优化
- [ ] **界面优化** (1.5天)
  - [ ] 界面交互优化
  - [ ] 移动端用户体验优化
  - [ ] 错误提示和帮助信息完善
  - [ ] 操作流程简化

- [ ] **功能完善** (1天)
  - [ ] 数据导入导出功能
  - [ ] 操作日志记录
  - [ ] 系统配置管理
  - [ ] 权限控制细化

- [ ] **最终测试** (1.5天)
  - [ ] 全功能回归测试
  - [ ] 用户验收测试准备
  - [ ] 性能压力测试
  - [ ] 安全测试验证

- [ ] **文档整理** (1天)
  - [ ] 用户操作手册
  - [ ] 系统管理员手册
  - [ ] API接口文档
  - [ ] 部署运维文档

**交付物**:
- 完整的系统功能
- 用户操作手册
- 技术文档
- 验收测试报告

---

## 部署与运维任务

### 阿里云环境配置
- [ ] **环境准备** (1天)
  - [ ] 阿里云ECS服务器配置检查
  - [ ] 数据库环境准备
  - [ ] Redis缓存配置
  - [ ] Nginx配置优化

- [ ] **自动化部署脚本** (2天)
  - [ ] 编写自动化部署脚本
  - [ ] 配置蓝绿部署策略
  - [ ] 设置滚动更新机制
  - [ ] 健康检查配置

- [ ] **监控告警设置** (1天)
  - [ ] 系统监控指标配置
  - [ ] 业务监控指标设置
  - [ ] 告警规则配置
  - [ ] 日志收集配置

**交付物**:
- 生产环境部署
- 自动化运维脚本
- 监控告警系统

---

## 任务管理规范

### 任务状态定义
- **未开始**: 任务尚未开始
- **进行中**: 任务正在进行
- **待审核**: 任务完成待代码审核
- **已完成**: 任务完成并通过审核
- **已阻塞**: 任务遇到阻塞问题

### 完成标准
每个任务的完成需要满足以下标准：
1. **功能完整**: 实现设计要求的所有功能点
2. **代码质量**: 通过代码审核，符合编码规范
3. **测试通过**: 单元测试和集成测试通过
4. **文档完善**: 相关技术文档和接口文档更新

### 风险控制
- **每周进度检查**: 每周五进行进度检查和风险评估
- **阻塞问题升级**: 超过1天的阻塞问题需要升级处理
- **质量门禁**: 每个阶段结束需要通过质量检查
- **变更管理**: 需求变更需要评估影响并调整计划

---

## 项目里程碑

| 里程碑 | 时间节点 | 关键交付物 | 验收标准 |
|--------|----------|------------|----------|
| **M1: 数据库设计完成** | Week 1 | 数据库设计文档、初始化脚本 | 数据库结构审核通过 |
| **M2: 生产模块后端完成** | Week 4 | 完整的生产管理API | API接口测试通过 |
| **M3: 移动端开发完成** | Week 5 | 移动端H5应用 | 移动端功能测试通过 |
| **M4: 第一阶段完成** | Week 8 | 生产制作管理模块 | 用户验收测试通过 |
| **M5: 第二阶段完成** | Week 14 | 业务支撑模块 | 集成测试通过 |
| **M6: 项目完成** | Week 18 | 完整的ERP系统 | 最终验收通过 |

---

## 资源需求

### 人员配置
- **后端开发**: 2人
- **前端开发**: 2人  
- **测试工程师**: 1人
- **项目经理**: 1人

### 技术栈要求
- **后端**: Java 8, Spring Boot, MyBatis, MySQL, Redis
- **前端**: Vue.js 2.6, Ant Design Vue, Axios
- **移动端**: Vue.js + H5, Vant UI
- **部署**: Docker, Nginx, 阿里云ECS

### 环境要求
- **开发环境**: 本地开发环境 + 测试服务器
- **测试环境**: 阿里云测试环境
- **生产环境**: 阿里云生产环境

---

**文档结束**

> 本TodoList将作为聆花文化ERP二次开发项目的详细执行计划，每个任务都有明确的完成标准和交付物。项目团队应严格按照此计划执行，并定期更新任务状态和进度报告。