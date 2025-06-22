#!/bin/bash

# jshERP 数据库管理脚本
# 提供数据库初始化、恢复、备份等功能
# 作者: AI Assistant
# 日期: 2025-06-21

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 配置变量
MYSQL_CONTAINER="jsherp-mysql-dev"
MYSQL_USER="jsh_user"
MYSQL_PASSWORD="123456"
MYSQL_DATABASE="jsh_erp"
GIT_SQL_FILE="jshERP-boot/docs/jsh_erp_latest.sql"
BACKUP_DIR="database_backups"

# 创建备份目录
mkdir -p "$BACKUP_DIR"

# 显示帮助信息
show_help() {
    echo -e "${CYAN}=== jshERP 数据库管理工具 ===${NC}"
    echo ""
    echo -e "${YELLOW}用法: $0 [选项]${NC}"
    echo ""
    echo -e "${BLUE}选项:${NC}"
    echo -e "  ${GREEN}init${NC}        从Git仓库初始化数据库"
    echo -e "  ${GREEN}restore${NC}     从备份文件恢复数据库"
    echo -e "  ${GREEN}backup${NC}      备份当前数据库"
    echo -e "  ${GREEN}status${NC}      检查数据库状态"
    echo -e "  ${GREEN}reset${NC}       重置数据库到初始状态"
    echo -e "  ${GREEN}help${NC}        显示此帮助信息"
    echo ""
    echo -e "${BLUE}示例:${NC}"
    echo -e "  $0 init         # 初始化数据库"
    echo -e "  $0 backup       # 备份数据库"
    echo -e "  $0 status       # 查看状态"
    echo ""
}

# 检查Docker容器状态
check_mysql_container() {
    if ! docker ps | grep -q "$MYSQL_CONTAINER"; then
        echo -e "${RED}错误: MySQL容器 $MYSQL_CONTAINER 未运行${NC}"
        echo -e "${YELLOW}请先启动MySQL容器: docker start $MYSQL_CONTAINER${NC}"
        exit 1
    fi
}

# 检查数据库状态
check_database_status() {
    echo -e "${BLUE}=== 数据库状态检查 ===${NC}"
    
    check_mysql_container
    echo -e "${GREEN}✓ MySQL容器正在运行${NC}"
    
    # 检查数据库连接
    if docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SELECT 1;" >/dev/null 2>&1; then
        echo -e "${GREEN}✓ 数据库连接正常${NC}"
    else
        echo -e "${RED}✗ 数据库连接失败${NC}"
        exit 1
    fi
    
    # 获取数据库信息
    TABLE_COUNT=$(docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SHOW TABLES;" 2>/dev/null | wc -l)
    TABLE_COUNT=$((TABLE_COUNT - 1))
    
    if [ "$TABLE_COUNT" -gt 0 ]; then
        USER_COUNT=$(docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SELECT COUNT(*) FROM jsh_user;" 2>/dev/null | tail -1)
        MATERIAL_COUNT=$(docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SELECT COUNT(*) FROM jsh_material;" 2>/dev/null | tail -1)
        
        echo -e "${GREEN}✓ 数据表数量: $TABLE_COUNT${NC}"
        echo -e "${GREEN}✓ 用户数量: $USER_COUNT${NC}"
        echo -e "${GREEN}✓ 物料数量: $MATERIAL_COUNT${NC}"
    else
        echo -e "${YELLOW}⚠ 数据库为空，需要初始化${NC}"
    fi
    
    echo ""
}

# 从Git仓库初始化数据库
init_database() {
    echo -e "${BLUE}=== 从Git仓库初始化数据库 ===${NC}"
    
    check_mysql_container
    
    # 检查SQL文件
    if [ ! -f "$GIT_SQL_FILE" ]; then
        echo -e "${RED}错误: Git SQL文件不存在: $GIT_SQL_FILE${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ 找到Git SQL文件: $GIT_SQL_FILE${NC}"
    FILE_SIZE=$(ls -lh "$GIT_SQL_FILE" | awk '{print $5}')
    echo -e "${BLUE}   文件大小: $FILE_SIZE${NC}"
    
    # 备份当前数据库
    echo -e "${YELLOW}正在备份当前数据库...${NC}"
    BACKUP_FILE="$BACKUP_DIR/backup_before_init_$(date +%Y%m%d_%H%M%S).sql"
    docker exec "$MYSQL_CONTAINER" mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" > "$BACKUP_FILE" 2>/dev/null
    echo -e "${GREEN}✓ 备份完成: $BACKUP_FILE${NC}"
    
    # 导入数据库
    echo -e "${YELLOW}正在导入数据库，请稍候...${NC}"
    if docker exec -i "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < "$GIT_SQL_FILE" 2>/dev/null; then
        echo -e "${GREEN}✓ 数据库初始化成功${NC}"
        check_database_status
    else
        echo -e "${RED}✗ 数据库初始化失败${NC}"
        exit 1
    fi
}

# 备份数据库
backup_database() {
    echo -e "${BLUE}=== 备份数据库 ===${NC}"
    
    check_mysql_container
    
    BACKUP_FILE="$BACKUP_DIR/jsh_erp_backup_$(date +%Y%m%d_%H%M%S).sql"
    echo -e "${YELLOW}正在备份数据库到: $BACKUP_FILE${NC}"
    
    if docker exec "$MYSQL_CONTAINER" mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" > "$BACKUP_FILE" 2>/dev/null; then
        echo -e "${GREEN}✓ 备份完成${NC}"
        FILE_SIZE=$(ls -lh "$BACKUP_FILE" | awk '{print $5}')
        echo -e "${BLUE}   备份文件大小: $FILE_SIZE${NC}"
    else
        echo -e "${RED}✗ 备份失败${NC}"
        exit 1
    fi
}

# 从备份恢复数据库
restore_database() {
    echo -e "${BLUE}=== 从备份恢复数据库 ===${NC}"
    
    check_mysql_container
    
    # 列出可用的备份文件
    echo -e "${YELLOW}可用的备份文件:${NC}"
    ls -la "$BACKUP_DIR"/*.sql 2>/dev/null | awk '{print NR ". " $9 " (" $5 " " $6 " " $7 ")"}' || {
        echo -e "${RED}没有找到备份文件${NC}"
        exit 1
    }
    
    echo ""
    echo -e "${YELLOW}请输入要恢复的备份文件编号:${NC}"
    read -r choice
    
    BACKUP_FILE=$(ls "$BACKUP_DIR"/*.sql 2>/dev/null | sed -n "${choice}p")
    
    if [ ! -f "$BACKUP_FILE" ]; then
        echo -e "${RED}无效的选择${NC}"
        exit 1
    fi
    
    echo -e "${YELLOW}正在从 $BACKUP_FILE 恢复数据库...${NC}"
    
    if docker exec -i "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < "$BACKUP_FILE" 2>/dev/null; then
        echo -e "${GREEN}✓ 数据库恢复成功${NC}"
        check_database_status
    else
        echo -e "${RED}✗ 数据库恢复失败${NC}"
        exit 1
    fi
}

# 重置数据库
reset_database() {
    echo -e "${BLUE}=== 重置数据库到初始状态 ===${NC}"
    echo -e "${RED}警告: 这将删除所有当前数据！${NC}"
    echo -e "${YELLOW}是否继续? (y/N):${NC}"
    read -r confirm
    
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo -e "${YELLOW}操作已取消${NC}"
        exit 0
    fi
    
    # 先备份
    backup_database
    
    # 然后初始化
    init_database
}

# 主程序
case "${1:-help}" in
    "init")
        init_database
        ;;
    "backup")
        backup_database
        ;;
    "restore")
        restore_database
        ;;
    "status")
        check_database_status
        ;;
    "reset")
        reset_database
        ;;
    "help"|*)
        show_help
        ;;
esac
