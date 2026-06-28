import { getCharacterAvatar } from '@/lib/avatars'
import { BANDAR_CHARACTER_ID } from '@/config/world'

export function ChatEmptyState() {
  return (
    <div className="flex flex-col items-center justify-center px-5 py-14 text-center sm:px-8 sm:py-20">
      <img
        src={getCharacterAvatar(BANDAR_CHARACTER_ID)}
        alt=""
        className="h-20 w-20 rounded-full border-2 border-jungle-gold/30 object-cover shadow-md sm:h-24 sm:w-24"
      />
      <p className="mt-6 max-w-sm font-display text-xl font-medium leading-snug text-foreground sm:text-2xl">
        Bandar is quietly waiting beneath the old tree.
      </p>
      <p className="mt-4 max-w-xs text-base leading-[1.75] text-muted-foreground">
        Whenever you&apos;re ready…
        <br />
        begin the conversation.
      </p>
    </div>
  )
}
