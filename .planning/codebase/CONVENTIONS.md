# Coding Conventions

**Analysis Date:** 2026-03-28

## Naming Patterns

### Java (Backend)

**Files:**
- Controller: `SysXxxController.java` - e.g., `SysUserController.java`
- Service Interface: `ISysXxxService.java` - e.g., `ISysUserService.java`
- Service Implementation: `SysXxxServiceImpl.java` - e.g., `SysUserServiceImpl.java`
- Mapper Interface: `SysXxxMapper.java` - e.g., `SysUserMapper.java`
- Mapper XML: `SysXxxMapper.xml` - e.g., `SysUserMapper.xml`
- Entity/Domain: `SysXxx.java` - e.g., `SysUser.java`, `BaseEntity.java`
- Enums: `XxxType.java`, `XxxEnum.java` - e.g., `BusinessType.java`, `UserStatus.java`
- Constants: `XxxConstants.java` - e.g., `UserConstants.java`

**Methods:**
- Query methods: `selectXxxList`, `selectXxxById`, `selectXxxByXxx`
- Insert methods: `insertXxx`, `addXxx`
- Update methods: `updateXxx`
- Delete methods: `deleteXxx`, `deleteXxxByIds`
- Check methods: `checkXxxUnique`, `checkXxxAllowed`, `checkXxxDataScope`
- Business methods: Use camelCase - e.g., `checkUserNameUnique`, `importUser`

**Variables:**
- camelCase - e.g., `userId`, `userName`, `deptId`
- Boolean methods: `isXxx()`, `checkXxx()`, `hasXxx()`

**Types:**
- Database fields: snake_case - e.g., `user_id`, `create_time`
- Java properties: camelCase - e.g., `userId`, `createTime`
- Constants: UPPER_SNAKE_CASE - e.g., `NORMAL = "0"`, `UNIQUE = true`

### TypeScript/Vue (Frontend)

**Files:**
- API: `xxx.ts` - e.g., `user.ts`, `role.ts`
- Components: `PascalCase.vue` - e.g., `UserManagement.vue`
- Store modules: `xxx.ts` - e.g., `user.ts`
- Types: `xxx.ts` in `types/api/` - e.g., `types/api/system/user.ts`
- Utils: `xxx.ts` - e.g., `request.ts`, `auth.ts`

**Variables and Functions:**
- camelCase - e.g., `userList`, `queryParams`, `handleAdd`
- Constants: UPPER_SNAKE_CASE for exported constants
- Type interfaces: PascalCase - e.g., `SysUser`, `UserQueryParams`, `AjaxResult`

**Vue Components:**
- Script setup with `lang="ts"` - `<script setup lang="ts" name="User">`
- Template refs: camelCase - e.g., `userRef`, `queryRef`
- Dict usage: `proxy.useDict("dict_type1", "dict_type2")`

## Code Style

### Java Formatting

**Indentation:** 4 spaces

**Braces:**
```java
// Standard brace style
if (condition) {
    doSomething();
} else {
    doOther();
}
```

**Imports:**
```java
// java.* first, then javax.*, then third-party
import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// Then com.ruoyi imports
import com.ruoyi.common.annotation.Log;
```

**Annotations:**
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

### TypeScript/Vue Formatting

**Quotes:** Single quotes for strings, double quotes for JSX attributes

**Semicolons:** Used at end of statements

**Type Annotations:**
```typescript
// Explicit types for function parameters and return values
export function listUser(query: UserQueryParams): Promise<TableDataInfo<SysUser[]>> {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}
```

**Vue Script Setup:**
```typescript
<script setup lang="ts" name="User">
import { getToken } from "@/utils/auth"
import useAppStore from '@/store/modules/app'
import type { SysUser, UserQueryParams } from '@/types/api/system/user'

const router = useRouter()
const appStore = useAppStore()
const { proxy } = getCurrentInstance()
const { sys_normal_disable, sys_user_sex } = proxy.useDict("sys_normal_disable", "sys_user_sex")

const userList = ref<SysUser[]>([])
const open = ref<boolean>(false)
</script>
```

## Import Organization

### Java
1. java.* standard library
2. javax.* extensions
3. Third-party libraries (org.*, com.*)
4. com.ruoyi internal imports

### TypeScript
```typescript
// External imports first
import axios from 'axios'
import { ElNotification, ElMessageBox, ElMessage } from 'element-plus'
// Then internal @ path imports
import { getToken } from '@/utils/auth'
import type { SysUser } from '@/types/api/system/user'
```

**Path Aliases (Frontend):**
- `@/*` maps to `src/*`
- `~/*` maps to project root

## Error Handling

### Java

**Service Layer:**
```java
@Override
public void checkUserAllowed(SysUser user) {
    if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin()) {
        throw new ServiceException("不允许操作超级管理员用户");
    }
}

// Validation
if (StringUtils.isNull(userList) || userList.size() == 0) {
    throw new ServiceException("导入用户数据不能为空！");
}
```

**Controller Layer:**
```java
@PostMapping
public AjaxResult add(@Validated @RequestBody SysUser user) {
    if (!userService.checkUserNameUnique(user)) {
        return error("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
    }
    return toAjax(userService.insertUser(user));
}
```

**Try-Catch Pattern:**
```java
try {
    // operation
} catch (Exception e) {
    failureNum++;
    String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
    failureMsg.append(msg + e.getMessage());
    log.error(msg, e);
}
```

### TypeScript

**Axios Interceptor:**
```typescript
// request interceptor
service.interceptors.request.use((config: any) => {
  // token handling
  if (getToken() && !isToken) {
    config.headers['Authorization'] = 'Bearer ' + getToken()
  }
  return config
})

// response interceptor
service.interceptors.response.use((res: any) => {
  const code = res.data.code || 200
  if (code === 401) {
    // handle unauthorized
  } else if (code === 500) {
    ElMessage({ message: msg, type: 'error' })
    return Promise.reject(new Error(msg))
  }
  return Promise.resolve(res.data)
}, (error: any) => {
  console.log('err' + error)
  ElMessage({ message: message, type: 'error', duration: 5 * 1000 })
  return Promise.reject(error)
})
```

## Logging

### Java
```java
// Use SLF4J Logger
protected final Logger logger = LoggerFactory.getLogger(this.getClass());

// In service implementations
private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

// Usage
log.error(msg, e);
log.warn("系统接口请求超时");
```

### TypeScript
```typescript
// console.warn for warnings
console.warn(`[${config.url}]: ` + message)

// console.error for errors
console.error(r)

// Avoid console.log in production
```

## Comments

### Java
```java
/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
public class SysUser extends BaseEntity {
    /** 用户ID */
    @Excel(name = "用户序号", type = Type.EXPORT, cellType = ColumnType.NUMERIC)
    private Long userId;
}
```

### TypeScript/Vue
```typescript
/** 用户分页查询参数 */
export interface UserQueryParams extends PageDomain {
  /** 用户名称 */
  userName?: string;
}
```

## Function Design

### Java

**Controller Method Pattern:**
```java
@PreAuthorize("@ss.hasPermi('system:user:list')")
@GetMapping("/list")
public TableDataInfo list(SysUser user) {
    startPage();
    List<SysUser> list = userService.selectUserList(user);
    return getDataTable(list);
}
```

**Service Method Pattern:**
```java
@Override
@Transactional
public int insertUser(SysUser user) {
    int rows = userMapper.insertUser(user);
    insertUserPost(user);
    insertUserRole(user);
    return rows;
}
```

**Method Parameters:**
- Use `@Param` for MyBatis mapper parameters when multiple parameters exist
- Use `@RequestBody` for POST/PUT request bodies
- Use `@PathVariable` for path parameters
- Use `@RequestParam` or implicit for query parameters

### TypeScript

**API Function Pattern:**
```typescript
export function listUser(query: UserQueryParams): Promise<TableDataInfo<SysUser[]>> {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

export function addUser(data: SysUser): Promise<AjaxResult> {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}
```

## Module Design

### Java Package Structure
```
com.ruoyi.web.controller.system/    # Controllers
com.ruoyi.system.service/           # Service interfaces
com.ruoyi.system.service.impl/    # Service implementations
com.ruoyi.system.mapper/           # MyBatis mappers
com.ruoyi.common.core.domain/      # Core domains
com.ruoyi.common.core.entity/      # Core entities
com.ruoyi.common.enums/            # Enums
com.ruoyi.common.constant/         # Constants
com.ruoyi.common.annotation/       # Custom annotations
com.ruoyi.common.utils/            # Utilities
```

### Frontend Structure
```
src/
  api/               # API calls
    system/          # System module APIs
    monitor/         # Monitor module APIs
  views/             # Vue page components
    system/          # System module pages
  store/             # Pinia stores
    modules/         # Store modules
  types/             # TypeScript types
    api/             # API response types
  utils/             # Utility functions
  components/        # Reusable components
```

## Database Conventions

### Table Naming
- System tables: `sys_xxx` (e.g., `sys_user`, `sys_role`, `sys_menu`)
- Business tables: `pems_xxx` (e.g., `pems_evidence`)

### Required Fields
```sql
CREATE TABLE xxx (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    del_flag        CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME COMMENT '更新时间',
    remark          VARCHAR(500) COMMENT '备注'
);
```

### Field Naming
- Java property: `userId` (camelCase)
- Database column: `user_id` (snake_case)

## API Response Format

### Standard Response (AjaxResult)
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... }
}
```

### Paginated Response (TableDataInfo)
```json
{
  "code": 200,
  "msg": "查询成功",
  "rows": [...],
  "total": 100
}
```

### Error Response
```json
{
  "code": 500,
  "msg": "错误信息"
}
```

## Transaction Management

### Java
```java
@Override
@Transactional
public int insertUser(SysUser user) {
    int rows = userMapper.insertUser(user);
    insertUserPost(user);
    insertUserRole(user);
    return rows;
}
```

## Validation

### Java Bean Validation
```java
@Xss(message = "用户昵称不能包含脚本字符")
@Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
public String getNickName() {
    return nickName;
}

@NotBlank(message = "用户账号不能为空")
@Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
public String getUserName() {
    return userName;
}

@Email(message = "邮箱格式不正确")
public String getEmail() {
    return email;
}
```

### TypeScript Validation
```typescript
const rules = {
  userName: [
    { required: true, message: "用户名称不能为空", trigger: "blur" },
    { min: 2, max: 20, message: "用户名称长度必须介于 2 和 20 之间", trigger: "blur" }
  ],
  password: [
    { required: true, message: "用户密码不能为空", trigger: "blur" },
    { pattern: /^[^<>"'|\\]+$/, message: "不能包含非法字符", trigger: "blur" }
  ],
  email: [{ type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }],
  phonenumber: [{ pattern: /^1[3|4|5|6|7|8|9][0-9]\d{8}$/, message: "请输入正确的手机号码", trigger: "blur" }]
}
```

---

*Convention analysis: 2026-03-28*
