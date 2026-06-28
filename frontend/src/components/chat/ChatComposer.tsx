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
      className="chat-composer shrink-0 border-t border-border/40 bg-card/80 px-3 py-2.5 backdrop-blur-md pb-[max(0.625rem,env(safe-area-inset-bottom))] sm:px-4 lg:py-3"
      onSubmit={onSubmit}
    >
      <div className="mx-auto flex max-w-3xl items-center gap-2">
        <Input
          value={value}
          onChange={(event) => onChange(event.target.value)}
          placeholder="Speak to Bandar…"
          aria-label="Message to Bandar"
          disabled={disabled}
          className="h-11 flex-1 rounded-full border-jungle-bark/15 bg-background/80 px-4 shadow-inner"
        />
        <Button
          type="submit"
          disabled={disabled || !value.trim()}
          aria-label="Speak"
          className="h-11 w-11 shrink-0 rounded-full bg-jungle-moss p-0 hover:bg-jungle-canopy"
        >
          <Send className="h-4 w-4" aria-hidden />
        </Button>
      </div>
    </form>
  )
}
