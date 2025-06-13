# jshERP 系统完整备份方案

## 目录
- [备份方案概述](#备份方案概述)
- [备份范围详细说明](#备份范围详细说明)
- [备份脚本使用指南](#备份脚本使用指南)
- [恢复流程详细说明](#恢复流程详细说明)
- [备份文件清单与大小估算](#备份文件清单与大小估算)
- [备份验证方法](#备份验证方法)
- [存储建议](#存储建议)
- [自动化备份方案](#自动化备份方案)
- [故障恢复场景](#故障恢复场景)

## 备份方案概述

### 设计目标
- **完整性**：备份所有必要的系统组件和数据
- **一致性**：确保备份数据的时间点一致性
- **可恢复性**：备份文件可以在不同环境中完整恢复
- **验证性**：提供备份完整性验证机制
- **自动化**：支持定时自动备份

### 备份策略
- **全量备份**：包含所有系统组件的完整备份
- **增量备份**：仅备份变更的数据（可选）
- **定期备份**：建议每日进行全量备份
- **异地备份**：备份文件存储在多个位置

## 备份范围详细说明

### 1. 源代码备份
```
📁 source_code/
├── jshERP_source_YYYYMMDD_HHMMSS.tar.gz     # 完整项目源码
├── config/                                   # 配置文件目录
│   ├── application.properties
│   ├── application-docker.properties
│   └── ...
├── modified_files/                          # 关键修改文件
│   ├── application-docker.properties       # MyBatis 配置修改
│   ├── UserController.java                 # 验证码生成修改
│   └── MODIFICATIONS.md                    # 修改说明文档
└── docker-compose.dev.yml                  # Docker 编排文件
```

**包含内容**：
- 完整的 jshERP 项目源代码
- 所有配置文件（包括修改过的配置）
- Docker 相关配置文件
- 自定义修改的代码文件
- 修改说明文档

**排除内容**：
- `node_modules` 目录
- `target` 编译目录
- `.git` 版本控制目录
- 临时文件和日志文件

### 2. 数据库备份
```
📁 database/
├── jsh_erp_full_YYYYMMDD_HHMMSS.sql        # 完整数据库备份
├── jsh_erp_schema_YYYYMMDD_HHMMSS.sql      # 仅表结构备份
├── jsh_erp_core_data_YYYYMMDD_HHMMSS.sql   # 核心数据备份
└── database_info.txt                        # 数据库信息统计
```

**备份内容**：
- 完整的 `jsh_erp` 数据库
- 所有表结构和数据
- 存储过程、触发器、事件
- 新增的字段（zero_change_amount_flag, customer_static_price_flag）

**备份特点**：
- 使用 `--single-transaction` 确保一致性
- 包含 `--routines --triggers --events` 完整对象
- 提供多种粒度的备份文件

### 3. Docker 镜像备份
```
📁 docker/
├── jsherp-backend-dev_YYYYMMDD_HHMMSS.tar.gz    # 后端镜像
├── jsherp-frontend-dev_YYYYMMDD_HHMMSS.tar.gz   # 前端镜像
├── mysql_5.7_YYYYMMDD_HHMMSS.tar.gz             # MySQL 镜像
├── redis_6.2_YYYYMMDD_HHMMSS.tar.gz             # Redis 镜像
├── nginx_latest_YYYYMMDD_HHMMSS.tar.gz          # Nginx 镜像
├── docker-compose.dev.yml                        # 编排文件
├── Dockerfile                                     # 构建文件
└── images_info.txt                               # 镜像信息
```

**备份内容**：
- 所有相关的 Docker 镜像
- Docker Compose 配置文件
- Dockerfile 构建文件
- 镜像版本和大小信息

### 4. Redis 数据备份
```
📁 redis/
├── dump_YYYYMMDD_HHMMSS.rdb                # Redis 数据文件
├── redis_config.txt                        # Redis 配置信息
├── redis_keys.txt                          # 所有键列表
└── redis_memory_info.txt                   # 内存使用信息
```

**备份内容**：
- Redis RDB 数据文件
- Redis 配置信息
- 缓存键统计信息
- 内存使用统计

### 5. 文档和脚本备份
```
📁 documents/
├── 总结文档.md                             # 系统部署文档
├── README.md                               # 项目说明文档
└── ...                                     # 其他文档

📁 scripts/
├── full_backup.sh                          # 备份脚本
├── full_restore.sh                         # 恢复脚本
├── deploy.sh                               # 部署脚本
└── ...                                     # 其他脚本
```

## 备份脚本使用指南

### 执行备份

#### 1. 准备工作
```bash
# 确保脚本有执行权限
chmod +x full_backup.sh

# 检查磁盘空间（建议至少 10GB 可用空间）
df -h

# 确保 jshERP 系统正在运行
cd /Users/macmini/Desktop/jshERP-0612-Cursor
docker-compose -f docker-compose.dev.yml ps
```

#### 2. 执行备份
```bash
# 执行完整备份
./full_backup.sh

# 查看备份进度（另开终端）
tail -f /backup/jshERP/YYYYMMDD_HHMMSS/backup.log
```

#### 3. 备份完成后检查
```bash
# 查看备份文件
ls -lh /backup/jshERP/

# 检查备份完整性
cd /backup/jshERP/YYYYMMDD_HHMMSS
md5sum -c backup_checksums.md5

# 查看备份信息
cat backup_info.txt
```

### 备份脚本参数说明

```bash
# 脚本内置配置变量
PROJECT_DIR="/Users/macmini/Desktop/jshERP-0612-Cursor"  # 项目目录
BACKUP_BASE_DIR="/backup/jshERP"                         # 备份基础目录
```

**可自定义配置**：
- 修改 `PROJECT_DIR` 变量指定不同的项目目录
- 修改 `BACKUP_BASE_DIR` 变量指定不同的备份目录
- 调整备份保留天数（默认 7 天）

### 备份过程监控

#### 实时监控
```bash
# 监控备份进度
watch -n 5 'du -sh /backup/jshERP/$(ls -t /backup/jshERP/ | head -1)'

# 监控系统资源
htop

# 监控 Docker 容器状态
watch 'docker stats --no-stream'
```

#### 日志分析
```bash
# 查看备份日志
tail -f /backup/jshERP/YYYYMMDD_HHMMSS/backup.log

# 搜索错误信息
grep -i error /backup/jshERP/YYYYMMDD_HHMMSS/backup.log

# 搜索警告信息
grep -i warning /backup/jshERP/YYYYMMDD_HHMMSS/backup.log
```

## 恢复流程详细说明

### 恢复前准备

#### 1. 环境检查
```bash
# 检查 Docker 环境
docker --version
docker-compose --version

# 检查磁盘空间
df -h

# 停止现有服务（如果有）
docker-compose -f docker-compose.dev.yml down
```

#### 2. 备份文件验证
```bash
# 检查备份文件完整性
tar -tzf jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz > /dev/null

# 查看备份文件大小
ls -lh jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz
```

### 执行恢复

#### 1. 基本恢复
```bash
# 使用默认恢复目录
./full_restore.sh /backup/jshERP/jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz

# 指定自定义恢复目录
./full_restore.sh /backup/jshERP/jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz /opt/jshERP
```

#### 2. 分步恢复（高级用户）
```bash
# 1. 解压备份文件
tar -xzf jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz

# 2. 恢复源代码
cd YYYYMMDD_HHMMSS/source_code
tar -xzf jshERP_source_*.tar.gz -C /path/to/restore

# 3. 恢复数据库
docker exec -i jsherp-mysql-dev mysql -u root -p123456 jsh_erp < database/jsh_erp_full_*.sql

# 4. 恢复 Redis
docker cp redis/dump_*.rdb jsherp-redis-dev:/data/dump.rdb

# 5. 恢复 Docker 镜像
for image in docker/*.tar.gz; do
    gunzip -c "$image" | docker load
done
```

### 恢复验证

#### 1. 服务状态检查
```bash
# 检查所有容器状态
docker-compose -f docker-compose.dev.yml ps

# 检查容器日志
docker logs jsherp-backend-dev
docker logs jsherp-frontend-dev
```

#### 2. 功能验证
```bash
# 检查前端页面
curl -s -o /dev/null -w "%{http_code}" http://localhost:3000

# 检查后端 API
curl -s -o /dev/null -w "%{http_code}" http://localhost:9999/jshERP-boot/user/randomImage

# 检查数据库连接
docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT COUNT(*) FROM jsh_erp.jsh_user"

# 检查 Redis 连接
docker exec jsherp-redis-dev redis-cli -a 1234abcd ping
```

#### 3. 业务功能验证
- 登录系统（admin / 123456）
- 检查菜单权限
- 验证核心业务功能
- 检查数据完整性

## 备份文件清单与大小估算

### 备份文件结构
```
jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz          # 最终压缩包
└── YYYYMMDD_HHMMSS/                                    # 备份目录
    ├── backup.log                                      # 备份日志
    ├── backup_info.txt                                 # 备份信息
    ├── backup_checksums.md5                           # 校验文件
    ├── file_manifest.txt                              # 文件清单
    ├── compressed_info.txt                            # 压缩信息
    ├── source_code/                                    # 源代码备份
    │   ├── jshERP_source_YYYYMMDD_HHMMSS.tar.gz       # ~50-100MB
    │   ├── config/                                     # ~1MB
    │   ├── modified_files/                             # ~1MB
    │   └── docker-compose.dev.yml                     # ~5KB
    ├── database/                                       # 数据库备份
    │   ├── jsh_erp_full_YYYYMMDD_HHMMSS.sql           # ~10-50MB
    │   ├── jsh_erp_schema_YYYYMMDD_HHMMSS.sql         # ~1-5MB
    │   ├── jsh_erp_core_data_YYYYMMDD_HHMMSS.sql      # ~1-10MB
    │   └── database_info.txt                          # ~1KB
    ├── redis/                                          # Redis 备份
    │   ├── dump_YYYYMMDD_HHMMSS.rdb                   # ~1-10MB
    │   ├── redis_config.txt                           # ~5KB
    │   ├── redis_keys.txt                             # ~10KB
    │   └── redis_memory_info.txt                      # ~5KB
    ├── docker/                                         # Docker 镜像
    │   ├── jsherp-backend-dev_YYYYMMDD_HHMMSS.tar.gz  # ~500-800MB
    │   ├── jsherp-frontend-dev_YYYYMMDD_HHMMSS.tar.gz # ~300-500MB
    │   ├── mysql_5.7_YYYYMMDD_HHMMSS.tar.gz           # ~200-300MB
    │   ├── redis_6.2_YYYYMMDD_HHMMSS.tar.gz           # ~50-100MB
    │   ├── nginx_latest_YYYYMMDD_HHMMSS.tar.gz        # ~50-100MB
    │   ├── docker-compose.dev.yml                     # ~5KB
    │   └── images_info.txt                            # ~1KB
    ├── documents/                                      # 文档备份
    │   ├── 总结文档.md                                 # ~100KB
    │   └── ...                                         # ~1MB
    └── scripts/                                        # 脚本备份
        ├── full_backup.sh                              # ~20KB
        ├── full_restore.sh                             # ~15KB
        └── ...                                         # ~50KB
```

### 大小估算

#### 详细大小分析
| 组件 | 未压缩大小 | 压缩后大小 | 说明 |
|------|------------|------------|------|
| 源代码 | 200-300MB | 50-100MB | 包含完整项目源码 |
| 数据库 | 20-100MB | 5-25MB | 取决于业务数据量 |
| Redis | 5-20MB | 1-5MB | 缓存数据 |
| Docker 镜像 | 1.5-2GB | 1-1.5GB | 所有相关镜像 |
| 文档脚本 | 5-10MB | 1-2MB | 文档和脚本文件 |
| **总计** | **1.7-2.4GB** | **1.1-1.6GB** | **最终压缩包大小** |

#### 影响因素
- **业务数据量**：数据库大小主要取决于实际业务数据
- **Docker 镜像**：占用最大空间，约占总备份的 80-90%
- **压缩比例**：通常可达到 30-40% 的压缩率

### 存储空间规划
```bash
# 建议存储空间配置
本地存储：    至少 10GB（保留 7 天备份）
云端存储：    至少 20GB（保留 14 天备份）
归档存储：    按需配置（长期保存重要备份）
```

## 备份验证方法

### 1. 文件完整性验证

#### MD5 校验
```bash
# 验证备份文件完整性
cd /backup/jshERP/YYYYMMDD_HHMMSS
md5sum -c backup_checksums.md5

# 验证压缩包完整性
md5sum jshERP_complete_backup_YYYYMMDD_HHMMSS.tar.gz
```

#### 文件大小验证
```bash
# 检查关键文件大小
ls -lh source_code/jshERP_source_*.tar.gz
ls -lh database/jsh_erp_full_*.sql
ls -lh docker/*.tar.gz

# 验证文件不为空
find . -name "*.sql" -size 0
find . -name "*.tar.gz" -size 0
```

### 2. 数据库备份验证

#### 语法验证
```bash
# 检查 SQL 文件语法
mysql --help --verbose | head -1
mysql -u root -p123456 --execute="source database/jsh_erp_full_*.sql" --force --verbose

# 检查表结构
mysql -u root -p123456 -e "SHOW TABLES FROM jsh_erp" | wc -l
```

#### 数据一致性验证
```bash
# 比较表记录数
mysql -u root -p123456 -e "
SELECT table_name, table_rows
FROM information_schema.tables
WHERE table_schema = 'jsh_erp'
ORDER BY table_rows DESC"
```

### 3. 恢复测试验证

#### 定期恢复测试
```bash
# 创建测试恢复脚本
cat > test_restore.sh << 'EOF'
#!/bin/bash
TEST_DIR="/tmp/jshERP_restore_test_$(date +%s)"
./full_restore.sh $1 $TEST_DIR
if [ $? -eq 0 ]; then
    echo "恢复测试成功"
    rm -rf $TEST_DIR
else
    echo "恢复测试失败"
    exit 1
fi
EOF

chmod +x test_restore.sh
```

#### 自动化验证
```bash
# 验证脚本示例
#!/bin/bash
BACKUP_FILE="$1"

# 1. 文件完整性验证
echo "验证文件完整性..."
if ! tar -tzf "$BACKUP_FILE" >/dev/null 2>&1; then
    echo "备份文件损坏"
    exit 1
fi

# 2. 解压验证
echo "验证解压..."
TEMP_DIR=$(mktemp -d)
if ! tar -xzf "$BACKUP_FILE" -C "$TEMP_DIR" >/dev/null 2>&1; then
    echo "解压失败"
    rm -rf "$TEMP_DIR"
    exit 1
fi

# 3. 关键文件验证
echo "验证关键文件..."
BACKUP_DIR=$(find "$TEMP_DIR" -maxdepth 1 -type d -name "20*" | head -1)
if [ ! -f "$BACKUP_DIR/database/jsh_erp_full_"*.sql ]; then
    echo "数据库备份文件缺失"
    rm -rf "$TEMP_DIR"
    exit 1
fi

echo "备份验证通过"
rm -rf "$TEMP_DIR"
```

## 存储建议

### 1. 本地存储方案

#### 存储位置
```bash
# 推荐存储路径
/backup/jshERP/                    # 主备份目录
/backup/jshERP/daily/              # 日常备份
/backup/jshERP/weekly/             # 周备份
/backup/jshERP/monthly/            # 月备份
/backup/jshERP/archive/            # 归档备份
```

#### 存储策略
- **日备份**：保留 7 天
- **周备份**：保留 4 周
- **月备份**：保留 12 个月
- **年备份**：长期保存

#### 磁盘配置
```bash
# 建议使用独立磁盘分区
sudo mkdir -p /backup
sudo mount /dev/sdb1 /backup

# 设置自动挂载
echo "/dev/sdb1 /backup ext4 defaults 0 2" >> /etc/fstab
```

### 2. 云端存储方案

#### 阿里云 OSS
```bash
# 安装 ossutil
wget http://gosspublic.alicdn.com/ossutil/1.7.0/ossutil64
chmod +x ossutil64

# 配置
./ossutil64 config

# 上传备份
./ossutil64 cp jshERP_complete_backup_*.tar.gz oss://your-bucket/jshERP/backups/
```

#### 腾讯云 COS
```bash
# 安装 coscmd
pip install coscmd

# 配置
coscmd config -a your_secret_id -s your_secret_key -b your_bucket -r your_region

# 上传备份
coscmd upload jshERP_complete_backup_*.tar.gz /jshERP/backups/
```

#### AWS S3
```bash
# 安装 AWS CLI
pip install awscli

# 配置
aws configure

# 上传备份
aws s3 cp jshERP_complete_backup_*.tar.gz s3://your-bucket/jshERP/backups/
```

### 3. 混合存储策略

#### 3-2-1 备份策略
- **3 份副本**：原始数据 + 2 份备份
- **2 种介质**：本地存储 + 云端存储
- **1 份异地**：至少 1 份备份存储在异地

#### 实施方案
```bash
# 本地备份
./full_backup.sh

# 云端同步
rsync -av /backup/jshERP/ user@remote-server:/backup/jshERP/

# 云存储上传
./upload_to_cloud.sh /backup/jshERP/jshERP_complete_backup_*.tar.gz
```

## 自动化备份方案

### 1. 定时备份配置

#### Cron 定时任务
```bash
# 编辑 crontab
crontab -e

# 添加定时备份任务
# 每天凌晨 2 点执行备份
0 2 * * * /path/to/full_backup.sh >> /var/log/jshERP_backup.log 2>&1

# 每周日凌晨 1 点执行周备份
0 1 * * 0 /path/to/weekly_backup.sh >> /var/log/jshERP_weekly_backup.log 2>&1

# 每月 1 号凌晨 0 点执行月备份
0 0 1 * * /path/to/monthly_backup.sh >> /var/log/jshERP_monthly_backup.log 2>&1
```

#### Systemd 定时器
```bash
# 创建备份服务文件
sudo tee /etc/systemd/system/jshERP-backup.service << 'EOF'
[Unit]
Description=jshERP System Backup
After=network.target

[Service]
Type=oneshot
User=root
ExecStart=/path/to/full_backup.sh
StandardOutput=journal
StandardError=journal
EOF

# 创建定时器文件
sudo tee /etc/systemd/system/jshERP-backup.timer << 'EOF'
[Unit]
Description=Run jshERP backup daily
Requires=jshERP-backup.service

[Timer]
OnCalendar=daily
Persistent=true

[Install]
WantedBy=timers.target
EOF

# 启用定时器
sudo systemctl enable jshERP-backup.timer
sudo systemctl start jshERP-backup.timer
```

### 2. 备份监控脚本

#### 备份状态检查
```bash
#!/bin/bash
# backup_monitor.sh

BACKUP_DIR="/backup/jshERP"
LOG_FILE="/var/log/backup_monitor.log"
ALERT_EMAIL="admin@company.com"

check_backup_status() {
    local today=$(date +%Y%m%d)
    local backup_file=$(find "$BACKUP_DIR" -name "*${today}*.tar.gz" | head -1)

    if [ -z "$backup_file" ]; then
        echo "$(date): 今日备份文件不存在" >> "$LOG_FILE"
        send_alert "备份失败" "今日备份文件不存在"
        return 1
    fi

    # 检查备份文件大小
    local file_size=$(stat -f%z "$backup_file" 2>/dev/null || stat -c%s "$backup_file")
    local min_size=$((500 * 1024 * 1024))  # 500MB

    if [ "$file_size" -lt "$min_size" ]; then
        echo "$(date): 备份文件过小: $file_size bytes" >> "$LOG_FILE"
        send_alert "备份异常" "备份文件大小异常: $(($file_size / 1024 / 1024))MB"
        return 1
    fi

    echo "$(date): 备份检查通过: $(basename "$backup_file")" >> "$LOG_FILE"
    return 0
}

send_alert() {
    local subject="$1"
    local message="$2"

    # 发送邮件告警
    echo "$message" | mail -s "jshERP 备份告警: $subject" "$ALERT_EMAIL"

    # 发送企业微信告警（可选）
    # curl -X POST "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=YOUR_KEY" \
    #      -H "Content-Type: application/json" \
    #      -d "{\"msgtype\":\"text\",\"text\":{\"content\":\"$subject: $message\"}}"
}

# 执行检查
check_backup_status
```

#### 磁盘空间监控
```bash
#!/bin/bash
# disk_monitor.sh

BACKUP_DIR="/backup"
THRESHOLD=80  # 磁盘使用率阈值

check_disk_space() {
    local usage=$(df "$BACKUP_DIR" | awk 'NR==2 {print $5}' | sed 's/%//')

    if [ "$usage" -gt "$THRESHOLD" ]; then
        echo "$(date): 备份磁盘空间不足: ${usage}%" >> /var/log/disk_monitor.log

        # 清理旧备份
        find "$BACKUP_DIR" -name "*.tar.gz" -mtime +30 -delete

        # 重新检查
        local new_usage=$(df "$BACKUP_DIR" | awk 'NR==2 {print $5}' | sed 's/%//')
        if [ "$new_usage" -gt "$THRESHOLD" ]; then
            send_alert "磁盘空间不足" "备份磁盘使用率: ${new_usage}%"
        fi
    fi
}

check_disk_space
```

### 3. 云端同步脚本

#### 自动上传到云存储
```bash
#!/bin/bash
# cloud_sync.sh

BACKUP_DIR="/backup/jshERP"
CLOUD_BUCKET="your-backup-bucket"
RETENTION_DAYS=30

sync_to_cloud() {
    local backup_files=$(find "$BACKUP_DIR" -name "*.tar.gz" -mtime -1)

    for file in $backup_files; do
        local filename=$(basename "$file")
        local cloud_path="jshERP/$(date +%Y/%m)/$filename"

        echo "上传到云存储: $filename"

        # AWS S3
        aws s3 cp "$file" "s3://$CLOUD_BUCKET/$cloud_path"

        # 或者阿里云 OSS
        # ossutil64 cp "$file" "oss://$CLOUD_BUCKET/$cloud_path"

        if [ $? -eq 0 ]; then
            echo "$(date): 云端同步成功: $filename" >> /var/log/cloud_sync.log
        else
            echo "$(date): 云端同步失败: $filename" >> /var/log/cloud_sync.log
        fi
    done

    # 清理云端旧文件
    cleanup_cloud_backups
}

cleanup_cloud_backups() {
    # 删除超过保留期的云端备份
    local cutoff_date=$(date -d "$RETENTION_DAYS days ago" +%Y-%m-%d)

    # AWS S3 示例
    aws s3 ls "s3://$CLOUD_BUCKET/jshERP/" --recursive | \
    awk '$1 < "'$cutoff_date'" {print $4}' | \
    while read file; do
        aws s3 rm "s3://$CLOUD_BUCKET/$file"
    done
}

sync_to_cloud
```

## 故障恢复场景

### 1. 完全系统故障

#### 场景描述
- 服务器硬件故障
- 操作系统损坏
- 数据完全丢失

#### 恢复步骤
```bash
# 1. 准备新环境
# 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# 2. 下载备份文件
# 从云存储下载最新备份
aws s3 cp s3://your-bucket/jshERP/backups/latest.tar.gz ./

# 3. 执行完整恢复
./full_restore.sh latest.tar.gz /opt/jshERP

# 4. 验证系统
curl http://localhost:3000
```

### 2. 数据库故障

#### 场景描述
- 数据库服务异常
- 数据损坏
- 误删除数据

#### 恢复步骤
```bash
# 1. 停止应用服务
docker-compose -f docker-compose.dev.yml stop jsherp-backend-dev

# 2. 备份当前数据库（如果可能）
docker exec jsherp-mysql-dev mysqldump -u root -p123456 jsh_erp > current_backup.sql

# 3. 恢复数据库
docker exec -i jsherp-mysql-dev mysql -u root -p123456 jsh_erp < backup/database/jsh_erp_full_*.sql

# 4. 重启服务
docker-compose -f docker-compose.dev.yml start jsherp-backend-dev

# 5. 验证数据
docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT COUNT(*) FROM jsh_erp.jsh_user"
```

### 3. 应用代码故障

#### 场景描述
- 代码部署错误
- 配置文件损坏
- 版本回滚需求

#### 恢复步骤
```bash
# 1. 停止服务
docker-compose -f docker-compose.dev.yml down

# 2. 恢复源代码
tar -xzf backup/source_code/jshERP_source_*.tar.gz -C /path/to/restore

# 3. 恢复配置文件
cp -r backup/source_code/config/* /path/to/restore/jshERP-boot/src/main/resources/

# 4. 重新构建和启动
docker-compose -f docker-compose.dev.yml build
docker-compose -f docker-compose.dev.yml up -d

# 5. 验证应用
curl http://localhost:9999/jshERP-boot/user/randomImage
```

### 4. 部分数据丢失

#### 场景描述
- 特定表数据丢失
- 配置数据损坏
- 用户数据异常

#### 恢复步骤
```bash
# 1. 分析丢失范围
docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SHOW TABLES FROM jsh_erp"

# 2. 从备份中提取特定数据
mysql -u root -p123456 jsh_erp < backup/database/jsh_erp_core_data_*.sql

# 3. 或者恢复特定表
mysqldump -u root -p123456 --where="1 limit 1000000" backup_db table_name | \
mysql -u root -p123456 jsh_erp

# 4. 验证数据完整性
docker exec jsherp-mysql-dev mysql -u root -p123456 -e "SELECT COUNT(*) FROM jsh_erp.jsh_user"
```

## 备份最佳实践

### 1. 备份策略建议
- **定期测试恢复**：每月至少进行一次完整恢复测试
- **多重备份**：本地 + 云端 + 异地存储
- **版本管理**：保留多个时间点的备份
- **监控告警**：及时发现备份异常

### 2. 安全考虑
- **加密备份**：敏感数据备份加密存储
- **访问控制**：限制备份文件访问权限
- **传输安全**：使用 HTTPS/SSL 传输备份文件
- **审计日志**：记录备份和恢复操作

### 3. 性能优化
- **增量备份**：对于大型数据库考虑增量备份
- **压缩优化**：选择合适的压缩算法
- **并行处理**：利用多核 CPU 并行备份
- **网络优化**：云端传输使用断点续传

---

**文档版本**: v1.0
**最后更新**: 2025年6月13日
**适用版本**: jshERP 最新版本
