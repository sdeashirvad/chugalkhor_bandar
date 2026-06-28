import { Leaf } from 'lucide-react'

interface EmptyStateProps {
  title: string
  description?: string
}

export function EmptyState({ title, description }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 rounded-xl border border-dashed border-jungle-bark/20 bg-card/60 px-6 py-12 text-center">
      <Leaf className="h-8 w-8 text-jungle-moss" aria-hidden />
      <h3 className="font-display text-sm font-medium">{title}</h3>
      {description ? <p className="max-w-sm text-sm text-muted-foreground">{description}</p> : null}
    </div>
  )
}
