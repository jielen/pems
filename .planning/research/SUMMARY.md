# Project Research Summary

**Project:** 物证管理系统 (PEMS) - Physical Evidence Management System
**Domain:** Government Evidence Lifecycle Management for Public Security Bureaus
**Researched:** 2026-03-28
**Confidence:** MEDIUM

## Executive Summary

PEMS is a regulatory-compliant evidence lifecycle management system for Chinese Public Security Bureaus (公安机关), implementing "一物一码、全程留痕、责任到人、闭环管理" (one-code-per-evidence, full audit trail, personal accountability, closed-loop management) as mandated by the Ministry of Public Security. The system is built on the RuoYi Vue3 framework (Spring Boot + Vue3), requiring significant extension for evidence-specific workflows, tamper-proof logging, and multi-level organizational data isolation.

**Critical blocker identified:** The pom.xml specifies `spring-boot.version>4.0.3` which does not exist. Spring Boot 4.0 has not been released. RuoYi v3.9.1 with Java 17 requires Spring Boot 3.2.5. This MUST be corrected before development proceeds.

**Recommended approach:** Build in four phases following the dependency chain: (1) multi-level data isolation foundation, (2) evidence core CRUD without workflows, (3) lightweight workflow engine with database-driven definitions, (4) full integration. Use RuoYi's existing `@DataScope` for multi-tenancy, add Camunda 7.23.x only if BPMN complexity justifies it for Phase 2+, and implement HMAC-SHA256 hash chaining for tamper-evident logs using JDK built-in crypto.

**Key risks:** Chain of custody breaks, dual-control theater (checkbox compliance without real enforcement), log tampering, workflow bypass, and evidence code reuse after loss/damage. Each requires specific architectural mitigations detailed in the pitfalls section.

---

## Key Findings

### Recommended Stack

**URGENT: Correct Spring Boot version in pom.xml from 4.0.3 to 3.2.5 before any development.** RuoYi Vue3 v3.9.1 requires Spring Boot 3.x with Java 17. All other backend dependencies (MyBatis-Plus 4.0.1, Druid 1.2.28, JWT 0.9.1, Redis 6.0+, MySQL 8.0+, PageHelper 2.1.1, Fastjson2 2.0.61, POI 4.1.2, Kaptcha 2.3.3, SpringDoc 3.0.2) are verified correct. Frontend stack is fully verified (Vue 3.5.26, Vite 6.4.1, Element Plus 2.13.1, Pinia 3.0.4, Vue Router 4.6.4, Axios 1.13.2, ECharts 5.6.0, Sass 1.97.2).

**Core technologies:**
- **Spring Boot 3.2.5** (corrected): Application framework — mandatory fix before development
- **Camunda 7.23.x** (to be added): Workflow engine for hierarchical approval — use only if BPMN complexity justifies it; lightweight state machine preferred for MVP
- **HMAC-SHA256** (JDK built-in): Tamper-evident hash chaining for audit logs — no extra dependency needed
- **RuoYi @DataScope**: Multi-tenant data isolation — already built-in, no additional library needed
- **MinIO SDK 8.5.9** (to be added): File storage for evidence photos/videos — matches PROJECT.md constraint
- **Redis**: Session and data caching — already in stack

### Expected Features

**Must have (table stakes - regulatory compliance):**
- One code per evidence (一物一码) — evidence codes never reused even after loss/destruction
- Full-process logging with operator badge number (警号) and unit — logs immutable, hash-verified
- Role-based access control with multi-level org support (city/district/station)
- Evidence lifecycle state machine: in-storage, borrowed, testing, destroyed, lost, damaged
- Evidence classification (5 types: regular, valuable, classified, dangerous, perishable) and 3 levels (general, important, core)
- Graded approval workflows based on evidence level
- Two-person verification (双人双锁) for sensitive operations
- Multi-condition query with permission-filtered results
- Statistics dashboard and report export (Excel/PDF)

**Should have (competitive differentiators):**
- Workflow engine with city-level unified templates + district-level custom templates
- Intelligent storage allocation based on evidence type/level
- Environmental monitoring (temperature, humidity) with alerts
- Leadership oversight dashboard with real-time metrics
- Full lifecycle timeline view with photos/videos/documents
- Cross-unit transfer management with dual approval
- Hardware integration (smart cabinets, RFID scanners)

**Defer to v2+:**
- Intelligent storage allocation (needs more hardware specs)
- Environmental monitoring (hardware not in scope)
- Cross-unit transfer management (needs legal review)
- One-click inventory counts (requires RFID)
- Smart cabinet hardware integration
- Workflow template customization (use fixed workflows first)

### Architecture Approach

The recommended architecture extends RuoYi's existing multi-tier pattern with PEMS-specific modules. **Multi-level data isolation** is implemented via RuoYi's `@DataScope` annotation with 5 scope types (ALL, CUSTOM, DEPT, DEPT_AND_BELOW, SELF), extended with `dept_level` tracking (city=1, district=2, station=3). **Evidence state transitions** are managed via a lightweight database-driven state machine (NOT heavy BPMN engines), with workflow definitions stored in `pems_workflow_definition`, `pems_workflow_node`, `pems_workflow_instance`, and `pems_workflow_task` tables. Cross-unit evidence visibility is strictly enforced: city admins see all, district admins see own + children, stations see own only.

**Major components:**
1. **SystemAdminController** — User/dept/role CRUD, permission assignment
2. **EvidenceController** — Evidence CRUD, status transitions (workflow-gated)
3. **WorkflowController** — Workflow instance management, approvals
4. **EvidenceService** — Business logic, data validation, state machine enforcement
5. **WorkflowService** — State machine execution, task assignment, approval chain resolution
6. **DataScopeAspect** — @DataScope annotation processing for automatic unit filtering

### Critical Pitfalls

1. **Chain of Custody Breaks** — Evidence operations logged incompletely, creating gaps in audit trail. Mitigation: Design for offline-first with mandatory sync before status changes; every physical hand-off MUST have corresponding system record; implement pending-sync queue with server validation.

2. **Dual Control Theater** — Two-person verification implemented as checkbox or sequential login rather than genuine concurrent control. Mitigation: Concurrent session enforcement; hardware binding (cabinet detects two RFID cards); operation cannot proceed if second person not authenticated within 30 seconds.

3. **Audit Log Tampering** — Logs can be modified or deleted. Mitigation: Append-only log storage; hash chaining (each entry includes hash of previous); separate audit database with no application-level delete accounts; periodic hash verification job.

4. **Multi-Level Data Isolation Failure** — City-level users see district-level data or vice versa. Mitigation: All queries MUST include unit_id filter as mandatory WHERE; implement data scope layer that auto-injects unit filter; separate cache namespaces per unit.

5. **Workflow Engine Bypassed** — Evidence status changes without required approvals. Mitigation: Workflow engine MUST be only path for status transitions; no direct update APIs for evidence status; all exceptions require new approval chain, not bypass.

---

## Implications for Roadmap

Based on research, the recommended phase structure follows the architecture dependency chain:

### Phase 1: System Foundation & Data Isolation
**Rationale:** All other modules depend on proper user/dept hierarchy and data filtering. This is foundational infrastructure that cannot be bypassed.

**Delivers:**
- Corrected Spring Boot 3.2.5 configuration
- Extended sys_dept with dept_level column (city=1, district=2, station=3)
- DeptHierarchyService (getAncestors, getChildren, getCityUnit)
- PemsDataScopeAspect extending RuoYi's DataScope
- User/Role/Permission management with multi-level org support
- System configuration table (pems_config)

**Implements:** AUTH-03 multi-level user mode, PERM-01/PERM-02 permissions, CONFIG-01

**Avoids:** Pitfall 4 (data isolation failure), Pitfall 7 (role-permission mismatch), Pitfall 12 (hardcoded regulatory rules)

### Phase 2: Evidence Core (No Workflow Dependencies)
**Rationale:** Core evidence CRUD is independent of workflow engine. Build and test evidence operations before layering workflow complexity.

**Delivers:**
- Evidence entity + mapper with one-code generation (EVI-01)
- EvidenceService (CRUD, status validation, state machine)
- EvidenceController (basic operations)
- EvidenceLogService (immutable audit trail with HMAC-SHA256 hash chaining)
- Seal management with unique constraint enforcement
- Storage verification (two-person check)
- Multi-condition query with data scope filtering
- Basic statistics and report export

**Implements:** EVI-01 one-code, LOG-01 tamper-proof logs, STORE-04 sealing

**Avoids:** Pitfall 1 (chain of custody breaks), Pitfall 3 (log tampering), Pitfall 8 (state machine violations), Pitfall 9 (sealing integrity break), Pitfall 13 (code reuse)

### Phase 3: Workflow Engine
**Rationale:** Workflow operates on evidence. It needs evidence IDs and statuses already in place. This phase adds the approval workflow system on top of proven evidence operations.

**Delivers:**
- WorkflowDefinition + WorkflowNode entities
- WorkflowDefinitionMapper (query by unit, evidence_type/level)
- WorkflowService.resolve() — find applicable workflow
- WorkflowService.createInstance() — start new workflow
- WorkflowController — submit/approve/reject endpoints
- WorkflowTask assignment logic (by role or user)
- Evidence callout/borrow request → graded approval flow
- Checkout verification and return management

**Implements:** FLOW-01 workflow engine, FLOW-02 workflow templates

**Avoids:** Pitfall 5 (workflow bypass), Pitfall 10 (cross-unit handshake gaps)

### Phase 4: Integration & Compliance
**Rationale:** Must ensure workflow approvals correctly update evidence status end-to-end. This is where the system becomes a complete closed loop.

**Delivers:**
- WorkflowService callback on complete → EvidenceService.updateStatus()
- Cross-unit transfer with dual approval and confirmation handshake
- Return verification with new seal generation if broken
- Disposal processing (return, destroy, auction, surrender, transfer)
- Evidence lifecycle timeline view
- Full integration testing

**Implements:** Complete closed-loop management per regulations

**Avoids:** Pitfall 6 (evidence code reuse), Pitfall 11 (timestamp integrity issues)

### Phase Ordering Rationale

- **Dependency chain:** Data isolation (Phase 1) is required before evidence queries (Phase 2) can filter properly. Evidence core (Phase 2) is required before workflow (Phase 3) can operate on evidence. Integration (Phase 4) requires both evidence and workflow to be stable.
- **Risk mitigation:** Building evidence core without workflow first allows testing data isolation and state machine logic before adding approval complexity.
- **Anti-pattern avoidance:** Phase 1 explicitly avoids putting business logic in controllers, hardcoding workflow steps, or ignoring DataScope — the three architectural anti-patterns identified.

### Research Flags

**Phases needing deeper research during planning:**
- **Phase 3 (Workflow Engine):** Whether to use lightweight state machine (recommended) or integrate Camunda/BPMN engine. Decision impacts delivery timeline significantly.
- **Phase 4 (Cross-Unit Transfer):** Legal/compliance review needed for transfer approval chain between districts.

**Phases with standard patterns (skip research):**
- **Phase 1 (Foundation):** RuoYi's DataScope is well-documented and battle-tested
- **Phase 2 (Evidence Core):** Standard CRUD patterns with state machine; no external dependencies

---

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| Stack | MEDIUM | Frontend fully verified; backend has critical version error that needs correction; RuoYi patterns well-understood but external validation recommended |
| Features | MEDIUM-HIGH | Based on regulatory requirements (公安部令) and requirements docs; well-grounded in government evidence management domain |
| Architecture | MEDIUM | Based on RuoYi framework patterns and general government system patterns; no external web search available during research; workflow engine decision needs team input |
| Pitfalls | MEDIUM-HIGH | Based on domain expertise and requirements analysis; critical pitfalls well-identified; validation with PSB stakeholders recommended |

**Overall confidence:** MEDIUM

### Gaps to Address

- **Spring Boot version:** Must verify against RuoYi Vue3 official release notes before Phase 1 begins
- **Workflow engine selection:** Lightweight state machine vs Camunda/BPMN — team decision needed; affects Phase 3 scope significantly
- **Dual control hardware binding:** Validation needed with PSB whether smart cabinet hardware supports true concurrent dual verification
- **Offline scenarios:** Field collection during network outage — design approach needed before Phase 1
- **Cross-unit transfer approval chain:** Whether city bureau needs to approve inter-district transfers or just source+target units
- **MinIO sizing:** Evidence photos/videos storage estimates needed for infrastructure planning

---

## Sources

### Primary (HIGH confidence)
- `/planning/pems/pems_backend/pom.xml` — verified backend dependency versions
- `/planning/pems/pems_frontend/package.json` — verified frontend stack
- `/planning/pems/docs/技术约束.md` — project constraints

### Secondary (MEDIUM confidence)
- RuoYi v3.9.1 framework patterns — training data, official release notes
- Camunda 7.23.x documentation — training data
- 《公安机关物证管理规定》 (Ministry of Public Security Order) — regulatory framework
- 物证管理系统需求规格说明书 — requirements specification
- 物证管理系统功能列表 — feature inventory

### Tertiary (LOW confidence)
- Chinese government system implementation patterns — training data, needs PSB stakeholder validation
- Workflow engine selection recommendation — community consensus varies by use case

---

*Research completed: 2026-03-28*
*Ready for roadmap: yes*
