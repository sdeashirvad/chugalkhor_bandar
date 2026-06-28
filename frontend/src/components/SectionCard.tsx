import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

interface SectionCardProps {
  title: string
  children: React.ReactNode
}

export function SectionCard({ title, children }: SectionCardProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>{title}</CardTitle>
      </CardHeader>
      <CardContent className="whitespace-pre-wrap text-sm leading-relaxed text-foreground/90">
        {children}
      </CardContent>
    </Card>
  )
}
