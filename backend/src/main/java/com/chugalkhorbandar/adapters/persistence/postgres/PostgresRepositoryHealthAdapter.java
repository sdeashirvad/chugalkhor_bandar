package com.chugalkhorbandar.adapters.persistence.postgres;

import com.chugalkhorbandar.ports.RepositoryHealthPort;
import javax.sql.DataSource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("postgres-dev")
public class PostgresRepositoryHealthAdapter implements RepositoryHealthPort {

    private final DataSource dataSource;

    public PostgresRepositoryHealthAdapter(DataSource dataSource) {
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
