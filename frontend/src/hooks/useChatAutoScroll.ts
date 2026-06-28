import { useEffect, useRef, type RefObject } from 'react'

const STICK_THRESHOLD_PX = 96

interface ChatAutoScrollOptions {
  signature: string
  isPending: boolean
  lastSender?: string
}

export function useChatAutoScroll(
  threadRef: RefObject<HTMLDivElement | null>,
  { signature, isPending, lastSender }: ChatAutoScrollOptions,
) {
  const stickToBottomRef = useRef(true)

  useEffect(() => {
    const thread = threadRef.current
    if (!thread) return

    const onScroll = () => {
      const distanceFromBottom = thread.scrollHeight - thread.scrollTop - thread.clientHeight
      stickToBottomRef.current = distanceFromBottom <= STICK_THRESHOLD_PX
    }

    onScroll()
    thread.addEventListener('scroll', onScroll, { passive: true })
    return () => thread.removeEventListener('scroll', onScroll)
  }, [threadRef])

  useEffect(() => {
    const thread = threadRef.current
    if (!thread) return

    const shouldScroll = stickToBottomRef.current || lastSender === 'USER' || isPending
    if (!shouldScroll) return

    const scrollToBottom = (behavior: ScrollBehavior) => {
      if (typeof thread.scrollTo === 'function') {
        thread.scrollTo({ top: thread.scrollHeight, behavior })
      } else {
        thread.scrollTop = thread.scrollHeight
      }
    }

    scrollToBottom(isPending || lastSender === 'BANDAR' ? 'smooth' : 'instant')

    if (!isPending && lastSender !== 'BANDAR') return

    const afterLayout = window.setTimeout(() => scrollToBottom('smooth'), 320)
    return () => window.clearTimeout(afterLayout)
  }, [signature, isPending, lastSender, threadRef])
}
