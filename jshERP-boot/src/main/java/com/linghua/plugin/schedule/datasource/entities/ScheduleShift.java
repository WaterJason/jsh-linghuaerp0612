package com.linghua.plugin.schedule.datasource.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Schedule Shift Entity
 * 排班班次实体类
 * 
 * @author 聆花掐丝珐琅馆技术团队
 * @version 1.0.0
 * @since 2023-12-01
 */
public class ScheduleShift {
    
    /** 主键ID */
    private Long id;
    
    /** 班次名称 */
    private String shiftName;
    
    /** 班次类型：FULL_DAY-全天班，MORNING-上午班，AFTERNOON-下午班，EVENING-晚班 */
    private String shiftType;
    
    /** 开始时间 */
    private String startTime;
    
    /** 结束时间 */
    private String endTime;
    
    /** 班次时长（小时） */
    private BigDecimal durationHours;
    
    /** 是否启用：0-禁用，1-启用 */
    private Boolean isActive;
    
    /** 排序 */
    private Integer sortOrder;
    
    /** 描述 */
    private String description;
    
    /** 显示颜色 */
    private String color;
    
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
    
    // ==================== 构造函数 ====================
    
    public ScheduleShift() {}
    
    public ScheduleShift(String shiftName, String shiftType) {
        this.shiftName = shiftName;
        this.shiftType = shiftType;
    }
    
    // ==================== Getter and Setter ====================
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getShiftName() {
        return shiftName;
    }
    
    public void setShiftName(String shiftName) {
        this.shiftName = shiftName == null ? null : shiftName.trim();
    }
    
    public String getShiftType() {
        return shiftType;
    }
    
    public void setShiftType(String shiftType) {
        this.shiftType = shiftType == null ? null : shiftType.trim();
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime == null ? null : endTime.trim();
    }
    
    public BigDecimal getDurationHours() {
        return durationHours;
    }
    
    public void setDurationHours(BigDecimal durationHours) {
        this.durationHours = durationHours;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color == null ? null : color.trim();
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
    
    // ==================== 业务方法 ====================
    
    /**
     * 检查班次是否有效
     * 
     * @return 是否有效
     */
    public boolean isValid() {
        return shiftName != null && !shiftName.trim().isEmpty() 
            && shiftType != null && !shiftType.trim().isEmpty()
            && isActive != null && isActive;
    }
    
    /**
     * 获取班次显示名称
     * 
     * @return 显示名称
     */
    public String getDisplayName() {
        if (startTime != null && endTime != null) {
            return shiftName + " (" + startTime + "-" + endTime + ")";
        }
        return shiftName;
    }
    
    // ==================== toString ====================
    
    @Override
    public String toString() {
        return "ScheduleShift{" +
                "id=" + id +
                ", shiftName='" + shiftName + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", durationHours=" + durationHours +
                ", isActive=" + isActive +
                ", sortOrder=" + sortOrder +
                ", tenantId=" + tenantId +
                '}';
    }
}
