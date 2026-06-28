import { AlertCircle } from 'lucide-react'
import { getApiErrorMessage } from '@/api/client'

interface ErrorStateProps {
  error: unknown
  title?: string
}

export function ErrorState({ error, title = 'The path was blocked' }: ErrorStateProps) {
  return (
    <div
      role="alert"
      className="flex flex-col items-center justify-center gap-2 rounded-xl border border-jungle-bark/20 bg-jungle-warm/10 px-6 py-12 text-center"
    >
      <AlertCircle className="h-8 w-8 text-jungle-bark" aria-hidden />
      <h3 className="font-display text-sm font-medium text-foreground">{title}</h3>
      <p className="max-w-md text-sm text-muted-foreground">{getApiErrorMessage(error)}</p>
    </div>
  )
}
