# jshERP 生产管理模块菜单配置说明

## 📋 菜单结构设计

### 菜单层级架构
```
05 - 生产管理 (一级菜单)
├── 0501 - 生产订单 (二级菜单)
├── 0502 - 崇左生产看板 (二级菜单)  
└── 0503 - 后工任务列表 (二级菜单)
```

## 🎯 详细配置信息

### 一级菜单：生产管理
| 字段 | 值 | 说明 |
|------|-----|------|
| number | 05 | 菜单编号 |
| name | 生产管理 | 菜单名称 |
| parent_number | 0 | 一级菜单 |
| url | /production | URL路径 |
| component | layouts/RouteView | 标准布局组件 |
| icon | build | 生产/建设图标 |
| sort | 0500 | 排序号 |
| push_btn | (空) | 一级菜单无按钮权限 |

### 二级菜单详情

#### 1. 生产订单 (0501)
| 字段 | 值 | 说明 |
|------|-----|------|
| number | 0501 | 菜单编号 |
| name | 生产订单 | 菜单名称 |
| parent_number | 05 | 父菜单编号 |
| url | /production/order | URL路径 |
| component | production/ProductionOrderList | Vue组件路径 |
| icon | profile | 订单管理图标 |
| push_btn | 1,2,3,4,5,6,7 | 完整权限 |

**按钮权限说明**：
- 1 = 新增生产订单
- 2 = 审核生产订单
- 3 = 导出订单数据
- 4 = 启用/禁用订单
- 5 = 打印订单
- 6 = 作废订单
- 7 = 反审核订单

#### 2. 崇左生产看板 (0502)
| 字段 | 值 | 说明 |
|------|-----|------|
| number | 0502 | 菜单编号 |
| name | 崇左生产看板 | 菜单名称 |
| parent_number | 05 | 父菜单编号 |
| url | /production/kanban | URL路径 |
| component | production/ChongzuoKanban | Vue组件路径 |
| icon | dashboard | 看板图标 |
| push_btn | 1,3,5 | 看板权限 |

**按钮权限说明**：
- 1 = 派工操作
- 3 = 导出看板数据
- 5 = 打印看板

#### 3. 后工任务列表 (0503)
| 字段 | 值 | 说明 |
|------|-----|------|
| number | 0503 | 菜单编号 |
| name | 后工任务列表 | 菜单名称 |
| parent_number | 05 | 父菜单编号 |
| url | /production/post-task | URL路径 |
| component | production/PostProcessingTaskList | Vue组件路径 |
| icon | ordered-list | 任务列表图标 |
| push_btn | 1,2,3 | 任务权限 |

**按钮权限说明**：
- 1 = 认领任务
- 2 = 完成任务
- 3 = 导出任务数据

## 🔧 前端组件要求

### 组件文件路径
```
jshERP-web/src/views/production/
├── ProductionOrderList.vue      # 生产订单列表
├── ChongzuoKanban.vue          # 崇左生产看板
└── PostProcessingTaskList.vue   # 后工任务列表
```

### 组件开发规范
1. **继承基础组件**：使用jshERP标准的列表组件模板
2. **API路径规范**：遵循RESTful设计，如 `/production/list`
3. **权限控制**：使用 `v-has` 指令控制按钮显示
4. **多租户支持**：所有API调用自动包含租户隔离

## 🎭 权限分配策略

### 目标角色
| 角色ID | 角色名称 | 权限范围 |
|--------|----------|----------|
| 4 | 管理员 | 完整权限 |
| 10 | 租户 | 业务权限 |
| 21 | 聆花管理员 | 系统权限 |
| 22 | 合伙人 | 有限权限 |

### 权限分配原则
1. **租户角色优先**：主要业务权限分配给租户角色
2. **管理员全权限**：管理员角色拥有所有权限
3. **按需分配**：其他角色根据业务需要分配特定权限

## 📝 部署步骤

### 1. 前置条件检查
- ✅ 前端Vue组件已创建
- ✅ 后端API接口已实现
- ✅ 数据库表结构已创建

### 2. 执行部署脚本
```sql
-- 使用快速部署脚本
source docs/生产管理菜单快速部署脚本.sql;
```

### 3. 验证部署结果
```sql
-- 检查菜单创建
SELECT number, name, url FROM jsh_function 
WHERE number IN ('05', '0501', '0502', '0503') 
AND delete_flag = '0';

-- 检查权限分配
SELECT r.name, ub.value FROM jsh_role r
JOIN jsh_user_business ub ON r.id = ub.key_id 
WHERE ub.type = 'RoleFunctions' AND r.id = 10;
```

### 4. 用户端验证
1. **清除缓存**：用户需要清除浏览器缓存
2. **重新登录**：重新登录系统查看新菜单
3. **功能测试**：测试各个菜单功能是否正常

## ⚠️ 注意事项

### 编号冲突处理
如果编号"05"已被占用：
1. 查询可用编号：`SELECT MAX(CAST(number AS UNSIGNED)) + 1 FROM jsh_function WHERE parent_number = '0'`
2. 修改脚本中所有相关编号
3. 确保父子关系正确

### 组件路径验证
确保前端组件路径与数据库配置一致：
- 数据库：`production/ProductionOrderList`
- 实际文件：`src/views/production/ProductionOrderList.vue`

### 权限字符串格式
按钮权限必须使用逗号分隔的数字格式：
- ✅ 正确：`1,2,3,4,5,6,7`
- ❌ 错误：`add,edit,delete`

## 🔄 回滚方案

如需回滚菜单配置：
```sql
-- 软删除菜单
UPDATE jsh_function 
SET delete_flag = '1' 
WHERE number IN ('05', '0501', '0502', '0503');

-- 清理权限分配
UPDATE jsh_user_business 
SET value = REGEXP_REPLACE(value, '\\[[0-9]+\\]', '') 
WHERE type = 'RoleFunctions' 
AND value LIKE '%生产管理相关功能ID%';
```

## 📊 性能优化建议

### 1. 菜单缓存
- 前端缓存用户权限数据7天
- 使用localStorage存储菜单结构

### 2. 权限预加载
- 登录时一次性加载所有权限
- 避免每次页面跳转都查询权限

### 3. 组件懒加载
```javascript
// 动态导入组件
const ProductionOrderList = () => import('@/views/production/ProductionOrderList');
```

## 🎯 成功验证标准

部署成功的标志：
1. ✅ 菜单在系统中正确显示
2. ✅ 点击菜单能正确跳转到对应页面
3. ✅ 按钮权限控制正常工作
4. ✅ 多租户数据隔离正确
5. ✅ 所有CRUD操作功能正常

## 📞 技术支持

如遇到问题，请检查：
1. **前端控制台错误**：组件加载失败
2. **网络请求错误**：API接口问题
3. **权限相关错误**：权限配置问题
4. **数据库错误**：SQL执行问题

通过本配置，jshERP生产管理模块将完全集成到系统菜单中，为用户提供完整的生产管理功能。
