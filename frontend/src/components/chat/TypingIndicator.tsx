import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { getCharacterAvatar } from '@/lib/avatars'
import { BANDAR_CHARACTER_ID } from '@/config/world'

const THINKING_PHRASES = [
  'Bandar is remembering something…',
  'Bandar is choosing his words…',
  'Bandar is thinking…',
] as const

export function TypingIndicator() {
  const [phraseIndex, setPhraseIndex] = useState(0)

  useEffect(() => {
    const timer = window.setInterval(() => {
      setPhraseIndex((current) => (current + 1) % THINKING_PHRASES.length)
    }, 2800)
    return () => window.clearInterval(timer)
  }, [])

  return (
    <motion.div
      className="mt-6 flex gap-3 sm:mt-7"
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.24, ease: 'easeOut' }}
      aria-live="polite"
      aria-label={THINKING_PHRASES[phraseIndex]}
    >
      <img
        src={getCharacterAvatar(BANDAR_CHARACTER_ID)}
        alt=""
        className="mt-0.5 h-9 w-9 shrink-0 rounded-full border border-jungle-gold/25 object-cover opacity-90 sm:h-10 sm:w-10"
      />
      <div className="chat-bubble-bandar max-w-[min(100%,34ch)] px-5 py-4">
        <p className="font-display text-sm text-muted-foreground">{THINKING_PHRASES[phraseIndex]}</p>
        <div className="mt-3 flex items-center gap-1.5" aria-hidden>
          {[0, 1, 2].map((index) => (
            <motion.span
              key={index}
              className="h-1.5 w-1.5 rounded-full bg-jungle-moss/45"
              animate={{ opacity: [0.2, 0.75, 0.2] }}
              transition={{ duration: 1.6, repeat: Infinity, delay: index * 0.22, ease: 'easeInOut' }}
            />
          ))}
        </div>
      </div>
    </motion.div>
  )
}
