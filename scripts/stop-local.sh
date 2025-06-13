#!/bin/bash

# jshERP 本地开发环境停止脚本
# 停止宿主机应用服务和Docker基础设施服务

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

# 停止前端服务
stop_frontend() {
    log_info "停止前端服务..."
    
    local frontend_pid_file="$PROJECT_ROOT/logs/frontend.pid"
    
    # 从 PID 文件停止
    if [ -f "$frontend_pid_file" ]; then
        local frontend_pid=$(cat "$frontend_pid_file")
        if ps -p "$frontend_pid" > /dev/null 2>&1; then
            log_info "停止前端进程 PID: $frontend_pid"
            kill "$frontend_pid" 2>/dev/null || true
            sleep 3
            
            # 强制终止如果还在运行
            if ps -p "$frontend_pid" > /dev/null 2>&1; then
                log_warning "强制终止前端进程"
                kill -9 "$frontend_pid" 2>/dev/null || true
            fi
        fi
        rm -f "$frontend_pid_file"
    fi
    
    # 停止占用 3000 端口的进程
    if lsof -ti:3000 > /dev/null 2>&1; then
        log_info "停止占用端口 3000 的进程"
        kill $(lsof -ti:3000) 2>/dev/null || true
        sleep 2
    fi
    
    log_success "前端服务已停止"
}

# 停止后端服务
stop_backend() {
    log_info "停止后端服务..."
    
    local backend_pid_file="$PROJECT_ROOT/logs/backend.pid"
    
    # 从 PID 文件停止
    if [ -f "$backend_pid_file" ]; then
        local backend_pid=$(cat "$backend_pid_file")
        if ps -p "$backend_pid" > /dev/null 2>&1; then
            log_info "停止后端进程 PID: $backend_pid"
            kill "$backend_pid" 2>/dev/null || true
            sleep 5
            
            # 强制终止如果还在运行
            if ps -p "$backend_pid" > /dev/null 2>&1; then
                log_warning "强制终止后端进程"
                kill -9 "$backend_pid" 2>/dev/null || true
            fi
        fi
        rm -f "$backend_pid_file"
    fi
    
    # 停止所有 jshERP 相关进程
    if pgrep -f "jshERP" > /dev/null; then
        log_info "停止所有 jshERP 相关进程"
        pkill -f "jshERP" 2>/dev/null || true
        sleep 3
    fi
    
    # 停止占用 9999 端口的进程
    if lsof -ti:9999 > /dev/null 2>&1; then
        log_info "停止占用端口 9999 的进程"
        kill $(lsof -ti:9999) 2>/dev/null || true
        sleep 2
    fi
    
    log_success "后端服务已停止"
}

# 停止基础设施服务
stop_infrastructure() {
    log_info "停止基础设施服务..."
    
    cd "$PROJECT_ROOT"
    
    # 检查是否有运行的基础设施服务
    if docker-compose -f docker-compose.infrastructure.yml ps | grep -q "Up"; then
        log_info "停止 Docker 基础设施服务..."
        docker-compose -f docker-compose.infrastructure.yml down
        log_success "基础设施服务已停止"
    else
        log_info "没有运行中的基础设施服务"
    fi
}

# 清理日志文件
cleanup_logs() {
    log_info "清理日志文件..."
    
    local logs_dir="$PROJECT_ROOT/logs"
    
    if [ -d "$logs_dir" ]; then
        # 备份当前日志
        local backup_dir="$logs_dir/backup_$(date +%Y%m%d_%H%M%S)"
        
        if [ -f "$logs_dir/backend.log" ] || [ -f "$logs_dir/frontend.log" ]; then
            mkdir -p "$backup_dir"
            
            if [ -f "$logs_dir/backend.log" ]; then
                mv "$logs_dir/backend.log" "$backup_dir/"
            fi
            
            if [ -f "$logs_dir/frontend.log" ]; then
                mv "$logs_dir/frontend.log" "$backup_dir/"
            fi
            
            log_info "日志已备份到: $backup_dir"
        fi
        
        # 清理 PID 文件
        rm -f "$logs_dir"/*.pid
    fi
    
    log_success "日志清理完成"
}

# 显示停止状态
show_stop_status() {
    echo ""
    echo "=========================================="
    echo "  🛑 jshERP 本地开发环境已停止"
    echo "=========================================="
    echo ""
    
    # 检查进程状态
    local frontend_running=false
    local backend_running=false
    local docker_running=false
    
    if lsof -ti:3000 > /dev/null 2>&1; then
        frontend_running=true
    fi
    
    if lsof -ti:9999 > /dev/null 2>&1 || pgrep -f "jshERP" > /dev/null; then
        backend_running=true
    fi
    
    if docker-compose -f docker-compose.infrastructure.yml ps 2>/dev/null | grep -q "Up"; then
        docker_running=true
    fi
    
    echo "服务状态:"
    if [ "$frontend_running" = true ]; then
        echo "  📱 前端服务:     ❌ 仍在运行"
    else
        echo "  📱 前端服务:     ✅ 已停止"
    fi
    
    if [ "$backend_running" = true ]; then
        echo "  🔧 后端服务:     ❌ 仍在运行"
    else
        echo "  🔧 后端服务:     ✅ 已停止"
    fi
    
    if [ "$docker_running" = true ]; then
        echo "  🐳 基础设施:     ❌ 仍在运行"
    else
        echo "  🐳 基础设施:     ✅ 已停止"
    fi
    
    echo ""
    
    if [ "$frontend_running" = true ] || [ "$backend_running" = true ] || [ "$docker_running" = true ]; then
        log_warning "部分服务可能仍在运行，请手动检查"
        echo ""
        echo "手动检查命令:"
        echo "  lsof -i:3000     # 检查前端端口"
        echo "  lsof -i:9999     # 检查后端端口"
        echo "  docker ps        # 检查 Docker 容器"
    else
        log_success "所有服务已成功停止"
        echo ""
        echo "重新启动命令:"
        echo "  ./scripts/start-local.sh     # 启动本地开发环境"
    fi
    
    echo ""
}

# 主执行流程
main() {
    echo "=========================================="
    echo "  🛑 停止 jshERP 本地开发环境"
    echo "=========================================="
    echo ""
    
    cd "$PROJECT_ROOT"
    
    # 询问是否保留基础设施
    read -p "是否保留基础设施服务（MySQL、Redis）继续运行？(y/N) " -n 1 -r
    echo
    
    local keep_infrastructure=false
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        keep_infrastructure=true
        log_info "将保留基础设施服务运行"
    fi
    
    # 停止应用服务
    stop_frontend
    stop_backend
    
    # 停止基础设施（如果需要）
    if [ "$keep_infrastructure" = false ]; then
        stop_infrastructure
    else
        log_info "保留基础设施服务运行"
    fi
    
    # 清理日志
    cleanup_logs
    
    # 显示停止状态
    show_stop_status
}

# 捕获退出信号
trap 'log_error "停止过程被中断"; exit 1' INT TERM

# 执行主函数
main "$@" 