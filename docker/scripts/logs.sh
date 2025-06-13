#!/bin/bash
# jshERP 日志查看脚本

set -e

echo "=========================================="
echo "        jshERP 日志查看脚本"
echo "=========================================="

# 参数处理
ENV=${1:-dev}
SERVICE=${2:-}

if [ "$ENV" = "dev" ]; then
    COMPOSE_FILE="docker-compose.dev.yml"
elif [ "$ENV" = "prod" ]; then
    COMPOSE_FILE="docker-compose.prod.yml"
else
    echo "❌ 无效的环境参数: $ENV"
    echo "用法: $0 [dev|prod] [服务名]"
    echo "环境参数:"
    echo "  dev  - 开发环境 (默认)"
    echo "  prod - 生产环境"
    echo "服务名 (可选):"
    echo "  jsherp-mysql     - MySQL 数据库"
    echo "  jsherp-redis     - Redis 缓存"
    echo "  jsherp-backend   - 后端服务"
    echo "  jsherp-frontend  - 前端服务"
    echo "  jsherp-nginx     - Nginx 代理"
    exit 1
fi

if [ -z "$SERVICE" ]; then
    echo "📋 查看所有服务日志..."
    docker-compose -f $COMPOSE_FILE logs -f
else
    echo "📋 查看 $SERVICE 服务日志..."
    docker-compose -f $COMPOSE_FILE logs -f $SERVICE
fi