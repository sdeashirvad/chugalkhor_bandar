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
    <header className="chat-presence shrink-0 border-b border-border/40 bg-card/70 px-4 py-3 backdrop-blur-md">
      <div className="mx-auto flex max-w-3xl items-center gap-3">
        <div className="relative flex shrink-0 items-center">
          <img
            src={getCharacterAvatar(BANDAR_CHARACTER_ID)}
            alt=""
            className="h-9 w-9 rounded-full border border-border/80 object-cover shadow-sm"
          />
          <img
            src={getCharacterAvatar(characterId)}
            alt=""
            className={cn(
              'absolute -bottom-0.5 -right-2 h-6 w-6 rounded-full border-2 border-card object-cover shadow-sm',
            )}
          />
        </div>
        <div className="min-w-0 flex-1">
          <p className="font-display text-sm font-semibold leading-tight text-foreground">Bandar</p>
          <p className="truncate text-xs text-muted-foreground">{subtitle}</p>
        </div>
        {pendingHint ? (
          <p className="hidden max-w-[9rem] truncate rounded-full bg-jungle-gold/10 px-2.5 py-1 text-[11px] text-jungle-bark sm:block" title={pendingHint}>
            {pendingHint}
          </p>
        ) : null}
      </div>
      {pendingHint ? (
        <p className="mx-auto mt-2 max-w-3xl truncate text-center text-[11px] text-jungle-bark/90 sm:hidden">{pendingHint}</p>
      ) : null}
    </header>
  )
}
