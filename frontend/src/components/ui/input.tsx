import { type ComponentProps } from 'react'
import { cn } from '@/lib/utils'

export function Input({ className, ...props }: ComponentProps<'input'>) {
  return (
    <input
      className={cn(
        'flex h-9 w-full rounded-md border border-border bg-white px-3 py-1 text-sm shadow-sm outline-none placeholder:text-muted-foreground focus-visible:ring-2 focus-visible:ring-zinc-400',
        className,
      )}
      {...props}
    />
  )
}
