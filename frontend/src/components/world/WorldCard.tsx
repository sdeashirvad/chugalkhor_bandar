import type { ReactNode } from 'react'
import { cn } from '@/lib/utils'

interface WorldCardProps {
  children: ReactNode
  className?: string
  title?: string
  subtitle?: string
  icon?: ReactNode
}

export function WorldCard({ children, className, title, subtitle, icon }: WorldCardProps) {
  return (
    <article className={cn('world-card paper-texture p-5', className)}>
      {title ? (
        <header className="mb-4 flex items-start gap-3">
          {icon ? <div className="text-jungle-moss">{icon}</div> : null}
          <div>
            <h2 className="font-display text-xl font-semibold text-foreground lg:text-lg">{title}</h2>
            {subtitle ? <p className="mt-1 text-base text-muted-foreground lg:text-sm">{subtitle}</p> : null}
          </div>
        </header>
      ) : null}
      {children}
    </article>
  )
}

interface HomeBlockProps {
  label: string
  children: ReactNode
  className?: string
}

export function HomeBlock({ label, children, className }: HomeBlockProps) {
  return (
    <WorldCard className={className} title={label}>
      {children}
    </WorldCard>
  )
}
