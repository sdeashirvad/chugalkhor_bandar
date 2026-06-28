import { Link } from 'react-router-dom'
import { ChevronLeft } from 'lucide-react'
import { getCharacterAvatar } from '@/lib/avatars'
import { BANDAR_CHARACTER_ID } from '@/config/world'
import { cn } from '@/lib/utils'

interface ChatHeaderProps {
  characterName: string
  characterId: string
  activeTopic?: string | null
  pendingHint?: string | null
}

export function ChatHeader({
  characterName,
  characterId,
  activeTopic,
  pendingHint,
}: ChatHeaderProps) {
  const subtitle = activeTopic ? `${characterName} · ${activeTopic}` : `with ${characterName}`

  return (
    <header className="chat-presence shrink-0 border-b border-border/40 bg-card/80 px-3 py-2 backdrop-blur-md pt-[max(0.5rem,env(safe-area-inset-top))] lg:px-4 lg:py-3 lg:pt-3">
      <div className="mx-auto flex max-w-3xl items-center gap-2 lg:gap-3">
        <Link
          to="/home"
          className="inline-flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-muted-foreground hover:bg-accent lg:hidden"
          aria-label="Back to Home"
        >
          <ChevronLeft className="h-5 w-5" aria-hidden />
        </Link>
        <div className="relative flex shrink-0 items-center">
          <img
            src={getCharacterAvatar(BANDAR_CHARACTER_ID)}
            alt=""
            className="h-8 w-8 rounded-full border border-border/80 object-cover shadow-sm lg:h-9 lg:w-9"
          />
          <img
            src={getCharacterAvatar(characterId)}
            alt=""
            className={cn(
              'absolute -bottom-0.5 -right-1.5 h-5 w-5 rounded-full border-2 border-card object-cover shadow-sm lg:-right-2 lg:h-6 lg:w-6',
            )}
          />
        </div>
        <div className="min-w-0 flex-1">
          <p className="font-display text-sm font-semibold leading-tight text-foreground">Bandar</p>
          <p className="truncate text-xs text-muted-foreground">{subtitle}</p>
        </div>
        {pendingHint ? (
          <p
            className="hidden max-w-[9rem] truncate rounded-full bg-jungle-gold/10 px-2.5 py-1 text-[11px] text-jungle-bark md:block"
            title={pendingHint}
          >
            {pendingHint}
          </p>
        ) : null}
      </div>
    </header>
  )
}
