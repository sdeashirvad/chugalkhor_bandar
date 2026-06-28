import { useNavigate } from 'react-router-dom'
import { EmptyState } from '@/components/EmptyState'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Button } from '@/components/ui/button'
import {
  useDismissNotification,
  useMarkNotificationRead,
  useNotifications,
} from '@/hooks/useNotifications'
import type { NotificationResponse } from '@/api/notifications'

const TYPE_LABELS: Record<string, string> = {
  GREETING: 'A greeting awaits',
  BIRTHDAY: 'A birthday celebration',
  FESTIVAL: 'A festival is coming',
  REMINDER: 'A promise is waiting',
  GOSSIP: 'Word from the Jungle',
  WORLD_EVENT: 'News from the Jungle',
}

function NotificationCard({
  notification,
  onOpen,
  onDismiss,
}: {
  notification: NotificationResponse
  onOpen: (notification: NotificationResponse) => void
  onDismiss: (notification: NotificationResponse) => void
}) {
  return (
    <article className="world-card p-5">
      <p className="text-xs font-medium text-jungle-gold">
        {TYPE_LABELS[notification.type] ?? 'Bandar has something to tell you'}
      </p>
      <h3 className="mt-2 font-display text-lg font-semibold">{notification.title}</h3>
      <p className="mt-3 whitespace-pre-wrap text-sm text-muted-foreground">{notification.summary}</p>
      <div className="mt-4 flex gap-2">
        <Button type="button" onClick={() => onOpen(notification)} className="bg-jungle-moss hover:bg-jungle-canopy">
          Open with Bandar
        </Button>
        <Button type="button" variant="outline" onClick={() => onDismiss(notification)}>
          Set aside
        </Button>
      </div>
    </article>
  )
}

export function NotificationsPage() {
  const navigate = useNavigate()
  const notifications = useNotifications()
  const markRead = useMarkNotificationRead()
  const dismiss = useDismissNotification()

  function handleOpen(notification: NotificationResponse) {
    markRead.mutate(notification.id, {
      onSuccess: () => {
        navigate('/chat', {
          state: {
            notificationInvitation: notification.summary,
            notificationId: notification.id,
            notificationType: notification.type,
          },
        })
      },
    })
  }

  return (
    <div className="space-y-6">
      <PageHeader
        title="Letters"
        description="Invitations from Bandar — curiosity, not alarms."
      />

      {notifications.isError ? <ErrorState error={notifications.error} title="Bandar could not find your letters" /> : null}

      {notifications.data?.length === 0 ? (
        <EmptyState title="Nothing is unread at the moment." description="The Jungle is quiet for now." />
      ) : null}

      <div className="space-y-4">
        {notifications.data?.map((notification) => (
          <NotificationCard
            key={notification.id}
            notification={notification}
            onOpen={handleOpen}
            onDismiss={(n) => dismiss.mutate(n.id)}
          />
        ))}
      </div>
    </div>
  )
}
