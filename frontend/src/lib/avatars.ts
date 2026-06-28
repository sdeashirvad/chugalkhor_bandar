import { AVATAR_BY_CHARACTER_ID, DEFAULT_AVATAR } from '@/config/world'

export function getCharacterAvatar(characterId: string): string {
  return AVATAR_BY_CHARACTER_ID[characterId] ?? DEFAULT_AVATAR
}
