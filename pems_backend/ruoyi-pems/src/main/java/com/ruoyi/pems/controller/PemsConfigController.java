package com.ruoyi.pems.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pems.domain.PemsConfig;
import com.ruoyi.pems.service.IPemsConfigService;

@RestController
@RequestMapping("/pems/config")
public class PemsConfigController extends BaseController {

    @Autowired
    private IPemsConfigService configService;

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(PemsConfig config) {
        startPage();
        return getDataTable(configService.selectConfigList(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(configService.selectConfigById(id));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "系统配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PemsConfig config) {
        if (!configService.checkConfigKeyUnique(config)) {
            return error("修改配置'" + config.getParamKey() + "'失败，配置键已存在");
        }
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "系统配置", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public AjaxResult refreshCache() {
        configService.refreshCache();
        return success();
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/evidence/types")
    public AjaxResult getEvidenceTypes() {
        return success(configService.selectEvidenceTypes());
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "物证类型配置", businessType = BusinessType.UPDATE)
    @PutMapping("/evidence/types")
    public AjaxResult updateEvidenceTypes(@RequestBody PemsConfig config) {
        config.setParamKey("evidence_type");
        if (!configService.checkConfigKeyUnique(config)) return error("配置键已存在");
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/evidence/levels")
    public AjaxResult getEvidenceLevels() {
        return success(configService.selectEvidenceLevels());
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "物证等级配置", businessType = BusinessType.UPDATE)
    @PutMapping("/evidence/levels")
    public AjaxResult updateEvidenceLevels(@RequestBody PemsConfig config) {
        config.setParamKey("evidence_level");
        if (!configService.checkConfigKeyUnique(config)) return error("配置键已存在");
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/numbering/rule")
    public AjaxResult getNumberingRule() {
        return success(configService.selectNumberingRule());
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "编号规则配置", businessType = BusinessType.UPDATE)
    @PutMapping("/numbering/rule")
    public AjaxResult updateNumberingRule(@RequestBody PemsConfig config) {
        config.setParamKey("numbering_rule");
        if (!configService.checkConfigKeyUnique(config)) return error("配置键已存在");
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/dual/lock")
    public AjaxResult getDualLockEnabled() {
        return success(configService.selectDualLockEnabled());
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "双人双锁配置", businessType = BusinessType.UPDATE)
    @PutMapping("/dual/lock")
    public AjaxResult updateDualLockEnabled(@RequestBody PemsConfig config) {
        config.setParamKey("dual_lock_enabled");
        config.setParamType("boolean");
        if (!configService.checkConfigKeyUnique(config)) return error("配置键已存在");
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/retention/period")
    public AjaxResult getRetentionPeriod() {
        return success(configService.selectConfigEntityByKey("retention_period"));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "保管期限配置", businessType = BusinessType.UPDATE)
    @PutMapping("/retention/period")
    public AjaxResult updateRetentionPeriod(@RequestBody PemsConfig config) {
        config.setParamKey("retention_period");
        config.setParamType("json");
        if (!configService.checkConfigKeyUnique(config)) return error("配置键已存在");
        return toAjax(configService.updateConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:list')")
    @GetMapping("/alert/rules")
    public AjaxResult getAlertRules() {
        return success(configService.selectConfigEntityByKey("alert_rules"));
    }

    @PreAuthorize("@ss.hasPermi('pems:config:edit')")
    @Log(title = "预警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping("/alert/rules")
    public AjaxResult updateAlertRules(@RequestBody PemsConfig config) {
        config.setParamKey("alert_rules");
        config.setParamType("json");
        if (!configService.checkConfigKeyUnique(config)) return error("配置键已存在");
        return toAjax(configService.updateConfig(config));
    }
}
