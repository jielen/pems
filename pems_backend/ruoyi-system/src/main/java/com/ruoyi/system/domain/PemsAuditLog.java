package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 物证审计日志表 pems_audit_log
 *
 * Immutable audit log with HMAC-SHA256 hash chain for tamper detection.
 * This table does NOT support update or delete operations.
 *
 * @author ruoyi
 */
@TableName("pems_audit_log")
public class PemsAuditLog extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 关联物证ID（系统操作时为空） */
    private Long evidenceId;

    /** 操作类型：CREATE/UPDATE/DELETE/TRANSFER/BORROW/RETURN/DESTROY等 */
    private String operationType;

    /** 操作人ID */
    private Long operatorId;

    /** 操作人姓名 */
    private String operatorName;

    /** 操作人警号 */
    private String operatorBadge;

    /** 操作人单位 */
    private String operatorUnit;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    /** 操作人IP地址 */
    private String operatorIp;

    /** 操作详情JSON格式 */
    private String detail;

    /** 前一条日志的哈希值（链式） */
    private String prevHash;

    /** 当前日志的HMAC-SHA256哈希值 */
    private String hashValue;

    /** 记录创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // Note: No updateTime/updateBy fields - audit logs are immutable

    public Long getId() {
        return id;
    }

    // No setter for id - auto-generated

    public Long getEvidenceId() {
        return evidenceId;
    }

    public void setEvidenceId(Long evidenceId) {
        this.evidenceId = evidenceId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorBadge() {
        return operatorBadge;
    }

    public void setOperatorBadge(String operatorBadge) {
        this.operatorBadge = operatorBadge;
    }

    public String getOperatorUnit() {
        return operatorUnit;
    }

    public void setOperatorUnit(String operatorUnit) {
        this.operatorUnit = operatorUnit;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPrevHash() {
        return prevHash;
    }

    // No public setter for prevHash - set internally during hash computation

    public String getHashValue() {
        return hashValue;
    }

    // No public setter for hashValue - computed and set internally

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * Internal method to set prevHash during hash chain computation.
     * This should only be called by the hash computation service.
     */
    public void assignPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    /**
     * Internal method to set computed hashValue during hash chain computation.
     * This should only be called by the hash computation service.
     */
    public void assignHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    /**
     * Builder-style method to construct audit log with all required fields.
     * Used during hash chain computation.
     */
    public static PemsAuditLogBuilder builder() {
        return new PemsAuditLogBuilder();
    }

    public static class PemsAuditLogBuilder {
        private Long evidenceId;
        private String operationType;
        private Long operatorId;
        private String operatorName;
        private String operatorBadge;
        private String operatorUnit;
        private Date operateTime;
        private String operatorIp;
        private String detail;

        public PemsAuditLogBuilder evidenceId(Long evidenceId) {
            this.evidenceId = evidenceId;
            return this;
        }

        public PemsAuditLogBuilder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public PemsAuditLogBuilder operatorId(Long operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public PemsAuditLogBuilder operatorName(String operatorName) {
            this.operatorName = operatorName;
            return this;
        }

        public PemsAuditLogBuilder operatorBadge(String operatorBadge) {
            this.operatorBadge = operatorBadge;
            return this;
        }

        public PemsAuditLogBuilder operatorUnit(String operatorUnit) {
            this.operatorUnit = operatorUnit;
            return this;
        }

        public PemsAuditLogBuilder operateTime(Date operateTime) {
            this.operateTime = operateTime;
            return this;
        }

        public PemsAuditLogBuilder operatorIp(String operatorIp) {
            this.operatorIp = operatorIp;
            return this;
        }

        public PemsAuditLogBuilder detail(String detail) {
            this.detail = detail;
            return this;
        }

        public PemsAuditLog build() {
            PemsAuditLog log = new PemsAuditLog();
            log.setEvidenceId(this.evidenceId);
            log.setOperationType(this.operationType);
            log.setOperatorId(this.operatorId);
            log.setOperatorName(this.operatorName);
            log.setOperatorBadge(this.operatorBadge);
            log.setOperatorUnit(this.operatorUnit);
            log.setOperateTime(this.operateTime);
            log.setOperatorIp(this.operatorIp);
            log.setDetail(this.detail);
            log.setCreateTime(new Date());
            return log;
        }
    }

    @Override
    public String toString() {
        return "PemsAuditLog{" +
            "id=" + id +
            ", evidenceId=" + evidenceId +
            ", operationType='" + operationType + "'" +
            ", operatorId=" + operatorId +
            ", operatorName='" + operatorName + "'" +
            ", operatorBadge='" + operatorBadge + "'" +
            ", operatorUnit='" + operatorUnit + "'" +
            ", operateTime=" + operateTime +
            ", operatorIp='" + operatorIp + "'" +
            ", detail='" + detail + "'" +
            ", prevHash='" + prevHash + "'" +
            ", hashValue='" + hashValue + "'" +
            ", createTime=" + createTime +
            "}";
    }
}
