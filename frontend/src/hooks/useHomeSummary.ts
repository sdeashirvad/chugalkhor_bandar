import { useQuery } from '@tanstack/react-query'
import { fetchCharacters } from '@/api/characters'
import { getLatestWorldTickDev, listWorldEvents } from '@/api/livingWorld'
import { listChronicles } from '@/api/chronicles'
import { listArtifacts } from '@/api/artifacts'
import { getUnreadNotificationCount } from '@/api/notifications'
import { getWorkingMemory } from '@/api/memory'
import { fetchCurrentConversation, fetchConversationMessages } from '@/api/conversation'
import { fetchCurrentSession } from '@/api/session'

export function useHomeSummary() {
  return useQuery({
    queryKey: ['home-summary'],
    queryFn: async () => {
      const [session, characters, unread, artifacts, events, chronicles, workingMemory, conversation, messages, tick] =
        await Promise.all([
          fetchCurrentSession(),
          fetchCharacters().catch(() => []),
          getUnreadNotificationCount().catch(() => ({ unreadCount: 0 })),
          listArtifacts().catch(() => []),
          listWorldEvents().catch(() => []),
          listChronicles().catch(() => []),
          getWorkingMemory().catch(() => null),
          fetchCurrentConversation().catch(() => null),
          fetchConversationMessages().catch(() => ({ messages: [] })),
          getLatestWorldTickDev().catch(() => null),
        ])

      const character = characters.find((c) => c.id === session.currentCharacter.id) ?? null
      const activeArtifacts = artifacts.filter((a) => a.status === 'ACTIVE' || a.status === 'NEW').length
      const bandarMessages = messages.messages.filter((m) => m.sender === 'BANDAR')
      const lastBandarLine = bandarMessages.length > 0 ? bandarMessages[bandarMessages.length - 1].content.slice(0, 120) : null

      return {
        session,
        character,
        workingMemory,
        lastBandarLine,
        unreadCount: unread.unreadCount,
        activeArtifacts,
        recentEvents: events.slice(0, 6),
        latestChronicle: chronicles[0] ?? null,
        worldDate: tick?.worldDate ?? null,
        conversation,
      }
    },
  })
}
