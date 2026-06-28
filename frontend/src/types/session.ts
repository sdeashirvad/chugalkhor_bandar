export const SESSION_STORAGE_KEY = 'chugalkhor_session_id'

export interface CurrentCharacter {
  id: string
  displayName: string
  titles: string[]
  species: string
  homeTerritory: string | null
  currentLocation?: string | null
}

export interface SessionResponse {
  sessionId: string
  currentCharacter: CurrentCharacter
  startedAt: string
  lastActivity: string
  status: string
}

export interface LoginRequest {
  animalName: string
  passkey: string
}
