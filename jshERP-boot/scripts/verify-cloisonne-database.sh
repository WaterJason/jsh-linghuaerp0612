#!/bin/bash

# =============================================
# 掐丝珐琅馆模块数据库验证脚本
# 版本: v1.0
# 作者: jshERP Team
# 创建时间: 2025-01-22
# =============================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m'

# 配置变量
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-jsh_erp}"
DB_USER="${DB_USER:-jsh_user}"
DB_PASS="${DB_PASS:-123456}"

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

# 检查数据库连接
check_database_connection() {
    log_info "检查数据库连接..."
    
    if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" &>/dev/null; then
        log_success "数据库连接正常"
    else
        log_error "无法连接到数据库，请检查配置"
        exit 1
    fi
}

# 验证表结构
verify_tables() {
    log_info "验证掐丝珐琅馆模块表结构..."
    
    # 期望的表列表
    local expected_tables=(
        "jsh_cloisonne_config"
        "jsh_cloisonne_schedule"
        "jsh_cloisonne_coffee_sales"
        "jsh_cloisonne_pos_product"
        "jsh_cloisonne_pos_order"
        "jsh_cloisonne_pos_order_item"
        "jsh_cloisonne_task"
    )
    
    local missing_tables=()
    local existing_tables=()
    
    for table in "${expected_tables[@]}"; do
        if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
           -e "SHOW TABLES LIKE '$table';" 2>/dev/null | grep -q "$table"; then
            existing_tables+=("$table")
        else
            missing_tables+=("$table")
        fi
    done
    
    echo -e "\n${PURPLE}=== 表结构验证结果 ===${NC}"
    echo "期望表数量: ${#expected_tables[@]}"
    echo "实际表数量: ${#existing_tables[@]}"
    
    if [ ${#existing_tables[@]} -eq ${#expected_tables[@]} ]; then
        log_success "所有表都已创建"
        for table in "${existing_tables[@]}"; do
            echo "  ✓ $table"
        done
    else
        log_warning "发现缺失的表:"
        for table in "${missing_tables[@]}"; do
            echo "  ✗ $table"
        done
    fi
}

# 验证表字段
verify_table_columns() {
    log_info "验证关键表字段..."
    
    echo -e "\n${PURPLE}=== 表字段验证 ===${NC}"
    
    # 验证排班表字段
    local schedule_columns=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -e "DESCRIBE jsh_cloisonne_schedule;" 2>/dev/null | awk 'NR>1 {print $1}' | tr '\n' ' ')
    
    echo "排班表字段: $schedule_columns"
    
    # 验证必要字段
    local required_fields=("id" "schedule_date" "employee_id" "shift_type" "tenant_id" "delete_flag")
    local missing_fields=()
    
    for field in "${required_fields[@]}"; do
        if [[ ! $schedule_columns =~ $field ]]; then
            missing_fields+=("$field")
        fi
    done
    
    if [ ${#missing_fields[@]} -eq 0 ]; then
        log_success "排班表关键字段验证通过"
    else
        log_warning "排班表缺失字段: ${missing_fields[*]}"
    fi
}

# 验证索引
verify_indexes() {
    log_info "验证表索引..."
    
    echo -e "\n${PURPLE}=== 索引验证 ===${NC}"
    
    # 检查排班表索引
    local schedule_indexes=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -e "SHOW INDEX FROM jsh_cloisonne_schedule;" 2>/dev/null | awk 'NR>1 {print $3}' | sort | uniq | tr '\n' ' ')
    
    echo "排班表索引: $schedule_indexes"
    
    # 检查是否有租户ID相关索引
    if [[ $schedule_indexes =~ "tenant" ]]; then
        log_success "发现租户相关索引"
    else
        log_warning "未发现租户相关索引，可能影响多租户查询性能"
    fi
}

# 验证视图
verify_views() {
    log_info "验证统计视图..."
    
    echo -e "\n${PURPLE}=== 视图验证 ===${NC}"
    
    local expected_views=(
        "v_cloisonne_schedule_stats"
        "v_cloisonne_coffee_stats"
        "v_cloisonne_pos_stats"
    )
    
    local existing_views=()
    local missing_views=()
    
    for view in "${expected_views[@]}"; do
        if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
           -e "SHOW TABLES LIKE '$view';" 2>/dev/null | grep -q "$view"; then
            existing_views+=("$view")
        else
            missing_views+=("$view")
        fi
    done
    
    echo "期望视图数量: ${#expected_views[@]}"
    echo "实际视图数量: ${#existing_views[@]}"
    
    if [ ${#existing_views[@]} -eq ${#expected_views[@]} ]; then
        log_success "所有统计视图都已创建"
        for view in "${existing_views[@]}"; do
            echo "  ✓ $view"
        done
    else
        log_warning "发现缺失的视图:"
        for view in "${missing_views[@]}"; do
            echo "  ✗ $view"
        done
    fi
}

# 验证触发器
verify_triggers() {
    log_info "验证数据库触发器..."
    
    echo -e "\n${PURPLE}=== 触发器验证 ===${NC}"
    
    local triggers=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -e "SHOW TRIGGERS LIKE '%cloisonne%';" 2>/dev/null | awk 'NR>1 {print $1}' | tr '\n' ' ')
    
    if [ -n "$triggers" ]; then
        log_success "发现触发器: $triggers"
    else
        log_warning "未发现掐丝珐琅馆相关触发器"
    fi
}

# 验证权限菜单
verify_permissions() {
    log_info "验证权限菜单配置..."
    
    echo -e "\n${PURPLE}=== 权限菜单验证 ===${NC}"
    
    # 检查主菜单
    local main_menu=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -sN -e "SELECT COUNT(*) FROM jsh_function WHERE name LIKE '%掐丝珐琅馆%' AND delete_flag = '0';" 2>/dev/null)
    
    if [ "$main_menu" -gt 0 ]; then
        log_success "发现掐丝珐琅馆主菜单"
    else
        log_warning "未发现掐丝珐琅馆主菜单"
    fi
    
    # 检查子菜单
    local sub_menus=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -sN -e "SELECT COUNT(*) FROM jsh_function WHERE url LIKE '/cloisonne/%' AND delete_flag = '0';" 2>/dev/null)
    
    echo "掐丝珐琅馆子菜单数量: $sub_menus"
    
    if [ "$sub_menus" -ge 4 ]; then
        log_success "子菜单配置正常"
    else
        log_warning "子菜单数量不足，期望至少4个"
    fi
}

# 验证配置数据
verify_config_data() {
    log_info "验证配置数据..."
    
    echo -e "\n${PURPLE}=== 配置数据验证 ===${NC}"
    
    local config_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -sN -e "SELECT COUNT(*) FROM jsh_cloisonne_config WHERE delete_flag = '0';" 2>/dev/null)
    
    echo "配置项数量: $config_count"
    
    if [ "$config_count" -gt 0 ]; then
        log_success "发现配置数据"
        
        # 显示配置项
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
            -e "SELECT config_key, config_type, description FROM jsh_cloisonne_config WHERE delete_flag = '0' LIMIT 5;" 2>/dev/null
    else
        log_warning "未发现配置数据"
    fi
}

# 验证示例数据
verify_sample_data() {
    log_info "验证示例数据..."
    
    echo -e "\n${PURPLE}=== 示例数据验证 ===${NC}"
    
    # 检查POS商品示例数据
    local product_count=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" \
        -sN -e "SELECT COUNT(*) FROM jsh_cloisonne_pos_product WHERE delete_flag = '0';" 2>/dev/null)
    
    echo "POS商品数量: $product_count"
    
    if [ "$product_count" -gt 0 ]; then
        log_success "发现POS商品示例数据"
    else
        log_warning "未发现POS商品示例数据"
    fi
}

# 数据完整性检查
verify_data_integrity() {
    log_info "验证数据完整性..."
    
    echo -e "\n${PURPLE}=== 数据完整性验证 ===${NC}"
    
    # 检查外键约束
    local foreign_keys=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        -e "SELECT COUNT(*) FROM information_schema.KEY_COLUMN_USAGE 
            WHERE CONSTRAINT_SCHEMA = '$DB_NAME' 
            AND TABLE_NAME LIKE 'jsh_cloisonne_%' 
            AND REFERENCED_TABLE_NAME IS NOT NULL;" 2>/dev/null | tail -n 1)
    
    echo "外键约束数量: $foreign_keys"
    
    if [ "$foreign_keys" -gt 0 ]; then
        log_success "发现外键约束，数据完整性有保障"
    else
        log_warning "未发现外键约束"
    fi
    
    # 检查唯一约束
    local unique_constraints=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        -e "SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS 
            WHERE CONSTRAINT_SCHEMA = '$DB_NAME' 
            AND TABLE_NAME LIKE 'jsh_cloisonne_%' 
            AND CONSTRAINT_TYPE = 'UNIQUE';" 2>/dev/null | tail -n 1)
    
    echo "唯一约束数量: $unique_constraints"
    
    if [ "$unique_constraints" -gt 0 ]; then
        log_success "发现唯一约束，防止数据重复"
    else
        log_warning "未发现唯一约束"
    fi
}

# 性能检查
verify_performance() {
    log_info "验证数据库性能配置..."
    
    echo -e "\n${PURPLE}=== 性能配置验证 ===${NC}"
    
    # 检查表引擎
    local innodb_tables=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        -e "SELECT COUNT(*) FROM information_schema.TABLES 
            WHERE TABLE_SCHEMA = '$DB_NAME' 
            AND TABLE_NAME LIKE 'jsh_cloisonne_%' 
            AND ENGINE = 'InnoDB';" 2>/dev/null | tail -n 1)
    
    echo "InnoDB表数量: $innodb_tables"
    
    if [ "$innodb_tables" -eq 7 ]; then
        log_success "所有表都使用InnoDB引擎，支持事务"
    else
        log_warning "部分表未使用InnoDB引擎"
    fi
    
    # 检查字符集
    local utf8_tables=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
        -e "SELECT COUNT(*) FROM information_schema.TABLES 
            WHERE TABLE_SCHEMA = '$DB_NAME' 
            AND TABLE_NAME LIKE 'jsh_cloisonne_%' 
            AND TABLE_COLLATION LIKE 'utf8%';" 2>/dev/null | tail -n 1)
    
    echo "UTF8字符集表数量: $utf8_tables"
    
    if [ "$utf8_tables" -eq 7 ]; then
        log_success "所有表都使用UTF8字符集"
    else
        log_warning "部分表未使用UTF8字符集"
    fi
}

# 生成验证报告
generate_report() {
    local report_file="/tmp/cloisonne-database-verification-$(date +%Y%m%d_%H%M%S).txt"
    
    echo "掐丝珐琅馆模块数据库验证报告" > "$report_file"
    echo "验证时间: $(date)" >> "$report_file"
    echo "数据库: $DB_HOST:$DB_PORT/$DB_NAME" >> "$report_file"
    echo "======================================" >> "$report_file"
    
    {
        verify_tables
        verify_table_columns
        verify_indexes
        verify_views
        verify_triggers
        verify_permissions
        verify_config_data
        verify_sample_data
        verify_data_integrity
        verify_performance
    } >> "$report_file" 2>&1
    
    echo -e "\n${GREEN}验证报告已生成: $report_file${NC}"
}

# 显示帮助信息
show_help() {
    echo "掐丝珐琅馆模块数据库验证脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help              显示帮助信息"
    echo "  -t, --tables            仅验证表结构"
    echo "  -v, --views             仅验证视图"
    echo "  -p, --permissions       仅验证权限菜单"
    echo "  -c, --config            仅验证配置数据"
    echo "  -r, --report            生成完整验证报告"
    echo "  --performance           仅验证性能配置"
    echo ""
    echo "环境变量:"
    echo "  DB_HOST                数据库主机 (默认: localhost)"
    echo "  DB_PORT                数据库端口 (默认: 3306)"
    echo "  DB_NAME                数据库名称 (默认: jsh_erp)"
    echo "  DB_USER                数据库用户 (默认: jsh_user)"
    echo "  DB_PASS                数据库密码 (默认: 123456)"
    echo ""
}

# 主函数
main() {
    check_database_connection
    
    case "${1:-}" in
        -h|--help)
            show_help
            ;;
        -t|--tables)
            verify_tables
            verify_table_columns
            ;;
        -v|--views)
            verify_views
            verify_triggers
            ;;
        -p|--permissions)
            verify_permissions
            ;;
        -c|--config)
            verify_config_data
            verify_sample_data
            ;;
        -r|--report)
            generate_report
            ;;
        --performance)
            verify_performance
            ;;
        "")
            # 默认执行完整验证
            echo -e "${PURPLE}掐丝珐琅馆模块数据库完整验证${NC}"
            verify_tables
            verify_table_columns
            verify_indexes
            verify_views
            verify_triggers
            verify_permissions
            verify_config_data
            verify_sample_data
            verify_data_integrity
            verify_performance
            
            echo -e "\n${GREEN}数据库验证完成！${NC}"
            ;;
        *)
            echo "未知选项: $1"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"
