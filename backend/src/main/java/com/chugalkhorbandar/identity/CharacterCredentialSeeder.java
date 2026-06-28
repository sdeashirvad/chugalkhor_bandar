package com.chugalkhorbandar.identity;

import com.chugalkhorbandar.config.ChugalkhorProperties;
import com.chugalkhorbandar.domain.identity.ports.CharacterCredentialRepository;
import com.chugalkhorbandar.domain.world.ports.WorldRepositoryProvider;
import com.chugalkhorbandar.domain.world.ports.query.CharacterQuery;
import com.chugalkhorbandar.domain.world.runtime.RuntimeCharacter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CharacterCredentialSeeder {

    private final WorldRepositoryProvider repositoryProvider;
    private final CharacterCredentialRepository credentials;
    private final ChugalkhorProperties properties;

    public CharacterCredentialSeeder(
            WorldRepositoryProvider repositoryProvider,
            CharacterCredentialRepository credentials,
            ChugalkhorProperties properties) {
        this.repositoryProvider = repositoryProvider;
        this.credentials = credentials;
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seedCredentials() {
        for (RuntimeCharacter character :
                repositoryProvider.characters().findAll(CharacterQuery.all())) {
            if (credentials.exists(character.id())) {
                continue;
            }
            String passkey = properties.getSession().getPasskeys().getOrDefault(
                    character.title(), properties.getSession().getDefaultPasskey());
            credentials.save(character.id(), passkey);
        }
    }
}
