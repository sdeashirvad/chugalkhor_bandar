import { motion } from 'framer-motion'
import { BANDAR_CHARACTER_ID } from '@/config/world'
import { getCharacterAvatar } from '@/lib/avatars'
import type { ConversationMessage } from '@/types/conversation'
import { cn } from '@/lib/utils'

interface MessageBubbleProps {
  message: ConversationMessage
  characterName: string
  characterId: string
  isGrouped?: boolean
}

export function MessageBubble({ message, characterName, characterId, isGrouped = false }: MessageBubbleProps) {
  const isUser = message.sender === 'USER'
  const isBandar = message.sender === 'BANDAR'
  const speakerName = isUser ? characterName : isBandar ? 'Bandar' : message.sender
  const avatar = isBandar ? getCharacterAvatar(BANDAR_CHARACTER_ID) : getCharacterAvatar(characterId)

  return (
    <motion.div
      className={cn('flex gap-2.5', isUser ? 'flex-row-reverse' : 'flex-row', isGrouped ? 'mt-1' : 'mt-4 first:mt-0')}
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.22, ease: 'easeOut' }}
    >
      {isGrouped ? (
        <div className="w-8 shrink-0" aria-hidden />
      ) : (
        <img
          src={avatar}
          alt=""
          className="mt-0.5 h-8 w-8 shrink-0 rounded-full border border-border/60 object-cover shadow-sm"
        />
      )}
      <div className={cn('max-w-[min(85%,28rem)]', isUser ? 'text-right' : 'text-left')}>
        {!isGrouped ? (
          <p className={cn('mb-1 text-[11px] font-medium tracking-wide text-muted-foreground', isUser && 'text-right')}>
            {speakerName}
          </p>
        ) : null}
        <div
          className={cn(
            'inline-block px-4 py-2.5 text-[15px] leading-relaxed shadow-sm',
            isUser && 'rounded-2xl rounded-tr-md bg-jungle-moss text-primary-foreground',
            isBandar && 'rounded-2xl rounded-tl-md border border-border/50 bg-card/95 text-foreground',
            !isUser && !isBandar && 'rounded-2xl bg-muted/80',
          )}
        >
          {message.content}
        </div>
      </div>
    </motion.div>
  )
}
