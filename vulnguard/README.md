# VulnGuard

VulnGuard is a Spring Boot 3.x (Java 21 LTS) application for tracking and analyzing security vulnerabilities across system assets.

## Tech Stack

- Spring Boot 3.x
- Spring Web (REST)
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Jsoup (vulnerability feed scraping)
- Spring Security (Basic auth)


## Domain Model

- **Vulnerability**: ID, title, description, severity score (0.0–10.0), published date.
- **SystemAsset**: ID, hostname, IP address, OS, importance level.
- **ScanReport**: ID, status, timestamp, linked to a single asset and many vulnerabilities.

Relationships:

- One `SystemAsset` → many `ScanReport`.
- Many `ScanReport` ↔ many `Vulnerability`.

## Running Locally

1. Ensure PostgreSQL is running and create a database:

   ```sql
   CREATE DATABASE vulnguard;
   ```

2. Adjust credentials in `src/main/resources/application.properties`:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/vulnguard
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```

3. Build and run:

   ```bash
   mvn spring-boot:run
   ```

4. Access the dashboard:

   - `http://localhost:8080/dashboard`

## Key REST Endpoints

- **Vulnerabilities**
  - `GET /api/v1/vulnerabilities`
  - `GET /api/v1/vulnerabilities/{id}`
  - `POST /api/v1/vulnerabilities`
  - `PUT /api/v1/vulnerabilities/{id}`
  - `DELETE /api/v1/vulnerabilities/{id}`
  - `POST /api/v1/vulnerabilities/scrape-latest` – uses Jsoup to scrape a simplified vulnerability feed and persist results.

- **System Assets**
  - `GET /api/v1/assets`
  - `GET /api/v1/assets/{id}`
  - `POST /api/v1/assets`
  - `PUT /api/v1/assets/{id}`
  - `DELETE /api/v1/assets/{id}`

- **Scan Reports**
  - `GET /api/v1/scan-reports`
  - `GET /api/v1/scan-reports/{id}`
  - `POST /api/v1/scan-reports`
  - `PUT /api/v1/scan-reports/{id}`
  - `DELETE /api/v1/scan-reports/{id}`

## Error Handling

- 404 responses use a structured JSON body when entities are not found.
- Validation errors on DTOs return 400 with field-level messages.

## Recent Improvements

- Migrated project to **Java 21** (LTS) – compile and run targets updated in `pom.xml`.
- Introduced mapper classes to separate DTO conversion logic.
- Added validation checks in services to surface missing foreign keys early.
- Computed `currentSecurityStatus` in domain model and reused in DTOs.
- Added global converters for enum binding via `WebConfig`.
- Added unit tests for mappers and service behavior.


## Docker (Run with Docker Compose)

Authentication: the application is protected with HTTP Basic; default user `admin` / `admin`.


The project includes a `docker-compose.yml` that starts a PostgreSQL database, the application, Prometheus and Grafana.

Quick start (builds the app image and starts services):

```bash
cd "/Users/rodion/vs code/vulnguard"
docker compose up --build -d
```

Check logs for the application:

```bash
docker compose logs --tail=200 app
```

Notes:
- The Compose file supplies `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD` so the containerized app connects to Postgres.
- For quick local development without Postgres, the app falls back to an in-memory H2 database when `SPRING_DATASOURCE_URL` is not provided.

Container details:
- The `app` service has a healthcheck that verifies `/actuator/health` to ensure the application is responsive.
- The `app` service uses a restart policy `unless-stopped` so it recovers from transient failures during local development.

Security note:
- The image currently runs the JVM process as the container user defined by the base image. For production, consider running as a non-root user and using a smaller base image (e.g., a distroless runtime) for better security.

