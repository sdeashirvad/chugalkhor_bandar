import axios, { type AxiosError } from 'axios'
import type { ApiError } from '@/types/api'
import { SESSION_STORAGE_KEY } from '@/types/session'

const baseURL = import.meta.env.VITE_API_BASE_URL ?? ''

export const apiClient = axios.create({
  baseURL,
  headers: {
    Accept: 'application/json',
  },
  withCredentials: true,
})

apiClient.interceptors.request.use((config) => {
  const sessionId = sessionStorage.getItem(SESSION_STORAGE_KEY)
  if (sessionId) {
    config.headers.set('X-Chugalkhor-Session', sessionId)
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiError>) => {
    if (error.response?.status === 401 && !error.config?.url?.includes('/api/session/login')) {
      sessionStorage.removeItem(SESSION_STORAGE_KEY)
      if (typeof window !== 'undefined' && !window.location.pathname.startsWith('/login')) {
        window.location.assign('/login?expired=1')
      }
    }
    return Promise.reject(error)
  },
)

export function getApiErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiError>(error)) {
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    if (error.response?.status === 401) {
      return 'Please log in to continue.'
    }
    if (error.response?.status === 404) {
      return 'The requested resource was not found.'
    }
    if (error.code === 'ERR_NETWORK') {
      return 'Unable to reach the Jungle API. Is the backend running?'
    }
    if (error.message) {
      return error.message
    }
  }
  if (error instanceof Error) {
    return error.message
  }
  return 'Something went wrong. Please try again.'
}
