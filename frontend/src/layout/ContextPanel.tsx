import { Link } from 'react-router-dom'
import { Bell } from 'lucide-react'
import { getCharacterAvatar } from '@/lib/avatars'
import { useSession } from '@/hooks/useSession'
import { useUnreadNotificationCount } from '@/hooks/useNotifications'
import { useCharacters } from '@/hooks/useCharacters'

export function ContextPanel() {
  const { data: session } = useSession()
  const unread = useUnreadNotificationCount()
  const characters = useCharacters()

  if (!session) return null

  const character = characters.data?.find((c) => c.id === session.currentCharacter.id)

  return (
    <aside className="hidden w-64 shrink-0 border-l border-border/80 bg-card/40 p-4 xl:block">
      <div className="world-card p-4">
        <img
          src={getCharacterAvatar(session.currentCharacter.id)}
          alt=""
          className="mx-auto h-20 w-20 rounded-full border-2 border-jungle-gold/30 object-cover"
        />
        <p className="mt-3 text-center font-display text-lg font-semibold">{session.currentCharacter.displayName}</p>
        {character?.currentPlaceName ? (
          <p className="mt-1 text-center text-xs text-muted-foreground">{character.currentPlaceName}</p>
        ) : null}
        {(unread.data?.unreadCount ?? 0) > 0 ? (
          <Link
            to="/notifications"
            className="mt-4 flex items-center justify-center gap-2 rounded-lg bg-jungle-gold/10 px-3 py-2 text-sm text-jungle-bark"
          >
            <Bell className="h-4 w-4" aria-hidden />
            {unread.data!.unreadCount} unread
          </Link>
        ) : null}
      </div>
    </aside>
  )
}
