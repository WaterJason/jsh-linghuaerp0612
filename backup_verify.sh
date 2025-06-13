#!/bin/bash

# jshERP 备份验证脚本
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
    echo "jshERP 备份验证脚本"
    echo ""
    echo "使用方法:"
    echo "  $0 <备份文件路径>"
    echo ""
    echo "参数说明:"
    echo "  备份文件路径    - 要验证的备份文件路径（.tar.gz 格式）"
    echo ""
    echo "示例:"
    echo "  $0 /backup/jshERP/jshERP_complete_backup_20250613_143022.tar.gz"
    echo "  $0 /backup/jshERP/20250613_143022/  # 验证解压后的目录"
}

# 检查参数
check_parameters() {
    if [ $# -ne 1 ]; then
        log_error "缺少必要参数"
        show_usage
        exit 1
    fi
    
    BACKUP_PATH="$1"
    
    if [ ! -e "$BACKUP_PATH" ]; then
        log_error "备份文件或目录不存在: $BACKUP_PATH"
        exit 1
    fi
    
    log "验证目标: $BACKUP_PATH"
}

# 验证压缩包完整性
verify_archive() {
    log "验证压缩包完整性..."
    
    if [ -f "$BACKUP_PATH" ] && [[ "$BACKUP_PATH" == *.tar.gz ]]; then
        # 验证 tar.gz 文件
        if tar -tzf "$BACKUP_PATH" >/dev/null 2>&1; then
            log_success "压缩包格式正确"
        else
            log_error "压缩包损坏或格式错误"
            return 1
        fi
        
        # 解压到临时目录
        TEMP_DIR=$(mktemp -d)
        tar -xzf "$BACKUP_PATH" -C "$TEMP_DIR"
        BACKUP_DIR=$(find "$TEMP_DIR" -maxdepth 1 -type d -name "20*" | head -1)
        
        if [ -z "$BACKUP_DIR" ]; then
            log_error "无法找到备份目录"
            rm -rf "$TEMP_DIR"
            return 1
        fi
        
        CLEANUP_TEMP=true
    elif [ -d "$BACKUP_PATH" ]; then
        # 直接验证目录
        BACKUP_DIR="$BACKUP_PATH"
        CLEANUP_TEMP=false
    else
        log_error "不支持的备份格式"
        return 1
    fi
    
    log_success "备份路径验证通过: $BACKUP_DIR"
}

# 验证 MD5 校验和
verify_checksums() {
    log "验证文件校验和..."
    
    if [ -f "$BACKUP_DIR/backup_checksums.md5" ]; then
        cd "$BACKUP_DIR"
        if md5sum -c backup_checksums.md5 >/dev/null 2>&1; then
            log_success "文件校验和验证通过"
        else
            log_error "文件校验和验证失败"
            return 1
        fi
    else
        log_warning "未找到校验和文件，跳过校验"
    fi
}

# 验证备份结构
verify_structure() {
    log "验证备份目录结构..."
    
    local errors=0
    
    # 检查必要目录
    local required_dirs=("source_code" "database" "docker")
    for dir in "${required_dirs[@]}"; do
        if [ ! -d "$BACKUP_DIR/$dir" ]; then
            log_error "缺少必要目录: $dir"
            ((errors++))
        else
            log_success "目录存在: $dir"
        fi
    done
    
    # 检查可选目录
    local optional_dirs=("redis" "documents" "scripts")
    for dir in "${optional_dirs[@]}"; do
        if [ -d "$BACKUP_DIR/$dir" ]; then
            log_success "可选目录存在: $dir"
        else
            log_warning "可选目录缺失: $dir"
        fi
    done
    
    if [ $errors -gt 0 ]; then
        log_error "目录结构验证失败，缺少 $errors 个必要目录"
        return 1
    fi
    
    log_success "备份目录结构验证通过"
}

# 验证源代码备份
verify_source_code() {
    log "验证源代码备份..."
    
    local source_dir="$BACKUP_DIR/source_code"
    local errors=0
    
    # 检查源代码压缩包
    local source_archive=$(find "$source_dir" -name "jshERP_source_*.tar.gz" | head -1)
    if [ -n "$source_archive" ]; then
        if tar -tzf "$source_archive" >/dev/null 2>&1; then
            log_success "源代码压缩包验证通过"
        else
            log_error "源代码压缩包损坏"
            ((errors++))
        fi
    else
        log_error "未找到源代码压缩包"
        ((errors++))
    fi
    
    # 检查配置文件
    if [ -d "$source_dir/config" ]; then
        if [ -f "$source_dir/config/application-docker.properties" ]; then
            log_success "配置文件存在"
        else
            log_warning "关键配置文件缺失"
        fi
    else
        log_warning "配置目录缺失"
    fi
    
    # 检查修改文件
    if [ -d "$source_dir/modified_files" ]; then
        if [ -f "$source_dir/modified_files/MODIFICATIONS.md" ]; then
            log_success "修改说明文档存在"
        else
            log_warning "修改说明文档缺失"
        fi
    else
        log_warning "修改文件目录缺失"
    fi
    
    if [ $errors -gt 0 ]; then
        return 1
    fi
    
    log_success "源代码备份验证通过"
}

# 验证数据库备份
verify_database() {
    log "验证数据库备份..."
    
    local db_dir="$BACKUP_DIR/database"
    local errors=0
    
    # 检查完整数据库备份
    local full_backup=$(find "$db_dir" -name "jsh_erp_full_*.sql" | head -1)
    if [ -n "$full_backup" ]; then
        if [ -s "$full_backup" ]; then
            # 检查 SQL 文件基本语法
            if head -10 "$full_backup" | grep -q "CREATE\|INSERT\|DROP"; then
                log_success "完整数据库备份验证通过"
            else
                log_error "数据库备份文件格式异常"
                ((errors++))
            fi
        else
            log_error "数据库备份文件为空"
            ((errors++))
        fi
    else
        log_error "未找到完整数据库备份文件"
        ((errors++))
    fi
    
    # 检查表结构备份
    local schema_backup=$(find "$db_dir" -name "jsh_erp_schema_*.sql" | head -1)
    if [ -n "$schema_backup" ]; then
        log_success "表结构备份存在"
    else
        log_warning "表结构备份缺失"
    fi
    
    # 检查核心数据备份
    local core_backup=$(find "$db_dir" -name "jsh_erp_core_data_*.sql" | head -1)
    if [ -n "$core_backup" ]; then
        log_success "核心数据备份存在"
    else
        log_warning "核心数据备份缺失"
    fi
    
    if [ $errors -gt 0 ]; then
        return 1
    fi
    
    log_success "数据库备份验证通过"
}

# 验证 Docker 镜像备份
verify_docker() {
    log "验证 Docker 镜像备份..."
    
    local docker_dir="$BACKUP_DIR/docker"
    local errors=0
    
    # 检查镜像文件
    local image_count=$(find "$docker_dir" -name "*.tar.gz" | wc -l)
    if [ "$image_count" -gt 0 ]; then
        log_success "找到 $image_count 个 Docker 镜像备份"
        
        # 验证镜像文件完整性
        find "$docker_dir" -name "*.tar.gz" | while read image_file; do
            if [ -s "$image_file" ]; then
                log_success "镜像文件存在: $(basename "$image_file")"
            else
                log_error "镜像文件为空: $(basename "$image_file")"
                ((errors++))
            fi
        done
    else
        log_warning "未找到 Docker 镜像备份文件"
    fi
    
    # 检查 Docker Compose 文件
    if [ -f "$docker_dir/docker-compose.dev.yml" ]; then
        log_success "Docker Compose 配置文件存在"
    else
        log_warning "Docker Compose 配置文件缺失"
    fi
    
    log_success "Docker 镜像备份验证通过"
}

# 验证 Redis 备份
verify_redis() {
    log "验证 Redis 备份..."
    
    local redis_dir="$BACKUP_DIR/redis"
    
    if [ ! -d "$redis_dir" ]; then
        log_warning "Redis 备份目录不存在"
        return 0
    fi
    
    # 检查 RDB 文件
    local rdb_file=$(find "$redis_dir" -name "dump_*.rdb" | head -1)
    if [ -n "$rdb_file" ]; then
        if [ -s "$rdb_file" ]; then
            log_success "Redis RDB 文件存在"
        else
            log_warning "Redis RDB 文件为空"
        fi
    else
        log_warning "未找到 Redis RDB 文件"
    fi
    
    # 检查配置文件
    if [ -f "$redis_dir/redis_config.txt" ]; then
        log_success "Redis 配置信息存在"
    else
        log_warning "Redis 配置信息缺失"
    fi
    
    log_success "Redis 备份验证通过"
}

# 验证备份信息
verify_backup_info() {
    log "验证备份信息..."
    
    # 检查备份信息文件
    if [ -f "$BACKUP_DIR/backup_info.txt" ]; then
        log_success "备份信息文件存在"
        
        # 显示备份信息摘要
        echo ""
        echo "=== 备份信息摘要 ==="
        grep -E "备份时间|备份版本|总备份大小" "$BACKUP_DIR/backup_info.txt" || true
        echo "==================="
        echo ""
    else
        log_warning "备份信息文件缺失"
    fi
    
    # 检查文件清单
    if [ -f "$BACKUP_DIR/file_manifest.txt" ]; then
        log_success "文件清单存在"
    else
        log_warning "文件清单缺失"
    fi
}

# 生成验证报告
generate_report() {
    local report_file="$BACKUP_DIR/verification_report.txt"
    
    cat > "$report_file" << EOF
=== jshERP 备份验证报告 ===

验证时间: $(date)
验证目标: $BACKUP_PATH
验证脚本: $0

=== 验证结果 ===
压缩包完整性: $ARCHIVE_STATUS
文件校验和: $CHECKSUM_STATUS
目录结构: $STRUCTURE_STATUS
源代码备份: $SOURCE_STATUS
数据库备份: $DATABASE_STATUS
Docker 镜像: $DOCKER_STATUS
Redis 备份: $REDIS_STATUS
备份信息: $INFO_STATUS

=== 总体评估 ===
验证状态: $OVERALL_STATUS
建议: $RECOMMENDATION

=== 详细信息 ===
$(cat "$BACKUP_DIR/backup_info.txt" 2>/dev/null || echo "备份信息文件不存在")

EOF

    log_success "验证报告已生成: $report_file"
}

# 清理临时文件
cleanup() {
    if [ "$CLEANUP_TEMP" = true ] && [ -n "$TEMP_DIR" ] && [ -d "$TEMP_DIR" ]; then
        rm -rf "$TEMP_DIR"
        log "清理临时文件完成"
    fi
}

# 主函数
main() {
    echo "=== jshERP 备份验证开始 ==="
    echo "验证时间: $(date)"
    echo "=================================="
    
    # 设置清理陷阱
    trap cleanup EXIT
    
    # 执行验证步骤
    check_parameters "$@"
    
    # 初始化状态变量
    ARCHIVE_STATUS="未验证"
    CHECKSUM_STATUS="未验证"
    STRUCTURE_STATUS="未验证"
    SOURCE_STATUS="未验证"
    DATABASE_STATUS="未验证"
    DOCKER_STATUS="未验证"
    REDIS_STATUS="未验证"
    INFO_STATUS="未验证"
    
    local total_errors=0
    
    # 验证压缩包
    if verify_archive; then
        ARCHIVE_STATUS="通过"
    else
        ARCHIVE_STATUS="失败"
        ((total_errors++))
    fi
    
    # 验证校验和
    if verify_checksums; then
        CHECKSUM_STATUS="通过"
    else
        CHECKSUM_STATUS="失败"
        ((total_errors++))
    fi
    
    # 验证结构
    if verify_structure; then
        STRUCTURE_STATUS="通过"
    else
        STRUCTURE_STATUS="失败"
        ((total_errors++))
    fi
    
    # 验证源代码
    if verify_source_code; then
        SOURCE_STATUS="通过"
    else
        SOURCE_STATUS="失败"
        ((total_errors++))
    fi
    
    # 验证数据库
    if verify_database; then
        DATABASE_STATUS="通过"
    else
        DATABASE_STATUS="失败"
        ((total_errors++))
    fi
    
    # 验证 Docker
    if verify_docker; then
        DOCKER_STATUS="通过"
    else
        DOCKER_STATUS="失败"
        ((total_errors++))
    fi
    
    # 验证 Redis
    if verify_redis; then
        REDIS_STATUS="通过"
    else
        REDIS_STATUS="警告"
    fi
    
    # 验证备份信息
    if verify_backup_info; then
        INFO_STATUS="通过"
    else
        INFO_STATUS="警告"
    fi
    
    # 生成总体评估
    if [ $total_errors -eq 0 ]; then
        OVERALL_STATUS="验证通过"
        RECOMMENDATION="备份文件完整，可以用于系统恢复"
        log_success "备份验证完全通过"
    elif [ $total_errors -le 2 ]; then
        OVERALL_STATUS="基本可用"
        RECOMMENDATION="备份文件基本完整，建议检查失败项目"
        log_warning "备份验证基本通过，有 $total_errors 个问题"
    else
        OVERALL_STATUS="验证失败"
        RECOMMENDATION="备份文件存在严重问题，不建议用于恢复"
        log_error "备份验证失败，发现 $total_errors 个严重问题"
    fi
    
    # 生成验证报告
    generate_report
    
    echo ""
    echo "=== 验证完成 ==="
    echo "总体状态: $OVERALL_STATUS"
    echo "错误数量: $total_errors"
    echo "验证报告: $BACKUP_DIR/verification_report.txt"
    echo "================="
    
    # 返回适当的退出码
    if [ $total_errors -eq 0 ]; then
        exit 0
    elif [ $total_errors -le 2 ]; then
        exit 1
    else
        exit 2
    fi
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
