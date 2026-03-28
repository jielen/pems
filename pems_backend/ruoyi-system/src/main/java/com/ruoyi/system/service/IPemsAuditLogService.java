package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.PemsAuditLog;

/**
 * 物证审计日志服务接口
 *
 * Immutable audit log service with HMAC-SHA256 hash chain.
 *
 * @author ruoyi
 */
public interface IPemsAuditLogService {

    /**
     * 记录审计日志
     *
     * @param evidenceId     物证ID（可选）
     * @param operationType  操作类型
     * @param detail         操作详情（JSON格式）
     */
    void recordLog(Long evidenceId, String operationType, String detail);

    /**
     * 查询审计日志列表
     *
     * @param query 查询条件
     * @return 审计日志列表
     */
    List<PemsAuditLog> selectLogList(PemsAuditLog query);

    /**
     * 根据ID查询审计日志
     *
     * @param id 日志ID
     * @return 审计日志
     */
    PemsAuditLog selectLogById(Long id);

    /**
     * 验证哈希链完整性
     *
     * @return 验证失败的个数，0表示哈希链完整有效
     */
    int verifyIntegrity();

    /**
     * 计算审计日志的哈希值
     *
     * @param log      审计日志
     * @param prevHash 前一个哈希值
     * @return 当前哈希值
     */
    String computeHash(PemsAuditLog log, String prevHash);
}
