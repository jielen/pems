---
phase: 01-foundation-data-isolation
plan: 03
type: execute
wave: 2
status: completed
duration: ~5 minutes
completed_date: "2026-03-28T12:32:00Z"
commits:
  - 5fdd8bb: "feat(01-03): create PEMS roles and permissions SQL seed data"
  - 587b85c: "feat(01-03): implement immediate permission refresh on role update"
  - e90e4c3: "feat(01-03): update role management UI with PEMS roles and data scope display"
files_created:
  - pems_backend/sql/pems_data.sql
files_modified:
  - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysUserRoleMapper.java
  - pems_backend/ruoyi-system/src/main/resources/mapper/system/SysUserRoleMapper.xml
  - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysRoleService.java
  - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysRoleServiceImpl.java
  - pems_backend/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRoleController.java
  - pems_frontend/src/views/system/role/index.vue
key_decisions:
  - Used pems:* prefix for all PEMS module permissions (evidence, borrow, config, log, store)
  - Role data_scope values: super_admin=1(all), evidence_admin/reviewer/auditor=4(dept+children), investigator=5(self)
  - Predefined PEMS roles marked as non-deletable system roles in UI
  - Permission cache refresh iterates Redis login_tokens to update all affected users immediately
requirements_addressed:
  - ROLE-01: 4 predefined roles (super_admin, evidence_admin, investigator, auditor)
  - ROLE-02: Custom roles can be created via role management UI
  - ROLE-03: Permission changes take effect immediately (cache eviction via clearUserPermsCacheForRole)
  - AUTH-04: Permission format is pems:evidence:list, pems:borrow:apply, etc.
deviations: []
---

# Phase 01 Plan 03: PEMS Role and Permission System Summary

## One-liner

PEMS role and permission system with pems:* prefixed permissions, immediate cache refresh, and predefined roles (super_admin, evidence_admin, investigator, auditor).

## What Was Built

### Task 1: SQL Seed Data (pems_data.sql)

Created comprehensive SQL seed data with PEMS menu entries and predefined roles:

**PEMS Menu Structure (sys_menu):**
- Menu ID 3000: 物证管理 (PEMS Module Parent)
  - Menu ID 3001-3007: 物证查询/新增/编辑/删除/详情/导出/导入
- Menu ID 3010: 借用管理
  - Menu ID 3011-3014: 借用申请/审批/查询/归还
- Menu ID 3020: 系统配置
  - Menu ID 3021-3022: 配置查询/修改
- Menu ID 3030: 审计日志
  - Menu ID 3031-3033: 日志查询/导出/详情
- Menu ID 3040: 物证入库
  - Menu ID 3041-3043: 入库接收/登记/仓位分配

**Predefined Roles:**
| Role Key | Role Name | Data Scope | Permissions |
|----------|-----------|------------|-------------|
| super_admin | 超级管理员 | 1 (全部数据) | All PEMS permissions |
| evidence_admin | 物证管理员 | 4 (本部门及以下) | Evidence + Config + Storage |
| investigator | 办案民警 | 5 (仅本人) | Evidence query only |
| auditor | 审计员 | 4 (本部门及以下) | Audit logs only |
| reviewer | 审核员 | 4 (本部门及以下) | Borrow approval only |

### Task 2: Immediate Permission Refresh

Enhanced `SysRoleServiceImpl` and `SysRoleController` with cache eviction:

**Changes:**
- Added `selectUserIdsByRoleId` method to `SysUserRoleMapper` for querying users by role
- Added `clearUserPermsCacheForRole(Long roleId)` method to refresh all affected users' permissions
- Controller now calls `clearUserPermsCacheForRole` after successful role updates
- When role permissions or data scope changes, all cached LoginUser objects in Redis are updated immediately

**Mechanism:**
1. Query `sys_user_role` to find all users with the modified role
2. Iterate Redis keys matching `login_tokens:*`
3. For matching users, reload permissions from DB and update Redis cache
4. Next API call by affected users reflects new permissions without logout

### Task 3: Role Management UI Updates

Enhanced `role/index.vue` with PEMS-specific features:

**Changes:**
- Added data scope column showing 全部数据/本部门及以下/仅本人
- Added system role badge for predefined PEMS roles (super_admin, evidence_admin, investigator, auditor, reviewer)
- Predefined roles show '系统' tag instead of action buttons (non-deletable)
- Added `isSystemRole()` helper to identify predefined roles
- Added `getDataScopeLabel()` helper for data scope display

## Commits

| Task | Commit | Description |
|------|--------|-------------|
| 1 | 5fdd8bb | Create PEMS roles and permissions SQL seed data |
| 2 | 587b85c | Implement immediate permission refresh on role update |
| 3 | e90e4c3 | Update role management UI with PEMS roles and data scope display |

## Verification

- All PEMS permissions use pems:* prefix (25 references in SQL)
- 5 predefined roles created with proper data scope
- Role-Permission mappings established per role responsibilities
- Permission cache refresh implemented and integrated into controller
- Frontend displays data scope and system role indicators

## Deviations from Plan

None - plan executed exactly as written.

## Auth Gates

None encountered.

## Known Stubs

None.

## Self-Check

- [x] pems_data.sql created with all PEMS menu items and permissions
- [x] 5 predefined roles with proper data_scope values
- [x] clearUserPermsCacheForRole method implemented in service layer
- [x] Controller integrated with cache refresh on role updates
- [x] Frontend shows data scope column and system role indicators
- [x] All 3 commits exist and match task descriptions

## Self-Check: PASSED
