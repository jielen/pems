# Architecture

**Analysis Date:** 2026-03-28

## Pattern Overview

**Overall:** Layered Architecture with Service-Oriented Design (RuoYi Framework)

Based on RuoYi v3.9.1 framework, this is a traditional multi-tier enterprise application:

- **Backend:** Spring Boot 4.0.3 with MyBatis-Plus ORM
- **Frontend:** Vue3 SPA with Element Plus UI
- **Authentication:** JWT token-based
- **Database:** MySQL 8.0 with Druid connection pool

**Key Characteristics:**
- Strict layer separation: Controller -> Service -> Mapper
- Interface-based service design (IService + ServiceImpl)
- MyBatis XML-based SQL mapping
- RESTful API convention
- Permission-based access control (RBAC)

## Layers

### Controller Layer (Web)

- **Purpose:** Handle HTTP requests, response formatting, and authorization
- **Location:** `ruoyi-admin/src/main/java/com/ruoyi/web/controller/`
- **Contains:** REST controllers for system modules (user, role, menu, dept, etc.)
- **Depends on:** Service layer interfaces
- **Used by:** Frontend SPA via Axios

**Key Pattern:**
```java
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }
}
```

### Service Layer (Business Logic)

- **Purpose:** Business logic, transaction management, data validation
- **Location:** `ruoyi-system/src/main/java/com/ruoyi/system/service/`
- **Contains:** Interface (ISysXxxService) + Implementation (SysXxxServiceImpl)
- **Depends on:** Mapper layer
- **Used by:** Controller layer

**Key Pattern:**
```java
@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper userMapper;

    @Override
    @Transactional
    public int insertUser(SysUser user) {
        int rows = userMapper.insertUser(user);
        insertUserPost(user);
        insertUserRole(user);
        return rows;
    }
}
```

### Mapper Layer (Data Access)

- **Purpose:** Database operations via MyBatis
- **Location:** `ruoyi-system/src/main/java/com/ruoyi/system/mapper/`
- **Contains:** MyBatis interfaces with annotation-based or XML-based SQL
- **Depends on:** MyBatis-Plus, Druid connection pool
- **Used by:** Service layer
- **XML Location:** `ruoyi-admin/src/main/resources/mapper/`

**Key Pattern:**
```java
public interface SysUserMapper {
    List<SysUser> selectUserList(SysUser sysUser);
    int insertUser(SysUser user);
    @Param("userId") Long userId  // Multiple params require @Param
}
```

### Common Module (Shared)

- **Purpose:** Shared utilities, base classes, annotations, domain objects
- **Location:** `ruoyi-common/src/main/java/com/ruoyi/common/`
- **Key Components:**
  - `core/controller/BaseController.java` - Base controller with pagination, response helpers
  - `core/domain/BaseEntity.java` - Entity base with audit fields (createBy, createTime, updateBy, updateTime, remark)
  - `core/domain/AjaxResult.java` - Unified API response format
  - `core/domain/TableDataInfo.java` - Paginated response wrapper
  - `annotation/` - Custom annotations (@Log, @DataScope, @RateLimiter, @RepeatSubmit)
  - `enums/` - Enumerations (BusinessType, OperatorType, UserStatus)
  - `utils/` - Utility classes (SecurityUtils, StringUtils, DateUtils, etc.)

### Framework Module (Configuration)

- **Purpose:** Spring Security, CORS, web configuration
- **Location:** `ruoyi-framework/src/main/java/com/ruoyi/framework/`
- **Contains:** Security config, permission handler, token filter

### Frontend (Vue3 SPA)

- **Purpose:** Single-page application UI
- **Location:** `pems_frontend/src/`
- **Key Files:**
  - `api/` - Axios-based API clients matching backend controllers
  - `views/` - Vue page components
  - `store/` - Pinia state management
  - `router/` - Vue Router with permission-based dynamic routes

## Data Flow

### Read Flow (GET Request)

1. **Frontend:** `src/api/system/user.ts` calls `listUser(query)` using Axios
2. **HTTP:** GET `/system/user/list?pageNum=1&pageSize=10`
3. **Controller:** `SysUserController.list(SysUser)` receives request
4. **BaseController:** `startPage()` sets pagination via PageHelper thread-local
5. **Service:** `ISysUserService.selectUserList(user)` executes business logic
6. **Mapper:** `SysUserMapper.selectUserList()` queries MySQL
7. **Response:** `TableDataInfo` with `rows[]` and `total` count
8. **Frontend:** Receives JSON, updates `el-table` via reactive binding

### Write Flow (POST Request)

1. **Frontend:** `addUser(data)` sends JSON body
2. **Controller:** Validates `@Validated` input, checks permissions `@PreAuthorize`
3. **BaseController:** `getUsername()` retrieves from security context
4. **Service:** `@Transactional` business logic via `ServiceImpl`
5. **Mapper:** `insertUser()` with auto-generated ID
6. **Response:** `AjaxResult.success()` or `AjaxResult.error(message)`

### Authentication Flow

1. Login: `POST /system/login` with username/password
2. `SysLoginService` validates credentials via `SecurityUtils`
3. `JwtUtils` generates token with expiration
4. Frontend stores token in `localStorage` via `src/utils/auth.ts`
5. Subsequent requests: `Authorization: Bearer {token}` header
6. `JwtAuthenticationFilter` validates token, populates `SecurityContext`

## Key Abstractions

### Unified Response Format (AjaxResult)

```java
// Extends HashMap, uses put() for chain-style API
AjaxResult.success()           // { code: 200, msg: "操作成功" }
AjaxResult.success(data)       // { code: 200, msg: "操作成功", data: {...} }
AjaxResult.error("失败原因")   // { code: 500, msg: "失败原因" }
AjaxResult.warn("警告")        // { code: 301, msg: "警告" }
```

### Paginated Response (TableDataInfo)

```java
// Used for list endpoints
{
  "code": 200,
  "msg": "查询成功",
  "rows": [...],      // Current page data
  "total": 100        // Total record count
}
```

### Base Entity (BaseEntity)

All domain entities extend `BaseEntity` which provides:
- `createBy`, `createTime` - Audit fields
- `updateBy`, `updateTime` - Audit fields
- `remark` - Comment/description
- `searchValue` - Internal search parameter
- `params` - Extended request parameters

### Permission Expression (@ss)

```java
@PreAuthorize("@ss.hasPermi('system:user:add')")   // Permission check
@PreAuthorize("@ss.hasRole('admin')")            // Role check
@PreAuthorize("@ss.hasPermi('system:user:edit') and @ss.hasPermi('system:user:list')")
```

## Entry Points

### Backend Application

- **Location:** `ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java`
- **Type:** Spring Boot application with `@SpringBootApplication`
- **Excludes:** DataSource auto-configuration (managed separately)

### Frontend Application

- **Entry:** `pems_frontend/index.html`
- **Main JS:** `pems_frontend/src/main.ts`
- **Root Component:** `pems_frontend/src/App.vue`

### API Base Path

- Backend: `http://localhost:8080/`
- Frontend Dev: `http://localhost:5173/` (Vite dev server)
- Frontend Proxies to: `http://localhost:8080/`

## Error Handling

### Backend

- **ServiceException:** Business rule violations
- **AjaxResult:** Error responses with HTTP 200 (code indicates success/failure)
- **@ControllerAdvice:** Global exception handling (if configured)

### Frontend

- **Axios Interceptor:** Catches HTTP 401, 500, network errors
- **Error Code Mapping:** `src/utils/errorCode.ts`
- **Retry Logic:** Session expiry prompts re-login

### Logging

- Backend: SLF4J with Logback (configured in `application.yml`)
- Log levels: `com.ruoyi: debug`, `org.springframework: warn`
- Location: `ruoyi-admin/logs/`

## Cross-Cutting Concerns

### Authentication

- JWT tokens with 30-minute expiration
- Token header: `Authorization: Bearer {token}`
- Redis session storage (optional)

### Authorization

- RBAC (Role-Based Access Control)
- Permission strings: `module:entity:operation`
- Data scope filtering via `@DataScope` annotation

### Validation

- JSR-303 annotations on entity fields
- Custom validators (e.g., `@Xss` for XSS prevention)
- Frontend: Element Plus form validation

### Logging/Audit

- `@Log(title="用户管理", businessType=BusinessType.INSERT)` annotation
- Operation logs stored in `sys_oper_log` table
- Login logs stored in `sys_logininfor` table

### Transaction

- `@Transactional` on service methods
- Automatic rollback on unchecked exceptions

### XSS Protection

- Filter enabled via `xss.enabled=true` in config
- URL patterns: `/system/*,/monitor/*,/tool/*`

---

*Architecture analysis: 2026-03-28*
