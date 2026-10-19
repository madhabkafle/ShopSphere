# ShopSphere — E-Commerce Microservices Platform

ShopSphere is a Spring Boot-based e-commerce application that is evolving from a monolithic architecture into a distributed microservices platform. It provides product management, JWT-based authentication, service discovery, API routing, asynchronous event communication, containerization, CI/CD automation, and Kubernetes deployment.

The project demonstrates practical implementation of Java microservices, PostgreSQL persistence, Apache Kafka messaging, Docker, GitHub Actions, and Kubernetes orchestration.

## Table of Contents

- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Microservices](#microservices)
- [Kafka Event Flow](#kafka-event-flow)
- [Docker and Containerization](#docker-and-containerization)
- [CI/CD Pipeline](#cicd-pipeline)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Local Development Setup](#local-development-setup)
- [API Testing](#api-testing)
- [Environment Configuration](#environment-configuration)
- [Troubleshooting](#troubleshooting)
- [Future Enhancements](#future-enhancements)

## Architecture

ShopSphere uses independently deployable Spring Boot services that communicate through HTTP APIs, service discovery, and asynchronous Kafka events.

```text
                         Client
                           |
                           v
                    API Gateway :8085
                           |
                           v
                    Product Service :8081
                           |
                +----------+----------+
                |                     |
                v                     v
           PostgreSQL             Kafka
       shopsphere_product      product-events
                                      |
                                      v
                              Inventory Service
                                    :8084

                 Auth Service :8083
                        |
                        v
                   PostgreSQL
                  shopsphere_auth

             Eureka Service Registry
                     :8761

       Shared infrastructure:
       PostgreSQL :5432
       Kafka      :9092
```

The API Gateway provides an entry point for client requests and routes product requests to Product Service. Auth Service handles authentication and JWT generation. Eureka provides service registration and discovery, while Kafka supports asynchronous communication between Product Service and Inventory Service.

PostgreSQL stores application data in separate databases for the monolith, Product Service, Auth Service, and Inventory Service.

## Features

- Product management through REST APIs.
- User registration and login with JWT authentication.
- API Gateway for request routing.
- Service discovery using Netflix Eureka.
- PostgreSQL integration using Spring Data JPA and Hibernate.
- Asynchronous event communication using Apache Kafka.
- Event-driven integration between Product Service and Inventory Service.
- Docker multi-stage builds for Spring Boot applications.
- Docker Hub image publishing.
- Automated build and test pipeline using GitHub Actions.
- Kubernetes deployments, Services, Secrets, ConfigMaps, and persistent storage.
- Separate deployment of microservices.
- Local development using Docker Desktop and Kubernetes.

## Technology Stack

| Category | Technologies |
|---|---|
| Language | Java 21 |
| Backend | Spring Boot |
| Build tool | Maven |
| API design | REST APIs |
| Database | PostgreSQL |
| Persistence | Spring Data JPA, Hibernate |
| Authentication | Spring Security, JWT |
| Microservices | Spring Cloud |
| API routing | Spring Cloud Gateway MVC |
| Service discovery | Netflix Eureka |
| Messaging | Apache Kafka |
| Containerization | Docker |
| Container registry | Docker Hub |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Development environment | IntelliJ IDEA, PowerShell |
| API testing | Postman |

## Project Structure

```text
ShopSphere/
├── shopsphere/
│   └── Original Spring Boot monolith
│
├── shopsphere-product-service/
│   └── shopsphere-product-service/
│       └── Product APIs and Kafka producer
│
├── shopsphere-auth-service/
│   └── Authentication and JWT generation
│
├── shopsphere-inventory-service/
│   └── Kafka consumer and inventory events
│
├── shopsphere-api-gateway/
│   └── Request routing
│
├── shopsphere-service-registry/
│   └── Eureka service registry
│
├── shopsphere-infra/
│   └── Local infrastructure configuration
│
├── k8s/
│   ├── data-services.yaml
│   ├── service-registry.yaml
│   ├── product-service.yaml
│   ├── auth-service.yaml
│   ├── inventory-service.yaml
│   └── api-gateway.yaml
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
└── README.md
```

## Microservices

### 1. Product Service

- **Port:** 8081
- **Database:** `shopsphere_product`
- **Responsibilities:** Product management and REST API operations.
- **Messaging:** Publishes product events to Kafka when products are created.

### 2. Auth Service

- **Port:** 8083
- **Database:** `shopsphere_auth`
- **Responsibilities:** User registration, login, and JWT generation.
- **Security:** Uses a configurable JWT signing secret.

### 3. Inventory Service

- **Port:** 8084
- **Responsibilities:** Consumes product events from Kafka.
- **Messaging:** Uses the `inventory-service-group` consumer group.

The current consumer processes and logs product events. Persistent inventory management is a potential future enhancement.

### 4. API Gateway

- **Port:** 8085
- **Responsibilities:** Provides a common entry point for client requests and routes product requests to Product Service.
- **Routing:** Uses Spring Cloud Gateway MVC with a load-balancing route configured for Product Service.

### 5. Eureka Service Registry

- **Port:** 8761
- **Responsibilities:** Registers application instances and allows services to discover registered services.

### 6. Original Monolith

- **Port:** 8080
- **Responsibilities:** Original ShopSphere application.
- **Deployment:** Separate from the microservices architecture.

The monolith is optional when running the microservices flow.

## Kafka Event Flow

ShopSphere uses Apache Kafka to support asynchronous communication between Product Service and Inventory Service.

The event flow is:

1. A client creates a product through Product Service.
2. Product Service publishes a product-created event.
3. Kafka stores the event in the `product-events` topic.
4. Inventory Service consumes the event independently.
5. The consumer processes and logs the event.

Example event payload:

```json
{
  "eventType": "PRODUCT_CREATED",
  "productId": 8,
  "productName": "Keyboard",
  "price": 1499.00
}
```

The producer and consumer are decoupled: Product Service does not need to make a synchronous HTTP call to Inventory Service to publish the event.

## Docker and Containerization

Each Spring Boot application has a Dockerfile that builds the application using Maven and packages it into a Java runtime image.

The multi-stage build separates compilation from runtime execution, reducing the need to include the Maven build environment in the final application image.

Docker images are published to Docker Hub under the `madhab05` namespace.

| Service | Docker Hub image |
|---|---|
| Monolith | `madhab05/shopsphere:latest` |
| Product Service | `madhab05/shopsphere-product-service:latest` |
| Auth Service | `madhab05/shopsphere-auth-service:latest` |
| Inventory Service | `madhab05/shopsphere-inventory-service:latest` |
| API Gateway | `madhab05/shopsphere-api-gateway:latest` |
| Service Registry | `madhab05/shopsphere-service-registry:latest` |

Images are also tagged with the corresponding Git commit SHA by the CI/CD workflow.

## CI/CD Pipeline

ShopSphere uses GitHub Actions to automate application verification and Docker image publishing.

The workflow is triggered by pushes to the `main` branch and pull requests.

### Pipeline stages

1. Check out the repository.
2. Set up Java 21 and Maven.
3. Start PostgreSQL for integration testing.
4. Build and verify the six Maven projects.
5. Build Docker images for the services.
6. Authenticate with Docker Hub using GitHub repository secrets.
7. Publish images tagged `latest` and with the Git commit SHA.

The six Maven projects are the monolith, Product Service, Auth Service, Inventory Service, API Gateway, and Service Registry.

### Required GitHub Actions secrets

Configure these secrets in the repository's **Settings → Secrets and variables → Actions**:

- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

Never place Docker Hub credentials, database passwords, or JWT signing secrets directly in the workflow file.

## Kubernetes Deployment

ShopSphere is deployed locally using Kubernetes through Docker Desktop.

The Kubernetes namespace is `shopsphere`.

### Infrastructure resources

- PostgreSQL StatefulSet and Service.
- Kafka StatefulSet and Service, running in KRaft mode.
- Persistent volume claims for PostgreSQL and Kafka.
- ConfigMap for PostgreSQL database initialization.
- Kubernetes Secrets for database credentials.
- Deployments and Services for application microservices.

### Prerequisites

- Docker Desktop with Kubernetes enabled.
- `kubectl` installed and configured for the `docker-desktop` context.
- Access to the published Docker Hub images.
- The ShopSphere repository cloned locally.

### Deploy the application

Open PowerShell in the repository root:

```powershell
cd C:\MadhabK\Projects\Shopshere
```

Create the namespace if it does not already exist:

```powershell
kubectl create namespace shopsphere --dry-run=client -o yaml | kubectl apply -f -
```

Apply the infrastructure manifests:

```powershell
kubectl apply -f .\k8s\data-services.yaml
```

Wait for PostgreSQL and Kafka to start:

```powershell
kubectl rollout status statefulset/postgres -n shopsphere --timeout=180s
kubectl rollout status statefulset/kafka -n shopsphere --timeout=180s
```

Deploy Eureka and the application services:

```powershell
kubectl apply -f .\k8s\service-registry.yaml
kubectl apply -f .\k8s\product-service.yaml
kubectl apply -f .\k8s\auth-service.yaml
kubectl apply -f .\k8s\inventory-service.yaml
kubectl apply -f .\k8s\api-gateway.yaml
```

Wait for the application deployments:

```powershell
kubectl rollout status deployment/service-registry -n shopsphere --timeout=180s
kubectl rollout status deployment/product-service -n shopsphere --timeout=180s
kubectl rollout status deployment/auth-service -n shopsphere --timeout=180s
kubectl rollout status deployment/inventory-service -n shopsphere --timeout=180s
kubectl rollout status deployment/api-gateway -n shopsphere --timeout=180s
```

### Verify deployment status

Check pods, Services, and persistent storage:

```powershell
kubectl get pods,svc,pvc -n shopsphere
```

Check logs for a particular service:

```powershell
kubectl logs deployment/product-service -n shopsphere
```

Replace `product-service` with the relevant Deployment name to inspect another application.

### Access the APIs locally

Expose the API Gateway through port forwarding:

```powershell
kubectl port-forward -n shopsphere svc/api-gateway 8085:8085
```

In another terminal, test the product endpoint:

```powershell
curl.exe -i http://localhost:8085/api/products
```

Expose the Eureka dashboard when needed:

```powershell
kubectl port-forward -n shopsphere svc/service-registry 8761:8761
```

Then open `http://localhost:8761` in your browser.

Port-forward commands must remain running while the corresponding local connection is in use.

### Kubernetes service addresses

Within the `shopsphere` namespace, the services use the following internal addresses:

| Dependency | Address |
|---|---|
| PostgreSQL | `postgres:5432` |
| Kafka | `kafka:9092` |
| Eureka | `http://service-registry:8761/eureka/` |
| Product Service | `http://product-service:8081` |
| Auth Service | `http://auth-service:8083` |
| Inventory Service | `http://inventory-service:8084` |
| API Gateway | `http://api-gateway:8085` |

Use Kubernetes Service DNS names rather than `localhost` when one container needs to communicate with another.

## Local Development Setup

### Prerequisites

- JDK 21
- IntelliJ IDEA or another Java IDE
- Maven or Maven Wrapper
- PostgreSQL
- Docker Desktop
- Git
- Postman

### Run infrastructure

Start Docker Desktop, then start Kafka using the Compose configuration in the infrastructure directory:

```powershell
docker compose up -d
```

Run the command from the directory containing the relevant Compose file.

Ensure PostgreSQL is running and that the required databases exist:

- `shopsphere`
- `shopsphere_product`
- `shopsphere_auth`
- `shopsphere_inventory`

### Start the services

Run each Spring Boot application from IntelliJ or through its Maven Wrapper:

1. Service Registry on port `8761`.
2. Auth Service on port `8083`.
3. Product Service on port `8081`.
4. Inventory Service on port `8084`.
5. API Gateway on port `8085`.

The original monolith can be started separately on port `8080`.

Configure each application's database credentials, JWT signing secret, Kafka bootstrap address, and Eureka URL for the selected environment.

For local development, the relevant addresses may use `localhost`. For Kubernetes, use the internal Service DNS names described above.

## API Testing

Use Postman or `curl.exe` to test the application.

Example product endpoint:

```http
GET http://localhost:8085/api/products
```

Example direct Product Service endpoint:

```http
GET http://localhost:8081/api/products
```

The Gateway currently has an explicitly configured route for `/api/products/**`. Other routes should be configured and tested before assuming that they are exposed through the Gateway.

For authentication testing, use the registration and login endpoints defined by Auth Service. Verify that login returns a JWT and that protected endpoints accept valid tokens and reject invalid or expired tokens, as applicable to the implemented security configuration.

For Kafka testing, create a product and inspect Inventory Service logs to verify that the corresponding event is consumed.

## Environment Configuration

The application uses environment variables for sensitive configuration.

Examples include:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
SPRING_DATASOURCE_URL
SPRING_KAFKA_BOOTSTRAP_SERVERS
```

Not every service requires every variable. Configure only the variables used by that service.

In Kubernetes, database credentials and the JWT signing secret are supplied through Kubernetes Secrets. Non-sensitive configuration, such as internal service addresses, can be supplied through environment variables or ConfigMaps.

**Security practices:**

- Do not commit passwords, JWT secrets, access tokens, or private credentials.
- Do not use development credentials in production.
- Use a consistent JWT signing secret between the services that issue and validate tokens.
- Rotate credentials if they are accidentally exposed.
- Use managed secrets and appropriate access controls for production deployments.

## Troubleshooting

### Pod is not starting

```powershell
kubectl get pods -n shopsphere
kubectl describe pod <pod-name> -n shopsphere
kubectl logs deployment/<deployment-name> -n shopsphere
```

Check for missing Secrets, incorrect environment variables, image-pull failures, and application startup exceptions.

### Database connection failure

Verify that PostgreSQL is running, the expected database exists, the Secret keys match the manifest, and the JDBC URL uses `postgres:5432` inside Kubernetes.

### Kafka connection failure

Verify that Kafka is running and the client uses `kafka:9092` within the Kubernetes namespace.

### Eureka or Gateway routing failure

Check that the services register with Eureka and that Gateway routes reference the correct service identifiers. When diagnosing routing failures, inspect both Gateway logs and the destination service logs.

### Deployment rollout timeout

```powershell
kubectl rollout status deployment/<deployment-name> -n shopsphere --timeout=180s
kubectl get events -n shopsphere --sort-by=.metadata.creationTimestamp
```

Use the events and pod logs to identify the cause before changing configuration or rebuilding images.

## Future Enhancements

Potential improvements include:

- Persistent inventory management with database-backed stock levels.
- Automated unit, integration, and end-to-end tests.
- More comprehensive API Gateway routing and authorization tests.
- Kubernetes readiness and liveness probes.
- Resource requests and limits.
- Centralized logging and metrics.
- Production-grade secrets management.
- Automated deployment to a cloud Kubernetes environment.
- Distributed tracing and resilience patterns.

## Project Status

ShopSphere has been containerized with Docker, integrated with GitHub Actions for automated verification and image publishing, and deployed to a local Kubernetes cluster.

The current architecture includes the monolith, Product Service, Auth Service, Inventory Service, API Gateway, Eureka Service Registry, PostgreSQL, and Kafka.

The Kubernetes deployment has been brought up successfully. End-to-end behavior should be validated whenever application code, routing, security, or infrastructure configuration changes.

## Learning Outcomes

This project provides hands-on experience with:

- Designing and deploying Spring Boot microservices.
- Building REST APIs and handling authentication with JWT.
- Integrating PostgreSQL through Spring Data JPA and Hibernate.
- Implementing event-driven communication with Apache Kafka.
- Configuring service discovery and API routing.
- Containerizing Java applications with multi-stage Docker builds.
- Automating builds and image publishing with GitHub Actions.
- Deploying and troubleshooting services using Kubernetes.
- Managing service configuration, Secrets, networking, and persistent storage.

---

**ShopSphere** is an actively maintained learning project focused on practical backend engineering, distributed systems, containerization, and deployment automation.
