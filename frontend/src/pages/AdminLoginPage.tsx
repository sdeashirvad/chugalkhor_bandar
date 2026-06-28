import { useState } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { readAdminAuth, validateAdminCredentials, writeAdminAuth } from '@/api/adminAuth'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

export function AdminLoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const from = (location.state as { from?: string } | null)?.from ?? '/admin/overview'
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  if (readAdminAuth()) {
    return <Navigate to={from} replace />
  }

  function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    if (validateAdminCredentials(username, password)) {
      writeAdminAuth(true)
      navigate(from, { replace: true })
      return
    }
    setError('Invalid admin credentials.')
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-muted/40 p-4">
      <div className="w-full max-w-md rounded-xl border border-border bg-card p-8 shadow-sm">
        <h1 className="text-xl font-semibold">Admin Observability</h1>
        <p className="mt-2 text-sm text-muted-foreground">
          Internal area for system monitoring and developer tools. Not for players.
        </p>
        <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
          <div>
            <label className="mb-1 block text-sm font-medium" htmlFor="adminUsername">
              Username
            </label>
            <Input
              id="adminUsername"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium" htmlFor="adminPassword">
              Password
            </label>
            <Input
              id="adminPassword"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </div>
          {error ? <p className="text-sm text-red-700">{error}</p> : null}
          <Button type="submit" className="w-full">
            Enter Admin
          </Button>
        </form>
      </div>
    </div>
  )
}
