FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY target/SpringProject15_ConfigServer-3.3.5.jar app.jar

EXPOSE 8085

ENTRYPOINT [ "java", "-jar", "app.jar"]