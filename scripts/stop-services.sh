#!/bin/bash

# jshERP 服务停止脚本
# 用于安全停止所有jshERP相关服务

set -e

# 颜色定义
RED='\033[0;31m'
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

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 停止单个容器
stop_container() {
    local container_name=$1
    local service_name=$2
    
    if docker ps --format "table {{.Names}}" | grep -q "^${container_name}$"; then
        log_info "停止 $service_name..."
        docker stop "$container_name" >/dev/null 2>&1
        log_success "$service_name 已停止"
    else
        log_warning "$service_name 未运行"
    fi
}

# 停止所有服务
stop_all_services() {
    log_info "开始停止jshERP所有服务..."
    
    # 按逆序停止服务（与启动顺序相反）
    stop_container "jsherp-phpmyadmin" "phpMyAdmin"
    stop_container "jsherp-nginx-dev" "Nginx代理"
    stop_container "jsherp-frontend-dev" "前端服务"
    stop_container "jsherp-backend-dev" "后端服务"
    stop_container "jsherp-redis-dev" "Redis缓存"
    stop_container "jsherp-mysql-dev" "MySQL数据库"
    
    log_success "所有服务已停止"
}

# 检查停止状态
check_stop_status() {
    log_info "检查停止状态..."
    
    containers=("jsherp-mysql-dev" "jsherp-redis-dev" "jsherp-backend-dev" "jsherp-frontend-dev" "jsherp-nginx-dev" "jsherp-phpmyadmin")
    
    all_stopped=true
    for container in "${containers[@]}"; do
        if docker ps --format "table {{.Names}}" | grep -q "^${container}$"; then
            log_warning "$container: 仍在运行"
            all_stopped=false
        else
            log_success "$container: 已停止"
        fi
    done
    
    if $all_stopped; then
        log_success "所有容器已成功停止"
    else
        log_warning "部分容器仍在运行"
    fi
}

# 强制停止（可选）
force_stop() {
    log_warning "执行强制停止..."
    
    containers=("jsherp-phpmyadmin" "jsherp-nginx-dev" "jsherp-frontend-dev" "jsherp-backend-dev" "jsherp-redis-dev" "jsherp-mysql-dev")
    
    for container in "${containers[@]}"; do
        if docker ps -a --format "table {{.Names}}" | grep -q "^${container}$"; then
            log_info "强制停止 $container..."
            docker kill "$container" >/dev/null 2>&1 || true
        fi
    done
    
    log_success "强制停止完成"
}

# 清理容器（可选）
cleanup_containers() {
    log_warning "清理停止的容器..."
    
    containers=("jsherp-phpmyadmin" "jsherp-nginx-dev" "jsherp-frontend-dev" "jsherp-backend-dev" "jsherp-redis-dev" "jsherp-mysql-dev")
    
    for container in "${containers[@]}"; do
        if docker ps -a --format "table {{.Names}}" | grep -q "^${container}$"; then
            if ! docker ps --format "table {{.Names}}" | grep -q "^${container}$"; then
                log_info "删除容器 $container..."
                docker rm "$container" >/dev/null 2>&1 || true
            fi
        fi
    done
    
    log_success "容器清理完成"
}

# 显示帮助信息
show_help() {
    echo "jshERP 服务停止脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示帮助信息"
    echo "  -f, --force    强制停止所有容器"
    echo "  -c, --cleanup  停止后清理容器"
    echo "  -a, --all      停止并清理所有容器"
    echo ""
    echo "示例:"
    echo "  $0              # 正常停止所有服务"
    echo "  $0 --force      # 强制停止所有服务"
    echo "  $0 --cleanup    # 停止并清理容器"
    echo "  $0 --all        # 强制停止并清理所有容器"
}

# 主函数
main() {
    local force_mode=false
    local cleanup_mode=false
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -f|--force)
                force_mode=true
                shift
                ;;
            -c|--cleanup)
                cleanup_mode=true
                shift
                ;;
            -a|--all)
                force_mode=true
                cleanup_mode=true
                shift
                ;;
            *)
                log_error "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    # 检查Docker状态
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker未运行"
        exit 1
    fi
    
    # 执行停止操作
    if $force_mode; then
        force_stop
    else
        stop_all_services
    fi
    
    # 检查停止状态
    sleep 2
    check_stop_status
    
    # 清理容器
    if $cleanup_mode; then
        cleanup_containers
    fi
    
    log_success "操作完成"
}

# 如果直接执行此脚本
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
