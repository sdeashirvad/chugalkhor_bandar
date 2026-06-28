import { useState } from 'react'
import { Navigate, useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import { motion } from 'framer-motion'
import { readSessionId } from '@/api/session'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Select } from '@/components/ui/select'
import { useCharacters } from '@/hooks/useCharacters'
import { useLogin, useSession } from '@/hooks/useSession'
import { gentleFade } from '@/lib/motion'

export function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const [searchParams] = useSearchParams()
  const sessionExpired = (location.state as { sessionExpired?: boolean } | null)?.sessionExpired
    || searchParams.get('expired') === '1'
  const sessionId = readSessionId()
  const { data: session, isLoading: sessionLoading } = useSession()
  const { data: characters = [], isLoading: charactersLoading } = useCharacters()
  const login = useLogin()
  const [animalName, setAnimalName] = useState('')
  const [passkey, setPasskey] = useState('')

  const sortedCharacters = [...characters].sort((a, b) => a.name.localeCompare(b.name))

  if (sessionId && session && !sessionLoading) {
    return <Navigate to="/home" replace />
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault()
    login.mutate(
      { animalName, passkey },
      {
        onSuccess: () => navigate('/home', { replace: true, state: { showWelcome: true } }),
      },
    )
  }

  return (
    <div className="login-portal-bg flex min-h-[100dvh] items-center justify-center px-4 py-6 sm:p-4">
      <motion.div
        className="w-full max-w-md rounded-2xl border border-jungle-gold/30 bg-jungle-parchment/70 p-5 shadow-2xl backdrop-blur-md sm:p-8"
        {...gentleFade}
      >
        <p className="font-display text-2xl font-semibold text-jungle-deep sm:text-3xl">Chugalkhor Bandar</p>
        <p className="mt-1 text-base text-jungle-bark sm:text-sm">The Jungle remembers.</p>

        <h1 className="mt-8 font-display text-xl font-medium text-foreground sm:text-2xl">Enter the Jungle</h1>
        <p className="mt-2 text-base text-muted-foreground sm:text-sm">
          Choose your character and family passkey to step inside.
        </p>

        {sessionExpired ? (
          <p className="mt-4 rounded-lg bg-jungle-gold/10 px-3 py-2 text-sm text-jungle-bark">
            Your path faded — please enter again.
          </p>
        ) : null}

        <form className="mt-6 space-y-4" onSubmit={handleSubmit}>
          <div>
            <label className="mb-1 block text-sm font-medium" htmlFor="animalName">
              Character
            </label>
            <Select
              id="animalName"
              value={animalName}
              onChange={(event) => setAnimalName(event.target.value)}
              required
              disabled={charactersLoading || sortedCharacters.length === 0}
              className="border-jungle-bark/20 bg-white/60"
            >
              <option value="" disabled>
                {charactersLoading ? 'Loading residents…' : 'Choose your character'}
              </option>
              {sortedCharacters.map((character) => (
                <option key={character.id} value={character.name}>
                  {character.name}
                  {character.species ? ` · ${character.species}` : ''}
                </option>
              ))}
            </Select>
          </div>
          <div>
            <label className="mb-1 block text-sm font-medium" htmlFor="passkey">
              Passkey
            </label>
            <Input
              id="passkey"
              type="password"
              value={passkey}
              onChange={(event) => setPasskey(event.target.value)}
              placeholder="Family passkey"
              autoComplete="current-password"
              required
              className="border-jungle-bark/20 bg-white/60"
            />
          </div>
          {login.isError ? (
            <p className="text-sm text-jungle-bark">That name isn&apos;t known in the Jungle, or the passkey was wrong.</p>
          ) : null}
          <Button type="submit" className="w-full bg-jungle-moss hover:bg-jungle-canopy" disabled={login.isPending}>
            {login.isPending ? 'Opening the gate…' : 'Step Inside'}
          </Button>
        </form>
      </motion.div>
    </div>
  )
}
