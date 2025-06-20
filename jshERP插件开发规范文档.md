# jshERP插件开发规范文档

## 📋 目录
1. [插件架构概述](#插件架构概述)
2. [开发环境要求](#开发环境要求)
3. [插件目录结构](#插件目录结构)
4. [核心开发规范](#核心开发规范)
5. [数据库设计规范](#数据库设计规范)
6. [API开发规范](#api开发规范)
7. [安全与权限规范](#安全与权限规范)
8. [部署与配置规范](#部署与配置规范)
9. [最佳实践](#最佳实践)
10. [示例代码](#示例代码)

---

## 📚 插件架构概述

### 插件框架技术栈
- **插件框架**: SpringBoot Plugin Framework (Gitee StarBlues)
- **版本**: 2.2.1-RELEASE
- **扩展支持**: MyBatis插件扩展
- **运行模式**: 生产模式 (prod)

### 插件类型支持
- **业务功能插件**: 新增业务模块（如排班管理、咖啡店管理）
- **数据扩展插件**: 扩展现有实体的功能
- **报表插件**: 定制化报表和统计功能
- **集成插件**: 第三方系统集成
- **工具插件**: 系统工具和实用功能

---

## 🛠️ 开发环境要求

### 基础环境
```yaml
Java版本: JDK 1.8+
Spring Boot: 2.x
MyBatis: 3.x
Maven: 3.6+
数据库: MySQL 5.7+
Redis: 用于缓存和会话管理
```

### 依赖管理
```xml
<!-- 主要插件框架依赖 -->
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework</artifactId>
    <version>2.2.1-RELEASE</version>
</dependency>

<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework-extension-mybatis</artifactId>
    <version>2.2.1-RELEASE</version>
</dependency>
```

---

## 📁 插件目录结构

### 标准插件项目结构
```
your-plugin/
├── pom.xml                                    # Maven配置
├── src/main/java/com/yourcompany/plugin/
│   ├── YourPluginApplication.java             # 插件主类
│   ├── controller/                            # REST控制器
│   │   └── YourController.java
│   ├── service/                               # 业务逻辑层
│   │   ├── YourService.java
│   │   └── impl/
│   │       └── YourServiceImpl.java
│   ├── datasource/                            # 数据访问层
│   │   ├── entities/                          # 实体类
│   │   │   └── YourEntity.java
│   │   ├── mappers/                           # MyBatis Mapper
│   │   │   ├── YourMapper.java
│   │   │   └── YourMapperEx.java
│   │   └── vo/                                # 值对象
│   │       └── YourVo.java
│   ├── config/                                # 配置类
│   │   └── YourConfig.java
│   └── constants/                             # 常量定义
│       └── YourConstants.java
├── src/main/resources/
│   ├── plugin.properties                      # 插件配置
│   ├── mapper_xml/                            # MyBatis XML
│   │   ├── YourMapper.xml
│   │   └── YourMapperEx.xml
│   └── static/                                # 静态资源
└── META-INF/
    └── MANIFEST.MF                            # 插件清单
```

### 插件配置文件示例
**plugin.properties**
```properties
# 插件基本信息
plugin.id=your-plugin-id
plugin.version=1.0.0
plugin.name=Your Plugin Name
plugin.description=Plugin description
plugin.provider=Your Company
plugin.requires=3.5.0

# 插件主类
plugin.class=com.yourcompany.plugin.YourPluginApplication

# 依赖插件（可选）
plugin.dependencies=

# 许可证信息
plugin.license=Apache-2.0
```

---

## 💻 核心开发规范

### 1. 插件主类规范
```java
package com.yourcompany.plugin;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.caller.Caller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 插件主应用类
 */
@SpringBootApplication
@Extract(bus = "yourPluginBus")
public class YourPluginApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(YourPluginApplication.class, args);
    }
    
    /**
     * 插件配置
     */
    @Bean
    public IntegrationConfiguration integrationConfiguration() {
        IntegrationConfiguration configuration = new IntegrationConfiguration();
        configuration.pluginPath("plugins");
        configuration.pluginConfigFilePath("pluginConfig");
        return configuration;
    }
}
```

### 2. Controller层规范
```java
package com.yourcompany.plugin.controller;

import com.jsh.erp.base.BaseController;
import com.jsh.erp.utils.BaseResponseInfo;
import com.jsh.erp.utils.ResponseJsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件控制器 - 必须继承BaseController
 */
@RestController
@RequestMapping("/your-plugin")
@Api(tags = "您的插件管理")
public class YourController extends BaseController {

    @Resource
    private YourService yourService;

    /**
     * 新增数据
     */
    @PostMapping("/add")
    @ApiOperation("新增数据")
    public BaseResponseInfo add(@RequestBody YourEntity entity, HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            yourService.addYourEntity(entity, request);
            return ResponseJsonUtil.returnStr(objectMap, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJsonUtil.returnStr(objectMap, 0);
        }
    }

    /**
     * 获取数据列表
     */
    @GetMapping("/list")
    @ApiOperation("获取数据列表")
    public BaseResponseInfo getList(HttpServletRequest request) throws Exception {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            String search = request.getParameter("search");
            String name = StringUtil.getInfo(search, "name");
            
            List<YourEntity> dataList = yourService.getYourEntityList(name);
            objectMap.put("rows", dataList);
            return ResponseJsonUtil.returnStr(objectMap, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJsonUtil.returnStr(objectMap, 0);
        }
    }
}
```

### 3. Service层规范
```java
package com.yourcompany.plugin.service;

import com.jsh.erp.utils.StringUtil;
import com.jsh.erp.utils.Tools;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 业务逻辑服务类
 */
@Service
public class YourService {

    @Resource
    private YourMapper yourMapper;
    
    @Resource
    private YourMapperEx yourMapperEx;

    /**
     * 新增数据
     */
    public int addYourEntity(YourEntity entity, HttpServletRequest request) throws Exception {
        // 获取当前用户和租户信息
        User userInfo = (User) request.getSession().getAttribute("user");
        if (userInfo != null) {
            entity.setTenantId(userInfo.getTenantId());
        }
        
        // 设置通用字段
        entity.setCreateTime(new Date());
        entity.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        
        return yourMapper.insertSelective(entity);
    }

    /**
     * 获取数据列表
     */
    public List<YourEntity> getYourEntityList(String name) throws Exception {
        // 获取当前租户ID
        Long tenantId = getCurrentTenantId();
        
        YourEntityExample example = new YourEntityExample();
        YourEntityExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualTo(BusinessConstants.DELETE_FLAG_EXISTS);
        
        if (tenantId != null) {
            criteria.andTenantIdEqualTo(tenantId);
        }
        
        if (StringUtil.isNotEmpty(name)) {
            criteria.andNameLike("%" + name + "%");
        }
        
        example.setOrderByClause("create_time desc");
        return yourMapper.selectByExample(example);
    }

    /**
     * 获取当前租户ID
     */
    private Long getCurrentTenantId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("X-Access-Token");
            return Tools.getTenantIdByToken(token);
        } catch (Exception e) {
            return 0L;
        }
    }
}
```

---

## 🗄️ 数据库设计规范

### 1. 表命名规范
```sql
-- 插件表命名必须以 jsh_ 开头
-- 格式: jsh_[plugin_name]_[table_name]
-- 示例: jsh_schedule_shift, jsh_coffeeshop_order
```

### 2. 标准字段规范
```sql
-- 所有表必须包含的标准字段
CREATE TABLE jsh_your_table (
    -- 主键：自增长ID
    id BIGINT(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    
    -- 多租户字段：租户ID（必需）
    tenant_id BIGINT(20) NOT NULL DEFAULT 0 COMMENT '租户ID',
    
    -- 软删除字段（必需）
    delete_flag VARCHAR(1) DEFAULT '0' COMMENT '删除标记，0-正常，1-删除',
    
    -- 审计字段（推荐）
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT(20) COMMENT '创建人',
    update_by BIGINT(20) COMMENT '更新人',
    
    -- 业务字段
    your_business_field VARCHAR(255) COMMENT '业务字段',
    
    -- 索引
    INDEX idx_tenant_id (tenant_id),
    INDEX idx_delete_flag (delete_flag),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='您的表注释';
```

### 3. 实体类规范
```java
package com.yourcompany.plugin.datasource.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * 实体类规范
 */
public class YourEntity implements Serializable {
    
    private Long id;
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    private Date updateTime;
    private Long createBy;
    private Long updateBy;
    
    // 业务字段
    private String yourBusinessField;
    
    // 构造函数
    public YourEntity() {}
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
        this.deleteFlag = deleteFlag;
    }
    
    // ... 其他getter/setter方法
    
    @Override
    public String toString() {
        return "YourEntity{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", deleteFlag='" + deleteFlag + '\'' +
                ", yourBusinessField='" + yourBusinessField + '\'' +
                '}';
    }
}
```

---

## 🌐 API开发规范

### 1. RESTful API设计
```
GET    /api/your-plugin/items       # 获取列表
GET    /api/your-plugin/items/{id}  # 获取详情
POST   /api/your-plugin/items       # 新增
PUT    /api/your-plugin/items/{id}  # 更新
DELETE /api/your-plugin/items/{id}  # 删除
```

### 2. 统一响应格式
```java
/**
 * 成功响应
 */
{
    "code": 200,
    "data": {
        "rows": [...],  // 列表数据
        "total": 100    // 总数（分页时）
    }
}

/**
 * 错误响应
 */
{
    "code": 500,
    "data": "错误信息描述"
}
```

### 3. 分页查询规范
```java
@GetMapping("/list")
public BaseResponseInfo getList(HttpServletRequest request) throws Exception {
    Map<String, Object> objectMap = new HashMap<>();
    try {
        // 启动分页
        PageUtils.startPage();
        
        // 查询数据
        List<YourEntity> list = yourService.getYourEntityList(request);
        
        // 返回分页结果
        return getDataTable(list);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseJsonUtil.returnStr(objectMap, 0);
    }
}
```

### 4. Swagger文档注解
```java
@Api(tags = "您的插件管理")
@RestController
@RequestMapping("/your-plugin")
public class YourController extends BaseController {

    @ApiOperation(value = "新增数据", notes = "新增数据的详细说明")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "名称", required = true, dataType = "String"),
        @ApiImplicitParam(name = "type", value = "类型", required = false, dataType = "String")
    })
    @PostMapping("/add")
    public BaseResponseInfo add(@RequestBody YourEntity entity, HttpServletRequest request) {
        // 实现逻辑
    }
}
```

---

## 🔐 安全与权限规范

### 1. 多租户数据隔离
```java
/**
 * 服务层必须进行租户隔离
 */
public List<YourEntity> getYourEntityList() throws Exception {
    // 获取当前租户ID
    Long tenantId = getCurrentTenantId();
    
    YourEntityExample example = new YourEntityExample();
    YourEntityExample.Criteria criteria = example.createCriteria();
    criteria.andDeleteFlagEqualTo(BusinessConstants.DELETE_FLAG_EXISTS);
    
    // 重要：必须进行租户隔离
    if (tenantId != null && tenantId != 0L) {
        criteria.andTenantIdEqualTo(tenantId);
    }
    
    return yourMapper.selectByExample(example);
}
```

### 2. 权限控制
```java
/**
 * 功能权限注册（在jsh_function表中）
 */
INSERT INTO jsh_function (
    number, name, parent_number, url, component, 
    enabled, type, tenant_id, delete_flag
) VALUES (
    'your-plugin', '您的插件', '', '/your-plugin', 'your-plugin/index',
    '1', '1', 0, '0'
);

/**
 * 用户业务权限检查
 */
@Service
public class YourPermissionService {
    
    public boolean hasPermission(String userId, String businessType) {
        // 检查用户是否有指定业务权限
        UserBusiness userBusiness = userBusinessService.findByUserIdAndType(userId, businessType);
        return userBusiness != null;
    }
}
```

### 3. Token验证
```java
/**
 * 控制器层Token验证
 */
@PostMapping("/add")
public BaseResponseInfo add(@RequestBody YourEntity entity, HttpServletRequest request) {
    try {
        // 获取Token
        String token = request.getHeader("X-Access-Token");
        if (StringUtil.isEmpty(token)) {
            return ResponseJsonUtil.returnStr(null, Constants.RES_STATE_LOGIN);
        }
        
        // 验证Token有效性
        User user = userService.getUserByToken(token);
        if (user == null) {
            return ResponseJsonUtil.returnStr(null, Constants.RES_STATE_LOGIN);
        }
        
        // 业务逻辑
        yourService.addYourEntity(entity, request);
        return ResponseJsonUtil.returnStr(null, Constants.RES_STATE_SEARCH_SUCCESS);
        
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseJsonUtil.returnStr(null, Constants.RES_STATE_SEARCH_ERROR);
    }
}
```

---

## 📦 部署与配置规范

### 1. 插件配置文件
**application-plugin.yml**
```yaml
# 插件专用配置
plugin:
  # 插件运行模式
  runMode: prod
  # 插件存放路径
  pluginPath: plugins
  # 插件配置文件路径
  pluginConfigFilePath: pluginConfig
  # 插件REST控制器路径
  pluginRestControllerPathPrefix: /api/plugin

# 数据源配置（如果需要独立数据源）
datasource:
  plugin:
    url: jdbc:mysql://localhost:3306/jsherp_plugin?useUnicode=true&characterEncoding=UTF-8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver

# MyBatis配置
mybatis-plus:
  mapper-locations: classpath:mapper_xml/*.xml
  type-aliases-package: com.yourcompany.plugin.datasource.entities
```

### 2. 插件描述符
**META-INF/MANIFEST.MF**
```
Manifest-Version: 1.0
Plugin-Id: your-plugin-id
Plugin-Version: 1.0.0
Plugin-Name: Your Plugin Name
Plugin-Description: Plugin description
Plugin-Provider: Your Company
Plugin-Class: com.yourcompany.plugin.YourPluginApplication
Plugin-Dependencies: 
Created-By: Your Company
```

### 3. Maven构建配置
```xml
<build>
    <finalName>your-plugin-${project.version}</finalName>
    <plugins>
        <!-- 插件打包插件 -->
        <plugin>
            <groupId>com.gitee.starblues</groupId>
            <artifactId>springboot-plugin-maven-plugin</artifactId>
            <version>2.2.1-RELEASE</version>
            <configuration>
                <pluginInfo>
                    <pluginId>${project.artifactId}</pluginId>
                    <pluginVersion>${project.version}</pluginVersion>
                    <pluginName>${project.name}</pluginName>
                    <pluginDescription>${project.description}</pluginDescription>
                    <pluginProvider>Your Company</pluginProvider>
                </pluginInfo>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

---

## ✨ 最佳实践

### 1. 错误处理
```java
/**
 * 统一异常处理
 */
@ControllerAdvice
public class PluginExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    public BaseResponseInfo handleException(Exception e) {
        logger.error("插件执行异常", e);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("message", "系统异常，请联系管理员");
        return ResponseJsonUtil.returnStr(objectMap, 0);
    }
}
```

### 2. 缓存使用
```java
/**
 * Redis缓存使用
 */
@Service
public class YourCacheService {
    
    @Resource
    private RedisService redisService;
    
    private static final String CACHE_PREFIX = "your_plugin:";
    private static final int CACHE_EXPIRE = 3600; // 1小时
    
    public YourEntity getFromCache(String key) {
        String cacheKey = CACHE_PREFIX + key;
        return redisService.getObjectFromSessionOfHash(cacheKey, key, YourEntity.class);
    }
    
    public void putToCache(String key, YourEntity entity) {
        String cacheKey = CACHE_PREFIX + key;
        redisService.setObjectToSessionOfHash(cacheKey, key, entity, CACHE_EXPIRE);
    }
}
```

### 3. 日志记录
```java
/**
 * 操作日志记录
 */
@Service
public class YourLogService {
    
    @Resource
    private LogService logService;
    
    public void recordOperation(String operation, String content, HttpServletRequest request) {
        try {
            User user = (User) request.getSession().getAttribute("user");
            Log log = new Log();
            log.setUserId(user != null ? user.getId() : null);
            log.setOperation(operation);
            log.setContent(content);
            log.setCreateTime(new Date());
            log.setClientIp(Tools.getLocalIp(request));
            logService.insertLog(log);
        } catch (Exception e) {
            logger.error("记录操作日志失败", e);
        }
    }
}
```

### 4. 性能优化
```java
/**
 * 分页查询优化
 */
public class YourOptimizedService {
    
    /**
     * 使用分页查询避免大量数据
     */
    public List<YourEntity> getYourEntityListWithPaging(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return yourMapper.selectByExample(new YourEntityExample());
    }
    
    /**
     * 使用批量操作提高性能
     */
    public int batchInsert(List<YourEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        
        int batchSize = 1000;
        int totalCount = 0;
        
        for (int i = 0; i < entities.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, entities.size());
            List<YourEntity> batch = entities.subList(i, endIndex);
            totalCount += yourMapperEx.batchInsert(batch);
        }
        
        return totalCount;
    }
}
```

---

## 🔧 示例代码

### 1. 完整的插件示例项目结构

**排班管理插件示例**
```java
// 主类
@SpringBootApplication
@Extract(bus = "schedulePluginBus")
public class SchedulePluginApplication {
    public static void main(String[] args) {
        SpringApplication.run(SchedulePluginApplication.class, args);
    }
}

// 实体类
public class ScheduleShift implements Serializable {
    private Long id;
    private String shiftName;
    private String shiftCode;
    private String startTime;
    private String endTime;
    private BigDecimal workHours;
    private BigDecimal hourlyRate;
    private String shiftType;
    private String isActive;
    private Long tenantId;
    private String deleteFlag;
    private Date createTime;
    // ... getters and setters
}

// 控制器
@RestController
@RequestMapping("/schedule")
@Api(tags = "排班管理")
public class ScheduleController extends BaseController {
    
    @Resource
    private ScheduleService scheduleService;
    
    @PostMapping("/shift/add")
    @ApiOperation("新增班次")
    public BaseResponseInfo addShift(@RequestBody ScheduleShift shift, HttpServletRequest request) {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            scheduleService.addScheduleShift(shift, request);
            return ResponseJsonUtil.returnStr(objectMap, 1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJsonUtil.returnStr(objectMap, 0);
        }
    }
    
    @GetMapping("/shift/list")
    @ApiOperation("班次列表")
    public BaseResponseInfo getShiftList(HttpServletRequest request) {
        Map<String, Object> objectMap = new HashMap<>();
        try {
            PageUtils.startPage();
            List<ScheduleShift> list = scheduleService.getScheduleShiftList(request);
            return getDataTable(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseJsonUtil.returnStr(objectMap, 0);
        }
    }
}

// 服务类
@Service
public class ScheduleService {
    
    @Resource
    private ScheduleShiftMapper scheduleShiftMapper;
    
    public int addScheduleShift(ScheduleShift shift, HttpServletRequest request) throws Exception {
        User userInfo = (User) request.getSession().getAttribute("user");
        if (userInfo != null) {
            shift.setTenantId(userInfo.getTenantId());
        }
        
        shift.setCreateTime(new Date());
        shift.setDeleteFlag(BusinessConstants.DELETE_FLAG_EXISTS);
        shift.setIsActive("1");
        
        return scheduleShiftMapper.insertSelective(shift);
    }
    
    public List<ScheduleShift> getScheduleShiftList(HttpServletRequest request) throws Exception {
        String search = request.getParameter("search");
        String shiftName = StringUtil.getInfo(search, "shiftName");
        
        Long tenantId = getCurrentTenantId(request);
        
        ScheduleShiftExample example = new ScheduleShiftExample();
        ScheduleShiftExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteFlagEqualTo(BusinessConstants.DELETE_FLAG_EXISTS);
        
        if (tenantId != null && tenantId != 0L) {
            criteria.andTenantIdEqualTo(tenantId);
        }
        
        if (StringUtil.isNotEmpty(shiftName)) {
            criteria.andShiftNameLike("%" + shiftName + "%");
        }
        
        example.setOrderByClause("create_time desc");
        return scheduleShiftMapper.selectByExample(example);
    }
    
    private Long getCurrentTenantId(HttpServletRequest request) {
        try {
            String token = request.getHeader("X-Access-Token");
            return Tools.getTenantIdByToken(token);
        } catch (Exception e) {
            return 0L;
        }
    }
}
```

### 2. MyBatis Mapper示例
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.yourcompany.plugin.datasource.mappers.ScheduleShiftMapperEx">

    <!-- 批量插入 -->
    <insert id="batchInsert" parameterType="java.util.List">
        INSERT INTO jsh_schedule_shift (
            shift_name, shift_code, start_time, end_time, work_hours,
            hourly_rate, shift_type, is_active, tenant_id, delete_flag, create_time
        ) VALUES
        <foreach collection="list" item="item" separator=",">
            (
                #{item.shiftName}, #{item.shiftCode}, #{item.startTime}, 
                #{item.endTime}, #{item.workHours}, #{item.hourlyRate},
                #{item.shiftType}, #{item.isActive}, #{item.tenantId}, 
                #{item.deleteFlag}, #{item.createTime}
            )
        </foreach>
    </insert>

    <!-- 复杂查询示例 -->
    <select id="getShiftStatistics" resultType="java.util.Map">
        SELECT 
            shift_type,
            COUNT(*) as shift_count,
            AVG(work_hours) as avg_work_hours,
            SUM(hourly_rate) as total_hourly_rate
        FROM jsh_schedule_shift 
        WHERE delete_flag = '0'
          AND tenant_id = #{tenantId}
          AND is_active = '1'
        GROUP BY shift_type
        ORDER BY shift_count DESC
    </select>

</mapper>
```

---

## 7. 功能权限和菜单配置规范

### 7.1 功能编号规范

插件功能编号应遵循jshERP的编号体系：

#### 编号分配策略
```
主功能编号：使用3位数字（如：040）
子功能编号：主功能编号 + 2位序号（如：04001、04002）
功能ID：使用递增的数字ID（如：260、261、262）
```

#### 编号示例
```sql
-- 主功能模块
040 - 插件主功能模块名称

-- 子功能模块  
04001 - 子功能1
04002 - 子功能2
04003 - 子功能3
04004 - 子功能4
```

### 7.2 功能权限配置规范

#### 标准功能权限表结构
```sql
INSERT INTO `jsh_function` (
    `id`,           -- 功能ID（递增数字）
    `number`,       -- 功能编号（字符串）
    `name`,         -- 功能名称
    `parent_number`,-- 父功能编号
    `url`,          -- 访问URL
    `component`,    -- 前端组件路径
    `state`,        -- 收缩状态（0-展开，1-收缩）
    `sort`,         -- 排序字段（通常与number相同）
    `enabled`,      -- 是否启用（1-启用，0-禁用）
    `type`,         -- 功能类型（固定值：'电脑版'）
    `push_btn`,     -- 按钮权限配置（JSON格式）
    `icon`,         -- 图标类名
    `delete_flag`   -- 删除标记（'0'-正常，'1'-删除）
) VALUES (功能配置);
```

#### 按钮权限配置格式
```json
[
    {
        "name": "新增",
        "title": "新增",
        "type": "primary",
        "size": "",
        "plain": false,
        "icon": "el-icon-plus",
        "jurisdiction": "",
        "judgeShow": true
    },
    {
        "name": "修改",
        "title": "修改", 
        "type": "primary",
        "size": "",
        "plain": false,
        "icon": "el-icon-edit",
        "jurisdiction": "",
        "judgeShow": true
    }
]
```

### 7.3 角色权限分配规范

#### 权限关系表配置
```sql
-- 查看现有角色
SELECT id, name, type, description FROM `jsh_role` WHERE delete_flag = '0';

-- 为角色分配功能权限
UPDATE `jsh_user_business` 
SET `value` = CASE 
    WHEN `value` IS NULL OR `value` = '' THEN '[功能ID1][功能ID2][功能ID3]'
    ELSE CONCAT(`value`, '[功能ID1][功能ID2][功能ID3]')
END
WHERE `type` = 'RoleFunctions' AND `key_id` = '角色ID' AND `delete_flag` = '0';
```

#### 权限验证SQL
```sql
-- 验证功能是否正确添加
SELECT id, number, name, parent_number, url, enabled, icon 
FROM `jsh_function` 
WHERE number LIKE '插件编号前缀%' 
AND delete_flag = '0'
ORDER BY number;

-- 验证角色权限分配
SELECT 
    r.id as role_id,
    r.name as role_name,
    ub.value as function_ids
FROM `jsh_role` r
LEFT JOIN `jsh_user_business` ub ON r.id = ub.key_id 
WHERE ub.type = 'RoleFunctions' AND ub.delete_flag = '0'
ORDER BY r.id;
```

### 7.4 权限配置最佳实践

#### 1. 创建权限安装脚本
创建独立的权限配置脚本：`install_permissions.sql`

```sql
-- ================================================
-- 插件名称 - 权限安装脚本
-- ================================================

-- 查询当前最大功能ID（避免冲突）
SELECT MAX(id) as max_id FROM `jsh_function`;

-- 功能权限配置（使用ON DUPLICATE KEY UPDATE支持重复执行）
INSERT INTO `jsh_function` (...) VALUES (...) 
ON DUPLICATE KEY UPDATE 
`name` = VALUES(`name`), 
`enabled` = VALUES(`enabled`), 
`push_btn` = VALUES(`push_btn`);

-- 权限分配示例（注释形式，需要手动调整）
/*
UPDATE `jsh_user_business` 
SET `value` = CONCAT(IFNULL(`value`, ''), '[功能ID列表]')
WHERE `type` = 'RoleFunctions' AND `key_id` = '角色ID' AND `delete_flag` = '0';
*/
```

#### 2. 创建权限配置文档
在插件根目录创建`INSTALL.md`，包含：
- 安装前准备
- 详细安装步骤
- 权限配置说明
- 验证和测试方法
- 故障排除指南

#### 3. 菜单路由配置规范
```
URL路径：/插件名称/功能模块
前端组件：/PluginName/ModuleName
图标：使用Element UI图标（el-icon-*）
```

### 7.5 插件权限配置模板

创建标准的插件权限配置模板：

```sql
-- 模板：插件功能权限配置
-- 插件名称：[插件名称]
-- 功能编号前缀：[XXX]

-- 主功能模块
INSERT INTO `jsh_function` (`id`, `number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) VALUES 
([主功能ID], '[主功能编号]', '[插件名称]', '0', '', 'Layout', 0, '[主功能编号]', 1, '电脑版', '', '[图标]', '0');

-- 子功能1
INSERT INTO `jsh_function` (`id`, `number`, `name`, `parent_number`, `url`, `component`, `state`, `sort`, `enabled`, `type`, `push_btn`, `icon`, `delete_flag`) VALUES 
([子功能ID1], '[子功能编号1]', '[子功能名称1]', '[主功能编号]', '/[插件路径]/[功能路径1]', '/[插件组件]/[功能组件1]', 0, '[子功能编号1]', 1, '电脑版', '[按钮权限JSON]', '[图标]', '0');

-- 权限分配（需要根据实际角色ID调整）
-- UPDATE `jsh_user_business` SET `value` = CONCAT(IFNULL(`value`, ''), '[功能ID列表]') WHERE `type` = 'RoleFunctions' AND `key_id` = '[角色ID]' AND `delete_flag` = '0';
```

---

## 📋 检查清单

### 开发前检查
- [ ] 确认插件功能需求和边界
- [ ] 设计数据库表结构
- [ ] 确认API接口设计
- [ ] 规划功能权限编号（避免与现有功能冲突）
- [ ] 制定开发计划

### 开发中检查
- [ ] 遵循目录结构规范
- [ ] 实现多租户数据隔离
- [ ] 添加适当的日志记录
- [ ] 实现错误处理机制
- [ ] 创建功能权限配置脚本
- [ ] 编写权限安装文档
- [ ] 编写单元测试

### 发布前检查
- [ ] 完成功能测试
- [ ] 完成权限配置测试
- [ ] 验证菜单和按钮权限控制
- [ ] 完成安全测试
- [ ] 完成性能测试
- [ ] 准备部署文档和安装指南
- [ ] 生成插件包

### 部署后检查
- [ ] 验证插件加载成功
- [ ] 验证数据库表创建成功
- [ ] 验证功能权限配置正确
- [ ] 验证菜单显示和权限控制
- [ ] 验证功能正常运行
- [ ] 检查日志输出
- [ ] 监控系统性能
- [ ] 收集用户反馈

---

## 📞 技术支持

### 开发文档参考
- [SpringBoot Plugin Framework文档](https://gitee.com/starblues/springboot-plugin-framework)
- [jshERP官方文档](https://www.jshERP.com)
- [MyBatis官方文档](https://mybatis.org/mybatis-3/)

### 常见问题解决
1. **插件加载失败**: 检查plugin.properties配置和MANIFEST.MF文件
2. **数据库连接问题**: 确认数据源配置和权限
3. **多租户数据混乱**: 检查所有查询是否包含tenantId过滤
4. **权限验证失败**: 确认功能已在jsh_function表中注册

### 联系方式
- **技术支持邮箱**: tech-support@yourcompany.com
- **开发者论坛**: https://forum.jsherp.com
- **问题反馈**: 请在GitHub Issues中提交

---

*本文档版本：v1.0.0*  
*最后更新：2024年12月*  
*适用于：jshERP 3.5.0+*