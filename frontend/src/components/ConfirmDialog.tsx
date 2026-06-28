import { AnimatePresence, motion } from 'framer-motion'
import { Button } from '@/components/ui/button'

interface ConfirmDialogProps {
  open: boolean
  title: string
  message: string
  confirmLabel?: string
  cancelLabel?: string
  loading?: boolean
  onConfirm: () => void
  onCancel: () => void
}

export function ConfirmDialog({
  open,
  title,
  message,
  confirmLabel = 'Confirm',
  cancelLabel = 'Cancel',
  loading = false,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  return (
    <AnimatePresence>
      {open ? (
        <>
          <motion.button
            type="button"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 z-[60] bg-black/40"
            aria-label="Close dialog"
            onClick={onCancel}
          />
          <motion.div
            role="dialog"
            aria-modal="true"
            aria-labelledby="confirm-dialog-title"
            initial={{ opacity: 0, scale: 0.96, y: 8 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            exit={{ opacity: 0, scale: 0.96, y: 8 }}
            className="fixed left-1/2 top-1/2 z-[70] w-[min(100%-2rem,20rem)] -translate-x-1/2 -translate-y-1/2 rounded-xl border border-jungle-gold/20 bg-jungle-parchment/95 p-5 shadow-2xl backdrop-blur-md"
          >
            <h2 id="confirm-dialog-title" className="font-display text-lg font-semibold text-jungle-deep">
              {title}
            </h2>
            <p className="mt-2 text-sm text-muted-foreground">{message}</p>
            <div className="mt-5 flex gap-2">
              <Button type="button" variant="outline" className="flex-1" onClick={onCancel} disabled={loading}>
                {cancelLabel}
              </Button>
              <Button
                type="button"
                className="flex-1 bg-jungle-moss hover:bg-jungle-canopy"
                onClick={onConfirm}
                disabled={loading}
              >
                {loading ? 'Leaving…' : confirmLabel}
              </Button>
            </div>
          </motion.div>
        </>
      ) : null}
    </AnimatePresence>
  )
}
