package com.jsh.erp.datasource.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 掐丝珐琅馆排班统计VO
 * 
 * @author jshERP
 * @since 2025-01-22
 */
public class CloisonneScheduleStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 统计日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate statisticsDate;

    /**
     * 员工ID
     */
    private Long employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 班次类型
     */
    private String shiftType;

    /**
     * 工作区域
     */
    private String workArea;

    /**
     * 总排班数
     */
    private Integer totalSchedules;

    /**
     * 总工作时长
     */
    private BigDecimal totalWorkHours;

    /**
     * 平均工作时长
     */
    private BigDecimal avgWorkHours;

    /**
     * 正常排班数
     */
    private Integer normalCount;

    /**
     * 请假次数
     */
    private Integer leaveCount;

    /**
     * 调班次数
     */
    private Integer swapCount;

    /**
     * 缺勤次数
     */
    private Integer absentCount;

    /**
     * 出勤率(%)
     */
    private BigDecimal attendanceRate;

    /**
     * 统计值(通用字段，用于各种统计场景)
     */
    private BigDecimal statisticsValue;

    /**
     * 统计数量(通用字段，用于各种统计场景)
     */
    private Integer statisticsCount;

    /**
     * 统计名称(通用字段，用于各种统计场景)
     */
    private String statisticsName;

    /**
     * 统计标签(通用字段，用于图表显示)
     */
    private String statisticsLabel;

    /**
     * 百分比(用于占比统计)
     */
    private BigDecimal percentage;

    // 构造函数
    public CloisonneScheduleStatistics() {}

    public CloisonneScheduleStatistics(String statisticsName, Integer statisticsCount) {
        this.statisticsName = statisticsName;
        this.statisticsCount = statisticsCount;
    }

    public CloisonneScheduleStatistics(String statisticsName, BigDecimal statisticsValue) {
        this.statisticsName = statisticsName;
        this.statisticsValue = statisticsValue;
    }

    public CloisonneScheduleStatistics(LocalDate statisticsDate, Integer statisticsCount) {
        this.statisticsDate = statisticsDate;
        this.statisticsCount = statisticsCount;
    }

    // Getter和Setter方法
    public LocalDate getStatisticsDate() {
        return statisticsDate;
    }

    public void setStatisticsDate(LocalDate statisticsDate) {
        this.statisticsDate = statisticsDate;
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

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public Integer getTotalSchedules() {
        return totalSchedules;
    }

    public void setTotalSchedules(Integer totalSchedules) {
        this.totalSchedules = totalSchedules;
    }

    public BigDecimal getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(BigDecimal totalWorkHours) {
        this.totalWorkHours = totalWorkHours;
    }

    public BigDecimal getAvgWorkHours() {
        return avgWorkHours;
    }

    public void setAvgWorkHours(BigDecimal avgWorkHours) {
        this.avgWorkHours = avgWorkHours;
    }

    public Integer getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(Integer normalCount) {
        this.normalCount = normalCount;
    }

    public Integer getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(Integer leaveCount) {
        this.leaveCount = leaveCount;
    }

    public Integer getSwapCount() {
        return swapCount;
    }

    public void setSwapCount(Integer swapCount) {
        this.swapCount = swapCount;
    }

    public Integer getAbsentCount() {
        return absentCount;
    }

    public void setAbsentCount(Integer absentCount) {
        this.absentCount = absentCount;
    }

    public BigDecimal getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(BigDecimal attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public BigDecimal getStatisticsValue() {
        return statisticsValue;
    }

    public void setStatisticsValue(BigDecimal statisticsValue) {
        this.statisticsValue = statisticsValue;
    }

    public Integer getStatisticsCount() {
        return statisticsCount;
    }

    public void setStatisticsCount(Integer statisticsCount) {
        this.statisticsCount = statisticsCount;
    }

    public String getStatisticsName() {
        return statisticsName;
    }

    public void setStatisticsName(String statisticsName) {
        this.statisticsName = statisticsName;
    }

    public String getStatisticsLabel() {
        return statisticsLabel;
    }

    public void setStatisticsLabel(String statisticsLabel) {
        this.statisticsLabel = statisticsLabel;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    @Override
    public String toString() {
        return "CloisonneScheduleStatistics{" +
                "statisticsDate=" + statisticsDate +
                ", employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", shiftType='" + shiftType + '\'' +
                ", workArea='" + workArea + '\'' +
                ", totalSchedules=" + totalSchedules +
                ", totalWorkHours=" + totalWorkHours +
                ", avgWorkHours=" + avgWorkHours +
                ", normalCount=" + normalCount +
                ", leaveCount=" + leaveCount +
                ", swapCount=" + swapCount +
                ", absentCount=" + absentCount +
                ", attendanceRate=" + attendanceRate +
                ", statisticsValue=" + statisticsValue +
                ", statisticsCount=" + statisticsCount +
                ", statisticsName='" + statisticsName + '\'' +
                ", statisticsLabel='" + statisticsLabel + '\'' +
                ", percentage=" + percentage +
                '}';
    }
}
