import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import {
  getReportingArchive,
  getReportingConfiguration,
  getReportingHistory,
  previewReportingHtml,
  previewReportingJson,
  previewReportingMarkdown,
  previewReportingTxt,
  sendTestReportEmail,
} from '@/api/reporting'

export function useReportingHistory() {
  return useQuery({
    queryKey: ['reporting', 'history'],
    queryFn: getReportingHistory,
  })
}

export function useReportingArchive() {
  return useQuery({
    queryKey: ['reporting', 'archive'],
    queryFn: getReportingArchive,
  })
}

export function useReportingConfiguration() {
  return useQuery({
    queryKey: ['reporting', 'configuration'],
    queryFn: getReportingConfiguration,
  })
}

export function usePreviewReportingHtml() {
  return useMutation({ mutationFn: previewReportingHtml })
}

export function usePreviewReportingTxt() {
  return useMutation({ mutationFn: previewReportingTxt })
}

export function usePreviewReportingJson() {
  return useMutation({ mutationFn: previewReportingJson })
}

export function usePreviewReportingMarkdown() {
  return useMutation({ mutationFn: previewReportingMarkdown })
}

export function useSendTestReportEmail() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: sendTestReportEmail,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reporting', 'history'] })
    },
  })
}
