package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.PemsConfig;

/**
 * 物证系统配置服务接口
 *
 * @author ruoyi
 */
public interface IPemsConfigService {

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
    PemsConfig selectConfigById(Long id);

    /**
     * 根据配置键查询配置值
     *
     * @param paramKey 配置键
     * @return 配置值
     */
    String selectConfigByKey(String paramKey);

    /**
     * 新增配置
     *
     * @param config 配置信息
     * @return 结果
     */
    int insertConfig(PemsConfig config);

    /**
     * 修改配置
     *
     * @param config 配置信息
     * @return 结果
     */
    int updateConfig(PemsConfig config);

    /**
     * 刷新配置缓存
     */
    void refreshCache();

    /**
     * 校验配置键是否唯一
     *
     * @param config 配置信息
     * @return 结果
     */
    boolean checkConfigKeyUnique(PemsConfig config);

    /**
     * 获取物证类型配置
     *
     * @return 物证类型列表
     */
    List<PemsConfig> selectEvidenceTypes();

    /**
     * 获取物证等级配置
     *
     * @return 物证等级列表
     */
    List<PemsConfig> selectEvidenceLevels();

    /**
     * 获取编号规则配置
     *
     * @return 编号规则
     */
    PemsConfig selectNumberingRule();

    /**
     * 获取双人双锁配置
     *
     * @return 是否启用双人双锁
     */
    boolean selectDualLockEnabled();
}
