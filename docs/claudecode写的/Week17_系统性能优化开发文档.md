# Week 17: 系统性能优化开发文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第三阶段 - 优化增强
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第6、7章性能优化规范

---

## 概述

本文档为Week 17的系统性能优化提供详细的实施指导。主要实现数据库查询优化、Redis缓存策略、API响应时间优化、前端性能提升等核心功能，全面提升聆花文化ERP系统的运行效率和用户体验。

---

## 数据库优化 (2天)

### 1. 慢查询分析与优化

#### 慢查询监控脚本

**文件路径**: `scripts/monitor/slow_query_monitor.sh`

```bash
#!/bin/bash

# 慢查询监控脚本
# 用于分析和监控MySQL慢查询

LOG_FILE="/var/log/mysql/slow-query.log"
REPORT_FILE="/var/log/jshERP/slow_query_report_$(date +%Y%m%d).txt"

echo "=== 慢查询分析报告 $(date) ===" > $REPORT_FILE

# 1. 统计慢查询总数
echo "1. 慢查询统计:" >> $REPORT_FILE
echo "总慢查询数: $(grep -c "^# Time:" $LOG_FILE)" >> $REPORT_FILE
echo "" >> $REPORT_FILE

# 2. 分析最慢的10个查询
echo "2. 最慢的10个查询:" >> $REPORT_FILE
mysql -e "
SELECT 
    ROUND(AVG(query_time), 2) as avg_query_time,
    ROUND(AVG(lock_time), 2) as avg_lock_time,
    ROUND(AVG(rows_examined), 0) as avg_rows_examined,
    COUNT(*) as query_count,
    LEFT(sql_text, 100) as sql_sample
FROM mysql.slow_log 
WHERE start_time >= DATE_SUB(NOW(), INTERVAL 1 DAY)
GROUP BY LEFT(sql_text, 100)
ORDER BY avg_query_time DESC 
LIMIT 10;
" >> $REPORT_FILE

# 3. 分析涉及的表
echo "3. 涉及表统计:" >> $REPORT_FILE
grep -o "FROM \w\+" $LOG_FILE | sort | uniq -c | sort -nr | head -10 >> $REPORT_FILE

echo "慢查询分析完成: $REPORT_FILE"
```

#### 数据库索引优化SQL

**文件路径**: `scripts/sql/index_optimization.sql`

```sql
-- 数据库索引优化脚本
-- 基于慢查询分析结果创建必要的索引

-- 1. 生产模块索引优化
-- 生产工单表索引
CREATE INDEX idx_production_order_status_date ON jsh_production_order(order_status, create_time);
CREATE INDEX idx_production_order_worker ON jsh_production_order(assigned_worker_id, order_status);
CREATE INDEX idx_production_order_material ON jsh_production_order(material_id, create_time);

-- 报工记录表索引
CREATE INDEX idx_work_report_order_date ON jsh_work_report(production_order_id, report_date);
CREATE INDEX idx_work_report_worker_status ON jsh_work_report(worker_id, report_status);

-- 物流追踪表索引
CREATE INDEX idx_logistics_track_order ON jsh_logistics_track(production_order_id, tracking_status);
CREATE INDEX idx_logistics_track_date ON jsh_logistics_track(create_time, tracking_status);

-- 2. 团建模块索引优化
-- 团建活动表索引
CREATE INDEX idx_teambuilding_activity_date_status ON jsh_teambuilding_activity(activity_date, status);
CREATE INDEX idx_teambuilding_activity_instructor ON jsh_teambuilding_activity(instructor_id, status);
CREATE INDEX idx_teambuilding_activity_client ON jsh_teambuilding_activity(client_company, activity_date);

-- 参与人员表索引
CREATE INDEX idx_teambuilding_participant_activity ON jsh_teambuilding_participant(activity_id, participant_role);

-- 3. 薪酬模块索引优化
-- 薪酬核算表索引
CREATE INDEX idx_salary_calculation_period_employee ON jsh_salary_calculation(salary_period, employee_id);
CREATE INDEX idx_salary_calculation_status ON jsh_salary_calculation(calculation_status, salary_period);

-- 4. 非遗特色模块索引优化
-- 工艺知识库索引
CREATE INDEX idx_craft_knowledge_category_level ON jsh_craft_knowledge(craft_category, craft_level);
CREATE INDEX idx_craft_knowledge_master ON jsh_craft_knowledge(master_id, is_public);

-- 艺术品档案索引
CREATE INDEX idx_artwork_archive_type_date ON jsh_artwork_archive(artwork_type, creation_date);
CREATE INDEX idx_artwork_archive_creator ON jsh_artwork_archive(creator_name, artwork_category);

-- 5. 复合索引优化
-- 多租户查询优化
CREATE INDEX idx_production_order_tenant_complex ON jsh_production_order(tenant_id, delete_flag, order_status, create_time);
CREATE INDEX idx_teambuilding_activity_tenant_complex ON jsh_teambuilding_activity(tenant_id, delete_flag, status, activity_date);
CREATE INDEX idx_salary_calculation_tenant_complex ON jsh_salary_calculation(tenant_id, delete_flag, calculation_status, salary_period);

-- 6. 全文检索索引
-- 工艺知识搜索优化
ALTER TABLE jsh_craft_knowledge ADD FULLTEXT(craft_name, craft_description, teaching_points);

-- 艺术品档案搜索优化
ALTER TABLE jsh_artwork_archive ADD FULLTEXT(artwork_name, artwork_description, cultural_significance);

-- 7. 分区表优化（针对大数据量表）
-- 工作报告表按月分区
ALTER TABLE jsh_work_report PARTITION BY RANGE (YEAR(report_date)*100 + MONTH(report_date))
(
    PARTITION p202501 VALUES LESS THAN (202502),
    PARTITION p202502 VALUES LESS THAN (202503),
    PARTITION p202503 VALUES LESS THAN (202504),
    PARTITION p202504 VALUES LESS THAN (202505),
    PARTITION p202505 VALUES LESS THAN (202506),
    PARTITION p202506 VALUES LESS THAN (202507),
    PARTITION p202507 VALUES LESS THAN (202508),
    PARTITION p202508 VALUES LESS THAN (202509),
    PARTITION p202509 VALUES LESS THAN (202510),
    PARTITION p202510 VALUES LESS THAN (202511),
    PARTITION p202511 VALUES LESS THAN (202512),
    PARTITION p202512 VALUES LESS THAN (202513),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);

-- 8. 查询重写优化示例
-- 优化前的慢查询
/*
SELECT po.*, wr.total_hours, COUNT(wr.id) as report_count
FROM jsh_production_order po
LEFT JOIN jsh_work_report wr ON po.id = wr.production_order_id
WHERE po.tenant_id = 1 AND po.delete_flag = '0'
AND po.create_time >= '2025-01-01'
GROUP BY po.id
ORDER BY po.create_time DESC;
*/

-- 优化后的查询（使用索引和减少JOIN）
/*
SELECT po.*,
       (SELECT SUM(total_hours) FROM jsh_work_report wr 
        WHERE wr.production_order_id = po.id AND wr.delete_flag = '0') as total_hours,
       (SELECT COUNT(*) FROM jsh_work_report wr 
        WHERE wr.production_order_id = po.id AND wr.delete_flag = '0') as report_count
FROM jsh_production_order po
WHERE po.tenant_id = 1 AND po.delete_flag = '0'
AND po.create_time >= '2025-01-01'
ORDER BY po.create_time DESC;
*/

-- 9. 统计信息更新
ANALYZE TABLE jsh_production_order;
ANALYZE TABLE jsh_work_report;
ANALYZE TABLE jsh_logistics_track;
ANALYZE TABLE jsh_teambuilding_activity;
ANALYZE TABLE jsh_salary_calculation;
ANALYZE TABLE jsh_craft_knowledge;
ANALYZE TABLE jsh_artwork_archive;

-- 10. 查询缓存配置优化
SET GLOBAL query_cache_size = 268435456; -- 256MB
SET GLOBAL query_cache_type = ON;
SET GLOBAL query_cache_limit = 2097152; -- 2MB
```

### 2. Redis缓存策略实施

#### CacheService.java - 统一缓存服务

**文件路径**: `com.jsh.erp.service.cache.CacheService`

```java
package com.jsh.erp.service.cache;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.JshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * 统一缓存服务
 * 实现分层缓存策略和缓存管理
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class CacheService {
    private Logger logger = LoggerFactory.getLogger(CacheService.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // 缓存key前缀定义
    private static final String PRODUCTION_ORDER_PREFIX = "production:order:";
    private static final String TEAMBUILDING_ACTIVITY_PREFIX = "teambuilding:activity:";
    private static final String CRAFT_KNOWLEDGE_PREFIX = "craft:knowledge:";
    private static final String USER_SESSION_PREFIX = "user:session:";
    private static final String SYSTEM_CONFIG_PREFIX = "system:config:";
    
    // 缓存过期时间定义（秒）
    private static final long DEFAULT_EXPIRE = 3600; // 1小时
    private static final long USER_SESSION_EXPIRE = 1800; // 30分钟
    private static final long SYSTEM_CONFIG_EXPIRE = 86400; // 24小时
    private static final long HOT_DATA_EXPIRE = 300; // 5分钟

    /**
     * 缓存生产工单数据
     */
    public void cacheProductionOrder(String orderId, Object orderData, Long tenantId) {
        try {
            String cacheKey = buildCacheKey(PRODUCTION_ORDER_PREFIX, orderId, tenantId);
            redisTemplate.opsForValue().set(cacheKey, orderData, DEFAULT_EXPIRE, TimeUnit.SECONDS);
            
            // 同时缓存到热点数据集合
            String hotDataKey = "hot:production:orders:" + tenantId;
            redisTemplate.opsForZSet().add(hotDataKey, orderId, System.currentTimeMillis());
            redisTemplate.expire(hotDataKey, HOT_DATA_EXPIRE, TimeUnit.SECONDS);
            
            logger.debug("缓存生产工单: {}", cacheKey);
        } catch (Exception e) {
            logger.error("缓存生产工单失败: " + orderId, e);
        }
    }

    /**
     * 获取缓存的生产工单数据
     */
    public Object getCachedProductionOrder(String orderId, Long tenantId) {
        try {
            String cacheKey = buildCacheKey(PRODUCTION_ORDER_PREFIX, orderId, tenantId);
            Object cachedData = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedData != null) {
                // 更新热点数据访问时间
                String hotDataKey = "hot:production:orders:" + tenantId;
                redisTemplate.opsForZSet().add(hotDataKey, orderId, System.currentTimeMillis());
                
                logger.debug("命中缓存: {}", cacheKey);
            }
            
            return cachedData;
        } catch (Exception e) {
            logger.error("获取缓存生产工单失败: " + orderId, e);
            return null;
        }
    }

    /**
     * 缓存团建活动数据
     */
    public void cacheTeambuildingActivity(String activityId, Object activityData, Long tenantId) {
        try {
            String cacheKey = buildCacheKey(TEAMBUILDING_ACTIVITY_PREFIX, activityId, tenantId);
            redisTemplate.opsForValue().set(cacheKey, activityData, DEFAULT_EXPIRE, TimeUnit.SECONDS);
            
            logger.debug("缓存团建活动: {}", cacheKey);
        } catch (Exception e) {
            logger.error("缓存团建活动失败: " + activityId, e);
        }
    }

    /**
     * 获取缓存的团建活动数据
     */
    public Object getCachedTeambuildingActivity(String activityId, Long tenantId) {
        try {
            String cacheKey = buildCacheKey(TEAMBUILDING_ACTIVITY_PREFIX, activityId, tenantId);
            return redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            logger.error("获取缓存团建活动失败: " + activityId, e);
            return null;
        }
    }

    /**
     * 缓存工艺知识数据
     */
    public void cacheCraftKnowledge(String craftId, Object craftData, Long tenantId) {
        try {
            String cacheKey = buildCacheKey(CRAFT_KNOWLEDGE_PREFIX, craftId, tenantId);
            // 工艺知识相对稳定，使用较长的缓存时间
            redisTemplate.opsForValue().set(cacheKey, craftData, SYSTEM_CONFIG_EXPIRE, TimeUnit.SECONDS);
            
            logger.debug("缓存工艺知识: {}", cacheKey);
        } catch (Exception e) {
            logger.error("缓存工艺知识失败: " + craftId, e);
        }
    }

    /**
     * 批量缓存数据
     */
    public void batchCacheData(String prefix, java.util.Map<String, Object> dataMap, Long tenantId, long expireSeconds) {
        try {
            ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
            
            for (java.util.Map.Entry<String, Object> entry : dataMap.entrySet()) {
                String cacheKey = buildCacheKey(prefix, entry.getKey(), tenantId);
                valueOps.set(cacheKey, entry.getValue(), expireSeconds, TimeUnit.SECONDS);
            }
            
            logger.debug("批量缓存数据完成: {} 条记录", dataMap.size());
        } catch (Exception e) {
            logger.error("批量缓存数据失败", e);
        }
    }

    /**
     * 缓存预热
     * 在系统启动或低峰期预加载热点数据
     */
    public void warmupCache(Long tenantId) {
        try {
            logger.info("开始缓存预热: tenantId={}", tenantId);
            
            // 1. 预热系统配置
            warmupSystemConfig(tenantId);
            
            // 2. 预热用户会话数据
            warmupUserSessions(tenantId);
            
            // 3. 预热热点生产工单
            warmupHotProductionOrders(tenantId);
            
            // 4. 预热工艺知识
            warmupCraftKnowledge(tenantId);
            
            logger.info("缓存预热完成: tenantId={}", tenantId);
        } catch (Exception e) {
            logger.error("缓存预热失败: tenantId=" + tenantId, e);
        }
    }

    /**
     * 清理过期缓存
     */
    public void cleanupExpiredCache() {
        try {
            logger.info("开始清理过期缓存");
            
            // 获取所有缓存key
            Set<String> allKeys = redisTemplate.keys("*");
            int cleanedCount = 0;
            
            for (String key : allKeys) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl == -1) {
                    // 没有设置过期时间的key，根据规则设置过期时间
                    if (key.startsWith(USER_SESSION_PREFIX)) {
                        redisTemplate.expire(key, USER_SESSION_EXPIRE, TimeUnit.SECONDS);
                    } else if (key.startsWith(SYSTEM_CONFIG_PREFIX)) {
                        redisTemplate.expire(key, SYSTEM_CONFIG_EXPIRE, TimeUnit.SECONDS);
                    } else {
                        redisTemplate.expire(key, DEFAULT_EXPIRE, TimeUnit.SECONDS);
                    }
                    cleanedCount++;
                }
            }
            
            logger.info("过期缓存清理完成，处理了 {} 个key", cleanedCount);
        } catch (Exception e) {
            logger.error("清理过期缓存失败", e);
        }
    }

    /**
     * 获取缓存统计信息
     */
    public JSONObject getCacheStatistics() {
        try {
            JSONObject stats = new JSONObject();
            
            // 获取Redis信息
            Object redisInfo = redisTemplate.execute(connection -> connection.info());
            
            // 统计各类缓存数量
            stats.put("productionOrderCount", countCacheByPrefix(PRODUCTION_ORDER_PREFIX));
            stats.put("teambuildingActivityCount", countCacheByPrefix(TEAMBUILDING_ACTIVITY_PREFIX));
            stats.put("craftKnowledgeCount", countCacheByPrefix(CRAFT_KNOWLEDGE_PREFIX));
            stats.put("userSessionCount", countCacheByPrefix(USER_SESSION_PREFIX));
            stats.put("systemConfigCount", countCacheByPrefix(SYSTEM_CONFIG_PREFIX));
            
            // Redis内存使用情况
            stats.put("redisInfo", redisInfo);
            
            return stats;
        } catch (Exception e) {
            logger.error("获取缓存统计失败", e);
            return new JSONObject();
        }
    }

    /**
     * 删除指定模式的缓存
     */
    public void deleteCache(String pattern, Long tenantId) {
        try {
            String fullPattern = pattern + ":" + tenantId + ":*";
            Set<String> keys = redisTemplate.keys(fullPattern);
            
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                logger.info("删除缓存完成: pattern={}, count={}", fullPattern, keys.size());
            }
        } catch (Exception e) {
            logger.error("删除缓存失败: pattern=" + pattern, e);
        }
    }

    // 私有方法实现
    private String buildCacheKey(String prefix, String id, Long tenantId) {
        return prefix + tenantId + ":" + id;
    }

    private int countCacheByPrefix(String prefix) {
        try {
            Set<String> keys = redisTemplate.keys(prefix + "*");
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            logger.error("统计缓存数量失败: prefix=" + prefix, e);
            return 0;
        }
    }

    private void warmupSystemConfig(Long tenantId) {
        // 实现系统配置预热逻辑
        logger.debug("预热系统配置: tenantId={}", tenantId);
    }

    private void warmupUserSessions(Long tenantId) {
        // 实现用户会话预热逻辑
        logger.debug("预热用户会话: tenantId={}", tenantId);
    }

    private void warmupHotProductionOrders(Long tenantId) {
        // 实现热点生产工单预热逻辑
        logger.debug("预热热点生产工单: tenantId={}", tenantId);
    }

    private void warmupCraftKnowledge(Long tenantId) {
        // 实现工艺知识预热逻辑
        logger.debug("预热工艺知识: tenantId={}", tenantId);
    }
}
```

---

## 接口性能优化 (1.5天)

### 1. API响应时间优化

#### PerformanceInterceptor.java - 性能监控拦截器

**文件路径**: `com.jsh.erp.interceptor.PerformanceInterceptor`

```java
package com.jsh.erp.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 性能监控拦截器
 * 监控API响应时间和调用统计
 * 
 * @author jshERP
 * @version 1.0
 */
@Component
public class PerformanceInterceptor implements HandlerInterceptor {
    private Logger logger = LoggerFactory.getLogger(PerformanceInterceptor.class);
    
    // 性能统计数据
    private static final ConcurrentHashMap<String, AtomicLong> API_CALL_COUNT = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicLong> API_TOTAL_TIME = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> API_MAX_TIME = new ConcurrentHashMap<>();
    
    // 慢接口阈值（毫秒）
    private static final long SLOW_API_THRESHOLD = 1000;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 记录请求开始时间
        request.setAttribute("startTime", System.currentTimeMillis());
        
        // 记录请求信息
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String apiKey = method + " " + uri;
        
        request.setAttribute("apiKey", apiKey);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            // 计算响应时间
            Long startTime = (Long) request.getAttribute("startTime");
            String apiKey = (String) request.getAttribute("apiKey");
            
            if (startTime != null && apiKey != null) {
                long responseTime = System.currentTimeMillis() - startTime;
                
                // 更新统计数据
                updateApiStatistics(apiKey, responseTime);
                
                // 记录慢接口
                if (responseTime > SLOW_API_THRESHOLD) {
                    logSlowApi(apiKey, responseTime, request);
                }
                
                // 记录性能日志
                logger.debug("API性能监控: {} - {}ms", apiKey, responseTime);
            }
        } catch (Exception e) {
            logger.error("性能监控拦截器异常", e);
        }
    }

    /**
     * 更新API统计数据
     */
    private void updateApiStatistics(String apiKey, long responseTime) {
        // 调用次数
        API_CALL_COUNT.computeIfAbsent(apiKey, k -> new AtomicLong(0)).incrementAndGet();
        
        // 总响应时间
        API_TOTAL_TIME.computeIfAbsent(apiKey, k -> new AtomicLong(0)).addAndGet(responseTime);
        
        // 最大响应时间
        API_MAX_TIME.compute(apiKey, (k, v) -> v == null ? responseTime : Math.max(v, responseTime));
    }

    /**
     * 记录慢接口
     */
    private void logSlowApi(String apiKey, long responseTime, HttpServletRequest request) {
        JSONObject slowApiInfo = new JSONObject();
        slowApiInfo.put("api", apiKey);
        slowApiInfo.put("responseTime", responseTime);
        slowApiInfo.put("userAgent", request.getHeader("User-Agent"));
        slowApiInfo.put("ip", getClientIpAddress(request));
        slowApiInfo.put("timestamp", System.currentTimeMillis());
        
        logger.warn("慢接口监控: {}", slowApiInfo.toJSONString());
        
        // 可以发送到监控系统或保存到数据库
        sendToMonitoringSystem(slowApiInfo);
    }

    /**
     * 获取API性能统计
     */
    public JSONObject getApiPerformanceStatistics() {
        JSONObject statistics = new JSONObject();
        
        for (String apiKey : API_CALL_COUNT.keySet()) {
            JSONObject apiStats = new JSONObject();
            
            long callCount = API_CALL_COUNT.get(apiKey).get();
            long totalTime = API_TOTAL_TIME.get(apiKey).get();
            long maxTime = API_MAX_TIME.get(apiKey);
            
            apiStats.put("callCount", callCount);
            apiStats.put("totalTime", totalTime);
            apiStats.put("averageTime", callCount > 0 ? totalTime / callCount : 0);
            apiStats.put("maxTime", maxTime);
            
            statistics.put(apiKey, apiStats);
        }
        
        return statistics;
    }

    /**
     * 重置统计数据
     */
    public void resetStatistics() {
        API_CALL_COUNT.clear();
        API_TOTAL_TIME.clear();
        API_MAX_TIME.clear();
        logger.info("API性能统计数据已重置");
    }

    // 工具方法
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    private void sendToMonitoringSystem(JSONObject slowApiInfo) {
        // 发送到监控系统的实现
        // 可以是HTTP请求、消息队列等
        logger.debug("发送慢接口监控数据: {}", slowApiInfo.toJSONString());
    }
}
```

### 2. 分页查询优化

#### PageOptimizationService.java - 分页优化服务

**文件路径**: `com.jsh.erp.service.optimization.PageOptimizationService`

```java
package com.jsh.erp.service.optimization;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.exception.JshException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页查询优化服务
 * 实现深分页优化和缓存策略
 * 
 * @author jshERP
 * @version 1.0
 */
@Service
public class PageOptimizationService {
    private Logger logger = LoggerFactory.getLogger(PageOptimizationService.class);

    @Resource
    private CacheService cacheService;

    /**
     * 优化的分页查询
     * 使用游标分页替代OFFSET分页
     */
    public JSONObject optimizedPageQuery(String tableName, Map<String, Object> conditions, 
                                       String orderField, String cursorValue, int pageSize) {
        try {
            // 1. 构建缓存key
            String cacheKey = buildPageCacheKey(tableName, conditions, orderField, cursorValue, pageSize);
            
            // 2. 尝试从缓存获取
            Object cachedResult = cacheService.getCachedData(cacheKey);
            if (cachedResult != null) {
                logger.debug("命中分页缓存: {}", cacheKey);
                return (JSONObject) cachedResult;
            }

            // 3. 执行优化的查询
            JSONObject result = executeOptimizedQuery(tableName, conditions, orderField, cursorValue, pageSize);
            
            // 4. 缓存结果
            cacheService.cacheData(cacheKey, result, 300); // 缓存5分钟
            
            return result;
            
        } catch (Exception e) {
            logger.error("优化分页查询失败", e);
            throw new JshException("分页查询失败: " + e.getMessage());
        }
    }

    /**
     * 深分页优化查询
     * 对于大数据量的分页查询使用延迟关联优化
     */
    public JSONObject deepPageOptimization(String tableName, Map<String, Object> conditions, 
                                         int pageNumber, int pageSize) {
        try {
            // 1. 计算偏移量
            int offset = (pageNumber - 1) * pageSize;
            
            // 2. 对于深分页（offset > 10000）使用优化策略
            if (offset > 10000) {
                return executeDeepPageQuery(tableName, conditions, pageNumber, pageSize);
            } else {
                return executeNormalPageQuery(tableName, conditions, offset, pageSize);
            }
            
        } catch (Exception e) {
            logger.error("深分页优化失败", e);
            throw new JshException("深分页查询失败: " + e.getMessage());
        }
    }

    /**
     * 分页数据预加载
     * 在用户查看当前页时预加载下一页数据
     */
    public void preloadNextPage(String tableName, Map<String, Object> conditions, 
                               String orderField, String cursorValue, int pageSize) {
        try {
            // 异步预加载下一页数据
            new Thread(() -> {
                try {
                    String nextCursorValue = calculateNextCursor(cursorValue, pageSize);
                    optimizedPageQuery(tableName, conditions, orderField, nextCursorValue, pageSize);
                    logger.debug("预加载下一页完成: {} -> {}", cursorValue, nextCursorValue);
                } catch (Exception e) {
                    logger.error("预加载下一页失败", e);
                }
            }).start();
            
        } catch (Exception e) {
            logger.error("启动预加载失败", e);
        }
    }

    // 私有方法实现
    private String buildPageCacheKey(String tableName, Map<String, Object> conditions, 
                                   String orderField, String cursorValue, int pageSize) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("page:")
                  .append(tableName)
                  .append(":")
                  .append(orderField)
                  .append(":")
                  .append(cursorValue)
                  .append(":")
                  .append(pageSize)
                  .append(":")
                  .append(conditions.hashCode());
        
        return keyBuilder.toString();
    }

    private JSONObject executeOptimizedQuery(String tableName, Map<String, Object> conditions, 
                                           String orderField, String cursorValue, int pageSize) {
        // 实现游标分页查询逻辑
        JSONObject result = new JSONObject();
        
        // 构建WHERE条件
        StringBuilder whereClause = new StringBuilder("WHERE 1=1");
        if (cursorValue != null && !cursorValue.isEmpty()) {
            whereClause.append(" AND ").append(orderField).append(" > '").append(cursorValue).append("'");
        }
        
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            whereClause.append(" AND ").append(entry.getKey()).append(" = '").append(entry.getValue()).append("'");
        }
        
        // 构建SQL
        String sql = String.format(
            "SELECT * FROM %s %s ORDER BY %s LIMIT %d",
            tableName, whereClause.toString(), orderField, pageSize + 1
        );
        
        // 执行查询（这里简化实现，实际应该调用Mapper）
        // List<Object> data = mapper.selectBySql(sql);
        
        // 构建返回结果
        result.put("data", new Object[]{}); // 实际数据
        result.put("hasNext", false); // 是否有下一页
        result.put("nextCursor", ""); // 下一页游标
        
        return result;
    }

    private JSONObject executeDeepPageQuery(String tableName, Map<String, Object> conditions, 
                                          int pageNumber, int pageSize) {
        // 使用延迟关联优化深分页
        JSONObject result = new JSONObject();
        
        // 1. 先查询ID列表
        String idSql = String.format(
            "SELECT id FROM %s WHERE 1=1 ORDER BY id LIMIT %d, %d",
            tableName, (pageNumber - 1) * pageSize, pageSize
        );
        
        // 2. 根据ID列表查询完整数据
        String dataSql = String.format(
            "SELECT * FROM %s WHERE id IN (%s) ORDER BY id",
            tableName, "子查询结果"
        );
        
        // 执行查询并返回结果
        result.put("data", new Object[]{});
        result.put("total", 0);
        result.put("pageNumber", pageNumber);
        result.put("pageSize", pageSize);
        
        return result;
    }

    private JSONObject executeNormalPageQuery(String tableName, Map<String, Object> conditions, 
                                            int offset, int pageSize) {
        // 普通分页查询
        JSONObject result = new JSONObject();
        
        String sql = String.format(
            "SELECT * FROM %s WHERE 1=1 ORDER BY id LIMIT %d, %d",
            tableName, offset, pageSize
        );
        
        result.put("data", new Object[]{});
        result.put("offset", offset);
        result.put("pageSize", pageSize);
        
        return result;
    }

    private String calculateNextCursor(String currentCursor, int pageSize) {
        // 计算下一页游标的逻辑
        return currentCursor + "_next";
    }
}
```

---

## 前端性能优化 (0.5天)

### 1. 组件懒加载优化

#### LazyLoadDirective.js - 懒加载指令

**文件路径**: `jshERP-web/src/directives/lazy-load.js`

```javascript
/**
 * 懒加载指令
 * 实现组件和图片的懒加载功能
 */

// 创建IntersectionObserver实例
const createObserver = (callback) => {
  const options = {
    root: null,
    rootMargin: '50px',
    threshold: 0.1
  }
  
  return new IntersectionObserver(callback, options)
}

// 懒加载组件指令
export const vLazyComponent = {
  mounted(el, binding) {
    const componentLoader = binding.value
    
    const observer = createObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          // 进入视口，加载组件
          if (typeof componentLoader === 'function') {
            componentLoader()
          }
          observer.unobserve(el)
        }
      })
    })
    
    observer.observe(el)
    
    // 保存observer实例，便于清理
    el._lazyObserver = observer
  },
  
  unmounted(el) {
    if (el._lazyObserver) {
      el._lazyObserver.disconnect()
      delete el._lazyObserver
    }
  }
}

// 懒加载图片指令
export const vLazyImage = {
  mounted(el, binding) {
    const imageSrc = binding.value
    
    // 设置占位图
    el.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjEwMCUiIGhlaWdodD0iMTAwJSIgZmlsbD0iI2Y1ZjVmNSIvPgo8dGV4dCB4PSI1MCUiIHk9IjUwJSIgZHk9Ii4zZW0iIHRleHQtYW5jaG9yPSJtaWRkbGUiIGZpbGw9IiNjY2MiPkxvYWRpbmcuLi48L3RleHQ+Cjwvc3ZnPg=='
    
    const observer = createObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          // 预加载图片
          const img = new Image()
          img.onload = () => {
            el.src = imageSrc
            el.classList.add('lazy-loaded')
          }
          img.onerror = () => {
            el.src = '/static/images/error-placeholder.png'
            el.classList.add('lazy-error')
          }
          img.src = imageSrc
          
          observer.unobserve(el)
        }
      })
    })
    
    observer.observe(el)
    el._lazyObserver = observer
  },
  
  unmounted(el) {
    if (el._lazyObserver) {
      el._lazyObserver.disconnect()
      delete el._lazyObserver
    }
  }
}

// 懒加载表格数据指令
export const vLazyTable = {
  mounted(el, binding) {
    const { loadData, threshold = 100 } = binding.value
    
    let loading = false
    
    const handleScroll = () => {
      if (loading) return
      
      const { scrollTop, scrollHeight, clientHeight } = el
      
      // 距离底部小于阈值时加载数据
      if (scrollTop + clientHeight >= scrollHeight - threshold) {
        loading = true
        
        loadData().then(() => {
          loading = false
        }).catch(() => {
          loading = false
        })
      }
    }
    
    el.addEventListener('scroll', handleScroll)
    el._scrollHandler = handleScroll
  },
  
  unmounted(el) {
    if (el._scrollHandler) {
      el.removeEventListener('scroll', el._scrollHandler)
      delete el._scrollHandler
    }
  }
}
```

### 2. 静态资源优化

#### webpack.optimization.config.js - Webpack优化配置

**文件路径**: `jshERP-web/build/webpack.optimization.config.js`

```javascript
/**
 * Webpack性能优化配置
 */

const CompressionPlugin = require('compression-webpack-plugin')
const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')
const TerserPlugin = require('terser-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin')

module.exports = {
  // 代码分割优化
  optimization: {
    splitChunks: {
      chunks: 'all',
      cacheGroups: {
        // 第三方库分离
        vendor: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          priority: 10,
          chunks: 'all',
          minChunks: 1,
          reuseExistingChunk: true
        },
        
        // Ant Design单独分离
        antd: {
          test: /[\\/]node_modules[\\/](ant-design-vue|@ant-design)[\\/]/,
          name: 'antd',
          priority: 20,
          chunks: 'all',
          reuseExistingChunk: true
        },
        
        // Vue相关库分离
        vue: {
          test: /[\\/]node_modules[\\/](vue|vue-router|vuex)[\\/]/,
          name: 'vue',
          priority: 20,
          chunks: 'all',
          reuseExistingChunk: true
        },
        
        // 工具库分离
        utils: {
          test: /[\\/]node_modules[\\/](lodash|moment|axios)[\\/]/,
          name: 'utils',
          priority: 15,
          chunks: 'all',
          reuseExistingChunk: true
        },
        
        // 公共模块分离
        common: {
          name: 'common',
          minChunks: 2,
          priority: 5,
          chunks: 'all',
          reuseExistingChunk: true
        }
      }
    },
    
    // 运行时代码分离
    runtimeChunk: {
      name: 'runtime'
    },
    
    // 压缩优化
    minimizer: [
      // JavaScript压缩
      new TerserPlugin({
        parallel: true,
        terserOptions: {
          compress: {
            drop_console: process.env.NODE_ENV === 'production',
            drop_debugger: true,
            pure_funcs: ['console.log']
          },
          format: {
            comments: false
          }
        },
        extractComments: false
      }),
      
      // CSS压缩
      new CssMinimizerPlugin({
        minimizerOptions: {
          preset: [
            'default',
            {
              discardComments: { removeAll: true }
            }
          ]
        }
      })
    ]
  },
  
  // 插件配置
  plugins: [
    // CSS提取
    new MiniCssExtractPlugin({
      filename: 'css/[name].[contenthash:8].css',
      chunkFilename: 'css/[name].[contenthash:8].css'
    }),
    
    // Gzip压缩
    new CompressionPlugin({
      algorithm: 'gzip',
      test: /\.(js|css|html|svg)$/,
      threshold: 8192,
      minRatio: 0.8,
      deleteOriginalAssets: false
    }),
    
    // 包分析（开发环境）
    ...(process.env.ANALYZE ? [
      new BundleAnalyzerPlugin({
        analyzerMode: 'server',
        openAnalyzer: true
      })
    ] : [])
  ],
  
  // 模块解析优化
  resolve: {
    // 减少文件搜索范围
    modules: ['node_modules'],
    
    // 优先使用ES6模块
    mainFields: ['module', 'main'],
    
    // 缓存解析结果
    cache: true,
    
    // 别名配置
    alias: {
      '@': path.resolve(__dirname, '../src'),
      'vue$': 'vue/dist/vue.esm-bundler.js'
    }
  },
  
  // 缓存配置
  cache: {
    type: 'filesystem',
    buildDependencies: {
      config: [__filename]
    }
  },
  
  // 性能提示
  performance: {
    hints: 'warning',
    maxEntrypointSize: 500000,
    maxAssetSize: 300000
  }
}
```

---

## 验收标准

### 1. 数据库性能优化
- [ ] **查询性能提升**
  - [ ] 慢查询数量减少80%以上
  - [ ] 平均查询响应时间 < 100ms
  - [ ] 索引覆盖率 > 95%
  - [ ] 查询缓存命中率 > 80%

### 2. 缓存系统优化
- [ ] **缓存效果验证**
  - [ ] 热点数据缓存命中率 > 85%
  - [ ] API响应时间减少50%以上
  - [ ] 数据库连接数减少30%
  - [ ] 系统吞吐量提升40%

### 3. 接口性能优化
- [ ] **API响应优化**
  - [ ] 95%的API响应时间 < 500ms
  - [ ] 分页查询优化效果显著
  - [ ] 并发处理能力提升
  - [ ] 慢接口监控完善

### 4. 前端性能优化
- [ ] **用户体验提升**
  - [ ] 首屏加载时间 < 2秒
  - [ ] 页面切换响应 < 200ms
  - [ ] 资源压缩率 > 60%
  - [ ] 移动端性能优良

---

## 交付物清单

1. **数据库优化**
   - 慢查询监控脚本
   - 索引优化SQL文件
   - 分区表配置脚本

2. **缓存系统**
   - CacheService.java
   - Redis配置文件
   - 缓存预热脚本

3. **接口优化**
   - PerformanceInterceptor.java
   - PageOptimizationService.java
   - API监控工具

4. **前端优化**
   - 懒加载指令
   - Webpack优化配置
   - 静态资源优化脚本

5. **性能测试报告**
   - 压力测试结果
   - 性能对比分析
   - 优化效果验证

---

**文档结束**

> 本文档为Week 17系统性能优化的完整实施指南，通过数据库优化、缓存策略、接口优化和前端性能提升等多个维度，全面提升聆花文化ERP系统的运行效率。所有优化方案都经过精心设计，确保在提升性能的同时保持系统稳定性。