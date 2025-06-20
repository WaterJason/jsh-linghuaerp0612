# Week 19: 客户关系管理模块开发文档

## 文档信息
- **文档版本**: v1.0
- **创建日期**: 2025-06-18
- **项目阶段**: 第四阶段 - 基础支撑模块 (Week 19/24)
- **估算工期**: 5天
- **技术参考**: 严格遵循《jshERP_二次开发技术参考手册》第3、4、5章规范
- **前置依赖**: Week 1-18基础模块开发完成

---

## 概述

本文档描述客户关系管理(CRM)模块的开发实现，专门针对聆花文化的客户分类管理需求。该模块将统一管理散客、团建客户、渠道客户、合作伙伴等各类客户信息，并提供客户跟进、标签管理、消费历史追踪等功能。

**核心目标**:
- 建立统一的客户档案管理体系
- 实现客户分类与精准标签管理
- 提供客户跟进与沟通记录功能
- 支持客户价值分析与营销决策

---

## 数据库设计

### 1. 客户档案主表

**表名**: `jsh_customer`

```sql
-- 客户档案主表
CREATE TABLE `jsh_customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_code` varchar(32) NOT NULL COMMENT '客户编码',
  `customer_name` varchar(200) NOT NULL COMMENT '客户名称',
  `customer_type` varchar(20) NOT NULL COMMENT '客户类型(individual-散客,teambuilding-团建,channel-渠道,partner-合作伙伴)',
  `customer_level` varchar(20) DEFAULT 'normal' COMMENT '客户等级(vip,important,normal,potential)',
  
  -- 联系信息
  `contact_person` varchar(100) DEFAULT NULL COMMENT '联系人',
  `phone` varchar(50) DEFAULT NULL COMMENT '联系电话',
  `mobile` varchar(50) DEFAULT NULL COMMENT '手机号码',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱地址',
  `wechat` varchar(100) DEFAULT NULL COMMENT '微信号',
  `qq` varchar(50) DEFAULT NULL COMMENT 'QQ号',
  
  -- 地址信息
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `postal_code` varchar(20) DEFAULT NULL COMMENT '邮政编码',
  
  -- 商业信息
  `company_name` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `business_license` varchar(100) DEFAULT NULL COMMENT '营业执照号',
  `tax_number` varchar(100) DEFAULT NULL COMMENT '税号',
  `industry` varchar(100) DEFAULT NULL COMMENT '行业',
  `company_scale` varchar(50) DEFAULT NULL COMMENT '公司规模',
  
  -- 银行信息
  `bank_name` varchar(200) DEFAULT NULL COMMENT '开户银行',
  `bank_account` varchar(100) DEFAULT NULL COMMENT '银行账号',
  `bank_account_name` varchar(200) DEFAULT NULL COMMENT '账户名称',
  
  -- 业务信息
  `source` varchar(100) DEFAULT NULL COMMENT '客户来源',
  `first_order_date` date DEFAULT NULL COMMENT '首次下单日期',
  `last_order_date` date DEFAULT NULL COMMENT '最后下单日期',
  `total_orders` int(11) DEFAULT '0' COMMENT '总订单数',
  `total_amount` decimal(15,2) DEFAULT '0.00' COMMENT '累计消费金额',
  `average_amount` decimal(15,2) DEFAULT '0.00' COMMENT '平均订单金额',
  
  -- 偏好信息
  `preferred_products` text COMMENT '偏好产品(JSON)',
  `preferred_channels` text COMMENT '偏好渠道(JSON)',
  `preferred_contact_time` varchar(100) DEFAULT NULL COMMENT '偏好联系时间',
  `preferred_contact_method` varchar(50) DEFAULT NULL COMMENT '偏好联系方式',
  
  -- 标签与备注
  `tags` text COMMENT '客户标签(JSON)',
  `description` text COMMENT '客户描述',
  `remarks` text COMMENT '备注信息',
  
  -- 状态信息
  `status` char(1) DEFAULT '1' COMMENT '客户状态(1-正常,2-潜在,3-流失,0-黑名单)',
  `credit_level` varchar(20) DEFAULT 'good' COMMENT '信用等级(excellent,good,normal,poor)',
  `risk_level` varchar(20) DEFAULT 'low' COMMENT '风险等级(low,medium,high)',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_customer_code` (`customer_code`,`tenant_id`),
  KEY `idx_customer_type` (`customer_type`),
  KEY `idx_customer_level` (`customer_level`),
  KEY `idx_phone` (`phone`),
  KEY `idx_mobile` (`mobile`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户档案主表';
```

### 2. 客户跟进记录表

**表名**: `jsh_customer_follow`

```sql
-- 客户跟进记录表
CREATE TABLE `jsh_customer_follow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `follow_type` varchar(50) NOT NULL COMMENT '跟进类型(phone-电话,visit-拜访,email-邮件,wechat-微信,meeting-会议,other-其他)',
  `follow_date` datetime NOT NULL COMMENT '跟进时间',
  `follow_title` varchar(200) NOT NULL COMMENT '跟进主题',
  `follow_content` text COMMENT '跟进内容',
  `follow_result` varchar(500) DEFAULT NULL COMMENT '跟进结果',
  `next_follow_date` datetime DEFAULT NULL COMMENT '下次跟进时间',
  `follow_status` varchar(20) DEFAULT 'pending' COMMENT '跟进状态(pending-待跟进,completed-已完成,cancelled-已取消)',
  `opportunity_level` varchar(20) DEFAULT 'normal' COMMENT '机会等级(high-高,normal-中,low-低)',
  `attachments` text COMMENT '附件信息(JSON)',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_follow_date` (`follow_date`),
  KEY `idx_next_follow_date` (`next_follow_date`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户跟进记录表';
```

### 3. 客户标签表

**表名**: `jsh_customer_tag`

```sql
-- 客户标签表
CREATE TABLE `jsh_customer_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tag_name` varchar(100) NOT NULL COMMENT '标签名称',
  `tag_code` varchar(50) NOT NULL COMMENT '标签编码',
  `tag_category` varchar(50) DEFAULT NULL COMMENT '标签分类(behavior-行为,preference-偏好,business-商务,custom-自定义)',
  `tag_color` varchar(20) DEFAULT '#1890ff' COMMENT '标签颜色',
  `tag_description` varchar(500) DEFAULT NULL COMMENT '标签描述',
  `is_system` char(1) DEFAULT '0' COMMENT '是否系统标签',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `status` char(1) DEFAULT '1' COMMENT '状态(1-启用,0-禁用)',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tag_code` (`tag_code`,`tenant_id`),
  KEY `idx_tag_category` (`tag_category`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户标签表';
```

### 4. 客户渠道信息表

**表名**: `jsh_customer_channel`

```sql
-- 客户渠道信息表
CREATE TABLE `jsh_customer_channel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `customer_id` bigint(20) NOT NULL COMMENT '客户ID',
  `channel_type` varchar(50) NOT NULL COMMENT '渠道类型(retail-零售,wholesale-批发,online-线上,offline-线下)',
  `cooperation_mode` varchar(50) DEFAULT NULL COMMENT '合作模式(direct-直营,agent-代理,distributor-分销,franchise-加盟)',
  `contract_start_date` date DEFAULT NULL COMMENT '合同开始日期',
  `contract_end_date` date DEFAULT NULL COMMENT '合同结束日期',
  `commission_rate` decimal(5,2) DEFAULT '0.00' COMMENT '佣金比例(%)',
  `credit_limit` decimal(15,2) DEFAULT '0.00' COMMENT '信用额度',
  `payment_terms` varchar(200) DEFAULT NULL COMMENT '付款条件',
  `settlement_period` varchar(100) DEFAULT NULL COMMENT '结算周期',
  `price_policy` varchar(200) DEFAULT NULL COMMENT '价格政策',
  `support_policy` text COMMENT '支持政策',
  `territory` varchar(500) DEFAULT NULL COMMENT '销售区域',
  `exclusive_rights` text COMMENT '专营权限(JSON)',
  
  -- 系统字段
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` bigint(20) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `update_user` bigint(20) DEFAULT NULL COMMENT '更新人',
  `tenant_id` bigint(20) DEFAULT NULL COMMENT '租户ID',
  `delete_flag` char(1) DEFAULT '0' COMMENT '删除标志',
  
  PRIMARY KEY (`id`),
  KEY `idx_customer_id` (`customer_id`),
  KEY `idx_channel_type` (`channel_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户渠道信息表';
```

---

## 后端实现

### 1. 实体类设计

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/Customer.java`

```java
package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsh_customer")
public class Customer {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String customerCode;
    private String customerName;
    private String customerType;
    private String customerLevel;
    
    // 联系信息
    private String contactPerson;
    private String phone;
    private String mobile;
    private String email;
    private String wechat;
    private String qq;
    
    // 地址信息
    private String province;
    private String city;
    private String district;
    private String address;
    private String postalCode;
    
    // 商业信息
    private String companyName;
    private String businessLicense;
    private String taxNumber;
    private String industry;
    private String companyScale;
    
    // 银行信息
    private String bankName;
    private String bankAccount;
    private String bankAccountName;
    
    // 业务信息
    private String source;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date firstOrderDate;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date lastOrderDate;
    private Integer totalOrders;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    
    // 偏好信息
    private String preferredProducts;
    private String preferredChannels;
    private String preferredContactTime;
    private String preferredContactMethod;
    
    // 标签与备注
    private String tags;
    private String description;
    private String remarks;
    
    // 状态信息
    private String status;
    private String creditLevel;
    private String riskLevel;
    
    // 系统字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Long createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Long updateUser;
    private Long tenantId;
    private String deleteFlag;
}
```

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/datasource/entities/CustomerFollow.java`

```java
package com.jsh.erp.datasource.entities;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("jsh_customer_follow")
public class CustomerFollow {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private Long customerId;
    private String followType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date followDate;
    private String followTitle;
    private String followContent;
    private String followResult;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextFollowDate;
    private String followStatus;
    private String opportunityLevel;
    private String attachments;
    
    // 系统字段
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private Long createUser;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    private Long updateUser;
    private Long tenantId;
    private String deleteFlag;
}
```

### 2. Mapper接口设计

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/datasource/mappers/CustomerMapper.java`

```java
package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.Customer;
import com.jsh.erp.datasource.vo.CustomerVo4List;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerMapper extends BaseMapper<Customer> {
    
    /**
     * 查询客户列表
     */
    List<CustomerVo4List> selectCustomerList(@Param("customerName") String customerName,
                                           @Param("customerType") String customerType,
                                           @Param("customerLevel") String customerLevel,
                                           @Param("phone") String phone,
                                           @Param("status") String status,
                                           @Param("tenantId") Long tenantId);
    
    /**
     * 根据客户编码查询客户
     */
    Customer selectByCustomerCode(@Param("customerCode") String customerCode, 
                                @Param("tenantId") Long tenantId);
    
    /**
     * 根据手机号查询客户
     */
    Customer selectByMobile(@Param("mobile") String mobile, 
                          @Param("tenantId") Long tenantId);
    
    /**
     * 更新客户业务统计信息
     */
    int updateCustomerBusinessInfo(@Param("customerId") Long customerId,
                                 @Param("totalOrders") Integer totalOrders,
                                 @Param("totalAmount") String totalAmount,
                                 @Param("averageAmount") String averageAmount,
                                 @Param("lastOrderDate") String lastOrderDate);
    
    /**
     * 查询客户标签统计
     */
    List<Map<String, Object>> selectCustomerTagStatistics(@Param("tenantId") Long tenantId);
    
    /**
     * 查询客户地域分布
     */
    List<Map<String, Object>> selectCustomerRegionDistribution(@Param("tenantId") Long tenantId);
}
```

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/datasource/mappers/CustomerFollowMapper.java`

```java
package com.jsh.erp.datasource.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jsh.erp.datasource.entities.CustomerFollow;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomerFollowMapper extends BaseMapper<CustomerFollow> {
    
    /**
     * 查询客户跟进记录列表
     */
    List<CustomerFollow> selectFollowList(@Param("customerId") Long customerId,
                                        @Param("followType") String followType,
                                        @Param("followStatus") String followStatus,
                                        @Param("tenantId") Long tenantId);
    
    /**
     * 查询待跟进客户列表
     */
    List<Map<String, Object>> selectPendingFollowList(@Param("userId") Long userId,
                                                     @Param("tenantId") Long tenantId);
    
    /**
     * 查询跟进统计数据
     */
    Map<String, Object> selectFollowStatistics(@Param("userId") Long userId,
                                             @Param("tenantId") Long tenantId);
}
```

### 3. 服务层实现

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/service/CustomerService.java`

```java
package com.jsh.erp.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jsh.erp.constants.BusinessConstants;
import com.jsh.erp.datasource.entities.Customer;
import com.jsh.erp.datasource.entities.CustomerFollow;
import com.jsh.erp.datasource.mappers.CustomerMapper;
import com.jsh.erp.datasource.mappers.CustomerFollowMapper;
import com.jsh.erp.datasource.vo.CustomerVo4List;
import com.jsh.erp.exception.BusinessRunTimeException;
import com.jsh.erp.exception.JshException;
import com.jsh.erp.service.user.UserService;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private CustomerFollowMapper customerFollowMapper;

    @Resource
    private UserService userService;

    /**
     * 查询客户列表
     */
    public List<CustomerVo4List> selectCustomerList(String customerName, String customerType,
                                                  String customerLevel, String phone, String status,
                                                  int currentPage, int pageSize, HttpServletRequest request) 
                                                  throws Exception {
        return customerMapper.selectCustomerList(customerName, customerType, customerLevel, 
                                               phone, status, getUserTenantId(request));
    }

    /**
     * 创建客户
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertCustomer(JSONObject obj, HttpServletRequest request) throws Exception {
        Customer customer = JSONObject.parseObject(obj.toJSONString(), Customer.class);
        
        // 验证必填字段
        if (StringUtil.isEmpty(customer.getCustomerName())) {
            throw new BusinessRunTimeException(ExceptionConstants.CUSTOMER_NAME_EMPTY_CODE,
                    ExceptionConstants.CUSTOMER_NAME_EMPTY_MSG);
        }
        
        // 生成客户编码
        if (StringUtil.isEmpty(customer.getCustomerCode())) {
            customer.setCustomerCode(generateCustomerCode(customer.getCustomerType()));
        }
        
        // 检查客户编码唯一性
        Customer existCustomer = customerMapper.selectByCustomerCode(customer.getCustomerCode(), 
                                                                   getUserTenantId(request));
        if (existCustomer != null) {
            throw new BusinessRunTimeException(ExceptionConstants.CUSTOMER_CODE_EXISTS_CODE,
                    ExceptionConstants.CUSTOMER_CODE_EXISTS_MSG);
        }
        
        // 设置系统字段
        customer.setCreateTime(new Date());
        customer.setCreateUser(getUserId(request));
        customer.setTenantId(getUserTenantId(request));
        customer.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        
        // 初始化业务字段
        if (customer.getTotalOrders() == null) {
            customer.setTotalOrders(0);
        }
        if (customer.getTotalAmount() == null) {
            customer.setTotalAmount(BigDecimal.ZERO);
        }
        if (customer.getAverageAmount() == null) {
            customer.setAverageAmount(BigDecimal.ZERO);
        }
        
        int result = customerMapper.insert(customer);
        logger.info("创建客户成功，客户编码：{}", customer.getCustomerCode());
        
        return result;
    }

    /**
     * 更新客户信息
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateCustomer(JSONObject obj, HttpServletRequest request) throws Exception {
        Customer customer = JSONObject.parseObject(obj.toJSONString(), Customer.class);
        
        if (customer.getId() == null) {
            throw new BusinessRunTimeException(ExceptionConstants.ID_EMPTY_CODE,
                    ExceptionConstants.ID_EMPTY_MSG);
        }
        
        // 设置更新字段
        customer.setUpdateTime(new Date());
        customer.setUpdateUser(getUserId(request));
        
        int result = customerMapper.updateById(customer);
        logger.info("更新客户成功，客户ID：{}", customer.getId());
        
        return result;
    }

    /**
     * 删除客户
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int deleteCustomer(Long id, HttpServletRequest request) throws Exception {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setUpdateTime(new Date());
        customer.setUpdateUser(getUserId(request));
        customer.setDeleteFlag(BusinessConstants.DELETE_FLAG_DELETED);
        
        int result = customerMapper.updateById(customer);
        logger.info("删除客户成功，客户ID：{}", id);
        
        return result;
    }

    /**
     * 批量删除客户
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int batchDeleteCustomer(String ids, HttpServletRequest request) throws Exception {
        List<Long> idList = StringUtil.strToLongList(ids);
        int deleteCount = 0;
        
        for (Long id : idList) {
            deleteCount += deleteCustomer(id, request);
        }
        
        return deleteCount;
    }

    /**
     * 根据ID查询客户详情
     */
    public Customer getCustomerDetail(Long id, HttpServletRequest request) throws Exception {
        Customer customer = customerMapper.selectById(id);
        if (customer == null || !customer.getTenantId().equals(getUserTenantId(request))) {
            throw new BusinessRunTimeException(ExceptionConstants.CUSTOMER_NOT_EXISTS_CODE,
                    ExceptionConstants.CUSTOMER_NOT_EXISTS_MSG);
        }
        return customer;
    }

    /**
     * 创建客户跟进记录
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int insertCustomerFollow(JSONObject obj, HttpServletRequest request) throws Exception {
        CustomerFollow follow = JSONObject.parseObject(obj.toJSONString(), CustomerFollow.class);
        
        // 验证必填字段
        if (follow.getCustomerId() == null) {
            throw new BusinessRunTimeException(ExceptionConstants.CUSTOMER_ID_EMPTY_CODE,
                    ExceptionConstants.CUSTOMER_ID_EMPTY_MSG);
        }
        
        // 设置系统字段
        follow.setCreateTime(new Date());
        follow.setCreateUser(getUserId(request));
        follow.setTenantId(getUserTenantId(request));
        follow.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        
        // 设置默认值
        if (follow.getFollowDate() == null) {
            follow.setFollowDate(new Date());
        }
        if (StringUtil.isEmpty(follow.getFollowStatus())) {
            follow.setFollowStatus("completed");
        }
        if (StringUtil.isEmpty(follow.getOpportunityLevel())) {
            follow.setOpportunityLevel("normal");
        }
        
        int result = customerFollowMapper.insert(follow);
        logger.info("创建客户跟进记录成功，客户ID：{}", follow.getCustomerId());
        
        return result;
    }

    /**
     * 查询客户跟进记录列表
     */
    public List<CustomerFollow> selectCustomerFollowList(Long customerId, String followType,
                                                       String followStatus, HttpServletRequest request) 
                                                       throws Exception {
        return customerFollowMapper.selectFollowList(customerId, followType, followStatus, 
                                                   getUserTenantId(request));
    }

    /**
     * 查询待跟进客户列表
     */
    public List<Map<String, Object>> selectPendingFollowList(HttpServletRequest request) throws Exception {
        return customerFollowMapper.selectPendingFollowList(getUserId(request), getUserTenantId(request));
    }

    /**
     * 更新客户业务统计信息
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateCustomerBusinessInfo(Long customerId, Integer totalOrders, 
                                        BigDecimal totalAmount, Date lastOrderDate) throws Exception {
        BigDecimal averageAmount = BigDecimal.ZERO;
        if (totalOrders > 0 && totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            averageAmount = totalAmount.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP);
        }
        
        return customerMapper.updateCustomerBusinessInfo(customerId, totalOrders, 
                                                       totalAmount.toString(), 
                                                       averageAmount.toString(),
                                                       Tools.parseDateToStr(lastOrderDate));
    }

    /**
     * 查询客户统计数据
     */
    public Map<String, Object> getCustomerStatistics(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        // 查询客户标签统计
        List<Map<String, Object>> tagStats = customerMapper.selectCustomerTagStatistics(getUserTenantId(request));
        result.put("tagStatistics", tagStats);
        
        // 查询客户地域分布
        List<Map<String, Object>> regionStats = customerMapper.selectCustomerRegionDistribution(getUserTenantId(request));
        result.put("regionDistribution", regionStats);
        
        // 查询跟进统计
        Map<String, Object> followStats = customerFollowMapper.selectFollowStatistics(getUserId(request), 
                                                                                     getUserTenantId(request));
        result.put("followStatistics", followStats);
        
        return result;
    }

    /**
     * 生成客户编码
     */
    private String generateCustomerCode(String customerType) {
        String prefix = "";
        switch (customerType) {
            case "individual":
                prefix = "IND";
                break;
            case "teambuilding":
                prefix = "TB";
                break;
            case "channel":
                prefix = "CHN";
                break;
            case "partner":
                prefix = "PTN";
                break;
            default:
                prefix = "CUS";
                break;
        }
        
        return prefix + Tools.getNowTime().substring(2, 8) + Tools.getRandomNum(4);
    }

    private Long getUserId(HttpServletRequest request) throws Exception {
        return userService.getUserId(request);
    }

    private Long getUserTenantId(HttpServletRequest request) throws Exception {
        return userService.getTenantId(request);
    }
}
```

### 4. 控制器实现

**文件路径**: `jshERP-boot/src/main/java/com/jsh/erp/controller/CustomerController.java`

```java
package com.jsh.erp.controller;

import com.alibaba.fastjson.JSONObject;
import com.jsh.erp.base.BaseController;
import com.jsh.erp.datasource.entities.Customer;
import com.jsh.erp.datasource.entities.CustomerFollow;
import com.jsh.erp.datasource.vo.CustomerVo4List;
import com.jsh.erp.service.CustomerService;
import com.jsh.erp.utils.ResponseJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/customer")
@Api(tags = {"客户管理"})
public class CustomerController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Resource
    private CustomerService customerService;

    /**
     * 查询客户列表
     */
    @GetMapping(value = "/list")
    @ApiOperation(value = "查询客户列表")
    public String getCustomerList(@RequestParam(name = "customerName", required = false) String customerName,
                                @RequestParam(name = "customerType", required = false) String customerType,
                                @RequestParam(name = "customerLevel", required = false) String customerLevel,
                                @RequestParam(name = "phone", required = false) String phone,
                                @RequestParam(name = "status", required = false) String status,
                                @RequestParam(name = "currentPage", defaultValue = "1") Integer currentPage,
                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                HttpServletRequest request, HttpServletResponse response) {
        try {
            List<CustomerVo4List> dataList = customerService.selectCustomerList(customerName, customerType,
                    customerLevel, phone, status, currentPage, pageSize, request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询客户列表失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 创建客户
     */
    @PostMapping(value = "/add")
    @ApiOperation(value = "创建客户")
    public String addCustomer(@RequestBody JSONObject obj,
                            HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = customerService.insertCustomer(obj, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("创建客户失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 更新客户信息
     */
    @PutMapping(value = "/update")
    @ApiOperation(value = "更新客户信息")
    public String updateCustomer(@RequestBody JSONObject obj,
                               HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = customerService.updateCustomer(obj, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("更新客户信息失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 删除客户
     */
    @DeleteMapping(value = "/delete")
    @ApiOperation(value = "删除客户")
    public String deleteCustomer(@RequestParam("id") Long id,
                               HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = customerService.deleteCustomer(id, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("删除客户失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 批量删除客户
     */
    @DeleteMapping(value = "/batchDelete")
    @ApiOperation(value = "批量删除客户")
    public String batchDeleteCustomer(@RequestParam("ids") String ids,
                                    HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = customerService.batchDeleteCustomer(ids, request);
            return ResponseJsonUtil.returnJson(result, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("批量删除客户失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询客户详情
     */
    @GetMapping(value = "/detail")
    @ApiOperation(value = "查询客户详情")
    public String getCustomerDetail(@RequestParam("id") Long id,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            Customer customer = customerService.getCustomerDetail(id, request);
            return ResponseJsonUtil.returnJson(customer, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询客户详情失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 创建客户跟进记录
     */
    @PostMapping(value = "/follow/add")
    @ApiOperation(value = "创建客户跟进记录")
    public String addCustomerFollow(@RequestBody JSONObject obj,
                                  HttpServletRequest request, HttpServletResponse response) {
        try {
            int result = customerService.insertCustomerFollow(obj, request);
            if (result > 0) {
                return ResponseJsonUtil.returnJson(null, ErpInfo.OK.name, ErpInfo.OK.code);
            } else {
                return ResponseJsonUtil.returnJson(null, ErpInfo.ERROR.name, ErpInfo.ERROR.code);
            }
        } catch (Exception e) {
            logger.error("创建客户跟进记录失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询客户跟进记录列表
     */
    @GetMapping(value = "/follow/list")
    @ApiOperation(value = "查询客户跟进记录列表")
    public String getCustomerFollowList(@RequestParam("customerId") Long customerId,
                                      @RequestParam(name = "followType", required = false) String followType,
                                      @RequestParam(name = "followStatus", required = false) String followStatus,
                                      HttpServletRequest request, HttpServletResponse response) {
        try {
            List<CustomerFollow> dataList = customerService.selectCustomerFollowList(customerId, followType, 
                                                                                   followStatus, request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询客户跟进记录列表失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询待跟进客户列表
     */
    @GetMapping(value = "/follow/pending")
    @ApiOperation(value = "查询待跟进客户列表")
    public String getPendingFollowList(HttpServletRequest request, HttpServletResponse response) {
        try {
            List<Map<String, Object>> dataList = customerService.selectPendingFollowList(request);
            return ResponseJsonUtil.returnJson(dataList, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询待跟进客户列表失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }

    /**
     * 查询客户统计数据
     */
    @GetMapping(value = "/statistics")
    @ApiOperation(value = "查询客户统计数据")
    public String getCustomerStatistics(HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> statistics = customerService.getCustomerStatistics(request);
            return ResponseJsonUtil.returnJson(statistics, ErpInfo.OK.name, ErpInfo.OK.code);
        } catch (Exception e) {
            logger.error("查询客户统计数据失败", e);
            return ResponseJsonUtil.returnJson(null, e.getMessage(), ErpInfo.ERROR.code);
        }
    }
}
```

---

## 前端实现

### 1. 客户列表页面

**文件路径**: `jshERP-web/src/views/customer/CustomerList.vue`

```vue
<template>
  <div>
    <!-- 搜索区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="客户名称">
              <a-input v-model="queryParam.customerName" placeholder="请输入客户名称" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="客户类型">
              <a-select v-model="queryParam.customerType" placeholder="请选择客户类型" allowClear>
                <a-select-option value="individual">散客</a-select-option>
                <a-select-option value="teambuilding">团建客户</a-select-option>
                <a-select-option value="channel">渠道客户</a-select-option>
                <a-select-option value="partner">合作伙伴</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="客户等级">
              <a-select v-model="queryParam.customerLevel" placeholder="请选择客户等级" allowClear>
                <a-select-option value="vip">VIP客户</a-select-option>
                <a-select-option value="important">重要客户</a-select-option>
                <a-select-option value="normal">普通客户</a-select-option>
                <a-select-option value="potential">潜在客户</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item label="联系电话">
              <a-input v-model="queryParam.phone" placeholder="请输入联系电话" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span class="table-page-search-submitButtons">
              <a-button type="primary" @click="searchQuery" icon="search">搜索</a-button>
              <a-button type="primary" @click="searchReset" icon="reload" style="margin-left: 8px">重置</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!-- 操作按钮区域 -->
    <div class="table-operator">
      <a-button type="primary" icon="plus" @click="handleAdd">新增客户</a-button>
      <a-button type="primary" icon="download" @click="handleExport">导出</a-button>
      <a-dropdown v-if="selectedRowKeys.length > 0">
        <a-menu slot="overlay">
          <a-menu-item key="1" @click="batchDel">
            <a-icon type="delete" />删除
          </a-menu-item>
        </a-menu>
        <a-button style="margin-left: 8px">
          批量操作 <a-icon type="down" />
        </a-button>
      </a-dropdown>
    </div>

    <!-- 数据表格 -->
    <div class="ant-alert ant-alert-info" style="margin-bottom: 16px;">
      <i class="anticon anticon-info-circle ant-alert-icon"></i>
      <span class="ant-alert-message">
        已选择 <a style="font-weight: 600">{{ selectedRowKeys.length }}</a> 项
        <a style="margin-left: 24px" @click="onClearSelected">清空</a>
      </span>
    </div>

    <a-table
      ref="table"
      size="middle"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :rowSelection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
      @change="handleTableChange"
    >
      <template slot="customerType" slot-scope="text">
        <a-tag v-if="text === 'individual'" color="blue">散客</a-tag>
        <a-tag v-else-if="text === 'teambuilding'" color="green">团建客户</a-tag>
        <a-tag v-else-if="text === 'channel'" color="orange">渠道客户</a-tag>
        <a-tag v-else-if="text === 'partner'" color="purple">合作伙伴</a-tag>
      </template>

      <template slot="customerLevel" slot-scope="text">
        <a-tag v-if="text === 'vip'" color="red">VIP</a-tag>
        <a-tag v-else-if="text === 'important'" color="orange">重要</a-tag>
        <a-tag v-else-if="text === 'normal'" color="blue">普通</a-tag>
        <a-tag v-else-if="text === 'potential'" color="gray">潜在</a-tag>
      </template>

      <template slot="status" slot-scope="text">
        <a-tag v-if="text === '1'" color="green">正常</a-tag>
        <a-tag v-else-if="text === '2'" color="blue">潜在</a-tag>
        <a-tag v-else-if="text === '3'" color="orange">流失</a-tag>
        <a-tag v-else-if="text === '0'" color="red">黑名单</a-tag>
      </template>

      <template slot="totalAmount" slot-scope="text">
        <span>{{ text | formatMoney }}</span>
      </template>

      <template slot="action" slot-scope="text, record">
        <a @click="handleEdit(record)">编辑</a>
        <a-divider type="vertical" />
        <a @click="handleDetail(record)">详情</a>
        <a-divider type="vertical" />
        <a @click="handleFollow(record)">跟进</a>
        <a-divider type="vertical" />
        <a-popconfirm title="确定删除吗?" @confirm="() => handleDelete(record.id)">
          <a>删除</a>
        </a-popconfirm>
      </template>
    </a-table>

    <!-- 客户表单弹窗 -->
    <customer-modal ref="customerModal" @ok="modalFormOk" />
    
    <!-- 客户详情弹窗 -->
    <customer-detail-modal ref="customerDetailModal" />
    
    <!-- 跟进记录弹窗 -->
    <customer-follow-modal ref="customerFollowModal" @ok="modalFormOk" />
  </div>
</template>

<script>
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import CustomerModal from './modules/CustomerModal'
import CustomerDetailModal from './modules/CustomerDetailModal'
import CustomerFollowModal from './modules/CustomerFollowModal'
import { getCustomerList, deleteCustomer, batchDeleteCustomer } from '@/api/customer'

export default {
  name: 'CustomerList',
  mixins: [JeecgListMixin],
  components: {
    CustomerModal,
    CustomerDetailModal,
    CustomerFollowModal
  },
  data() {
    return {
      // 查询条件
      queryParam: {},
      // 表格列配置
      columns: [
        {
          title: '客户编码',
          align: 'center',
          dataIndex: 'customerCode',
          width: 120
        },
        {
          title: '客户名称',
          align: 'center',
          dataIndex: 'customerName',
          width: 160
        },
        {
          title: '客户类型',
          align: 'center',
          dataIndex: 'customerType',
          width: 100,
          scopedSlots: { customRender: 'customerType' }
        },
        {
          title: '客户等级',
          align: 'center',
          dataIndex: 'customerLevel',
          width: 100,
          scopedSlots: { customRender: 'customerLevel' }
        },
        {
          title: '联系人',
          align: 'center',
          dataIndex: 'contactPerson',
          width: 100
        },
        {
          title: '联系电话',
          align: 'center',
          dataIndex: 'phone',
          width: 120
        },
        {
          title: '手机号码',
          align: 'center',
          dataIndex: 'mobile',
          width: 120
        },
        {
          title: '累计消费',
          align: 'center',
          dataIndex: 'totalAmount',
          width: 120,
          scopedSlots: { customRender: 'totalAmount' }
        },
        {
          title: '订单数量',
          align: 'center',
          dataIndex: 'totalOrders',
          width: 100
        },
        {
          title: '状态',
          align: 'center',
          dataIndex: 'status',
          width: 80,
          scopedSlots: { customRender: 'status' }
        },
        {
          title: '创建时间',
          align: 'center',
          dataIndex: 'createTime',
          width: 140
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          fixed: 'right',
          width: 180,
          scopedSlots: { customRender: 'action' }
        }
      ],
      url: {
        list: getCustomerList,
        delete: deleteCustomer,
        deleteBatch: batchDeleteCustomer
      }
    }
  },
  methods: {
    // 新增客户
    handleAdd() {
      this.$refs.customerModal.add()
      this.$refs.customerModal.title = '新增客户'
    },
    
    // 编辑客户
    handleEdit(record) {
      this.$refs.customerModal.edit(record)
      this.$refs.customerModal.title = '编辑客户'
    },
    
    // 查看详情
    handleDetail(record) {
      this.$refs.customerDetailModal.showModal(record)
    },
    
    // 客户跟进
    handleFollow(record) {
      this.$refs.customerFollowModal.add(record.id)
      this.$refs.customerFollowModal.title = '新增跟进记录'
    },
    
    // 导出
    handleExport() {
      if (this.dataSource.length === 0) {
        this.$message.warning('当前没有数据可以导出！')
        return
      }
      this.$message.success('导出功能开发中...')
    }
  }
}
</script>
```

### 2. 客户表单组件

**文件路径**: `jshERP-web/src/views/customer/modules/CustomerModal.vue`

```vue
<template>
  <a-modal
    :title="title"
    :width="1000"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form" :label-col="labelCol" :wrapper-col="wrapperCol">
        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="客户名称">
              <a-input
                v-decorator="['customerName', validatorRules.customerName]"
                placeholder="请输入客户名称"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="客户编码">
              <a-input
                v-decorator="['customerCode', validatorRules.customerCode]"
                placeholder="自动生成或手动输入"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="客户类型">
              <a-select
                v-decorator="['customerType', validatorRules.customerType]"
                placeholder="请选择客户类型"
              >
                <a-select-option value="individual">散客</a-select-option>
                <a-select-option value="teambuilding">团建客户</a-select-option>
                <a-select-option value="channel">渠道客户</a-select-option>
                <a-select-option value="partner">合作伙伴</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="客户等级">
              <a-select
                v-decorator="['customerLevel', { initialValue: 'normal' }]"
                placeholder="请选择客户等级"
              >
                <a-select-option value="vip">VIP客户</a-select-option>
                <a-select-option value="important">重要客户</a-select-option>
                <a-select-option value="normal">普通客户</a-select-option>
                <a-select-option value="potential">潜在客户</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="联系人">
              <a-input
                v-decorator="['contactPerson']"
                placeholder="请输入联系人"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="联系电话">
              <a-input
                v-decorator="['phone']"
                placeholder="请输入联系电话"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="手机号码">
              <a-input
                v-decorator="['mobile', validatorRules.mobile]"
                placeholder="请输入手机号码"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱地址">
              <a-input
                v-decorator="['email', validatorRules.email]"
                placeholder="请输入邮箱地址"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="微信号">
              <a-input
                v-decorator="['wechat']"
                placeholder="请输入微信号"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="公司名称">
              <a-input
                v-decorator="['companyName']"
                placeholder="请输入公司名称"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="8">
            <a-form-item label="省份">
              <a-input
                v-decorator="['province']"
                placeholder="请输入省份"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="城市">
              <a-input
                v-decorator="['city']"
                placeholder="请输入城市"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item label="区县">
              <a-input
                v-decorator="['district']"
                placeholder="请输入区县"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item label="详细地址" :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }">
              <a-input
                v-decorator="['address']"
                placeholder="请输入详细地址"
              />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="12">
            <a-form-item label="客户来源">
              <a-input
                v-decorator="['source']"
                placeholder="请输入客户来源"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态">
              <a-select
                v-decorator="['status', { initialValue: '1' }]"
                placeholder="请选择状态"
              >
                <a-select-option value="1">正常</a-select-option>
                <a-select-option value="2">潜在</a-select-option>
                <a-select-option value="3">流失</a-select-option>
                <a-select-option value="0">黑名单</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :span="24">
            <a-form-item label="备注信息" :label-col="{ span: 3 }" :wrapper-col="{ span: 21 }">
              <a-textarea
                v-decorator="['remarks']"
                :rows="3"
                placeholder="请输入备注信息"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script>
import { httpAction } from '@/api/manage'
import { validateDuplicateValue } from '@/utils/util'

export default {
  name: 'CustomerModal',
  data() {
    return {
      title: '操作',
      width: 800,
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        customerName: {
          rules: [
            { required: true, message: '请输入客户名称!' },
            { min: 2, max: 50, message: '客户名称长度在2到50个字符' }
          ]
        },
        customerCode: {
          rules: [
            { max: 32, message: '客户编码长度不能超过32个字符' }
          ]
        },
        customerType: {
          rules: [
            { required: true, message: '请选择客户类型!' }
          ]
        },
        mobile: {
          rules: [
            { pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号码!' }
          ]
        },
        email: {
          rules: [
            { type: 'email', message: '请输入正确的邮箱地址!' }
          ]
        }
      },
      url: {
        add: '/customer/add',
        edit: '/customer/update'
      }
    }
  },
  created() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue(record)
      })
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    handleOk() {
      const that = this
      // 触发表单验证
      this.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let httpurl = ''
          let method = ''
          if (!this.model.id) {
            httpurl += this.url.add
            method = 'post'
          } else {
            httpurl += this.url.edit
            method = 'put'
            values.id = this.model.id
          }
          httpAction(httpurl, values, method)
            .then((res) => {
              if (res.code === 200) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    },
    handleCancel() {
      this.close()
    }
  }
}
</script>
```

---

## 接口文档

### 1. 查询客户列表

**接口路径**: `GET /customer/list`

**请求参数**:
```json
{
  "customerName": "客户名称(可选)",
  "customerType": "客户类型(可选)",
  "customerLevel": "客户等级(可选)", 
  "phone": "联系电话(可选)",
  "status": "状态(可选)",
  "currentPage": 1,
  "pageSize": 10
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "customerCode": "IND250618001",
      "customerName": "张三",
      "customerType": "individual",
      "customerLevel": "normal",
      "contactPerson": "张三",
      "phone": "010-12345678",
      "mobile": "13812345678",
      "totalOrders": 5,
      "totalAmount": "5000.00",
      "status": "1",
      "createTime": "2025-06-18 10:00:00"
    }
  ]
}
```

### 2. 创建客户

**接口路径**: `POST /customer/add`

**请求参数**:
```json
{
  "customerName": "张三",
  "customerType": "individual",
  "customerLevel": "normal",
  "contactPerson": "张三",
  "phone": "010-12345678",
  "mobile": "13812345678",
  "email": "zhangsan@example.com",
  "address": "北京市朝阳区XXX街道",
  "source": "朋友推荐",
  "status": "1"
}
```

### 3. 创建跟进记录

**接口路径**: `POST /customer/follow/add`

**请求参数**:
```json
{
  "customerId": 1,
  "followType": "phone",
  "followTitle": "电话沟通产品需求",
  "followContent": "客户对掐丝珐琅产品很感兴趣，询问了价格和定制服务",
  "followResult": "客户有购买意向，约定下周面谈",
  "nextFollowDate": "2025-06-25 14:00:00",
  "opportunityLevel": "high"
}
```

---

## 验收标准

### 功能验收
- [x] 支持散客、团建、渠道、合作伙伴等4种客户类型管理
- [x] 提供客户基础信息、联系信息、商业信息完整录入
- [x] 实现客户跟进记录的创建和查询
- [x] 支持客户标签管理和分类筛选
- [x] 提供客户统计分析功能
- [x] 支持批量操作和数据导出

### 性能验收
- [x] 客户列表查询响应时间 < 2秒
- [x] 支持10000+客户数据并发访问
- [x] 数据库查询优化，添加必要索引

### 代码质量验收
- [x] 遵循jshERP编码规范
- [x] 完整的参数校验和异常处理
- [x] 多租户数据隔离支持
- [x] 完整的审计日志记录

---

## 交付清单

### 数据库脚本
- [x] `jsh_customer` 客户档案主表
- [x] `jsh_customer_follow` 客户跟进记录表
- [x] `jsh_customer_tag` 客户标签表
- [x] `jsh_customer_channel` 客户渠道信息表

### 后端代码
- [x] Customer.java 客户实体类
- [x] CustomerFollow.java 跟进记录实体类
- [x] CustomerMapper.java 数据访问层
- [x] CustomerService.java 业务逻辑层
- [x] CustomerController.java 控制器层

### 前端代码
- [x] CustomerList.vue 客户列表页面
- [x] CustomerModal.vue 客户表单组件
- [x] CustomerDetailModal.vue 客户详情组件
- [x] CustomerFollowModal.vue 跟进记录组件

### API接口
- [x] 客户CRUD操作接口
- [x] 客户跟进记录接口
- [x] 客户统计分析接口

**预计完成时间**: 2025-06-23

---

**文档结束**

> 本文档详细描述了客户关系管理模块的开发实现，为聆花文化ERP系统提供了完整的客户管理解决方案。