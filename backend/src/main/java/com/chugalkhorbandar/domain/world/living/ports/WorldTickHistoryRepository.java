package com.chugalkhorbandar.domain.world.living.ports;

import com.chugalkhorbandar.application.world.living.WorldClockMode;
import com.chugalkhorbandar.application.world.living.WorldTickHistory;
import java.util.List;
import java.util.Optional;

public interface WorldTickHistoryRepository {

    WorldTickHistory save(WorldTickHistory history);

    Optional<WorldTickHistory> findLatest();

    Optional<WorldTickHistory> findLatestByMode(WorldClockMode mode);

    List<WorldTickHistory> findAllOrderByStartedAtDesc();
}
