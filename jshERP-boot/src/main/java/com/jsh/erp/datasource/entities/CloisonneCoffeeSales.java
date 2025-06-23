package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 掐丝珐琅馆咖啡店销售实体类
 * 
 * @author jshERP
 * @since 2025-01-22
 */
@TableName("jsh_cloisonne_coffee_sales")
public class CloisonneCoffeeSales implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 销售日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate salesDate;

    /**
     * 销售收入
     */
    private BigDecimal revenue;

    /**
     * 订单数量
     */
    private Integer orderCount;

    /**
     * 平均订单金额
     */
    private BigDecimal avgOrderAmount;

    /**
     * 现金收入
     */
    private BigDecimal paymentCash;

    /**
     * 支付宝收入
     */
    private BigDecimal paymentAlipay;

    /**
     * 微信收入
     */
    private BigDecimal paymentWechat;

    /**
     * 银行卡收入
     */
    private BigDecimal paymentCard;

    /**
     * 录入人ID
     */
    private Long recorderId;

    /**
     * 录入人姓名
     */
    private String recorderName;

    /**
     * 销售凭证图片
     */
    private String images;

    /**
     * 备注
     */
    private String notes;

    /**
     * 关联财务记录ID
     */
    private Long financeRecordId;

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
    public CloisonneCoffeeSales() {}

    public CloisonneCoffeeSales(LocalDate salesDate, BigDecimal revenue, Integer orderCount, Long recorderId, String recorderName) {
        this.salesDate = salesDate;
        this.revenue = revenue;
        this.orderCount = orderCount;
        this.recorderId = recorderId;
        this.recorderName = recorderName;
        this.deleteFlag = "0";
    }

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSalesDate() {
        return salesDate;
    }

    public void setSalesDate(LocalDate salesDate) {
        this.salesDate = salesDate;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getAvgOrderAmount() {
        return avgOrderAmount;
    }

    public void setAvgOrderAmount(BigDecimal avgOrderAmount) {
        this.avgOrderAmount = avgOrderAmount;
    }

    public BigDecimal getPaymentCash() {
        return paymentCash;
    }

    public void setPaymentCash(BigDecimal paymentCash) {
        this.paymentCash = paymentCash;
    }

    public BigDecimal getPaymentAlipay() {
        return paymentAlipay;
    }

    public void setPaymentAlipay(BigDecimal paymentAlipay) {
        this.paymentAlipay = paymentAlipay;
    }

    public BigDecimal getPaymentWechat() {
        return paymentWechat;
    }

    public void setPaymentWechat(BigDecimal paymentWechat) {
        this.paymentWechat = paymentWechat;
    }

    public BigDecimal getPaymentCard() {
        return paymentCard;
    }

    public void setPaymentCard(BigDecimal paymentCard) {
        this.paymentCard = paymentCard;
    }

    public Long getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(Long recorderId) {
        this.recorderId = recorderId;
    }

    public String getRecorderName() {
        return recorderName;
    }

    public void setRecorderName(String recorderName) {
        this.recorderName = recorderName;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getFinanceRecordId() {
        return financeRecordId;
    }

    public void setFinanceRecordId(Long financeRecordId) {
        this.financeRecordId = financeRecordId;
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
        return "CloisonneCoffeeSales{" +
                "id=" + id +
                ", salesDate=" + salesDate +
                ", revenue=" + revenue +
                ", orderCount=" + orderCount +
                ", avgOrderAmount=" + avgOrderAmount +
                ", paymentCash=" + paymentCash +
                ", paymentAlipay=" + paymentAlipay +
                ", paymentWechat=" + paymentWechat +
                ", paymentCard=" + paymentCard +
                ", recorderId=" + recorderId +
                ", recorderName='" + recorderName + '\'' +
                ", images='" + images + '\'' +
                ", notes='" + notes + '\'' +
                ", financeRecordId=" + financeRecordId +
                ", tenantId=" + tenantId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser=" + createUser +
                ", updateUser=" + updateUser +
                ", deleteFlag='" + deleteFlag + '\'' +
                '}';
    }
}
