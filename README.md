# 🏨 Hotel Rating System
Welcome to the Hotel Rating System. This is a distributed full-stack application built using a Microservices architecture. It allows users to discover hotels, manage bookings, and provide ratings through a modern React interface, secured by a dedicated OAuth2/JWT Authentication server.

# Microservices Architecture (OAuth2.O Authorization Flow) Block-Diagram

![image alt](https://github.com/Hemant15-Bl/Microservices/blob/master/Screenshot%202026-01-02%20103702.png?raw=true)

---
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
│   └── hotel-rating-ui/     # React.js
├── docker-compose.yml  
└── start-app.bat
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

## 🐳 Containerization & Deployment
This project is fully Dockerized to ensure consistent environments across Development, Testing, and Production. It uses Docker Compose to manage the multi-container setup, including networking, volumes for persistent data, and health-checked startup sequences.

### 🏗️ Docker Architecture
- **Isolation**: Each service (Java, React, Databases) runs in its own isolated container.
- **Persistence**: Docker Volumes are used for MySQL, PostgreSQL, and MongoDB to ensure data survives container restarts.
- **Networking**: A dedicated bridge network (rating-net) allows services to communicate using container names (e.g., http://auth-service:8086) instead of volatile IP addresses.
- **Optimization**: Uses SerialGC and TieredCompilation flags to optimize JVM performance in resource-constrained environments (like 8GB RAM systems).

## 🚀 Getting Started
### 1. Prerequisites
Before running the application, ensure you have the following installed:
- **Docker Desktop** (Allocated with at least 8GB RAM for optimal performance).
- **Git**
- **Node.js & npm** (Only if running frontend manually).
### 2. Installation & Setup
Follow these steps to get the system up and running on your local machine:
 ### Step 1: Clone the Repository
````Bash
git clone https://github.com/Hemant15-Bl/hotel_ratings_app.git
cd hotel_ratings_app
````

## Step 2: Run with Docker (Recommended)
The simplest way to start the entire ecosystem is using the optimized startup script. This handles the specific order required for Eureka, Config Server, and the Auth Server to stabilize.
```bash
# Run the automated batch script (Windows)
start-app.bat
```
#### ⏱️ Note on Startup Time:
- **8GB RAM Systems:** Initial boot may take **7-8 minutes** as 7+ JVMs initialize and perform database migrations.
- **16GB+ RAM Systems:** Expect a much faster startup of **2-3 minutes.**

## Step 3: Access the Application 
Once the containers are "Healthy" in Docker Desktop:
- Frontend: http://localhost:5173
- Eureka Dashboard: http://localhost:8761
- API Gateway: http://localhost:8084


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
