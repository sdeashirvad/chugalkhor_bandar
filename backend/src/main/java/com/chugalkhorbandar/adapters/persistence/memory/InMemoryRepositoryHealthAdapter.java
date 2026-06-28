package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.ports.RepositoryHealthPort;
import javax.sql.DataSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class InMemoryRepositoryHealthAdapter implements RepositoryHealthPort {

    private final DataSource dataSource;

    public InMemoryRepositoryHealthAdapter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean isHealthy() {
        try (var connection = dataSource.getConnection()) {
            return connection.isValid(2);
        } catch (Exception e) {
            return false;
        }
    }
}
