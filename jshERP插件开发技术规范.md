# jshERP 插件开发技术规范

## 📋 文档概述

本文档基于对 jshERP 系统的深入研究，提供了完整的插件开发技术规范和最佳实践指导。文档涵盖了从环境配置到代码实现的全流程开发标准，旨在帮助开发者快速掌握 jshERP 插件开发技术，确保插件质量和系统兼容性。

### 🎯 适用范围
- jshERP 系统版本：3.5.0+
- 插件框架：SpringBoot Plugin Framework 2.2.1-RELEASE
- 技术栈：Spring Boot 2.x + MyBatis 3.x + Vue 3 + Ant Design Vue 1.5.2

### 📚 文档结构
1. [插件开发环境配置标准](#1-插件开发环境配置标准)
2. [项目结构规范](#2-项目结构规范)
3. [三层架构开发规范](#3-三层架构开发规范)
4. [前端Vue组件开发规范](#4-前端vue组件开发规范)
5. [API接口设计规范](#5-api接口设计规范)
6. [数据库设计规范](#6-数据库设计规范)
7. [权限集成规范](#7-权限集成规范)
8. [插件打包部署规范](#8-插件打包部署规范)
9. [代码质量标准](#9-代码质量标准)
10. [完整代码模板](#10-完整代码模板)

---

## 1. 插件开发环境配置标准

### 1.1 基础环境要求

#### 开发工具版本要求
```yaml
必需环境:
  - JDK: OpenJDK 1.8+ (推荐 1.8.0_311)
  - Maven: 3.6.0+ (推荐 3.8.6)
  - Node.js: 16.0.0+ (推荐 16.20.0)
  - npm: 8.0.0+ (推荐 8.19.4)

数据库环境:
  - MySQL: 5.7+ (推荐 5.7.44)
  - Redis: 6.0+ (推荐 6.2.7)

开发工具:
  - IDE: IntelliJ IDEA 2022.3+ / Eclipse 2022-12+
  - 版本控制: Git 2.30+
  - API测试: Postman / Apifox
```

#### 环境变量配置
```bash
# Java环境配置
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk
export PATH=$JAVA_HOME/bin:$PATH

# Maven环境配置
export MAVEN_HOME=/opt/maven
export PATH=$MAVEN_HOME/bin:$PATH

# Node.js环境配置
export NODE_HOME=/usr/local/node
export PATH=$NODE_HOME/bin:$PATH
```

### 1.2 jshERP 开发环境搭建

#### 1.2.1 克隆主项目
```bash
# 克隆 jshERP 主项目
git clone https://gitee.com/jishenghua/JSH_ERP.git
cd JSH_ERP

# 切换到稳定分支
git checkout main
```

#### 1.2.2 数据库初始化
```sql
-- 创建数据库
CREATE DATABASE jsh_erp DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入基础数据
-- 执行 jshERP-boot/src/main/resources/sql/ 目录下的SQL文件
```

#### 1.2.3 配置文件设置
```yaml
# application-dev.yml
server:
  port: 9999

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/jsh_erp?useUnicode=true&characterEncoding=utf8&autoReconnect=true&useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
    username: root
    password: your_password
  
  redis:
    host: localhost
    port: 6379
    password: 
    database: 0

# 插件配置
plugin:
  runMode: dev
  pluginPath: plugins
  pluginConfigFilePath: pluginConfig
  pluginRestControllerPathPrefix: /api/plugin
  enablePluginIdRestControllerPathPrefix: true
```

### 1.3 IDE 开发环境配置

#### 1.3.1 IntelliJ IDEA 配置
```xml
<!-- Maven 配置 -->
<settings>
  <localRepository>/path/to/maven/repository</localRepository>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

#### 1.3.2 代码格式化配置
```xml
<!-- 导入 jshERP 代码格式化规则 -->
<!-- File -> Settings -> Editor -> Code Style -> Java -->
<!-- 导入项目根目录下的 code-style.xml -->
```

---

## 2. 项目结构规范

### 2.1 标准插件项目结构

```
your-plugin/
├── pom.xml                                    # Maven 项目配置文件
├── plugin.properties                          # 插件元信息配置
├── README.md                                  # 插件说明文档
├── CHANGELOG.md                               # 版本更新日志
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourcompany/plugin/
│   │   │       ├── YourPluginApplication.java      # 插件主类
│   │   │       ├── controller/                     # REST 控制器层
│   │   │       │   ├── BaseController.java         # 基础控制器
│   │   │       │   └── YourController.java         # 业务控制器
│   │   │       ├── service/                        # 业务逻辑层
│   │   │       │   ├── BaseService.java            # 基础服务类
│   │   │       │   └── YourService.java            # 业务服务类
│   │   │       ├── datasource/                     # 数据访问层
│   │   │       │   ├── entities/                   # 实体类
│   │   │       │   │   └── YourEntity.java
│   │   │       │   └── mappers/                    # Mapper 接口
│   │   │       │       ├── YourMapper.java         # 基础 Mapper
│   │   │       │       └── YourMapperEx.java       # 扩展 Mapper
│   │   │       ├── config/                         # 配置类
│   │   │       │   ├── PluginConfig.java           # 插件配置
│   │   │       │   └── DatabaseConfig.java         # 数据库配置
│   │   │       ├── constants/                      # 常量定义
│   │   │       │   └── YourConstants.java
│   │   │       ├── utils/                          # 工具类
│   │   │       │   └── YourUtils.java
│   │   │       └── exception/                      # 异常类
│   │   │           └── YourException.java
│   │   └── resources/
│   │       ├── mapper_xml/                         # MyBatis 映射文件
│   │       │   ├── YourMapper.xml
│   │       │   └── YourMapperEx.xml
│   │       ├── sql/                                # 数据库脚本
│   │       │   ├── init.sql                        # 初始化脚本
│   │       │   └── upgrade.sql                     # 升级脚本
│   │       ├── static/                             # 静态资源
│   │       │   ├── js/
│   │       │   │   └── views/                      # Vue 组件
│   │       │   │       ├── YourList.vue            # 列表页面
│   │       │   │       ├── YourForm.vue            # 表单页面
│   │       │   │       └── YourDetail.vue          # 详情页面
│   │       │   ├── css/                            # 样式文件
│   │       │   └── images/                         # 图片资源
│   │       ├── templates/                          # 模板文件
│   │       ├── application-plugin.yml              # 插件配置文件
│   │       └── logback-spring.xml                  # 日志配置
│   └── test/                                       # 测试代码
│       ├── java/
│       │   └── com/yourcompany/plugin/
│       │       ├── controller/                     # 控制器测试
│       │       ├── service/                        # 服务层测试
│       │       └── mapper/                         # 数据访问测试
│       └── resources/
│           └── application-test.yml                # 测试配置
├── docs/                                           # 文档目录
│   ├── API文档.md                                  # API 接口文档
│   ├── 安装指南.md                                 # 安装部署指南
│   ├── 用户手册.md                                 # 用户操作手册
│   └── 开发说明.md                                 # 开发技术说明
└── scripts/                                       # 脚本目录
    ├── build.sh                                   # 构建脚本
    ├── deploy.sh                                  # 部署脚本
    └── test.sh                                    # 测试脚本
```

### 2.2 文件命名规范

#### 2.2.1 Java 类命名规范
```java
// 控制器类命名：业务名称 + Controller
public class CustomerTagController extends BaseController {}

// 服务类命名：业务名称 + Service  
public class CustomerTagService extends BaseService {}

// 实体类命名：业务名称（单数形式）
public class CustomerTag {}

// Mapper 接口命名：实体名称 + Mapper
public interface CustomerTagMapper {}
public interface CustomerTagMapperEx {}  // 扩展 Mapper

// 常量类命名：业务名称 + Constants
public class CustomerTagConstants {}

// 工具类命名：业务名称 + Utils
public class CustomerTagUtils {}
```

#### 2.2.2 前端文件命名规范
```javascript
// Vue 组件命名：业务名称 + 功能描述
CustomerTagList.vue      // 列表页面
CustomerTagForm.vue      // 表单页面  
CustomerTagDetail.vue    // 详情页面

// API 文件命名：业务名称 + Api
customer-tag-api.js      // API 接口定义

// 样式文件命名：业务名称
customer-tag.less        // 样式文件
```

### 2.3 配置文件模板

#### 2.3.1 plugin.properties 模板
```properties
# 插件基本信息
plugin.id=your-plugin-id
plugin.version=1.0.0
plugin.name=Your Plugin Name
plugin.description=Your plugin description here
plugin.provider=Your Company Name
plugin.requires=3.5.0
plugin.class=com.yourcompany.plugin.YourPluginApplication

# 插件分类和标签
plugin.category=business
plugin.tags=customer,management,tag

# 插件依赖（可选）
plugin.dependencies=

# 插件许可证信息
plugin.license=MIT
plugin.licenseUrl=https://opensource.org/licenses/MIT

# 插件主页和文档
plugin.homepage=https://your-website.com
plugin.documentation=https://docs.your-website.com
```

#### 2.3.2 application-plugin.yml 模板
```yaml
# 插件专用配置
your-plugin:
  # 业务配置
  business:
    enable: true
    default-page-size: 20
    max-page-size: 100
  
  # 缓存配置
  cache:
    enable: true
    expire-time: 3600
    key-prefix: "your-plugin:"
  
  # 文件上传配置
  upload:
    max-file-size: 10MB
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx
    upload-path: /uploads/your-plugin/

# 日志配置
logging:
  level:
    com.yourcompany.plugin: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%logger{50}] - %msg%n"

---

## 3. 三层架构开发规范

### 3.1 Controller 层开发规范

#### 3.1.1 基础控制器模板
```java
package com.yourcompany.plugin.controller;

import com.jsh.erp.controller.BaseController;
import com.jsh.erp.utils.ResponseJsonUtil;
import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.datasource.entities.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Base Controller for Plugin
 * Provides common functionality for all plugin controllers
 *
 * @author Your Name
 * @version 1.0.0
 */
@RestController
@Api(tags = "Plugin Base Controller")
public abstract class PluginBaseController extends BaseController {

    /**
     * Get current tenant ID from request
     *
     * @param request HTTP request
     * @return tenant ID
     */
    protected Long getCurrentTenantId(HttpServletRequest request) {
        String token = request.getHeader("X-Access-Token");
        return Long.parseLong(StringUtil.getInfo(token, "tenantId", "0"));
    }

    /**
     * Get current user ID from request
     *
     * @param request HTTP request
     * @return user ID
     */
    protected Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("X-Access-Token");
        return Long.parseLong(StringUtil.getInfo(token, "userId", "0"));
    }

    /**
     * Get current user from session
     *
     * @param request HTTP request
     * @return user object
     */
    protected User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    /**
     * Return success response
     *
     * @param data response data
     * @return success response
     */
    protected BaseResponseInfo success(Object data) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("data", data);
        objectMap.put("message", "操作成功");
        return ResponseJsonUtil.returnStr(objectMap, 1);
    }

    /**
     * Return success response with message
     *
     * @param data response data
     * @param message success message
     * @return success response
     */
    protected BaseResponseInfo success(Object data, String message) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("data", data);
        objectMap.put("message", message);
        return ResponseJsonUtil.returnStr(objectMap, 1);
    }

    /**
     * Return error response
     *
     * @param message error message
     * @return error response
     */
    protected BaseResponseInfo error(String message) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("message", message);
        return ResponseJsonUtil.returnStr(objectMap, 0);
    }
}
```

#### 3.1.2 业务控制器模板
```java
package com.yourcompany.plugin.controller;

import com.yourcompany.plugin.service.YourService;
import com.yourcompany.plugin.datasource.entities.YourEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Your Business Controller
 * Handles HTTP requests for your business logic
 *
 * @author Your Name
 * @version 1.0.0
 */
@RestController
@RequestMapping("/your-business")
@Api(tags = "Your Business Management")
public class YourController extends PluginBaseController {

    @Resource
    private YourService yourService;

    /**
     * Get entity list with pagination
     *
     * @param request HTTP request
     * @return paginated entity list
     */
    @GetMapping("/list")
    @ApiOperation("获取实体列表")
    public BaseResponseInfo getList(HttpServletRequest request) {
        try {
            // Start pagination - automatically gets pageNum and pageSize from request
            startPage();

            // Get list data
            List<Map<String, Object>> list = yourService.getList(request);

            // Return paginated data - automatically wraps total and rows
            return getDataTable(list);
        } catch (Exception e) {
            logger.error("获取列表失败", e);
            return error("获取列表失败：" + e.getMessage());
        }
    }

    /**
     * Get entity by ID
     *
     * @param id entity ID
     * @param request HTTP request
     * @return entity details
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取实体详情")
    public BaseResponseInfo getById(
            @ApiParam("实体ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Map<String, Object> entity = yourService.getById(id, request);
            return success(entity);
        } catch (Exception e) {
            logger.error("获取实体详情失败", e);
            return error("获取实体详情失败：" + e.getMessage());
        }
    }

    /**
     * Add new entity
     *
     * @param entity entity data
     * @param request HTTP request
     * @return operation result
     */
    @PostMapping("/add")
    @ApiOperation("新增实体")
    public BaseResponseInfo add(
            @ApiParam("实体数据") @RequestBody YourEntity entity,
            HttpServletRequest request) {
        try {
            int result = yourService.addEntity(entity, request);
            if (result > 0) {
                return success(null, "新增成功");
            } else {
                return error("新增失败");
            }
        } catch (Exception e) {
            logger.error("新增实体失败", e);
            return error("新增失败：" + e.getMessage());
        }
    }

    /**
     * Update entity
     *
     * @param entity entity data
     * @param request HTTP request
     * @return operation result
     */
    @PutMapping("/update")
    @ApiOperation("更新实体")
    public BaseResponseInfo update(
            @ApiParam("实体数据") @RequestBody YourEntity entity,
            HttpServletRequest request) {
        try {
            int result = yourService.updateEntity(entity, request);
            if (result > 0) {
                return success(null, "更新成功");
            } else {
                return error("更新失败");
            }
        } catch (Exception e) {
            logger.error("更新实体失败", e);
            return error("更新失败：" + e.getMessage());
        }
    }

    /**
     * Delete entity by ID
     *
     * @param id entity ID
     * @param request HTTP request
     * @return operation result
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除实体")
    public BaseResponseInfo delete(
            @ApiParam("实体ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            int result = yourService.deleteEntity(id, request);
            if (result > 0) {
                return success(null, "删除成功");
            } else {
                return error("删除失败");
            }
        } catch (Exception e) {
            logger.error("删除实体失败", e);
            return error("删除失败：" + e.getMessage());
        }
    }

    /**
     * Batch delete entities
     *
     * @param ids entity IDs
     * @param request HTTP request
     * @return operation result
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除实体")
    public BaseResponseInfo batchDelete(
            @ApiParam("实体ID列表") @RequestBody List<Long> ids,
            HttpServletRequest request) {
        try {
            int result = yourService.batchDelete(ids, request);
            return success(null, "批量删除成功，共删除 " + result + " 条记录");
        } catch (Exception e) {
            logger.error("批量删除失败", e);
            return error("批量删除失败：" + e.getMessage());
        }
    }
}
```

### 3.2 Service 层开发规范

#### 3.2.1 基础服务类模板
```java
package com.yourcompany.plugin.service;

import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.datasource.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Base Service for Plugin
 * Provides common functionality for all plugin services
 *
 * @author Your Name
 * @version 1.0.0
 */
@Service
public abstract class PluginBaseService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Get current tenant ID from request
     *
     * @param request HTTP request
     * @return tenant ID
     */
    protected Long getCurrentTenantId(HttpServletRequest request) {
        String token = request.getHeader("X-Access-Token");
        return Long.parseLong(StringUtil.getInfo(token, "tenantId", "0"));
    }

    /**
     * Get current user ID from request
     *
     * @param request HTTP request
     * @return user ID
     */
    protected Long getCurrentUserId(HttpServletRequest request) {
        String token = request.getHeader("X-Access-Token");
        return Long.parseLong(StringUtil.getInfo(token, "userId", "0"));
    }

    /**
     * Get current user from session
     *
     * @param request HTTP request
     * @return user object
     */
    protected User getCurrentUser(HttpServletRequest request) {
        return (User) request.getSession().getAttribute("user");
    }

    /**
     * Validate entity data
     *
     * @param entity entity to validate
     * @throws Exception if validation fails
     */
    protected void validateEntity(Object entity) throws Exception {
        if (entity == null) {
            throw new Exception("实体数据不能为空");
        }
        // Add specific validation logic here
    }

    /**
     * Check user permission
     *
     * @param request HTTP request
     * @param permission required permission
     * @throws Exception if permission check fails
     */
    protected void checkPermission(HttpServletRequest request, String permission) throws Exception {
        User user = getCurrentUser(request);
        if (user == null) {
            throw new Exception("用户未登录或登录已过期");
        }
        // Add permission check logic here
    }

    /**
     * Set common fields for create operation
     *
     * @param entity entity to set fields
     * @param request HTTP request
     */
    protected void setCreateFields(Object entity, HttpServletRequest request) {
        // Use reflection to set common fields like createTime, createBy, tenantId
        // Implementation depends on your entity structure
    }

    /**
     * Set common fields for update operation
     *
     * @param entity entity to set fields
     * @param request HTTP request
     */
    protected void setUpdateFields(Object entity, HttpServletRequest request) {
        // Use reflection to set common fields like updateTime, updateBy
        // Implementation depends on your entity structure
    }
}
```

#### 3.2.2 业务服务类模板
```java
package com.yourcompany.plugin.service;

import com.yourcompany.plugin.datasource.entities.YourEntity;
import com.yourcompany.plugin.datasource.mappers.YourMapper;
import com.yourcompany.plugin.datasource.mappers.YourMapperEx;
import com.yourcompany.plugin.constants.YourConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Your Business Service
 * Handles business logic for your entities
 *
 * @author Your Name
 * @version 1.0.0
 */
@Service
public class YourService extends PluginBaseService {

    @Resource
    private YourMapper yourMapper;

    @Resource
    private YourMapperEx yourMapperEx;

    /**
     * Get entity list with search conditions
     *
     * @param request HTTP request containing search parameters
     * @return entity list
     * @throws Exception if operation fails
     */
    public List<Map<String, Object>> getList(HttpServletRequest request) throws Exception {
        // Get search parameters from request
        String search = request.getParameter("search");
        String name = StringUtil.getInfo(search, "name");
        String status = StringUtil.getInfo(search, "status");
        String startDate = StringUtil.getInfo(search, "startDate");
        String endDate = StringUtil.getInfo(search, "endDate");

        // Get current tenant ID for data isolation
        Long tenantId = getCurrentTenantId(request);

        // Call mapper to get data
        return yourMapperEx.getList(tenantId, name, status, startDate, endDate);
    }

    /**
     * Get entity by ID
     *
     * @param id entity ID
     * @param request HTTP request
     * @return entity details
     * @throws Exception if entity not found or no permission
     */
    public Map<String, Object> getById(Long id, HttpServletRequest request) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("实体ID不能为空");
        }

        Long tenantId = getCurrentTenantId(request);
        Map<String, Object> entity = yourMapperEx.getById(id, tenantId);

        if (entity == null) {
            throw new Exception("实体不存在或无权限访问");
        }

        return entity;
    }

    /**
     * Add new entity
     *
     * @param entity entity data
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int addEntity(YourEntity entity, HttpServletRequest request) throws Exception {
        // Validate entity data
        validateEntity(entity);
        validateBusinessRules(entity, request);

        // Set common fields
        entity.setId(null); // Ensure it's a new entity
        entity.setTenantId(getCurrentTenantId(request));
        entity.setDeleteFlag(YourConstants.DELETE_FLAG_EXISTS);
        entity.setCreateTime(new Date());
        entity.setCreateBy(getCurrentUserId(request));

        // Set default values if needed
        if (StringUtil.isEmpty(entity.getStatus())) {
            entity.setStatus(YourConstants.STATUS_ACTIVE);
        }

        // Save to database
        int result = yourMapper.insertSelective(entity);

        // Log operation
        logger.info("新增实体成功，ID: {}, 操作人: {}", entity.getId(), getCurrentUserId(request));

        return result;
    }

    /**
     * Update entity
     *
     * @param entity entity data
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int updateEntity(YourEntity entity, HttpServletRequest request) throws Exception {
        // Validate entity data
        if (entity.getId() == null || entity.getId() <= 0) {
            throw new Exception("实体ID不能为空");
        }

        // Check if entity exists and belongs to current tenant
        YourEntity existingEntity = yourMapper.selectByPrimaryKey(entity.getId());
        if (existingEntity == null || !existingEntity.getTenantId().equals(getCurrentTenantId(request))) {
            throw new Exception("实体不存在或无权限修改");
        }

        // Validate business rules
        validateEntity(entity);
        validateBusinessRules(entity, request);

        // Set update fields
        entity.setTenantId(getCurrentTenantId(request));
        entity.setUpdateTime(new Date());
        entity.setUpdateBy(getCurrentUserId(request));

        // Update database
        int result = yourMapper.updateByPrimaryKeySelective(entity);

        // Log operation
        logger.info("更新实体成功，ID: {}, 操作人: {}", entity.getId(), getCurrentUserId(request));

        return result;
    }

    /**
     * Delete entity by ID
     *
     * @param id entity ID
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteEntity(Long id, HttpServletRequest request) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("实体ID不能为空");
        }

        // Check if entity exists and belongs to current tenant
        YourEntity existingEntity = yourMapper.selectByPrimaryKey(id);
        if (existingEntity == null || !existingEntity.getTenantId().equals(getCurrentTenantId(request))) {
            throw new Exception("实体不存在或无权限删除");
        }

        // Check if entity can be deleted (business rules)
        validateDeleteOperation(existingEntity, request);

        // Soft delete - set delete flag
        YourEntity updateEntity = new YourEntity();
        updateEntity.setId(id);
        updateEntity.setDeleteFlag(YourConstants.DELETE_FLAG_DELETED);
        updateEntity.setUpdateTime(new Date());
        updateEntity.setUpdateBy(getCurrentUserId(request));

        int result = yourMapper.updateByPrimaryKeySelective(updateEntity);

        // Log operation
        logger.info("删除实体成功，ID: {}, 操作人: {}", id, getCurrentUserId(request));

        return result;
    }

    /**
     * Batch delete entities
     *
     * @param ids entity IDs
     * @param request HTTP request
     * @return number of affected rows
     * @throws Exception if operation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Long> ids, HttpServletRequest request) throws Exception {
        if (ids == null || ids.isEmpty()) {
            throw new Exception("删除ID列表不能为空");
        }

        Long tenantId = getCurrentTenantId(request);
        Long userId = getCurrentUserId(request);

        // Batch soft delete
        int result = yourMapperEx.batchDelete(ids, tenantId, userId);

        // Log operation
        logger.info("批量删除实体成功，数量: {}, 操作人: {}", result, userId);

        return result;
    }

    /**
     * Validate business rules
     *
     * @param entity entity to validate
     * @param request HTTP request
     * @throws Exception if validation fails
     */
    private void validateBusinessRules(YourEntity entity, HttpServletRequest request) throws Exception {
        // Add your specific business validation logic here
        if (StringUtil.isEmpty(entity.getName())) {
            throw new Exception("实体名称不能为空");
        }

        // Check for duplicate names within tenant
        Long tenantId = getCurrentTenantId(request);
        boolean exists = yourMapperEx.existsByName(entity.getName(), entity.getId(), tenantId);
        if (exists) {
            throw new Exception("实体名称已存在");
        }
    }

    /**
     * Validate delete operation
     *
     * @param entity entity to delete
     * @param request HTTP request
     * @throws Exception if delete is not allowed
     */
    private void validateDeleteOperation(YourEntity entity, HttpServletRequest request) throws Exception {
        // Add your specific delete validation logic here
        // For example, check if entity is referenced by other entities

        if (YourConstants.STATUS_LOCKED.equals(entity.getStatus())) {
            throw new Exception("锁定状态的实体不能删除");
        }
    }
}
```

### 3.3 Mapper 层开发规范

#### 3.3.1 基础 Mapper 接口模板
```java
package com.yourcompany.plugin.datasource.mappers;

import com.yourcompany.plugin.datasource.entities.YourEntity;

/**
 * Your Entity Basic Mapper Interface
 * Provides standard CRUD operations
 *
 * @author Your Name
 * @version 1.0.0
 */
public interface YourMapper {

    /**
     * Select by primary key
     *
     * @param id primary key
     * @return entity or null if not found
     */
    YourEntity selectByPrimaryKey(Long id);

    /**
     * Insert entity selectively
     * Only insert non-null fields
     *
     * @param record entity to insert
     * @return number of affected rows
     */
    int insertSelective(YourEntity record);

    /**
     * Update by primary key selectively
     * Only update non-null fields
     *
     * @param record entity to update
     * @return number of affected rows
     */
    int updateByPrimaryKeySelective(YourEntity record);

    /**
     * Delete by primary key
     * Physical delete - use with caution
     *
     * @param id primary key
     * @return number of affected rows
     */
    int deleteByPrimaryKey(Long id);
}
```

#### 3.3.2 扩展 Mapper 接口模板
```java
package com.yourcompany.plugin.datasource.mappers;

import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * Your Entity Extended Mapper Interface
 * Provides complex queries and business-specific operations
 *
 * @author Your Name
 * @version 1.0.0
 */
public interface YourMapperEx {

    /**
     * Get entity list with search conditions
     *
     * @param tenantId tenant ID for data isolation
     * @param name entity name (fuzzy search)
     * @param status entity status
     * @param startDate start date for date range search
     * @param endDate end date for date range search
     * @return entity list with additional information
     */
    List<Map<String, Object>> getList(@Param("tenantId") Long tenantId,
                                     @Param("name") String name,
                                     @Param("status") String status,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);

    /**
     * Get entity details by ID
     *
     * @param id entity ID
     * @param tenantId tenant ID for data isolation
     * @return entity details with related information
     */
    Map<String, Object> getById(@Param("id") Long id,
                               @Param("tenantId") Long tenantId);

    /**
     * Check if entity name exists
     *
     * @param name entity name
     * @param excludeId entity ID to exclude (for update operations)
     * @param tenantId tenant ID for data isolation
     * @return true if exists, false otherwise
     */
    boolean existsByName(@Param("name") String name,
                        @Param("excludeId") Long excludeId,
                        @Param("tenantId") Long tenantId);

    /**
     * Batch delete entities (soft delete)
     *
     * @param ids entity IDs to delete
     * @param tenantId tenant ID for data isolation
     * @param updateBy user ID who performs the operation
     * @return number of affected rows
     */
    int batchDelete(@Param("ids") List<Long> ids,
                   @Param("tenantId") Long tenantId,
                   @Param("updateBy") Long updateBy);

    /**
     * Get entity statistics
     *
     * @param tenantId tenant ID for data isolation
     * @param startDate start date for statistics
     * @param endDate end date for statistics
     * @return statistics data
     */
    Map<String, Object> getStatistics(@Param("tenantId") Long tenantId,
                                     @Param("startDate") String startDate,
                                     @Param("endDate") String endDate);

    /**
     * Get entities by status
     *
     * @param status entity status
     * @param tenantId tenant ID for data isolation
     * @return entity list
     */
    List<Map<String, Object>> getByStatus(@Param("status") String status,
                                         @Param("tenantId") Long tenantId);
}
```

---

## 4. 前端Vue组件开发规范

### 4.1 Vue 3 Composition API 开发标准

#### 4.1.1 组件基础结构模板
```vue
<template>
  <div class="your-component-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>{{ pageTitle }}</h2>
      <div class="page-description">{{ pageDescription }}</div>
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <a-card :bordered="false" size="small">
        <a-form
          ref="searchFormRef"
          :model="searchForm"
          layout="inline"
          class="search-form"
        >
          <a-form-item label="名称" name="name">
            <a-input
              v-model:value="searchForm.name"
              placeholder="请输入名称"
              allow-clear
              @press-enter="handleSearch"
            />
          </a-form-item>

          <a-form-item label="状态" name="status">
            <a-select
              v-model:value="searchForm.status"
              placeholder="请选择状态"
              allow-clear
              style="width: 120px"
            >
              <a-select-option value="active">启用</a-select-option>
              <a-select-option value="inactive">禁用</a-select-option>
            </a-select>
          </a-form-item>

          <a-form-item label="创建时间" name="dateRange">
            <a-range-picker
              v-model:value="searchForm.dateRange"
              format="YYYY-MM-DD"
              :placeholder="['开始日期', '结束日期']"
            />
          </a-form-item>

          <a-form-item>
            <a-space>
              <a-button type="primary" @click="handleSearch">
                <template #icon><SearchOutlined /></template>
                搜索
              </a-button>
              <a-button @click="handleReset">
                <template #icon><ReloadOutlined /></template>
                重置
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-card>
    </div>

    <!-- 操作区域 -->
    <div class="action-section">
      <a-card :bordered="false" size="small">
        <a-space>
          <a-button type="primary" @click="handleAdd">
            <template #icon><PlusOutlined /></template>
            新增
          </a-button>
          <a-button
            type="danger"
            :disabled="!hasSelected"
            @click="handleBatchDelete"
          >
            <template #icon><DeleteOutlined /></template>
            批量删除
          </a-button>
          <a-button @click="handleExport">
            <template #icon><ExportOutlined /></template>
            导出
          </a-button>
        </a-space>
      </a-card>
    </div>

    <!-- 表格区域 -->
    <div class="table-section">
      <a-card :bordered="false">
        <a-table
          ref="tableRef"
          :columns="columns"
          :data-source="dataSource"
          :pagination="pagination"
          :loading="loading"
          :row-selection="rowSelection"
          :scroll="{ x: 1200 }"
          size="middle"
          @change="handleTableChange"
        >
          <!-- 状态列自定义渲染 -->
          <template #status="{ record }">
            <a-tag :color="record.status === 'active' ? 'green' : 'red'">
              {{ record.status === 'active' ? '启用' : '禁用' }}
            </a-tag>
          </template>

          <!-- 操作列自定义渲染 -->
          <template #action="{ record }">
            <a-space>
              <a-button size="small" type="link" @click="handleView(record)">
                查看
              </a-button>
              <a-button size="small" type="link" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-popconfirm
                title="确定要删除这条记录吗？"
                @confirm="handleDelete(record)"
              >
                <a-button size="small" type="link" danger>
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </a-table>
      </a-card>
    </div>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      :width="800"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
    >
      <a-form
        ref="formRef"
        :model="form"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="名称" name="name">
          <a-input v-model:value="form.name" placeholder="请输入名称" />
        </a-form-item>

        <a-form-item label="状态" name="status">
          <a-radio-group v-model:value="form.status">
            <a-radio value="active">启用</a-radio>
            <a-radio value="inactive">禁用</a-radio>
          </a-radio-group>
        </a-form-item>

        <a-form-item label="描述" name="description">
          <a-textarea
            v-model:value="form.description"
            placeholder="请输入描述"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, computed, onMounted, watch } from 'vue'
import { message, Modal } from 'ant-design-vue'
import {
  SearchOutlined,
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  ExportOutlined
} from '@ant-design/icons-vue'
import { yourEntityApi } from '@/api/your-entity-api'

export default defineComponent({
  name: 'YourEntityList',
  components: {
    SearchOutlined,
    ReloadOutlined,
    PlusOutlined,
    DeleteOutlined,
    ExportOutlined
  },
  setup() {
    // ==================== 响应式数据定义 ====================

    // 页面基础信息
    const pageTitle = ref('实体管理')
    const pageDescription = ref('管理系统中的实体信息')

    // 加载状态
    const loading = ref(false)
    const modalLoading = ref(false)

    // 表格数据
    const dataSource = ref([])
    const selectedRowKeys = ref([])

    // 弹窗状态
    const modalVisible = ref(false)
    const modalTitle = ref('')
    const isEdit = ref(false)

    // 表单引用
    const searchFormRef = ref()
    const formRef = ref()
    const tableRef = ref()

    // 搜索表单数据
    const searchForm = reactive({
      name: '',
      status: undefined,
      dateRange: []
    })

    // 编辑表单数据
    const form = reactive({
      id: null,
      name: '',
      status: 'active',
      description: ''
    })

    // 分页配置
    const pagination = reactive({
      current: 1,
      pageSize: 20,
      total: 0,
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: (total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
    })

    // ==================== 计算属性 ====================

    // 是否有选中的行
    const hasSelected = computed(() => selectedRowKeys.value.length > 0)

    // 弹窗标题
    const computedModalTitle = computed(() => {
      return isEdit.value ? '编辑实体' : '新增实体'
    })

    // ==================== 表格配置 ====================

    // 表格列定义
    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
        width: 80,
        sorter: true
      },
      {
        title: '名称',
        dataIndex: 'name',
        key: 'name',
        width: 200,
        ellipsis: true
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        width: 100,
        slots: { customRender: 'status' }
      },
      {
        title: '描述',
        dataIndex: 'description',
        key: 'description',
        ellipsis: true
      },
      {
        title: '创建时间',
        dataIndex: 'createTime',
        key: 'createTime',
        width: 180,
        sorter: true
      },
      {
        title: '操作',
        key: 'action',
        width: 200,
        fixed: 'right',
        slots: { customRender: 'action' }
      }
    ]

    // 行选择配置
    const rowSelection = {
      selectedRowKeys: selectedRowKeys,
      onChange: (keys) => {
        selectedRowKeys.value = keys
      },
      onSelectAll: (selected, selectedRows, changeRows) => {
        console.log('onSelectAll', selected, selectedRows, changeRows)
      }
    }

    // 表单验证规则
    const rules = {
      name: [
        { required: true, message: '请输入名称', trigger: 'blur' },
        { min: 2, max: 50, message: '名称长度在 2 到 50 个字符', trigger: 'blur' }
      ],
      status: [
        { required: true, message: '请选择状态', trigger: 'change' }
      ]
    }

    // ==================== 方法定义 ====================

    /**
     * 加载表格数据
     */
    const loadData = async () => {
      try {
        loading.value = true

        // 构建查询参数
        const params = {
          current: pagination.current,
          pageSize: pagination.pageSize,
          search: JSON.stringify({
            name: searchForm.name,
            status: searchForm.status,
            startDate: searchForm.dateRange?.[0]?.format('YYYY-MM-DD'),
            endDate: searchForm.dateRange?.[1]?.format('YYYY-MM-DD')
          })
        }

        // 调用API
        const response = await yourEntityApi.getList(params)

        // 更新数据
        dataSource.value = response.rows || []
        pagination.total = response.total || 0

      } catch (error) {
        console.error('加载数据失败:', error)
        message.error('加载数据失败：' + (error.message || '未知错误'))
      } finally {
        loading.value = false
      }
    }

    /**
     * 搜索处理
     */
    const handleSearch = () => {
      pagination.current = 1
      loadData()
    }

    /**
     * 重置搜索
     */
    const handleReset = () => {
      searchFormRef.value?.resetFields()
      Object.assign(searchForm, {
        name: '',
        status: undefined,
        dateRange: []
      })
      pagination.current = 1
      loadData()
    }

    /**
     * 表格变化处理（分页、排序、筛选）
     */
    const handleTableChange = (pag, filters, sorter) => {
      pagination.current = pag.current
      pagination.pageSize = pag.pageSize
      loadData()
    }

    /**
     * 新增处理
     */
    const handleAdd = () => {
      isEdit.value = false
      modalTitle.value = '新增实体'

      // 重置表单
      Object.assign(form, {
        id: null,
        name: '',
        status: 'active',
        description: ''
      })

      modalVisible.value = true
    }

    /**
     * 编辑处理
     */
    const handleEdit = async (record) => {
      try {
        isEdit.value = true
        modalTitle.value = '编辑实体'

        // 获取详细信息
        const response = await yourEntityApi.getById(record.id)
        Object.assign(form, response.data)

        modalVisible.value = true
      } catch (error) {
        message.error('获取详情失败：' + error.message)
      }
    }

    /**
     * 查看处理
     */
    const handleView = async (record) => {
      try {
        const response = await yourEntityApi.getById(record.id)

        // 显示详情弹窗或跳转到详情页面
        Modal.info({
          title: '实体详情',
          width: 600,
          content: h('div', [
            h('p', `名称: ${response.data.name}`),
            h('p', `状态: ${response.data.status}`),
            h('p', `描述: ${response.data.description}`),
            h('p', `创建时间: ${response.data.createTime}`)
          ])
        })
      } catch (error) {
        message.error('获取详情失败：' + error.message)
      }
    }

    /**
     * 删除处理
     */
    const handleDelete = async (record) => {
      try {
        await yourEntityApi.delete(record.id)
        message.success('删除成功')
        loadData()
      } catch (error) {
        message.error('删除失败：' + error.message)
      }
    }

    /**
     * 批量删除处理
     */
    const handleBatchDelete = () => {
      if (selectedRowKeys.value.length === 0) {
        message.warning('请选择要删除的记录')
        return
      }

      Modal.confirm({
        title: '确认删除',
        content: `确定要删除选中的 ${selectedRowKeys.value.length} 条记录吗？`,
        onOk: async () => {
          try {
            await yourEntityApi.batchDelete(selectedRowKeys.value)
            message.success('批量删除成功')
            selectedRowKeys.value = []
            loadData()
          } catch (error) {
            message.error('批量删除失败：' + error.message)
          }
        }
      })
    }

    /**
     * 导出处理
     */
    const handleExport = async () => {
      try {
        loading.value = true
        const params = {
          search: JSON.stringify({
            name: searchForm.name,
            status: searchForm.status,
            startDate: searchForm.dateRange?.[0]?.format('YYYY-MM-DD'),
            endDate: searchForm.dateRange?.[1]?.format('YYYY-MM-DD')
          })
        }

        await yourEntityApi.export(params)
        message.success('导出成功')
      } catch (error) {
        message.error('导出失败：' + error.message)
      } finally {
        loading.value = false
      }
    }

    /**
     * 弹窗确定处理
     */
    const handleModalOk = async () => {
      try {
        // 表单验证
        await formRef.value?.validate()

        modalLoading.value = true

        if (isEdit.value) {
          // 更新
          await yourEntityApi.update(form)
          message.success('更新成功')
        } else {
          // 新增
          await yourEntityApi.add(form)
          message.success('新增成功')
        }

        modalVisible.value = false
        loadData()

      } catch (error) {
        if (error.errorFields) {
          // 表单验证错误
          return
        }
        message.error((isEdit.value ? '更新' : '新增') + '失败：' + error.message)
      } finally {
        modalLoading.value = false
      }
    }

    /**
     * 弹窗取消处理
     */
    const handleModalCancel = () => {
      modalVisible.value = false
      formRef.value?.resetFields()
    }

    // ==================== 生命周期钩子 ====================

    onMounted(() => {
      loadData()
    })

    // ==================== 监听器 ====================

    // 监听搜索表单变化，实现实时搜索（可选）
    watch(
      () => searchForm.name,
      (newVal) => {
        // 可以实现防抖搜索
        // debounce(() => handleSearch(), 500)
      }
    )

    // ==================== 返回数据和方法 ====================

    return {
      // 页面数据
      pageTitle,
      pageDescription,

      // 状态数据
      loading,
      modalLoading,
      dataSource,
      selectedRowKeys,
      modalVisible,
      modalTitle: computedModalTitle,
      hasSelected,

      // 表单数据
      searchForm,
      form,
      pagination,

      // 表格配置
      columns,
      rowSelection,
      rules,

      // 表单引用
      searchFormRef,
      formRef,
      tableRef,

      // 方法
      loadData,
      handleSearch,
      handleReset,
      handleTableChange,
      handleAdd,
      handleEdit,
      handleView,
      handleDelete,
      handleBatchDelete,
      handleExport,
      handleModalOk,
      handleModalCancel
    }
  }
})
</script>

<style lang="less" scoped>
.your-component-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;

  .page-header {
    margin-bottom: 24px;

    h2 {
      margin: 0 0 8px 0;
      font-size: 20px;
      font-weight: 500;
      color: #262626;
    }

    .page-description {
      color: #8c8c8c;
      font-size: 14px;
    }
  }

  .search-section,
  .action-section,
  .table-section {
    margin-bottom: 16px;
  }

  .search-form {
    .ant-form-item {
      margin-bottom: 16px;
    }
  }

  :deep(.ant-table) {
    .ant-table-thead > tr > th {
      background: #fafafa;
      font-weight: 500;
    }

    .ant-table-tbody > tr:hover > td {
      background: #f5f5f5;
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    padding: 16px;

    .search-form {
      .ant-form-item {
        margin-bottom: 12px;
      }
    }

    .action-section {
      .ant-space {
        flex-wrap: wrap;
      }
    }
  }
}
</style>
```

---

## 5. API接口设计规范

### 5.1 REST API 路径设计标准

#### 5.1.1 插件API路径规范
```javascript
// 插件API路径格式
/api/plugin/{pluginId}/{resource}/{action}

// 示例路径
/api/plugin/customer-tag/tag/list          // 获取标签列表
/api/plugin/customer-tag/tag/add           // 新增标签
/api/plugin/customer-tag/tag/update        // 更新标签
/api/plugin/customer-tag/tag/delete/{id}   // 删除标签
/api/plugin/customer-tag/tag/{id}          // 获取标签详情

// 复杂业务操作
/api/plugin/customer-tag/tag/batch-delete  // 批量删除
/api/plugin/customer-tag/tag/export        // 导出数据
/api/plugin/customer-tag/tag/import        // 导入数据
/api/plugin/customer-tag/report/summary    // 统计报表
```

#### 5.1.2 HTTP方法使用规范
```javascript
// GET - 查询操作
GET /api/plugin/{pluginId}/entity/list      // 获取列表
GET /api/plugin/{pluginId}/entity/{id}      // 获取详情
GET /api/plugin/{pluginId}/entity/search    // 搜索查询

// POST - 创建操作
POST /api/plugin/{pluginId}/entity/add      // 新增实体
POST /api/plugin/{pluginId}/entity/batch    // 批量操作
POST /api/plugin/{pluginId}/entity/import   // 导入数据

// PUT - 更新操作
PUT /api/plugin/{pluginId}/entity/update    // 更新实体
PUT /api/plugin/{pluginId}/entity/{id}      // 更新指定实体

// DELETE - 删除操作
DELETE /api/plugin/{pluginId}/entity/{id}   // 删除实体
DELETE /api/plugin/{pluginId}/entity/batch  // 批量删除
```

### 5.2 请求响应格式规范

#### 5.2.1 统一响应格式
```json
// 成功响应格式
{
  "code": 200,
  "data": {
    "rows": [...],           // 列表数据
    "total": 100,           // 总数（分页时）
    "message": "操作成功"    // 成功消息
  }
}

// 错误响应格式
{
  "code": 500,
  "data": "错误信息描述"
}

// 分页响应格式
{
  "code": 200,
  "data": {
    "rows": [
      {
        "id": 1,
        "name": "实体名称",
        "status": "active",
        "createTime": "2023-12-01 10:00:00"
      }
    ],
    "total": 100,
    "current": 1,
    "pageSize": 20,
    "message": "查询成功"
  }
}
```

#### 5.2.2 请求参数格式
```javascript
// 列表查询参数
{
  "current": 1,              // 当前页码
  "pageSize": 20,            // 每页大小
  "search": "{\"name\":\"关键词\",\"status\":\"active\"}"  // 搜索条件JSON字符串
}

// 新增/更新参数
{
  "name": "实体名称",
  "status": "active",
  "description": "实体描述",
  "customField": "自定义字段值"
}

// 批量操作参数
{
  "ids": [1, 2, 3, 4, 5],    // ID数组
  "action": "delete",        // 操作类型
  "params": {                // 额外参数
    "reason": "批量删除原因"
  }
}
```

### 5.3 API接口实现模板

#### 5.3.1 前端API调用模板
```javascript
// api/your-entity-api.js
import { getAction, postAction, putAction, deleteAction } from '@/api/manage'

const API_BASE = '/api/plugin/your-plugin'

export const yourEntityApi = {
  /**
   * 获取实体列表
   * @param {Object} params 查询参数
   * @returns {Promise} API响应
   */
  getList(params) {
    return getAction(`${API_BASE}/entity/list`, params)
  },

  /**
   * 获取实体详情
   * @param {Number} id 实体ID
   * @returns {Promise} API响应
   */
  getById(id) {
    return getAction(`${API_BASE}/entity/${id}`)
  },

  /**
   * 新增实体
   * @param {Object} data 实体数据
   * @returns {Promise} API响应
   */
  add(data) {
    return postAction(`${API_BASE}/entity/add`, data)
  },

  /**
   * 更新实体
   * @param {Object} data 实体数据
   * @returns {Promise} API响应
   */
  update(data) {
    return putAction(`${API_BASE}/entity/update`, data)
  },

  /**
   * 删除实体
   * @param {Number} id 实体ID
   * @returns {Promise} API响应
   */
  delete(id) {
    return deleteAction(`${API_BASE}/entity/${id}`)
  },

  /**
   * 批量删除实体
   * @param {Array} ids 实体ID数组
   * @returns {Promise} API响应
   */
  batchDelete(ids) {
    return postAction(`${API_BASE}/entity/batch-delete`, { ids })
  },

  /**
   * 导出数据
   * @param {Object} params 导出参数
   * @returns {Promise} API响应
   */
  export(params) {
    return postAction(`${API_BASE}/entity/export`, params, {
      responseType: 'blob'
    })
  },

  /**
   * 获取统计数据
   * @param {Object} params 统计参数
   * @returns {Promise} API响应
   */
  getStatistics(params) {
    return getAction(`${API_BASE}/report/statistics`, params)
  }
}
```

---

## 6. 数据库设计规范

### 6.1 表结构设计标准

#### 6.1.1 表命名规范
```sql
-- 表名格式：jsh_ + 插件功能名称 + 业务实体名称
-- 示例：
jsh_customer_tag           -- 客户标签表
jsh_customer_tag_relation  -- 客户标签关系表
jsh_inventory_check_plan   -- 盘点计划表
jsh_inventory_check_record -- 盘点记录表

-- 命名规则：
-- 1. 必须以 jsh_ 开头
-- 2. 使用下划线分隔单词
-- 3. 全部小写字母
-- 4. 名称要具有描述性
-- 5. 避免使用缩写
```

#### 6.1.2 标准字段规范
```sql
-- 所有业务表必须包含的标准字段
CREATE TABLE `jsh_your_entity` (
    -- 主键字段
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

    -- 业务字段（根据实际需求定义）
    `name` VARCHAR(100) NOT NULL COMMENT '名称',
    `code` VARCHAR(50) COMMENT '编码',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    `description` TEXT COMMENT '描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',

    -- 多租户字段（必需）
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',

    -- 软删除字段（必需）
    `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记(0-存在,1-删除)',

    -- 审计字段（必需）
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT(20) COMMENT '创建人ID',
    `update_by` BIGINT(20) COMMENT '更新人ID',

    -- 索引定义
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_delete_flag` (`delete_flag`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_name` (`name`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='你的实体表';
```

#### 6.1.3 字段类型规范
```sql
-- 字符串类型
VARCHAR(50)     -- 短字符串（名称、编码等）
VARCHAR(100)    -- 中等字符串（标题、描述等）
VARCHAR(200)    -- 长字符串（地址、备注等）
TEXT            -- 长文本（详细描述、内容等）
LONGTEXT        -- 超长文本（JSON数据、配置等）

-- 数值类型
TINYINT(1)      -- 布尔值（0/1）
INT             -- 整数（数量、排序等）
BIGINT(20)      -- 长整数（ID、金额分等）
DECIMAL(10,2)   -- 精确小数（金额、比率等）
DOUBLE          -- 浮点数（坐标、系数等）

-- 日期时间类型
DATE            -- 日期（生日、截止日期等）
DATETIME        -- 日期时间（创建时间、更新时间等）
TIMESTAMP       -- 时间戳（自动更新时间等）

-- 枚举类型（使用VARCHAR存储）
VARCHAR(20)     -- 状态枚举（active/inactive等）
```

### 6.2 多租户数据隔离设计

#### 6.2.1 租户隔离策略
```sql
-- 策略1：所有业务表添加 tenant_id 字段
ALTER TABLE jsh_your_entity ADD COLUMN `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID';
ALTER TABLE jsh_your_entity ADD INDEX `idx_tenant_id` (`tenant_id`);

-- 策略2：查询时必须包含租户条件
-- 正确的查询方式
SELECT * FROM jsh_your_entity
WHERE tenant_id = #{tenantId}
  AND delete_flag = '0'
  AND name LIKE CONCAT('%', #{name}, '%');

-- 错误的查询方式（缺少租户隔离）
SELECT * FROM jsh_your_entity
WHERE delete_flag = '0'
  AND name LIKE CONCAT('%', #{name}, '%');
```

#### 6.2.2 MyBatis Plus 自动租户隔离
```java
// 配置 MyBatis Plus 多租户插件
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加多租户插件
        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        tenantInterceptor.setTenantLineHandler(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                // 从当前上下文获取租户ID
                Long tenantId = TenantContextHolder.getTenantId();
                return new LongValue(tenantId);
            }

            @Override
            public String getTenantIdColumn() {
                return "tenant_id";
            }

            @Override
            public boolean ignoreTable(String tableName) {
                // 忽略系统表
                return "jsh_user".equals(tableName)
                    || "jsh_role".equals(tableName)
                    || "jsh_function".equals(tableName);
            }
        });

        interceptor.addInnerInterceptor(tenantInterceptor);
        return interceptor;
    }
}
```

### 6.3 数据库脚本管理

#### 6.3.1 初始化脚本模板
```sql
-- init.sql - 插件初始化脚本
-- =====================================================
-- 插件名称: Your Plugin Name
-- 版本: 1.0.0
-- 创建时间: 2023-12-01
-- 说明: 创建插件相关的数据库表和初始数据
-- =====================================================

-- 1. 创建主业务表
CREATE TABLE IF NOT EXISTS `jsh_your_entity` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '名称',
    `code` VARCHAR(50) COMMENT '编码',
    `status` VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    `description` TEXT COMMENT '描述',
    `sort_order` INT DEFAULT 0 COMMENT '排序',

    -- 标准字段
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
    `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT(20) COMMENT '创建人ID',
    `update_by` BIGINT(20) COMMENT '更新人ID',

    -- 索引
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_delete_flag` (`delete_flag`),
    INDEX `idx_name` (`name`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='你的实体表';

-- 2. 创建关联表（如果需要）
CREATE TABLE IF NOT EXISTS `jsh_your_entity_relation` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `entity_id` BIGINT(20) NOT NULL COMMENT '实体ID',
    `related_id` BIGINT(20) NOT NULL COMMENT '关联ID',
    `relation_type` VARCHAR(50) NOT NULL COMMENT '关联类型',

    -- 标准字段
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
    `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) COMMENT '创建人ID',

    -- 索引
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_entity_id` (`entity_id`),
    INDEX `idx_related_id` (`related_id`),
    UNIQUE KEY `uk_entity_related` (`entity_id`, `related_id`, `relation_type`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实体关联表';

-- 3. 插入功能权限数据
INSERT INTO `jsh_function` (
    `number`, `name`, `parent_number`, `url`, `component`,
    `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`,
    `tenant_id`, `delete_flag`
) VALUES (
    'your-plugin', '你的插件', '', '/your-plugin', 'your-plugin/index',
    '0', 100, '1', '电脑版',
    '[{"title":"查看","value":"view"},{"title":"新增","value":"add"},{"title":"修改","value":"edit"},{"title":"删除","value":"delete"}]',
    'your-icon',
    0, '0'
) ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `url` = VALUES(`url`),
    `component` = VALUES(`component`);

-- 4. 插入初始数据（如果需要）
INSERT INTO `jsh_your_entity` (
    `name`, `code`, `status`, `description`, `sort_order`,
    `tenant_id`, `delete_flag`, `create_time`, `create_by`
) VALUES
    ('默认实体', 'DEFAULT', 'active', '系统默认实体', 1, 0, '0', NOW(), 0)
ON DUPLICATE KEY UPDATE
    `description` = VALUES(`description`);

-- 5. 创建序列表（如果需要自定义编号）
CREATE TABLE IF NOT EXISTS `jsh_your_sequence` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `seq_name` VARCHAR(50) NOT NULL COMMENT '序列名称',
    `current_val` BIGINT(20) DEFAULT 0 COMMENT '当前值',
    `increment_val` INT DEFAULT 1 COMMENT '增长步长',
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',

    UNIQUE KEY `uk_seq_tenant` (`seq_name`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='序列表';

-- 插入序列初始数据
INSERT INTO `jsh_your_sequence` (`seq_name`, `current_val`, `tenant_id`)
VALUES ('YOUR_ENTITY_CODE', 1000, 0)
ON DUPLICATE KEY UPDATE `current_val` = VALUES(`current_val`);
```

#### 6.3.2 升级脚本模板
```sql
-- upgrade.sql - 插件升级脚本
-- =====================================================
-- 插件名称: Your Plugin Name
-- 版本: 1.1.0 (从 1.0.0 升级)
-- 升级时间: 2023-12-15
-- 说明: 升级插件数据库结构和数据
-- =====================================================

-- 版本检查
SET @plugin_version = '1.1.0';
SET @plugin_name = 'your-plugin';

-- 1. 添加新字段
ALTER TABLE `jsh_your_entity`
ADD COLUMN `new_field` VARCHAR(100) COMMENT '新增字段' AFTER `description`,
ADD COLUMN `config_json` JSON COMMENT '配置JSON' AFTER `new_field`;

-- 2. 修改现有字段
ALTER TABLE `jsh_your_entity`
MODIFY COLUMN `description` TEXT COMMENT '描述信息（已修改）';

-- 3. 添加新索引
ALTER TABLE `jsh_your_entity`
ADD INDEX `idx_new_field` (`new_field`);

-- 4. 创建新表
CREATE TABLE IF NOT EXISTS `jsh_your_entity_log` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `entity_id` BIGINT(20) NOT NULL COMMENT '实体ID',
    `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型',
    `old_data` JSON COMMENT '旧数据',
    `new_data` JSON COMMENT '新数据',
    `operation_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `operator_id` BIGINT(20) COMMENT '操作人ID',
    `tenant_id` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',

    INDEX `idx_entity_id` (`entity_id`),
    INDEX `idx_tenant_id` (`tenant_id`),
    INDEX `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='实体操作日志表';

-- 5. 数据迁移
UPDATE `jsh_your_entity`
SET `new_field` = CONCAT('migrated_', `code`)
WHERE `new_field` IS NULL;

-- 6. 更新功能权限
UPDATE `jsh_function`
SET `push_btn` = '[{"title":"查看","value":"view"},{"title":"新增","value":"add"},{"title":"修改","value":"edit"},{"title":"删除","value":"delete"},{"title":"导出","value":"export"}]'
WHERE `number` = 'your-plugin';

-- 7. 插入新的功能权限
INSERT INTO `jsh_function` (
    `number`, `name`, `parent_number`, `url`, `component`,
    `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`,
    `tenant_id`, `delete_flag`
) VALUES (
    'your-plugin-log', '操作日志', 'your-plugin', '/your-plugin/log', 'your-plugin/log/index',
    '0', 101, '1', '电脑版',
    '[{"title":"查看","value":"view"},{"title":"导出","value":"export"}]',
    'log-icon',
    0, '0'
) ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`);

-- 8. 记录升级日志
INSERT INTO `jsh_plugin_upgrade_log` (
    `plugin_name`, `from_version`, `to_version`, `upgrade_time`, `upgrade_sql`
) VALUES (
    @plugin_name, '1.0.0', @plugin_version, NOW(), 'upgrade.sql'
);
```

---

## 7. 权限集成规范

### 7.1 功能权限注册

#### 7.1.1 权限表结构说明
```sql
-- jsh_function 表结构说明
CREATE TABLE `jsh_function` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '功能ID',
    `number` VARCHAR(50) NOT NULL COMMENT '功能编号',
    `name` VARCHAR(50) NOT NULL COMMENT '功能名称',
    `parent_number` VARCHAR(50) COMMENT '父功能编号',
    `url` VARCHAR(100) COMMENT '访问URL',
    `component` VARCHAR(100) COMMENT '前端组件路径',
    `state` VARCHAR(1) DEFAULT '0' COMMENT '收缩状态(0-展开,1-收缩)',
    `sort` INT DEFAULT 0 COMMENT '排序',
    `enabled` VARCHAR(1) DEFAULT '1' COMMENT '是否启用(0-禁用,1-启用)',
    `type` VARCHAR(10) DEFAULT '电脑版' COMMENT '功能类型',
    `push_btn` TEXT COMMENT '按钮权限配置(JSON格式)',
    `icon` VARCHAR(50) COMMENT '图标类名',
    `tenant_id` BIGINT(20) DEFAULT 0 COMMENT '租户ID',
    `delete_flag` VARCHAR(1) DEFAULT '0' COMMENT '删除标记'
);
```

#### 7.1.2 插件权限注册模板
```sql
-- 插件权限注册脚本
-- 1. 主功能权限
INSERT INTO `jsh_function` (
    `number`, `name`, `parent_number`, `url`, `component`,
    `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`,
    `tenant_id`, `delete_flag`
) VALUES (
    -- 主菜单
    'customer-tag', '客户标签管理', '', '/customer-tag', 'customer-tag/index',
    '0', 200, '1', '电脑版',
    '[
        {"title":"查看","value":"view"},
        {"title":"新增","value":"add"},
        {"title":"修改","value":"edit"},
        {"title":"删除","value":"delete"},
        {"title":"导出","value":"export"},
        {"title":"导入","value":"import"}
    ]',
    'tags',
    0, '0'
) ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `url` = VALUES(`url`),
    `component` = VALUES(`component`),
    `push_btn` = VALUES(`push_btn`);

-- 2. 子功能权限（如果有）
INSERT INTO `jsh_function` (
    `number`, `name`, `parent_number`, `url`, `component`,
    `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`,
    `tenant_id`, `delete_flag`
) VALUES (
    -- 标签管理子功能
    'customer-tag-manage', '标签管理', 'customer-tag', '/customer-tag/manage', 'customer-tag/manage/index',
    '0', 201, '1', '电脑版',
    '[
        {"title":"查看","value":"view"},
        {"title":"新增","value":"add"},
        {"title":"修改","value":"edit"},
        {"title":"删除","value":"delete"}
    ]',
    'tag',
    0, '0'
),
(
    -- 标签统计子功能
    'customer-tag-report', '标签统计', 'customer-tag', '/customer-tag/report', 'customer-tag/report/index',
    '0', 202, '1', '电脑版',
    '[
        {"title":"查看","value":"view"},
        {"title":"导出","value":"export"}
    ]',
    'bar-chart',
    0, '0'
) ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `url` = VALUES(`url`),
    `component` = VALUES(`component`),
    `push_btn` = VALUES(`push_btn`);
```

### 7.2 用户权限验证

#### 7.2.1 后端权限验证
```java
// 权限验证服务类
@Service
public class PluginPermissionService {

    @Resource
    private UserService userService;

    @Resource
    private FunctionService functionService;

    /**
     * 检查用户是否有指定功能权限
     *
     * @param request HTTP请求
     * @param functionNumber 功能编号
     * @return 是否有权限
     */
    public boolean hasPermission(HttpServletRequest request, String functionNumber) {
        try {
            // 获取当前用户
            User user = getCurrentUser(request);
            if (user == null) {
                return false;
            }

            // 超级管理员拥有所有权限
            if ("admin".equals(user.getLoginName())) {
                return true;
            }

            // 获取用户权限列表
            List<String> userPermissions = functionService.getCurrentUserFunIdList(request);

            // 根据功能编号获取功能ID
            String functionId = functionService.getFunctionIdByNumber(functionNumber);

            return userPermissions.contains(functionId);

        } catch (Exception e) {
            logger.error("权限验证失败", e);
            return false;
        }
    }

    /**
     * 检查用户是否有指定按钮权限
     *
     * @param request HTTP请求
     * @param functionNumber 功能编号
     * @param buttonValue 按钮值
     * @return 是否有权限
     */
    public boolean hasButtonPermission(HttpServletRequest request, String functionNumber, String buttonValue) {
        try {
            // 先检查功能权限
            if (!hasPermission(request, functionNumber)) {
                return false;
            }

            // 获取用户按钮权限
            List<String> buttonPermissions = functionService.getCurrentUserButtonPermissions(request, functionNumber);

            return buttonPermissions.contains(buttonValue);

        } catch (Exception e) {
            logger.error("按钮权限验证失败", e);
            return false;
        }
    }

    /**
     * 权限验证注解处理
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequirePermission {
        String value(); // 功能编号
        String button() default ""; // 按钮权限
    }

    /**
     * 权限验证切面
     */
    @Aspect
    @Component
    public class PermissionAspect {

        @Resource
        private PluginPermissionService permissionService;

        @Around("@annotation(requirePermission)")
        public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
            // 获取请求对象
            HttpServletRequest request = getRequest();

            // 检查功能权限
            if (!permissionService.hasPermission(request, requirePermission.value())) {
                throw new Exception("无权限访问此功能");
            }

            // 检查按钮权限
            if (StringUtil.isNotEmpty(requirePermission.button())) {
                if (!permissionService.hasButtonPermission(request, requirePermission.value(), requirePermission.button())) {
                    throw new Exception("无权限执行此操作");
                }
            }

            return joinPoint.proceed();
        }
    }
}
```

#### 7.2.2 前端权限验证
```javascript
// 权限验证工具类
export const permissionUtils = {
  /**
   * 检查是否有功能权限
   * @param {String} functionNumber 功能编号
   * @returns {Boolean} 是否有权限
   */
  hasPermission(functionNumber) {
    const userPermissions = this.getUserPermissions()
    return userPermissions.includes(functionNumber)
  },

  /**
   * 检查是否有按钮权限
   * @param {String} functionNumber 功能编号
   * @param {String} buttonValue 按钮值
   * @returns {Boolean} 是否有权限
   */
  hasButtonPermission(functionNumber, buttonValue) {
    if (!this.hasPermission(functionNumber)) {
      return false
    }

    const buttonPermissions = this.getButtonPermissions(functionNumber)
    return buttonPermissions.includes(buttonValue)
  },

  /**
   * 获取用户权限列表
   * @returns {Array} 权限列表
   */
  getUserPermissions() {
    const userInfo = this.$store.getters.userInfo
    return userInfo?.permissions || []
  },

  /**
   * 获取按钮权限列表
   * @param {String} functionNumber 功能编号
   * @returns {Array} 按钮权限列表
   */
  getButtonPermissions(functionNumber) {
    const userInfo = this.$store.getters.userInfo
    const functionPermissions = userInfo?.functionPermissions || {}
    return functionPermissions[functionNumber] || []
  }
}

// Vue 3 权限指令
export const permissionDirective = {
  mounted(el, binding) {
    const { value } = binding
    if (!value) return

    const { function: functionNumber, button } = value

    let hasPermission = false
    if (button) {
      hasPermission = permissionUtils.hasButtonPermission(functionNumber, button)
    } else {
      hasPermission = permissionUtils.hasPermission(functionNumber)
    }

    if (!hasPermission) {
      el.style.display = 'none'
      // 或者移除元素
      // el.parentNode && el.parentNode.removeChild(el)
    }
  },

  updated(el, binding) {
    this.mounted(el, binding)
  }
}

// 在 Vue 组件中使用
export default defineComponent({
  setup() {
    // 权限检查方法
    const checkPermission = (functionNumber, button = '') => {
      if (button) {
        return permissionUtils.hasButtonPermission(functionNumber, button)
      }
      return permissionUtils.hasPermission(functionNumber)
    }

    return {
      checkPermission
    }
  }
})
```

---

## 8. 插件打包部署规范

### 8.1 Maven 构建配置

#### 8.1.1 pom.xml 配置模板
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 插件基本信息 -->
    <groupId>com.yourcompany.plugin</groupId>
    <artifactId>your-plugin</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <name>Your Plugin Name</name>
    <description>Your plugin description</description>

    <!-- 属性配置 -->
    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>

        <!-- 版本管理 -->
        <spring-boot.version>2.7.0</spring-boot.version>
        <mybatis-plus.version>3.5.2</mybatis-plus.version>
        <plugin-framework.version>2.2.1-RELEASE</plugin-framework.version>
    </properties>

    <!-- 依赖管理 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <!-- 项目依赖 -->
    <dependencies>
        <!-- Spring Boot Starter -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
            <scope>provided</scope>
        </dependency>

        <!-- MyBatis Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- 插件框架 -->
        <dependency>
            <groupId>com.gitee.starblues</groupId>
            <artifactId>springboot-plugin-framework</artifactId>
            <version>${plugin-framework.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- jshERP 核心依赖 -->
        <dependency>
            <groupId>com.jsh.erp</groupId>
            <artifactId>jshERP-boot</artifactId>
            <version>3.5.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- 工具类依赖 -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>5.8.10</version>
        </dependency>

        <!-- Swagger 文档 -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.9.2</version>
            <scope>provided</scope>
        </dependency>

        <!-- 测试依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <!-- 构建配置 -->
    <build>
        <finalName>${project.artifactId}-${project.version}</finalName>

        <plugins>
            <!-- 编译插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <!-- 资源插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>

            <!-- 打包插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <addClasspath>true</addClasspath>
                            <mainClass>com.yourcompany.plugin.YourPluginApplication</mainClass>
                        </manifest>
                        <manifestEntries>
                            <Plugin-Id>${project.artifactId}</Plugin-Id>
                            <Plugin-Version>${project.version}</Plugin-Version>
                            <Plugin-Provider>Your Company</Plugin-Provider>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>

            <!-- 依赖插件 -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-dependency-plugin</artifactId>
                <version>3.2.0</version>
                <executions>
                    <execution>
                        <id>copy-dependencies</id>
                        <phase>package</phase>
                        <goals>
                            <goal>copy-dependencies</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${project.build.directory}/lib</outputDirectory>
                            <excludeScope>provided</excludeScope>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

#### 8.1.2 JAR包结构规范
```
your-plugin-1.0.0.jar
├── META-INF/
│   ├── MANIFEST.MF                    # JAR包清单文件
│   └── maven/
│       └── com.yourcompany.plugin/
│           └── your-plugin/
│               ├── pom.xml
│               └── pom.properties
├── com/
│   └── yourcompany/
│       └── plugin/
│           ├── YourPluginApplication.class    # 插件主类
│           ├── controller/                    # 控制器类
│           ├── service/                       # 服务类
│           ├── datasource/                    # 数据访问类
│           ├── config/                        # 配置类
│           ├── constants/                     # 常量类
│           └── utils/                         # 工具类
├── mapper_xml/                               # MyBatis映射文件
│   ├── YourMapper.xml
│   └── YourMapperEx.xml
├── sql/                                      # 数据库脚本
│   ├── init.sql
│   └── upgrade.sql
├── static/                                   # 静态资源
│   ├── js/
│   │   └── views/                           # Vue组件
│   ├── css/                                 # 样式文件
│   └── images/                              # 图片资源
├── application-plugin.yml                    # 插件配置文件
├── plugin.properties                         # 插件元信息
└── lib/                                      # 依赖库（如果有）
    └── your-dependency.jar
```

### 8.2 插件安装部署流程

#### 8.2.1 插件安装步骤
```bash
# 1. 构建插件JAR包
mvn clean package

# 2. 检查构建结果
ls -la target/
# 应该看到：your-plugin-1.0.0.jar

# 3. 验证JAR包结构
jar -tf target/your-plugin-1.0.0.jar | head -20

# 4. 复制到插件目录（开发环境）
cp target/your-plugin-1.0.0.jar /path/to/jshERP/plugins/

# 5. 重启jshERP服务
# 开发环境：重启IDE中的应用
# 生产环境：重启服务器
```

#### 8.2.2 通过管理界面安装
```javascript
// 前端上传安装流程
const installPlugin = async (file) => {
  try {
    // 1. 创建FormData
    const formData = new FormData()
    formData.append('file', file)

    // 2. 上传并安装插件
    const response = await pluginApi.uploadInstall(formData)

    if (response.code === 200) {
      message.success('插件安装成功')

      // 3. 刷新插件列表
      await loadPluginList()

      // 4. 启动插件
      await pluginApi.start(response.data.pluginId)

      message.success('插件启动成功')
    }
  } catch (error) {
    message.error('插件安装失败：' + error.message)
  }
}

// 后端安装API
@PostMapping("/uploadInstallPluginJar")
@ApiOperation("上传安装插件JAR包")
public BaseResponseInfo uploadInstallPluginJar(
    @RequestParam("file") MultipartFile file,
    HttpServletRequest request) {

    try {
        // 1. 验证文件类型
        if (!file.getOriginalFilename().endsWith(".jar")) {
            return error("只支持JAR文件格式");
        }

        // 2. 保存文件到临时目录
        String tempPath = saveToTempDirectory(file);

        // 3. 验证插件格式
        validatePluginStructure(tempPath);

        // 4. 安装插件
        String pluginId = pluginManager.install(tempPath);

        // 5. 返回安装结果
        Map<String, Object> result = new HashMap<>();
        result.put("pluginId", pluginId);
        result.put("message", "插件安装成功");

        return success(result);

    } catch (Exception e) {
        logger.error("插件安装失败", e);
        return error("插件安装失败：" + e.getMessage());
    }
}
```

#### 8.2.3 插件生命周期管理
```java
// 插件生命周期管理服务
@Service
public class PluginLifecycleService {

    @Resource
    private PluginManager pluginManager;

    /**
     * 安装插件
     */
    public String installPlugin(String jarPath) throws Exception {
        try {
            // 1. 验证插件包
            validatePluginPackage(jarPath);

            // 2. 执行安装
            String pluginId = pluginManager.install(jarPath);

            // 3. 执行数据库初始化脚本
            executeInitScript(pluginId);

            // 4. 注册插件权限
            registerPluginPermissions(pluginId);

            logger.info("插件安装成功: {}", pluginId);
            return pluginId;

        } catch (Exception e) {
            logger.error("插件安装失败", e);
            throw new Exception("插件安装失败：" + e.getMessage());
        }
    }

    /**
     * 启动插件
     */
    public boolean startPlugin(String pluginId) throws Exception {
        try {
            // 1. 启动插件
            boolean result = pluginManager.start(pluginId);

            if (result) {
                // 2. 执行启动后处理
                executePostStartActions(pluginId);

                logger.info("插件启动成功: {}", pluginId);
            }

            return result;

        } catch (Exception e) {
            logger.error("插件启动失败", e);
            throw new Exception("插件启动失败：" + e.getMessage());
        }
    }

    /**
     * 停止插件
     */
    public boolean stopPlugin(String pluginId) throws Exception {
        try {
            // 1. 执行停止前处理
            executePreStopActions(pluginId);

            // 2. 停止插件
            boolean result = pluginManager.stop(pluginId);

            if (result) {
                logger.info("插件停止成功: {}", pluginId);
            }

            return result;

        } catch (Exception e) {
            logger.error("插件停止失败", e);
            throw new Exception("插件停止失败：" + e.getMessage());
        }
    }

    /**
     * 卸载插件
     */
    public boolean uninstallPlugin(String pluginId) throws Exception {
        try {
            // 1. 停止插件
            if (pluginManager.getPluginState(pluginId) == PluginState.STARTED) {
                stopPlugin(pluginId);
            }

            // 2. 清理插件数据
            cleanupPluginData(pluginId);

            // 3. 移除插件权限
            removePluginPermissions(pluginId);

            // 4. 卸载插件
            boolean result = pluginManager.uninstall(pluginId);

            if (result) {
                logger.info("插件卸载成功: {}", pluginId);
            }

            return result;

        } catch (Exception e) {
            logger.error("插件卸载失败", e);
            throw new Exception("插件卸载失败：" + e.getMessage());
        }
    }
}
```

### 8.3 自动化构建脚本

#### 8.3.1 构建脚本模板
```bash
#!/bin/bash
# build.sh - 插件构建脚本

set -e  # 遇到错误立即退出

# 脚本配置
PLUGIN_NAME="your-plugin"
PLUGIN_VERSION="1.0.0"
BUILD_DIR="target"
DIST_DIR="dist"

echo "=========================================="
echo "开始构建插件: $PLUGIN_NAME v$PLUGIN_VERSION"
echo "=========================================="

# 1. 清理构建目录
echo "1. 清理构建目录..."
mvn clean
rm -rf $DIST_DIR
mkdir -p $DIST_DIR

# 2. 编译和打包
echo "2. 编译和打包..."
mvn package -DskipTests

# 3. 验证构建结果
echo "3. 验证构建结果..."
JAR_FILE="$BUILD_DIR/$PLUGIN_NAME-$PLUGIN_VERSION.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: JAR文件不存在: $JAR_FILE"
    exit 1
fi

# 4. 检查JAR包结构
echo "4. 检查JAR包结构..."
jar -tf "$JAR_FILE" | grep -E "(plugin\.properties|.*Application\.class)" > /dev/null
if [ $? -ne 0 ]; then
    echo "错误: JAR包结构不正确"
    exit 1
fi

# 5. 复制构建产物
echo "5. 复制构建产物..."
cp "$JAR_FILE" "$DIST_DIR/"
cp "README.md" "$DIST_DIR/" 2>/dev/null || echo "警告: README.md 不存在"
cp "CHANGELOG.md" "$DIST_DIR/" 2>/dev/null || echo "警告: CHANGELOG.md 不存在"

# 6. 生成安装包
echo "6. 生成安装包..."
cd $DIST_DIR
tar -czf "$PLUGIN_NAME-$PLUGIN_VERSION.tar.gz" *
cd ..

# 7. 显示构建结果
echo "=========================================="
echo "构建完成!"
echo "JAR文件: $JAR_FILE"
echo "安装包: $DIST_DIR/$PLUGIN_NAME-$PLUGIN_VERSION.tar.gz"
echo "文件大小:"
ls -lh "$JAR_FILE"
ls -lh "$DIST_DIR/$PLUGIN_NAME-$PLUGIN_VERSION.tar.gz"
echo "=========================================="
```

#### 8.3.2 部署脚本模板
```bash
#!/bin/bash
# deploy.sh - 插件部署脚本

set -e

# 配置参数
PLUGIN_NAME="your-plugin"
PLUGIN_VERSION="1.0.0"
JSHERP_HOME="/path/to/jshERP"
PLUGINS_DIR="$JSHERP_HOME/plugins"
BACKUP_DIR="$JSHERP_HOME/backup/plugins"

echo "=========================================="
echo "开始部署插件: $PLUGIN_NAME v$PLUGIN_VERSION"
echo "=========================================="

# 1. 检查jshERP环境
echo "1. 检查jshERP环境..."
if [ ! -d "$JSHERP_HOME" ]; then
    echo "错误: jshERP目录不存在: $JSHERP_HOME"
    exit 1
fi

if [ ! -d "$PLUGINS_DIR" ]; then
    echo "创建插件目录: $PLUGINS_DIR"
    mkdir -p "$PLUGINS_DIR"
fi

# 2. 备份现有插件
echo "2. 备份现有插件..."
mkdir -p "$BACKUP_DIR"
if [ -f "$PLUGINS_DIR/$PLUGIN_NAME-*.jar" ]; then
    mv "$PLUGINS_DIR/$PLUGIN_NAME-"*.jar "$BACKUP_DIR/"
    echo "已备份现有插件到: $BACKUP_DIR"
fi

# 3. 部署新插件
echo "3. 部署新插件..."
JAR_FILE="target/$PLUGIN_NAME-$PLUGIN_VERSION.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "错误: 插件文件不存在: $JAR_FILE"
    echo "请先运行构建脚本: ./build.sh"
    exit 1
fi

cp "$JAR_FILE" "$PLUGINS_DIR/"
echo "插件已部署到: $PLUGINS_DIR"

# 4. 设置文件权限
echo "4. 设置文件权限..."
chmod 644 "$PLUGINS_DIR/$PLUGIN_NAME-$PLUGIN_VERSION.jar"

# 5. 重启服务（可选）
read -p "是否重启jshERP服务? (y/N): " restart_service
if [[ $restart_service =~ ^[Yy]$ ]]; then
    echo "5. 重启jshERP服务..."
    # 根据实际部署方式调整重启命令
    # systemctl restart jsherp
    # docker-compose restart jsherp
    echo "请手动重启jshERP服务"
fi

echo "=========================================="
echo "部署完成!"
echo "插件文件: $PLUGINS_DIR/$PLUGIN_NAME-$PLUGIN_VERSION.jar"
echo "备份目录: $BACKUP_DIR"
echo "=========================================="
```

---

## 9. 代码质量标准

### 9.1 Java代码命名规范

#### 9.1.1 类命名规范
```java
// 1. 类名使用大驼峰命名法（PascalCase）
public class CustomerTagController {}        // 控制器类
public class CustomerTagService {}           // 服务类
public class CustomerTag {}                  // 实体类
public class CustomerTagMapper {}            // Mapper接口
public class CustomerTagConstants {}         // 常量类
public class CustomerTagUtils {}             // 工具类
public class CustomerTagException {}         // 异常类

// 2. 接口命名
public interface CustomerTagService {}       // 服务接口
public interface CustomerTagRepository {}    // 仓储接口

// 3. 抽象类命名
public abstract class BaseController {}      // 基础控制器
public abstract class AbstractService {}     // 抽象服务

// 4. 枚举类命名
public enum CustomerTagStatus {              // 状态枚举
    ACTIVE, INACTIVE, DELETED
}

// 5. 内部类命名
public class CustomerTagController {
    public static class TagRequest {}        // 静态内部类
    private class TagValidator {}            // 私有内部类
}
```

#### 9.1.2 方法命名规范
```java
public class CustomerTagService {

    // 1. 方法名使用小驼峰命名法（camelCase）
    public List<CustomerTag> getTagList() {}           // 获取列表
    public CustomerTag getTagById(Long id) {}          // 根据ID获取
    public int addTag(CustomerTag tag) {}              // 新增
    public int updateTag(CustomerTag tag) {}           // 更新
    public int deleteTag(Long id) {}                   // 删除
    public boolean existsByName(String name) {}        // 存在性检查

    // 2. 布尔方法命名
    public boolean isActive(CustomerTag tag) {}        // 状态判断
    public boolean hasPermission(String permission) {} // 权限检查
    public boolean canDelete(Long id) {}               // 能力判断

    // 3. 转换方法命名
    public CustomerTagVO toVO(CustomerTag tag) {}      // 转换为VO
    public CustomerTag fromDTO(CustomerTagDTO dto) {}  // 从DTO转换

    // 4. 验证方法命名
    public void validateTag(CustomerTag tag) {}        // 验证
    public void checkPermission(String permission) {}  // 检查

    // 5. 处理方法命名
    public void processTagCreation(CustomerTag tag) {} // 处理创建
    public void handleTagUpdate(CustomerTag tag) {}    // 处理更新
}
```

#### 9.1.3 变量命名规范
```java
public class CustomerTagController {

    // 1. 成员变量使用小驼峰命名法
    private CustomerTagService customerTagService;     // 服务注入
    private static final Logger logger = LoggerFactory.getLogger(CustomerTagController.class);

    // 2. 常量使用全大写下划线分隔
    private static final String DEFAULT_STATUS = "ACTIVE";
    private static final int MAX_TAG_COUNT = 100;
    private static final long CACHE_EXPIRE_TIME = 3600L;

    // 3. 局部变量使用小驼峰命名法
    public BaseResponseInfo getList(HttpServletRequest request) {
        String searchKeyword = request.getParameter("keyword");    // 搜索关键词
        int currentPage = Integer.parseInt(request.getParameter("page"));  // 当前页码
        List<CustomerTag> tagList = customerTagService.getList(); // 标签列表
        Map<String, Object> resultMap = new HashMap<>();          // 结果映射

        return success(resultMap);
    }

    // 4. 集合变量命名
    List<CustomerTag> tagList = new ArrayList<>();        // 列表
    Set<String> tagNameSet = new HashSet<>();            // 集合
    Map<Long, CustomerTag> tagMap = new HashMap<>();     // 映射

    // 5. 临时变量命名
    for (CustomerTag tag : tagList) {                    // 循环变量
        String tagName = tag.getName();                  // 临时变量
        if (StringUtil.isNotEmpty(tagName)) {
            // 处理逻辑
        }
    }
}
```

### 9.2 注释规范

#### 9.2.1 类注释规范
```java
/**
 * Customer Tag Management Controller
 * Provides REST APIs for customer tag operations including CRUD and batch operations
 *
 * @author Your Name
 * @version 1.0.0
 * @since 2023-12-01
 * @see CustomerTagService
 * @see CustomerTag
 */
@RestController
@RequestMapping("/customer-tag")
@Api(tags = "Customer Tag Management")
public class CustomerTagController extends BaseController {

    /**
     * Customer tag service for business logic operations
     */
    @Resource
    private CustomerTagService customerTagService;

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(CustomerTagController.class);
}
```

#### 9.2.2 方法注释规范
```java
/**
 * Get customer tag list with pagination and search conditions
 *
 * This method supports the following search parameters:
 * - name: Tag name (fuzzy search)
 * - status: Tag status (exact match)
 * - startDate: Creation start date
 * - endDate: Creation end date
 *
 * @param request HTTP request containing search parameters and pagination info
 * @return BaseResponseInfo containing paginated tag list
 * @throws Exception if database operation fails or permission denied
 *
 * @apiNote This API requires 'customer-tag:view' permission
 * @since 1.0.0
 *
 * Example usage:
 * GET /api/plugin/customer-tag/tag/list?current=1&pageSize=20&search={"name":"VIP"}
 */
@GetMapping("/list")
@ApiOperation("Get customer tag list")
@RequirePermission("customer-tag")
public BaseResponseInfo getList(HttpServletRequest request) {
    try {
        // Start pagination - automatically gets pageNum and pageSize from request
        startPage();

        // Get list data with search conditions
        List<Map<String, Object>> list = customerTagService.getList(request);

        // Return paginated data - automatically wraps total and rows
        return getDataTable(list);

    } catch (Exception e) {
        logger.error("Failed to get customer tag list", e);
        return error("Failed to get tag list: " + e.getMessage());
    }
}

/**
 * Add new customer tag
 *
 * @param tag Customer tag data to be added
 * @param request HTTP request for user context
 * @return BaseResponseInfo operation result
 * @throws Exception if validation fails or database operation fails
 */
@PostMapping("/add")
@ApiOperation("Add new customer tag")
@RequirePermission(value = "customer-tag", button = "add")
public BaseResponseInfo add(
        @ApiParam("Customer tag data") @RequestBody CustomerTag tag,
        HttpServletRequest request) throws Exception {

    // Validate input parameters
    if (tag == null) {
        return error("Tag data cannot be null");
    }

    // Add tag through service
    int result = customerTagService.addTag(tag, request);

    if (result > 0) {
        return success(null, "Tag added successfully");
    } else {
        return error("Failed to add tag");
    }
}
```

#### 9.2.3 行内注释规范
```java
public class CustomerTagService {

    public int addTag(CustomerTag tag, HttpServletRequest request) throws Exception {
        // Validate tag data
        validateTag(tag);

        // Get current user and tenant information
        Long tenantId = getCurrentTenantId(request);
        Long userId = getCurrentUserId(request);

        // Check for duplicate tag names within the same tenant
        boolean exists = customerTagMapper.existsByName(tag.getName(), null, tenantId);
        if (exists) {
            throw new Exception("Tag name already exists");
        }

        // Set default values and audit fields
        tag.setId(null);                                    // Ensure it's a new record
        tag.setTenantId(tenantId);                         // Set tenant isolation
        tag.setDeleteFlag(CustomerTagConstants.DELETE_FLAG_EXISTS);  // Set delete flag
        tag.setCreateTime(new Date());                     // Set creation time
        tag.setCreateBy(userId);                           // Set creator

        // Set default status if not provided
        if (StringUtil.isEmpty(tag.getStatus())) {
            tag.setStatus(CustomerTagConstants.STATUS_ACTIVE);
        }

        // Save to database
        int result = customerTagMapper.insertSelective(tag);

        // Log the operation for audit trail
        logger.info("Customer tag added successfully, ID: {}, Name: {}, Operator: {}",
                   tag.getId(), tag.getName(), userId);

        return result;
    }
}
```

### 9.3 异常处理标准

#### 9.3.1 异常分类和处理
```java
/**
 * Plugin Exception Hierarchy
 *
 * Exception
 * └── RuntimeException
 *     └── PluginException (Base plugin exception)
 *         ├── PluginValidationException (Validation errors)
 *         ├── PluginPermissionException (Permission errors)
 *         ├── PluginBusinessException (Business logic errors)
 *         └── PluginSystemException (System errors)
 */

// 1. 基础插件异常
public class PluginException extends RuntimeException {
    private String errorCode;
    private Object[] args;

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }

    // Getters and setters
    public String getErrorCode() { return errorCode; }
    public Object[] getArgs() { return args; }
}

// 2. 验证异常
public class PluginValidationException extends PluginException {
    public PluginValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}

// 3. 权限异常
public class PluginPermissionException extends PluginException {
    public PluginPermissionException(String message) {
        super("PERMISSION_DENIED", message);
    }
}

// 4. 业务异常
public class PluginBusinessException extends PluginException {
    public PluginBusinessException(String message) {
        super("BUSINESS_ERROR", message);
    }
}

// 5. 系统异常
public class PluginSystemException extends PluginException {
    public PluginSystemException(String message, Throwable cause) {
        super("SYSTEM_ERROR", message, cause);
    }
}
```

#### 9.3.2 异常处理最佳实践
```java
@Service
public class CustomerTagService {

    /**
     * Add customer tag with comprehensive error handling
     */
    @Transactional(rollbackFor = Exception.class)
    public int addTag(CustomerTag tag, HttpServletRequest request) {
        try {
            // 1. Parameter validation
            if (tag == null) {
                throw new PluginValidationException("Tag data cannot be null");
            }

            if (StringUtil.isEmpty(tag.getName())) {
                throw new PluginValidationException("Tag name is required");
            }

            if (tag.getName().length() > 50) {
                throw new PluginValidationException("Tag name cannot exceed 50 characters");
            }

            // 2. Business validation
            Long tenantId = getCurrentTenantId(request);
            boolean exists = customerTagMapper.existsByName(tag.getName(), null, tenantId);
            if (exists) {
                throw new PluginBusinessException("Tag name already exists: " + tag.getName());
            }

            // 3. Permission check
            if (!hasPermission(request, "customer-tag", "add")) {
                throw new PluginPermissionException("No permission to add customer tag");
            }

            // 4. Set audit fields
            setCreateFields(tag, request);

            // 5. Database operation
            int result = customerTagMapper.insertSelective(tag);

            if (result <= 0) {
                throw new PluginSystemException("Failed to insert tag into database", null);
            }

            // 6. Log success
            logger.info("Customer tag added successfully: {}", tag.getName());

            return result;

        } catch (PluginException e) {
            // Re-throw plugin exceptions
            logger.warn("Plugin exception in addTag: {}", e.getMessage());
            throw e;

        } catch (DataAccessException e) {
            // Handle database exceptions
            logger.error("Database error in addTag", e);
            throw new PluginSystemException("Database operation failed", e);

        } catch (Exception e) {
            // Handle unexpected exceptions
            logger.error("Unexpected error in addTag", e);
            throw new PluginSystemException("Unexpected error occurred", e);
        }
    }
}

/**
 * Global exception handler for plugin
 */
@ControllerAdvice
public class PluginExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(PluginExceptionHandler.class);

    /**
     * Handle plugin validation exceptions
     */
    @ExceptionHandler(PluginValidationException.class)
    public BaseResponseInfo handleValidationException(PluginValidationException e) {
        logger.warn("Validation error: {}", e.getMessage());
        return ResponseJsonUtil.returnStr(createErrorResponse(e.getMessage()), 0);
    }

    /**
     * Handle plugin permission exceptions
     */
    @ExceptionHandler(PluginPermissionException.class)
    public BaseResponseInfo handlePermissionException(PluginPermissionException e) {
        logger.warn("Permission error: {}", e.getMessage());
        return ResponseJsonUtil.returnStr(createErrorResponse(e.getMessage()), 0);
    }

    /**
     * Handle plugin business exceptions
     */
    @ExceptionHandler(PluginBusinessException.class)
    public BaseResponseInfo handleBusinessException(PluginBusinessException e) {
        logger.warn("Business error: {}", e.getMessage());
        return ResponseJsonUtil.returnStr(createErrorResponse(e.getMessage()), 0);
    }

    /**
     * Handle plugin system exceptions
     */
    @ExceptionHandler(PluginSystemException.class)
    public BaseResponseInfo handleSystemException(PluginSystemException e) {
        logger.error("System error: {}", e.getMessage(), e);
        return ResponseJsonUtil.returnStr(createErrorResponse("System error occurred"), 0);
    }

    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public BaseResponseInfo handleGeneralException(Exception e) {
        logger.error("Unexpected error: {}", e.getMessage(), e);
        return ResponseJsonUtil.returnStr(createErrorResponse("Internal server error"), 0);
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("message", message);
        errorMap.put("timestamp", new Date());
        return errorMap;
    }
}
```

### 9.4 单元测试要求

#### 9.4.1 测试覆盖率要求
```java
/**
 * Unit Test Coverage Requirements:
 * - Service Layer: 80%+ line coverage, 70%+ branch coverage
 * - Controller Layer: 70%+ line coverage, 60%+ branch coverage
 * - Mapper Layer: 60%+ line coverage
 * - Utility Classes: 90%+ line coverage
 */

// 测试基类
@SpringBootTest
@Transactional
@Rollback
public abstract class BasePluginTest {

    @Autowired
    protected TestEntityManager entityManager;

    @MockBean
    protected UserService userService;

    /**
     * Setup test data before each test
     */
    @BeforeEach
    public void setUp() {
        // Mock current user
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLoginName("testuser");
        mockUser.setTenantId(1L);

        when(userService.getCurrentUser(any())).thenReturn(mockUser);
    }

    /**
     * Clean up test data after each test
     */
    @AfterEach
    public void tearDown() {
        entityManager.clear();
    }
}
```

#### 9.4.2 Service层测试模板
```java
/**
 * Customer Tag Service Unit Tests
 * Tests all business logic methods with various scenarios
 */
@ExtendWith(MockitoExtension.class)
class CustomerTagServiceTest extends BasePluginTest {

    @InjectMocks
    private CustomerTagService customerTagService;

    @Mock
    private CustomerTagMapper customerTagMapper;

    @Mock
    private CustomerTagMapperEx customerTagMapperEx;

    @Mock
    private HttpServletRequest request;

    private CustomerTag testTag;

    @BeforeEach
    void setUp() {
        super.setUp();

        // Prepare test data
        testTag = new CustomerTag();
        testTag.setId(1L);
        testTag.setName("VIP Customer");
        testTag.setStatus("active");
        testTag.setDescription("VIP customer tag");
        testTag.setTenantId(1L);
        testTag.setDeleteFlag("0");
    }

    @Test
    @DisplayName("Add tag - Success case")
    void addTag_Success() throws Exception {
        // Given
        when(customerTagMapperEx.existsByName(anyString(), any(), anyLong())).thenReturn(false);
        when(customerTagMapper.insertSelective(any(CustomerTag.class))).thenReturn(1);
        when(request.getHeader("X-Access-Token")).thenReturn("mock-token");

        // When
        int result = customerTagService.addTag(testTag, request);

        // Then
        assertEquals(1, result);
        verify(customerTagMapper).insertSelective(any(CustomerTag.class));
        verify(customerTagMapperEx).existsByName(testTag.getName(), null, 1L);
    }

    @Test
    @DisplayName("Add tag - Duplicate name should throw exception")
    void addTag_DuplicateName_ShouldThrowException() {
        // Given
        when(customerTagMapperEx.existsByName(anyString(), any(), anyLong())).thenReturn(true);
        when(request.getHeader("X-Access-Token")).thenReturn("mock-token");

        // When & Then
        PluginBusinessException exception = assertThrows(
            PluginBusinessException.class,
            () -> customerTagService.addTag(testTag, request)
        );

        assertEquals("Tag name already exists: " + testTag.getName(), exception.getMessage());
        verify(customerTagMapper, never()).insertSelective(any());
    }

    @Test
    @DisplayName("Add tag - Null tag should throw validation exception")
    void addTag_NullTag_ShouldThrowValidationException() {
        // When & Then
        PluginValidationException exception = assertThrows(
            PluginValidationException.class,
            () -> customerTagService.addTag(null, request)
        );

        assertEquals("Tag data cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Get tag list - Success case")
    void getTagList_Success() throws Exception {
        // Given
        List<Map<String, Object>> expectedList = Arrays.asList(
            createTagMap(1L, "VIP", "active"),
            createTagMap(2L, "Regular", "active")
        );

        when(customerTagMapperEx.getList(anyLong(), anyString(), anyString(), anyString(), anyString()))
            .thenReturn(expectedList);
        when(request.getHeader("X-Access-Token")).thenReturn("mock-token");
        when(request.getParameter("search")).thenReturn("{\"name\":\"VIP\"}");

        // When
        List<Map<String, Object>> result = customerTagService.getList(request);

        // Then
        assertEquals(2, result.size());
        assertEquals("VIP", result.get(0).get("name"));
        verify(customerTagMapperEx).getList(eq(1L), eq("VIP"), any(), any(), any());
    }

    private Map<String, Object> createTagMap(Long id, String name, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("status", status);
        return map;
    }
}
```

#### 9.4.3 Controller层测试模板
```java
/**
 * Customer Tag Controller Integration Tests
 * Tests REST API endpoints with mock MVC
 */
@WebMvcTest(CustomerTagController.class)
class CustomerTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerTagService customerTagService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /list - Should return tag list")
    void getList_ShouldReturnTagList() throws Exception {
        // Given
        List<Map<String, Object>> mockList = Arrays.asList(
            createTagMap(1L, "VIP", "active"),
            createTagMap(2L, "Regular", "active")
        );

        when(customerTagService.getList(any())).thenReturn(mockList);

        // When & Then
        mockMvc.perform(get("/api/plugin/customer-tag/tag/list")
                .param("current", "1")
                .param("pageSize", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.rows").isArray())
                .andExpect(jsonPath("$.data.rows[0].name").value("VIP"));
    }

    @Test
    @DisplayName("POST /add - Should add new tag")
    void add_ShouldAddNewTag() throws Exception {
        // Given
        CustomerTag newTag = new CustomerTag();
        newTag.setName("Premium");
        newTag.setStatus("active");
        newTag.setDescription("Premium customer tag");

        when(customerTagService.addTag(any(CustomerTag.class), any())).thenReturn(1);

        // When & Then
        mockMvc.perform(post("/api/plugin/customer-tag/tag/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.message").value("Tag added successfully"));
    }

    @Test
    @DisplayName("POST /add - Should handle validation error")
    void add_ShouldHandleValidationError() throws Exception {
        // Given
        CustomerTag invalidTag = new CustomerTag();
        // Missing required name field

        when(customerTagService.addTag(any(CustomerTag.class), any()))
            .thenThrow(new PluginValidationException("Tag name is required"));

        // When & Then
        mockMvc.perform(post("/api/plugin/customer-tag/tag/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.message").value("Tag name is required"));
    }

    private Map<String, Object> createTagMap(Long id, String name, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("name", name);
        map.put("status", status);
        return map;
    }
}
```

---

## 10. 完整代码模板

### 10.1 插件项目模板结构

本章节提供一个完整的、可直接使用的插件项目模板，包含所有必要的文件和配置。

#### 10.1.1 项目目录结构
```
customer-tag-plugin/
├── pom.xml                                    # Maven配置文件
├── plugin.properties                          # 插件元信息
├── README.md                                  # 项目说明文档
├── CHANGELOG.md                               # 版本更新日志
├── build.sh                                  # 构建脚本
├── deploy.sh                                 # 部署脚本
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/plugin/customertag/
│   │   │       ├── CustomerTagPluginApplication.java      # 插件主类
│   │   │       ├── controller/                            # 控制器层
│   │   │       │   └── CustomerTagController.java
│   │   │       ├── service/                               # 服务层
│   │   │       │   └── CustomerTagService.java
│   │   │       ├── datasource/                            # 数据访问层
│   │   │       │   ├── entities/
│   │   │       │   │   └── CustomerTag.java
│   │   │       │   └── mappers/
│   │   │       │       ├── CustomerTagMapper.java
│   │   │       │       └── CustomerTagMapperEx.java
│   │   │       ├── config/                                # 配置类
│   │   │       │   └── CustomerTagConfig.java
│   │   │       ├── constants/                             # 常量定义
│   │   │       │   └── CustomerTagConstants.java
│   │   │       ├── utils/                                 # 工具类
│   │   │       │   └── CustomerTagUtils.java
│   │   │       └── exception/                             # 异常类
│   │   │           └── CustomerTagException.java
│   │   └── resources/
│   │       ├── mapper_xml/                                # MyBatis映射文件
│   │       │   ├── CustomerTagMapper.xml
│   │       │   └── CustomerTagMapperEx.xml
│   │       ├── sql/                                       # 数据库脚本
│   │       │   ├── init.sql
│   │       │   └── upgrade.sql
│   │       ├── static/                                    # 静态资源
│   │       │   └── js/
│   │       │       └── views/
│   │       │           ├── CustomerTagList.vue
│   │       │           └── CustomerTagForm.vue
│   │       ├── application-plugin.yml                     # 插件配置
│   │       └── logback-spring.xml                         # 日志配置
│   └── test/                                              # 测试代码
│       ├── java/
│       │   └── com/example/plugin/customertag/
│       │       ├── controller/
│       │       │   └── CustomerTagControllerTest.java
│       │       ├── service/
│       │       │   └── CustomerTagServiceTest.java
│       │       └── BasePluginTest.java
│       └── resources/
│           └── application-test.yml
└── docs/                                                  # 文档目录
    ├── API文档.md
    ├── 安装指南.md
    └── 用户手册.md
```

#### 10.1.2 核心文件模板

**插件主类 (CustomerTagPluginApplication.java)**
```java
package com.example.plugin.customertag;

import com.gitee.starblues.annotation.Extract;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Customer Tag Plugin Main Application
 *
 * @author Plugin Developer
 * @version 1.0.0
 */
@SpringBootApplication
@Extract(bus = "pluginBus")
public class CustomerTagPluginApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerTagPluginApplication.class, args);
    }
}
```

**实体类 (CustomerTag.java)**
```java
package com.example.plugin.customertag.datasource.entities;

import java.util.Date;

/**
 * Customer Tag Entity
 *
 * @author Plugin Developer
 * @version 1.0.0
 */
public class CustomerTag {

    private Long id;
    private String name;
    private String color;
    private String description;
    private Integer sortOrder;
    private String status;

    // Standard fields
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Date updateTime;
    private Long createBy;
    private Long updateBy;

    // Constructors
    public CustomerTag() {}

    public CustomerTag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(String deleteFlag) { this.deleteFlag = deleteFlag; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Long getCreateBy() { return createBy; }
    public void setCreateBy(Long createBy) { this.createBy = createBy; }

    public Long getUpdateBy() { return updateBy; }
    public void setUpdateBy(Long updateBy) { this.updateBy = updateBy; }

    @Override
    public String toString() {
        return "CustomerTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
```

### 10.2 使用说明

#### 10.2.1 快速开始指南

1. **下载模板**
```bash
# 复制模板到新项目目录
cp -r customer-tag-plugin your-new-plugin
cd your-new-plugin
```

2. **修改项目信息**
```bash
# 修改 pom.xml 中的项目信息
# 修改 plugin.properties 中的插件信息
# 修改包名和类名
```

3. **构建和部署**
```bash
# 构建插件
./build.sh

# 部署插件
./deploy.sh
```

#### 10.2.2 定制化开发指南

1. **修改业务实体**
   - 修改 `CustomerTag.java` 实体类
   - 更新数据库表结构 `init.sql`
   - 调整 MyBatis 映射文件

2. **扩展业务逻辑**
   - 在 `CustomerTagService.java` 中添加业务方法
   - 在 `CustomerTagController.java` 中添加 API 接口
   - 更新前端 Vue 组件

3. **添加新功能模块**
   - 创建新的实体类和 Mapper
   - 添加对应的 Service 和 Controller
   - 注册新的功能权限

#### 10.2.3 最佳实践建议

1. **代码规范**
   - 严格遵循命名规范
   - 添加完整的注释
   - 实现异常处理

2. **测试覆盖**
   - 编写单元测试
   - 进行集成测试
   - 验证功能完整性

3. **性能优化**
   - 使用缓存机制
   - 优化数据库查询
   - 实现分页功能

4. **安全考虑**
   - 实现权限控制
   - 验证输入参数
   - 防止 SQL 注入

---

## 📋 总结

本技术规范文档提供了 jshERP 插件开发的完整指导，涵盖了从环境配置到代码实现的全流程标准。通过遵循本规范，开发者可以：

### ✅ 技术收益
- **标准化开发流程**：统一的项目结构和代码规范
- **提高开发效率**：完整的代码模板和最佳实践
- **保证代码质量**：严格的质量标准和测试要求
- **简化部署流程**：自动化构建和部署脚本

### 🎯 应用场景
- **新插件开发**：使用完整的项目模板快速启动
- **现有插件优化**：参考规范进行代码重构
- **团队协作开发**：统一的开发标准和流程
- **插件维护升级**：规范化的版本管理和升级流程

### 🚀 持续改进
本规范将根据 jshERP 系统的发展和实际使用反馈持续更新完善，确保始终提供最佳的开发体验和技术指导。

---

**文档版本**: 1.0.0
**最后更新**: 2023-12-01
**维护团队**: jshERP 插件开发团队
```
```
```
```
```
