package com.chugalkhorbandar.domain.identity.ports;

import java.util.Optional;

public interface CharacterCredentialRepository {

    void save(String characterId, String passkey);

    Optional<String> findPasskeyByCharacterId(String characterId);

    boolean exists(String characterId);
}
