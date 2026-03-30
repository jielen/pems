package com.ruoyi.pems.domain;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

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

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long evidenceId;
    private String operationType;
    private Long operatorId;
    private String operatorName;
    private String operatorBadge;
    private String operatorUnit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;
    private String operatorIp;
    private String detail;
    private String prevHash;
    private String hashValue;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId() { return id; }
    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public Long getOperatorId() { return operatorId; }
    public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
    public String getOperatorName() { return operatorName; }
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
    public String getOperatorBadge() { return operatorBadge; }
    public void setOperatorBadge(String operatorBadge) { this.operatorBadge = operatorBadge; }
    public String getOperatorUnit() { return operatorUnit; }
    public void setOperatorUnit(String operatorUnit) { this.operatorUnit = operatorUnit; }
    public Date getOperateTime() { return operateTime; }
    public void setOperateTime(Date operateTime) { this.operateTime = operateTime; }
    public String getOperatorIp() { return operatorIp; }
    public void setOperatorIp(String operatorIp) { this.operatorIp = operatorIp; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getPrevHash() { return prevHash; }
    public String getHashValue() { return hashValue; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public void assignPrevHash(String prevHash) { this.prevHash = prevHash; }
    public void assignHashValue(String hashValue) { this.hashValue = hashValue; }

    public static PemsAuditLogBuilder builder() { return new PemsAuditLogBuilder(); }

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

        public PemsAuditLogBuilder evidenceId(Long v) { this.evidenceId = v; return this; }
        public PemsAuditLogBuilder operationType(String v) { this.operationType = v; return this; }
        public PemsAuditLogBuilder operatorId(Long v) { this.operatorId = v; return this; }
        public PemsAuditLogBuilder operatorName(String v) { this.operatorName = v; return this; }
        public PemsAuditLogBuilder operatorBadge(String v) { this.operatorBadge = v; return this; }
        public PemsAuditLogBuilder operatorUnit(String v) { this.operatorUnit = v; return this; }
        public PemsAuditLogBuilder operateTime(Date v) { this.operateTime = v; return this; }
        public PemsAuditLogBuilder operatorIp(String v) { this.operatorIp = v; return this; }
        public PemsAuditLogBuilder detail(String v) { this.detail = v; return this; }

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
        return "PemsAuditLog{id=" + id + ", evidenceId=" + evidenceId + ", operationType='" + operationType +
               "', operatorId=" + operatorId + ", operatorName='" + operatorName + "', operateTime=" + operateTime + "}";
    }
}
