package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.constants.ExceptionConstants;
import com.jsh.erp.datasource.entities.*;
import com.jsh.erp.datasource.mappers.ProductionOrderMapper;
import com.jsh.erp.datasource.mappers.ProductionOrderMapperEx;
import com.jsh.erp.exception.BusinessRunTimeException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ProductionServiceImpl 单元测试类
 *
 * @author jshERP
 * @date 2025-06-21
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductionServiceImplTest {

    @Mock
    private ProductionOrderMapper productionOrderMapper;

    @Mock
    private ProductionOrderMapperEx productionOrderMapperEx;

    @Mock
    private UserService userService;

    @Mock
    private LogService logService;

    @Mock
    private DepotHeadService depotHeadService;

    @Mock
    private MaterialService materialService;

    @Mock
    private DepotService depotService;

    @Mock
    private DepotItemService depotItemService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ProductionServiceImpl productionService;

    private User mockUser;
    private DepotHead mockSalesOrder;
    private ProductionOrder mockProductionOrder;

    @Before
    public void setUp() {
        // 设置模拟用户
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setTenantId(1L);
        mockUser.setUsername("testuser");

        // 设置模拟销售订单
        mockSalesOrder = new DepotHead();
        mockSalesOrder.setId(1L);
        mockSalesOrder.setNumber("SO20250621001");
        mockSalesOrder.setOperTime(new Date());
        mockSalesOrder.setType(BusinessConstants.DEPOTHEAD_TYPE_OUT);
        mockSalesOrder.setSubType(BusinessConstants.SUB_TYPE_SALES);

        // 设置模拟生产订单
        mockProductionOrder = new ProductionOrder();
        mockProductionOrder.setId(1L);
        mockProductionOrder.setOrderNumber("PO20250621001");
        mockProductionOrder.setOriginalSaleOrderId(1L);
        mockProductionOrder.setMaterialId(1L);
        mockProductionOrder.setQuantity(new BigDecimal("5"));
        mockProductionOrder.setStatus("PENDING");
    }

    @Test
    public void testGetProductionOrder_Success() throws Exception {
        // Given
        Long productionOrderId = 1L;
        when(productionOrderMapper.selectByPrimaryKey(productionOrderId)).thenReturn(mockProductionOrder);

        // When
        ProductionOrder result = productionService.getProductionOrder(productionOrderId);

        // Then
        assertNotNull(result);
        assertEquals(mockProductionOrder.getId(), result.getId());
        assertEquals(mockProductionOrder.getOrderNumber(), result.getOrderNumber());
        verify(productionOrderMapper).selectByPrimaryKey(productionOrderId);
    }

    @Test
    public void testGetProductionOrder_NotFound() throws Exception {
        // Given
        Long productionOrderId = 999L;
        when(productionOrderMapper.selectByPrimaryKey(productionOrderId)).thenReturn(null);

        // When
        ProductionOrder result = productionService.getProductionOrder(productionOrderId);

        // Then
        assertNull(result);
        verify(productionOrderMapper).selectByPrimaryKey(productionOrderId);
    }

    @Test
    public void testGenerateFromSalesOrder_Success_StockSufficient() throws Exception {
        // Given
        Long salesOrderId = 1L;
        BigDecimal currentStock = new BigDecimal("10"); // 库存充足
        BigDecimal requiredQuantity = new BigDecimal("5");

        // Mock 依赖服务
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(depotHeadService.getDepotHead(salesOrderId)).thenReturn(mockSalesOrder);
        when(productionOrderMapper.insertSelective(any(ProductionOrder.class))).thenReturn(1);
        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);
        
        // Mock 生成订单号
        when(productionOrderMapperEx.findMaxOrderNoByPrefix(anyString())).thenReturn(null);

        // When
        Map<String, Object> result = productionService.generateFromSalesOrder(salesOrderId, request);

        // Then
        assertNotNull(result);
        assertEquals("生产订单创建成功", result.get("message"));
        assertTrue(result.get("stockCheckResult").toString().contains("底胎库存充足"));
        assertTrue(result.get("purchaseOrderResult").toString().contains("库存充足，无需创建采购订单"));
        assertTrue(result.get("transferOrderResult").toString().contains("已创建调拨单"));

        // 验证方法调用
        verify(depotHeadService).getDepotHead(salesOrderId);
        verify(productionOrderMapper).insertSelective(any(ProductionOrder.class));
        verify(materialService, times(3)).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
        verify(depotHeadService).addDepotHeadAndDetail(anyString(), anyString(), eq(request)); // 调拨单
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testGenerateFromSalesOrder_Success_StockInsufficient() throws Exception {
        // Given
        Long salesOrderId = 1L;
        BigDecimal currentStock = new BigDecimal("2"); // 库存不足
        BigDecimal requiredQuantity = new BigDecimal("5");

        // Mock 依赖服务
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(depotHeadService.getDepotHead(salesOrderId)).thenReturn(mockSalesOrder);
        when(productionOrderMapper.insertSelective(any(ProductionOrder.class))).thenReturn(1);
        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);
        
        // Mock 生成订单号
        when(productionOrderMapperEx.findMaxOrderNoByPrefix(anyString())).thenReturn(null);

        // When
        Map<String, Object> result = productionService.generateFromSalesOrder(salesOrderId, request);

        // Then
        assertNotNull(result);
        assertEquals("生产订单创建成功", result.get("message"));
        assertTrue(result.get("stockCheckResult").toString().contains("底胎库存不足"));
        assertTrue(result.get("purchaseOrderResult").toString().contains("已创建采购订单"));
        assertTrue(result.get("transferOrderResult").toString().contains("主仓库库存不足"));

        // 验证方法调用
        verify(depotHeadService).getDepotHead(salesOrderId);
        verify(productionOrderMapper).insertSelective(any(ProductionOrder.class));
        verify(materialService, times(3)).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
        verify(depotHeadService, times(1)).addDepotHeadAndDetail(anyString(), anyString(), eq(request)); // 只创建采购订单
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testGenerateFromSalesOrder_SalesOrderNotFound() throws Exception {
        // Given
        Long salesOrderId = 999L;
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(depotHeadService.getDepotHead(salesOrderId)).thenReturn(null);

        // When & Then
        try {
            productionService.generateFromSalesOrder(salesOrderId, request);
            fail("Expected BusinessRunTimeException to be thrown");
        } catch (BusinessRunTimeException exception) {
            assertEquals(ExceptionConstants.DATA_READ_FAIL_CODE, exception.getCode());
            assertEquals("销售订单不存在", exception.getMessage());
        }
        verify(depotHeadService).getDepotHead(salesOrderId);
        verify(productionOrderMapper, never()).insertSelective(any(ProductionOrder.class));
    }

    @Test
    public void testGenerateFromSalesOrder_ProductionOrderInsertFailed() throws Exception {
        // Given
        Long salesOrderId = 1L;
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(depotHeadService.getDepotHead(salesOrderId)).thenReturn(mockSalesOrder);
        when(productionOrderMapper.insertSelective(any(ProductionOrder.class))).thenReturn(0); // 插入失败
        when(productionOrderMapperEx.findMaxOrderNoByPrefix(anyString())).thenReturn(null);

        // When & Then
        try {
            productionService.generateFromSalesOrder(salesOrderId, request);
            fail("Expected BusinessRunTimeException to be thrown");
        } catch (BusinessRunTimeException exception) {
            assertEquals(ExceptionConstants.DATA_WRITE_FAIL_CODE, exception.getCode());
            assertEquals("生产订单创建失败", exception.getMessage());
        }
        verify(productionOrderMapper).insertSelective(any(ProductionOrder.class));
    }

    @Test
    public void testInsertProductionOrder_Success() throws Exception {
        // Given
        JSONObject obj = new JSONObject();
        obj.put("orderNumber", "PO20250621001");
        obj.put("materialId", 1L);
        obj.put("quantity", new BigDecimal("5"));
        obj.put("status", "PENDING");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapper.insertSelective(any(ProductionOrder.class))).thenReturn(1);

        // When
        int result = productionService.insertProductionOrder(obj, request);

        // Then
        assertEquals(1, result);
        verify(productionOrderMapper).insertSelective(any(ProductionOrder.class));
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testUpdateProductionOrder_Success() throws Exception {
        // Given
        JSONObject obj = new JSONObject();
        obj.put("id", 1L);
        obj.put("orderNumber", "PO20250621001");
        obj.put("status", "IN_PROGRESS");

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapper.updateByPrimaryKeySelective(any(ProductionOrder.class))).thenReturn(1);

        // When
        int result = productionService.updateProductionOrder(obj, request);

        // Then
        assertEquals(1, result);
        verify(productionOrderMapper).updateByPrimaryKeySelective(any(ProductionOrder.class));
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testDeleteProductionOrder_Success() throws Exception {
        // Given
        Long productionOrderId = 1L;
        when(productionOrderMapper.selectByPrimaryKey(productionOrderId)).thenReturn(mockProductionOrder);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapper.updateByPrimaryKeySelective(any(ProductionOrder.class))).thenReturn(1);

        // When
        int result = productionService.deleteProductionOrder(productionOrderId, request);

        // Then
        assertEquals(1, result);
        verify(productionOrderMapper).selectByPrimaryKey(productionOrderId);
        verify(productionOrderMapper).updateByPrimaryKeySelective(any(ProductionOrder.class));
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testDeleteProductionOrder_NotFound() throws Exception {
        // Given
        Long productionOrderId = 999L;
        when(productionOrderMapper.selectByPrimaryKey(productionOrderId)).thenReturn(null);

        // When & Then
        try {
            productionService.deleteProductionOrder(productionOrderId, request);
            fail("Expected BusinessRunTimeException to be thrown");
        } catch (BusinessRunTimeException exception) {
            assertEquals(ExceptionConstants.DATA_READ_FAIL_CODE, exception.getCode());
            assertEquals("生产订单不存在", exception.getMessage());
        }
        verify(productionOrderMapper).selectByPrimaryKey(productionOrderId);
        verify(productionOrderMapper, never()).updateByPrimaryKeySelective(any(ProductionOrder.class));
    }

    @Test
    public void testGenerateOrderNo_Success() throws Exception {
        // Given
        String datePrefix = "PO20250621";
        when(productionOrderMapperEx.findMaxOrderNoByPrefix(datePrefix)).thenReturn(null);

        // When
        String orderNo = productionService.generateOrderNumber();

        // Then
        assertNotNull(orderNo);
        assertTrue(orderNo.startsWith(datePrefix));
        assertTrue(orderNo.endsWith("0001"));
        verify(productionOrderMapperEx).findMaxOrderNoByPrefix(datePrefix);
    }

    @Test
    public void testGenerateOrderNo_WithExistingOrders() throws Exception {
        // Given
        String datePrefix = "PO20250621";
        String maxOrderNo = "PO202506210005";
        when(productionOrderMapperEx.findMaxOrderNoByPrefix(datePrefix)).thenReturn(maxOrderNo);

        // When
        String orderNo = productionService.generateOrderNumber();

        // Then
        assertNotNull(orderNo);
        assertTrue(orderNo.startsWith(datePrefix));
        assertTrue(orderNo.endsWith("0006"));
        verify(productionOrderMapperEx).findMaxOrderNoByPrefix(datePrefix);
    }

    @Test
    public void testCheckOrderNoExists_True() throws Exception {
        // Given
        String orderNo = "PO20250621001";
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.selectByOrderNo(orderNo, mockUser.getTenantId())).thenReturn(mockProductionOrder);

        // When
        boolean exists = productionService.checkOrderNoExists(orderNo);

        // Then
        assertTrue(exists);
        verify(productionOrderMapperEx).selectByOrderNo(orderNo, mockUser.getTenantId());
    }

    @Test
    public void testCheckOrderNoExists_False() throws Exception {
        // Given
        String orderNo = "PO20250621999";
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.selectByOrderNo(orderNo, mockUser.getTenantId())).thenReturn(null);

        // When
        boolean exists = productionService.checkOrderNoExists(orderNo);

        // Then
        assertFalse(exists);
        verify(productionOrderMapperEx).selectByOrderNo(orderNo, mockUser.getTenantId());
    }

    @Test
    public void testUpdateStatus_Success() throws Exception {
        // Given
        Long productionOrderId = 1L;
        String status = "IN_PROGRESS";
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.updateStatus(eq(productionOrderId), eq(status), eq(mockUser.getId()), any(Date.class))).thenReturn(1);

        // When
        int result = productionService.updateStatus(productionOrderId, status, request);

        // Then
        assertEquals(1, result);
        verify(productionOrderMapperEx).updateStatus(eq(productionOrderId), eq(status), eq(mockUser.getId()), any(Date.class));
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testGetStatistics_Success() throws Exception {
        // Given
        when(userService.getCurrentUser()).thenReturn(mockUser);
        
        List<Object> statusStats = Arrays.asList(
            createStatusStat("PENDING", 5),
            createStatusStat("IN_PROGRESS", 3),
            createStatusStat("COMPLETED", 2)
        );
        
        List<Object> costStats = Arrays.asList(
            createCostStat("PENDING", new BigDecimal("1000")),
            createCostStat("COMPLETED", new BigDecimal("2000"))
        );
        
        when(productionOrderMapperEx.getStatusStatistics(mockUser.getTenantId())).thenReturn(statusStats);
        when(productionOrderMapperEx.getCostStatistics(anyString(), anyString(), eq(mockUser.getTenantId()))).thenReturn(costStats);

        // When
        Map<String, Object> result = productionService.getStatistics();

        // Then
        assertNotNull(result);
        assertEquals(statusStats, result.get("statusStatistics"));
        assertEquals(costStats, result.get("costStatistics"));
        verify(productionOrderMapperEx).getStatusStatistics(mockUser.getTenantId());
        verify(productionOrderMapperEx).getCostStatistics(anyString(), anyString(), eq(mockUser.getTenantId()));
    }

    // 辅助方法：创建状态统计数据
    private Map<String, Object> createStatusStat(String status, int count) {
        Map<String, Object> stat = new HashMap<>();
        stat.put("status", status);
        stat.put("count", count);
        return stat;
    }

    // 辅助方法：创建成本统计数据
    private Map<String, Object> createCostStat(String status, BigDecimal cost) {
        Map<String, Object> stat = new HashMap<>();
        stat.put("status", status);
        stat.put("totalCost", cost);
        return stat;
    }

    /**
     * 测试库存检查方法 - 库存充足场景
     */
    @Test
    public void testCheckBaseStockAvailability_StockSufficient() throws Exception {
        // Given
        BigDecimal currentStock = new BigDecimal("10");
        BigDecimal requiredQuantity = new BigDecimal("5");
        mockProductionOrder.setQuantity(requiredQuantity);

        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);

        // When
        String result = ReflectionTestUtils.invokeMethod(productionService, "checkBaseStockAvailability", mockProductionOrder);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("底胎库存充足"));
        assertTrue(result.contains("当前库存：10"));
        assertTrue(result.contains("需要数量：5"));
        verify(materialService).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
    }

    @Test
    public void testCheckBaseStockAvailability_StockInsufficient() throws Exception {
        // Given
        BigDecimal currentStock = new BigDecimal("2");
        BigDecimal requiredQuantity = new BigDecimal("5");
        mockProductionOrder.setQuantity(requiredQuantity);

        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);

        // When
        String result = ReflectionTestUtils.invokeMethod(productionService, "checkBaseStockAvailability", mockProductionOrder);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("底胎库存不足"));
        assertTrue(result.contains("当前库存：2"));
        assertTrue(result.contains("需要数量：5"));
        assertTrue(result.contains("缺少：3"));
        verify(materialService).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
    }

    @Test
    public void testCreateServicePurchaseOrder_StockSufficient() throws Exception {
        // Given
        BigDecimal currentStock = new BigDecimal("10");
        BigDecimal requiredQuantity = new BigDecimal("5");
        mockProductionOrder.setQuantity(requiredQuantity);

        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);

        // When
        String result = ReflectionTestUtils.invokeMethod(productionService, "createServicePurchaseOrder", mockProductionOrder, request);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("库存充足，无需创建采购订单"));
        verify(materialService).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
        verify(depotHeadService, never()).addDepotHeadAndDetail(anyString(), anyString(), eq(request));
    }

    @Test
    public void testCreateServicePurchaseOrder_StockInsufficient() throws Exception {
        // Given
        BigDecimal currentStock = new BigDecimal("2");
        BigDecimal requiredQuantity = new BigDecimal("5");
        mockProductionOrder.setQuantity(requiredQuantity);

        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);

        // When
        String result = ReflectionTestUtils.invokeMethod(productionService, "createServicePurchaseOrder", mockProductionOrder, request);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("已创建采购订单"));
        assertTrue(result.contains("采购数量：3"));
        verify(materialService).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
        verify(depotHeadService).addDepotHeadAndDetail(anyString(), anyString(), eq(request));
    }

    @Test
    public void testCreateBaseTransferOrder_StockSufficient() throws Exception {
        // Given
        BigDecimal currentStock = new BigDecimal("10");
        BigDecimal transferQuantity = new BigDecimal("5");
        mockProductionOrder.setQuantity(transferQuantity);

        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);

        // When
        String result = ReflectionTestUtils.invokeMethod(productionService, "createBaseTransferOrder", mockProductionOrder, request);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("已创建调拨单"));
        assertTrue(result.contains("调拨数量：5"));
        assertTrue(result.contains("从主仓库调拨到崇左仓库"));
        verify(materialService).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
        verify(depotHeadService).addDepotHeadAndDetail(anyString(), anyString(), eq(request));
    }

    @Test
    public void testCreateBaseTransferOrder_StockInsufficient() throws Exception {
        // Given
        BigDecimal currentStock = new BigDecimal("2");
        BigDecimal transferQuantity = new BigDecimal("5");
        mockProductionOrder.setQuantity(transferQuantity);

        when(materialService.getCurrentStockByMaterialIdAndDepotId(1L, 1L)).thenReturn(currentStock);

        // When
        String result = ReflectionTestUtils.invokeMethod(productionService, "createBaseTransferOrder", mockProductionOrder, request);

        // Then
        assertNotNull(result);
        assertTrue(result.contains("主仓库库存不足，无法创建调拨单"));
        assertTrue(result.contains("当前库存：2"));
        assertTrue(result.contains("需要数量：5"));
        verify(materialService).getCurrentStockByMaterialIdAndDepotId(1L, 1L);
        verify(depotHeadService, never()).addDepotHeadAndDetail(anyString(), anyString(), eq(request));
    }

    @Test
    public void testBatchDeleteProductionOrder_Success() throws Exception {
        // Given
        String ids = "1,2,3";
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.batchDeleteByIds(any(Date.class), eq(mockUser.getId()), any(String[].class))).thenReturn(3);

        // When
        int result = productionService.batchDeleteProductionOrder(ids, request);

        // Then
        assertEquals(3, result);
        verify(productionOrderMapperEx).batchDeleteByIds(any(Date.class), eq(mockUser.getId()), any(String[].class));
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testGetExpiringSoonOrders_Success() throws Exception {
        // Given
        Integer days = 7;
        List<ProductionOrder> expiredOrders = Arrays.asList(mockProductionOrder);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.selectExpiringSoon(days, mockUser.getTenantId())).thenReturn(expiredOrders);

        // When
        List<ProductionOrder> result = productionService.getExpiringSoonOrders(days);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockProductionOrder.getId(), result.get(0).getId());
        verify(productionOrderMapperEx).selectExpiringSoon(days, mockUser.getTenantId());
    }

    @Test
    public void testUpdateCost_Success() throws Exception {
        // Given
        Long productionOrderId = 1L;
        String totalCost = "1000.00";
        String materialCost = "600.00";
        String laborCost = "400.00";

        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.updateCost(eq(productionOrderId), eq(totalCost), eq(materialCost),
                eq(laborCost), eq(mockUser.getId()), any(Date.class))).thenReturn(1);

        // When
        int result = productionService.updateCost(productionOrderId, totalCost, materialCost, laborCost, request);

        // Then
        assertEquals(1, result);
        verify(productionOrderMapperEx).updateCost(eq(productionOrderId), eq(totalCost), eq(materialCost),
                eq(laborCost), eq(mockUser.getId()), any(Date.class));
        verify(logService).insertLog(eq("生产管理"), anyString(), eq(request));
    }

    @Test
    public void testGetProductionOrdersBySalesOrderId_Success() throws Exception {
        // Given
        Long salesOrderId = 1L;
        List<ProductionOrder> orders = Arrays.asList(mockProductionOrder);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.selectBySalesOrderId(salesOrderId, mockUser.getTenantId())).thenReturn(orders);

        // When
        List<ProductionOrder> result = productionService.getProductionOrdersBySalesOrderId(salesOrderId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockProductionOrder.getId(), result.get(0).getId());
        verify(productionOrderMapperEx).selectBySalesOrderId(salesOrderId, mockUser.getTenantId());
    }

    @Test
    public void testGetProductionOrdersByMaterialId_Success() throws Exception {
        // Given
        Long materialId = 1L;
        List<ProductionOrder> orders = Arrays.asList(mockProductionOrder);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(productionOrderMapperEx.selectByMaterialId(materialId, mockUser.getTenantId())).thenReturn(orders);

        // When
        List<ProductionOrder> result = productionService.getProductionOrdersByMaterialId(materialId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockProductionOrder.getId(), result.get(0).getId());
        verify(productionOrderMapperEx).selectByMaterialId(materialId, mockUser.getTenantId());
    }
}
