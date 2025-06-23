package com.linghua.plugin.schedule.datasource.entities;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Schedule Calendar View Object
 * 日历排班视图对象
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class ScheduleCalendarVO {
    
    /** 日期 */
    private Date date;
    
    /** 日期字符串（YYYY-MM-DD格式） */
    private String dateStr;
    
    /** 是否是今天 */
    private Boolean isToday;
    
    /** 是否是工作日 */
    private Boolean isWorkday;
    
    /** 是否是周末 */
    private Boolean isWeekend;
    
    /** 是否是节假日 */
    private Boolean isHoliday;
    
    /** 该日期的排班记录列表 */
    private List<ScheduleAssignmentVO> assignments;
    
    /** 排班人数统计 */
    private Integer assignmentCount;
    
    /** 是否有排班冲突 */
    private Boolean hasConflict;
    
    /** 冲突信息 */
    private String conflictInfo;
    
    // ==================== 内部类：排班记录视图对象 ====================
    
    public static class ScheduleAssignmentVO {
        /** 排班记录ID */
        private Long id;
        
        /** 用户ID */
        private Long userId;
        
        /** 用户姓名 */
        private String userName;
        
        /** 班次ID */
        private Long shiftId;
        
        /** 班次名称 */
        private String shiftName;
        
        /** 班次类型 */
        private String shiftType;
        
        /** 班次颜色 */
        private String shiftColor;
        
        /** 班次时间 */
        private String shiftTime;
        
        /** 排班状态 */
        private String status;
        
        /** 状态显示名称 */
        private String statusName;
        
        /** 备注 */
        private String notes;
        
        // 构造函数
        public ScheduleAssignmentVO() {}
        
        public ScheduleAssignmentVO(Long userId, String userName, String shiftName) {
            this.userId = userId;
            this.userName = userName;
            this.shiftName = shiftName;
        }
        
        // Getter and Setter
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        
        public Long getShiftId() { return shiftId; }
        public void setShiftId(Long shiftId) { this.shiftId = shiftId; }
        
        public String getShiftName() { return shiftName; }
        public void setShiftName(String shiftName) { this.shiftName = shiftName; }
        
        public String getShiftType() { return shiftType; }
        public void setShiftType(String shiftType) { this.shiftType = shiftType; }
        
        public String getShiftColor() { return shiftColor; }
        public void setShiftColor(String shiftColor) { this.shiftColor = shiftColor; }
        
        public String getShiftTime() { return shiftTime; }
        public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getStatusName() { return statusName; }
        public void setStatusName(String statusName) { this.statusName = statusName; }
        
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
        
        /**
         * 获取显示标签
         */
        public String getDisplayLabel() {
            return userName + " - " + shiftName;
        }
    }
    
    // ==================== 构造函数 ====================
    
    public ScheduleCalendarVO() {
        this.assignments = new ArrayList<>();
    }
    
    public ScheduleCalendarVO(Date date) {
        this.date = date;
        this.assignments = new ArrayList<>();
    }
    
    // ==================== Getter and Setter ====================
    
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    public String getDateStr() {
        return dateStr;
    }
    
    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }
    
    public Boolean getIsToday() {
        return isToday;
    }
    
    public void setIsToday(Boolean isToday) {
        this.isToday = isToday;
    }
    
    public Boolean getIsWorkday() {
        return isWorkday;
    }
    
    public void setIsWorkday(Boolean isWorkday) {
        this.isWorkday = isWorkday;
    }
    
    public Boolean getIsWeekend() {
        return isWeekend;
    }
    
    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }
    
    public Boolean getIsHoliday() {
        return isHoliday;
    }
    
    public void setIsHoliday(Boolean isHoliday) {
        this.isHoliday = isHoliday;
    }
    
    public List<ScheduleAssignmentVO> getAssignments() {
        return assignments;
    }
    
    public void setAssignments(List<ScheduleAssignmentVO> assignments) {
        this.assignments = assignments;
        this.assignmentCount = assignments != null ? assignments.size() : 0;
    }
    
    public Integer getAssignmentCount() {
        return assignmentCount;
    }
    
    public void setAssignmentCount(Integer assignmentCount) {
        this.assignmentCount = assignmentCount;
    }
    
    public Boolean getHasConflict() {
        return hasConflict;
    }
    
    public void setHasConflict(Boolean hasConflict) {
        this.hasConflict = hasConflict;
    }
    
    public String getConflictInfo() {
        return conflictInfo;
    }
    
    public void setConflictInfo(String conflictInfo) {
        this.conflictInfo = conflictInfo;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 添加排班记录
     * 
     * @param assignment 排班记录
     */
    public void addAssignment(ScheduleAssignmentVO assignment) {
        if (this.assignments == null) {
            this.assignments = new ArrayList<>();
        }
        this.assignments.add(assignment);
        this.assignmentCount = this.assignments.size();
    }
    
    /**
     * 检查是否有排班
     * 
     * @return 是否有排班
     */
    public boolean hasAssignments() {
        return assignments != null && !assignments.isEmpty();
    }
    
    /**
     * 获取排班人员名单
     * 
     * @return 人员名单字符串
     */
    public String getAssignmentNames() {
        if (assignments == null || assignments.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < assignments.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(assignments.get(i).getUserName());
        }
        return sb.toString();
    }
    
    /**
     * 检查指定用户是否已排班
     * 
     * @param userId 用户ID
     * @return 是否已排班
     */
    public boolean isUserAssigned(Long userId) {
        if (assignments == null || userId == null) {
            return false;
        }
        
        return assignments.stream()
                .anyMatch(assignment -> userId.equals(assignment.getUserId()));
    }
    
    /**
     * 获取CSS类名（用于前端样式）
     * 
     * @return CSS类名
     */
    public String getCssClass() {
        StringBuilder sb = new StringBuilder();
        
        if (Boolean.TRUE.equals(isToday)) {
            sb.append("today ");
        }
        
        if (Boolean.TRUE.equals(isWeekend)) {
            sb.append("weekend ");
        }
        
        if (Boolean.TRUE.equals(isHoliday)) {
            sb.append("holiday ");
        }
        
        if (hasAssignments()) {
            sb.append("has-assignment ");
        }
        
        if (Boolean.TRUE.equals(hasConflict)) {
            sb.append("has-conflict ");
        }
        
        return sb.toString().trim();
    }
    
    @Override
    public String toString() {
        return "ScheduleCalendarVO{" +
                "dateStr='" + dateStr + '\'' +
                ", assignmentCount=" + assignmentCount +
                ", hasConflict=" + hasConflict +
                '}';
    }
}
