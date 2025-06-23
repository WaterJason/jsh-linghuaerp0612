# ProductionServiceImpl 单元测试运行指南

## 📋 测试概述

ProductionServiceImpl 的单元测试已经完成，包含了完整的测试用例覆盖：

### 🧪 测试用例列表

#### 1. 核心业务方法测试
- **testGenerateFromSalesOrder_Success_StockSufficient** - 库存充足场景
- **testGenerateFromSalesOrder_Success_StockInsufficient** - 库存不足场景
- **testGenerateFromSalesOrder_SalesOrderNotFound** - 销售订单不存在
- **testGenerateFromSalesOrder_ProductionOrderInsertFailed** - 生产订单创建失败

#### 2. CRUD操作测试
- **testGetProductionOrder_Success** - 获取生产订单成功
- **testGetProductionOrder_NotFound** - 生产订单不存在
- **testInsertProductionOrder_Success** - 新增生产订单
- **testUpdateProductionOrder_Success** - 更新生产订单
- **testDeleteProductionOrder_Success** - 删除生产订单
- **testDeleteProductionOrder_NotFound** - 删除不存在的订单

#### 3. 业务逻辑测试
- **testGenerateOrderNo_Success** - 生成订单号
- **testGenerateOrderNo_WithExistingOrders** - 有现有订单时生成订单号
- **testCheckOrderNoExists_True/False** - 检查订单号是否存在
- **testUpdateStatus_Success** - 更新订单状态
- **testGetStatistics_Success** - 获取统计信息

#### 4. 私有方法测试（使用ReflectionTestUtils）
- **testCheckBaseStockAvailability_StockSufficient** - 库存检查（充足）
- **testCheckBaseStockAvailability_StockInsufficient** - 库存检查（不足）
- **testCreateServicePurchaseOrder_StockSufficient** - 采购订单创建（库存充足）
- **testCreateServicePurchaseOrder_StockInsufficient** - 采购订单创建（库存不足）
- **testCreateBaseTransferOrder_StockSufficient** - 调拨单创建（库存充足）
- **testCreateBaseTransferOrder_StockInsufficient** - 调拨单创建（库存不足）

#### 5. 批量操作和扩展功能测试
- **testBatchDeleteProductionOrder_Success** - 批量删除
- **testGetExpiringSoonOrders_Success** - 获取即将到期订单
- **testUpdateCost_Success** - 更新成本
- **testGetProductionOrdersBySalesOrderId_Success** - 根据销售订单ID查询
- **testGetProductionOrdersByMaterialId_Success** - 根据产品ID查询

## 🚀 运行测试

### 方法1：使用Maven命令行
```bash
# 进入项目根目录
cd /Users/macmini/Desktop/jshERP-0612-Cursor/jshERP-boot

# 运行单个测试类
mvn test -Dtest=ProductionServiceImplTest

# 运行特定测试方法
mvn test -Dtest=ProductionServiceImplTest#testGenerateFromSalesOrder_Success_StockSufficient

# 运行所有测试
mvn test
```

### 方法2：使用IDE运行
1. 在IDE中打开 `ProductionServiceImplTest.java`
2. 右键点击类名或方法名
3. 选择 "Run Test" 或 "Debug Test"

### 方法3：使用Gradle（如果项目使用Gradle）
```bash
# 运行单个测试类
./gradlew test --tests ProductionServiceImplTest

# 运行特定测试方法
./gradlew test --tests ProductionServiceImplTest.testGenerateFromSalesOrder_Success_StockSufficient
```

## 📊 测试覆盖率

### 预期覆盖率
- **方法覆盖率**: 95%+
- **行覆盖率**: 90%+
- **分支覆盖率**: 85%+

### 覆盖的功能点
✅ **智能生成工单流程**：
- 销售订单验证
- 生产订单创建
- 库存检查逻辑
- 采购订单创建
- 调拨单创建
- 异常处理

✅ **基础CRUD操作**：
- 增删改查操作
- 数据验证
- 权限检查
- 日志记录

✅ **业务规则验证**：
- 订单号生成规则
- 状态流转规则
- 成本计算逻辑
- 统计信息计算

## 🔧 测试配置

### 使用的测试框架
- **JUnit 4**: 测试框架
- **Mockito**: Mock框架
- **Spring Test**: Spring集成测试支持

### Mock的服务
```java
@Mock private ProductionOrderMapper productionOrderMapper;
@Mock private ProductionOrderMapperEx productionOrderMapperEx;
@Mock private UserService userService;
@Mock private LogService logService;
@Mock private DepotHeadService depotHeadService;
@Mock private MaterialService materialService;
@Mock private DepotService depotService;
@Mock private DepotItemService depotItemService;
@Mock private HttpServletRequest request;
```

### 测试数据设置
```java
// 模拟用户
mockUser.setId(1L);
mockUser.setTenantId(1L);
mockUser.setUsername("testuser");

// 模拟销售订单
mockSalesOrder.setId(1L);
mockSalesOrder.setNumber("SO20250621001");
mockSalesOrder.setType(BusinessConstants.DEPOTHEAD_TYPE_OUT);

// 模拟生产订单
mockProductionOrder.setId(1L);
mockProductionOrder.setOrderNumber("PO20250621001");
mockProductionOrder.setQuantity(new BigDecimal("5"));
```

## 🧪 关键测试场景

### 场景1：库存充足的智能生成工单
```java
@Test
public void testGenerateFromSalesOrder_Success_StockSufficient() {
    // 设置库存充足：当前库存10，需要数量5
    when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L))
        .thenReturn(new BigDecimal("10"));
    
    // 验证结果：
    // - 生产订单创建成功
    // - 库存检查显示充足
    // - 无需创建采购订单
    // - 创建调拨单成功
}
```

### 场景2：库存不足的智能生成工单
```java
@Test
public void testGenerateFromSalesOrder_Success_StockInsufficient() {
    // 设置库存不足：当前库存2，需要数量5
    when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L))
        .thenReturn(new BigDecimal("2"));
    
    // 验证结果：
    // - 生产订单创建成功
    // - 库存检查显示不足
    // - 创建采购订单（采购数量3）
    // - 调拨单创建失败（库存不足）
}
```

## 📝 测试验证点

### 1. 返回结果验证
```java
// 验证返回的Map包含正确的信息
assertNotNull(result);
assertEquals("生产订单创建成功", result.get("message"));
assertTrue(result.get("stockCheckResult").toString().contains("底胎库存充足"));
```

### 2. 方法调用验证
```java
// 验证依赖服务被正确调用
verify(depotHeadService).getDepotHead(salesOrderId);
verify(productionOrderMapper).insertSelective(any(ProductionOrder.class));
verify(materialService, times(3)).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
```

### 3. 异常处理验证
```java
// 验证异常情况的处理
try {
    productionService.generateFromSalesOrder(salesOrderId, request);
    fail("Expected BusinessRunTimeException to be thrown");
} catch (BusinessRunTimeException exception) {
    assertEquals(ExceptionConstants.DATA_READ_FAIL_CODE, exception.getCode());
}
```

## ⚠️ 注意事项

### 1. 测试环境要求
- Java 8+
- Maven 3.6+
- Spring Boot 2.x
- 确保所有依赖已正确配置

### 2. 测试数据隔离
- 每个测试方法都有独立的Mock设置
- 使用@Before方法初始化通用测试数据
- 避免测试之间的数据污染

### 3. 异步操作测试
- 如果有异步操作，需要使用适当的等待机制
- 考虑使用@Timeout注解防止测试无限等待

### 4. 数据库相关测试
- 当前测试使用Mock，不涉及真实数据库
- 如需集成测试，考虑使用@SpringBootTest和测试数据库

## 🔍 故障排除

### 常见问题1：Mock未生效
**症状**: NullPointerException或方法未被调用
**解决**: 检查@Mock注解和when().thenReturn()设置

### 常见问题2：字段名不匹配
**症状**: 测试失败，字段值不正确
**解决**: 确保测试中使用的字段名与实体类一致

### 常见问题3：依赖注入失败
**症状**: @InjectMocks的对象为null
**解决**: 确保使用@RunWith(MockitoJUnitRunner.class)

## ✅ 测试清单

运行测试前检查：
- [ ] 所有依赖服务已正确Mock
- [ ] 测试数据设置完整
- [ ] 字段名与实体类匹配
- [ ] 异常场景已覆盖
- [ ] 验证点设置正确

运行测试后验证：
- [ ] 所有测试用例通过
- [ ] 覆盖率达到预期
- [ ] 无警告或错误信息
- [ ] 测试报告生成正常
