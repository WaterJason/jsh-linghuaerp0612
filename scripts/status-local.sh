#!/bin/bash

# jshERP 本地开发环境状态检查脚本
# 检查所有服务的运行状态

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

# 检查前端服务状态
check_frontend_status() {
    local status="❌ 未运行"
    local details=""
    
    # 检查端口占用
    if lsof -ti:3000 > /dev/null 2>&1; then
        status="✅ 运行中"
        local pid=$(lsof -ti:3000)
        details="PID: $pid"
        
        # 检查HTTP响应
        if curl -s http://localhost:3000 > /dev/null 2>&1; then
            details="$details, 响应正常"
        else
            details="$details, 响应异常"
            status="⚠️  运行但无响应"
        fi
    fi
    
    echo -e "  📱 前端服务 (Port 3000):  $status"
    if [ -n "$details" ]; then
        echo "     详情: $details"
    fi
    
    # 检查 PID 文件
    local pid_file="$PROJECT_ROOT/logs/frontend.pid"
    if [ -f "$pid_file" ]; then
        local recorded_pid=$(cat "$pid_file")
        if ps -p "$recorded_pid" > /dev/null 2>&1; then
            echo "     记录的 PID: $recorded_pid (运行中)"
        else
            echo "     记录的 PID: $recorded_pid (已停止)"
        fi
    fi
}

# 检查后端服务状态
check_backend_status() {
    local status="❌ 未运行"
    local details=""
    
    # 检查端口占用
    if lsof -ti:9999 > /dev/null 2>&1; then
        status="✅ 运行中"
        local pid=$(lsof -ti:9999)
        details="PID: $pid"
        
        # 检查健康端点
        if curl -s http://localhost:9999/jshERP-boot/actuator/health > /dev/null 2>&1; then
            details="$details, 健康检查通过"
        elif curl -s http://localhost:9999/jshERP-boot > /dev/null 2>&1; then
            details="$details, 应用已启动"
        else
            details="$details, 启动中或异常"
            status="⚠️  运行但无响应"
        fi
    fi
    
    echo -e "  🔧 后端服务 (Port 9999):  $status"
    if [ -n "$details" ]; then
        echo "     详情: $details"
    fi
    
    # 检查 PID 文件
    local pid_file="$PROJECT_ROOT/logs/backend.pid"
    if [ -f "$pid_file" ]; then
        local recorded_pid=$(cat "$pid_file")
        if ps -p "$recorded_pid" > /dev/null 2>&1; then
            echo "     记录的 PID: $recorded_pid (运行中)"
        else
            echo "     记录的 PID: $recorded_pid (已停止)"
        fi
    fi
    
    # 检查 Maven 进程
    if pgrep -f "maven.*jshERP" > /dev/null 2>&1; then
        local maven_pid=$(pgrep -f "maven.*jshERP")
        echo "     Maven 进程: $maven_pid"
    fi
}

# 检查MySQL服务状态
check_mysql_status() {
    local status="❌ 未运行"
    local details=""
    
    if docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "jsherp-mysql.*Up"; then
        status="✅ 运行中"
        
        # 检查健康状态
        local health=$(docker inspect jsherp-mysql-local --format='{{.State.Health.Status}}' 2>/dev/null || echo "unknown")
        if [ "$health" = "healthy" ]; then
            details="健康状态: 正常"
        else
            details="健康状态: $health"
        fi
        
        # 检查连接
        if docker exec jsherp-mysql-local mysqladmin ping -h localhost --silent 2>/dev/null; then
            details="$details, 连接正常"
        else
            details="$details, 连接异常"
        fi
    fi
    
    echo -e "  🗄️  MySQL (Port 3306):     $status"
    if [ -n "$details" ]; then
        echo "     详情: $details"
    fi
}

# 检查Redis服务状态
check_redis_status() {
    local status="❌ 未运行"
    local details=""
    
    if docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "jsherp-redis.*Up"; then
        status="✅ 运行中"
        
        # 检查健康状态
        local health=$(docker inspect jsherp-redis-local --format='{{.State.Health.Status}}' 2>/dev/null || echo "unknown")
        if [ "$health" = "healthy" ]; then
            details="健康状态: 正常"
        else
            details="健康状态: $health"
        fi
        
        # 检查连接
        if docker exec jsherp-redis-local redis-cli ping 2>/dev/null | grep -q "PONG"; then
            details="$details, 连接正常"
        else
            details="$details, 连接异常"
        fi
    fi
    
    echo -e "  📦 Redis (Port 6379):      $status"
    if [ -n "$details" ]; then
        echo "     详情: $details"
    fi
}

# 检查Nginx代理状态
check_nginx_status() {
    local status="❌ 未运行"
    local details=""
    
    if docker ps --format "table {{.Names}}\t{{.Status}}" | grep -q "jsherp-nginx.*Up"; then
        status="✅ 运行中"
        
        # 检查HTTP响应
        if curl -s http://localhost:8000/health > /dev/null 2>&1; then
            details="代理正常"
        else
            details="代理异常"
            status="⚠️  运行但无响应"
        fi
    fi
    
    echo -e "  🌐 Nginx (Port 8000):      $status"
    if [ -n "$details" ]; then
        echo "     详情: $details"
    fi
}

# 检查系统资源
check_system_resources() {
    echo ""
    echo "📊 系统资源使用情况:"
    
    # CPU 使用率
    if command -v top &> /dev/null; then
        local cpu_usage=$(top -l 1 -n 0 | grep "CPU usage" | awk '{print $3}' | sed 's/%//' 2>/dev/null || echo "N/A")
        echo "  💾 CPU 使用率: ${cpu_usage}%"
    fi
    
    # 内存使用
    if command -v vm_stat &> /dev/null; then
        local memory_info=$(vm_stat | head -4 | tail -3)
        echo "  🧠 内存信息:"
        echo "$memory_info" | sed 's/^/     /'
    fi
    
    # 磁盘使用
    local disk_usage=$(df -h . | tail -1 | awk '{print $5}' 2>/dev/null || echo "N/A")
    echo "  💽 磁盘使用率: $disk_usage"
    
    # Docker 资源使用
    if command -v docker &> /dev/null && docker info &> /dev/null; then
        echo "  🐳 Docker 容器:"
        docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}" | grep jsherp || echo "     无 jshERP 相关容器"
    fi
}

# 检查网络连通性
check_network_connectivity() {
    echo ""
    echo "🌐 网络连通性检查:"
    
    # 前端到后端
    if curl -s http://localhost:9999/jshERP-boot > /dev/null 2>&1; then
        echo "  ✅ 前端 → 后端: 正常"
    else
        echo "  ❌ 前端 → 后端: 异常"
    fi
    
    # 后端到数据库
    if docker exec jsherp-mysql-local mysqladmin ping -h localhost --silent 2>/dev/null; then
        echo "  ✅ 后端 → MySQL: 正常"
    else
        echo "  ❌ 后端 → MySQL: 异常"
    fi
    
    # 后端到Redis
    if docker exec jsherp-redis-local redis-cli ping 2>/dev/null | grep -q "PONG"; then
        echo "  ✅ 后端 → Redis: 正常"
    else
        echo "  ❌ 后端 → Redis: 异常"
    fi
    
    # Nginx代理
    if curl -s http://localhost:8000 > /dev/null 2>&1; then
        echo "  ✅ Nginx 代理: 正常"
    else
        echo "  ❌ Nginx 代理: 异常"
    fi
}

# 显示日志文件信息
show_log_info() {
    echo ""
    echo "📋 日志文件信息:"
    
    local logs_dir="$PROJECT_ROOT/logs"
    
    if [ -d "$logs_dir" ]; then
        if [ -f "$logs_dir/backend.log" ]; then
            local backend_size=$(ls -lh "$logs_dir/backend.log" | awk '{print $5}')
            local backend_lines=$(wc -l < "$logs_dir/backend.log" 2>/dev/null || echo "0")
            echo "  📄 后端日志: $backend_size ($backend_lines 行)"
            echo "     路径: $logs_dir/backend.log"
        else
            echo "  📄 后端日志: 不存在"
        fi
        
        if [ -f "$logs_dir/frontend.log" ]; then
            local frontend_size=$(ls -lh "$logs_dir/frontend.log" | awk '{print $5}')
            local frontend_lines=$(wc -l < "$logs_dir/frontend.log" 2>/dev/null || echo "0")
            echo "  📄 前端日志: $frontend_size ($frontend_lines 行)"
            echo "     路径: $logs_dir/frontend.log"
        else
            echo "  📄 前端日志: 不存在"
        fi
    else
        echo "  📁 日志目录不存在"
    fi
}

# 显示端口占用情况
show_port_usage() {
    echo ""
    echo "🔌 端口占用情况:"
    
    local ports=(3000 8000 9999 3306 6379)
    
    for port in "${ports[@]}"; do
        if lsof -ti:$port > /dev/null 2>&1; then
            local pid=$(lsof -ti:$port)
            local process=$(ps -p $pid -o comm= 2>/dev/null || echo "unknown")
            echo "  🟢 $port: 占用中 (PID: $pid, 进程: $process)"
        else
            echo "  🔴 $port: 空闲"
        fi
    done
}

# 主执行流程
main() {
    echo "=========================================="
    echo "  📊 jshERP 本地开发环境状态检查"
    echo "=========================================="
    echo ""
    echo "🚀 应用服务状态:"
    
    # 检查应用服务
    check_frontend_status
    check_backend_status
    
    echo ""
    echo "🏗️  基础设施服务状态:"
    
    # 检查基础设施服务
    check_mysql_status
    check_redis_status
    check_nginx_status
    
    # 系统资源检查
    check_system_resources
    
    # 网络连通性检查
    check_network_connectivity
    
    # 日志文件信息
    show_log_info
    
    # 端口占用情况
    show_port_usage
    
    echo ""
    echo "=========================================="
    echo "  📋 管理命令"
    echo "=========================================="
    echo ""
    echo "  ./scripts/start-local.sh     # 启动本地开发环境"
    echo "  ./scripts/stop-local.sh      # 停止本地开发环境"
    echo "  ./scripts/logs-local.sh      # 查看服务日志"
    echo ""
    echo "  tail -f logs/backend.log     # 实时查看后端日志"
    echo "  tail -f logs/frontend.log    # 实时查看前端日志"
    echo ""
}

# 执行主函数
main "$@" 