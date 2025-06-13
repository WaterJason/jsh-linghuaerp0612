#!/bin/bash
# jshERP 开发环境启动脚本

set -e

echo "=========================================="
echo "      jshERP 开发环境启动脚本"
echo "=========================================="

# 检查 Docker 和 Docker Compose
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 检查 .env 文件
if [ ! -f .env ]; then
    echo "❌ .env 文件不存在，请先创建环境配置文件"
    exit 1
fi

# 创建必要的目录
echo "📁 创建必要的目录..."
mkdir -p volumes/mysql volumes/redis volumes/uploads volumes/logs/backend volumes/logs/nginx

# 停止可能存在的容器
echo "🛑 停止现有的容器..."
docker-compose -f docker-compose.dev.yml down --remove-orphans

# 清理悬空镜像（可选）
echo "🧹 清理悬空镜像..."
docker image prune -f

# 启动开发环境
echo "🚀 启动开发环境..."
docker-compose -f docker-compose.dev.yml up -d

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 10

# 检查服务状态
echo "📊 检查服务状态..."
docker-compose -f docker-compose.dev.yml ps

echo ""
echo "=========================================="
echo "✅ 开发环境启动完成！"
echo "=========================================="
echo "📱 前端访问地址: http://localhost:3000"
echo "🔧 后端API地址: http://localhost:9999/jshERP-boot"
echo "🌐 Nginx代理地址: http://localhost:8000"
echo "🗄️ MySQL端口: 3306"
echo "🔴 Redis端口: 6379"
echo ""
echo "📋 查看日志: docker-compose -f docker-compose.dev.yml logs -f [服务名]"
echo "🛑 停止服务: docker-compose -f docker-compose.dev.yml down"
echo "=========================================="