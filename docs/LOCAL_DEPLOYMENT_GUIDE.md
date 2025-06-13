# jshERP 本地开发环境部署指南

## 概述

本指南介绍如何将 jshERP 项目从 Docker 容器化部署迁移到本地开发环境。在新的部署方式中：

- **基础设施服务**（MySQL、Redis、Nginx）继续在 Docker 中运行
- **应用服务**（SpringBoot 后端、Vue.js 前端）直接在宿主机运行

这种混合部署方式结合了 Docker 的便捷性和本地开发的灵活性。

## 架构对比

### 原 Docker 架构
```
┌─────────────────────────────────────────┐
│              Docker 环境                │
├─────────────┬─────────────┬─────────────┤
│   前端容器  │   后端容器  │  基础设施   │
│   Vue.js    │  SpringBoot │ MySQL/Redis │
│   Port 3000 │  Port 9999  │   Nginx     │
└─────────────┴─────────────┴─────────────┘
```

### 新混合架构
```
┌─────────────────────┐ ┌─────────────────────┐
│      宿主机环境     │ │    Docker 环境      │
├─────────┬───────────┤ ├─────────────────────┤
│ 前端    │ 后端      │ │     基础设施        │
│ Vue.js  │SpringBoot │ │  MySQL/Redis/Nginx  │
│Port 3000│Port 9999  │ │   Port 3306/6379    │
└─────────┴───────────┘ └─────────────────────┘
```

## 迁移步骤

### 1. 环境准备

#### 1.1 安装系统依赖

```bash
# 运行依赖安装脚本
chmod +x scripts/install-dependencies.sh
./scripts/install-dependencies.sh
```

该脚本将自动安装：
- Java 8 JDK
- Maven 3.x
- Node.js 18+
- Yarn
- Docker & Docker Compose

#### 1.2 验证环境

```bash
# 检查 Java 版本
java -version

# 检查 Maven 版本
mvn -version

# 检查 Node.js 版本
node -v

# 检查 Yarn 版本
yarn -v

# 检查 Docker 版本
docker --version
docker-compose --version
```

### 2. 停止现有 Docker 服务

```bash
# 停止所有 Docker 服务
docker-compose -f docker-compose.dev.yml down

# 清理容器（可选）
docker system prune -f
```

### 3. 启动基础设施服务

```bash
# 启动 MySQL、Redis、Nginx 基础设施
docker-compose -f docker-compose.infrastructure.yml up -d

# 检查服务状态
docker-compose -f docker-compose.infrastructure.yml ps
```

### 4. 配置应用服务

#### 4.1 后端配置

后端使用 `application-local.properties` 配置文件：

```properties
# 服务器配置
server.port=9999
server.servlet.context-path=/jshERP-boot

# 数据库配置（连接到 Docker MySQL）
spring.datasource.url=jdbc:mysql://localhost:3306/jsh_erp?...
spring.datasource.username=jsh_user
spring.datasource.password=123456

# Redis 配置（连接到 Docker Redis）
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=1234abcd
```

#### 4.2 前端配置

前端的 `vue.config.js` 已更新代理配置：

```javascript
devServer: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
        '/jshERP-boot': {
            target: 'http://localhost:9999', // 指向本地后端
            ws: false,
            changeOrigin: true
        }
    }
}
```

### 5. 启动应用服务

#### 5.1 使用自动化脚本（推荐）

```bash
# 一键启动所有服务
chmod +x scripts/start-local.sh
./scripts/start-local.sh
```

#### 5.2 手动启动

**启动后端服务：**
```bash
cd jshERP-boot
export SPRING_PROFILES_ACTIVE=local
mvn spring-boot:run
```

**启动前端服务：**
```bash
cd jshERP-web
yarn install
yarn serve
```

## 服务管理

### 启动服务
```bash
./scripts/start-local.sh    # 启动所有服务
```

### 停止服务
```bash
./scripts/stop-local.sh     # 停止所有服务
```

### 查看状态
```bash
./scripts/status-local.sh   # 检查所有服务状态
```

### 查看日志
```bash
./scripts/logs-local.sh                    # 查看所有日志
./scripts/logs-local.sh -f backend         # 实时跟踪后端日志
./scripts/logs-local.sh -n 50 frontend     # 查看前端最后50行日志
```

## 服务访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端应用 | http://localhost:3000 | Vue.js 开发服务器 |
| 后端 API | http://localhost:9999/jshERP-boot | SpringBoot 应用 |
| Nginx 代理 | http://localhost:8000 | 统一代理入口 |
| API 文档 | http://localhost:9999/jshERP-boot/doc.html | Swagger 文档 |
| MySQL | localhost:3306 | 数据库连接 |
| Redis | localhost:6379 | 缓存连接 |

## 开发工作流

### 1. 日常开发启动

```bash
# 1. 启动基础设施（只需要启动一次）
docker-compose -f docker-compose.infrastructure.yml up -d

# 2. 启动应用服务
./scripts/start-local.sh
```

### 2. 代码调试

**后端调试：**
- 直接在 IDE 中启动 SpringBoot 应用
- 使用 `application-local.properties` 配置
- 支持热重载和断点调试

**前端调试：**
- 使用 `yarn serve` 启动开发服务器
- 支持热重载和浏览器调试工具

### 3. 日志查看

```bash
# 实时查看后端日志
tail -f logs/backend.log

# 实时查看前端日志
tail -f logs/frontend.log

# 查看数据库日志
docker logs -f jsherp-mysql-local
```

### 4. 停止开发环境

```bash
# 停止应用服务，保留基础设施
./scripts/stop-local.sh
# 选择 'y' 保留基础设施运行

# 完全停止所有服务
./scripts/stop-local.sh
# 选择 'N' 停止所有服务
```

## 故障排除

### 1. 端口冲突

如果遇到端口被占用的问题：

```bash
# 检查端口占用
lsof -i:3000  # 前端端口
lsof -i:9999  # 后端端口
lsof -i:3306  # MySQL 端口
lsof -i:6379  # Redis 端口

# 终止占用进程
kill $(lsof -ti:端口号)
```

### 2. 数据库连接问题

```bash
# 检查 MySQL 容器状态
docker exec jsherp-mysql-local mysqladmin ping

# 重启 MySQL 服务
docker-compose -f docker-compose.infrastructure.yml restart jsherp-mysql
```

### 3. 前端依赖问题

```bash
# 清理并重新安装依赖
cd jshERP-web
rm -rf node_modules yarn.lock
yarn install
```

### 4. 后端启动问题

```bash
# 检查 Java 环境
java -version

# 清理 Maven 缓存
mvn clean

# 重新编译
mvn compile
```

## 性能优化

### 1. JVM 优化

编辑 `local.env` 文件调整 JVM 参数：

```bash
# JVM 配置
JVM_OPTS=-Xms512m -Xmx1024m -XX:+UseG1GC
```

### 2. Node.js 优化

```bash
# 增加 Node.js 内存限制
export NODE_OPTIONS="--max_old_space_size=4096"
```

### 3. MySQL 优化

编辑 `docker-compose.infrastructure.yml` 中的 MySQL 配置：

```yaml
command: [
  '--innodb_buffer_pool_size=512M',
  '--innodb_log_file_size=128M'
]
```

## 环境变量配置

主要配置文件：`local.env`

```bash
# MySQL 配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=jsh_erp
MYSQL_USER=jsh_user
MYSQL_PASSWORD=123456

# Redis 配置
REDIS_PASSWORD=1234abcd

# 端口配置
HOST_MYSQL_PORT=3306
HOST_REDIS_PORT=6379
HOST_NGINX_PORT=8000

# 开发环境配置
NODE_ENV=development
SPRING_PROFILES_ACTIVE=local
```

## 备份与恢复

### 1. 数据库备份

```bash
# 备份数据库
docker exec jsherp-mysql-local mysqldump -u root -p123456 jsh_erp > backup_$(date +%Y%m%d).sql

# 恢复数据库
docker exec -i jsherp-mysql-local mysql -u root -p123456 jsh_erp < backup_20231201.sql
```

### 2. 配置备份

```bash
# 备份配置文件
tar -czf config_backup_$(date +%Y%m%d).tar.gz \
  jshERP-boot/src/main/resources/application-local.properties \
  jshERP-web/vue.config.js \
  local.env \
  docker-compose.infrastructure.yml
```

## 常见问题

### Q1: 为什么选择混合部署？

**A:** 混合部署结合了 Docker 和本地开发的优势：
- 基础设施标准化，避免环境配置问题
- 应用代码直接运行，便于调试和热重载
- 资源使用更高效，启动速度更快

### Q2: 如何切换回纯 Docker 部署？

**A:** 可以随时切换回原来的 Docker 部署：
```bash
./scripts/stop-local.sh
docker-compose -f docker-compose.dev.yml up -d
```

### Q3: 生产环境怎么部署？

**A:** 生产环境建议使用完整的 Docker 部署：
```bash
docker-compose -f docker-compose.prod.yml up -d
```

### Q4: 如何同时运行多个项目？

**A:** 修改端口配置避免冲突：
- 修改 `local.env` 中的端口设置
- 更新应用配置中的端口号
- 确保数据库名称不冲突

## 技术支持

如果遇到问题，请按以下顺序排查：

1. 查看服务状态：`./scripts/status-local.sh`
2. 检查日志：`./scripts/logs-local.sh`
3. 验证网络连通性
4. 检查配置文件
5. 重启相关服务

更多帮助请参考项目文档或提交 Issue。 