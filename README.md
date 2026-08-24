# Currency Converter

A Spring Boot REST backend that converts amounts between world currencies using live rates
from an external exchange-rate provider.

Each user has their own account, and every conversion they run is recorded so their most
frequently used currency pairs can be surfaced back to them. To avoid hammering the external
provider with the same lookup over and over, rates are cached in **Redis** behind a short TTL:
the first request for a pair goes out to the provider, and every request that follows within
the TTL is served straight from the cache.

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [How it works](#how-it-works)
- [Project layout](#project-layout)
- [Getting started](#getting-started)
- [API documentation (Swagger)](#api-documentation-swagger)
- [Using the API](#using-the-api)
- [Endpoint reference](#endpoint-reference)
- [Current limitations](#current-limitations)

---

## Features

| Feature | Description |
| --- | --- |
| **Live currency list** | Fetches every currency code supported by the provider. |
| **Conversion** | Converts an amount from one currency to another at the current rate. |
| **Registration & login** | Accounts with BCrypt-hashed passwords (strength 12). |
| **Stateless JWT auth** | Login hands out a one-hour token; every protected call carries it. |
| **Redis rate cache** | Short-TTL caching of currency pairs, so repeat conversions skip the external API. |
| **Per-user frequents** | The pairs a user converts most recently are kept per account in MySQL. |
| **Interactive API docs** | Swagger UI generated from the code with springdoc-openapi. |

---

## Tech stack

- **Java 21**, **Spring Boot 3.5.7**
- **Spring Web** — REST controllers
- **Spring Security** + **jjwt 0.12.6** — authentication and JWT signing
- **Spring Data JPA** + **MySQL** — users, preferences, frequents
- **Redis** (via **Jedis 7**) — rate cache
- **springdoc-openapi 2.9.0** — OpenAPI 3 spec and Swagger UI
- **Spring AOP** — method logging
- **Lombok** — boilerplate reduction

---

## How it works

### Caching

Rate lookups go through the `CurrencyCache` interface, which has two implementations:

| Implementation | TTL | Notes |
| --- | --- | --- |
| `RedisCache` | **10 seconds** per currency pair | Marked `@Primary` — this is the one in use. The currency list is cached without expiry. |
| `InMemoryCache` | 2 hours per currency pair | A plain `HashMap` fallback, kept for local work without a Redis instance. |

A conversion request therefore takes one of two paths:

```
GET /api/compute?from=USD&to=EUR&amount=100
        │
        ├── rate for USD:EUR in Redis and still fresh? ──> return cached rate  (no external call)
        │
        └── otherwise ──> call the external provider ──> store rate in Redis ──> return it
```

**Running without Redis:** remove the `@Primary` annotation from `RedisCache` and put it on
`InMemoryCache` instead — everything else works unchanged.

### Authentication

1. `POST /user/register` creates the account, storing the password BCrypt-hashed.
2. `POST /user/login` verifies the credentials, generates a fresh per-user signing key, and
   returns a JWT **in the `Token` response header** (not in the body).
3. `JWTFilter` intercepts every other request, reads `Authorization: Bearer <token>`, verifies
   the signature against that user's key, and populates the security context.

Tokens expire after **one hour**. Because a new signing key is generated on each login, logging
in again invalidates any token issued earlier.

### Frequents

Each successful conversion is recorded on a background thread so it never delays the response.
Up to **four** pairs are kept per user; once that is full, the least recently used one is
evicted to make room for the new pair.

---

## Project layout

```
Backend/currency_converter/
├── Dockerfile                     # Redis image with the project's redis.conf
├── pom.xml
└── src/main/
    ├── java/com/curr_convert/currency_converter/
    │   ├── CurrencyConverterApplication.java
    │   ├── aspects/       Logger.java              # AOP method logging
    │   ├── configs/       SecurityCfg.java         # filter chain, auth provider
    │   │                  JWTFilter.java           # Bearer-token authentication
    │   │                  OpenAPICfg.java          # Swagger metadata + bearer scheme
    │   ├── controller/    CurrencyController.java  # /api
    │   │                  UserController.java      # /user
    │   │                  ExceptionController.java # global error handling
    │   ├── dto/           CurrencyPair, PrincipleUserDetails
    │   ├── model/         UserPrinciple, UserPreferences, UserFrequents
    │   ├── repo/          JPA repositories
    │   │   └── cache/     CurrencyCache, RedisCache, InMemoryCache
    │   ├── service/       CurrencyService, UserService
    │   │   └── api/       APIConnector.java        # external provider client
    │   └── utils/         ParameterStringBuilder.java
    └── resources/
        └── application.properties.example
```

---

## Getting started

### Prerequisites

- JDK 21 or newer
- Maven 3.9+ (or the bundled `mvnw` wrapper)
- A running MySQL server
- A running Redis server
- An API key from an exchange-rate provider

### 1. Clone

```bash
git clone <repository-url>
cd Currency-Converter/Backend/currency_converter
```

### 2. Create the database

```sql
CREATE DATABASE currency_converter;
```

Tables are created automatically by Hibernate on first start (`ddl-auto=update`).

### 3. Start Redis

Any Redis instance listening on `localhost:6379` will do:

```bash
docker run -d --name currency-redis -p 6379:6379 redis:alpine
```

> **Note:** the `Dockerfile` in this directory builds a Redis image around a custom
> `src/Redis.conf`, but that config file is not checked in — `docker build .` will fail until it
> is added. Use the plain image above in the meantime.

> The Redis host is currently hard-coded to `redis://localhost:6379` in `RedisCache.java`.

### 4. Configure the application

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then fill in your provider API key and database credentials. `*.properties` is git-ignored, so
your real configuration will not be committed.

### 5. Run

```bash
./mvnw spring-boot:run        # Linux / macOS
mvnw.cmd spring-boot:run      # Windows
```

Or build a jar and run it:

```bash
./mvnw clean package
java -jar target/currency_converter-0.0.1-SNAPSHOT.jar
```

The application starts on **http://localhost:8080**.

---

## API documentation (Swagger)

Once the application is running:

| Resource | URL |
| --- | --- |
| **Swagger UI** | <http://localhost:8080/swagger-ui.html> |
| **OpenAPI 3 spec (JSON)** | <http://localhost:8080/v3/api-docs> |
| **OpenAPI 3 spec (YAML)** | <http://localhost:8080/v3/api-docs.yaml> |

Both are open without authentication so the documentation is reachable before you have a token.

### Authorising inside Swagger UI

The protected endpoints need a JWT, and the UI can hold it for you:

1. Expand `POST /user/register` and create an account.
2. Expand `POST /user/login` and execute it with the same credentials.
3. Copy the JWT out of the **`Token` response header** — expand the response headers section
   to see it; it is not in the response body.
4. Click the **Authorize** button at the top right, paste the token, and confirm.

Every subsequent "Try it out" call will carry `Authorization: Bearer <token>` automatically.

---

## Using the API

### Register

```bash
curl -X POST http://localhost:8080/user/register \
     -H "Content-Type: application/json" \
     -d '{"username":"seif","password":"s3cret"}'
```

```
201 Created — "User Created!"
```

### Log in and capture the token

```bash
curl -i -X POST http://localhost:8080/user/login \
     -H "Content-Type: application/json" \
     -d '{"username":"seif","password":"s3cret"}'
```

```
HTTP/1.1 200 OK
Token: eyJhbGciOiJIUzI1NiJ9...     <-- your JWT
"Success!"
```

### List the available currencies

```bash
curl http://localhost:8080/api/ \
     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

```json
["AED","AFN","ALL","AMD","USD","EUR", "..."]
```

### Convert an amount

```bash
curl "http://localhost:8080/api/compute?from=USD&to=EUR&amount=100" \
     -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

```
84.31
```

---

## Endpoint reference

| Method | Path | Auth | Description |
| --- | --- | --- | --- |
| `POST` | `/user/register` | — | Create an account. `201 User Created!` / `400 User Exists!` |
| `POST` | `/user/login` | — | Log in; returns the JWT in the `Token` header. `200` / `401` |
| `GET` | `/api/` | JWT | List every supported currency code. |
| `GET` | `/api/compute` | JWT | Convert `amount` from `from` to `to`. |
| `GET` | `/swagger-ui.html` | — | Interactive API documentation. |
| `GET` | `/v3/api-docs` | — | OpenAPI 3 specification. |

### Error responses

| Status | Body | Meaning |
| --- | --- | --- |
| `401` | *(empty)* | No token sent, or the credentials were wrong. |
| `404` | `User Not Found!` | The username in the token no longer exists. |
| `510` | `Token Expired!` | The JWT is past its one-hour lifetime — log in again. |

---

## Current limitations

Worth knowing before building on top of this:

- **Rates are not live yet.** `APIConnector.getRate()` returns a random number as a placeholder;
  only the currency *list* is really fetched from the provider. The caching, persistence and
  auth layers around it are complete, so wiring in the provider's rate endpoint is a
  self-contained change.
- **Frequents and favourites are stored but not exposed.** `UserFrequents` and `UserPreferences`
  are persisted on every conversion, but `CurrencyService.getUserFrequents` and
  `getUserPreferences` still throw `Not Implemented` and no endpoint reads them yet.
- **The Redis address is hard-coded** in `RedisCache.java` rather than read from configuration,
  and a new `UnifiedJedis` is opened per cache operation.
- **The pair TTL is 10 seconds**, which suits development; a production deployment would want a
  longer, configurable value.
