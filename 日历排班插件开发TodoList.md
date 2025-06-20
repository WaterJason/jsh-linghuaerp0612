# 日历排班插件开发 TodoList

## 📋 项目概述

**插件名称**: 日历排班插件 (Calendar Schedule Plugin)  
**业务场景**: 聆花掐丝珐琅馆排班管理  
**核心功能**: 班次设置、日历排班、人员管理  
**开发协议**: RIPER-5 (RESEARCH→INNOVATE→PLAN→EXECUTE→REVIEW)

## 🎯 功能需求

### 核心功能
- **班次设置**: 全天班配置和管理
- **日历排班**: 按日历视图点击排班，支持批量选择日期
- **人员管理**: 日历视图显示排班人员名字，支持多人同日排班
- **数据集成**: 使用jshERP"用户"模块的人员数据

### 技术要求
- 基于jshERP插件开发技术规范
- 使用Ant Design Vue Calendar组件
- 支持多租户数据隔离
- 集成jshERP权限体系

## 📅 开发任务清单

### 第一阶段：项目初始化和环境搭建 (1-2天)

#### ✅ 任务1：项目结构创建
- [ ] 1.1 基于模板创建插件项目结构
- [ ] 1.2 配置 `plugin.properties` 文件
  ```properties
  plugin.id=calendar-schedule-plugin
  plugin.name=日历排班插件
  plugin.description=聆花掐丝珐琅馆日历排班管理系统
  plugin.version=1.0.0
  plugin.provider=聆花掐丝珐琅馆
  plugin.requires=3.5.0
  plugin.class=com.linghua.plugin.schedule.CalendarSchedulePluginApplication
  ```
- [ ] 1.3 配置 `pom.xml` Maven依赖
- [ ] 1.4 创建插件主类 `CalendarSchedulePluginApplication.java`

#### ✅ 任务2：开发环境配置
- [ ] 2.1 配置开发环境（JDK 1.8、Maven、Node.js）
- [ ] 2.2 导入 jshERP 主项目依赖
- [ ] 2.3 配置数据库连接
- [ ] 2.4 验证插件框架基础功能

### 第二阶段：数据库设计和实体创建 (2-3天)

#### ✅ 任务3：数据库表设计
- [ ] 3.1 设计班次配置表 `jsh_schedule_shift`
  ```sql
  CREATE TABLE `jsh_schedule_shift` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `shift_name` VARCHAR(50) NOT NULL COMMENT '班次名称',
    `shift_type` VARCHAR(20) DEFAULT 'FULL_DAY' COMMENT '班次类型',
    `start_time` TIME COMMENT '开始时间',
    `end_time` TIME COMMENT '结束时间', 
    `duration_hours` DECIMAL(4,2) COMMENT '班次时长',
    `is_active` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `description` TEXT COMMENT '描述',
    
    -- 标准字段
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
    `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT(20) COMMENT '创建人ID',
    `update_by` BIGINT(20) COMMENT '更新人ID',
    
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_delete_flag` (`delete_flag`),
    INDEX `idx_shift_type` (`shift_type`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班班次配置表';
  ```
- [ ] 3.2 设计排班记录表 `jsh_schedule_assignment`
  ```sql
  CREATE TABLE `jsh_schedule_assignment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `schedule_date` DATE NOT NULL COMMENT '排班日期',
    `shift_id` BIGINT(20) NOT NULL COMMENT '班次ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `user_name` VARCHAR(50) COMMENT '用户姓名',
    `status` VARCHAR(20) DEFAULT 'SCHEDULED' COMMENT '排班状态',
    `notes` TEXT COMMENT '备注',
    
    -- 标准字段
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
    `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT(20) COMMENT '创建人ID',
    `update_by` BIGINT(20) COMMENT '更新人ID',
    
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_delete_flag` (`delete_flag`),
    INDEX `idx_schedule_date` (`schedule_date`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_shift_id` (`shift_id`),
    UNIQUE KEY `uk_date_shift_user` (`schedule_date`, `shift_id`, `user_id`, `tenant_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='排班分配记录表';
  ```
- [ ] 3.3 编写数据库初始化脚本 `init.sql`
- [ ] 3.4 编写权限注册脚本

#### ✅ 任务4：实体类创建
- [ ] 4.1 创建 `ScheduleShift.java` 班次实体
- [ ] 4.2 创建 `ScheduleAssignment.java` 排班实体
- [ ] 4.3 创建 `ScheduleCalendarVO.java` 日历视图对象
- [ ] 4.4 创建常量类 `ScheduleConstants.java`

### 第三阶段：后端业务逻辑开发 (3-4天)

#### ✅ 任务5：Mapper层开发
- [ ] 5.1 创建 `ScheduleShiftMapper.java` 和 `ScheduleShiftMapperEx.java`
- [ ] 5.2 创建 `ScheduleAssignmentMapper.java` 和 `ScheduleAssignmentMapperEx.java`
- [ ] 5.3 编写 MyBatis XML 映射文件
- [ ] 5.4 实现复杂查询方法
  - 按月份获取排班数据
  - 按用户获取排班统计
  - 批量排班操作

#### ✅ 任务6：Service层开发
- [ ] 6.1 创建 `ScheduleShiftService.java`
  - 班次CRUD操作
  - 班次配置管理
- [ ] 6.2 创建 `ScheduleAssignmentService.java`
  - 排班CRUD操作
  - 批量排班功能
  - 排班冲突检查
- [ ] 6.3 创建 `ScheduleCalendarService.java`
  - 日历数据组装
  - 月视图数据获取
  - 用户排班统计

#### ✅ 任务7：Controller层开发
- [ ] 7.1 创建 `ScheduleShiftController.java`
  ```java
  - GET /shift/list - 获取班次列表
  - POST /shift/add - 新增班次
  - PUT /shift/update - 更新班次
  - DELETE /shift/{id} - 删除班次
  ```
- [ ] 7.2 创建 `ScheduleAssignmentController.java`
  ```java
  - GET /assignment/calendar - 获取日历排班数据
  - POST /assignment/add - 单日排班
  - POST /assignment/batch - 批量排班
  - DELETE /assignment/{id} - 取消排班
  ```
- [ ] 7.3 创建 `ScheduleCalendarController.java`
  ```java
  - GET /calendar/month - 获取月度日历数据
  - GET /calendar/user/{userId} - 获取用户排班
  - GET /calendar/statistics - 获取排班统计
  ```

### 第四阶段：前端界面开发 (4-5天)

#### ✅ 任务8：Vue组件开发
- [ ] 8.1 创建 `ScheduleCalendar.vue` 主日历组件
  - 使用 Ant Design Vue Calendar 组件
  - 实现月视图显示
  - 支持日期点击选择
- [ ] 8.2 创建 `ScheduleShiftManage.vue` 班次管理组件
  - 班次列表展示
  - 班次新增/编辑表单
- [ ] 8.3 创建 `ScheduleAssignmentModal.vue` 排班弹窗
  - 单日排班表单
  - 用户选择器（集成jshERP用户数据）
  - 班次选择器

#### ✅ 任务9：日历功能实现
- [ ] 9.1 实现日历日期渲染
- [ ] 9.2 实现批量日期选择
- [ ] 9.3 实现排班人员显示

#### ✅ 任务10：用户集成功能
- [ ] 10.1 集成 jshERP 用户数据
- [ ] 10.2 实现用户选择器组件
- [ ] 10.3 实现用户权限控制

### 第五阶段：高级功能开发 (2-3天)

#### ✅ 任务11：批量操作功能
- [ ] 11.1 实现批量排班
- [ ] 11.2 实现排班模板
- [ ] 11.3 实现排班复制

#### ✅ 任务12：数据统计功能
- [ ] 12.1 实现排班统计报表
- [ ] 12.2 实现排班冲突检查
- [ ] 12.3 实现排班导出功能

### 第六阶段：测试和优化 (2-3天)

#### ✅ 任务13：单元测试
- [ ] 13.1 编写 Service 层单元测试
- [ ] 13.2 编写 Controller 层集成测试
- [ ] 13.3 编写前端组件测试
- [ ] 13.4 测试覆盖率达到80%+

#### ✅ 任务14：功能测试
- [ ] 14.1 测试日历显示功能
- [ ] 14.2 测试排班CRUD操作
- [ ] 14.3 测试批量操作功能
- [ ] 14.4 测试用户权限控制
- [ ] 14.5 测试多租户数据隔离

#### ✅ 任务15：性能优化
- [ ] 15.1 优化日历数据加载
- [ ] 15.2 实现排班数据缓存
- [ ] 15.3 优化前端渲染性能
- [ ] 15.4 数据库查询优化

### 第七阶段：部署和文档 (1-2天)

#### ✅ 任务16：插件打包部署
- [ ] 16.1 配置构建脚本
- [ ] 16.2 生成插件JAR包
- [ ] 16.3 测试插件安装流程
- [ ] 16.4 验证插件功能完整性

#### ✅ 任务17：文档编写
- [ ] 17.1 编写API接口文档
- [ ] 17.2 编写用户操作手册
- [ ] 17.3 编写安装部署指南
- [ ] 17.4 编写开发技术说明

## ⏰ 预估时间

**总开发时间**: 15-20个工作日  
**团队配置**: 1名后端开发 + 1名前端开发  
**里程碑节点**:
- 第1周：完成基础框架和数据库设计
- 第2周：完成核心业务逻辑开发  
- 第3周：完成前端界面和高级功能
- 第4周：完成测试、优化和部署

## 🎯 关键技术要点

### 1. 用户数据集成
```java
// 获取jshERP用户数据
@Resource
private UserService userService;

public List<User> getActiveUsers(HttpServletRequest request) {
    return userService.getActiveUsersByTenant(getCurrentTenantId(request));
}
```

### 2. 日历组件实现
```vue
<template>
  <a-calendar v-model:value="selectedDate" @select="onDateSelect">
    <template #dateCellRender="{ current }">
      <div class="schedule-cell">
        <div v-for="assignment in getAssignments(current)" 
             :key="assignment.id" 
             class="assignment-tag">
          {{ assignment.userName }}
        </div>
      </div>
    </template>
  </a-calendar>
</template>
```

### 3. 批量排班逻辑
```java
@Transactional(rollbackFor = Exception.class)
public int batchAssignSchedule(List<String> dates, List<Long> userIds, 
                              Long shiftId, HttpServletRequest request) {
    // 批量创建排班记录
    // 检查冲突
    // 保存数据
}
```

---

**文档版本**: 1.0.0  
**创建时间**: 2023-12-01  
**开发团队**: 聆花掐丝珐琅馆技术团队
