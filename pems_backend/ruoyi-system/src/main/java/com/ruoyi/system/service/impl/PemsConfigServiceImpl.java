package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.PemsConfig;
import com.ruoyi.system.mapper.PemsConfigMapper;
import com.ruoyi.system.service.IPemsConfigService;

/**
 * 物证系统配置服务实现
 *
 * @author ruoyi
 */
@Service
public class PemsConfigServiceImpl implements IPemsConfigService {

    /** 配置缓存名称 */
    private static final String CACHE_NAME = "pems:config:";

    @Autowired
    private PemsConfigMapper configMapper;

    /**
     * 查询配置列表
     */
    @Override
    public List<PemsConfig> selectConfigList(PemsConfig config) {
        return configMapper.selectConfigList(config);
    }

    /**
     * 根据ID查询配置
     */
    @Override
    public PemsConfig selectConfigById(Long id) {
        return configMapper.selectConfigById(id);
    }

    /**
     * 根据配置键查询配置值
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "#paramKey")
    public String selectConfigByKey(String paramKey) {
        PemsConfig config = configMapper.selectConfigByKey(paramKey);
        return config != null ? config.getParamValue() : null;
    }

    /**
     * 根据配置键查询配置实体
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "#paramKey")
    public PemsConfig selectConfigEntityByKey(String paramKey) {
        return configMapper.selectConfigByKey(paramKey);
    }

    /**
     * 新增配置
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public int insertConfig(PemsConfig config) {
        config.setCreateBy(SecurityUtils.getUsername());
        return configMapper.insertConfig(config);
    }

    /**
     * 修改配置
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public int updateConfig(PemsConfig config) {
        config.setUpdateBy(SecurityUtils.getUsername());
        return configMapper.updateConfig(config);
    }

    /**
     * 刷新配置缓存
     */
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void refreshCache() {
        // Cache evict annotation handles this
    }

    /**
     * 校验配置键是否唯一
     */
    @Override
    public boolean checkConfigKeyUnique(PemsConfig config) {
        Long configId = config.getId() != null ? config.getId() : -1L;
        PemsConfig uniqueConfig = configMapper.checkConfigKeyUnique(config.getParamKey());
        if (uniqueConfig != null && uniqueConfig.getId().longValue() != configId.longValue()) {
            return false;
        }
        return true;
    }

    /**
     * 获取物证类型配置
     */
    @Override
    public List<PemsConfig> selectEvidenceTypes() {
        PemsConfig query = new PemsConfig();
        query.setParamKey("evidence_type");
        List<PemsConfig> list = configMapper.selectConfigList(query);
        return list;
    }

    /**
     * 获取物证等级配置
     */
    @Override
    public List<PemsConfig> selectEvidenceLevels() {
        PemsConfig query = new PemsConfig();
        query.setParamKey("evidence_level");
        List<PemsConfig> list = configMapper.selectConfigList(query);
        return list;
    }

    /**
     * 获取编号规则配置
     */
    @Override
    public PemsConfig selectNumberingRule() {
        return configMapper.selectConfigByKey("numbering_rule");
    }

    /**
     * 获取双人双锁配置
     */
    @Override
    public boolean selectDualLockEnabled() {
        PemsConfig config = configMapper.selectConfigByKey("dual_lock_enabled");
        return config != null && "true".equalsIgnoreCase(config.getParamValue());
    }
}
