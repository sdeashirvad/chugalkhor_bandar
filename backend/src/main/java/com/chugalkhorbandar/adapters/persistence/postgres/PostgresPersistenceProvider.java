package com.chugalkhorbandar.adapters.persistence.postgres;

import com.chugalkhorbandar.ports.PersistenceProvider;
import com.chugalkhorbandar.ports.PersistenceProviderType;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("postgres-dev")
public class PostgresPersistenceProvider implements PersistenceProvider {

    @Override
    public PersistenceProviderType getType() {
        return PersistenceProviderType.POSTGRESQL;
    }
}
