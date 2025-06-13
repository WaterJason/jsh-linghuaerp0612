# jshERP 前端服务 Dockerfile
# 基于 Node 20.17.0，支持 Vue 2.7.16

# 开发环境镜像
FROM node:20.17.0-alpine AS development

# 安装必要的工具
RUN apk add --no-cache git

# 设置工作目录
WORKDIR /app

# 设置 npm 源为淘宝镜像（加速国内构建）
RUN npm config set registry https://registry.npmmirror.com/

# 复制 package.json 和 yarn.lock
COPY jshERP-web/package.json jshERP-web/yarn.lock ./

# 安装依赖
RUN yarn install --frozen-lockfile

# 复制源代码
COPY jshERP-web/ .

# 创建 Docker 环境专用的 vue.config.js
RUN echo 'const path = require("path")\n\
const CompressionPlugin = require("compression-webpack-plugin")\n\
\n\
function resolve (dir) {\n\
    return path.join(__dirname, dir)\n\
}\n\
\n\
module.exports = {\n\
    productionSourceMap: false,\n\
    configureWebpack: config => {\n\
        if (process.env.NODE_ENV === "production") {\n\
            config.optimization.minimizer[0].options.terserOptions.compress.drop_console = true\n\
        }\n\
    },\n\
    chainWebpack: (config) => {\n\
        config.resolve.alias\n\
            .set("@$", resolve("src"))\n\
            .set("@api", resolve("src/api"))\n\
            .set("@assets", resolve("src/assets"))\n\
            .set("@comp", resolve("src/components"))\n\
            .set("@views", resolve("src/views"))\n\
        if (process.env.NODE_ENV === "production") {\n\
            config.plugin("compressionPlugin").use(new CompressionPlugin({\n\
                test: /\\.(js|css|less)$/,\n\
                threshold: 10240,\n\
                deleteOriginalAssets: false\n\
            }))\n\
        }\n\
    },\n\
    css: {\n\
        loaderOptions: {\n\
            less: {\n\
                modifyVars: {\n\
                    "primary-color": "#1890FF",\n\
                    "link-color": "#1890FF",\n\
                    "border-radius-base": "4px"\n\
                },\n\
                javascriptEnabled: true\n\
            }\n\
        }\n\
    },\n\
    devServer: {\n\
        port: 8080,\n\
        host: "0.0.0.0",\n\
        proxy: {\n\
            "/jshERP-boot": {\n\
                target: "http://jsherp-backend-dev:9999",\n\
                ws: false,\n\
                changeOrigin: true\n\
            }\n\
        }\n\
    },\n\
    lintOnSave: false\n\
}' > vue.config.js

# 暴露开发服务器端口
EXPOSE 8080

# 开发模式启动命令
CMD ["yarn", "serve"]

# 构建阶段 - 生产环境构建
FROM node:20.17.0-alpine AS builder

# 设置工作目录
WORKDIR /app# 设置 npm 源为淘宝镜像
RUN npm config set registry https://registry.npmmirror.com/

# 复制 package.json 和 yarn.lock
COPY jshERP-web/package.json jshERP-web/yarn.lock ./

# 安装依赖
RUN yarn install --frozen-lockfile

# 复制源代码
COPY jshERP-web/ .

# 构建生产版本
RUN yarn build

# 生产环境镜像 - 使用 Nginx 提供静态文件服务
FROM nginx:1.12.2-alpine AS production

# 安装 tzdata 用于时区设置
RUN apk add --no-cache tzdata

# 设置时区
ARG TIMEZONE=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TIMEZONE /etc/localtime && echo $TIMEZONE > /etc/timezone

# 从构建阶段复制构建文件
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置文件
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动 Nginx
CMD ["nginx", "-g", "daemon off;"]