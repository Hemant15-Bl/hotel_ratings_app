FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN apt-get update && \
	apt-get install -y curl && \
	rm -rf /var/lib/apt/lists/*

COPY target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT [ "java", "-XX:TieredStopAtLevel=1", "-noverify", "-jar", "app.jar" ]