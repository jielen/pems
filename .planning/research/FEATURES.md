# Feature Landscape

**Domain:** Physical Evidence Management System (物证管理系统)
**Researched:** 2026-03-28
**Confidence:** MEDIUM-HIGH (based on requirements documents and domain knowledge of Chinese government regulations)

## Background

This system must comply with the "公安机关物证管理规定" (Public Security Evidence Management Regulations), which mandates:
- **一物一码** (One evidence, one code)
- **全程留痕** (Full process tracking)
- **责任到人** (Responsibility to person)
- **闭环管理** (Closed-loop management)

## Table Stakes

Features users expect. Missing any of these = product fails regulatory compliance or is unusable.

### Core Regulatory Requirements

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| One code per evidence (一物一码) | Mandated by regulation; foundation of entire system | High | Code never reused even if evidence lost/destroyed |
| Full-process logging (全程留痕) | Every operation logged with operator, time, result | High | Logs cannot be deleted, hash-verified for tamper detection |
| Responsibility attribution (责任到人) | Each operation records operator badge number and unit | Medium | Must link to official identity |
| Role-based access control | Minimize privilege principle; different roles see different data | Medium | Admin, evidence keeper, investigator, approver, auditor |
| Evidence lifecycle states | Status tracking: in-storage, borrowed, testing, destroyed, lost, damaged | Medium | State machine enforced, cannot skip states |
| Evidence classification | 5 types: regular, valuable, classified, dangerous, perishable | Low | Each type has different handling procedures |
| Evidence level | 3 levels: general, important, core | Low | Determines approval workflow |

### System Management

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| User management | System must have users with proper identification | Low | Badge number, unit, contact required |
| Role and permission management | Granular permissions per module:entity:operation | Medium | Predefined roles + custom roles |
| Multi-level organization support | City-level and district-level units with data isolation | High | Same-level units cannot see each other's data; upper level can see lower |
| Operation audit logs | Logs with hash verification, tamper-evident | Medium | Retention: 3 years standard, 5 years for classified evidence |
| System configuration | Configurable classification, retention periods, numbering rules | Low | Admin function |

### Evidence Core Operations

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Evidence registration | Capture evidence at scene with photos, documents | Medium | Pre-registration with temporary code before storage |
| Storage verification | Two-person verification before accepting into storage | Medium | Pass/fail with documented reason |
| Seal management | Unique seal number bound to evidence, photographed | Low | Seal number required for retrieval |
| Storage receipt generation | Standardized legal document with e-signatures | Medium | Must match police documentation standards |
| Evidence callout/borrow request | Investigator initiates request with justification | Medium | Must specify purpose, duration, recipient |
| Graded approval workflow | Approval chain varies by evidence level | High | General: supervisor only; Core: supervisor + legal + leadership |
| Checkout verification | Two-person verification at evidence release | Medium | Photo/video record required |
| Return management | Verify evidence condition on return, new seal if broken | Medium | Must document any discrepancies |
| Disposal processing | Handle: return, destroy, auction, surrender, transfer with case | High | Multi-level approvals, chain of custody maintained |

### Query and Reporting

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| Multi-condition query | Find evidence by code, case number, name, type, status, date | Medium | Permission-filtered results |
| Statistics dashboard | View inventory counts by type, status, unit | Medium | Real-time updates |
| Report export | Excel/PDF for official documentation | Low |符合公安报表规范 with watermarks |

---

## Differentiators

Features that set this product apart. Not expected by regulations, but valued by users and procurements.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| **Workflow engine with templates** | City-level unified workflow templates + district-level custom templates; flexible without losing standardization | Very High | Key competitive advantage; allows procurement to win contracts requiring local customization |
| **Intelligent storage allocation** | Auto-assign storage location based on evidence type, level, environmental needs | Medium | Reduces manual planning, prevents storage conflicts |
| **Environmental monitoring** | Real-time temperature, humidity, smoke, intrusion detection with alerts | Medium | Requires hardware integration (sensors) |
| **Proactive warning system** | Automatic alerts for: approaching retention deadline, overdue returns, environmental anomalies, incomplete workflows | Medium | Reduces manual tracking burden |
| **One-click inventory counts** | Scan all evidence codes in storage area, auto-compare with system records | Medium | Requires RFID/barcode scanner integration |
| **Leadership oversight dashboard** | Real-time command center view for supervisors: key metrics, warnings, unit comparisons | Medium | Supports hierarchical review |
| **Full lifecycle timeline view** | Click any evidence, see complete chronological record with photos, videos, documents | Low | Graphical visualization aids investigation |
| **Cross-unit transfer management** | Handle evidence moving between different police units with proper approvals and documentation | High | Critical for multi-level deployment |
| **Hardware integration (smart cabinets)** | Direct system-to-cabinet communication: auto-open relevant compartment, auto-lock after access | High | Requires hardware vendor partnerships |
| **Video/audio evidence linkage** | Link surveillance footage to specific evidence operations (checkout, return, disposal) | Medium | Provides additional chain of custody proof |
| **Data integrity verification** | Automated hash verification of all logs, periodic integrity checks, violation detection and alerting | Medium | Builds trust with auditors |

---

## Anti-Features

Features to explicitly NOT build. May be requested but are inappropriate.

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| **Evidence deletion** | Regulatory requirement: evidence records must be retained; logical deletion only | Use del_flag = '2' for soft delete; never physically delete |
| **External network access** | Security requirement: must operate on police internal network only | Pure internal network deployment; no external APIs |
| **Single-user mode** | Accountability requires multiple users with distinct identities | Proper authentication with badge number tracking |
| **Configurable role permissions that bypass two-person rules** | Dual control is regulatory requirement for sensitive operations | Two-person verification enforced at operation level, not configurable |
| **Evidence code reuse** | One-to-one code mapping is foundational | Lost/destroyed evidence marked with status, code retired |
| **Real-time chat/messaging between users** | Not evidence management; creates attack surface | Use police existing communication systems |
| **Cloud deployment outside government infrastructure** | Data sovereignty and security requirements | On-premise deployment only |
| **Evidence modification after storage** | Chain of custody integrity requirement | Supplementary information only; core fields immutable post-storage |

---

## Feature Dependencies

```
Evidence Registration → Storage Verification → Storage Receipt Generation
                                                    ↓
Evidence Callout Request → Graded Approval → Checkout Verification → Return Management
                                                    ↓
                                            Disposal Processing → Disposal Archiving

System Configuration → Evidence Classification → Intelligent Storage Allocation
                    ↘
User Management → Role Management → Multi-level Organization Support

All Operations → Operation Logs → Audit Logs (tamper-evident)
```

---

## MVP Recommendation (Phase 1)

Based on project constraints (delivery 2026-04-30, RuoYi framework), prioritize:

### Must Include (Table Stakes)
1. User/Role/Permission management with multi-level org support
2. Evidence one-code generation and registration
3. Evidence classification (5 types) and level (3 levels)
4. Storage workflow: verification, seal management, receipt generation
5. Callout/borrow workflow with graded approval
6. Checkout verification and return management
7. Operation logs with hash verification
8. Multi-condition query
9. Basic statistics and report export

### Defer to Phase 2 (Differentiators)
- Intelligent storage allocation (needs more hardware specs)
- Environmental monitoring (hardware not in scope)
- Cross-unit transfer management (complex, needs legal review)
- Leadership dashboard (can use basic query/statistics)
- Workflow engine templates (can use fixed workflows first)

---

## Phase 2+ Features

These are legitimate features but beyond MVP scope:

| Feature | Rationale for Deferral |
|---------|----------------------|
| Dual two-person lock control | Needs hardware integration |
| Intelligent storage allocation | Requires仓位定义 and rules config |
| Environmental monitoring | Requires hardware (sensors) |
| One-click inventory counts | Requires RFID scanner integration |
| Cross-unit transfer | Needs legal/compliance review |
| Workflow template customization | Can use fixed workflows first |
| Leadership dashboard enhancements | Basic stats sufficient for MVP |
| Hardware (smart cabinet) integration | Hardware not in scope |

---

## Sources

- 《公安机关物证管理规定》 (Public Security Evidence Management Regulations) -公安部令
- 《公安执法规范化建设要求》
- 物证管理系统需求规格说明书 (Requirements Specification Document)
- 物证管理系统功能列表 (Feature List)
- Project.md - MVP scope definition
