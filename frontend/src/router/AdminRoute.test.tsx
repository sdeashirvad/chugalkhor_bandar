import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { beforeEach, describe, expect, it } from 'vitest'
import { AdminRoute } from '@/router/AdminRoute'
import { writeAdminAuth } from '@/api/adminAuth'

describe('AdminRoute', () => {
  beforeEach(() => {
    writeAdminAuth(false)
  })

  it('redirects unauthenticated users to admin login', () => {
    writeAdminAuth(false)
    const queryClient = new QueryClient()
    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={['/admin/overview']}>
          <Routes>
            <Route path="/admin" element={<div>Login</div>} />
            <Route path="/admin/*" element={<AdminRoute />}>
              <Route path="overview" element={<div>Overview</div>} />
            </Route>
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    )
    expect(writeAdminAuth(false)).toBeUndefined()
  })

  it('renders child route when authenticated', () => {
    writeAdminAuth(true)
    const queryClient = new QueryClient()
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter initialEntries={['/overview']}>
          <Routes>
            <Route element={<AdminRoute />}>
              <Route path="overview" element={<div>Overview</div>} />
            </Route>
          </Routes>
        </MemoryRouter>
      </QueryClientProvider>,
    )
    expect(getByText('Overview')).toBeInTheDocument()
  })
})
