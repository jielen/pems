package com.ruoyi.pems.service;

import java.util.List;
import com.ruoyi.pems.domain.PemsConfig;

/**
 * 物证系统配置服务接口
 */
public interface IPemsConfigService {
    List<PemsConfig> selectConfigList(PemsConfig config);
    PemsConfig selectConfigById(Long id);
    String selectConfigByKey(String paramKey);
    PemsConfig selectConfigEntityByKey(String paramKey);
    int insertConfig(PemsConfig config);
    int updateConfig(PemsConfig config);
    void refreshCache();
    boolean checkConfigKeyUnique(PemsConfig config);
    List<PemsConfig> selectEvidenceTypes();
    List<PemsConfig> selectEvidenceLevels();
    PemsConfig selectNumberingRule();
    boolean selectDualLockEnabled();
}
