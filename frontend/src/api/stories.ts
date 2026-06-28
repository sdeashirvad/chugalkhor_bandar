import { apiClient } from '@/api/client'
import type { StoryDetails, StorySummary } from '@/types/api'

export async function fetchStories(): Promise<StorySummary[]> {
  const { data } = await apiClient.get<StorySummary[]>('/api/stories')
  return data
}

export async function fetchStoryById(id: string): Promise<StoryDetails> {
  const { data } = await apiClient.get<StoryDetails>(`/api/stories/${id}`)
  return data
}
