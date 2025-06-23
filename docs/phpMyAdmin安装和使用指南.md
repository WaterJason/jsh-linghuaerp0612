# jshERP phpMyAdmin 安装和使用指南

## 🎯 目标
在Docker中安装phpMyAdmin，连接到jshERP的MySQL数据库，方便执行SQL脚本管理生产管理模块。

## 🚀 快速启动phpMyAdmin

### 方法1：使用一键启动脚本（推荐）
```bash
# 进入jshERP项目目录
cd /Users/macmini/Desktop/jshERP-0612-Cursor

# 运行启动脚本
./scripts/start-phpmyadmin.sh
```

### 方法2：手动Docker命令启动
```bash
# 1. 确保jshERP开发环境正在运行
docker-compose -f docker-compose.dev.yml ps

# 2. 创建网络（如果不存在）
docker network create jsherp_dev_network 2>/dev/null || true

# 3. 启动phpMyAdmin容器
docker run -d \
  --name jsherp-phpmyadmin \
  --network jsherp_dev_network \
  -e PMA_HOST=jsherp-mysql-dev \
  -e PMA_PORT=3306 \
  -e PMA_USER=root \
  -e PMA_PASSWORD=123456 \
  -e PMA_ARBITRARY=1 \
  -e UPLOAD_LIMIT=100M \
  -e MEMORY_LIMIT=512M \
  -e TZ=Asia/Shanghai \
  -p 8081:80 \
  --restart unless-stopped \
  phpmyadmin/phpmyadmin:latest

# 4. 等待启动完成
sleep 15

# 5. 检查状态
docker ps | grep phpmyadmin
```

### 方法3：使用docker-compose启动
```bash
# 使用专门的phpMyAdmin配置文件
docker-compose -f docker-compose.phpmyadmin.yml up -d phpmyadmin
```

## 🌐 访问phpMyAdmin

### 访问信息
- **URL**: http://localhost:8081
- **用户名**: root
- **密码**: 123456
- **数据库**: jsh_erp

### 登录步骤
1. 打开浏览器访问 http://localhost:8081
2. 输入用户名: `root`
3. 输入密码: `123456`
4. 点击"执行"登录

## 📊 执行生产管理SQL脚本

### 第一步：选择数据库
1. 登录phpMyAdmin后，在左侧选择 `jsh_erp` 数据库
2. 确认看到jshERP的数据表（如jsh_user、jsh_function等）

### 第二步：执行SQL脚本
1. 点击顶部的"SQL"标签
2. 复制以下SQL脚本内容：

```sql
-- =====================================================
-- jshERP 生产管理模块完整部署脚本
-- =====================================================

-- 1. 检查编号05是否可用
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ 编号05可用，可以继续执行'
        ELSE '❌ 编号05已被占用，请修改脚本中的编号'
    END as 检查结果
FROM jsh_function 
WHERE number = '05' AND delete_flag = '0';

-- 2. 创建生产管理菜单结构
INSERT IGNORE INTO jsh_function (number, name, parent_number, url, component, state, sort, enabled, type, push_btn, icon, delete_flag) VALUES
('05', '生产管理', '0', '/production', 'layouts/RouteView', 0, '0500', 1, '电脑版', '', 'build', '0'),
('0501', '生产订单', '05', '/production/order', 'production/ProductionOrderList', 0, '0501', 1, '电脑版', '1,2,3,4,5,6,7', 'profile', '0'),
('0502', '崇左生产看板', '05', '/production/kanban', 'production/ChongzuoKanban', 0, '0502', 1, '电脑版', '1,3,5', 'dashboard', '0'),
('0503', '后工任务列表', '05', '/production/post-task', 'production/PostProcessingTaskList', 0, '0503', 1, '电脑版', '1,2,3', 'ordered-list', '0');

-- 3. 为管理员角色(ID=4)分配权限
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag) VALUES ('RoleFunctions', 4, '', '0');

UPDATE jsh_user_business 
SET value = CONCAT(
    COALESCE(value, ''),
    '[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0501' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0502' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0503' AND delete_flag = '0'), ']'
)
WHERE type = 'RoleFunctions' 
AND key_id = 4
AND delete_flag = '0'
AND value NOT LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%');

-- 4. 为租户角色(ID=10)分配权限
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag) VALUES ('RoleFunctions', 10, '', '0');

UPDATE jsh_user_business 
SET value = CONCAT(
    COALESCE(value, ''),
    '[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0501' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0502' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0503' AND delete_flag = '0'), ']'
)
WHERE type = 'RoleFunctions' 
AND key_id = 10
AND delete_flag = '0'
AND value NOT LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%');

-- 5. 确保admin用户有管理员角色
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'UserRole', u.id, '[4]', '0'
FROM jsh_user u
WHERE u.username = 'admin' AND u.delete_flag = '0';

UPDATE jsh_user_business ub
JOIN jsh_user u ON ub.key_id = u.id
SET ub.value = CASE 
    WHEN ub.value LIKE '%[4]%' THEN ub.value
    ELSE CONCAT(COALESCE(ub.value, ''), '[4]')
END
WHERE ub.type = 'UserRole' 
AND u.username = 'admin'
AND ub.delete_flag = '0'
AND u.delete_flag = '0';

-- 6. 确保waterxi用户有租户角色
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'UserRole', u.id, '[10]', '0'
FROM jsh_user u
WHERE u.username = 'waterxi' AND u.delete_flag = '0';

UPDATE jsh_user_business ub
JOIN jsh_user u ON ub.key_id = u.id
SET ub.value = CASE 
    WHEN ub.value LIKE '%[10]%' THEN ub.value
    ELSE CONCAT(COALESCE(ub.value, ''), '[10]')
END
WHERE ub.type = 'UserRole' 
AND u.username = 'waterxi'
AND ub.delete_flag = '0'
AND u.delete_flag = '0';

-- 7. 验证部署结果
SELECT '=== 菜单创建验证 ===' as 检查项目;
SELECT number as 菜单编号, name as 菜单名称, url as URL路径 
FROM jsh_function 
WHERE number IN ('05', '0501', '0502', '0503') 
AND delete_flag = '0'
ORDER BY number;

SELECT '=== 权限分配验证 ===' as 检查项目;
SELECT 
    u.username as 用户名,
    r.name as 角色名称,
    f.name as 功能名称,
    f.number as 菜单编号
FROM jsh_user u
JOIN jsh_user_business ub1 ON u.id = ub1.key_id AND ub1.type = 'UserRole' AND ub1.delete_flag = '0'
JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub1.value, '[', ''), ']', ''))
JOIN jsh_user_business ub2 ON r.id = ub2.key_id AND ub2.type = 'RoleFunctions' AND ub2.delete_flag = '0'
JOIN jsh_function f ON FIND_IN_SET(f.id, REPLACE(REPLACE(ub2.value, '[', ''), ']', ''))
WHERE u.username IN ('admin', 'waterxi') 
AND f.number LIKE '05%'
AND f.delete_flag = '0'
ORDER BY u.username, f.number;
```

3. 点击"执行"按钮运行脚本
4. 查看执行结果，确认菜单创建和权限分配成功

### 第三步：验证结果
执行完成后，您应该看到：
- ✅ 4个菜单记录创建成功
- ✅ admin和waterxi用户的权限分配成功
- ✅ 验证查询显示完整的权限链

## 🔧 故障排除

### 问题1：无法访问phpMyAdmin
**解决方案**：
```bash
# 检查容器状态
docker ps | grep phpmyadmin

# 查看容器日志
docker logs jsherp-phpmyadmin

# 重启容器
docker restart jsherp-phpmyadmin
```

### 问题2：无法连接数据库
**解决方案**：
```bash
# 检查MySQL容器状态
docker ps | grep mysql

# 检查网络连接
docker network ls | grep jsherp

# 重启MySQL容器
docker restart jsherp-mysql-dev
```

### 问题3：SQL执行失败
**可能原因**：
- 数据库连接问题
- 权限不足
- SQL语法错误

**解决方案**：
- 确认选择了正确的数据库(jsh_erp)
- 使用root用户登录
- 分段执行SQL脚本

## 📝 执行后操作

### 1. 通知用户重新登录
执行SQL脚本后，需要通知admin和waterxi用户：
- 清除浏览器缓存
- 重新登录jshERP系统
- 查看是否出现"生产管理"菜单

### 2. 清除缓存的方法
在浏览器控制台执行：
```javascript
localStorage.clear();
sessionStorage.clear();
location.reload();
```

### 3. 验证菜单显示
用户重新登录后应该看到：
```
📋 生产管理
├── 📄 生产订单
├── 📊 崇左生产看板  
└── 📋 后工任务列表
```

## 🎯 成功标准

部署成功的标志：
1. ✅ phpMyAdmin可以正常访问
2. ✅ 能够连接到jsh_erp数据库
3. ✅ SQL脚本执行无错误
4. ✅ 验证查询显示正确结果
5. ✅ 用户重新登录后看到生产管理菜单

## 🔄 清理和维护

### 停止phpMyAdmin
```bash
# 停止容器
docker stop jsherp-phpmyadmin

# 删除容器
docker rm jsherp-phpmyadmin
```

### 重新启动
```bash
# 重新运行启动脚本
./scripts/start-phpmyadmin.sh
```

通过以上步骤，您就可以成功在Docker中安装phpMyAdmin，连接到jshERP数据库，并执行生产管理模块的SQL脚本了！
