import { type ComponentProps } from 'react'
import { cn } from '@/lib/utils'

export function Badge({
  className,
  variant = 'default',
  ...props
}: ComponentProps<'span'> & { variant?: 'default' | 'success' | 'warning' | 'outline' }) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-md px-2 py-0.5 text-xs font-medium',
        variant === 'default' && 'bg-primary text-primary-foreground',
        variant === 'success' && 'bg-emerald-100 text-emerald-800',
        variant === 'warning' && 'bg-amber-100 text-amber-800',
        variant === 'outline' && 'border border-border bg-background text-foreground',
        className,
      )}
      {...props}
    />
  )
}
