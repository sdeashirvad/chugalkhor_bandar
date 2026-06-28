import { Link } from 'react-router-dom'
import { Breadcrumbs } from '@/components/Breadcrumbs'
import { EmptyState } from '@/components/EmptyState'
import { ErrorState } from '@/components/ErrorState'
import { PageHeader } from '@/components/PageHeader'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useStories } from '@/hooks/useStories'

export function StoriesPage() {
  const { data, isLoading, isError, error } = useStories()

  if (isLoading) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-32" />
        <div className="grid gap-4 md:grid-cols-2">
          {Array.from({ length: 4 }).map((_, index) => (
            <Skeleton key={index} className="h-32" />
          ))}
        </div>
      </div>
    )
  }

  if (isError) {
    return <ErrorState error={error} title="Unable to load stories" />
  }

  return (
    <div>
      <Breadcrumbs items={[{ label: 'World', to: '/world' }, { label: 'Stories' }]} />
      <PageHeader title="Stories" description="Canonical tales from the Jungle chronicle." />

      {!data || data.length === 0 ? (
        <EmptyState title="No stories found." description="Stories will appear after bootstrap." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {data.map((story) => (
            <Link key={story.id} to={`/stories/${story.id}`} className="block">
              <Card className="h-full transition-colors hover:bg-muted/30">
                <CardHeader>
                  <CardTitle>{story.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="line-clamp-4 text-sm text-muted-foreground">{story.summary || 'No summary.'}</p>
                  {story.era ? <p className="mt-2 text-xs text-muted-foreground">{story.era}</p> : null}
                </CardContent>
              </Card>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
