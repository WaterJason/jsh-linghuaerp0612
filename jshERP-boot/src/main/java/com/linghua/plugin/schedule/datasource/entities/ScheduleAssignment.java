package com.linghua.plugin.schedule.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Schedule Assignment Entity
 * 排班分配记录实体类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class ScheduleAssignment {
    
    /** 主键ID */
    private Long id;
    
    /** 排班日期 */
    private Date scheduleDate;
    
    /** 班次ID */
    private Long shiftId;
    
    /** 用户ID（关联jsh_user表） */
    private Long userId;
    
    /** 用户姓名（冗余字段，便于显示） */
    private String userName;
    
    /** 排班状态：SCHEDULED-已排班，CONFIRMED-已确认，CANCELLED-已取消，COMPLETED-已完成 */
    private String status;
    
    /** 备注 */
    private String notes;
    
    /** 实际工作时长（小时） */
    private BigDecimal workHours;
    
    /** 签到时间 */
    private Date checkInTime;
    
    /** 签退时间 */
    private Date checkOutTime;
    
    // ==================== 标准字段 ====================
    
    /** 租户ID */
    private Long tenantId;
    
    /** 删除标记：0-存在，1-删除 */
    private String deleteFlag;
    
    /** 创建时间 */
    private Date createTime;
    
    /** 更新时间 */
    private Date updateTime;
    
    /** 创建人ID */
    private Long createBy;
    
    /** 更新人ID */
    private Long updateBy;
    
    // ==================== 关联对象（非数据库字段） ====================
    
    /** 班次信息（关联查询时使用） */
    private ScheduleShift shift;
    
    // ==================== 构造函数 ====================
    
    public ScheduleAssignment() {}
    
    public ScheduleAssignment(Date scheduleDate, Long shiftId, Long userId) {
        this.scheduleDate = scheduleDate;
        this.shiftId = shiftId;
        this.userId = userId;
    }
    
    // ==================== Getter and Setter ====================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Date getScheduleDate() {
        return scheduleDate;
    }
    
    public void setScheduleDate(Date scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
    
    public Long getShiftId() {
        return shiftId;
    }
    
    public void setShiftId(Long shiftId) {
        this.shiftId = shiftId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes == null ? null : notes.trim();
    }
    
    public BigDecimal getWorkHours() {
        return workHours;
    }
    
    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }
    
    public Date getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public Date getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(Date checkOutTime) {
        this.checkOutTime = checkOutTime;
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
    
    public Long getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
    
    public Long getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }
    
    public ScheduleShift getShift() {
        return shift;
    }
    
    public void setShift(ScheduleShift shift) {
        this.shift = shift;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 检查排班记录是否有效
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return scheduleDate != null && shiftId != null && userId != null;
    }
    
    /**
     * 检查是否已签到
     * 
     * @return 是否已签到
     */
    public boolean isCheckedIn() {
        return checkInTime != null;
    }
    
    /**
     * 检查是否已签退
     * 
     * @return 是否已签退
     */
    public boolean isCheckedOut() {
        return checkOutTime != null;
    }
    
    /**
     * 检查排班是否已完成
     * 
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    /**
     * 检查排班是否已取消
     * 
     * @return 是否已取消
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    /**
     * 获取排班显示信息
     * 
     * @return 显示信息
     */
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        if (userName != null) {
            sb.append(userName);
        }
        if (shift != null) {
            sb.append(" - ").append(shift.getShiftName());
        }
        return sb.toString();
    }
    
    // ==================== toString ====================
    
    @Override
    public String toString() {
        return "ScheduleAssignment{" +
                "id=" + id +
                ", scheduleDate=" + scheduleDate +
                ", shiftId=" + shiftId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", status='" + status + '\'' +
                ", tenantId=" + tenantId +
                '}';
    }
}
