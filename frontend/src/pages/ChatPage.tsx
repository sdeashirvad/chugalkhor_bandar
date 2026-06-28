import { useEffect, useMemo, useRef, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { ErrorState } from '@/components/ErrorState'
import { LoadingSpinner } from '@/components/LoadingSpinner'
import { ChatComposer } from '@/components/chat/ChatComposer'
import { ChatEmptyState } from '@/components/chat/ChatEmptyState'
import { ChatHeader } from '@/components/chat/ChatHeader'
import { MessageBubble } from '@/components/chat/MessageBubble'
import { TypingIndicator } from '@/components/chat/TypingIndicator'
import {
  useConversation,
  useConversationMessages,
  useSendMessage,
  useStartConversation,
} from '@/hooks/useConversation'
import { useChatAutoScroll } from '@/hooks/useChatAutoScroll'
import { useStagedMessages } from '@/hooks/useStagedMessages'
import { useSession } from '@/hooks/useSession'
import { useWorkingMemory } from '@/hooks/useWorkingMemory'
import { useUnreadNotificationCount } from '@/hooks/useNotifications'
import { useArtifacts } from '@/hooks/useArtifacts'

export function ChatPage() {
  const location = useLocation()
  const invitation = (location.state as { notificationInvitation?: string } | null)?.notificationInvitation
  const threadRef = useRef<HTMLDivElement>(null)
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

  const messages = messagesQuery.data?.messages ?? []
  const visibleMessages = useStagedMessages(messages, sendMessage.isPending)
  const messagesSignature = useMemo(
    () => visibleMessages.map((message) => `${message.messageId}:${message.content.length}`).join('|'),
    [visibleMessages],
  )
  const lastSender = visibleMessages[visibleMessages.length - 1]?.sender

  useChatAutoScroll(threadRef, {
    signature: messagesSignature,
    isPending: sendMessage.isPending,
    lastSender,
  })

  const isBootstrapping =
    conversation.isLoading || startConversation.isPending || (conversation.isError && startConversation.isPending)
  const isLoadingMessages = messagesQuery.isLoading && !messagesQuery.data

  if (isBootstrapping) {
    return (
      <div className="flex h-[100dvh] items-center justify-center lg:h-[calc(100dvh-3.5rem-1.5rem)]">
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

  const characterId = session?.currentCharacter.id ?? ''
  const characterName = session?.currentCharacter.displayName ?? 'You'
  const characterTitle = session?.currentCharacter.titles[0] ?? null

  const pendingHints: string[] = []
  if ((unread.data?.unreadCount ?? 0) > 0) pendingHints.push('Letters waiting')
  if (activeArtifactCount > 0) {
    pendingHints.push(`${activeArtifactCount} unfinished matter${activeArtifactCount === 1 ? '' : 's'}`)
  }

  return (
    <div className="chat-surface flex h-[100dvh] flex-col lg:mx-auto lg:h-[calc(100dvh-3.5rem-2rem)] lg:max-w-4xl lg:overflow-hidden lg:rounded-2xl lg:border lg:border-border/50 lg:shadow-md">
      {session ? (
        <ChatHeader
          characterName={characterName}
          characterId={characterId}
          activeTopic={workingMemory.data?.activeTopic}
          pendingHint={pendingHints.length > 0 ? pendingHints.join(' · ') : null}
        />
      ) : null}

      <div
        ref={threadRef}
        className="chat-thread flex min-h-0 flex-1 flex-col overflow-y-auto px-4 py-4 sm:px-5 sm:py-5 lg:px-8 lg:py-6"
        aria-live="polite"
      >
        <div className="mx-auto w-full max-w-2xl flex-1 pb-4">
          {invitation ? (
            <aside className="mb-8 rounded-2xl border border-jungle-gold/20 bg-jungle-gold/5 px-5 py-4 text-base leading-[1.7] text-jungle-bark">
              <p className="font-display font-medium text-foreground">Bandar reached out</p>
              <p className="mt-3 whitespace-pre-wrap text-muted-foreground">{invitation}</p>
            </aside>
          ) : null}

          {isLoadingMessages ? (
            <div className="flex justify-center py-16">
              <LoadingSpinner label="Gathering the conversation…" />
            </div>
          ) : null}

          {!isLoadingMessages && messages.length === 0 ? <ChatEmptyState /> : null}

          {visibleMessages.map((message, index) => {
            const previous = visibleMessages[index - 1]
            const isGrouped = previous?.sender === message.sender
            const isSpeakerChange = Boolean(previous && previous.sender !== message.sender)
            return (
              <MessageBubble
                key={message.messageId}
                message={message}
                characterName={characterName}
                characterId={characterId}
                characterTitle={characterTitle}
                isGrouped={isGrouped}
                isSpeakerChange={isSpeakerChange}
              />
            )
          })}

          {sendMessage.isPending ? <TypingIndicator /> : null}
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
