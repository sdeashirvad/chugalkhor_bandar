import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { ChatPage } from '@/pages/ChatPage'

const useConversationMock = vi.fn()
const useConversationMessagesMock = vi.fn()
const useStartConversationMock = vi.fn()
const useSendMessageMock = vi.fn()

vi.mock('@/hooks/useConversation', () => ({
  useConversation: () => useConversationMock(),
  useConversationMessages: () => useConversationMessagesMock(),
  useStartConversation: () => useStartConversationMock(),
  useSendMessage: () => useSendMessageMock(),
}))

vi.mock('@/hooks/useSession', () => ({
  useSession: () => ({
    data: {
      sessionId: 's1',
      currentCharacter: {
        id: 'char1',
        displayName: 'Alpha',
        titles: ['Alpha'],
        species: 'Rabbitu',
        homeTerritory: null,
      },
      startedAt: '2026-06-27T12:00:00Z',
      lastActivity: '2026-06-27T12:00:00Z',
      status: 'ACTIVE',
    },
  }),
}))

vi.mock('@/hooks/useWorkingMemory', () => ({
  useWorkingMemory: () => ({ data: null }),
}))

vi.mock('@/hooks/useUnreadNotificationCount', () => ({
  useUnreadNotificationCount: () => ({ data: { unreadCount: 0 } }),
}))

vi.mock('@/hooks/useArtifacts', () => ({
  useArtifacts: () => ({ data: [] }),
}))

function renderChat() {
  const queryClient = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  return render(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter>
        <ChatPage />
      </MemoryRouter>
    </QueryClientProvider>,
  )
}

describe('ChatPage', () => {
  beforeEach(() => {
    useConversationMock.mockReturnValue({
      isLoading: false,
      isError: false,
      isSuccess: true,
      data: {
        conversationId: 'c1',
        sessionId: 's1',
        currentCharacter: {
          id: 'char1',
          displayName: 'Alpha',
          titles: ['Alpha'],
          species: 'Rabbitu',
          homeTerritory: null,
        },
        startedAt: '2026-06-27T12:00:00Z',
        lastActivity: '2026-06-27T12:00:00Z',
        status: 'ACTIVE',
      },
    })
    useStartConversationMock.mockReturnValue({
      mutate: vi.fn(),
      isPending: false,
      isError: false,
    })
    useConversationMessagesMock.mockReturnValue({
      isLoading: false,
      isError: false,
      data: {
        messages: [
          {
            messageId: 'm1',
            sender: 'USER',
            timestamp: '2026-06-27T12:00:00Z',
            content: 'Hello',
            visibility: 'PUBLIC',
            metadata: {},
          },
          {
            messageId: 'm2',
            sender: 'BANDAR',
            timestamp: '2026-06-27T12:00:01Z',
            content: 'I heard you.',
            visibility: 'PUBLIC',
            metadata: {},
          },
        ],
      },
    })
    useSendMessageMock.mockReturnValue({
      mutate: vi.fn(),
      isPending: false,
      isError: false,
    })
  })

  it('renders messages and input', () => {
    renderChat()

    expect(screen.getAllByText('Alpha').length).toBeGreaterThan(0)
    expect(screen.getByText('Hello')).toBeInTheDocument()
    expect(screen.getByText('I heard you.')).toBeInTheDocument()
    expect(screen.getByLabelText('Message to Bandar')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Speak' })).toBeInTheDocument()
  })

  it('submits message on send', async () => {
    const mutate = vi.fn()
    useSendMessageMock.mockReturnValue({
      mutate,
      isPending: false,
      isError: false,
    })
    const user = userEvent.setup()

    renderChat()

    await user.type(screen.getByLabelText('Message to Bandar'), 'Hi Bandar')
    await user.click(screen.getByRole('button', { name: 'Speak' }))

    expect(mutate).toHaveBeenCalledWith({ content: 'Hi Bandar' })
  })
})
