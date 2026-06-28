import { MemoryRouter } from 'react-router-dom'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { EntityLink } from '@/components/EntityLink'

describe('EntityLink', () => {
  it('renders a navigable character link', () => {
    render(
      <MemoryRouter>
        <EntityLink entity={{ id: 'character_a', name: 'Alpha', type: 'character' }} />
      </MemoryRouter>,
    )
    const link = screen.getByRole('link', { name: 'Alpha' })
    expect(link).toHaveAttribute('href', '/characters/character_a')
  })
})
