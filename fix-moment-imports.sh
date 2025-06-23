#!/bin/bash

# 批量替换moment导入为dayjs导入的脚本
# 用于修复jshERP前端项目中的moment依赖问题

echo "开始批量替换moment导入为dayjs导入..."

# 进入前端项目目录
cd /Users/macmini/Desktop/jshERP-0612-Cursor/jshERP-web

# 查找所有包含moment导入的文件
files=$(grep -r "import moment from 'moment'" src/ --include="*.vue" --include="*.js" -l)

echo "找到以下文件需要修改："
echo "$files"

# 对每个文件进行替换
for file in $files; do
    echo "正在处理: $file"
    
    # 替换导入语句
    sed -i '' "s/import moment from 'moment'/import dayjs from 'dayjs'/g" "$file"
    
    # 替换moment()调用为dayjs()调用
    sed -i '' "s/moment(/dayjs(/g" "$file"
    
    # 替换moment变量引用
    sed -i '' "s/moment,/dayjs,/g" "$file"
    sed -i '' "s/moment$/dayjs/g" "$file"
    
    echo "完成: $file"
done

echo "批量替换完成！"

# 检查是否还有遗漏的moment引用
echo "检查是否还有遗漏的moment引用..."
remaining=$(grep -r "moment" src/ --include="*.vue" --include="*.js" | grep -v "dayjs" | grep -v "comment" | head -10)

if [ -n "$remaining" ]; then
    echo "发现以下可能需要手动处理的moment引用："
    echo "$remaining"
else
    echo "所有moment引用已成功替换为dayjs！"
fi
