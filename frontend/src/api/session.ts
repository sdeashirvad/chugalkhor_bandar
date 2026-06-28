import { apiClient } from '@/api/client'
import { SESSION_STORAGE_KEY } from '@/types/session'
import type { LoginRequest, SessionResponse } from '@/types/session'

export function persistSessionId(sessionId: string) {
  sessionStorage.setItem(SESSION_STORAGE_KEY, sessionId)
}

export function clearSessionId() {
  sessionStorage.removeItem(SESSION_STORAGE_KEY)
}

export function readSessionId(): string | null {
  return sessionStorage.getItem(SESSION_STORAGE_KEY)
}

export async function login(request: LoginRequest): Promise<SessionResponse> {
  const { data } = await apiClient.post<SessionResponse>('/api/session/login', request)
  persistSessionId(data.sessionId)
  return data
}

export async function logout(): Promise<void> {
  await apiClient.post('/api/session/logout')
  clearSessionId()
}

export async function fetchCurrentSession(): Promise<SessionResponse> {
  const { data } = await apiClient.get<SessionResponse>('/api/session/current')
  persistSessionId(data.sessionId)
  return data
}
