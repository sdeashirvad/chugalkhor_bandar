interface PropertyGridProps {
  items: Array<{ label: string; value: React.ReactNode }>
}

export function PropertyGrid({ items }: PropertyGridProps) {
  return (
    <dl className="grid gap-4 sm:grid-cols-2">
      {items.map((item) => (
        <div key={item.label} className="rounded-lg border border-border bg-card p-4">
          <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
            {item.label}
          </dt>
          <dd className="mt-1 text-sm font-medium">{item.value}</dd>
        </div>
      ))}
    </dl>
  )
}
