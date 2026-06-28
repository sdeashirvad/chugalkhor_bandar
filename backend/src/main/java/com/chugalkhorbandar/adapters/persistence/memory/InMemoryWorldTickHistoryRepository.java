package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.world.living.WorldClockMode;
import com.chugalkhorbandar.application.world.living.WorldTickHistory;
import com.chugalkhorbandar.domain.world.living.ports.WorldTickHistoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryWorldTickHistoryRepository implements WorldTickHistoryRepository {

    private final InMemoryLivingWorldStore store;

    public InMemoryWorldTickHistoryRepository(InMemoryLivingWorldStore store) {
        this.store = store;
    }

    @Override
    public WorldTickHistory save(WorldTickHistory history) {
        return store.saveTickHistory(history);
    }

    @Override
    public Optional<WorldTickHistory> findLatest() {
        return store.findLatestTickHistory();
    }

    @Override
    public Optional<WorldTickHistory> findLatestByMode(WorldClockMode mode) {
        return store.findLatestTickHistoryByMode(mode);
    }

    @Override
    public List<WorldTickHistory> findAllOrderByStartedAtDesc() {
        return store.findAllTickHistory();
    }
}
