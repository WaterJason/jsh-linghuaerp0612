package com.linghua.plugin.schedule.utils;

import com.linghua.plugin.schedule.config.SchedulePluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Schedule Cache Utils
 * 排班缓存工具类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@Component
public class ScheduleCacheUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleCacheUtils.class);
    
    @Resource(name = "scheduleRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private SchedulePluginConfig pluginConfig;
    
    // ==================== 基础操作 ====================
    
    /**
     * 设置缓存
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        try {
            String fullKey = pluginConfig.getCacheKey(key);
            redisTemplate.opsForValue().set(fullKey, value, timeout, TimeUnit.SECONDS);
            logger.debug("设置缓存成功: key={}, timeout={}s", fullKey, timeout);
        } catch (Exception e) {
            logger.error("设置缓存失败: key={}", key, e);
        }
    }
    
    /**
     * 设置缓存（使用默认过期时间）
     * 
     * @param key 缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        set(key, value, pluginConfig.getCacheExpireTime());
    }
    
    /**
     * 获取缓存
     * 
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(String key) {
        try {
            String fullKey = pluginConfig.getCacheKey(key);
            Object value = redisTemplate.opsForValue().get(fullKey);
            logger.debug("获取缓存: key={}, found={}", fullKey, value != null);
            return value;
        } catch (Exception e) {
            logger.error("获取缓存失败: key={}", key, e);
            return null;
        }
    }
    
    /**
     * 获取缓存（指定类型）
     * 
     * @param key 缓存键
     * @param clazz 目标类型
     * @param <T> 泛型类型
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * 删除缓存
     * 
     * @param key 缓存键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        try {
            String fullKey = pluginConfig.getCacheKey(key);
            Boolean result = redisTemplate.delete(fullKey);
            logger.debug("删除缓存: key={}, result={}", fullKey, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("删除缓存失败: key={}", key, e);
            return false;
        }
    }
    
    /**
     * 批量删除缓存
     * 
     * @param keys 缓存键集合
     * @return 删除的数量
     */
    public long delete(Collection<String> keys) {
        try {
            if (keys == null || keys.isEmpty()) {
                return 0;
            }
            
            List<String> fullKeys = keys.stream()
                    .map(pluginConfig::getCacheKey)
                    .collect(java.util.stream.Collectors.toList());
            
            Long result = redisTemplate.delete(fullKeys);
            logger.debug("批量删除缓存: count={}, result={}", keys.size(), result);
            return result != null ? result : 0;
        } catch (Exception e) {
            logger.error("批量删除缓存失败: keys={}", keys, e);
            return 0;
        }
    }
    
    /**
     * 检查缓存是否存在
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            String fullKey = pluginConfig.getCacheKey(key);
            Boolean result = redisTemplate.hasKey(fullKey);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("检查缓存存在性失败: key={}", key, e);
            return false;
        }
    }
    
    /**
     * 设置过期时间
     * 
     * @param key 缓存键
     * @param timeout 过期时间（秒）
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout) {
        try {
            String fullKey = pluginConfig.getCacheKey(key);
            Boolean result = redisTemplate.expire(fullKey, timeout, TimeUnit.SECONDS);
            logger.debug("设置过期时间: key={}, timeout={}s, result={}", fullKey, timeout, result);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            logger.error("设置过期时间失败: key={}", key, e);
            return false;
        }
    }
    
    /**
     * 获取过期时间
     * 
     * @param key 缓存键
     * @return 过期时间（秒），-1表示永不过期，-2表示键不存在
     */
    public long getExpire(String key) {
        try {
            String fullKey = pluginConfig.getCacheKey(key);
            Long result = redisTemplate.getExpire(fullKey, TimeUnit.SECONDS);
            return result != null ? result : -2;
        } catch (Exception e) {
            logger.error("获取过期时间失败: key={}", key, e);
            return -2;
        }
    }
    
    // ==================== 业务缓存方法 ====================
    
    /**
     * 缓存班次列表
     * 
     * @param tenantId 租户ID
     * @param shifts 班次列表
     */
    public void cacheShiftList(Long tenantId, Object shifts) {
        String key = "shifts:list:" + tenantId;
        set(key, shifts, 1800); // 30分钟
    }
    
    /**
     * 获取班次列表缓存
     * 
     * @param tenantId 租户ID
     * @return 班次列表
     */
    public Object getShiftListCache(Long tenantId) {
        String key = "shifts:list:" + tenantId;
        return get(key);
    }
    
    /**
     * 清除班次相关缓存
     * 
     * @param tenantId 租户ID
     */
    public void clearShiftCache(Long tenantId) {
        String pattern = "shifts:*:" + tenantId;
        clearCacheByPattern(pattern);
    }
    
    /**
     * 缓存日历数据
     * 
     * @param tenantId 租户ID
     * @param year 年份
     * @param month 月份
     * @param calendarData 日历数据
     */
    public void cacheCalendarData(Long tenantId, Integer year, Integer month, Object calendarData) {
        String key = String.format("calendar:%d:%d:%d", tenantId, year, month);
        set(key, calendarData, 3600); // 1小时
    }
    
    /**
     * 获取日历数据缓存
     * 
     * @param tenantId 租户ID
     * @param year 年份
     * @param month 月份
     * @return 日历数据
     */
    public Object getCalendarDataCache(Long tenantId, Integer year, Integer month) {
        String key = String.format("calendar:%d:%d:%d", tenantId, year, month);
        return get(key);
    }
    
    /**
     * 清除日历相关缓存
     * 
     * @param tenantId 租户ID
     */
    public void clearCalendarCache(Long tenantId) {
        String pattern = "calendar:" + tenantId + ":*";
        clearCacheByPattern(pattern);
    }
    
    /**
     * 缓存用户排班数据
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param assignments 排班数据
     */
    public void cacheUserAssignments(Long tenantId, Long userId, String startDate, String endDate, Object assignments) {
        String key = String.format("assignments:user:%d:%d:%s:%s", tenantId, userId, startDate, endDate);
        set(key, assignments, 1800); // 30分钟
    }
    
    /**
     * 获取用户排班数据缓存
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班数据
     */
    public Object getUserAssignmentsCache(Long tenantId, Long userId, String startDate, String endDate) {
        String key = String.format("assignments:user:%d:%d:%s:%s", tenantId, userId, startDate, endDate);
        return get(key);
    }
    
    /**
     * 清除排班相关缓存
     * 
     * @param tenantId 租户ID
     */
    public void clearAssignmentCache(Long tenantId) {
        String pattern = "assignments:*:" + tenantId + ":*";
        clearCacheByPattern(pattern);
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 根据模式清除缓存
     * 
     * @param pattern 模式
     */
    private void clearCacheByPattern(String pattern) {
        try {
            String fullPattern = pluginConfig.getCacheKey(pattern);
            Set<String> keys = redisTemplate.keys(fullPattern);
            if (keys != null && !keys.isEmpty()) {
                Long result = redisTemplate.delete(keys);
                logger.debug("根据模式清除缓存: pattern={}, count={}", fullPattern, result);
            }
        } catch (Exception e) {
            logger.error("根据模式清除缓存失败: pattern={}", pattern, e);
        }
    }
    
    /**
     * 获取缓存统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getCacheStats() {
        try {
            String pattern = pluginConfig.getCacheKey("*");
            Set<String> keys = redisTemplate.keys(pattern);
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("totalKeys", keys != null ? keys.size() : 0);
            stats.put("keyPrefix", pluginConfig.getCacheKeyPrefix());
            stats.put("defaultExpireTime", pluginConfig.getCacheExpireTime());
            
            return stats;
        } catch (Exception e) {
            logger.error("获取缓存统计信息失败", e);
            return new java.util.HashMap<>();
        }
    }
}
