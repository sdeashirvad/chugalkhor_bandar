import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { fetchCurrentSession, login, logout, readSessionId } from '@/api/session'
import type { LoginRequest } from '@/types/session'

export function useSession() {
  const sessionId = readSessionId()
  return useQuery({
    queryKey: ['session', 'current'],
    queryFn: fetchCurrentSession,
    enabled: Boolean(sessionId),
    retry: false,
    staleTime: 60_000,
  })
}

export function useLogin() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (request: LoginRequest) => login(request),
    onSuccess: (session) => {
      queryClient.setQueryData(['session', 'current'], session)
    },
  })
}

export function useLogout() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: logout,
    onSuccess: () => {
      queryClient.setQueryData(['session', 'current'], null)
      queryClient.invalidateQueries({ queryKey: ['session'] })
    },
  })
}
