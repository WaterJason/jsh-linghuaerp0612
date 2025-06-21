# jshERP 菜单栏设置与权限配置完整指南

## 📋 概述

本文档基于jshERP系统的实际二次开发经验，详细说明如何正确设置菜单栏和配置权限，是jshERP二次开发的重要参考文档。

## 🏗️ 菜单系统架构

### 核心组件关系
```
数据库层 (jsh_function) 
    ↓
权限层 (jsh_user_business + jsh_role)
    ↓  
前端路由层 (动态路由生成)
    ↓
UI组件层 (SideMenu.vue + Vue组件)
```

### 关键数据表
- **jsh_function**: 功能菜单定义表
- **jsh_role**: 角色定义表  
- **jsh_user_business**: 用户业务关系表（角色权限分配）
- **jsh_user**: 用户表

## 🗂️ 菜单层级结构设计

### 编号规范
```
一级菜单: 01, 02, 03, ..., 09
二级菜单: 0101, 0102, 0201, 0202, ...
三级菜单: 010101, 010102, 020101, ...
```

### 实际案例
```
03 - 报表管理 (一级菜单)
├── 0301 - 库存报表 (二级菜单)
│   ├── 030112 - 库存预警 (三级菜单)
│   └── 030113 - 商品库存 (三级菜单)
└── 0302 - 财务报表 (二级菜单)

09 - 盘点业务 (一级菜单)  
├── 0901 - 库存盘点 (二级菜单)
├── 0902 - 盘点统计 (二级菜单)
└── 0903 - 盘点设置 (二级菜单)
```

## 📊 数据库操作规范

### 1. 创建一级菜单
```sql
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '09',                    -- 菜单编号
    '盘点业务',              -- 菜单名称
    '0',                     -- 父菜单编号（0表示一级菜单）
    '/inventory_business',   -- URL路径（唯一）
    '/layouts/TabLayout',    -- 组件路径（一级菜单固定）
    0,                       -- 状态
    '0900',                  -- 排序号
    1,                       -- 是否启用
    '电脑版',                -- 类型
    '',                      -- 按钮权限（一级菜单为空）
    'audit',                 -- 图标
    '0'                      -- 删除标志
);
```

### 2. 创建二级菜单
```sql
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0901',                           -- 菜单编号
    '库存盘点',                       -- 菜单名称  
    '09',                             -- 父菜单编号
    '/inventory/InventoryCheckList',  -- URL路径
    '/inventory/InventoryCheckList',  -- Vue组件路径
    0,                                -- 状态
    '0901',                           -- 排序号
    1,                                -- 是否启用
    '电脑版',                         -- 类型
    '1,2,3,5,6,7',                   -- 按钮权限
    'database',                       -- 图标
    '0'                               -- 删除标志
);
```

### 3. 按钮权限配置
```sql
-- 权限字符串说明
'1,2,3,5,6,7'
-- 1=新增, 2=审核, 3=导出, 4=启用禁用, 5=打印, 6=作废, 7=反审核
```

## 👥 角色权限配置

### 1. 查看用户角色关系
```sql
SELECT 
    u.id as user_id,
    u.username,
    u.login_name,
    ub.value as role_ids,
    r.name as role_name
FROM jsh_user u
LEFT JOIN jsh_user_business ub ON u.id = ub.key_id AND ub.type = 'UserRole'
LEFT JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub.value, '[', ''), ']', ''))
WHERE u.delete_flag = '0' 
ORDER BY u.id;
```

### 2. 查看角色功能权限
```sql
SELECT 
    r.id,
    r.name as role_name,
    ub.value as function_ids
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions'
WHERE r.delete_flag = '0' AND ub.delete_flag = '0'
ORDER BY r.id;
```

### 3. 为角色分配功能权限
```sql
-- 方法1：追加权限
UPDATE jsh_user_business 
SET value = CONCAT(value, '[功能ID1][功能ID2][功能ID3]')
WHERE type = 'RoleFunctions' 
AND key_id = '角色ID'
AND delete_flag = '0'
AND value NOT LIKE '%[功能ID1]%';

-- 方法2：创建新权限记录
INSERT INTO jsh_user_business (type, key_id, value, delete_flag)
VALUES ('RoleFunctions', '角色ID', '[功能ID1][功能ID2]', '0');
```

## 🎯 jshERP角色体系

### 角色类型说明
| 角色ID | 角色名称 | 用户类型 | 权限范围 |
|--------|----------|----------|----------|
| 4 | 管理员 | admin | 平台管理员，管理租户 |
| 10 | 租户 | waterxi等 | 租户管理员，业务管理 |
| 21 | 聆花管理员 | jsh等 | 系统管理员 |
| 22 | 合伙人 | 业务用户 | 有限业务权限 |

### 权限分配原则
1. **admin用户**：平台级权限，主要用于租户管理
2. **租户用户**：业务级权限，日常业务操作
3. **功能权限**：优先分配给租户角色
4. **特殊权限**：根据业务需要分配给特定角色

## 🔧 前端组件开发

### 1. Vue组件路径规范
```
/views/模块名/组件名.vue
例如：/inventory/InventoryCheckList.vue
```

### 2. API调用规范
```javascript
// 在Vue组件中定义API路径
url: {
  list: "/inventoryCheck/list",
  delete: "/inventoryCheck/delete", 
  add: "/inventoryCheck/add",
  update: "/inventoryCheck/update"
}
```

### 3. 权限控制指令
```html
<!-- 按钮权限控制 -->
<a-button v-has="'inventory_check:add'">新增</a-button>
<a-button v-has="'inventory_check:edit'">编辑</a-button>
<a-button v-has="'inventory_check:delete'">删除</a-button>
```

## 🚀 完整操作流程

### 新增菜单功能的标准流程

#### 第一步：设计菜单结构
1. 确定菜单层级关系
2. 分配菜单编号
3. 设计URL和组件路径

#### 第二步：数据库操作
1. 创建父菜单（如果需要）
2. 创建子菜单功能记录
3. 配置按钮权限

#### 第三步：权限分配
1. 确定目标角色
2. 为角色分配功能权限
3. 验证权限分配结果

#### 第四步：前端开发
1. 创建Vue组件
2. 配置API调用
3. 添加权限控制

#### 第五步：测试验证
1. 清除前端缓存
2. 重新登录测试
3. 验证菜单显示和功能

## ⚠️ 常见问题与解决

### 1. 菜单不显示
**原因**：权限未分配或缓存未清理
**解决**：
```javascript
// 清除缓存
localStorage.clear();
sessionStorage.clear();
location.reload();
```

### 2. 页面空白
**原因**：组件路径错误或组件不存在
**解决**：检查component字段与实际文件路径一致

### 3. API调用失败
**原因**：后端Controller不存在
**解决**：创建对应的Controller和Service

### 4. 权限控制无效
**原因**：权限字符串格式错误
**解决**：检查push_btn字段格式和前端v-has指令

## 📝 最佳实践

### 1. 命名规范
- **菜单名称**：简洁明了，符合业务语义
- **URL路径**：使用小写字母和下划线
- **组件名称**：使用PascalCase命名

### 2. 权限设计
- **最小权限原则**：只分配必要权限
- **角色继承**：合理设计角色层级
- **权限验证**：前后端双重验证

### 3. 开发流程
- **先设计后开发**：明确需求和架构
- **增量开发**：逐步完善功能
- **充分测试**：确保功能稳定

## 🔍 调试工具

### 1. 权限检查SQL
```sql
-- 检查用户权限
SELECT 
    u.username,
    r.name as role_name,
    f.name as function_name,
    f.url
FROM jsh_user u
JOIN jsh_user_business ub1 ON u.id = ub1.key_id AND ub1.type = 'UserRole'
JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub1.value, '[', ''), ']', ''))
JOIN jsh_user_business ub2 ON r.id = ub2.key_id AND ub2.type = 'RoleFunctions'  
JOIN jsh_function f ON FIND_IN_SET(f.id, REPLACE(REPLACE(ub2.value, '[', ''), ']', ''))
WHERE u.username = 'waterxi' AND f.delete_flag = '0'
ORDER BY f.number;
```

### 2. 前端调试代码
```javascript
// 检查当前用户权限
console.log('用户权限:', this.$store.getters.permissionList);
console.log('按钮权限:', this.$ls.get('winBtnStrList'));
```

## 🔄 动态路由生成机制

### 路由生成流程
```javascript
// 1. 用户登录后获取权限列表
this.$store.dispatch('GetPermissionList')

// 2. 根据权限生成动态路由
generateIndexRouter(permissionList)

// 3. 添加到Vue Router
this.$router.addRoutes(dynamicRoutes)
```

### 关键文件
- **permission.js**: 路由守卫和权限初始化
- **util.js**: 动态路由生成工具函数
- **store/modules/user.js**: 用户权限状态管理

## 🎨 前端权限控制

### 1. 菜单权限控制
```javascript
// SideMenu.vue 中的权限过滤
computed: {
  filteredMenus() {
    return this.filterMenuByPermission(this.menus, this.permissionList);
  }
}
```

### 2. 按钮权限控制
```javascript
// 自定义指令 v-has
Vue.directive('has', {
  bind(el, binding, vnode) {
    const permission = binding.value;
    const hasPermission = checkPermission(permission);
    if (!hasPermission) {
      el.style.display = 'none';
    }
  }
});
```

### 3. 权限验证函数
```javascript
// 检查按钮权限
function checkPermission(permission) {
  const btnStrList = Vue.ls.get('winBtnStrList') || [];
  const [module, action] = permission.split(':');

  return btnStrList.some(btn =>
    btn.url.includes(module) &&
    btn.btnStr.includes(getActionCode(action))
  );
}
```

## 🔧 后端API开发规范

### 1. Controller层规范
```java
@RestController
@RequestMapping("/inventoryCheck")
@Api(tags = "库存盘点管理")
public class InventoryCheckController {

    @GetMapping("/list")
    @ApiOperation("获取盘点单列表")
    public ResponseInfo getList(@RequestParam Map<String, Object> params) {
        // 实现逻辑
    }

    @PostMapping("/add")
    @ApiOperation("新增盘点单")
    public ResponseInfo add(@RequestBody InventoryCheck entity) {
        // 实现逻辑
    }
}
```

### 2. 权限注解
```java
@PreAuthorize("hasPermission('inventory_check:add')")
@PostMapping("/add")
public ResponseInfo add(@RequestBody InventoryCheck entity) {
    // 方法实现
}
```

### 3. 多租户数据隔离
```java
// 在Service层添加租户过滤
public List<InventoryCheck> getList(Map<String, Object> params) {
    String tenantId = getCurrentTenantId();
    params.put("tenantId", tenantId);
    return mapper.selectList(params);
}
```

## 📋 实战案例：盘点业务模块

### 完整实现步骤

#### 1. 数据库设计
```sql
-- 创建盘点业务菜单结构
INSERT INTO jsh_function VALUES
(273, '09', '盘点业务', '0', '/inventory_business', '/layouts/TabLayout', 0, '0900', 1, '电脑版', '', 'audit', '0'),
(270, '0901', '库存盘点', '09', '/inventory/InventoryCheckList', '/inventory/InventoryCheckList', 0, '0901', 1, '电脑版', '1,2,3,5,6,7', 'database', '0'),
(274, '0902', '盘点统计', '09', '/inventory/statistics', '/inventory/InventoryStatistics', 0, '0902', 1, '电脑版', '3', 'bar-chart', '0'),
(275, '0903', '盘点设置', '09', '/inventory/settings', '/inventory/InventorySettings', 0, '0903', 1, '电脑版', '1', 'setting', '0');
```

#### 2. 权限分配
```sql
-- 为租户角色分配权限
UPDATE jsh_user_business
SET value = CONCAT(value, '[270][273][274][275]')
WHERE type = 'RoleFunctions' AND key_id = '10';
```

#### 3. 前端组件开发
```vue
<!-- InventoryCheckList.vue -->
<template>
  <a-card>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <!-- 搜索表单 -->
    </div>

    <!-- 操作按钮 -->
    <div class="table-operator">
      <a-button @click="handleAdd" v-has="'inventory_check:add'">新增</a-button>
    </div>

    <!-- 数据表格 -->
    <a-table :columns="columns" :dataSource="dataSource">
      <!-- 表格内容 -->
    </a-table>
  </a-card>
</template>
```

## 🚨 安全注意事项

### 1. 权限验证
- **前端验证**：仅用于UI控制，不能作为安全依据
- **后端验证**：必须在每个API接口进行权限验证
- **数据隔离**：确保多租户数据安全隔离

### 2. SQL注入防护
```java
// 使用参数化查询
@Select("SELECT * FROM jsh_function WHERE name = #{name} AND delete_flag = '0'")
List<Function> selectByName(@Param("name") String name);
```

### 3. 权限提升防护
```java
// 检查用户是否有权限修改目标角色
public void assignRole(Long userId, Long roleId) {
    if (!hasPermissionToAssignRole(getCurrentUser(), roleId)) {
        throw new SecurityException("权限不足");
    }
    // 执行分配逻辑
}
```

## 📈 性能优化

### 1. 权限缓存
```javascript
// 缓存用户权限数据
Vue.ls.set('permissionList', permissionData, 7 * 24 * 60 * 60 * 1000); // 7天
Vue.ls.set('winBtnStrList', buttonPermissions, 7 * 24 * 60 * 60 * 1000);
```

### 2. 菜单懒加载
```javascript
// 动态导入组件
const InventoryCheckList = () => import('@/views/inventory/InventoryCheckList');
```

### 3. 权限预加载
```javascript
// 在应用启动时预加载权限数据
async created() {
  await this.$store.dispatch('GetPermissionList');
  await this.$store.dispatch('GetUserBtnList');
}
```

## 📚 总结

菜单栏设置是jshERP二次开发的核心技能，掌握以下要点：

1. **理解架构**：数据库→权限→路由→组件的完整链路
2. **规范操作**：遵循编号规范和命名规范
3. **权限控制**：正确配置角色权限和按钮权限
4. **安全意识**：前后端双重验证，防止权限绕过
5. **性能优化**：合理使用缓存和懒加载
6. **测试验证**：确保功能完整可用
7. **文档记录**：保持开发文档的完整性

### 关键成功因素
- **系统性思维**：理解整个权限体系的运作机制
- **细节把控**：注意编号、路径、权限字符串的准确性
- **用户体验**：确保权限控制不影响正常使用流程
- **安全第一**：始终以安全为前提进行权限设计

通过本指南，开发者可以系统性地掌握jshERP菜单系统的开发和维护技能，为企业级应用的二次开发奠定坚实基础。
