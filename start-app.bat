@echo off
echo [1/4] Starting core infrastructure (DBs, Redis, Eureka)...
:: Start the small ones first
docker-compose up -d discovery-service redis auth-db hotel-db rating-db user-db watchtower
timeout /t 30 /nobreak > nul

echo [2/4] Starting Config Server...
docker-compose up -d config-server
:: Config server needs to be fully up before microservices touch it
echo Waiting for Config Server to stabilize...
timeout /t 45 /nobreak > nul

echo [3/4] Starting Core Services (Auth, Hotel, Rating, User)...
:: Start business services together, but keep Gateway for last
docker-compose up -d auth-service hotel-service rating-service user-service
echo Waiting 60 seconds for services to register with Eureka...
timeout /t 60 /nobreak > nul

echo [4/4] Starting API Gateway and Frontend...
:: Gateway only starts when services are likely ready
docker-compose up -d apigateway-service frontend

echo All services are launching!
pause