# 物证管理系统 (PEMS) Roadmap

## Project Overview

- **Project:** 物证管理系统 (Physical Evidence Management System)
- **Core Value:** 一物一码 + 全程留痕 + 责任到人
- **Framework:** RuoYi Vue3 v3.9.1 (SpringBoot 3.2.5 + Vue3)
- **Delivery:** 2026-04-30

## Phases

- [ ] **Phase 1: Foundation & Data Isolation** - Multi-level auth, roles, orgs, audit logging, system config
- [ ] **Phase 2: Evidence Core Operations** - Evidence CRUD, check-in, storage, without workflow
- [ ] **Phase 3: Workflow Engine** - Workflow definitions, templates, and evidence transfer workflow
- [ ] **Phase 4: Integration & Dashboard** - End-to-end workflow integration, dashboard, cross-unit transfer

## Phase Details

### Phase 1: Foundation & Data Isolation

**Goal:** Users can authenticate, manage users/roles within their org hierarchy, and all operations are immutably logged

**Depends on:** Nothing (first phase)

**Requirements:** AUTH-01, AUTH-02, AUTH-03, AUTH-04, ROLE-01, ROLE-02, ROLE-03, ORG-01, ORG-02, ORG-03, ORG-04, LOG-01, LOG-02, LOG-03, LOG-04, CONFIG-01, CONFIG-02, CONFIG-03, CONFIG-04, CONFIG-05, CONFIG-06, CONFIG-07

**Success Criteria** (what must be TRUE):
1. User can log in with username/password and receive JWT token that persists across sessions
2. Super admin can create/edit/disable users and assign roles within their jurisdiction
3. Multi-level users (city/district) can only see and manage data within their scope (data isolation enforced)
4. Role permissions follow format `module:entity:operation` and changes take effect immediately
5. All operations produce immutable audit logs with user ID, timestamp, IP, and operation details
6. Audit logs use HMAC-SHA256 hash chaining and cannot be deleted or modified
7. Auditor role can view logs and export audit reports
8. Admin can configure evidence types, levels, retention periods, numbering rules, units, dual-lock settings, and alert rules

**Plans**: 4 plans (01-01 to 01-04)

**Plan list:**
- [x] 01-01-PLAN.md — Database schema (pems_config, pems_audit_log, sys_dept_extend)
- [ ] 01-02-PLAN.md — Organization & Data Scope (unit_code, DataScopeAspect)
- [x] 01-03-PLAN.md — Role & Permission System (pems:* permissions)
- [ ] 01-04-PLAN.md — Audit & Config Management (hash chain, config UI)

**UI hint**: yes

---

### Phase 2: Evidence Core Operations

**Goal:** Users can register evidence with unique codes, check evidence into storage, and query evidence; all operations logged immutably

**Depends on**: Phase 1

**Requirements**: EVI-01, EVI-02, EVI-03, EVI-04, EVI-05, EVI-06, EVI-07, EVI-08, STORE-01, STORE-02, STORE-03, STORE-04, STORE-05, STORE-06

**Success Criteria** (what must be TRUE):
1. Each new evidence item receives a system-generated unique code (one-code-per-evidence, never reused even after loss/destruction)
2. User can enter evidence information (case number, name, type, level, quantity, unit, etc.) and edit it with modification history recorded
3. Evidence can be assigned to categories (regular/valuable/classified/dangerous/perishable) and levels (general/important/core)
4. Evidence status (in-storage/borrowed/testing/destroyed/lost/damaged) transitions are validated by the state machine
5. User can record evidence check-in with receiver, time, and handover list
6. User can verify evidence for storage (single-person in Phase 1, dual-person in Phase 2+)
7. System recommends storage positions based on evidence type and level
8. User can bind/unbind unique seal numbers with photo documentation
9. System generates standardized storage receipt with electronic signature
10. User can upload and view evidence photos
11. User can query evidence by multiple conditions with results filtered by data scope

**Plans**: TBD

**UI hint**: yes

---

### Phase 3: Workflow Engine

**Goal:** Evidence transfers (call, borrow, return, cross-unit) are governed by a configurable workflow engine with graded approval based on evidence level

**Depends on**: Phase 2

**Requirements**: WF-01, WF-02, WF-03, WF-04, WF-05, WF-06, WF-07, WF-08, TRANS-01, TRANS-02, TRANS-03, TRANS-04, TRANS-05, TRANS-06

**Success Criteria** (what must be TRUE):
1. Admin can create, edit, enable/disable workflow definitions with templates
2. Each workflow node can specify approver roles, fixed approvers, or unit managers
3. Workflow nodes support conditional routing based on evidence type or level
4. City-level units use unified workflow templates; district units can have independent templates
5. User can initiate workflow instances for evidence operations (call, transfer, return)
6. System determines correct workflow template based on evidence level and unit configuration
7. Approvers can view pending tasks, approve/reject with comments, and see流转历史
8. System sends alerts for approval timeouts
9. Evidence checkout requires verification linking the workflow instance to physical evidence
10. Evidence return triggers new seal generation if original seal is broken
11. Cross-unit transfers route through approval chains of both source and target units

**Plans**: TBD

---

### Phase 4: Integration & Dashboard

**Goal:** Complete closed-loop evidence lifecycle with leadership oversight dashboard; all workflow approvals trigger proper evidence status transitions

**Depends on**: Phase 3

**Requirements**: DASH-01, DASH-02, DASH-03

**Success Criteria** (what must be TRUE):
1. Workflow approval completion automatically updates evidence status in the system
2. Complete evidence lifecycle timeline viewable with all state transitions and operations
3. Leader dashboard displays in-storage, borrowed, checked-out, and total evidence counts
4. Dashboard shows evidence breakdown by type, status, and level with charts
5. Dashboard displays warning statistics (overdue returns, expiring retention, etc.)

**Plans**: TBD

**UI hint**: yes

---

## Progress

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Data Isolation | 2/4 | In Progress | - |
| 2. Evidence Core Operations | 0/1 | Not started | - |
| 3. Workflow Engine | 0/1 | Not started | - |
| 4. Integration & Dashboard | 0/1 | Not started | - |

---

## Coverage

**Total v1 requirements: 53**

| Phase | Requirements | Count |
|-------|--------------|-------|
| 1 - Foundation & Data Isolation | AUTH-01, AUTH-02, AUTH-03, AUTH-04, ROLE-01, ROLE-02, ROLE-03, ORG-01, ORG-02, ORG-03, ORG-04, LOG-01, LOG-02, LOG-03, LOG-04, CONFIG-01, CONFIG-02, CONFIG-03, CONFIG-04, CONFIG-05, CONFIG-06, CONFIG-07 | 26 |
| 2 - Evidence Core Operations | EVI-01, EVI-02, EVI-03, EVI-04, EVI-05, EVI-06, EVI-07, EVI-08, STORE-01, STORE-02, STORE-03, STORE-04, STORE-05, STORE-06 | 14 |
| 3 - Workflow Engine | WF-01, WF-02, WF-03, WF-04, WF-05, WF-06, WF-07, WF-08, TRANS-01, TRANS-02, TRANS-03, TRANS-04, TRANS-05, TRANS-06 | 14 |
| 4 - Integration & Dashboard | DASH-01, DASH-02, DASH-03 | 3 |

**All 53 v1 requirements mapped to phases (100% coverage)**

---

*Last updated: 2026-03-28*
