## VulnGuard

Spring Boot app for tracking vulnerabilities and running simulated scans against tracked system assets.

### What you need
- Java 21
- Maven
- (Optional) PostgreSQL (default fallback is in-memory H2)
- (Optional) Docker + Docker Compose

### Start (local)
1. Go to the module folder:
   - `cd "vulnguard"`
2. Run:
   - `mvn spring-boot:run`
3. Open:
   - Dashboard: `http://localhost:8080/dashboard`
4. Basic auth (for API + dashboard):
   - `admin` / `admin`

### Start (Docker)
1. From repo root:
   - `cd "vulnguard"`
2. Run:
   - `docker compose up --build -d`
3. Open:
   - Dashboard: `http://localhost:8080/dashboard`
   - Grafana: `http://localhost:3000`
   - Prometheus: `http://localhost:9090`

### Main API endpoints (Base path: `/api/v1`)
- Assets: `/api/v1/assets`
- Vulnerabilities: `/api/v1/vulnerabilities`
- Scan reports: `/api/v1/scan-reports`

