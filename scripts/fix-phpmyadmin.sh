#!/bin/bash

# jshERP phpMyAdmin 修复启动脚本
# 解决MySQL连接问题的系统性修复方案

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Docker是否运行
check_docker() {
    log_info "检查Docker环境..."
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未运行，请启动 Docker Desktop 后重试"
        log_info "启动Docker Desktop: open -a Docker"
        exit 1
    fi
    log_success "Docker 环境检查通过"
}

# 清理现有容器和网络
cleanup_existing() {
    log_info "清理现有容器和网络..."
    
    # 停止并删除phpMyAdmin容器
    if docker ps -a --format "table {{.Names}}" | grep -q "jsherp-phpmyadmin"; then
        log_info "停止现有phpMyAdmin容器..."
        docker stop jsherp-phpmyadmin || true
        docker rm jsherp-phpmyadmin || true
    fi
    
    # 停止并删除MySQL容器（如果存在）
    if docker ps -a --format "table {{.Names}}" | grep -q "jsherp-mysql-dev"; then
        log_warning "发现现有MySQL容器，将重启..."
        docker stop jsherp-mysql-dev || true
    fi
}

# 启动基础设施服务
start_infrastructure() {
    log_info "启动jshERP基础设施服务..."
    
    # 使用docker-compose启动MySQL和Redis
    if [ -f "docker-compose.dev.yml" ]; then
        log_info "使用开发环境配置启动MySQL和Redis..."
        docker-compose -f docker-compose.dev.yml up -d jsherp-mysql jsherp-redis
        
        # 等待MySQL启动
        log_info "等待MySQL服务启动..."
        sleep 15
        
        # 检查MySQL是否健康
        for i in {1..30}; do
            if docker exec jsherp-mysql-dev mysqladmin ping -h localhost -u root -p123456 > /dev/null 2>&1; then
                log_success "MySQL服务已就绪"
                break
            fi
            if [ $i -eq 30 ]; then
                log_error "MySQL服务启动超时"
                exit 1
            fi
            sleep 2
        done
    else
        log_error "未找到docker-compose.dev.yml文件"
        exit 1
    fi
}

# 启动phpMyAdmin
start_phpmyadmin() {
    log_info "启动phpMyAdmin服务..."
    
    # 使用docker-compose启动phpMyAdmin
    docker-compose -f docker-compose.phpmyadmin.yml up -d phpmyadmin
    
    # 等待phpMyAdmin启动
    log_info "等待phpMyAdmin启动..."
    sleep 10
    
    # 检查phpMyAdmin状态
    for i in {1..20}; do
        if curl -s http://localhost:8081 > /dev/null 2>&1; then
            log_success "phpMyAdmin Web界面已就绪"
            return 0
        fi
        if [ $i -eq 20 ]; then
            log_error "phpMyAdmin启动超时"
            docker logs jsherp-phpmyadmin
            exit 1
        fi
        sleep 3
    done
}

# 验证数据库连接
verify_connection() {
    log_info "验证数据库连接..."
    
    # 检查jsh_erp数据库是否存在
    if docker exec jsherp-mysql-dev mysql -u root -p123456 -e "USE jsh_erp; SHOW TABLES LIMIT 1;" > /dev/null 2>&1; then
        log_success "jsh_erp数据库连接正常"
    else
        log_warning "jsh_erp数据库可能不存在，但MySQL连接正常"
        log_info "可以通过phpMyAdmin创建数据库"
    fi
}

# 显示访问信息
show_access_info() {
    log_success "=== phpMyAdmin 修复完成 ==="
    echo ""
    echo "🌐 访问地址: http://localhost:8081"
    echo "👤 用户名: root"
    echo "🔑 密码: 123456"
    echo "🗄️  数据库: jsh_erp"
    echo ""
    log_info "连接信息:"
    echo "   服务器: jsherp-mysql-dev"
    echo "   端口: 3306"
    echo "   用户: root / jsh_user"
    echo "   密码: 123456"
    echo ""
    log_success "修复完成！现在可以正常使用phpMyAdmin管理jshERP数据库了。"
}

# 主函数
main() {
    echo "🔧 jshERP phpMyAdmin 修复工具"
    echo "================================"
    
    check_docker
    cleanup_existing
    start_infrastructure
    start_phpmyadmin
    verify_connection
    show_access_info
    
    # 自动打开浏览器
    if command -v open > /dev/null; then
        log_info "正在打开浏览器..."
        open http://localhost:8081
    fi
}

# 脚本入口
main "$@"
