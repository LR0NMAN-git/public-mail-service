# Mail Service

这是一个邮件服务应用程序，可以获取和显示邮件内容，并高亮显示验证码。

## 构建和推送Docker镜像

1. 构建JAR文件：
   ```
   ./gradlew bootJar
   ```

2. 构建Docker镜像：
   ```
   ./gradlew docker
   ```

3. 推送Docker镜像到Docker Hub：
   ```
   ./gradlew dockerPush
   ```

## 使用Docker Compose部署

1. 使用docker-compose.yml文件部署应用程序：
   ```
   docker-compose up -d
   ```

2. 应用程序将在端口8144上运行。

## 环境变量

- `SERVER_PORT`: 应用程序运行的端口。
- `MAIL_PROVIDER`: 邮件提供商。
- `MAIL_SERVER_HOST`: 邮件服务器主机名。
- `MAIL_SERVER_PORT`: 邮件服务器端口。
- `MAIL_SERVER_PROTOCOL`: 邮件服务器协议。
- `MAIL_SERVER_USERNAME`: 邮件服务器用户名。
- `MAIL_SERVER_PASSWORD`: 邮件服务器密码。
- `MAIL_SERVER_FOLDER`: 邮件文件夹。
- `MAIL_SERVER_READTIMEOUT`: 邮件服务器读取超时时间。
- `APP_PASSWORD`: 应用程序密码。

所有环境变量现在都是必填的。如果缺少任何环境变量，应用程序将不会启动。在Docker环境中，必须通过docker-compose.yml文件中的environment部分来设置这些环境变量。

application.yaml文件中使用了Spring Boot的环境变量占位符语法，格式为`${VARIABLE_NAME:default_value}`。这意味着：
- 如果设置了环境变量VARIABLE_NAME，将使用环境变量的值
- 如果未设置环境变量VARIABLE_NAME，将使用冒号后的默认值

## API端点

- `GET /health`: 健康检查端点，用于检查应用程序和邮件服务器的连接状态。
- `GET /emails`: 获取最新的邮件列表。

## 访问应用程序

应用程序启动后，可以通过以下URL访问：

```
http://localhost:8144
```