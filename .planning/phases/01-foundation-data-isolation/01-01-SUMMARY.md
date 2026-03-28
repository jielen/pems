---
phase: 01-foundation-data-isolation
plan: 01
type: execute
wave: 1
status: completed
duration: ~5 minutes
completed_date: "2026-03-28T12:25:20Z"
commits:
  - 8cd936e: "feat(01-foundation-data-isolation): create foundation schema with 3 tables"
  - e48c6b3: "feat(01-foundation-data-isolation): create PemsConfig and PemsAuditLog entities"
  - c6fcc31: "feat(01-foundation-data-isolation): create SysDeptExtend entity and extend SysDept"
files_created:
  - pems_backend/sql/pems_schema.sql
  - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/domain/PemsConfig.java
  - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/domain/PemsAuditLog.java
  - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDeptExtend.java
files_modified:
  - pems_backend/ruoyi-common/src/main/java/com/ruoyi/common/core/domain/entity/SysDept.java
key_decisions:
  - Extended SysDept instead of creating new entity to minimize framework changes
  - sys_dept_extend linked via FK to sys_dept for data integrity
  - PemsAuditLog uses builder pattern and internal hash setters for HMAC-SHA256 chain
deviations: []
requirements_addressed:
  - CONFIG-01 through CONFIG-07: Pre-populated in pems_config table
  - LOG-01 through LOG-04: Hash chain implementation started in entity design
---

# Phase 01 Plan 01: Foundation Schema Summary

## One-liner

Database schema foundation with pems_config, pems_audit_log (HMAC-SHA256 hash chain), and sys_dept_extend tables for data isolation.

## What Was Built

Created the foundation database schema and Java entities for Phase 1 data isolation:

### Database Tables (pems_schema.sql)

1. **pems_config** - System configuration storage
   - Stores CONFIG-01 through CONFIG-07 defaults
   - Includes: evidence_type, evidence_level, retention_period, numbering_rule, dual_lock settings, alert_rules

2. **pems_audit_log** - Immutable audit log with HMAC-SHA256 hash chain
   - prev_hash and hash_value columns for chain integrity
   - No update_time/update_by (immutable design)
   - Supports both evidence operations and system operations

3. **sys_dept_extend** - Department extension for multi-level unit hierarchy
   - unit_code field (format: parent_code + 3-digit sequence)
   - dept_level field (1=city, 2=district, 3=station)
   - ancestors_path for DataScope queries

### Java Entities

1. **PemsConfig.java** - MyBatis-Plus annotated entity
   - @TableName("pems_config")
   - @NotBlank validation on paramKey
   - Follows RuoYi BaseEntity convention

2. **PemsAuditLog.java** - Immutable audit log entity
   - @TableName("pems_audit_log")
   - Builder pattern for construction
   - Internal methods assignPrevHash/assignHashValue for hash chain computation
   - No public setters for hash-related fields

3. **SysDeptExtend.java** - Department extension entity
   - @TableName("sys_dept_extend")
   - FK to sys_dept via dept_id

4. **SysDept.java (extended)** - Added fields
   - unitCode (String)
   - deptLevel (Integer)
   - @TableField(exist = false) for children
   - Updated toString() with new fields

## Commits

| Task | Commit | Description |
|------|--------|-------------|
| 1 | 8cd936e | Create foundation schema with 3 tables |
| 2 | e48c6b3 | Create PemsConfig and PemsAuditLog entities |
| 3 | c6fcc31 | Create SysDeptExtend and extend SysDept |

## Verification

- All 3 tables created with proper fields and comments
- All entities compile and follow RuoYi conventions
- pems_audit_log has prev_hash and hash_value for HMAC-SHA256 chain
- sys_dept_extend linked to sys_dept via dept_id PK

## Deviations from Plan

None - plan executed exactly as written.

## Auth Gates

None encountered.

## Known Stubs

None.

## Self-Check

- [x] pems_schema.sql created with all 3 tables
- [x] pems_config pre-populated with CONFIG-01~07 defaults
- [x] pems_audit_log has prev_hash and hash_value columns
- [x] sys_dept_extend has unit_code and dept_level fields
- [x] All 3 commits exist and match task descriptions

## Self-Check: PASSED
