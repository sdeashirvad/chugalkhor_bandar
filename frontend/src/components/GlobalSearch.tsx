import { useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Search } from 'lucide-react'
import { Input } from '@/components/ui/input'
import { useWorldSearch } from '@/hooks/useWorldSearch'
import { entityPath } from '@/lib/routes'

type GlobalSearchProps = {
  autoFocus?: boolean
  onNavigate?: () => void
}

export function GlobalSearch({ autoFocus = false, onNavigate }: GlobalSearchProps) {
  const [query, setQuery] = useState('')
  const [open, setOpen] = useState(false)
  const containerRef = useRef<HTMLDivElement>(null)
  const navigate = useNavigate()
  const { results, isLoading } = useWorldSearch(query)

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  return (
    <div ref={containerRef} className="relative w-full lg:max-w-md">
      <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
      <Input
        className="pl-9"
        placeholder="Search the Jungle…"
        value={query}
        autoFocus={autoFocus}
        onChange={(event) => {
          setQuery(event.target.value)
          setOpen(true)
        }}
        onFocus={() => setOpen(true)}
        aria-label="Search the Jungle"
      />
      {open && query.trim() ? (
        <div className="absolute z-50 mt-1 max-h-72 w-full overflow-auto rounded-md border border-border bg-card shadow-lg">
          {isLoading ? (
            <p className="p-3 text-sm text-muted-foreground">Searching…</p>
          ) : results.length === 0 ? (
            <p className="p-3 text-sm text-muted-foreground">No matches found.</p>
          ) : (
            results.map((result) => (
              <button
                key={`${result.type}-${result.id}`}
                type="button"
                className="flex w-full flex-col items-start gap-0.5 border-b border-border px-3 py-2 text-left last:border-b-0 hover:bg-muted/50"
                onClick={() => {
                  navigate(entityPath(result))
                  setOpen(false)
                  setQuery('')
                  onNavigate?.()
                }}
              >
                <span className="text-sm font-medium">{result.name}</span>
                <span className="text-xs text-muted-foreground capitalize">
                  {result.type}
                  {result.subtitle ? ` · ${result.subtitle}` : ''}
                </span>
              </button>
            ))
          )}
        </div>
      ) : null}
    </div>
  )
}
