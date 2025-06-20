package com.linghua.plugin.schedule.config;

import com.gitee.starblues.annotation.Extract;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Schedule Plugin Configuration
 * 排班插件配置类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
@Configuration
@Extract
@ComponentScan(basePackages = "com.linghua.plugin.schedule")
@ConfigurationProperties(prefix = "calendar-schedule")
public class SchedulePluginConfig {
    
    // ==================== 业务配置属性 ====================
    
    /** 是否启用插件 */
    private boolean enable = true;
    
    /** 默认分页大小 */
    private int defaultPageSize = 20;
    
    /** 最大分页大小 */
    private int maxPageSize = 100;
    
    /** 缓存过期时间（秒） */
    private int cacheExpireTime = 3600;
    
    /** 缓存键前缀 */
    private String cacheKeyPrefix = "calendar-schedule:";
    
    /** 默认班次配置 */
    private DefaultShiftConfig defaultShift = new DefaultShiftConfig();
    
    /** 日历配置 */
    private CalendarConfig calendar = new CalendarConfig();
    
    // ==================== 内部配置类 ====================
    
    /**
     * 默认班次配置
     */
    public static class DefaultShiftConfig {
        private String name = "全天班";
        private String type = "FULL_DAY";
        private String startTime = "09:00";
        private String endTime = "18:00";
        private Double durationHours = 8.0;
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        
        public Double getDurationHours() { return durationHours; }
        public void setDurationHours(Double durationHours) { this.durationHours = durationHours; }
    }
    
    /**
     * 日历配置
     */
    public static class CalendarConfig {
        private String[] viewTypes = {"month", "week", "day"};
        private String defaultView = "month";
        private int[] workDays = {1, 2, 3, 4, 5, 6, 7};
        private boolean showWeekend = true;
        
        // Getters and Setters
        public String[] getViewTypes() { return viewTypes; }
        public void setViewTypes(String[] viewTypes) { this.viewTypes = viewTypes; }
        
        public String getDefaultView() { return defaultView; }
        public void setDefaultView(String defaultView) { this.defaultView = defaultView; }
        
        public int[] getWorkDays() { return workDays; }
        public void setWorkDays(int[] workDays) { this.workDays = workDays; }
        
        public boolean isShowWeekend() { return showWeekend; }
        public void setShowWeekend(boolean showWeekend) { this.showWeekend = showWeekend; }
    }
    
    // ==================== Bean配置 ====================
    
    /**
     * Redis模板配置
     * 
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate
     */
    @Bean("scheduleRedisTemplate")
    @Extract
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 设置序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        
        // Key序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // Value序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    // ==================== Getters and Setters ====================
    
    public boolean isEnable() {
        return enable;
    }
    
    public void setEnable(boolean enable) {
        this.enable = enable;
    }
    
    public int getDefaultPageSize() {
        return defaultPageSize;
    }
    
    public void setDefaultPageSize(int defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }
    
    public int getMaxPageSize() {
        return maxPageSize;
    }
    
    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }
    
    public int getCacheExpireTime() {
        return cacheExpireTime;
    }
    
    public void setCacheExpireTime(int cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
    }
    
    public String getCacheKeyPrefix() {
        return cacheKeyPrefix;
    }
    
    public void setCacheKeyPrefix(String cacheKeyPrefix) {
        this.cacheKeyPrefix = cacheKeyPrefix;
    }
    
    public DefaultShiftConfig getDefaultShift() {
        return defaultShift;
    }
    
    public void setDefaultShift(DefaultShiftConfig defaultShift) {
        this.defaultShift = defaultShift;
    }
    
    public CalendarConfig getCalendar() {
        return calendar;
    }
    
    public void setCalendar(CalendarConfig calendar) {
        this.calendar = calendar;
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 获取完整的缓存键
     * 
     * @param key 缓存键
     * @return 完整的缓存键
     */
    public String getCacheKey(String key) {
        return cacheKeyPrefix + key;
    }
    
    /**
     * 验证分页参数
     * 
     * @param pageSize 分页大小
     * @return 验证后的分页大小
     */
    public int validatePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return defaultPageSize;
        }
        return Math.min(pageSize, maxPageSize);
    }
    
    /**
     * 检查插件是否启用
     * 
     * @throws RuntimeException 如果插件未启用
     */
    public void checkPluginEnabled() {
        if (!enable) {
            throw new RuntimeException("日历排班插件未启用");
        }
    }
    
    @Override
    public String toString() {
        return "SchedulePluginConfig{" +
                "enable=" + enable +
                ", defaultPageSize=" + defaultPageSize +
                ", maxPageSize=" + maxPageSize +
                ", cacheExpireTime=" + cacheExpireTime +
                ", cacheKeyPrefix='" + cacheKeyPrefix + '\'' +
                '}';
    }
}
