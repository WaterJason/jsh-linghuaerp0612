-- =====================================================
-- jshERP 生产管理模块权限分配修复脚本
-- 解决admin和waterxi用户看不到生产管理菜单的问题
-- =====================================================

-- =====================================================
-- 第一步：检查当前用户和角色状态
-- =====================================================

-- 1.1 检查主要用户的基本信息
SELECT 
    id as 用户ID,
    username as 用户名,
    login_name as 登录名,
    status as 状态,
    is_manager as 是否管理员,
    tenant_id as 租户ID
FROM jsh_user 
WHERE username IN ('admin', 'waterxi', 'jsh') 
AND delete_flag = '0'
ORDER BY id;

-- 1.2 检查用户的角色分配情况
SELECT 
    u.username as 用户名,
    u.id as 用户ID,
    ub.value as 角色ID列表,
    r.id as 角色ID,
    r.name as 角色名称
FROM jsh_user u
LEFT JOIN jsh_user_business ub ON u.id = ub.key_id AND ub.type = 'UserRole' AND ub.delete_flag = '0'
LEFT JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub.value, '[', ''), ']', ''))
WHERE u.username IN ('admin', 'waterxi', 'jsh') 
AND u.delete_flag = '0'
ORDER BY u.id, r.id;

-- 1.3 检查生产管理菜单的功能ID
SELECT 
    id as 功能ID,
    number as 菜单编号,
    name as 菜单名称,
    parent_number as 父编号
FROM jsh_function 
WHERE number IN ('05', '0501', '0502', '0503')
AND delete_flag = '0'
ORDER BY number;

-- =====================================================
-- 第二步：检查角色权限分配情况
-- =====================================================

-- 2.1 检查各角色的功能权限
SELECT 
    r.id as 角色ID,
    r.name as 角色名称,
    ub.value as 功能权限列表,
    CASE 
        WHEN ub.value LIKE '%[' || (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0') || ']%' 
        THEN '✅ 已有生产管理权限'
        ELSE '❌ 缺少生产管理权限'
    END as 权限状态
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions' AND ub.delete_flag = '0'
WHERE r.delete_flag = '0'
ORDER BY r.id;

-- =====================================================
-- 第三步：为角色分配生产管理权限
-- =====================================================

-- 3.1 为管理员角色(ID=4)分配权限
-- 先检查是否已有RoleFunctions记录
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'RoleFunctions', 4, '', '0'
WHERE NOT EXISTS (
    SELECT 1 FROM jsh_user_business 
    WHERE type = 'RoleFunctions' AND key_id = 4 AND delete_flag = '0'
);

-- 更新管理员角色权限
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

-- 3.2 为租户角色(ID=10)分配权限
-- 先检查是否已有RoleFunctions记录
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'RoleFunctions', 10, '', '0'
WHERE NOT EXISTS (
    SELECT 1 FROM jsh_user_business 
    WHERE type = 'RoleFunctions' AND key_id = 10 AND delete_flag = '0'
);

-- 更新租户角色权限
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

-- 3.3 为聆花管理员角色(ID=21)分配权限
-- 先检查是否已有RoleFunctions记录
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'RoleFunctions', 21, '', '0'
WHERE NOT EXISTS (
    SELECT 1 FROM jsh_user_business 
    WHERE type = 'RoleFunctions' AND key_id = 21 AND delete_flag = '0'
);

-- 更新聆花管理员角色权限
UPDATE jsh_user_business 
SET value = CONCAT(
    COALESCE(value, ''),
    '[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0501' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0502' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0503' AND delete_flag = '0'), ']'
)
WHERE type = 'RoleFunctions' 
AND key_id = 21
AND delete_flag = '0'
AND value NOT LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%');

-- =====================================================
-- 第四步：确保用户有正确的角色分配
-- =====================================================

-- 4.1 确保admin用户有管理员角色
-- 先检查admin用户是否已有UserRole记录
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'UserRole', u.id, '[4]', '0'
FROM jsh_user u
WHERE u.username = 'admin' AND u.delete_flag = '0'
AND NOT EXISTS (
    SELECT 1 FROM jsh_user_business 
    WHERE type = 'UserRole' AND key_id = u.id AND delete_flag = '0'
);

-- 更新admin用户角色（确保包含管理员角色4）
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

-- 4.2 确保waterxi用户有租户角色
-- 先检查waterxi用户是否已有UserRole记录
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'UserRole', u.id, '[10]', '0'
FROM jsh_user u
WHERE u.username = 'waterxi' AND u.delete_flag = '0'
AND NOT EXISTS (
    SELECT 1 FROM jsh_user_business 
    WHERE type = 'UserRole' AND key_id = u.id AND delete_flag = '0'
);

-- 更新waterxi用户角色（确保包含租户角色10）
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

-- 4.3 确保jsh用户有聆花管理员角色
-- 先检查jsh用户是否已有UserRole记录
INSERT IGNORE INTO jsh_user_business (type, key_id, value, delete_flag)
SELECT 'UserRole', u.id, '[21]', '0'
FROM jsh_user u
WHERE u.username = 'jsh' AND u.delete_flag = '0'
AND NOT EXISTS (
    SELECT 1 FROM jsh_user_business 
    WHERE type = 'UserRole' AND key_id = u.id AND delete_flag = '0'
);

-- 更新jsh用户角色（确保包含聆花管理员角色21）
UPDATE jsh_user_business ub
JOIN jsh_user u ON ub.key_id = u.id
SET ub.value = CASE 
    WHEN ub.value LIKE '%[21]%' THEN ub.value
    ELSE CONCAT(COALESCE(ub.value, ''), '[21]')
END
WHERE ub.type = 'UserRole' 
AND u.username = 'jsh'
AND ub.delete_flag = '0'
AND u.delete_flag = '0';

-- =====================================================
-- 第五步：验证权限分配结果
-- =====================================================

-- 5.1 验证用户角色分配
SELECT 
    '用户角色验证' as 检查项目,
    u.username as 用户名,
    ub.value as 角色列表,
    CASE 
        WHEN u.username = 'admin' AND ub.value LIKE '%[4]%' THEN '✅ admin有管理员角色'
        WHEN u.username = 'waterxi' AND ub.value LIKE '%[10]%' THEN '✅ waterxi有租户角色'
        WHEN u.username = 'jsh' AND ub.value LIKE '%[21]%' THEN '✅ jsh有聆花管理员角色'
        ELSE '❌ 角色分配异常'
    END as 验证结果
FROM jsh_user u
LEFT JOIN jsh_user_business ub ON u.id = ub.key_id AND ub.type = 'UserRole' AND ub.delete_flag = '0'
WHERE u.username IN ('admin', 'waterxi', 'jsh') 
AND u.delete_flag = '0'
ORDER BY u.username;

-- 5.2 验证角色功能权限
SELECT 
    '角色权限验证' as 检查项目,
    r.name as 角色名称,
    CASE 
        WHEN ub.value LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%') 
        THEN '✅ 已分配生产管理权限'
        ELSE '❌ 缺少生产管理权限'
    END as 验证结果
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions' AND ub.delete_flag = '0'
WHERE r.id IN (4, 10, 21)
AND r.delete_flag = '0'
ORDER BY r.id;

-- 5.3 验证用户最终权限（完整权限链）
SELECT 
    '用户权限链验证' as 检查项目,
    u.username as 用户名,
    r.name as 角色名称,
    f.name as 功能名称,
    f.number as 菜单编号
FROM jsh_user u
JOIN jsh_user_business ub1 ON u.id = ub1.key_id AND ub1.type = 'UserRole' AND ub1.delete_flag = '0'
JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub1.value, '[', ''), ']', ''))
JOIN jsh_user_business ub2 ON r.id = ub2.key_id AND ub2.type = 'RoleFunctions' AND ub2.delete_flag = '0'
JOIN jsh_function f ON FIND_IN_SET(f.id, REPLACE(REPLACE(ub2.value, '[', ''), ']', ''))
WHERE u.username IN ('admin', 'waterxi', 'jsh') 
AND f.number LIKE '05%'
AND f.delete_flag = '0'
ORDER BY u.username, f.number;

-- =====================================================
-- 执行说明
-- =====================================================

/*
执行步骤：
1. 先执行第一步和第二步的检查脚本，了解当前状态
2. 执行第三步和第四步的权限分配脚本
3. 执行第五步的验证脚本，确认权限分配成功
4. 通知用户清除缓存并重新登录

重要提醒：
- 权限分配后，用户必须重新登录才能看到新菜单
- 建议用户清除浏览器缓存：localStorage.clear(); sessionStorage.clear(); location.reload();
- 如果仍然看不到菜单，检查前端Vue组件是否存在

故障排除：
- 如果验证失败，检查用户ID和角色ID是否正确
- 如果权限链断裂，检查jsh_user_business表的数据完整性
- 如果菜单仍不显示，检查前端权限控制逻辑
*/
