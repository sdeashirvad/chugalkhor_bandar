import { NavLink, Outlet } from 'react-router-dom'
import {
  Activity,
  Archive,
  BookOpen,
  Building2,
  FlaskConical,
  Globe,
  Inbox,
  LogOut,
  Mail,
  Map,
  MapPin,
  Moon,
  ScrollText,
} from 'lucide-react'
import { Link, useNavigate } from 'react-router-dom'
import { writeAdminAuth } from '@/api/adminAuth'
import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'

const adminNavItems = [
  { to: '/admin/overview', label: 'System Overview', icon: Activity },
  { to: '/admin/developer', label: 'Developer Panels', icon: FlaskConical },
  { to: '/admin/memory/inbox', label: 'Memory Inbox', icon: Inbox },
  { to: '/admin/memory/consolidation', label: 'Consolidation', icon: Moon },
  { to: '/admin/reporting', label: 'Delivery History', icon: Mail },
  { to: '/admin/living-world', label: 'Living World Tick', icon: Globe },
  { to: '/admin/chronicles', label: 'Chronicle Writer', icon: ScrollText },
  { to: '/admin/artifacts', label: 'Artifacts Trace', icon: Archive },
  { to: '/admin/explore/stories', label: 'Stories', icon: BookOpen },
  { to: '/admin/explore/territories', label: 'Territories', icon: Map },
  { to: '/admin/explore/places', label: 'Places', icon: MapPin },
  { to: '/admin/explore/organizations', label: 'Organizations', icon: Building2 },
]

export function AdminShell() {
  const navigate = useNavigate()

  function handleLogout() {
    writeAdminAuth(false)
    navigate('/admin', { replace: true })
  }

  return (
    <div className="flex min-h-screen flex-col bg-muted/30">
      <header className="flex h-14 items-center justify-between border-b border-border bg-card px-4 lg:px-6">
        <div>
          <p className="text-sm font-semibold">Admin Observability</p>
          <p className="text-xs text-muted-foreground">Internal Jungle systems</p>
        </div>
        <div className="flex items-center gap-2">
          <Link to="/home" className="text-xs text-muted-foreground hover:text-foreground">
            Exit to Jungle
          </Link>
          <Button type="button" variant="outline" size="sm" onClick={handleLogout} className="gap-1">
            <LogOut className="h-3.5 w-3.5" aria-hidden />
            Sign out
          </Button>
        </div>
      </header>
      <div className="flex flex-1 flex-col lg:flex-row">
        <aside className="w-full border-b border-border bg-card lg:w-56 lg:border-b-0 lg:border-r">
          <nav className="flex gap-1 overflow-x-auto p-2 lg:flex-col lg:overflow-visible lg:p-3">
            {adminNavItems.map(({ to, label, icon: Icon }) => (
              <NavLink
                key={to}
                to={to}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-2 rounded-md px-3 py-2 text-sm font-medium whitespace-nowrap transition-colors',
                    isActive
                      ? 'bg-accent text-foreground'
                      : 'text-muted-foreground hover:bg-accent hover:text-foreground',
                  )
                }
              >
                <Icon className="h-4 w-4" aria-hidden />
                {label}
              </NavLink>
            ))}
          </nav>
        </aside>
        <main className="flex-1 p-4 lg:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
