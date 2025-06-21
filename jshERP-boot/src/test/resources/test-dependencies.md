# jshERP 生产管理模块单元测试说明

## 测试框架依赖

本项目使用以下测试框架和工具：

### 1. JUnit 5 (Jupiter)
- **版本**: 通过 `spring-boot-starter-test` 自动引入
- **用途**: 核心测试框架，提供测试注解和断言
- **主要注解**:
  - `@Test`: 标记测试方法
  - `@BeforeEach`: 每个测试方法执行前运行
  - `@ExtendWith`: 扩展测试功能

### 2. Mockito
- **版本**: 通过 `spring-boot-starter-test` 自动引入
- **用途**: Mock对象创建和行为验证
- **主要注解**:
  - `@Mock`: 创建Mock对象
  - `@InjectMocks`: 注入Mock对象到被测试类
  - `@ExtendWith(MockitoExtension.class)`: 启用Mockito扩展

### 3. Spring Test
- **版本**: 通过 `spring-boot-starter-test` 自动引入
- **用途**: Spring上下文测试支持
- **主要工具**:
  - `ReflectionTestUtils`: 反射工具，用于测试私有方法

## 测试类结构

### ProductionServiceImplTest.java

#### 测试覆盖范围
1. **基础CRUD操作测试**
   - `testGetProductionOrder_Success()` - 获取生产订单成功
   - `testGetProductionOrder_NotFound()` - 生产订单不存在
   - `testInsertProductionOrder_Success()` - 插入生产订单成功
   - `testUpdateProductionOrder_Success()` - 更新生产订单成功
   - `testDeleteProductionOrder_Success()` - 删除生产订单成功
   - `testDeleteProductionOrder_NotFound()` - 删除不存在的生产订单

2. **核心业务逻辑测试**
   - `testGenerateFromSalesOrder_Success_StockSufficient()` - 库存充足场景
   - `testGenerateFromSalesOrder_Success_StockInsufficient()` - 库存不足场景
   - `testGenerateFromSalesOrder_SalesOrderNotFound()` - 销售订单不存在
   - `testGenerateFromSalesOrder_ProductionOrderInsertFailed()` - 生产订单创建失败

3. **私有方法测试**（使用ReflectionTestUtils）
   - `testCheckBaseStockAvailability_StockSufficient()` - 库存检查（充足）
   - `testCheckBaseStockAvailability_StockInsufficient()` - 库存检查（不足）
   - `testCreateServicePurchaseOrder_StockSufficient()` - 采购订单创建（库存充足）
   - `testCreateServicePurchaseOrder_StockInsufficient()` - 采购订单创建（库存不足）
   - `testCreateBaseTransferOrder_StockSufficient()` - 调拨单创建（库存充足）
   - `testCreateBaseTransferOrder_StockInsufficient()` - 调拨单创建（库存不足）

4. **工具方法测试**
   - `testGenerateOrderNo_Success()` - 订单号生成（首次）
   - `testGenerateOrderNo_WithExistingOrders()` - 订单号生成（已有订单）
   - `testCheckOrderNoExists_True()` - 订单号存在检查
   - `testCheckOrderNoExists_False()` - 订单号不存在检查

5. **状态管理测试**
   - `testUpdateStatus_Success()` - 状态更新成功
   - `testUpdateCost_Success()` - 成本更新成功

6. **统计分析测试**
   - `testGetStatistics_Success()` - 统计数据获取成功

7. **批量操作测试**
   - `testBatchDeleteProductionOrder_Success()` - 批量删除成功
   - `testGetExpiringSoonOrders_Success()` - 获取即将到期订单

## 测试数据准备

### Mock对象设置
```java
@BeforeEach
void setUp() {
    // 模拟用户
    mockUser = new User();
    mockUser.setId(1L);
    mockUser.setTenantId(1L);
    mockUser.setUsername("testuser");

    // 模拟销售订单
    mockSalesOrder = new DepotHead();
    mockSalesOrder.setId(1L);
    mockSalesOrder.setNumber("SO20250621001");
    mockSalesOrder.setOperTime(new Date());

    // 模拟生产订单
    mockProductionOrder = new ProductionOrder();
    mockProductionOrder.setId(1L);
    mockProductionOrder.setOrderNo("PO20250621001");
    mockProductionOrder.setQuantity(new BigDecimal("5"));
}
```

## 运行测试

### 1. 使用Maven命令
```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=ProductionServiceImplTest

# 运行特定测试方法
mvn test -Dtest=ProductionServiceImplTest#testGenerateFromSalesOrder_Success_StockSufficient
```

### 2. 使用IDE
- 在IDE中右键点击测试类或测试方法
- 选择"Run Test"或"Debug Test"

### 3. 使用测试运行器
```bash
# 编译并运行测试运行器
mvn compile test-compile
java -cp target/test-classes:target/classes com.jsh.erp.service.ProductionServiceTestRunner
```

## 测试验证要点

### 1. 库存充足场景验证
- ✅ 生产订单创建成功
- ✅ 库存检查返回"充足"信息
- ✅ 不创建采购订单
- ✅ 创建调拨单

### 2. 库存不足场景验证
- ✅ 生产订单创建成功
- ✅ 库存检查返回"不足"信息
- ✅ 自动创建采购订单
- ✅ 不创建调拨单（因为库存不足）

### 3. 异常场景验证
- ✅ 销售订单不存在时抛出BusinessRunTimeException
- ✅ 生产订单创建失败时抛出BusinessRunTimeException
- ✅ 异常信息和错误码正确

### 4. Mock验证
- ✅ 验证依赖服务的方法调用次数
- ✅ 验证方法调用参数正确性
- ✅ 验证日志记录调用

## 测试覆盖率目标

- **方法覆盖率**: > 90%
- **行覆盖率**: > 85%
- **分支覆盖率**: > 80%

## 注意事项

1. **私有方法测试**: 使用ReflectionTestUtils访问私有方法
2. **异步操作**: 如有异步操作，需要使用适当的等待机制
3. **数据库事务**: 测试中使用@Transactional注解确保数据隔离
4. **Mock对象**: 确保Mock对象的行为与实际对象一致
5. **测试数据**: 使用有意义的测试数据，避免魔法数字
