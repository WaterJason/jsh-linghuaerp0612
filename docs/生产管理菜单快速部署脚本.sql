-- =====================================================
-- jshERP 生产管理模块菜单快速部署脚本
-- 执行前请确保前端Vue组件已创建完成
-- =====================================================

-- 检查编号05是否可用
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN '✅ 编号05可用，可以继续执行'
        ELSE '❌ 编号05已被占用，请修改脚本中的编号'
    END as 检查结果
FROM jsh_function 
WHERE number = '05' AND delete_flag = '0';

-- 如果上面显示可用，继续执行以下脚本

-- 1. 创建一级菜单：生产管理
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '05', '生产管理', '0', '/production', 'layouts/RouteView', 
    0, '0500', 1, '电脑版', '', 'build', '0'
);

-- 2. 创建二级菜单：生产订单
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0501', '生产订单', '05', '/production/order', 'production/ProductionOrderList', 
    0, '0501', 1, '电脑版', '1,2,3,4,5,6,7', 'profile', '0'
);

-- 3. 创建二级菜单：崇左生产看板
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0502', '崇左生产看板', '05', '/production/kanban', 'production/ChongzuoKanban', 
    0, '0502', 1, '电脑版', '1,3,5', 'dashboard', '0'
);

-- 4. 创建二级菜单：后工任务列表
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0503', '后工任务列表', '05', '/production/post-task', 'production/PostProcessingTaskList', 
    0, '0503', 1, '电脑版', '1,2,3', 'ordered-list', '0'
);

-- 5. 为租户角色分配权限
UPDATE jsh_user_business 
SET value = CONCAT(
    COALESCE(value, ''),
    '[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0501' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0502' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0503' AND delete_flag = '0'), ']'
)
WHERE type = 'RoleFunctions' 
AND key_id = '10'
AND delete_flag = '0'
AND value NOT LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%');

-- 6. 验证创建结果
SELECT 
    '菜单创建验证' as 检查项目,
    COUNT(*) as 创建数量,
    CASE 
        WHEN COUNT(*) = 4 THEN '✅ 菜单创建成功'
        ELSE '❌ 菜单创建不完整'
    END as 状态
FROM jsh_function 
WHERE number IN ('05', '0501', '0502', '0503') 
AND delete_flag = '0';

-- 7. 验证权限分配
SELECT 
    '权限分配验证' as 检查项目,
    r.name as 角色名称,
    CASE 
        WHEN ub.value LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%') 
        THEN '✅ 权限分配成功'
        ELSE '❌ 权限分配失败'
    END as 状态
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions' AND ub.delete_flag = '0'
WHERE r.id = 10 AND r.delete_flag = '0';

-- 执行完成后的操作提醒
SELECT 
    '部署完成提醒' as 提醒事项,
    '请通知用户清除浏览器缓存并重新登录查看新菜单' as 操作说明;
