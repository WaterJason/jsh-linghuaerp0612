-- =====================================================
-- jshERP薪酬管理模块权限测试脚本
-- 创建时间: 2025-06-22
-- 描述: 测试薪酬管理模块的权限配置是否正确
-- =====================================================

-- 1. 验证菜单权限配置
SELECT 
    '菜单权限验证' as test_type,
    f.number,
    f.name,
    f.parent_number,
    f.url,
    f.enabled,
    f.type,
    f.push_btn,
    CASE 
        WHEN f.enabled = 1 THEN '✓ 已启用'
        ELSE '✗ 未启用'
    END as status
FROM jsh_function f 
WHERE f.number LIKE '11%'
ORDER BY f.number;

-- 2. 验证用户权限分配
SELECT 
    '用户权限验证' as test_type,
    u.username,
    ub.value as function_number,
    f.name as function_name,
    ub.btn_str as button_permissions,
    ub.tenant_id
FROM jsh_user_business ub
LEFT JOIN jsh_user u ON ub.key_id = u.id
LEFT JOIN jsh_function f ON ub.value = f.number
WHERE ub.value LIKE '11%'
ORDER BY u.username, ub.value;

-- 3. 验证权限完整性
SELECT 
    '权限完整性检查' as test_type,
    f.number,
    f.name,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM jsh_user_business ub 
            WHERE ub.value = f.number
        ) THEN '✓ 已分配'
        ELSE '⚠ 未分配'
    END as assignment_status,
    (
        SELECT COUNT(*) FROM jsh_user_business ub 
        WHERE ub.value = f.number
    ) as assigned_users
FROM jsh_function f 
WHERE f.number LIKE '11%'
ORDER BY f.number;

-- 4. 创建测试用户和角色
-- 注意：这些是测试数据，生产环境请谨慎使用

-- 创建测试用户（如果不存在）
INSERT IGNORE INTO jsh_user (
    id, username, login_name, password, status, description, 
    tenant_id, delete_flag, create_time
) VALUES 
(1001, '薪酬管理员', 'salary_admin', 'e10adc3949ba59abbe56e057f20f883e', 0, '薪酬管理员测试账号', 0, '0', NOW()),
(1002, '薪酬专员', 'salary_staff', 'e10adc3949ba59abbe56e057f20f883e', 0, '薪酬专员测试账号', 0, '0', NOW()),
(1003, '薪酬查询员', 'salary_viewer', 'e10adc3949ba59abbe56e057f20f883e', 0, '薪酬查询员测试账号', 0, '0', NOW());

-- 为测试用户分配薪酬管理权限
-- 薪酬管理员：拥有所有权限
INSERT IGNORE INTO jsh_user_business (type, key_id, value, btn_str, tenant_id) VALUES 
('UserRole', 1001, '1101', '1,2,3,4,7', 0),
('UserRole', 1001, '1102', '1,2,3,4,7,8', 0),
('UserRole', 1001, '1103', '1,2,3,4,7,9', 0),
('UserRole', 1001, '1104', '4,7', 0),
('UserRole', 1001, '1105', '1,2,3,4', 0);

-- 薪酬专员：拥有档案和计算权限
INSERT IGNORE INTO jsh_user_business (type, key_id, value, btn_str, tenant_id) VALUES 
('UserRole', 1002, '1101', '1,2,3,4,7', 0),
('UserRole', 1002, '1102', '1,2,3,4,7,8', 0),
('UserRole', 1002, '1104', '4,7', 0);

-- 薪酬查询员：只有查看权限
INSERT IGNORE INTO jsh_user_business (type, key_id, value, btn_str, tenant_id) VALUES 
('UserRole', 1003, '1104', '4,7', 0);

-- 5. 权限测试查询
SELECT 
    '权限测试结果' as test_type,
    u.username,
    f.name as function_name,
    ub.btn_str as permissions,
    CASE 
        WHEN FIND_IN_SET('1', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_add,
    CASE 
        WHEN FIND_IN_SET('2', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_edit,
    CASE 
        WHEN FIND_IN_SET('3', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_delete,
    CASE 
        WHEN FIND_IN_SET('4', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_view,
    CASE 
        WHEN FIND_IN_SET('7', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_export,
    CASE 
        WHEN FIND_IN_SET('8', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_calculate,
    CASE 
        WHEN FIND_IN_SET('9', ub.btn_str) > 0 THEN '✓' ELSE '✗'
    END as can_pay
FROM jsh_user_business ub
LEFT JOIN jsh_user u ON ub.key_id = u.id
LEFT JOIN jsh_function f ON ub.value = f.number
WHERE ub.value LIKE '11%' AND u.id IN (1001, 1002, 1003)
ORDER BY u.username, f.number;

-- 6. 数据权限测试
-- 创建测试薪酬档案数据
INSERT IGNORE INTO jsh_salary_profile (
    id, employee_id, employee_name, department, position, 
    daily_wage, salary_status, tenant_id, delete_flag, create_time
) VALUES 
(1001, 1001, '张三', '珐琅制作', '制作师', 200.00, 'ACTIVE', 0, '0', NOW()),
(1002, 1002, '李四', '咖啡服务', '咖啡师', 180.00, 'ACTIVE', 0, '0', NOW()),
(1003, 1003, '王五', '培训教学', '讲师', 250.00, 'ACTIVE', 0, '0', NOW());

-- 验证多租户数据隔离
SELECT 
    '多租户隔离测试' as test_type,
    tenant_id,
    COUNT(*) as profile_count,
    GROUP_CONCAT(employee_name) as employees
FROM jsh_salary_profile 
WHERE delete_flag = '0'
GROUP BY tenant_id
ORDER BY tenant_id;

-- 7. 权限验证函数测试
-- 模拟权限检查逻辑
SELECT 
    '权限检查模拟' as test_type,
    u.username,
    '薪酬档案管理' as function_name,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM jsh_user_business ub 
            WHERE ub.key_id = u.id 
            AND ub.value = '1101' 
            AND FIND_IN_SET('1', ub.btn_str) > 0
        ) THEN '✓ 有新增权限'
        ELSE '✗ 无新增权限'
    END as add_permission,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM jsh_user_business ub 
            WHERE ub.key_id = u.id 
            AND ub.value = '1102' 
            AND FIND_IN_SET('8', ub.btn_str) > 0
        ) THEN '✓ 有计算权限'
        ELSE '✗ 无计算权限'
    END as calculate_permission,
    CASE 
        WHEN EXISTS (
            SELECT 1 FROM jsh_user_business ub 
            WHERE ub.key_id = u.id 
            AND ub.value = '1103' 
            AND FIND_IN_SET('9', ub.btn_str) > 0
        ) THEN '✓ 有发放权限'
        ELSE '✗ 无发放权限'
    END as payment_permission
FROM jsh_user u 
WHERE u.id IN (1001, 1002, 1003)
ORDER BY u.username;

-- 8. 清理测试数据（可选）
-- 如果需要清理测试数据，取消注释以下语句
/*
DELETE FROM jsh_user_business WHERE key_id IN (1001, 1002, 1003);
DELETE FROM jsh_salary_profile WHERE id IN (1001, 1002, 1003);
DELETE FROM jsh_user WHERE id IN (1001, 1002, 1003);
*/

-- 9. 权限配置总结报告
SELECT 
    '权限配置总结' as report_type,
    (SELECT COUNT(*) FROM jsh_function WHERE number LIKE '11%') as total_functions,
    (SELECT COUNT(DISTINCT key_id) FROM jsh_user_business WHERE value LIKE '11%') as assigned_users,
    (SELECT COUNT(*) FROM jsh_user_business WHERE value LIKE '11%') as total_assignments,
    CASE 
        WHEN (SELECT COUNT(*) FROM jsh_function WHERE number LIKE '11%' AND enabled = 1) = 6 
        THEN '✓ 菜单配置完整'
        ELSE '⚠ 菜单配置不完整'
    END as menu_status,
    CASE 
        WHEN (SELECT COUNT(*) FROM jsh_user_business WHERE value LIKE '11%') > 0 
        THEN '✓ 权限已分配'
        ELSE '⚠ 权限未分配'
    END as permission_status;

-- 显示测试完成信息
SELECT 
    '测试完成' as message,
    NOW() as test_time,
    '薪酬管理模块权限配置测试已完成，请检查上述结果' as description;
