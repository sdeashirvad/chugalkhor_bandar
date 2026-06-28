import { useEffect, useMemo } from 'react'
import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import {
  WELCOME_VARIANTS,
  WELCOME_WITH_ARTIFACTS,
  WELCOME_WITH_NOTIFICATIONS,
} from '@/config/world'
import { getCharacterAvatar } from '@/lib/avatars'
import { gentleFade } from '@/lib/motion'
import { useArtifacts } from '@/hooks/useArtifacts'
import { useUnreadNotificationCount } from '@/hooks/useNotifications'
import { useSession } from '@/hooks/useSession'

interface WelcomeOverlayProps {
  onComplete: () => void
}

function pickWelcomeLine(
  characterId: string,
  unreadCount: number,
  activeArtifactCount: number,
): string {
  const pool = [...(WELCOME_VARIANTS[characterId] ?? WELCOME_VARIANTS.default)]
  if (unreadCount > 0) {
    pool.push(...WELCOME_WITH_NOTIFICATIONS)
  }
  if (activeArtifactCount > 0) {
    pool.push(...WELCOME_WITH_ARTIFACTS)
  }
  const today = new Date().toISOString().slice(0, 10)
  const seed = `${characterId}-${today}-${unreadCount}-${activeArtifactCount}`
  let hash = 0
  for (let i = 0; i < seed.length; i++) {
    hash = (hash * 31 + seed.charCodeAt(i)) >>> 0
  }
  return pool[hash % pool.length]
}

export function WelcomeOverlay({ onComplete }: WelcomeOverlayProps) {
  const navigate = useNavigate()
  const { data: session } = useSession()
  const unread = useUnreadNotificationCount()
  const artifacts = useArtifacts()

  const activeCount = useMemo(
    () => artifacts.data?.filter((a) => a.status === 'ACTIVE' || a.status === 'NEW').length ?? 0,
    [artifacts.data],
  )

  const line = useMemo(() => {
    if (!session) return ''
    return pickWelcomeLine(
      session.currentCharacter.id,
      unread.data?.unreadCount ?? 0,
      activeCount,
    )
  }, [session, unread.data, activeCount])

  useEffect(() => {
    const timer = window.setTimeout(onComplete, 2800)
    return () => window.clearTimeout(timer)
  }, [onComplete])

  if (!session) return null

  return (
    <motion.div
      className="fixed inset-0 z-50 flex items-center justify-center bg-jungle-deep/90 px-6"
      {...gentleFade}
      role="dialog"
      aria-label="Welcome to the Jungle"
    >
      <button
        type="button"
        className="absolute right-4 top-4 text-sm text-jungle-parchment/70 hover:text-jungle-parchment"
        onClick={onComplete}
      >
        Skip
      </button>
      <div className="max-w-md text-center">
        <motion.img
          src={getCharacterAvatar(session.currentCharacter.id)}
          alt=""
          className="mx-auto h-24 w-24 rounded-full border-4 border-jungle-gold/40 object-cover"
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.5 }}
        />
        <motion.p
          className="mt-6 font-display text-2xl font-semibold text-jungle-parchment"
          initial={{ opacity: 0, y: 12 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.4 }}
        >
          {session.currentCharacter.displayName}
        </motion.p>
        <motion.p
          className="mt-4 text-lg text-jungle-parchment/90"
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.7, duration: 0.5 }}
        >
          {line}
        </motion.p>
        <button
          type="button"
          className="mt-8 text-sm text-jungle-gold underline-offset-2 hover:underline"
          onClick={() => {
            onComplete()
            if ((unread.data?.unreadCount ?? 0) > 0) {
              navigate('/notifications')
            }
          }}
        >
          Continue into the Jungle
        </button>
      </div>
    </motion.div>
  )
}
