package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.ports.PersistenceProvider;
import com.chugalkhorbandar.ports.PersistenceProviderType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev & !postgres-dev")
public class InMemoryPersistenceProvider implements PersistenceProvider {

    @Override
    public PersistenceProviderType getType() {
        return PersistenceProviderType.IN_MEMORY_H2;
    }
}
