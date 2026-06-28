package com.chugalkhorbandar.domain.world.living.ports;

import com.chugalkhorbandar.application.world.living.WorldEvent;
import com.chugalkhorbandar.application.world.living.WorldEventType;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface WorldEventRepository {

    WorldEvent save(WorldEvent event);

    Optional<WorldEvent> findById(String id);

    List<WorldEvent> findAllOrderByCreatedAtDesc();

    List<WorldEvent> findByTypeOrderByCreatedAtDesc(WorldEventType type);

    Set<String> findAllEventIds();
}
