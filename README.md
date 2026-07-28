# 🛡️ CloudVault-Security
> **Spring Cloud Gateway, HashiCorp Vault, Distributed Redis Caching & OAuth2 / OpenID Connect Microservices Architecture**

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.0-6DB33F?style=for-the-badge&logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0.0-6DB33F?style=for-the-badge&logo=spring)
![OAuth2](https://img.shields.io/badge/OAuth2_/_OIDC-Security-3C873A?style=for-the-badge&logo=openid)
![HashiCorp Vault](https://img.shields.io/badge/HashiCorp-Vault-00C4B3?style=for-the-badge&logo=vault)
![Redis](https://img.shields.io/badge/Redis-Distributed_Cache-DC382D?style=for-the-badge&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)

---

## 📐 Enterprise Architecture

```mermaid
graph TD
    Client[📱 Client / Frontend] -->|HTTP Requests| Gateway[🛡️ Spring Cloud Gateway :8080]
    
    subgraph Security & Config Layer
        Gateway -->|Validate JWT Token| AuthServer[🔐 OAuth2 Auth Service :8090]
        Gateway -->|Fetch Dynamic Secrets| Vault[🗝️ HashiCorp Vault :8200]
    end
    
    subgraph Microservices Layer
        Gateway -->|Routed Request| UserService[👤 User Service :8091]
        UserService -->|Distributed Cache| Redis[⚡ Redis Cache :6379]
        UserService -->|Persistence| UserDB[(🗄️ Postgres User DB)]
    end
```

---

## 🗓️ 5-Day Development Roadmap

| Day | Module / Component | Technologies | Status |
| :--- | :--- | :--- | :---: |
| 📅 **Day 1** | `api-gateway` | Spring Cloud Gateway, Rate Limiting, Route Filters | 🔄 In Progress |
| 📅 **Day 2** | `auth-service` | OAuth2 / JWT Authorization Server, OIDC | ⏳ Queued |
| 📅 **Day 3** | `config-vault` | Spring Cloud Config & HashiCorp Vault Secret Management | ⏳ Queued |
| 📅 **Day 4** | `user-service` | User Management & Redis Distributed Caching Layer | ⏳ Queued |
| 📅 **Day 5** | `orchestration` | Docker Compose & Kubernetes Ingress Controller | ⏳ Queued |

---

## 👨‍💻 Author
**Ahmet Furkan Kısacık**  
* Software Engineer & Technical Instructor  
* Website: [ahmetfurkankisacik.com](https://ahmetfurkankisacik.com)  
* LinkedIn: [linkedin.com/in/afkdev](https://www.linkedin.com/in/afkdev)
