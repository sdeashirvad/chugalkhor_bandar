import { useQuery } from '@tanstack/react-query'
import { fetchStories, fetchStoryById } from '@/api/stories'

export function useStories() {
  return useQuery({
    queryKey: ['stories'],
    queryFn: fetchStories,
  })
}

export function useStory(id: string | undefined) {
  return useQuery({
    queryKey: ['stories', id],
    queryFn: () => fetchStoryById(id!),
    enabled: Boolean(id),
  })
}
