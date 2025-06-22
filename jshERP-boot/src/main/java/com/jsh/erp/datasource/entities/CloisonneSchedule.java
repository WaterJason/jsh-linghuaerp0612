package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 掐丝珐琅馆排班实体类
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@TableName("jsh_cloisonne_schedule")
public class CloisonneSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 排班日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate scheduleDate;

    /**
     * 员工ID(关联jsh_user.id)
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 班次类型(早班/中班/晚班/夜班/全天)
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
     * 工作时长(小时)
     */
    private BigDecimal workHours;

    /**
     * 工作区域(咖啡店/展厅/收银台/全部)
     */
    private String workArea;

    /**
     * 状态(normal-正常/leave-请假/swap-调班/absent-缺勤)
     */
    private String status;

    /**
     * 优先级(low-低/normal-普通/high-高/urgent-紧急)
     */
    private String priority;

    /**
     * 备注信息
     */
    private String notes;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private Long createUser;

    /**
     * 更新人ID
     */
    private Long updateUser;

    /**
     * 删除标记(0-存在,1-删除)
     */
    private String deleteFlag;

    // 构造函数
    public CloisonneSchedule() {}

    public CloisonneSchedule(LocalDate scheduleDate, Long employeeId, String employeeName, String shiftType) {
        this.scheduleDate = scheduleDate;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.shiftType = shiftType;
        this.status = "normal";
        this.deleteFlag = "0";
    }

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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
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

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    @Override
    public String toString() {
        return "CloisonneSchedule{" +
                "id=" + id +
                ", scheduleDate=" + scheduleDate +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", workHours=" + workHours +
                ", workArea='" + workArea + '\'' +
                ", status='" + status + '\'' +
                ", priority='" + priority + '\'' +
                ", notes='" + notes + '\'' +
                ", tenantId=" + tenantId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser=" + createUser +
                ", updateUser=" + updateUser +
                ", deleteFlag='" + deleteFlag + '\'' +
                '}';
    }
}
