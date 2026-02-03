FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY target/SpringProject14_APIGateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT [ "java", "-jar", "app.jar" ]