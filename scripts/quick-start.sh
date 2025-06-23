#!/bin/bash

# jshERP 快速启动脚本
# 用于快速启动已配置好的jshERP开发环境

set -e

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# 检查Docker状态
check_docker() {
    if ! docker info >/dev/null 2>&1; then
        echo "Docker未运行，请先启动Docker"
        exit 1
    fi
}

# 启动所有服务
start_all_services() {
    log_info "启动jshERP开发环境..."
    
    # 启动基础服务
    log_info "启动MySQL和Redis..."
    docker start jsherp-mysql-dev jsherp-redis-dev 2>/dev/null || {
        log_warning "基础服务容器不存在，使用docker-compose启动..."
        cd /Users/macmini/Desktop/jshERP-0612-Cursor
        docker-compose -f docker-compose.dev.yml up -d jsherp-mysql-dev jsherp-redis-dev
    }
    
    # 等待数据库就绪
    log_info "等待MySQL数据库就绪..."
    sleep 15
    
    # 启动后端服务
    log_info "启动后端服务..."
    docker start jsherp-backend-dev 2>/dev/null || {
        cd /Users/macmini/Desktop/jshERP-0612-Cursor
        docker-compose -f docker-compose.dev.yml up -d jsherp-backend-dev
    }
    
    # 等待后端就绪
    log_info "等待后端服务就绪..."
    sleep 30
    
    # 启动前端服务
    log_info "启动前端服务..."
    docker start jsherp-frontend-dev 2>/dev/null || {
        cd /Users/macmini/Desktop/jshERP-0612-Cursor
        docker-compose -f docker-compose.dev.yml up -d jsherp-frontend-dev
    }
    
    # 启动Nginx
    log_info "启动Nginx代理..."
    docker start jsherp-nginx-dev 2>/dev/null || {
        cd /Users/macmini/Desktop/jshERP-0612-Cursor
        docker-compose -f docker-compose.dev.yml up -d jsherp-nginx-dev
    }
    
    # 启动phpMyAdmin
    log_info "启动phpMyAdmin..."
    docker start jsherp-phpmyadmin 2>/dev/null || {
        docker run -d --name jsherp-phpmyadmin \
            --network jsherp_network_dev \
            -p 8081:80 \
            -e PMA_HOST=jsherp-mysql-dev \
            -e PMA_PORT=3306 \
            -e PMA_USER=jsh_user \
            -e PMA_PASSWORD=123456 \
            phpmyadmin/phpmyadmin:latest
    }
    
    log_success "所有服务启动完成"
}

# 检查服务状态
check_status() {
    log_info "检查服务状态..."
    
    services=("jsherp-mysql-dev:MySQL数据库" "jsherp-redis-dev:Redis缓存" "jsherp-backend-dev:后端服务" "jsherp-frontend-dev:前端服务" "jsherp-nginx-dev:Nginx代理" "jsherp-phpmyadmin:phpMyAdmin")
    
    for service in "${services[@]}"; do
        container=$(echo "$service" | cut -d: -f1)
        name=$(echo "$service" | cut -d: -f2)
        
        if docker ps --format "table {{.Names}}" | grep -q "^${container}$"; then
            log_success "$name: 运行中"
        else
            log_warning "$name: 未运行"
        fi
    done
}

# 显示访问信息
show_info() {
    echo ""
    echo -e "${GREEN}=== jshERP 开发环境访问信息 ===${NC}"
    echo ""
    echo -e "${GREEN}前端应用:${NC}     http://localhost:8080"
    echo -e "${GREEN}后端API:${NC}      http://localhost:9999"
    echo -e "${GREEN}Nginx代理:${NC}    http://localhost:8000"
    echo -e "${GREEN}phpMyAdmin:${NC}   http://localhost:8081"
    echo ""
    echo -e "${YELLOW}默认登录账号:${NC}"
    echo -e "  管理员: waterxi / 123456"
    echo -e "  租户用户: jsh / 123456"
    echo ""
}

# 主函数
main() {
    check_docker
    start_all_services
    sleep 10
    check_status
    show_info
}

# 如果直接执行此脚本
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
