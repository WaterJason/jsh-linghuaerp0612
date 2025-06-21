-- ========================================
-- jshERP 生产管理模块菜单配置SQL脚本
-- 创建时间: 2025-06-21
-- 说明: 为生产管理模块创建菜单结构
-- ========================================

-- ----------------------------
-- 1. 创建一级菜单：生产管理
-- ----------------------------
INSERT INTO `jsh_function` (
    `number`, 
    `name`, 
    `parent_number`, 
    `url`, 
    `component`, 
    `state`, 
    `sort`, 
    `enabled`, 
    `type`, 
    `push_btn`, 
    `icon`, 
    `delete_flag`
) VALUES (
    '05',                           -- 编号：05（一级菜单）
    '生产管理',                      -- 名称
    '0',                            -- 父级编号：0表示一级菜单
    '/production',                  -- URL路径
    '/layouts/TabLayout',           -- 组件路径（目录类型使用TabLayout）
    b'0',                          -- 收缩状态：0-展开
    '0500',                        -- 排序：0500
    b'1',                          -- 启用状态：1-启用
    '电脑版',                       -- 类型：固定值
    '',                            -- 按钮权限：目录类型为空
    'tool',                        -- 图标：生产工具图标
    '0'                            -- 删除标记：0-正常
);

-- ----------------------------
-- 2. 创建二级菜单：生产订单
-- ----------------------------
INSERT INTO `jsh_function` (
    `number`, 
    `name`, 
    `parent_number`, 
    `url`, 
    `component`, 
    `state`, 
    `sort`, 
    `enabled`, 
    `type`, 
    `push_btn`, 
    `icon`, 
    `delete_flag`
) VALUES (
    '0501',                         -- 编号：0501（二级菜单）
    '生产订单',                      -- 名称
    '05',                           -- 父级编号：05
    '/production/order',            -- URL路径
    '/production/ProductionOrderList', -- 组件路径
    b'0',                          -- 收缩状态：0-展开
    '0510',                        -- 排序：0510
    b'1',                          -- 启用状态：1-启用
    '电脑版',                       -- 类型：固定值
    'add,edit,delete,batch_delete,check,finish', -- 按钮权限
    'profile',                     -- 图标
    '0'                            -- 删除标记：0-正常
);

-- ----------------------------
-- 3. 创建二级菜单：崇左生产看板
-- ----------------------------
INSERT INTO `jsh_function` (
    `number`, 
    `name`, 
    `parent_number`, 
    `url`, 
    `component`, 
    `state`, 
    `sort`, 
    `enabled`, 
    `type`, 
    `push_btn`, 
    `icon`, 
    `delete_flag`
) VALUES (
    '0502',                         -- 编号：0502（二级菜单）
    '崇左生产看板',                  -- 名称
    '05',                           -- 父级编号：05
    '/production/kanban',           -- URL路径
    '/production/ChongzuoKanban',   -- 组件路径
    b'0',                          -- 收缩状态：0-展开
    '0520',                        -- 排序：0520
    b'1',                          -- 启用状态：1-启用
    '电脑版',                       -- 类型：固定值
    'dispatch,complete',           -- 按钮权限：派单,完工
    'dashboard',                   -- 图标：看板图标
    '0'                            -- 删除标记：0-正常
);

-- ----------------------------
-- 4. 创建二级菜单：后工任务列表
-- ----------------------------
INSERT INTO `jsh_function` (
    `number`, 
    `name`, 
    `parent_number`, 
    `url`, 
    `component`, 
    `state`, 
    `sort`, 
    `enabled`, 
    `type`, 
    `push_btn`, 
    `icon`, 
    `delete_flag`
) VALUES (
    '0503',                         -- 编号：0503（二级菜单）
    '后工任务列表',                  -- 名称
    '05',                           -- 父级编号：05
    '/production/post-task',        -- URL路径
    '/production/PostProcessingTaskList', -- 组件路径
    b'0',                          -- 收缩状态：0-展开
    '0530',                        -- 排序：0530
    b'1',                          -- 启用状态：1-启用
    '电脑版',                       -- 类型：固定值
    'claim,complete',              -- 按钮权限：认领,完工
    'ordered-list',                -- 图标：任务列表图标
    '0'                            -- 删除标记：0-正常
);

-- ----------------------------
-- 验证插入结果的查询语句（可选执行）
-- ----------------------------
-- SELECT 
--     number, 
--     name, 
--     parent_number, 
--     url, 
--     component, 
--     push_btn, 
--     icon
-- FROM jsh_function 
-- WHERE number IN ('05', '0501', '0502', '0503') 
-- ORDER BY number;

-- ----------------------------
-- 说明：
-- 1. 一级菜单编号 05 确认未被使用，可以安全创建
-- 2. 排序规则：一级菜单0500，二级菜单0510、0520、0530
-- 3. 按钮权限说明：
--    - add: 新增
--    - edit: 编辑  
--    - delete: 删除
--    - batch_delete: 批量删除
--    - check: 审核
--    - finish: 完成
--    - dispatch: 派单
--    - complete: 完工
--    - claim: 认领
-- 4. 图标使用Ant Design图标库中的标准图标
-- 5. 所有菜单默认启用，租户ID为0（全局菜单）
-- ----------------------------
