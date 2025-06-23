#!/bin/bash

# =============================================
# 掐丝珐琅馆模块性能监控脚本
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
CYAN='\033[0;36m'
NC='\033[0m'

# 配置变量
DB_HOST="${DB_HOST:-localhost}"
DB_PORT="${DB_PORT:-3306}"
DB_NAME="${DB_NAME:-jsh_erp}"
DB_USER="${DB_USER:-jsh_user}"
DB_PASS="${DB_PASS:-123456}"

MONITOR_INTERVAL="${MONITOR_INTERVAL:-5}"
LOG_FILE="/var/log/jsherp/cloisonne-performance.log"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
    echo "$(date '+%Y-%m-%d %H:%M:%S') [INFO] $1" >> "$LOG_FILE"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
    echo "$(date '+%Y-%m-%d %H:%M:%S') [WARNING] $1" >> "$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    echo "$(date '+%Y-%m-%d %H:%M:%S') [ERROR] $1" >> "$LOG_FILE"
}

# 创建日志目录
create_log_dir() {
    local log_dir=$(dirname "$LOG_FILE")
    if [ ! -d "$log_dir" ]; then
        sudo mkdir -p "$log_dir"
        sudo chown $(whoami):$(whoami) "$log_dir"
    fi
}

# 检查数据库连接
check_database_connection() {
    if ! mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1;" &>/dev/null; then
        log_error "无法连接到数据库"
        exit 1
    fi
}

# 获取表统计信息
get_table_stats() {
    echo -e "\n${CYAN}=== 掐丝珐琅馆模块表统计 ===${NC}"
    
    local sql="
    SELECT 
        table_name AS '表名',
        table_rows AS '记录数',
        ROUND(data_length/1024/1024, 2) AS '数据大小(MB)',
        ROUND(index_length/1024/1024, 2) AS '索引大小(MB)',
        ROUND((data_length + index_length)/1024/1024, 2) AS '总大小(MB)'
    FROM information_schema.tables 
    WHERE table_schema = '$DB_NAME' 
    AND table_name LIKE 'jsh_cloisonne_%'
    ORDER BY (data_length + index_length) DESC;
    "
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql"
}

# 获取慢查询统计
get_slow_queries() {
    echo -e "\n${CYAN}=== 慢查询统计 ===${NC}"
    
    local sql="
    SELECT 
        SUBSTRING(sql_text, 1, 100) AS '查询语句',
        count_star AS '执行次数',
        ROUND(avg_timer_wait/1000000000000, 3) AS '平均执行时间(秒)',
        ROUND(max_timer_wait/1000000000000, 3) AS '最大执行时间(秒)'
    FROM performance_schema.events_statements_summary_by_digest 
    WHERE sql_text LIKE '%cloisonne%'
    AND avg_timer_wait > 1000000000
    ORDER BY avg_timer_wait DESC 
    LIMIT 10;
    "
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql" 2>/dev/null || echo "Performance Schema未启用或无慢查询数据"
}

# 获取索引使用情况
get_index_usage() {
    echo -e "\n${CYAN}=== 索引使用情况 ===${NC}"
    
    local sql="
    SELECT 
        table_name AS '表名',
        index_name AS '索引名',
        column_name AS '列名',
        cardinality AS '基数',
        CASE 
            WHEN non_unique = 0 THEN '唯一索引'
            ELSE '普通索引'
        END AS '索引类型'
    FROM information_schema.statistics 
    WHERE table_schema = '$DB_NAME' 
    AND table_name LIKE 'jsh_cloisonne_%'
    ORDER BY table_name, seq_in_index;
    "
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql"
}

# 获取连接状态
get_connection_stats() {
    echo -e "\n${CYAN}=== 数据库连接状态 ===${NC}"
    
    local sql="
    SHOW STATUS LIKE 'Connections';
    SHOW STATUS LIKE 'Threads_connected';
    SHOW STATUS LIKE 'Threads_running';
    SHOW STATUS LIKE 'Max_used_connections';
    "
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql"
}

# 获取缓存命中率
get_cache_hit_rate() {
    echo -e "\n${CYAN}=== 缓存命中率 ===${NC}"
    
    local sql="
    SELECT 
        ROUND(
            (1 - (Innodb_buffer_pool_reads / Innodb_buffer_pool_read_requests)) * 100, 2
        ) AS 'InnoDB缓存命中率(%)'
    FROM 
        (SELECT variable_value AS Innodb_buffer_pool_reads FROM information_schema.global_status WHERE variable_name = 'Innodb_buffer_pool_reads') AS reads,
        (SELECT variable_value AS Innodb_buffer_pool_read_requests FROM information_schema.global_status WHERE variable_name = 'Innodb_buffer_pool_read_requests') AS requests;
    "
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql"
}

# 获取锁等待情况
get_lock_waits() {
    echo -e "\n${CYAN}=== 锁等待情况 ===${NC}"
    
    local sql="
    SELECT 
        r.trx_id AS '等待事务ID',
        r.trx_mysql_thread_id AS '等待线程ID',
        SUBSTRING(r.trx_query, 1, 100) AS '等待查询',
        b.trx_id AS '阻塞事务ID',
        b.trx_mysql_thread_id AS '阻塞线程ID',
        SUBSTRING(b.trx_query, 1, 100) AS '阻塞查询'
    FROM information_schema.innodb_lock_waits w
    INNER JOIN information_schema.innodb_trx b ON b.trx_id = w.blocking_trx_id
    INNER JOIN information_schema.innodb_trx r ON r.trx_id = w.requesting_trx_id;
    "
    
    local result=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql" 2>/dev/null)
    if [ -z "$result" ]; then
        echo "当前无锁等待"
    else
        echo "$result"
    fi
}

# 检查表空间使用情况
check_tablespace_usage() {
    echo -e "\n${CYAN}=== 表空间使用情况 ===${NC}"
    
    local sql="
    SELECT 
        table_schema AS '数据库',
        ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS '总大小(MB)',
        ROUND(SUM(data_free) / 1024 / 1024, 2) AS '碎片大小(MB)',
        ROUND(SUM(data_free) / SUM(data_length + index_length) * 100, 2) AS '碎片率(%)'
    FROM information_schema.tables 
    WHERE table_schema = '$DB_NAME'
    GROUP BY table_schema;
    "
    
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "$sql"
}

# 生成性能报告
generate_performance_report() {
    local report_file="/tmp/cloisonne-performance-report-$(date +%Y%m%d_%H%M%S).txt"
    
    echo "掐丝珐琅馆模块性能报告" > "$report_file"
    echo "生成时间: $(date)" >> "$report_file"
    echo "======================================" >> "$report_file"
    
    {
        get_table_stats
        get_slow_queries
        get_index_usage
        get_connection_stats
        get_cache_hit_rate
        get_lock_waits
        check_tablespace_usage
    } >> "$report_file" 2>&1
    
    echo -e "\n${GREEN}性能报告已生成: $report_file${NC}"
}

# 实时监控模式
real_time_monitor() {
    echo -e "${GREEN}开始实时监控 (每${MONITOR_INTERVAL}秒刷新，按Ctrl+C退出)${NC}\n"
    
    while true; do
        clear
        echo -e "${PURPLE}掐丝珐琅馆模块实时性能监控${NC}"
        echo -e "${PURPLE}时间: $(date)${NC}"
        echo -e "${PURPLE}监控间隔: ${MONITOR_INTERVAL}秒${NC}"
        
        # 显示关键指标
        echo -e "\n${CYAN}=== 关键性能指标 ===${NC}"
        
        # 当前连接数
        local connections=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
            -sN -e "SHOW STATUS LIKE 'Threads_connected';" | awk '{print $2}')
        echo "当前连接数: $connections"
        
        # 正在运行的线程数
        local running_threads=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
            -sN -e "SHOW STATUS LIKE 'Threads_running';" | awk '{print $2}')
        echo "运行中线程: $running_threads"
        
        # 查询每秒执行数
        local qps=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" \
            -sN -e "SHOW STATUS LIKE 'Queries';" | awk '{print $2}')
        echo "累计查询数: $qps"
        
        # 掐丝珐琅馆表记录数
        echo -e "\n${CYAN}=== 模块表记录数 ===${NC}"
        mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -t -e "
        SELECT 
            SUBSTRING(table_name, 14) AS '表名',
            table_rows AS '记录数'
        FROM information_schema.tables 
        WHERE table_schema = '$DB_NAME' 
        AND table_name LIKE 'jsh_cloisonne_%'
        ORDER BY table_rows DESC;
        "
        
        sleep "$MONITOR_INTERVAL"
    done
}

# 优化建议
suggest_optimizations() {
    echo -e "\n${CYAN}=== 性能优化建议 ===${NC}"
    
    # 检查是否有未使用的索引
    local unused_indexes=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -sN -e "
    SELECT COUNT(*) FROM information_schema.statistics s
    LEFT JOIN performance_schema.table_io_waits_summary_by_index_usage i 
    ON s.table_schema = i.object_schema AND s.table_name = i.object_name AND s.index_name = i.index_name
    WHERE s.table_schema = '$DB_NAME' 
    AND s.table_name LIKE 'jsh_cloisonne_%'
    AND i.index_name IS NULL
    AND s.index_name != 'PRIMARY';
    " 2>/dev/null || echo "0")
    
    if [ "$unused_indexes" -gt 0 ]; then
        echo -e "${YELLOW}⚠ 发现 $unused_indexes 个可能未使用的索引，建议检查${NC}"
    fi
    
    # 检查表碎片
    local fragmented_tables=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -sN -e "
    SELECT COUNT(*) FROM information_schema.tables 
    WHERE table_schema = '$DB_NAME' 
    AND table_name LIKE 'jsh_cloisonne_%'
    AND data_free > 0;
    ")
    
    if [ "$fragmented_tables" -gt 0 ]; then
        echo -e "${YELLOW}⚠ 发现 $fragmented_tables 个表存在碎片，建议执行 OPTIMIZE TABLE${NC}"
    fi
    
    # 检查缓存命中率
    local hit_rate=$(mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -sN -e "
    SELECT ROUND((1 - (Innodb_buffer_pool_reads / Innodb_buffer_pool_read_requests)) * 100, 2)
    FROM 
        (SELECT variable_value AS Innodb_buffer_pool_reads FROM information_schema.global_status WHERE variable_name = 'Innodb_buffer_pool_reads') AS reads,
        (SELECT variable_value AS Innodb_buffer_pool_read_requests FROM information_schema.global_status WHERE variable_name = 'Innodb_buffer_pool_read_requests') AS requests;
    " 2>/dev/null || echo "0")
    
    if [ "${hit_rate%.*}" -lt 95 ]; then
        echo -e "${YELLOW}⚠ InnoDB缓存命中率较低 ($hit_rate%)，建议增加 innodb_buffer_pool_size${NC}"
    else
        echo -e "${GREEN}✓ InnoDB缓存命中率良好 ($hit_rate%)${NC}"
    fi
    
    echo -e "\n${GREEN}优化建议检查完成${NC}"
}

# 显示帮助信息
show_help() {
    echo "掐丝珐琅馆模块性能监控脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help              显示帮助信息"
    echo "  -s, --stats             显示表统计信息"
    echo "  -q, --slow-queries      显示慢查询统计"
    echo "  -i, --index-usage       显示索引使用情况"
    echo "  -c, --connections       显示连接状态"
    echo "  -l, --locks             显示锁等待情况"
    echo "  -r, --report            生成完整性能报告"
    echo "  -m, --monitor           实时监控模式"
    echo "  -o, --optimize          显示优化建议"
    echo ""
    echo "环境变量:"
    echo "  DB_HOST                数据库主机 (默认: localhost)"
    echo "  DB_PORT                数据库端口 (默认: 3306)"
    echo "  DB_NAME                数据库名称 (默认: jsh_erp)"
    echo "  DB_USER                数据库用户 (默认: jsh_user)"
    echo "  DB_PASS                数据库密码 (默认: 123456)"
    echo "  MONITOR_INTERVAL       监控间隔秒数 (默认: 5)"
    echo ""
}

# 主函数
main() {
    create_log_dir
    check_database_connection
    
    case "${1:-}" in
        -h|--help)
            show_help
            ;;
        -s|--stats)
            get_table_stats
            ;;
        -q|--slow-queries)
            get_slow_queries
            ;;
        -i|--index-usage)
            get_index_usage
            ;;
        -c|--connections)
            get_connection_stats
            ;;
        -l|--locks)
            get_lock_waits
            ;;
        -r|--report)
            generate_performance_report
            ;;
        -m|--monitor)
            real_time_monitor
            ;;
        -o|--optimize)
            suggest_optimizations
            ;;
        "")
            # 默认显示概览
            echo -e "${PURPLE}掐丝珐琅馆模块性能概览${NC}"
            get_table_stats
            get_connection_stats
            suggest_optimizations
            ;;
        *)
            echo "未知选项: $1"
            show_help
            exit 1
            ;;
    esac
}

# 捕获中断信号
trap 'echo -e "\n${YELLOW}监控已停止${NC}"; exit 0' INT

# 执行主函数
main "$@"
