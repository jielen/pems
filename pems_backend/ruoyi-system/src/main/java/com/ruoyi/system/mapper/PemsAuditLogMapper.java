package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.PemsAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物证审计日志Mapper接口
 *
 * Immutable audit log - only INSERT and SELECT operations are supported.
 * NO update or delete operations are allowed.
 *
 * @author ruoyi
 */
@Mapper
public interface PemsAuditLogMapper {

    /**
     * 插入审计日志
     *
     * @param log 审计日志
     * @return 影响行数
     */
    int insert(PemsAuditLog log);

    /**
     * 查询最新的审计日志（用于获取上一个哈希值）
     *
     * @return 最新审计日志
     */
    PemsAuditLog selectLatest();

    /**
     * 查询审计日志列表
     *
     * @param query 查询条件
     * @return 审计日志列表
     */
    List<PemsAuditLog> selectList(PemsAuditLog query);

    /**
     * 根据ID查询审计日志
     *
     * @param id 日志ID
     * @return 审计日志
     */
    PemsAuditLog selectById(@Param("id") Long id);

    /**
     * 验证哈希链完整性
     * 返回从某位置开始验证失败的个数
     *
     * @param fromId 起始ID
     * @return 验证失败的个数，0表示全部有效
     */
    int verifyHashChain(@Param("fromId") Long fromId);
}
