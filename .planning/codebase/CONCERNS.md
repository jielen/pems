# Codebase Concerns

**Analysis Date:** 2026-03-28

## Tech Debt

### PEMS Business Logic Not Implemented
- **Issue:** CLAUDE.md documents complete `com.ruoyi.pems/` module structure (controller, service, mapper, domain, enums) but none of these packages exist in the codebase.
- **Files:** None exist - only framework modules present
- **Impact:** The physical evidence management system has no implementation. Only the RuoYi admin framework exists.
- **Fix approach:** Implement PEMS modules following the documented architecture in CLAUDE.md.

### Large Utility Classes
- **Issue:** Several utility classes exceed reasonable size limits, violating the "many small files" convention.
- **Files:**
  - `ruoyi-common/src/main/java/com/ruoyi/common/utils/poi/ExcelUtil.java` - 1956 lines
  - `ruoyi-common/src/main/java/com/ruoyi/common/core/text/Convert.java` - 1018 lines
  - `ruoyi-common/src/main/java/com/ruoyi/common/utils/StringUtils.java` - 920 lines
  - `ruoyi-common/src/main/java/com/ruoyi/common/utils/html/HTMLFilter.java` - 569 lines
- **Impact:** Difficult to maintain, test, and understand. High coupling.
- **Fix approach:** Split into focused utility classes by feature (ExcelReader, ExcelWriter, DateConverter, StringValidator, etc.).

### Dual Frontend Structure
- **Issue:** Two separate frontend directories exist with inconsistent tech stacks.
- **Files:**
  - `ruoyi-ui/` - Vue 2.6.12 (Element UI 2.15.14) - older stack
  - `pems_frontend/` - Vue 3.5.26 (Element Plus 2.13.1) - newer stack
- **Impact:** Confusion on which to use, doubled maintenance effort, inconsistent UI.
- **Fix approach:** Consolidate on pems_frontend (Vue3) and deprecate ruoyi-ui.

### Unfinished Module Structure
- **Issue:** Backend modules exist only for framework (system, common, framework, admin, generator, quartz). No business modules.
- **Impact:** System cannot manage evidence without implementing business modules.
- **Fix approach:** Create `ruoyi-pems/` module following the package structure in CLAUDE.md.

## Known Bugs

### SysUserOnlineServiceImpl Null Returns
- **Issue:** Service implementation contains multiple stubbed methods returning null.
- **Files:** `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUserOnlineServiceImpl.java` (lines 31, 48, 66, 80)
- **Symptoms:** Online user queries may return null instead of empty results.
- **Trigger:** Calling selectOnlineByIpaddr, selectOnlineByUserName, selectOnlineByInfo, or loginUserToUserOnline with non-matching criteria.
- **Workaround:** Null-check results before use.

### TestController Appears to be Demo Code
- **Issue:** Only test file found appears to be a placeholder, not actual unit tests.
- **Files:** `ruoyi-admin/src/main/java/com/ruoyi/web/controller/tool/TestController.java`
- **Impact:** No regression protection, no TDD possible.
- **Workaround:** Write actual unit tests for all service implementations.

## Security Considerations

### SQL Injection Blacklist Approach
- **Risk:** SqlUtil uses a blacklist regex pattern for SQL injection prevention.
- **Files:** `ruoyi-common/src/main/java/com/ruoyi/common/utils/sql/SqlUtil.java` (line 16)
- **Current mitigation:** SQL_REGEX pattern filters common SQL keywords.
- **Recommendations:**
  - Replace blacklist with parameterized queries where possible
  - The `${params.dataScope}` in MyBatis XML is built server-side in DataScopeAspect - acceptable
  - The `${sql}` in GenTableMapper.xml line 175 is mitigated by filterKeyword() but still risky
  - Consider using MyBatis-Plus's built-in SQL injection prevention

### Code Generator SQL Execution
- **Risk:** GenController.createTableSave() allows arbitrary SQL execution via the code generator.
- **Files:**
  - `ruoyi-generator/src/main/resources/mapper/generator/GenTableMapper.xml` (line 175: `${sql}`)
  - `ruoyi-generator/src/main/java/com/ruoyi/generator/controller/GenController.java` (lines 130-159)
- **Current mitigation:** filterKeyword() checks for dangerous keywords before execution.
- **Recommendations:** Restrict code generator to admin-only access, consider removing from production.

### Overly Permissive CORS
- **Risk:** API returns `Access-Control-Allow-Origin: *` allowing cross-origin requests from any domain.
- **Files:** `ruoyi-generator/src/main/java/com/ruoyi/generator/controller/GenController.java` (line 257)
- **Current mitigation:** Requires authentication for most endpoints.
- **Recommendations:** Restrict CORS to known frontend origins instead of wildcard.

### Swagger/OpenAPI Documentation Publicly Accessible
- **Risk:** API documentation exposed at `/swagger-ui.html` and `/v3/api-docs/**`.
- **Files:** `ruoyi-admin/src/main/java/com/ruoyi/web/core/config/SwaggerConfig.java`
- **Current mitigation:** Documentation endpoints in permitAll list - intended for internal use.
- **Recommendations:** Protect Swagger endpoints behind authentication or remove in production.

### JWT Version 0.9.1
- **Risk:** Using outdated JWT library (jjwt 0.9.1 from 2015) with known vulnerabilities.
- **Files:** `pom.xml` (line 31), `ruoyi-framework/src/main/java/com/ruoyi/framework/web/service/TokenService.java`
- **Current mitigation:** Token is signed with secret key configured in properties.
- **Recommendations:** Upgrade to jjwt 0.12.x which has better security and API.

### Swagger API Key in Header
- **Risk:** Authorization header used for API key authentication is logged in access logs.
- **Files:** `ruoyi-admin/src/main/java/com/ruoyi/web/core/config/SwaggerConfig.java` (line 44)
- **Recommendations:** Use OAuth2 or other token-based auth that supports token rotation.

## Performance Bottlenecks

### ExcelUtil Memory Usage
- **Problem:** 1956-line Excel utility may cause OutOfMemoryError with large Excel files.
- **Files:** `ruoyi-common/src/main/java/com/ruoyi/common/utils/poi/ExcelUtil.java`
- **Cause:** Loads entire Excel into memory via HSSFWorkbook/XSSFWorkbook.
- **Improvement path:** Use streaming approach with SXSSFWorkbook for large exports (already imported but not used).

### DataScopeAspect N+1 Query Pattern
- **Problem:** Data scope filtering may execute additional queries for each role.
- **Files:** `ruoyi-framework/src/main/java/com/ruoyi/framework/aspectj/DataScopeAspect.java` (lines 96-101)
- **Cause:** For each role with DATA_SCOPE_CUSTOM, executes separate query for scopeCustomIds.
- **Improvement path:** Batch query all custom role IDs in single query.

### Missing Database Indexes
- **Concern:** No explicit index recommendations found in SQL scripts.
- **Files:** `sql/` directory exists but may lack optimization for evidence queries.
- **Impact:** Evidence searches (by code, case number, status) may degrade with large tables.
- **Improvement path:** Add composite indexes on pems_evidence for (evidence_code), (case_no), (status).

## Fragile Areas

### DataScope Dynamic SQL Building
- **Files:** `ruoyi-system/src/main/resources/mapper/system/SysUserMapper.xml` (lines 86, 103, 121)
- **Why fragile:** Uses `${params.dataScope}` which is built dynamically. If DataScopeAspect logic changes, queries may break silently.
- **Safe modification:** Only modify through DataScopeAspect, never directly in XML.
- **Test coverage:** None detected.

### BaseEntity Params Map
- **Files:** `ruoyi-common/src/main/java/com/ruoyi/common/core/domain/BaseEntity.java`
- **Why fragile:** Uses generic Map<String, Object> for params, allowing arbitrary keys. No type safety.
- **Safe modification:** Create typed parameter classes (DataScopeParams, DateRangeParams).

### XssHttpServletRequestWrapper
- **Files:** `ruoyi-common/src/main/java/com/ruoyi/common/filter/XssHttpServletRequestWrapper.java`
- **Why fragile:** XSS filter may miss edge cases in user input, depends on regex patterns.
- **Safe modification:** Use framework-provided sanitization, validate on boundaries.

## Scaling Limits

### Redis as Single Point
- **Current capacity:** Single Redis instance configured.
- **Limit:** Redis failure causes complete auth failure.
- **Scaling path:** Configure Redis Sentinel or Cluster for high availability.

### File Upload Storage
- **Current capacity:** Local filesystem storage configured.
- **Limit:** No horizontal scaling, local disk limits.
- **Scaling path:** Migrate to object storage (OSS, MinIO) for cloud-native deployment.

### JWT Token Refresh
- **Current capacity:** 30-minute default token expiry.
- **Limit:** Users logged out after token expiry even if session is valid.
- **Scaling path:** Implement token refresh mechanism with sliding window.

## Dependencies at Risk

| Package | Version | Risk | Impact | Migration Plan |
|---------|---------|------|--------|---------------|
| jjwt | 0.9.1 | Outdated, known CVEs | Token forgery possible | Upgrade to 0.12.x |
| poi | 4.1.2 | Older version | Memory issues with large files | Upgrade to 4.1.3 |
| vue | 2.6.12 (ruoyi-ui) | EOL | Security patches unavailable | Migrate to pems_frontend (Vue3) |
| element-ui | 2.15.14 (ruoyi-ui) | Older version | Compatibility issues | Use Element Plus in pems_frontend |

## Missing Critical Features

### Evidence Management Core
- **Problem:** No evidence CRUD operations, status transitions, or chain-of-custody tracking.
- **Blocks:** Cannot use system for its intended purpose.

### Authentication Enhancement
- **Problem:** No two-factor authentication, no session management UI, no password policy enforcement.
- **Blocks:** Cannot meet security requirements for police evidence systems.

### Audit Logging for Evidence
- **Problem:** System has generic operlog but no evidence-specific audit trail.
- **Blocks:** Cannot meet "全程留痕" (full audit trail) requirement in CLAUDE.md.

### Dual Lock Mechanism
- **Problem:** CLAUDE.md describes dual-lock for out/destroy operations but not implemented.
- **Blocks:** Cannot enforce two-person integrity for sensitive operations.

## Test Coverage Gaps

### Backend Unit Tests
- **What's not tested:** All service implementations, mappers, controllers.
- **Files:** Only `TestController.java` exists, no actual unit tests.
- **Risk:** Any refactoring or bug fix could break functionality undetected.
- **Priority:** HIGH - Required before any production deployment.

### Frontend Tests
- **What's not tested:** All Vue components, composables, utilities.
- **Files:** No `.spec.ts` or `.test.ts` files found.
- **Risk:** UI changes can introduce regressions silently.
- **Priority:** HIGH - Required before any production deployment.

### Integration Tests
- **What's not tested:** Database operations, Redis caching, JWT authentication flow.
- **Risk:** End-to-end workflows may fail in production.
- **Priority:** MEDIUM - Important for evidence management workflows.

### Security Tests
- **What's not tested:** SQL injection, XSS, CSRF, authentication bypass.
- **Risk:** Security vulnerabilities may go undetected.
- **Priority:** HIGH - Critical for police evidence system.

---

*Concerns audit: 2026-03-28*
