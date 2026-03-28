import request from '@/utils/request'
import type { AjaxResult, TableDataInfo } from '@/types'

// 配置查询参数
export interface ConfigQuery {
  pageNum?: number
  pageSize?: number
  paramKey?: string
  paramType?: string
  paramValue?: string
}

// 配置
export interface PemsConfig {
  id?: number
  paramKey: string
  paramValue: string
  paramType: string
  remark?: string
  createBy?: string
  createTime?: string
  updateBy?: string
  updateTime?: string
}

// 查询配置列表
export function listConfig(query: ConfigQuery): Promise<TableDataInfo<PemsConfig[]>> {
  return request({
    url: '/pems/config/list',
    method: 'get',
    params: query
  })
}

// 查询配置详情
export function getConfig(id: number): Promise<AjaxResult<PemsConfig>> {
  return request({
    url: '/pems/config/' + id,
    method: 'get'
  })
}

// 修改配置
export function updateConfig(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config',
    method: 'put',
    data: data
  })
}

// 刷新配置缓存
export function refreshConfigCache(): Promise<AjaxResult> {
  return request({
    url: '/pems/config/refreshCache',
    method: 'delete'
  })
}

// ==================== CONFIG-01~07 specific APIs ====================

// 获取物证类型配置 (CONFIG-01)
export function getEvidenceTypes(): Promise<AjaxResult<PemsConfig[]>> {
  return request({
    url: '/pems/config/evidence/types',
    method: 'get'
  })
}

// 更新物证类型配置 (CONFIG-01)
export function updateEvidenceTypes(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config/evidence/types',
    method: 'put',
    data: data
  })
}

// 获取物证等级配置 (CONFIG-02)
export function getEvidenceLevels(): Promise<AjaxResult<PemsConfig[]>> {
  return request({
    url: '/pems/config/evidence/levels',
    method: 'get'
  })
}

// 更新物证等级配置 (CONFIG-02)
export function updateEvidenceLevels(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config/evidence/levels',
    method: 'put',
    data: data
  })
}

// 获取编号规则配置 (CONFIG-04)
export function getNumberingRule(): Promise<AjaxResult<PemsConfig>> {
  return request({
    url: '/pems/config/numbering/rule',
    method: 'get'
  })
}

// 更新编号规则配置 (CONFIG-04)
export function updateNumberingRule(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config/numbering/rule',
    method: 'put',
    data: data
  })
}

// 获取双人双锁配置 (CONFIG-06)
export function getDualLockEnabled(): Promise<AjaxResult<boolean>> {
  return request({
    url: '/pems/config/dual/lock',
    method: 'get'
  })
}

// 更新双人双锁配置 (CONFIG-06)
export function updateDualLockEnabled(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config/dual/lock',
    method: 'put',
    data: data
  })
}

// 获取保管期限配置 (CONFIG-03)
export function getRetentionPeriod(): Promise<AjaxResult<PemsConfig>> {
  return request({
    url: '/pems/config/retention/period',
    method: 'get'
  })
}

// 更新保管期限配置 (CONFIG-03)
export function updateRetentionPeriod(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config/retention/period',
    method: 'put',
    data: data
  })
}

// 获取预警规则配置 (CONFIG-07)
export function getAlertRules(): Promise<AjaxResult<PemsConfig>> {
  return request({
    url: '/pems/config/alert/rules',
    method: 'get'
  })
}

// 更新预警规则配置 (CONFIG-07)
export function updateAlertRules(data: PemsConfig): Promise<AjaxResult> {
  return request({
    url: '/pems/config/alert/rules',
    method: 'put',
    data: data
  })
}
