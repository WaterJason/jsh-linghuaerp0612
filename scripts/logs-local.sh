#!/bin/bash

# jshERP 本地开发环境日志查看脚本
# 查看前端、后端和基础设施服务日志

set -e

# 脚本目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 显示使用帮助
show_usage() {
    echo "使用方法: $0 [选项] [服务名]"
    echo ""
    echo "选项:"
    echo "  -f, --follow     实时跟踪日志（类似 tail -f）"
    echo "  -n, --lines N    显示最后 N 行日志（默认: 100）"
    echo "  -h, --help       显示此帮助信息"
    echo ""
    echo "服务名:"
    echo "  frontend         前端服务日志"
    echo "  backend          后端服务日志"
    echo "  mysql            MySQL 数据库日志"
    echo "  redis            Redis 缓存日志"
    echo "  nginx            Nginx 代理日志"
    echo "  all              所有服务日志（默认）"
    echo ""
    echo "示例:"
    echo "  $0                          # 显示所有服务的最新日志"
    echo "  $0 -f backend               # 实时跟踪后端日志"
    echo "  $0 -n 50 frontend           # 显示前端最后 50 行日志"
    echo "  $0 --follow all             # 实时跟踪所有服务日志"
}

# 默认参数
FOLLOW=false
LINES=100
SERVICE="all"

# 解析命令行参数
while [[ $# -gt 0 ]]; do
    case $1 in
        -f|--follow)
            FOLLOW=true
            shift
            ;;
        -n|--lines)
            LINES="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        frontend|backend|mysql|redis|nginx|all)
            SERVICE="$1"
            shift
            ;;
        *)
            echo "未知参数: $1"
            show_usage
            exit 1
            ;;
    esac
done

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

# 显示前端日志
show_frontend_logs() {
    local log_file="$PROJECT_ROOT/logs/frontend.log"
    
    echo -e "${BLUE}📱 前端服务日志${NC}"
    echo "================================"
    
    if [ -f "$log_file" ]; then
        if [ "$FOLLOW" = true ]; then
            log_info "实时跟踪前端日志 (Ctrl+C 退出)"
            tail -f "$log_file"
        else
            tail -n "$LINES" "$log_file"
        fi
    else
        log_warning "前端日志文件不存在: $log_file"
    fi
}

# 显示后端日志
show_backend_logs() {
    local log_file="$PROJECT_ROOT/logs/backend.log"
    
    echo -e "${BLUE}🔧 后端服务日志${NC}"
    echo "================================"
    
    if [ -f "$log_file" ]; then
        if [ "$FOLLOW" = true ]; then
            log_info "实时跟踪后端日志 (Ctrl+C 退出)"
            tail -f "$log_file"
        else
            tail -n "$LINES" "$log_file"
        fi
    else
        log_warning "后端日志文件不存在: $log_file"
    fi
}

# 显示 MySQL 日志
show_mysql_logs() {
    echo -e "${BLUE}🗄️  MySQL 数据库日志${NC}"
    echo "================================"
    
    if docker ps | grep -q "jsherp-mysql-local"; then
        if [ "$FOLLOW" = true ]; then
            log_info "实时跟踪 MySQL 日志 (Ctrl+C 退出)"
            docker logs -f jsherp-mysql-local
        else
            docker logs --tail "$LINES" jsherp-mysql-local
        fi
    else
        log_warning "MySQL 容器未运行"
    fi
}

# 显示 Redis 日志
show_redis_logs() {
    echo -e "${BLUE}📦 Redis 缓存日志${NC}"
    echo "================================"
    
    if docker ps | grep -q "jsherp-redis-local"; then
        if [ "$FOLLOW" = true ]; then
            log_info "实时跟踪 Redis 日志 (Ctrl+C 退出)"
            docker logs -f jsherp-redis-local
        else
            docker logs --tail "$LINES" jsherp-redis-local
        fi
    else
        log_warning "Redis 容器未运行"
    fi
}

# 显示 Nginx 日志
show_nginx_logs() {
    echo -e "${BLUE}🌐 Nginx 代理日志${NC}"
    echo "================================"
    
    if docker ps | grep -q "jsherp-nginx-local"; then
        if [ "$FOLLOW" = true ]; then
            log_info "实时跟踪 Nginx 日志 (Ctrl+C 退出)"
            docker logs -f jsherp-nginx-local
        else
            docker logs --tail "$LINES" jsherp-nginx-local
        fi
    else
        log_warning "Nginx 容器未运行"
    fi
}

# 显示所有服务日志
show_all_logs() {
    if [ "$FOLLOW" = true ]; then
        log_info "实时跟踪所有服务日志..."
        log_warning "注意：将同时显示多个服务的日志，可能较难阅读"
        sleep 2
        
        # 使用 multitail 或分别处理
        echo "=== 开始多服务日志跟踪 ==="
        
        # 创建临时脚本来并行跟踪日志
        local temp_script="/tmp/jsherp_logs_$$.sh"
        cat > "$temp_script" << 'EOF'
#!/bin/bash
trap 'kill $(jobs -p) 2>/dev/null' EXIT

# 前端日志
if [ -f "$1/logs/frontend.log" ]; then
    tail -f "$1/logs/frontend.log" | sed 's/^/[FRONTEND] /' &
fi

# 后端日志
if [ -f "$1/logs/backend.log" ]; then
    tail -f "$1/logs/backend.log" | sed 's/^/[BACKEND] /' &
fi

# Docker 日志
if docker ps | grep -q "jsherp-mysql-local"; then
    docker logs -f jsherp-mysql-local 2>&1 | sed 's/^/[MYSQL] /' &
fi

if docker ps | grep -q "jsherp-redis-local"; then
    docker logs -f jsherp-redis-local 2>&1 | sed 's/^/[REDIS] /' &
fi

if docker ps | grep -q "jsherp-nginx-local"; then
    docker logs -f jsherp-nginx-local 2>&1 | sed 's/^/[NGINX] /' &
fi

wait
EOF
        chmod +x "$temp_script"
        "$temp_script" "$PROJECT_ROOT"
        rm -f "$temp_script"
    else
        echo "=== 所有服务日志概览 ==="
        echo ""
        
        show_frontend_logs
        echo ""
        show_backend_logs
        echo ""
        show_mysql_logs
        echo ""
        show_redis_logs
        echo ""
        show_nginx_logs
    fi
}

# 主执行流程
main() {
    echo "=========================================="
    echo "  📋 jshERP 本地开发环境日志查看"
    echo "=========================================="
    echo ""
    
    cd "$PROJECT_ROOT"
    
    case "$SERVICE" in
        frontend)
            show_frontend_logs
            ;;
        backend)
            show_backend_logs
            ;;
        mysql)
            show_mysql_logs
            ;;
        redis)
            show_redis_logs
            ;;
        nginx)
            show_nginx_logs
            ;;
        all)
            show_all_logs
            ;;
        *)
            log_error "未知服务: $SERVICE"
            show_usage
            exit 1
            ;;
    esac
}

# 捕获退出信号
trap 'echo ""; log_info "日志查看已退出"; exit 0' INT TERM

# 执行主函数
main "$@" 