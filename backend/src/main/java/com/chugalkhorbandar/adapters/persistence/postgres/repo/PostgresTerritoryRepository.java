package com.chugalkhorbandar.adapters.persistence.postgres.repo;

import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.ensureAbsent;
import static com.chugalkhorbandar.adapters.persistence.postgres.repo.PostgresRepositorySupport.requireRuntime;

import com.chugalkhorbandar.adapters.persistence.PersistenceException;
import com.chugalkhorbandar.adapters.persistence.postgres.jpa.TerritoryJpaRepository;
import com.chugalkhorbandar.adapters.persistence.postgres.mapper.TerritoryMapper;
import com.chugalkhorbandar.domain.world.ports.TerritoryRepository;
import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class PostgresTerritoryRepository implements TerritoryRepository {

    private final TerritoryJpaRepository jpa;

    public PostgresTerritoryRepository(TerritoryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void create(RuntimeTerritory territory) {
        ensureAbsent(jpa, territory.id(), "Territories");
        jpa.save(TerritoryMapper.toEntity(territory));
    }

    @Override
    public boolean exists(String territoryId) {
        return jpa.existsById(territoryId);
    }

    @Override
    public Optional<RuntimeTerritory> findById(String territoryId) {
        return jpa.findById(territoryId).map(TerritoryMapper::toRuntime);
    }

    @Override
    public List<RuntimeTerritory> findAll() {
        return jpa.findAll().stream().map(TerritoryMapper::toRuntime).toList();
    }

    @Override
    public void changeRuler(String territoryId, String newRulerId) {
        RuntimeTerritory territory = requireRuntime(findById(territoryId), territoryId, "Territories");
        jpa.save(TerritoryMapper.toEntity(territory.withCurrentRulerId(newRulerId)));
    }

    @Override
    public void transferOwnership(String territoryId, String fromRulerId, String toRulerId) {
        RuntimeTerritory territory = requireRuntime(findById(territoryId), territoryId, "Territories");
        if (!Objects.equals(territory.currentRulerId(), fromRulerId)) {
            throw new PersistenceException(
                    "Territory " + territoryId + " is not ruled by " + fromRulerId);
        }
        jpa.save(TerritoryMapper.toEntity(territory.withCurrentRulerId(toRulerId)));
    }
}
