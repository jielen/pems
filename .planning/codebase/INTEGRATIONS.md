# External Integrations

**Analysis Date:** 2026-03-28

## APIs & External Services

**None detected** - This is a self-contained management system with no external API integrations.

The application uses only local/embedded services:
- No third-party payment gateways
- No external identity providers (custom JWT-based auth)
- No cloud storage services (local filesystem)
- No SMS/notification external APIs
- No mapping/geolocation services

## Data Storage

**Database:**
- MySQL 8.0+
  - Connection: `jdbc:mysql://localhost:3306/ry-vue` (configured in `application-druid.yml`)
  - ORM Client: MyBatis-Plus 4.0.1
  - Connection Pool: Druid 1.2.28
  - Driver: `com.mysql.cj.jdbc.Driver`
  - Credentials managed via `application-druid.yml` (should be environment variables in production)

**File Storage:**
- Local filesystem
  - Path: `D:/ruoyi/uploadPath` (configurable via `ruoyi.profile`)
  - Used for: user uploads, evidence attachments, photos, videos
  - No cloud storage integration (S3, OSS, etc.)

**Caching:**
- Redis 6.0+
  - Host: `localhost:6379` (configurable via `spring.data.redis.*`)
  - Database index: 0
  - Purpose: Session storage, token caching, general caching
  - Client: Spring Data Redis with Lettuce connection pool
  - Timeout: 10s
  - Pool settings: min-idle=0, max-idle=8, max-active=8

## Authentication & Identity

**Auth Provider:**
- Custom JWT-based authentication
  - Library: `io.jsonwebtoken:jjwt 0.9.1`
  - Token header: `Authorization`
  - Token expiry: 30 minutes (configurable)
  - Token secret: configured in `token.secret` (dev default)

**Captcha:**
- Kaptcha 2.3.3 (self-hosted)
  - Types: `math` (default) or `char`
  - Configured via `ruoyi.captchaType`

**Permission System:**
- Custom annotation-based (`@PreAuthorize`)
- Permission format: `module:entity:operation` (e.g., `system:user:add`)
- No external IAM/SSO integration

## Monitoring & Observability

**Error Tracking:**
- None detected (no Sentry, Bugsnag, or similar)

**Logs:**
- Spring Boot logging (configured in `application.yml`)
- Log levels: `com.ruoyi: debug`, `org.springframework: warn`
- Operation logs stored in database (`sys_oper_log` table)
- Login logs stored in database (`sys_logininfor` table)

**Performance Monitoring:**
- Druid statViewServlet enabled at `/druid/*`
  - Default credentials: `ruoyi:123456`
  - Provides: SQL monitoring, connection pool stats, slow SQL detection

**System Monitoring:**
- Server info via Oshi library (CPU, memory, disk)
- `ServerController` exposes server metrics

## CI/CD & Deployment

**Hosting:**
- Self-hosted on premise (government/enterprise environment)
- Spring Boot embedded Tomcat
- No cloud platform detected (no AWS, Azure, GCP integrations)

**CI Pipeline:**
- None detected in repository
- Maven for backend builds
- npm for frontend builds

**Containerization:**
- None detected (no Dockerfile, docker-compose.yml)

## Environment Configuration

**Required env vars (backend):**
- None explicitly required - all config in `application.yml` and `application-druid.yml`
- Database credentials in config files (should be environment variables)
- Redis credentials in config files (password optional, empty by default)

**Required env vars (frontend):**
- `VITE_APP_TITLE` - Application title
- `VITE_APP_ENV` - Environment (development/production)
- `VITE_APP_BASE_API` - Backend API base path

**Frontend env files:**
- `.env.development` - Dev settings (proxies to localhost:8080)
- `.env.production` - Production settings (VITE_BUILD_COMPRESS=gzip)
- `.env.staging` - Staging settings

## Webhooks & Callbacks

**Incoming:**
- None detected
- No webhook receivers configured
- No REST callbacks to external services

**Outgoing:**
- None detected
- No outbound webhook calls
- No external service integrations requiring callbacks

## Security Considerations

**Detected:**
- XSS filter enabled (`xss.enabled: true`)
- CSRF protection via referer filtering (configurable)
- Rate limiting annotations available (`@RateLimiter`)
- Password locking after failed attempts (5 max, 10 min lock)
- JWT tokens for stateless authentication
- Kaptcha for bot prevention

**Missing/At Risk:**
- Database credentials hardcoded in config files (should use vault/secrets manager)
- JWT secret hardcoded in config (should be environment variable)
- Druid console credentials hardcoded (should be changed in production)
- No external security scanning in CI pipeline
- No intrusion detection integration

---

*Integration audit: 2026-03-28*
