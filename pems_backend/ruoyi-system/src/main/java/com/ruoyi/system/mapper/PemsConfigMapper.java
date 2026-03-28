package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.PemsConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 物证系统配置Mapper接口
 *
 * @author ruoyi
 */
@Mapper
public interface PemsConfigMapper {

    /**
     * 查询配置列表
     *
     * @param config 查询条件
     * @return 配置列表
     */
    List<PemsConfig> selectConfigList(PemsConfig config);

    /**
     * 根据ID查询配置
     *
     * @param id 配置ID
     * @return 配置信息
     */
    PemsConfig selectConfigById(@Param("id") Long id);

    /**
     * 根据配置键查询配置
     *
     * @param paramKey 配置键
     * @return 配置信息
     */
    PemsConfig selectConfigByKey(@Param("paramKey") String paramKey);

    /**
     * 新增配置
     *
     * @param config 配置信息
     * @return 影响行数
     */
    int insertConfig(PemsConfig config);

    /**
     * 修改配置
     *
     * @param config 配置信息
     * @return 影响行数
     */
    int updateConfig(PemsConfig config);

    /**
     * 校验配置键是否唯一
     *
     * @param config 配置信息
     * @return 结果
     */
    PemsConfig checkConfigKeyUnique(@Param("paramKey") String paramKey);
}
