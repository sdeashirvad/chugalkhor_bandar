import { Loader2 } from 'lucide-react'

interface LoadingSpinnerProps {
  label?: string
}

export function LoadingSpinner({ label = 'Bandar is checking the Jungle…' }: LoadingSpinnerProps) {
  return (
    <div className="flex items-center justify-center gap-2 py-12 text-sm text-muted-foreground">
      <Loader2 className="h-4 w-4 animate-spin text-jungle-moss" aria-hidden />
      <span>{label}</span>
    </div>
  )
}
