import { useCallback, useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import { AnimatePresence, motion } from 'framer-motion'
import { Header } from '@/layout/Header'
import { Sidebar } from '@/layout/Sidebar'
import { MobileBottomNav } from '@/layout/MobileBottomNav'
import { ContextPanel } from '@/layout/ContextPanel'
import { WelcomeOverlay } from '@/components/world/WelcomeOverlay'
import { fadeSlide } from '@/lib/motion'
import { cn } from '@/lib/utils'

export function AppShell() {
  const location = useLocation()
  const isChat = location.pathname === '/chat'
  const [showWelcome, setShowWelcome] = useState(
    () => (location.state as { showWelcome?: boolean } | null)?.showWelcome ?? false,
  )

  const dismissWelcome = useCallback(() => setShowWelcome(false), [])

  return (
    <div className="jungle-shell-bg flex min-h-[100dvh] flex-col">
      <Header />
      <div className="flex min-h-0 flex-1 flex-col lg:flex-row">
        <Sidebar />
        <main
          className={cn(
            'flex-1 overflow-x-hidden lg:p-6 lg:pb-6',
            isChat
              ? 'p-0 pb-[calc(4.5rem+env(safe-area-inset-bottom))]'
              : 'p-3 pb-[calc(4.5rem+env(safe-area-inset-bottom))]',
          )}
        >
          <AnimatePresence mode="wait">
            <motion.div key={location.pathname} {...fadeSlide}>
              <Outlet />
            </motion.div>
          </AnimatePresence>
        </main>
        <ContextPanel />
      </div>
      <MobileBottomNav />
      {showWelcome ? <WelcomeOverlay onComplete={dismissWelcome} /> : null}
    </div>
  )
}
