@echo off
echo [1/3] Starting Infrastructure (Databases, Redis, Eureka, Config)...
docker-compose up -d discovery-service config-server redis auth-db hotel-db rating-db user-db

echo Waiting 45 seconds for Config Server and Eureka to stabilize...
timeout /t 45 /nobreak > nul

echo [2/3] Starting Auth Service...
docker-compose up -d auth-service

echo Waiting 20 seconds for Auth Server...
timeout /t 50 /nobreak > nul

echo [3/3] Starting remaining Microservices and Frontend...
docker-compose up -d
echo All services are launching! Check status with: docker-compose ps
pause