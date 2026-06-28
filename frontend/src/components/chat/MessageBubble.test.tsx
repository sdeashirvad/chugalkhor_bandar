import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { MessageBubble } from '@/components/chat/MessageBubble'
import type { ConversationMessage } from '@/types/conversation'

const bandarMessage: ConversationMessage = {
  messageId: 'm1',
  sender: 'BANDAR',
  timestamp: '2026-06-28T12:00:00Z',
  content: 'Hello **friend**.\n\n* **Little Brother:** A brave soul.\n* **Rabbitu Minister:** A sweet heart.',
  visibility: 'PUBLIC',
  metadata: {},
}

describe('MessageBubble', () => {
  it('renders Bandar markdown with bold text and lists', () => {
    render(
      <MessageBubble message={bandarMessage} characterName="Alpha" characterId="char1" characterTitle="Prince" />,
    )

    expect(screen.getByText('Storyteller')).toBeInTheDocument()
    expect(screen.getByText('friend').tagName).toBe('STRONG')
    expect(screen.getByText('Little Brother:')).toBeInTheDocument()
    expect(screen.getByText('A brave soul.')).toBeInTheDocument()
    expect(screen.getAllByRole('listitem')).toHaveLength(2)
  })

  it('renders user messages as plain text with character title', () => {
    render(
      <MessageBubble
        message={{ ...bandarMessage, sender: 'USER', content: 'Hi **Bandar**' }}
        characterName="Second Hippu"
        characterId="char1"
        characterTitle="Adventurer"
      />,
    )

    expect(screen.getByText('Second Hippu')).toBeInTheDocument()
    expect(screen.getByText('Adventurer')).toBeInTheDocument()
    expect(screen.getByText('Hi **Bandar**')).toBeInTheDocument()
  })
})
