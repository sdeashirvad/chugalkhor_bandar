import { Search } from 'lucide-react'
import { Input } from '@/components/ui/input'

interface SearchBoxProps {
  value: string
  onChange: (value: string) => void
  placeholder?: string
}

export function SearchBox({ value, onChange, placeholder = 'Search…' }: SearchBoxProps) {
  return (
    <div className="relative max-w-md">
      <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
      <Input
        className="pl-9"
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        aria-label={placeholder}
      />
    </div>
  )
}
