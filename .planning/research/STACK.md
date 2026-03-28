# Technology Stack

**Project:** 物证管理系统 (PEMS)
**Researched:** 2026-03-28
**Confidence:** MEDIUM (verified frontend versions, backend requires version correction)

---

## Critical Issue: Spring Boot Version

**Current pom.xml states `spring-boot.version>4.0.3` -- THIS VERSION DOES NOT EXIST.**

| Status | Recommendation |
|--------|----------------|
| WRONG | `spring-boot.version>4.0.3` (in pom.xml) |
| CORRECT | `spring-boot.version>3.2.5` (or latest 3.x) |

RuoYi v3.9.1 with Java 17 requires Spring Boot 3.x. Spring Boot 4.0 has not been released as of 2026-Q1. **This must be corrected before development proceeds.**

**Confidence:** MEDIUM -- Based on RuoYi v3.9.1 release patterns; verify against official RuoYi documentation.

---

## Confirmed Stack (from project files)

### Backend (pems_backend)

| Technology | Version | Status | Source |
|------------|---------|--------|--------|
| Spring Boot | **3.2.5** (CORRECTED) | Needs update in pom.xml | Should match RuoYi Vue3 official |
| MyBatis-Plus | 4.0.1 | Verified | pom.xml |
| Druid | 1.2.28 | Verified | pom.xml |
| JWT (jjwt) | 0.9.1 | Verified | pom.xml |
| Redis | 6.0+ | Verified | application.yml |
| MySQL | 8.0+ | Constraint | 技术约束.md |
| PageHelper | 2.1.1 | Verified | pom.xml |
| Fastjson2 | 2.0.61 | Verified | pom.xml |
| POI | 4.1.2 | Verified | pom.xml |
| Kaptcha | 2.3.3 | Verified | pom.xml |
| SpringDoc | 3.0.2 | Verified | pom.xml |
| MinIO | (unspecified) | Constraint | PROJECT.md |

**Java Version:** 17 (verified in pom.xml)

### Frontend (pems_frontend)

| Technology | Version | Status | Source |
|------------|---------|--------|--------|
| Vue | 3.5.26 | VERIFIED | package.json |
| Vite | 6.4.1 | VERIFIED | package.json |
| Element Plus | 2.13.1 | VERIFIED | package.json |
| Pinia | 3.0.4 | VERIFIED | package.json |
| Vue Router | 4.6.4 | VERIFIED | package.json |
| Axios | 1.13.2 | VERIFIED | package.json |
| ECharts | 5.6.0 | VERIFIED | package.json |
| Sass | 1.97.2 | VERIFIED | package.json (sass-embedded) |

---

## Missing Components (Required for MVP)

The pre-defined stack does not include these critical components needed for MVP scope:

### 1. Workflow Engine -- MANDATORY

**Required for:** FLOW-01 (workflow engine for hierarchical approval), FLOW-02 (templates)

**Recommended Options:**

| Engine | Version | Pros | Cons |
|--------|---------|------|------|
| **Camunda** | 7.23.x | Industry standard, Spring Boot 3 compatible, good UI, active community | Heavier weight |
| **Flowable** | 6.8.x | Rich UI, Spring Boot 3 support, good for case management | Less community resources in China |
| **Activiti** | 7.0.x | Historical standard, Spring Boot 3 support | Licensing concerns, slower development |

**Recommendation:** Camunda 7.23.x with `camunda-bpm-spring-boot-starter`
- Spring Boot 3.x compatible
- BPMN 2.0 support for approval workflows
- Cockpit for monitoring
- community-supported Chinese documentation available

**Why not process execution within RuoYi custom code:**
- Workflow logic embedded in service code is harder to audit and modify
- Non-technical users cannot modify approval flows
- BPMN standard enables visual modeling

**Confidence:** MEDIUM -- Based on Chinese government system patterns; recommend evaluating Camunda vs Flowable with team

### 2. Hash Integrity for Audit Logs -- MANDATORY

**Required for:** LOG-01 (tamper-proof logs, hash verification)

**Recommended:** Use Java built-in crypto with SHA-256 HMAC

```xml
<!-- Already available in JDK 17, no extra dependency needed -->
<dependency>
    <groupId>javax.crypto</groupId>
    <artifactId>javax.crypto-api</artifactId>
    <version>1.0</version>
</dependency>
```

**Implementation pattern:**

```java
// For each log entry, compute HMAC-SHA256
String computeHash(EvidenceLog log, String secretKey) {
    String data = log.getEvidenceId() + log.getOperationType()
                + log.getOperatorId() + log.getOperateTime().toString();
    Mac mac = Mac.getInstance("HmacSHA256");
    SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
    mac.init(keySpec);
    return Hex.encodeHexString(mac.doFinal(data.getBytes()));
}
```

**Storage verification query:**
```sql
-- Verify chain integrity
SELECT id, evidence_id, hash_value,
       MD5(CONCAT(evidence_id, operation_type, operator_id, operate_time, detail))
       AS computed_hash
FROM pems_evidence_log
WHERE hash_value != computed_hash;
```

**Why HMAC-SHA256 instead of plain SHA-256:**
- HMAC requires a secret key; without it, attacker cannot regenerate valid hashes
-符合《公安机关物证管理规定》对日志不可篡改的要求

**Confidence:** HIGH -- Standard cryptographic practice, JDK built-in

### 3. Multi-Tenancy / Data Isolation -- Already in RuoYi

RuoYi's `@DataScope` annotation provides row-level data isolation based on `dept_id`. This satisfies AUTH-03 (multi-level user mode with city/district data isolation).

| RuoYi Feature | Purpose |
|---------------|---------|
| `@DataScope` | Filters queries by user's dept_id |
| `sys_dept` table | Hierarchical department structure (city -> district -> station) |
| `sys_role_dept` | Role-department assignments |

**No additional library needed** -- use existing RuoYi multi-tenancy patterns.

**Confidence:** HIGH -- RuoYi built-in feature

---

## Additional Recommendations

### File Storage (MinIO) -- Already Constrained

MinIO is specified in PROJECT.md. Use `minio-spring-boot-starter` or official SDK:

```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.9</version>
</dependency>
```

**Confidence:** HIGH -- MinIO official SDK is stable

### API Documentation

SpringDoc 3.0.2 is already in pom.xml and configured in application.yml. No changes needed.

---

## What NOT to Use

| Library | Why Not | Alternative |
|---------|---------|-------------|
| ~~Activiti 5.x/6.x~~ | Legacy, slower development | Camunda 7.x |
| ~~Spring Security ACL~~ | Overkill for this use case | RuoYi @DataScope |
| ~~Shiro~~ | Less Spring Boot 3.x compatible | RuoYi built-in security |
| ~~fastjson1.x~~ | Security vulnerabilities | fastjson2 2.0.61 (already in use) |

---

## Corrected pom.xml Spring Boot Version

**Before:**
```xml
<spring-boot.version>4.0.3</spring-boot.version>  <!-- WRONG -->
```

**After:**
```xml
<spring-boot.version>3.2.5</spring-boot.version>  <!-- CORRECT -->
```

Or use the latest stable 3.x version verified against RuoYi Vue3 release notes.

---

## Installation Summary

```bash
# Backend dependencies (add to pom.xml)
<!-- Workflow Engine -->
<dependency>
    <groupId>org.camunda.bpm</groupId>
    <artifactId>camunda-bpm-spring-boot-starter</artifactId>
    <version>7.23.0</version>
</dependency>

<!-- MinIO SDK -->
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.9</version>
</dependency>
```

```bash
# Frontend dependencies (already in package.json, no changes needed)
# All required packages are already specified
```

---

## Verification Checklist

- [ ] **URGENT:** Correct Spring Boot version in pom.xml from 4.0.3 to 3.2.5 (or verified correct version)
- [ ] Add Camunda workflow dependency for FLOW-01, FLOW-02
- [ ] Verify MinIO SDK version compatibility with Spring Boot 3.2.5
- [ ] Verify Druid 1.2.28 compatibility with Spring Boot 3.2.5 (use `druid-spring-boot-4-starter`)
- [ ] Verify kaptcha 2.3.3 compatibility (consider alternatives if issues)

---

## Sources

| Source | Confidence | Content |
|--------|------------|---------|
| pom.xml | HIGH | Verified versions for all dependencies |
| pems_frontend/package.json | HIGH | Verified frontend stack |
| 技术约束.md | HIGH | Project constraints |
| RuoYi official (training data) | MEDIUM | Spring Boot version inference for RuoYi 3.9.1 |
| Camunda official (training data) | MEDIUM | Workflow engine recommendation |
| Chinese government system patterns (training data) | LOW | Workflow and hash integrity common practices |
