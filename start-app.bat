@REM @echo off
@REM echo [1/4] Starting core infrastructure (DBs, Redis, Eureka)...
@REM :: Start the small ones first
@REM docker-compose up -d discovery-service redis auth-db hotel-db rating-db user-db watchtower
@REM timeout /t 30 /nobreak > nul

@REM echo [2/4] Starting Config Server...
@REM docker-compose up -d config-server
@REM :: Config server needs to be fully up before microservices touch it
@REM echo Waiting for Config Server to stabilize...
@REM timeout /t 45 /nobreak > nul

@REM echo [3/4] Starting Core Services (Auth, Hotel, Rating, User)...
@REM :: Start business services together, but keep Gateway for last
@REM docker-compose up -d auth-service hotel-service rating-service user-service
@REM echo Waiting 60 seconds for services to register with Eureka...
@REM timeout /t 60 /nobreak > nul

@REM echo [4/4] Starting API Gateway and Frontend...
@REM :: Gateway only starts when services are likely ready
@REM docker-compose up -d apigateway-service frontend

@REM echo All services are launching!
@REM pause

@echo off
echo [1/2] Pulling latest images from Docker Hub...
docker-compose pull

echo [2/2] Launching all services...
:: We use --no-build because GitHub Actions already did the building
docker-compose up -d --remove-orphans

echo Services are starting in the background. 
echo Use 'docker-compose ps' to check status.
pause