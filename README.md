# AI Proxy Platform

Backend for the **Proxy Design Pattern** workshop — built with Spring Boot 3 and Java 17.

## What is this project?

A simulated AI text-generation platform where users consume a mock AI service.
The backend uses the **Proxy Pattern** to apply two layers of access control before
reaching the real service:

1. **RateLimitProxyService** — enforces requests-per-minute limits per plan
2. **QuotaProxyService** — enforces monthly token quotas per plan
3. **RealMockAIGenerationService** — the actual (simulated) text generator

## Architecture

```
HTTP Request
      │
      ▼
RateLimitProxyService     ← checks req/min limit, increments window counter
      │
      ▼
QuotaProxyService         ← checks monthly token quota, records daily usage
      │
      ▼
RealMockAIGenerationService  ← simulates 1200ms latency, returns mock text
```

The controller only depends on `AIGenerationService` (the shared interface).
The proxy chain is assembled in `ServiceConfig` using explicit `@Bean` methods.

## Plans

| Plan       | Requests / min | Monthly tokens |
|------------|----------------|----------------|
| FREE       | 10             | 50,000         |
| PRO        | 60             | 500,000        |
| ENTERPRISE | unlimited (-1) | unlimited (-1) |

Demo users pre-loaded on startup: `user-free-1`, `user-pro-1`, `user-enterprise-1`.

## Technologies

- Java 17
- Spring Boot 3.2
- Maven
- Lombok
- Spring Actuator

## Endpoints

### AI Generation

| Method | Path               | Body                          | Description              |
|--------|--------------------|-------------------------------|--------------------------|
| POST   | /api/ai/generate   | `{ userId, prompt }`          | Generate text via proxies|

### Quota Management

| Method | Path                       | Params / Body              | Description               |
|--------|----------------------------|----------------------------|---------------------------|
| GET    | /api/quota/status          | `?userId=`                 | Current quota and limits  |
| GET    | /api/quota/history         | `?userId=`                 | Last 7 days of usage      |
| POST   | /api/quota/upgrade         | `{ userId, newPlan }`      | Upgrade user plan         |

### Other

| Method | Path      | Description         |
|--------|-----------|---------------------|
| GET    | /health   | Returns plain "OK"  |

## Error responses

All errors return a consistent JSON body:

```json
{
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded for plan FREE. Limit: 10 req/min.",
  "timestamp": "2026-04-17T18:00:00",
  "retryAfterSeconds": 47
}
```

| Status | Meaning                        |
|--------|--------------------------------|
| 400    | Bad request / invalid input    |
| 402    | Monthly quota exceeded         |
| 404    | User not found                 |
| 429    | Rate limit exceeded            |
| 500    | Unexpected server error        |

## How to run locally

```bash
./mvnw spring-boot:run
```

On Windows (Git Bash):
```bash
./mvnw spring-boot:run
```

On Windows (Command Prompt):
```bash
mvnw.cmd spring-boot:run
```

Server starts at: http://localhost:8080

## Example requests

```bash
# Generate text
curl -X POST http://localhost:8080/api/ai/generate \
  -H "Content-Type: application/json" \
  -d '{"userId":"user-free-1","prompt":"Explain quantum computing"}'

# Check quota status
curl "http://localhost:8080/api/quota/status?userId=user-pro-1"

# View usage history
curl "http://localhost:8080/api/quota/history?userId=user-free-1"

# Upgrade plan
curl -X POST http://localhost:8080/api/quota/upgrade \
  -H "Content-Type: application/json" \
  -d '{"userId":"user-free-1","newPlan":"PRO"}'
```

## Deploy to Railway or Render

The server port is read from the `PORT` environment variable (default 8080):

```
server.port=${PORT:8080}
```

Steps for **Railway**:
1. Connect your GitHub repository
2. Railway detects Spring Boot automatically via `pom.xml`
3. Set start command: `./mvnw spring-boot:run` (or let Railway auto-detect)
4. Deploy — Railway injects `PORT` automatically

Steps for **Render**:
1. Create a new Web Service → connect GitHub repository
2. Build command: `./mvnw package -DskipTests`
3. Start command: `java -jar target/aiproxyplatform-0.0.1-SNAPSHOT.jar`
4. Render injects `PORT` automatically
