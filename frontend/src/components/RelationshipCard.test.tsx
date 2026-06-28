import { MemoryRouter } from 'react-router-dom'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { RelationshipCard } from '@/components/RelationshipCard'

describe('RelationshipCard', () => {
  it('links to the target character', () => {
    render(
      <MemoryRouter>
        <RelationshipCard
          relationship={{
            id: 'rel-1',
            title: 'Alliance',
            relationshipType: 'Ally',
            status: 'Active',
            targetCharacter: { id: 'character_b', name: 'Beta', type: 'character' },
          }}
        />
      </MemoryRouter>,
    )

    expect(screen.getByRole('link')).toHaveAttribute('href', '/characters/character_b')
    expect(screen.getByText('Beta')).toBeInTheDocument()
    expect(screen.getByText('Ally')).toBeInTheDocument()
  })
})
