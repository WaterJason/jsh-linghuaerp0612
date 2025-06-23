#!/bin/bash

# jshERP功能按钮权限一键修复脚本
# 作者: Augment Code
# 版本: 1.0
# 用途: 快速诊断和修复jshERP系统中功能按钮消失的问题

set -e

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

# 检查Docker容器状态
check_containers() {
    log_info "检查Docker容器状态..."
    
    # 检查MySQL容器
    if ! docker ps | grep -q "jsherp-mysql-dev"; then
        log_error "MySQL容器未运行，请先启动MySQL容器"
        exit 1
    fi
    
    # 检查前端容器
    if ! docker ps | grep -q "jsherp-frontend-dev"; then
        log_warning "前端容器未运行，尝试重启..."
        docker restart jsherp-frontend-dev
        sleep 10
    fi
    
    # 检查后端容器
    if ! docker ps | grep -q "jsherp-backend-dev"; then
        log_warning "后端容器未运行，尝试重启..."
        docker restart jsherp-backend-dev
        sleep 15
    fi
    
    log_success "Docker容器状态检查完成"
}

# 诊断权限配置
diagnose_permissions() {
    log_info "诊断权限配置..."
    
    # 检查btn_str字段状态
    local result=$(docker exec jsherp-mysql-dev mysql -ujsh_user -p123456 jsh_erp -e "
    SELECT key_id, 
           CASE WHEN btn_str IS NULL THEN 'NULL' 
                WHEN btn_str = '' THEN 'EMPTY' 
                ELSE CONCAT('LENGTH:', CHAR_LENGTH(btn_str)) 
           END as btn_str_status
    FROM jsh_user_business 
    WHERE type = 'RoleFunctions' AND key_id IN (4, 10);" 2>/dev/null)
    
    echo "$result"
    
    # 检查是否包含关键功能ID
    local has_salary=$(docker exec jsherp-mysql-dev mysql -ujsh_user -p123456 jsh_erp -e "
    SELECT CASE WHEN btn_str LIKE '%\"funId\":1101%' THEN 'YES' ELSE 'NO' END as has_salary
    FROM jsh_user_business 
    WHERE type = 'RoleFunctions' AND key_id = 10;" 2>/dev/null | tail -n 1)
    
    local has_production=$(docker exec jsherp-mysql-dev mysql -ujsh_user -p123456 jsh_erp -e "
    SELECT CASE WHEN btn_str LIKE '%\"funId\":283%' THEN 'YES' ELSE 'NO' END as has_production
    FROM jsh_user_business 
    WHERE type = 'RoleFunctions' AND key_id = 10;" 2>/dev/null | tail -n 1)
    
    if [[ "$has_salary" == "YES" && "$has_production" == "YES" ]]; then
        log_success "权限配置正常，包含所有关键功能ID"
        return 0
    else
        log_warning "权限配置不完整，需要修复"
        return 1
    fi
}

# 修复权限配置
fix_permissions() {
    log_info "修复权限配置..."
    
    # 完整的按钮权限配置
    local btn_str='[
{"funId":13,"btnStr":"1"},
{"funId":14,"btnStr":"1"},
{"funId":16,"btnStr":"1"},
{"funId":18,"btnStr":"1"},
{"funId":22,"btnStr":"1"},
{"funId":23,"btnStr":"1,3"},
{"funId":25,"btnStr":"1,3"},
{"funId":26,"btnStr":"1"},
{"funId":31,"btnStr":"1"},
{"funId":33,"btnStr":"1,2,3,7"},
{"funId":40,"btnStr":"1,2,3,7"},
{"funId":41,"btnStr":"1,2,3,7"},
{"funId":194,"btnStr":"1"},
{"funId":195,"btnStr":"1"},
{"funId":197,"btnStr":"1,2,3,7"},
{"funId":199,"btnStr":"1,2,3,7"},
{"funId":200,"btnStr":"1,2,3,7"},
{"funId":201,"btnStr":"1,2,3,7"},
{"funId":202,"btnStr":"1,2,3,7"},
{"funId":203,"btnStr":"1,2,3,7"},
{"funId":241,"btnStr":"1,2,3,7"},
{"funId":242,"btnStr":"1,2,3,7"},
{"funId":261,"btnStr":"1,2,3,7"},
{"funId":281,"btnStr":"1"},
{"funId":283,"btnStr":"1,2,3,7"},
{"funId":290,"btnStr":"1,2,3,7"},
{"funId":291,"btnStr":"1,2,3,7"},
{"funId":292,"btnStr":"1,2,3,7"},
{"funId":293,"btnStr":"1,2,3,7"},
{"funId":295,"btnStr":"1,2,3,5,6,7"},
{"funId":296,"btnStr":"1,2,3,5,6,7"},
{"funId":297,"btnStr":"1,2,3,5,6,7"},
{"funId":298,"btnStr":"1,2,3,5,6,7"},
{"funId":299,"btnStr":"1,2,3,5,6,7"},
{"funId":301,"btnStr":"1,7"},
{"funId":302,"btnStr":"1,2,3,4,7"},
{"funId":303,"btnStr":"1,2,3,4,7"},
{"funId":304,"btnStr":"1,2,3,4,7"},
{"funId":305,"btnStr":"1,2,3,7"},
{"funId":307,"btnStr":"1,2,3,4,7"},
{"funId":1100,"btnStr":"1"},
{"funId":1101,"btnStr":"1,2,3,4,7"},
{"funId":1102,"btnStr":"1,2,3,4,7,8"},
{"funId":1103,"btnStr":"1,2,3,4,7,9"},
{"funId":1104,"btnStr":"4,7"},
{"funId":1105,"btnStr":"1,2,3,4"}
]'
    
    # 更新角色10的权限
    docker exec jsherp-mysql-dev mysql -ujsh_user -p123456 jsh_erp -e "
    UPDATE jsh_user_business 
    SET btn_str = '$btn_str'
    WHERE type = 'RoleFunctions' AND key_id = '10';" 2>/dev/null
    
    # 更新角色4的权限
    docker exec jsherp-mysql-dev mysql -ujsh_user -p123456 jsh_erp -e "
    UPDATE jsh_user_business 
    SET btn_str = '$btn_str'
    WHERE type = 'RoleFunctions' AND key_id = '4';" 2>/dev/null
    
    log_success "权限配置修复完成"
}

# 验证修复结果
verify_fix() {
    log_info "验证修复结果..."
    
    # 等待前端服务完全启动
    log_info "等待前端服务启动..."
    sleep 10
    
    # 检查前端服务是否可访问
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080 | grep -q "200"; then
        log_success "前端服务正常运行"
    else
        log_warning "前端服务可能还在启动中，请稍后手动验证"
    fi
    
    # 再次检查权限配置
    if diagnose_permissions > /dev/null 2>&1; then
        log_success "权限配置验证通过"
    else
        log_error "权限配置验证失败"
        return 1
    fi
}

# 主函数
main() {
    echo "=================================================="
    echo "    jshERP功能按钮权限一键修复脚本"
    echo "=================================================="
    echo ""
    
    # 步骤1: 检查容器状态
    check_containers
    echo ""
    
    # 步骤2: 诊断权限配置
    if diagnose_permissions; then
        log_info "权限配置正常，检查是否为服务问题..."
        
        # 重启前端服务以确保正常运行
        log_info "重启前端服务以确保正常运行..."
        docker restart jsherp-frontend-dev
        sleep 15
        
        log_success "服务重启完成，功能按钮应该已恢复正常"
    else
        # 步骤3: 修复权限配置
        fix_permissions
        echo ""
        
        # 步骤4: 验证修复结果
        verify_fix
    fi
    
    echo ""
    echo "=================================================="
    log_success "修复脚本执行完成！"
    echo ""
    echo "请访问以下页面验证功能按钮是否正常："
    echo "- 薪酬档案: http://localhost:8080/salary/profile"
    echo "- 生产订单: http://localhost:8080/production/order"
    echo "- 掐丝珐琅馆: http://localhost:8080/cloisonne/dashboard"
    echo "=================================================="
}

# 执行主函数
main "$@"
