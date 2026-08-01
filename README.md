# 🛡️ CloudVault-Security: Event-Driven Microservices & Security Architecture

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Event--Driven-red.svg)](https://kafka.apache.org/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-OAuth2%20%2F%20JWT-blue.svg)](https://spring.io/projects/spring-security)
[![Docker](https://img.shields.io/badge/Docker-Compose%20Orchestrated-blue)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

An enterprise-grade, distributed microservices architecture demonstrating **API Gateway Routing**, **OAuth2 & JWT Authorization Server**, and **Resilient Event-Driven Messaging with Apache Kafka & Dead Letter Queue (DLQ)** patterns.

---

## 🏛️ System Architecture Diagram

```
                              ┌────────────────────────┐
                              │     Client / Web       │
                              └───────────┬────────────┘
                                          │  HTTP Requests
                                          ▼
                         ┌──────────────────────────────────┐
                         │   api-gateway (Port: 8080)       │
                         │   Spring Cloud Gateway + WebFlux │
                         └────────────────┬─────────────────┘
                                          │
                  ┌───────────────────────┴───────────────────────┐
                  │                                               │
                  ▼                                               ▼
  ┌───────────────────────────────┐               ┌───────────────────────────────┐
  │   auth-service (Port: 8081)   │               │ vault-event-service (8082)    │
  │   Spring Security + JJWT      │               │ Kafka Producer & Consumer     │
  └───────────────┬───────────────┘               └───────────────┬───────────────┘
                  │                                               │
                  ▼                                               ▼
  ┌───────────────────────────────┐               ┌───────────────────────────────┐
  │      PostgreSQL Database      │               │     Apache Kafka Cluster      │
  │     (User & Role Schema)      │               │ (file-vault-events & DLQ)     │
  └───────────────────────────────┘               └───────────────────────────────┘
```

---

## 🚀 Key Microservices & Features

### 1. `api-gateway` (Port: 8080)
- **Spring Cloud Gateway (WebFlux)** reactive routing engine.
- Dynamic path routing (`/api/v1/auth/**`, `/api/v1/vault-events/**`).
- Rate limiting and centralized logging global filters.
- Resilient fallback endpoints for microservice outages.

### 2. `auth-service` (Port: 8081)
- **OAuth2 & JWT Authorization Server** built with Spring Security 6 & JJWT `0.12.5`.
- HMAC-SHA256 token signing and BCrypt password hashing.
- Dual-Token Architecture: Access Token (15-min TTL) & Refresh Token (7-day TTL) Rotation.
- User registration, login authentication, token refresh, and validation endpoints.

### 3. `vault-event-service` (Port: 8082)
- **Asynchronous Event-Driven Messaging** powered by **Apache Kafka**.
- **Resiliency & Fault-Tolerance:**
  - **Exponential/Fixed Backoff Retry:** Automatically retries failed consumer events.
  - **Dead Letter Queue (DLQ):** Quarantines unrecoverable failed messages into `file-vault-events.DLQ` topic for audit and manual review.

---

## 📡 REST API Specifications

### 🔑 Authentication Service (`/api/v1/auth`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Register new user & issue JWT tokens |
| `POST` | `/api/v1/auth/login` | Authenticate user credentials & issue JWT tokens |
| `POST` | `/api/v1/auth/refresh` | Refresh Access Token using valid Refresh Token |
| `GET` | `/api/v1/auth/validate` | Verify JWT token validity & extract user details |

### ⚡ Event Service (`/api/v1/vault-events`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/v1/vault-events/publish` | Publish file vault event to Kafka topic |
| `GET` | `/api/v1/vault-events/processed` | Retrieve list of successfully processed events |
| `GET` | `/api/v1/vault-events/dlq` | Retrieve list of quarantined DLQ events |

---

## 🐳 Quick Start with Docker Compose

To launch the entire multi-container architecture (Zookeeper, Kafka, PostgreSQL, Auth Service, Vault Event Service, API Gateway):

```bash
# Clone repository
git clone https://github.com/ahmetfurkankisacik/CloudVault-Security.git
cd CloudVault-Security

# Build & launch containers
docker compose up --build -d

# Check running container statuses
docker compose ps
```

---

## 🧪 Running Unit & Integration Tests

Each microservice contains complete unit and integration tests covering JWT generation, Security filters, Kafka producers/consumers, and REST controllers:

```bash
# Run tests for auth-service
cd auth-service && mvn clean test

# Run tests for vault-event-service
cd ../vault-event-service && mvn clean test
```

---

## 👨‍💻 Author

**Ahmet Furkan Kısacık (AFK)**  
*Computer Engineer & Software Development Instructor*  
- **Website:** [ahmetfurkankisacik.com](https://ahmetfurkankisacik.com)  
- **LinkedIn:** [linkedin.com/in/afkdev](https://www.linkedin.com/in/afkdev)  
- **GitHub:** [github.com/ahmetfurkankisacik](https://github.com/ahmetfurkankisacik)
