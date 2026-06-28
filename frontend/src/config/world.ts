export const FEATURED_CHARACTER_IDS = [
  'character_hippu_king',
  'character_second_hippu',
  'character_rabbitu_minister',
  'character_little_brother',
] as const

export const BANDAR_CHARACTER_ID = 'character_bandar'

export const AVATAR_BY_CHARACTER_ID: Record<string, string> = {
  character_bandar: '/assets/avatars/bandar.webp',
  character_hippu_king: '/assets/avatars/hippu-king.webp',
  character_second_hippu: '/assets/avatars/second-hippu.webp',
  character_rabbitu_minister: '/assets/avatars/rabbitu-minister.webp',
  character_little_brother: '/assets/avatars/little-brother.webp',
  character_giraffe_sir: '/assets/avatars/giraffe-sir.webp',
  character_bhaisiya: '/assets/avatars/bhaisiya.webp',
  character_hathiya: '/assets/avatars/hathiya.webp',
  character_chhotua: '/assets/avatars/chhotua.webp',
  character_bhaiya_ji: '/assets/avatars/bhaiya-ji.webp',
  character_hippu_horse: '/assets/avatars/hippu-horse.webp',
  character_golden_hippu_horse: '/assets/avatars/golden-hippu-horse.webp',
  character_flying_hippu_horse: '/assets/avatars/flying-hippu-horse.webp',
}

export const DEFAULT_AVATAR = '/assets/avatars/default-resident.webp'

export const ACTIVITY_HINTS: Record<string, string> = {
  character_hippu_king: 'Holding Court',
  character_second_hippu: 'Exploring Border Jungle',
  character_rabbitu_minister: 'Reading old records',
  character_little_brother: 'At Giraffe Jungle School',
  character_bandar: 'Collecting stories',
}

export const WELCOME_VARIANTS: Record<string, string[]> = {
  character_hippu_king: [
    'Hippu King… you came back.',
    'The Palace is awake.',
    'Your jungle has news.',
    'Bandar has been collecting stories.',
    'Welcome back, ruler of 176 jungles.',
    'The old stones of Hippu Palace remember you.',
    'Before we begin, I remembered something.',
  ],
  character_second_hippu: [
    'Second Hippu, the border is restless.',
    'Another expedition, perhaps?',
    'Bandar has a tale from the edge of the Jungle.',
    'Welcome back, commander.',
    'The crow spies were busy.',
    'I\'ve been waiting with a story.',
  ],
  character_rabbitu_minister: [
    'Rabbitu Minister, I heard a rumor.',
    'The old records have changed again.',
    'Bandar found something interesting in the archives.',
    'Your quiet jungle has not been quiet.',
    'Welcome back, Minister of many jungles.',
    'The Jungle was quieter without you.',
  ],
  character_little_brother: [
    'Little Brother… back from the jungle paths?',
    'Giraffe Sir has not sent another mountain of homework, has he?',
    'The bees at Beehive Grove were buzzing a melody today.',
    'Bandar saved a story just for you.',
    'Welcome home, young prince of Hippu Palace.',
    'The Palace felt quieter without your footsteps.',
  ],
  default: [
    'Welcome back to the Jungle.',
    'The canopy remembers your footsteps.',
    'Bandar noticed you returned.',
    'Something may have happened while you were away.',
    'The Jungle has news.',
  ],
}

export const WELCOME_WITH_NOTIFICATIONS = [
  'Before we begin, I remembered something.',
  'Welcome back. The Jungle has news.',
  'Bandar has something to tell you.',
]

export const WELCOME_WITH_ARTIFACTS = [
  'I\'ve been waiting with a story.',
  'An unfinished matter awaits.',
  'A promise still lingers in the air.',
]

export function isFeaturedCharacter(characterId: string): boolean {
  return (FEATURED_CHARACTER_IDS as readonly string[]).includes(characterId)
}
