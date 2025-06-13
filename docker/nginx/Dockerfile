# jshERP Nginx 代理服务 Dockerfile
# 基于 Nginx 1.12.2，提供统一的入口和负载均衡

FROM --platform=linux/amd64 nginx:1.12.2-alpine

# 安装 tzdata 用于时区设置
RUN apk add --no-cache tzdata

# 设置时区
ARG TIMEZONE=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TIMEZONE /etc/localtime && echo $TIMEZONE > /etc/timezone

# 创建日志目录
RUN mkdir -p /var/log/nginx

# 复制自定义配置文件
COPY docker/nginx/nginx.conf /etc/nginx/nginx.conf
COPY docker/nginx/default.conf /etc/nginx/conf.d/default.conf

# 创建 SSL 证书目录（可选）
RUN mkdir -p /etc/nginx/ssl

# 暴露端口
EXPOSE 80 443

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]