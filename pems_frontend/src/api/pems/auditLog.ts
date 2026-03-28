import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'

// 审计日志查询参数
export interface AuditLogQuery {
  pageNum?: number
  pageSize?: number
  evidenceId?: number
  operationType?: string
  operatorName?: string
  operatorBadge?: string
  operatorUnit?: string
  beginTime?: string
  endTime?: string
}

// 审计日志
export interface PemsAuditLog {
  id: number
  evidenceId: number
  operationType: string
  operatorId: number
  operatorName: string
  operatorBadge: string
  operatorUnit: string
  operateTime: string
  operatorIp: string
  detail: string
  prevHash: string
  hashValue: string
  createTime: string
}

// 查询审计日志列表
export function listAuditLog(query: AuditLogQuery): Promise<TableDataInfo<PemsAuditLog[]>> {
  return request({
    url: '/pems/audit/log/list',
    method: 'get',
    params: query
  })
}

// 查询审计日志详情
export function getAuditLog(id: number): Promise<AjaxResult<PemsAuditLog>> {
  return request({
    url: '/pems/audit/log/' + id,
    method: 'get'
  })
}

// 验证哈希链完整性
export function verifyAuditLog(): Promise<AjaxResult<{ broken: number }>> {
  return request({
    url: '/pems/audit/log/verify',
    method: 'get'
  })
}

// 导出审计日志
export function exportAuditLog(query: AuditLogQuery) {
  return request({
    url: '/pems/audit/log/export',
    method: 'get',
    params: query,
    responseType: 'blob'
  })
}
