import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  discardMemoryInboxItem,
  getMemoryInboxGenerationTrace,
  getMemoryInboxItem,
  listAllMemoryInboxDev,
  listMemoryInbox,
  reviewMemoryInboxItem,
} from '@/api/memoryInbox'

export function useMemoryInbox() {
  return useQuery({
    queryKey: ['memory-inbox'],
    queryFn: listMemoryInbox,
  })
}

export function useMemoryInboxItem(id: string | undefined) {
  return useQuery({
    queryKey: ['memory-inbox', id],
    queryFn: () => getMemoryInboxItem(id!),
    enabled: Boolean(id),
  })
}

export function useReviewMemoryInboxItem() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: reviewMemoryInboxItem,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['memory-inbox'] })
    },
  })
}

export function useDiscardMemoryInboxItem() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: discardMemoryInboxItem,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['memory-inbox'] })
    },
  })
}

export function useMemoryInboxGenerationTrace() {
  return useQuery({
    queryKey: ['memory-inbox', 'dev-generation'],
    queryFn: getMemoryInboxGenerationTrace,
    retry: false,
  })
}

export function useAllMemoryInboxDev() {
  return useQuery({
    queryKey: ['memory-inbox', 'dev-all'],
    queryFn: listAllMemoryInboxDev,
    retry: false,
  })
}
