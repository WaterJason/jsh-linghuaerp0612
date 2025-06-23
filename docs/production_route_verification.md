# jshERP 生产管理模块路由验证指南

## 📋 路由配置概览

jshERP使用动态路由生成机制，路由会根据数据库菜单配置自动生成。

### 已配置的菜单结构

```
06 生产管理 (/production)
├── 0601 生产订单 (/production/order)
├── 0602 崇左生产看板 (/production/kanban)
└── 0604 后工任务列表 (/production/post-task)
```

## 🔧 路由生成机制

### 1. 动态路由生成流程
```
用户登录 → 获取菜单权限 → 动态生成路由 → 注册到Vue Router
```

### 2. 关键文件
- **`src/permission.js`** - 路由守卫和权限验证
- **`src/utils/util.js`** - `generateIndexRouter()` 和 `generateChildRouters()` 函数
- **`src/store/modules/permission.js`** - 路由状态管理

### 3. 组件路径解析规则
```javascript
// 在 generateChildRouters 函数中
if(item.component.indexOf("layouts")>=0){
  componentPath = () => import('@/components'+item.component);
} else {
  componentPath = () => import('@/views'+item.component);
}
```

## ✅ 验证步骤

### 1. 检查菜单数据
```sql
SELECT number, name, parent_number, url, component 
FROM jsh_function 
WHERE (number = '06' OR parent_number = '06') AND delete_flag = '0' 
ORDER BY number;
```

**预期结果：**
```
06    | 生产管理      | 0  | /production      | /layouts/TabLayout
0601  | 生产订单      | 06 | /production/order | production/ProductionOrderList
0602  | 崇左生产看板  | 06 | /production/kanban | production/ChongzuoKanban
0604  | 后工任务列表  | 06 | /production/post-task | production/PostProcessingTaskList
```

### 2. 检查Vue组件文件
确认以下文件存在：
- ✅ `jshERP-web/src/views/production/ProductionOrderList.vue`
- ✅ `jshERP-web/src/views/production/ChongzuoKanban.vue`
- ✅ `jshERP-web/src/views/production/PostProcessingTaskList.vue`

### 3. 检查用户权限
```sql
-- 检查管理员角色权限
SELECT value FROM jsh_user_business 
WHERE type = 'RoleFunctions' AND key_id = '4' AND delete_flag = '0';
```

确认权限字符串包含菜单ID：`[281][283][284][285]`

### 4. 前端路由验证

#### 方法1：浏览器开发者工具
1. 登录系统后，按F12打开开发者工具
2. 在Console中输入：
```javascript
console.log('当前路由：', this.$router.options.routes)
console.log('动态路由：', this.$store.getters.addRouters)
```

#### 方法2：直接访问URL
测试以下URL是否能正常访问：
- http://localhost:8080/production/order
- http://localhost:8080/production/kanban
- http://localhost:8080/production/post-task

#### 方法3：检查菜单显示
登录后检查左侧菜单是否显示"生产管理"及其子菜单。

## 🔍 故障排除

### 问题1：菜单不显示
**可能原因：**
- 用户没有相应菜单权限
- 菜单数据配置错误

**解决方案：**
```sql
-- 检查用户角色
SELECT * FROM jsh_user_business WHERE key_id = '120' AND type = 'UserRole';

-- 检查角色权限
SELECT * FROM jsh_user_business WHERE key_id = '4' AND type = 'RoleFunctions';
```

### 问题2：路由404错误
**可能原因：**
- Vue组件文件路径错误
- 组件文件有语法错误

**解决方案：**
1. 检查组件文件是否存在
2. 检查组件语法是否正确
3. 查看浏览器控制台错误信息

### 问题3：路由生成失败
**可能原因：**
- 菜单数据格式错误
- 权限获取失败

**解决方案：**
1. 清理浏览器缓存
2. 重新登录系统
3. 检查网络请求是否成功

## 🧪 测试用例

### 测试用例1：菜单权限测试
1. 使用管理员账号登录
2. 检查是否能看到"生产管理"菜单
3. 点击各子菜单是否能正常跳转

### 测试用例2：路由跳转测试
1. 直接在地址栏输入 `/production/kanban`
2. 检查是否能正常加载崇左生产看板页面
3. 检查页面功能是否正常

### 测试用例3：权限控制测试
1. 创建一个没有生产管理权限的用户
2. 登录后检查是否看不到生产管理菜单
3. 直接访问URL是否被拦截

## 📊 预期的路由结构

动态生成的路由应该类似于：

```javascript
{
  path: '/production',
  name: '生产管理',
  component: () => import('@/components/layouts/TabLayout'),
  meta: {
    title: '生产管理',
    icon: 'production',
    keepAlive: true
  },
  children: [
    {
      path: '/production/order',
      name: '生产订单',
      component: () => import('@/views/production/ProductionOrderList'),
      meta: {
        title: '生产订单',
        icon: 'file-text',
        keepAlive: true
      }
    },
    {
      path: '/production/kanban',
      name: '崇左生产看板',
      component: () => import('@/views/production/ChongzuoKanban'),
      meta: {
        title: '崇左生产看板',
        icon: 'dashboard',
        keepAlive: true
      }
    },
    {
      path: '/production/post-task',
      name: '后工任务列表',
      component: () => import('@/views/production/PostProcessingTaskList'),
      meta: {
        title: '后工任务列表',
        icon: 'tool',
        keepAlive: true
      }
    }
  ]
}
```

## 🎯 验证清单

- [ ] 菜单数据已正确插入数据库
- [ ] 用户权限已正确配置
- [ ] Vue组件文件存在且无语法错误
- [ ] 能够在菜单中看到生产管理模块
- [ ] 能够正常跳转到各个子页面
- [ ] 页面功能正常工作
- [ ] API接口调用正常

## 📝 注意事项

1. **缓存问题**：修改菜单配置后需要清理浏览器缓存并重新登录
2. **权限问题**：确保用户有相应的菜单访问权限
3. **组件错误**：Vue组件语法错误会导致路由加载失败
4. **开发模式**：开发环境下热重载可能需要手动刷新页面
