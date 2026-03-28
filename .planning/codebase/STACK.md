# Technology Stack

**Analysis Date:** 2026-03-28

## Languages

**Primary:**
- Java 17 - Backend API and business logic
- TypeScript 5.6.3 - Frontend (Vue3 with Vite)
- JavaScript - Frontend (Vue2 legacy, Node build tools)

**Secondary:**
- Vue 3.5.26 - Primary frontend framework (pems_frontend)
- Vue 2.6.12 - Legacy frontend (ruoyi-ui)
- SCSS/Sass 1.97.2 - CSS preprocessing

## Runtime

**Backend:**
- JVM 17 - Java runtime

**Frontend:**
- Node.js (for build/dev server only)
- npm 3.0+ (package management)

**Package Manager:**
- Maven - Backend dependency management
- npm - Frontend dependency management
- Lockfile: package-lock.json (implied by npm)

## Frameworks

**Backend Core:**
- SpringBoot 4.0.3 - Application framework
- MyBatis-Plus 4.0.1 - ORM layer
- Druid 1.2.28 - Database connection pool

**Backend Utilities:**
- PageHelper 2.1.1 - Pagination
- Fastjson2 2.0.61 - JSON serialization
- POI 4.1.2 - Excel import/export
- Kaptcha 2.3.3 - Captcha generation
- SpringDoc 3.0.2 - OpenAPI/Swagger documentation
- Oshi 6.10.0 - System information monitoring
- YAUAA 8.1.0 - User agent parsing

**Frontend (pems_frontend - Vue3/Vite):**
- Vue 3.5.26 - Framework
- Vite 6.4.1 - Build tool
- Vue Router 4.6.4 - Routing
- Pinia 3.0.4 - State management
- Element Plus 2.13.1 - UI component library
- Axios 1.13.2 - HTTP client
- ECharts 5.6.0 - Charts
- Sass 1.97.2 - CSS preprocessor

**Frontend (ruoyi-ui - Vue2/Legacy):**
- Vue 2.6.12 - Framework
- Element UI 2.15.14 - UI component library
- Vuex 3.6.0 - State management
- axios 0.28.1 - HTTP client
- Vue CLI 4.4.6 - Build tool

**Testing:**
- JUnit (Spring Boot test, implied)
- vue-test-utils (Vue2, legacy frontend)

**Build/Dev:**
- Maven 3.13.0+ (backend compile/package)
- Vite 6.4.1 (frontend dev/build)
- Vue CLI 4.4.6 (legacy frontend)

## Key Dependencies

**Critical:**
- `com.alibaba.fastjson2:fastjson2 2.0.61` - JSON processing
- `com.baomidou:mybatis-plus 4.0.1` - ORM
- `com.alibaba:druid 1.2.28` - Connection pooling
- `io.jsonwebtoken:jjwt 0.9.1` - Token authentication
- `org.springdoc:springdoc 3.0.2` - API documentation

**Infrastructure:**
- MySQL 8.0+ - Primary database
- Redis 6.0+ - Caching and session storage (Spring Data Redis)
- `com.github.pagehelper:pagehelper 2.1.1` - Database pagination

**File Processing:**
- `org.apache.poi:poi-ooxml 4.1.2` - Excel export/import
- `commons-io:commons-io 2.21.0` - IO utilities

**Monitoring:**
- `nl.basjes.parse.useragent:yauaa 8.1.0` - Browser/device detection
- `com.github.oshi:oshi-core 6.10.0` - Server metrics

## Configuration

**Environment:**
- Spring profiles: `druid` (primary datasource config)
- Frontend env vars via `.env.development`, `.env.production`, `.env.staging`
- Backend config: `ruoyi-admin/src/main/resources/application.yml`
- Database config: `ruoyi-admin/src/main/resources/application-druid.yml`

**Key configs:**
- `server.port: 8080` - Backend HTTP port
- `ruoyi.profile: D:/ruoyi/uploadPath` - File upload directory
- `spring.data.redis.*` - Redis connection settings
- `token.secret: abcdefghijklmnopqrstuvwxyz` - JWT signing key (dev only, should be env var)
- `token.expireTime: 30` - Token expiry in minutes
- `captchaType: math` - Captcha type (math/char)
- `druid.datasource.master.url` - MySQL connection string

**Build:**
- Maven multi-module project with 6 modules
- Frontend has two separate Vue apps: `pems_frontend` (Vue3) and `ruoyi-ui` (Vue2)

## Platform Requirements

**Development:**
- Java 17+
- Node.js 18+ (for pems_frontend)
- Node.js 8.9+ (for ruoyi-ui legacy)
- MySQL 8.0+
- Redis 6.0+

**Production:**
- Java 17 runtime
- MySQL 8.0+ database
- Redis 6.0+ server
- Servlet container (embedded Tomcat via Spring Boot)
- Static file server or CDN for frontend assets

---

*Stack analysis: 2026-03-28*
