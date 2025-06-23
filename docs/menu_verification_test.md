# jshERP 生产管理菜单验证测试

## 📋 菜单配置验证结果

### ✅ 数据库菜单配置
```sql
-- 菜单配置验证查询结果
SELECT id, number, name, parent_number, type, url, component, push_btn, icon, sort, state, enabled, delete_flag 
FROM jsh_function 
WHERE (number = '06' OR parent_number = '06') AND delete_flag = '0' 
ORDER BY number;
```

**验证结果**：
| ID  | 编号 | 名称         | 父编号 | URL                    | 组件                              | 按钮权限                           | 图标      | 状态 |
|-----|------|--------------|--------|------------------------|------------------------------------|-----------------------------------|-----------|------|
| 281 | 06   | 生产管理     | 0      | /production            | /layouts/TabLayout                | None                              | production| 启用 |
| 283 | 0601 | 生产订单     | 06     | /production/order      | production/ProductionOrderList    | add,edit,delete,batch_delete,check,finish | file-text | 启用 |
| 284 | 0602 | 崇左生产看板 | 06     | /production/kanban     | production/ChongzuoKanban         | dispatch,complete                 | dashboard | 启用 |
| 285 | 0604 | 后工任务列表 | 06     | /production/post-task  | production/PostProcessingTaskList | claim,complete                    | tool      | 启用 |

### ✅ 用户权限配置
```sql
-- 管理员用户角色验证
SELECT ub.id, ub.type, ub.key_id, ub.value 
FROM jsh_user_business ub
WHERE ub.type = 'UserRole' AND ub.key_id = '120' AND ub.delete_flag = '0';
```

**验证结果**：
- 用户ID: 120 (admin)
- 角色ID: [4] (管理员角色)

```sql
-- 管理员角色功能权限验证
SELECT ub.id, ub.type, ub.key_id, ub.value 
FROM jsh_user_business ub
WHERE ub.type = 'RoleFunctions' AND ub.key_id = '4' AND ub.delete_flag = '0';
```

**验证结果**：
- 权限字符串包含：`[281][283][284][285]`
- ✅ 生产管理菜单权限已正确配置

### ✅ Vue组件文件验证
```
jshERP-web/src/views/production/
├── ProductionOrderList.vue      ✅ 存在
├── ChongzuoKanban.vue          ✅ 存在
├── PostProcessingTaskList.vue  ✅ 存在
└── modules/
    └── ProductionOrderModal.vue ✅ 存在
```

### ✅ API文件验证
```
jshERP-web/src/api/production.js ✅ 存在 (304行)
```

## 🧪 菜单功能测试步骤

### 1. 前端启动测试
```bash
# 进入前端目录
cd /Users/macmini/Desktop/jshERP-0612-Cursor/jshERP-web

# 启动前端服务
npm run serve
```

**预期结果**：
- 前端服务在 http://localhost:8080 启动成功
- 无编译错误
- Vue组件正常加载

### 2. 后端启动测试
```bash
# 进入后端目录
cd /Users/macmini/Desktop/jshERP-0612-Cursor/jshERP-boot

# 启动后端服务
mvn spring-boot:run
```

**预期结果**：
- 后端服务在 http://localhost:9999 启动成功
- 数据库连接正常
- API接口可访问

### 3. 登录测试
1. 访问 http://localhost:8080
2. 使用管理员账号登录：
   - 用户名：admin
   - 密码：123456

**预期结果**：
- 登录成功
- 获取用户权限列表
- 动态路由生成成功

### 4. 菜单显示测试
登录后检查左侧菜单：

**预期显示**：
```
📋 生产管理
├── 📄 生产订单
├── 📊 崇左生产看板
└── 🔧 后工任务列表
```

### 5. 页面跳转测试

#### 5.1 生产订单页面
- **URL**: http://localhost:8080/production/order
- **组件**: ProductionOrderList.vue
- **预期功能**: 生产订单列表、新增、编辑、删除

#### 5.2 崇左生产看板页面
- **URL**: http://localhost:8080/production/kanban
- **组件**: ChongzuoKanban.vue
- **预期功能**: 三列看板、派单、完工

#### 5.3 后工任务列表页面
- **URL**: http://localhost:8080/production/post-task
- **组件**: PostProcessingTaskList.vue
- **预期功能**: 任务列表、认领、完成

### 6. API接口测试

#### 6.1 生产订单API测试
```bash
# 获取生产订单列表
curl -X GET "http://localhost:9999/jshERP-boot/production/list" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 智能生成工单
curl -X POST "http://localhost:9999/jshERP-boot/production/generateFromOrder?saleOrderId=278" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

#### 6.2 工单API测试
```bash
# 获取看板数据
curl -X GET "http://localhost:9999/jshERP-boot/workOrder/kanbanData" \
  -H "Authorization: Bearer YOUR_TOKEN"

# 获取看板工单列表
curl -X GET "http://localhost:9999/jshERP-boot/workOrder/kanbanList?status=PENDING" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 🔍 故障排除指南

### 问题1: 菜单不显示
**可能原因**：
- 用户权限配置错误
- 菜单数据配置错误
- 前端缓存问题

**解决方案**：
```sql
-- 检查用户权限
SELECT * FROM jsh_user_business WHERE key_id = '120' AND type = 'UserRole';

-- 检查角色权限
SELECT * FROM jsh_user_business WHERE key_id = '4' AND type = 'RoleFunctions';

-- 清理浏览器缓存并重新登录
```

### 问题2: 页面404错误
**可能原因**：
- Vue组件文件不存在
- 路由配置错误
- 组件语法错误

**解决方案**：
```bash
# 检查组件文件
ls -la jshERP-web/src/views/production/

# 检查组件语法
npm run lint

# 查看浏览器控制台错误
```

### 问题3: API调用失败
**可能原因**：
- 后端服务未启动
- API路径错误
- 权限验证失败

**解决方案**：
```bash
# 检查后端服务状态
curl http://localhost:9999/jshERP-boot/user/randomImage

# 检查API路径
grep -r "production" jshERP-boot/src/main/java/com/jsh/erp/controller/

# 查看后端日志
tail -f jshERP-boot/logs/application.log
```

## ✅ 验证清单

### 数据库配置
- [x] 菜单数据已正确插入
- [x] 用户权限已正确配置
- [x] 角色权限已正确配置
- [x] 菜单层级关系正确

### 前端文件
- [x] Vue组件文件存在
- [x] API文件存在
- [x] 组件路径正确
- [x] 组件语法正确

### 后端文件
- [x] Controller类存在
- [x] Service类存在
- [x] Mapper接口存在
- [x] XML映射文件存在

### 功能测试
- [ ] 前端服务启动成功
- [ ] 后端服务启动成功
- [ ] 用户登录成功
- [ ] 菜单正常显示
- [ ] 页面跳转正常
- [ ] API调用成功

## 🚀 快速验证命令

### 一键验证脚本
```bash
#!/bin/bash
echo "🔍 jshERP 生产管理菜单验证"

# 1. 检查数据库菜单配置
echo "1. 检查菜单配置..."
mysql -u jsh_user -p123456 -h localhost jsh_erp -e "
SELECT number, name, parent_number, url, component 
FROM jsh_function 
WHERE (number = '06' OR parent_number = '06') AND delete_flag = '0' 
ORDER BY number;"

# 2. 检查Vue组件文件
echo "2. 检查Vue组件..."
ls -la jshERP-web/src/views/production/

# 3. 检查API文件
echo "3. 检查API文件..."
ls -la jshERP-web/src/api/production.js

# 4. 检查后端Controller
echo "4. 检查后端Controller..."
ls -la jshERP-boot/src/main/java/com/jsh/erp/controller/ProductionController.java
ls -la jshERP-boot/src/main/java/com/jsh/erp/controller/WorkOrderController.java

echo "✅ 验证完成"
```

## 📊 预期验证结果

如果所有配置正确，您应该能够：

1. ✅ **看到菜单**: 登录后在左侧菜单看到"生产管理"及其子菜单
2. ✅ **正常跳转**: 点击菜单项能正常跳转到对应页面
3. ✅ **页面加载**: 页面能正常加载，无404或组件错误
4. ✅ **API调用**: 页面能正常调用后端API获取数据
5. ✅ **功能操作**: 能进行基本的CRUD操作和业务功能

**如果遇到任何问题，请按照故障排除指南进行检查和修复。**
