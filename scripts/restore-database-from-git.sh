#!/bin/bash

# jshERP 数据库恢复脚本 - 使用Git仓库中的SQL文件
# 作者: AI Assistant
# 日期: 2025-06-21

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置变量
MYSQL_CONTAINER="jsherp-mysql-dev"
MYSQL_USER="jsh_user"
MYSQL_PASSWORD="123456"
MYSQL_DATABASE="jsh_erp"
SQL_FILE="jshERP-boot/docs/jsh_erp_2025-06-11_06-43-53_mysql_data_pbMor.sql"

echo -e "${BLUE}=== jshERP 数据库恢复脚本 ===${NC}"
echo -e "${YELLOW}使用Git仓库中的SQL文件恢复数据库${NC}"
echo ""

# 检查Docker容器状态
echo -e "${BLUE}1. 检查MySQL容器状态...${NC}"
if ! docker ps | grep -q "$MYSQL_CONTAINER"; then
    echo -e "${RED}错误: MySQL容器 $MYSQL_CONTAINER 未运行${NC}"
    echo -e "${YELLOW}请先启动MySQL容器${NC}"
    exit 1
fi
echo -e "${GREEN}✓ MySQL容器正在运行${NC}"

# 检查SQL文件是否存在
echo -e "${BLUE}2. 检查SQL文件...${NC}"
if [ ! -f "$SQL_FILE" ]; then
    echo -e "${RED}错误: SQL文件不存在: $SQL_FILE${NC}"
    exit 1
fi
echo -e "${GREEN}✓ SQL文件存在: $SQL_FILE${NC}"

# 显示文件信息
FILE_SIZE=$(ls -lh "$SQL_FILE" | awk '{print $5}')
echo -e "${BLUE}   文件大小: $FILE_SIZE${NC}"

# 备份当前数据库
echo -e "${BLUE}3. 备份当前数据库...${NC}"
BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
docker exec "$MYSQL_CONTAINER" mysqldump -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" > "$BACKUP_FILE" 2>/dev/null
echo -e "${GREEN}✓ 当前数据库已备份到: $BACKUP_FILE${NC}"

# 恢复数据库
echo -e "${BLUE}4. 恢复数据库...${NC}"
echo -e "${YELLOW}正在导入SQL文件，请稍候...${NC}"

if docker exec -i "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < "$SQL_FILE" 2>/dev/null; then
    echo -e "${GREEN}✓ 数据库恢复成功${NC}"
else
    echo -e "${RED}✗ 数据库恢复失败${NC}"
    echo -e "${YELLOW}正在恢复备份...${NC}"
    docker exec -i "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" "$MYSQL_DATABASE" < "$BACKUP_FILE" 2>/dev/null
    echo -e "${YELLOW}已恢复到备份状态${NC}"
    exit 1
fi

# 验证恢复结果
echo -e "${BLUE}5. 验证恢复结果...${NC}"
TABLE_COUNT=$(docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SHOW TABLES;" 2>/dev/null | wc -l)
TABLE_COUNT=$((TABLE_COUNT - 1)) # 减去表头

USER_COUNT=$(docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SELECT COUNT(*) FROM jsh_user;" 2>/dev/null | tail -1)

MATERIAL_COUNT=$(docker exec "$MYSQL_CONTAINER" mysql -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "USE $MYSQL_DATABASE; SELECT COUNT(*) FROM jsh_material;" 2>/dev/null | tail -1)

echo -e "${GREEN}✓ 数据表数量: $TABLE_COUNT${NC}"
echo -e "${GREEN}✓ 用户数量: $USER_COUNT${NC}"
echo -e "${GREEN}✓ 物料数量: $MATERIAL_COUNT${NC}"

# 清理备份文件
echo -e "${BLUE}6. 清理临时文件...${NC}"
rm -f "$BACKUP_FILE"
echo -e "${GREEN}✓ 临时备份文件已清理${NC}"

echo ""
echo -e "${GREEN}=== 数据库恢复完成 ===${NC}"
echo -e "${BLUE}现在可以通过以下方式访问:${NC}"
echo -e "${YELLOW}• phpMyAdmin: http://localhost:8081${NC}"
echo -e "${YELLOW}• jshERP前端: http://localhost:3000${NC}"
echo -e "${YELLOW}• jshERP后端: http://localhost:9999/jshERP-boot${NC}"
echo ""
echo -e "${BLUE}登录信息:${NC}"
echo -e "${YELLOW}• 数据库用户: $MYSQL_USER / $MYSQL_PASSWORD${NC}"
echo -e "${YELLOW}• jshERP用户: waterxi / 123456${NC}"
echo ""
