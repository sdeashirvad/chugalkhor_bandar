import { Link, useNavigate } from 'react-router-dom'
import { Bell, LogOut, Search, X } from 'lucide-react'
import { motion, AnimatePresence } from 'framer-motion'
import { ConfirmDialog } from '@/components/ConfirmDialog'
import { GlobalSearch } from '@/components/GlobalSearch'
import { Button } from '@/components/ui/button'
import { getCharacterAvatar } from '@/lib/avatars'
import { useLogout, useSession } from '@/hooks/useSession'
import { useUnreadNotificationCount } from '@/hooks/useNotifications'
import { useState } from 'react'

export function Header() {
  const navigate = useNavigate()
  const { data: session } = useSession()
  const unread = useUnreadNotificationCount()
  const logout = useLogout()
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false)
  const [searchOpen, setSearchOpen] = useState(false)

  function handleLogoutConfirm() {
    logout.mutate(undefined, {
      onSuccess: () => {
        setLogoutDialogOpen(false)
        navigate('/login', { replace: true })
      },
    })
  }

  return (
    <>
      <header className="flex h-14 shrink-0 items-center gap-2 border-b border-border/80 bg-card/90 px-3 backdrop-blur-sm sm:gap-4 sm:px-4 lg:px-6">
        <Link to="/home" className="flex shrink-0 items-center gap-2">
          <img src="/assets/icons/jungle-leaf.svg" alt="" className="h-6 w-6" />
          <div className="hidden min-[380px]:block">
            <p className="font-display text-sm font-semibold leading-none">Chugalkhor Bandar</p>
            <p className="text-xs text-muted-foreground">The Jungle</p>
          </div>
        </Link>

        <div className="hidden flex-1 lg:block">
          <GlobalSearch />
        </div>

        {session ? (
          <div className="ml-auto flex shrink-0 items-center gap-2 text-sm sm:gap-3">
            <button
              type="button"
              className="inline-flex h-11 w-11 items-center justify-center rounded-full border border-border hover:bg-accent lg:hidden"
              aria-label="Search the Jungle"
              onClick={() => setSearchOpen(true)}
            >
              <Search className="h-4 w-4" aria-hidden />
            </button>
            <div className="hidden items-center gap-2 sm:flex">
              <img
                src={getCharacterAvatar(session.currentCharacter.id)}
                alt=""
                className="h-8 w-8 rounded-full border border-jungle-gold/30 object-cover"
              />
              <div className="hidden text-right md:block">
                <p className="font-medium leading-none">{session.currentCharacter.displayName}</p>
                <p className="text-xs text-muted-foreground">{session.currentCharacter.species || 'Resident'}</p>
              </div>
            </div>
            <Link
              to="/notifications"
              className="relative inline-flex h-11 w-11 items-center justify-center rounded-full border border-border hover:bg-accent lg:hidden"
              aria-label="Letters"
            >
              <Bell className="h-4 w-4" aria-hidden />
              {unread.data && unread.data.unreadCount > 0 ? (
                <motion.span
                  className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-jungle-gold px-1 text-[10px] font-semibold text-jungle-deep"
                  animate={{ scale: [1, 1.08, 1] }}
                  transition={{ duration: 2, repeat: Infinity }}
                >
                  {unread.data.unreadCount}
                </motion.span>
              ) : null}
            </Link>
            <Link
              to="/notifications"
              className="relative hidden h-9 w-9 items-center justify-center rounded-full border border-border hover:bg-accent lg:inline-flex"
              aria-label="Letters"
            >
              <Bell className="h-4 w-4" aria-hidden />
              {unread.data && unread.data.unreadCount > 0 ? (
                <motion.span
                  className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-jungle-gold px-1 text-[10px] font-semibold text-jungle-deep"
                  animate={{ scale: [1, 1.08, 1] }}
                  transition={{ duration: 2, repeat: Infinity }}
                >
                  {unread.data.unreadCount}
                </motion.span>
              ) : null}
            </Link>
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => setLogoutDialogOpen(true)}
              className="h-11 gap-1 px-2 sm:px-3"
              aria-label="Leave the Jungle"
            >
              <LogOut className="h-4 w-4" aria-hidden />
              <span className="hidden sm:inline">Leave</span>
            </Button>
          </div>
        ) : null}
      </header>

      <AnimatePresence>
        {searchOpen ? (
          <motion.div
            initial={{ opacity: 0, y: -8 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -8 }}
            className="fixed inset-x-0 top-0 z-50 border-b border-border bg-card p-3 shadow-lg lg:hidden"
          >
            <div className="flex items-center gap-2">
              <div className="flex-1">
                <GlobalSearch autoFocus onNavigate={() => setSearchOpen(false)} />
              </div>
              <button
                type="button"
                className="inline-flex h-11 w-11 shrink-0 items-center justify-center rounded-full border border-border hover:bg-accent"
                aria-label="Close search"
                onClick={() => setSearchOpen(false)}
              >
                <X className="h-4 w-4" aria-hidden />
              </button>
            </div>
          </motion.div>
        ) : null}
      </AnimatePresence>

      <ConfirmDialog
        open={logoutDialogOpen}
        title="Leave the Jungle?"
        message="Your session will end and you'll return to the gate."
        confirmLabel="Leave"
        cancelLabel="Stay"
        loading={logout.isPending}
        onConfirm={handleLogoutConfirm}
        onCancel={() => setLogoutDialogOpen(false)}
      />
    </>
  )
}
