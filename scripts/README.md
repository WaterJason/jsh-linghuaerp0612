# jshERP Docker 管理脚本

本目录包含用于管理jshERP Docker开发环境的自动化脚本。

## 脚本列表

### 1. docker-build.sh - 完整构建脚本
**用途**: 从零开始构建完整的jshERP开发环境

**功能**:
- 检查Docker运行状态
- 清理旧容器（可选）
- 构建Docker镜像（可选）
- 按正确顺序启动所有服务
- 验证服务运行状态
- 显示访问信息

**使用方法**:
```bash
# 完整构建（推荐首次使用）
./scripts/docker-build.sh

# 脚本会询问是否清理旧容器和重新构建镜像
# 根据需要选择 y 或 n
```

### 2. quick-start.sh - 快速启动脚本
**用途**: 快速启动已配置好的jshERP环境

**功能**:
- 启动所有已存在的容器
- 如果容器不存在，自动使用docker-compose创建
- 检查服务状态
- 显示访问信息

**使用方法**:
```bash
# 快速启动所有服务
./scripts/quick-start.sh
```

### 3. stop-services.sh - 服务停止脚本
**用途**: 安全停止jshERP相关服务

**功能**:
- 按正确顺序停止所有服务
- 强制停止选项
- 清理容器选项
- 状态检查

**使用方法**:
```bash
# 正常停止所有服务
./scripts/stop-services.sh

# 强制停止所有服务
./scripts/stop-services.sh --force

# 停止并清理容器
./scripts/stop-services.sh --cleanup

# 强制停止并清理所有容器
./scripts/stop-services.sh --all

# 查看帮助信息
./scripts/stop-services.sh --help
```

## 使用场景

### 首次部署
```bash
# 1. 克隆项目到本地
git clone <repository>
cd jshERP-0612-Cursor

# 2. 运行完整构建脚本
./scripts/docker-build.sh
```

### 日常开发
```bash
# 启动开发环境
./scripts/quick-start.sh

# 开发工作...

# 停止开发环境
./scripts/stop-services.sh
```

### 环境重置
```bash
# 停止并清理所有容器
./scripts/stop-services.sh --all

# 重新构建环境
./scripts/docker-build.sh
```

## 服务端口说明

| 服务 | 端口 | 访问地址 | 说明 |
|------|------|----------|------|
| 前端Web | 8080 | http://localhost:8080 | Vue.js应用 |
| 后端API | 9999 | http://localhost:9999 | Spring Boot API |
| Nginx代理 | 8000 | http://localhost:8000 | 反向代理 |
| phpMyAdmin | 8081 | http://localhost:8081 | 数据库管理 |
| MySQL | 3306 | localhost:3306 | 数据库服务 |
| Redis | 6379 | localhost:6379 | 缓存服务 |

## 默认账号信息

### 应用登录
- **管理员**: waterxi / 123456
- **租户用户**: jsh / 123456

### 数据库连接
- **主机**: localhost
- **端口**: 3306
- **数据库**: jsh_erp
- **用户名**: jsh_user
- **密码**: 123456

## 故障排查

### 脚本执行权限问题
```bash
# 给脚本添加执行权限
chmod +x scripts/*.sh
```

### Docker未运行
```bash
# 启动Docker Desktop
# 或在Linux上启动Docker服务
sudo systemctl start docker
```

### 端口冲突
```bash
# 检查端口占用
lsof -i :8080
lsof -i :9999

# 停止占用端口的进程
kill -9 <PID>
```

### 容器启动失败
```bash
# 查看容器日志
docker logs <container_name>

# 检查容器状态
docker ps -a

# 重新构建镜像
./scripts/docker-build.sh
```

### 网络连接问题
```bash
# 检查Docker网络
docker network ls
docker network inspect jsherp_network_dev

# 重新创建网络
docker network rm jsherp_network_dev
docker-compose -f docker-compose.dev.yml up -d
```

## 注意事项

1. **执行目录**: 所有脚本都应在项目根目录下执行
2. **Docker状态**: 确保Docker Desktop正在运行
3. **端口占用**: 确保相关端口未被其他应用占用
4. **磁盘空间**: 确保有足够的磁盘空间用于镜像和容器
5. **网络连接**: 首次构建需要下载镜像，确保网络连接正常

## 维护建议

### 定期清理
```bash
# 清理未使用的镜像
docker image prune -f

# 清理未使用的容器
docker container prune -f

# 清理未使用的网络
docker network prune -f

# 清理未使用的数据卷
docker volume prune -f
```

### 备份重要数据
```bash
# 备份数据库
docker exec jsherp-mysql-dev mysqldump -u jsh_user -p123456 jsh_erp > backup.sql

# 备份数据卷
docker run --rm -v jsherp_mysql_data:/data -v $(pwd):/backup alpine tar czf /backup/mysql_backup.tar.gz /data
```

## 相关文档

- [Docker环境问题总结](../docs/Docker环境问题总结.md)
- [Augment Code工作模式规范](../docs/Augment-Code工作模式规范.md)
- [jshERP官方文档](https://gitee.com/jishenghua/JSH_ERP)

## 技术支持

如遇到问题，请参考：
1. 查看容器日志进行初步排查
2. 参考问题总结文档中的解决方案
3. 检查Docker和系统环境配置
4. 提交Issue到项目仓库
