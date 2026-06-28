import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { LoadingSpinner } from '@/components/LoadingSpinner'
import { useSession } from '@/hooks/useSession'
import { readSessionId } from '@/api/session'

export function ProtectedRoute() {
  const location = useLocation()
  const sessionId = readSessionId()
  const { data, isLoading, isError } = useSession()

  if (!sessionId) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <LoadingSpinner label="Checking session…" />
      </div>
    )
  }

  if (isError || !data) {
    return <Navigate to="/login" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}
