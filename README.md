# Reward Points Service

A Spring Boot REST API that calculates customer reward points for retail purchases.

## Rewards rule

For every transaction:
- **2 points** for every dollar spent **over $100**
- **1 point** for every dollar spent **between $50 and $100**
- **0 points** for the portion of a purchase at or below $50

> Example: a **$120** purchase = 2×$20 (the amount over $100) + 1×$50 (the amount between $50 and $100) = **90 points**.

Points are always calculated **per transaction**, then summed per calendar month and in total per customer.

---

## Tech stack

| Layer | Technology                               |
|---|------------------------------------------|
| Language / Runtime | Java 25                                  |
| Framework | Spring Boot 4.1.0' (Web, Data JPA, Validation) |
| Database | H2 in-memory database                    |
| API docs | springdoc-openapi (Swagger UI)           |
| Build | Gradle (Groovy DSL)                      |
| Testing | JUnit 5, Mockito, AssertJ, Spring MockMvc |

## Architecture

The project follows a standard layered architecture with clear separation of concerns:

```
Controller  →  Service (interface + impl)  →  Repository (Spring Data JPA)  →  H2
   ↑                    ↑
 DTOs           RewardCalculator (isolated points formula)
   ↑
GlobalExceptionHandler (centralized error handling, @RestControllerAdvice)
```

- **Controllers** (`controller/`) — thin, HTTP-only concerns: routing, request/response mapping, validation triggers.
- **Services** (`service/`, `service/impl/`) — business logic behind interfaces, so implementations are swappable and mockable in tests.
- **Repositories** (`repository/`) — Spring Data JPA interfaces, no query logic leaks into services.
- **`RewardCalculator`** — the reward formula lives in its own class, decoupled from persistence and web layers, so it can be unit tested in complete isolation.
- **DTOs** (`dto/`) — requests/responses never expose JPA entities directly.
- **`GlobalExceptionHandler`** — a single `@RestControllerAdvice` translates every exception type (not found, bad date range, validation failures, malformed JSON, type mismatches, unexpected errors) into a consistent JSON error shape.

---

## Getting started

### Prerequisites
- JDK 25
- Gradle 9.x (or generate the wrapper once with your local Gradle install — see below)

### First-time setup: generate the Gradle wrapper

This project ships with `build.gradle` and `settings.gradle` but not the wrapper binary itself (it's a small jar that's normally committed to the repo). Generate it once, from a machine with Gradle installed:

```bash
gradle wrapper --gradle-version 9.5.1
```

This creates `gradlew`, `gradlew.bat`, and `gradle/wrapper/`. Commit those and from then on everyone can use `./gradlew` without installing Gradle themselves. If you'd rather not bother, just use your local `gradle` command directly in all commands below (e.g. `gradle bootRun` instead of `./gradlew bootRun`).

### Run the application

```bash
./gradlew bootRun
```

The app starts on **http://localhost:8080** and seeds a demo dataset (3 customers, transactions spanning January–March 2024, covering every points tier — see `DataSeeder.java`).

### Run the tests

```bash
./gradlew test
```

Includes:
- Unit tests for the reward points formula (`RewardCalculatorTest`) — covers every tier boundary, rounding, zero/negative amounts, and the exact $120 → 90 points example from the spec.
- Unit tests for `RewardServiceImpl` and `TransactionServiceImpl` (Mockito) — aggregation across months, date-range filtering, not-found and invalid-range error paths.
- Integration tests (`@SpringBootTest` + `MockMvc`) for the Reward and Transaction controllers — full HTTP round-trip against a real (test-profile) H2 database, including validation and error responses.

### Explore the API

Once running:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs
- **H2 console:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:rewardsdb`, user `sa`, no password)

---

## API reference

All endpoints are prefixed with `/api`. All error responses share this shape:

```json
{
  "timestamp": "2026-07-22T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 999",
  "path": "/api/rewards/customers/999"
}
```

Validation failures additionally include a `validationErrors` map of `field -> message`.

### Customers

| Method | Path | Description |
|---|---|---|
| GET | `/api/customers` | List all customers |
| GET | `/api/customers/{id}` | Get a single customer |

### Transactions

| Method | Path | Description |
|---|---|---|
| POST | `/api/transactions` | Record a new purchase |
| GET | `/api/transactions` | List transactions, optionally filtered |

**GET `/api/transactions` query parameters** (all optional, combinable):
- `customerId` — only this customer's transactions
- `startDate`, `endDate` — inclusive date range (`yyyy-MM-dd`)

**POST `/api/transactions` request body:**
```json
{
  "customerId": 1,
  "transactionDate": "2024-01-15",
  "amount": 120.00
}
```
Validation: `customerId` required and must reference an existing customer (404 if not); `transactionDate` required and cannot be in the future; `amount` required and must be greater than 0.

**Response (201 Created):**
```json
{
  "id": 7,
  "customerId": 1,
  "customerName": "Alice Anderson",
  "transactionDate": "2024-01-15",
  "amount": 120.00,
  "pointsEarned": 90
}
```

### Rewards

| Method | Path | Description |
|---|---|---|
| GET | `/api/rewards` | Reward summary for **every** customer |
| GET | `/api/rewards/customers/{customerId}` | Reward summary for one customer, by id |
| GET | `/api/rewards/customers/name/{customerName}` | Reward summary for one customer, by name (case-insensitive) |

All three accept optional `startDate` and `endDate` query parameters (`yyyy-MM-dd`, inclusive) to scope the calculation to a date range. Omit both to aggregate over all recorded transactions.

**Example: `GET /api/rewards/customers/1`**
```json
{
  "customerId": 1,
  "customerName": "Alice Anderson",
  "monthlyRewards": [
    { "month": "2024-01", "points": 90 },
    { "month": "2024-02", "points": 275 },
    { "month": "2024-03", "points": 51 }
  ],
  "totalPoints": 416
}
```

**Example: `GET /api/rewards?startDate=2024-02-01&endDate=2024-02-29`**
Returns the same shape as `GET /api/rewards`, but as an array, restricted to February for every customer.


