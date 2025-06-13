# jshERP Docker 容器化部署指南

本文档提供了 jshERP 系统的完整 Docker 容器化解决方案，支持开发环境和生产环境的快速搭建。

## 📋 目录

- [系统架构](#系统架构)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [环境配置](#环境配置)
- [开发环境](#开发环境)
- [生产环境](#生产环境)
- [服务管理](#服务管理)
- [端口配置](#端口配置)
- [数据持久化](#数据持久化)
- [常见问题](#常见问题)
- [故障排查](#故障排查)

## 🏗️ 系统架构

jshERP Docker 容器化架构包含以下服务组件：

```
┌─────────────────────────────────────────┐
│              Nginx (1.12.2)             │  ← 反向代理/负载均衡
└─────────────────┬───────────────────────┘
                  │
    ┌─────────────┴──────────────┐
    ▼                            ▼
┌─────────────────┐  ┌─────────────────┐
│  Frontend       │  │   Backend       │
│  Vue 2.7.16     │  │ SpringBoot 2.0  │
│  Node 20.17.0   │  │   JDK 1.8       │
└─────────────────┘  └─────────────────┘
                              │
            ┌─────────────────┴──────────────┐
            ▼                                ▼
    ┌─────────────────┐            ┌─────────────────┐
    │     MySQL       │            │     Redis       │
    │     5.7.33      │            │     6.2.1       │
    └─────────────────┘            └─────────────────┘
```

## 💻 环境要求

### 必需软件
- **Docker**: 20.10.0+
- **Docker Compose**: 1.29.0+
- **操作系统**: Linux/macOS/Windows
- **内存**: 最少 4GB RAM (推荐 8GB+)
- **磁盘空间**: 最少 10GB 可用空间

### 推荐配置
- **CPU**: 4 核心以上
- **内存**: 8GB RAM 以上
- **磁盘**: SSD 硬盘，20GB+ 可用空间## 🚀 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/WaterJason/jshERP
cd jshERP-0612-Cursor
```

### 2. 环境配置
```bash
# 复制环境配置文件（已提供默认配置）
cp .env .env.local  # 可选：创建本地配置副本

# 根据需要修改端口配置
vim .env
```

### 3. 启动开发环境
```bash
# 使用脚本启动（推荐）
./docker/scripts/start-dev.sh

# 或手动启动
docker-compose -f docker-compose.dev.yml up -d
```

### 4. 访问系统
- **前端应用**: http://localhost:3000
- **后端API**: http://localhost:9999/jshERP-boot
- **Nginx代理**: http://localhost:8000
- **默认账号**: admin / 123456

## ⚙️ 环境配置

### 端口配置
在 `.env` 文件中可以自定义各服务的端口映射：

```bash
# 前端服务端口
HOST_FRONTEND_PORT=3000

# 后端服务端口  
HOST_BACKEND_PORT=9999

# MySQL 数据库端口
HOST_MYSQL_PORT=3306

# Redis 缓存端口
HOST_REDIS_PORT=6379

# Nginx 代理端口
HOST_NGINX_PORT=8000
```

### 数据库配置
```bash
# MySQL 配置
MYSQL_ROOT_PASSWORD=123456
MYSQL_DATABASE=jsh_erp
MYSQL_USER=jsh_user
MYSQL_PASSWORD=123456

# Redis 配置  
REDIS_PASSWORD=1234abcd
```## 🔧 开发环境

开发环境支持热重载，代码修改后自动重新编译和部署。

### 启动开发环境
```bash
# 使用启动脚本
./docker/scripts/start-dev.sh

# 手动启动
docker-compose -f docker-compose.dev.yml up -d
```

### 开发环境特性
- ✅ **前端热重载**: Vue 开发服务器自动重新编译
- ✅ **后端热重载**: SpringBoot DevTools 支持
- ✅ **源码挂载**: 本地代码变更实时同步到容器
- ✅ **调试模式**: 支持远程调试
- ✅ **实时日志**: 实时查看应用日志

### 源码挂载目录
```
./jshERP-boot → /app            # 后端源码
./jshERP-web → /app             # 前端源码
~/.m2 → /root/.m2               # Maven 本地仓库
```

## 🏭 生产环境

生产环境优化了性能和安全性配置。

### 启动生产环境
```bash
# 使用启动脚本
./docker/scripts/start-prod.sh

# 手动启动
docker-compose -f docker-compose.prod.yml up -d
```

### 生产环境特性
- ✅ **多阶段构建**: 最小化镜像体积
- ✅ **资源限制**: 限制CPU和内存使用
- ✅ **健康检查**: 自动健康状态监控
- ✅ **日志轮转**: 防止日志文件过大
- ✅ **安全加固**: 非root用户运行

## 📊 服务管理

### 查看服务状态
```bash
# 开发环境
docker-compose -f docker-compose.dev.yml ps

# 生产环境  
docker-compose -f docker-compose.prod.yml ps
```

### 查看服务日志
```bash
# 使用日志脚本
./docker/scripts/logs.sh dev [服务名]

# 手动查看
docker-compose -f docker-compose.dev.yml logs -f [服务名]
```

### 停止服务
```bash
# 使用停止脚本
./docker/scripts/stop.sh [dev|prod|all]

# 手动停止
docker-compose -f docker-compose.dev.yml down
```## 🔌 端口配置

### 默认端口映射
| 服务 | 容器端口 | 宿主机端口 | 说明 |
|------|---------|-----------|------|
| 前端 | 8080 | 3000 | Vue 开发服务器 |
| 后端 | 9999 | 9999 | SpringBoot API |
| MySQL | 3306 | 3306 | 数据库服务 |
| Redis | 6379 | 6379 | 缓存服务 |
| Nginx | 80 | 8000 | 反向代理 |

### 端口冲突解决
如果默认端口被占用，可以修改 `.env` 文件：

```bash
# 例如：修改前端端口为 3001
HOST_FRONTEND_PORT=3001

# 修改 Nginx 端口为 8080  
HOST_NGINX_PORT=8080
```

## 💾 数据持久化

### 数据卷挂载
```
./volumes/mysql     → MySQL 数据目录
./volumes/redis     → Redis 数据目录  
./volumes/uploads   → 文件上传目录
./volumes/logs      → 应用日志目录
```

### 数据备份
```bash
# MySQL 数据备份
docker exec jsherp-mysql-dev mysqldump -uroot -p123456 jsh_erp > backup_$(date +%Y%m%d).sql

# Redis 数据备份
docker exec jsherp-redis-dev redis-cli --rdb /data/dump_$(date +%Y%m%d).rdb
```

## ❓ 常见问题

### Q1: 端口被占用如何解决？
**A**: 修改 `.env` 文件中的端口配置，然后重新启动服务。

### Q2: 如何重置数据库？
**A**: 
```bash
# 停止服务
./docker/scripts/stop.sh

# 删除数据卷
rm -rf ./volumes/mysql

# 重新启动服务
./docker/scripts/start-dev.sh
```

### Q3: 前端修改代码后不生效？
**A**: 确保使用开发环境启动，检查文件挂载是否正确：
```bash
docker-compose -f docker-compose.dev.yml exec jsherp-frontend-dev ls -la /app
```

### Q4: 后端启动失败？
**A**: 检查数据库连接和依赖是否就绪：
```bash
# 查看后端日志
./docker/scripts/logs.sh dev jsherp-backend-dev

# 检查数据库连接
docker-compose -f docker-compose.dev.yml exec jsherp-mysql mysql -uroot -p123456 -e "SHOW DATABASES;"
```## 🔍 故障排查

### 检查容器状态
```bash
# 查看所有容器状态
docker ps -a

# 查看特定容器日志
docker logs -f <容器名>

# 进入容器内部调试
docker exec -it <容器名> /bin/bash
```

### 常见错误及解决方案

#### 1. 数据库连接失败
```bash
# 检查 MySQL 容器状态
docker-compose -f docker-compose.dev.yml ps jsherp-mysql

# 检查网络连通性
docker-compose -f docker-compose.dev.yml exec jsherp-backend-dev ping jsherp-mysql
```

#### 2. Redis 连接失败
```bash
# 测试 Redis 连接
docker-compose -f docker-compose.dev.yml exec jsherp-redis redis-cli ping
```

#### 3. 文件上传失败
```bash
# 检查上传目录权限
docker-compose -f docker-compose.dev.yml exec jsherp-backend-dev ls -la /opt/jshERP/upload
```

#### 4. 前端资源加载失败
```bash
# 检查 Nginx 配置
docker-compose -f docker-compose.dev.yml exec jsherp-nginx-dev nginx -t

# 重新加载 Nginx 配置
docker-compose -f docker-compose.dev.yml exec jsherp-nginx-dev nginx -s reload
```

### 性能优化建议

#### 开发环境
- 启用文件监听轮询：`CHOKIDAR_USEPOLLING=true`
- 使用 cached 挂载模式减少 I/O 开销
- 适当调整容器内存限制

#### 生产环境
- 使用多阶段构建优化镜像大小
- 启用应用层缓存
- 配置适当的 JVM 参数
- 使用 SSD 存储提升数据库性能

## 📞 技术支持

如果在使用过程中遇到问题，可以通过以下方式获取帮助：

1. **查看日志**: 使用 `./docker/scripts/logs.sh` 查看详细日志
2. **检查配置**: 确保 `.env` 文件配置正确
3. **重启服务**: 尝试重启相关服务解决临时问题
4. **清理环境**: 使用 `docker system prune` 清理无用资源

## 📝 更新历史

- **v1.0.0**: 初始 Docker 容器化方案
  - 支持开发和生产环境分离
  - 完整的服务编排和配置
  - 数据持久化和健康检查
  - 详细的使用文档和故障排查指南

---

**🎉 恭喜！您已成功完成 jshERP 系统的 Docker 容器化部署。开始您的开发之旅吧！**