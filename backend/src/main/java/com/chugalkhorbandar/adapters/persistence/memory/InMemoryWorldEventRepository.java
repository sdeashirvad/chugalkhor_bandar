package com.chugalkhorbandar.adapters.persistence.memory;

import com.chugalkhorbandar.application.world.living.WorldEvent;
import com.chugalkhorbandar.application.world.living.WorldEventType;
import com.chugalkhorbandar.domain.world.living.ports.WorldEventRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!postgres-dev")
public class InMemoryWorldEventRepository implements WorldEventRepository {

    private final InMemoryLivingWorldStore store;

    public InMemoryWorldEventRepository(InMemoryLivingWorldStore store) {
        this.store = store;
    }

    @Override
    public WorldEvent save(WorldEvent event) {
        return store.saveEvent(event);
    }

    @Override
    public Optional<WorldEvent> findById(String id) {
        return store.findEventById(id);
    }

    @Override
    public List<WorldEvent> findAllOrderByCreatedAtDesc() {
        return store.findAllEvents();
    }

    @Override
    public List<WorldEvent> findByTypeOrderByCreatedAtDesc(WorldEventType type) {
        return store.findEventsByType(type);
    }

    @Override
    public Set<String> findAllEventIds() {
        return store.findAllEventIds();
    }
}
