package com.jsh.erp.service.salary.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 员工数据DTO
 * 
 * @author jshERP
 * @date 2025-06-22
 */
public class EmployeeData {

    private Long employeeId;
    private String employeeName;
    private String department;
    private String position;
    private Date entryDate;
    private BigDecimal dailyWage;
    private Long tenantId;
    
    // 业务数据
    private Map<String, Object> salesData;      // 销售数据
    private Map<String, Object> productionData; // 生产数据
    private Map<String, Object> trainingData;   // 培训数据
    private Map<String, Object> attendanceData; // 考勤数据
    private Map<String, Object> customData;     // 自定义数据

    public EmployeeData() {
    }

    public EmployeeData(Long employeeId, String employeeName) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
    }

    // Getter and Setter methods
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public BigDecimal getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(BigDecimal dailyWage) {
        this.dailyWage = dailyWage;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Map<String, Object> getSalesData() {
        return salesData;
    }

    public void setSalesData(Map<String, Object> salesData) {
        this.salesData = salesData;
    }

    public Map<String, Object> getProductionData() {
        return productionData;
    }

    public void setProductionData(Map<String, Object> productionData) {
        this.productionData = productionData;
    }

    public Map<String, Object> getTrainingData() {
        return trainingData;
    }

    public void setTrainingData(Map<String, Object> trainingData) {
        this.trainingData = trainingData;
    }

    public Map<String, Object> getAttendanceData() {
        return attendanceData;
    }

    public void setAttendanceData(Map<String, Object> attendanceData) {
        this.attendanceData = attendanceData;
    }

    public Map<String, Object> getCustomData() {
        return customData;
    }

    public void setCustomData(Map<String, Object> customData) {
        this.customData = customData;
    }

    @Override
    public String toString() {
        return "EmployeeData{" +
                "employeeId=" + employeeId +
                ", employeeName='" + employeeName + '\'' +
                ", department='" + department + '\'' +
                ", position='" + position + '\'' +
                ", dailyWage=" + dailyWage +
                ", tenantId=" + tenantId +
                '}';
    }
}
