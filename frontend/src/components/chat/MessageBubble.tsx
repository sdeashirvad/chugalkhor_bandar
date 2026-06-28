import { motion } from 'framer-motion'
import { ChatMessageContent } from '@/components/chat/ChatMessageContent'
import { BANDAR_CHARACTER_ID } from '@/config/world'
import { formatMessageTime } from '@/lib/chat/formatMessageTime'
import { getCharacterAvatar } from '@/lib/avatars'
import type { ConversationMessage } from '@/types/conversation'
import { cn } from '@/lib/utils'

interface MessageBubbleProps {
  message: ConversationMessage
  characterName: string
  characterId: string
  characterTitle?: string | null
  isGrouped?: boolean
  isSpeakerChange?: boolean
}

export function MessageBubble({
  message,
  characterName,
  characterId,
  characterTitle,
  isGrouped = false,
  isSpeakerChange = false,
}: MessageBubbleProps) {
  const isUser = message.sender === 'USER'
  const isBandar = message.sender === 'BANDAR'
  const avatar = isBandar ? getCharacterAvatar(BANDAR_CHARACTER_ID) : getCharacterAvatar(characterId)
  const timestamp = formatMessageTime(message.timestamp)

  return (
    <motion.article
      className={cn(
        'group/message flex gap-3',
        isUser ? 'flex-row-reverse' : 'flex-row',
        isSpeakerChange ? 'mt-7 sm:mt-8' : isGrouped ? 'mt-2' : 'mt-5 sm:mt-6',
        'first:mt-0',
      )}
      initial={{ opacity: 0, y: 10, scale: 0.985 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ duration: 0.28, ease: [0.22, 1, 0.36, 1] }}
      aria-label={`${isBandar ? 'Bandar' : characterName} message`}
    >
      {isGrouped ? (
        <div className="w-9 shrink-0 sm:w-10" aria-hidden />
      ) : (
        <div className="relative shrink-0">
          <img
            src={avatar}
            alt=""
            className={cn(
              'mt-0.5 rounded-full object-cover shadow-sm',
              isBandar
                ? 'h-9 w-9 border border-jungle-gold/25 sm:h-10 sm:w-10'
                : 'h-9 w-9 border border-jungle-moss/25 sm:h-10 sm:w-10',
            )}
          />
          {isBandar ? (
            <span
              className="absolute bottom-0 right-0 h-2.5 w-2.5 rounded-full border-2 border-card bg-jungle-moss"
              aria-hidden
            />
          ) : null}
        </div>
      )}

      <div
        className={cn(
          'min-w-0',
          isUser ? 'max-w-[min(88%,20rem)] text-right' : 'max-w-[min(100%,38ch)] text-left',
        )}
      >
        {!isGrouped ? (
          <header
            className={cn(
              'mb-2 flex items-center gap-2',
              isUser ? 'justify-end' : 'justify-start',
            )}
          >
            <div className={cn('min-w-0', isUser && 'text-right')}>
              <p className="font-display text-sm font-semibold leading-tight text-foreground">
                {isBandar ? 'Bandar' : characterName}
              </p>
              <p className="text-[11px] leading-snug tracking-wide text-muted-foreground">
                {isBandar ? 'Storyteller' : characterTitle || 'Jungle resident'}
              </p>
            </div>
          </header>
        ) : null}

        <div
          className={cn(
            'relative text-left',
            isUser && 'chat-bubble-user ml-auto inline-block',
            isBandar && 'chat-bubble-bandar block',
            !isUser && !isBandar && 'chat-bubble-other inline-block',
          )}
        >
          <ChatMessageContent
            content={message.content}
            variant={isUser ? 'user' : isBandar ? 'bandar' : 'other'}
          />
          {timestamp ? (
            <time
              dateTime={message.timestamp}
              className={cn(
                'pointer-events-none absolute -bottom-5 text-[10px] text-muted-foreground/70 opacity-0 transition-opacity duration-200',
                'group-hover/message:pointer-events-auto group-hover/message:opacity-100 group-focus-within/message:opacity-100',
                isUser ? 'right-1' : 'left-1',
              )}
            >
              {timestamp}
            </time>
          ) : null}
        </div>
      </div>
    </motion.article>
  )
}
