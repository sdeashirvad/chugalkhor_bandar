package com.chugalkhorbandar.domain.world.ports;

import com.chugalkhorbandar.domain.world.runtime.RuntimeTerritory;
import java.util.List;
import java.util.Optional;

public interface TerritoryRepository {

    void create(RuntimeTerritory territory);

    boolean exists(String territoryId);

    Optional<RuntimeTerritory> findById(String territoryId);

    List<RuntimeTerritory> findAll();

    void changeRuler(String territoryId, String newRulerId);

    void transferOwnership(String territoryId, String fromRulerId, String toRulerId);
}
