import { render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { StatCard } from '@/components/StatCard'

describe('StatCard', () => {
  it('renders label and value', () => {
    render(<StatCard label="Characters" value={13} hint="In the Jungle" />)
    expect(screen.getByText('Characters')).toBeInTheDocument()
    expect(screen.getByText('13')).toBeInTheDocument()
    expect(screen.getByText('In the Jungle')).toBeInTheDocument()
  })
})
