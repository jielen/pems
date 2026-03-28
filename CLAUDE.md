# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在本仓库中工作时提供指导。

## ⚠️ 技术约束（必须遵守）

**所有 plan 的 `<context>` 必须引用 `docs/requirements/技术约束.md`。**

技术约束涵盖：
- 数据库表必须字段（del_flag, create_by, create_time, update_by, update_time, remark）
- Controller/Service/Mapper/Entity 层约定
- API 路径风格和响应格式
- 权限控制注解
- 前端 API 和组件规范
- 菜单表和日志记录约定

执行任何数据库或后端编码任务前，必须先阅读 `docs/requirements/技术约束.md`。

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
| Redis | 6.0+ | 缓存 |
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
├── ruoyi-common/         # 公共模块（必须依赖）
│   └── src/main/java/com/ruoyi/common/
│       ├── annotation/    # @Log, @DataScope, @RateLimiter, @RepeatSubmit
│       ├── core/controller/BaseController.java
│       ├── core/domain/   # BaseEntity, AjaxResult, TableDataInfo
│       └── exception/
├── ruoyi-framework/       # 框架配置（安全、跨域、权限）
├── ruoyi-system/         # 业务模块（Service + Mapper + Domain）
│   └── src/main/java/com/ruoyi/system/
│       ├── service/       # ISysXxxService
│       ├── service/impl/  # SysXxxServiceImpl
│       ├── mapper/        # SysXxxMapper
│       └── domain/        # 实体类、VO
├── ruoyi-generator/      # 代码生成器
├── ruoyi-quartz/         # 定时任务
└── sql/                  # 数据库脚本
```

### 前端（Vue3结构）
```
pems_frontend/
├── src/
│   ├── api/              # API接口（对应后端Controller）
│   ├── views/            # 页面组件
│   ├── router/           # 路由配置
│   ├── store/            # Pinia状态管理
│   ├── utils/            # request.ts封装axios
│   └── permission.ts/    # 路由权限守卫
├── vite.config.ts/
└── package.json
```

## 详细技术约定

所有技术约定（Controller层、Service层、Mapper层、Entity层、数据库表、API路径、权限控制、前端规范）详见：

**`docs/requirements/技术约束.md`**

该文件为规范参考，执行计划时会自动加载。

## 开发注意事项

1. **必须先引入ruoyi-common**：所有业务模块必须依赖common
2. **Controller必须继承BaseController**
3. **Mapper参数用@Param**：多个参数必须指定参数名
4. **数据库表必须有del_flag**：逻辑删除标志
5. **前端API统一用request**：不要直接用axios
6. **菜单路由对应组件路径**：如 system/user/index 对应 views/system/user/index.vue
