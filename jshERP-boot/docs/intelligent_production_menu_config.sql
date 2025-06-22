-- =====================================================
-- jshERP 智能生产管理模块菜单配置脚本
-- 创建时间: 2025-06-22
-- 说明: 在现有"06生产管理"模块下添加智能生产管理功能菜单
-- =====================================================

-- 设置字符集
SET NAMES utf8mb4;

-- =====================================================
-- 第一步：检查现有菜单结构
-- =====================================================

-- 1.1 检查现有生产管理菜单
SELECT 
    number as 菜单编号,
    name as 菜单名称,
    parent_number as 父编号,
    url as URL路径,
    component as 组件路径,
    push_btn as 按钮权限,
    enabled as 是否启用
FROM jsh_function 
WHERE (number = '06' OR parent_number = '06')
AND delete_flag = '0'
ORDER BY number;

-- 1.2 检查可用编号
SELECT 
    CASE 
        WHEN COUNT(*) = 0 THEN CONCAT('编号', check_number, '可用')
        ELSE CONCAT('编号', check_number, '已被占用')
    END as 可用性检查
FROM jsh_function 
CROSS JOIN (
    SELECT '0605' as check_number UNION ALL
    SELECT '0606' UNION ALL
    SELECT '0607' UNION ALL
    SELECT '0608' UNION ALL
    SELECT '0609'
) numbers
WHERE number = check_number AND delete_flag = '0';

-- =====================================================
-- 第二步：创建智能生产管理菜单
-- =====================================================

-- 2.1 生产工单管理 (0605)
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0605',                                    -- 菜单编号
    '生产工单管理',                            -- 菜单名称
    '06',                                      -- 父菜单编号
    '/production/work-order',                  -- URL路径
    '/production/ProductionWorkOrder',         -- Vue组件路径
    0,                                         -- 状态
    '0605',                                    -- 排序号
    1,                                         -- 是否启用
    '电脑版',                                  -- 类型
    '1,2,3,5,6,7',                            -- 按钮权限：1=新增,2=审核,3=导出,5=打印,6=作废,7=反审核
    'file-text',                               -- 图标
    '0'                                        -- 删除标志
);

-- 2.2 生产任务管理 (0606)
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0606',                                    -- 菜单编号
    '生产任务管理',                            -- 菜单名称
    '06',                                      -- 父菜单编号
    '/production/task',                        -- URL路径
    '/production/ProductionTask',              -- Vue组件路径
    0,                                         -- 状态
    '0606',                                    -- 排序号
    1,                                         -- 是否启用
    '电脑版',                                  -- 类型
    '1,2,3,5,6,7',                            -- 按钮权限
    'schedule',                                -- 图标
    '0'                                        -- 删除标志
);

-- 2.3 生产报工管理 (0607)
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0607',                                    -- 菜单编号
    '生产报工管理',                            -- 菜单名称
    '06',                                      -- 父菜单编号
    '/production/report',                      -- URL路径
    '/production/ProductionReport',            -- Vue组件路径
    0,                                         -- 状态
    '0607',                                    -- 排序号
    1,                                         -- 是否启用
    '电脑版',                                  -- 类型
    '1,2,3,5,6,7',                            -- 按钮权限
    'form',                                    -- 图标
    '0'                                        -- 删除标志
);

-- 2.4 质量检验管理 (0608)
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0608',                                    -- 菜单编号
    '质量检验管理',                            -- 菜单名称
    '06',                                      -- 父菜单编号
    '/production/quality',                     -- URL路径
    '/production/QualityInspection',           -- Vue组件路径
    0,                                         -- 状态
    '0608',                                    -- 排序号
    1,                                         -- 是否启用
    '电脑版',                                  -- 类型
    '1,2,3,5,6,7',                            -- 按钮权限
    'safety-certificate',                      -- 图标
    '0'                                        -- 删除标志
);

-- 2.5 工人技能管理 (0609)
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    state, sort, enabled, type, push_btn, icon, delete_flag
) VALUES (
    '0609',                                    -- 菜单编号
    '工人技能管理',                            -- 菜单名称
    '06',                                      -- 父菜单编号
    '/production/worker-skill',                -- URL路径
    '/production/WorkerSkill',                 -- Vue组件路径
    0,                                         -- 状态
    '0609',                                    -- 排序号
    1,                                         -- 是否启用
    '电脑版',                                  -- 类型
    '1,2,3,5,6,7',                            -- 按钮权限
    'team',                                    -- 图标
    '0'                                        -- 删除标志
);

-- =====================================================
-- 第三步：更新现有菜单（可选）
-- =====================================================

-- 3.1 更新崇左生产看板菜单（如果需要）
UPDATE jsh_function 
SET 
    name = '智能生产看板',
    url = '/production/intelligent-kanban',
    component = '/production/IntelligentKanban',
    push_btn = '1,2,3,5,6,7',
    icon = 'dashboard'
WHERE number = '0602' AND delete_flag = '0';

-- 3.2 更新后工任务列表菜单（如果需要）
UPDATE jsh_function 
SET 
    name = '后工任务管理',
    url = '/production/post-processing',
    component = '/production/PostProcessing',
    push_btn = '1,2,3,5,6,7',
    icon = 'tool'
WHERE number = '0604' AND delete_flag = '0';

-- =====================================================
-- 第四步：获取新创建菜单的ID（用于权限分配）
-- =====================================================

-- 4.1 获取新创建菜单的ID
SELECT 
    id as 功能ID,
    number as 菜单编号,
    name as 菜单名称,
    parent_number as 父编号,
    url as URL路径,
    component as 组件路径
FROM jsh_function 
WHERE number IN ('0605', '0606', '0607', '0608', '0609')
AND delete_flag = '0'
ORDER BY number;

-- =====================================================
-- 第五步：权限分配（需要手动执行）
-- =====================================================

-- 5.1 查看当前管理员角色权限
SELECT 
    r.id as 角色ID,
    r.name as 角色名称,
    ub.value as 当前功能权限
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions'
WHERE r.name LIKE '%管理员%' AND r.delete_flag = '0'
ORDER BY r.id;

-- 5.2 为管理员角色添加新功能权限（示例，需要根据实际ID调整）
/*
-- 注意：需要将下面的功能ID替换为实际查询到的ID
UPDATE jsh_user_business 
SET value = CONCAT(
    IFNULL(value, ''), 
    '[新功能ID1][新功能ID2][新功能ID3][新功能ID4][新功能ID5]'
)
WHERE type = 'RoleFunctions' 
AND key_id = '4'   -- 管理员角色ID，需要根据实际情况调整
AND delete_flag = '0';
*/

-- =====================================================
-- 第六步：验证菜单创建结果
-- =====================================================

-- 6.1 验证完整的生产管理菜单层级结构
SELECT 
    f1.number as 一级菜单编号,
    f1.name as 一级菜单名称,
    f2.number as 二级菜单编号,
    f2.name as 二级菜单名称,
    f2.url as URL路径,
    f2.component as 组件路径,
    f2.push_btn as 按钮权限,
    f2.icon as 图标,
    f2.enabled as 是否启用
FROM jsh_function f1
LEFT JOIN jsh_function f2 ON f1.number = f2.parent_number AND f2.delete_flag = '0'
WHERE f1.number = '06' 
AND f1.delete_flag = '0'
ORDER BY f2.number;

-- 6.2 验证菜单URL唯一性
SELECT 
    url as URL路径,
    COUNT(*) as 重复数量,
    GROUP_CONCAT(name) as 菜单名称列表
FROM jsh_function 
WHERE url IS NOT NULL 
AND url != '' 
AND delete_flag = '0'
GROUP BY url
HAVING COUNT(*) > 1;

-- 6.3 验证菜单编号唯一性
SELECT 
    number as 菜单编号,
    COUNT(*) as 重复数量,
    GROUP_CONCAT(name) as 菜单名称列表
FROM jsh_function 
WHERE number IS NOT NULL 
AND number != '' 
AND delete_flag = '0'
GROUP BY number
HAVING COUNT(*) > 1;

-- =====================================================
-- 第七步：清理和回滚脚本（如果需要）
-- =====================================================

-- 7.1 删除新创建的菜单（回滚脚本）
/*
UPDATE jsh_function 
SET delete_flag = '1'
WHERE number IN ('0605', '0606', '0607', '0608', '0609')
AND delete_flag = '0';
*/

-- 7.2 从角色权限中移除（回滚脚本）
/*
UPDATE jsh_user_business 
SET value = REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(
    value, 
    '[功能ID1]', ''), 
    '[功能ID2]', ''), 
    '[功能ID3]', ''), 
    '[功能ID4]', ''), 
    '[功能ID5]', '')
WHERE type = 'RoleFunctions' 
AND delete_flag = '0';
*/

-- =====================================================
-- 脚本执行完成
-- =====================================================
