import { Archive, Bell, Globe, Home, MessageCircle, ScrollText, Users } from 'lucide-react'
import { NavLink } from 'react-router-dom'
import { cn } from '@/lib/utils'

const navItems = [
  { to: '/home', label: 'Home', icon: Home },
  { to: '/chat', label: 'Bandar', icon: MessageCircle },
  { to: '/characters', label: 'Residents', icon: Users },
  { to: '/notifications', label: 'Letters', icon: Bell },
  { to: '/living-world', label: 'Living World', icon: Globe },
  { to: '/chronicles', label: 'Chronicles', icon: ScrollText },
  { to: '/artifacts', label: 'Unfinished Matters', icon: Archive },
]

export function Sidebar() {
  return (
    <aside className="hidden w-56 shrink-0 border-r border-border/80 bg-card/60 lg:block">
      <nav className="flex flex-col gap-1 p-3">
        {navItems.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            end={to === '/home'}
            className={({ isActive }) =>
              cn(
                'flex min-h-11 items-center gap-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors',
                isActive
                  ? 'bg-jungle-moss/15 text-jungle-moss'
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
  )
}
