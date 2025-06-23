#!/bin/bash

# =============================================
# 掐丝珐琅馆模块自动化部署脚本
# 版本: v1.0
# 作者: jshERP Team
# 创建时间: 2025-01-22
# =============================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
MODULE_NAME="cloisonne"
BACKUP_DIR="/tmp/jsherp-backup-$(date +%Y%m%d_%H%M%S)"

# 数据库配置
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-jsh_erp}"
DB_USER="${DB_USER:-jsh_user}"
DB_PASS="${DB_PASS:-123456}"

# 应用配置
JSHERP_BOOT_PATH="${JSHERP_BOOT_PATH:-/opt/jshERP-boot}"
JSHERP_WEB_PATH="${JSHERP_WEB_PATH:-/opt/jshERP-web}"
JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-8-openjdk}"

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
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        log_error "Java未安装，请先安装Java 1.8+"
        exit 1
    fi
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装，请先安装Maven 3.6+"
        exit 1
    fi
    
    # 检查Node.js
    if ! command -v node &> /dev/null; then
        log_error "Node.js未安装，请先安装Node.js 14.x+"
        exit 1
    fi
    
    # 检查MySQL客户端
    if ! command -v mysql &> /dev/null; then
        log_error "MySQL客户端未安装，请先安装MySQL客户端"
        exit 1
    fi
    
    log_success "系统依赖检查通过"
}

# 创建备份
create_backup() {
    log_info "创建备份目录: $BACKUP_DIR"
    mkdir -p "$BACKUP_DIR"
    
    # 备份数据库
    log_info "备份数据库..."
    mysqldump -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        --single-transaction --routines --triggers \
        "$DB_NAME" > "$BACKUP_DIR/database_backup.sql"
    
    # 备份后端代码
    if [ -d "$JSHERP_BOOT_PATH" ]; then
        log_info "备份后端代码..."
        tar -czf "$BACKUP_DIR/backend_backup.tar.gz" -C "$JSHERP_BOOT_PATH" .
    fi
    
    # 备份前端代码
    if [ -d "$JSHERP_WEB_PATH" ]; then
        log_info "备份前端代码..."
        tar -czf "$BACKUP_DIR/frontend_backup.tar.gz" -C "$JSHERP_WEB_PATH" .
    fi
    
    log_success "备份完成: $BACKUP_DIR"
}

# 部署数据库
deploy_database() {
    log_info "部署数据库结构..."
    
    # 检查数据库连接
    if ! mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null; then
        log_error "无法连接到数据库，请检查配置"
        exit 1
    fi
    
    # 执行建表脚本
    local sql_file="$PROJECT_ROOT/docs/cloisonne_module_tables.sql"
    if [ -f "$sql_file" ]; then
        log_info "执行建表脚本..."
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < "$sql_file"
        log_success "数据库结构部署完成"
    else
        log_error "找不到建表脚本: $sql_file"
        exit 1
    fi
    
    # 验证表创建
    log_info "验证表创建..."
    local table_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        -sN -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME' AND table_name LIKE 'jsh_cloisonne_%';" 2>/dev/null)
    
    if [ "$table_count" -eq 7 ]; then
        log_success "数据库表验证通过 (7张表)"
    else
        log_warning "数据库表数量异常: $table_count (期望: 7)"
    fi
}

# 部署后端
deploy_backend() {
    log_info "部署后端代码..."
    
    # 检查目标目录
    if [ ! -d "$JSHERP_BOOT_PATH" ]; then
        log_error "jshERP后端目录不存在: $JSHERP_BOOT_PATH"
        exit 1
    fi
    
    # 复制Java文件
    log_info "复制Java源文件..."
    
    # 实体类
    cp -r "$PROJECT_ROOT/src/main/java/com/jsh/erp/datasource/entities/Cloisonne"* \
        "$JSHERP_BOOT_PATH/src/main/java/com/jsh/erp/datasource/entities/" 2>/dev/null || true
    
    # Mapper接口
    cp -r "$PROJECT_ROOT/src/main/java/com/jsh/erp/datasource/mappers/Cloisonne"* \
        "$JSHERP_BOOT_PATH/src/main/java/com/jsh/erp/datasource/mappers/" 2>/dev/null || true
    
    # VO类
    cp -r "$PROJECT_ROOT/src/main/java/com/jsh/erp/datasource/vo/Cloisonne"* \
        "$JSHERP_BOOT_PATH/src/main/java/com/jsh/erp/datasource/vo/" 2>/dev/null || true
    
    # Service层
    cp -r "$PROJECT_ROOT/src/main/java/com/jsh/erp/service/cloisonne" \
        "$JSHERP_BOOT_PATH/src/main/java/com/jsh/erp/service/" 2>/dev/null || true
    
    # Controller层
    cp -r "$PROJECT_ROOT/src/main/java/com/jsh/erp/controller/Cloisonne"* \
        "$JSHERP_BOOT_PATH/src/main/java/com/jsh/erp/controller/" 2>/dev/null || true
    
    # XML映射文件
    log_info "复制MyBatis映射文件..."
    mkdir -p "$JSHERP_BOOT_PATH/src/main/resources/mapper_xml/cloisonne"
    cp -r "$PROJECT_ROOT/src/main/resources/mapper_xml/cloisonne"* \
        "$JSHERP_BOOT_PATH/src/main/resources/mapper_xml/" 2>/dev/null || true
    
    # 编译后端
    log_info "编译后端项目..."
    cd "$JSHERP_BOOT_PATH"
    mvn clean compile -DskipTests
    
    if [ $? -eq 0 ]; then
        log_success "后端编译成功"
    else
        log_error "后端编译失败"
        exit 1
    fi
    
    # 打包
    log_info "打包后端项目..."
    mvn package -DskipTests
    
    if [ $? -eq 0 ]; then
        log_success "后端打包成功"
    else
        log_error "后端打包失败"
        exit 1
    fi
}

# 部署前端
deploy_frontend() {
    log_info "部署前端代码..."
    
    # 检查目标目录
    if [ ! -d "$JSHERP_WEB_PATH" ]; then
        log_error "jshERP前端目录不存在: $JSHERP_WEB_PATH"
        exit 1
    fi
    
    # 复制Vue组件
    log_info "复制Vue组件..."
    cp -r "$PROJECT_ROOT/../jshERP-web/src/views/cloisonne" \
        "$JSHERP_WEB_PATH/src/views/" 2>/dev/null || true
    
    # 复制API接口
    log_info "复制API接口..."
    cp "$PROJECT_ROOT/../jshERP-web/src/api/cloisonne.js" \
        "$JSHERP_WEB_PATH/src/api/" 2>/dev/null || true
    
    # 复制路由配置
    log_info "复制路由配置..."
    cp "$PROJECT_ROOT/../jshERP-web/src/router/modules/cloisonne.js" \
        "$JSHERP_WEB_PATH/src/router/modules/" 2>/dev/null || true
    
    # 安装依赖
    log_info "安装前端依赖..."
    cd "$JSHERP_WEB_PATH"
    npm install
    
    if [ $? -eq 0 ]; then
        log_success "前端依赖安装成功"
    else
        log_error "前端依赖安装失败"
        exit 1
    fi
    
    # 构建前端
    log_info "构建前端项目..."
    npm run build
    
    if [ $? -eq 0 ]; then
        log_success "前端构建成功"
    else
        log_error "前端构建失败"
        exit 1
    fi
}

# 配置权限
configure_permissions() {
    log_info "配置用户权限..."
    
    # 这里可以添加权限配置的SQL脚本
    local permission_sql="
    -- 为管理员角色分配掐丝珐琅馆权限
    INSERT IGNORE INTO jsh_user_business (type, key_id, value, btn_str, tenant_id, delete_flag) 
    VALUES ('UserRole', '1', '10,1001,1002,1003,1004,1005,1006', '1,2,3,7', '0', '0');
    "
    
    echo "$permission_sql" | mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME"
    
    log_success "权限配置完成"
}

# 验证部署
verify_deployment() {
    log_info "验证部署结果..."
    
    # 验证数据库表
    local table_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        -sN -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='$DB_NAME' AND table_name LIKE 'jsh_cloisonne_%';" 2>/dev/null)
    
    if [ "$table_count" -eq 7 ]; then
        log_success "✓ 数据库表验证通过"
    else
        log_error "✗ 数据库表验证失败"
    fi
    
    # 验证后端文件
    if [ -f "$JSHERP_BOOT_PATH/target/jshERP-boot-"*.jar ]; then
        log_success "✓ 后端JAR包生成成功"
    else
        log_error "✗ 后端JAR包生成失败"
    fi
    
    # 验证前端文件
    if [ -d "$JSHERP_WEB_PATH/dist" ]; then
        log_success "✓ 前端构建文件生成成功"
    else
        log_error "✗ 前端构建文件生成失败"
    fi
    
    log_info "部署验证完成"
}

# 显示帮助信息
show_help() {
    echo "掐丝珐琅馆模块部署脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help              显示帮助信息"
    echo "  -b, --backup-only       仅创建备份"
    echo "  -d, --database-only     仅部署数据库"
    echo "  -f, --frontend-only     仅部署前端"
    echo "  -r, --backend-only      仅部署后端"
    echo "  -v, --verify-only       仅验证部署"
    echo "  --skip-backup          跳过备份步骤"
    echo "  --skip-verification    跳过验证步骤"
    echo ""
    echo "环境变量:"
    echo "  DB_HOST                数据库主机 (默认: localhost)"
    echo "  DB_PORT                数据库端口 (默认: 3306)"
    echo "  DB_NAME                数据库名称 (默认: jsh_erp)"
    echo "  DB_USER                数据库用户 (默认: jsh_user)"
    echo "  DB_PASS                数据库密码 (默认: 123456)"
    echo "  JSHERP_BOOT_PATH       后端项目路径"
    echo "  JSHERP_WEB_PATH        前端项目路径"
    echo ""
}

# 主函数
main() {
    local backup_only=false
    local database_only=false
    local frontend_only=false
    local backend_only=false
    local verify_only=false
    local skip_backup=false
    local skip_verification=false
    
    # 解析命令行参数
    while [[ $# -gt 0 ]]; do
        case $1 in
            -h|--help)
                show_help
                exit 0
                ;;
            -b|--backup-only)
                backup_only=true
                shift
                ;;
            -d|--database-only)
                database_only=true
                shift
                ;;
            -f|--frontend-only)
                frontend_only=true
                shift
                ;;
            -r|--backend-only)
                backend_only=true
                shift
                ;;
            -v|--verify-only)
                verify_only=true
                shift
                ;;
            --skip-backup)
                skip_backup=true
                shift
                ;;
            --skip-verification)
                skip_verification=true
                shift
                ;;
            *)
                log_error "未知参数: $1"
                show_help
                exit 1
                ;;
        esac
    done
    
    log_info "开始部署掐丝珐琅馆模块..."
    log_info "项目根目录: $PROJECT_ROOT"
    
    # 检查依赖
    check_dependencies
    
    # 执行相应的操作
    if [ "$backup_only" = true ]; then
        create_backup
        exit 0
    fi
    
    if [ "$verify_only" = true ]; then
        verify_deployment
        exit 0
    fi
    
    # 创建备份
    if [ "$skip_backup" = false ]; then
        create_backup
    fi
    
    # 部署数据库
    if [ "$database_only" = true ] || [ "$frontend_only" = false ] && [ "$backend_only" = false ]; then
        deploy_database
    fi
    
    # 部署后端
    if [ "$backend_only" = true ] || [ "$database_only" = false ] && [ "$frontend_only" = false ]; then
        deploy_backend
    fi
    
    # 部署前端
    if [ "$frontend_only" = true ] || [ "$database_only" = false ] && [ "$backend_only" = false ]; then
        deploy_frontend
    fi
    
    # 配置权限
    if [ "$database_only" = true ] || [ "$frontend_only" = false ] && [ "$backend_only" = false ]; then
        configure_permissions
    fi
    
    # 验证部署
    if [ "$skip_verification" = false ]; then
        verify_deployment
    fi
    
    log_success "掐丝珐琅馆模块部署完成！"
    log_info "备份位置: $BACKUP_DIR"
    log_info "请重启jshERP服务以使更改生效"
}

# 执行主函数
main "$@"
