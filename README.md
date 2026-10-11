# ShopSphere — E-Commerce Microservices

ShopSphere is an e-commerce application designed to manage products and user authentication. The project is evolving from a Spring Boot monolith into a microservices architecture, with independent services and event-driven communication using Apache Kafka.

## Key Features

- Product management through REST APIs
- User registration and login with JWT authentication
- API Gateway for routing requests and validating JWTs
- Service discovery using Netflix Eureka
- PostgreSQL integration with Spring Data JPA
- Asynchronous event communication using Apache Kafka
- Docker Compose for running Kafka locally

## Architecture

```text
                         Client
                           |
                           v
                    API Gateway :8085
                           |
             +-------------+-------------+
             |                           |
             v                           v
      Auth Service :8083         Product Service :8081
             |                           |
             v                           v
       PostgreSQL                  PostgreSQL
                                         |
                                         v
                                    Kafka Topic
                                  product-events
                                         |
                                         v
                                Inventory Service :8084

                     Eureka Server :8761
                 Service discovery for services
```

## Project Structure

| Directory | Responsibility |
|---|---|
| `shopsphere/` | Original Spring Boot monolith |
| `shopsphere-product-service/` | Product REST APIs and Kafka producer |
| `shopsphere-auth-service/` | User authentication and JWT generation |
| `shopsphere-inventory-service/` | Consumes product events from Kafka |
| `shopsphere-api-gateway/` | Request routing and JWT validation |
| `shopsphere-service-registry/` | Eureka service discovery |
| `shopsphere-infra/` | Docker Compose infrastructure configuration |

## Technology Stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **Build Tool:** Maven
- **API:** REST
- **Database:** PostgreSQL
- **Persistence:** Spring Data JPA and Hibernate
- **Security:** Spring Security and JWT
- **Microservices:** Spring Cloud Gateway and Netflix Eureka
- **Messaging:** Apache Kafka
- **Containerization:** Docker and Docker Compose
- **Testing:** Postman

## Kafka Event Flow

When a product is created, Product Service publishes a JSON event to the `product-events` Kafka topic. Inventory Service consumes the event independently.

Example event:

```json
{
  "eventType": "PRODUCT_CREATED",
  "productId": 8,
  "productName": "Keyboard",
  "price": 1499.00
}
```

The current Inventory Service consumes and logs these events. Persistent inventory management is planned as a future enhancement.

## Prerequisites

- JDK 21
- IntelliJ IDEA
- Maven
- PostgreSQL
- Docker Desktop
- Git

## Running the Project

1. Start Docker Desktop.
2. Start Kafka from the infrastructure directory:

   ```powershell
   docker compose up -d
   ```

3. Start Eureka Server on port `8761`.
4. Start Auth Service on port `8083`.
5. Start Product Service on port `8081`.
6. Start Inventory Service on port `8084`.
7. Start API Gateway on port `8085`.

Configure database credentials and the JWT signing secret through environment variables in IntelliJ. Use the same JWT secret for Auth Service and API Gateway.

The original monolith is a separate application and is not required for the microservices flow.

## Current Status

- Product CRUD service implemented
- Authentication and JWT integration implemented
- API Gateway and Eureka integration configured
- Kafka producer and consumer integration working
- Inventory database persistence planned
- Further enhancements: inventory management, automated testing, CI/CD, and Kubernetes deployment

## Learning Goals

This project is being developed to gain hands-on experience with Spring Boot, REST API design, authentication, microservices architecture, asynchronous messaging, Docker, and production-oriented software development.

---

**Note:** This is an actively developed learning project. Features and architecture may evolve as new capabilities are implemented.
