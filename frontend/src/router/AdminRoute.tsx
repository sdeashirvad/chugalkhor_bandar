import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { readAdminAuth } from '@/api/adminAuth'

export function AdminRoute() {
  const location = useLocation()

  if (!readAdminAuth()) {
    return <Navigate to="/admin" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}
