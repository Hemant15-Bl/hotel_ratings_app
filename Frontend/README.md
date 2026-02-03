# React + Vite

- [@vitejs/plugin-react](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react) uses [Babel](https://babeljs.io/) for Fast Refresh
- [@vitejs/plugin-react-swc](https://github.com/vitejs/vite-plugin-react/blob/main/packages/plugin-react-swc) uses [SWC](https://swc.rs/) for Fast Refresh

# Hotel Rating System
Welcome to the Hotel Rating System Microservices project. This application is designed to manage various aspects of a hotel, including adding hotels, and handling user ratings.

## Features
This application provides the following features:

### User Service
 - User registration and authentication.
 - User can book hotel rooms.
 - User can rate hotels.

### Hotel Service
 - Add hotels with details.
 - Add rooms to hotels.
 - Get hotel details by ID.
 - Get a list of all booked rooms in a hotel.

### Rating Service
 - Add ratings and reviews for hotels.
 - Get all ratings.
 - Get ratings by user ID or hotel ID.

### Service Registry
 - Register and discover microservices.

### Configuration Server
 - Manage centralized configurations for microservices.

### API Gateway
 - Gateway for accessing microservices.

## Tech Stack
 - Java
 - Spring Boot
 - Spring Cloud
 - Spring Cloud Eureka
 - Spring Cloud Config
 - Spring Security
 - Jwt Authentication
 - Spring Data JPA
 - Spring Web
 - Spring Data MongoDB
 - Spring Data REST
 - Spring Cloud Gateway
 - Netflix Eureka
 - MySQL
 - MongoDB
 - PostgreSQL
 - GitHub

## Checking Service Status
#### - Eureka Server:
 - Eureka Dashboard: http://localhost:8761
  - You can check the status of all registered microservices here. It will show which services are up and running and their corresponding instances.
Instances currently registered with Eureka:

#### - API-GATEWAY:

 - Availability Zones: UP (1) - DESKTOP-5G4FEQU:Apigateway-Service:8084

#### - CONFIG-SERVER:

- Availability Zones: UP (1) - DESKTOP-5G4FEQU:Config-Server:8085

#### - HOTELS-SERVICE:

- Availability Zones: UP (1) - DESKTOP-5G4FEQU:Hotel-Service:8082

#### - RATING-SERVICE:

- Availability Zones: UP (1) - DESKTOP-5G4FEQU:Rating-Service:8083

#### - USERS-SERVICE:

- Availability Zones: UP (1) - DESKTOP-5G4FEQU:Employee-Registry:8081
#### - API Gateway Default URL: http://localhost:8084

>>>>>>> 1e5c91f16298224ab23cc71cc31b552468aca7e5
