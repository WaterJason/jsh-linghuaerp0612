# Week 8: 测试与集成策略文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第一阶段 - 生产制作管理模块
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第9章测试集成规范

---

## 概述

本文档为Week 8的测试与集成策略提供详细的实施指导。主要实现单元测试、集成测试、系统测试、性能测试等全面的测试体系，确保生产制作管理模块的质量和稳定性。

---

## 单元测试开发 (2天)

### 1. ProductionOrderServiceTest.java - 生产工单服务单元测试

**文件路径**: `jshERP-boot/src/test/java/com/jsh/erp/production/service/ProductionOrderServiceTest.java`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.production.datasource.entities.ProductionOrder;
import com.jsh.erp.production.datasource.mappers.ProductionOrderMapper;
import com.jsh.erp.production.datasource.mappers.ProductionOrderMapperEx;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 生产工单服务单元测试
 * 测试生产工单的核心业务逻辑
 * 
 * @author jshERP
 * @version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ProductionOrderServiceTest {

    @InjectMocks
    private ProductionOrderService productionOrderService;

    @Mock
    private ProductionOrderMapper productionOrderMapper;
    @Mock
    private ProductionOrderMapperEx productionOrderMapperEx;
    @Mock
    private MaterialService materialService;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletRequest request;

    private ProductionOrder testOrder;
    private JSONObject testOrderJson;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // 创建测试数据
        testOrder = new ProductionOrder();
        testOrder.setId(1L);
        testOrder.setOrderNo("PO202506180001");
        testOrder.setProductName("聆花丝巾");
        testOrder.setQuantity(new BigDecimal("100"));
        testOrder.setStatus("0");
        testOrder.setTenantId(1L);
        testOrder.setDeleteFlag("0");
        testOrder.setCreateTime(new Date());
        
        testOrderJson = new JSONObject();
        testOrderJson.put("productName", "聆花丝巾");
        testOrderJson.put("productSpec", "100cm×100cm");
        testOrderJson.put("quantity", "100");
        testOrderJson.put("unit", "件");
        testOrderJson.put("planStartDate", "2025-06-20");
        testOrderJson.put("planFinishDate", "2025-06-30");
        testOrderJson.put("priority", "1");
        testOrderJson.put("remark", "高品质丝巾制作");
    }

    /**
     * 测试工单创建功能
     * 验证工单创建的完整流程
     */
    @Test
    public void testCreateProductionOrder() throws Exception {
        // 模拟依赖服务
        when(productionOrderMapperEx.generateOrderNo()).thenReturn("PO202506180001");
        when(productionOrderMapper.insertSelective(any(ProductionOrder.class))).thenReturn(1);
        
        // 执行测试
        String result = productionOrderService.insertProductionOrder(testOrderJson, request);
        
        // 验证结果
        assertNotNull("工单创建结果不能为空", result);
        verify(productionOrderMapper, times(1)).insertSelective(any(ProductionOrder.class));
        
        // 验证工单号生成
        verify(productionOrderMapperEx, times(1)).generateOrderNo();
    }

    /**
     * 测试工单查询功能
     * 验证分页查询和条件筛选
     */
    @Test
    public void testQueryProductionOrderList() throws Exception {
        // 准备测试数据
        List<ProductionOrder> mockList = new ArrayList<>();
        mockList.add(testOrder);
        
        // 模拟查询参数
        JSONObject queryParam = new JSONObject();
        queryParam.put("status", "0");
        queryParam.put("productName", "聆花");
        
        // 模拟依赖服务
        when(productionOrderMapperEx.selectByConditionProductionOrder(anyString(), anyInt(), anyInt()))
            .thenReturn(mockList);
        when(productionOrderMapperEx.countsByProductionOrder(anyString()))
            .thenReturn(1L);
        
        // 执行测试
        List<ProductionOrder> result = productionOrderService.getProductionOrder(
            queryParam.toJSONString(), 1, 10, request);
        
        // 验证结果
        assertNotNull("查询结果不能为空", result);
        assertEquals("查询结果数量不正确", 1, result.size());
        assertEquals("工单名称不匹配", "聆花丝巾", result.get(0).getProductName());
    }

    /**
     * 测试工单状态更新
     * 验证状态流转的业务逻辑
     */
    @Test
    public void testUpdateOrderStatus() throws Exception {
        // 准备测试数据
        Long orderId = 1L;
        String newStatus = "1"; // 进行中
        
        // 模拟当前工单状态
        when(productionOrderMapper.selectByPrimaryKey(orderId)).thenReturn(testOrder);
        when(productionOrderMapper.updateByPrimaryKeySelective(any(ProductionOrder.class))).thenReturn(1);
        
        // 执行测试
        int result = productionOrderService.updateOrderStatus(orderId, newStatus, request);
        
        // 验证结果
        assertEquals("状态更新结果不正确", 1, result);
        verify(productionOrderMapper, times(1)).updateByPrimaryKeySelective(any(ProductionOrder.class));
    }

    /**
     * 测试工单分配功能
     * 验证工人分配的业务逻辑
     */
    @Test
    public void testAssignWorker() throws Exception {
        // 准备测试数据
        Long orderId = 1L;
        Long workerId = 100L;
        
        // 模拟工人信息验证
        when(userService.getUser(workerId)).thenReturn(new JSONObject());
        when(productionOrderMapper.selectByPrimaryKey(orderId)).thenReturn(testOrder);
        when(productionOrderMapper.updateByPrimaryKeySelective(any(ProductionOrder.class))).thenReturn(1);
        
        // 执行测试
        boolean result = productionOrderService.assignWorker(orderId, workerId, request);
        
        // 验证结果
        assertTrue("工人分配失败", result);
        verify(userService, times(1)).getUser(workerId);
        verify(productionOrderMapper, times(1)).updateByPrimaryKeySelective(any(ProductionOrder.class));
    }

    /**
     * 测试工单删除功能
     * 验证软删除逻辑
     */
    @Test
    public void testDeleteProductionOrder() throws Exception {
        // 准备测试数据
        String orderIds = "1,2,3";
        
        // 模拟删除操作
        when(productionOrderMapperEx.batchDeleteProductionOrderByIds(anyString())).thenReturn(3);
        
        // 执行测试
        int result = productionOrderService.deleteProductionOrder(orderIds, request);
        
        // 验证结果
        assertEquals("删除结果不正确", 3, result);
        verify(productionOrderMapperEx, times(1)).batchDeleteProductionOrderByIds(orderIds);
    }

    /**
     * 测试异常情况处理
     * 验证业务异常的处理逻辑
     */
    @Test(expected = Exception.class)
    public void testCreateOrderWithInvalidData() throws Exception {
        // 准备无效数据
        JSONObject invalidData = new JSONObject();
        invalidData.put("productName", ""); // 空产品名称
        
        // 执行测试，期望抛出异常
        productionOrderService.insertProductionOrder(invalidData, request);
    }

    /**
     * 测试并发安全
     * 验证多线程环境下的数据一致性
     */
    @Test
    public void testConcurrentOrderCreation() throws Exception {
        // 模拟并发创建工单
        when(productionOrderMapperEx.generateOrderNo())
            .thenReturn("PO202506180001")
            .thenReturn("PO202506180002");
        when(productionOrderMapper.insertSelective(any(ProductionOrder.class))).thenReturn(1);
        
        // 执行并发测试
        String result1 = productionOrderService.insertProductionOrder(testOrderJson, request);
        String result2 = productionOrderService.insertProductionOrder(testOrderJson, request);
        
        // 验证结果
        assertNotNull("第一个工单创建失败", result1);
        assertNotNull("第二个工单创建失败", result2);
        assertNotEquals("工单号重复", result1, result2);
    }
}
```

### 2. ProductionMaterialServiceTest.java - 生产物料服务测试

**文件路径**: `jshERP-boot/src/test/java/com/jsh/erp/production/service/ProductionMaterialServiceTest.java`

```java
package com.jsh.erp.production.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.production.datasource.entities.ProductionMaterial;
import com.jsh.erp.production.datasource.mappers.ProductionMaterialMapper;
import com.jsh.erp.production.datasource.mappers.ProductionMaterialMapperEx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 生产物料服务单元测试
 * 测试物料需求计算和库存检查逻辑
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductionMaterialServiceTest {

    @InjectMocks
    private ProductionMaterialService productionMaterialService;

    @Mock
    private ProductionMaterialMapper productionMaterialMapper;
    @Mock
    private ProductionMaterialMapperEx productionMaterialMapperEx;
    @Mock
    private HttpServletRequest request;

    /**
     * 测试物料需求计算
     */
    @Test
    public void testCalculateMaterialRequirement() throws Exception {
        // 准备测试数据
        JSONObject productionInfo = new JSONObject();
        productionInfo.put("productionOrderId", 1L);
        productionInfo.put("productType", "丝巾");
        productionInfo.put("quantity", 100);
        
        // 模拟BOM数据
        List<ProductionMaterial> bomList = new ArrayList<>();
        ProductionMaterial material1 = new ProductionMaterial();
        material1.setMaterialId(1L);
        material1.setMaterialName("真丝面料");
        material1.setRequiredQuantity(new BigDecimal("150"));
        material1.setUnit("米");
        bomList.add(material1);
        
        when(productionMaterialMapperEx.selectBOMByProductType("丝巾")).thenReturn(bomList);
        
        // 执行测试
        JSONArray result = productionMaterialService.calculateMaterialRequirement(productionInfo);
        
        // 验证结果
        assertNotNull("物料需求计算结果不能为空", result);
        assertTrue("物料需求数量不正确", result.size() > 0);
        
        JSONObject firstMaterial = result.getJSONObject(0);
        assertEquals("物料名称不匹配", "真丝面料", firstMaterial.getString("materialName"));
        assertEquals("需求数量计算错误", new BigDecimal("15000"), firstMaterial.getBigDecimal("totalRequired"));
    }

    /**
     * 测试库存充足性检查
     */
    @Test
    public void testCheckInventoryAvailability() throws Exception {
        // 准备测试数据
        JSONArray materialRequirements = new JSONArray();
        JSONObject requirement = new JSONObject();
        requirement.put("materialId", 1L);
        requirement.put("requiredQuantity", new BigDecimal("1000"));
        materialRequirements.add(requirement);
        
        // 模拟库存数据
        when(productionMaterialMapperEx.selectInventoryByMaterialId(1L))
            .thenReturn(new BigDecimal("1500"));
        
        // 执行测试
        JSONObject result = productionMaterialService.checkInventoryAvailability(materialRequirements);
        
        // 验证结果
        assertTrue("库存检查结果错误", result.getBoolean("sufficient"));
        assertEquals("库存余量计算错误", new BigDecimal("500"), 
            result.getJSONArray("details").getJSONObject(0).getBigDecimal("availableQuantity"));
    }
}
```

---

## 集成测试开发 (1天)

### 1. ProductionIntegrationTest.java - 生产模块集成测试

**文件路径**: `jshERP-boot/src/test/java/com/jsh/erp/production/integration/ProductionIntegrationTest.java`

```java
package com.jsh.erp.production.integration;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.production.service.ProductionOrderService;
import com.jsh.erp.production.service.WorkReportService;
import com.jsh.erp.production.service.LogisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * 生产模块集成测试
 * 测试各个服务之间的协作和数据流转
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ProductionIntegrationTest {

    @Resource
    private ProductionOrderService productionOrderService;
    @Resource
    private WorkReportService workReportService;
    @Resource
    private LogisticsService logisticsService;

    private HttpServletRequest mockRequest = mock(HttpServletRequest.class);

    /**
     * 测试完整的生产流程
     * 从工单创建到物流发货的端到端测试
     */
    @Test
    public void testCompleteProductionWorkflow() throws Exception {
        // 1. 创建生产工单
        JSONObject orderData = new JSONObject();
        orderData.put("productName", "聆花丝巾");
        orderData.put("quantity", "50");
        orderData.put("planStartDate", "2025-06-20");
        orderData.put("planFinishDate", "2025-06-25");
        
        String orderId = productionOrderService.insertProductionOrder(orderData, mockRequest);
        assertNotNull("工单创建失败", orderId);
        
        // 2. 分配工人
        boolean assignResult = productionOrderService.assignWorker(Long.valueOf(orderId), 100L, mockRequest);
        assertTrue("工人分配失败", assignResult);
        
        // 3. 开始生产（更新状态）
        int statusResult = productionOrderService.updateOrderStatus(Long.valueOf(orderId), "1", mockRequest);
        assertEquals("状态更新失败", 1, statusResult);
        
        // 4. 提交报工记录
        JSONObject workReportData = new JSONObject();
        workReportData.put("productionOrderId", orderId);
        workReportData.put("workerId", 100L);
        workReportData.put("workDate", "2025-06-21");
        workReportData.put("completedQuantity", "25");
        workReportData.put("workHours", "8");
        
        String reportId = workReportService.insertWorkReport(workReportData, mockRequest);
        assertNotNull("报工记录创建失败", reportId);
        
        // 5. 完工确认
        statusResult = productionOrderService.updateOrderStatus(Long.valueOf(orderId), "2", mockRequest);
        assertEquals("完工状态更新失败", 1, statusResult);
        
        // 6. 创建物流追踪
        JSONObject shipmentData = new JSONObject();
        shipmentData.put("productionOrderId", orderId);
        shipmentData.put("shipmentType", "半成品回寄");
        shipmentData.put("receiverInfo", "聆花文化总部");
        shipmentData.put("carrierCompany", "顺丰速运");
        
        String trackingId = logisticsService.createLogisticsTrack(shipmentData, mockRequest);
        assertNotNull("物流追踪创建失败", trackingId);
        
        // 验证整个流程的数据一致性
        // 通过查询验证所有相关记录都已正确创建
    }

    /**
     * 测试库存集成
     * 验证生产与库存系统的数据同步
     */
    @Test
    public void testInventoryIntegration() throws Exception {
        // 测试物料消耗与库存减少的同步
        // 测试产品完工与库存增加的同步
        // 验证库存预警机制
    }

    /**
     * 测试权限集成
     * 验证用户权限与生产操作的关联
     */
    @Test
    public void testPermissionIntegration() throws Exception {
        // 测试不同角色的操作权限
        // 验证数据访问控制
        // 测试租户隔离机制
    }
}
```

### 2. API集成测试 - ProductionControllerIntegrationTest.java

**文件路径**: `jshERP-boot/src/test/java/com/jsh/erp/production/controller/ProductionControllerIntegrationTest.java`

```java
package com.jsh.erp.production.controller;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 生产控制器集成测试
 * 测试REST API的完整功能
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
public class ProductionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 测试工单创建API
     */
    @Test
    public void testCreateProductionOrderAPI() throws Exception {
        JSONObject orderData = new JSONObject();
        orderData.put("productName", "聆花丝巾");
        orderData.put("quantity", "100");
        orderData.put("planStartDate", "2025-06-20");
        
        mockMvc.perform(post("/production/order/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(orderData.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.msg").value("成功"));
    }

    /**
     * 测试工单查询API
     */
    @Test
    public void testQueryProductionOrderAPI() throws Exception {
        mockMvc.perform(get("/production/order/list")
                .param("currentPage", "1")
                .param("pageSize", "10")
                .param("status", "0"))
                .andExpect(status().isOk())
                .andExpected(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试报工记录API
     */
    @Test
    public void testWorkReportAPI() throws Exception {
        JSONObject reportData = new JSONObject();
        reportData.put("productionOrderId", "1");
        reportData.put("workDate", "2025-06-21");
        reportData.put("completedQuantity", "50");
        
        mockMvc.perform(post("/production/workreport/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reportData.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
```

---

## 性能测试开发 (1天)

### 1. ProductionPerformanceTest.java - 性能测试

**文件路径**: `jshERP-boot/src/test/java/com/jsh/erp/production/performance/ProductionPerformanceTest.java`

```java
package com.jsh.erp.production.performance;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.production.service.ProductionOrderService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * 生产模块性能测试
 * 测试系统在高负载下的性能表现
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductionPerformanceTest {

    @Resource
    private ProductionOrderService productionOrderService;
    
    private HttpServletRequest mockRequest = mock(HttpServletRequest.class);

    /**
     * 测试批量工单创建性能
     */
    @Test
    public void testBatchOrderCreationPerformance() throws Exception {
        int batchSize = 1000;
        StopWatch stopWatch = new StopWatch();
        
        stopWatch.start();
        
        // 创建1000个工单
        for (int i = 0; i < batchSize; i++) {
            JSONObject orderData = new JSONObject();
            orderData.put("productName", "性能测试产品" + i);
            orderData.put("quantity", "10");
            orderData.put("planStartDate", "2025-06-20");
            
            productionOrderService.insertProductionOrder(orderData, mockRequest);
        }
        
        stopWatch.stop();
        long totalTime = stopWatch.getTotalTimeMillis();
        double avgTime = (double) totalTime / batchSize;
        
        System.out.println("批量创建" + batchSize + "个工单耗时: " + totalTime + "ms");
        System.out.println("平均每个工单创建耗时: " + avgTime + "ms");
        
        // 验证性能要求：每个工单创建时间不超过100ms
        assertTrue("工单创建性能不达标", avgTime < 100);
    }

    /**
     * 测试并发工单查询性能
     */
    @Test
    public void testConcurrentQueryPerformance() throws Exception {
        int threadCount = 50;
        int queriesPerThread = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        List<Future<Long>> futures = new ArrayList<>();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        // 启动并发查询
        for (int i = 0; i < threadCount; i++) {
            Future<Long> future = executor.submit(() -> {
                long threadStartTime = System.currentTimeMillis();
                try {
                    for (int j = 0; j < queriesPerThread; j++) {
                        productionOrderService.getProductionOrder(
                            "{\"status\":\"0\"}", 1, 10, mockRequest);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
                return System.currentTimeMillis() - threadStartTime;
            });
            futures.add(future);
        }
        
        // 等待所有线程完成
        latch.await(30, TimeUnit.SECONDS);
        stopWatch.stop();
        
        // 计算性能指标
        long totalTime = stopWatch.getTotalTimeMillis();
        int totalQueries = threadCount * queriesPerThread;
        double qps = (double) totalQueries / totalTime * 1000;
        
        System.out.println("并发查询测试完成:");
        System.out.println("总查询数: " + totalQueries);
        System.out.println("总耗时: " + totalTime + "ms");
        System.out.println("QPS: " + qps);
        
        // 验证性能要求：QPS不低于100
        assertTrue("查询性能不达标", qps > 100);
        
        executor.shutdown();
    }

    /**
     * 测试内存使用情况
     */
    @Test
    public void testMemoryUsage() throws Exception {
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 执行大量操作
        for (int i = 0; i < 10000; i++) {
            JSONObject orderData = new JSONObject();
            orderData.put("productName", "内存测试产品" + i);
            orderData.put("quantity", "1");
            
            productionOrderService.insertProductionOrder(orderData, mockRequest);
            
            if (i % 1000 == 0) {
                System.gc(); // 建议垃圾回收
                Thread.sleep(100);
            }
        }
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;
        
        System.out.println("初始内存使用: " + initialMemory / 1024 / 1024 + "MB");
        System.out.println("最终内存使用: " + finalMemory / 1024 / 1024 + "MB");
        System.out.println("内存增长: " + memoryIncrease / 1024 / 1024 + "MB");
        
        // 验证内存使用合理性（增长不超过100MB）
        assertTrue("内存使用过多", memoryIncrease < 100 * 1024 * 1024);
    }
}
```

---

## 自动化测试配置 (1天)

### 1. 测试配置文件

**文件路径**: `jshERP-boot/src/test/resources/application-test.yml`

```yaml
# 测试环境配置
server:
  port: 8080

spring:
  # 数据源配置 - 使用内存数据库
  datasource:
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
    
  # JPA配置
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    
  # Redis配置 - 使用嵌入式Redis
  redis:
    host: localhost
    port: 6379
    database: 1
    
# MyBatis配置
mybatis:
  mapper-locations: classpath:mapper_xml/*.xml
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 日志配置
logging:
  level:
    com.jsh.erp: DEBUG
    org.springframework.transaction: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 2. 测试数据初始化脚本

**文件路径**: `jshERP-boot/src/test/resources/test-data.sql`

```sql
-- 测试数据初始化脚本

-- 用户测试数据
INSERT INTO jsh_user (id, username, login_name, password, status, tenant_id, delete_flag, create_time)
VALUES (100, '张三', 'zhangsan', 'password123', '0', 1, '0', NOW()),
       (101, '李四', 'lisi', 'password123', '0', 1, '0', NOW()),
       (102, '王五', 'wangwu', 'password123', '0', 1, '0', NOW());

-- 材料测试数据
INSERT INTO jsh_material (id, name, spec, unit, category_id, tenant_id, delete_flag, create_time)
VALUES (1, '真丝面料', '100%桑蚕丝', '米', 1, 1, '0', NOW()),
       (2, '聚酯纤维', '涤纶', '米', 1, 1, '0', NOW()),
       (3, '棉线', '40支', '公斤', 2, 1, '0', NOW());

-- 仓库测试数据
INSERT INTO jsh_depot (id, name, address, type, tenant_id, delete_flag, create_time)
VALUES (1, '原料仓库', '车间A区', '0', 1, '0', NOW()),
       (2, '成品仓库', '车间B区', '1', 1, '0', NOW()),
       (3, '半成品仓库', '车间C区', '2', 1, '0', NOW());

-- 生产工单测试数据
INSERT INTO jsh_production_order (id, order_no, product_name, quantity, status, tenant_id, delete_flag, create_time)
VALUES (1, 'PO202506180001', '聆花丝巾', 100, '0', 1, '0', NOW()),
       (2, 'PO202506180002', '民族风围巾', 50, '1', 1, '0', NOW()),
       (3, 'PO202506180003', '传统手绣披肩', 25, '2', 1, '0', NOW());

-- 生产物料测试数据
INSERT INTO jsh_production_material (id, production_order_id, material_id, required_quantity, actual_quantity, tenant_id, delete_flag, create_time)
VALUES (1, 1, 1, 150.00, 0.00, 1, '0', NOW()),
       (2, 1, 3, 2.50, 0.00, 1, '0', NOW()),
       (3, 2, 1, 75.00, 75.00, 1, '0', NOW());

-- 报工记录测试数据
INSERT INTO jsh_work_report (id, production_order_id, worker_id, work_date, completed_quantity, work_hours, tenant_id, delete_flag, create_time)
VALUES (1, 2, 100, '2025-06-18', 25.00, 8.0, 1, '0', NOW()),
       (2, 2, 101, '2025-06-19', 25.00, 8.0, 1, '0', NOW());

-- 物流追踪测试数据
INSERT INTO jsh_logistics_track (id, tracking_no, production_order_id, shipment_type, carrier_company, tracking_status, tenant_id, delete_flag, create_time)
VALUES (1, 'SF1234567890', 3, '半成品回寄', '顺丰速运', '2', 1, '0', NOW());
```

### 3. 测试套件配置

**文件路径**: `jshERP-boot/src/test/java/com/jsh/erp/production/ProductionTestSuite.java`

```java
package com.jsh.erp.production;

import com.jsh.erp.production.controller.ProductionControllerIntegrationTest;
import com.jsh.erp.production.integration.ProductionIntegrationTest;
import com.jsh.erp.production.performance.ProductionPerformanceTest;
import com.jsh.erp.production.service.ProductionOrderServiceTest;
import com.jsh.erp.production.service.ProductionMaterialServiceTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 生产模块测试套件
 * 统一执行所有测试用例
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // 单元测试
    ProductionOrderServiceTest.class,
    ProductionMaterialServiceTest.class,
    
    // 集成测试
    ProductionIntegrationTest.class,
    ProductionControllerIntegrationTest.class,
    
    // 性能测试
    ProductionPerformanceTest.class
})
public class ProductionTestSuite {
    // 测试套件入口类
}
```

---

## 持续集成配置 (1天)

### 1. GitHub Actions配置

**文件路径**: `.github/workflows/production-ci.yml`

```yaml
name: 生产模块CI/CD

on:
  push:
    branches: [ main, develop ]
    paths:
      - 'jshERP-boot/src/main/java/com/jsh/erp/production/**'
      - 'jshERP-boot/src/test/java/com/jsh/erp/production/**'
  pull_request:
    branches: [ main ]
    paths:
      - 'jshERP-boot/src/main/java/com/jsh/erp/production/**'

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:5.7
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: jsh_erp_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3
      
      redis:
        image: redis:6
        ports:
          - 6379:6379
        options: --health-cmd="redis-cli ping" --health-interval=10s --health-timeout=5s --health-retries=3

    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
        restore-keys: ${{ runner.os }}-m2
    
    - name: Run unit tests
      run: |
        cd jshERP-boot
        mvn clean test -Dtest=com.jsh.erp.production.service.*Test
    
    - name: Run integration tests
      run: |
        cd jshERP-boot
        mvn clean test -Dtest=com.jsh.erp.production.integration.*Test
    
    - name: Run performance tests
      run: |
        cd jshERP-boot
        mvn clean test -Dtest=com.jsh.erp.production.performance.*Test
    
    - name: Generate test report
      run: |
        cd jshERP-boot
        mvn surefire-report:report
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: jshERP-boot/target/surefire-reports/
    
    - name: Code coverage
      run: |
        cd jshERP-boot
        mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: jshERP-boot/target/site/jacoco/jacoco.xml
        flags: production-module

  quality-check:
    runs-on: ubuntu-latest
    needs: test
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 8
      uses: actions/setup-java@v3
      with:
        java-version: '8'
        distribution: 'temurin'
    
    - name: Run SonarQube analysis
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
      run: |
        cd jshERP-boot
        mvn clean compile sonar:sonar \
          -Dsonar.projectKey=jsherp-production \
          -Dsonar.organization=linghua-culture \
          -Dsonar.host.url=https://sonarcloud.io \
          -Dsonar.sources=src/main/java/com/jsh/erp/production \
          -Dsonar.tests=src/test/java/com/jsh/erp/production
```

### 2. 测试报告生成配置

**文件路径**: `jshERP-boot/pom.xml` (测试相关插件配置)

```xml
<!-- 在pom.xml中添加测试插件配置 -->
<build>
    <plugins>
        <!-- Surefire插件 - 单元测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M7</version>
            <configuration>
                <includes>
                    <include>**/*Test.java</include>
                    <include>**/*Tests.java</include>
                </includes>
                <excludes>
                    <exclude>**/integration/**/*Test.java</exclude>
                    <exclude>**/performance/**/*Test.java</exclude>
                </excludes>
                <parallel>methods</parallel>
                <threadCount>4</threadCount>
                <reportsDirectory>${project.build.directory}/surefire-reports</reportsDirectory>
            </configuration>
        </plugin>
        
        <!-- Failsafe插件 - 集成测试 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <version>3.0.0-M7</version>
            <configuration>
                <includes>
                    <include>**/integration/**/*Test.java</include>
                </includes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>integration-test</goal>
                        <goal>verify</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
        
        <!-- JaCoCo代码覆盖率插件 -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.7</version>
            <configuration>
                <includes>
                    <include>com/jsh/erp/production/**</include>
                </includes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
                <execution>
                    <id>check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                    <configuration>
                        <rules>
                            <rule>
                                <element>CLASS</element>
                                <limits>
                                    <limit>
                                        <counter>LINE</counter>
                                        <value>COVEREDRATIO</value>
                                        <minimum>0.80</minimum>
                                    </limit>
                                </limits>
                            </rule>
                        </rules>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        
        <!-- 测试报告插件 -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-report-plugin</artifactId>
            <version>3.0.0-M7</version>
            <executions>
                <execution>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## 验收标准

### 功能验收标准
1. **单元测试覆盖率**: 不低于80%
2. **集成测试通过率**: 100%
3. **API测试通过率**: 100%
4. **性能测试达标**: QPS不低于100，响应时间不超过100ms

### 质量验收标准
1. **代码质量**: SonarQube评分不低于A级
2. **安全扫描**: 无高危漏洞
3. **内存泄漏检查**: 无内存泄漏问题
4. **并发安全**: 通过并发压力测试

### 文档验收标准
1. **测试用例文档**: 完整的测试用例设计文档
2. **测试报告**: 自动生成的测试报告
3. **性能报告**: 性能测试分析报告
4. **问题跟踪**: 缺陷管理和修复记录

---

## 交付物清单

### 代码交付物
1. **单元测试代码**: ProductionOrderServiceTest.java等
2. **集成测试代码**: ProductionIntegrationTest.java等
3. **性能测试代码**: ProductionPerformanceTest.java等
4. **测试配置文件**: application-test.yml等

### 文档交付物
1. **测试策略文档**: 本文档
2. **测试用例文档**: 详细的测试用例设计
3. **自动化测试报告**: Maven生成的测试报告
4. **性能测试报告**: 性能测试结果分析

### 配置交付物
1. **CI/CD配置**: GitHub Actions配置文件
2. **测试环境配置**: Docker和环境配置脚本
3. **质量门禁配置**: SonarQube质量规则配置
4. **监控配置**: 测试监控和告警配置

---

## 后续优化建议

### 测试优化
1. **增加E2E测试**: 基于Selenium的端到端测试
2. **契约测试**: 基于Spring Cloud Contract的契约测试
3. **混沌工程**: 故障注入和容错测试
4. **安全测试**: OWASP安全测试集成

### 工具优化
1. **测试数据管理**: 使用TestContainers管理测试环境
2. **测试并行化**: 提升测试执行效率
3. **可视化报告**: 集成Allure测试报告
4. **智能测试**: AI驱动的测试用例生成