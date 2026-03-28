---
phase: 01-foundation-data-isolation
plan: 02
subsystem: infra
tags: [java, spring-boot, mybatis-plus, data-scope, unit-hierarchy]

# Dependency graph
requires:
  - phase: 01-foundation-data-isolation
    provides: sys_dept_extend table with unit_code field
provides:
  - Multi-level data isolation via unit_code hierarchy (city sees all districts, districts isolated)
  - Auto-generated unit_code on department insert/update (format: 000/000001/000001001)
  - DATA_SCOPE_UNIT_HIERARCHY = "6" for role-based unit filtering
affects:
  - 01-foundation-data-isolation (plans 03, 04)
  - Evidence core operations (data isolation depends on this)

# Tech tracking
tech-stack:
  added: [mybatis-plus-spring-boot3-starter:3.5.5]
  patterns:
    - Unit code hierarchy pattern: parent_code + 3-digit sequence
    - DataScope aspect extension for unit-level filtering

key-files:
  created:
    - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeptExtendMapper.java
    - pems_backend/ruoyi-system/src/main/resources/mapper/system/SysDeptExtendMapper.xml
  modified:
    - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeptServiceImpl.java
    - pems_backend/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeptMapper.java
    - pems_backend/ruoyi-system/src/main/resources/mapper/system/SysDeptMapper.xml
    - pems_backend/ruoyi-framework/src/main/java/com/ruoyi/framework/aspectj/DataScopeAspect.java
    - pems_backend/ruoyi-common/src/main/java/com/ruoyi/common/core/domain/entity/SysDept.java
    - pems_backend/pom.xml
    - pems_backend/ruoyi-common/pom.xml
    - pems_frontend/src/types/api/system/dept.ts
    - pems_frontend/src/views/system/dept/index.vue

key-decisions:
  - "Root dept (parentId=0) gets unit_code='000', children get parent_code + 3-digit sequence"
  - "City level (deptLevel=1) sees all descendant units via LIKE prefix match"
  - "District/station levels see only their own unit via exact match"
  - "Unit hierarchy filter is AND-ed with existing role-based data scopes"

patterns-established:
  - "Unit code generation: generateUnitCode() method in SysDeptServiceImpl"
  - "DataScope hierarchy: DATA_SCOPE_UNIT_HIERARCHY constant + getUnitCodeHierarchySql()"

requirements-completed: [ORG-01, ORG-02, ORG-03, ORG-04]

# Metrics
duration: ~15min
completed: 2026-03-28
---

# Phase 01 Plan 02: Organization & Data Scope Summary

**Multi-level data isolation with unit_code hierarchy: city sees all districts, districts isolated from each other, auto-generated unit codes on department create/update**

## Performance

- **Duration:** ~15 min
- **Started:** 2026-03-28T12:30:26Z
- **Completed:** 2026-03-28T12:45:00Z
- **Tasks:** 3
- **Files modified:** 13 (8 backend, 2 frontend, 3 pom/config)

## Accomplishments

- Implemented unit_code generation logic in SysDeptServiceImpl (root="000", children="parentXXX")
- Extended DataScopeAspect with DATA_SCOPE_UNIT_HIERARCHY = "6" for multi-level data isolation
- Added unitCode and deptLevel columns to frontend department management UI

## Task Commits

Each task was committed atomically:

1. **Task 1: Implement unit_code generation logic in SysDeptServiceImpl** - `f0c2f63` (feat)
2. **Task 2: Extend DataScopeAspect for unit_code hierarchy filtering** - `e5d24c5` (feat)
3. **Task 3: Add unit management to frontend and verify integration** - `962500d` (feat)

**Plan metadata commit:** (to be created after this summary)

## Files Created/Modified

**Backend (pems_backend):**

- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeptExtendMapper.java` - New mapper for sys_dept_extend CRUD
- `ruoyi-system/src/main/resources/mapper/system/SysDeptExtendMapper.xml` - SQL mappings for dept extend operations
- `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDeptServiceImpl.java` - Added generateUnitCode(), modified insertDept/updateDept
- `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDeptMapper.java` - Added SysDeptExtend import
- `ruoyi-system/src/main/resources/mapper/system/SysDeptMapper.xml` - Added unit_code/dept_level to resultMap and joins
- `ruoyi-framework/src/main/java/com/ruoyi/framework/aspectj/DataScopeAspect.java` - Added DATA_SCOPE_UNIT_HIERARCHY constant and getUnitCodeHierarchySql() method
- `ruoyi-common/src/main/java/com/ruoyi/common/core/domain/entity/SysDept.java` - Added TableField import for children field
- `pom.xml` - Added mybatis-plus-spring-boot3-starter:3.5.5 to dependencyManagement
- `ruoyi-common/pom.xml` - Added mybatis-plus dependency

**Frontend (pems_frontend):**

- `src/types/api/system/dept.ts` - Added unitCode and deptLevel fields to SysDept interface
- `src/views/system/dept/index.vue` - Added unitCode and deptLevel columns to table and dialog

## Decisions Made

- Used 3-digit sequence padding for unit_code (e.g., "001", "042") to maintain consistent format
- Root department (parentId=0) gets unit_code="000" with deptLevel=1
- Child departments increment parent's unit_code (parent="000", child="001", grandchild="000001")
- City-level (deptLevel=1) uses LIKE prefix match to see all descendants
- District/station levels use exact match for strict isolation
- Unit hierarchy filter is AND-combined with role-based DataScope filters

## Deviations from Plan

None - plan executed exactly as written.

## Issues Encountered

**Pre-existing compilation issues fixed:**

1. **[Rule 3 - Blocking] MyBatis-Plus dependency missing in ruoyi-common**
   - SysDept.java used `@TableField(exist = false)` annotation but mybatis-plus was not in dependencies
   - Added mybatis-plus-spring-boot3-starter:3.5.5 to parent pom.xml and ruoyi-common/pom.xml

## Next Phase Readiness

- sys_dept_extend table ready for data isolation
- DataScopeAspect supports DATA_SCOPE_UNIT_HIERARCHY = "6"
- Frontend displays unit_code and dept_level columns
- Ready for plan 01-03 (Role & Permission System)

---
*Phase: 01-foundation-data-isolation Plan 02*
*Completed: 2026-03-28*
