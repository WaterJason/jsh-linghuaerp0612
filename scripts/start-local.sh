#!/bin/bash

# jshERP 本地开发环境启动脚本
# 启动基础设施服务（Docker）和应用服务（宿主机）

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

# 检查依赖
check_dependencies() {
    log_info "检查系统依赖..."
    
    local missing_deps=()
    
    if ! command -v java &> /dev/null; then
        missing_deps+=("java")
    fi
    
    if ! command -v mvn &> /dev/null; then
        missing_deps+=("maven")
    fi
    
    if ! command -v node &> /dev/null; then
        missing_deps+=("node")
    fi
    
    if ! command -v yarn &> /dev/null; then
        missing_deps+=("yarn")
    fi
    
    if ! command -v docker &> /dev/null; then
        missing_deps+=("docker")
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        missing_deps+=("docker-compose")
    fi
    
    if [ ${#missing_deps[@]} -ne 0 ]; then
        log_error "缺少以下依赖: ${missing_deps[*]}"
        log_info "请运行 ./scripts/install-dependencies.sh 安装依赖"
        exit 1
    fi
    
    log_success "所有依赖已安装"
}

# 启动基础设施服务
start_infrastructure() {
    log_info "启动基础设施服务（MySQL、Redis、Nginx）..."
    
    cd "$PROJECT_ROOT"
    
    # 检查是否已有运行的服务
    if docker-compose -f docker-compose.infrastructure.yml ps | grep -q "Up"; then
        log_warning "检测到已运行的基础设施服务"
        read -p "是否重启服务？(y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker-compose -f docker-compose.infrastructure.yml down
        else
            log_info "跳过基础设施服务启动"
            return
        fi
    fi
    
    # 启动服务
    docker-compose -f docker-compose.infrastructure.yml up -d
    
    # 等待服务就绪
    log_info "等待基础设施服务启动..."
    sleep 10
    
    # 检查服务状态
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if docker-compose -f docker-compose.infrastructure.yml ps | grep -q "jsherp-mysql.*Up.*healthy"; then
            log_success "MySQL 服务已就绪"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "MySQL 服务启动超时"
            exit 1
        fi
        
        log_info "等待 MySQL 服务启动... ($attempt/$max_attempts)"
        sleep 5
        ((attempt++))
    done
    
    # 检查 Redis
    attempt=1
    while [ $attempt -le $max_attempts ]; do
        if docker-compose -f docker-compose.infrastructure.yml ps | grep -q "jsherp-redis.*Up.*healthy"; then
            log_success "Redis 服务已就绪"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "Redis 服务启动超时"
            exit 1
        fi
        
        log_info "等待 Redis 服务启动... ($attempt/$max_attempts)"
        sleep 3
        ((attempt++))
    done
    
    log_success "基础设施服务启动完成"
}

# 安装前端依赖
install_frontend_deps() {
    log_info "安装前端依赖..."
    
    cd "$PROJECT_ROOT/jshERP-web"
    
    if [ ! -d "node_modules" ] || [ ! -f "yarn.lock" ]; then
        log_info "首次安装，这可能需要几分钟..."
        yarn install
    else
        log_info "更新依赖..."
        yarn install --check-files
    fi
    
    log_success "前端依赖安装完成"
}

# 启动后端服务
start_backend() {
    log_info "启动后端服务..."
    
    cd "$PROJECT_ROOT/jshERP-boot"
    
    # 设置环境变量
    export SPRING_PROFILES_ACTIVE=local
    
    # 检查是否存在正在运行的后端进程
    if pgrep -f "jshERP" > /dev/null; then
        log_warning "检测到已运行的后端进程"
        read -p "是否终止现有进程？(y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            pkill -f "jshERP" || true
            sleep 3
        fi
    fi
    
    # 启动后端服务
    log_info "启动 SpringBoot 应用..."
    nohup mvn spring-boot:run -Dspring-boot.run.profiles=local > ../logs/backend.log 2>&1 &
    BACKEND_PID=$!
    
    # 保存 PID
    echo $BACKEND_PID > ../logs/backend.pid
    
    log_info "后端服务 PID: $BACKEND_PID"
    log_info "后端日志文件: $PROJECT_ROOT/logs/backend.log"
    
    # 等待后端服务启动
    log_info "等待后端服务启动..."
    local max_attempts=60
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:9999/jshERP-boot/health > /dev/null 2>&1; then
            log_success "后端服务已就绪 (http://localhost:9999)"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "后端服务启动超时"
            log_info "请检查日志: tail -f $PROJECT_ROOT/logs/backend.log"
            exit 1
        fi
        
        log_info "等待后端服务启动... ($attempt/$max_attempts)"
        sleep 5
        ((attempt++))
    done
}

# 启动前端服务
start_frontend() {
    log_info "启动前端服务..."
    
    cd "$PROJECT_ROOT/jshERP-web"
    
    # 检查是否存在正在运行的前端进程
    if lsof -ti:3000 > /dev/null 2>&1; then
        log_warning "端口 3000 已被占用"
        read -p "是否终止占用进程？(y/N) " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            kill $(lsof -ti:3000) || true
            sleep 3
        fi
    fi
    
    # 启动前端服务
    log_info "启动 Vue.js 开发服务器..."
    nohup yarn serve > ../logs/frontend.log 2>&1 &
    FRONTEND_PID=$!
    
    # 保存 PID
    echo $FRONTEND_PID > ../logs/frontend.pid
    
    log_info "前端服务 PID: $FRONTEND_PID"
    log_info "前端日志文件: $PROJECT_ROOT/logs/frontend.log"
    
    # 等待前端服务启动
    log_info "等待前端服务启动..."
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s http://localhost:3000 > /dev/null 2>&1; then
            log_success "前端服务已就绪 (http://localhost:3000)"
            break
        fi
        
        if [ $attempt -eq $max_attempts ]; then
            log_error "前端服务启动超时"
            log_info "请检查日志: tail -f $PROJECT_ROOT/logs/frontend.log"
            exit 1
        fi
        
        log_info "等待前端服务启动... ($attempt/$max_attempts)"
        sleep 3
        ((attempt++))
    done
}

# 显示服务信息
show_service_info() {
    echo ""
    echo "=========================================="
    echo "  🎉 jshERP 本地开发环境启动完成!"
    echo "=========================================="
    echo ""
    echo "服务访问地址:"
    echo "  📱 前端应用:     http://localhost:3000"
    echo "  🔧 后端 API:     http://localhost:9999/jshERP-boot"
    echo "  🌐 Nginx 代理:   http://localhost:8000"
    echo "  📊 API 文档:     http://localhost:9999/jshERP-boot/doc.html"
    echo ""
    echo "数据库连接信息:"
    echo "  🗄️  MySQL:       localhost:3306/jsh_erp"
    echo "  📦 Redis:        localhost:6379"
    echo ""
    echo "日志文件:"
    echo "  📋 后端日志:     $PROJECT_ROOT/logs/backend.log"
    echo "  📋 前端日志:     $PROJECT_ROOT/logs/frontend.log"
    echo ""
    echo "管理命令:"
    echo "  ./scripts/stop-local.sh      # 停止所有服务"
    echo "  ./scripts/status-local.sh    # 查看服务状态"
    echo "  ./scripts/logs-local.sh      # 查看服务日志"
    echo ""
}

# 创建日志目录
create_log_dir() {
    mkdir -p "$PROJECT_ROOT/logs"
}

# 主执行流程
main() {
    echo "=========================================="
    echo "  🚀 启动 jshERP 本地开发环境"
    echo "=========================================="
    echo ""
    
    cd "$PROJECT_ROOT"
    
    # 创建日志目录
    create_log_dir
    
    # 检查依赖
    check_dependencies
    
    # 启动基础设施
    start_infrastructure
    
    # 安装前端依赖
    install_frontend_deps
    
    # 启动后端服务
    start_backend
    
    # 启动前端服务
    start_frontend
    
    # 显示服务信息
    show_service_info
}

# 捕获退出信号
trap 'log_error "启动过程被中断"; exit 1' INT TERM

# 执行主函数
main "$@" 