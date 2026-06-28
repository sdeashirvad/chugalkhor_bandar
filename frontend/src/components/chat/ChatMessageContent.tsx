import type { Components } from 'react-markdown'
import Markdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import {
  isMarkdownListBlock,
  isNarrationBlock,
  splitInlineNarration,
  splitStoryBlocks,
  stripNarrationMarkers,
} from '@/lib/chat/splitStoryBlocks'
import { cn } from '@/lib/utils'

interface ChatMessageContentProps {
  content: string
  variant: 'user' | 'bandar' | 'other'
}

const bandarMarkdownComponents: Components = {
  p: ({ children }) => <p className="chat-paragraph">{children}</p>,
  strong: ({ children }) => <strong className="font-semibold text-foreground">{children}</strong>,
  em: ({ children }) => <em className="chat-narration">{children}</em>,
  ul: ({ children }) => <ul className="chat-list chat-list-disc">{children}</ul>,
  ol: ({ children }) => <ol className="chat-list chat-list-decimal">{children}</ol>,
  li: ({ children }) => <li className="chat-list-item">{children}</li>,
  a: ({ href, children }) => (
    <a href={href} className="chat-link" target="_blank" rel="noreferrer noopener">
      {children}
    </a>
  ),
}

function InlineStoryText({ text }: { text: string }) {
  const parts = splitInlineNarration(text)
  const hasMarkdown = parts.some((part) => part.type === 'text' && /[*_[]/.test(part.value))

  if (hasMarkdown) {
    return (
      <Markdown remarkPlugins={[remarkGfm]} components={bandarMarkdownComponents}>
        {text}
      </Markdown>
    )
  }

  return (
    <>
      {parts.map((part, index) =>
        part.type === 'narration' ? (
          <span key={index} className="chat-narration">
            {stripNarrationMarkers(part.value)}
          </span>
        ) : (
          <span key={index}>{part.value}</span>
        ),
      )}
    </>
  )
}

function StoryBlock({ block }: { block: string }) {
  if (isMarkdownListBlock(block)) {
    return (
      <Markdown remarkPlugins={[remarkGfm]} components={bandarMarkdownComponents}>
        {block}
      </Markdown>
    )
  }

  if (isNarrationBlock(block)) {
    return <p className="chat-narration-block">{stripNarrationMarkers(block)}</p>
  }

  return (
    <p className="chat-paragraph">
      <InlineStoryText text={block} />
    </p>
  )
}

export function ChatMessageContent({ content, variant }: ChatMessageContentProps) {
  if (variant === 'user') {
    return <span className="chat-user-text">{content}</span>
  }

  const blocks = splitStoryBlocks(content)

  return (
    <div className={cn('chat-markdown', variant === 'bandar' && 'chat-markdown-bandar')}>
      {blocks.map((block, index) => (
        <StoryBlock key={`${index}-${block.slice(0, 24)}`} block={block} />
      ))}
    </div>
  )
}
