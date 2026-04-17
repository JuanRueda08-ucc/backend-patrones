# AI Proxy Platform

Backend for the Proxy Design Pattern workshop built with Spring Boot 3.

## What is this project?

A platform where different users consume a simulated AI text-generation service.
The backend will implement proxy layers for rate limiting and quota management per user plan.
This phase establishes the technical foundation only.

## Technologies

- Java 17
- Spring Boot 3.2
- Maven
- Lombok
- Spring Actuator

## How to run locally

```bash
./mvnw spring-boot:run
```

Or on Windows:

```bash
mvnw.cmd spring-boot:run
```

The server starts at: http://localhost:8080

## Available endpoints

| Method | Path      | Description         |
|--------|-----------|---------------------|
| GET    | /health   | Returns plain "OK"  |
| GET    | /actuator | Actuator endpoints  |
