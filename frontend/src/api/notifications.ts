import { apiClient } from '@/api/client'

export type NotificationType =
  | 'GREETING'
  | 'REMINDER'
  | 'STORY'
  | 'GOSSIP'
  | 'QUESTION'
  | 'MEMORY'
  | 'FESTIVAL'
  | 'BIRTHDAY'
  | 'WORLD_EVENT'
  | 'SYSTEM'

export type NotificationPriority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export type NotificationStatus = 'PENDING' | 'DELIVERED' | 'READ' | 'DISMISSED' | 'EXPIRED'

export interface NotificationResponse {
  id: string
  recipientCharacterId: string
  type: NotificationType
  priority: NotificationPriority
  title: string
  summary: string
  status: NotificationStatus
  createdAt: string
  expiresAt: string
  source: string
  trigger: string
  metadata: Record<string, string>
}

export interface NotificationUnreadCountResponse {
  unreadCount: number
}

export interface NotificationGenerationTraceEntry {
  rule: string
  reason: string
}

export interface NotificationGenerationResponse {
  characterId: string
  generatedAt: string
  trace: NotificationGenerationTraceEntry[]
  generatedNotifications: NotificationResponse[]
}

export async function listNotifications(): Promise<NotificationResponse[]> {
  const { data } = await apiClient.get<NotificationResponse[]>('/api/notifications')
  return data
}

export async function getUnreadNotificationCount(): Promise<NotificationUnreadCountResponse> {
  const { data } = await apiClient.get<NotificationUnreadCountResponse>('/api/notifications/unread-count')
  return data
}

export async function markNotificationRead(id: string): Promise<NotificationResponse> {
  const { data } = await apiClient.post<NotificationResponse>(`/api/notifications/${id}/read`)
  return data
}

export async function dismissNotification(id: string): Promise<NotificationResponse> {
  const { data } = await apiClient.post<NotificationResponse>(`/api/notifications/${id}/dismiss`)
  return data
}

export async function getNotificationGenerationTrace(): Promise<NotificationGenerationResponse> {
  const { data } = await apiClient.get<NotificationGenerationResponse>('/api/notifications/dev/generation')
  return data
}

export async function listAllNotificationsDev(): Promise<NotificationResponse[]> {
  const { data } = await apiClient.get<NotificationResponse[]>('/api/notifications/dev/all')
  return data
}
