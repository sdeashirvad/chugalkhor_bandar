import { apiClient } from '@/api/client'
import type { WorldStatus } from '@/types/api'

export async function fetchWorldStatus(): Promise<WorldStatus> {
  const { data } = await apiClient.get<WorldStatus>('/api/world/status')
  return data
}
