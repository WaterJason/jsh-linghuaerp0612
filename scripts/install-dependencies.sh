#!/bin/bash

# jshERP 本地开发环境依赖安装脚本
# 支持 macOS 和 Linux 系统

set -e

echo "=========================================="
echo "  jshERP 本地开发环境依赖安装"
echo "=========================================="

# 检测操作系统
OS="$(uname -s)"
case "${OS}" in
    Linux*)     MACHINE=Linux;;
    Darwin*)    MACHINE=Mac;;
    *)          MACHINE="UNKNOWN:${OS}"
esac

echo "检测到操作系统: ${MACHINE}"

# 安装 Java 8
install_java() {
    echo "正在检查 Java 环境..."
    
    if command -v java &> /dev/null && java -version 2>&1 | grep -q "1.8"; then
        echo "✅ Java 8 已安装"
        java -version
    else
        echo "❌ 需要安装 Java 8"
        
        if [[ "$MACHINE" == "Mac" ]]; then
            echo "在 macOS 上安装 Java 8..."
            if command -v brew &> /dev/null; then
                brew install --cask adoptopenjdk8
            else
                echo "请先安装 Homebrew，然后运行: brew install --cask adoptopenjdk8"
                exit 1
            fi
        elif [[ "$MACHINE" == "Linux" ]]; then
            echo "在 Linux 上安装 Java 8..."
            if command -v apt-get &> /dev/null; then
                sudo apt-get update
                sudo apt-get install -y openjdk-8-jdk
            elif command -v yum &> /dev/null; then
                sudo yum install -y java-1.8.0-openjdk-devel
            else
                echo "请手动安装 Java 8"
                exit 1
            fi
        fi
    fi
}

# 安装 Maven
install_maven() {
    echo "正在检查 Maven 环境..."
    
    if command -v mvn &> /dev/null; then
        echo "✅ Maven 已安装"
        mvn -version
    else
        echo "❌ 需要安装 Maven"
        
        if [[ "$MACHINE" == "Mac" ]]; then
            echo "在 macOS 上安装 Maven..."
            if command -v brew &> /dev/null; then
                brew install maven
            else
                echo "请先安装 Homebrew，然后运行: brew install maven"
                exit 1
            fi
        elif [[ "$MACHINE" == "Linux" ]]; then
            echo "在 Linux 上安装 Maven..."
            if command -v apt-get &> /dev/null; then
                sudo apt-get update
                sudo apt-get install -y maven
            elif command -v yum &> /dev/null; then
                sudo yum install -y maven
            else
                echo "请手动安装 Maven"
                exit 1
            fi
        fi
    fi
}

# 安装 Node.js 和 Yarn
install_nodejs() {
    echo "正在检查 Node.js 环境..."
    
    if command -v node &> /dev/null; then
        NODE_VERSION=$(node -v)
        echo "✅ Node.js 已安装: ${NODE_VERSION}"
        
        # 检查版本是否为 16+ 
        NODE_MAJOR_VERSION=$(node -v | cut -d'.' -f1 | cut -d'v' -f2)
        if [ "$NODE_MAJOR_VERSION" -lt 16 ]; then
            echo "⚠️  建议使用 Node.js 16+ 版本"
        fi
    else
        echo "❌ 需要安装 Node.js"
        
        if [[ "$MACHINE" == "Mac" ]]; then
            echo "在 macOS 上安装 Node.js..."
            if command -v brew &> /dev/null; then
                brew install node@18
                brew link node@18
            else
                echo "请先安装 Homebrew，然后运行: brew install node@18"
                exit 1
            fi
        elif [[ "$MACHINE" == "Linux" ]]; then
            echo "在 Linux 上安装 Node.js..."
            curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
            sudo apt-get install -y nodejs
        fi
    fi
    
    # 安装 Yarn
    echo "正在检查 Yarn 环境..."
    if command -v yarn &> /dev/null; then
        echo "✅ Yarn 已安装"
        yarn --version
    else
        echo "❌ 需要安装 Yarn"
        npm install -g yarn
    fi
}

# 安装 Docker 和 Docker Compose
install_docker() {
    echo "正在检查 Docker 环境..."
    
    if command -v docker &> /dev/null && command -v docker-compose &> /dev/null; then
        echo "✅ Docker 和 Docker Compose 已安装"
        docker --version
        docker-compose --version
    else
        echo "❌ 需要安装 Docker 和 Docker Compose"
        
        if [[ "$MACHINE" == "Mac" ]]; then
            echo "请下载并安装 Docker Desktop for Mac: https://www.docker.com/products/docker-desktop"
        elif [[ "$MACHINE" == "Linux" ]]; then
            echo "在 Linux 上安装 Docker..."
            curl -fsSL https://get.docker.com -o get-docker.sh
            sudo sh get-docker.sh
            sudo usermod -aG docker $USER
            
            # 安装 Docker Compose
            sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
            sudo chmod +x /usr/local/bin/docker-compose
            
            echo "请重新登录以使 Docker 权限生效"
        fi
    fi
}

# 配置 Maven 镜像源
configure_maven() {
    echo "正在配置 Maven 镜像源..."
    
    MAVEN_HOME_DIR="$HOME/.m2"
    SETTINGS_FILE="$MAVEN_HOME_DIR/settings.xml"
    
    mkdir -p "$MAVEN_HOME_DIR"
    
    if [ ! -f "$SETTINGS_FILE" ]; then
        cat > "$SETTINGS_FILE" << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <mirrors>
        <mirror>
            <id>aliyunmaven</id>
            <mirrorOf>*</mirrorOf>
            <name>阿里云公共仓库</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </mirror>
    </mirrors>
    
    <profiles>
        <profile>
            <id>jdk-1.8</id>
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>1.8</jdk>
            </activation>
            <properties>
                <maven.compiler.source>1.8</maven.compiler.source>
                <maven.compiler.target>1.8</maven.compiler.target>
                <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
    
</settings>
EOF
        echo "✅ Maven 配置文件已创建: $SETTINGS_FILE"
    else
        echo "✅ Maven 配置文件已存在: $SETTINGS_FILE"
    fi
}

# 配置 Yarn 镜像源
configure_yarn() {
    echo "正在配置 Yarn 镜像源..."
    yarn config set registry https://registry.npmmirror.com/
    echo "✅ Yarn 镜像源已配置为 npmmirror"
}

# 主执行流程
main() {
    echo "开始安装依赖..."
    
    install_java
    install_maven
    install_nodejs
    install_docker
    
    echo ""
    echo "开始配置镜像源..."
    configure_maven
    configure_yarn
    
    echo ""
    echo "=========================================="
    echo "  ✅ 依赖安装完成！"
    echo "=========================================="
    echo ""
    echo "环境信息:"
    echo "- Java: $(java -version 2>&1 | head -n 1)"
    echo "- Maven: $(mvn -version 2>&1 | head -n 1)"
    echo "- Node.js: $(node -v)"
    echo "- Yarn: $(yarn -v)"
    echo "- Docker: $(docker --version)"
    echo "- Docker Compose: $(docker-compose --version)"
    echo ""
    echo "接下来可以运行:"
    echo "  ./scripts/start-local.sh    # 启动本地开发环境"
    echo ""
}

# 执行主函数
main "$@" 