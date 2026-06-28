import { Link } from 'react-router-dom'
import { PageHeader } from '@/components/PageHeader'

export function NotFoundPage() {
  return (
    <div>
      <PageHeader title="Page not found" description="This path does not exist in the Jungle explorer." />
      <Link to="/world" className="text-sm font-medium hover:underline">
        Return to world dashboard
      </Link>
    </div>
  )
}
