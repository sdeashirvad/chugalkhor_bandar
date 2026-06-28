import type { Components } from 'react-markdown'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import { cn } from '@/lib/utils'

interface ChatMessageContentProps {
  content: string
  variant: 'user' | 'bandar' | 'other'
}

const bandarComponents: Components = {
  p: ({ children }) => <p className="mb-2 last:mb-0">{children}</p>,
  strong: ({ children }) => <strong className="font-semibold">{children}</strong>,
  em: ({ children }) => <em className="italic">{children}</em>,
  ul: ({ children }) => <ul className="my-2 list-disc space-y-1.5 pl-4 last:mb-0">{children}</ul>,
  ol: ({ children }) => <ol className="my-2 list-decimal space-y-1.5 pl-4 last:mb-0">{children}</ol>,
  li: ({ children }) => <li className="leading-relaxed">{children}</li>,
  a: ({ href, children }) => (
    <a href={href} className="underline underline-offset-2" target="_blank" rel="noreferrer noopener">
      {children}
    </a>
  ),
}

export function ChatMessageContent({ content, variant }: ChatMessageContentProps) {
  if (variant === 'user') {
    return <span className="whitespace-pre-wrap">{content}</span>
  }

  return (
    <div className={cn('chat-markdown text-left', variant === 'bandar' && 'text-foreground')}>
      <Markdown remarkPlugins={[remarkGfm]} components={bandarComponents}>
        {content}
      </Markdown>
    </div>
  )
}
