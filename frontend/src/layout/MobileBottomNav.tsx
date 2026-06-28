import { Archive, Bell, Compass, Globe, Home, MessageCircle, ScrollText, Users } from 'lucide-react'
import { useMemo, useState } from 'react'
import { NavLink, useLocation } from 'react-router-dom'
import { cn } from '@/lib/utils'

const primaryItems = [
  { to: '/home', label: 'Home', icon: Home },
  { to: '/chat', label: 'Bandar', icon: MessageCircle },
] as const

const exploreItems = [
  { to: '/characters', label: 'Residents', icon: Users },
  { to: '/notifications', label: 'Letters', icon: Bell },
  { to: '/living-world', label: 'Living World', icon: Globe },
  { to: '/chronicles', label: 'Chronicles', icon: ScrollText },
  { to: '/artifacts', label: 'Unfinished Matters', icon: Archive },
] as const

export function MobileBottomNav() {
  const [exploreOpen, setExploreOpen] = useState(false)
  const location = useLocation()

  const exploreActive = useMemo(
    () => exploreItems.some(({ to }) => location.pathname === to || location.pathname.startsWith(`${to}/`)),
    [location.pathname],
  )

  return (
    <>
      {exploreOpen ? (
        <button
          type="button"
          className="fixed inset-0 z-40 bg-black/25 lg:hidden"
          aria-label="Close explore menu"
          onClick={() => setExploreOpen(false)}
        />
      ) : null}
      <nav
        className={cn(
          'mobile-bottom-nav fixed inset-x-0 bottom-0 z-50 border-t border-border/80 bg-card/95 backdrop-blur-sm lg:hidden',
          location.pathname === '/chat' && 'hidden',
        )}
      >
        {exploreOpen ? (
          <div className="border-b border-border/60 px-2 py-2">
            <p className="px-2 pb-1 text-xs font-semibold uppercase tracking-wide text-muted-foreground">Explore</p>
            <ul className="grid gap-0.5">
              {exploreItems.map(({ to, label, icon: Icon }) => (
                <li key={to}>
                  <NavLink
                    to={to}
                    onClick={() => setExploreOpen(false)}
                    className={({ isActive }) =>
                      cn(
                        'flex min-h-11 items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium',
                        isActive ? 'bg-jungle-moss/15 text-jungle-moss' : 'text-foreground hover:bg-accent',
                      )
                    }
                  >
                    <Icon className="h-5 w-5 shrink-0" aria-hidden />
                    {label}
                  </NavLink>
                </li>
              ))}
            </ul>
          </div>
        ) : null}
        <div className="flex items-stretch">
          {primaryItems.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              end={to === '/home'}
              className={({ isActive }) =>
                cn(
                  'flex min-h-[52px] min-w-0 flex-1 flex-col items-center justify-center gap-0.5 px-2 py-2 text-xs font-medium transition-colors',
                  isActive ? 'text-jungle-moss' : 'text-muted-foreground',
                )
              }
            >
              <Icon className="h-5 w-5 shrink-0" aria-hidden />
              <span className="truncate">{label}</span>
            </NavLink>
          ))}
          <button
            type="button"
            onClick={() => setExploreOpen((open) => !open)}
            className={cn(
              'flex min-h-[52px] min-w-0 flex-1 flex-col items-center justify-center gap-0.5 px-2 py-2 text-xs font-medium transition-colors',
              exploreOpen || exploreActive ? 'text-jungle-moss' : 'text-muted-foreground',
            )}
            aria-expanded={exploreOpen}
            aria-label="Explore the Jungle"
          >
            <Compass className="h-5 w-5 shrink-0" aria-hidden />
            <span>Explore</span>
          </button>
        </div>
      </nav>
    </>
  )
}
