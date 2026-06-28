import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import { LoginPage } from '@/pages/LoginPage'

const mutateMock = vi.fn()

vi.mock('@/hooks/useSession', () => ({
  useSession: () => ({ data: null, isLoading: false }),
  useLogin: () => ({
    mutate: mutateMock,
    isPending: false,
    isError: false,
  }),
}))

vi.mock('@/hooks/useCharacters', () => ({
  useCharacters: () => ({
    data: [
      { id: 'hippu-king', name: 'Hippu King', species: 'Hippopotamus', titles: [], currentPlace: null, currentPlaceName: null, lastSeenAt: null },
    ],
    isLoading: false,
  }),
}))

vi.mock('@/api/session', () => ({
  readSessionId: () => null,
}))

describe('LoginPage', () => {
  it('renders jungle portal copy', () => {
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )

    expect(screen.getByText('Chugalkhor Bandar')).toBeInTheDocument()
    expect(screen.getByText('The Jungle remembers.')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Step Inside' })).toBeInTheDocument()
  })

  it('submits character credentials', async () => {
    const user = userEvent.setup()
    render(
      <MemoryRouter>
        <LoginPage />
      </MemoryRouter>,
    )

    await user.selectOptions(screen.getByLabelText('Character'), 'Hippu King')
    await user.type(screen.getByLabelText('Passkey'), 'jungle')
    await user.click(screen.getByRole('button', { name: 'Step Inside' }))

    expect(mutateMock).toHaveBeenCalled()
  })
})
