# 🏨 Hotel Rating System
Welcome to the Hotel Rating System. This is a distributed full-stack application built using a Microservices architecture. It allows users to discover hotels, manage bookings, and provide ratings through a modern React interface, secured by a dedicated OAuth2/JWT Authentication server.

## 📂 Project Structure
The repository is organized into two main sections:
- **/Backend:** Spring Boot microservices, security, and infrastructure.
- **/Frontend:** React.js application for users and admins.

```text
hotel-ratings-app (root)
├── backend/
│   ├── service-registry/    # Eureka Server
│   ├── config-server/       # Centralized Config
│   ├── api-gateway/         # Routing & Auth Filter
│   ├── auth-server/         # OAuth2.0 Logic
│   ├── user-service/        # MySQL
│   ├── hotel-service/       # PostgreSQL
│   └── rating-service/      # MongoDB
└── frontend/
    └── hotel-rating-ui/     # React.js
```
---

## 🛠️ Tech Stack
- **Category:**          Technologies
- **Backend:**           Java 17+, Spring Boot 3.x, Maven
- **Microservices:**     Spring Cloud (Eureka, Config Server, API Gateway)
- **SecuritySpring:**    Security, JWT, OAuth2 (AuthServer)
- **Frontend:**          React.js, Vite, Axios, Bootstrap/Tailwind
- **Databases:**         MySQL (Users), MongoDB (Ratings), PostgreSQL(Hotels/Auth)

---

## 📡 Microservices Architecture
### Core Services
1. AuthServer (Port: 8086): Dedicated service for issuing JWT tokens and handling User Authentication.
2. API Gateway (Port: 8084): The single entry point for the Frontend. Handles routing and security filtering.
3. Config Server (Port: 8085): Centralized configuration management for all services.
4. Service Registry (Port: 8761): Netflix Eureka server for service discovery.

### Business Services
- User Service (8081): Manages user profiles and booking history.
- Hotel Service (8082): Handles hotel inventory and room details.
- Rating Service (8083): Manages hotel reviews and ratings (NoSQL).

## 💻 Frontend Features (React)
- Modern UI: Redesigned Home, About, and Contact pages.
- Secure Access: Integrated with AuthServer for Login/Signup.
- Dynamic Dashboard: Users can view and rate hotels in real-time.
- Admin Panel: Dedicated routes for adding hotels and managing inventory.

## 🚀 Getting Started
### 1. Backend Setup
1. Start Service Registry (Eureka) first.
2. Start Config Server.
3. Start AuthServer.
4. Start the remaining microservices (User, Hotel, Rating, Gateway).
### 2. Frontend Setup

````Bash
cd Frontend
npm install
npm run dev
````

## Service Endpoints
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8084
- Frontend UI: http://localhost:5173 (Vite)


# OAuth2.O Authorization Flow Block-Diagram

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Screenshot%202026-01-02%20103702.png?raw=true)

---

## User Panel
![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/landingPage.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Login.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Signup.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Contact.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/About.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/User-dashboard.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/View-hotel-user.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Profile.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Update.png?raw=true)

---

---
## Admin Panel

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Admin-dashboard.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/users.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/hotels.png?raw=true)

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/ratings.png?raw=true)
