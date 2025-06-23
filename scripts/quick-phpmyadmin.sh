#!/bin/bash

# jshERP phpMyAdmin 快速启动脚本
# 修复网络配置问题后的简化启动

set -e

echo "🚀 启动phpMyAdmin..."

# 停止现有phpMyAdmin容器
echo "清理现有容器..."
docker stop jsherp-phpmyadmin 2>/dev/null || true
docker rm jsherp-phpmyadmin 2>/dev/null || true

# 启动phpMyAdmin
echo "启动phpMyAdmin服务..."
docker-compose -f docker-compose.phpmyadmin.yml up -d phpmyadmin

# 等待启动
echo "等待服务启动..."
sleep 10

# 检查状态
if docker ps --format "table {{.Names}}" | grep -q "jsherp-phpmyadmin"; then
    echo "✅ phpMyAdmin启动成功！"
    echo ""
    echo "🌐 访问地址: http://localhost:8081"
    echo "👤 用户名: root"
    echo "🔑 密码: 123456"
    echo "🗄️  数据库: jsh_erp"
    echo ""
    
    # 自动打开浏览器
    if command -v open > /dev/null; then
        echo "正在打开浏览器..."
        open http://localhost:8081
    fi
else
    echo "❌ phpMyAdmin启动失败"
    echo "查看日志:"
    docker logs jsherp-phpmyadmin
fi
