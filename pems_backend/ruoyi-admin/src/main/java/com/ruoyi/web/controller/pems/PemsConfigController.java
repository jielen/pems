package com.ruoyi.web.controller.pems;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.PemsConfig;
import com.ruoyi.system.service.IPemsConfigService;

/**
 * 物证系统配置管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/pems/config")
public class PemsConfigController extends BaseController {

    @Autowired
    private IPemsConfigService configService;

    /**
     * 获取配置列表
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(PemsConfig config) {
        startPage();
        List<PemsConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    /**
     * 根据ID获取配置详情
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(configService.selectConfigById(id));
    }

    /**
     * 修改配置
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "系统配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PemsConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return error("修改配置'" + config.getParamKey() + "'失败，配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 刷新配置缓存
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "系统配置", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache() {
        configService.refreshCache();
        return success();
    }

    // ==================== CONFIG-01~07 specific endpoints ====================

    /**
     * 获取物证类型配置 (CONFIG-01)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/evidence/types")
    public AjaxResult getEvidenceTypes() {
        List<PemsConfig> list = configService.selectEvidenceTypes();
        return success(list);
    }

    /**
     * 更新物证类型配置 (CONFIG-01)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "物证类型配置", businessType = BusinessType.UPDATE)
    @PutMapping("/evidence/types")
    public AjaxResult updateEvidenceTypes(@RequestBody PemsConfig config) {
        config.setParamKey("evidence_type");
        if (!configService.checkConfigKeyUnique(config)) {
            return error("配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 获取物证等级配置 (CONFIG-02)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/evidence/levels")
    public AjaxResult getEvidenceLevels() {
        List<PemsConfig> list = configService.selectEvidenceLevels();
        return success(list);
    }

    /**
     * 更新物证等级配置 (CONFIG-02)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "物证等级配置", businessType = BusinessType.UPDATE)
    @PutMapping("/evidence/levels")
    public AjaxResult updateEvidenceLevels(@RequestBody PemsConfig config) {
        config.setParamKey("evidence_level");
        if (!configService.checkConfigKeyUnique(config)) {
            return error("配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 获取编号规则配置 (CONFIG-04)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/numbering/rule")
    public AjaxResult getNumberingRule() {
        PemsConfig config = configService.selectNumberingRule();
        return success(config);
    }

    /**
     * 更新编号规则配置 (CONFIG-04)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "编号规则配置", businessType = BusinessType.UPDATE)
    @PutMapping("/numbering/rule")
    public AjaxResult updateNumberingRule(@RequestBody PemsConfig config) {
        config.setParamKey("numbering_rule");
        if (!configService.checkConfigKeyUnique(config)) {
            return error("配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 获取双人双锁配置 (CONFIG-06)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/dual/lock")
    public AjaxResult getDualLockEnabled() {
        boolean enabled = configService.selectDualLockEnabled();
        return success(enabled);
    }

    /**
     * 更新双人双锁配置 (CONFIG-06)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "双人双锁配置", businessType = BusinessType.UPDATE)
    @PutMapping("/dual/lock")
    public AjaxResult updateDualLockEnabled(@RequestBody PemsConfig config) {
        config.setParamKey("dual_lock_enabled");
        config.setParamType("boolean");
        if (!configService.checkConfigKeyUnique(config)) {
            return error("配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 获取保管期限配置 (CONFIG-03)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/retention/period")
    public AjaxResult getRetentionPeriod() {
        PemsConfig config = configService.selectConfigByKey("retention_period");
        return success(config);
    }

    /**
     * 更新保管期限配置 (CONFIG-03)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "保管期限配置", businessType = BusinessType.UPDATE)
    @PutMapping("/retention/period")
    public AjaxResult updateRetentionPeriod(@RequestBody PemsConfig config) {
        config.setParamKey("retention_period");
        config.setParamType("json");
        if (!configService.checkConfigKeyUnique(config)) {
            return error("配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    /**
     * 获取预警规则配置 (CONFIG-07)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/alert/rules")
    public AjaxResult getAlertRules() {
        PemsConfig config = configService.selectConfigByKey("alert_rules");
        return success(config);
    }

    /**
     * 更新预警规则配置 (CONFIG-07)
     */
    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "预警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping("/alert/rules")
    public AjaxResult updateAlertRules(@RequestBody PemsConfig config) {
        config.setParamKey("alert_rules");
        config.setParamType("json");
        if (!configService.checkConfigKeyUnique(config)) {
            return error("配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }
}
