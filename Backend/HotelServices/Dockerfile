FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

COPY target/HotelService-1.1.jar app.jar

EXPOSE 8082

ENTRYPOINT [ "java", "-jar", "app.jar" ]
