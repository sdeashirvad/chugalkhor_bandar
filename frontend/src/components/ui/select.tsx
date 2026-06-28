import { type ComponentProps } from 'react'
import { cn } from '@/lib/utils'

export function Select({ className, ...props }: ComponentProps<'select'>) {
  return (
    <select
      className={cn(
        'flex h-11 w-full appearance-none rounded-md border border-border bg-white px-3 py-2 text-sm shadow-sm outline-none focus-visible:ring-2 focus-visible:ring-zinc-400',
        className,
      )}
      {...props}
    />
  )
}
