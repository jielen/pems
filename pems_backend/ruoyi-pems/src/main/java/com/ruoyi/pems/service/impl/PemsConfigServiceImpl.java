package com.ruoyi.pems.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.pems.domain.PemsConfig;
import com.ruoyi.pems.mapper.PemsConfigMapper;
import com.ruoyi.pems.service.IPemsConfigService;

@Service
public class PemsConfigServiceImpl implements IPemsConfigService {

    private static final String CACHE_NAME = "pems:config:";

    @Autowired
    private PemsConfigMapper configMapper;

    @Override
    public List<PemsConfig> selectConfigList(PemsConfig config) {
        return configMapper.selectConfigList(config);
    }

    @Override
    public PemsConfig selectConfigById(Long id) {
        return configMapper.selectConfigById(id);
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#paramKey")
    public String selectConfigByKey(String paramKey) {
        PemsConfig config = configMapper.selectConfigByKey(paramKey);
        return config != null ? config.getParamValue() : null;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#paramKey")
    public PemsConfig selectConfigEntityByKey(String paramKey) {
        return configMapper.selectConfigByKey(paramKey);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public int insertConfig(PemsConfig config) {
        config.setCreateBy(SecurityUtils.getUsername());
        return configMapper.insertConfig(config);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public int updateConfig(PemsConfig config) {
        config.setUpdateBy(SecurityUtils.getUsername());
        return configMapper.updateConfig(config);
    }

    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void refreshCache() {}

    @Override
    public boolean checkConfigKeyUnique(PemsConfig config) {
        Long configId = config.getId() != null ? config.getId() : -1L;
        PemsConfig uniqueConfig = configMapper.checkConfigKeyUnique(config.getParamKey());
        return uniqueConfig == null || uniqueConfig.getId().longValue() == configId.longValue();
    }

    @Override
    public List<PemsConfig> selectEvidenceTypes() {
        PemsConfig query = new PemsConfig();
        query.setParamKey("evidence_type");
        return configMapper.selectConfigList(query);
    }

    @Override
    public List<PemsConfig> selectEvidenceLevels() {
        PemsConfig query = new PemsConfig();
        query.setParamKey("evidence_level");
        return configMapper.selectConfigList(query);
    }

    @Override
    public PemsConfig selectNumberingRule() {
        return configMapper.selectConfigByKey("numbering_rule");
    }

    @Override
    public boolean selectDualLockEnabled() {
        PemsConfig config = configMapper.selectConfigByKey("dual_lock_enabled");
        return config != null && "true".equalsIgnoreCase(config.getParamValue());
    }
}
