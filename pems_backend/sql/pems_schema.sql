-- ================================================================
-- PEMS Schema - Phase 1: Foundation & Data Isolation
-- ================================================================
-- This schema creates the foundation tables for:
-- 1. pems_config - System configuration storage
-- 2. pems_audit_log - Immutable audit log with HMAC-SHA256 hash chain
-- 3. sys_dept_extend - Department extension for multi-level unit support
-- ================================================================

-- ================================================================
-- Table 1: pems_config - System Configuration Table
-- ================================================================
-- Stores all system configuration parameters including:
-- - Evidence types (普通/贵重/涉密/危险/易腐)
-- - Evidence levels (一般/重要/核心)
-- - Retention periods
-- - Numbering rules
-- - Dual lock settings
-- - Alert rules
-- ================================================================
DROP TABLE IF EXISTS pems_config;
CREATE TABLE pems_config (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    del_flag        CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by       VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    param_key       VARCHAR(100) NOT NULL COMMENT '配置键（唯一）',
    param_value     VARCHAR(500) COMMENT '配置值',
    param_type      VARCHAR(50) COMMENT '配置类型：string/number/boolean/json',
    remark          VARCHAR(255) COMMENT '备注说明',
    UNIQUE KEY uk_param_key (param_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物证系统配置表';

-- ================================================================
-- Pre-populate default configuration values (CONFIG-01 ~ CONFIG-07)
-- ================================================================
INSERT INTO pems_config (param_key, param_value, param_type, remark) VALUES
    ('evidence_type', '1,2,3,4,5', 'string', '物证分类：1普通 2贵重 3涉密 4危险 5易腐'),
    ('evidence_level', '1,2,3', 'string', '物证等级：1一般 2重要 3核心'),
    ('retention_period', '{}', 'json', '保管期限JSON，格式：{"1":365,"2":730,"3":1095,"4":30,"5":7}'),
    ('numbering_rule', 'YEAR|MONTH|SEQ|CHECK', 'string', '一物一码编号规则：年月顺序校验位'),
    ('dual_lock_enabled', 'false', 'boolean', '双人双锁开关（具体逻辑二期实现）'),
    ('dual_lock_for_out', 'false', 'boolean', '出库双人双锁开关'),
    ('dual_lock_for_destroy', 'true', 'boolean', '销毁双人双锁开关'),
    ('alert_rules', '{}', 'json', '预警规则JSON');

-- ================================================================
-- Table 2: pems_audit_log - Immutable Audit Log with Hash Chain
-- ================================================================
-- Features:
-- - Immutable: No update_time, update_by columns
-- - Hash chain: Each log links to previous log's hash (HMAC-SHA256)
-- - Complete operation details in JSON format
-- - Supports both evidence operations and system operations
-- ================================================================
DROP TABLE IF EXISTS pems_audit_log;
CREATE TABLE pems_audit_log (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    create_by           VARCHAR(64) DEFAULT '' COMMENT '创建者',
    evidence_id         BIGINT COMMENT '关联物证ID（系统操作时为空）',
    operation_type      VARCHAR(50) NOT NULL COMMENT '操作类型：CREATE/UPDATE/DELETE/TRANSFER/BORROW/RETURN/DESTROY等',
    operator_id        BIGINT NOT NULL COMMENT '操作人ID',
    operator_name      VARCHAR(50) COMMENT '操作人姓名',
    operator_badge     VARCHAR(20) COMMENT '操作人警号',
    operator_unit      VARCHAR(100) COMMENT '操作人单位',
    operate_time       DATETIME NOT NULL COMMENT '操作时间',
    operator_ip        VARCHAR(50) COMMENT '操作人IP地址',
    detail             TEXT COMMENT '操作详情JSON格式',
    prev_hash          VARCHAR(64) COMMENT '前一条日志的哈希值（链式）',
    hash_value         VARCHAR(64) COMMENT '当前日志的HMAC-SHA256哈希值',
    create_time        DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    -- No update_time/update_by: audit logs are immutable
    -- No del_flag: audit logs cannot be deleted
    INDEX idx_evidence_id (evidence_id),
    INDEX idx_operator_id (operator_id),
    INDEX idx_operate_time (operate_time),
    INDEX idx_operation_type (operation_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物证审计日志表（不可删除、不可篡改）';

-- ================================================================
-- Table 3: sys_dept_extend - Department Extension Table
-- ================================================================
-- Extends sys_dept for multi-level unit hierarchy support:
-- - City level (1): e.g., "000" (provincial capital)
-- - District level (2): e.g., "000001" (district under city)
-- - Station level (3): e.g., "000001001" (police station under district)
--
-- Unit code format: parent_code + 3-digit sequence
-- Example hierarchy:
--   000 (市级)
--   ├── 000001 (区县1)
--   │   ├── 000001001 (派出所1)
--   │   └── 000001002 (派出所2)
--   └── 000002 (区县2)
-- ================================================================
DROP TABLE IF EXISTS sys_dept_extend;
CREATE TABLE sys_dept_extend (
    dept_id             BIGINT PRIMARY KEY COMMENT '部门ID（关联sys_dept）',
    del_flag            CHAR(1) DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    create_by           VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by           VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    unit_code           VARCHAR(50) COMMENT '单位编码（唯一，如000/000001/000001001）',
    dept_level          INT COMMENT '单位层级：1=市级 2=区县 3=派出所',
    parent_unit_code    VARCHAR(50) COMMENT '上级单位编码',
    sequence_num        INT DEFAULT 0 COMMENT '同级单位内的序号（3位数字）',
    ancestors_path      VARCHAR(500) COMMENT '祖先单位编码路径（用于DataScope查询）',
    remark              VARCHAR(500) COMMENT '备注',
    UNIQUE KEY uk_unit_code (unit_code),
    KEY idx_parent_unit_code (parent_unit_code),
    CONSTRAINT fk_dept_extend_dept FOREIGN KEY (dept_id) REFERENCES sys_dept(dept_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门扩展表（单位层级）';

-- ================================================================
-- End of PEMS Schema
-- ================================================================
