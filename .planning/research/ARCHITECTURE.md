# Architecture Patterns: Government Evidence Management System

**Project:** PEMS (Physical Evidence Management System)
**Researched:** 2026-03-28
**Confidence:** MEDIUM (based on framework patterns + requirements analysis, no external research due to tool restrictions)

---

## Executive Summary

The PEMS system is a multi-tier government evidence lifecycle management system built on RuoYi Vue3 framework. It requires a **hierarchical multi-tenant architecture** for city/district-county data isolation, and a **state-machine workflow engine** for tiered approval processes. The existing RuoYi framework provides the foundation (dept hierarchy, @DataScope filtering), which PEMS must extend with evidence-specific business logic.

**Key architectural decisions needed:**
1. Where to implement multi-unit data isolation (MySQL schema vs application-layer filtering)
2. Workflow engine selection (build custom vs integrate existing)
3. How to handle cross-unit evidence transfers

---

## Recommended Architecture

### Component Boundaries

```
┌─────────────────────────────────────────────────────────────────────┐
│                        FRONTEND (Vue3)                              │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐ │
│  │   System    │  │  Evidence   │  │  Workflow   │  │  Report   │ │
│  │   Admin     │  │  Management  │  │  Approval   │  │  Dashboard│ │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └─────┬─────┘ │
└─────────┼────────────────┼────────────────┼───────────────┼───────┘
          │                │                │               │
          ▼                ▼                ▼               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     API GATEWAY / Nginx                              │
│              (Authentication, Rate Limiting, Routing)                 │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │
┌─────────────────────────────────┴───────────────────────────────────┐
│                      BACKEND (SpringBoot)                            │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                    PEMS Module                                 │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │   │
│  │  │   System     │  │  Evidence   │  │    Workflow         │  │   │
│  │  │   Admin      │  │  Service    │  │    Engine           │  │   │
│  │  │  Controller  │  │  Controller │  │    Controller       │  │   │
│  │  └──────┬──────┘  └──────┬──────┘  └──────────┬──────────┘  │   │
│  │         │                │                     │              │   │
│  │  ┌──────┴────────────────┴─────────────────────┴──────────┐   │   │
│  │  │              SERVICE LAYER                              │   │   │
│  │  │  ISysUserService    IEvidenceService   IWorkflowService│   │   │
│  │  │  ISysDeptService    IEvidenceLogService                │   │   │
│  │  └──────────────────────────┬──────────────────────────────┘   │   │
│  │                             │                                   │   │
│  │  ┌──────────────────────────┴──────────────────────────────┐   │   │
│  │  │              DATA ACCESS LAYER                           │   │   │
│  │  │  SysUserMapper    EvidenceMapper    WorkflowMapper      │   │   │
│  │  └──────────────────────────────────────────────────────────┘   │   │
│  └──────────────────────────────────────────────────────────────┘   │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │                 RUOYI FRAMEWORK CORE                          │   │
│  │  BaseController | @DataScope | SecurityUtils | RedisCache    │   │
│  └──────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────┬───────────────────────────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          ▼                       ▼                       ▼
    ┌───────────┐          ┌───────────┐          ┌───────────┐
    │   MySQL   │          │   Redis   │          │   MinIO   │
    │  (Primary) │          │  (Cache)  │          │  (Files)  │
    └───────────┘          └───────────┘          └───────────┘
```

### Data Isolation Architecture (Multi-Level Users)

**Option Selected: Application-Layer Filtering with @DataScope**

The RuoYi framework already provides `@DataScope` annotation with 5 data scope types. PEMS extends this pattern:

| DataScope Value | Name | Behavior |
|-----------------|------|----------|
| 1 | ALL | Can see all units' data (super admin only) |
| 2 | CUSTOM | Can see assigned deptIds only |
| 3 | DEPT | Can see own dept data only |
| 4 | DEPT_AND_BELOW | Can see dept + all child depts (city-level) |
| 5 | SELF | Can see own data only |

**Evidence Data Isolation Strategy:**

```
Evidence Query Filter Chain:
1. Check user's role + dataScope
2. If dataScope = 4 (DEPT_AND_BELOW) and user.dept.level = CITY:
   → Return all evidence where storage_unit IN (city + all district/county depts)
3. If dataScope = 3 (DEPT) and user.dept.level = DISTRICT:
   → Return only evidence where storage_unit = user.deptId
4. Cross-unit transfer requires explicit approval workflow
```

**Dept Level Hierarchy (sys_dept extension):**

```sql
-- Extend sys_dept with level tracking
ALTER TABLE sys_dept ADD COLUMN dept_level TINYINT DEFAULT 1
COMMENT '1=city, 2=district/county';

-- Sample data:
-- 100 (city bureau, level=1)
-- 100001 (district1, level=2, parent=100)
-- 100002 (district2, level=2, parent=100)
-- 100001001 (police station, level=3, parent=100001)
```

### Workflow Engine Architecture

**Recommendation: State Machine Pattern with DB-driven Configuration**

Do NOT use heavy BPMN engines (Camunda, Activiti) for MVP. Use a lightweight state machine with database-backed workflow definitions.

**Workflow Definition Model:**

```sql
CREATE TABLE pems_workflow_definition (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_key     VARCHAR(50) NOT NULL COMMENT 'workflow identifier',
    version          INT DEFAULT 1 COMMENT 'version number',
    name             VARCHAR(100) NOT NULL COMMENT 'workflow name',
    evidence_type    TINYINT COMMENT 'NULL=applies to all, 1=normal,2=precious,etc',
    evidence_level   TINYINT COMMENT 'NULL=applies to all, 1=general,2=important,3=core',
    unit_id          BIGINT COMMENT 'NULL=system default, else unit-specific',
    is_active        CHAR(1) DEFAULT '1',
    create_time      DATETIME
);

CREATE TABLE pems_workflow_node (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_id     BIGINT NOT NULL,
    node_order      INT NOT NULL COMMENT 'execution order',
    node_name       VARCHAR(50) NOT NULL COMMENT 'e.g., 班组长审核',
    node_type       VARCHAR(20) NOT NULL COMMENT 'APPROVAL/ACTION/SYSTEM',
    approver_type   VARCHAR(20) NOT NULL COMMENT 'ROLE/USER/UNIT_LEADER',
    approver_expr   VARCHAR(200) COMMENT 'role_key or user_id or dynamic expression',
    timeout_hours   INT DEFAULT 72 COMMENT 'auto-escalate after timeout',
    action_expr     VARCHAR(200) COMMENT 'EL expression for auto-approve/reject',
    create_time     DATETIME
);

CREATE TABLE pems_workflow_instance (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    workflow_def_id BIGINT NOT NULL,
    evidence_id     BIGINT NOT NULL,
    current_node_id BIGINT,
    status          VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/EXPIRED',
    applicant_id    BIGINT NOT NULL,
    apply_time      DATETIME,
    complete_time   DATETIME,
    create_time     DATETIME
);

CREATE TABLE pems_workflow_task (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    instance_id     BIGINT NOT NULL,
    node_id         BIGINT NOT NULL,
    assignee_id     BIGINT COMMENT 'user who should act',
    assignee_role   VARCHAR(50) COMMENT 'fallback if assignee not available',
    task_status     VARCHAR(20) DEFAULT 'WAITING' COMMENT 'WAITING/COMPLETED/SKIPPED',
    decision        VARCHAR(20) COMMENT 'AGREE/REJECT/ABSTAIN',
    comment         TEXT,
    operate_time    DATETIME,
    create_time     DATETIME
);
```

**State Transition Flow:**

```
Evidence Status State Machine:
┌────────┐    submit     ┌──────────┐   approve   ┌────────┐
│ INWARE │──────────────▶│ PENDING  │────────────▶│ BORROWED│
└────────┘               └────┬─────┘             └────────┘
     ▲                        │ reject                 │
     │                        ▼                        │
     │                   ┌─────────┐                  │
     └───────────────────│ REJECTED│                  │
                         └─────────┘                  │
                                                     ▼
                                              ┌────────────┐
                                              │  RETURNED  │
                                              └─────┬──────┘
                                                    │入库核验
                                                    ▼
                                              ┌────────┐
                                              │ INWARE │
                                              └────────┘
```

**Multi-Unit Workflow Template:**

```java
// Workflow resolution logic
public WorkflowDefinition resolveWorkflow(Evidence evidence, String operationType) {
    // 1. Try unit-specific template first
    WorkflowDefinition unitTemplate = findActiveDefinition(
        evidence.getStorageUnitId(),
        evidence.getEvidenceLevel()
    );
    if (unitTemplate != null) {
        return unitTemplate;
    }

    // 2. Fall back to city-level unified template
    Long cityUnitId = getParentCityUnitId(evidence.getStorageUnitId());
    WorkflowDefinition cityTemplate = findActiveDefinition(
        cityUnitId,
        evidence.getEvidenceLevel()
    );
    if (cityTemplate != null) {
        return cityTemplate;
    }

    // 3. Fall back to system default
    return findSystemDefaultDefinition(evidence.getEvidenceLevel());
}
```

---

## Component Details

### Component Boundaries

| Component | Responsibility | Communicates With |
|-----------|----------------|-------------------|
| **SystemAdminController** | User/dept/role CRUD, permission assignment | SysUserService, SysDeptService |
| **EvidenceController** | Evidence CRUD, status transitions | EvidenceService, WorkflowService |
| **WorkflowController** | Workflow instance management, approvals | WorkflowService, EvidenceService |
| **EvidenceService** | Business logic, data validation | EvidenceMapper, EvidenceLogMapper |
| **WorkflowService** | State machine execution, task assignment | WorkflowMapper, EvidenceService |
| **DataScopeAspect** | @DataScope annotation processing | MyBatis interceptor chain |

### Data Flow

**Evidence Lifecycle Flow:**

```
1. CREATE (办案民警)
   EvidenceController.submit()
   → EvidenceService.validate()
   → EvidenceMapper.insert() [status=INWARE]
   → EvidenceLogMapper.insert() [operationType=CREATE]

2. BORROW REQUEST (办案民警)
   WorkflowController.submitBorrow()
   → WorkflowService.createInstance(evidenceId, "BORROW")
   → WorkflowTaskMapper.insert() [assign to 班组长]
   → Evidence status unchanged until approved

3. APPROVAL (班组长/领导)
   WorkflowController.approve()
   → WorkflowTaskMapper.update(decision)
   → WorkflowService.evaluateNextNode()
   → If final approved: EvidenceService.updateStatus(BORROWED)

4. RETURN (物证管理员)
   EvidenceController.returnEvidence()
   → EvidenceService.verifyReturn()
   → EvidenceService.updateStatus(INWARE)
   → EvidenceLogMapper.insert()
```

**Cross-Unit Transfer Flow:**

```
1. District A initiates transfer request
   → WorkflowController.submitTransfer(targetUnitId=B)
   → Creates workflow instance with dual approvers (source + target)

2. Source unit approval (District A 领导)
   → WorkflowTaskService.complete(sourceApproval)

3. Target unit approval (District B 领导)
   → WorkflowTaskService.complete(targetApproval)
   → EvidenceService.updateStorageUnit(targetUnitId)
   → EvidenceLogMapper.insert(TRANSFER_COMPLETE)
```

### Cross-Unit vs Intra-Unit Data Visibility

| Scenario | Data Scope | Visible Evidence |
|----------|-----------|-----------------|
| City admin views all | dataScope=1 (ALL) | All city + districts |
| District admin views own | dataScope=4 (DEPT_AND_BELOW) | Own district + stations |
| Police station views own | dataScope=3 (DEPT) | Own station only |
| 办案民警 views case evidence | dataScope=5 (SELF) + case filter | Own submitted evidence |

---

## Build Order Implications

### Phase 1: Foundation (Multi-Level Data Isolation)

**Why first:** All other modules depend on proper user/dept hierarchy and data filtering.

```
Build Sequence:
1. Extend sys_dept with dept_level column
2. Implement DeptHierarchyService (getAncestors, getChildren, getCityUnit)
3. Create PemsDataScopeAspect extending RuoYi's DataScope
4. Add evidence_level column to pems_evidence table
5. Unit test data isolation with different user roles
```

**Critical dependency:** Workflow engine requires knowing the approval chain, which depends on dept hierarchy.

### Phase 2: Evidence Core (No Workflow Dependencies)

**Why second:** Core evidence CRUD is independent of workflow. Workflow wraps it.

```
Build Sequence:
1. Evidence entity + mapper
2. EvidenceService (CRUD, status validation)
3. EvidenceController (basic operations)
4. EvidenceLogService (immutable audit trail)
5. Evidence query with data scope filtering
```

### Phase 3: Workflow Engine (Depends on Evidence Core)

**Why third:** Workflow operates on evidence. It needs evidence IDs and statuses.

```
Build Sequence:
1. WorkflowDefinition + WorkflowNode entities
2. WorkflowDefinitionMapper (query by unit, evidence_type/level)
3. WorkflowService.resolve() - find applicable workflow
4. WorkflowService.createInstance() - start new workflow
5. WorkflowController - submit/approve/reject endpoints
6. WorkflowTask assignment logic (by role or user)
```

### Phase 4: Integration (Workflow + Evidence Status)

**Why last:** Must ensure workflow approvals correctly update evidence status.

```
Build Sequence:
1. WorkflowService callback on complete → EvidenceService.updateStatus()
2. Dual-lock integration (Phase 2 MVP: no-op, Phase 2 full: enforce)
3. Cross-unit transfer with dual approval
4. End-to-end integration tests
```

---

## Anti-Patterns to Avoid

### Anti-Pattern 1: Putting Business Logic in Controllers

**What:** Controllers doing validation, status checks, calculations.

**Why bad:** Untestable, violates single responsibility, RuoYi convention.

**Instead:** All business logic in Service layer. Controller only:
- Parameter extraction
- Permission check via @PreAuthorize
- Response formatting via BaseController methods

### Anti-Pattern 2: Hardcoded Workflow Steps

**What:** Switch statements or if-else chains for approval steps.

**Why bad:** Every workflow change requires code deployment. Can't have unit-specific templates.

**Instead:** Database-driven workflow definition. Add approval levels via data, not code.

### Anti-Pattern 3: Ignoring DataScope for Evidence Queries

**What:** Evidence queries without dept filtering, relying only on role checks.

**Why bad:** Data leakage between units. Violates "一物一码、全程留痕、责任到人" requirement.

**Instead:** Every evidence query MUST apply @DataScope or explicit unit filter.

### Anti-Pattern 4: Single-Table Inheritance for All Evidence Types

**What:** One massive pems_evidence table with all type-specific fields nullable.

**Why bad:** Too many nullable columns, no type safety, complex queries.

**Instead:** Evidence type-specific fields in separate tables (pems_evidence_hazardous, pems_evidence_precious) with FK to main evidence.

---

## Scalability Considerations

| Concern | 100 Users | 10K Users | 1M Users |
|---------|-----------|------------|----------|
| **Data isolation** | Single DB | Single DB + Redis cache | Shard by unit_id |
| **Workflow instances** | Single table | Partition by status | Archive completed |
| **Evidence logs** | Single table | Partition by month | Archive + summary tables |
| **Session management** | Redis | Redis cluster | Redis + JWT refresh |

---

## Key Integration Points

### External System Integration (Phase 2)

| System | Integration Method | Purpose |
|--------|-------------------|---------|
| 警务综合平台 | REST API | Case information sync |
| 执法办案平台 | REST API | Case status updates |
| 智能物证柜 | RS485/TCP | Auto lock/unlock,仓位 status |
| RFID扫码枪 | HID input | Quick evidence lookup |

### Audit Trail Requirements

Per 《公安机关物证管理规定》:
- All operations logged with operator_badge (警号)
- Logs immutable, hash-chained
- Log retention: 3 years standard, 5 years for classified evidence

---

## Sources

**Internal Analysis:**
- RuoYi v3.9.1 framework patterns (BaseController, @DataScope, SysDept hierarchy)
- Project requirements (物证管理系统需求规格说明书)
- Technical constraints (技术约束.md)

**No external sources verified** - WebSearch tool restricted. Architecture recommendations based on:
- RuoYi framework's built-in multi-tenant patterns
- General government evidence management workflow patterns
- Standard state machine + workflow engine patterns

**Validation needed for:**
- Actual workflow engine selection (custom vs Activiti/Camunda)
- Government-specific integration requirements
- Performance benchmarks for evidence photo/video storage (MinIO)

---

## Open Questions

1. **Workflow engine selection:** Should we build custom state machine (recommended for MVP) or integrate Activiti/Camunda for Phase 2?
2. **Cross-unit approval chain:** When transferring evidence between districts, does the city bureau need to approve, or just source+target units?
3. **Evidence photos/videos:** Storage size estimates? Does MinIO handle large files well, or need separate file server?
4. **Dual-lock implementation:** MVP reserves dual-lock config. Should we implement the enforcement logic, or just store the config flag?
