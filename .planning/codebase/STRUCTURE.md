# Codebase Structure

**Analysis Date:** 2026-03-28

## Directory Layout

```
/mnt/d/workspace/pems/
├── pems_backend/                    # Spring Boot backend
│   ├── ruoyi-admin/                 # Main application + Controllers
│   │   ├── src/main/java/com/ruoyi/
│   │   │   └── web/controller/     # REST controllers
│   │   └── src/main/resources/     # Config, mapper XML, i18n
│   ├── ruoyi-common/               # Shared module
│   │   └── src/main/java/com/ruoyi/common/
│   ├── ruoyi-framework/            # Security, config
│   ├── ruoyi-system/               # Business modules
│   │   └── src/main/java/com/ruoyi/system/
│   │       ├── service/            # Service interfaces + impl
│   │       ├── mapper/             # MyBatis interfaces
│   │       └── domain/             # Domain entities, VO
│   ├── ruoyi-generator/            # Code generator
│   ├── ruoyi-quartz/               # Scheduled tasks
│   └── sql/                        # Database scripts
│
├── pems_frontend/                  # Vue3 frontend
│   ├── src/
│   │   ├── api/                   # Axios API clients
│   │   ├── assets/                # Static assets
│   │   ├── components/            # Reusable Vue components
│   │   ├── directives/            # Custom Vue directives
│   │   ├── layout/                # App shell components
│   │   ├── plugins/               # Element Plus, cache
│   │   ├── router/                # Vue Router config
│   │   ├── store/                 # Pinia stores
│   │   ├── types/                 # TypeScript interfaces
│   │   ├── utils/                 # Utility functions
│   │   └── views/                  # Page components
│   ├── package.json
│   └── vite.config.ts
│
├── docs/                          # Documentation
│   ├── design/                    # Design documents
│   ├── meeting/                   # Meeting notes
│   ├── requirements/             # Requirements specs
│   └── ui/                        # UI mockups
│
└── .planning/codebase/            # This analysis output
```

## Backend Directory Purposes

### ruoyi-admin (Main Application)

**Purpose:** Application bootstrap and web layer

**Contains:**
- `RuoYiApplication.java` - Spring Boot entry point
- Controllers: `web/controller/system/`, `web/controller/monitor/`, `web/controller/tool/`
- Core config: `web/core/config/`

**Key Files:**
- `src/main/resources/application.yml` - Main configuration
- `src/main/resources/application-druid.yml` - Database pool config
- `src/main/resources/mybatis/` - MyBatis config
- `src/main/resources/mapper/` - SQL mapping XML files
- `src/main/resources/i18n/` - Internationalization

### ruoyi-common (Shared Module)

**Purpose:** Common utilities and base classes required by all modules

**Contains:**
- `annotation/` - Custom annotations
- `config/` - Common configuration
- `constant/` - Constants (HttpStatus, UserConstants, etc.)
- `core/controller/BaseController.java` - Controller base class
- `core/domain/` - AjaxResult, TableDataInfo, BaseEntity
- `core/domain/entity/` - SysUser, SysRole, SysDept, SysMenu
- `core/page/` - Pagination support classes
- `enums/` - Enumerations
- `exception/` - Exception classes
- `filter/` - Servlet filters
- `utils/` - Utility classes (60+ files)
- `xss/` - XSS filtering

### ruoyi-system (Business Module)

**Purpose:** Business logic implementation

**Contains:**
```
system/
├── service/
│   ├── ISysUserService.java       # Interface
│   └── impl/
│       └── SysUserServiceImpl.java
├── mapper/
│   └── SysUserMapper.java         # MyBatis interface
└── domain/
    ├── SysUser.java               # Entity
    ├── SysRole.java
    ├── SysDept.java
    ├── SysMenu.java
    └── vo/                        # View objects
```

**Module Structure per Entity (e.g., User):**
- `ISysUserService.java` - Service interface
- `SysUserServiceImpl.java` - Service implementation
- `SysUserMapper.java` - Mapper interface
- `SysUserMapper.xml` - MyBatis XML (in resources/mapper/system/)
- `SysUser.java` - Domain entity

### ruoyi-framework (Framework Config)

**Purpose:** Spring Security, web configuration

**Key Components:**
- `config/SecurityConfig.java` - Spring Security setup
- `aspectj/` - AOP aspects
- `manager/` - Async manager, scheduled tasks
- `shiro/` - Shiro subject (legacy)

### ruoyi-generator (Code Generator)

**Purpose:** Generate CRUD code from database tables

**Contains:** Generator templates,Velocity templates

### ruoyi-quartz (Scheduled Tasks)

**Purpose:** Job scheduling

**Contains:** Job entities, Quartz configuration

## Frontend Directory Purposes

### src/api/ (API Clients)

**Purpose:** Axios-based HTTP clients matching backend controllers

**Structure:**
```
api/
├── system/
│   ├── user.ts        # /system/user/*
│   ├── role.ts        # /system/role/*
│   ├── menu.ts        # /system/menu/*
│   ├── dept.ts        # /system/dept/*
│   └── dict/
├── monitor/
│   ├── online.ts
│   ├── job.ts
│   └── operlog.ts
└── tool/
    ├── gen.ts         # Code generation
    └── swagger.ts
```

### src/views/ (Page Components)

**Purpose:** Route-level Vue components

**Structure:**
```
views/
├── index.vue           # Dashboard
├── login.vue           # Login page
├── register.vue        # Registration
├── error/
│   ├── 401.vue
│   └── 404.vue
├── system/
│   ├── user/
│   │   ├── index.vue          # User list page
│   │   └── profile/            # User profile pages
│   ├── role/
│   │   ├── index.vue
│   │   └── authUser.vue
│   ├── dept/
│   ├── menu/
│   ├── post/
│   ├── dict/
│   └── notice/
├── monitor/
│   ├── online/
│   ├── job/
│   └── operlog/
└── tool/
    ├── gen/
    └── build/
```

### src/components/ (Reusable Components)

**Purpose:** Generic Vue components

**Key Components:**
- `Pagination/` - Table pagination wrapper
- `FileUpload/` - File upload with progress
- `ImageUpload/` - Image upload preview
- `Editor/` - Rich text editor (VueQuill)
- `Breadcrumb/` - Breadcrumb navigation
- `RightToolbar/` - Table column config
- `DictTag/` - Dictionary badge
- `RuoYi/` - Documentation links

### src/layout/ (App Shell)

**Purpose:** Application layout components

```
layout/
├── index.vue                    # Main layout
└── components/
    ├── Sidebar/                 # Left navigation
    ├── TopBar/                  # Header
    ├── TagsView/               # Tab navigation
    ├── Copyright/               # Footer
    └── Settings/               # Theme settings
```

### src/store/ (Pinia State)

**Purpose:** Reactive state management

**Modules:**
- `store/modules/user.ts` - User info, permissions, token
- `store/modules/permission.ts` - Routes, permissions
- `store/modules/tagsView.ts` - Opened tabs
- `store/modules/app.ts` - App settings
- `store/modules/dict.ts` - Dictionary cache
- `store/modules/settings.ts` - Theme settings

### src/router/ (Vue Router)

**Purpose:** Client-side routing

**Files:**
- `index.ts` - Router configuration
- `get-page-title.ts` - Document title helper

### src/utils/ (Utilities)

**Key Files:**
- `utils/request.ts` - Axios instance with interceptors
- `utils/auth.ts` - Token management
- `utils/ruoyi.ts` - Common helpers (parseStrEmpty, tansParams)
- `utils/errorCode.ts` - Error message mapping
- `utils/validate.ts` - Form validation rules
- `utils/generator/` - Code generator helpers

### src/types/ (TypeScript Definitions)

```
types/
├── api/
│   ├── system/
│   │   └── user.ts         # Type definitions for API responses
│   ├── monitor/
│   └── tool/
└── index.d.ts
```

## Key File Locations

### Backend Entry Points

| File | Purpose |
|------|---------|
| `ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java` | Spring Boot main class |
| `ruoyi-admin/src/main/resources/application.yml` | Application config |
| `ruoyi-admin/src/main/resources/application-druid.yml` | Database pool config |

### Backend Controller Examples

| File | Purpose |
|------|---------|
| `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysUserController.java` | User management API |
| `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLoginController.java` | Authentication API |
| `ruoyi-admin/src/main/java/com/ruoyi/web/controller/monitor/SysIndexController.java` | Dashboard API |

### Backend Service Examples

| File | Purpose |
|------|---------|
| `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysUserService.java` | User service interface |
| `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUserServiceImpl.java` | User service implementation |

### Backend Mapper Examples

| File | Purpose |
|------|---------|
| `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysUserMapper.java` | MyBatis interface |
| `ruoyi-admin/src/main/resources/mapper/system/SysUserMapper.xml` | SQL mapping |

### Frontend Entry Points

| File | Purpose |
|------|---------|
| `pems_frontend/index.html` | HTML entry |
| `pems_frontend/src/main.ts` | Application bootstrap |
| `pems_frontend/src/App.vue` | Root component |

### Frontend API Files

| File | Purpose |
|------|---------|
| `pems_frontend/src/api/system/user.ts` | User API client |
| `pems_frontend/src/utils/request.ts` | Axios instance |

### Frontend Page Components

| File | Purpose |
|------|---------|
| `pems_frontend/src/views/system/user/index.vue` | User management page |
| `pems_frontend/src/views/login.vue` | Login page |
| `pems_frontend/src/views/index.vue` | Dashboard |

## Naming Conventions

### Backend

**Files:**
- Controllers: `SysXxxController.java` (e.g., `SysUserController`)
- Services: `ISysXxxService.java`, `SysXxxServiceImpl.java`
- Mappers: `SysXxxMapper.java`
- Entities: `SysXxx.java` (in `domain/` or `entity/`)
- Enums: `XxxEnum.java` or `XxxType.java`
- Utils: `XxxUtils.java` or `XxxUtil.java`

**Methods:**
- Query: `selectXxxList`, `selectXxxById`
- Insert: `insertXxx`
- Update: `updateXxx`
- Delete: `deleteXxxById`, `deleteXxxByIds`
- Check: `checkXxxUnique`, `checkXxxAllowed`

**URL Paths (RESTful):**
- `GET /system/user/list` - List users
- `GET /system/user/{userId}` - Get user
- `POST /system/user` - Create user
- `PUT /system/user` - Update user
- `DELETE /system/user/{userId}` - Delete user

**Database:**
- Tables: `sys_xxx` (lowercase, underscore)
- Columns: `xxx_yyy` (lowercase, underscore)
- Java: `xxxYyy` (camelCase)

### Frontend

**Files:**
- API clients: `xxx.ts` (e.g., `user.ts`)
- Vue components: `PascalCase.vue` or `kebab-case.vue`
- Store modules: `xxx.ts` (e.g., `user.ts`)
- Utilities: `xxx.ts` (e.g., `request.ts`)

**Variables/Functions:**
- Variables: `camelCase` (e.g., `userList`, `pageSize`)
- Functions: `camelCase` (e.g., `listUser`, `addUser`)
- Components: `PascalCase` (e.g., `ElTable`, `UserProfile`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_COUNT`)

## Where to Add New Code

### New Backend Module

1. **Domain Entity** - `ruoyi-system/src/main/java/com/ruoyi/system/domain/Xxx.java`
   - Extend `BaseEntity`
   - Add JSR-303 validation annotations
   - Add `@Excel` for export

2. **Mapper Interface** - `ruoyi-system/src/main/java/com/ruoyi/system/mapper/XxxMapper.java`
   - Define CRUD methods
   - Use `@Param` for multiple parameters

3. **Mapper XML** - `ruoyi-admin/src/main/resources/mapper/system/XxxMapper.xml`
   - Write SQL in `<mapper>` with resultMap

4. **Service Interface** - `ruoyi-system/src/main/java/com/ruoyi/system/service/IXxxService.java`
   - Define business operations

5. **Service Impl** - `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/XxxServiceImpl.java`
   - `@Service` annotation
   - `@Transactional` for write operations
   - Inject mapper(s)

6. **Controller** - `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/XxxController.java`
   - Extend `BaseController`
   - `@RestController` + `@RequestMapping`
   - Add `@PreAuthorize` for permissions
   - Add `@Log` for audit

### New Frontend Feature

1. **API Client** - `pems_frontend/src/api/system/xxx.ts`
   - Export functions matching backend endpoints
   - Use `request()` with proper HTTP method

2. **Page Component** - `pems_frontend/src/views/system/xxx/index.vue`
   - Use Element Plus components
   - Include search form, table, pagination
   - Use `v-hasPermi` directive for buttons

3. **Types** - `pems_frontend/src/types/api/system/xxx.d.ts`
   - Define interfaces for API params/responses

4. **Store Module** (if needed) - `pems_frontend/src/store/modules/xxx.ts`
   - Use Pinia `defineStore`

5. **Route** - Add to dynamic routes in `permission.ts`
   - Include menu in `sys_menu` table

## Special Directories

### Backend Resources

**mapper/**
- Contains MyBatis XML files
- Subdirectory: `system/`, `monitor/`, `tool/`

**i18n/**
- Internationalization properties files
- Files: `messages.properties`, `messages_zh_CN.properties`

### Frontend Assets

**assets/**
- Static files: images, icons, styles
- `assets/icons/svg/` - Custom SVG icons

**node_modules/**
- npm dependencies
- **NOT committed to git**

### Generated/Downloaded

**ruoyi-admin/logs/**
- Runtime log files
- Not committed

**uploadPath (D:/ruoyi/uploadPath)**
- User-uploaded files
- Not committed

---

*Structure analysis: 2026-03-28*
