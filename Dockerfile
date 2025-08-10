# 使用官方OpenJDK运行时作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 将jar文件和启动脚本复制到容器中
COPY build/libs/*.jar app.jar
COPY scripts/startup.sh startup.sh

# 设置启动脚本的执行权限
RUN chmod +x startup.sh

# 暴露端口8144
EXPOSE 8144

# 使用启动脚本来运行应用程序
ENTRYPOINT ["./startup.sh"]