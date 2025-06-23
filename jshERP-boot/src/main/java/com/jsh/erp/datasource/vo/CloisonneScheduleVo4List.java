package com.jsh.erp.datasource.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 掐丝珐琅馆排班列表VO
 * 
 * @author jshERP
 * @since 2025-01-22
 */
public class CloisonneScheduleVo4List implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 排班日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 员工角色
     */
    private String employeeRole;

    /**
     * 员工头像
     */
    private String employeeAvatar;

    /**
     * 员工电话
     */
    private String employeePhone;

    /**
     * 班次类型
     */
    private String shiftType;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    /**
     * 工作时长
     */
    private BigDecimal workHours;

    /**
     * 工作区域
     */
    private String workArea;

    /**
     * 状态
     */
    private String status;

    /**
     * 状态显示名称
     */
    private String statusName;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 优先级显示名称
     */
    private String priorityName;

    /**
     * 备注信息
     */
    private String notes;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 星期几(1-7，1表示周一)
     */
    private Integer dayOfWeek;

    /**
     * 星期几显示名称
     */
    private String dayOfWeekName;

    /**
     * 是否是今天
     */
    private Boolean isToday;

    /**
     * 是否是周末
     */
    private Boolean isWeekend;

    // 构造函数
    public CloisonneScheduleVo4List() {}

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getEmployeeAvatar() {
        return employeeAvatar;
    }

    public void setEmployeeAvatar(String employeeAvatar) {
        this.employeeAvatar = employeeAvatar;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public void setPriorityName(String priorityName) {
        this.priorityName = priorityName;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfWeekName() {
        return dayOfWeekName;
    }

    public void setDayOfWeekName(String dayOfWeekName) {
        this.dayOfWeekName = dayOfWeekName;
    }

    public Boolean getIsToday() {
        return isToday;
    }

    public void setIsToday(Boolean isToday) {
        this.isToday = isToday;
    }

    public Boolean getIsWeekend() {
        return isWeekend;
    }

    public void setIsWeekend(Boolean isWeekend) {
        this.isWeekend = isWeekend;
    }

    @Override
    public String toString() {
        return "CloisonneScheduleVo4List{" +
                "id=" + id +
                ", scheduleDate=" + scheduleDate +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", employeeRole='" + employeeRole + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", workHours=" + workHours +
                ", workArea='" + workArea + '\'' +
                ", status='" + status + '\'' +
                ", statusName='" + statusName + '\'' +
                ", priority='" + priority + '\'' +
                ", priorityName='" + priorityName + '\'' +
                ", notes='" + notes + '\'' +
                ", createTime=" + createTime +
                ", dayOfWeek=" + dayOfWeek +
                ", dayOfWeekName='" + dayOfWeekName + '\'' +
                ", isToday=" + isToday +
                ", isWeekend=" + isWeekend +
                '}';
    }
}
