import { Send } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'

interface ChatComposerProps {
  value: string
  disabled?: boolean
  onChange: (value: string) => void
  onSubmit: (event: React.FormEvent) => void
}

export function ChatComposer({ value, disabled, onChange, onSubmit }: ChatComposerProps) {
  return (
    <form
      className="chat-composer shrink-0 border-t border-border/30 bg-card/85 px-4 py-3 backdrop-blur-md pb-[max(0.75rem,env(safe-area-inset-bottom))] sm:px-5 lg:py-4"
      onSubmit={onSubmit}
    >
      <div className="mx-auto flex max-w-3xl items-center gap-2">
        <Input
          value={value}
          onChange={(event) => onChange(event.target.value)}
          placeholder="Speak to Bandar…"
          aria-label="Message to Bandar"
          disabled={disabled}
          className="h-12 min-h-12 flex-1 rounded-full border-jungle-bark/15 bg-background/90 px-5 text-base shadow-inner"
        />
        <Button
          type="submit"
          disabled={disabled || !value.trim()}
          aria-label="Speak"
          className="h-12 w-12 min-h-12 min-w-12 shrink-0 rounded-full bg-jungle-moss p-0 hover:bg-jungle-canopy"
        >
          <Send className="h-4 w-4" aria-hidden />
        </Button>
      </div>
    </form>
  )
}
