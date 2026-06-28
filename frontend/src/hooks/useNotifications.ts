import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  dismissNotification,
  getNotificationGenerationTrace,
  getUnreadNotificationCount,
  listAllNotificationsDev,
  listNotifications,
  markNotificationRead,
} from '@/api/notifications'

export function useNotifications() {
  return useQuery({
    queryKey: ['notifications'],
    queryFn: listNotifications,
  })
}

export function useUnreadNotificationCount() {
  return useQuery({
    queryKey: ['notifications', 'unread-count'],
    queryFn: getUnreadNotificationCount,
    refetchInterval: 30000,
  })
}

export function useMarkNotificationRead() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: markNotificationRead,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
  })
}

export function useDismissNotification() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: dismissNotification,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] })
    },
  })
}

export function useNotificationGenerationTrace() {
  return useQuery({
    queryKey: ['notifications', 'dev-generation'],
    queryFn: getNotificationGenerationTrace,
    retry: false,
  })
}

export function useAllNotificationsDev() {
  return useQuery({
    queryKey: ['notifications', 'dev-all'],
    queryFn: listAllNotificationsDev,
    retry: false,
  })
}
