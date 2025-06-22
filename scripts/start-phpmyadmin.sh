#!/bin/bash

# jshERP phpMyAdmin 启动脚本
# 用于快速启动phpMyAdmin管理jshERP数据库

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
    if ! docker info > /dev/null 2>&1; then
        log_error "Docker 未运行，请启动 Docker 后重试"
        exit 1
    fi
    log_info "Docker 环境检查通过"
}

# 检查jshERP MySQL容器是否运行
check_mysql_container() {
    log_info "检查jshERP MySQL容器状态..."
    
    if docker ps --format "table {{.Names}}" | grep -q "jsherp-mysql-dev"; then
        log_success "jshERP MySQL容器正在运行"
        return 0
    elif docker ps -a --format "table {{.Names}}" | grep -q "jsherp-mysql-dev"; then
        log_warning "jshERP MySQL容器已停止，正在启动..."
        docker start jsherp-mysql-dev
        sleep 10
        log_success "jshERP MySQL容器已启动"
        return 0
    else
        log_error "未找到jshERP MySQL容器，请先启动jshERP开发环境"
        log_info "请运行: docker-compose -f docker-compose.dev.yml up -d"
        exit 1
    fi
}

# 检查网络是否存在
check_network() {
    log_info "检查Docker网络..."
    
    if ! docker network ls | grep -q "jsherp_dev_network"; then
        log_warning "jsherp_dev_network 网络不存在，正在创建..."
        docker network create jsherp_dev_network
        log_success "网络创建成功"
    else
        log_success "Docker网络检查通过"
    fi
}

# 启动phpMyAdmin
start_phpmyadmin() {
    log_info "启动phpMyAdmin..."
    
    # 检查是否已经运行
    if docker ps --format "table {{.Names}}" | grep -q "jsherp-phpmyadmin"; then
        log_warning "phpMyAdmin已经在运行，正在重启..."
        docker stop jsherp-phpmyadmin
        docker rm jsherp-phpmyadmin
    fi
    
    # 启动phpMyAdmin容器
    docker run -d \
        --name jsherp-phpmyadmin \
        --network jsherp_dev_network \
        -e PMA_HOST=jsherp-mysql-dev \
        -e PMA_PORT=3306 \
        -e PMA_USER=root \
        -e PMA_PASSWORD=123456 \
        -e PMA_ARBITRARY=1 \
        -e UPLOAD_LIMIT=100M \
        -e MEMORY_LIMIT=512M \
        -e MAX_EXECUTION_TIME=600 \
        -e TZ=Asia/Shanghai \
        -p 8081:80 \
        -v "$(pwd)/docker/phpmyadmin/config.user.inc.php:/etc/phpmyadmin/config.user.inc.php:ro" \
        --restart unless-stopped \
        phpmyadmin/phpmyadmin:latest
    
    # 等待容器启动
    log_info "等待phpMyAdmin启动..."
    sleep 10
    
    # 检查容器状态
    if docker ps --format "table {{.Names}}" | grep -q "jsherp-phpmyadmin"; then
        log_success "phpMyAdmin启动成功！"
        return 0
    else
        log_error "phpMyAdmin启动失败"
        docker logs jsherp-phpmyadmin
        exit 1
    fi
}

# 显示访问信息
show_access_info() {
    log_success "=== phpMyAdmin 访问信息 ==="
    echo ""
    echo "🌐 访问地址: http://localhost:8081"
    echo "👤 用户名: root"
    echo "🔑 密码: 123456"
    echo "🗄️  数据库: jsh_erp"
    echo ""
    log_info "数据库连接信息:"
    echo "   服务器: jsherp-mysql-dev"
    echo "   端口: 3306"
    echo "   用户: root / jsh_user"
    echo "   密码: 123456"
    echo ""
    log_warning "注意事项:"
    echo "   1. 请确保jshERP开发环境正在运行"
    echo "   2. 如果无法访问，请检查Docker容器状态"
    echo "   3. 执行SQL脚本时请选择正确的数据库"
    echo ""
}

# 检查phpMyAdmin状态
check_phpmyadmin_status() {
    log_info "检查phpMyAdmin状态..."
    
    # 检查容器是否运行
    if docker ps --format "table {{.Names}}" | grep -q "jsherp-phpmyadmin"; then
        log_success "phpMyAdmin容器正在运行"
        
        # 检查端口是否可访问
        if curl -s http://localhost:8081 > /dev/null; then
            log_success "phpMyAdmin Web界面可访问"
            return 0
        else
            log_warning "phpMyAdmin Web界面暂时无法访问，请稍等片刻"
            return 1
        fi
    else
        log_error "phpMyAdmin容器未运行"
        return 1
    fi
}

# 主函数
main() {
    echo "🐳 jshERP phpMyAdmin 启动工具"
    echo "================================"
    
    check_docker
    check_mysql_container
    check_network
    start_phpmyadmin
    
    # 等待服务完全启动
    log_info "等待服务完全启动..."
    sleep 15
    
    # 检查状态并显示访问信息
    if check_phpmyadmin_status; then
        show_access_info
        
        # 自动打开浏览器（可选）
        if command -v open > /dev/null; then
            log_info "正在打开浏览器..."
            open http://localhost:8081
        fi
    else
        log_error "phpMyAdmin启动可能有问题，请检查日志"
        echo "查看日志命令: docker logs jsherp-phpmyadmin"
    fi
}

# 脚本入口
main "$@"
