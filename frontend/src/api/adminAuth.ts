export const ADMIN_AUTH_STORAGE_KEY = 'chugalkhor_admin_auth'

export function readAdminAuth(): boolean {
  return sessionStorage.getItem(ADMIN_AUTH_STORAGE_KEY) === 'true'
}

export function writeAdminAuth(authenticated: boolean): void {
  if (authenticated) {
    sessionStorage.setItem(ADMIN_AUTH_STORAGE_KEY, 'true')
  } else {
    sessionStorage.removeItem(ADMIN_AUTH_STORAGE_KEY)
  }
}

export function validateAdminCredentials(username: string, password: string): boolean {
  return username === 'ADMIN' && password === 'ADMIN'
}
