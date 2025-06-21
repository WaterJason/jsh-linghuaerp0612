-- =====================================================
-- jshERP 生产管理模块菜单配置脚本
-- 创建时间: 2025-06-21
-- 说明: 根据jshERP菜单配置规范创建完整的生产管理菜单结构
-- =====================================================

-- =====================================================
-- 第一步：检查编号可用性
-- =====================================================

-- 1.1 检查一级菜单编号使用情况
SELECT 
    number as 菜单编号,
    name as 菜单名称,
    parent_number as 父编号,
    type as 类型
FROM jsh_function 
WHERE parent_number = '0' 
AND delete_flag = '0'
ORDER BY number;

-- 1.2 检查编号"05"是否被占用
SELECT 
    COUNT(*) as 编号05使用数量,
    CASE 
        WHEN COUNT(*) = 0 THEN '编号05可用'
        ELSE '编号05已被占用，需要使用其他编号'
    END as 可用性检查
FROM jsh_function 
WHERE number = '05' 
AND delete_flag = '0';

-- 1.3 查找下一个可用的一级菜单编号
SELECT 
    CONCAT('建议使用编号: ', 
        LPAD(
            CAST(
                COALESCE(
                    (SELECT MAX(CAST(number AS UNSIGNED)) + 1 
                     FROM jsh_function 
                     WHERE parent_number = '0' 
                     AND delete_flag = '0' 
                     AND number REGEXP '^[0-9]+$'
                     AND LENGTH(number) <= 2), 
                    5
                ) AS CHAR
            ), 
            2, 
            '0'
        )
    ) as 推荐编号;

-- =====================================================
-- 第二步：创建生产管理菜单结构
-- =====================================================

-- 2.1 创建一级菜单：生产管理
INSERT INTO jsh_function (
    number,           -- 菜单编号
    name,             -- 菜单名称
    parent_number,    -- 父菜单编号（0表示一级菜单）
    url,              -- URL路径
    component,        -- 组件路径
    state,            -- 状态
    sort,             -- 排序号
    enabled,          -- 是否启用
    type,             -- 类型
    push_btn,         -- 按钮权限（一级菜单为空）
    icon,             -- 图标
    delete_flag       -- 删除标志
) VALUES (
    '05',                           -- 使用编号05
    '生产管理',                     -- 菜单名称
    '0',                            -- 一级菜单
    '/production',                  -- URL路径
    'layouts/RouteView',            -- 一级菜单组件（标准布局）
    0,                              -- 状态：正常
    '0500',                         -- 排序号
    1,                              -- 启用
    '电脑版',                       -- 类型
    '',                             -- 一级菜单无按钮权限
    'build',                        -- 生产管理图标（工厂/建设图标）
    '0'                             -- 未删除
);

-- 2.2 创建二级菜单：生产订单管理
INSERT INTO jsh_function (
    number,
    name,
    parent_number,
    url,
    component,
    state,
    sort,
    enabled,
    type,
    push_btn,
    icon,
    delete_flag
) VALUES (
    '0501',                                    -- 二级菜单编号
    '生产订单',                                -- 菜单名称
    '05',                                      -- 父菜单编号
    '/production/order',                       -- URL路径
    'production/ProductionOrderList',          -- Vue组件路径
    0,                                         -- 状态
    '0501',                                    -- 排序号
    1,                                         -- 启用
    '电脑版',                                  -- 类型
    '1,2,3,4,5,6,7',                          -- 按钮权限：新增,审核,导出,启用禁用,打印,作废,反审核
    'profile',                                 -- 订单管理图标
    '0'                                        -- 未删除
);

-- 2.3 创建二级菜单：崇左生产看板
INSERT INTO jsh_function (
    number,
    name,
    parent_number,
    url,
    component,
    state,
    sort,
    enabled,
    type,
    push_btn,
    icon,
    delete_flag
) VALUES (
    '0502',                                    -- 二级菜单编号
    '崇左生产看板',                            -- 菜单名称
    '05',                                      -- 父菜单编号
    '/production/kanban',                      -- URL路径
    'production/ChongzuoKanban',               -- Vue组件路径
    0,                                         -- 状态
    '0502',                                    -- 排序号
    1,                                         -- 启用
    '电脑版',                                  -- 类型
    '1,3,5',                                   -- 按钮权限：新增(派工),导出,打印
    'dashboard',                               -- 看板图标
    '0'                                        -- 未删除
);

-- 2.4 创建二级菜单：后工任务列表
INSERT INTO jsh_function (
    number,
    name,
    parent_number,
    url,
    component,
    state,
    sort,
    enabled,
    type,
    push_btn,
    icon,
    delete_flag
) VALUES (
    '0503',                                    -- 二级菜单编号
    '后工任务列表',                            -- 菜单名称
    '05',                                      -- 父菜单编号
    '/production/post-task',                   -- URL路径
    'production/PostProcessingTaskList',       -- Vue组件路径
    0,                                         -- 状态
    '0503',                                    -- 排序号
    1,                                         -- 启用
    '电脑版',                                  -- 类型
    '1,2,3',                                   -- 按钮权限：新增(认领),审核(完成),导出
    'ordered-list',                            -- 任务列表图标
    '0'                                        -- 未删除
);

-- =====================================================
-- 第三步：权限分配
-- =====================================================

-- 3.1 获取新创建菜单的ID（用于权限分配）
SELECT 
    id as 功能ID,
    number as 菜单编号,
    name as 菜单名称,
    parent_number as 父编号
FROM jsh_function 
WHERE number IN ('05', '0501', '0502', '0503')
AND delete_flag = '0'
ORDER BY number;

-- 3.2 为租户角色(ID=10)分配生产管理权限
-- 注意：需要先获取上面查询的功能ID，然后替换下面的[功能ID]
UPDATE jsh_user_business 
SET value = CONCAT(
    COALESCE(value, ''),
    '[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0501' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0502' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0503' AND delete_flag = '0'), ']'
)
WHERE type = 'RoleFunctions' 
AND key_id = '10'  -- 租户角色ID
AND delete_flag = '0'
AND value NOT LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%');

-- 3.3 为管理员角色(ID=4)分配生产管理权限
UPDATE jsh_user_business 
SET value = CONCAT(
    COALESCE(value, ''),
    '[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0501' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0502' AND delete_flag = '0'), ']',
    '[', (SELECT id FROM jsh_function WHERE number = '0503' AND delete_flag = '0'), ']'
)
WHERE type = 'RoleFunctions' 
AND key_id = '4'   -- 管理员角色ID
AND delete_flag = '0'
AND value NOT LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%');

-- =====================================================
-- 第四步：验证菜单创建结果
-- =====================================================

-- 4.1 验证菜单层级结构
SELECT 
    f1.number as 一级菜单编号,
    f1.name as 一级菜单名称,
    f2.number as 二级菜单编号,
    f2.name as 二级菜单名称,
    f2.url as URL路径,
    f2.component as 组件路径,
    f2.push_btn as 按钮权限
FROM jsh_function f1
LEFT JOIN jsh_function f2 ON f1.number = f2.parent_number AND f2.delete_flag = '0'
WHERE f1.number = '05' 
AND f1.delete_flag = '0'
ORDER BY f2.number;

-- 4.2 验证权限分配结果
SELECT 
    r.id as 角色ID,
    r.name as 角色名称,
    ub.value as 功能权限列表,
    CASE 
        WHEN ub.value LIKE CONCAT('%[', (SELECT id FROM jsh_function WHERE number = '05' AND delete_flag = '0'), ']%') 
        THEN '✅ 已分配生产管理权限'
        ELSE '❌ 未分配生产管理权限'
    END as 权限状态
FROM jsh_role r
LEFT JOIN jsh_user_business ub ON r.id = ub.key_id AND ub.type = 'RoleFunctions' AND ub.delete_flag = '0'
WHERE r.id IN (4, 10, 21, 22)  -- 主要角色
AND r.delete_flag = '0'
ORDER BY r.id;

-- 4.3 验证用户权限（以waterxi用户为例）
SELECT 
    u.username as 用户名,
    r.name as 角色名称,
    f.number as 菜单编号,
    f.name as 菜单名称,
    f.url as URL路径
FROM jsh_user u
JOIN jsh_user_business ub1 ON u.id = ub1.key_id AND ub1.type = 'UserRole' AND ub1.delete_flag = '0'
JOIN jsh_role r ON FIND_IN_SET(r.id, REPLACE(REPLACE(ub1.value, '[', ''), ']', ''))
JOIN jsh_user_business ub2 ON r.id = ub2.key_id AND ub2.type = 'RoleFunctions' AND ub2.delete_flag = '0'
JOIN jsh_function f ON FIND_IN_SET(f.id, REPLACE(REPLACE(ub2.value, '[', ''), ']', ''))
WHERE u.username = 'waterxi' 
AND f.number LIKE '05%'  -- 生产管理相关菜单
AND f.delete_flag = '0'
ORDER BY f.number;

-- =====================================================
-- 第五步：清理和回滚脚本（如需要）
-- =====================================================

-- 5.1 回滚脚本（谨慎使用）
/*
-- 删除生产管理菜单（软删除）
UPDATE jsh_function 
SET delete_flag = '1' 
WHERE number IN ('05', '0501', '0502', '0503');

-- 从角色权限中移除生产管理权限
UPDATE jsh_user_business 
SET value = REPLACE(value, CONCAT('[', (SELECT id FROM jsh_function WHERE number = '05'), ']'), '')
WHERE type = 'RoleFunctions';

UPDATE jsh_user_business 
SET value = REPLACE(value, CONCAT('[', (SELECT id FROM jsh_function WHERE number = '0501'), ']'), '')
WHERE type = 'RoleFunctions';

UPDATE jsh_user_business 
SET value = REPLACE(value, CONCAT('[', (SELECT id FROM jsh_function WHERE number = '0502'), ']'), '')
WHERE type = 'RoleFunctions';

UPDATE jsh_user_business 
SET value = REPLACE(value, CONCAT('[', (SELECT id FROM jsh_function WHERE number = '0503'), ']'), '')
WHERE type = 'RoleFunctions';
*/

-- =====================================================
-- 执行说明和注意事项
-- =====================================================

/*
执行顺序：
1. 先执行第一步的检查脚本，确认编号05可用
2. 如果编号05被占用，修改脚本中的编号为可用编号
3. 执行第二步创建菜单结构
4. 执行第三步分配权限
5. 执行第四步验证结果

注意事项：
1. 执行前请备份数据库
2. 确保前端Vue组件文件已创建
3. 权限分配后需要用户重新登录才能看到新菜单
4. 如果菜单不显示，检查组件路径是否正确
5. 按钮权限字符串格式：1=新增,2=审核,3=导出,4=启用禁用,5=打印,6=作废,7=反审核

前端缓存清理：
- 用户需要清除浏览器缓存或重新登录
- localStorage.clear(); sessionStorage.clear(); location.reload();

组件路径说明：
- 一级菜单：layouts/RouteView（固定）
- 二级菜单：模块名/组件名（相对于views目录）
- 实际文件路径：src/views/production/ProductionOrderList.vue
*/
