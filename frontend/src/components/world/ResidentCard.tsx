import { ACTIVITY_HINTS } from '@/config/world'
import { getCharacterAvatar } from '@/lib/avatars'
import { cn } from '@/lib/utils'
import { LastSeenBadge } from '@/components/world/LastSeenBadge'

export interface ResidentCardProps {
  id: string
  name: string
  titles: string[]
  currentPlaceName: string | null
  lastSeenAt: string | null
  featured?: boolean
  isCurrentUser?: boolean
  activityHint?: string | null
  lastTopic?: string | null
  onClick?: () => void
}

export function ResidentCard({
  id,
  name,
  titles,
  currentPlaceName,
  lastSeenAt,
  featured = false,
  isCurrentUser = false,
  activityHint,
  lastTopic,
  onClick,
}: ResidentCardProps) {
  const hint = activityHint ?? ACTIVITY_HINTS[id] ?? (currentPlaceName ? `At ${currentPlaceName}` : 'In the Jungle')
  const Wrapper = onClick ? 'button' : 'article'

  return (
    <Wrapper
      type={onClick ? 'button' : undefined}
      onClick={onClick}
      className={cn(
        'world-card paper-texture w-full text-left transition-transform hover:scale-[1.01]',
        featured ? 'p-6' : 'p-4',
        onClick && 'cursor-pointer',
      )}
    >
      <div className="flex items-start gap-4">
        <img
          src={getCharacterAvatar(id)}
          alt=""
          className={cn('rounded-full border-2 border-jungle-gold/30 object-cover', featured ? 'h-16 w-16' : 'h-12 w-12')}
        />
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className={cn('font-display font-semibold', featured ? 'text-xl lg:text-lg' : 'text-lg lg:text-base')}>{name}</h3>
            {isCurrentUser ? (
              <span className="rounded-full bg-jungle-moss/15 px-2 py-0.5 text-xs font-medium text-jungle-moss">You</span>
            ) : null}
          </div>
          {titles.length > 0 ? (
            <p className="mt-0.5 text-base text-muted-foreground lg:text-sm">{titles.join(' · ')}</p>
          ) : null}
          <p className="mt-2 text-base font-medium text-jungle-bark lg:text-sm">{name} — {hint}</p>
          {currentPlaceName ? (
            <p className="mt-1 text-sm text-muted-foreground">{currentPlaceName}</p>
          ) : null}
          {featured ? (
            <div className="mt-3">
              <LastSeenBadge lastSeenAt={lastSeenAt} lastTopic={isCurrentUser ? lastTopic : null} />
            </div>
          ) : null}
        </div>
      </div>
    </Wrapper>
  )
}
