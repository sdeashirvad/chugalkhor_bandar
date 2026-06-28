package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.domain.identity.ports.CharacterCredentialRepository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryCharacterCredentialRepository implements CharacterCredentialRepository {

    private final ConcurrentHashMap<String, String> credentials = new ConcurrentHashMap<>();

    @Override
    public void save(String characterId, String passkey) {
        credentials.put(characterId, passkey);
    }

    @Override
    public Optional<String> findPasskeyByCharacterId(String characterId) {
        return Optional.ofNullable(credentials.get(characterId));
    }

    @Override
    public boolean exists(String characterId) {
        return credentials.containsKey(characterId);
    }
}
