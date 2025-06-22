package com.jsh.erp.datasource.entities;

import java.util.Date;

/**
 * 排班记录实体类
 * 对应数据库表：jsh_schedule_entry
 * 
 * @author Augment Code
 * @date 2025-06-21
 */
public class ScheduleEntry {
    /**
     * 主键
     */
    private Long id;

    /**
     * 班次ID，关联jsh_schedule_shift表
     */
    private Long shiftId;

    /**
     * 员工ID，关联jsh_user表
     */
    private Long employeeId;

    /**
     * 排班日期
     */
    private Date scheduleDate;

    /**
     * 状态：SCHEDULED-已排班，CONFIRMED-已确认，CANCELLED-已取消
     */
    private String status;

    /**
     * 实际开始时间
     */
    private Date actualStartTime;

    /**
     * 实际结束时间
     */
    private Date actualEndTime;

    /**
     * 休息时长（分钟）
     */
    private Integer breakDuration;

    /**
     * 加班时长（分钟）
     */
    private Integer overtimeDuration;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户
     */
    private Long createUser;

    /**
     * 更新用户
     */
    private Long updateUser;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 删除标记，0未删除，1删除
     */
    private String deleteFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getShiftId() {
        return shiftId;
    }

    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Date getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public Integer getBreakDuration() {
        return breakDuration;
    }

    public void setBreakDuration(Integer breakDuration) {
        this.breakDuration = breakDuration;
    }

    public Integer getOvertimeDuration() {
        return overtimeDuration;
    }

    public void setOvertimeDuration(Integer overtimeDuration) {
        this.overtimeDuration = overtimeDuration;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag == null ? null : deleteFlag.trim();
    }

    @Override
    public String toString() {
        return "ScheduleEntry{" +
                "id=" + id +
                ", shiftId=" + shiftId +
                ", employeeId=" + employeeId +
                ", scheduleDate=" + scheduleDate +
                ", status='" + status + '\'' +
                ", actualStartTime=" + actualStartTime +
                ", actualEndTime=" + actualEndTime +
                ", breakDuration=" + breakDuration +
                ", overtimeDuration=" + overtimeDuration +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser=" + createUser +
                ", updateUser=" + updateUser +
                ", tenantId=" + tenantId +
                ", deleteFlag='" + deleteFlag + '\'' +
                '}';
    }
}
