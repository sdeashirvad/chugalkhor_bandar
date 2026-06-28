import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatSectionLabel(key: string): string {
  const labels: Record<string, string> = {
    summary: 'Summary',
    participants: 'Participants',
    majorPlaces: 'Places',
    places: 'Places',
    beginning: 'Beginning',
    keyEvents: 'Timeline',
    timeline: 'Timeline',
    consequences: 'Consequences',
    ending: 'Ending',
    ministers: 'Ministers',
    goals: 'Goals',
    knownGoals: 'Goals',
    government: 'Government',
    primaryInstitutions: 'Institutions',
  }
  return labels[key] ?? key.replace(/([A-Z])/g, ' $1').replace(/^./, (s) => s.toUpperCase())
}

export function formatDateTime(value: string | null | undefined): string {
  if (!value) return '—'
  try {
    return new Intl.DateTimeFormat(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short',
    }).format(new Date(value))
  } catch {
    return value
  }
}

export function formatProvider(value: string): string {
  return value.replaceAll('_', ' ')
}
