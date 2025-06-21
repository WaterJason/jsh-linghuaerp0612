# jshERP 生产管理模块路由配置指南

## 路由系统工作原理

jshERP 使用的是**动态路由生成机制**，不需要手动配置静态路由。路由是根据后端菜单数据自动生成的。

### 1. 动态路由生成流程

```
用户登录 → 获取菜单权限 → 动态生成路由 → 注册到Vue Router
```

#### 关键文件和函数：

1. **`src/permission.js`** - 路由守卫，负责权限验证和动态路由生成
2. **`src/utils/util.js`** - `generateIndexRouter()` 和 `generateChildRouters()` 函数
3. **`src/store/modules/permission.js`** - 路由状态管理

### 2. 路由生成规则

#### 组件路径解析规则：
```javascript
// 在 generateChildRouters 函数中
if(item.component.indexOf("layouts")>=0){
  componentPath = () => import('@/components'+item.component);
} else {
  componentPath = () => import('@/views'+item.component);
}
```

#### 菜单数据到路由的映射：
```javascript
let menu = {
  path: item.url,           // 来自菜单的 url 字段
  name: item.text,          // 来自菜单的 name 字段  
  component: componentPath, // 根据 component 字段动态导入
  meta: {
    id: item.id,
    title: item.text,
    icon: item.icon,
    url: item.url,
    componentName: componentName,
    internalOrExternal: true,
    keepAlive: true
  }
}
```

## 生产管理模块路由配置

### 1. 菜单数据结构

执行 `docs/production_menu_config.sql` 后，系统会自动生成以下菜单结构：

```sql
-- 一级菜单
INSERT INTO jsh_function VALUES ('05', '生产管理', '0', '/production', '/layouts/TabLayout', ...);

-- 二级菜单
INSERT INTO jsh_function VALUES ('0501', '生产订单', '05', '/production/order', '/production/ProductionOrderList', ...);
INSERT INTO jsh_function VALUES ('0502', '崇左生产看板', '05', '/production/kanban', '/production/ChongzuoKanban', ...);
INSERT INTO jsh_function VALUES ('0503', '后工任务列表', '05', '/production/post-task', '/production/PostProcessingTaskList', ...);
```

### 2. 自动生成的路由结构

系统会自动生成如下路由结构：

```javascript
{
  path: '/production',
  name: '生产管理',
  component: () => import('@/components/layouts/TabLayout'),
  meta: { title: '生产管理', icon: 'tool' },
  children: [
    {
      path: '/production/order',
      name: '生产订单',
      component: () => import('@/views/production/ProductionOrderList'),
      meta: { title: '生产订单', icon: 'profile' }
    },
    {
      path: '/production/kanban',
      name: '崇左生产看板',
      component: () => import('@/views/production/ChongzuoKanban'),
      meta: { title: '崇左生产看板', icon: 'dashboard' }
    },
    {
      path: '/production/post-task',
      name: '后工任务列表',
      component: () => import('@/views/production/PostProcessingTaskList'),
      meta: { title: '后工任务列表', icon: 'ordered-list' }
    }
  ]
}
```

## 配置步骤

### 1. 执行SQL脚本
```bash
# 在数据库中执行菜单配置脚本
mysql -u username -p database_name < docs/production_menu_config.sql
```

### 2. 分配菜单权限
1. 登录系统管理员账号
2. 进入 **系统管理 → 角色管理**
3. 编辑相关角色，勾选新增的生产管理菜单
4. 保存权限配置

### 3. 重新登录
用户需要重新登录系统，以获取最新的菜单权限和路由配置。

## 文件结构

确保以下Vue组件文件已正确创建：

```
jshERP-web/src/views/production/
├── ChongzuoKanban.vue              # 崇左生产看板
├── ProductionOrderList.vue         # 生产订单列表
├── PostProcessingTaskList.vue      # 后工任务列表
└── modules/
    └── ProductionOrderModal.vue    # 生产订单模态框
```

## 验证路由配置

### 1. 检查菜单数据
```sql
SELECT number, name, parent_number, url, component 
FROM jsh_function 
WHERE number IN ('05', '0501', '0502', '0503') 
ORDER BY number;
```

### 2. 检查前端路由
1. 登录系统后，按F12打开开发者工具
2. 在Console中输入：`console.log(this.$router.options.routes)`
3. 查看是否包含生产管理相关路由

### 3. 测试页面访问
直接访问以下URL测试：
- http://localhost:8080/production/order
- http://localhost:8080/production/kanban  
- http://localhost:8080/production/post-task

## 注意事项

1. **组件路径规范**：Vue组件必须放在 `src/views/` 目录下，路径要与菜单配置中的 `component` 字段一致

2. **懒加载**：所有路由组件都会自动使用懒加载模式

3. **权限控制**：路由访问受菜单权限控制，用户必须有相应菜单权限才能访问

4. **缓存清理**：修改菜单配置后，建议清理浏览器缓存并重新登录

5. **开发模式**：开发时如果路由不生效，检查Vue组件是否有语法错误

## 故障排除

### 路由不生效
1. 检查菜单数据是否正确插入
2. 检查用户角色是否有相应权限
3. 检查Vue组件文件路径是否正确
4. 清理浏览器缓存并重新登录

### 组件加载失败
1. 检查组件文件是否存在
2. 检查组件语法是否正确
3. 检查import路径是否正确

### 权限问题
1. 确认用户角色已分配相应菜单权限
2. 检查按钮权限配置是否正确
3. 验证后端接口权限配置

通过以上配置，生产管理模块的路由将自动生效，无需手动配置静态路由文件。
