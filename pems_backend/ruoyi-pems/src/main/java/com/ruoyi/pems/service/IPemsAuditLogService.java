package com.ruoyi.pems.service;

import java.util.List;
import com.ruoyi.pems.domain.PemsAuditLog;

/**
 * 物证审计日志服务接口
 */
public interface IPemsAuditLogService {
    void recordLog(Long evidenceId, String operationType, String detail);
    List<PemsAuditLog> selectLogList(PemsAuditLog query);
    PemsAuditLog selectLogById(Long id);
    int verifyIntegrity();
    String computeHash(PemsAuditLog log, String prevHash);
}
