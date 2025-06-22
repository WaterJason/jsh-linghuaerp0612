package com.jsh.erp.datasource.entities;

import java.math.BigDecimal;

/**
 * 排班记录扩展实体类
 * 包含关联的班次和员工信息
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
public class ScheduleEntryEx extends ScheduleEntry {
    
    /**
     * 班次名称
     */
    private String shiftName;
    
    /**
     * 班次类型
     */
    private String shiftType;
    
    /**
     * 班次开始时间
     */
    private String shiftStartTime;
    
    /**
     * 班次结束时间
     */
    private String shiftEndTime;
    
    /**
     * 班次时长（小时）
     */
    private BigDecimal shiftDurationHours;
    
    /**
     * 班次颜色
     */
    private String shiftColor;
    
    /**
     * 员工姓名
     */
    private String employeeName;
    
    /**
     * 员工登录名
     */
    private String employeeLoginName;
    
    /**
     * 排班日期字符串（用于前端显示）
     */
    private String scheduleDateStr;
    
    /**
     * 状态显示名称
     */
    private String statusName;
    
    /**
     * 实际工作时长（分钟）
     */
    private Integer actualWorkDuration;

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public String getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public BigDecimal getShiftDurationHours() {
        return shiftDurationHours;
    }

    public void setShiftDurationHours(BigDecimal shiftDurationHours) {
        this.shiftDurationHours = shiftDurationHours;
    }

    public String getShiftColor() {
        return shiftColor;
    }

    public void setShiftColor(String shiftColor) {
        this.shiftColor = shiftColor;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeeLoginName() {
        return employeeLoginName;
    }

    public void setEmployeeLoginName(String employeeLoginName) {
        this.employeeLoginName = employeeLoginName;
    }

    public String getScheduleDateStr() {
        return scheduleDateStr;
    }

    public void setScheduleDateStr(String scheduleDateStr) {
        this.scheduleDateStr = scheduleDateStr;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getActualWorkDuration() {
        return actualWorkDuration;
    }

    public void setActualWorkDuration(Integer actualWorkDuration) {
        this.actualWorkDuration = actualWorkDuration;
    }

    @Override
    public String toString() {
        return "ScheduleEntryEx{" +
                "shiftName='" + shiftName + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", shiftStartTime='" + shiftStartTime + '\'' +
                ", shiftEndTime='" + shiftEndTime + '\'' +
                ", shiftDurationHours=" + shiftDurationHours +
                ", shiftColor='" + shiftColor + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeLoginName='" + employeeLoginName + '\'' +
                ", scheduleDateStr='" + scheduleDateStr + '\'' +
                ", statusName='" + statusName + '\'' +
                ", actualWorkDuration=" + actualWorkDuration +
                "} " + super.toString();
    }
}
