package com.ruoyi.pems.service.impl;

import java.util.Date;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.pems.domain.PemsAuditLog;
import com.ruoyi.pems.mapper.PemsAuditLogMapper;
import com.ruoyi.pems.service.IPemsAuditLogService;

@Service
public class PemsAuditLogServiceImpl implements IPemsAuditLogService {

    private static final String HMAC_KEY = "pems-audit-secret-key-change-in-production";
    private static final String GENESIS_HASH = "GENESIS";

    @Autowired
    private PemsAuditLogMapper auditLogMapper;

    @Override
    public void recordLog(Long evidenceId, String operationType, String detail) {
        PemsAuditLog log = new PemsAuditLog();
        log.setEvidenceId(evidenceId);
        log.setOperationType(operationType);
        log.setDetail(detail);

        Long userId = SecurityUtils.getUserId();
        String userName = SecurityUtils.getUsername();
        String badge = SecurityUtils.getLoginUser() != null
            ? SecurityUtils.getLoginUser().getUser().getBadge() : null;
        String unitName = SecurityUtils.getLoginUser() != null
            && SecurityUtils.getLoginUser().getUser().getDept() != null
            ? SecurityUtils.getLoginUser().getUser().getDept().getDeptName() : null;

        log.setOperatorId(userId);
        log.setOperatorName(userName);
        log.setOperatorBadge(badge);
        log.setOperatorUnit(unitName);
        log.setOperateTime(new Date());
        log.setOperatorIp(IpUtils.getIpAddr());

        PemsAuditLog prevLog = auditLogMapper.selectLatest();
        String prevHash = (prevLog != null) ? prevLog.getHashValue() : GENESIS_HASH;
        log.assignPrevHash(prevHash);
        log.assignHashValue(computeHash(log, prevHash));

        auditLogMapper.insert(log);
    }

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
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hash computation failed", e);
        }
    }

    @Override
    public int verifyIntegrity() {
        List<PemsAuditLog> allLogs = auditLogMapper.selectList(new PemsAuditLog());
        if (allLogs == null || allLogs.isEmpty()) return 0;
        int brokenCount = 0;
        String expectedPrevHash = GENESIS_HASH;
        for (PemsAuditLog log : allLogs) {
            if (!expectedPrevHash.equals(log.getPrevHash())) { brokenCount++; continue; }
            if (!computeHash(log, expectedPrevHash).equals(log.getHashValue())) brokenCount++;
            expectedPrevHash = log.getHashValue();
        }
        return brokenCount;
    }

    @Override
    public List<PemsAuditLog> selectLogList(PemsAuditLog query) {
        return auditLogMapper.selectList(query);
    }

    @Override
    public PemsAuditLog selectLogById(Long id) {
        return auditLogMapper.selectById(id);
    }
}
