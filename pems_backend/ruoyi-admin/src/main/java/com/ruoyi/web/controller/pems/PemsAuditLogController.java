package com.ruoyi.web.controller.pems;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.PemsAuditLog;
import com.ruoyi.system.service.IPemsAuditLogService;

/**
 * 物证审计日志管理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/pems/audit/log")
public class PemsAuditLogController extends BaseController {

    @Autowired
    private IPemsAuditLogService auditLogService;

    /**
     * 获取审计日志列表
     */
    @PreAuthorize("@ss.hasPermi('pems:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(PemsAuditLog query) {
        startPage();
        List<PemsAuditLog> list = auditLogService.selectLogList(query);
        return getDataTable(list);
    }

    /**
     * 导出审计日志
     */
    @PreAuthorize("@ss.hasPermi('pems:log:export')")
    @GetMapping("/export")
    public void export(HttpServletResponse response, PemsAuditLog query) {
        List<PemsAuditLog> list = auditLogService.selectLogList(query);
        ExcelUtil<PemsAuditLog> util = new ExcelUtil<PemsAuditLog>(PemsAuditLog.class);
        util.exportExcel(response, list, "审计日志");
    }

    /**
     * 获取审计日志详情
     */
    @PreAuthorize("@ss.hasPermi('pems:log:list')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return success(auditLogService.selectLogById(id));
    }

    /**
     * 验证哈希链完整性
     */
    @PreAuthorize("@ss.hasPermi('pems:log:list')")
    @GetMapping("/verify")
    public AjaxResult verifyIntegrity() {
        int broken = auditLogService.verifyIntegrity();
        if (broken == 0) {
            return success("哈希链完整有效");
        } else {
            return error("哈希链在 " + broken + " 处断裂");
        }
    }
}
