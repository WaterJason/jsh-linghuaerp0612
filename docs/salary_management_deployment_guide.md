# jshERP薪酬管理模块部署和使用指南

## 📋 目录
1. [部署前准备](#部署前准备)
2. [数据库部署](#数据库部署)
3. [后端部署](#后端部署)
4. [前端部署](#前端部署)
5. [权限配置](#权限配置)
6. [功能测试](#功能测试)
7. [使用指南](#使用指南)
8. [常见问题](#常见问题)

## 🚀 部署前准备

### 环境要求
- **Java**: JDK 1.8+
- **数据库**: MySQL 5.7+
- **缓存**: Redis 6.0+
- **前端**: Node.js 14+
- **服务器**: CentOS 7+ 或 Ubuntu 18+

### 检查现有jshERP环境
```bash
# 检查Java版本
java -version

# 检查MySQL连接
mysql -u root -p

# 检查Redis服务
redis-cli ping

# 检查jshERP服务状态
ps aux | grep jsh
```

## 🗄️ 数据库部署

### 1. 执行数据表创建脚本
```sql
-- 连接到jshERP数据库
USE jsh_erp;

-- 执行表创建脚本
SOURCE /path/to/jshERP-database/salary_management_tables.sql;

-- 执行基础数据初始化
SOURCE /path/to/jshERP-database/salary_management_init_data.sql;

-- 执行权限配置脚本
SOURCE /path/to/jshERP-database/salary_management_permissions.sql;
```

### 2. 验证数据表创建
```sql
-- 检查表是否创建成功
SHOW TABLES LIKE 'jsh_salary%';

-- 检查薪酬项目基础数据
SELECT item_code, item_name, item_type, status FROM jsh_salary_item WHERE delete_flag = '0';

-- 检查菜单权限配置
SELECT number, name, url, push_btn FROM jsh_function WHERE number LIKE '11%';
```

### 3. 数据库索引优化（可选）
```sql
-- 为高频查询字段添加索引
ALTER TABLE jsh_salary_profile ADD INDEX idx_employee_tenant_status (employee_id, tenant_id, salary_status, delete_flag);
ALTER TABLE jsh_salary_calculation ADD INDEX idx_month_status_tenant (calculation_month, status, tenant_id, delete_flag);
ALTER TABLE jsh_salary_payment ADD INDEX idx_status_date_tenant (payment_status, payment_date, tenant_id, delete_flag);
```

## 🔧 后端部署

### 1. 复制后端代码文件
```bash
# 复制实体类
cp -r jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/Salary* /path/to/jshERP/jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/
cp -r jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/EmployeeSalaryItem.java /path/to/jshERP/jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/

# 复制Mapper接口
cp -r jshERP-boot/src/main/java/com/jsh/erp/datasource/mappers/Salary* /path/to/jshERP/jshERP-boot/src/main/java/com/jsh/erp/datasource/mappers/
cp -r jshERP-boot/src/main/java/com/jsh/erp/datasource/mappers/EmployeeSalaryItemMapper* /path/to/jshERP/jshERP-boot/src/main/java/com/jsh/erp/datasource/mappers/

# 复制Service类
cp -r jshERP-boot/src/main/java/com/jsh/erp/service/salary/ /path/to/jshERP/jshERP-boot/src/main/java/com/jsh/erp/service/

# 复制Controller类
cp -r jshERP-boot/src/main/java/com/jsh/erp/controller/Salary* /path/to/jshERP/jshERP-boot/src/main/java/com/jsh/erp/controller/
```

### 2. 配置应用程序
```yaml
# 在application.yml中添加薪酬管理相关配置
salary:
  calculation:
    # 薪酬计算引擎配置
    engine:
      # 是否启用异步计算
      async-enabled: true
      # 计算超时时间（分钟）
      timeout-minutes: 30
      # 批量计算大小
      batch-size: 100
    
    # 缓存配置
    cache:
      # 薪酬项目配置缓存时间（小时）
      salary-items-ttl: 24
      # 员工薪酬档案缓存时间（小时）
      salary-profiles-ttl: 12
      # 计算结果缓存时间（天）
      calculation-results-ttl: 7
  
  # 权限配置
  permission:
    # 数据权限级别：PERSONAL-个人，DEPARTMENT-部门，ALL-全部
    default-data-level: PERSONAL
    # 是否启用审批流程
    approval-enabled: true
```

### 3. 编译和重启服务
```bash
# 进入jshERP后端目录
cd /path/to/jshERP/jshERP-boot

# 编译项目
mvn clean compile

# 重启jshERP服务
./restart.sh
# 或者
systemctl restart jsherp
```

## 🎨 前端部署

### 1. 复制前端代码文件
```bash
# 复制Vue组件
cp -r jshERP-web/src/views/salary/ /path/to/jshERP/jshERP-web/src/views/

# 复制API接口文件
cp jshERP-web/src/api/salary.js /path/to/jshERP/jshERP-web/src/api/

# 复制路由配置
cp jshERP-web/src/router/salary.js /path/to/jshERP/jshERP-web/src/router/
```

### 2. 更新路由配置
```javascript
// 在 /path/to/jshERP/jshERP-web/src/router/index.js 中添加
import salaryRoutes from './salary'

// 在路由配置中添加薪酬管理路由
const routes = [
  // ... 其他路由
  salaryRoutes,
  // ... 其他路由
]
```

### 3. 更新菜单配置
```javascript
// 在菜单配置文件中添加薪酬管理菜单
{
  path: '/salary',
  name: 'salary',
  component: RouteView,
  meta: {
    title: '薪酬管理',
    icon: 'money-collect',
    permission: ['salary']
  },
  children: [
    // 子菜单配置...
  ]
}
```

### 4. 编译和部署前端
```bash
# 进入前端目录
cd /path/to/jshERP/jshERP-web

# 安装依赖（如果需要）
npm install

# 编译生产版本
npm run build

# 部署到Nginx
cp -r dist/* /var/www/html/jsherp/
```

## 🔐 权限配置

### 1. 为管理员用户分配权限
```sql
-- 查询管理员用户ID
SELECT id, username FROM jsh_user WHERE username = 'admin';

-- 为管理员分配薪酬管理权限（假设用户ID为1）
INSERT INTO jsh_user_business (type, key_id, value, btn_str, tenant_id) VALUES 
('UserRole', 1, '1101', '1,2,3,4,7', 0),
('UserRole', 1, '1102', '1,2,3,4,7,8', 0),
('UserRole', 1, '1103', '1,2,3,4,7,9', 0),
('UserRole', 1, '1104', '4,7', 0),
('UserRole', 1, '1105', '1,2,3,4', 0);
```

### 2. 创建薪酬管理角色
```sql
-- 创建薪酬管理员角色
INSERT INTO jsh_role (name, type, value, description, tenant_id) VALUES 
('薪酬管理员', 'UserRole', '1101,1102,1103,1104,1105', '拥有完整薪酬管理权限', 0);

-- 创建薪酬专员角色
INSERT INTO jsh_role (name, type, value, description, tenant_id) VALUES 
('薪酬专员', 'UserRole', '1101,1102,1104', '拥有薪酬档案和计算权限', 0);
```

## 🧪 功能测试

### 1. 基础功能测试
```bash
# 测试API接口
curl -X GET "http://localhost:8080/salary/profile/list" \
  -H "X-Access-Token: YOUR_TOKEN"

# 测试薪酬项目查询
curl -X GET "http://localhost:8080/salary/item/list" \
  -H "X-Access-Token: YOUR_TOKEN"
```

### 2. 数据库连接测试
```sql
-- 测试数据插入
INSERT INTO jsh_salary_profile (employee_id, employee_name, department, daily_wage, tenant_id) 
VALUES (1, '测试员工', '测试部门', 200.00, 0);

-- 测试数据查询
SELECT * FROM jsh_salary_profile WHERE tenant_id = 0 AND delete_flag = '0';
```

### 3. 前端页面测试
1. 登录jshERP系统
2. 检查左侧菜单是否显示"薪酬管理"
3. 点击进入各个子菜单页面
4. 测试基本的增删改查功能

## 📖 使用指南

### 1. 薪酬档案管理
1. **新增员工薪酬档案**
   - 进入"薪酬管理" → "薪酬档案"
   - 点击"新增薪酬档案"按钮
   - 填写员工基本信息和日薪标准
   - 保存后可进行薪酬结构配置

2. **配置薪酬结构**
   - 在薪酬档案列表中点击"配置薪酬"
   - 选择适用的薪酬项目
   - 设置个人定制比例或金额
   - 设置生效时间和失效时间

### 2. 薪酬计算
1. **执行月度薪酬计算**
   - 进入"薪酬管理" → "薪酬计算"
   - 选择计算月份和员工范围
   - 点击"开始计算"按钮
   - 等待计算完成并查看结果

2. **审批薪酬计算**
   - 查看待审批的计算记录
   - 点击"详情"查看计算明细
   - 选择"审批通过"或"审批拒绝"
   - 填写审批意见

### 3. 薪酬发放
1. **创建发放记录**
   - 进入"薪酬管理" → "薪酬发放"
   - 选择已审批的计算记录
   - 点击"执行发放"
   - 选择发放方式

2. **生成薪资条**
   - 在发放记录中点击"生成薪资条"
   - 系统自动生成PDF格式薪资条
   - 可下载或打印薪资条

## ❓ 常见问题

### Q1: 数据表创建失败
**A**: 检查MySQL版本和字符集设置，确保使用UTF8字符集

### Q2: 前端页面无法访问
**A**: 检查路由配置和权限设置，确保用户有相应的菜单权限

### Q3: 薪酬计算结果不准确
**A**: 检查薪酬项目配置和计算策略，确保基础数据正确

### Q4: 权限配置不生效
**A**: 清除浏览器缓存，重新登录系统

### Q5: 性能问题
**A**: 检查Redis缓存配置，优化数据库索引

## 📞 技术支持

如遇到部署或使用问题，请：
1. 查看系统日志文件
2. 检查数据库连接状态
3. 验证权限配置
4. 联系技术支持团队

---

**部署完成后，jshERP薪酬管理模块即可正常使用！** 🎉
