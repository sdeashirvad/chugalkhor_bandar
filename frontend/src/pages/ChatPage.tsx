import { useEffect, useRef, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { ErrorState } from '@/components/ErrorState'
import { LoadingSpinner } from '@/components/LoadingSpinner'
import { ChatComposer } from '@/components/chat/ChatComposer'
import { ChatEmptyState } from '@/components/chat/ChatEmptyState'
import { ChatHeader } from '@/components/chat/ChatHeader'
import { MessageBubble } from '@/components/chat/MessageBubble'
import { TypingIndicator } from '@/components/chat/TypingIndicator'
import { Skeleton } from '@/components/ui/skeleton'
import {
  useConversation,
  useConversationMessages,
  useSendMessage,
  useStartConversation,
} from '@/hooks/useConversation'
import { useSession } from '@/hooks/useSession'
import { useWorkingMemory } from '@/hooks/useWorkingMemory'
import { useUnreadNotificationCount } from '@/hooks/useNotifications'
import { useArtifacts } from '@/hooks/useArtifacts'

export function ChatPage() {
  const location = useLocation()
  const invitation = (location.state as { notificationInvitation?: string } | null)?.notificationInvitation
  const bottomRef = useRef<HTMLDivElement>(null)
  const [draft, setDraft] = useState('')
  const [initialized, setInitialized] = useState(false)
  const { data: session } = useSession()
  const conversation = useConversation()
  const startConversation = useStartConversation()
  const sendMessage = useSendMessage()
  const messagesQuery = useConversationMessages(
    conversation.isSuccess || initialized,
    sendMessage.isPending ? 800 : false,
  )
  const workingMemory = useWorkingMemory()
  const unread = useUnreadNotificationCount()
  const artifacts = useArtifacts()

  const activeArtifactCount = artifacts.data?.filter((a) => a.status === 'ACTIVE' || a.status === 'NEW').length ?? 0

  useEffect(() => {
    if (initialized || conversation.isLoading) return
    if (conversation.isSuccess) {
      setInitialized(true)
      return
    }
    if (conversation.isError && !startConversation.isPending) {
      startConversation.mutate(undefined, { onSuccess: () => setInitialized(true) })
    }
  }, [conversation, initialized, startConversation])

  useEffect(() => {
    bottomRef.current?.scrollIntoView?.({ behavior: 'smooth' })
  }, [messagesQuery.data?.messages.length, sendMessage.isPending])

  const isBootstrapping =
    conversation.isLoading || startConversation.isPending || (conversation.isError && startConversation.isPending)
  const isLoadingMessages = messagesQuery.isLoading && !messagesQuery.data

  if (isBootstrapping) {
    return (
      <div className="flex h-[calc(100dvh-3.5rem-4.5rem-env(safe-area-inset-bottom))] items-center justify-center lg:h-[calc(100dvh-3.5rem-1.5rem)]">
        <LoadingSpinner label="Bandar is preparing to speak…" />
      </div>
    )
  }

  if (startConversation.isError) {
    return <ErrorState error={startConversation.error} title="Bandar could not find that trail" />
  }

  if (messagesQuery.isError) {
    return <ErrorState error={messagesQuery.error} title="The conversation path was blocked" />
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    const content = draft.trim()
    if (!content || sendMessage.isPending) return
    setDraft('')
    sendMessage.mutate({ content })
  }

  const messages = messagesQuery.data?.messages ?? []
  const characterId = session?.currentCharacter.id ?? ''
  const characterName = session?.currentCharacter.displayName ?? 'You'

  const pendingHints: string[] = []
  if ((unread.data?.unreadCount ?? 0) > 0) pendingHints.push('Letters waiting')
  if (activeArtifactCount > 0) {
    pendingHints.push(`${activeArtifactCount} unfinished matter${activeArtifactCount === 1 ? '' : 's'}`)
  }

  return (
    <div className="chat-surface flex h-[calc(100dvh-3.5rem-4.5rem-env(safe-area-inset-bottom))] flex-col lg:mx-auto lg:h-[calc(100dvh-3.5rem-2rem)] lg:max-w-4xl lg:overflow-hidden lg:rounded-2xl lg:border lg:border-border/50 lg:shadow-md">
      {session ? (
        <ChatHeader
          characterName={characterName}
          characterId={characterId}
          activeTopic={workingMemory.data?.activeTopic}
          pendingHint={pendingHints.length > 0 ? pendingHints.join(' · ') : null}
        />
      ) : null}

      <div className="chat-thread flex min-h-0 flex-1 flex-col overflow-y-auto px-3 py-4 sm:px-4" aria-live="polite">
        <div className="mx-auto w-full max-w-3xl flex-1">
          {invitation ? (
            <aside className="mb-6 rounded-xl border border-jungle-gold/20 bg-jungle-gold/5 px-4 py-3 text-sm leading-relaxed text-jungle-bark">
              <p className="font-display font-medium text-foreground">Bandar reached out</p>
              <p className="mt-2 whitespace-pre-wrap text-muted-foreground">{invitation}</p>
            </aside>
          ) : null}

          {isLoadingMessages ? (
            <div className="flex justify-center py-12">
              <LoadingSpinner label="Gathering the conversation…" />
            </div>
          ) : null}

          {!isLoadingMessages && messages.length === 0 ? <ChatEmptyState /> : null}

          {messages.map((message, index) => {
            const previous = messages[index - 1]
            const isGrouped = previous?.sender === message.sender
            return (
              <MessageBubble
                key={message.messageId}
                message={message}
                characterName={characterName}
                characterId={characterId}
                isGrouped={isGrouped}
              />
            )
          })}

          {sendMessage.isPending ? <TypingIndicator /> : null}
          <div ref={bottomRef} className="h-1" />
        </div>
      </div>

      <ChatComposer
        value={draft}
        disabled={sendMessage.isPending}
        onChange={setDraft}
        onSubmit={handleSubmit}
      />

      {sendMessage.isError ? (
        <div className="shrink-0 px-4 pb-3">
          <ErrorState error={sendMessage.error} title="Bandar could not hear that" />
        </div>
      ) : null}
    </div>
  )
}
