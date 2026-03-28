# Phase 1 Context: Foundation & Data Isolation

**Phase:** 1 - Foundation & Data Isolation
**Created:** 2026-03-28

## Decisions

### 单位层级结构 (ORG)

| Decision | Choice |
|----------|--------|
| 单位表 | 复用SysDept，扩展字段 |
| 编码规则 | 上级编码 + 3位本级序号 |
| 示例 | 000（市级）→ 000001（区县）→ 000001001（派出所） |
| 同级序号 | 自动递增（如000001, 000002） |
| 层级关系 | 父子关系（SysDept parent_id） |

**Rationale:** 继承框架能力，减少开发量；单位编码便于数据隔离和汇总查询。

### 数据权限粒度 (AUTH/ORG)

| Decision | Choice |
|----------|--------|
| 市级查看范围 | 可查看下级区县明细数据 |
| 市级操作权限 | 操作（如转移）需区县授权 |
| 区县查看范围 | 完全隔离，只能看自己单位数据 |
| 同级区县之间 | 不可互看数据 |

**Rationale:** 上级有督查权，但操作需属地授权，符合公安管理体制。

### 审计日志 (LOG)

| Decision | Status |
|----------|--------|
| 不可删除 | 已确定 |
| 不可篡改 | 已确定 |
| 哈希校验 | HMAC-SHA256，链式 |
| 审计员可查看 | 已确定 |
| 导出报告 | 已确定（Excel/PDF二期） |

**Implementation note:** 审计日志记录使用 @Log 注解 + 切面拦截，HMAC-SHA256 哈希链在 EvidenceLogMapper 层实现。

### 系统配置 (CONFIG)

| Config Item | 说明 |
|-------------|------|
| 物证分类 | 普通/贵重/涉密/危险/易腐 |
| 物证等级 | 一般/重要/核心 |
| 保管期限 | 各类型物证的保管时限 |
| 编号规则 | 一物一码生成规则 |
| 单位配置 | 单位信息 |
| 双人双锁开关 | 配置项（具体逻辑二期） |
| 预警规则 | 到期提醒等 |

**Implementation note:** 配置存储在 pems_config 表，前端通过配置管理页面维护。

## Not Discussed (Deferred to Later)

- 日志哈希链的具体验证时机（实时/定时）
- 配置变更的审批流程
- 双人双锁的具体触发场景（第二期）

## Requirements Mapped

| REQ-ID | Description | Status |
|--------|-------------|--------|
| AUTH-01 | 用户登录（JWT） | Locked |
| AUTH-02 | 超级管理员用户管理 | Locked |
| AUTH-03 | 多级用户模式 | Locked |
| AUTH-04 | 角色权限（模块:实体:操作） | Locked |
| ORG-01 | 单位管理 | Locked |
| ORG-02 | 用户归属单位 | Locked |
| ORG-03 | 同级隔离/上级查看下级 | Locked |
| ORG-04 | @DataScope扩展 | Locked |
| LOG-01~04 | 审计日志 | Locked |
| CONFIG-01~07 | 系统配置 | Locked |

## Canonical References

- `.planning/PROJECT.md` - 项目概述
- `.planning/REQUIREMENTS.md` - 需求列表
- `.planning/ROADMAP.md` - 阶段规划
- `.planning/codebase/STACK.md` - 技术栈
- `.planning/codebase/ARCHITECTURE.md` - 架构设计
- `docs/requirements/技术约束.md` - 若依框架约定

---

*Last updated: 2026-03-28 after discuss-phase*
