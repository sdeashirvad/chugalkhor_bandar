import { apiClient } from '@/api/client'
import type { OrganizationDetails, OrganizationSummary } from '@/types/api'

export async function fetchOrganizations(): Promise<OrganizationSummary[]> {
  const { data } = await apiClient.get<OrganizationSummary[]>('/api/organizations')
  return data
}

export async function fetchOrganizationById(id: string): Promise<OrganizationDetails> {
  const { data } = await apiClient.get<OrganizationDetails>(`/api/organizations/${id}`)
  return data
}
