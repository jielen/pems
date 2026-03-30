-- ================================================================
-- V2: PEMS Initial Data
-- Phase 1: Foundation & Data Isolation
-- Predefined roles, permissions, and menus
-- ================================================================

-- ================================================================
-- PEMS Module Menus (sys_menu)
-- ================================================================
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
-- 物证管理模块
(3000, '物证管理', 0, 1, 'pems', NULL, '', 1, 0, 'M', '0', '0', '', 'fa fa-gavel', '', '2024-01-01 00:00:00', '', NULL, 'PEMS模块父菜单'),
(3001, '物证查询', 3000, 1, 'list', 'pems/evidence/index', '', 1, 0, 'C', '0', '0', 'pems:evidence:list', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3002, '物证新增', 3000, 2, 'add', 'pems/evidence/add', '', 1, 0, 'F', '0', '0', 'pems:evidence:add', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3003, '物证编辑', 3000, 3, 'edit', 'pems/evidence/edit', '', 1, 0, 'F', '0', '0', 'pems:evidence:edit', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3004, '物证删除', 3000, 4, 'remove', 'pems/evidence/remove', '', 1, 0, 'F', '0', '0', 'pems:evidence:remove', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3005, '物证详情', 3000, 5, 'detail', 'pems/evidence/detail', '', 1, 0, 'F', '0', '0', 'pems:evidence:detail', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3006, '物证导出', 3000, 6, 'export', 'pems/evidence/export', '', 1, 0, 'F', '0', '0', 'pems:evidence:export', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3007, '物证导入', 3000, 7, 'import', 'pems/evidence/import', '', 1, 0, 'F', '0', '0', 'pems:evidence:import', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),

-- 借用管理模块
(3010, '借用管理', 0, 2, 'borrow', NULL, '', 1, 0, 'M', '0', '0', '', 'fa fa-exchange', '', '2024-01-01 00:00:00', '', NULL, '借用管理模块父菜单'),
(3011, '借用申请', 3010, 1, 'apply', 'pems/borrow/apply', '', 1, 0, 'C', '0', '0', 'pems:borrow:apply', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3012, '借用审批', 3010, 2, 'approval', 'pems/borrow/approval', '', 1, 0, 'C', '0', '0', 'pems:borrow:approval', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3013, '借用查询', 3010, 3, 'list', 'pems/borrow/list', '', 1, 0, 'C', '0', '0', 'pems:borrow:list', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3014, '借用归还', 3010, 4, 'return', 'pems/borrow/return', '', 1, 0, 'C', '0', '0', 'pems:borrow:return', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),

-- 系统配置模块
(3020, '系统配置', 0, 3, 'config', NULL, '', 1, 0, 'M', '0', '0', '', 'fa fa-cog', '', '2024-01-01 00:00:00', '', NULL, '系统配置模块父菜单'),
(3021, '配置查询', 3020, 1, 'list', 'pems/config/index', '', 1, 0, 'C', '0', '0', 'pems:config:list', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3022, '配置修改', 3020, 2, 'edit', 'pems/config/edit', '', 1, 0, 'F', '0', '0', 'pems:config:edit', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),

-- 审计日志模块
(3030, '审计日志', 0, 4, 'audit', NULL, '', 1, 0, 'M', '0', '0', '', 'fa fa-file-text', '', '2024-01-01 00:00:00', '', NULL, '审计日志模块父菜单'),
(3031, '日志查询', 3030, 1, 'list', 'pems/auditLog/index', '', 1, 0, 'C', '0', '0', 'pems:audit:list', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3032, '日志导出', 3030, 2, 'export', 'pems/auditLog/export', '', 1, 0, 'F', '0', '0', 'pems:audit:export', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3033, '日志详情', 3030, 3, 'detail', 'pems/auditLog/detail', '', 1, 0, 'F', '0', '0', 'pems:audit:detail', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),

-- 物证入库模块
(3040, '物证入库', 0, 5, 'storage', NULL, '', 1, 0, 'M', '0', '0', '', 'fa fa-sign-in', '', '2024-01-01 00:00:00', '', NULL, '物证入库模块父菜单'),
(3041, '入库接收', 3040, 1, 'receive', 'pems/storage/receive', '', 1, 0, 'C', '0', '0', 'pems:storage:receive', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3042, '入库登记', 3040, 2, 'register', 'pems/storage/register', '', 1, 0, 'C', '0', '0', 'pems:storage:register', '', 'admin', '2024-01-01 00:00:00', '', NULL, ''),
(3043, '仓位分配', 3040, 3, 'allocate', 'pems/storage/allocate', '', 1, 0, 'C', '0', '0', 'pems:storage:allocate', '', 'admin', '2024-01-01 00:00:00', '', NULL, '');

-- ================================================================
-- PEMS Predefined Roles (sys_role)
-- ================================================================
REPLACE INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark) VALUES
(1, '超级管理员', 'super_admin', 1, '1', 1, 1, '0', '0', 'admin', '2024-01-01 00:00:00', '', NULL, '系统内置超级管理员，不可删除'),
(2, '物证管理员', 'evidence_admin', 2, '4', 1, 1, '0', '0', 'admin', '2024-01-01 00:00:00', '', NULL, '物证管理员，负责物证日常管理'),
(3, '办案民警', 'investigator', 3, '5', 1, 1, '0', '0', 'admin', '2024-01-01 00:00:00', '', NULL, '办案民警，只能查看自己录入的物证'),
(4, '审计员', 'auditor', 4, '4', 1, 1, '0', '0', 'admin', '2024-01-01 00:00:00', '', NULL, '审计员，只能查看审计日志'),
(5, '审核员', 'reviewer', 5, '4', 1, 1, '0', '0', 'admin', '2024-01-01 00:00:00', '', NULL, '审核员，负责借用审批');

-- ================================================================
-- PEMS Role Permissions (sys_role_menu)
-- ================================================================
-- super_admin: 所有权限
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 1, menu_id FROM sys_menu WHERE menu_id >= 3000;

-- evidence_admin: 物证管理 + 系统配置 + 物证入库
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 2, menu_id FROM sys_menu WHERE menu_id IN (3000,3001,3002,3003,3004,3005,3006,3007,3020,3021,3040,3041,3042,3043);

-- investigator: 物证查询 + 借用申请
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 3, menu_id FROM sys_menu WHERE menu_id IN (3001,3011);

-- auditor: 审计日志
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 4, menu_id FROM sys_menu WHERE menu_id IN (3030,3031,3032,3033);

-- reviewer: 借用审批
INSERT INTO sys_role_menu (role_id, menu_id) SELECT 5, menu_id FROM sys_menu WHERE menu_id IN (3010,3012,3013,3014);
