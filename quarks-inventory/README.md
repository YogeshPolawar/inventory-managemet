# Quarks Inventory Management

This is a Spring Boot-based inventory management system that supports:
- Redis caching for improved performance
- Pessimistic locking to handle concurrent updates
- REST APIs for stock management and reservation
- Swagger UI for API documentation
- In-memory H2 database for demo/testing

## Features

- Add new items
- Add stock to items
- Reserve stock
- Cancel reservations
- View available stock
- Redis cache support for available stock
- Swagger API docs
- JUnit unit tests

## Prerequisites

- Java 17+
- Maven 3+
- Redis running on `localhost:6379`

## Build and Run

### Run Redis

You must have Redis running locally. Use Docker if not installed:

```bash
docker run --name redis -p 6379:6379 -d redis
```

### Build Project

```bash
mvn clean package
```

### Run App

```bash
java -jar target/inventory-1.0-SNAPSHOT.jar
```

App runs at: `http://localhost:8080`

Swagger UI available at: `http://localhost:8080/swagger-ui.html`

### Run Tests

```bash
mvn test
```

## Docker Build & Run

```bash
mvn clean package
docker build -t quarks-inventory .
docker run -p 8080:8080 quarks-inventory
```

## Author

Quarks Inventory System - Demo Project for Interview Showcase
