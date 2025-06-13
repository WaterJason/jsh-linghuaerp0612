#!/bin/bash

# jshERP 系统完整恢复脚本
# 版本: v1.0
# 作者: jshERP 部署团队
# 日期: 2025-06-13

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] ✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ❌ $1${NC}"
}

# 显示使用说明
show_usage() {
    echo "使用方法:"
    echo "  $0 <备份文件路径> [恢复目录]"
    echo ""
    echo "参数说明:"
    echo "  备份文件路径    - 完整备份的 tar.gz 文件路径"
    echo "  恢复目录        - 可选，指定恢复到的目录，默认为 /Users/macmini/Desktop/jshERP-0612-Cursor-restored"
    echo ""
    echo "示例:"
    echo "  $0 /backup/jshERP/jshERP_complete_backup_20250613_143022.tar.gz"
    echo "  $0 /backup/jshERP/jshERP_complete_backup_20250613_143022.tar.gz /opt/jshERP"
}

# 检查参数
check_parameters() {
    if [ $# -lt 1 ]; then
        log_error "缺少必要参数"
        show_usage
        exit 1
    fi
    
    BACKUP_FILE="$1"
    RESTORE_DIR="${2:-/Users/macmini/Desktop/jshERP-0612-Cursor-restored}"
    
    if [ ! -f "$BACKUP_FILE" ]; then
        log_error "备份文件不存在: $BACKUP_FILE"
        exit 1
    fi
    
    log "备份文件: $BACKUP_FILE"
    log "恢复目录: $RESTORE_DIR"
}

# 检查前置条件
check_prerequisites() {
    log "检查前置条件..."
    
    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装"
        exit 1
    fi
    
    # 检查 Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose 未安装"
        exit 1
    fi
    
    # 检查磁盘空间
    AVAILABLE_SPACE=$(df -BG . | awk 'NR==2 {print $4}' | sed 's/G//')
    if [ "$AVAILABLE_SPACE" -lt 10 ]; then
        log_warning "可用磁盘空间不足 10GB，可能影响恢复过程"
    fi
    
    log_success "前置条件检查完成"
}

# 解压备份文件
extract_backup() {
    log "解压备份文件..."
    
    # 创建临时目录
    TEMP_DIR=$(mktemp -d)
    
    # 解压备份文件
    tar -xzf "$BACKUP_FILE" -C "$TEMP_DIR"
    
    # 查找备份目录
    BACKUP_EXTRACT_DIR=$(find "$TEMP_DIR" -maxdepth 1 -type d -name "20*" | head -1)
    
    if [ -z "$BACKUP_EXTRACT_DIR" ]; then
        log_error "无法找到备份目录"
        exit 1
    fi
    
    log_success "备份文件解压完成: $BACKUP_EXTRACT_DIR"
}

# 验证备份完整性
verify_backup_integrity() {
    log "验证备份完整性..."
    
    # 检查校验文件
    if [ -f "$BACKUP_EXTRACT_DIR/backup_checksums.md5" ]; then
        cd "$BACKUP_EXTRACT_DIR"
        if md5sum -c backup_checksums.md5 >/dev/null 2>&1; then
            log_success "备份文件校验通过"
        else
            log_error "备份文件校验失败"
            exit 1
        fi
    else
        log_warning "未找到校验文件，跳过完整性验证"
    fi
    
    # 检查关键文件
    local missing_files=0
    
    if [ ! -f "$BACKUP_EXTRACT_DIR/source_code/jshERP_source_"*.tar.gz ]; then
        log_error "源代码备份文件缺失"
        ((missing_files++))
    fi
    
    if [ ! -f "$BACKUP_EXTRACT_DIR/database/jsh_erp_full_"*.sql ]; then
        log_error "数据库备份文件缺失"
        ((missing_files++))
    fi
    
    if [ $missing_files -gt 0 ]; then
        log_error "发现 $missing_files 个关键文件缺失"
        exit 1
    fi
    
    log_success "备份完整性验证通过"
}

# 恢复源代码
restore_source_code() {
    log "恢复源代码..."
    
    # 创建恢复目录
    mkdir -p "$RESTORE_DIR"
    
    # 解压源代码
    SOURCE_ARCHIVE=$(find "$BACKUP_EXTRACT_DIR/source_code" -name "jshERP_source_*.tar.gz" | head -1)
    if [ -n "$SOURCE_ARCHIVE" ]; then
        tar -xzf "$SOURCE_ARCHIVE" -C "$RESTORE_DIR"
        log_success "源代码恢复完成"
    else
        log_error "未找到源代码备份文件"
        exit 1
    fi
    
    # 恢复配置文件
    if [ -d "$BACKUP_EXTRACT_DIR/source_code/config" ]; then
        cp -r "$BACKUP_EXTRACT_DIR/source_code/config/"* "$RESTORE_DIR/jshERP-boot/src/main/resources/"
        log_success "配置文件恢复完成"
    fi
    
    # 恢复修改过的文件
    if [ -d "$BACKUP_EXTRACT_DIR/source_code/modified_files" ]; then
        # 恢复 MyBatis 配置
        if [ -f "$BACKUP_EXTRACT_DIR/source_code/modified_files/application-docker.properties" ]; then
            cp "$BACKUP_EXTRACT_DIR/source_code/modified_files/application-docker.properties" \
               "$RESTORE_DIR/jshERP-boot/src/main/resources/"
        fi
        
        # 恢复验证码修改
        if [ -f "$BACKUP_EXTRACT_DIR/source_code/modified_files/UserController.java" ]; then
            cp "$BACKUP_EXTRACT_DIR/source_code/modified_files/UserController.java" \
               "$RESTORE_DIR/jshERP-boot/src/main/java/com/jsh/erp/controller/"
        fi
        
        log_success "修改文件恢复完成"
    fi
}

# 恢复 Docker 镜像
restore_docker_images() {
    log "恢复 Docker 镜像..."
    
    # 查找镜像备份文件
    IMAGE_FILES=$(find "$BACKUP_EXTRACT_DIR/docker" -name "*.tar.gz" -not -name "*source*")
    
    if [ -z "$IMAGE_FILES" ]; then
        log_warning "未找到 Docker 镜像备份文件"
        return
    fi
    
    # 恢复每个镜像
    for image_file in $IMAGE_FILES; do
        log "恢复镜像: $(basename "$image_file")"
        gunzip -c "$image_file" | docker load
    done
    
    log_success "Docker 镜像恢复完成"
}

# 启动基础服务
start_basic_services() {
    log "启动基础服务..."
    
    cd "$RESTORE_DIR"
    
    # 启动 MySQL 和 Redis
    docker-compose -f docker-compose.dev.yml up -d jsherp-mysql-dev jsherp-redis-dev
    
    # 等待服务启动
    log "等待数据库服务启动..."
    sleep 30
    
    # 检查服务状态
    if docker ps | grep -q "jsherp-mysql-dev"; then
        log_success "MySQL 服务启动成功"
    else
        log_error "MySQL 服务启动失败"
        exit 1
    fi
    
    if docker ps | grep -q "jsherp-redis-dev"; then
        log_success "Redis 服务启动成功"
    else
        log_warning "Redis 服务启动失败"
    fi
}

# 恢复数据库
restore_database() {
    log "恢复数据库..."
    
    # 查找数据库备份文件
    DB_BACKUP=$(find "$BACKUP_EXTRACT_DIR/database" -name "jsh_erp_full_*.sql" | head -1)
    
    if [ -z "$DB_BACKUP" ]; then
        log_error "未找到数据库备份文件"
        exit 1
    fi
    
    # 等待数据库完全启动
    log "等待数据库完全启动..."
    for i in {1..30}; do
        if docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT 1" >/dev/null 2>&1; then
            break
        fi
        sleep 2
    done
    
    # 恢复数据库
    log "导入数据库备份..."
    docker exec -i jsherp-mysql-dev mysql -u root -p123456 jsh_erp < "$DB_BACKUP"
    
    # 验证数据库恢复
    TABLE_COUNT=$(docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'jsh_erp'" | tail -1)
    
    if [ "$TABLE_COUNT" -gt 0 ]; then
        log_success "数据库恢复完成，共 $TABLE_COUNT 个表"
    else
        log_error "数据库恢复失败"
        exit 1
    fi
}

# 恢复 Redis 数据
restore_redis() {
    log "恢复 Redis 数据..."
    
    # 查找 Redis 备份文件
    REDIS_BACKUP=$(find "$BACKUP_EXTRACT_DIR/redis" -name "dump_*.rdb" | head -1)
    
    if [ -z "$REDIS_BACKUP" ]; then
        log_warning "未找到 Redis 备份文件"
        return
    fi
    
    # 停止 Redis 服务
    docker-compose -f "$RESTORE_DIR/docker-compose.dev.yml" stop jsherp-redis-dev
    
    # 复制备份文件
    docker cp "$REDIS_BACKUP" jsherp-redis-dev:/data/dump.rdb
    
    # 重启 Redis 服务
    docker-compose -f "$RESTORE_DIR/docker-compose.dev.yml" start jsherp-redis-dev
    
    # 等待 Redis 启动
    sleep 10
    
    # 验证 Redis 恢复
    if docker exec jsherp-redis-dev redis-cli -a 1234abcd ping >/dev/null 2>&1; then
        log_success "Redis 数据恢复完成"
    else
        log_warning "Redis 数据恢复可能失败"
    fi
}

# 启动完整系统
start_full_system() {
    log "启动完整系统..."
    
    cd "$RESTORE_DIR"
    
    # 修复前端代理配置（如果需要）
    if [ -f "jshERP-web/vue.config.js" ]; then
        sed -i.bak 's/http:\/\/localhost:9999/http:\/\/jsherp-backend-dev:9999/g' jshERP-web/vue.config.js 2>/dev/null || true
    fi
    
    # 启动所有服务
    docker-compose -f docker-compose.dev.yml up -d
    
    # 等待服务启动
    log "等待所有服务启动..."
    sleep 60
    
    # 检查服务状态
    docker-compose -f docker-compose.dev.yml ps
}

# 验证系统恢复
verify_system_restore() {
    log "验证系统恢复..."
    
    local errors=0
    
    # 检查前端服务
    FRONTEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000 2>/dev/null || echo "000")
    if [ "$FRONTEND_STATUS" = "200" ]; then
        log_success "前端服务正常"
    else
        log_error "前端服务异常，状态码：$FRONTEND_STATUS"
        ((errors++))
    fi
    
    # 检查后端服务
    BACKEND_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9999/jshERP-boot/user/randomImage 2>/dev/null || echo "000")
    if [ "$BACKEND_STATUS" = "200" ]; then
        log_success "后端服务正常"
    else
        log_error "后端服务异常，状态码：$BACKEND_STATUS"
        ((errors++))
    fi
    
    # 检查数据库连接
    if docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT COUNT(*) FROM jsh_erp.jsh_user" >/dev/null 2>&1; then
        log_success "数据库连接正常"
    else
        log_error "数据库连接异常"
        ((errors++))
    fi
    
    # 检查 Redis 连接
    if docker exec jsherp-redis-dev redis-cli -a 1234abcd ping >/dev/null 2>&1; then
        log_success "Redis 连接正常"
    else
        log_warning "Redis 连接异常"
    fi
    
    if [ $errors -eq 0 ]; then
        log_success "系统恢复验证通过"
        return 0
    else
        log_error "系统恢复验证失败，发现 $errors 个错误"
        return 1
    fi
}

# 清理临时文件
cleanup() {
    if [ -n "$TEMP_DIR" ] && [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
        log "清理临时文件完成"
    fi
}

# 主函数
main() {
    echo "=== jshERP 系统完整恢复开始 ==="
    echo "恢复时间: $(date)"
    echo "=================================="
    
    # 设置清理陷阱
    trap cleanup EXIT
    
    # 执行恢复步骤
    check_parameters "$@"
    check_prerequisites
    extract_backup
    verify_backup_integrity
    restore_source_code
    restore_docker_images
    start_basic_services
    restore_database
    restore_redis
    start_full_system
    
    # 验证恢复结果
    if verify_system_restore; then
        echo ""
        echo "=== 恢复完成 ==="
        echo "恢复目录: $RESTORE_DIR"
        echo "前端地址: http://localhost:3000"
        echo "后端地址: http://localhost:9999/jshERP-boot"
        echo "默认账号: admin / 123456"
        echo "================="
        
        # 显示服务状态
        cd "$RESTORE_DIR"
        docker-compose -f docker-compose.dev.yml ps
        
    else
        log_error "系统恢复失败，请检查错误信息"
        exit 1
    fi
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
