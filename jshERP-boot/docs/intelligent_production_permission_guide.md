# jshERP 智能生产管理模块权限配置指南

## 📋 概述

本文档详细说明了智能生产管理模块的菜单权限配置，包括菜单结构、权限分配、按钮权限等内容。

## 🎯 菜单结构

### 一级菜单
- **编号**: 06
- **名称**: 生产管理
- **说明**: 智能生产管理模块的主菜单

### 二级菜单

#### 1. 生产工单管理 (0605)
- **功能ID**: 295
- **URL路径**: `/production/work-order`
- **Vue组件**: `/production/ProductionWorkOrder`
- **图标**: `file-text`
- **按钮权限**: `1,2,3,5,6,7`
  - 1: 新增工单
  - 2: 审核工单
  - 3: 导出数据
  - 5: 打印工单
  - 6: 作废工单
  - 7: 反审核

#### 2. 生产任务管理 (0606)
- **功能ID**: 296
- **URL路径**: `/production/task`
- **Vue组件**: `/production/ProductionTask`
- **图标**: `schedule`
- **按钮权限**: `1,2,3,5,6,7`
  - 1: 新增任务
  - 2: 审核任务
  - 3: 导出数据
  - 5: 打印任务
  - 6: 作废任务
  - 7: 反审核

#### 3. 生产报工管理 (0607)
- **功能ID**: 297
- **URL路径**: `/production/report`
- **Vue组件**: `/production/ProductionReport`
- **图标**: `form`
- **按钮权限**: `1,2,3,5,6,7`
  - 1: 新增报工
  - 2: 审核报工
  - 3: 导出数据
  - 5: 打印报工
  - 6: 作废报工
  - 7: 反审核

#### 4. 质量检验管理 (0608)
- **功能ID**: 298
- **URL路径**: `/production/quality`
- **Vue组件**: `/production/QualityInspection`
- **图标**: `safety-certificate`
- **按钮权限**: `1,2,3,5,6,7`
  - 1: 新增质检
  - 2: 审核质检
  - 3: 导出数据
  - 5: 打印质检
  - 6: 作废质检
  - 7: 反审核

#### 5. 工人技能管理 (0609)
- **功能ID**: 299
- **URL路径**: `/production/worker-skill`
- **Vue组件**: `/production/WorkerSkill`
- **图标**: `team`
- **按钮权限**: `1,2,3,5,6,7`
  - 1: 新增技能
  - 2: 审核技能
  - 3: 导出数据
  - 5: 打印技能
  - 6: 作废技能
  - 7: 反审核

## 🔐 权限分配

### 已配置角色

#### 管理员 (角色ID: 4)
- **权限状态**: ✅ 已分配所有智能生产管理功能权限
- **功能权限**: [295][296][297][298][299]
- **说明**: 拥有所有智能生产管理模块的完整权限

#### 聆花管理员 (角色ID: 21)
- **权限状态**: ✅ 已分配所有智能生产管理功能权限
- **功能权限**: [295][296][297][298][299]
- **说明**: 拥有所有智能生产管理模块的完整权限

### 权限分配方法

#### 1. 为新角色分配权限
```sql
-- 为指定角色添加智能生产管理权限
UPDATE jsh_user_business 
SET value = CONCAT(
    IFNULL(value, ''), 
    '[295][296][297][298][299]'
)
WHERE type = 'RoleFunctions' 
AND key_id = '角色ID'   -- 替换为实际角色ID
AND delete_flag = '0';
```

#### 2. 为角色分配部分权限
```sql
-- 只分配生产工单和任务管理权限
UPDATE jsh_user_business 
SET value = CONCAT(
    IFNULL(value, ''), 
    '[295][296]'
)
WHERE type = 'RoleFunctions' 
AND key_id = '角色ID'
AND delete_flag = '0';
```

#### 3. 移除权限
```sql
-- 移除智能生产管理权限
UPDATE jsh_user_business 
SET value = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
    value, 
    '[295]', ''), 
    '[296]', ''), 
    '[297]', ''), 
    '[298]', ''), 
    '[299]', '')
WHERE type = 'RoleFunctions' 
AND delete_flag = '0';
```

## 🎨 按钮权限说明

### 标准按钮权限编码
- **1**: 新增 (Add)
- **2**: 审核 (Audit)
- **3**: 导出 (Export)
- **4**: 编辑 (Edit) - 未使用
- **5**: 打印 (Print)
- **6**: 作废 (Void)
- **7**: 反审核 (Reverse Audit)

### 自定义按钮权限
可以根据业务需求添加自定义按钮权限：
- **8**: 分配任务
- **9**: 开始任务
- **10**: 完成任务
- **11**: 质检确认
- **12**: 技能认证

## 🔧 配置验证

### 1. 验证菜单创建
```sql
-- 检查菜单是否创建成功
SELECT 
    number as 菜单编号,
    name as 菜单名称,
    url as URL路径,
    component as 组件路径,
    enabled as 是否启用
FROM jsh_function 
WHERE number IN ('0605', '0606', '0607', '0608', '0609')
AND delete_flag = '0'
ORDER BY number;
```

### 2. 验证权限分配
```sql
-- 检查角色权限分配
SELECT 
    r.name as 角色名称,
    CASE WHEN ub.value LIKE '%[295]%' THEN '✓' ELSE '✗' END as 生产工单管理,
    CASE WHEN ub.value LIKE '%[296]%' THEN '✓' ELSE '✗' END as 生产任务管理,
    CASE WHEN ub.value LIKE '%[297]%' THEN '✓' ELSE '✗' END as 生产报工管理,
    CASE WHEN ub.value LIKE '%[298]%' THEN '✓' ELSE '✗' END as 质量检验管理,
    CASE WHEN ub.value LIKE '%[299]%' THEN '✓' ELSE '✗' END as 工人技能管理
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions'
WHERE r.delete_flag = '0'
ORDER BY r.id;
```

### 3. 验证URL唯一性
```sql
-- 检查URL是否有冲突
SELECT 
    url as URL路径,
    COUNT(*) as 重复数量,
    GROUP_CONCAT(name) as 菜单名称列表
FROM jsh_function 
WHERE url IS NOT NULL 
AND url != '' 
AND delete_flag = '0'
GROUP BY url
HAVING COUNT(*) > 1;
```

## 🚀 部署后操作

### 1. 清理浏览器缓存
用户需要清理浏览器缓存或强制刷新页面以看到新菜单。

### 2. 重新登录
建议用户重新登录系统以刷新权限缓存。

### 3. 验证菜单显示
登录后检查"生产管理"菜单下是否显示新增的5个子菜单。

## 🔄 回滚操作

如果需要回滚菜单配置，可以执行以下操作：

### 1. 删除菜单
```sql
-- 逻辑删除新创建的菜单
UPDATE jsh_function 
SET delete_flag = '1'
WHERE number IN ('0605', '0606', '0607', '0608', '0609')
AND delete_flag = '0';
```

### 2. 移除权限
```sql
-- 从所有角色中移除权限
UPDATE jsh_user_business 
SET value = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
    value, 
    '[295]', ''), 
    '[296]', ''), 
    '[297]', ''), 
    '[298]', ''), 
    '[299]', '')
WHERE type = 'RoleFunctions' 
AND delete_flag = '0';
```

## 📞 技术支持

如有权限配置相关问题，请联系技术支持团队。

---

**配置完成时间**: 2025-06-22  
**配置版本**: v1.0  
**维护人员**: jshERP开发团队
