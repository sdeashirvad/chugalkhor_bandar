package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import com.chugalkhorbandar.adapters.persistence.postgres.entity.CharacterCredentialEntity;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.CharacterCredentialJpaRepository;
import com.chugalkhorbandar.domain.identity.ports.CharacterCredentialRepository;
import java.util.Optional;

public final class PostgresCharacterCredentialRepository implements CharacterCredentialRepository {

    private final CharacterCredentialJpaRepository jpa;

    public PostgresCharacterCredentialRepository(CharacterCredentialJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(String characterId, String passkey) {
        jpa.save(new CharacterCredentialEntity(characterId, passkey));
    }

    @Override
    public Optional<String> findPasskeyByCharacterId(String characterId) {
        return jpa.findById(characterId).map(CharacterCredentialEntity::getPasskey);
    }

    @Override
    public boolean exists(String characterId) {
        return jpa.existsById(characterId);
    }
}
