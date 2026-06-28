import { render, screen } from '@testing-library/react'
import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import { CharacterDetailsPage } from '@/pages/CharacterDetailsPage'

const sampleCharacter = {
  id: 'character_little_brother',
  name: 'Little Brother',
  profile: 'Youngest Hippu prince.',
  titles: ['Prince', 'Student'],
  history: '',
  assets: '',
  relationships: [],
  preferences: {},
  publicFacts: [],
  currentLocation: { placeId: 'place_hippu_palace', placeName: 'Hippu Palace' },
  currentTerritory: { id: 'territory_hippu_kingdom', name: 'Hippu Kingdom', type: 'territory' },
  organizations: [],
}

vi.mock('@/hooks/useCharacters', () => ({
  useCharacter: () => ({
    data: sampleCharacter,
    isLoading: false,
    isError: false,
    error: null,
  }),
}))

describe('CharacterDetailsPage', () => {
  it('shows character portrait from avatar mapping', () => {
    const { container } = render(
      <MemoryRouter initialEntries={['/characters/character_little_brother']}>
        <Routes>
          <Route path="/characters/:id" element={<CharacterDetailsPage />} />
        </Routes>
      </MemoryRouter>,
    )

    const portrait = container.querySelector('img')
    expect(portrait).toHaveAttribute('src', '/assets/avatars/little-brother.webp')
    expect(screen.getByRole('heading', { level: 1, name: 'Little Brother' })).toBeInTheDocument()
  })
})
