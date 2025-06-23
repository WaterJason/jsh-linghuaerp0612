# 掐丝珐琅馆模块 - jshERP业务扩展

<div align="center">

![jshERP Logo](https://img.shields.io/badge/jshERP-掐丝珐琅馆模块-blue?style=for-the-badge)
![Version](https://img.shields.io/badge/version-v1.0-green?style=for-the-badge)
![License](https://img.shields.io/badge/license-MIT-orange?style=for-the-badge)

**专为博物馆、艺术馆等文化场所设计的综合管理系统**

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [技术架构](#-技术架构) • [部署指南](#-部署指南) • [API文档](#-api文档)

</div>

---

## 📋 项目概述

掐丝珐琅馆模块是基于jshERP开发的专业化业务扩展模块，专门服务于博物馆、艺术馆、文化展览馆等场所的日常运营管理需求。该模块采用现代化的技术架构，提供员工排班、咖啡店销售、POS销售、任务管理等核心功能，与jshERP现有系统无缝集成。

### 🎯 设计理念
- **专业化**：针对文化场所的特殊需求定制开发
- **集成化**：与jshERP现有模块深度集成，数据互通
- **现代化**：采用最新的前端技术和用户体验设计
- **安全性**：完整的多租户架构和权限控制体系

---

## ✨ 功能特性

### 🗓️ 智能排班管理
- **多视图支持**：日历视图、列表视图、统计视图
- **拖拽排班**：直观的拖拽操作，快速安排员工班次
- **冲突检测**：智能检测排班冲突，避免重复安排
- **批量操作**：支持批量创建、修改、删除排班记录
- **统计分析**：员工工时统计、出勤率分析、班次分布

### 📊 实时数据仪表板
- **运营概览**：实时显示关键业务指标
- **值班信息**：当前值班人员详情和联系方式
- **销售趋势**：可视化销售数据趋势图表
- **快速操作**：一键录入销售、创建任务、安排排班

### ☕ 咖啡店销售管理
- **销售记录**：日销售数据录入和管理
- **图片上传**：销售凭证图片存储
- **趋势分析**：销售数据对比和趋势分析
- **财务集成**：自动生成财务收入记录

### 🛍️ POS销售系统
- **商品管理**：商品信息维护和分类管理
- **订单处理**：完整的销售订单流程
- **支付支持**：现金、支付宝、微信、银行卡多种支付方式
- **库存同步**：与jshERP库存模块实时同步

### 📋 任务管理系统
- **任务分配**：灵活的任务创建和分配机制
- **进度跟踪**：实时跟踪任务执行进度
- **优先级管理**：支持任务优先级设置
- **完成统计**：任务完成率统计和分析

---

## 🏗️ 技术架构

### 后端技术栈
- **核心框架**：Spring Boot 2.x
- **数据访问**：MyBatis + MyBatis Plus
- **数据库**：MySQL 5.7.33
- **缓存**：Redis 6.2.1
- **认证**：JWT Token
- **API文档**：Swagger2

### 前端技术栈
- **核心框架**：Vue.js 2.7.16
- **UI组件库**：Ant Design Vue 1.5.2
- **路由管理**：Vue Router 3.0.1
- **状态管理**：Vuex 3.1.0
- **HTTP客户端**：Axios 0.18.0
- **图表库**：ECharts 5.x

### 架构特点
- **多租户架构**：完整的数据隔离和权限控制
- **微服务友好**：模块化设计，易于扩展
- **RESTful API**：标准的REST接口设计
- **响应式设计**：完美适配PC和移动端

---

## 🚀 快速开始

### 环境要求
- **Java**: JDK 1.8+
- **Node.js**: 14.x+
- **MySQL**: 5.7+
- **Redis**: 6.0+
- **Maven**: 3.6+

### 安装步骤

1. **数据库初始化**
```bash
mysql -u root -p < jshERP-boot/docs/cloisonne_module_tables.sql
```

2. **后端部署**
```bash
cd jshERP-boot
mvn clean package -DskipTests
java -jar target/jshERP-boot-*.jar
```

3. **前端部署**
```bash
cd jshERP-web
npm install
npm run serve
```

4. **自动化部署**（推荐）
```bash
chmod +x jshERP-boot/scripts/deploy-cloisonne-module.sh
./jshERP-boot/scripts/deploy-cloisonne-module.sh
```

### 访问系统
- **前端地址**：http://localhost:8080
- **后端API**：http://localhost:9999
- **默认账户**：admin / 123456

---

## 📚 项目结构

```
cloisonne-module/
├── jshERP-boot/                    # 后端项目
│   ├── src/main/java/
│   │   └── com/jsh/erp/
│   │       ├── controller/         # 控制器层
│   │       ├── service/cloisonne/  # 业务逻辑层
│   │       └── datasource/         # 数据访问层
│   ├── src/main/resources/
│   │   └── mapper_xml/cloisonne/   # MyBatis映射文件
│   ├── docs/                       # 文档目录
│   └── scripts/                    # 部署脚本
├── jshERP-web/                     # 前端项目
│   ├── src/
│   │   ├── views/cloisonne/        # 页面组件
│   │   ├── api/cloisonne.js        # API接口
│   │   └── router/modules/cloisonne.js # 路由配置
│   └── public/                     # 静态资源
└── README.md                       # 项目说明
```

---

## 🔧 配置说明

### 数据库配置
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jsh_erp?useUnicode=true&characterEncoding=utf8
    username: jsh_user
    password: 123456
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

### 权限配置
```sql
-- 为用户分配掐丝珐琅馆权限
INSERT INTO jsh_user_business (type, key_id, value, btn_str, tenant_id, delete_flag) 
VALUES ('UserRole', '用户ID', '10,1001,1002,1003,1004', '1,2,3,7', '租户ID', '0');
```

---

## 📖 API文档

### 排班管理API
```http
GET    /cloisonne/schedule/list          # 获取排班列表
POST   /cloisonne/schedule/add           # 创建排班
PUT    /cloisonne/schedule/update        # 更新排班
DELETE /cloisonne/schedule/delete/{id}   # 删除排班
GET    /cloisonne/schedule/statistics    # 获取统计数据
```

### 仪表板API
```http
GET    /cloisonne/dashboard/overview     # 获取概览数据
POST   /cloisonne/dashboard/coffee-sales # 创建咖啡店销售
POST   /cloisonne/dashboard/pos-order    # 创建POS订单
```

### 完整API文档
访问 http://localhost:9999/swagger-ui.html 查看完整的API文档

---

## 🛡️ 安全特性

### 多租户数据隔离
- 所有数据表包含 `tenant_id` 字段
- 自动注入租户上下文
- 强制数据过滤，确保数据安全

### 权限控制体系
- **菜单权限**：控制页面访问
- **按钮权限**：控制操作权限
- **数据权限**：控制数据范围

### 数据安全
- **软删除**：`delete_flag` 字段标记删除
- **审计日志**：完整的操作记录
- **数据加密**：敏感数据加密存储

---

## 📊 性能监控

### 监控脚本
```bash
# 查看性能概览
./jshERP-boot/scripts/monitor-cloisonne-performance.sh

# 实时监控
./jshERP-boot/scripts/monitor-cloisonne-performance.sh -m

# 生成性能报告
./jshERP-boot/scripts/monitor-cloisonne-performance.sh -r
```

### 关键指标
- **数据库连接数**：监控连接池使用情况
- **查询性能**：慢查询统计和优化
- **缓存命中率**：Redis缓存效果
- **表空间使用**：存储空间监控

---

## 📞 技术支持

### 联系方式
- **邮箱**：support@jsherp.com
- **官网**：https://www.jsherp.com
- **文档**：https://docs.jsherp.com/cloisonne
- **GitHub**：https://github.com/jsherp/cloisonne-module

### 问题反馈
如果您在使用过程中遇到问题，请通过以下方式反馈：
1. GitHub Issues
2. 技术支持邮箱
3. 官方QQ群：123456789

---

## 🎉 致谢

感谢所有为本项目做出贡献的开发者和用户！

特别感谢：
- [jshERP](https://gitee.com/jishenghua/JSH_ERP) 提供的优秀基础框架
- [Ant Design Vue](https://antdv.com/) 提供的优秀UI组件库
- [ECharts](https://echarts.apache.org/) 提供的强大图表库

---

<div align="center">

**⭐ 如果这个项目对您有帮助，请给我们一个Star！⭐**

Made with ❤️ by jshERP Team

</div>
