package com.linghua.plugin.schedule.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Schedule Utils
 * 排班工具类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class ScheduleUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScheduleUtils.class);
    
    // ==================== 日期时间常量 ====================
    
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MONTH_FORMAT = "yyyy-MM";
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    
    // ==================== 日期时间工具方法 ====================
    
    /**
     * 获取当前日期字符串
     * 
     * @return 当前日期 (yyyy-MM-dd)
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
    
    /**
     * 获取当前时间字符串
     * 
     * @return 当前时间 (HH:mm:ss)
     */
    public static String getCurrentTime() {
        return LocalTime.now().format(TIME_FORMATTER);
    }
    
    /**
     * 获取当前日期时间字符串
     * 
     * @return 当前日期时间 (yyyy-MM-dd HH:mm:ss)
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * 格式化日期
     * 
     * @param date 日期
     * @return 格式化后的日期字符串
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * 格式化时间
     * 
     * @param date 日期时间
     * @return 格式化后的时间字符串
     */
    public static String formatTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * 格式化日期时间
     * 
     * @param date 日期时间
     * @return 格式化后的日期时间字符串
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * 解析日期字符串
     * 
     * @param dateStr 日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            logger.error("解析日期失败: {}", dateStr, e);
            return null;
        }
    }
    
    /**
     * 解析时间字符串
     * 
     * @param timeStr 时间字符串
     * @return 日期对象
     */
    public static Date parseTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
            return sdf.parse(timeStr);
        } catch (ParseException e) {
            logger.error("解析时间失败: {}", timeStr, e);
            return null;
        }
    }
    
    /**
     * 计算两个时间之间的小时数
     * 
     * @param startTime 开始时间 (HH:mm:ss)
     * @param endTime 结束时间 (HH:mm:ss)
     * @return 小时数
     */
    public static double calculateHours(String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        
        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
            LocalTime end = LocalTime.parse(endTime, TIME_FORMATTER);
            
            // 处理跨天情况
            if (end.isBefore(start)) {
                end = end.plusHours(24);
            }
            
            long minutes = ChronoUnit.MINUTES.between(start, end);
            return minutes / 60.0;
        } catch (Exception e) {
            logger.error("计算工作时长失败: startTime={}, endTime={}", startTime, endTime, e);
            return 0.0;
        }
    }
    
    /**
     * 获取月份的第一天
     * 
     * @param year 年份
     * @param month 月份
     * @return 第一天日期字符串
     */
    public static String getFirstDayOfMonth(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        return firstDay.format(DATE_FORMATTER);
    }
    
    /**
     * 获取月份的最后一天
     * 
     * @param year 年份
     * @param month 月份
     * @return 最后一天日期字符串
     */
    public static String getLastDayOfMonth(int year, int month) {
        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
        return lastDay.format(DATE_FORMATTER);
    }
    
    /**
     * 获取日期范围内的所有日期
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 日期列表
     */
    public static List<String> getDateRange(String startDate, String endDate) {
        List<String> dates = new ArrayList<>();
        
        try {
            LocalDate start = LocalDate.parse(startDate, DATE_FORMATTER);
            LocalDate end = LocalDate.parse(endDate, DATE_FORMATTER);
            
            LocalDate current = start;
            while (!current.isAfter(end)) {
                dates.add(current.format(DATE_FORMATTER));
                current = current.plusDays(1);
            }
        } catch (Exception e) {
            logger.error("获取日期范围失败: startDate={}, endDate={}", startDate, endDate, e);
        }
        
        return dates;
    }
    
    /**
     * 检查是否是周末
     * 
     * @param dateStr 日期字符串
     * @return 是否是周末
     */
    public static boolean isWeekend(String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            int dayOfWeek = date.getDayOfWeek().getValue();
            return dayOfWeek == 6 || dayOfWeek == 7; // 周六或周日
        } catch (Exception e) {
            logger.error("检查周末失败: {}", dateStr, e);
            return false;
        }
    }
    
    // ==================== 字符串工具方法 ====================
    
    /**
     * 检查字符串是否为空
     * 
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 检查字符串是否不为空
     * 
     * @param str 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 安全的字符串转换
     * 
     * @param obj 对象
     * @return 字符串
     */
    public static String safeToString(Object obj) {
        return obj != null ? obj.toString() : "";
    }
    
    /**
     * 生成随机字符串
     * 
     * @param length 长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return sb.toString();
    }
    
    // ==================== 验证工具方法 ====================
    
    /**
     * 验证日期格式
     * 
     * @param dateStr 日期字符串
     * @return 是否有效
     */
    public static boolean isValidDate(String dateStr) {
        if (isEmpty(dateStr)) {
            return false;
        }
        
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证时间格式
     * 
     * @param timeStr 时间字符串
     * @return 是否有效
     */
    public static boolean isValidTime(String timeStr) {
        if (isEmpty(timeStr)) {
            return false;
        }
        
        try {
            LocalTime.parse(timeStr, TIME_FORMATTER);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱
     * @return 是否有效
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return Pattern.matches(emailRegex, email);
    }
    
    /**
     * 验证手机号格式
     * 
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) {
            return false;
        }
        
        String phoneRegex = "^1[3-9]\\d{9}$";
        return Pattern.matches(phoneRegex, phone);
    }
    
    // ==================== 集合工具方法 ====================
    
    /**
     * 检查集合是否为空
     * 
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * 检查集合是否不为空
     * 
     * @param collection 集合
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 安全获取列表元素
     * 
     * @param list 列表
     * @param index 索引
     * @param <T> 元素类型
     * @return 元素或null
     */
    public static <T> T safeGet(List<T> list, int index) {
        if (list == null || index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }
    
    // ==================== 数值工具方法 ====================
    
    /**
     * 安全的Long转换
     * 
     * @param obj 对象
     * @param defaultValue 默认值
     * @return Long值
     */
    public static Long safeLong(Object obj, Long defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        
        try {
            if (obj instanceof Long) {
                return (Long) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).longValue();
            } else {
                return Long.parseLong(obj.toString());
            }
        } catch (Exception e) {
            logger.warn("Long转换失败: {}", obj, e);
            return defaultValue;
        }
    }
    
    /**
     * 安全的Integer转换
     * 
     * @param obj 对象
     * @param defaultValue 默认值
     * @return Integer值
     */
    public static Integer safeInteger(Object obj, Integer defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        
        try {
            if (obj instanceof Integer) {
                return (Integer) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).intValue();
            } else {
                return Integer.parseInt(obj.toString());
            }
        } catch (Exception e) {
            logger.warn("Integer转换失败: {}", obj, e);
            return defaultValue;
        }
    }
    
    /**
     * 安全的Double转换
     * 
     * @param obj 对象
     * @param defaultValue 默认值
     * @return Double值
     */
    public static Double safeDouble(Object obj, Double defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        
        try {
            if (obj instanceof Double) {
                return (Double) obj;
            } else if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            } else {
                return Double.parseDouble(obj.toString());
            }
        } catch (Exception e) {
            logger.warn("Double转换失败: {}", obj, e);
            return defaultValue;
        }
    }
}
