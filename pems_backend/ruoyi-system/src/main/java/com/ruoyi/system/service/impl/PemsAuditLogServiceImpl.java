package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.PemsAuditLog;
import com.ruoyi.system.mapper.PemsAuditLogMapper;
import com.ruoyi.system.service.IPemsAuditLogService;

/**
 * 物证审计日志服务实现
 *
 * Immutable audit log service with HMAC-SHA256 hash chain.
 * Audit logs can NEVER be updated or deleted.
 *
 * @author ruoyi
 */
@Service
public class PemsAuditLogServiceImpl implements IPemsAuditLogService {

    /** HMAC secret key for hash computation - should be in config/env in production */
    private static final String HMAC_KEY = "pems-audit-secret-key-change-in-production";

    /** Genesis block marker for the first log entry */
    private static final String GENESIS_HASH = "GENESIS";

    @Autowired
    private PemsAuditLogMapper auditLogMapper;

    /**
     * 记录审计日志
     */
    @Override
    public void recordLog(Long evidenceId, String operationType, String detail) {
        PemsAuditLog log = new PemsAuditLog();

        // Set evidence and operation info
        log.setEvidenceId(evidenceId);
        log.setOperationType(operationType);
        log.setDetail(detail);

        // Get current user info from SecurityUtils
        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        String badge = SecurityUtils.getLoginUser() != null
            ? SecurityUtils.getLoginUser().getUser().getBadge()
            : null;
        String unitName = SecurityUtils.getLoginUser() != null
            ? SecurityUtils.getLoginUser().getUser().getDept() != null
                ? SecurityUtils.getLoginUser().getUser().getDept().getDeptName()
                : null
            : null;

        log.setOperatorId(userId);
        log.setOperatorName(userName);
        log.setOperatorBadge(badge);
        log.setOperatorUnit(unitName);
        log.setOperateTime(new Date());
        log.setOperatorIp(IpUtils.getIpAddr());

        // Get previous hash for chain
        PemsAuditLog prevLog = auditLogMapper.selectLatest();
        String prevHash = (prevLog != null) ? prevLog.getHashValue() : GENESIS_HASH;
        log.assignPrevHash(prevHash);

        // Compute current hash: HMAC-SHA256(prevHash + evidenceId + operationType + operatorId + operateTime + detail)
        String hash = computeHash(log, prevHash);
        log.assignHashValue(hash);

        // Insert only - audit logs are IMMUTABLE
        auditLogMapper.insert(log);
    }

    /**
     * 计算审计日志的哈希值
     */
    @Override
    public String computeHash(PemsAuditLog log, String prevHash) {
        String data = prevHash + "|" +
                      (log.getEvidenceId() != null ? log.getEvidenceId() : "") + "|" +
                      (log.getOperationType() != null ? log.getOperationType() : "") + "|" +
                      (log.getOperatorId() != null ? log.getOperatorId() : "") + "|" +
                      (log.getOperateTime() != null ? log.getOperateTime().getTime() : "") + "|" +
                      (log.getDetail() != null ? log.getDetail() : "");
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(HMAC_KEY.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Hash computation failed", e);
        }
    }

    /**
     * 验证哈希链完整性
     * 遍历所有日志，按顺序验证每个哈希是否正确
     */
    @Override
    public int verifyIntegrity() {
        List<PemsAuditLog> allLogs = auditLogMapper.selectList(new PemsAuditLog());

        if (allLogs == null || allLogs.isEmpty()) {
            return 0; // No logs = valid chain
        }

        int brokenCount = 0;
        String expectedPrevHash = GENESIS_HASH;

        for (PemsAuditLog log : allLogs) {
            // Verify prevHash matches expected
            if (!expectedPrevHash.equals(log.getPrevHash())) {
                brokenCount++;
                continue;
            }

            // Verify hash matches computed
            String computedHash = computeHash(log, expectedPrevHash);
            if (!computedHash.equals(log.getHashValue())) {
                brokenCount++;
            }

            // Move to next
            expectedPrevHash = log.getHashValue();
        }

        return brokenCount;
    }

    /**
     * 查询审计日志列表
     */
    @Override
    public List<PemsAuditLog> selectLogList(PemsAuditLog query) {
        return auditLogMapper.selectList(query);
    }

    /**
     * 根据ID查询审计日志
     */
    @Override
    public PemsAuditLog selectLogById(Long id) {
        return auditLogMapper.selectById(id);
    }

    /**
     * Convert bytes to hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
