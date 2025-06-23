# 掐丝珐琅馆模块与jshERP现有模块集成方案

## 📋 集成概述

掐丝珐琅馆模块作为jshERP的业务扩展模块，与现有核心模块深度集成，实现数据共享、业务协同和权限统一管理。

---

## 🔗 核心集成点

### 1. 与用户权限模块集成

#### 1.1 数据库关联设计
```sql
-- 排班管理关联用户表
ALTER TABLE `jsh_cloisonne_schedule` 
ADD CONSTRAINT `fk_schedule_employee` 
FOREIGN KEY (`employee_id`) REFERENCES `jsh_user`(`id`);

-- 咖啡店销售关联用户表
ALTER TABLE `jsh_cloisonne_coffee_sales` 
ADD CONSTRAINT `fk_coffee_recorder` 
FOREIGN KEY (`recorder_id`) REFERENCES `jsh_user`(`id`);

-- POS订单关联用户表
ALTER TABLE `jsh_cloisonne_pos_order` 
ADD CONSTRAINT `fk_pos_cashier` 
FOREIGN KEY (`cashier_id`) REFERENCES `jsh_user`(`id`);
```

#### 1.2 权限菜单集成
```sql
-- 在jsh_function表中添加掐丝珐琅馆菜单
INSERT INTO `jsh_function` (`number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) VALUES
-- 主菜单
('10', '掐丝珐琅馆', '0', '/cloisonne', '/layouts/TabLayout', '0', '10', '1', '电脑版', '', 'shop', '0'),
-- 子菜单
('1001', '总览仪表板', '10', '/cloisonne/dashboard', '/cloisonne/Dashboard', '0', '1001', '1', '电脑版', '1', 'dashboard', '0'),
('1002', '排班管理', '10', '/cloisonne/schedule', '/cloisonne/Schedule', '0', '1002', '1', '电脑版', '1,2,3,7', 'calendar', '0'),
('1003', '咖啡店管理', '10', '/cloisonne/coffee', '/cloisonne/Coffee', '0', '1003', '1', '电脑版', '1,2,3,7', 'coffee', '0'),
('1004', 'POS销售', '10', '/cloisonne/pos', '/cloisonne/POS', '0', '1004', '1', '电脑版', '1,2,3,7', 'shopping-cart', '0');

-- 角色权限分配示例
INSERT INTO `jsh_user_business` (`type`, `key_id`, `value`, `btn_str`, `tenant_id`, `delete_flag`) VALUES
('UserRole', '管理员角色ID', '10,1001,1002,1003,1004', '1,2,3,7', '租户ID', '0');
```

#### 1.3 用户信息集成服务
```java
package com.jsh.erp.service.cloisonne;

import com.jsh.erp.datasource.entities.User;
import com.jsh.erp.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 掐丝珐琅馆用户集成服务
 */
@Service
public class CloisonneUserIntegrationService {

    @Autowired
    private UserService userService;

    /**
     * 获取员工详细信息
     */
    public User getEmployeeInfo(Long employeeId, Long tenantId) {
        return userService.getUser(employeeId);
    }

    /**
     * 获取当前租户下的所有员工
     */
    public List<User> getAllEmployees(Long tenantId) {
        return userService.getUserListByTenantId(tenantId);
    }

    /**
     * 检查用户是否有掐丝珐琅馆模块权限
     */
    public boolean hasCloisonnePermission(Long userId, String functionNumber) {
        return userService.checkUserPermission(userId, functionNumber);
    }

    /**
     * 获取用户在掐丝珐琅馆模块的按钮权限
     */
    public String getUserButtonPermissions(Long userId, String functionNumber) {
        return userService.getUserButtonStr(userId, functionNumber);
    }
}
```

### 2. 与商品库存模块集成

#### 2.1 数据库关联设计
```sql
-- POS商品关联商品主表
ALTER TABLE `jsh_cloisonne_pos_product` 
ADD CONSTRAINT `fk_pos_product_material` 
FOREIGN KEY (`material_id`) REFERENCES `jsh_material`(`id`);

-- 添加库存同步字段
ALTER TABLE `jsh_cloisonne_pos_product` 
ADD COLUMN `sync_stock` TINYINT(1) DEFAULT 1 COMMENT '是否同步库存(0-否,1-是)';
```

#### 2.2 商品信息集成服务
```java
package com.jsh.erp.service.cloisonne;

import com.jsh.erp.datasource.entities.Material;
import com.jsh.erp.datasource.entities.MaterialExtend;
import com.jsh.erp.service.material.MaterialService;
import com.jsh.erp.service.materialExtend.MaterialExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 掐丝珐琅馆商品集成服务
 */
@Service
public class CloisonneMaterialIntegrationService {

    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private MaterialExtendService materialExtendService;

    /**
     * 从商品主表同步商品信息到POS商品表
     */
    public boolean syncMaterialToPOS(Long materialId, Long tenantId) {
        try {
            // 获取商品基本信息
            Material material = materialService.getMaterial(materialId);
            if (material == null || !material.getTenantId().equals(tenantId)) {
                return false;
            }

            // 获取商品扩展信息(价格、条码等)
            List<MaterialExtend> extendList = materialExtendService.getListByMaterialId(materialId);
            
            // 创建或更新POS商品记录
            for (MaterialExtend extend : extendList) {
                CloisonnePosProduct posProduct = new CloisonnePosProduct();
                posProduct.setMaterialId(materialId);
                posProduct.setProductCode(extend.getBarCode());
                posProduct.setProductName(material.getName());
                posProduct.setPrice(extend.getCommodityDecimal());
                posProduct.setOriginalPrice(extend.getCommodityDecimal());
                posProduct.setCostPrice(extend.getPurchaseDecimal());
                posProduct.setUnit(extend.getCommodityUnit());
                posProduct.setTenantId(tenantId);
                
                // 保存到POS商品表
                cloisonnePosProductService.saveOrUpdate(posProduct);
            }
            
            return true;
        } catch (Exception e) {
            logger.error("同步商品到POS失败", e);
            return false;
        }
    }

    /**
     * 销售后更新库存
     */
    public boolean updateStockAfterSale(Long materialId, String sku, Integer quantity, Long tenantId) {
        try {
            // 调用库存服务更新库存
            return materialService.updateStock(materialId, sku, -quantity, tenantId);
        } catch (Exception e) {
            logger.error("更新库存失败", e);
            return false;
        }
    }

    /**
     * 获取商品当前库存
     */
    public Integer getCurrentStock(Long materialId, String sku, Long tenantId) {
        return materialService.getCurrentStock(materialId, sku, tenantId);
    }

    /**
     * 批量同步商品信息
     */
    public boolean batchSyncMaterials(List<Long> materialIds, Long tenantId) {
        boolean allSuccess = true;
        for (Long materialId : materialIds) {
            if (!syncMaterialToPOS(materialId, tenantId)) {
                allSuccess = false;
            }
        }
        return allSuccess;
    }
}
```

### 3. 与财务模块集成

#### 3.1 财务记录自动生成
```java
package com.jsh.erp.service.cloisonne;

import com.jsh.erp.datasource.entities.AccountHead;
import com.jsh.erp.datasource.entities.AccountItem;
import com.jsh.erp.service.account.AccountHeadService;
import com.jsh.erp.service.account.AccountItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 掐丝珐琅馆财务集成服务
 */
@Service
public class CloisonneFinanceIntegrationService {

    @Autowired
    private AccountHeadService accountHeadService;
    
    @Autowired
    private AccountItemService accountItemService;

    /**
     * 咖啡店销售生成财务收入记录
     */
    @Transactional
    public boolean generateCoffeeSalesFinanceRecord(CloisonneCoffeeSales coffeeSales) {
        try {
            // 创建财务主表记录
            AccountHead accountHead = new AccountHead();
            accountHead.setType("收入");
            accountHead.setBillNo(generateBillNo("COFFEE"));
            accountHead.setBillTime(coffeeSales.getCreateTime());
            accountHead.setHandsPersonId(coffeeSales.getRecorderId());
            accountHead.setChangeAmount(coffeeSales.getRevenue());
            accountHead.setTotalPrice(coffeeSales.getRevenue());
            accountHead.setRemark("咖啡店销售收入 - " + coffeeSales.getSalesDate());
            accountHead.setTenantId(coffeeSales.getTenantId());
            
            // 保存财务主表
            accountHeadService.save(accountHead);
            
            // 创建财务明细记录
            AccountItem accountItem = new AccountItem();
            accountItem.setHeaderId(accountHead.getId());
            accountItem.setAccountId(getDefaultAccountId(coffeeSales.getTenantId()));
            accountItem.setEachAmount(coffeeSales.getRevenue());
            accountItem.setRemark("咖啡店日销售收入");
            accountItem.setTenantId(coffeeSales.getTenantId());
            
            // 保存财务明细
            accountItemService.save(accountItem);
            
            return true;
        } catch (Exception e) {
            logger.error("生成咖啡店销售财务记录失败", e);
            throw new RuntimeException("财务记录生成失败", e);
        }
    }

    /**
     * POS销售生成财务收入记录
     */
    @Transactional
    public boolean generatePOSSalesFinanceRecord(CloisonnePosOrder posOrder) {
        try {
            // 创建财务主表记录
            AccountHead accountHead = new AccountHead();
            accountHead.setType("收入");
            accountHead.setBillNo(generateBillNo("POS"));
            accountHead.setBillTime(posOrder.getOrderTime());
            accountHead.setHandsPersonId(posOrder.getCashierId());
            accountHead.setChangeAmount(posOrder.getActualAmount());
            accountHead.setTotalPrice(posOrder.getTotalAmount());
            accountHead.setRemark("POS销售收入 - 订单号:" + posOrder.getOrderNo());
            accountHead.setTenantId(posOrder.getTenantId());
            
            // 保存财务主表
            accountHeadService.save(accountHead);
            
            // 创建财务明细记录
            AccountItem accountItem = new AccountItem();
            accountItem.setHeaderId(accountHead.getId());
            accountItem.setAccountId(getAccountByPaymentMethod(posOrder.getPaymentMethod(), posOrder.getTenantId()));
            accountItem.setEachAmount(posOrder.getActualAmount());
            accountItem.setRemark("POS销售收入 - " + posOrder.getPaymentMethod());
            accountItem.setTenantId(posOrder.getTenantId());
            
            // 保存财务明细
            accountItemService.save(accountItem);
            
            return true;
        } catch (Exception e) {
            logger.error("生成POS销售财务记录失败", e);
            throw new RuntimeException("财务记录生成失败", e);
        }
    }

    /**
     * 根据支付方式获取对应账户
     */
    private Long getAccountByPaymentMethod(String paymentMethod, Long tenantId) {
        switch (paymentMethod) {
            case "cash":
                return getCashAccountId(tenantId);
            case "alipay":
                return getAlipayAccountId(tenantId);
            case "wechat":
                return getWechatAccountId(tenantId);
            case "card":
                return getBankAccountId(tenantId);
            default:
                return getDefaultAccountId(tenantId);
        }
    }

    /**
     * 生成单据编号
     */
    private String generateBillNo(String prefix) {
        return prefix + System.currentTimeMillis();
    }
}
```

### 4. 业务流程集成示例

#### 4.1 POS销售完整业务流程
```java
package com.jsh.erp.service.cloisonne.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * POS销售业务集成实现
 */
@Service
public class CloisonnePOSIntegratedService {

    @Autowired
    private CloisonnePosOrderService posOrderService;
    
    @Autowired
    private CloisonneMaterialIntegrationService materialIntegrationService;
    
    @Autowired
    private CloisonneFinanceIntegrationService financeIntegrationService;

    /**
     * 完整的POS销售流程
     * 1. 创建订单
     * 2. 更新库存
     * 3. 生成财务记录
     */
    @Transactional
    public boolean processPOSSale(CloisonnePosOrder order, List<CloisonnePosOrderItem> items) {
        try {
            // 1. 保存订单主表
            posOrderService.save(order);
            
            // 2. 保存订单明细并更新库存
            for (CloisonnePosOrderItem item : items) {
                item.setOrderId(order.getId());
                posOrderItemService.save(item);
                
                // 更新商品库存
                if (!materialIntegrationService.updateStockAfterSale(
                    item.getProductId(), 
                    item.getProductCode(), 
                    item.getQuantity(), 
                    order.getTenantId())) {
                    throw new RuntimeException("库存更新失败");
                }
            }
            
            // 3. 生成财务记录
            if (!financeIntegrationService.generatePOSSalesFinanceRecord(order)) {
                throw new RuntimeException("财务记录生成失败");
            }
            
            // 4. 记录操作日志
            logService.insertLog("POS销售", 
                "创建订单:" + order.getOrderNo() + ",金额:" + order.getActualAmount(),
                order.getCashierId(), order.getTenantId());
            
            return true;
        } catch (Exception e) {
            logger.error("POS销售流程处理失败", e);
            throw new RuntimeException("POS销售处理失败", e);
        }
    }
}
```

---

## 🔧 集成配置

### 1. Spring配置
```java
@Configuration
@ComponentScan("com.jsh.erp.service.cloisonne")
public class CloisonneModuleConfig {
    
    /**
     * 掐丝珐琅馆模块集成服务配置
     */
    @Bean
    public CloisonneIntegrationService cloisonneIntegrationService() {
        return new CloisonneIntegrationService();
    }
}
```

### 2. MyBatis配置
```xml
<!-- 在mybatis-config.xml中添加 -->
<mappers>
    <mapper resource="mapper_xml/cloisonne/CloisonneScheduleMapper.xml"/>
    <mapper resource="mapper_xml/cloisonne/CloisonneCoffeeSalesMapper.xml"/>
    <mapper resource="mapper_xml/cloisonne/CloisonnePosOrderMapper.xml"/>
</mappers>
```

---

## 📊 数据流向图

```
用户登录 → 权限验证(jsh_function) → 掐丝珐琅馆模块
    ↓
排班管理 → 关联员工信息(jsh_user) → 生成排班记录
    ↓
咖啡店销售 → 录入销售数据 → 生成财务记录(jsh_account_head/item)
    ↓
POS销售 → 选择商品(jsh_material) → 更新库存 → 生成订单 → 财务记录
```

---

## ✅ 集成验证清单

- [ ] 用户权限正确关联
- [ ] 商品信息同步正常
- [ ] 库存更新准确
- [ ] 财务记录生成正确
- [ ] 多租户数据隔离
- [ ] 软删除机制正常
- [ ] 操作日志记录完整
- [ ] 事务回滚机制有效

---

*集成方案版本: v1.0*
*最后更新: 2025-01-22*
