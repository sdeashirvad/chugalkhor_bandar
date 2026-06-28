import { createBrowserRouter, Navigate } from 'react-router-dom'
import { AppShell } from '@/layout/AppShell'
import { AdminShell } from '@/layout/AdminShell'
import { AdminRoute } from '@/router/AdminRoute'
import { ProtectedRoute } from '@/router/ProtectedRoute'
import { HomePage } from '@/pages/HomePage'
import { AdminLoginPage } from '@/pages/AdminLoginPage'
import { AdminOverviewPage } from '@/pages/admin/AdminOverviewPage'
import { LoginPage } from '@/pages/LoginPage'
import { ChatPage } from '@/pages/ChatPage'
import { CharactersPage } from '@/pages/CharactersPage'
import { CharacterDetailsPage } from '@/pages/CharacterDetailsPage'
import { NotificationsPage } from '@/pages/NotificationsPage'
import { ArtifactsPage } from '@/pages/ArtifactsPage'
import { LivingWorldPage } from '@/pages/LivingWorldPage'
import { ChroniclesPage } from '@/pages/ChroniclesPage'
import { DeveloperPage } from '@/pages/DeveloperPage'
import { MemoryInboxPage } from '@/pages/MemoryInboxPage'
import { MemoryConsolidationPage } from '@/pages/MemoryConsolidationPage'
import { ReportingPage } from '@/pages/ReportingPage'
import { StoriesPage } from '@/pages/StoriesPage'
import { StoryDetailsPage } from '@/pages/StoryDetailsPage'
import { TerritoriesPage } from '@/pages/TerritoriesPage'
import { TerritoryDetailsPage } from '@/pages/TerritoryDetailsPage'
import { PlacesPage } from '@/pages/PlacesPage'
import { PlaceDetailsPage } from '@/pages/PlaceDetailsPage'
import { OrganizationsPage } from '@/pages/OrganizationsPage'
import { OrganizationDetailsPage } from '@/pages/OrganizationDetailsPage'
import { NotFoundPage } from '@/pages/NotFoundPage'

export const appRoutes = [
  { path: '/login', element: <LoginPage /> },
  { path: '/admin', element: <AdminLoginPage /> },
  {
    path: '/admin',
    element: <AdminRoute />,
    children: [
      {
        element: <AdminShell />,
        children: [
          { index: true, element: <Navigate to="/admin/overview" replace /> },
          { path: 'overview', element: <AdminOverviewPage /> },
          { path: 'developer', element: <DeveloperPage /> },
          { path: 'memory/inbox', element: <MemoryInboxPage /> },
          { path: 'memory/consolidation', element: <MemoryConsolidationPage /> },
          { path: 'reporting', element: <ReportingPage /> },
          { path: 'living-world', element: <LivingWorldPage adminMode /> },
          { path: 'chronicles', element: <ChroniclesPage adminMode /> },
          { path: 'artifacts', element: <ArtifactsPage adminMode /> },
          { path: 'explore/stories', element: <StoriesPage /> },
          { path: 'explore/stories/:id', element: <StoryDetailsPage /> },
          { path: 'explore/territories', element: <TerritoriesPage /> },
          { path: 'explore/territories/:id', element: <TerritoryDetailsPage /> },
          { path: 'explore/places', element: <PlacesPage /> },
          { path: 'explore/places/:id', element: <PlaceDetailsPage /> },
          { path: 'explore/organizations', element: <OrganizationsPage /> },
          { path: 'explore/organizations/:id', element: <OrganizationDetailsPage /> },
        ],
      },
    ],
  },
  {
    path: '/',
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppShell />,
        children: [
          { index: true, element: <Navigate to="/home" replace /> },
          { path: 'home', element: <HomePage /> },
          { path: 'world', element: <Navigate to="/home" replace /> },
          { path: 'notifications', element: <NotificationsPage /> },
          { path: 'artifacts', element: <ArtifactsPage /> },
          { path: 'chronicles', element: <ChroniclesPage /> },
          { path: 'living-world', element: <LivingWorldPage /> },
          { path: 'chat', element: <ChatPage /> },
          { path: 'characters', element: <CharactersPage /> },
          { path: 'characters/:id', element: <CharacterDetailsPage /> },
          { path: 'dev', element: <Navigate to="/admin/developer" replace /> },
          { path: 'memory/inbox', element: <Navigate to="/admin/memory/inbox" replace /> },
          { path: 'memory/consolidation', element: <Navigate to="/admin/memory/consolidation" replace /> },
          { path: 'reporting', element: <Navigate to="/admin/reporting" replace /> },
          { path: 'stories', element: <Navigate to="/admin/explore/stories" replace /> },
          { path: 'territories', element: <Navigate to="/admin/explore/territories" replace /> },
          { path: 'places', element: <Navigate to="/admin/explore/places" replace /> },
          { path: 'organizations', element: <Navigate to="/admin/explore/organizations" replace /> },
          { path: '*', element: <NotFoundPage /> },
        ],
      },
    ],
  },
]

export const router = createBrowserRouter(appRoutes)
