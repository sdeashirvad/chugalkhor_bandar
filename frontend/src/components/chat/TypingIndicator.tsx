import { motion } from 'framer-motion'
import { getCharacterAvatar } from '@/lib/avatars'
import { BANDAR_CHARACTER_ID } from '@/config/world'

export function TypingIndicator() {
  return (
    <div className="mt-4 flex gap-2.5">
      <img
        src={getCharacterAvatar(BANDAR_CHARACTER_ID)}
        alt=""
        className="mt-0.5 h-8 w-8 shrink-0 rounded-full border border-border/60 object-cover opacity-80"
      />
      <div className="flex items-center gap-1.5 rounded-2xl rounded-tl-md border border-border/40 bg-card/80 px-4 py-3 shadow-sm">
        {[0, 1, 2].map((i) => (
          <motion.span
            key={i}
            className="h-1.5 w-1.5 rounded-full bg-jungle-moss/50"
            animate={{ opacity: [0.25, 0.9, 0.25], scale: [0.92, 1, 0.92] }}
            transition={{ duration: 1.4, repeat: Infinity, delay: i * 0.18, ease: 'easeInOut' }}
          />
        ))}
      </div>
    </div>
  )
}
