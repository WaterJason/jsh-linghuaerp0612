package com.linghua.plugin.schedule.constants;

/**
 * Schedule Constants
 * 排班相关常量定义
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class ScheduleConstants {
    
    // ==================== 通用常量 ====================
    
    /** 删除标记 - 存在 */
    public static final String DELETE_FLAG_EXISTS = "0";
    
    /** 删除标记 - 已删除 */
    public static final String DELETE_FLAG_DELETED = "1";
    
    // ==================== 班次相关常量 ====================
    
    /** 班次类型 - 全天班 */
    public static final String SHIFT_TYPE_FULL_DAY = "FULL_DAY";
    
    /** 班次类型 - 上午班 */
    public static final String SHIFT_TYPE_MORNING = "MORNING";
    
    /** 班次类型 - 下午班 */
    public static final String SHIFT_TYPE_AFTERNOON = "AFTERNOON";
    
    /** 班次类型 - 晚班 */
    public static final String SHIFT_TYPE_EVENING = "EVENING";
    
    /** 班次状态 - 启用 */
    public static final String SHIFT_STATUS_ACTIVE = "ACTIVE";
    
    /** 班次状态 - 禁用 */
    public static final String SHIFT_STATUS_INACTIVE = "INACTIVE";
    
    // ==================== 排班相关常量 ====================
    
    /** 排班状态 - 已排班 */
    public static final String ASSIGNMENT_STATUS_SCHEDULED = "SCHEDULED";
    
    /** 排班状态 - 已确认 */
    public static final String ASSIGNMENT_STATUS_CONFIRMED = "CONFIRMED";
    
    /** 排班状态 - 已取消 */
    public static final String ASSIGNMENT_STATUS_CANCELLED = "CANCELLED";
    
    /** 排班状态 - 已完成 */
    public static final String ASSIGNMENT_STATUS_COMPLETED = "COMPLETED";
    
    // ==================== 日历相关常量 ====================
    
    /** 日历视图类型 - 月视图 */
    public static final String CALENDAR_VIEW_MONTH = "month";
    
    /** 日历视图类型 - 周视图 */
    public static final String CALENDAR_VIEW_WEEK = "week";
    
    /** 日历视图类型 - 日视图 */
    public static final String CALENDAR_VIEW_DAY = "day";
    
    // ==================== 权限相关常量 ====================
    
    /** 功能权限 - 日历排班 */
    public static final String PERMISSION_CALENDAR_SCHEDULE = "calendar-schedule";
    
    /** 按钮权限 - 查看 */
    public static final String BUTTON_VIEW = "view";
    
    /** 按钮权限 - 新增 */
    public static final String BUTTON_ADD = "add";
    
    /** 按钮权限 - 编辑 */
    public static final String BUTTON_EDIT = "edit";
    
    /** 按钮权限 - 删除 */
    public static final String BUTTON_DELETE = "delete";
    
    /** 按钮权限 - 导出 */
    public static final String BUTTON_EXPORT = "export";
    
    /** 按钮权限 - 批量操作 */
    public static final String BUTTON_BATCH = "batch";
    
    // ==================== 缓存相关常量 ====================
    
    /** 缓存键前缀 - 班次 */
    public static final String CACHE_KEY_SHIFT = "calendar-schedule:shift:";
    
    /** 缓存键前缀 - 排班 */
    public static final String CACHE_KEY_ASSIGNMENT = "calendar-schedule:assignment:";
    
    /** 缓存键前缀 - 用户排班 */
    public static final String CACHE_KEY_USER_ASSIGNMENT = "calendar-schedule:user-assignment:";
    
    /** 缓存过期时间（秒） */
    public static final int CACHE_EXPIRE_TIME = 3600;
    
    // ==================== 错误码常量 ====================
    
    /** 错误码 - 班次不存在 */
    public static final String ERROR_SHIFT_NOT_FOUND = "SHIFT_NOT_FOUND";
    
    /** 错误码 - 排班冲突 */
    public static final String ERROR_ASSIGNMENT_CONFLICT = "ASSIGNMENT_CONFLICT";
    
    /** 错误码 - 用户不存在 */
    public static final String ERROR_USER_NOT_FOUND = "USER_NOT_FOUND";
    
    /** 错误码 - 权限不足 */
    public static final String ERROR_PERMISSION_DENIED = "PERMISSION_DENIED";
    
    // ==================== 私有构造函数 ====================
    
    private ScheduleConstants() {
        // 防止实例化
    }
}
