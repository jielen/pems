# Domain Pitfalls: 物证管理系统 (Physical Evidence Management System)

**Project:** PEMS - 面向公安机关的物证全生命周期数字化管理系统
**Researched:** 2026-03-28
**Domain:** Chinese Public Security Evidence Management (公安机关物证管理)
**Confidence:** MEDIUM-HIGH

> **Note:** This research was conducted using domain expertise and requirements analysis. External web search was unavailable during research. Findings should be validated with actual Public Security Bureau (PSB) stakeholders before implementation.

---

## Critical Pitfalls

Mistakes that cause legal invalidation of evidence, regulatory non-compliance, or rewrite-level issues.

### Pitfall 1: Chain of Custody Breaks (全程留痕断点)

**What goes wrong:** Evidence lifecycle operations are recorded incompletely. Gaps appear in the audit trail where physical evidence moved but no system record exists.

**Why it happens:**
- Offline data entry scenarios (field collection without connectivity)
- Manual exception handling that bypasses system
- Integration gaps between hardware (RFID, smart cabinets) and system
- Temporary evidence states (in-transit, processing) not tracked

**Consequences:**
- Evidence becomes inadmissible in court due to chain of custody breaks
- Legal liability for officers when evidence integrity cannot be proven
- Audit failures during regulatory inspection
- Cannot reconstruct events during investigations

**Prevention:**
- Design for offline-first with mandatory sync before status changes
- Every physical hand-off MUST have corresponding system record
- Hardware events (cabinet open/close, scan) automatically generate logs
- Implement "pending sync" queue with server-side validation

**Warning signs:**
- Evidence status changes without corresponding operator record
- Time gaps > 30 minutes between consecutive operations on same evidence
- "Manual override" or "exception" entries in logs
- Physical count mismatches during inventory

**Phase:** Phase 1 (System Management) - must establish audit infrastructure before Phase 2

---

### Pitfall 2: Dual Control Becomes Theater (双人双锁形同虚设)

**What goes wrong:** Two-person verification is implemented as a checkbox or sequential login rather than genuine concurrent dual-control that prevents single-person execution.

**Why it happens:**
- Two users can authenticate sequentially from same terminal
- No real-time constraint that both must be present
- Session-based rather than operation-based locking
- Configured "off" by default for convenience

**Consequences:**
- Regulatory violation - key operations (destruction, core evidence removal) can be done by one person
- Evidence tampering possible without detection
- Failed regulatory audits
- Criminal liability for responsible officers

**Prevention:**
- Concurrent session enforcement (both users must have active sessions simultaneously)
- Hardware binding (柜子本身 must detect two RFID cards present)
- Operation cannot be initiated if second person not authenticated within 30 seconds
- Audit log captures both individuals' credentials before operation proceeds

**Warning signs:**
- Dual operations showing sequential timestamps (same IP, 1-2 seconds apart)
- No physical hardware feedback (cabinet doesn't require two card reads)
- Config flags that disable dual control
- Users sharing passwords

**Phase:** Phase 2 (Evidence Storage Module) - implement properly or don't implement at all

---

### Pitfall 3: Audit Log Tampering (日志可篡改)

**What goes wrong:** Operation logs can be modified or deleted, defeating the tamper-evident requirement.

**Why it happens:**
- Implementing logs as regular database records with UPDATE/DELETE
- No hash chaining or blockchain-style integrity
- Admin accounts with delete privileges
- No separate immutable storage

**Consequences:**
- Evidence history can be altered after the fact
- Legal proceedings compromised when defense proves tampering
- Cannot detect unauthorized access
- Regulatory non-compliance with "不可篡改" requirement

**Prevention:**
- Logs stored in append-only structure (database with delete triggers disabled)
- Each log entry includes hash of previous entry (hash chain)
- Separate audit database with no application-level delete accounts
- Periodic hash verification job that detects any tampering
- WORM (Write Once Read Many) storage option for highest security

**Warning signs:**
- Log entries with editable timestamps
- Any DELETE operations possible on log tables
- Admin accounts can access log modification
- No hash verification scheduled job

**Phase:** Phase 1 (System Management) - LOG-01 is a core MVP requirement

---

### Pitfall 4: Multi-Level Data Isolation Failure (数据隔离失效)

**What goes wrong:** City-level users can see district-level data, or vice versa. Data leaks across organizational hierarchies.

**Why it happens:**
- Filter logic missing or incorrect in repository queries
- User unit_id not properly propagated to all data access paths
- Cross-unit transfer operations expose data before approval
- Cached data accessible across units

**Consequences:**
- Privacy violations
- Case information leaks
- Organizational trust failure
- Cannot pass security audits

**Prevention:**
- All queries must include unit_id filter as mandatory WHERE condition
- Implement data scope layer that automatically injects unit filter
- Cross-unit data access requires explicit approval record
- Separate cache namespaces per unit
- Unit hierarchy (city → district → station) enforced in data access

**Warning signs:**
- Users seeing evidence not belonging to their unit
- API responses containing data from other units
- Cross-unit queries returning results
- No unit_id in entity schemas

**Phase:** Phase 1 (System Management) - AUTH-03 multi-level mode is MVP requirement

---

### Pitfall 5: Workflow Engine Bypassed (工作流引擎绕过)

**What goes wrong:** Approval workflows can be bypassed - evidence moves to next state without required approvals, or approvals happen after the fact.

**Why it happens:**
- Direct status update APIs that skip workflow engine
- Admin functions that force-status without approval chain
- Workflow engine not enforcing state machine constraints
- Exception handlers that clear pending approvals

**Consequences:**
- Evidence released without proper authorization
- Regulatory violation (evidence must follow approval chain)
- Liability for approving officers
- Invalidates evidence handling procedures

**Prevention:**
- Workflow engine MUST be the only path for status transitions
- No direct update APIs for evidence status
- All exceptions require new approval chain, not bypass
- Status transitions create immutable workflow records
- Periodic job detects evidence in invalid state combinations

**Warning signs:**
- Evidence status changes without corresponding workflow instance
- Approval timestamps AFTER status change timestamps
- "Force approve" or "admin override" functions
- Workflow engine can be disabled by config

**Phase:** Phase 1 (System Management) - FLOW-01/FLOW-02 are core MVP requirements

---

### Pitfall 6: Evidence Code Reuse After Loss/Damage (编码重用)

**What goes wrong:** Lost or damaged evidence codes are reassigned to new evidence items, breaking one-to-one code binding.

**Why it happens:**
- Code generation logic that doesn't track retired codes
- Database unique constraint missing or disabled
- Status "丢失/损毁" treated as available for reassignment
- No historical record linking code to original evidence

**Consequences:**
- Evidence confusion in court proceedings
- Cannot prove which evidence was presented
- Legal challenges to evidence authenticity
- Audit failures

**Prevention:**
- Lost/damaged status permanently retires the code
- New evidence MUST get new code
- Query API returns historical code assignment records
- Unique constraint on (evidence_code, del_flag) where del_flag='0'

**Warning signs:**
- Evidence queries returning multiple items with same code
- Codes being reassigned after loss/damage
- No historical tracking of code assignments

**Phase:** Phase 2 (Evidence In/Out Module) - EVI-01 is MVP requirement

---

## Moderate Pitfalls

Mistakes that cause significant issues but don't necessarily require full rewrite.

### Pitfall 7: Role-Permission Workflow Mismatch (权责不符)

**What goes wrong:** Permissions assigned don't match actual police organizational workflow. Users can do things they shouldn't, or can't do things they need to.

**Why it happens:**
- System roles designed by IT, not by PSB workflow experts
- Permissions don't account for actual organizational hierarchy (科/处/队)
- Dynamic role assignment not supported
- Temporary permissions for special operations missing

**Prevention:**
- Detailed workflow analysis with actual PSB stakeholders BEFORE development
- Permission model matches actual approval chain (承办人 → 班组长 → 法制审核员 → 领导)
- Temporary role elevation for special cases
- Role simulation testing with real users before go-live

**Phase:** Phase 1 (System Management) - PERM-01/PERM-02 are MVP

---

### Pitfall 8: Evidence State Machine Violations (状态机错误)

**What goes wrong:** Evidence reaches invalid states (e.g., "借出" while already "销毁"), or required transitions are impossible.

**Why it happens:**
- State transition rules not modeled as formal state machine
- Exception paths create invalid states
- Concurrent operations race to invalid state
- Missing states (e.g., no "送检中" intermediate state)

**Prevention:**
- Implement evidence state as formal state machine with defined transitions
- All transitions validated server-side, not just UI enforced
- Concurrent access uses optimistic locking (version field)
- States and transitions defined in configuration, not code

**Warning signs:**
- Evidence in impossible state combinations
- Operations that fail because state prevents valid transition
- No validation when status changes
- Direct database updates bypassing application

**Phase:** Phase 2 (Evidence In/Out Module)

---

### Pitfall 9: Sealing (封签) Integrity Break

**What goes wrong:** Seal numbers can be duplicated, seals can be replaced without logging, and seal verification is not enforced at check-out.

**Why it happens:**
- Seal number uniqueness not enforced at DB level
- Re-sealing doesn't invalidate old seal record
- Physical seal check not bound to system verification
- Seal replacement doesn't require approval

**Prevention:**
- Unique constraint on seal number at DB level
- Re-sealing creates new seal record, old seal marked "replaced" with reason
- Check-out requires physical seal scan verification
- Seal replacement requires supervisor approval and logging

**Phase:** Phase 2 (Evidence In/Out Module) - STORE-04 is MVP

---

### Pitfall 10: Cross-Unit Transfer Handshake Gaps

**What goes wrong:** Evidence transferred between units gets stuck - origin unit marks as transferred but receiving unit hasn't confirmed, or vice versa.

**Why it happens:**
- Transfer workflow is fire-and-forget
- No confirmation handshake from receiving unit
- Network failures leave transfers in limbo
- No escalation for unconfirmed transfers

**Prevention:**
- Transfer requires explicit receiving confirmation
- Origin cannot mark "transferred" until receiving confirms "received"
- Unconfirmed transfers escalate automatically after timeout
- Transfer states: 待接收 → 运输中 → 已接收 (or 已拒绝)

**Phase:** Phase 2 (Evidence In/Out Module) - TRANS-04 is MVP

---

### Pitfall 11: Timestamp Integrity Issues (时间戳篡改)

**What goes wrong:** System timestamps can be manipulated, either intentionally or through server clock errors. This destroys legal validity of time-based evidence.

**Why it happens:**
- Application relies on server clock without validation
- No trusted time source
- Admin can modify server time
- Timezone handling errors

**Prevention:**
- Integrate with NTP server for trusted timestamps
- Log ingestion timestamp (server) vs event timestamp (user-provided) separately
- Blockchain anchoring for critical timestamps
- Alert on significant clock drifts

**Phase:** Phase 1 (System Management)

---

### Pitfall 12: Hardcoded Regulatory Rules

**What goes wrong:** Compliance rules from regulations are hardcoded, making it impossible to adapt when regulations change or vary by region.

**Why it happens:**
- Regulatory requirements implemented as if/else in code
- No configuration table for compliance rules
- "It matches current regulation" treated as sufficient

**Prevention:**
- Regulatory rules stored in configuration tables (物证分类配置, 保管期限配置, 预警规则配置)
- Rule engine for complex approval chains
- CONFIG-01 must be implemented properly, not deferred

**Phase:** Phase 1 (System Management) - CONFIG-01 is MVP

---

## Minor Pitfalls

### Pitfall 13: Paper-Digital Hybrid Records

**What goes wrong:** System allows parallel paper records that can diverge from digital records, creating conflicts and double-maintenance.

**Prevention:** System must be authoritative source. Paper records are scan-and-archive only.

### Pitfall 14: Photo/Video Attachment Integrity

**What goes wrong:** Photos and videos attached to evidence can be modified after capture, defeating their evidentiary value.

**Prevention:** Media files hashed at capture, hash stored in immutable log, re-hash verification on access.

### Pitfall 15: Notification/Reminder Blind Spots

**What goes wrong:** System reminders (归还提醒, 到期预警) exist but users ignore them because they're buried or not actionable.

**Prevention:** Alerts must be prominent, actionable, and tracked. Ignored alerts escalate.

---

## Phase-Specific Warnings

| Phase | Critical Pitfall | Mitigation |
|-------|------------------|------------|
| Phase 1: System Management | Log tampering, data isolation failure, workflow bypass | Build compliance infrastructure first |
| Phase 2: Evidence In/Out | Dual control theater, chain of custody breaks, evidence code reuse | Strict state machine, hardware binding |
| Phase 2 (continued): Workflow | Workflow bypass, cross-unit handshake gaps | Workflow engine as only path |
| Phase 3: Evidence Custody | Sealing integrity break, timestamp issues | Physical verification binding, NTP |
| Phase 4+: Query/Statistics | Role-permission mismatch, hardcoded rules | Configurable rules engine |

---

## Sources & References

**Regulatory Framework:**
- 《公安机关物证管理规定》 (Public Security Evidence Management Regulations - Ministry of Public Security Order)
- 《公安执法规范化建设要求》
- 《公安机关信息系统安全等级保护定级指南》

**Key Requirements (from requirements doc):**
- 一物一码：Evidence codes never reused (EVI-01)
- 全程留痕：Complete audit trail, no gaps (LOG-01)
- 责任到人：Operator accountability with badge numbers (multiple requirements)
- 闭环管理：Closed-loop from intake to disposal

**Technology Decisions (from PROJECT.md):**
- RuoYi Vue3 framework (brownfield)
- MySQL 8.0 + Redis 6.0 + MinIO
- MVP delivery: 2026-04-30

---

## Validation Needed

The following should be validated with actual PSB stakeholders before finalizing roadmap:

1. **Dual Control Implementation**: Does hardware (smart cabinets) support true dual verification, or only sequential card reads?
2. **Offline Scenarios**: What happens during network outage? Field collection must still be recorded.
3. **Existing System Integration**: Does data migration from existing systems risk breaking chain of custody?
4. **Time Zone Handling**: Is the system expected to work across provinces with different local times?

**Confidence:** MEDIUM - Domain expertise applied, but external validation unavailable.
