#!/bin/bash

# ProductionServiceImpl 单元测试运行脚本
# 用于快速运行和验证生产管理模块的单元测试

echo "🧪 jshERP 生产管理模块单元测试运行脚本"
echo "================================================"

# 设置项目路径
PROJECT_ROOT="/Users/macmini/Desktop/jshERP-0612-Cursor/jshERP-boot"
TEST_CLASS="com.jsh.erp.service.ProductionServiceImplTest"

# 检查项目目录是否存在
if [ ! -d "$PROJECT_ROOT" ]; then
    echo "❌ 错误：项目目录不存在 $PROJECT_ROOT"
    exit 1
fi

# 进入项目目录
cd "$PROJECT_ROOT"
echo "📁 当前目录：$(pwd)"

# 检查Maven是否可用
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误：Maven 未安装或不在PATH中"
    exit 1
fi

echo "✅ Maven 版本：$(mvn -version | head -1)"

# 函数：运行特定测试方法
run_specific_test() {
    local test_method=$1
    echo ""
    echo "🔍 运行测试方法：$test_method"
    echo "----------------------------------------"
    mvn test -Dtest="$TEST_CLASS#$test_method" -q
    if [ $? -eq 0 ]; then
        echo "✅ $test_method - 通过"
    else
        echo "❌ $test_method - 失败"
        return 1
    fi
}

# 函数：运行所有测试
run_all_tests() {
    echo ""
    echo "🚀 运行所有 ProductionServiceImpl 测试..."
    echo "----------------------------------------"
    mvn test -Dtest="$TEST_CLASS" -q
    if [ $? -eq 0 ]; then
        echo "✅ 所有测试通过"
        return 0
    else
        echo "❌ 部分测试失败"
        return 1
    fi
}

# 函数：显示测试菜单
show_menu() {
    echo ""
    echo "请选择要运行的测试："
    echo "1. 运行所有测试"
    echo "2. 智能生成工单测试（库存充足场景）"
    echo "3. 智能生成工单测试（库存不足场景）"
    echo "4. 基础CRUD操作测试"
    echo "5. 私有方法测试"
    echo "6. 异常处理测试"
    echo "7. 生成测试报告"
    echo "8. 退出"
    echo ""
    read -p "请输入选项 (1-8): " choice
}

# 主循环
while true; do
    show_menu
    
    case $choice in
        1)
            echo "🎯 运行所有测试..."
            run_all_tests
            ;;
        2)
            echo "🎯 运行智能生成工单测试（库存充足场景）..."
            run_specific_test "testGenerateFromSalesOrder_Success_StockSufficient"
            ;;
        3)
            echo "🎯 运行智能生成工单测试（库存不足场景）..."
            run_specific_test "testGenerateFromSalesOrder_Success_StockInsufficient"
            ;;
        4)
            echo "🎯 运行基础CRUD操作测试..."
            run_specific_test "testGetProductionOrder_Success"
            run_specific_test "testInsertProductionOrder_Success"
            run_specific_test "testUpdateProductionOrder_Success"
            run_specific_test "testDeleteProductionOrder_Success"
            ;;
        5)
            echo "🎯 运行私有方法测试..."
            run_specific_test "testCheckBaseStockAvailability_StockSufficient"
            run_specific_test "testCheckBaseStockAvailability_StockInsufficient"
            run_specific_test "testCreateServicePurchaseOrder_StockSufficient"
            run_specific_test "testCreateServicePurchaseOrder_StockInsufficient"
            run_specific_test "testCreateBaseTransferOrder_StockSufficient"
            run_specific_test "testCreateBaseTransferOrder_StockInsufficient"
            ;;
        6)
            echo "🎯 运行异常处理测试..."
            run_specific_test "testGenerateFromSalesOrder_SalesOrderNotFound"
            run_specific_test "testGenerateFromSalesOrder_ProductionOrderInsertFailed"
            run_specific_test "testDeleteProductionOrder_NotFound"
            ;;
        7)
            echo "📊 生成测试报告..."
            echo "运行测试并生成详细报告..."
            mvn test -Dtest="$TEST_CLASS" surefire-report:report
            if [ $? -eq 0 ]; then
                echo "✅ 测试报告已生成"
                echo "📁 报告位置：target/site/surefire-report.html"
                
                # 尝试打开报告（macOS）
                if command -v open &> /dev/null; then
                    read -p "是否打开测试报告？(y/n): " open_report
                    if [ "$open_report" = "y" ] || [ "$open_report" = "Y" ]; then
                        open "target/site/surefire-report.html"
                    fi
                fi
            else
                echo "❌ 测试报告生成失败"
            fi
            ;;
        8)
            echo "👋 退出测试脚本"
            exit 0
            ;;
        *)
            echo "❌ 无效选项，请输入 1-8"
            ;;
    esac
    
    echo ""
    read -p "按回车键继续..."
done
