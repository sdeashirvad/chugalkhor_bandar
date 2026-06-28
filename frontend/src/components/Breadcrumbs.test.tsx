import { MemoryRouter } from 'react-router-dom'
import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { Breadcrumbs } from '@/components/Breadcrumbs'

describe('Breadcrumbs', () => {
  it('renders linked and current crumbs', () => {
    render(
      <MemoryRouter>
        <Breadcrumbs
          items={[
            { label: 'World', to: '/world' },
            { label: 'Characters', to: '/characters' },
            { label: 'Rabbitu Minister' },
          ]}
        />
      </MemoryRouter>,
    )

    expect(screen.getByRole('link', { name: 'World' })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: 'Characters' })).toBeInTheDocument()
    expect(screen.getByText('Rabbitu Minister')).toBeInTheDocument()
  })
})
