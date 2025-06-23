# jshERP Docker环境问题总结

## 概述
本文档记录了在jshERP Docker开发环境配置过程中遇到的问题、解决方案和最佳实践。

**日期**: 2025-06-21  
**环境**: macOS + Docker Desktop  
**项目**: jshERP v3.5 开发环境  

## 问题清单

### 1. 前端服务端口映射配置错误

#### 问题描述
- **现象**: 前端容器运行正常，但无法通过浏览器访问
- **错误**: `ERR_CONNECTION_RESET` 连接重置错误
- **根本原因**: docker-compose.dev.yml中端口映射配置不一致

#### 原始配置问题
```yaml
# 错误配置
jsherp-frontend-dev:
  command: sh -c "yarn serve --host 0.0.0.0 --port 8080"
  ports:
    - "${HOST_FRONTEND_PORT:-3000}:8080"
```

#### 解决方案
```yaml
# 正确配置
jsherp-frontend-dev:
  command: sh -c "yarn serve --host 0.0.0.0 --port 3000"
  ports:
    - "8080:3000"
```

#### 关键修改点
1. **容器内端口**: 从8080改为3000
2. **端口映射**: 从`3000:8080`改为`8080:3000`
3. **移除环境变量**: 使用固定端口映射避免配置混乱

### 2. Sass-loader依赖缺失

#### 问题描述
- **现象**: 前端编译过程中出现sass-loader相关警告
- **影响**: 可能导致样式编译失败

#### 解决方案
```bash
# 在容器内安装缺失依赖
docker exec jsherp-frontend-dev sh -c "cd /app && yarn add sass sass-loader --dev"
```

### 3. 容器误删除问题

#### 问题描述
- **现象**: 在调试过程中意外删除了运行中的容器
- **影响**: 丢失容器内的临时数据和配置

#### 预防措施
1. **备份策略**: 重要数据使用数据卷持久化
2. **操作确认**: 删除容器前进行二次确认
3. **脚本化管理**: 使用脚本统一管理容器生命周期

### 4. phpMyAdmin容器配置

#### 问题描述
- **现象**: phpMyAdmin容器被误删后需要重新创建
- **挑战**: 需要正确配置网络和环境变量

#### 解决方案
```bash
docker run -d --name jsherp-phpmyadmin \
  --network jsherp_network_dev \
  -p 8081:80 \
  -e PMA_HOST=jsherp-mysql-dev \
  -e PMA_PORT=3306 \
  -e PMA_USER=jsh_user \
  -e PMA_PASSWORD=123456 \
  phpmyadmin/phpmyadmin:latest
```

## 最佳实践

### 1. 端口配置规范
```yaml
# 推荐的端口配置模式
services:
  frontend:
    ports:
      - "主机端口:容器端口"  # 明确指定，避免使用变量
    command: "服务启动命令 --port 容器端口"
```

### 2. 服务启动顺序
```bash
# 推荐的启动顺序
1. 基础服务 (MySQL, Redis)
2. 等待数据库就绪 (健康检查)
3. 后端服务 (Spring Boot)
4. 等待后端API就绪
5. 前端服务 (Vue.js)
6. 代理服务 (Nginx)
7. 管理工具 (phpMyAdmin)
```

### 3. 健康检查配置
```yaml
# MySQL健康检查示例
healthcheck:
  test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "jsh_user", "-p123456"]
  timeout: 20s
  retries: 10
  start_period: 40s
  interval: 30s
```

### 4. 网络配置
```yaml
# 自定义网络配置
networks:
  jsherp_network_dev:
    driver: bridge
    ipam:
      config:
        - subnet: 172.18.0.0/16
```

## 故障排查指南

### 1. 容器无法启动
```bash
# 检查容器状态
docker ps -a

# 查看容器日志
docker logs <container_name> --tail 50

# 检查端口占用
lsof -i :<port>
```

### 2. 网络连接问题
```bash
# 检查网络配置
docker network ls
docker network inspect <network_name>

# 测试容器间连通性
docker exec <container> ping <target_container>

# 测试端口连通性
docker exec <container> nc -zv <host> <port>
```

### 3. 前端编译问题
```bash
# 检查Node.js版本
docker exec <frontend_container> node --version

# 重新安装依赖
docker exec <frontend_container> sh -c "cd /app && rm -rf node_modules && yarn install"

# 检查编译输出
docker logs <frontend_container> --tail 100
```

## 环境验证清单

### 服务运行状态
- [ ] jsherp-mysql-dev (端口3306)
- [ ] jsherp-redis-dev (端口6379)  
- [ ] jsherp-backend-dev (端口9999)
- [ ] jsherp-frontend-dev (端口8080)
- [ ] jsherp-nginx-dev (端口8000)
- [ ] jsherp-phpmyadmin (端口8081)

### 功能验证
- [ ] 前端页面正常加载 (http://localhost:8080)
- [ ] 后端API响应正常 (http://localhost:9999)
- [ ] 数据库连接正常 (MySQL客户端测试)
- [ ] phpMyAdmin访问正常 (http://localhost:8081)
- [ ] 用户登录功能正常

### 性能检查
- [ ] 前端编译时间 < 2分钟
- [ ] 后端启动时间 < 3分钟
- [ ] 页面加载时间 < 5秒
- [ ] API响应时间 < 1秒

## 维护建议

### 1. 定期维护
- 每周清理未使用的镜像和容器
- 每月更新基础镜像版本
- 定期备份数据库数据

### 2. 监控告警
- 设置容器健康检查
- 监控磁盘空间使用
- 关注容器资源消耗

### 3. 文档更新
- 及时更新配置变更
- 记录新发现的问题和解决方案
- 维护故障排查知识库

## 相关文件

- `docker-compose.yml` - 生产环境配置
- `docker-compose.dev.yml` - 开发环境配置  
- `scripts/docker-build.sh` - 自动化构建脚本
- `docs/Docker环境问题总结.md` - 本文档

## 联系信息

如遇到新问题或需要技术支持，请参考：
- jshERP官方文档
- Docker官方文档
- 项目Issue跟踪系统
