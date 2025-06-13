#!/bin/bash

# jshERP 系统完整备份脚本
# 版本: v1.0
# 作者: jshERP 部署团队
# 日期: 2025-06-13

set -e

# 配置变量
PROJECT_DIR="/Users/macmini/Desktop/jshERP-0612-Cursor"
BACKUP_BASE_DIR="$HOME/backup/jshERP"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="$BACKUP_BASE_DIR/$TIMESTAMP"
LOG_FILE="$BACKUP_DIR/backup.log"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 日志函数
log() {
    if [ -f "$LOG_FILE" ]; then
        echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
    else
        echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1"
    fi
}

log_success() {
    if [ -f "$LOG_FILE" ]; then
        echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] ✅ $1${NC}" | tee -a "$LOG_FILE"
    else
        echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] ✅ $1${NC}"
    fi
}

log_warning() {
    if [ -f "$LOG_FILE" ]; then
        echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}" | tee -a "$LOG_FILE"
    else
        echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠️  $1${NC}"
    fi
}

log_error() {
    if [ -f "$LOG_FILE" ]; then
        echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ❌ $1${NC}" | tee -a "$LOG_FILE"
    else
        echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ❌ $1${NC}"
    fi
}

# 检查前置条件
check_prerequisites() {
    log "检查前置条件..."
    
    # 检查项目目录
    if [ ! -d "$PROJECT_DIR" ]; then
        log_error "项目目录不存在: $PROJECT_DIR"
        exit 1
    fi
    
    # 检查 Docker 服务
    if ! docker ps >/dev/null 2>&1; then
        log_error "Docker 服务未运行"
        exit 1
    fi
    
    # 检查容器状态
    if ! docker-compose -f "$PROJECT_DIR/docker-compose.dev.yml" ps | grep -q "Up"; then
        log_warning "部分容器未运行，将尝试备份可用的服务"
    fi
    
    # 创建备份目录
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$BACKUP_DIR/source_code"
    mkdir -p "$BACKUP_DIR/database"
    mkdir -p "$BACKUP_DIR/redis"
    mkdir -p "$BACKUP_DIR/docker"
    mkdir -p "$BACKUP_DIR/documents"
    mkdir -p "$BACKUP_DIR/scripts"

    # 创建日志文件
    touch "$LOG_FILE"
    
    log_success "前置条件检查完成"
}

# 备份源代码
backup_source_code() {
    log "开始备份源代码..."
    
    cd "$PROJECT_DIR"
    
    # 备份整个项目目录（排除不必要的文件）
    tar -czf "$BACKUP_DIR/source_code/jshERP_source_$TIMESTAMP.tar.gz" \
        --exclude='node_modules' \
        --exclude='target' \
        --exclude='*.log' \
        --exclude='.git' \
        --exclude='*.tmp' \
        .
    
    # 单独备份关键配置文件
    cp -r jshERP-boot/src/main/resources/ "$BACKUP_DIR/source_code/config/"
    cp docker-compose.dev.yml "$BACKUP_DIR/source_code/"
    
    # 备份修改过的关键文件
    mkdir -p "$BACKUP_DIR/source_code/modified_files"
    
    # 备份 MyBatis 配置修改
    cp jshERP-boot/src/main/resources/application-docker.properties "$BACKUP_DIR/source_code/modified_files/"
    
    # 备份验证码修改
    cp jshERP-boot/src/main/java/com/jsh/erp/controller/UserController.java "$BACKUP_DIR/source_code/modified_files/"
    
    # 创建修改说明文件
    cat > "$BACKUP_DIR/source_code/modified_files/MODIFICATIONS.md" << 'EOF'
# jshERP 系统关键修改说明

## 1. MyBatis 配置修改
文件: application-docker.properties
修改内容: 
- 原配置: mybatis-plus.mapper-locations=classpath*:/mapper_xml/*Mapper.xml
- 新配置: mybatis-plus.mapper-locations=classpath*:/mapper_xml/*.xml
修改原因: 确保 MapperEx.xml 扩展映射文件能够被正确加载

## 2. 验证码生成修改
文件: UserController.java
修改内容: 添加了 generateSimpleSvgCaptcha() 方法
修改原因: 解决 Docker 环境中图形库依赖问题，使用 SVG 格式生成验证码

## 3. 数据库表结构更新
表: jsh_system_config
新增字段:
- zero_change_amount_flag varchar(1) DEFAULT '0' COMMENT '零收付款启用标记，0未启用，1启用'
- customer_static_price_flag varchar(1) DEFAULT '0' COMMENT '客户静态单价启用标记，0未启用，1启用'
EOF
    
    log_success "源代码备份完成"
}

# 备份数据库
backup_database() {
    log "开始备份数据库..."
    
    # 检查 MySQL 容器状态
    if ! docker ps | grep -q "jsherp-mysql-dev"; then
        log_error "MySQL 容器未运行，跳过数据库备份"
        return 1
    fi
    
    # 备份完整数据库
    docker exec jsherp-mysql-dev mysqldump \
        -u root -p123456 \
        --single-transaction \
        --routines \
        --triggers \
        --events \
        --hex-blob \
        jsh_erp > "$BACKUP_DIR/database/jsh_erp_full_$TIMESTAMP.sql"
    
    # 备份仅表结构
    docker exec jsherp-mysql-dev mysqldump \
        -u root -p123456 \
        --no-data \
        --routines \
        --triggers \
        --events \
        jsh_erp > "$BACKUP_DIR/database/jsh_erp_schema_$TIMESTAMP.sql"
    
    # 备份关键表数据
    docker exec jsherp-mysql-dev mysqldump \
        -u root -p123456 \
        --single-transaction \
        jsh_erp \
        jsh_user jsh_role jsh_function jsh_system_config jsh_platform_config \
        > "$BACKUP_DIR/database/jsh_erp_core_data_$TIMESTAMP.sql"
    
    # 验证数据库备份
    if [ -s "$BACKUP_DIR/database/jsh_erp_full_$TIMESTAMP.sql" ]; then
        log_success "数据库备份完成"
        
        # 生成数据库信息
        docker exec jsherp-mysql-dev mysql -u root -p123456 -e "
        SELECT 
            table_name AS '表名',
            table_rows AS '记录数',
            ROUND(((data_length + index_length) / 1024 / 1024), 2) AS '大小(MB)'
        FROM information_schema.tables 
        WHERE table_schema = 'jsh_erp' 
        ORDER BY table_rows DESC;" > "$BACKUP_DIR/database/database_info.txt"
        
    else
        log_error "数据库备份失败"
        return 1
    fi
}

# 备份 Redis 数据
backup_redis() {
    log "开始备份 Redis 数据..."
    
    # 检查 Redis 容器状态
    if ! docker ps | grep -q "jsherp-redis-dev"; then
        log_error "Redis 容器未运行，跳过 Redis 备份"
        return 1
    fi
    
    # 强制保存当前数据
    docker exec jsherp-redis-dev redis-cli -a 1234abcd BGSAVE
    
    # 等待保存完成
    sleep 5
    
    # 复制 RDB 文件
    docker cp jsherp-redis-dev:/data/dump.rdb "$BACKUP_DIR/redis/dump_$TIMESTAMP.rdb"
    
    # 备份 Redis 配置信息
    docker exec jsherp-redis-dev redis-cli -a 1234abcd CONFIG GET "*" > "$BACKUP_DIR/redis/redis_config.txt"
    
    # 备份所有键信息
    docker exec jsherp-redis-dev redis-cli -a 1234abcd --scan > "$BACKUP_DIR/redis/redis_keys.txt"
    
    # 备份内存使用信息
    docker exec jsherp-redis-dev redis-cli -a 1234abcd INFO memory > "$BACKUP_DIR/redis/redis_memory_info.txt"
    
    log_success "Redis 数据备份完成"
}

# 备份 Docker 镜像和配置
backup_docker() {
    log "开始备份 Docker 镜像和配置..."
    
    # 获取相关镜像列表
    IMAGES=$(docker images --format "{{.Repository}}:{{.Tag}}" | grep -E "(jsherp|mysql|redis|nginx)" | grep -v "<none>")
    
    # 备份镜像
    for image in $IMAGES; do
        log "备份镜像: $image"
        image_file=$(echo "$image" | sed 's/[\/:]/_/g')
        docker save "$image" | gzip > "$BACKUP_DIR/docker/${image_file}_$TIMESTAMP.tar.gz"
    done
    
    # 备份 Docker Compose 文件
    cp "$PROJECT_DIR/docker-compose.dev.yml" "$BACKUP_DIR/docker/"
    
    # 备份 Dockerfile
    find "$PROJECT_DIR" -name "Dockerfile*" -exec cp {} "$BACKUP_DIR/docker/" \;
    
    # 生成镜像信息
    docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}" | grep -E "(jsherp|mysql|redis|nginx)" > "$BACKUP_DIR/docker/images_info.txt"
    
    log_success "Docker 镜像和配置备份完成"
}

# 备份文档和脚本
backup_documents() {
    log "开始备份文档和脚本..."

    # 备份总结文档
    if [ -f "$PROJECT_DIR/总结文档.md" ]; then
        cp "$PROJECT_DIR/总结文档.md" "$BACKUP_DIR/documents/"
    fi

    # 备份部署脚本
    if [ -f "$PROJECT_DIR/deploy.sh" ]; then
        cp "$PROJECT_DIR/deploy.sh" "$BACKUP_DIR/scripts/"
    fi

    # 备份当前备份脚本
    cp "$0" "$BACKUP_DIR/scripts/full_backup.sh"

    # 备份其他文档
    find "$PROJECT_DIR" -name "*.md" -o -name "*.txt" -o -name "README*" | while read file; do
        cp "$file" "$BACKUP_DIR/documents/" 2>/dev/null || true
    done

    log_success "文档和脚本备份完成"
}

# 生成备份清单和校验
generate_manifest() {
    log "生成备份清单和校验信息..."

    # 生成文件清单
    find "$BACKUP_DIR" -type f -exec ls -lh {} \; > "$BACKUP_DIR/file_manifest.txt"

    # 生成 MD5 校验文件
    find "$BACKUP_DIR" -type f -not -name "*.md5" -exec md5sum {} \; > "$BACKUP_DIR/backup_checksums.md5"

    # 生成备份信息文件
    cat > "$BACKUP_DIR/backup_info.txt" << EOF
=== jshERP 系统完整备份信息 ===

备份时间: $(date)
备份版本: v1.0
项目目录: $PROJECT_DIR
备份目录: $BACKUP_DIR

=== 系统信息 ===
操作系统: $(uname -a)
Docker 版本: $(docker --version)
Docker Compose 版本: $(docker-compose --version)

=== 服务状态 ===
$(docker-compose -f "$PROJECT_DIR/docker-compose.dev.yml" ps 2>/dev/null || echo "无法获取服务状态")

=== 数据库信息 ===
$(docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT VERSION() AS mysql_version" 2>/dev/null || echo "无法获取数据库版本")

=== Redis 信息 ===
$(docker exec jsherp-redis-dev redis-cli -a 1234abcd INFO server | grep redis_version 2>/dev/null || echo "无法获取 Redis 版本")

=== 备份大小统计 ===
$(du -sh "$BACKUP_DIR"/* | sort -hr)

总备份大小: $(du -sh "$BACKUP_DIR" | cut -f1)
EOF

    log_success "备份清单和校验信息生成完成"
}

# 验证备份完整性
verify_backup() {
    log "验证备份完整性..."

    local errors=0

    # 验证源代码备份
    if [ ! -f "$BACKUP_DIR/source_code/jshERP_source_$TIMESTAMP.tar.gz" ]; then
        log_error "源代码备份文件缺失"
        ((errors++))
    fi

    # 验证数据库备份
    if [ ! -f "$BACKUP_DIR/database/jsh_erp_full_$TIMESTAMP.sql" ]; then
        log_error "数据库备份文件缺失"
        ((errors++))
    else
        # 检查数据库备份文件是否为空
        if [ ! -s "$BACKUP_DIR/database/jsh_erp_full_$TIMESTAMP.sql" ]; then
            log_error "数据库备份文件为空"
            ((errors++))
        fi
    fi

    # 验证 Redis 备份
    if [ ! -f "$BACKUP_DIR/redis/dump_$TIMESTAMP.rdb" ]; then
        log_warning "Redis 备份文件缺失"
    fi

    # 验证 MD5 校验
    if [ -f "$BACKUP_DIR/backup_checksums.md5" ]; then
        cd "$BACKUP_DIR"
        if ! md5sum -c backup_checksums.md5 >/dev/null 2>&1; then
            log_error "备份文件校验失败"
            ((errors++))
        fi
    fi

    if [ $errors -eq 0 ]; then
        log_success "备份完整性验证通过"
        return 0
    else
        log_error "发现 $errors 个错误，备份可能不完整"
        return 1
    fi
}

# 压缩备份文件
compress_backup() {
    log "压缩备份文件..."

    cd "$BACKUP_BASE_DIR"

    # 创建最终的压缩包
    tar -czf "jshERP_complete_backup_$TIMESTAMP.tar.gz" "$TIMESTAMP/"

    # 生成压缩包信息
    ls -lh "jshERP_complete_backup_$TIMESTAMP.tar.gz" > "$TIMESTAMP/compressed_info.txt"

    log_success "备份文件压缩完成: jshERP_complete_backup_$TIMESTAMP.tar.gz"
}

# 清理旧备份
cleanup_old_backups() {
    log "清理旧备份文件..."

    # 保留最近 7 天的备份
    find "$BACKUP_BASE_DIR" -name "jshERP_complete_backup_*.tar.gz" -mtime +7 -delete 2>/dev/null || true
    find "$BACKUP_BASE_DIR" -maxdepth 1 -type d -name "20*" -mtime +7 -exec rm -rf {} \; 2>/dev/null || true

    log_success "旧备份清理完成"
}

# 主函数
main() {
    echo "=== jshERP 系统完整备份开始 ==="
    echo "备份时间: $(date)"
    echo "备份目录: $BACKUP_DIR"
    echo "=================================="

    # 执行备份步骤
    check_prerequisites
    backup_source_code
    backup_database
    backup_redis
    backup_docker
    backup_documents
    generate_manifest

    # 验证备份
    if verify_backup; then
        compress_backup
        cleanup_old_backups

        echo ""
        echo "=== 备份完成 ==="
        echo "备份位置: $BACKUP_BASE_DIR/jshERP_complete_backup_$TIMESTAMP.tar.gz"
        echo "备份大小: $(du -sh "$BACKUP_BASE_DIR/jshERP_complete_backup_$TIMESTAMP.tar.gz" | cut -f1)"
        echo "详细信息: $BACKUP_DIR/backup_info.txt"
        echo "================="

        # 显示备份摘要
        cat "$BACKUP_DIR/backup_info.txt"

    else
        log_error "备份验证失败，请检查日志: $LOG_FILE"
        exit 1
    fi
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
