import { apiClient } from '@/api/client'

export interface DeliveryHistoryResponse {
  id: string
  reportId: string
  recipient: string
  status: string
  provider: string
  providerMessageId: string
  attempt: number
  latencyMs: number
  error: string
  createdAt: string
}

export interface ReportArchiveResponse {
  reportId: string
  htmlContent: string
  txtContent: string
  jsonContent: string
  markdownContent: string
  createdAt: string
}

export interface ReportingAttachmentConfig {
  txt: boolean
  json: boolean
  md: boolean
  html: boolean
}

export interface ReportingConfigurationResponse {
  enabled: boolean
  archiveEnabled: boolean
  retryEnabled: boolean
  previewEnabled: boolean
  maxRetries: number
  subjectTemplate: string
  sender: string
  recipients: string[]
  closings: string[]
  attachments: ReportingAttachmentConfig
  emailEnabled: boolean
}

export interface SendTestEmailResponse {
  status: string
  error: string
  recipientsSent: number
  recipientsFailed: number
}

export async function getReportingHistory(): Promise<DeliveryHistoryResponse[]> {
  const { data } = await apiClient.get<DeliveryHistoryResponse[]>('/api/reporting/history')
  return data
}

export async function getReportingArchive(): Promise<ReportArchiveResponse[]> {
  const { data } = await apiClient.get<ReportArchiveResponse[]>('/api/reporting/archive')
  return data
}

export async function getReportingArchiveById(reportId: string): Promise<ReportArchiveResponse> {
  const { data } = await apiClient.get<ReportArchiveResponse>(`/api/reporting/archive/${reportId}`)
  return data
}

export async function getReportingConfiguration(): Promise<ReportingConfigurationResponse> {
  const { data } = await apiClient.get<ReportingConfigurationResponse>('/api/reporting/dev/configuration')
  return data
}

export async function previewReportingHtml(): Promise<string> {
  const { data } = await apiClient.get<string>('/api/reporting/preview/html', {
    responseType: 'text',
  })
  return data
}

export async function previewReportingTxt(): Promise<string> {
  const { data } = await apiClient.get<string>('/api/reporting/preview/txt', {
    responseType: 'text',
  })
  return data
}

export async function previewReportingJson(): Promise<string> {
  const { data } = await apiClient.get<string>('/api/reporting/preview/json', {
    responseType: 'text',
  })
  return data
}

export async function previewReportingMarkdown(): Promise<string> {
  const { data } = await apiClient.get<string>('/api/reporting/preview/md', {
    responseType: 'text',
  })
  return data
}

export async function sendTestReportEmail(): Promise<SendTestEmailResponse> {
  const { data } = await apiClient.post<SendTestEmailResponse>('/api/reporting/send-test')
  return data
}
