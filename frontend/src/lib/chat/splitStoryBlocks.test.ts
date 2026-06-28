import { describe, expect, it } from 'vitest'
import {
  isNarrationBlock,
  splitInlineNarration,
  splitStoryBlocks,
  stripNarrationMarkers,
} from '@/lib/chat/splitStoryBlocks'

describe('splitStoryBlocks', () => {
  it('preserves paragraph breaks', () => {
    expect(splitStoryBlocks('First paragraph.\n\nSecond paragraph.')).toEqual([
      'First paragraph.',
      'Second paragraph.',
    ])
  })

  it('detects narration blocks', () => {
    expect(isNarrationBlock('(laughs)')).toBe(true)
    expect(isNarrationBlock('*Bandar smiles.*')).toBe(true)
  })

  it('splits inline parenthetical narration', () => {
    expect(splitInlineNarration('Hello (laughs) friend')).toEqual([
      { type: 'text', value: 'Hello ' },
      { type: 'narration', value: '(laughs)' },
      { type: 'text', value: ' friend' },
    ])
  })

  it('strips narration markers', () => {
    expect(stripNarrationMarkers('(leaning in)')).toBe('leaning in')
    expect(stripNarrationMarkers('*Bandar smiles.*')).toBe('Bandar smiles.')
  })
})
