#!/bin/bash
# jshERP 环境停止脚本

set -e

echo "=========================================="
echo "        jshERP 环境停止脚本"
echo "=========================================="

# 参数处理
ENV=${1:-dev}

if [ "$ENV" = "dev" ]; then
    echo "🛑 停止开发环境..."
    docker-compose -f docker-compose.dev.yml down --remove-orphans
elif [ "$ENV" = "prod" ]; then
    echo "🛑 停止生产环境..."
    docker-compose -f docker-compose.prod.yml down --remove-orphans
elif [ "$ENV" = "all" ]; then
    echo "🛑 停止所有环境..."
    docker-compose -f docker-compose.dev.yml down --remove-orphans 2>/dev/null || true
    docker-compose -f docker-compose.prod.yml down --remove-orphans 2>/dev/null || true
    docker-compose -f docker-compose.yml down --remove-orphans 2>/dev/null || true
else
    echo "❌ 无效的环境参数: $ENV"
    echo "用法: $0 [dev|prod|all]"
    echo "  dev  - 停止开发环境 (默认)"
    echo "  prod - 停止生产环境"
    echo "  all  - 停止所有环境"
    exit 1
fi

echo "✅ 环境停止完成！"