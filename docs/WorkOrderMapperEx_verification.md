# WorkOrderMapperEx.xml 验证文档

## 📋 概述

WorkOrderMapperEx.xml 文件已成功创建并配置，包含了 `selectWorkOrderListForKanban` 查询方法，用于崇左生产看板的数据获取。

## 🗄️ 文件位置

```
jshERP-boot/src/main/resources/mapper_xml/WorkOrderMapperEx.xml
```

## 🔧 核心查询方法

### selectWorkOrderListForKanban

**功能描述：**
- 查询看板工单列表，关联产品和用户信息
- 支持按状态和类型筛选
- 返回工单ID、工单号、产品名称、产品图片URL、负责人姓名和当前状态

**参数：**
- `status` (String) - 工单状态筛选
- `type` (String) - 工单类型筛选  
- `tenantId` (Long) - 租户ID（多租户数据隔离）

**返回字段：**
```java
Map<String, Object> {
    "id": 工单ID,
    "workOrderNumber": 工单号,
    "productionOrderId": 生产订单ID,
    "workType": 工单类型,
    "status": 状态,
    "handlerUserId": 处理人ID,
    "cost": 成本,
    "completionImageUrl": 完工图片URL,
    "logisticsNumber": 物流单号,
    "createTime": 创建时间,
    "updateTime": 更新时间,
    "materialName": 产品名称,
    "materialImgUrl": 产品图片URL,
    "materialStandard": 产品规格,
    "materialModel": 产品型号,
    "materialColor": 产品颜色,
    "handlerName": 处理人姓名
}
```

## 🔗 表关联关系

```sql
jsh_work_order (主表)
├── LEFT JOIN jsh_production_order (通过 production_order_id)
│   └── LEFT JOIN jsh_material (通过 material_id) 
└── LEFT JOIN jsh_user (通过 handler_user_id)
```

## 📊 查询特性

### 1. 智能排序
```sql
ORDER BY 
    CASE wo.status 
        WHEN 'PENDING' THEN 1      -- 待生产优先
        WHEN 'IN_PROGRESS' THEN 2  -- 生产中次之
        WHEN 'COMPLETED' THEN 3    -- 已完成最后
        ELSE 4
    END,
    wo.create_time DESC            -- 同状态按创建时间倒序
```

### 2. 多租户支持
- 自动过滤租户数据：`wo.tenant_id = #{tenantId}`
- 关联表也进行租户过滤

### 3. 逻辑删除过滤
- 主表：`IFNULL(wo.delete_flag, '0') != '1'`
- 关联表：`IFNULL(po.delete_flag, '0') != '1'`

### 4. 动态条件筛选
```xml
<!-- 状态筛选 -->
<if test="status != null and status != ''">
    AND wo.status = #{status}
</if>

<!-- 工单类型筛选 -->
<if test="type != null and type != ''">
    AND wo.work_type = #{type}
</if>
```

## 🧪 测试验证

### 1. SQL测试
```sql
-- 测试基础查询
SELECT 
    wo.id,
    wo.work_order_number,
    wo.work_type,
    wo.status,
    m.name AS material_name,
    u.username AS handler_name
FROM jsh_work_order wo
LEFT JOIN jsh_production_order po ON wo.production_order_id = po.id 
LEFT JOIN jsh_material m ON po.material_id = m.id 
LEFT JOIN jsh_user u ON wo.handler_user_id = u.id 
WHERE IFNULL(wo.delete_flag, '0') != '1'
LIMIT 5;
```

### 2. 状态筛选测试
```sql
-- 测试状态筛选
SELECT COUNT(*) FROM jsh_work_order WHERE status = 'PENDING';
SELECT COUNT(*) FROM jsh_work_order WHERE status = 'IN_PROGRESS';
SELECT COUNT(*) FROM jsh_work_order WHERE status = 'COMPLETED';
```

### 3. 类型筛选测试
```sql
-- 测试类型筛选
SELECT COUNT(*) FROM jsh_work_order WHERE work_type = 'CLOISONNE';
SELECT COUNT(*) FROM jsh_work_order WHERE work_type = 'ACCESSORY';
SELECT COUNT(*) FROM jsh_work_order WHERE work_type = 'POST_PROCESS';
```

## 🎯 接口调用示例

### Java Service层调用
```java
@Service
public class WorkOrderServiceImpl implements WorkOrderService {
    
    @Resource
    private WorkOrderMapperEx workOrderMapperEx;
    
    @Override
    public List<Map<String, Object>> getWorkOrderListForKanban(String status, String type) {
        Long tenantId = UserUtils.getCurrentTenantId();
        return workOrderMapperEx.selectWorkOrderListForKanban(status, type, tenantId);
    }
}
```

### Controller层调用
```java
@GetMapping("/kanbanList")
public String getKanbanList(@RequestParam(value = "status", required = false) String status,
                           @RequestParam(value = "type", required = false) String type) {
    List<Map<String, Object>> list = workOrderService.getWorkOrderListForKanban(status, type);
    Map<String, Object> result = new HashMap<>();
    result.put("rows", list);
    result.put("total", list.size());
    return returnJson(result, ErpInfo.OK.name, ErpInfo.OK.code);
}
```

## 📝 使用场景

### 1. 崇左生产看板
- 获取待生产工单：`status = "PENDING"`
- 获取生产中工单：`status = "IN_PROGRESS"`
- 获取已完成工单：`status = "COMPLETED"`

### 2. 工单类型筛选
- 掐丝点蓝工单：`type = "CLOISONNE"`
- 配饰制作工单：`type = "ACCESSORY"`
- 后工工单：`type = "POST_PROCESS"`

### 3. 综合筛选
- 生产中的掐丝点蓝工单：`status = "IN_PROGRESS", type = "CLOISONNE"`

## ⚠️ 注意事项

1. **数据完整性**：确保生产订单关联了正确的产品ID
2. **用户权限**：处理人必须是有效的用户
3. **多租户**：所有查询都会自动应用租户过滤
4. **性能优化**：建议在相关字段上建立索引

## 🔍 故障排除

### 问题1：产品信息为空
**原因**：生产订单没有关联产品或产品已被删除
**解决**：检查 jsh_production_order.material_id 字段

### 问题2：处理人姓名为空
**原因**：工单没有分配处理人或用户已被删除
**解决**：检查 jsh_work_order.handler_user_id 字段

### 问题3：查询结果为空
**原因**：租户ID不匹配或数据被逻辑删除
**解决**：检查 tenant_id 和 delete_flag 字段

## ✅ 验证清单

- [x] XML文件已创建在正确位置
- [x] namespace 与接口路径匹配
- [x] resultMap 正确映射所有字段
- [x] SQL查询语法正确
- [x] 表关联关系正确
- [x] 参数绑定正确
- [x] 多租户支持已实现
- [x] 逻辑删除过滤已实现
- [x] 动态条件筛选已实现
- [x] 排序逻辑已实现
