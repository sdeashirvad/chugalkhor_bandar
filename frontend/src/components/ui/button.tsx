import { type ComponentProps } from 'react'
import { cn } from '@/lib/utils'

interface ButtonProps extends ComponentProps<'button'> {
  variant?: 'default' | 'outline'
  size?: 'default' | 'sm'
}

export function Button({ className, variant = 'default', size = 'default', ...props }: ButtonProps) {
  return (
    <button
      className={cn(
        'inline-flex items-center justify-center rounded-md text-sm font-medium transition-colors disabled:opacity-50',
        size === 'sm' ? 'h-8 px-3 text-xs' : 'h-9 px-4',
        variant === 'outline'
          ? 'border border-border bg-card hover:bg-accent'
          : 'bg-primary text-primary-foreground hover:opacity-90',
        className,
      )}
      {...props}
    />
  )
}
