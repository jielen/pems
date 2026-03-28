-- ================================================================
-- PEMS Data - Phase 1: Role and Permission System
-- ================================================================
-- This file creates:
-- 1. PEMS menu entries (sys_menu) with pems:* permission prefix
-- 2. Predefined PEMS roles (sys_role)
-- 3. Role-Permission mappings (sys_role_menu)
--
-- Permission Format: pems:<module>:<operation>
-- Modules: evidence (物证管理), borrow (借用管理), config (配置管理), log (审计日志)
-- ================================================================

-- ================================================================
-- Section 1: PEMS Menu Entries (sys_menu)
-- ================================================================
-- Menu IDs 3000-3999 reserved for PEMS modules
-- M = Directory/Catalog, C = Menu, F = Button/Permission
-- ================================================================

-- PEMS Module Parent (物证管理系统)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3000, '物证管理', 0, 5, 'pems', NULL, '', '', 1, 0, 'M', '0', '0', '', 'pems', 'admin', sysdate(), '', null, '物证管理目录');

-- Evidence Management Module (物证管理)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3001, '物证查询', 3000, 1, 'evidence', 'pems/evidence/index', '', '', 1, 0, 'C', '0', '0', 'pems:evidence:list', 'search', 'admin', sysdate(), '', null, '物证查询菜单');

-- Evidence Permissions (Buttons)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
(3002, '物证新增', 3001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:evidence:add', '#', 'admin', sysdate(), '', null, ''),
(3003, '物证编辑', 3001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:evidence:edit', '#', 'admin', sysdate(), '', null, ''),
(3004, '物证删除', 3001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:evidence:remove', '#', 'admin', sysdate(), '', null, ''),
(3005, '物证详情', 3001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:evidence:query', '#', 'admin', sysdate(), '', null, ''),
(3006, '物证导出', 3001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:evidence:export', '#', 'admin', sysdate(), '', null, ''),
(3007, '物证导入', 3001, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:evidence:import', '#', 'admin', sysdate(), '', null, '');

-- Borrow Management Module (借用管理)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3010, '借用管理', 3000, 2, 'borrow', 'pems/borrow/index', '', '', 1, 0, 'C', '0', '0', 'pems:borrow:list', 'borrow', 'admin', sysdate(), '', null, '借用管理菜单');

-- Borrow Permissions (Buttons)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
(3011, '借用申请', 3010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:borrow:apply', '#', 'admin', sysdate(), '', null, ''),
(3012, '借用审批', 3010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:borrow:approve', '#', 'admin', sysdate(), '', null, ''),
(3013, '借用查询', 3010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:borrow:list', '#', 'admin', sysdate(), '', null, ''),
(3014, '借用归还', 3010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:borrow:return', '#', 'admin', sysdate(), '', null, '');

-- Configuration Module (配置管理)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3020, '系统配置', 3000, 3, 'config', 'pems/config/index', '', '', 1, 0, 'C', '0', '0', 'pems:config:list', 'config', 'admin', sysdate(), '', null, '系统配置菜单');

-- Config Permissions (Buttons)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
(3021, '配置查询', 3020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:config:query', '#', 'admin', sysdate(), '', null, ''),
(3022, '配置修改', 3020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:config:edit', '#', 'admin', sysdate(), '', null, '');

-- Audit Log Module (审计日志)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3030, '审计日志', 3000, 4, 'audit', 'pems/audit/index', '', '', 1, 0, 'C', '0', '0', 'pems:log:list', 'log', 'admin', sysdate(), '', null, '审计日志菜单');

-- Audit Log Permissions (Buttons)
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
(3031, '日志查询', 3030, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:log:query', '#', 'admin', sysdate(), '', null, ''),
(3032, '日志导出', 3030, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:log:export', '#', 'admin', sysdate(), '', null, ''),
(3033, '日志详情', 3030, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:log:detail', '#', 'admin', sysdate(), '', null, '');

-- Storage/Receive Module (物证接收/入库) - Future Phase 2
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (3040, '物证入库', 3000, 5, 'store', 'pems/store/index', '', '', 1, 0, 'C', '0', '0', 'pems:store:list', 'warehouse', 'admin', sysdate(), '', null, '物证入库菜单');

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
(3041, '入库接收', 3040, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:store:receive', '#', 'admin', sysdate(), '', null, ''),
(3042, '入库登记', 3040, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:store:add', '#', 'admin', sysdate(), '', null, ''),
(3043, '仓位分配', 3040, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'pems:store:allocate', '#', 'admin', sysdate(), '', null, '');

-- ================================================================
-- Section 2: Predefined PEMS Roles (sys_role)
-- ================================================================
-- Note: role_id 1 and 2 already exist in ry_20250522.sql
-- We update existing roles and add new ones for PEMS
--
-- Data Scope values:
-- 1 = All data (全部数据权限)
-- 2 = Custom data (自定数据权限)
-- 3 = Dept data (本部门数据权限)
-- 4 = Dept and children (本部门及以下数据权限)
-- 5 = Self only (仅本人数据权限)
-- ================================================================

-- Update existing role_id 1 to be super_admin with full permissions
UPDATE sys_role SET role_key = 'super_admin', role_name = '超级管理员', data_scope = '1', status = '0', remark = '系统超级管理员，拥有所有权限' WHERE role_id = 1;

-- Add PEMS-specific roles (avoiding ID conflicts with existing roles)
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES
(3, '物证管理员', 'evidence_admin', 2, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '物证管理员，负责物证接收、入库、核验等操作'),
(4, '办案民警', 'investigator', 3, '5', 1, 1, '0', '0', 'admin', sysdate(), '', null, '办案民警，可查询本人经手的物证'),
(5, '审计员', 'auditor', 4, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '审计员，可查看所有审计日志'),
(6, '审核员', 'reviewer', 5, '4', 1, 1, '0', '0', 'admin', sysdate(), '', null, '审核员，负责审批物证调用、归还等申请');

-- ================================================================
-- Section 3: Role-Permission Mappings (sys_role_menu)
-- ================================================================
-- super_admin (role_id=1): All PEMS permissions
-- evidence_admin (role_id=3): Evidence management + config, no audit logs
-- investigator (role_id=4): Query evidence only
-- auditor (role_id=5): Audit logs only
-- reviewer (role_id=6): Approval permissions
-- ================================================================

-- super_admin gets all PEMS permissions (3000-3043)
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id >= 3000 AND menu_id <= 3043;

-- evidence_admin gets evidence management, storage, and config (no audit logs)
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, menu_id FROM sys_menu WHERE menu_id IN (3000, 3001, 3002, 3003, 3004, 3005, 3006, 3007, 3010, 3011, 3012, 3013, 3014, 3020, 3021, 3022, 3040, 3041, 3042, 3043);

-- investigator gets evidence query only
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 4, menu_id FROM sys_menu WHERE menu_id IN (3001, 3005);

-- auditor gets audit logs only
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 5, menu_id FROM sys_menu WHERE menu_id IN (3030, 3031, 3032, 3033);

-- reviewer gets borrow approval permissions
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 6, menu_id FROM sys_menu WHERE menu_id IN (3010, 3012);

-- ================================================================
-- Section 4: Data Scope Constraints (sys_role_dept)
-- ================================================================
-- For roles with data_scope=4 (dept and children), optionally
-- restrict to specific departments. Empty means all depts in scope.
-- This section is optional and can be populated during deployment.
-- ================================================================
-- Example: evidence_admin can only manage dept 100 and children
-- INSERT INTO sys_role_dept (role_id, dept_id) VALUES (3, 100);

-- ================================================================
-- End of PEMS Data Seed
-- ================================================================
