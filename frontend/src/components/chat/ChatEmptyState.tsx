import { getCharacterAvatar } from '@/lib/avatars'
import { BANDAR_CHARACTER_ID } from '@/config/world'

export function ChatEmptyState() {
  return (
    <div className="flex flex-col items-center justify-center px-6 py-16 text-center">
      <img
        src={getCharacterAvatar(BANDAR_CHARACTER_ID)}
        alt=""
        className="h-16 w-16 rounded-full border-2 border-jungle-gold/25 object-cover shadow-md"
      />
      <p className="mt-5 font-display text-lg font-medium text-foreground">Bandar is listening</p>
      <p className="mt-2 max-w-xs text-sm leading-relaxed text-muted-foreground">
        The Jungle is quiet. Say hello — a story may begin.
      </p>
    </div>
  )
}
