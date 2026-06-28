export interface EntityReference {
  id: string
  name: string
  type: 'character' | 'place' | 'territory' | 'organization' | 'story' | string
}

export interface WorldStatus {
  status: string
  bootstrapVersion: string
  bootstrapTimestamp: string | null
  runtimeStartedAt: string | null
  persistenceProvider: string
  characters: number
  stories: number
  territories: number
  places: number
  organizations: number
  relationships: number
  timelineEntries: number
  charactersBySpecies: Record<string, number>
  storiesByEra: Record<string, number>
}

export interface CharacterSummary {
  id: string
  name: string
  species: string
  titles: string[]
  currentPlace: string | null
  currentPlaceName: string | null
  lastSeenAt: string | null
}

export interface RelationshipSummary {
  id: string
  title: string
  relationshipType: string | null
  status: string | null
  targetCharacter: EntityReference | null
}

export interface CurrentLocation {
  placeId: string | null
  placeName: string | null
  territoryId: string | null
  territoryName: string | null
}

export interface OrganizationMembership {
  organizationId: string
  organizationName: string
  role: string
}

export interface CharacterDetails {
  id: string
  name: string
  profile: string
  titles: string[]
  history: string
  assets: string
  relationships: RelationshipSummary[]
  preferences: Record<string, string>
  publicFacts: string[]
  currentLocation: CurrentLocation
  currentTerritory: EntityReference | null
  organizations: OrganizationMembership[]
}

export interface StorySummary {
  id: string
  title: string
  summary: string
  era: string
}

export interface StoryDetails {
  id: string
  title: string
  summary: string
  era: string
  participants: EntityReference[]
  places: EntityReference[]
  sections: Record<string, string>
  linkedStories: Record<string, string>
}

export interface TerritorySummary {
  id: string
  name: string
  ruler: EntityReference | null
}

export interface TerritoryDetails {
  id: string
  name: string
  ruler: EntityReference | null
  ministers: EntityReference[]
  places: EntityReference[]
  sections: Record<string, string>
}

export interface PlaceSummary {
  id: string
  name: string
  type: string
  territory: EntityReference | null
}

export interface PlaceDetails {
  id: string
  name: string
  type: string
  territory: EntityReference | null
  owner: EntityReference | null
  connectedPlaces: EntityReference[]
  sections: Record<string, string>
}

export interface OrganizationSummary {
  id: string
  name: string
  type: string
  leader: EntityReference | null
}

export interface OrganizationDetails {
  id: string
  name: string
  type: string
  leader: EntityReference | null
  headquarters: EntityReference | null
  members: EntityReference[]
  sections: Record<string, string>
}

export interface SearchResult {
  id: string
  name: string
  type: EntityReference['type']
  subtitle?: string
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
}
