package com.ruoyi.pems.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pems.domain.PemsAuditLog;
import com.ruoyi.pems.service.IPemsAuditLogService;

@RestController
@RequestMapping("/pems/audit/log")
public class PemsAuditLogController extends BaseController {

    @Autowired
    private IPemsAuditLogService auditLogService;

    @PreAuthorize("@ss.hasPermi('pems:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(PemsAuditLog query) {
        startPage();
        return getDataTable(auditLogService.selectLogList(query));
    }

    @PreAuthorize("@ss.hasPermi('pems:log:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response, PemsAuditLog query) {
        List<PemsAuditLog> list = auditLogService.selectLogList(query);
        ExcelUtil<PemsAuditLog> util = new ExcelUtil<>(PemsAuditLog.class);
        util.exportExcel(response, list, "审计日志");
    }

    @PreAuthorize("@ss.hasPermi('pems:log:list')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(auditLogService.selectLogById(id));
    }

    @PreAuthorize("@ss.hasPermi('pems:log:list')")
    @GetMapping("/verify")
    public AjaxResult verifyIntegrity() {
        int broken = auditLogService.verifyIntegrity();
        return broken == 0 ? success("哈希链完整有效") : error("哈希链在 " + broken + " 处断裂");
    }
}
