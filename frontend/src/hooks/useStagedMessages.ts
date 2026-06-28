import { useEffect, useRef, useState } from 'react'
import type { ConversationMessage } from '@/types/conversation'

const BANDAR_REVEAL_MS = 850

export function useStagedMessages(messages: ConversationMessage[], isAwaitingReply: boolean) {
  const [visibleIds, setVisibleIds] = useState<Set<string>>(
    () => new Set(messages.map((message) => message.messageId)),
  )
  const scheduledRef = useRef<Set<string>>(new Set())
  const timersRef = useRef<number[]>([])

  const messageKey = messages.map((message) => message.messageId).join('|')

  useEffect(() => {
    return () => {
      timersRef.current.forEach((timer) => window.clearTimeout(timer))
      timersRef.current = []
    }
  }, [])

  useEffect(() => {
    timersRef.current.forEach((timer) => window.clearTimeout(timer))
    timersRef.current = []

    if (!isAwaitingReply) {
      scheduledRef.current = new Set()
      setVisibleIds(new Set(messages.map((message) => message.messageId)))
      return
    }

    setVisibleIds((previous) => {
      const next = new Set(previous)
      for (const message of messages) {
        if (message.sender === 'USER' || message.sender === 'SYSTEM') {
          next.add(message.messageId)
        }
      }
      return next
    })

    const unrevealedBandar = messages.filter(
      (message) =>
        message.sender === 'BANDAR' &&
        !scheduledRef.current.has(message.messageId),
    )

    unrevealedBandar.forEach((message, index) => {
      scheduledRef.current.add(message.messageId)
      const timer = window.setTimeout(() => {
        setVisibleIds((previous) => {
          const next = new Set(previous)
          next.add(message.messageId)
          return next
        })
      }, 400 + index * BANDAR_REVEAL_MS)
      timersRef.current.push(timer)
    })
  }, [messageKey, isAwaitingReply, messages])

  return messages.filter((message) => visibleIds.has(message.messageId))
}
