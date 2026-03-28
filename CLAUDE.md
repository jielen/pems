# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在本仓库中工作时提供指导。

## 项目概述

**物证管理系统** 是面向公安机关的物证全生命周期数字化管理系统，严格遵循《公安机关物证管理规定》"一物一码、全程留痕、责任到人、闭环管理"的管理要求。

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| SpringBoot | 4.0.3 | 基础框架 |
| MyBatis-Plus | 4.0.1 | ORM框架 |
| Druid | 1.2.28 | 数据库连接池 |
| JWT | 0.9.1 | Token认证 |
| Redis | 6.0+ | 缓存（框架依赖） |
| MySQL | 8.0+ | 数据库 |
| PageHelper | 2.1.1 | 分页插件 |
| Fastjson2 | 2.0.61 | JSON处理 |
| POI | 4.1.2 | Excel处理 |
| Kaptcha | 2.3.3 | 验证码 |
| SpringDoc | 3.0.2 | Swagger文档 |

### 前端
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue3 | 3.5.26 | 核心框架 |
| Vite | 6.4.1 | 构建工具 |
| Element Plus | 2.13.1 | UI组件库 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 4.6.4 | 路由 |
| Axios | 1.13.2 | HTTP客户端 |
| ECharts | 5.6.0 | 图表 |
| Sass | 1.97.2 | CSS预处理 |

## 项目结构

### 后端（ruoyi模块结构）

```
pems_backend/
├── ruoyi-admin/           # 主启动类 + Controller
│   └── src/main/java/com/ruoyi/
│       └── web/controller/
│           └── system/    # 业务Controller（用户、角色、菜单等）
├── ruoyi-common/         # 公共模块（必须依赖）
│   └── src/main/java/com/ruoyi/common/
│       ├── annotation/    # 注解：@Log, @DataScope, @RateLimiter, @RepeatSubmit
│       ├── core/
│       │   ├── controller/BaseController.java  # 基础Controller
│       │   ├── domain/     # 实体基类BaseEntity、AjaxResult、TableDataInfo
│       │   └── entity/     # 用户、角色、菜单、部门等核心实体
│       ├── enums/          # 枚举：BusinessType, OperatorType, UserStatus
│       ├── utils/          # 工具类：SecurityUtils, StringUtils, DateUtils等
│       └── exception/      # 异常类
├── ruoyi-framework/       # 框架配置（安全、跨域、权限）
├── ruoyi-system/         # 业务模块（Service + Mapper + Domain）
│   └── src/main/java/com/ruoyi/system/
│       ├── service/       # 业务接口：ISysXxxService
│       ├── service/impl/  # 业务实现：SysXxxServiceImpl
│       ├── mapper/        # MyBatis接口：SysXxxMapper
│       └── domain/        # 实体类、VO对象
├── ruoyi-generator/      # 代码生成器
├── ruoyi-quartz/         # 定时任务
└── sql/                  # 数据库脚本
```

### 前端（Vue3结构）

```
pems_frontend/
├── src/
│   ├── api/              # API接口定义（对应后端Controller）
│   │   ├── system/      # 系统模块API（user.ts, role.ts, menu.ts等）
│   │   ├── monitor/     # 监控模块API
│   │   └── tool/        # 工具模块API
│   ├── views/            # 页面组件
│   │   ├── system/      # 系统模块页面（user/index.vue, role/index.vue）
│   │   ├── monitor/     # 监控模块页面
│   │   └── tool/        # 工具模块页面
│   ├── router/           # 路由配置
│   ├── store/            # Pinia状态管理
│   ├── utils/            # 工具函数（request.ts封装axios）
│   ├── permission.ts/    # 路由权限守卫
│   └── settings.ts/      # 系统配置
├── vite.config.ts/       # Vite配置
└── package.json
```

## 若依框架核心约定（必须遵守）

### 1. 后端分层约定

**Controller层**（必须继承BaseController）
```java
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {
    // 使用 @PreAuthorize("@ss.hasPermi('system:user:list')") 权限校验
    // 使用 @Log(title = "用户管理", businessType = BusinessType.INSERT) 记录日志
    // 使用 startPage() + getDataTable(list) 返回分页数据
    // 使用 toAjax(rows) 或 success()/error() 返回结果
}
```

**Service层**（接口 + 实现）
```java
// 接口：ISysXxxService.java
public interface ISysUserService {
    List<SysUser> selectUserList(SysUser user);
    int insertUser(SysUser user);
    // ...
}

// 实现：SysXxxServiceImpl.java
@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private SysUserMapper userMapper;  // 注入Mapper
}
```

**Mapper层**（MyBatis接口 + XML）
```java
// 接口：SysXxxMapper.java
public interface SysUserMapper {
    List<SysUser> selectUserList(SysUser sysUser);
    int insertUser(SysUser user);
    @Param("userId") Long userId  // 多个参数必须用@Param
}

// XML：mapper/system/SysXxxMapper.xml
<mapper namespace="com.ruoyi.system.mapper.SysUserMapper">
    <resultMap id="SysUserResult" type="SysUser">
        <id property="userId" column="user_id"/>
    </resultMap>
</mapper>
```

### 2. 实体类约定

**继承BaseEntity**
```java
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;
    // 字段：userId, deptId, userName, nickName, email, phonenumber, sex, status, delFlag...
    // 必须有：createBy, createTime, updateBy, updateTime, remark（继承自BaseEntity）
}
```

**字段命名规范**
- Java属性：userId, userName, createTime, updateBy（小驼峰）
- 数据库字段：user_id, user_name, create_time, update_by（下划线）
- 枚举/常量：使用@NotNull, @Size, @Email等注解校验
- Excel导出：使用@Excel(name = "用户序号", type = Type.EXPORT)

### 3. 数据库表约定

**必须字段**
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

**表名前缀**
- 系统表：sys_xxx（sys_user, sys_role, sys_menu, sys_dept）
- 业务表：自定，建议 pems_xxx（pems_evidence, pems_evidence_log）

### 4. API路径约定

**RESTful风格**
```
GET    /system/user/list         # 查询列表（分页）
GET    /system/user/{userId}     # 查询详情
POST   /system/user              # 新增
PUT    /system/user              # 修改
DELETE /system/user/{userId}     # 删除
```

**响应格式**
```json
// 成功：{ "code": 200, "msg": "操作成功", "data": {...} }
// 失败：{ "code": 500, "msg": "错误信息" }
// 分页：{ "code": 200, "msg": "查询成功", "rows": [...], "total": 100 }
```

### 5. 权限控制约定

**权限标识格式**
```
模块:实体:操作
system:user:list     # 用户列表
system:user:add      # 用户新增
system:user:edit     # 用户修改
system:user:remove   # 用户删除
system:user:export   # 用户导出
system:user:import   # 用户导入
```

**使用注解**
```java
@PreAuthorize("@ss.hasPermi('system:user:list')")      // 权限校验
@PreAuthorize("@ss.hasRole('admin')")                   // 角色校验
@Log(title = "用户管理", businessType = BusinessType.DELETE)  // 日志记录
@RepeatSubmit                                        // 防重复提交
```

### 6. 前端约定

**API定义**（src/api/system/user.ts）
```typescript
import request from '@/utils/request'
// 使用框架封装的axios实例
export function listUser(query): Promise<TableDataInfo<SysUser[]>> {
  return request({ url: '/system/user/list', method: 'get', params: query })
}
```

**页面组件**（src/views/system/user/index.vue）
```vue
<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" :inline="true">
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="queryParams.userName" />
      </el-form-item>
    </el-form>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="userList">
      <el-table-column prop="userName" label="用户名称" />
    </el-table>

    <!-- 分页 -->
    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" />
  </div>
</template>
```

**权限指令**
```vue
<el-button @click="handleAdd" v-hasPermi="['system:user:add']">新增</el-button>
```

### 7. 菜单表约定（sys_menu）

| 字段 | 说明 |
|------|------|
| menu_type | M目录，C菜单，F按钮 |
| perms | 权限标识（如system:user:add） |
| path | 路由地址 |
| component | 组件路径（如system/user/index） |
| query | 路由参数（如?id=1） |

```
一级菜单(M) → 二级菜单(C) → 按钮(F)
例：系统管理(M) → 用户管理(C) → 用户新增(F)
```

### 8. 日志记录约定

**操作类型枚举**（BusinessType）
```java
INSERT, UPDATE, DELETE, GRANT, EXPORT, IMPORT, FORCE, GENCODE, CLEAN, OTHER
```

**@Log注解使用**
```java
@Log(title = "用户管理", businessType = BusinessType.INSERT)
@Log(title = "用户管理", businessType = BusinessType.UPDATE)
@Log(title = "用户管理", businessType = BusinessType.DELETE)
@Log(title = "用户管理", businessType = BusinessType.EXPORT)
```

## 物证管理系统模块规划

### 后端包结构
```
com.ruoyi.pems/
├── controller/
│   └── EvidenceController.java      # 物证管理
├── service/
│   ├── IEvidenceService.java
│   └── impl/EvidenceServiceImpl.java
├── mapper/
│   ├── EvidenceMapper.java
│   └── EvidenceLogMapper.java
├── domain/
│   ├── Evidence.java               # 物证实体
│   ├── EvidenceLog.java            # 操作日志实体
│   └── vo/
│       ├── EvidenceQueryVO.java
│       └── EvidenceDetailVO.java
└── enums/
    ├── EvidenceTypeEnum.java       # 物证类型：普通/贵重/涉密/危险/易腐
    ├── EvidenceLevelEnum.java      # 物证等级：一般/重要/核心
    └── EvidenceStatusEnum.java      # 物证状态：在库/借出/送检/销毁/丢失/损毁
```

### 前端文件结构
```
src/
├── api/
│   └── pems/
│       ├── evidence.ts              # 物证管理API
│       └── borrow.ts               # 借用流转API
├── views/
│   └── pems/
│       ├── evidence/
│       │   ├── index.vue           # 物证列表
│       │   ├── detail.vue         # 物证详情
│       │   └── form.vue           # 物证表单
│       └── borrow/
│           ├── apply.vue           # 借用申请
│           └── approval.vue        # 审批
```

## 核心数据库表（物证管理）

```sql
-- 物证基础表
CREATE TABLE pems_evidence (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    evidence_code   VARCHAR(50) NOT NULL UNIQUE COMMENT '物证编码',
    case_no         VARCHAR(50) COMMENT '案件编号',
    case_name       VARCHAR(200) COMMENT '案件名称',
    evidence_name   VARCHAR(100) NOT NULL COMMENT '物证名称',
    evidence_type   TINYINT COMMENT '类型：1普通 2贵重 3涉密 4危险 5易腐',
    evidence_level  TINYINT COMMENT '等级：1一般 2重要 3核心',
    quantity        INT DEFAULT 1 COMMENT '数量',
    unit            VARCHAR(20) COMMENT '单位',
    spec            VARCHAR(100) COMMENT '规格',
    color           VARCHAR(50) COMMENT '颜色',
    features        TEXT COMMENT '特征描述',
    status          TINYINT DEFAULT 0 COMMENT '状态：0在库 1借出 2送检 3销毁 4丢失 5损毁',
    storage_pos     VARCHAR(100) COMMENT '保管位置',
    seal_no         VARCHAR(50) COMMENT '封签号',
    storage_unit    VARCHAR(100) COMMENT '保管单位',
    keeper_id       BIGINT COMMENT '保管人ID',
    photos          TEXT COMMENT '照片URLs',
    videos          TEXT COMMENT '视频URLs',
    del_flag        CHAR(1) DEFAULT '0',
    create_by       VARCHAR(64),
    create_time     DATETIME,
    update_by       VARCHAR(64),
    update_time     DATETIME,
    remark          VARCHAR(500)
);

-- 物证操作流水表（全程留痕）
CREATE TABLE pems_evidence_log (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    evidence_id     BIGINT NOT NULL,
    evidence_code   VARCHAR(50),
    operation_type  VARCHAR(50) NOT NULL COMMENT '操作类型',
    operator_id     BIGINT NOT NULL,
    operator_name   VARCHAR(50),
    operator_badge  VARCHAR(20) COMMENT '警号',
    operator_unit   VARCHAR(100),
    operate_time    DATETIME NOT NULL,
    operator_ip     VARCHAR(50),
    detail          TEXT COMMENT '操作详情JSON',
    operator2_id    BIGINT COMMENT '第二操作人ID',
    operator2_name  VARCHAR(50),
    hash_value      VARCHAR(64) COMMENT '防篡改哈希',
    create_time     DATETIME
);

-- 系统配置表（单/双人模式开关）
CREATE TABLE pems_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    param_key       VARCHAR(100) NOT NULL UNIQUE,
    param_value     VARCHAR(500),
    remark          VARCHAR(255),
    create_time     DATETIME,
    update_time     DATETIME
);

-- 预置配置：
-- dual_lock_enabled = "false"
-- dual_lock_for_out = "false"
-- dual_lock_for_destroy = "true"
```

## 开发注意事项

1. **必须先引入ruoyi-common**：所有业务模块必须依赖common
2. **Controller必须继承BaseController**：使用框架封装的方法
3. **Mapper参数用@Param**：多个参数必须指定参数名
4. **数据库表必须有del_flag**：逻辑删除标志
5. **权限标识必须唯一**：system:xxx:yyy格式
6. **前端API统一用request**：不要直接用axios
7. **菜单路由对应组件路径**：如system/user/index对应views/system/user/index.vue
