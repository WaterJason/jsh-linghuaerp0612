# jshERP 后端服务 Dockerfile
# 基于 OpenJDK 1.8，支持 SpringBoot 2.0.0

# 构建阶段 - 使用 Maven 编译项目
FROM --platform=linux/amd64 maven:3.6.3-openjdk-8-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY jshERP-boot/pom.xml .

# 下载依赖（利用 Docker 缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY jshERP-boot/src ./src

# 编译项目
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用精简的 JRE 镜像
FROM --platform=linux/amd64 openjdk:8-jre-slim

# 安装必要的软件包和字体（支持中文）
RUN apt-get update && apt-get install -y \
    wget \
    curl \
    tzdata \
    fontconfig \
    && rm -rf /var/lib/apt/lists/*

# 设置时区
ARG TIMEZONE=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TIMEZONE /etc/localtime && echo $TIMEZONE > /etc/timezone# 创建应用用户（安全考虑）
RUN groupadd -r jsherp && useradd -r -g jsherp jsherp

# 设置工作目录
WORKDIR /app

# 创建必要的目录
RUN mkdir -p /opt/jshERP/upload /opt/tmp/tomcat && \
    chown -R jsherp:jsherp /opt/jshERP /opt/tmp /app

# 从构建阶段复制编译好的 JAR 文件
COPY --from=builder /app/target/jshERP.jar ./app.jar

# 创建启动脚本
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'set -e' >> /app/start.sh && \
    echo 'echo "Starting jshERP Backend Service..."' >> /app/start.sh && \
    echo 'echo "Java Version:"' >> /app/start.sh && \
    echo 'java -version' >> /app/start.sh && \
    echo 'echo "JVM Options: $JVM_OPTS"' >> /app/start.sh && \
    echo 'exec java $JVM_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# 切换到应用用户
USER jsherp

# 暴露端口
EXPOSE 9999

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:9999/jshERP-boot/health || exit 1

# 启动应用
CMD ["/app/start.sh"]