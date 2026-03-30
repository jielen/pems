package com.ruoyi.pems.mapper;

import java.util.List;
import com.ruoyi.pems.domain.PemsConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物证系统配置Mapper接口
 */
@Mapper
public interface PemsConfigMapper {
    List<PemsConfig> selectConfigList(PemsConfig config);
    PemsConfig selectConfigById(@Param("id") Long id);
    PemsConfig selectConfigByKey(@Param("paramKey") String paramKey);
    int insertConfig(PemsConfig config);
    int updateConfig(PemsConfig config);
    PemsConfig checkConfigKeyUnique(@Param("paramKey") String paramKey);
}
