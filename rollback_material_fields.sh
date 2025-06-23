#!/bin/bash

# jshERP商品管理字段优化回滚脚本
# 使用方法: ./rollback_material_fields.sh

echo "开始回滚jshERP商品管理字段优化..."

# 1. 恢复前端文件
echo "1. 恢复前端文件..."
if [ -f "jshERP-web/src/views/material/modules/MaterialModal.vue.backup" ]; then
    cp jshERP-web/src/views/material/modules/MaterialModal.vue.backup jshERP-web/src/views/material/modules/MaterialModal.vue
    echo "   ✓ MaterialModal.vue 已恢复"
else
    echo "   ✗ MaterialModal.vue.backup 文件不存在"
fi

if [ -f "jshERP-web/src/views/material/MaterialList.vue.backup" ]; then
    cp jshERP-web/src/views/material/MaterialList.vue.backup jshERP-web/src/views/material/MaterialList.vue
    echo "   ✓ MaterialList.vue 已恢复"
else
    echo "   ✗ MaterialList.vue.backup 文件不存在"
fi

# 2. 恢复数据库配置
echo "2. 恢复数据库MaterialProperty配置..."
echo "请手动执行以下SQL语句来恢复数据库配置："
echo ""
echo "-- 删除新增的MaterialProperty配置"
echo "DELETE FROM jsh_material_property WHERE id IN (7, 8);"
echo ""
echo "-- 恢复原有配置（如果需要）"
echo "UPDATE jsh_material_property SET another_name = '作品描述' WHERE id = 6;"
echo ""

# 3. 重启前端服务
echo "3. 重启前端服务..."
docker-compose -f docker-compose.dev.yml restart jsherp-frontend-dev

echo ""
echo "回滚完成！"
echo "注意：请手动执行上述SQL语句来完全恢复数据库配置。"
