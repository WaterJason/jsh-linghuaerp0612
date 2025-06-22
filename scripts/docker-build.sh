#!/bin/bash

# jshERP Docker环境构建脚本
# 作者: Augment Code
# 日期: 2025-06-21
# 描述: 自动化构建和启动jshERP开发环境

set -e  # 遇到错误立即退出

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
    log_info "检查Docker运行状态..."
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker未运行，请先启动Docker"
        exit 1
    fi
    log_success "Docker运行正常"
}

# 检查docker-compose文件
check_compose_files() {
    log_info "检查docker-compose配置文件..."
    if [[ ! -f "docker-compose.yml" ]]; then
        log_error "docker-compose.yml文件不存在"
        exit 1
    fi
    if [[ ! -f "docker-compose.dev.yml" ]]; then
        log_error "docker-compose.dev.yml文件不存在"
        exit 1
    fi
    log_success "docker-compose配置文件检查通过"
}

# 清理旧容器
cleanup_containers() {
    log_info "清理旧容器..."
    
    # 停止所有相关容器
    containers=("jsherp-frontend-dev" "jsherp-backend-dev" "jsherp-mysql-dev" "jsherp-redis-dev" "jsherp-nginx-dev" "jsherp-phpmyadmin")
    
    for container in "${containers[@]}"; do
        if docker ps -a --format "table {{.Names}}" | grep -q "^${container}$"; then
            log_info "停止容器: $container"
            docker stop "$container" >/dev/null 2>&1 || true
            log_info "删除容器: $container"
            docker rm "$container" >/dev/null 2>&1 || true
        fi
    done
    
    log_success "容器清理完成"
}

# 构建镜像
build_images() {
    log_info "构建Docker镜像..."
    
    # 构建后端镜像
    log_info "构建后端镜像..."
    docker-compose -f docker-compose.dev.yml build jsherp-backend-dev
    
    # 构建Nginx镜像
    log_info "构建Nginx镜像..."
    docker-compose -f docker-compose.dev.yml build jsherp-nginx-dev
    
    log_success "镜像构建完成"
}

# 启动服务
start_services() {
    log_info "启动jshERP服务..."
    
    # 按顺序启动服务
    log_info "启动基础服务 (MySQL, Redis)..."
    docker-compose -f docker-compose.dev.yml up -d jsherp-mysql-dev jsherp-redis-dev
    
    # 等待数据库启动
    log_info "等待MySQL数据库启动..."
    sleep 30
    
    # 检查MySQL健康状态
    for i in {1..30}; do
        if docker exec jsherp-mysql-dev mysqladmin ping -h localhost -u jsh_user -p123456 >/dev/null 2>&1; then
            log_success "MySQL数据库启动成功"
            break
        fi
        if [[ $i -eq 30 ]]; then
            log_error "MySQL数据库启动超时"
            exit 1
        fi
        sleep 2
    done
    
    # 启动后端服务
    log_info "启动后端服务..."
    docker-compose -f docker-compose.dev.yml up -d jsherp-backend-dev
    
    # 等待后端服务启动
    log_info "等待后端服务启动..."
    sleep 60
    
    # 启动前端服务
    log_info "启动前端服务..."
    docker-compose -f docker-compose.dev.yml up -d jsherp-frontend-dev
    
    # 等待前端服务启动
    log_info "等待前端服务启动..."
    sleep 90
    
    # 启动Nginx代理
    log_info "启动Nginx代理..."
    docker-compose -f docker-compose.dev.yml up -d jsherp-nginx-dev
    
    # 启动phpMyAdmin
    log_info "启动phpMyAdmin..."
    docker run -d --name jsherp-phpmyadmin \
        --network jsherp_network_dev \
        -p 8081:80 \
        -e PMA_HOST=jsherp-mysql-dev \
        -e PMA_PORT=3306 \
        -e PMA_USER=jsh_user \
        -e PMA_PASSWORD=123456 \
        phpmyadmin/phpmyadmin:latest
    
    log_success "所有服务启动完成"
}

# 检查服务状态
check_services() {
    log_info "检查服务运行状态..."
    
    # 检查容器状态
    containers=("jsherp-mysql-dev" "jsherp-redis-dev" "jsherp-backend-dev" "jsherp-frontend-dev" "jsherp-nginx-dev" "jsherp-phpmyadmin")
    
    for container in "${containers[@]}"; do
        if docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "^${container}"; then
            status=$(docker ps --format "table {{.Names}}\t{{.Status}}" | grep "^${container}" | awk '{print $2}')
            log_success "$container: $status"
        else
            log_error "$container: 未运行"
        fi
    done
    
    # 检查端口访问
    log_info "检查服务端口..."
    
    ports=("3306:MySQL" "6379:Redis" "9999:后端API" "8080:前端Web" "8000:Nginx代理" "8081:phpMyAdmin")
    
    for port_info in "${ports[@]}"; do
        port=$(echo "$port_info" | cut -d: -f1)
        service=$(echo "$port_info" | cut -d: -f2)
        
        if nc -z localhost "$port" 2>/dev/null; then
            log_success "$service (端口$port): 可访问"
        else
            log_warning "$service (端口$port): 不可访问"
        fi
    done
}

# 显示访问信息
show_access_info() {
    log_info "jshERP开发环境访问信息:"
    echo ""
    echo -e "${GREEN}前端应用:${NC}     http://localhost:8080"
    echo -e "${GREEN}后端API:${NC}      http://localhost:9999"
    echo -e "${GREEN}Nginx代理:${NC}    http://localhost:8000"
    echo -e "${GREEN}phpMyAdmin:${NC}   http://localhost:8081"
    echo -e "${GREEN}MySQL数据库:${NC}  localhost:3306 (用户: jsh_user, 密码: 123456)"
    echo -e "${GREEN}Redis缓存:${NC}    localhost:6379"
    echo ""
    echo -e "${YELLOW}默认登录账号:${NC}"
    echo -e "  管理员: waterxi / 123456"
    echo -e "  租户用户: jsh / 123456"
    echo ""
}

# 主函数
main() {
    log_info "开始构建jshERP Docker开发环境..."
    
    # 检查前置条件
    check_docker
    check_compose_files
    
    # 询问是否清理旧容器
    read -p "是否清理旧容器? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        cleanup_containers
    fi
    
    # 询问是否重新构建镜像
    read -p "是否重新构建镜像? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        build_images
    fi
    
    # 启动服务
    start_services
    
    # 检查服务状态
    sleep 10
    check_services
    
    # 显示访问信息
    show_access_info
    
    log_success "jshERP Docker开发环境构建完成!"
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
