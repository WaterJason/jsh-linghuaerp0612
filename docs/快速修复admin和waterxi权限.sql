-- =====================================================
-- 快速修复admin和waterxi用户生产管理权限
-- 解决菜单不显示问题
-- =====================================================

-- 检查当前状态
SELECT '当前用户状态检查' as 检查项目;
SELECT username as 用户名, id as 用户ID FROM jsh_user WHERE username IN ('admin', 'waterxi') AND delete_flag = '0';

SELECT '生产管理菜单ID检查' as 检查项目;
SELECT number as 菜单编号, id as 功能ID, name as 菜单名称 FROM jsh_function WHERE number IN ('05', '0501', '0502', '0503') AND delete_flag = '0';

-- 1. 确保生产管理菜单存在（如果不存在则创建）
INSERT IGNORE INTO jsh_function (number, name, parent_number, url, component, state, sort, enabled, type, push_btn, icon, delete_flag) VALUES
('05', '生产管理', '0', '/production', 'layouts/RouteView', 0, '0500', 1, '电脑版', '', 'build', '0'),
('0501', '生产订单', '05', '/production/order', 'production/ProductionOrderList', 0, '0501', 1, '电脑版', '1,2,3,4,5,6,7', 'profile', '0'),
('0502', '崇左生产看板', '05', '/production/kanban', 'production/ChongzuoKanban', 0, '0502', 1, '电脑版', '1,3,5', 'dashboard', '0'),
('0503', '后工任务列表', '05', '/production/post-task', 'production/PostProcessingTaskList', 0, '0503', 1, '电脑版', '1,2,3', 'ordered-list', '0');

-- 2. 为管理员角色(ID=4)分配权限
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

-- 3. 为租户角色(ID=10)分配权限
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

-- 4. 确保admin用户有管理员角色
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

-- 5. 确保waterxi用户有租户角色
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

-- 6. 验证修复结果
SELECT '修复结果验证' as 检查项目;

SELECT 
    u.username as 用户名,
    r.name as 角色名称,
    f.name as 功能名称,
    f.number as 菜单编号,
    '✅ 权限分配成功' as 状态
FROM jsh_user u
JOIN jsh_user_business ub1 ON u.id = ub1.key_id AND ub1.type = 'UserRole' AND ub1.delete_flag = '0'
JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub1.value, '[', ''), ']', ''))
JOIN jsh_user_business ub2 ON r.id = ub2.key_id AND ub2.type = 'RoleFunctions' AND ub2.delete_flag = '0'
JOIN jsh_function f ON FIND_IN_SET(f.id, REPLACE(REPLACE(ub2.value, '[', ''), ']', ''))
WHERE u.username IN ('admin', 'waterxi') 
AND f.number LIKE '05%'
AND f.delete_flag = '0'
ORDER BY u.username, f.number;

-- 7. 最终提醒
SELECT 
    '重要提醒' as 提醒事项,
    'admin和waterxi用户需要重新登录才能看到生产管理菜单' as 操作说明,
    '建议清除浏览器缓存: localStorage.clear(); sessionStorage.clear(); location.reload();' as 缓存清理;
