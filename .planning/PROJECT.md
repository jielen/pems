# 物证管理系统 (PEMS)

## What This Is

面向公安机关的物证全生命周期数字化管理系统，严格遵循《公安机关物证管理规定》"**一物一码、全程留痕、责任到人、闭环管理**"的管理要求。

**核心价值**：
- 一物一码：每件物证唯一编码，全生命周期绑定
- 全程留痕：所有操作记录不可删除、不可篡改
- 责任到人：操作可追溯，操作人有警号和签名

## Why This Exists

现有物证管理存在以下核心痛点：
1. 人工登记效率低，易出错、易丢失
2. 物证追溯困难，无法全程追踪
3. 保管流程不规范，不符合《公安机关物证管理规定》
4. 权限管控不严格，责任无法到人
5. 处置不及时，缺乏预警机制
6. 纸质记录多，数字化程度低
7. 流转无闭环，出现问题无法溯源追责

## Core Value

**一物一码 + 全程留痕 + 责任到人**

## Project Type

Brownfield — 基于若依前后端分离框架（RuoYi v3.9.1 Vue3版本）进行开发。

- 后端：`pems_backend/`（SpringBoot + MyBatis-Plus）
- 前端：`pems_frontend/`（Vue3 + Element Plus）
- 技术栈：MySQL 8.0 + Redis 6.0 + MinIO

## Constraints

### Timeline
- **交付时间**：2026-04-30

### 技术约束
- 框架：若依Vue3版本（RuoYi v3.9.1）
- 数据库：MySQL 8.0+
- 缓存：Redis 6.0+
- 文件存储：MinIO
- 硬件对接：RFID扫码枪、RFID标签写印一体机

### MVP范围（第一期）
1. **系统管理与权限管控模块**
   - 用户管理、角色权限
   - **多级用户模式**（市级/区县单位数据隔离）
   - 双人双锁配置（第二期实现具体控制）
   - 操作日志审计（不可删改、哈希校验）
   - 系统基础配置

2. **物证基础信息管理模块**
   - 物证唯一编码自动生成（一物一码）
   - 物证信息录入、编辑
   - 物证分类管理（普通/贵重/涉密/危险/易腐）
   - 物证等级管理（一般/重要/核心）

3. **物证接收与入库模块**
   - 现场物证接收
   - 入库单人核验（双人双锁第二期实现）
   - 智能仓位分配
   - 封签管理
   - 入库单生成

4. **物证调用与流转模块**
   - 调用申请
   - 分级审批流程（按物证等级）
   - 出库核验
   - 归还管理
   - **工作流引擎**（支持统一模板+单位独立模板）
   - 跨单位移交（多级模式）

### 第二期功能
- 双人双锁具体控制（入库、出库、销毁）
- 物证保管与监控模块
- 物证处置与销毁模块
- 查询统计模块
- 报表导出

### Out of Scope
- 移动端（警务通等）
- 报表导出（Excel/PDF）
- 硬件设备对接（RFID扫码枪、标签写印一体机）

## Users

| 角色 | 职责 | 权限级别 |
|------|------|----------|
| 超级管理员 | 系统配置、用户管理、角色权限分配、日志管理 | 最高权限 |
| 物证管理员（保管员） | 物证接收、入库核验、仓位分配、封签管理、物证出库核验、盘点 | 操作执行 |
| 办案民警 | 录入物证信息、提交调用/归还/处置申请、查看本人经办案件物证 | 申请/查看 |
| 审核员/领导 | 分级审批、查看Dashboard、督查物证管理 | 审批/督查 |
| 审计员 | 查看日志、审计报告、数据完整性校验 | 仅查看 |
| 见证人 | 扣押、入库环节核验（记录信息） | 参与核验 |
| 监督人 | 销毁、关键出库时双人监督 | 监督 |

## Key Decisions

| Decision | Rationale | Status |
|----------|-----------|--------|
| MVP支持多级用户模式 | 权限和数据隔离的基础设计必须一期完成 | — Pending |
| 工作流引擎一期实现 | 所有审批业务都通过工作流执行 | — Pending |
| 双人双锁二期实现 | 一期单人审批，二期增加双人控制 | — Pending |
| 多级模式下支持单位独立工作流模板 | 市级可统一模板，区县可独立配置 | — Pending |

## Requirements

### Active

- [ ] **AUTH-01**: 用户可登录系统并保持会话
- [ ] **AUTH-02**: 超级管理员可管理用户、角色、权限
- [ ] **AUTH-03**: 系统支持多级用户模式（市级/区县单位数据隔离）
- [ ] **PERM-01**: 基于角色的权限控制，权限标识格式：模块:实体:操作
- [ ] **PERM-02**: 双人双锁可配置（具体控制二期实现）
- [ ] **LOG-01**: 操作日志不可删除、不可篡改，支持哈希校验
- [ ] **EVI-01**: 物证唯一编码自动生成（一物一码）
- [ ] **EVI-02**: 物证信息录入、编辑、分类管理
- [ ] **EVI-03**: 物证等级管理（一般/重要/核心）
- [ ] **STORE-01**: 现场物证接收
- [ ] **STORE-02**: 入库核验（单人，二期双人）
- [ ] **STORE-03**: 智能仓位分配
- [ ] **STORE-04**: 封签管理（唯一封签号、拍照存档）
- [ ] **FLOW-01**: 工作流引擎支持分级审批
- [ ] **FLOW-02**: 工作流模板：统一模板+单位独立模板
- [ ] **TRANS-01**: 物证调用申请与审批
- [ ] **TRANS-02**: 物证出库核验
- [ ] **TRANS-03**: 物证归还管理
- [ ] **TRANS-04**: 跨单位移交（多级模式）
- [ ] **CONFIG-01**: 系统基础配置（物证分类、保管期限、预警规则、编号规则、单位配置）

### Out of Scope

- 移动端（警务通等）— 第二期讨论
- 报表导出（Excel/PDF）— 第二期
- 硬件设备对接（RFID扫码枪、标签写印一体机）— 第二期
- 双人双锁具体控制逻辑 — 第二期
- 物证保管与监控（盘点、预警、状态锁定）— 第二期
- 物证处置与销毁模块 — 第二期

---

*Last updated: 2026-03-28 after Phase 01 completion*

## Current State

**Phase 01: Foundation & Data Isolation — COMPLETE**
- Database schema: pems_config, pems_audit_log (HMAC-SHA256 hash chain), sys_dept_extend
- Unit hierarchy: multi-level data isolation via unit_code hierarchy (city → district)
- Role/Permission system: 5 predefined roles (super_admin, evidence_admin, investigator, auditor, reviewer) with pems:* permission prefix
- Audit logging: immutable audit logs with HMAC-SHA256 hash chain integrity verification
- Config management: system configuration UI with CONFIG-01~07 coverage
