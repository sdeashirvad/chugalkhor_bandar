import { useQuery } from '@tanstack/react-query'
import { fetchOrganizationById, fetchOrganizations } from '@/api/organizations'

export function useOrganizations() {
  return useQuery({ queryKey: ['organizations'], queryFn: fetchOrganizations })
}

export function useOrganization(id: string | undefined) {
  return useQuery({
    queryKey: ['organizations', id],
    queryFn: () => fetchOrganizationById(id!),
    enabled: Boolean(id),
  })
}
