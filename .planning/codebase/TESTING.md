# Testing Patterns

**Analysis Date:** 2026-03-28

## Test Framework

### Backend (Java)

**Not detected** - No test framework is currently configured in the project.

**Required Dependencies (pom.xml additions):**
```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito (included in spring-boot-starter-test) -->
<!-- AssertJ for assertions -->
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- H2 for in-memory database testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

**Run Commands:**
```bash
mvn test                              # Run all tests
mvn test -Dtest=*ServiceTest         # Run specific test class
mvn test -Dtest=*ServiceTest#test*   # Run specific test method
mvn test -DfailIfNoTests=false        # Don't fail if no tests found
mvn test -Dsurefire.reportFormat=plain # Plain text output
```

**Test Directory:** `src/test/java/com/ruoyi/`

### Frontend (Vue3/TypeScript)

**Not detected** - No test framework is currently configured.

**Required Dependencies (package.json additions):**
```json
{
  "devDependencies": {
    "vitest": "^2.0.0",
    "@vue/test-utils": "^2.4.0",
    "jsdom": "^24.0.0",
    "@testing-library/vue": "^8.0.0",
    "@testing-library/jest-dom": "^6.0.0"
  },
  "scripts": {
    "test": "vitest",
    "test:coverage": "vitest run --coverage",
    "test:ui": "vitest --ui"
  }
}
```

**Vitest Configuration (vitest.config.ts):**
```typescript
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  test: {
    globals: true,
    environment: 'jsdom',
    include: ['src/**/*.{test,spec}.{js,ts}'],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html']
    }
  }
})
```

**Run Commands:**
```bash
npm test               # Run all tests (watch mode)
npm run test:coverage  # Run with coverage report
npm run test:ui        # Run with UI browser
```

**Test Directory:** `src/**/*.test.ts` or `src/**/*.spec.ts`

## Test File Organization

### Backend

**Location:** `src/test/java/com/ruoyi/`

**Naming:**
- Test classes: `XxxServiceTest.java`, `XxxControllerTest.java`
- Test methods: `testMethodName()`, `shouldDoXxx()`

**Structure:**
```
src/test/java/com/ruoyi/
├── system/
│   └── service/
│       └── SysUserServiceTest.java
├── web/
│   └── controller/
│       └── SysUserControllerTest.java
└── common/
    └── utils/
        └── StringUtilsTest.java
```

### Frontend

**Location:** Co-located with source files or in `tests/` directory

**Naming:**
- Test files: `xxx.test.ts`, `xxx.spec.ts`

**Structure:**
```
src/
├── api/
│   └── system/
│       └── user.test.ts
├── views/
│   └── system/
│       └── user/
│           └── index.test.ts
├── store/
│   └── modules/
│       └── user.test.ts
└── tests/
    └── setup.ts
```

## Test Structure

### Java Unit Tests

**Service Layer Test Pattern:**
```java
package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SysUserServiceTest {

    @Mock
    private SysUserMapper userMapper;

    @InjectMocks
    private SysUserServiceImpl userService;

    @Test
    void testSelectUserByUserName() {
        // Given
        SysUser expectedUser = new SysUser();
        expectedUser.setUserName("admin");
        when(userMapper.selectUserByUserName("admin")).thenReturn(expectedUser);

        // When
        SysUser result = userService.selectUserByUserName("admin");

        // Then
        assertNotNull(result);
        assertEquals("admin", result.getUserName());
        verify(userMapper, times(1)).selectUserByUserName("admin");
    }

    @Test
    void testCheckUserNameUnique_WhenUserExists() {
        // Given
        SysUser existingUser = new SysUser();
        existingUser.setUserId(1L);
        existingUser.setUserName("admin");
        when(userMapper.checkUserNameUnique("admin")).thenReturn(existingUser);

        SysUser newUser = new SysUser();
        newUser.setUserName("admin");

        // When
        boolean result = userService.checkUserNameUnique(newUser);

        // Then
        assertFalse(result);
    }
}
```

**Controller Layer Test Pattern (MockMvc):**
```java
package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SysUserController.class)
class SysUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISysUserService userService;

    @Test
    @WithMockUser(roles = "admin")
    void testListUsers() throws Exception {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("test");
        when(userService.selectUserList(any(SysUser.class)))
            .thenReturn(java.util.Arrays.asList(user));

        mockMvc.perform(get("/system/user/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rows").isArray());
    }
}
```

**Integration Test Pattern:**
```java
@SpringBootTest
class SysUserServiceIntegrationTest {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private DataSource dataSource;

    @Test
    void testInsertUser() {
        // Given
        SysUser user = new SysUser();
        user.setUserName("testuser");
        user.setNickName("Test User");
        user.setPassword("123456");
        user.setDeptId(1L);

        // When
        int result = userService.insertUser(user);

        // Then
        assertTrue(result > 0);
        assertNotNull(user.getUserId());
    }
}
```

### TypeScript/Vue Tests

**API Test Pattern:**
```typescript
import { describe, it, expect, vi } from 'vitest'
import { listUser, addUser, getUser } from '@/api/system/user'
import axios from 'axios'

vi.mock('axios')
const mockedAxios = axios as jest.Mocked<typeof axios>

describe('User API', () => {
  it('should fetch user list', async () => {
    const mockData = {
      code: 200,
      msg: '查询成功',
      rows: [{ userId: 1, userName: 'admin' }],
      total: 1
    }
    mockedAxios.create.mockReturnValue({
      get: vi.fn().mockResolvedValue({ data: mockData }),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    } as any)

    const result = await listUser({ pageNum: 1, pageSize: 10 })
    expect(result.rows).toHaveLength(1)
    expect(result.total).toBe(1)
  })

  it('should add new user', async () => {
    const mockResponse = { code: 200, msg: '操作成功' }
    mockedAxios.create.mockReturnValue({
      post: vi.fn().mockResolvedValue({ data: mockResponse })
    } as any)

    const newUser = { userName: 'newuser', nickName: 'New User' }
    const result = await addUser(newUser)
    expect(result.code).toBe(200)
  })
})
```

**Vue Component Test Pattern:**
```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createTestingPinia } from '@pinia/testing'
import UserList from '@/views/system/user/index.vue'

describe('UserList Component', () => {
  it('renders user list', () => {
    const wrapper = mount(UserList, {
      global: {
        plugins: [createTestingPinia()]
      }
    })

    expect(wrapper.find('.app-container').exists()).toBe(true)
  })

  it('handles search query', async () => {
    const wrapper = mount(UserList, {
      global: {
        plugins: [createTestingPinia()]
      }
    })

    const searchInput = wrapper.find('input')
    await searchInput.setValue('admin')

    expect(wrapper.vm.queryParams.userName).toBe('admin')
  })
})
```

**Store (Pinia) Test Pattern:**
```typescript
import { describe, it, expect } from 'vitest'
import { createTestingPinia } from '@pinia/testing'
import useUserStore from '@/store/modules/user'

describe('User Store', () => {
  const createStore = () => {
    return createTestingPinia({
      initialState: {
        user: {
          token: 'test-token',
          userInfo: { userId: 1, userName: 'admin' }
        }
      }
    })
  }

  it('has token after login', () => {
    const pinia = createStore()
    const store = useUserStore(pinia)

    expect(store.token).toBe('test-token')
  })
})
```

## Mocking

### Java

**Mockito Usage:**
```java
@Mock
private SysUserMapper userMapper;

@InjectMocks
private SysUserServiceImpl userService;

// Verify
verify(userMapper, times(1)).selectUserList(any(SysUser.class));
verify(userMapper, never()).deleteUserById(any());

// Argument matching
when(userMapper.selectUserById(argThat(id -> id > 0))).thenReturn(user);
```

**Spring MockBeans:**
```java
@MockBean
private ISysUserService userService;
```

### TypeScript

**ViM Mock:**
```typescript
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      get: vi.fn(),
      post: vi.fn(),
      put: vi.fn(),
      delete: vi.fn()
    }))
  }
}))
```

**MSW (Mock Service Worker) for API:**
```typescript
import { setupServer } from 'msw/node'
import { http, HttpResponse } from 'msw'

export const handlers = [
  http.get('/system/user/list', () => {
    return HttpResponse.json({
      code: 200,
      rows: [{ userId: 1, userName: 'test' }],
      total: 1
    })
  })
]

export const server = setupServer(...handlers)
```

## Fixtures and Factories

### Java

**Test Data Builder Pattern:**
```java
public class SysUserBuilder {
    private SysUser user = new SysUser();

    public SysUserBuilder withUserId(Long userId) {
        user.setUserId(userId);
        return this;
    }

    public SysUserBuilder withUserName(String userName) {
        user.setUserName(userName);
        return this;
    }

    public SysUserBuilder withDeptId(Long deptId) {
        user.setDeptId(deptId);
        return this;
    }

    public SysUserBuilder asAdmin() {
        user.setUserId(1L);
        user.setUserName("admin");
        return this;
    }

    public SysUser build() {
        return user;
    }
}

// Usage
SysUser testUser = new SysUserBuilder()
    .withUserName("testuser")
    .withDeptId(100L)
    .build();
```

### TypeScript

**Factory Pattern:**
```typescript
const createMockUser = (overrides: Partial<SysUser> = {}): SysUser => ({
  userId: 1,
  userName: 'testuser',
  nickName: 'Test User',
  email: 'test@example.com',
  phonenumber: '13800138000',
  status: '0',
  deptId: 100,
  ...overrides
})

// Usage
const adminUser = createMockUser({ userId: 1, userName: 'admin' })
const disabledUser = createMockUser({ status: '1' })
```

## Coverage

### Backend (JaCoCo)

**Configuration (pom.xml):**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

**Run with Coverage:**
```bash
mvn test jacoco:report
mvn test jacoco:check  # Enforce minimum coverage
```

**Minimum Coverage Target:** 80%

### Frontend (Vitest + Istanbul)

**Configuration (vitest.config.ts):**
```typescript
export default defineConfig({
  test: {
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html'],
      thresholds: {
        lines: 80,
        functions: 80,
        branches: 80,
        statements: 80
      }
    }
  }
})
```

**Run with Coverage:**
```bash
npm run test:coverage
```

**View HTML Report:** `coverage/index.html`

## Test Types

### Backend

**Unit Tests:**
- Service methods in isolation
- Utility class functions
- Validation logic
- Business rule enforcement

**Integration Tests:**
- Database operations with @SpringBootTest
- MyBatis mapper queries
- Controller endpoints with MockMvc
- Transaction management

**Not currently used:** E2E tests

### Frontend

**Unit Tests:**
- API functions
- Utility functions
- Vue component rendering
- Pinia store actions

**Component Tests:**
- User interactions
- Form validation
- Event handling

**Integration Tests:**
- Multi-component workflows
- Router navigation (with Vue Router testing)

**Not currently used:** E2E tests (consider Playwright or Cypress)

## Common Patterns

### Async Testing (Java)
```java
@Test
void testAsyncOperation() {
    // Use CompletableFuture for async code
    CompletableFuture<List<SysUser>> future = CompletableFuture.supplyAsync(() ->
        userService.selectUserList(new SysUser())
    );

    List<SysUser> result = future.join();
    assertNotNull(result);
}
```

### Async Testing (TypeScript)
```typescript
it('should fetch users async', async () => {
  const result = await listUser({ pageNum: 1 })
  expect(result.rows).toBeDefined()
})
```

### Error Testing (Java)
```java
@Test
void testCheckUserAllowed_ThrowsExceptionForAdmin() {
    SysUser adminUser = new SysUser();
    adminUser.setUserId(1L);  // Admin ID

    ServiceException exception = assertThrows(
        ServiceException.class,
        () -> userService.checkUserAllowed(adminUser)
    );

    assertEquals("不允许操作超级管理员用户", exception.getMessage());
}
```

### Error Testing (TypeScript)
```typescript
it('handles API error', async () => {
  mockedAxios.create.mockReturnValue({
    get: vi.fn().mockRejectedValue(new Error('Network error'))
  } as any)

  await expect(listUser({})).rejects.toThrow('Network error')
})
```

---

*Testing analysis: 2026-03-28*
