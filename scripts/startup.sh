#!/bin/bash

# 检查必要的环境变量是否已设置
required_vars=(
  "MAIL_SERVER_HOST"
  "MAIL_SERVER_PORT"
  "MAIL_SERVER_PROTOCOL"
  "MAIL_SERVER_USERNAME"
  "MAIL_SERVER_PASSWORD"
  "APP_PASSWORD"
)

missing_vars=()

for var in "${required_vars[@]}"; do
  if [ -z "${!var}" ]; then
    missing_vars+=("$var")
  fi
done

if [ ${#missing_vars[@]} -ne 0 ]; then
  echo "错误: 以下必要的环境变量未设置: ${missing_vars[*]}"
  exit 1
fi

# 如果所有变量都已设置，则启动应用程序
exec java -jar /app/app.jar