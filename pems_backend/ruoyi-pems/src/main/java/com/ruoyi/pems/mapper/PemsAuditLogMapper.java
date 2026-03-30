package com.ruoyi.pems.mapper;

import java.util.List;
import com.ruoyi.pems.domain.PemsAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物证审计日志Mapper接口（只允许INSERT和SELECT）
 */
@Mapper
public interface PemsAuditLogMapper {
    int insert(PemsAuditLog log);
    PemsAuditLog selectLatest();
    List<PemsAuditLog> selectList(PemsAuditLog query);
    PemsAuditLog selectById(@Param("id") Long id);
    int verifyHashChain(@Param("fromId") Long fromId);
}
